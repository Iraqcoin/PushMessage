/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.collections4.queue.CircularFifoQueue;

/**
 *
 * @author root
 */
public class MyConcurrentCircularQueue<T> {

    private final CircularFifoQueue<T> queue;

    public MyConcurrentCircularQueue(int size) {
        queue = new CircularFifoQueue(size);
    }

    public synchronized Collection<T> getAll() {
        ArrayList<T> list = new ArrayList<T>();
        if (queue.size() > 0) {
            for (T t : queue) {
                list.add(t);
            }
        }
        return (Collection<T>) list;
    }

    public synchronized void add(T obj) {
        queue.add(obj);
    }

    public CircularFifoQueue asQueue() {
        return queue;
    }
}
