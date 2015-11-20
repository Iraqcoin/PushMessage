/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.noti;

import com.vng.zing.pusheventmessage.common.Utils;
import com.vng.zing.pusheventmessage.thrift.Noti;
import com.vng.zing.pusheventmessage.common.InactiveClose;
import com.vng.zing.pusheventmessage.common.InactiveCloser;
import com.vng.zing.pusheventmessage.common.MyConcurrentCircularQueue;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.thirdparty.apns.IpSelector;
import com.vng.zing.pushnotification.thirdparty.apns.utils.Frame;
import com.vng.zing.pushnotification.thirdparty.apns.utils.ResponseFrame;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import javax.net.ssl.SSLSocketFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class Nsc implements InactiveClose {

    private static final Logger LOGGER = LogManager.getLogger(Nsc.class);

    private static final int CACHE_SIZE = 200;

    private Noti noti;
    private SSLSocketFactory socketFactory;

    private Socket socket;

    private Frame frame;

    private IpSelector ipSelector;
    private int port;

    private DataInputStream input;

    private boolean closed = false;

    private AtomicBoolean available = new AtomicBoolean(false);

    private AtomicInteger idGen = new AtomicInteger();

    private MyConcurrentCircularQueue<PushedNoti> cache = new MyConcurrentCircularQueue<>(CACHE_SIZE);

    private long lastActiveTime = System.currentTimeMillis();

    private synchronized void createConnection() throws UnknownHostException, IOException {
        if (closed) {
            return;
        }
        String address = ipSelector.nextAddress();
        LOGGER.info("CREATE NEW CONNECTION to Apns " + address);
        socket = socketFactory.createSocket(NsConnectionPool.getDefaultProxySocket(address), address, port, true);
        input = new DataInputStream(socket.getInputStream());

    }

    Nsc(IpSelector ipSelector, int port, SSLSocketFactory socketFactory, Noti noti) throws IOException {
        this.noti = noti;
        this.ipSelector = ipSelector;
        this.port = port;
        this.socketFactory = socketFactory;

        JSONObject aps = new JSONObject();
        JSONObject alert = new JSONObject();
        alert.put("title", noti.getTitle());
        alert.put("body", noti.getMessage());
        aps.put("alert", alert);
        aps.put("sound", noti.getSound());

        JSONObject json = new JSONObject();
        json.put("aps", aps);
        frame = Frame.create()
                .expirationDate(noti.expireDate)
                .payload(json.toString())
                .notificationIdentifier(3)
                .highPriority();

        createConnection();
        available.set(true);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        ResponseFrame read = ResponseFrame.read(input);
                        available.set(false);
                        LOGGER.warn("HAS ERROR:" + read);
                        repush(read.getIdentifer());
                    }
                } catch (IOException ex) {
                    LOGGER.error("Reader are closed");
                }
            }
        }).start();
    }

    public void repush(int id) throws IOException {
        createConnection();

        int size = cache.asQueue().size();
        PushedNoti x;
        int i;
        for (i = 0; i < size; i++) {
            x = (PushedNoti) cache.asQueue().remove();
            LOGGER.info("# " + x.id);
            if (x.id == id) {
                LOGGER.info("FOUND");
                i++;
                break;
            }
        }
        for (; i < size; i++) {
            x = (PushedNoti) cache.asQueue().remove();
            LOGGER.info("RE-PUSH > " + x.token);
            write(x.token);
        }
        available.set(true);
    }

    private void write(String token) throws IOException {
        lastActiveTime = System.currentTimeMillis();

        int id = idGen.incrementAndGet();
        PushedNoti pushedNoti = new PushedNoti(id, token);
        cache.add(pushedNoti);

        byte[] notiData = frame.clone()
                .notificationIdentifier(port)
                .notificationIdentifier(id)
                .deviceToken(Utils.hexStringToByteArray(token))
                .build();

        socket.getOutputStream().write(notiData);
        socket.getOutputStream().flush();
    }

    public boolean push(String token) throws IOException {
        if (!available.get()) {
            return false;
        }
        LOGGER.info("PUSH > " + token);
        write(token);
        return true;
    }

    @Override
    public void close() {
        closed = true;

        try {
            socket.close();
        } catch (IOException ex) {
        }
        System.out.println("CONNECTION CLOSE");
    }

    @Override
    public String toString() {
        return "Nsc[" + socket.getRemoteSocketAddress() + ']';
    }

    @Override
    public long lastActiveTime() {
        return lastActiveTime;
    }

}
