/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.thirdparty.apns;

/**
 *
 * @author root
 */
public interface Handler<T> {

    void handle(T t);

}
