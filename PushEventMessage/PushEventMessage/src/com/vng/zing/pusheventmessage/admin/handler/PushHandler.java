/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.handler;

import com.vng.zing.pusheventmessage.admin.common.Params;
import com.vng.zing.pusheventmessage.admin.common.SessionUtils;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.HttpUtils;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import com.vng.zing.pusheventmessage.db.ScheduledTaskDb;
import com.vng.zing.pusheventmessage.entity.ScheduledTask;
import com.vng.zing.pusheventmessage.model.PushNotiModel;
import com.vng.zing.pusheventmessage.service.SvTaskScheduler;
import com.vng.zing.pusheventmessage.thrift.Noti;
import com.vng.zing.pushnotification.dao.impl.EventHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.utils.ZExpression;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author root
 */
public class PushHandler {

    private static final Logger LOGGER = Logger.getLogger(PushHandler.class);

    public static void getHistoryPage(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        long userId = SessionUtils.getUserId(req);

        int appId = Params.getAppId(req);
        int offset = Params.getOffset(req);
        int size = Params.getSize(req);
        List<Map<String, Object>> page = Clients.eventDb.getPage(appId, offset, size);
        JSONArray array = new JSONArray();
        for (Map<String, Object> map : page) {
            array.add(new JSONObject(map));
        }
        HttpUtils.responseJsonValues(resp, 0, array);
    }

    public static void getScheduledPage(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        long userId = SessionUtils.getUserId(req);

        int appId = Params.getAppId(req);
        int offset = Params.getOffset(req);
        int size = Params.getSize(req);
        List<ScheduledTask> list = ScheduledTaskDb.list(appId, offset, size);
        JSONArray array = new JSONArray();
        JSONParser parser = new JSONParser();
        for (ScheduledTask task : list) {
            try {
                String requestId = task.getModel();

                Map<String, Object> requestModel = Clients.eventDb.get(requestId);
                String _request = (String) requestModel.get("request");
                if (_request != null && !_request.isEmpty()) {
                    requestModel.put("request", parser.parse(_request));
                }

                JSONObject request = new JSONObject(requestModel);
                JSONObject item = task.toJson();
                item.put("request", request);
                array.add(item);
            } catch (ParseException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
        HttpUtils.responseJsonValues(resp, 0, array);
    }

    private static List<String> getList(JSONObject json, String param) throws InvalidRequestException {
        List<String> result = new ArrayList<>();
        try {
            JSONObject in = (JSONObject) json.get(param);
            if (in != null) {
                JSONArray array = (JSONArray) in.get("$in");
                for (Object obj : array) {
                    result.add((String) obj);
                }
            }
        } catch (ClassCastException ex) {
            throw new InvalidRequestException("Invalid data values");
        }
        return result;
    }

    private static List<Long> getLongList(JSONObject json, String param) throws InvalidRequestException {
        List<Long> result = new ArrayList<>();
        try {
            JSONObject in = (JSONObject) json.get(param);
            if (in != null) {
                JSONArray array = (JSONArray) in.get("$in");
                for (Object obj : array) {
                    result.add((Long) obj);
                }
            }
        } catch (ClassCastException ex) {
            throw new InvalidRequestException("Invalid data values");
        }
        return result;
    }

    public static void createPush(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        long userId = SessionUtils.getUserId(req);
        int appId = Params.getAppId(req);
        String platform = Params.getPlatform(req);
        JSONObject filter = Params.getJson("filter", req);
        long time = Params.getTime(req);
        String msg = Params.getMsg(req);
        String title = Params.getTitle(req);

        filter.get("");

        if (platform.equals("android")) {
            PushNotiModel.pushAndroidNoti(appId, new Noti().setMessage(msg).setTitle(title),
                    getList(filter, "app_version"), getList(filter, "os_version"), getList(filter, "sdk_version"),
                    getList(filter, "package_name"), null, Collections.EMPTY_LIST, time);
        } else if (platform.equals("ios")) {
            PushNotiModel.pushIosNoti(appId, new Noti().setMessage(msg).setTitle(title),
                    getList(filter, "app_version"), getList(filter, "os_version"), getList(filter, "sdk_version"),
                    getList(filter, "package_name"), null, Collections.EMPTY_LIST, time);
        } else {
            throw new InvalidRequestException("Invalid Platform");
        }
        HttpUtils.responseJsonValues(resp, 0, time);
    }

    public static void getScheduledCount(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);

        int appId = Params.getAppId(req);
        long count = ScheduledTaskDb.count(appId);
        HttpUtils.responseJsonValues(resp, appId, count);
    }
        public static void getHistoryCount(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);

        int appId = Params.getAppId(req);
        long count = EventHandler.count(appId);
        HttpUtils.responseJsonValues(resp, appId, count);
    }

    public static void getPush(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);

        int appId = Params.getAppId(req);
        String requestId = Params.getRequestId(req);
        Map request = Clients.eventDb.get(requestId);
        if (request == null) {
            HttpUtils.responseJsonCode(resp, 1, "No such push!");
            return;
        }
        HttpUtils.responseJsonValues(resp, 0, new JSONObject(request));
    }

    public static void countDevice(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);

        int appId = Params.getAppId(req);
        String platform = Params.getPlatform(req);
        JSONObject filter = Params.getJson("filter", req);

        RequestMessage.Builder builder = new RequestMessage.Builder(String.valueOf(appId), platform);
        addExpressionByJson(builder, filter);

        long count = Clients.deviceDb.count(builder.build());

        HttpUtils.responseJsonValues(resp, 0, count);
    }

    public static void addExpressionByJson(RequestMessage.Builder builder, JSONObject json) throws InvalidRequestException {
        try {
            for (Object key : json.keySet()) {
                String keyName = (String) key;
                JSONObject value = (JSONObject) json.get(keyName);
                Iterator iterator = value.keySet().iterator();
                if (!iterator.hasNext()) {
                    throw new InvalidRequestException("Invalid value at " + keyName);
                }
                String op = (String) iterator.next();
                Object opValue = value.get(op);
                JSONArray array;
                switch (op) {
                    case "$gt":
                        throw new UnsupportedOperationException("$gt");
                    case "$lt":
                        throw new UnsupportedOperationException("$lt");
                    case "$eq":
                        throw new UnsupportedOperationException("$eq");
                    case "$in":
                        array = (JSONArray) opValue;
                        if (!array.isEmpty()) {
                            builder.addExpression(new ZExpression(keyName, array, ZExpression.ZOperator.AND));
                        }
                        break;
                }
            }
        } catch (ClassCastException e) {
            LOGGER.error(e.getMessage(), e);
            throw new InvalidRequestException("Invalid filter value");
        }
    }

    public static void deleteSchedule(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, BackendServiceException {
        SessionUtils.getUserId(req);

        int taskId = Params.getTaskId(req);
        SvTaskScheduler.removeTask(taskId);

        HttpUtils.responseJsonCode(resp, 0, "Success");
    }
}
