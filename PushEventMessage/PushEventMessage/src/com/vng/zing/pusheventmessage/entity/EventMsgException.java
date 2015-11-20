/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.entity;

/**
 *
 * @author namnt3
 */
public class EventMsgException extends Exception {

    private long errorCode;

    public EventMsgException() {
    }

    public EventMsgException(String string) {
        super(string);
    }

    public EventMsgException(String string, long errorCode) {
        super(string + ", errorCode =  " + errorCode);
        this.errorCode = errorCode;
    }

    public long getErrorCode() {
        return errorCode;
    }

}
