/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.model;

import com.vng.zing.configer.ZConfig;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.db.TrackingDB;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.common.Utils;
import com.vng.zing.pusheventmessage.thrift.EventMsg;
import com.vng.zing.pusheventmessage.common.MyWorker;
import com.vng.zing.pusheventmessage.common.TaskHandler;
import com.vng.zing.stats.Profiler;
import com.vng.zing.stats.ThreadProfiler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class ViewQuotaModel {

    public static Logger LOGGER = Logger.getLogger(ViewQuotaModel.class);

    public static final String COUNTER_KEY_PREFIX = "v";

    private static final long WORKER_DELAY_TIME = ZConfig.Instance.getLong(EventMsgModel.class, "common", "worker_delay_time", 30000); // 10 mintues

    private static MyWorker<IncreaseViewRequest> myWorker;

    public static void init() {
        myWorker = new MyWorker<IncreaseViewRequest>(WORKER_DELAY_TIME, 5);
        myWorker.setTaskHandler(new TaskHandler<IncreaseViewRequest>() {
            @Override
            public void handle(List<IncreaseViewRequest> taskQueue) {
                ThreadProfiler threadProfiler = Profiler.createThreadProfiler("ViewQuotaModel", false);

                threadProfiler.push(ViewQuotaModel.class, "increaseViewCounter");

                LOGGER.info(MsgBuilder.format("increase view counters, size = ??", taskQueue.size()));

                List<String> counterKeys = new ArrayList<String>();
                Map<String, EventMsg> msgs = new HashMap<String, EventMsg>();

                for (IncreaseViewRequest request : taskQueue) {
                    String key = COUNTER_KEY_PREFIX + request.msg.getId() + "_" + request.sdkId;
                    counterKeys.add(key);
                    msgs.put(key, request.msg);
                }
                try {
                    TrackingDB.increaseCounters(counterKeys);
                    Map<String, Long> counters = TrackingDB.getCounters(msgs);

                    for (IncreaseViewRequest request : taskQueue) {
                        String key = COUNTER_KEY_PREFIX + request.msg.getId() + "_" + request.sdkId;
                        Long currentCountValue = counters.get(key);
                        if (currentCountValue != null) {
                            if (currentCountValue >= request.msg.getVisibleTimes()) {
                                OutOfQuotaViewModel.addBlackList(request.msg.id, request.sdkId);
                            }
                        }
                    }
                } catch (BackendServiceException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }

                threadProfiler.pop(ViewQuotaModel.class, "increaseViewCounter");

                Profiler.closeThreadProfiler();
            }
        });
    }

    public static boolean hasViewCount(EventMsg msg) {
        return msg.getVisibleTimes() < 1000;
    }

    public static boolean isOutOfViewQuota(EventMsg msg, Long sdkId) {
        if (hasViewCount(msg)) {
            return OutOfQuotaViewModel.getBlacklist(msg.getId()).contains(sdkId);
        }
        return false;
    }

    public static void increaseViewCount(EventMsg msg, Long sdkId) {
        if (hasViewCount(msg)) {
            myWorker.addTask(new IncreaseViewRequest(sdkId, msg));
        }
    }

    public static class IncreaseViewRequest {

        public long sdkId;
        public EventMsg msg;

        public IncreaseViewRequest(long sdkId, EventMsg msg) {
            this.sdkId = sdkId;
            this.msg = msg;
        }

        public int getDays() {
            return Utils.getDays(msg.getStartDate(), msg.getEndDate());
        }

    }

}
