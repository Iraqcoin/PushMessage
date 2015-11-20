/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.service;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.db.ScheduledTaskDb;
import com.vng.zing.pusheventmessage.entity.ScheduledTask;
import com.vng.zing.pusheventmessage.noti.IosPushService;
import com.vng.zing.pushnotification.common.InvalidRequestException;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.EventHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.service.ServiceFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author root
 */
public class SvTaskScheduler {

    private static final Logger LOGGER = LogManager.getLogger(SvTaskScheduler.class);

    private static final ConcurrentSkipListMap<Integer, ScheduledTask> tasks = new ConcurrentSkipListMap<>();
    private static EventDAO eventDAO = new EventHandler();

    static {
        try {
            for (ScheduledTask task : list(0, 100)) {
                tasks.put(task.getId(), task);
                System.out.println(task);
            }
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Iterator<Map.Entry<Integer, ScheduledTask>> iterator = tasks.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, ScheduledTask> entry = iterator.next();
                        if (entry.getValue().getTime() <= System.currentTimeMillis()) {
                            try {
                                removeTask(entry.getKey());
                                taskProcess(entry.getValue());
                            } catch (BackendServiceException ex) {
                                LOGGER.error(ex.getMessage(), ex);
                            }
                        }
                    }

                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            }
        }).start();
    }

    public static int addTask(int appId, long time, int type, String model) throws BackendServiceException {
        int id = ScheduledTaskDb.create(appId, time, model, type, ScheduledTask.STATUS_WAITING);

        ScheduledTask task = new ScheduledTask();
        task.setId(id);
        task.setModel(model);
        task.setTime(time);
        task.setType(type);

        tasks.put(task.getId(), task);
        return id;
    }

    public static void removeTask(int id) throws BackendServiceException {
        Iterator<Map.Entry<Integer, ScheduledTask>> iterator = tasks.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, ScheduledTask> entry = iterator.next();
            if (entry.getValue().getId() == id) {
                iterator.remove();
                break;
            }
        }
        ScheduledTaskDb.delete(id);
    }

    public static List<ScheduledTask> list(int offset, int size) throws BackendServiceException {
        return ScheduledTaskDb.list(offset, size);
    }

    private static void taskProcess(ScheduledTask task) {
        LOGGER.info("PROCESS " + task);
        try {
            switch (task.getType()) {
                case ScheduledTask.TYPE_PUSH_ANDROID_NOTI: {
                    try {
//                        JSONObject model = task.getModelAsJson();
//                        String requestId = (String) model.get("requestId");
                        String requestId = task.getModel();
                        Map<String, Object> event = eventDAO.get(requestId);
                        String request = (String) event.get("request");
                        RequestMessage requestMessgae = RequestMessage.parse(request);
                        ServiceFactory.getInstance().deliver(requestMessgae);
                    } catch (ParseException ex) {
                        LOGGER.error("Invalid task model: " + task);
                    } catch (InvalidRequestException ex) {
                        LOGGER.error("Invalid RequestMessage", ex);
                    }

                    return;
                }
                case ScheduledTask.TYPE_PUSH_IOS_NOTI: {
//                        JSONObject model = task.getModelAsJson();
//                        String requestId = (String) model.get("requestId");
                    String requestId = task.getModel();
                    IosPushService.startPush(requestId);
                    return;
                }
                default: {
                    LOGGER.warn(MsgBuilder.format("The ScheduledTask is not supported. type=??", task.getType()));
                }
            }
        } catch (BackendServiceException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
