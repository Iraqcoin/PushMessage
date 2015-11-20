/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.service.impl;

import com.vng.zing.configer.ZConfig;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.common.Controller;
import com.vng.zing.pushnotification.common.InvalidRequestException;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.service.Service;
import static com.vng.zing.pushnotification.service.impl.AbstractService.logger;
import com.vng.zing.pushnotification.thirdparty.gcm.GCMCallback;
import com.vng.zing.pushnotification.thirdparty.gcm.GCMRequest;
import com.vng.zing.pushnotification.thirdparty.gcm.GCMService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class AndroidPushService extends AbstractService {

    private static final String[] REQUIRED_PARAMS = new String[]{
                                        Constant.Request.PARAM_APP_ID, 
                                        Constant.Request.PARAM_SENDER_ID};
    private static final String platform = "android";
    private static final int MAX_TOKEN_PER_GCM_REQUEST_DEFAULT = 1000;
    private final int awaitTimeInSecond = ZConfig.Instance.getInt(Service.class, platform, "awaitTimeInSecond", 5);
    private final BlockingQueue<List<Long>> deviceIdBatchQueue = new LinkedBlockingQueue<>(500);
    
    private final DeviceDAO deviceHandler = new DeviceHandler();
    private GCMService GCMService;
    private int maxTokenPerGCMRequest;
    
    public AndroidPushService() {
        super(REQUIRED_PARAMS);
        init();
    }
    
    private void init(){
        maxTokenPerGCMRequest = ZConfig.Instance.getInt(Service.class, platform, 
                "maxTokenPerGCMRequest", MAX_TOKEN_PER_GCM_REQUEST_DEFAULT);
        if(maxTokenPerGCMRequest <= 0 || maxTokenPerGCMRequest > MAX_TOKEN_PER_GCM_REQUEST_DEFAULT){
            maxTokenPerGCMRequest = MAX_TOKEN_PER_GCM_REQUEST_DEFAULT;
        }
        
        GCMService = new GCMService.Builder().build();
    }


    @Override
    protected void validateRequestMessage(RequestMessage requestMessage) throws InvalidRequestException {
        super.validateRequestMessage(requestMessage);
        if(requestMessage.getData().isEmpty()){
            throw new InvalidRequestException("Message body is empty");
        }
    }
    
    
    @Override
    protected void process(RequestMessage requestMessage) throws BackendServiceException, InterruptedException {
        logger.info("Processing: " + requestMessage.valueAsJson());
        //  Get proper data from request message
        //
        String appId = requestMessage.getAppId();
        String requestId = requestMessage.getRequestId();
        
        //  Init executor and fetch data 
        //
        List<Long> expectedTokenIds = getExpectedTokenId(requestMessage);
        // for testting
//        List<Long> expectedTokenIds = new ArrayList<>();
//        for (int i = 0; i < 10000; i++) {
//            expectedTokenIds.add(expectedTokenIds1.get(i));
//        }
        // end for testting
        
        logger.info(String.format("[AppId=%s;ReqId=%s] Just found %s token(s)", 
                appId, requestId, expectedTokenIds == null ? 0 : expectedTokenIds.size()));
        if(expectedTokenIds == null || expectedTokenIds.isEmpty()){
            eventHandler.update(requestId, 0, 0, 0, EventDAO.State.DONE);
            return;
        }
        eventHandler.update(requestId, expectedTokenIds.size(), 0, 0, EventDAO.State.PROCESSING);
        ExecutorService executor = buildExecutorService(expectedTokenIds.size());
        int maximumPoolSize = ((ThreadPoolExecutor)executor).getMaximumPoolSize();
        for (int i = 0; i < maximumPoolSize; i++) {
            executor.submit(new GCMWorker(requestId, appId, requestMessage.getSenderId(), requestMessage.getData()));
        }
        
        //  Push message to GCM and receive callback
        //
        int batchSize = Math.min(maxTokenPerGCMRequest, expectedTokenIds.size());
        List<Long> batchTokenIds = new ArrayList<>(batchSize);
        int counter = 0;
        for(; counter < expectedTokenIds.size() && Controller.getInstance().get(appId) == false;){
            batchTokenIds.add(expectedTokenIds.get(counter));
            if(++counter % batchSize == 0){
                deviceIdBatchQueue.put(batchTokenIds);
                batchTokenIds = new ArrayList<>(batchSize);
            }
        }
        // push the last tokens
        if(Controller.getInstance().get(appId)){
            logger.info("Got pause signal. Try to stop all running.");
            executor.shutdownNow();
            return;
        }
        else if(!batchTokenIds.isEmpty()){
            deviceIdBatchQueue.put(batchTokenIds);
        }
        
        //  Wait for workers finish with exponential growth
        //
        awaitTermination(executor);
        eventHandler.update(requestId, null, null, null, EventDAO.State.DONE);
    }

    
    private void awaitTermination(ExecutorService executor) throws InterruptedException{
        executor.shutdown();
        int retry = 1;
        boolean isTerminated;
        do{
            long awaitTime = (long) Math.pow(awaitTimeInSecond, retry);
            isTerminated = executor.awaitTermination(
                    awaitTime, TimeUnit.SECONDS);
            if(!isTerminated){
                logger.info(String.format("just wait in %s seconds", awaitTime));
            }
            retry++;
            if(retry > 3){
                break;
            }
        }while(!isTerminated);
    }
    
    
    /**
     * utility method helps getting token list (as receiver list)
     * 
     * @param requestMessage
     * @return 
     */
    private List<Long> getExpectedTokenId(RequestMessage requestMessage) throws BackendServiceException {
        return deviceHandler.getDeviceIds(requestMessage);
    }
    
    /**
     * utility method helps building executor service 
     * 
     * @param tokenSize
     * @return 
     */
    private ExecutorService buildExecutorService(int tokenSize){
        // TODO should use value from config file
        int totalRequest = tokenSize / maxTokenPerGCMRequest + 1;
        int maximumPoolSize = ZConfig.Instance.getInt(Service.class, platform, "maximumPoolSize", 32);
        if(totalRequest < maximumPoolSize){
            maximumPoolSize = totalRequest;
        }
        return new ThreadPoolExecutor(maximumPoolSize, maximumPoolSize,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    
    private static final short DEFAULT_MAX_RETRY_COUNT = 5;
    private static final short DEFAULT_INITIAL_BACKOFF_TIME_SECONDS = 2;
    private final short maxRetryCount = DEFAULT_MAX_RETRY_COUNT;
    private final short initialBackoffTimeSeconds = DEFAULT_INITIAL_BACKOFF_TIME_SECONDS;
    private static final int pollingTimeout = ZConfig.Instance.getInt(Service.class, platform, "pollingTimeOut", 1000);
    
    private final AtomicInteger _counter = new AtomicInteger(0);
    /**
     * Class as worker handles (until getting null data from queue after waiting <pollingTimeOut> milliseconds: <br/>
     * <ul>
     *   <li>get token list from database</li>
     *   <li>push message to GCM server</li>
     *   <li>updateExtra token DB when receiving callback</li>
     * </ul>
     * 
     */
    private class GCMWorker implements Runnable{

        private final String requestId;
        private final String appId;
        private final String senderId;
        private final Map<String, String> data;
        private short nextRetryTime = initialBackoffTimeSeconds;
        
        private GCMWorker(String requestId, String appId, String senderId, Map<String, String> data) {
            this.requestId = requestId;
            this.appId = appId;
            this.senderId = senderId;
            this.data = new HashMap<>(data);
        }
        
         @Override
        public void run() {
            try {
                launch();
            } 
            catch (Exception ex) {
                logger.error("Worker has stopped because of interrupt signal", ex);
            }
        }

        private void launch() throws InterruptedException {
            List<Long> deviceIds; 
            logger.info("Worker started");
            while((deviceIds = deviceIdBatchQueue.poll(pollingTimeout, TimeUnit.MILLISECONDS)) != null){
                try {
                    List<String> tokens = deviceHandler.getTokensByDeviceId(appId, platform, deviceIds);

                    if(tokens == null || tokens.isEmpty()){
                        logger.info(String.format("Not found any tokens from %s given device ids. Continue", deviceIds.size()));
                        continue;
                    }

                    GCMCallback callback;
                    short retryCount = 0;
                    GCMRequest request = new GCMRequest.Builder(senderId)
                            .withListRecipientId(tokens)
                            .withData(data)
                            .build();
                    do {
                        callback = GCMService.push(request);
                        if(callback.isMarkedRetry()) {
                            if(retryCount >= maxRetryCount){
                                logger.error("Maximum retry is reached");
                                break;
                            }
                            retryCount++;
                            if(callback.retryTime() > 0){
                                nextRetryTime = callback.retryTime();
                            }
                            nextRetryTime = Double.valueOf(Math.pow(nextRetryTime,2)).shortValue();
                            try {
                                TimeUnit.SECONDS.sleep(nextRetryTime);
                            } catch (InterruptedException ex) {
                                logger.error("Error occured when attempting retry", ex);
                                break;
                            }
                        }
                    } while(callback.isMarkedRetry());
                    long lastDeviceId = deviceIds.get(deviceIds.size() - 1);
                    eventHandler.pause(requestId, lastDeviceId);
                    callback.setTotalInput(tokens.size());
                    processCallback(callback);
                }
                catch(BackendServiceException ex){
                    logger.error("Encounter one batch processing fail", ex);
                }
            }
            logger.info("Worker stopped");
                
        } 
        
        private void processCallback(GCMCallback callback) throws BackendServiceException{
            if(callback.isSuccess()){
                eventHandler.update(requestId, null, callback.totalInput(), null, EventDAO.State.PAUSED);
             }
             else if(callback.getFailureCode() != null){
                 logger.fatal(String.format("[AppId=%s;ReqId=%s] Encountered GCM Error %s", appId, requestId, callback.getFailureCode().name()));
                 eventHandler.update(requestId, null, null, callback.totalInput(), EventDAO.State.PAUSED);
             }
             else{
                 List<String> invalidIds = callback.getInvalidIds();
                 if(invalidIds != null && !invalidIds.isEmpty()){
                     deviceHandler.remove(appId, platform, invalidIds); 
                 }
                 Map<String, String> canonicals = callback.getCanonicals();
                 if(canonicals != null && !canonicals.isEmpty()){
                     deviceHandler.batchUpdateByToken(appId, platform, canonicals);
                 }
                 eventHandler.update(requestId, null, callback.totalSuccess(), callback.totalFail(), EventDAO.State.PAUSED);
             }
        }

    }
}
