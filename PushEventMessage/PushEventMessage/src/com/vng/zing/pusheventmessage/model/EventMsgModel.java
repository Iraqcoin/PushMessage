/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.model;

import com.vng.zing.common.HReqParam;
import com.vng.zing.configer.ZConfig;
import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.db.EventMsgDb;
import com.vng.zing.pusheventmessage.db.TrackingDB;
import com.vng.zing.pusheventmessage.common.ErrorCode;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.common.Utils;
import com.vng.zing.pusheventmessage.common.AESEncrypt;
import com.vng.zing.pusheventmessage.entity.EventMsgException;
import com.vng.zing.pusheventmessage.entity.EventMsgRequest;
import com.vng.zing.pusheventmessage.entity.SdkInfo;
import com.vng.zing.pusheventmessage.entity.SdkModel;
import com.vng.zing.pusheventmessage.thrift.EventMsg;
import com.vng.zing.pusheventmessage.thrift.EventMsgRequestInfo;
import com.vng.zing.pusheventmessage.thrift.MobilePlatform;
import com.vng.zing.pusheventmessage.common.MyConcurrentCircularQueue;
import com.vng.zing.zcommon.thrift.ECode;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author namnt3
 */
public class EventMsgModel extends BaseModel {

    private static final Logger LOGGER = ZLogger.getLogger(EventMsgModel.class);

    private static EventMsgModel inst;

    public static EventMsgModel getInst() {
        if (inst == null) {
            inst = new EventMsgModel();
        }
        return inst;
    }

    private static final JSONObject INVALID_REQUEST_RESULT;
    private static final long DELAY_TIME;
    private static final long TIME_OUT;

    static {
        INVALID_REQUEST_RESULT = new JSONObject();
        INVALID_REQUEST_RESULT.put("errorCode", ErrorCode.INVALID_REQUEST.getCode());
        DELAY_TIME = ZConfig.Instance.getLong(EventMsgModel.class, "common", "delay_time", 600000); // 10 mintues
        LOGGER.info("DELAY_TIME = " + DELAY_TIME);
        TIME_OUT = ZConfig.Instance.getLong(EventMsgModel.class, "common", "time_out", 10000); // seconds
        LOGGER.info("TIME_OUT = " + TIME_OUT);
    }

    private static final ConcurrentHashMap<Integer, MyConcurrentCircularQueue<EventMsgRequestInfo>> responsedResults = new ConcurrentHashMap<Integer, MyConcurrentCircularQueue<EventMsgRequestInfo>>();

//    public static final EvictingQueue responsedResult = EvictingQueue.create(10);
    public static MyConcurrentCircularQueue<EventMsgRequestInfo> getResponsedResults(int appId, boolean createIfNeeded) {
        MyConcurrentCircularQueue queue = responsedResults.get(appId);
        if (queue == null && createIfNeeded) {
            MyConcurrentCircularQueue newQueue = new MyConcurrentCircularQueue(6);
            queue = responsedResults.putIfAbsent(appId, newQueue);
            if (queue == null) {
                queue = newQueue;
            }
        }
        return queue;
    }

    public static void init() throws EventMsgException, BackendServiceException {
    }

    public static long getViewCount(long msgId, String appUser, long zaloId, String sdkId) throws BackendServiceException {
        EventMsg msg = EventMsgDb.find(msgId);
        if (msg == null) {
            return -ECode.NOT_EXIST.getValue();
        }
        int activeDays = Utils.getDays(msg.getStartDate(), msg.getEndDate());
        long viewCount = 0;
//        long temp;
//        if (appUser != null) {
//            temp = TrackingDB.getViewCount(msg.getId(), "u" + appUser, activeDays);
//            viewCount += temp;
//            LOGGER.info(MsgBulider.format("View count of msgId[??], appUser[??] = ??", msgId, appUser, temp));
//        }
//        if (zaloId > 0) {
//            temp = TrackingDB.getViewCount(msg.getId(), "z" + String.valueOf(appUser), activeDays);
//            viewCount += temp;
//            LOGGER.info(MsgBulider.format("View count of msgId[??], zaloId[??] = ??", msgId, zaloId, temp));
//        }
//        if (deviceId != null) {
//            temp = TrackingDB.getViewCount(msg.getId(), "d" + deviceId, activeDays);
//            viewCount += temp;
//            LOGGER.info(MsgBulider.format("View count of deviceId[??], deviceId[??] = ??", msgId, deviceId, temp));
//        }
        viewCount = TrackingDB.getViewCount(msg.getId(), sdkId, activeDays);
        return viewCount;
    }

    @Override
    public void process(HttpServletRequest req, HttpServletResponse resp) {
        String sdkId = HReqParam.getString(req, "sdkId", null);

        byte[] data = null;
        if (ServletFileUpload.isMultipartContent(req)) {
            try {
                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List items = upload.parseRequest(req);
                for (Object obj : items) {
                    FileItem fileItem = (FileItem) obj;
                    if ("data".equals(fileItem.getFieldName()) && fileItem.getSize() > 0) {
                        data = fileItem.get();
                    }
                }
            } catch (FileUploadException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("[IN] sdkId = " + sdkId);
            LOGGER.info("[IN] data size  = " + (data == null ? "NULL" : data.length));
        }

        if (sdkId == null || data == null) {
            responseJSON(INVALID_REQUEST_RESULT, resp);
            LOGGER.warn("Missing Params (SdkId or Data is null).");
            return;
        }
        SdkInfo sdkInfos;
        try {
            sdkInfos = SdkModel.getInstance().validateSdkId(sdkId);
        } catch (Exception e) {
            responseJSON(INVALID_REQUEST_RESULT, resp);
            LOGGER.warn("Decrypt SdkId fail! " + e.getMessage(), e);
            return;
        }

        if (sdkInfos == null || sdkInfos.getMeta() == null) {
            responseJSON(INVALID_REQUEST_RESULT, resp);
            return;
        }

        JSONParser jsonParser = new JSONParser();
        String privateKey = sdkInfos.getMeta().get("privateKey");

        String stringData;
        JSONObject reqParams;
        try {
            stringData = AESEncrypt.decrypt(privateKey, data);
            reqParams = (JSONObject) jsonParser.parse(stringData);
        } catch (Exception e) {
            LOGGER.warn("Decrypt Data fail!" + e.getMessage());
            responseJSON(INVALID_REQUEST_RESULT, resp);
            return;
        }

        EventMsgRequest request = new EventMsgRequest(sdkInfos, reqParams);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("==================================================");
            LOGGER.info(reqParams);
        }
        LOGGER.info("==================================================");
        LOGGER.info(sdkInfos);
        LOGGER.info(request);

        String validateResult = request.validate();
        if (validateResult != null) {
            LOGGER.info("Validate result fail: " + validateResult);
            responseJSON(INVALID_REQUEST_RESULT, resp);
            return;
        }

        EventMsg resultMsg = null;
//        boolean hasLoginMsg = false;

        long clientIdentifier = sdkInfos.getSdkId();
//        String clientIdentifier = null;
//        if (request.getAppUser() != null && !request.getAppUser().trim().isEmpty()) {
//            clientIdentifier = request.getAppUser().trim();
//            if (clientIdentifier.equals(String.valueOf(request.getZaloId()))) { // Ios send zaloId in User Field.
//                clientIdentifier = "u" + clientIdentifier;
//            } else {
//                clientIdentifier = "z" + clientIdentifier;
//            }
//        } else if (request.getZaloId() != null) {
//            clientIdentifier = "z" + String.valueOf(request.getZaloId());
//        } else if (request.getDeviceId() != null && !request.getDeviceId().trim().isEmpty()) {
//            clientIdentifier = "d" + request.getDeviceId().trim();
//        }

//        if (clientIdentifier == null) {
//            LOGGER.warn(MsgBulider.format("Request doesn't have a clientIdentifier! appUser[??] zaloId[??] deviceId[??]", request.getAppUser(), request.getZaloId(), request.getDeviceId()));
//            responseJSON(INVALID_REQUEST_RESULT, resp);
//            return;
//        }
        List<EventMsg> msgs;

        if (request.getLoggedIn()) {
            msgs = CachedMsgModel.getLoggedInMsgs(sdkInfos.getAppId());
//            hasLoginMsg = (msgs != null && msgs.size() > 0);
        } else {
            msgs = CachedMsgModel.getCommonMsgs(sdkInfos.getAppId());
            List<EventMsg> loggedInMsg = CachedMsgModel.getLoggedInMsgs(sdkInfos.getAppId());
//            hasLoginMsg = (loggedInMsg != null && loggedInMsg.size() > 0);

        }

        EventMsgRequestInfo responseInfo = null;
        EventMsgRequestInfo temp = new EventMsgRequestInfo();
        temp.setTimestamp(System.currentTimeMillis());

        if (msgs != null) {
            for (EventMsg msg : msgs) {
                try {
                    if (checkCommonConditions(request, msg, clientIdentifier, temp)) {
                        if (hasUserConditions(msg)) {
                            if (checkUserConditions(request, msg, temp)) {
                                resultMsg = msg;
                                responseInfo = temp.deepCopy();
                                break;
                            }
                        } else {
                            resultMsg = msg;
                            responseInfo = temp.deepCopy();
                            break;
                        }
                    }
                } catch (BackendServiceException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                    resultMsg = null;
                }
            }
        }

        //TODO: Tracking view times.
        if (resultMsg != null) {
            ViewQuotaModel.increaseViewCount(resultMsg, clientIdentifier);
//            try {
//                TrackingDB.increaseViewCount(resultMsg.getId(), clientIdentifier);/////////////////////////////////////////////////////
//            } catch (BackendServiceException ex) {
//                LOGGER.error(ex.getMessage(), ex);
//                resultMsg = null;
//                responseInfo = null;
//            }
        } else {
            responseInfo = temp;
        }

        JSONObject result = new JSONObject();

        String html = "";
        String hash = "";
        long delayTime = DELAY_TIME; // default;
        if (resultMsg != null) {
            if (resultMsg.isSetDelayTime() && resultMsg.getDelayTime() >= 0) {
                delayTime = resultMsg.getDelayTime();
            }

            if (request.getPlatform() == MobilePlatform.ANDROID) {
                if (!resultMsg.getAndroidHtmlHash().equals(request.getMsgHash())) {
                    html = resultMsg.getAndroidHtml();
                }
                hash = resultMsg.getAndroidHtmlHash();
            } else if (request.getPlatform() == MobilePlatform.IOS) {
                if (!resultMsg.getIosHtmlHash().equals(request.getMsgHash())) {
                    html = resultMsg.getIosHtml();
                }
                hash = resultMsg.getIosHtmlHash();
            } else if (request.getPlatform() == MobilePlatform.WPHONE) {
                if (!resultMsg.getWphoneHtmlHash().equals(request.getMsgHash())) {
                    html = resultMsg.getWphoneHtml();
                }
                hash = resultMsg.getWphoneHtmlHash();
            }

            if (html.isEmpty()) {
                LOGGER.info("htmlHash is matched! Don't need response HTML content.");
            }

            result.put("errorCode", ErrorCode.SUCCESS.getCode());

        } else {
            result.put("errorCode", ErrorCode.NOT_ANY.getCode());
        }

        //TODO: inject data to html
        html = html.replaceAll("__ORIENTATION__", String.valueOf(request.getOrientation()));
        html = html.replaceAll("__SCREEN_WIDTH__", String.valueOf(request.getScreenWidth()));
        html = html.replaceAll("__SCREEN_HEIGHT__", String.valueOf(request.getScreenHeight()));

        result.put("html", html);
        result.put("msgHash", hash);
//        result.put("hasLoginMsg", hasLoginMsg);
        result.put("delayTime", delayTime);
        result.put("timeOut", TIME_OUT);

        responseJSON(result, resp);

        // saving result
        LOGGER.info(MsgBuilder.format("[OUT] errorCode[??], msgHash[??], msgId[??], delayTime[??], timeOut[??]", result.get("errorCode"), hash, resultMsg == null ? "" : resultMsg.getId(), result.get("delayTime"), result.get("timeOut")));

        if (responseInfo != null) {
            try {
                responseInfo.setPlatform(request.getPlatform());
                responseInfo.setOsVersion(sdkInfos.getOsVerrsion());
                responseInfo.setResponseCode((Integer) result.get("errorCode"));
                responseInfo.setAppVersion(request.getAppVersion());
                responseInfo.setSdkVersion(sdkInfos.getSdkVersion());
                responseInfo.setSdkId(sdkInfos.getSdkId());
                getResponsedResults(sdkInfos.getAppId(), true).add(responseInfo);
                LOGGER.info("Save to response queue: appId = " + sdkInfos.getAppId());
            } catch (Exception e) {
                LOGGER.info(e.getMessage(), e);
            }
        }
        LOGGER.info("==================================================");
    }

    private boolean checkCommonConditions(EventMsgRequest request, EventMsg msg, long clientIdentifier, EventMsgRequestInfo responseInfo) throws BackendServiceException {
        LOGGER.info(MsgBuilder.format("checkCommonConditions msgId[??] ", msg.getId()));
        if (System.currentTimeMillis() < msg.getStartDate() || msg.getEndDate() < System.currentTimeMillis()) {
            LOGGER.info("**Msg is out-of-date.");
            responseInfo.setReason("Msg is out-of-date");
            return false;
        }

        if (msg.getPlatforms().size() > 0 && !msg.getPlatforms().contains(request.getPlatform())) {
            LOGGER.info("**Platform not match, " + request.getPlatform());
            responseInfo.setReason("Platform not match");
            return false;
        }

        if (request.getPlatform() == MobilePlatform.IOS) {
            if (msg.getBundleIds().size() > 0 && !msg.getBundleIds().contains(request.getBundleId())) {
                LOGGER.info("**BundleId not match, " + request.getBundleId());
                responseInfo.setReason("BundleId not match");
                return false;
            }
        } else if (request.getPlatform() == MobilePlatform.ANDROID) {
            if (msg.getPackageNames().size() > 0 && !msg.getPackageNames().contains(request.getPackageName())) {
                LOGGER.info("**PackageName not match, " + request.getPackageName());
                responseInfo.setReason("PackageName not match");
                return false;
            }

        } else if (request.getPlatform() == MobilePlatform.WPHONE) {
            if (msg.getGuids().size() > 0 && !msg.getGuids().contains(request.getGuid())) {
                LOGGER.info("**Guid not match, " + request.getGuid());
                responseInfo.setReason("GUID not match");
                return false;
            }
        }

        if (msg.getSdkVersions().size() > 0 && !msg.getSdkVersions().contains(request.getSdkVersion())) {
            LOGGER.info("**SdkVersion not match, " + request.getSdkVersion());
            responseInfo.setReason("GUID not match");
            return false;
        }

        if (msg.getAppVersions().size() > 0 && !msg.getAppVersions().contains(request.getAppVersion())) {
            LOGGER.info("**AppVersion not match " + request.getAppVersion());
            responseInfo.setReason("AppVersion not match");
            return false;
        }

        if (ViewQuotaModel.isOutOfViewQuota(msg, clientIdentifier)) {
            LOGGER.info("**OUT OF QUOTA");
            responseInfo.setReason("Out of quota");
            return false;
        }

//        switch (msg.getPeriodType()) {
//            case NONE: {
//                int activeDays = Utils.getDays(msg.getStartDate(), msg.getEndDate());
//                long viewCount = TrackingDB.getViewCount(msg.getId(), clientIdentifier, activeDays);
//
//                responseInfo.setViewCounter(viewCount);
//                responseInfo.setViewLimit(msg.getVisibleTimes());
//
//                if (viewCount >= msg.getVisibleTimes()) {
//                    LOGGER.info("**Match limitation view viewCount=" + viewCount);
//                    responseInfo.setReason("View limited");
//                    return false;
//                }
//                break;
//            }
//            case DAY: {
//                long viewCount = TrackingDB.getViewCount(msg.getId(), clientIdentifier, 1);
//
//                responseInfo.setViewCounter(viewCount);
//                responseInfo.setViewLimit(msg.getVisibleTimes());
//
//                if (TrackingDB.getViewCount(msg.getId(), clientIdentifier, 1) >= msg.getVisibleTimes()) {
//                    LOGGER.info("**Match limitation view per day viewCount=" + viewCount);
//                    responseInfo.setReason("View limited");
//                    return false;
//                }
//                break;
//            }
//            default: {
//                LOGGER.warn("**Unsupported Period type " + msg.getPeriodType());
//            }
//        }
        return true;
    }

    private boolean hasUserConditions(EventMsg msg) {
        return msg.getZaloIds().size() > 0 || msg.getAppUsers().size() > 0;
    }

    private boolean checkUserConditions(EventMsgRequest request, EventMsg msg, EventMsgRequestInfo responseInfo) {
        if (msg.getAppUsers().size() > 0 && msg.getAppUsers().contains(request.getAppUser())) {
            return true;
        }

        if (msg.getZaloIds().size() > 0 && msg.getZaloIds().contains(request.getZaloId())) {
            return true;
        }
        LOGGER.info(MsgBuilder.format("**ZaloId[??] and AppUser[??] is not match!"));
        responseInfo.setReason("ZaloId/AppUser not match");
        return false;
    }

}
