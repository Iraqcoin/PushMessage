/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.db;

import static com.vng.zing.pusheventmessage.client.Clients.sqlDb;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author namnt3
 */
public class ViewLogDb {

    private static final Logger LOGGER = Logger.getLogger(ViewLogDb.class);

//    public static void saveLog(long msgId, String clientIdentifier) throws BackendServiceException {
//        Connection con = sqlDb.getDbConnection();
//
//        if (con == null) {
//            throw new BackendServiceException("No Sql Connection.");
//        }
//
//        Profiler.getThreadProfiler().push(EventMsgDb.class, "INSERT");
//        try {
//            PreparedStatement query = con.prepareStatement("INSERT INTO ViewLog(msgId, clientId) values(?, ?)");
//            query.setLong(1, msgId);
//            query.setString(2, clientIdentifier);
//            int result = query.executeUpdate();
//            if (result > 0) {
//                TrackingDB.increaseCounter("c" + msgId);
//            }
//        } catch (SQLException ex) {
//            LOGGER.error(MsgBulider.format("INSERT INTO ViewLog(msgId, clientId) values(??, ??) fail[??] ", msgId, clientIdentifier, ex.getMessage()), ex);
//        } finally {
//            sqlDb.releaseDbConnection(con);
//            Profiler.getThreadProfiler().pop(EventMsgDb.class, "INSERT");
//        }
//    }
//
//    public static long getView() {
//        return 0L;
//    }
//
//    public static long getUniqueView(long msgId) throws BackendServiceException {
//        Connection con = sqlDb.getDbConnection();
//
//        if (con == null) {
//            throw new BackendServiceException("No Sql Connection.");
//        }
//
//        Profiler.getThreadProfiler().push(EventMsgDb.class, "SELECT");
//        try {
//            PreparedStatement query = con.prepareStatement("SELECT COUNT(*) count from ViewLog WHERE msgId=??");
//            query.setLong(1, msgId);
//            ResultSet executeQuery = query.executeQuery();
//            while (executeQuery.next()) {
//                return executeQuery.getLong("count");
//            }
//        } catch (SQLException ex) {
//            LOGGER.error(MsgBulider.format("SELECT COUNT(*) count from ViewLog WHERE msgId=?? fail[??]", msgId, ex.getMessage()), ex);
//        } finally {
//            sqlDb.releaseDbConnection(con);
//            Profiler.getThreadProfiler().pop(EventMsgDb.class, "SELECT");
//        }
//        return 0L;
//    }
}
