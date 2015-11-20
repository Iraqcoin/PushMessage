/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.common;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class Constant {
    
    public static class Request {
        
        public static final String PARAM_REQUEST_ID = "request_id";
        public static final String PARAM_REQUESTER = "requester";
        public static final String PARAM_SENDER_ID = "sender_id";
        public static final String PARAM_APP_VERSION = "app_version";
        public static final String PARAM_OS_VERSION = "os_version";
        public static final String PARAM_SDK_VERSION = "sdk_version";
        public static final String PARAM_PACKAGE_NAME = "package_name";
        public static final String PARAM_DATA = "data";
        public static final String PARAM_MESSAGE = "message";
        public static final String PARAM_APP_ID = "app_id";
        public static final String PARAM_PLATFORM = "platform";
        public static final String PARAM_LAST_DEVICE_ID = "last_device_id";
        public static final String PARAM_SDK_ID = "sdk_id";
        public static final String PARAM_ZALO_ID = "zalo_id";
        public static final String PARAM_APP_USER = "app_user";
        public static final String PARAM_TITLE = "title";
    }
    
    public static class Device {
        
        public static final String COL_TOKEN = "token";
        public static final String COL_APP_VERSION = Request.PARAM_APP_VERSION;
        public static final String COL_OS_VERSION = Request.PARAM_OS_VERSION;
        public static final String COL_SDK_VERSION = Request.PARAM_SDK_VERSION;
        public static final String COL_PACKAGE_NAME = Request.PARAM_PACKAGE_NAME;
    }
}