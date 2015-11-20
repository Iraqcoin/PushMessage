/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.utils;

import java.util.Map;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class Utils {
    
    public static <T> T get(Map<String, Object> data, String key, Class<T> clazz){
        if(data == null || data.isEmpty()){
            return null;
        }
        if(!data.containsKey(key)){
            return null;
        }
        Object value = data.get(key);
        if(clazz.isAssignableFrom(String.class)
                || clazz.isAssignableFrom(Integer.class)
                || clazz.isAssignableFrom(Long.class)
                || clazz.isAssignableFrom(Boolean.class)){
            return (T) value;
        }
        String cause = String.format("Unable to cast {} to class {}", value, clazz);
        throw new ClassCastException(cause);
    }
    
    public static boolean isNullOrEmpty(String str){
        return str == null || "".equals(str.trim());
    }
}
