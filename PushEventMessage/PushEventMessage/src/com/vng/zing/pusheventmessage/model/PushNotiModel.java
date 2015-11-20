/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.model;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.common.Platform;
import com.vng.zing.pusheventmessage.entity.ScheduledTask;
import com.vng.zing.pusheventmessage.noti.IosPushService;
import com.vng.zing.pusheventmessage.service.SvTaskScheduler;
import com.vng.zing.pusheventmessage.thrift.Noti;
import com.vng.zing.pusheventmessage.thrift.PushNotiInfo;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.common.InvalidRequestException;
import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.AdminHandler;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.pushnotification.dao.impl.EventHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.service.ServiceFactory;
import com.vng.zing.pushnotification.utils.ZExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class PushNotiModel {

    private static final Logger LOGGER = LogManager.getLogger(PushNotiModel.class);

    private static DeviceDAO deviceDAO = new DeviceHandler();
    private static EventDAO eventDAO = new EventHandler();

    private static AdminDAO adminDao = new AdminHandler();

    private static void getAndAddFirst(String field, Map<String, Object> des, List<String> source) {
        if (source != null && source.size() > 0) {
            des.put(field, source.get(0));
        }
    }

    public static String getGoogleSenderId(int appId) throws BackendServiceException {
        return adminDao.getApiKey(String.valueOf(appId), Platform.ANDROID.toString());
//        return "AIzaSyCgv3gnO1tMwfP4U8H3CY2GauMcDxC8hZ0";
    }

    public static void saveDevice(long sdkId, int appId, String platform, String token, String appVersion, String osVersion, String sdkVersion, String packageName) throws BackendServiceException {
        Map<String, String> map = new HashMap<>();

        map.put(Constant.Request.PARAM_APP_VERSION, appVersion);
        map.put(Constant.Request.PARAM_OS_VERSION, osVersion);
        map.put(Constant.Request.PARAM_SDK_VERSION, sdkVersion);
        map.put(Constant.Request.PARAM_PACKAGE_NAME, packageName);

        deviceDAO.addOrUpdate(String.valueOf(appId), platform, sdkId, token, map);
    }

    public static String generateRequestId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String pushAndroidNoti(int appId, Noti noti, List<String> appVersion, List<String> osVersion, List<String> sdkVersion, List<String> packageNames, List<Long> zaloId, List<String> appUser, long time) throws BackendServiceException {
        String requestId = generateRequestId();
        String appCode = String.valueOf(appId);

        Map<String, Object> map = new HashMap<>();
        map.put(Constant.Request.PARAM_APP_ID, String.valueOf(appId));
        map.put(Constant.Request.PARAM_SENDER_ID, getGoogleSenderId(appId));
        map.put(Constant.Request.PARAM_REQUEST_ID, requestId);
        map.put(Constant.Request.PARAM_MESSAGE, noti.getMessage());
        map.put(Constant.Request.PARAM_PLATFORM, Platform.ANDROID.toString());
        map.put("request", requestId);

        getAndAddFirst(Constant.Request.PARAM_APP_VERSION, map, appVersion);
        getAndAddFirst(Constant.Request.PARAM_OS_VERSION, map, osVersion);
        getAndAddFirst(Constant.Request.PARAM_SDK_VERSION, map, sdkVersion);
        getAndAddFirst(Constant.Request.PARAM_PACKAGE_NAME, map, packageNames);

        map.put("state", EventDAO.State.PAUSED.name());

        JSONObject msg = new JSONObject();
        msg.put("title", noti.getTitle());
        msg.put("msg", noti.getMessage());

        Map<String, String> data = new HashMap<>();
        data.put("message", noti.getMessage());
        data.put("title", noti.getTitle());

        final RequestMessage.Builder build = new RequestMessage.Builder(String.valueOf(appId), Platform.ANDROID.toString())
                //                .setMessage(noti.getMessage())
                .setData(data)
                //                .setMessage(msg.toJSONString())
                .setSenderId(getGoogleSenderId(appId))
                .setRequiredParams("defaultAdmin", requestId, getGoogleSenderId(appId));

        if (appVersion != null && !appVersion.isEmpty()) {
            for (String value : appVersion) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (osVersion != null && !osVersion.isEmpty()) {
            for (String value : osVersion) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_OS_VERSION, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (sdkVersion != null && !sdkVersion.isEmpty()) {
            for (String value : sdkVersion) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_SDK_VERSION, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (packageNames != null && !packageNames.isEmpty()) {
            for (String value : packageNames) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_PACKAGE_NAME, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (zaloId != null && !zaloId.isEmpty()) {
            for (long value : zaloId) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_ZALO_ID, String.valueOf(value), ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (appUser != null && !appUser.isEmpty()) {
            for (String value : appUser) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_APP_USER, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }

        if (time == 0) {
            eventDAO.create(appCode, Platform.ANDROID.toString(), "defaultAmdmin", requestId, build.build().valueAsJson(), EventDAO.State.PROCESSING);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServiceFactory.getInstance().deliver(build.build());
                    } catch (InvalidRequestException ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            }).start();
        } else {
            eventDAO.create(appCode, Platform.ANDROID.toString(), "defaultAmdmin", requestId, build.build().valueAsJson(), EventDAO.State.SCHEDULED);
//            JSONObject json = new JSONObject();
//            json.put("requestId", requestId);
            SvTaskScheduler.addTask(appId, time, ScheduledTask.TYPE_PUSH_ANDROID_NOTI, requestId);
        }

        return requestId;
    }

    private static String getFirst(List list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.valueOf(list.get(0));
    }

    public static String pushIosNoti(int appId, Noti noti, List<String> appVersion, List<String> osVersion, List<String> sdkVersion, List<String> packageNames, List<Long> zaloId, List<String> appUser, long time) throws BackendServiceException {
        final String requestId = generateRequestId();
        String appCode = String.valueOf(appId);

        Map<String, Object> map = new HashMap<>();
        map.put(Constant.Request.PARAM_APP_ID, String.valueOf(appId));
        map.put(Constant.Request.PARAM_SENDER_ID, getGoogleSenderId(appId));
        map.put(Constant.Request.PARAM_REQUEST_ID, requestId);
        map.put(Constant.Request.PARAM_MESSAGE, noti.getMessage());
        map.put(Constant.Request.PARAM_PLATFORM, Platform.IOS.toString());
        map.put("request", requestId);

        getAndAddFirst(Constant.Request.PARAM_APP_VERSION, map, appVersion);
        getAndAddFirst(Constant.Request.PARAM_OS_VERSION, map, osVersion);
        getAndAddFirst(Constant.Request.PARAM_SDK_VERSION, map, sdkVersion);
        getAndAddFirst(Constant.Request.PARAM_PACKAGE_NAME, map, packageNames);

        map.put("state", EventDAO.State.PAUSED.name());

//        JSONObject msg = new JSONObject();
//        msg.put("title", noti.getTitle());
//        msg.put("msg", noti.getMessage());
        Map<String, String> data = new HashMap<>();
        data.put("message", noti.getMessage());
        data.put("title", noti.getTitle());

        RequestMessage.Builder build = new RequestMessage.Builder(String.valueOf(appId), Platform.IOS.toString())
                .setData(data)
                //                .setMessage(msg.toJSONString())
                .setSenderId(getGoogleSenderId(appId))
                .setRequiredParams("defaultAdmin", requestId, getGoogleSenderId(appId));

        if (appVersion != null && !appVersion.isEmpty()) {
            for (String value : appVersion) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (osVersion != null && !osVersion.isEmpty()) {
            for (String value : osVersion) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_OS_VERSION, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (sdkVersion != null && !sdkVersion.isEmpty()) {
            for (String value : sdkVersion) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_SDK_VERSION, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (packageNames != null && !packageNames.isEmpty()) {
            for (String value : packageNames) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_PACKAGE_NAME, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (zaloId != null && !zaloId.isEmpty()) {
            for (long value : zaloId) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_ZALO_ID, String.valueOf(value), ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
        if (appUser != null && !appUser.isEmpty()) {
            for (String value : appUser) {
                build.addExpression(new ZExpression(Constant.Request.PARAM_APP_USER, value, ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }

        LOGGER.info("PERSIST: " + build.build().valueAsJson());

        if (time == 0) {
            eventDAO.create(appCode, Platform.IOS.toString(), "defaultAmdmin", requestId, build.build().valueAsJson(), EventDAO.State.PROCESSING);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        IosPushService.startPush(requestId);
                    } catch (Exception ex) {
                        LOGGER.error(ex.getMessage(), ex);
                    }
                }
            }).start();
        } else {
            eventDAO.create(appCode, Platform.IOS.toString(), "defaultAmdmin", requestId, build.build().valueAsJson(), EventDAO.State.SCHEDULED);
//            JSONObject json = new JSONObject();
//            json.put("requestId", requestId);
            SvTaskScheduler.addTask(appId, time, ScheduledTask.TYPE_PUSH_IOS_NOTI, requestId);
        }

        return requestId;
    }

    public static PushNotiInfo getInfo(String requestId) throws BackendServiceException {
        Map<String, Object> info = eventDAO.get(requestId);

        //request_id, app_id, platform, found, pushed, request, state
//        String msg = (String) info.get(Constant.Request.PARAM_MESSAGE);
        int appId = -1;
        int found = -1;
        int pushed = -1;
        try {
            appId = Integer.valueOf((String) info.get(Constant.Request.PARAM_APP_ID));
            found = (int) info.get("found");
            pushed = (int) info.get("pushed");
        } catch (NumberFormatException e) {
        }
        String platform = (String) info.get(Constant.Request.PARAM_PLATFORM);
        String state = (String) info.get("state");

        PushNotiInfo result = new PushNotiInfo()
                .setAppId(appId)
                .setPlatform(platform)
                .setFound(found)
                .setPushed(pushed)
                .setState(state);

        return result;
    }

    public static List<PushNotiInfo> getPushNotiPage(int appId, int offset, int pageSize) throws BackendServiceException {
        List<Map<String, Object>> events = eventDAO.getPage(appId, offset, pageSize);

        List<PushNotiInfo> result = new ArrayList<>();

        for (Map<String, Object> map : events) {
            int found = map.get("found") == null ? 0 : (int) map.get("found");
            int pushed = map.get("pushed") == null ? 0 : (int) map.get("pushed");
            int failed = map.get("failed") == null ? 0 : (int) map.get("failed");
            PushNotiInfo info = new PushNotiInfo()
                    .setAppId(appId)
                    .setRequestId(String.valueOf(map.get(Constant.Request.PARAM_REQUEST_ID)))
                    .setPlatform(String.valueOf(map.get(Constant.Request.PARAM_PLATFORM)))
                    .setFound(found)
                    .setPushed(pushed)
                    .setFail(failed)
                    .setState(String.valueOf(map.get("state")));
            result.add(info);
        }

        return result;
    }

    public static long countPushNotiTask(int appId) throws BackendServiceException {
        return eventDAO.countByApp(appId);
    }
}
