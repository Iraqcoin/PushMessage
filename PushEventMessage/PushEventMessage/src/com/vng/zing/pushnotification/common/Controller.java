/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class Controller {
 
    private final ConcurrentMap<String, Boolean> pauseFlag = new ConcurrentHashMap<>();
    private static final Controller INSTANCE = new Controller();
    
    private Controller(){
    }
    
    public static Controller getInstance(){
        return INSTANCE;
    }
    
    public void addOrUpdate(String key, boolean flag){
        pauseFlag.put(key, flag);
    }
    
    public boolean get(String key){
        if(!pauseFlag.containsKey(key)){
            return false;
        }
        return pauseFlag.remove(key);
    }
}
