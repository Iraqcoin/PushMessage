/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.db;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.thrift.Noti;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class NotiDb {

    private static final Logger LOGGER = LogManager.getLogger(NotiDb.class);

    public static int createNoti(Noti noti) throws BackendServiceException {
        String sql = "INSERT INTO noti(message,badge,sound,icon,extraData) values (?,?,?,?,?)";

        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "INSERT");
        try {
            PreparedStatement query = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            query.setString(1, noti.getMessage());
            query.setInt(2, noti.getBadge());
            query.setString(3, noti.getSound());
            query.setString(4, noti.getIcon());
            query.setString(5, noti.getExtraData());

            if (query.executeUpdate() > 0) {
                ResultSet generatedKeys = query.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
            throw new SQLException("Creating user failed.");
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "INSERT");
        }
    }

    public static boolean update(Noti noti) throws BackendServiceException {
        String sql = "UPDATE noti SET message=?,badge=?,sound=?,icon=?,extraData=? WHERE id = ?";

        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "INSERT");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            query.setString(1, noti.getMessage());
            query.setInt(2, noti.getBadge());
            query.setString(3, noti.getSound());
            query.setString(4, noti.getIcon());
            query.setString(5, noti.getExtraData());
            query.setInt(6, noti.getId());

            return query.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "INSERT");
        }
    }

    public static Noti getNoti(int notiId) throws BackendServiceException {
        String sql = "SELECT * FROM noti WHERE id=?";

        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "SELECT");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            query.setInt(1, notiId);

            ResultSet result = query.executeQuery();
            List<Noti> notis = toNoti(result);
            if (notis != null && notis.size() > 0) {
                return notis.get(0);
            }
            return null;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("SELECT fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "SELECT");
        }
    }

    public static boolean remove(int notiId) throws BackendServiceException {
        String sql = "DELETE FROM noti WHERE id=?";
        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "DELETE");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            query.setInt(1, notiId);

            return query.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("DELETE fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "DELETE");
        }
    }

    private static List<Noti> toNoti(ResultSet result) throws SQLException {
        //message,badge,sound,icon,extraData
        List<Noti> notis = new ArrayList<Noti>();
        while (result.next()) {
            Noti noti = new Noti();
            noti.setId(result.getInt("id"));
            noti.setMessage(result.getString("message"));
            noti.setBadge(result.getInt("badge"));
            noti.setSound(result.getString("sound"));
            noti.setIcon(result.getString("icon"));
            noti.setExtraData(result.getString("extraData"));
            notis.add(noti);
        }
        return notis;
    }

}
