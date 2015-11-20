/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.thirdparty.apns;

import com.vng.zing.pusheventmessage.app.MainApp;
import com.vng.zing.pusheventmessage.common.InactiveClose;
import com.vng.zing.pusheventmessage.noti.NsConnectionPool;
import com.vng.zing.pushnotification.thirdparty.apns.utils.FeedbackTuple;
import com.vng.zing.pushnotification.thirdparty.apns.utils.MySocket;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;
import org.apache.log4j.LogManager;

/**
 *
 * @author namnt3
 */
public class FeedbackService implements InactiveClose {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(FeedbackService.class);

    private static final String APPLE_FEEDBACK_PRO = "feedback.push.apple.com";
    private static final String APPLE_FEEDBACK_DEV = "feedback.sandbox.push.apple.com";
    private static final int APPLE_FEEDBACK_PORT = 2196;

    private ExecutorService exe = Executors.newFixedThreadPool(2);

    private Handler<FeedbackTuple> handler;
    private final SSLSocketFactory factory;
    private String address;

    private Socket socket;

    private long lastReceive = 0;

    public FeedbackService(String p12, String key, boolean production) {
        factory = MySocket.getFactory(new File(p12), key);
        if (production) {
            address = APPLE_FEEDBACK_PRO;
        } else {
            address = APPLE_FEEDBACK_DEV;
        }
    }

    public void setHandler(Handler<FeedbackTuple> handler) {
        this.handler = handler;
    }

    public void stop() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                LOGGER.info(ex.getMessage());
            }
        }
    }

    public void start() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LOGGER.info("Create connection to " + address);
                    System.out.println(address);
                    socket = factory.createSocket(NsConnectionPool.getDefaultProxySocket(address), address, APPLE_FEEDBACK_PORT, true);

                    DataInputStream stream = new DataInputStream(socket.getInputStream());

                    while (true) {
                        final FeedbackTuple read = FeedbackTuple.read(stream);
                        lastReceive = System.currentTimeMillis();

                        exe.submit(new Runnable() {
                            @Override
                            public void run() {
                                handler.handle(read);
                            }
                        });
                    }
                } catch (IOException ex) {
                    LOGGER.info("Disconnected to " + address);
                }
            }
        }).start();
    }

    @Override
    public long lastActiveTime() {
        return lastReceive;
    }

    @Override
    public void close() {
        this.stop();
    }

    public static void main(String args[]) throws IOException {
        MainApp.init();
        FeedbackService sv = new FeedbackService("/home/namnt3/Downloads/abcdef/push-cert-zmusicstore-live.p12", "123456", true);
//        FeedbackService sv = new FeedbackService("/home/namnt3/Downloads/abcdef/push-cert-zmusicstore-sandbox.p12", "123456", false);
        sv.setHandler(new Handler<FeedbackTuple>() {

            @Override
            public void handle(FeedbackTuple t) {
                System.out.println(t.getTokenLength());
            }
        });

        sv.start();
    }
}
