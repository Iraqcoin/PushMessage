/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.client;

/**
 *
 * @author namnt3
 */
public enum KeyPrefixes {

    EVENT_MSG_DB("msgdb_"), LAST_VIEW_TIME_DB("lvtdb_");

    private final String keyPrefix;

    private KeyPrefixes(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }
}
