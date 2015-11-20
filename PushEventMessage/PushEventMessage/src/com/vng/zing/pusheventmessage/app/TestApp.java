/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.app;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.entity.ScheduledTask;
import com.vng.zing.pusheventmessage.service.SvTaskScheduler;

/**
 *
 * @author root
 */
public class TestApp {

    public static void main(String args[]) throws BackendServiceException {
        MainApp.init();

        SvTaskScheduler.addTask(10073, System.currentTimeMillis() + 3000, ScheduledTask.TYPE_PUSH_ANDROID_NOTI, "TASK1");

    }
}
