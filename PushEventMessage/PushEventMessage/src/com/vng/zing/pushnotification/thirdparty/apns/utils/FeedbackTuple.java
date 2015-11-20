/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.thirdparty.apns.utils;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author namnt3
 */
public class FeedbackTuple {

    private int time;
    private byte[] deviceToken;

    private String hexToken;

    public FeedbackTuple() {
    }

    public FeedbackTuple(int time, byte[] deviceToken) {
        this.time = time;
        this.deviceToken = deviceToken;
    }

    public int getTime() {
        return time;
    }

    public short getTokenLength() {
        return (short) (deviceToken == null ? 0 : deviceToken.length);
    }

    public byte[] getDeviceToken() {
        return deviceToken;
    }

    public static FeedbackTuple read(DataInputStream input) throws IOException {
        FeedbackTuple tuple = new FeedbackTuple();
        tuple.time = input.readInt();
        short length = input.readShort();
        if (length < 0) {
            throw new IOException("Feedback.tokenLength can't be  < 0");
        }
        tuple.deviceToken = new byte[length];
        input.readFully(tuple.deviceToken);
        return tuple;
    }

    @Override
    public String toString() {
        return "FeedbackTuple{" + "time=" + time + ", deviceToken=" + HexConverter.getHex(deviceToken) + '}';
    }

}
