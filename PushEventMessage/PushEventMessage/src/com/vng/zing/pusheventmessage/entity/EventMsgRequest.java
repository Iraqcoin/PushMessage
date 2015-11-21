/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.entity;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.thrift.MobilePlatform;
//import com.vng.zing.zalooauth.ZCypher;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author namnt3
 */
public class EventMsgRequest {

    private static final Logger LOGGER = ZLogger.getLogger(EventMsgRequest.class);

    private int appId;
    private MobilePlatform platform;
    private String sdkVersion;
    private String packageName;
    private String bundleId;
    private String guid;
    private String appVersion;
    private Long zaloId;
    private String appUser;
    private String deviceId;

    private Long screenWidth;
    private Long screenHeight;

    private String msgHash;

    private Boolean loggedIn;
    
    private Integer orientation;
    
    private String osVersion;

    public EventMsgRequest(SdkInfo sdkInfo, JSONObject reqParams) {

//        this.appId = ZCypher.decodeAppId(sdkInfo.getAppId());
        this.appId = sdkInfo.getAppId();
        String plf = sdkInfo.getPlatform();
        if ("ios".equals(plf)) {
            this.platform = MobilePlatform.IOS;
        } else if ("android".equals(plf)) {
            this.platform = MobilePlatform.ANDROID;
        } else if ("wphone".equals(plf)) {
            this.platform = MobilePlatform.WPHONE;
        }
        this.sdkVersion = sdkInfo.getSdkVersion();

        try {
            this.packageName = (String) reqParams.get("packageName");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting package name fail!" + reqParams.get("packageName"));
        }
        try {
            this.bundleId = (String) reqParams.get("bundleId");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting bundleId fail!" + reqParams.get("bundleId"));
        }
        try {
            this.guid = (String) reqParams.get("guid");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting guid fail!" + reqParams.get("guid"));
        }
        try {
            this.appVersion = (String) reqParams.get("appVersion");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting appVersion fail!" + reqParams.get("appVersion"));
        }
        try {
            String strZaloId = String.valueOf(reqParams.get("zaloId")); // this can be Long or String.
            if (strZaloId != null && !strZaloId.isEmpty()) {
                this.zaloId = Long.parseLong(strZaloId);
            }
        } catch (NumberFormatException e) {
            LOGGER.warn("Parsing zaloId fail!" + reqParams.get("zaloId"));
        }
        try {
            this.appUser = (String) reqParams.get("appUser");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting appUser fail!" + reqParams.get("appUser"));
        }
        try {
            this.deviceId = (String) reqParams.get("deviceId");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting deviceId fail!" + reqParams.get("deviceId"));
        }
        try {
            this.screenWidth = (Long) reqParams.get("screenWidth");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting screenWidth fail!" + reqParams.get("screenWidth"));
        }
        try {
            this.screenHeight = (Long) reqParams.get("screenHeight");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting screenHeight fail!" + reqParams.get("screenHeight"));
        }
        try {
            this.msgHash = (String) reqParams.get("msgHash");
        } catch (ClassCastException e) {
            LOGGER.warn("Casting msgId fail!" + reqParams.get("msgHash"));
        }
        try {
            this.loggedIn = false;
            String temp = String.valueOf(reqParams.get("loggedIn")).toLowerCase();
            if ("1".equals(temp) || "true".equals(temp)) {
                this.loggedIn = true;
            }
        } catch (ClassCastException e) {
            LOGGER.warn("Casting loggedIn fail!" + reqParams.get("loggedIn"));
        }
//        this.loggedIn = false;
//        if (this.appUser != null && !this.appUser.trim().isEmpty()) {
//            this.loggedIn = true;
//        }
        try {
            this.orientation = Integer.parseInt(String.valueOf(reqParams.get("orientation")));
        } catch(NumberFormatException e) {
            LOGGER.warn("Parsing orientation fail!" + reqParams.get("orientation"));
        }
        try {
            this.osVersion = (String)reqParams.get("osVersion");
        } catch(ClassCastException e) {
            LOGGER.warn("Casting orientation fail!" + reqParams.get("osVersion"));
        }
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public MobilePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(MobilePlatform platform) {
        this.platform = platform;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Long getZaloId() {
        return zaloId;
    }

    public void setZaloId(Long zaloId) {
        this.zaloId = zaloId;
    }

    public String getAppUser() {
        return appUser;
    }

    public void setAppUser(String appUser) {
        this.appUser = appUser;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(Long screenWidth) {
        this.screenWidth = screenWidth;
    }

    public Long getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(Long screenHeight) {
        this.screenHeight = screenHeight;
    }

    public String getMsgHash() {
        return msgHash;
    }

    public void setMsgHash(String msgHash) {
        this.msgHash = msgHash;
    }

    public String validate() {
        if (this.getAppId() <= 0) {
            return "Invalid appId";
        }
        return null;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public String getOsVersion() {
        return osVersion;
    }
    
    @Override
    public String toString() {
        return "EventMsgRequest{" + "\nappId=" + appId + ", \nplatform=" + platform + ", \nsdkVersion=" + sdkVersion + ", \npackageName=" + packageName + ", \nbundleId=" + bundleId + ", \nguid=" + guid + ", \nappVersion=" + appVersion + ", \nzaloId=" + zaloId + ", \nappUser=" + appUser + ", \ndeviceId=" + deviceId + ", \nscreenWidth=" + screenWidth + ", \nscreenHeight=" + screenHeight + ", \nmsgHash=" + msgHash + ", \nloggedIn=" + loggedIn + ", \nosVersion=" + osVersion + ", \norientation=" + orientation + '}';
    }

}
