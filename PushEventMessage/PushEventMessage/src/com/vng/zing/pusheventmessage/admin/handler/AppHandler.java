/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.handler;

import com.vng.zing.pusheventmessage.admin.common.BeanUtils;
import com.vng.zing.pusheventmessage.admin.common.Params;
import com.vng.zing.pusheventmessage.admin.common.SessionUtils;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.HttpUtils;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import com.vng.zing.pusheventmessage.common.Platform;
import com.vng.zing.pusheventmessage.db.AppOwnerDb;
import com.vng.zing.pusheventmessage.db.ScheduledTaskDb;
import com.vng.zing.pusheventmessage.entity.AppInfo;
import com.vng.zing.pusheventmessage.entity.DeviceInfo;
import com.vng.zing.pushnotification.common.MySQLClient;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.stats.Profiler;
import com.vng.zing.zalooauthmw.thrift.TAppInfoResult;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class AppHandler {

    private static final Logger LOGGER = Logger.getLogger(AppHandler.class);

    public static void getApps(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, IOException, BackendServiceException {
        long userId = SessionUtils.getUserId(req);

        List<Integer> appIds = AppOwnerDb.getAppByOwner(userId);
        JSONArray array = new JSONArray();

        for (int appId : appIds) {
            // TAppInfoResult appInfoResult = Clients.zoaClient.getApp(appId);
            AppInfo appInfoResult = getAppInfo(appId);
            long deviceCount = 0;
            deviceCount += Clients.deviceDb.count(appId, Platform.ANDROID.toString());
            deviceCount += Clients.deviceDb.count(appId, Platform.IOS.toString());

            long pushCount = Clients.eventDb.countByApp(appId);
            long scheduleCount = ScheduledTaskDb.count(appId);

            if (appInfoResult.getError() < 0) {
            } else {
                array.add(BeanUtils.newJson()
                        .set("id", appId)
                        //.set("name", appInfoResult.getAppInfo().getName())
                        .set("name", appInfoResult.getName())
                        // .set("logo", appInfoResult.getAppInfo().logoUrl)
                        .set("logo", appInfoResult.getLogo())
                        .set("deviceCount", deviceCount)
                        .set("pushCount", pushCount)
                        .set("scheduleCount", scheduleCount)
                        .build()
                );
            }
        }// end for

        HttpUtils.responseJsonValues(resp, 0, array);
    }

    public static AppInfo getAppInfo(int appId) {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            return null;
        }
        AppInfo app = new AppInfo();
        Profiler.getThreadProfiler().push(DeviceHandler.class, "Get app");
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM app WHERE id = ?");
            stm.setInt(1, appId);
            ResultSet rs = null;
            rs = stm.executeQuery();
            if (rs.next()) {
                app.setId(appId);
                app.setName(rs.getString(2));
                app.setLogo(rs.getString(3));
                app.setError(1);
            }
        } catch (Exception ex) {
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "Get app");
        }
        return app;
    }

    public static void getDevicesPage(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, IOException, BackendServiceException {
        long userId = SessionUtils.getUserId(req);

        int appId = Params.getAppId(req);
        String platform = Params.getPlatform(req);
        int offset = Params.getOffset(req);
        int size = Params.getSize(req);

        List<DeviceInfo> devicePage = Clients.deviceDb.getDevicePage(appId, platform, offset, size);
        JSONArray array = new JSONArray();
        for (DeviceInfo info : devicePage) {
            JSONObject json = info.toJson();
            BeanUtils.changeFieldName(json, "sdkId", "id");
            array.add(json);
        }
        HttpUtils.responseJsonValues(resp, 0, array);
    }

    public static void getApp(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        long userId = SessionUtils.getUserId(req);

        int appId = Params.getAppId(req);
        //TAppInfoResult appInfoResult = Clients.zoaClient.getApp(appId);
        AppInfo appInfoResult = getAppInfo(appId);
        long deviceCount = 0;
        deviceCount += Clients.deviceDb.count(appId, Platform.ANDROID.toString());
        deviceCount += Clients.deviceDb.count(appId, Platform.IOS.toString());

        long pushCount = Clients.eventDb.countByApp(appId);
        long scheduleCount = ScheduledTaskDb.count(appId);

        if (appInfoResult.getError() < 0) {
            throw new RuntimeException("Can't get app Info, appId=" + appId);
        }
        HttpUtils.responseJsonValues(resp, 0, BeanUtils.newJson()
                .set("id", appId)
                // .set("name", appInfoResult.getAppInfo().getName())
                .set("name", appInfoResult.getName())
                // .set("logo", appInfoResult.getAppInfo().logoUrl)
                .set("logo", appInfoResult.getLogo())
                .set("deviceCount", deviceCount)
                .set("pushCount", pushCount)
                .set("scheduleCount", scheduleCount)
                .build());
    }
}
