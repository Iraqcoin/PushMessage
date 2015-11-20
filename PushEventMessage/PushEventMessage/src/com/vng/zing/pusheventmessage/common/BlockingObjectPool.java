/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class BlockingObjectPool<T> {

    private static Logger LOGGER = LogManager.getLogger(BlockingObjectPool.class);

    private BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    private ObjectFactory<T> factory;

    private boolean closed;

    public BlockingObjectPool(ObjectFactory<T> factory, int size) {
        this.factory = factory;
        for (int i = 0; i < size; i++) {
            queue.add(factory.create());
        }
    }

    public T borrow() throws InterruptedException {
        T result = null;
        while (!closed) {
            result = queue.poll(1000, TimeUnit.MILLISECONDS);
            if (result != null) {
                return result;
            }
        }
        return result;
    }

    public boolean returnObject(T t) {
        if (closed) {
            factory.destroy(t);
        }
        try {
            queue.put(t);
            return true;
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return false;
    }

    public void close() {
        while (true) {
            T poll = queue.poll();
            if (poll != null) {
                factory.destroy(poll);
            } else {
                return;
            }
        }
    }
}
