/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

/**
 *
 * @author root
 */
public enum Platform {

    IOS, ANDROID;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
