/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.utils;

import com.vng.zing.pushnotification.common.Constant;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class DeviceHelper {

    public static final String[] extraColumns = new String[]{
        Constant.Request.PARAM_APP_VERSION, Constant.Request.PARAM_OS_VERSION, Constant.Request.PARAM_PACKAGE_NAME, Constant.Request.PARAM_SDK_VERSION, Constant.Request.PARAM_APP_USER, Constant.Request.PARAM_ZALO_ID
    };

    /**
     * return token table name
     *
     * @param appId
     * @param platform
     * @return
     */
    public static String getTokenTableName(String appId, String platform) {
        return "tbl_" + appId + "_" + platform + "_token";
    }

    public static String getTokenTableName(int appId, String platform) {
        return "tbl_" + appId + "_" + platform + "_token";
    }

    /**
     * return extra table name
     *
     * @param appId
     * @param platform
     * @return
     */
    public static String getExtraTableName(String appId, String platform) {
        return "tbl_" + appId + "_" + platform + "_query";
    }
    public static String getExtraTableName(int appId, String platform) {
        return "tbl_" + appId + "_" + platform + "_query";
    }
}
