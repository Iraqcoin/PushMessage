/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.dao.impl;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pushnotification.common.MySQLClient;
import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.utils.DeviceHelper;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class AdminHandler implements AdminDAO{

    private static final org.apache.log4j.Logger logger = ZLogger.getLogger(AdminHandler.class);
    
    private static final String TOKEN_TABLE = "CREATE TABLE `%s` ("
            + "  `sdk_id` bigint(20) NOT NULL,"
            + "  `token` varchar(155) NOT NULL,"
            + "  `active` char(2) DEFAULT 'Y',"
            + "  `created_at` datetime DEFAULT NULL,"
            + "  `updated_at` datetime DEFAULT NULL,"
            + "  PRIMARY KEY (`sdk_id`),"
            + "  UNIQUE KEY `Unique_token` (`token`)"
            + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1;";
    
    private static final String EXTRA_TABLE = "CREATE TABLE `%s` ("
            + "  `sdk_id` bigint(20) NOT NULL,"
            + "  `app_version` char(16) DEFAULT NULL,"
            + "  `os_version` char(16) DEFAULT NULL,"
            + "  `sdk_version` char(16) DEFAULT NULL,"
            + "  `package_name` varchar(128) DEFAULT NULL,"
            + "  `app_user` varchar(255) DEFAULT NULL,"
            + "  `zalo_id` varchar(20) DEFAULT NULL,"
            + "  `created_at` datetime DEFAULT NULL,"
            + "  `updated_at` datetime DEFAULT NULL,"
            + "  PRIMARY KEY (`sdk_id`),"
            + "  KEY `base_idx` (`app_version`,`os_version`,`sdk_version`)"
            + ") ENGINE=MyISAM  DEFAULT CHARSET=latin1;";
    
    @Override
    public boolean initNewApp(String appId, String platform) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(DeviceHandler.class, "Create new table");
        try {
            if(appId == null || "".equals(appId.trim())
                    || platform == null || "".equals(platform.trim())){
                logger.error(
                        String.format("Could not create new table with parameters: appId=[%s] and platform=[%s]", appId, platform));
                throw new BackendServiceException("AppId and platform parameters cannot be null or empty");
            }
            PreparedStatement tokenStm = conn.prepareStatement(String.format(TOKEN_TABLE, DeviceHelper.getTokenTableName(appId, platform)));
            tokenStm.execute();
            
            PreparedStatement extraStm = conn.prepareStatement(String.format(EXTRA_TABLE, DeviceHelper.getExtraTableName(appId, platform)));
            extraStm.execute();
            return true;
        } 
        catch (SQLException ex) {
            throw new BackendServiceException(
                    String.format("Create new table of appId = %s on platform %s fail. Caused by: %s"
                        , appId, platform, ex.getMessage()), ex.getErrorCode());
        } 
        finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "Create new table");
        } 
    }
    
    
    @Override
    public int saveOrUpdateApiKey(String appId, String platform, String token) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(DeviceHandler.class, "Save or update api_key");
        try {
            if(appId == null || "".equals(appId.trim())
                    || platform == null || "".equals(platform.trim())){
                logger.error(
                        String.format("saveOrUpdateApiKey encountered error with parameters: appId=[%s] and platform=[%s]", appId, platform));
                throw new BackendServiceException("AppId and platform parameters cannot be null or empty");
            }
            
            PreparedStatement stm = conn.prepareStatement("INSERT INTO tbl_api_key (app_id, platform, api_key, created_at, updated_at) VALUES (?,?,?, now(), now()) "
                                        + "ON DUPLICATE KEY UPDATE api_key = VALUES(api_key), updated_at=VALUES(updated_at)");
            stm.setString(1, appId);
            stm.setString(2, platform);
            stm.setString(3, token);
            return stm.executeUpdate();
            
        } 
        catch (SQLException ex) {
            throw new BackendServiceException(
                    String.format("SaveOrUpdate api_key of appId = %s on platform %s fail. Caused by: %s"
                        , appId, platform, ex.getMessage()), ex.getErrorCode());
        } 
        finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "Save or update api_key");
        } 
    }
    
    @Override
    public String getApiKey(String appId, String platform) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(DeviceHandler.class, "Get api_key");
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT api_key FROM tbl_api_key WHERE app_id = ? AND platform = ?");
            stm.setString(1, appId);
            stm.setString(2, platform);
            ResultSet rs = stm.executeQuery();
            if(rs.next()){
                return rs.getString("api_key");
            }
            return "";
        } 
        catch (SQLException ex) {
            throw new BackendServiceException(
                    String.format("Get api_key of appId = %s on platform %s fail. Caused by: %s"
                        , appId, platform, ex.getMessage()), ex.getErrorCode());
        } 
        finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "Get api_key");
        } 
    }
    
    @Override
    public List<Map<String, String>> getAllApiKey(String appId) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(DeviceHandler.class, "Get all api_key");
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT platform, api_key FROM tbl_api_key WHERE app_id = ?");
            stm.setString(1, appId);
            ResultSet rs = stm.executeQuery();
            return resultSetToList(rs);
        } 
        catch (SQLException ex) {
            throw new BackendServiceException(
                    String.format("Get all api_key of appId = %s fail. Caused by: %s"
                        , appId, ex.getMessage()), ex.getErrorCode());
        } 
        finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "Get all api_key");
        } 
    }
    
    
    private List<Map<String, String>> resultSetToList(ResultSet rs) throws SQLException{
        List<Map<String, String>> result = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        while(rs.next()){
            Map<String, String> data = new HashMap<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i);
                String value = rs.getString(key);
                data.put(key, value);
            }
            result.add(data);
        }
        return result;
    }
}
