/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.client;

import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.AdminHandler;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.pushnotification.dao.impl.EventHandler;

/**
 *
 * @author root
 */
public class Clients {

    public static final MysqlClient sqlDb = new MysqlClient("msgDb");
    public static final ZOAuthClient zoaClient = new ZOAuthClient("zoauthmw");
    
    public static final DeviceDAO deviceDb = new DeviceHandler();
    public static final EventDAO eventDb = new EventHandler();

    public static final AdminDAO adminDao = new AdminHandler();
}
