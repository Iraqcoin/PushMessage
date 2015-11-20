package com.vng.zing.pushnotification.dao.impl;

import com.vng.zing.configer.ZConfig;
import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.entity.DeviceInfo;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.common.MySQLClient;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.utils.DeviceHelper;
import com.vng.zing.pushnotification.utils.Utils;
import com.vng.zing.pushnotification.utils.ZExpression;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.simple.JSONValue;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class DeviceHandler implements DeviceDAO {

    private static final Logger logger = ZLogger.getLogger(DeviceHandler.class);
    
    @Override
    public int remove(String appCode, String platform, long deviceId) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(DeviceHandler.class, "REMOVE DEVICE BY ID");
        int result;
        try {
            String query = String.format("DELETE FROM %s WHERE  sdk_id = ?", DeviceHelper.getTokenTableName(appCode, platform));
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setLong(1, deviceId);
            result = stm.executeUpdate();

            query = String.format("DELETE FROM %s WHERE  sdk_id = ?", DeviceHelper.getExtraTableName(appCode, platform));
            PreparedStatement extraStm = conn.prepareStatement(query);
            extraStm.setLong(1, deviceId);
            result &= extraStm.executeUpdate();
            return result;
        } catch (SQLException ex) {
            throw new BackendServiceException("Remove device fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "REMOVE DEVICE BY ID");
        }
    }

    @Override
    public int remove(String appCode, String platform, String token) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        int result = 0;
        Profiler.getThreadProfiler().push(DeviceHandler.class, "REMOVE DEVICE BY TOKEN");
        try {
            String query = String.format("SELECT sdk_id FROM %s WHERE  token = ?",
                    DeviceHelper.getTokenTableName(appCode, platform));
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, token);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                result = remove(appCode, platform, rs.getLong(1));
            }

            return result;
        } catch (SQLException ex) {
            throw new BackendServiceException("Deactive device fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "REMOVE DEVICE BY TOKEN");
        }
    }
    
    
    private int batchRemoveExtra(String appCode, String platform, List<Long> deviceId) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        int result = 0;
        Profiler.getThreadProfiler().push(DeviceHandler.class, "REMOVE DEVICES BY ID");
        try {
            if (deviceId == null || deviceId.isEmpty()) {
                return result;
            }
            StringBuilder inClause = new StringBuilder();
            for (long id : deviceId) {
                inClause.append(",")
                        .append(id);
            }
            String query = String.format("DELETE FROM %s WHERE  sdk_id IN (%s)", DeviceHelper.getExtraTableName(appCode, platform), inClause.substring(1));
            PreparedStatement stm1 = conn.prepareStatement(query);
            stm1.executeUpdate();

            return result;
        } catch (SQLException ex) {
            throw new BackendServiceException("Remove devices fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "REMOVE DEVICES BY ID");
        }
    }
    

    private int batchRemoveToken(String appCode, String platform, List<Long> deviceId) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        int result = 0;
        Profiler.getThreadProfiler().push(DeviceHandler.class, "REMOVE DEVICES BY ID");
        try {
            if (deviceId == null || deviceId.isEmpty()) {
                return result;
            }
            StringBuilder inClause = new StringBuilder();
            for (long id : deviceId) {
                inClause.append(",")
                        .append(id);
            }
            String query = String.format("DELETE FROM %s WHERE  sdk_id IN (%s)", DeviceHelper.getTokenTableName(appCode, platform), inClause.substring(1));
            PreparedStatement stm = conn.prepareStatement(query);
            result = stm.executeUpdate();

            return result;
        } catch (SQLException ex) {
            throw new BackendServiceException("Remove devices fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "REMOVE DEVICES BY ID");
        }
    }
    
    @Override
    public int batchRemove(String appCode, String platform, List<Long> deviceId) throws BackendServiceException {
        if(deviceId == null || deviceId.isEmpty()){
            return 0;
        }
        int removedTokens = batchRemoveToken(appCode, platform, deviceId);
        int removedExtra = batchRemoveExtra(appCode, platform, deviceId);
        if(removedTokens != removedExtra){
            StringBuilder listDeviceId = new StringBuilder();
            for(long id : deviceId){
                listDeviceId.append(",").append(id);
            }
            logger.warn("Number of removed tokens is not equal total of extra records. Need checking manually list id: " + listDeviceId.substring(1));
        }
        return removedTokens;
    }

    @Override
    public int remove(String appCode, String platform, List<String> token) throws BackendServiceException {
        boolean markDeleteToken = ZConfig.Instance.getBoolean(DeviceHandler.class, "pushnotification", "markDeleteToken", false);
        int result;
        if (markDeleteToken) {
            result = markDelete(appCode, platform, token);
        } else {
            result = immediateDelete(appCode, platform, token);
        }
        return result;
    }

    @Override
    public int update(String appCode, String platform, String oldToken, String newToken) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(DeviceHandler.class, "UPDATE DEVICE");
        try {
            String query = String.format("UPDATE %s SET token = ? WHERE  token = ?", DeviceHelper.getTokenTableName(appCode, platform));
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setString(1, newToken);
            stm.setString(2, oldToken);
            return stm.executeUpdate();
        } catch (SQLException ex) {
            throw new BackendServiceException("Update canonical token fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "UPDATE DEVICE");
        }
    }

    @Override
    public int batchUpdateByToken(String appCode, String platform, Map<String, String> mapOldToNewToken) throws BackendServiceException {
        int result = 0;
        if (mapOldToNewToken == null || mapOldToNewToken.isEmpty()) {
            return result;
        }

        for (Map.Entry<String, String> entry : mapOldToNewToken.entrySet()) {
            result += update(appCode, platform, entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    public List<Long> getDeviceIds(RequestMessage requestMessage) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        List<Long> deviceIds = new ArrayList<>();
        Profiler.getThreadProfiler().push(DeviceHandler.class, "getDeviceIds");
        try {
            List<ZExpression> expressions = requestMessage.getExpressions();
            StringBuilder whereClause = buildWhereClause(expressions);

            String tableName = DeviceHelper.getExtraTableName(requestMessage.getAppId(), requestMessage.getPlatform());

            int lastDeviceId = requestMessage.getExtra(Constant.Request.PARAM_LAST_DEVICE_ID, Integer.class, 0);
            String query = String.format("SELECT sdk_id FROM %s WHERE ( %s ) AND sdk_id > ? ORDER BY sdk_id ",
                    tableName, whereClause.toString());
            if (!expressions.isEmpty()) {
                int margin = expressions.get(0).getOperator().getOperator().length();
                query = String.format("SELECT sdk_id FROM %s WHERE ( %s ) AND sdk_id > ? ORDER BY sdk_id ",
                        tableName, whereClause.substring(margin + 2));
            }
            PreparedStatement stm = conn.prepareStatement(query);
            int index = 1;
            for (ZExpression exp : expressions) {
                if (ZExpression.ZComparisonOperator.LIKE.equals(exp.getComparisonOperator())) {
                    stm.setString(index++, exp.getValue() + "%");
                } else if (ZExpression.ZComparisonOperator.IN.equals(exp.getComparisonOperator())) {
                    List<String> inValues = (List<String>) JSONValue.parse(exp.getValue());
                    for (String val : inValues) {
                        stm.setString(index++, val);
                    }
                } else {
                    stm.setString(index++, exp.getValue());
                }
            }
            stm.setInt(index, lastDeviceId);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                deviceIds.add(rs.getLong("sdk_id"));
            }
        } catch (SQLException ex) {
            throw new BackendServiceException("Get device id fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "getDeviceIds");
        }
        return deviceIds;
    }

    @Override
    public long count(RequestMessage requestMessage) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(DeviceHandler.class, "getDeviceIds");
        try {
            List<ZExpression> expressions = requestMessage.getExpressions();
            StringBuilder whereClause = buildWhereClause(expressions);

            String tableName = DeviceHelper.getExtraTableName(requestMessage.getAppId(), requestMessage.getPlatform());
            String tokenTableName = DeviceHelper.getTokenTableName(requestMessage.getAppId(), requestMessage.getPlatform());

            int lastDeviceId = requestMessage.getExtra(Constant.Request.PARAM_LAST_DEVICE_ID, Integer.class, 0);
//            String query = String.format("SELECT count(*) AS C FROM %s a RIGHT JOIN %s b ON a.sdk_id=b.sdk_id WHERE ( %s ) AND a.sdk_id > ?",
//                    tableName, tokenTableName, whereClause.toString());
            String query = String.format("SELECT count(*) AS C FROM %s WHERE ( %s ) AND sdk_id > ?",
                    tableName, whereClause.toString());
            if (!expressions.isEmpty()) {
                int margin = expressions.get(0).getOperator().getOperator().length();
                query = String.format("SELECT count(*) AS C FROM %s WHERE ( %s ) AND sdk_id > ?",
                        tableName, whereClause.substring(margin + 2));
            }
//            System.out.println(query);
            PreparedStatement stm = conn.prepareStatement(query);
            int index = 1;
            for (ZExpression exp : expressions) {
                if (ZExpression.ZComparisonOperator.LIKE.equals(exp.getComparisonOperator())) {
                    stm.setString(index++, exp.getValue() + "%");
                } else if (ZExpression.ZComparisonOperator.IN.equals(exp.getComparisonOperator())) {
                    List<String> inValues = (List<String>) JSONValue.parse(exp.getValue());
                    for (String val : inValues) {
                        stm.setString(index++, val);
                    }
                } else {
                    stm.setString(index++, exp.getValue());
                }
            }
            stm.setInt(index, lastDeviceId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getLong("C");
            }
        } catch (SQLException ex) {
            throw new BackendServiceException("Get device id fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "getDeviceIds");
        }
        return 0;
    }

    @Override
    public List<String> getTokensByDeviceId(String appCode, String platform, List<Long> deviceId) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        List<String> tokens = new ArrayList<>();
        Profiler.getThreadProfiler().push(DeviceHandler.class, "getTokensByDeviceId");
        try {
            StringBuilder inClause = new StringBuilder();
            for (long id : deviceId) {
                inClause.append(",")
                        .append(id);
            }
            if ("".equals(inClause.toString())) {
                return tokens;
            }

            String table = DeviceHelper.getTokenTableName(appCode, platform);
            String query = String.format("SELECT sdk_id, token FROM %s WHERE sdk_id IN (%s) AND active = 'Y'", table, inClause.substring(1));
            PreparedStatement stm = conn.prepareStatement(query);
            ResultSet rs = stm.executeQuery();
            while (rs.next()) {
                tokens.add(rs.getString("token"));
            }
        } catch (SQLException ex) {
            throw new BackendServiceException("Get tokens by device id fail. Caused by: " + ex.getMessage());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "getTokensByDeviceId");
        }
        return tokens;
    }

    @Override
    public int addOrUpdate(String appId, String platform, long sdkId, String token, Map<String, String> extra) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(DeviceHandler.class, "ADD_OR_UPDATE DEVICE");
        int result = 0;
        try {
            if (!Utils.isNullOrEmpty(token)) {
                String query = String.format("INSERT INTO %s (sdk_id, token, created_at, updated_at) VALUES(?,?, now(), now())  "
                        + "ON DUPLICATE KEY UPDATE token=VALUES(token), sdk_id=VALUES(sdk_id), updated_at=values(updated_at)", DeviceHelper.getTokenTableName(appId, platform));
                PreparedStatement stm = conn.prepareStatement(query);
                stm.setLong(1, sdkId);
                stm.setString(2, token);
                result = stm.executeUpdate();
            }
            if (extra != null && !extra.isEmpty()) {
                addOrUpdateMetadata(appId, platform, sdkId, extra, conn);
            }

        } catch (SQLException ex) {
            throw new BackendServiceException("Add or update device fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "ADD_OR_UPDATE DEVICE");
        }
        return result;
    }

    //*** private method   ***\\
    private int markDelete(String appCode, String platform, List<String> token) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(DeviceHandler.class, "DEACTIVE DEVICE");
        try {
            if (token == null || token.isEmpty()) {
                return 0;
            }
            StringBuilder inClause = new StringBuilder();
            for (String tk : token) {
                inClause.append(",'")
                        .append(tk)
                        .append("'");
            }
            String query = String.format("UPDATE %s SET active = 'N' WHERE  token IN (%s)",
                    DeviceHelper.getTokenTableName(appCode, platform), inClause.substring(1));
            PreparedStatement stm = conn.prepareStatement(query);
            return stm.executeUpdate();
        } catch (SQLException ex) {
            throw new BackendServiceException("Deactive device fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "DEACTIVE DEVICE");
        }
    }

    /**
     * add or update meta data (extra data)
     *
     * @param appCode
     * @param platform
     * @param deviceId
     * @param extra
     * @param conn
     * @throws SQLException
     */
    private void addOrUpdateMetadata(String appCode, String platform, long deviceId, Map<String, String> extra, Connection conn) throws SQLException {

        StringBuilder colNames = new StringBuilder();
        StringBuilder questionMarks = new StringBuilder();
        StringBuilder updateClause = new StringBuilder();
        List<String> values = new ArrayList<>();
        for (Map.Entry<String, String> entry : extra.entrySet()) {
            String colName = entry.getKey();
            String colVal = entry.getValue();
            for (String extraColumn : DeviceHelper.extraColumns) {
                if (extraColumn.equals(colName)) {
                    colNames.append(",")
                            .append(colName);
                    questionMarks.append(",?");
                    values.add(colVal);
                    updateClause.append(",")
                            .append(colName)
                            .append("=VALUES(").append(colName).append(") ");
                    break;
                }
            }
        }
        if (colNames.length() > 0 && questionMarks.length() > 0 && updateClause.length() > 0) {
            String tableName = DeviceHelper.getExtraTableName(appCode, platform);
            String query = String.format("INSERT INTO %s(sdk_id %s, created_at, updated_at) VALUES(? %s, now(), now()) ON DUPLICATE KEY UPDATE %s, updated_at=values(updated_at)",
                    tableName, colNames.toString(), questionMarks.toString(), updateClause.substring(1));
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setLong(1, deviceId);
            int index = 2;
            for (String val : values) {
                stm.setString(index++, val);
            }
            stm.executeUpdate();
        }
    }

    /**
     *
     * @param expressions
     * @return
     */
    private StringBuilder buildWhereClause(List<ZExpression> expressions) {
        StringBuilder whereClause = new StringBuilder("1");
        for (ZExpression exp : expressions) {
            if (ZExpression.ZComparisonOperator.IN.equals(exp.getComparisonOperator())) {
                List<String> inValues = (List<String>) JSONValue.parse(exp.getValue());
                String questionMarks = "";
                for (String inValue : inValues) {
                    questionMarks += ",?";
                }
                whereClause.append(" ")
                        .append(exp.getOperator().getOperator())
                        .append(" ")
                        .append(exp.getProperty())
                        .append(" ")
                        .append(exp.getComparisonOperator().getOperator())
                        .append(" (")
                        .append(questionMarks.substring(Utils.isNullOrEmpty(questionMarks) ? 0 : 1))
                        .append(")");
            } else {
                whereClause.append(" ")
                        .append(exp.getOperator().getOperator())
                        .append(" ")
                        .append(exp.getProperty())
                        .append(" ")
                        .append(exp.getComparisonOperator().getOperator())
                        .append(" ")
                        .append("?");
            }
        }
        return whereClause;
    }

    /**
     *
     * @param appCode
     * @param platform
     * @param token
     * @return
     * @throws BackendServiceException
     */
    private int immediateDelete(String appCode, String platform, List<String> token) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        int result = 0;
        Profiler.getThreadProfiler().push(DeviceHandler.class, "DELETE DEVICE");
        try {
            if (token == null || token.isEmpty()) {
                return result;
            }
            StringBuilder inClause = new StringBuilder();
            for (String tk : token) {
                inClause.append(",'")
                        .append(tk)
                        .append("'");
            }
            String query = String.format("SELECT sdk_id FROM %s WHERE  token IN (%s)",
                    DeviceHelper.getTokenTableName(appCode, platform), inClause.substring(1));
            PreparedStatement stm = conn.prepareStatement(query);
            ResultSet rs = stm.executeQuery();
            List<Long> deviceIds = new ArrayList<>();
            while (rs.next()) {
                deviceIds.add(rs.getLong(1));
            }

            return batchRemove(appCode, platform, deviceIds);
        } catch (SQLException ex) {
            throw new BackendServiceException("Deactive device fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "DELETE DEVICE");
        }
    }

    @Override
    public long count(int appId, String platform) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        int result = 0;
        Profiler.getThreadProfiler().push(DeviceHandler.class, "COUNT DEVICE");
        try {
            String query = "SELECT COUNT(*) AS C from " + DeviceHelper.getTokenTableName(appId, platform);
            PreparedStatement stm = conn.prepareStatement(query);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getLong("C");
            }
            return 0;
        } catch (SQLException ex) {
            throw new BackendServiceException("Deactive device fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "COUNT DEVICE");
        }
    }

    @Override
    public List<DeviceInfo> getDevicePage(int appId, String platform, int offset, int size) throws BackendServiceException {
        Connection conn = MySQLClient.getConnection();
        if (conn == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(DeviceHandler.class, "getDevicePage");
        try {
            String query = "SELECT a.sdk_id, a.token,a.created_at,a.updated_at, app_version,os_version,sdk_version,package_name,app_user,zalo_id "
                    + "FROM %s AS a LEFT JOIN %s AS b ON a.sdk_id=b.sdk_id "
                    + "LIMIT ?, ?;";
            query = String.format(query, DeviceHelper.getTokenTableName(appId, platform), DeviceHelper.getExtraTableName(appId, platform));
            PreparedStatement stm = conn.prepareStatement(query);
            stm.setInt(1, offset);
            stm.setInt(2, size);
            ResultSet rs = stm.executeQuery();

            List<DeviceInfo> result = new ArrayList<>();
            while (rs.next()) {
                result.add(DeviceInfo.parse(rs));
            }
            return result;
        } catch (SQLException ex) {
            throw new BackendServiceException("Deactive device fail. Caused by: " + ex.getMessage(), ex.getErrorCode());
        } finally {
            MySQLClient.releaseConnection(conn);
            Profiler.getThreadProfiler().pop(DeviceHandler.class, "getDevicePage");
        }
    }

//    private JSONObject convertToJson(ResultSet rs) throws SQLException {
//        JSONObject json = new JSONObject();
//        json.put("id", rs.getLong("sdk_id"));
//        return json;
//    }
}
