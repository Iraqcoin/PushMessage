/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.common.InvalidRequestException;
import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.AdminHandler;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.pushnotification.dao.impl.EventHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.service.Service;
import com.vng.zing.pushnotification.service.ServiceFactory;
import com.vng.zing.pushnotification.service.impl.AndroidPushService;
import com.vng.zing.pushnotification.utils.ZExpression;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class LocalTest {
    
    EventDAO eventHandler = new EventHandler();
    DeviceDAO deviceHandler = new DeviceHandler();
    AdminDAO adminHandler = new AdminHandler();
    ObjectMapper objectMapper = new ObjectMapper();

    Service service = new AndroidPushService();
        
    public static void main(String[] args) throws InvalidRequestException, BackendServiceException {
        LocalTest test = new LocalTest();
        for (int i = 0; i < 4; i++) {
//            test.register(i);
        }
//        test.testLog();
//        test.addTable();
        try {
            test.push();
        } catch (JsonProcessingException ex) {
            logger.error(ex.getMessage());
        } catch (BackendServiceException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    private static final Logger logger = ZLogger.getLogger(LocalTest.class);
    private void testLog(){
        logger.debug("debug message");
        logger.info("info message");
        logger.warn("warn message");
        logger.error("error message");
    }
    
    
    public void seriallizeRequest(){
        RequestMessage rm = new RequestMessage.Builder("test", "android")
                .setRequiredParams("requester", "requestId", "senderId")
                .addData("message", "hello")
                .addData("icon", "@happy")
                .addExpression(new ZExpression("app_version", "1.2", ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL))
                .addExpression(new ZExpression("os_version", "2.3", ZExpression.ZOperator.OR, ZExpression.ZComparisonOperator.EQUAL))
                .build();
        
        String valuesAsJson = rm.valueAsJson();
        System.out.println("Json: " + valuesAsJson);
        
        try {
//            eventHandler.create("test", "android", "requester", "requestId", valuesAsJson, EventDAO.State.PROCESSING);
            RequestMessage restore = RequestMessage.parse(valuesAsJson);
            if("test".equals(restore.getAppId())
                    && "android".equals(restore.getPlatform())
                    && "requester".equals(restore.getRequester())){
                System.out.println("The same");
                restore.addExtra(Constant.Request.PARAM_LAST_DEVICE_ID, 27);
                ServiceFactory.getInstance().deliver(restore);
            }
        } 
        catch (ParseException | InvalidRequestException ex) {
            ex.getMessage();
        }
    }
    
    public void addToken() throws BackendServiceException{
        adminHandler.saveOrUpdateApiKey("tlvn", "android", "android_token_test");
        adminHandler.saveOrUpdateApiKey("tlvn", "ios", "ios_token_test");
        adminHandler.saveOrUpdateApiKey("tlvn", "wp", "wp_token_test");
        
        System.out.println(JSONValue.toJSONString(adminHandler.getAllApiKey("tlvn")));
        System.out.println(JSONValue.toJSONString(adminHandler.getApiKey("tlvn", "android")));
        
        adminHandler.saveOrUpdateApiKey("tlvn", "android", "android_token_test_updated");
        System.out.println(JSONValue.toJSONString(adminHandler.getAllApiKey("tlvn")));
    }
    public void addTable(){
        try {
            System.out.println("Success with " + adminHandler.initNewApp("vlvn5", "android"));
        } catch (BackendServiceException ex) {
            System.out.println("Fail by " + ex.getMessage());
        }
    }
    
    public void getRequestById() throws JsonProcessingException, BackendServiceException{
        Map<String, Object> request = eventHandler.get("1442369315264333");
        System.out.println(objectMapper.writeValueAsString(request));
    }
    
    public void push() throws JsonProcessingException, BackendServiceException, InvalidRequestException{
        String requestId = String.valueOf(Calendar.getInstance().getTimeInMillis());
        RequestMessage request = new RequestMessage.Builder("test", "android")
                .setId(requestId)
                .setSenderId("AIzaSyDnuPVEsqG6dvmxDRzb3hxCklFyTlve2Ic")
                .belongTo("test@vng.com.vn")
                .addData("message", "Hello world 123")
//                .addExpression(new ZExpression(Constant.Request.PARAM_ZALO_ID, Arrays.asList("144444"), ZExpression.ZOperator.OR))
                .addExpression(new ZExpression(Constant.Request.PARAM_OS_VERSION, "vhMpGuRyhpZnQntS", ZExpression.ZOperator.OR, ZExpression.ZComparisonOperator.EQUAL))
                .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "sxWKCkh79qytP6FP", ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL))
                .build();
        ServiceFactory.getInstance().deliver(request);
        
    }
    
    
    public void register(int index){
        try {
            deviceHandler.addOrUpdate("test", "android", System.currentTimeMillis(),tokens[index], metaDataList.get(index));
        } catch (BackendServiceException ex) {
            logger.error(ex.getMessage());
        }
    }
    
    String[] tokens = new String[]{
        "f3mC-zs8nZE:APA91bHgHJL30vBwH6NEnJphb3CmKC5RM8G1TYW9KJRW_6idIO6DxO2In66h7eD297H9toWQ__GmxAMWMoEp1GciqcX6YdWwddpwnxx01OSOvfJUzVPtaRyjuDmbVgE9_eZHZtlImiw_",
        "cIT_nmfgheg:APA91bEKdJd3QvG8LZDhB7tS167P2Moroz_6yE-fAX6hQe_9JWvwtBQ55HgRIYq6SzVUohQFW7QiXM6Qy6dzTckc8TAo-eXg8ORdC8Zu0Nk11UW7l6ytA9zTw2m-ROlwpOfOYDblufd9",
        "ctN2QtIW-TM:APA91bFL501V9ORGmrOWcpeQfFeVoUqXL1dsd_UXd1hsBt2lpQXihPhLb5wxr9V17bNV8kw2i2G8Fw_QCuVoTZWNIIfQFYbXHspEFdyRNFnGVP29cAm3E8oUCjhLSO3VIvk2fHyIygCM",
        "dmvUH-D2yp0:APA91bE3fPPxf4kLU4sWBAzzzCgP3FebCcjUvt8y0M77-RRO14eAQ_1_3FkMRerNl077H0BGFaWEtmHSQgd0L2L9VH6yS8_NVJEgg_NIs9woX2KtnRgJpj8BXUqXJMG1ARK3bx0ZZhq3"
    };
    
    private static final List<Map<String, String>> metaDataList =  new ArrayList<>();
    static {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("app_version", "1.2.345");
        metaData.put("os_version", "2.3");
        metaDataList.add(metaData);
        
        metaData = new HashMap<>();
        metaData.put("app_version", "10.2");
        metaData.put("os_version", "5.1");
        metaDataList.add(metaData);
        
        metaData = new HashMap<>();
        metaData.put("app_version", "2.345");
        metaData.put("os_version", "5.1");
        metaDataList.add(metaData);
        
        metaData = new HashMap<>();
        metaData.put("app_version", "1.2.345");
        metaData.put("os_version", "5.1");
        metaDataList.add(metaData);
    }
}
