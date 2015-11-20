/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.db;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class AppOwnerDb {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(AppOwnerDb.class);

    public static boolean addOwner(long userId, int appId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "addOwner");
        try {
            PreparedStatement query = con.prepareStatement("INSERT INTO app_owner(app_id, user_id) VALUES(?, ?);");
            query.setInt(1, appId);
            query.setLong(2, userId);
            int row = query.executeUpdate();
            if (row == 1) {
                return true;
            }
            return false;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("INSERT INTO app_owner fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "addOwner");
        }
    }

    public static boolean removeOwner(long userId, int appId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "removeOwner");
        try {
            PreparedStatement query = con.prepareStatement("DELETE FROM app_owner WHERE app_id=? AND user_id=?;");
            query.setInt(1, appId);
            query.setLong(2, userId);
            int row = query.executeUpdate();
            if (row == 1) {
                return true;
            }
            return false;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("DELETE FROM app_owner fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "removeOwner");
        }
    }

    public static List<Long> getAppOwner(int appId) throws BackendServiceException {
        String sql = "SELECT user_id FROM app_owner WHERE app_id=? LIMIT 100;";
        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "SELECT app_owner");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            query.setInt(1, appId);
            ResultSet resultSet = query.executeQuery();

            List<Long> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(resultSet.getLong("user_id"));
            }

            return list;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("SELECT app_owner fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("SELECT app_owner fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "SELECT app_owner");
        }
    }

    public static List<Integer> getAppByOwner(long userId) throws BackendServiceException {
        String sql = "SELECT app_id FROM app_owner WHERE user_id=? LIMIT 100;";
        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "SELECT app_owner");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            query.setLong(1, userId);
            ResultSet resultSet = query.executeQuery();

            List<Integer> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(resultSet.getInt("app_id"));
            }

            return list;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("SELECT app_owner fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("SELECT app_owner fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "SELECT app_owner");
        }
    }
}
