/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.test;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.utils.DeviceHelper;
import com.vng.zing.pushnotification.utils.ZExpression;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class TestDeviceDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TestDeviceDAO.class);
    DeviceDAO handler = new DeviceHandler();
    
    public static void main(String[] args) {
        TestDeviceDAO test = new TestDeviceDAO();
        test.testAll();
        test.testRemove();
        test.testConsoleHandler();
    }
    
    private void testConsoleHandler(){
        
        Map<String, String> extra = new HashMap<>();
        for(String extraCol : DeviceHelper.extraColumns){
            extra.put(extraCol, "1.2.3");
        }
        try {
            String token = "token_test_" + currentTime();
            long sdkId = System.currentTimeMillis();
            int result = handler.addOrUpdate("test", "android", sdkId, token, extra);
            System.out.println("Added " + result + " device(s)");
            
            extra.put(Constant.Request.PARAM_APP_VERSION, "2.1");
            extra.put(Constant.Request.PARAM_OS_VERSION, "5.1");
            result = handler.addOrUpdate("test", "android", sdkId, token, extra);
            System.out.println("Updated " + result + " device(s)");
        } catch (BackendServiceException ex) {
            logger.error(ex.getMessage(), ex);
        }
        
    }
    
    private void testRemove(){
        try {
            long sdkId = System.currentTimeMillis();
            Map<String, String> extra = new HashMap<>();
            for(String extraCol : DeviceHelper.extraColumns){
                extra.put(extraCol, "1.2.3");
            }
            
            handler.addOrUpdate("ken", "android", sdkId, "test_token_vip_9999", extra);
            List<Long> deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                    .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                    .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3", ZExpression.ZComparisonOperator.EQUAL))
                    .build());
            if(deviceIds.size() == 1 && deviceIds.get(0) == sdkId){
                System.out.println("Add and get device id work right");
            }
            
            handler.remove("ken", "android", "test_token_vip_9999");
            List<String> tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.isEmpty()){
                System.out.println("remove device by id works right");
            }
            
            deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                            .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                            .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3", ZExpression.ZComparisonOperator.EQUAL))
                            .build());
            if(deviceIds.isEmpty()){
                System.out.println("Remove device by id removed extra record. It's right");
            }
            
            //
            //  Test batch remove by list of device ids
            //
            handler.addOrUpdate("ken", "android", sdkId, "test_token_vip_9999", extra);
            deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                    .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                    .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3", ZExpression.ZComparisonOperator.EQUAL))
                    .build());
            if(deviceIds.size() == 1 && deviceIds.get(0) == sdkId){
                System.out.println("Add and get device id work right");
            }
            handler.batchRemove("ken", "android", deviceIds);
            tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.isEmpty()){
                System.out.println("remove device by id works right");
            }
            
            deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                            .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                            .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3", ZExpression.ZComparisonOperator.EQUAL))
                            .build());
            if(deviceIds.isEmpty()){
                System.out.println("Remove device by id removed extra record. It's right");
            }
            
            //
            //  Test batch remove by list of tokens
            //
            handler.addOrUpdate("ken", "android", sdkId, "test_token_vip_9999", extra);
            deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                    .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                    .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3", ZExpression.ZComparisonOperator.EQUAL))
                    .build());
            if(deviceIds.size() == 1 && deviceIds.get(0) == sdkId){
                System.out.println("Add and get device id work right");
            }
            tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            handler.remove("ken", "android", tokens);
            tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.isEmpty()){
                System.out.println("remove device by id works right");
            }
            
            deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                            .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                            .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3", ZExpression.ZComparisonOperator.EQUAL))
                            .build());
            if(deviceIds.isEmpty()){
                System.out.println("Remove device by id removed extra record. It's right");
            }
        } catch (BackendServiceException ex) {
            java.util.logging.Logger.getLogger(TestDeviceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void testAll(){
        try {
            long sdkId = System.currentTimeMillis();
            Map<String, String> extra = new HashMap<>();
            for(String extraCol : DeviceHelper.extraColumns){
                extra.put(extraCol, "1.2.3");
            }
            handler.addOrUpdate("ken", "android", sdkId, "test_token_vip_9999", extra);
            List<Long> deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                            .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                            .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3", ZExpression.ZComparisonOperator.EQUAL))
                            .build());
            if(deviceIds.size() == 1 && deviceIds.get(0) == sdkId){
                System.out.println("Add and get device id work right");
            }
            
            List<String> tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.size() == 1 && "test_token_vip_9999".equals(tokens.get(0))){
                System.out.println("Add and get device id and get token work right");
            }
            
            handler.update("ken", "android", "test_token_vip_9999", "test_token_vip_9999_new");
            tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.size() == 1 && "test_token_vip_9999_new".equals(tokens.get(0))){
                System.out.println("update token by old token works right");
            }
            
            Map<String, String> old2newToken = new HashMap<>();
            old2newToken.put("test_token_vip_9999_new", "test_token_vip_9999_new_22222");
            handler.batchUpdateByToken("ken", "android", old2newToken);
            tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.size() == 1 && "test_token_vip_9999_new_22222".equals(tokens.get(0))){
                System.out.println("batch update token works right");
            }
            
            handler.addOrUpdate("ken", "android", sdkId, "test_token_vip_2", null);
            tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.size() == 1 && "test_token_vip_2".equals(tokens.get(0))){
                System.out.println("addorupdate token works right");
            }
            
            for(String extraCol : DeviceHelper.extraColumns){
                extra.put(extraCol, "1.2.3.4.5");
            }
            handler.addOrUpdate("ken", "android", sdkId, null, extra);
            tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.size() == 1 && "test_token_vip_2".equals(tokens.get(0))){
                System.out.println("addorupdate extra works right");
            }
            
            deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                            .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                            .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3.4.5", ZExpression.ZComparisonOperator.EQUAL))
                            .build());
            if(deviceIds.size() == 1 && deviceIds.get(0) == sdkId){
                System.out.println("Addorupdate extra works right");
            }
            
            handler.remove("ken", "android", sdkId);
            tokens = handler.getTokensByDeviceId("ken", "android", deviceIds);
            if(tokens.isEmpty()){
                System.out.println("remove device by id works right");
            }
            
            deviceIds = handler.getDeviceIds(new RequestMessage.Builder("ken", "android")
                            .setRequiredParams("test@vng.com.vn", "test_request_id", "test_sender_id")
                            .addExpression(new ZExpression(Constant.Request.PARAM_APP_VERSION, "1.2.3.4.5", ZExpression.ZComparisonOperator.EQUAL))
                            .build());
            if(deviceIds.isEmpty()){
                System.out.println("Remove device by id removed extra record. It's right");
            }
        } catch (BackendServiceException ex) {
            java.util.logging.Logger.getLogger(TestDeviceDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String currentTime(){
        return String.valueOf(Calendar.getInstance().getTimeInMillis());
    }
}
