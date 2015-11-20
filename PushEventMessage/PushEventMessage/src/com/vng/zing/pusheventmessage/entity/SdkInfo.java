package com.vng.zing.pusheventmessage.entity;

import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author anhlh
 */
public class SdkInfo {

    private long sdkId;
    private int appId;
    private String sdkVersion;
    private String platform;
    private String osVerrsion;
    private String model;
    private String screenSize;
    private Map<String, String> meta;

    public long getSdkId() {
        return sdkId;
    }

    public void setSdkId(long sdkId) {
        this.sdkId = sdkId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOsVerrsion() {
        return osVerrsion;
    }

    public void setOsVerrsion(String osVerrsion) {
        this.osVerrsion = osVerrsion;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public String getJsonStringMeta() {
        JSONObject jmeta = new JSONObject(meta);
        return jmeta.toJSONString();
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public void setJsonStringMeta(String meta) {
        try {
            JSONObject jmeta = (JSONObject) JSONValue.parseWithException(meta);
            this.meta = jmeta;
        } catch (ParseException ex) {
        }
    }

    @Override
    public String toString() {
        return "sdkId=" + sdkId + ", appId=" + appId + ", platform=" + platform + ", osVerrsion=" + osVerrsion + ", sdkVersion=" + sdkVersion + ", model=" + model;
    }
}
