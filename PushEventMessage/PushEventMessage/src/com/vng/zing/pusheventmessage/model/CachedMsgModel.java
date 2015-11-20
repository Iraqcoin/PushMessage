/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.model;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.db.EventMsgDb;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.thrift.EventMsg;
import com.vng.zing.pusheventmessage.thrift.PeriodType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author namnt3
 */
public class CachedMsgModel {

    private static final Logger LOGGER = Logger.getLogger(CachedMsgModel.class);

    private static final Map<Integer, List<EventMsg>> commonMsgs = new HashMap<Integer, List<EventMsg>>();

    private static final Map<Integer, List<EventMsg>> loggedInMsgs = new HashMap<Integer, List<EventMsg>>();

    private static final Set<Integer> loadedAppIds = new HashSet<Integer>();

    public static void init() throws BackendServiceException {
        List<EventMsg> activeMsgs = EventMsgDb.getActiveMsg();
        LOGGER.info(MsgBuilder.format("Loading activeMsgs, size = ??", activeMsgs.size()));
        for (EventMsg msg : activeMsgs) {
            LOGGER.info(MsgBuilder.format("NEW Active msg[msgId=??]", msg.getId()));
            List<EventMsg> list = null;
            if (msg.isLoggedIn()) {
                list = commonMsgs.get(msg.getAppId());
                if (list == null) {
                    list = new ArrayList<EventMsg>();
                }
                commonMsgs.put(msg.getAppId(), list);

            } else {
                list = loggedInMsgs.get(msg.getAppId());
                if (list == null) {
                    list = new ArrayList<EventMsg>();
                }
                loggedInMsgs.put(msg.getAppId(), list);
            }
            list.add(msg);
            OutOfQuotaViewModel.loadBlacklist(msg.getId());
        }
    }

    public static synchronized List<EventMsg> reload(int appId) {
        LOGGER.info(MsgBuilder.format("Reload App[appid=??] Event Messages", appId));
        List<EventMsg> newMessages = null;
        try {
            newMessages = EventMsgDb.getActiveMsg(appId);
        } catch (BackendServiceException ex) {
            LOGGER.error(MsgBuilder.format("Can't load Active Msg of app[??] - ??", appId, ex.getMessage()), ex);
        }
        if (newMessages == null) {
            LOGGER.error(MsgBuilder.format("Load app[??] msg fail!", appId));
        } else if (newMessages.size() > 0) {
            List<EventMsg> commons = new ArrayList<EventMsg>();
            List<EventMsg> logged = new ArrayList<EventMsg>();
            for (EventMsg msg : newMessages) {
                if (msg.isLoggedIn()) {
                    logged.add(msg);
                } else {
                    commons.add(msg);
                }
            }
            LOGGER.info(MsgBuilder.format("App[appId=??] has [??] before login msgs", appId, commons.size()));
            LOGGER.info(MsgBuilder.format("App[appId=??] has [??] logged-in msgs", appId, logged.size()));
            commonMsgs.put(appId, commons);
            loggedInMsgs.put(appId, logged);
        } else {
            commonMsgs.remove(appId);
            loggedInMsgs.remove(appId);
        }
        return newMessages;
    }

    public static synchronized void loadAppMsgs(int appId) {
        if (!loadedAppIds.contains(appId)) {
            reload(appId);
            loadedAppIds.add(appId);
        }
    }

    public static synchronized void reloadAll() {
        loadedAppIds.clear();
    }

    public static List<EventMsg> getCommonMsgs(int appId) {
        if (!loadedAppIds.contains(appId)) {
            loadAppMsgs(appId);
        }
        return commonMsgs.get(appId);
    }

    public static List<EventMsg> getLoggedInMsgs(int appId) {
        if (!loadedAppIds.contains(appId)) {
            loadAppMsgs(appId);
        }
        return loggedInMsgs.get(appId);
    }

    public static List<EventMsg> getDailyResetMsg() {
        List<EventMsg> msgs = new ArrayList<EventMsg>();
        for (List<EventMsg> list : commonMsgs.values()) {
            for (EventMsg msg : list) {
                if (msg.getPeriodType() == PeriodType.DAY) {
                    msgs.add(msg);
                }
            }
        }
        for (List<EventMsg> list : loggedInMsgs.values()) {
            for (EventMsg msg : list) {
                if (msg.getPeriodType() == PeriodType.DAY) {
                    msgs.add(msg);
                }
            }
        }
        return msgs;
    }
}
