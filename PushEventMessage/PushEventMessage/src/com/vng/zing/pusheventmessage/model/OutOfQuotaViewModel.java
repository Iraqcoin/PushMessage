/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.model;

import com.vng.zing.configer.ZConfig;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.db.OutOfQuotaDb;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.stats.Profiler;
import com.vng.zing.stats.ThreadProfiler;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.ConcurrentHashSet;

/**
 *
 * @author root
 */
public class OutOfQuotaViewModel {

    private static final Logger LOGGER = Logger.getLogger(OutOfQuotaViewModel.class);

    private static final long OUT_OF_QUOTA_DELAY_TIME = ZConfig.Instance.getLong(EventMsgModel.class, "common", "view_quota_update", 60000); // 10 mintues

    private static final ConcurrentHashMap<Long, ConcurrentHashSet<Long>> blacklists = new ConcurrentHashMap<Long, ConcurrentHashSet<Long>>();

    private static final ConcurrentHashSet<Long> hasNewUpdate = new ConcurrentHashSet<Long>();

    public static void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    Long[] msgIds = hasNewUpdate.toArray(new Long[hasNewUpdate.size()]);
                    LOGGER.info(MsgBuilder.format("Being update out-of-view-quota, msgids: ??", Arrays.toString(msgIds)));
                    if (msgIds.length > 0) {
                        for (Long msgId : msgIds) {
                            try {
                                Profiler.createThreadProfiler("OutOfQuotaViewModel", false);
                                Profiler.getThreadProfiler().push(OutOfQuotaViewModel.class, "upDateOutOfQuotaList");
                                hasNewUpdate.remove(msgId);
                                ConcurrentHashSet<Long> sdkIds = blacklists.get(msgId);
                                if (sdkIds != null) {
                                    LOGGER.info(MsgBuilder.format("Update out-of-view-quota, msgid[??], sdkIds.size = ??", msgId, sdkIds.size()));
                                    OutOfQuotaDb.save(msgId, sdkIds);
                                }
                                Profiler.getThreadProfiler().pop(OutOfQuotaViewModel.class, "upDateOutOfQuotaList");
                                Profiler.closeThreadProfiler();

                                try {
                                    Thread.sleep(OUT_OF_QUOTA_DELAY_TIME);
                                } catch (InterruptedException ex) {
                                }
                            } catch (BackendServiceException ex) {
                                LOGGER.error(ex.getMessage(), ex);
                                hasNewUpdate.add(msgId);
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(OUT_OF_QUOTA_DELAY_TIME);
                        } catch (InterruptedException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }

                }
            }
        }).start();
    }

    public static synchronized void loadBlacklist(long msgId) throws BackendServiceException {

        LOGGER.info(MsgBuilder.format("Loading out-of-view-quota list of msgId[??] ", msgId));
        ConcurrentHashSet<Long> result = new ConcurrentHashSet<Long>();

        Set<Long> blackList = OutOfQuotaDb.get(msgId);
        if (blackList != null) {
            result.addAll(blackList);
        }

        blacklists.put(msgId, result);
        LOGGER.info(MsgBuilder.format("Loaded out-of-view-quota list of msgId[??], size = ??", msgId, result.size()));
    }

    public static Set<Long> getBlacklist(long msgId) {
        Set<Long> set = blacklists.get(msgId);
        if (set == null) {
            blacklists.putIfAbsent(msgId, new ConcurrentHashSet<Long>());
            set = blacklists.get(msgId);
        }
        return set;
    }

    public static void addBlackList(long msgId, long sdkId) {
        LOGGER.info(MsgBuilder.format("Add out-of-view-quota msgId[??], sdkId[??]", msgId, sdkId));
        Set<Long> list = getBlacklist(msgId);
        int size = list.size();
        list.add(sdkId);
        if (size != list.size()) {
            hasNewUpdate.add(msgId);
        }
    }

    public static void resetBlackList(long msgId) {
        ThreadProfiler threadProfiler = Profiler.getThreadProfiler();
        if (threadProfiler == null) {
            threadProfiler = Profiler.createThreadProfiler("OutOfQuotaViewModel", false);
        }
        threadProfiler.push(OutOfQuotaViewModel.class, "resetOutOfViewQuotaList");

        LOGGER.info(MsgBuilder.format("RESET BlackList msgId[??]", msgId));
        ConcurrentHashSet<Long> get = blacklists.get(msgId);
        if (get != null) {
            get.clear();
            hasNewUpdate.add(msgId);
        }

        threadProfiler.pop(OutOfQuotaViewModel.class, "resetOutOfViewQuotaList");
    }
}
