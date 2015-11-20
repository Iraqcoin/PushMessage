/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.service;

import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.utils.ZExpression;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class RequestMessage {
    
    //***  required  ***\\
    private String appId;
    private String platform;
    private String requester;
    private String senderId;
    private String requestId;
    
    //***  initialize at least one property  ***\\
    private final Map<String, String> data = new HashMap<>();
    private final List<String> hasRequiredParams = new ArrayList<>();
    
    //***  initialize at least one property  ***\\
    private List<ZExpression> expressions = new ArrayList<>();
    private final Map<String, Object> extraData = new HashMap<>();
    
    /**
     * verify required parameter
     * 
     * @param fieldName
     * @return 
     */
    public boolean contains(String fieldName){
        return hasRequiredParams.contains(fieldName);
    }
    
    public void addExtra(String key, Object value){
        extraData.put(key, value);
    }
    
    public <T> T getExtra(String key, Class<T> clazz, T defaultValue){
        if(!extraData.containsKey(key)){
            return defaultValue;
        }
        Object val = extraData.get(key);
        try{
            return clazz.cast(val);
        }catch(ClassCastException ex){
            return defaultValue;
        }
    }
    
    
    public static RequestMessage parse(String valueAsJson) throws ParseException{
        Map<String, Object> requestAsMap = (Map<String, Object>) JSONValue.parseWithException(valueAsJson);
        if(requestAsMap == null || requestAsMap.isEmpty()){
            return null;
        }
        String appId = String.valueOf(requestAsMap.get(Constant.Request.PARAM_APP_ID));
        String platform = String.valueOf(requestAsMap.get(Constant.Request.PARAM_PLATFORM));
        String requester = String.valueOf(requestAsMap.get(Constant.Request.PARAM_REQUESTER));
        String requestId = String.valueOf(requestAsMap.get(Constant.Request.PARAM_REQUEST_ID));
        String senderId = String.valueOf(requestAsMap.get(Constant.Request.PARAM_SENDER_ID));
        
        Map<String, String> data = (Map<String, String>) requestAsMap.get(Constant.Request.PARAM_DATA);
        List<Map<String, String>> expressionsAsJson = (List<Map<String, String>>) requestAsMap.get("expressions");
        
        List<ZExpression> expressions = new ArrayList<>();
        for(Map<String, String> expAsJson : expressionsAsJson){
            expressions.add(ZExpression.parseFromJson(expAsJson));
        }
        
        return new RequestMessage.Builder(appId, platform)
                .setRequiredParams(requester, requestId, senderId)
                .setExpressions(expressions)
                .setData(data)
                .build();
    }
    
    public String valueAsJson(){
        Map<String, Object> _data = new HashMap<>();
        _data.put(Constant.Request.PARAM_APP_ID, appId);
        _data.put(Constant.Request.PARAM_PLATFORM, platform);
        _data.put(Constant.Request.PARAM_REQUESTER, requester);
        _data.put(Constant.Request.PARAM_REQUEST_ID, requestId);
        _data.put(Constant.Request.PARAM_SENDER_ID, senderId);
        _data.put(Constant.Request.PARAM_DATA, data);
        
        List<Map<String, String>> _expressions = new ArrayList<>();
        for(ZExpression exp : expressions){
            _expressions.add(exp.valuesAsMap());
        }
        _data.put("expressions", _expressions);
        return JSONValue.toJSONString(_data);
    }
    
    private RequestMessage(){
    }
    
    public static class Builder {
        private final RequestMessage requestMessage = new RequestMessage();
        
        public Builder(String appId, String platform){
             this.requestMessage.appId = appId;
             this.requestMessage.platform = platform;
             this.requestMessage.hasRequiredParams.add(Constant.Request.PARAM_APP_ID);
             this.requestMessage.hasRequiredParams.add(Constant.Request.PARAM_PLATFORM);
        }
        
        public Builder belongTo(String requester){
            this.requestMessage.requester = requester;
            this.requestMessage.hasRequiredParams.add(Constant.Request.PARAM_REQUESTER);
            return this;
        }
        
        public Builder setId(String requestId){
            this.requestMessage.requestId = requestId;
            this.requestMessage.hasRequiredParams.add(Constant.Request.PARAM_REQUEST_ID);
            return this;
        }
        
        public Builder setSenderId(String senderId){
            this.requestMessage.senderId = senderId;
            this.requestMessage.hasRequiredParams.add(Constant.Request.PARAM_SENDER_ID);
            return this;
        }
        
        public Builder setRequiredParams(String requester, String requestId, String senderId){
            this.requestMessage.requester = requester;
            this.requestMessage.requestId = requestId;
            this.requestMessage.senderId = senderId;
            this.requestMessage.hasRequiredParams.add(Constant.Request.PARAM_REQUESTER);
            this.requestMessage.hasRequiredParams.add(Constant.Request.PARAM_REQUEST_ID);
            this.requestMessage.hasRequiredParams.add(Constant.Request.PARAM_SENDER_ID);
            return this;
        }
        
        public Builder setMessage(String message){
            this.requestMessage.data.put(Constant.Request.PARAM_MESSAGE, message);
            return this;
        }
        
        public Builder addData(String key, String value){
            this.requestMessage.data.put(key, value);
            return this;
        }
        
        public Builder addExpression(ZExpression exp){
            this.requestMessage.expressions.add(exp);
            return this;
        }
        
        public Builder setData(Map<String, String> data){
            this.requestMessage.data.putAll(data);
            return this;
        }
        
        public Builder setExpressions(List<ZExpression> expressions){
            this.requestMessage.expressions = expressions;
            return this;
        }
        
        public RequestMessage build(){
            return this.requestMessage;
        }
        
    }
    
    public String getAppId() {
        return appId;
    }

    public String getPlatform() {
        return platform;
    }

    public String getRequester() {
        return requester;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRequestId() {
        return requestId;
    }

    public Map<String, String> getData() {
        return data;
    }

    public List<ZExpression> getExpressions(){
        return this.expressions;
    }
}
