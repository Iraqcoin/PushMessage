/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.handler;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.admin.common.Params;
import com.vng.zing.pusheventmessage.admin.common.SessionUtils;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.HttpUtils;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import com.vng.zing.pusheventmessage.common.Platform;
import com.vng.zing.pusheventmessage.handlers.NotiInfoHandler;
import com.vng.zing.pusheventmessage.model.NotiInfoModel;
import com.vng.zing.pusheventmessage.model.PushNotiModel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class SettingHandler {

    private static final Logger LOGGER = ZLogger.getLogger(SettingHandler.class);

    public static void getGcmKey(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);
        int appId = Params.getAppId(req);

        String key = PushNotiModel.getGoogleSenderId(appId);
        HttpUtils.responseJsonValues(resp, 0, key);
    }

    public static void getApnsKey(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);
        int appId = Params.getAppId(req);
        String key = Clients.adminDao.getApiKey(String.valueOf(appId), Platform.IOS.toString());

        HttpUtils.responseJsonValues(resp, 0, key);
    }

    public static void hasApnsSslKey(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException {
        SessionUtils.getUserId(req);
        int appId = Params.getAppId(req);

        boolean result = NotiInfoModel.hasApnsKeyFile(appId);
        HttpUtils.responseJsonValues(resp, 0, result);
    }

    public static void setApnsKey(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);
        int appId = Params.getAppId(req);
        String key = Params.getKey(req);

        NotiInfoModel.getInst().setApnsKey(appId, key);
    }

    public static void setGcmKey(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);
        int appId = Params.getAppId(req);
        String key = Params.getKey(req);

        NotiInfoModel.setGoogleApiKey(appId, key);
    }

    public static void setApnsSslFile(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, IOException, BackendServiceException, ServletException {
        SessionUtils.getUserId(req);
        int appId = Params.getAppId(req);

//        Part part = req.getPart("ssl-file");
//        byte[] array = IOUtils.toByteArray(part.getInputStream());
        byte[] data = null;
        if (ServletFileUpload.isMultipartContent(req)) {
            try {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List items = upload.parseRequest(req);
                for (Object obj : items) {
                    FileItem fileItem = (FileItem) obj;
                    if ("file".equals(fileItem.getFieldName()) && fileItem.getSize() > 0) {
                        data = fileItem.get();
                    }
                }
            } catch (FileUploadException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        System.out.println(data.length);
        System.out.println(appId);

        NotiInfoModel.getInst().saveApnsKeyFile(appId, ByteBuffer.wrap(data));
        HttpUtils.responseJsonValues(resp, 0, 0);
    }
}
