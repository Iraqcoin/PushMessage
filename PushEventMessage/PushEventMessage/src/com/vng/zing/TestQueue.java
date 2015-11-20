/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing;

import com.vng.zing.pusheventmessage.common.MyConcurrentCircularQueue;
import java.util.Iterator;

/**
 *
 * @author root
 */
public class TestQueue {

    public static void main(String args[]) {
        MyConcurrentCircularQueue<String> x = new MyConcurrentCircularQueue<>(5);
        x.add("a");
        x.add("b");
        x.add("c");
        x.add("d");
        x.add("e");
        x.add("f");

        String str = (String) x.asQueue().poll();
        Iterator iterator = x.asQueue().iterator();
        while (str != null) {
            System.out.println(str);
            str = (String) x.asQueue().poll();
        }

    }
}
