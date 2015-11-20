package com.vng.zing.pushnotification.service.impl;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.common.InvalidRequestException;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.EventHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.service.Service;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public abstract class AbstractService implements Service{
    
    public AbstractService(String[] requiredParams){
        this.requiredParams = Arrays.asList(requiredParams);
        
    }
    

    /**
     * 
     * @param requestMessage
     * @throws InvalidRequestException 
     */
    protected void validateRequestMessage(RequestMessage requestMessage) throws InvalidRequestException{
         for(String requiredParam : requiredParams){
            if(!requestMessage.contains(requiredParam)){
                String message = String.format("Param %s is required", requiredParam);
                throw new InvalidRequestException(message);
            }
        }
     }
    
    
    /**
     * 
     * @param requestMessage
     * @throws BackendServiceException
     * @throws InterruptedException 
     */
    protected abstract void process(RequestMessage requestMessage) throws BackendServiceException, InterruptedException;

    
    /**
     * 
     * @param request 
     */
    @Override
    public void push(RequestMessage request) {
        if(request == null){
            logger.fatal("Request is null or empty");
            return;
        }
        if(request.getRequestId() == null || "".equals(request.getRequestId())){
            logger.fatal("Request id is missing");
            return;
        }
        try {
            logger.info("[START] request id " + request.getRequestId());
            validateRequestMessage(request);
            process(request);
        } 
        catch (InvalidRequestException | InterruptedException ex) {
            logger.fatal(ex.getMessage(), ex);
        } 
        catch (BackendServiceException ex) {
            logger.fatal(String.format("[AppId=%s;RequestId=%s] Encountered Backend service exception", request.getAppId(), request.getRequestId())
                        , ex);
            try {
                eventHandler.error(request.getRequestId(), EventDAO.State.ERROR, ex.getMessage());
            } catch (BackendServiceException e) {
                logger.fatal(String.format("[AppId=%s;RequestId=%s] Could not update event status to "
                        + "ERROR when encountering backend service exception", request.getAppId(), request.getRequestId()), e);
            }
        } catch (Throwable th){
            logger.fatal("unpredictable error", th);
        } finally {
            logger.info("[END] request id " + request.getRequestId());
        }
    }
    
    
    private List<String> requiredParams = Arrays.asList(new String[]{Constant.Request.PARAM_REQUEST_ID, Constant.Request.PARAM_SENDER_ID});
    protected EventDAO eventHandler =  new EventHandler();
    protected static final Logger logger = ZLogger.getLogger(Service.class);
}
