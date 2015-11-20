/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.thirdparty.apns;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author namnt3
 */
public class IpSelector {

    private final String domain;

    private final List<String> selected = new ArrayList<>();

    private final Random random = new Random();

    public IpSelector(String domain) {
        this.domain = domain;
    }

    public String nextAddress() throws UnknownHostException {
        return domain;
    }

//    public String nextAddress() throws UnknownHostException {
//        InetAddress[] inet = Inet4Address.getAllByName(domain);
//
//        String ip = null;
//
//        for (InetAddress address : inet) {
//            if (!selected.contains(address.getHostAddress())) {
//                ip = address.getHostAddress();
//                break;
//            }
//        }
//
//        if (ip != null) {
//            selected.add(ip);
//            return ip;
//        } else {
//            return selected.get(random.nextInt(selected.size()));
//        }
//    }

}
