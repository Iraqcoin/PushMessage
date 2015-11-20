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
public class BackendServiceException extends Exception {

    private long errorCode;

    public BackendServiceException() {
    }

    public BackendServiceException(String string) {
        super(string);
    }

    public BackendServiceException(String string, long errorCode) {
        super(string + ", errorCode =  " + errorCode);
        this.errorCode = errorCode;
    }

    public long getErrorCode() {
        return errorCode;
    }

}
