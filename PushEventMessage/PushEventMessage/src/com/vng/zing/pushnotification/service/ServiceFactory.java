package com.vng.zing.pushnotification.service;

import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.common.InvalidRequestException;
import com.vng.zing.pushnotification.service.impl.AndroidPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class ServiceFactory {

    private ServiceFactory() {
    }

    private Service getService(String platform) {
        Service service = null;
        switch (platform) {
            case "android":
                service = new AndroidPushService();
                break;
            case "ios":
                break;
            default:
                break;
        }
        return service;
    }

    public void deliver(RequestMessage requestMessage) throws InvalidRequestException {
        logger.info("Start deliver message");
        if (requestMessage == null) {
            logger.info("Request is null or empty");
            throw new InvalidRequestException("Request is null or empty");
        }
        for (String requiredParam : requiredParams) {
            if (!requestMessage.contains(requiredParam)) {
                String message = String.format("Param %s is required", requiredParam);
                logger.info(message);
                throw new InvalidRequestException(message);
            }
        }

        Service service = getService(requestMessage.getPlatform());
        if (service == null) {
            throw new InvalidRequestException(requestMessage.getPlatform() + " is not supported now");
        }
        new Thread(new Runner(service, requestMessage)).start();
        logger.info("Deliver message success");
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
    }

    private class Runner implements Runnable {

        private final Service service;
        private final RequestMessage requestMessage;

        private Runner(Service service, RequestMessage requestMessage) {
            this.service = service;
            this.requestMessage = requestMessage;
        }

        @Override
        public void run() {
            service.push(requestMessage);
        }
    }

    private static final ServiceFactory INSTANCE = new ServiceFactory();
    private static final String[] requiredParams = new String[]{
        Constant.Request.PARAM_REQUEST_ID, Constant.Request.PARAM_APP_ID, Constant.Request.PARAM_PLATFORM};
    private static final Logger logger = LoggerFactory.getLogger(ServiceFactory.class);
}
