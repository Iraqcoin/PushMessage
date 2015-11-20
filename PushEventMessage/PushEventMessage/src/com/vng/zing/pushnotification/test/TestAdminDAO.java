/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.test;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.dao.impl.AdminHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class TestAdminDAO {
    
    AdminDAO adminHandler = new AdminHandler();
    
    public static void main(String[] args) {
        new TestAdminDAO().testInit();
        new TestAdminDAO().saveOrUpdateSenderId();
    }
    
    private void testInit(){
        try {
            adminHandler.initNewApp("ken", "android");
        } catch (BackendServiceException ex) {
            Logger.getLogger(TestAdminDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void saveOrUpdateSenderId(){
        try {
            adminHandler.saveOrUpdateApiKey("ken", "ios", "ken_ios_senderid_updated");
            if("ken_ios_senderid_updated".equals(adminHandler.getApiKey("ken", "ios"))){
                System.out.println("Insert and get work right");
            }
            adminHandler.saveOrUpdateApiKey("ken", "android", "ken_ios_senderid");
            if(adminHandler.getAllApiKey("ken").size() == 2){
                System.out.println("Insert and get all work right");
            }
            adminHandler.saveOrUpdateApiKey("ken", "ios", "ken_ios_senderid_updated");
            if("ken_ios_senderid_updated".equals(adminHandler.getApiKey("ken", "ios"))){
                System.out.println("Update and get work right");
            }
            
        } catch (BackendServiceException ex) {
            Logger.getLogger(TestAdminDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
