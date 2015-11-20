/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.thirdparty.apns.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author namnt3
 */
public class Frame {

    private static final Logger LOGGER = LogManager.getLogger(Frame.class);

    private final byte ITEM_DEVICE_TOKEN = 1;
    private final byte ITEM_PAYLOAD = 2;
    private final byte ITEM_NOTIFICATION_IDENTIFIER = 3;
    private final byte ITEM_EXPIRATION_DATE = 4;
    private final byte ITEM_PRIORITY = 5;

    private final short LONG_SIZE = Long.SIZE / Byte.SIZE;

    private final int CMD = 2;

    private ByteArrayOutputStream frameData;
    private DataOutputStream writer;

    private Frame() {
        frameData = new ByteArrayOutputStream(32);
        writer = new DataOutputStream(frameData);
    }

    private Frame(ByteArrayOutputStream arr) {
        frameData = new ByteArrayOutputStream(arr.size());
        try {
            frameData.write(arr.toByteArray());
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        writer = new DataOutputStream(frameData);
    }

    public static Frame create() {
        return new Frame();
    }

    public Frame deviceToken(byte[] token) {
        try {
            writer.writeByte(ITEM_DEVICE_TOKEN);
            writer.writeShort(token.length);
            writer.write(token);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        return this;
    }

    public Frame payload(String payload) {
        try {
            writer.writeByte(ITEM_PAYLOAD);
            byte[] data = payload.getBytes();
            writer.writeShort(data.length);
            writer.write(data);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        return this;
    }

    public Frame notificationIdentifier(int nid) {
        try {
            writer.writeByte(ITEM_NOTIFICATION_IDENTIFIER);
            writer.writeShort(4);
            writer.writeInt(nid);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        return this;
    }

    public Frame expirationDate(int time) {
        try {
            writer.writeByte(ITEM_EXPIRATION_DATE);
            writer.writeShort(4);
            writer.writeInt(time);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        return this;
    }

    public Frame highPriority() {
        try {
            writer.writeByte(ITEM_PRIORITY);
            writer.writeShort(1);
            writer.writeByte(10);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        return this;
    }

    public Frame lowPriority() {
        try {
            writer.writeByte(ITEM_PRIORITY);
            writer.writeShort(1);
            writer.writeByte(5);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
        return this;
    }

    public Frame clone() {
        return new Frame(this.frameData);
    }

    public byte[] build() {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(buffer);

            out.writeByte(CMD);
            out.writeInt(frameData.size());
            out.write(frameData.toByteArray());

            return buffer.toByteArray();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return null;
    }

}
