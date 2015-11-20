package com.vng.zing.pushnotification.dao.impl;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.db.ScheduledTaskDb;
import com.vng.zing.pushnotification.common.MySQLClient;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class EventHandler implements EventDAO {

    @Override
    public String create(String appCode, String platform, String userId, String requestId, String request, EventDAO.State state) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventHandler.class, "CREATE");
        try {
            PreparedStatement stm = conn.prepareStatement("INSERT INTO tbl_history(app_id, platform, requester, request_id, request, state, created_at, updated_at) "
                    + "VALUES(?, ?, ?, ?, ?, ?, now(), now())");
            stm.setString(1, appCode);
            stm.setString(2, platform);
            stm.setString(3, userId);
            stm.setString(4, requestId);
            stm.setString(5, request);
            stm.setString(6, state.name());
            stm.executeUpdate();
            return requestId;
        } catch (SQLException ex) {
            throw new BackendServiceException("Create new event fail. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "CREATE");
        }
    }

    @Override
    public Map<String, Object> get(String requestId) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Map<String, Object> result = new HashMap<>();
        Profiler.getThreadProfiler().push(EventHandler.class, "RETRIEVE EVENT");
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT request_id, app_id, platform, requester, found, pushed, request, paused_at, state "
                    + "FROM tbl_history WHERE request_id = ?");
            stm.setString(1, requestId);
            result = resultSetToMap(stm.executeQuery());
        } catch (SQLException ex) {
            throw new BackendServiceException("Retrieve event fail. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "RETRIEVE EVENT");
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getAll() throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Profiler.getThreadProfiler().push(EventHandler.class, "RETRIEVE ALL EVENT");
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT request_id, app_id, platform, requester, found, pushed, failed, request, paused_at, state "
                    + "FROM tbl_history ORDER BY created_at DESC, updated_at DESC");
            result = resultSetToList(stm.executeQuery());
        } catch (SQLException ex) {
            throw new BackendServiceException("Retrieve all event fail. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "RETRIEVE ALL EVENT");
        }
        return result;
    }

    @Override
    public int error(String requestId, EventDAO.State state, String message) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventHandler.class, "UPDATE EVENT");
        try {
            PreparedStatement stm = conn.prepareStatement("UPDATE tbl_history SET state = ?, error = ?, updated_at = now() WHERE request_id = ?");
            stm.setString(1, state.name());
            stm.setString(2, message);
            stm.setString(3, requestId);
            return stm.executeUpdate();
        } catch (SQLException ex) {
            throw new BackendServiceException("Error occured when updating success event. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "UPDATE EVENT");
        }
    }

    @Override
    public int update(String requestId, Integer found, Integer pushed, Integer failed, EventDAO.State state) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventHandler.class, "UPDATE EVENT");
        try {
            StringBuilder updateClause = new StringBuilder();
            List<Integer> values = new ArrayList<>();
            if (null != found) {
                updateClause.append(",found = ?");
                values.add(found);
            }
            if (null != pushed) {
                updateClause.append(",pushed = pushed + ?");
                values.add(pushed);
            }
            if (null != failed) {
                updateClause.append(",failed = failed + ?");
                values.add(failed);
            }

            String query = String.format("UPDATE tbl_history SET updated_at = now(), state = ? %s WHERE request_id=?", updateClause.toString());
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, state.name());
            int index = 2;
            for (int val : values) {
                stm.setInt(index++, val);
            }
            stm.setString(index, requestId);
            return stm.executeUpdate();
        } catch (SQLException ex) {
            throw new BackendServiceException("Error occured when updating success event. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "UPDATE EVENT");
        }
    }

    @Override
    public int pause(String requestId, long deviceId) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventHandler.class, "UPDATE LAST DEVICE ID");
        try {
            PreparedStatement stm = conn.prepareStatement("UPDATE tbl_history SET updated_at = now(), state = ?, paused_at = ? WHERE request_id=?");
            stm.setString(1, EventDAO.State.PAUSED.name());
            stm.setLong(2, deviceId);
            stm.setString(3, requestId);
            return stm.executeUpdate();
        } catch (SQLException ex) {
            throw new BackendServiceException("Error occured when updating last processed device id. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "UPDATE LAST DEVICE ID");
        }
    }

    private Map<String, Object> resultSetToMap(ResultSet rs) throws SQLException {
        Map<String, Object> data = new HashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        if (rs.next()) {
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i);
                Object value = rs.getObject(key);
                data.put(key, value);
            }
        }
        return data;
    }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        while (rs.next()) {
            Map<String, Object> data = new HashMap<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String key = meta.getColumnName(i);
                Object value = rs.getObject(key);
                if (value instanceof Date) {
                    Date date = (Date) value;
                    value = date.getTime();
                }
                data.put(key, value);
            }
            result.add(data);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getPage(int appId, int offset, int pageSize) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Profiler.getThreadProfiler().push(EventHandler.class, "getPage");
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT request_id, app_id, platform, requester, found, pushed, failed, request, paused_at, state,updated_at "
                    + "FROM tbl_history WHERE app_id=? ORDER BY created_at DESC, updated_at DESC LIMIT ?, ?");
            stm.setInt(1, appId);
            stm.setInt(2, offset);
            stm.setInt(3, pageSize);
            result = resultSetToList(stm.executeQuery());
        } catch (SQLException ex) {
            throw new BackendServiceException("Retrieve all event fail. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "getPage");
        }
        return result;
    }
     public static long count(int appId) throws BackendServiceException {
        String sql = "SELECT COUNT(*) AS C FROM tbl_history WHERE app_id=?";
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
           // LOGGER.error(MsgBuilder.format("DELETE fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(ScheduledTaskDb.class, "DELETE");
        }
    }

    @Override
    public long countByApp(int appId) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventHandler.class, "countByApp");
        try {
            PreparedStatement stm = conn.prepareStatement("SELECT COUNT(*) AS C FROM tbl_history WHERE app_id = ?");
            stm.setInt(1, appId);
            ResultSet result = stm.executeQuery();
            if (result.next()) {
                return result.getLong("C");
            }
            return 0;
        } catch (SQLException ex) {
            throw new BackendServiceException("Error occured when updating success event. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(EventHandler.class, "countByApp");
        }
    }

}
