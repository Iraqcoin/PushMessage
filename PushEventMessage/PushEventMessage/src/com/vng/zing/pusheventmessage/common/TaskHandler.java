/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import java.util.List;

/**
 *
 * @author root
 */
public interface TaskHandler<T> {

    public void handle(List<T> taskQueue);
}
