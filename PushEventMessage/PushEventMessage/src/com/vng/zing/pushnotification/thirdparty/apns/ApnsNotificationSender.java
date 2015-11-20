/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.thirdparty.apns;

import com.vng.zing.pushnotification.thirdparty.apns.utils.FeedbackTuple;
import com.vng.zing.pushnotification.thirdparty.apns.utils.Frame;
import com.vng.zing.pushnotification.thirdparty.apns.utils.ResponseFrame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Random;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author namnt3
 */
public class ApnsNotificationSender {

    private static final Logger LOGGER = LogManager.getLogger(ApnsNotificationSender.class);

    private static final int PORT = 2195;
    private static final String PRODUCT_GATEWAY = "gateway.push.apple.com";
    private static final String DEVELOPMENT_GATEWAY = "gateway.sandbox.push.apple.com";

    private static final char[] keypass = "wshr.ut".toCharArray();

    private SSLSocketFactory factory;

    private boolean production;

    public ApnsNotificationSender(String p12, String key, boolean production) {
        factory = getFactory(new File(p12), key);

        if (factory == null) {
            throw new RuntimeException("Loading key files fail");
        }
        this.production = production;
    }

    public Socket getConnection() throws IOException {
        if (production) {
            InetAddress[] inet = Inet4Address.getAllByName(PRODUCT_GATEWAY);

            int i = new Random().nextInt(inet.length);
            return factory.createSocket(inet[i], PORT);
        }
        InetAddress[] inet = Inet4Address.getAllByName(DEVELOPMENT_GATEWAY);
        int i = new Random().nextInt(inet.length);
        return factory.createSocket(inet[i], PORT);
    }

    private SSLSocketFactory getFactory(File pKeyFile, String pKeyPassword) {
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            InputStream keyInput = new FileInputStream(pKeyFile);
            keyStore.load(keyInput, pKeyPassword.toCharArray());
            keyInput.close();

            keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

            return context.getSocketFactory();
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (KeyStoreException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (CertificateException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (UnrecoverableKeyException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (KeyManagementException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void start(ApnsNotificationSender sender) throws IOException {

        Socket connection = sender.getConnection();
        System.err.println("CONNECTED " + connection.getRemoteSocketAddress());

        JSONObject json = new JSONObject();

//        json.put("aps", new JsonObject().put("alert", new JsonObject().put("title", "myTitle").put("body", "myBody")).put("sound", "default"));
        byte[] bytes = hexStringToByteArray("a0c2cc8d040404b669eb44b9cd1ca9930b97302cf78976d6eed3baf72451ae6e");
        System.out.println(bytes.length);

        int expiry = new Long(System.currentTimeMillis() / 1000L).intValue();
        System.out.println("expiry " + expiry);

        final byte[] frame = Frame.create()
                .deviceToken(bytes)
                .expirationDate(expiry)
                .payload(json.toString())
                .notificationIdentifier(3)
                .highPriority()
                .build();

        final DataOutputStream out = new DataOutputStream(connection.getOutputStream());

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        out.write(frame);
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }).start();

        final DataInputStream in = new DataInputStream(connection.getInputStream());

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        System.out.println(ResponseFrame.read(in));
                    }
                } catch (IOException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }).start();
    }

    public static void main(String args[]) throws IOException {
        String p12 = "/home/namnt3/NetBeansProjects/nowmsg/src/main/resources/push-cert-zmusicstore-live.p12";

        ApnsNotificationSender sender = new ApnsNotificationSender(p12, "123456", true);
        start(sender);
        FeedbackService feedback = new FeedbackService(p12, "123456", true);
        feedback.setHandler(new Handler<FeedbackTuple>() {
            @Override
            public void handle(FeedbackTuple event) {
                System.err.println(event);
            }
        });

        feedback.start();

//        start(sender);
//        start(sender);
//        final IpSelector ip = new IpSelector(PRODUCT_GATEWAY);
//        int k = Benchmarker.inst().addAction(PRODUCT_GATEWAY);
//        Benchmarker.inst().loop(1, k, new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    ip.nextAddress();
//                } catch (UnknownHostException ex) {
//                    Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
    }
}
