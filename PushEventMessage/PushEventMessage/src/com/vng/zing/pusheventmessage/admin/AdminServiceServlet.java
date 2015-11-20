/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.admin.handler.AppHandler;
import com.vng.zing.pusheventmessage.admin.handler.PushHandler;
import com.vng.zing.pusheventmessage.admin.handler.SettingHandler;
import com.vng.zing.pusheventmessage.admin.handler.UserHandler;
import com.vng.zing.pusheventmessage.common.HttpUtils;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import com.vng.zing.pusheventmessage.model.BaseModel;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class AdminServiceServlet extends HttpServlet {

    private static final Logger LOGGER = ZLogger.getLogger(BaseModel.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            HttpUtils.responseJsonCode(resp, -1000, "Invalid request");
            return;
        }
        try {
            
            switch (pathInfo) {
                case "/login":
                    UserHandler.login(req, resp);
                    return;
                case "/logout":
                    UserHandler.logout(req, resp);
                    return;
                case "/createPush":
                    PushHandler.createPush(req, resp);
                    return;
                case "/deleteSchedule":
                    PushHandler.deleteSchedule(req, resp);
                    return;
                case "/countDevice":
                    PushHandler.countDevice(req, resp);
                    return;
                case "/setApnsKey":
                    SettingHandler.setApnsKey(req, resp);
                    return;
                case "/setApnsSslFile":
                    SettingHandler.setApnsSslFile(req, resp);
                    return;
                case "/setGcmKey": 
                    SettingHandler.setGcmKey(req, resp);
                    return;
                default:
                    HttpUtils.responseJsonCode(resp, -1000, "Invalid request");
                    return;
            }
        } catch (InvalidRequestException ex) {
            HttpUtils.responseJsonCode(resp, ex.getCode(), ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            HttpUtils.responseJsonCode(resp, -13);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            HttpUtils.responseJsonCode(resp, -1000, "Invalid request");
            return;
        }
        try {
            switch (pathInfo) {
                case "/getProfile":
                    UserHandler.getProfile(req, resp);
                    return;
                case "/getApp":
                    AppHandler.getApp(req, resp);
                    return;
                case "/getApps":
                    AppHandler.getApps(req, resp);
                    return;
                case "/getDevicePage":
                    AppHandler.getDevicesPage(req, resp);
                    return;
                case "/getScheduledPage":
                    PushHandler.getScheduledPage(req, resp);
                    return;
                case "/getScheduledCount":
                    PushHandler.getScheduledCount(req, resp);
                    return;
                case "/getHistoryPage":
                    PushHandler.getHistoryPage(req, resp);
                    return;
                  case "/getHistoryCount":
                    PushHandler.getHistoryCount(req, resp);
                    return;
                case "/getPush":
                    PushHandler.getPush(req, resp);
                    return;
                case "/getGcmKey":
                    SettingHandler.getGcmKey(req, resp);
                    return;
                case "/getApnsKey":
                    SettingHandler.getApnsKey(req, resp);
                    return;
                case "/hasApnsSslKey":
                    SettingHandler.hasApnsSslKey(req, resp);
                    return;
                default:
                    HttpUtils.responseJsonCode(resp, -1000, "Invalid request");
                    return;
            }
        } catch (InvalidRequestException ex) {
            HttpUtils.responseJsonCode(resp, ex.getCode(), ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            HttpUtils.responseJsonCode(resp, -13);
        }
    }

}
