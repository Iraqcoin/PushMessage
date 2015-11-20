/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.noti;

import com.vng.zing.pusheventmessage.app.MainApp;
import com.vng.zing.pusheventmessage.thrift.Noti;
import com.vng.zing.pusheventmessage.common.BlockingObjectPool;
import com.vng.zing.pusheventmessage.common.ObjectFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class IosNotiPusher {

    private static final Logger LOGGER = LogManager.getLogger(IosNotiPusher.class);

    private NsConnectionPool connections;

    private String keyFilePath;

    public IosNotiPusher(String keyFilePath, String keyPass, Noti noti, boolean production) throws Exception {
        this.keyFilePath = keyFilePath;

        LOGGER.info("INIT IosNotiPusher, production = " + production);

        File f = new File(keyFilePath);
        if (!f.canRead() && !f.isFile()) {
            throw new FileNotFoundException(keyFilePath + " can't be read");
        }

        if (production) {
            connections = NsConnectionPool.createProductionPool(keyFilePath, keyPass, noti);
        } else {
            connections = NsConnectionPool.createDevPool(keyFilePath, keyPass, noti);
        }
    }

    public CountDownLatch push(List<String> tokens) {
        final CountDownLatch latch = new CountDownLatch(tokens.size());

        for (String token : tokens) {
            connections.submit(latch, token);
        }

        return latch;
    }

    public void close() {
        connections.close();
    }

    public static void main(String args[]) throws Exception {
        MainApp.init();

        Noti noti = new Noti()
                .setMessage("hehe")
                .setTitle("aaaa")
                .setSound("default");
        IosNotiPusher push = new IosNotiPusher("/home/namnt3/Downloads/abcdef/push-cert-zmusicstore-live.p12", "123456", noti, true);
//        IosNotiPusher push = new IosNotiPusher("/home/namnt3/Downloads/abcdef/push-cert-zmusicstore-sandbox.p12", "123456", noti, false);

        CountDownLatch result = null;
        for (int i = 0; i < 1; i++) {
            if (result != null) {
                result.await();
                LOGGER.info("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDONE ROUND " + (i - 1));
            }
            List<String> a = new ArrayList<>();
//            a.add(String.valueOf("c760a2ff1fc0654da2c2cdb4a065e561bcb4584b8f892e40d043ba26b69befd1"));
//            a.add(String.valueOf("f98bb93246b9f5f46ef5bbdb655639e79971ab4bb18da61d91d9920cc75ea1ce"));
//            a.add(String.valueOf("08b67a610179504f8696ec803c2a274fd8989cda5e9ae5bba5476a1d2dc2bfaa"));
//            a.add(String.valueOf("df990882f0801816fe2648fe2279bc79f60845a70535e727c53ec9c305f4eb4b"));
//            a.add(String.valueOf("1af574416b49e107457182343bfa5c26860788e143f1ac207616e2cc25a24b0c"));
//            a.add(String.valueOf("8e8741511ca8d98d80a02682bc5d8687cdba4da7d201f525b2ef633b47e8d068"));   //0
//            a.add(String.valueOf("c817cc44ffa31505bf0d665b29323ff15d9c457d2554b0c47acfb800e8a774f3"));
//            a.add(String.valueOf("a0ee52fdb5e3294b5ad190e2f4caa1c38772bfb9e83ebac198fb4948b96ff297"));
//            a.add(String.valueOf("5359b7ed947eb678e6047da4c8cbbffdd9812bc3c9685176debe380dbe185634"));
//            a.add(String.valueOf("8c1b03c847b3c1180a640d7a1208eaf369214e306afb10f9e5b21f0e5f39e21a")); //9

//            a.add(String.valueOf("c760a2ff1fc0654da2c2cdb4a065e561bcb4584b8f892e40d043ba26b69befd1"));
            a.add(String.valueOf("a0c2cc8d040404b669eb44b9cd1ca9930b97302cf78976d6eed3baf72451ae6e")); //8
//            a.add(String.valueOf("e23cde7b739580d3164d3e51219bc4656f37147f842141e2835cb13f045d8f5a")); // 7

//            a.add(String.valueOf("c760a2ff1fc0654da2c2cdb4a065e561bcb4584b8f892e40d043ba26b69befd1"));
//            a.add(String.valueOf("9e22ce5578d1f0b2bb8b55467801b6dedee0490e5fd3c8126bf25ff66fc17559")); //6

//            a.add(String.valueOf("c760a2ff1fc0654da2c2cdb4a065e561bcb4584b8f892e40d043ba26b69befd1"));
            result = push.push(a);
        }

        result.await();
        push.close();
        System.out.println("DONE");
//        BlockingObjectPool<String> str = new BlockingObjectPool<>(new ObjectFactory<String>() {
//
//            private int i = 0;
//
//            @Override
//            public String create() {
//                return String.valueOf(i++);
//            }
//
//            @Override
//            public void destroy(String obj) {
//                System.out.println("close " + obj);
//            }
//        }, 4);
//
//        for (int i = 0; i < 100; i++) {
//            String x = str.borrow();
//            System.out.println(x);
//            str.returnObject(x);
//        }
    }

}
