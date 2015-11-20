/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

/**
 *
 * @author namnt3
 */
public enum ErrorCode {

    INVALID_REQUEST(-2),
    SYS_ERR(-1),
    NOT_ANY(0),
    SUCCESS(1);

    private final int code;

    private ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

}
