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
public class InvalidRequestException extends Exception {

    private final int code;

    public InvalidRequestException(String string) {
        super(string);
        this.code = -1;
    }

    public InvalidRequestException(int code, String string) {
        super(string);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
