/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.model;

import com.mysql.jdbc.MysqlErrorNumbers;
import com.vng.zing.common.HReqParam;
import com.vng.zing.configer.ZConfig;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.common.ErrorCode;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.common.Platform;
import com.vng.zing.pusheventmessage.common.Utils;
import com.vng.zing.pusheventmessage.entity.EventMsgException;
import com.vng.zing.pusheventmessage.entity.SdkInfo;
import com.vng.zing.pusheventmessage.entity.SdkModel;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.impl.AdminHandler;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class NotiInfoModel extends BaseModel {

    private static final Logger LOGGER = LogManager.getLogger(NotiInfoModel.class);

    private static NotiInfoModel inst;

    private static final JSONObject INVALID_REQUEST_RESULT;
    private static final JSONObject SUCCESS_REQUEST_RESULT;

    private static DeviceDAO deviceDao = new DeviceHandler();
    private static AdminDAO adminDao = new AdminHandler();

    public static String KEY_DIR = ZConfig.Instance.getString(NotiInfoModel.class, "common", "keysDir", null); // 10 mintues

    public static boolean USE_PROXY = ZConfig.Instance.getBoolean(NotiInfoModel.class, "common", "use_proxy", false); // 10 mintues
    public static String PROXY_HOST = ZConfig.Instance.getString(NotiInfoModel.class, "common", "proxy_host", null); // 10 mintues
    public static int PROXY_PORT = ZConfig.Instance.getInt(NotiInfoModel.class, "common", "proxy_port", 0); // 10 mintues

    static {
        INVALID_REQUEST_RESULT = new JSONObject();
        INVALID_REQUEST_RESULT.put("errorCode", ErrorCode.INVALID_REQUEST.getCode());
        SUCCESS_REQUEST_RESULT = new JSONObject();
        SUCCESS_REQUEST_RESULT.put("errorCode", ErrorCode.SUCCESS.getCode());
    }
//    

    public static void init() throws EventMsgException {
        if (KEY_DIR == null) {
            throw new EventMsgException("Missing KeysDir configuration");
        }
        File file = new File(KEY_DIR);
        if (!file.exists() || !file.isDirectory()) {
            throw new EventMsgException(MsgBuilder.format("Keys dir[??] doesn't exist", KEY_DIR));
        }
        KEY_DIR = file.getAbsolutePath();
    }

    public static void setGoogleApiKey(int appId, String apiKey) throws BackendServiceException {
        adminDao.saveOrUpdateApiKey(String.valueOf(appId), Platform.ANDROID.toString(), apiKey);
    }

    public static NotiInfoModel getInst() {
        if (inst == null) {
            inst = new NotiInfoModel();
        }
        return inst;
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp) {
        String sdkId = HReqParam.getString(req, "sdkId", null);
        LOGGER.info("query: " + req.getQueryString());

        String appVersion = HReqParam.getString(req, "appVersion", null);
        String token = HReqParam.getString(req, "token", null);
        String packageName = HReqParam.getString(req, "packageName", null);
        String bundleId = HReqParam.getString(req, "bundleId", null);
        String guid = HReqParam.getString(req, "guid", null);
        String zaloId = HReqParam.getString(req, "zaloId", null);
        String appUser = HReqParam.getString(req, "appUser", null);
        String sdkVersion = HReqParam.getString(req, "sdkVersion", null);

        LOGGER.info(MsgBuilder.format("appVersion=??, packageName=??, bundleId=??, guid=??, token=??, zaloId=??, appUser=??", appVersion, packageName, bundleId, guid, token, zaloId, appUser));

        if (sdkId == null || appVersion == null || token == null || (packageName == null && bundleId == null && guid == null)) {
            responseJSON(INVALID_REQUEST_RESULT, resp);
            LOGGER.warn("Missing Params (SdkId or Data is null).");
            return;
        }
        SdkInfo sdkInfos;
        try {
            sdkInfos = SdkModel.getInstance().validateSdkId(sdkId);
        } catch (Exception e) {
            responseJSON(INVALID_REQUEST_RESULT, resp);
            LOGGER.warn("Decrypt SdkId fail! " + e.getMessage());
            return;
        }

        if (sdkInfos == null) {
            responseJSON(INVALID_REQUEST_RESULT, resp);
            return;
        }

        String pkg = Utils.getFirstNotEmptyValue(packageName, bundleId, guid);

        saveToken(resp, sdkInfos, appVersion, token, pkg, zaloId, appUser, sdkVersion);
    }

    private String stringValue(HttpServletResponse resp, JSONObject values, String... keys) {
        for (String key : keys) {
            Object valueObj = values.get(key);
            if (valueObj != null || valueObj instanceof String) {
                return (String) valueObj;
            }
        }

        LOGGER.warn("Invalid " + Arrays.toString(keys) + " value!");
        responseJSON(INVALID_REQUEST_RESULT, resp);
        return null;
    }

    private void saveToken(HttpServletResponse resp, SdkInfo sdkInfo, String appVersion, String token, String packageName, String zaloId, String appUser, String sdkVersion) {
        responseJSON(SUCCESS_REQUEST_RESULT, resp);

        if (sdkVersion == null) {
            sdkVersion = sdkInfo.getSdkVersion();
        }

        LOGGER.info(MsgBuilder.format("sdkId=??, appVersion=??, token=??, packageName=??, osVersion=??, sdkVersion=??", sdkInfo.getSdkId(), appVersion, token, packageName, sdkInfo.getOsVerrsion(), sdkVersion));

        Map<String, String> map = new HashMap<>();
        map.put(Constant.Request.PARAM_APP_VERSION, appVersion);
        map.put(Constant.Request.PARAM_OS_VERSION, sdkInfo.getOsVerrsion());
        map.put(Constant.Request.PARAM_SDK_VERSION, sdkVersion);
        map.put(Constant.Request.PARAM_PACKAGE_NAME, packageName);
//        map.put(Constant.Request.PARAM_SDK_ID, packageName);
        map.put(Constant.Request.PARAM_ZALO_ID, zaloId);
        map.put(Constant.Request.PARAM_APP_USER, appUser);

        try {
            LOGGER.info(MsgBuilder.format("app[??] has new token[??]", sdkInfo.getAppId(), token));
            deviceDao.addOrUpdate(String.valueOf(sdkInfo.getAppId()), sdkInfo.getPlatform(), sdkInfo.getSdkId(), token, map);
        } catch (BackendServiceException ex) {
            if (ex.getErrorCode() == MysqlErrorNumbers.ER_NO_SUCH_TABLE) {
                LOGGER.info("App is not registered");
            } else {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public void saveApnsKeyFile(int appId, ByteBuffer file, String keyPass) throws IOException, BackendServiceException {
        File keyFile = new File(getApnsKeyFilePath(appId));
        keyFile.deleteOnExit();
        try {
            FileOutputStream out = new FileOutputStream(keyFile);
            out.getChannel().write(file);
            out.getChannel().close();
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        adminDao.saveOrUpdateApiKey(String.valueOf(appId), "ios", keyPass);
    }

    public void saveApnsKeyFile(int appId, ByteBuffer file) throws IOException, BackendServiceException {
        File keyFile = new File(getApnsKeyFilePath(appId));
        keyFile.deleteOnExit();
        try {
            FileOutputStream out = new FileOutputStream(keyFile);
            out.getChannel().write(file);
            out.getChannel().close();
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
    
    public void setApnsKey(int appId, String key) throws BackendServiceException {
        adminDao.saveOrUpdateApiKey(String.valueOf(appId), "ios", key);
    }

    public void createApp(int appId) throws BackendServiceException {
        try {
            adminDao.initNewApp(String.valueOf(appId), Platform.IOS.toString());
        } catch (BackendServiceException ex) {
            if (ex.getErrorCode() != MysqlErrorNumbers.ER_TABLE_EXISTS_ERROR) {
                LOGGER.info("Table already existed");
                throw ex;
            }
        }
        try {
            adminDao.initNewApp(String.valueOf(appId), Platform.ANDROID.toString());
        } catch (BackendServiceException ex) {
            if (ex.getErrorCode() != MysqlErrorNumbers.ER_TABLE_EXISTS_ERROR) {
                LOGGER.info("Table already existed");
                throw ex;
            }
        }
    }

    public static String getApnsKeyFilePath(int appId) {
        return KEY_DIR + "/" + appId + ".p12";
    }

    public static boolean hasApnsKeyFile(int appId) {
        File keyFile = new File(getApnsKeyFilePath(appId));
        return keyFile.exists() && !keyFile.isDirectory();
    }

    public static boolean hasGcmKey(int appId) throws BackendServiceException {
        String str = adminDao.getApiKey(String.valueOf(appId), Platform.ANDROID.toString());
        return str != null && !str.isEmpty();
    }

}
