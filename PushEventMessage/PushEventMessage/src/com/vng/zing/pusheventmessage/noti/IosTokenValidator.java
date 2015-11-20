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
import com.vng.zing.pusheventmessage.common.InactiveCloser;
import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.AdminHandler;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.pushnotification.dao.impl.EventHandler;
import com.vng.zing.pushnotification.thirdparty.apns.FeedbackService;
import com.vng.zing.pushnotification.thirdparty.apns.Handler;
import com.vng.zing.pushnotification.thirdparty.apns.utils.FeedbackTuple;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class IosTokenValidator {

    private static final Logger LOGGER = ZLogger.getLogger(IosTokenValidator.class);

    private static final InactiveCloser closer = new InactiveCloser(30000);

    private static final DeviceDAO deviceDb = new DeviceHandler();
    private static final EventDAO eventDb = new EventHandler();
    private static AdminDAO adminDao = new AdminHandler();

    public static void validateTokens(final int appId, boolean production) throws BackendServiceException {
        String token = adminDao.getApiKey(String.valueOf(appId), Platform.IOS.toString());
        if (token == null || token.isEmpty()) {
            LOGGER.info(MsgBuilder.format("No such appId[??]", appId));
            return;
        }

        String keyFile = NotiInfoModel.getApnsKeyFilePath(appId);
        File file = new File(keyFile);
        if (!file.exists() || !file.canRead()) {
            LOGGER.info(MsgBuilder.format("Key file[??] doesn't EXIST", keyFile));
            return;
        }

        final FeedbackService feedbacks = new FeedbackService(keyFile, token, production);
        feedbacks.setHandler(new Handler<FeedbackTuple>() {
            @Override
            public void handle(FeedbackTuple tuple) {
                String token = DatatypeConverter.printHexBinary(tuple.getDeviceToken());
                LOGGER.info("A token is invalid " + token);
                try {
                    deviceDb.remove(String.valueOf(appId), Platform.IOS.toString(), token);
                } catch (BackendServiceException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    feedbacks.stop();
                }
            }
        });

        try {
            closer.add(feedbacks);
            feedbacks.start();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

}
