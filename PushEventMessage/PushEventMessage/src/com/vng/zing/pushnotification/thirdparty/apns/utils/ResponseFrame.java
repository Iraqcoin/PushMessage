/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.thirdparty.apns.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author namnt3
 */
public class ResponseFrame {

    enum Status {

        NO_ERROR(0), PROCESSING_ERROR(1), MISSING_DEVICE_TOKEN(2), MISSING_TOPIC(3), MISSING_PAYLOAD(4), INVALID_TOKEN_SIZE(5),
        INVALID_TOPIC_SIZE(6), INVALID_PAYLOAD_SIZE(7), INVALID_TOKEN(8), SHUTDOWN(9), UNKNOWN(255);

        private static Map<Byte, Status> map;
        private final byte status;

        private Status(int status) {
            this.status = (byte) status;
            add();
        }

        void add() {
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(status, this);
        }

        public static Status valueOf(byte v) {
            return map.get(v);
        }

    }

    private byte cmd;
    private byte statusCode;
    private Status status;

    private int identifer;

    public byte getCmd() {
        return cmd;
    }

    public byte getStatusCode() {
        return statusCode;
    }

    public int getIdentifer() {
        return identifer;
    }

    public static ResponseFrame parse(byte[] bytes) {
        if (bytes == null || bytes.length < 6) {
            throw new RuntimeException("Invalid size");
        }
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        ResponseFrame frame = new ResponseFrame();
        frame.cmd = bb.get();
        frame.statusCode = bb.get();
        frame.identifer = bb.getInt();
        frame.status = Status.valueOf(frame.statusCode);
        return frame;
    }

    public static ResponseFrame read(DataInputStream stream) throws IOException {
        byte[] chunk = new byte[6];
        stream.readFully(chunk);
        return parse(chunk);
    }

    @Override
    public String toString() {
        return "ResponseFrame{" + "cmd=" + cmd + ", statusCode=" + statusCode + ", status=" + status + ", identifer=" + identifer + '}';
    }

}
