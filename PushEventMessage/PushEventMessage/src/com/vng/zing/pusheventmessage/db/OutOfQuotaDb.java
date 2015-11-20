/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.db;

import com.vng.zing.pusheventmessage.app.MainApp;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.stats.Profiler;
import com.vng.zing.strlist32bm.thrift.StrList32bmService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class OutOfQuotaDb {

    private static final Logger LOGGER = Logger.getLogger(OutOfQuotaDb.class);

    private static String toString(Set<Long> sdkIds) {
        StringBuilder buffer = new StringBuilder(sdkIds.size() * (10));
        for (Long sdkId : sdkIds) {
            buffer.append(sdkId).append(";");
        }
        return buffer.toString();
    }

    private static Set<Long> toSdkIdSet(String sdkIds) {
        Set<Long> result = new HashSet<Long>();
        long sdkId = 0;
        for (int i = 0; i < sdkIds.length(); i++) {
            if (sdkIds.charAt(i) == ';') {
                result.add(sdkId);
                sdkId = 0;
            }
            switch (sdkIds.charAt(i)) {
                case '0':
                    sdkId = sdkId * 10;
                    break;
                case '1':
                    sdkId = sdkId * 10 + 1;
                    break;
                case '2':
                    sdkId = sdkId * 10 + 2;
                    break;
                case '3':
                    sdkId = sdkId * 10 + 3;
                    break;
                case '4':
                    sdkId = sdkId * 10 + 4;
                    break;
                case '5':
                    sdkId = sdkId * 10 + 5;
                    break;
                case '6':
                    sdkId = sdkId * 10 + 6;
                    break;
                case '7':
                    sdkId = sdkId * 10 + 7;
                    break;
                case '8':
                    sdkId = sdkId * 10 + 8;
                    break;
                case '9':
                    sdkId = sdkId * 10 + 9;
                    break;
                default:
                    sdkId = 0;
            }
        }
        return result;
    }

    public static void insert(long msgId, Set<Long> sdkIds) throws BackendServiceException {
        String ids = toString(sdkIds);

        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "INSERT out_of_quota");
        try {
            PreparedStatement query = con.prepareStatement("INSERT INTO out_of_quota(msg_id, sdkids) VALUES(?,?);");
            query.setLong(1, msgId);
            query.setString(2, ids);
            query.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("INSERT INTO out_of_quota fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "INSERT out_of_quota");
        }
    }

    public static void update(long msgId, Set<Long> sdkIds) throws BackendServiceException {
        String ids = toString(sdkIds);

        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "UPDATE out_of_quota");
        try {
            PreparedStatement query = con.prepareStatement("UPDATE out_of_quota SET sdkids=? WHERE msg_id=?;");
            query.setString(1, ids);
            query.setLong(2, msgId);
            query.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("UPDATE out_of_quota fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("UPDATE out_of_quota!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "UPDATE out_of_quota");
        }
    }

    public static boolean exists(long msgId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "SELECT msg_id");
        try {
            PreparedStatement query = con.prepareStatement("SELECT msg_id FROM out_of_quota WHERE msg_id=?");
            query.setLong(1, msgId);
            ResultSet executeQuery = query.executeQuery();
            return executeQuery.next();
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("UPDATE out_of_quota fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("UPDATE out_of_quota!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "SELECT msg_id");
        }

    }

    public static void save(long msgId, Set<Long> sdkids) throws BackendServiceException {
        if (exists(msgId)) {
            update(msgId, sdkids);
        } else {
            insert(msgId, sdkids);
        }
    }

    public static Set<Long> get(long msgId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();
        String sdkids = null;

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "SELECT out_of_quota");
        try {
            PreparedStatement query = con.prepareStatement("SELECT msg_id,sdkids FROM out_of_quota WHERE msg_id=?");
            query.setLong(1, msgId);
            ResultSet executeQuery = query.executeQuery();
            if (executeQuery.next()) {
                sdkids = executeQuery.getString("sdkids");
            }
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("SELECT FROM out_of_quota fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("SELECT FROM out_of_quota!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "SELECT out_of_quota");
        }

        if (sdkids == null) {
            return null;
        }
        return toSdkIdSet(sdkids);
    }

//    public static Set<Long> getSdkIds(long msgId) {
//    }
//
//    public static void save(long msgId, Set<Long> skdIds) {
//    }
    public static void main(String args[]) throws BackendServiceException {
        MainApp.init();
        Set<Long> ids = new HashSet<Long>();
        long MAX = 600000;
        for (long i = 0L; i < MAX; i++) {
            ids.add(i);
        }

        long t = System.currentTimeMillis();
        save(10L, ids);
        System.out.println("SET TIME = " + (System.currentTimeMillis() - t));

        t = System.currentTimeMillis();
        ids = get(10L);
        System.out.println("GET TIME = " + (System.currentTimeMillis() - t));
        System.out.println(ids);

        StrList32bmService service = new StrList32bmService();
        StrList32bmService.Client client = null;
    }
}
