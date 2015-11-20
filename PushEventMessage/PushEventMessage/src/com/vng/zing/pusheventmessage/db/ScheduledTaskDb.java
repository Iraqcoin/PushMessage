/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.db;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.entity.ScheduledTask;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class ScheduledTaskDb {

    private static final Logger LOGGER = LogManager.getLogger(ScheduledTaskDb.class);

    public static int create(int appId, long time, String json, int type, int status) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "scheduled");
        try {
            PreparedStatement query = con.prepareStatement("INSERT INTO scheduled_task(time, model, type, status, app_id) VALUES(?,?,?,?, ?);");
            query.setLong(1, time);
            query.setString(2, json);
            query.setInt(3, type);
            query.setInt(4, status);
            query.setInt(5, appId);
            int row = query.executeUpdate();
            if (row == 1) {
                query = con.prepareStatement(" SELECT LAST_INSERT_ID() AS id;");
                ResultSet resultSet = query.executeQuery();
                while (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("INSERT INTO scheduled_task fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "scheduled");
        }
        return -1;
    }

    public static void update(int id, long time, String json, int type, int status) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "UPDATE scheduled_task");
        try {
            PreparedStatement query = con.prepareStatement("UPDATE scheduled_task SET time=?, model=?, type=?, status=? WHERE id=?;");
            query.setLong(1, time);
            query.setString(2, json);
            query.setInt(3, type);
            query.setInt(4, status);
            query.setInt(5, id);
            query.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("UPDATE scheduled_task fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("UPDATE scheduled_task!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "UPDATE scheduled_task");
        }
    }

    public static boolean delete(int id) throws BackendServiceException {
        String sql = "DELETE FROM scheduled_task WHERE id=?";
        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "DELETE");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            query.setInt(1, id);

            return query.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("DELETE fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "DELETE");
        }
    }

    public static long count(int appId) throws BackendServiceException {
        String sql = "SELECT COUNT(*) AS C FROM scheduled_task WHERE app_id=?";
        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "DELETE");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            query.setInt(1, appId);

            ResultSet result = query.executeQuery();
            if (result.next()) {
                return result.getLong("C");
            }
            return 0;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("DELETE fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "DELETE");
        }
    }

    public static List<ScheduledTask> list(int offset, int size) throws BackendServiceException {
        String sql = "SELECT * FROM scheduled_task LIMIT " + offset + ", " + size;
        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "SELECT scheduled_task");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            ResultSet resultSet = query.executeQuery();

            List<ScheduledTask> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(parse(resultSet));
            }

            return list;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("SELECT scheduled_task fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("SELECT scheduled_task fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "SELECT scheduled_task");
        }
    }

    public static List<ScheduledTask> list(int appId, int offset, int size) throws BackendServiceException {
        String sql = "SELECT * FROM scheduled_task WHERE app_id=? ORDER BY time LIMIT " + offset + ", " + size;
        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(ScheduledTaskDb.class, "SELECT scheduled_task");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            query.setInt(1, appId);
            ResultSet resultSet = query.executeQuery();

            List<ScheduledTask> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(parse(resultSet));
            }

            return list;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("SELECT scheduled_task fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("SELECT scheduled_task fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "SELECT scheduled_task");
        }
    }

    private static ScheduledTask parse(ResultSet resultSet) throws SQLException {
        ScheduledTask result = new ScheduledTask();
        result.setId(resultSet.getInt("id"));
        result.setTime(resultSet.getLong("time"));
        result.setModel(resultSet.getString("model"));
        result.setType(resultSet.getInt("type"));
        result.setStatus(resultSet.getInt("status"));
        return result;
    }

}
