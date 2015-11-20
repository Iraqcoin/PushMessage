/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.noti;

import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.model.NotiInfoModel;
import com.vng.zing.pusheventmessage.thrift.Noti;
import com.vng.zing.pusheventmessage.common.BlockingObjectPool;
import com.vng.zing.pusheventmessage.common.InactiveCloser;
import com.vng.zing.pusheventmessage.common.ObjectFactory;
import com.vng.zing.pushnotification.thirdparty.apns.IpSelector;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class NsConnectionPool {

    private static final Logger LOGGER = LogManager.getLogger(NsConnectionPool.class);
    private static final int CONNECTION = 6;

    private static final int PORT = 2195;
    private static final String PRODUCT_GATEWAY = "gateway.push.apple.com";
    private static final String DEVELOPMENT_GATEWAY = "gateway.sandbox.push.apple.com";

    private static final InactiveCloser closer = new InactiveCloser(15000);

    private LinkedBlockingQueue<Msg> tasks = new LinkedBlockingQueue<>();

    private final ExecutorService exe;

    private boolean closed = false;

    private IpSelector ipSelector;
    private SSLSocketFactory socketFactory;

    private BlockingObjectPool<Nsc> connectionPool;

    public static NsConnectionPool createProductionPool(String keyFile, String keyPass, Noti noti) throws Exception {
        return new NsConnectionPool(true, keyFile, keyPass, noti);
    }

    public static NsConnectionPool createDevPool(String keyFile, String keyPass, Noti noti) throws Exception {
        return new NsConnectionPool(false, keyFile, keyPass, noti);
    }

    private NsConnectionPool(boolean production, String keyFile, String keyPass, Noti noti) throws Exception {
        File f = new File(keyFile);
        if (!f.canRead() && !f.isFile()) {
            throw new FileNotFoundException(keyFile + " can't be read");
        }

        socketFactory = getSocketFactory(f, keyPass);
        if (socketFactory == null) {
            throw new Exception("Can't load key file " + keyFile);
        }
        initConnections(production, keyFile, keyPass, noti);

        exe = Executors.newFixedThreadPool(CONNECTION);
        for (int i = 0; i < CONNECTION; i++) {
            exe.submit(new Runnable() {
                @Override
                public void run() {
                    LOGGER.info("Create new thread for pushing Apns Noti");
                    try {
                        while (!closed) {
                            Msg msg = null;
                            Nsc con = null;
                            try {
                                msg = tasks.poll(1000, TimeUnit.MILLISECONDS);
                                if (msg != null) {
                                    //write msg to queue;

                                    con = connectionPool.borrow();
                                    if (con != null) {
                                        boolean pushed = con.push(msg.token);
                                        if (pushed) {
                                            msg.done();
                                        } else {
                                            tasks.add(msg);
                                        }

                                    } else {
                                        tasks.add(msg);
                                        LOGGER.error("No connection for pushing noti to " + msg.token);
                                    }

                                }
                            } catch (InterruptedException ex) {
                                LOGGER.error(ex.getMessage(), ex);
                            } finally {
                                if (con != null) {
                                    connectionPool.returnObject(con);
                                    con = null;
                                }
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    LOGGER.info("Thread closed");
                }
            });
        }
    }

    public void close() {
        closed = true;
        exe.shutdown();
        try {
            exe.awaitTermination(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        try {
            connectionPool.close();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * Push task to sending queue; <br/>
     * Waiting if necessary for space to become available
     *
     * @param latch
     * @param token
     * @throws java.lang.InterruptedException
     */
    public void submit(final CountDownLatch latch, String token) {
        try {
            tasks.put(new Msg(token) {
                @Override
                void done() {
                    latch.countDown();
                }
            });
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private void initConnections(boolean production, String key, String keyPass, final Noti noti) {
        if (production) {
            ipSelector = new IpSelector(PRODUCT_GATEWAY);
        } else {
            ipSelector = new IpSelector(DEVELOPMENT_GATEWAY);
        }

        connectionPool = new BlockingObjectPool<>(new ObjectFactory<Nsc>() {

            @Override
            public Nsc create() {
                try {
                    Nsc nsc = new Nsc(ipSelector, PORT, socketFactory, noti);
                    closer.add(nsc);
                    return nsc;
                } catch (IOException ex) {
                    LOGGER.error(ex);
                }
                return null;
            }

            @Override
            public void destroy(Nsc obj) {
//                try {
//                    obj.close();
//                } catch (IOException ex) {
//                    LOGGER.info(ex.getMessage());
//                }
            }
        }, CONNECTION);

    }

    public static SSLSocketFactory getSocketFactory(File pKeyFile, String pKeyPassword) {
        LOGGER.debug(MsgBuilder.format("Load key file[??], pass[??]", pKeyFile, pKeyPassword));
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

    public static Socket getDefaultProxySocket(String address) throws IOException {
        //10.30.12.134:1080

        Socket socket;

        if (NotiInfoModel.USE_PROXY) {
            //"10.30.12.134":1080
            SocketAddress proxyAddress = new InetSocketAddress(NotiInfoModel.PROXY_HOST, NotiInfoModel.PROXY_PORT);
            socket = new Socket(new Proxy(Proxy.Type.SOCKS, proxyAddress));
            LOGGER.info(MsgBuilder.format("Using proxy ??:??", NotiInfoModel.PROXY_HOST, NotiInfoModel.PROXY_PORT));
        } else {
            socket = new Socket();
        }

//        SocketAddress proxyAddress = new InetSocketAddress("113.190.247.135", 9027); //vietnam SOCK5
//        Socket socket = new Socket();
        InetSocketAddress remoteService = new InetSocketAddress(address, PORT);
        socket.connect(remoteService);
        return socket;
    }

    public static abstract class Msg {

        String token;

        public Msg(String token) {
            this.token = token;
        }

        abstract void done();
    }
}
