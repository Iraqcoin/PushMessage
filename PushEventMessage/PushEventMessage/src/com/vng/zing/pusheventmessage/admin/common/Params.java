/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.common;

import com.vng.zing.common.HReqParam;
import com.vng.zing.pusheventmessage.common.Auth;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author root
 */
public class Params {

    public static int getAppId(HttpServletRequest req) throws InvalidRequestException {
        int appId = HReqParam.getInt(req, "appId", -1);
        if (appId < 0) {
            throw new InvalidRequestException("Missing appId");
        }
        return appId;
    }

    public static long getTime(HttpServletRequest req) throws InvalidRequestException {
        String time = HReqParam.getString(req, "time", null);
        if (time == null) {
            throw new InvalidRequestException("Missing time");
        }
        time = time.trim();
        if (time.isEmpty()) {
            return 0;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            return format.parse(time).getTime();
        } catch (java.text.ParseException ex) {
            throw new InvalidRequestException("Invalid date");
        }
    }

    public static String getMsg(HttpServletRequest req) throws InvalidRequestException {
        String msg = HReqParam.getString(req, "msg", null);
        if (msg == null) {
            throw new InvalidRequestException("Missing msg");
        }
        return msg;
    }

    public static String getTitle(HttpServletRequest req) throws InvalidRequestException {
        String title = HReqParam.getString(req, "title", null);
        if (title == null) {
            throw new InvalidRequestException("Missing title");
        }
        return title;
    }

//    public static String getUserId(HttpServletRequest req) throws InvalidRequestException {
//        Auth identity = Auth.getIdentity(req);
//        if (identity.isLogged) {
//            return String.valueOf(identity.userId);
//        }
//        throw new InvalidRequestException("Missing userId");

//        String userId = HReqParam.getString(req, "userId", null);
//        if (userId == null) {
//            throw new InvalidRequestException("Missing userId");
//        }
//        return userId;
//    }

    public static String getPassword(HttpServletRequest req) throws InvalidRequestException {
        String password = HReqParam.getString(req, "password", null);
        if (password == null) {
            throw new InvalidRequestException("Missing password");
        }
        return password;
    }

    public static int getOffset(HttpServletRequest req) throws InvalidRequestException {
        Integer i = HReqParam.getInt(req, "offset", null);
        if (i == null) {
            throw new InvalidRequestException("Missing offset");
        }
        return i;
    }

    public static int getTaskId(HttpServletRequest req) throws InvalidRequestException {
        Integer i = HReqParam.getInt(req, "taskId", null);
        if (i == null) {
            throw new InvalidRequestException("Missing taskId");
        }
        return i;
    }

    public static int getSize(HttpServletRequest req) throws InvalidRequestException {
        Integer i = HReqParam.getInt(req, "size", null);
        if (i == null) {
            throw new InvalidRequestException("Missing size");
        }
        return i;
    }

    public static String getPlatform(HttpServletRequest req) throws InvalidRequestException {
        String platform = HReqParam.getString(req, "platform", null);
        if (platform == null) {
            throw new InvalidRequestException("Missing platform");
        }
        return platform;
    }

    public static String getRequestId(HttpServletRequest req) throws InvalidRequestException {
        String requestId = HReqParam.getString(req, "requestId", null);
        if (requestId == null) {
            throw new InvalidRequestException("Missing requestId");
        }
        return requestId;
    }

    public static JSONObject getJson(String name, HttpServletRequest req) throws InvalidRequestException {
        String value = HReqParam.getString(req, name, null);
        if (value == null) {
            throw new InvalidRequestException("Missing " + name);
        }
        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(value);
        } catch (ParseException ex) {
            throw new InvalidRequestException(MsgBuilder.format("Field ?? has invalid json value", name));
        }
    }

    public static String getKey(HttpServletRequest req) throws InvalidRequestException {
        String key = HReqParam.getString(req, "key", null);
        if (key == null) {
            throw new InvalidRequestException("Missing key");
        }
        return key;
    }
}
