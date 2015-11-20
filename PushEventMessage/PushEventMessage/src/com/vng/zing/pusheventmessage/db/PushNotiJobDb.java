/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.db;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.thrift.PushNotiJob;
import com.vng.zing.pusheventmessage.common.SqlUtils;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class PushNotiJobDb {

    private static final Logger LOGGER = LogManager.getLogger(PushNotiJobDb.class);

    public static int create(PushNotiJob job) throws BackendServiceException {
        String sql = "INSERT INTO push_noti_job(appId,osVersion,sdkVersion,zaloId,appUser,bundleIds,packageNames,guids) VALUES (?,?,?,?,?,?,?,?);";

        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "INSERT");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            SqlUtils.setVariables(query, 1, job.getAppId(), job.getOsVersion(), job.getSdkVersion(), job.getZaloId(), job.getAppUser(), job.getBundleIds(), job.getPackageNames(), job.getGuids());

            return query.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "INSERT");
        }
    }

    public static boolean update(PushNotiJob job) throws BackendServiceException {
        String sql = "UPDATE push_noti_job set appId=?,osVersion=?,sdkVersion=?,zaloId=?,appUser=?,bundleIds=?,packageNames=?,guids=?,android=?,ios=?,wphone=? WHERE id=?";

        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "UPDATE");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            SqlUtils.setVariables(query, 1, job.getAppId(), job.getOsVersion(), job.getSdkVersion(), job.getZaloId(), job.getAppUser(), job.getBundleIds(), job.getPackageNames(), job.getGuids(), job.getAndroidNoti(), job.id);

            return query.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("UPDATE fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("UPDATE fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "UPDATE");
        }
    }

    public PushNotiJob get(int id) throws BackendServiceException {
        String sql = "SELECT * FROM push_noti_job WHERE id=??";

        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "UPDATE");
        try {
            PreparedStatement query = con.prepareStatement(sql);
            SqlUtils.setVariables(query, 1, id);

            ResultSet resultSet = query.executeQuery();
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("UPDATE fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("UPDATE fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "UPDATE");

            return null;
        }
    }

    private PushNotiJob parse(ResultSet resultSet) throws SQLException {
        //push_noti_job(appId,osVersion,sdkVersion,zaloId,appUser,bundleIds,packageNames,guids,android,ios,wphone
        PushNotiJob job = new PushNotiJob();
        job.setAppId(resultSet.getInt("appId"));
        job.setOsVersion(SqlUtils.toList(resultSet.getString("osVersion")));
        job.setSdkVersion(SqlUtils.toList(resultSet.getString("sdkVersion")));
        job.setZaloId(SqlUtils.toList(resultSet.getString("zaloId")));
        job.setAppUser(SqlUtils.toList(resultSet.getString("appUser")));
        job.setBundleIds(SqlUtils.toList(resultSet.getString("bundleIds")));
        job.setPackageNames(SqlUtils.toList(resultSet.getString("packageNames")));
        job.setGuids(SqlUtils.toList(resultSet.getString("guids")));
        return job;
    }

    public static boolean delete(int id) {
        return false;
    }

}
