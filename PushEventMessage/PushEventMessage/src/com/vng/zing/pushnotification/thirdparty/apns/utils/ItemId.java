/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.thirdparty.apns.utils;

/**
 *
 * @author namnt3
 */
public enum ItemId {

    DEVICE_TOKEN(1), PLAYLOAD(2), NOTIFICATION_IDENTIFIER(3), EXPIRATION_DATE(4), PRIORITY(5);

    private final int id;

    private ItemId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
