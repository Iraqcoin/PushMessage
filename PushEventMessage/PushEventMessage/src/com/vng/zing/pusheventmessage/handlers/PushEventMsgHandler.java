/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.handlers;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.db.EventMsgDb;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.entity.EventMsgException;
import com.vng.zing.pusheventmessage.model.CachedMsgModel;
import com.vng.zing.pusheventmessage.model.EventMsgModel;
import com.vng.zing.pusheventmessage.model.NotiInfoModel;
import com.vng.zing.pusheventmessage.model.OutOfQuotaViewModel;
import com.vng.zing.pusheventmessage.model.PushNotiModel;
import com.vng.zing.pusheventmessage.thrift.CountResult;
import com.vng.zing.pusheventmessage.thrift.EventMsg;
import com.vng.zing.pusheventmessage.thrift.EventMsgRequestInfo;
import com.vng.zing.pusheventmessage.thrift.EventMsgResult;
import com.vng.zing.pusheventmessage.thrift.GetLastRequestResult;
import com.vng.zing.pusheventmessage.thrift.GetPushNotiTask;
import com.vng.zing.pusheventmessage.thrift.GetPushNotiTaskPage;
import com.vng.zing.pusheventmessage.thrift.HasKeyFileResult;
import com.vng.zing.pusheventmessage.thrift.ListEventMsgResult;
import com.vng.zing.pusheventmessage.thrift.Noti;
import com.vng.zing.pusheventmessage.thrift.PeriodType;
import com.vng.zing.pusheventmessage.thrift.PushEventMsgService;
import com.vng.zing.pusheventmessage.thrift.PushNotiInfo;
import com.vng.zing.pusheventmessage.thrift.PushNotiResult;
import com.vng.zing.pusheventmessage.common.MyConcurrentCircularQueue;
import com.vng.zing.pusheventmessage.db.AppOwnerDb;
import com.vng.zing.pusheventmessage.thrift.GetAllScheduledTaskResult;
import com.vng.zing.pusheventmessage.thrift.GetAppOwnerResult;
import com.vng.zing.stats.Profiler;
import com.vng.zing.zcommon.thrift.ECode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.thrift.TException;

/**
 *
 * @author namnt3
 */
public class PushEventMsgHandler implements PushEventMsgService.Iface {

    private static final org.apache.log4j.Logger LOGGER = ZLogger.getLogger(PushEventMsgHandler.class);

    @Override
    public long createMsg(EventMsg eventMsg) throws TException {
        LOGGER.info("[IN] createMsg ");

        Profiler.createThreadProfiler("PushEventMsgHandler.createMsg", false);

        long result;
        try {
            result = EventMsgDb.create(eventMsg);
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        } catch (EventMsgException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        }

        Profiler.closeThreadProfiler();
        LOGGER.info("[OUT] createMsg ");
        return result;
    }

    @Override
    public EventMsgResult getEventMsg(long eventMsgId) throws TException {
        LOGGER.info("[IN] getEventMsg");

        Profiler.createThreadProfiler("PushEventMsgHandler.getEventMsg", false);

        EventMsgResult result = new EventMsgResult();
        try {
            EventMsg msg = EventMsgDb.find(eventMsgId);
            if (msg == null) {
                result.setResultCode(-ECode.NOT_EXIST.getValue());
            } else {
                result.setResultCode(0);
            }
            result.setEventMsg(msg);
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result.setResultCode(-ECode.C_FAIL.getValue());
        }
        Profiler.closeThreadProfiler();
        LOGGER.info("[OUT] getEventMsg ");
        return result;
    }

    @Override
    public int updateEventMsg(EventMsg eventMsg) throws TException {
        LOGGER.info("[IN] updateEventMsg ");
        Profiler.createThreadProfiler("PushEventMsgHandler.updateEventMsg", false);

        int result;
        try {
            EventMsg old = EventMsgDb.find(eventMsg.getId());
            if (old != null) {
                if (old.getPeriodType() == eventMsg.getPeriodType() && old.getVisibleTimes() < eventMsg.getVisibleTimes()) {
                    OutOfQuotaViewModel.resetBlackList(eventMsg.getId());
                } else if (old.getPeriodType() == PeriodType.NONE && eventMsg.getPeriodType() == PeriodType.DAY) {
                    OutOfQuotaViewModel.resetBlackList(eventMsg.getId());
                }
            }
            EventMsgDb.update(eventMsg);
            result = 0;
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        } catch (EventMsgException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        }
        Profiler.closeThreadProfiler();
        LOGGER.info("[OUT] updateEventMsg ");
        return result;
    }

    @Override
    public ListEventMsgResult findEventMsg(int appId) throws TException {
        LOGGER.info("[IN] findEventMsg appId=" + appId);

        Profiler.createThreadProfiler("PushEventMsgHandler.findEventMsg", false);

        ListEventMsgResult result = new ListEventMsgResult();
        try {
            List<EventMsg> msgs = EventMsgDb.findAndGetBasicInfo(appId);
            if (msgs == null) {
                result.setResultCode(-ECode.NOT_EXIST.getValue());
            } else {
                result.setResultCode(0);
                result.setEventMsg(msgs);
            }
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result.setResultCode(-ECode.C_FAIL.getValue());
        }
        Profiler.closeThreadProfiler();

        LOGGER.info("[OUT] findEventMsg appId=" + appId);
        return result;
    }

    @Override
    public long forceReloadEventMsg(int appId) throws TException {
        LOGGER.info("[IN] forceReloadEventMsg appId=" + appId);
        Profiler.createThreadProfiler("PushEventMsgHandler.forceReloadEventMsg", false);

        int result;

        List<EventMsg> reload = CachedMsgModel.reload(appId);
        if (reload == null) {
            result = -ECode.C_FAIL.getValue();
        } else {
            result = reload.size();
        }

        Profiler.closeThreadProfiler();

        LOGGER.info("[OUT] forceReloadEventMsg appId=" + appId + " --> " + result);
        return result;
    }

    @Override
    public int deleteEventMsg(long eventMsgId) throws TException {
        LOGGER.info("[IN] deleteEventMsg eventMsgId=" + eventMsgId);
        Profiler.createThreadProfiler("PushEventMsgHandler.deleteEventMsg", false);

        int result;
        try {
            boolean deleted = EventMsgDb.delete(eventMsgId);
            if (deleted) {
                result = 0;
            } else {
                result = -ECode.NOT_EXIST.getValue();
            }
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        }

        Profiler.closeThreadProfiler();

        LOGGER.info("[OUT] deleteEventMsg eventMsgId=" + eventMsgId + " --> " + result);
        return result;
    }

    @Override
    public long getViewCount(long msgId, String identifer) throws TException {
        LOGGER.info(MsgBuilder.format("[IN] getViewCount msgId[??], identifer[??]", msgId, identifer));
        Profiler.createThreadProfiler("PushEventMsgHandler.getViewCount", false);

        long result;
        try {
            String appId = identifer;
            long zaloId = -1L;
            try {
                zaloId = Long.parseLong(identifer);
            } catch (Exception e) {
            }
            String deviceId = identifer;
            result = EventMsgModel.getViewCount(msgId, appId, zaloId, deviceId);
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        }

        Profiler.closeThreadProfiler();

        LOGGER.info(MsgBuilder.format("[OUT] getViewCount msgId[??], identifier[??] --> ??", msgId, identifer, result));
        return result;
    }

    @Override
    public GetLastRequestResult getLastRequest(int appId) throws TException {
        LOGGER.info("[IN] getLastReqest appId=" + appId);

        Profiler.createThreadProfiler("PushEventMsgHandler.getLastRequest", false);

        GetLastRequestResult result = new GetLastRequestResult();
        result.setResultCode(0);
        List<EventMsgRequestInfo> msgs = new ArrayList<EventMsgRequestInfo>();
        MyConcurrentCircularQueue responsedResults = EventMsgModel.getResponsedResults(appId, false);
        if (responsedResults != null) {
            msgs.addAll(responsedResults.getAll());
        }
        result.setEventMsgRequests(msgs);
        Profiler.closeThreadProfiler();

        LOGGER.debug("[OUT] getLastReqest appId=" + appId + " > size = " + msgs.size());
        return result;
    }

    @Override
    public PushNotiResult pushAndroidNoti(int appId, Noti noti, List<String> appVersion, List<String> osVersion, List<String> sdkVersion, List<String> packageNames, List<Long> zaloId, List<String> appUser, long time) throws TException {
        LOGGER.info("[IN] pushAndroidNoti appId=" + appId);

        Profiler.createThreadProfiler("PushEventMsgHandler.pushAndroidNoti", false);

        PushNotiResult result = new PushNotiResult();
        try {
            String requestId = PushNotiModel.pushAndroidNoti(appId, noti, appVersion, osVersion, sdkVersion, packageNames, zaloId, appUser, time);
            result.setCode(0);
            result.setRequestId(requestId);
        } catch (BackendServiceException ex) {
            result.setCode(-ECode.C_FAIL.getValue());
            LOGGER.error(ex.getMessage(), ex);
        }

        Profiler.closeThreadProfiler();

        LOGGER.debug("[OUT] pushAndroiNoti appId=" + appId + " > result=" + result);
        return result;
    }

    @Override
    public int saveAndroidDeviceInfo(long sdkId, int appId, String platform, String token, String appVersion, String osVersion, String sdkVersion, String packageName) throws TException {
        LOGGER.info("[IN] saveAndroidDeviceInfo() appId=" + appId);

        int result = 0;

        try {
            PushNotiModel.saveDevice(sdkId, appId, platform, token, appVersion, osVersion, sdkVersion, packageName);
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        }
        LOGGER.info("[IN] saveAndroidDeviceInfo() >");
        return result;
    }

    @Override
    public GetPushNotiTask getPushNotiTask(String requestId) throws TException {
        LOGGER.info("[IN] getPushNotiTask() requestId=" + requestId);
        GetPushNotiTask result = new GetPushNotiTask();
        try {
            PushNotiInfo info = PushNotiModel.getInfo(requestId);
            result.setCode(0);
            result.setInfo(info);
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result.setCode(-ECode.C_FAIL.getValue());
        }
        LOGGER.info("[IN] getPushNotiTask() >");
        return result;
    }

    @Override
    public int setApnsKeyFile(int appId, ByteBuffer file, String keyPass) throws TException {
        LOGGER.info(MsgBuilder.format("[IN] setApnsKeyFile() appId=??, size=?? byte", appId, file.array().length));

        int result = 0;
        try {
            NotiInfoModel.getInst().saveApnsKeyFile(appId, file, keyPass);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        }

        LOGGER.info("[IN] setApnsKeyFile() > " + result);
        return result;
    }

    @Override
    public PushNotiResult pushIosNoti(int appId, Noti noti, List<String> appVersion, List<String> osVersion, List<String> sdkVersion, List<String> packageNames, List<Long> zaloId, List<String> appUser, long time) throws TException {
        LOGGER.info("[IN] pushIosNoti appId=" + appId);

        Profiler.createThreadProfiler("PushEventMsgHandler.pushIosNoti", false);

        PushNotiResult result = new PushNotiResult();
        try {
            String requestId = PushNotiModel.pushIosNoti(appId, noti, appVersion, osVersion, sdkVersion, packageNames, zaloId, appUser, time);
            result.setCode(0);
            result.setRequestId(requestId);
        } catch (BackendServiceException ex) {
            result.setCode(-ECode.C_FAIL.getValue());
            LOGGER.error(ex.getMessage(), ex);
        }

        Profiler.closeThreadProfiler();

        LOGGER.debug("[OUT] pushIosNoti appId=" + appId + " > result=" + result);
        return result;
    }

    @Override
    public int createAppTable(int appId) throws TException {
        LOGGER.info(MsgBuilder.format("[IN] createAppTable() appId=??", appId));

        int result = 0;
        try {
            NotiInfoModel.getInst().createApp(appId);
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        }

        LOGGER.info("[IN] createAppTable() > " + result);
        return result;
    }

    @Override
    public int setGoogleApiKey(int appId, String apiKey) throws TException {
        LOGGER.info(MsgBuilder.format("[IN] setGoogleApiKey() appId=??", appId));

        int result = 0;
        try {
            NotiInfoModel.getInst().setGoogleApiKey(appId, apiKey);
        } catch (BackendServiceException ex) {
            LOGGER.error(ex.getMessage(), ex);
            result = -ECode.C_FAIL.getValue();
        }

        LOGGER.info("[IN] setGoogleApiKey() > " + result);
        return result;
    }

    @Override
    public GetPushNotiTaskPage getPushNotiTaskPage(int appId, int offset, int size) throws TException {
        LOGGER.info("[IN] getPushNotiTaskPage appId=" + appId);

        Profiler.createThreadProfiler("PushEventMsgHandler.getPushNotiTaskPage", false);

        GetPushNotiTaskPage result = new GetPushNotiTaskPage();
        try {
            List<PushNotiInfo> page = PushNotiModel.getPushNotiPage(appId, offset, size);
            result.setInfo(page);
        } catch (BackendServiceException ex) {
            result.setCode(-ECode.C_FAIL.getValue());
            LOGGER.error(ex.getMessage(), ex);
        }

        Profiler.closeThreadProfiler();

        LOGGER.debug("[OUT] getPushNotiTaskPage appId=" + appId + " > code=" + result.getCode());
        return result;
    }

    @Override
    public CountResult countPushNotiTask(int appId) throws TException {
        LOGGER.info("[IN] countPushNotiTask appId=" + appId);

        Profiler.createThreadProfiler("PushEventMsgHandler.countPushNotiTask", false);

        CountResult result = new CountResult();
        try {
            result.setCount(PushNotiModel.countPushNotiTask(appId));
        } catch (BackendServiceException ex) {
            result.setCode(-ECode.C_FAIL.getValue());
            LOGGER.error(ex.getMessage(), ex);
        }

        Profiler.closeThreadProfiler();

        LOGGER.debug("[OUT] countPushNotiTask appId=" + appId + " > " + result.getCode());
        return result;
    }

    @Override
    public int hasApnsKeyFile(int appId) throws TException {
        LOGGER.info(MsgBuilder.format("[IN] hasApnsKeyFile() appId=??", appId));

        int result = 0;
        if (NotiInfoModel.hasApnsKeyFile(appId)) {
            result = 1;
        }

        LOGGER.info("[IN] hasApnsKeyFile() > " + result);
        return result;
    }

    @Override
    public int hasGcmKey(int appId) throws TException {
        LOGGER.info(MsgBuilder.format("[IN] hasGcmKey() appId=??", appId));

        int result = 0;

        try {
            if (NotiInfoModel.hasGcmKey(appId)) {
                result = 1;
            }
        } catch (BackendServiceException ex) {
            result = (-ECode.C_FAIL.getValue());
        }

        LOGGER.info("[IN] hasGcmKey() > " + result);
        return result;
    }

    @Override
    public GetAllScheduledTaskResult getAllScheduledTask(int appId) throws TException {
        LOGGER.info("[IN] countPushNotiTask appId=" + appId);

        Profiler.createThreadProfiler("PushEventMsgHandler.countPushNotiTask", false);

        GetAllScheduledTaskResult result = new GetAllScheduledTaskResult();
        try {
            result.setCode(0);
            PushNotiModel.countPushNotiTask(appId);
        } catch (BackendServiceException ex) {
            result.setCode(-ECode.C_FAIL.getValue());
            LOGGER.error(ex.getMessage(), ex);
        }

        Profiler.closeThreadProfiler();

        LOGGER.debug("[OUT] countPushNotiTask appId=" + appId + " > " + result.getCode());
        return result;
    }

    @Override
    public int setAppOwner(int appId, long userId) throws TException {
        LOGGER.info(MsgBuilder.format("[IN] setAppOwner() appId=??, userId??", appId, userId));

        int result = 0;

        try {
            AppOwnerDb.addOwner(userId, appId);
        } catch (BackendServiceException ex) {
            result = (-ECode.C_FAIL.getValue());
        }

        LOGGER.info("[IN] setAppOwner() > " + result);
        return 0;
    }

    @Override
    public int removeAppOwner(int appId, long userId) throws TException {
        LOGGER.info(MsgBuilder.format("[IN] removeAppOwner() appId=??, userId??", appId, userId));

        int result = 0;

        try {
            AppOwnerDb.removeOwner(userId, appId);
        } catch (BackendServiceException ex) {
            result = (-ECode.C_FAIL.getValue());
        }

        LOGGER.info("[IN] removeAppOwner() > " + result);
        return 0;
    }

    @Override
    public GetAppOwnerResult getAppOwner(int appId) throws TException {
        LOGGER.info("[IN] getAppOwner appId=" + appId);

        Profiler.createThreadProfiler("PushEventMsgHandler.getAppOwner", false);

        GetAppOwnerResult result = new GetAppOwnerResult();
        try {
            result.setCode(0);
            result.setUser(AppOwnerDb.getAppOwner(appId));
        } catch (BackendServiceException ex) {
            result.setCode(-ECode.C_FAIL.getValue());
            LOGGER.error(ex.getMessage(), ex);
        }

        Profiler.closeThreadProfiler();

        LOGGER.debug("[OUT] countPushNotiTask appId=" + appId + " > " + result.getCode());
        return result;
    }
}
