/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.common;

import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class BeanUtils {

    public static JsonBuilder newJson() {
        return new JsonBuilder();
    }

    public static class JsonBuilder {

        private JSONObject json = new JSONObject();

        public JsonBuilder set(String fieldName, Object obj) {
            json.put(fieldName, obj);
            return this;
        }

        public JSONObject build() {
            JSONObject result = json;
            json = null;
            return result;
        }
    }

    public static void changeFieldName(JSONObject json, String source, String des) {
        Object value = json.get(source);
        if (value != null) {
            json.remove(source);
            json.put(des, value);
        }
    }
}
