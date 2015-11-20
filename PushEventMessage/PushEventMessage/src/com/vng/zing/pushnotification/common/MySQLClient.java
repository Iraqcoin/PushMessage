/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.common;

import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.client.MysqlClient;
import java.sql.Connection;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public abstract class MySQLClient {
    
//    protected static final String CLIENT_NAME_DEFAULT = "pushnotification";
    private static final MysqlClient mysqlClient = Clients.sqlDb;
    
    private MySQLClient(){
    }
    
    public static Connection getConnection(){
        return mysqlClient.getDbConnection();
    }
    
    public static void releaseConnection(Connection conn){
        mysqlClient.releaseDbConnection(conn);
    }
}
