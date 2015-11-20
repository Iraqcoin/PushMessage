/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.entity;

import com.vng.zing.pusheventmessage.admin.common.BeanUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author root
 */
public class ScheduledTask {

    public static final int STATUS_WAITING = 1;

    public static final int TYPE_PUSH_ANDROID_NOTI = 1;
    public static final int TYPE_PUSH_IOS_NOTI = 2;

    private int id;
    private int appId;
    private String model;
    private long time;
    private int type;
    private int status;

    public ScheduledTask() {
    }

    public ScheduledTask(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

//    public JSONObject getModelAsJson() throws ParseException {
//        JSONParser parser = new JSONParser();
//        return (JSONObject) parser.parse(model);
//    }

    public JSONObject toJson() throws ParseException {
        return BeanUtils.newJson()
                .set("id", id)
                .set("appId", appId)
                .set("model", model)
                .set("time", time)
                .set("type", type)
                .set("status", status)
                .build();
    }
    
    @Override
    public String toString() {
        return "ScheduledTask{" + "id=" + id + ", model=" + model + ", time=" + time + ", type=" + type + ", status=" + status + '}';
    }

}
