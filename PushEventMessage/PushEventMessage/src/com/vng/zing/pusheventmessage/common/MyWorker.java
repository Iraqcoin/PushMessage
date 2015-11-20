/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import com.vng.zing.pusheventmessage.model.ViewQuotaModel;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

/**
 *
 * Start running tasks in two cases: <br/>
 * 1. taskQueue.size is greater than or equal taskSize; <br/>
 * 2. delayTime timer is over;
 *
 * @author namnt3
 */
public class MyWorker<T> {

    private static final Logger LOGGER = Logger.getLogger(MyWorker.class);

    private LinkedBlockingQueue<T> taskQueue = new LinkedBlockingQueue();

    private TaskHandler<T> taskHandler;

    private final long delayTime;
    private final int taskSize;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private AtomicBoolean running = new AtomicBoolean(false);

    public MyWorker(long delayTime, int taskSize) {
        this.delayTime = delayTime;
        this.taskSize = taskSize;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                process(true);
            }
        }, delayTime, delayTime);
    }

    public void addTask(T obj) {
        taskQueue.add(obj);
        if (taskQueue.size() >= taskSize) {
            process(false);
        }
    }

    private void process(final boolean doFirstSmallChunk) {
        if (!running.get()) {
            LOGGER.info("START processing");
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    running.set(true);
                    try {
                        if (taskHandler != null && taskQueue.size() > 0) {
                            if (doFirstSmallChunk) {
                                ArrayList todoList = new ArrayList(taskSize);
                                int dateCount = 0;
                                for (int i = 0; i < taskSize; i++) {
                                    ViewQuotaModel.IncreaseViewRequest task = (ViewQuotaModel.IncreaseViewRequest) taskQueue.peek();
                                    if (task == null) {
                                        break;
                                    }
                                    int days = task.getDays();
                                    if (days >= 200) {
                                        LOGGER.error("Message DURATION days > 200  -> skip");
                                        taskQueue.poll();
                                        continue;
                                    }
                                    if (dateCount + days < 200) {
                                        dateCount += days;
                                        todoList.add(taskQueue.poll());
                                    } else {
                                        break;
                                    }
                                }
                                taskHandler.handle(todoList);
                            }
                            while (taskQueue.size() >= taskSize) {
                                ArrayList todoList = new ArrayList(taskSize);
                                int dateCount = 0;
                                for (int i = 0; i < taskSize; i++) {
                                    ViewQuotaModel.IncreaseViewRequest task = (ViewQuotaModel.IncreaseViewRequest) taskQueue.peek();
                                    if (task == null) {
                                        break;
                                    }
                                    int days = task.getDays();
                                    if (days >= 200) {
                                        LOGGER.error("Message DURATION days > 200  -> skip");
                                        taskQueue.poll();
                                        continue;
                                    }
                                    if (dateCount + days < 200) {
                                        dateCount += days;
                                        todoList.add(taskQueue.poll());
                                    } else {
                                        break;
                                    }
                                }
                                taskHandler.handle(todoList);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    running.set(false);
                }
            });
        }
    }

    public TaskHandler getTaskHandler() {
        return taskHandler;
    }

    public void setTaskHandler(TaskHandler<T> taskHandler) {
        this.taskHandler = taskHandler;
    }

}
