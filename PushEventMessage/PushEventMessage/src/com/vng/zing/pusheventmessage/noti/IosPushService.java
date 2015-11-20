/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.noti;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.common.Platform;
import com.vng.zing.pusheventmessage.model.NotiInfoModel;
import com.vng.zing.pusheventmessage.thrift.Noti;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.AdminHandler;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.pushnotification.dao.impl.EventHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.stats.Profiler;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author root
 */
public class IosPushService {

    private static final Logger LOGGER = ZLogger.getLogger(IosPushService.class);

    private static final int BATCH_SIZE = 1000;

    private static final DeviceDAO deviceDb = new DeviceHandler();
    private static final EventDAO eventDb = new EventHandler();
    private static AdminDAO adminDao = new AdminHandler();

    private static final ConcurrentHashMap<String, Worker> workers = new ConcurrentHashMap<>();

    public static void startPush(final String requestId) throws BackendServiceException {
        //request_id, app_id, platform, requester, found, pushed, request, paused_at, state
        Map<String, Object> job = eventDb.get(requestId);

        /**
         * paused_at --- null platform --- ios requester --- defaultAmdmin
         * request_id --- 8a45c4f7678d4b0b8efe7c461fedf897 request ---
         * {"platform":"ios","requester":"defaultAdmin","request_id":"8a45c4f7678d4b0b8efe7c461fedf897","sender_id":"AIzaSyDnuPVEsqG6dvmxDRzb3hxCklFyTlve2Ic","data":{"message":"Abcdef"},"app_id":"9999","expressions":[]}
         * state --- PROCESSING pushed --- 0 app_id --- 9999 found --- 0
         */
        String platform = (String) job.get(Constant.Request.PARAM_PLATFORM);
        if (!"ios".equals(platform)) {
            LOGGER.info(MsgBuilder.format("Request platform is ??, It's not 'ios' => skip", platform));
        }
        String request = (String) job.get("request");
        try {
            final RequestMessage r = RequestMessage.parse(request);
            final int appId = Integer.parseInt(r.getAppId());

            String sslKey = NotiInfoModel.KEY_DIR + "/" + appId + ".p12";

//            JSONObject json = (JSONObject) new JSONParser().parse(r.getData().get("message"));

//            LOGGER.info("Json message: " + json);
//            Noti noti = new Noti()
//                    .setTitle((String) json.get("title"))
//                    .setMessage((String) json.get("msg"))
//                    .setSound("default");
            
            
            Noti noti = new Noti()
                    .setTitle(r.getData().get("message"))
                    .setMessage(r.getData().get("title"))
                    .setSound("default");

            String pass = adminDao.getApiKey(String.valueOf(appId), Platform.IOS.toString());

            try {

                final boolean productionE;

                if ("development".equalsIgnoreCase(System.getProperty("zappprof"))) {
//                    productionE = false;
                    productionE = true;
                } else {
                    productionE = true;
                }

                final IosNotiPusher pusher = new IosNotiPusher(sslKey, pass, noti, productionE);

                final List<Long> sdkIds = deviceDb.getDeviceIds(r);

                final List<List<Long>> batchs = new LinkedList<>();

                LOGGER.info(MsgBuilder.format("preparing pushing Noti to ?? TOKENS", sdkIds.size()));
                for (int i = 0; i < sdkIds.size();) {
                    List<Long> batch = new ArrayList<>();
                    for (int j = 0; j < BATCH_SIZE && i < sdkIds.size(); j++, i++) {
                        batch.add(sdkIds.get(i));
                    }

                    batchs.add(batch);
                }
                LOGGER.info(MsgBuilder.format("There is ?? batchs to be pushed", batchs.size()));

                Worker worker = new Worker(pusher) {

                    int currentBatch = 0;
                    AtomicInteger count = new AtomicInteger(0);

                    @Override
                    public List<String> nextTokens() {
                        LOGGER.info("get NEXT BATCH " + (currentBatch + 1));
//                        return Arrays.asList("a0c2cc8d040404b669eb44b9cd1ca9930b97302cf78976d6eed3baf72451ae6e");
                        if (currentBatch < batchs.size()) {
                            try {
                                List<String> tokens = deviceDb.getTokensByDeviceId(r.getAppId(), r.getPlatform(), batchs.get(currentBatch));
                                currentBatch++;
                                if (tokens != null && !tokens.isEmpty()) {
                                    eventDb.pause(requestId, currentBatch);
                                }

                                try {
                                    count.addAndGet(tokens.size());
                                    System.out.println("# COUNT  " + count);
                                    eventDb.update(requestId, sdkIds.size(), count.get(), 0, EventDAO.State.PROCESSING);
                                } catch (BackendServiceException ex) {
                                    LOGGER.error(ex.getMessage(), ex);
                                }

                                return tokens;
                            } catch (BackendServiceException ex) {
                                LOGGER.error(ex.getMessage(), ex);
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onComplete() {
                        LOGGER.info("COMPLETED");
                        workers.remove(requestId);
                        pusher.close();
                        try {
                            eventDb.update(requestId, sdkIds.size(), null, 0, EventDAO.State.DONE);
                        } catch (BackendServiceException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }

                        try {
                            IosTokenValidator.validateTokens(appId, productionE);
                        } catch (BackendServiceException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }

                    @Override
                    public void onPause() {
                        LOGGER.info("PAUSED");
                        workers.remove(requestId);
                        pusher.close();
                        try {
                            IosTokenValidator.validateTokens(appId, productionE);
                        } catch (BackendServiceException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                };

                workers.put(requestId, worker);
                worker.start();

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                eventDb.error(requestId, EventDAO.State.ERROR, ex.getMessage());
            }

        } catch (ParseException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public static abstract class Worker {

        private boolean pushing = true;
        private IosNotiPusher pusher;

        private Thread thread;

        private Worker(IosNotiPusher pusher) {
            this.pusher = pusher;
        }

        public void stop() {
            pushing = false;
        }

        public void start() {
            if (thread != null) {
                LOGGER.info("This Worker is stopped!");
                return;
            }
            pushing = true;

            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Profiler.createThreadProfiler("PushIosNoti", false);
                    try {
                        CountDownLatch result = null;
                        while (pushing) {
                            if (result != null) {
                                try {
                                    result.await();
                                    Profiler.getThreadProfiler().pop(Worker.class, "PushIosBatch");
                                } catch (InterruptedException ex) {
                                    LOGGER.warn(ex.getMessage(), ex);
                                }
                            }
                            List<String> tokens = nextTokens();
                            if (tokens == null || tokens.isEmpty()) {
                                LOGGER.info("NO any tokens left to be pushed");
                                break;
                            }
                            Profiler.getThreadProfiler().push(Worker.class, "PushIosBatch");
                            result = pusher.push(tokens);
                        }

                        if (result != null) {
                            try {
                                result.await();
                            } catch (InterruptedException ex) {
                                LOGGER.warn(ex.getMessage(), ex);
                            }
                        }

                        if (!pushing) {
                            onPause();
                        } else {
                            pushing = false;
                            onComplete();
                        }
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    } finally {
                        Profiler.closeThreadProfiler();
                    }
                }
            });
            thread.start();
        }

        public abstract List<String> nextTokens();

        public abstract void onComplete();

        public abstract void onPause();
    }

}
