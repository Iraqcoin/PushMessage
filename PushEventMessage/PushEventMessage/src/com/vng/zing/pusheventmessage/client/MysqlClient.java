package com.vng.zing.pusheventmessage.client;

import com.vng.zing.pusheventmessage.db.EventMsgDb;
import com.vng.zing.configer.ZConfig;
import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.stats.Profiler;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author namnt3
 */
public class MysqlClient {

    private static Logger _logger = ZLogger.getLogger(MysqlClient.class);
    private String _host;
    private int _port;
    private String _dbname;
    private String _user;
    private String _password;
    private BlockingQueue<Connection> pool;
    private String url;
    private static Map<String, MysqlClient> _MysqlClient = new HashMap<String, MysqlClient>();

    public MysqlClient(String name) {
        _host = ZConfig.Instance.getString(MysqlClient.class, name, "host", "localhost");
        _port = ZConfig.Instance.getInt(MysqlClient.class, name, "port", 3306);
        _dbname = ZConfig.Instance.getString(MysqlClient.class, name, "dbname", "zpublish");
        _user = ZConfig.Instance.getString(MysqlClient.class, name, "uname", "root");
        _password = ZConfig.Instance.getString(MysqlClient.class, name, "passwd", "root");
        this.init(ZConfig.Instance.getInt(MysqlClient.class, name, "poolsize", 10));
    }

    public static MysqlClient getMysqlClient(String _name) {
        MysqlClient result;

        if (_MysqlClient.get(_name) == null) {
            synchronized (_MysqlClient) {
                result = new MysqlClient(_name);
                _MysqlClient.put(_name, result);
            }
        } else {
            result = _MysqlClient.get(_name);
        }
        return result;
    }

    private boolean init(int poolsize) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            url = "jdbc:mysql://" + _host + ":" + _port + "/" + _dbname + "?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&interactiveClient=true&" + "&user=" + _user
                    + "&password=" + _password;

            BlockingQueue<Connection> cnnPool = new ArrayBlockingQueue<Connection>(poolsize);
            while (cnnPool.size() < poolsize) {
                cnnPool.add(DriverManager.getConnection(url));
            }
            pool = cnnPool;
            _logger.info("MysqlClient init pool success");
        } catch (Exception ex) {
            _logger.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public Connection getDbConnection() {
        Connection conn = null;
        int retry = 0;
        do {
            try {
                conn = pool.poll(10000, TimeUnit.MILLISECONDS);
                if (conn == null || !conn.isValid(0)) {
                    conn = DriverManager.getConnection(url);
                }
            } catch (Exception ex) {
                _logger.warn(ex);
            }
            ++retry;
        } while (conn == null && retry < 3);
        return conn;
    }

    public void releaseDbConnection(java.sql.Connection conn) {
        if (conn != null) {
            try {
                if(!pool.offer(conn)){
                    conn.close();
                }
            } catch (Exception ex) {
                _logger.error(ex.getMessage(), ex);
            }
        }
    }

//    public void insert(String queryString, Array array) throws BackendServiceException, SQLException {
//        Connection con = Clients.sqlDb.getDbConnection();
//
//        if (con == null) {
//            throw new BackendServiceException("No Sql Connection.");
//        }
//
//        Profiler.getThreadProfiler().push(EventMsgDb.class, "INSERT");
//        try {
//            PreparedStatement query = con.prepareStatement(queryString);
//
//            query.setArray(1, array);
//            query.executeUpdate();
//        } catch (SQLException ex) {
//            _logger.error(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()), ex);
//            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
//        } finally {
//            Clients.sqlDb.releaseDbConnection(con);
//            Profiler.getThreadProfiler().pop(EventMsgDb.class, "INSERT");
//        }
//    }
}
