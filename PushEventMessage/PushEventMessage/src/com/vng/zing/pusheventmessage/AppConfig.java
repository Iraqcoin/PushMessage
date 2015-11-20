/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage;

import com.vng.zing.configer.ZConfig;
import com.vng.zing.pusheventmessage.client.ZaloIDClient;

/**
 *
 * @author root
 */
public class AppConfig {

   public static String APP_DOMAIN = "http://dev.events-msg.zaloapp.com:8017";
   // public static String APP_DOMAIN = "http://dev.events-msg.zaloapp.com";
    public static String ZALO_ID_DOMAIN = "http://dev.id.zaloapp.com";
    
//    public static String LOGIN_TEMPLATE = ZConfig.Instance.getString(ZaloIDClient.class, "zappcms", "login-template", "");
    public static String LOGIN_TEMPLATE = "eb9e8b53b7165e480707";
    
    public static String APP_ID = "JQ85WD10020";

}
