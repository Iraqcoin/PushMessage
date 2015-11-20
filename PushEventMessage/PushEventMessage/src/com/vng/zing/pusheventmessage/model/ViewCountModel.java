/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.model;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.db.TrackingDB;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author namnt3
 */
public class ViewCountModel {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(ViewCountModel.class);

    private static final ConcurrentHashMap<String, Integer> cache = new ConcurrentHashMap<String, Integer>();

    private static final BlockingQueue<String> increaseQueue = new LinkedBlockingDeque<String>();

    private static void init() {
        new Thread(startWorker()).start();
    }

//    public static long getViewCount(long eventMsgId, String clientIdentifier, int days) {
//        String key = eventMsgId + "_" + clientIdentifier;
//        Integer counter = cache.get(key);
//        if (counter == null) {
//            try {
//                counter = TrackingDB.getCounter(key, days).intValue();
//                cache.put(key, counter);
//            } catch (BackendServiceException ex) {
//                LOGGER.error(ex.getMessage(), ex);
//            }
//        }
//        return counter == null ? 0 : counter;
//    }

    private static void increaseViewCount(long eventMsgId, String clientIdentifier) {
        String key = eventMsgId + "_" + clientIdentifier;
        increaseQueue.add(key);
    }

    private static Runnable startWorker() {
        return new Runnable() {
            @Override
            public void run() {
                String key;
                while (true) {
                    try {
                        key = increaseQueue.take();
                        if (key != null) {
                            TrackingDB.increaseCounter(key);
                            Integer cachedValue = cache.get(key);
                            if (cachedValue != null) {
                                cache.put(key, cachedValue + 1);
                            }
                        }
                    } catch (Exception ex) {
                        LOGGER.info(ex.getMessage(), ex);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            LOGGER.info(e.getMessage());
                        }
                    }
                }
            }
        };
    }

}
