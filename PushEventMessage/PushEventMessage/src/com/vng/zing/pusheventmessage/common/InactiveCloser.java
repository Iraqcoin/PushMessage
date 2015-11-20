/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import com.vng.zing.pusheventmessage.common.MsgBuilder;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class InactiveCloser {
    
    private static final Logger LOGGER = LogManager.getLogger(InactiveClose.class);
    
    private ConcurrentLinkedQueue<InactiveClose> list = new ConcurrentLinkedQueue<>();
    
    private long timeToLive = 0;
    
    public InactiveCloser(long timeToLiveValue) {
        this.timeToLive = timeToLiveValue;
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        check();
                        Thread.sleep(timeToLive);
                    }
                } catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }).start();
    }
    
    public void add(InactiveClose beClosed) {
        list.add(beClosed);
    }
    
    public void check() {
        Iterator<InactiveClose> iterator = list.iterator();
        while (iterator.hasNext()) {
            InactiveClose next = iterator.next();
            if (next.lastActiveTime() + timeToLive < System.currentTimeMillis()) {
                next.close();;
                iterator.remove();
            }
        }
    }
    
    public static void main(String args[]) {
        InactiveCloser ser = new InactiveCloser(3000);
        
        ser.add(new InactiveClose() {
            
            long x = System.currentTimeMillis();
            
            @Override
            public long lastActiveTime() {
                return x;
            }
            
            @Override
            public void close() {
                System.out.println("LOSE");
            }
        });
        
    }
}
