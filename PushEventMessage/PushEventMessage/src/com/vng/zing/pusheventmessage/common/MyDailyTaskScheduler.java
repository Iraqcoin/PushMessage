/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import com.vng.zing.pusheventmessage.model.CachedMsgModel;
import com.vng.zing.pusheventmessage.model.OutOfQuotaViewModel;
import com.vng.zing.pusheventmessage.thrift.EventMsg;
import com.vng.zing.stats.Profiler;
import com.vng.zing.stats.ThreadProfiler;
import java.util.List;
import org.apache.log4j.Logger;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import org.quartz.Job;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author root
 */
public class MyDailyTaskScheduler implements Job {

    private static final Logger LOGGER = Logger.getLogger(MyDailyTaskScheduler.class);

    public static void init() throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();

        JobDetail job = newJob(MyDailyTaskScheduler.class)
                .withIdentity("job1", "group1")
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("resetBlackList", "group1")
                .withSchedule(dailyAtHourAndMinute(0, 00))
                .build();

        sched.scheduleJob(job, trigger);
        sched.start();
    }

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        ThreadProfiler threadProfiler = Profiler.createThreadProfiler("MyDailyTaskScheduler", false);

//        threadProfiler.push(MyDailyTaskScheduler.class, "resetDailyMsg");
        LOGGER.info("START DAILY JOBs");
        List<EventMsg> dailyResetMsg = CachedMsgModel.getDailyResetMsg();
        for (EventMsg msg : dailyResetMsg) {
            OutOfQuotaViewModel.resetBlackList(msg.id);
        }
//        threadProfiler.pop(MyDailyTaskScheduler.class, "resetDailyMsg");

        Profiler.closeThreadProfiler();
    }

}
