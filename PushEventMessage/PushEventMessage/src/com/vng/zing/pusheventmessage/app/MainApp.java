/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.app;

import com.vng.zing.jni.ClassLoaderUtil;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.db.EventMsgDb;
import com.vng.zing.pusheventmessage.entity.EventMsgException;
import com.vng.zing.pusheventmessage.model.CachedMsgModel;
import com.vng.zing.pusheventmessage.model.EventMsgModel;
import com.vng.zing.pusheventmessage.model.NotiInfoModel;
import com.vng.zing.pusheventmessage.model.OutOfQuotaViewModel;
import com.vng.zing.pusheventmessage.model.ViewQuotaModel;
import com.vng.zing.pusheventmessage.servers.HServers;
import com.vng.zing.pusheventmessage.servers.TServers;
import com.vng.zing.pusheventmessage.common.MyDailyTaskScheduler;
import org.quartz.SchedulerException;

/**
 *
 * @author namnt3
 */
public class MainApp {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(MainApp.class);

    public static int init() {
        ClassLoaderUtil.loadCommonLibs();

//        System.setProperty("java.net.useSystemProxies", "true");
//        System.setProperty("https.proxyHost", "10.30.12.30");
//        System.setProperty("https.proxyPort", "81");
//        System.setProperty("http.proxyHost", "10.30.12.30");
//        System.setProperty("http.proxyPort", "81");

        try {
            EventMsgDb.init();
            CachedMsgModel.init();
            EventMsgModel.init();
            
            ViewQuotaModel.init();
            
            OutOfQuotaViewModel.init();
            
            MyDailyTaskScheduler.init();
            
            NotiInfoModel.init();
        } catch (BackendServiceException ex) {
            LOGGER.error("App init error", ex);
            return -1;
        } catch (EventMsgException ex) {
            LOGGER.error("App init error", ex);
            return -1;
        } catch (SchedulerException ex) {
            LOGGER.error("App init error", ex);
            return -1;
        }

        return 0;
    }

    public static void start() {
        HServers hServers = new HServers();
        if (!hServers.setupAndStart()) {
            System.err.println("Could not start http servers! Exit now.");
            System.exit(1);
        }

        TServers tServers = new TServers();
        if (!tServers.setupAndStart()) {
            System.err.println("Could not start thrift servers! Exit now.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        if (init() < 0) {
            LOGGER.info("App init fail. Will now exit.");
            System.exit(1);
            return;
        }
        start();
    }
}
