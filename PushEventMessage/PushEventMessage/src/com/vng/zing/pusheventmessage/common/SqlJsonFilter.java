/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import com.vng.zing.pusheventmessage.admin.common.BeanUtils;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.utils.ZExpression;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class SqlJsonFilter {

//    public String toWhereSection(JSONObject json) throws Exception {
//        for (Object key : json.keySet()) {
//            String keyName = (String) key;
//            JSONObject value = (JSONObject) json.get(keyName);
//            Iterator iterator = value.keySet().iterator();
//            if (!iterator.hasNext()) {
//                throw new Exception("Invalid value at " + keyName);
//            }
//            String op = (String) iterator.next();
//            Object opValue = value.get(op);
//            switch (op) {
//                case "$gt":
//                    throw new UnsupportedOperationException("$gt");
//                case "$lt":
//                    throw new UnsupportedOperationException("$lt");
//                case "$eq":
//                    throw new UnsupportedOperationException("$ltt");
//                case "$neq":
//                    throw new UnsupportedOperationException("$ltt");
//                case "$in":
//                    JSONArray array = (JSONArray) opValue;
//                    break;
//            }
//        }
//    }
    public static void addExpressionByJson(RequestMessage.Builder builder, JSONObject json) throws Exception {
        for (Object key : json.keySet()) {
            String keyName = (String) key;
            JSONObject value = (JSONObject) json.get(keyName);
            Iterator iterator = value.keySet().iterator();
            if (!iterator.hasNext()) {
                throw new Exception("Invalid value at " + keyName);
            }
            String op = (String) iterator.next();
            Object opValue = value.get(op);
            JSONArray array;
            switch (op) {
                case "$gt":
                    throw new UnsupportedOperationException("$gt");
                case "$lt":
                    throw new UnsupportedOperationException("$lt");
                case "$eq":
                    throw new UnsupportedOperationException("$ltt");
                case "$in":
                    array = (JSONArray) opValue;
                    builder.addExpression(new ZExpression(keyName, array, ZExpression.ZOperator.AND));
                    break;
            }
        }
    }

    public static void main(String args[]) throws Exception {
        RequestMessage.Builder b = new RequestMessage.Builder("10073", "android");
        
        JSONArray array = new JSONArray();
        array.add("abc");
        array.add("def");
        
        addExpressionByJson(b, BeanUtils.newJson()
                .set("abc", BeanUtils.newJson()
                        .set("$in", array)
                        .build())
                .build());
        
        System.out.println(b.build().valueAsJson());

    }
}
