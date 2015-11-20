/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.dao.impl;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pushnotification.common.MySQLClient;
import com.vng.zing.pushnotification.dao.ControlPanelDAO;
import com.vng.zing.pushnotification.utils.DeviceHelper;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
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
public class ControlPanelHandler implements ControlPanelDAO{

    @Override
    public List<Map<String, Object>> getAllDevices(String appId, String platform) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        Profiler.getThreadProfiler().push(EventHandler.class, "RETRIEVE ALL DEVICE");
        try {
            String query = String.format("SELECT sdk_id, app_version, os_version, sdk_version, package_name, "
                    + "app_user, zalo_id, created_at, updated_at "
                    + "FROM %s ORDER BY created_at DESC, updated_at DESC", DeviceHelper.getExtraTableName(appId, platform));
            PreparedStatement stm = conn.prepareStatement(query);
            result = resultSetToList(stm.executeQuery());
        } 
        catch (SQLException ex) {
            throw new BackendServiceException("Retrieve all device fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } 
        finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "RETRIEVE ALL DEVICE");
        }
        return result; 
    }
    
    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException{
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        while(rs.next()){
            Map<String, Object> data = new HashMap<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i);
                Object value = rs.getObject(key);
                data.put(key, value);
            }
            result.add(data);
        }
        return result;
    }
}
