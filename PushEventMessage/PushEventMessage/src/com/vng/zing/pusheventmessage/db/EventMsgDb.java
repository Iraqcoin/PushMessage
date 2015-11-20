/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.db;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.entity.EventMsgException;
import com.vng.zing.pusheventmessage.thrift.EventMsg;
import com.vng.zing.pusheventmessage.thrift.MobilePlatform;
import com.vng.zing.pusheventmessage.thrift.MsgStatus;
import com.vng.zing.pusheventmessage.thrift.PeriodType;
import com.vng.zing.pusheventmessage.thrift.PopupSize;
import com.vng.zing.stats.Profiler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author namnt3
 */
public class EventMsgDb {

    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(EventMsgDb.class);

    private static final String TB_MSGS = "msgs";

    public static void init() throws BackendServiceException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        findByAppId(1);
    }

    public static boolean delete(long eventMsgId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }

        Profiler.getThreadProfiler().push(EventMsgDb.class, "DELETE");
        try {
            PreparedStatement query = con.prepareStatement("DELETE FROM " + TB_MSGS + " WHERE id=?;");
            query.setLong(1, eventMsgId);
            int result = query.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("DELETE eventMsgId[??] fail!, ??", eventMsgId, ex.getMessage()), ex);
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "DELETE");
        }
        return false;
    }

    public static List<EventMsg> findByAppId(int appId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();
        List<EventMsg> result = new ArrayList<EventMsg>();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(EventMsgDb.class, "SELECT");
        try {
            PreparedStatement query = con.prepareStatement("SELECT * FROM " + TB_MSGS + " WHERE app_id=?;");
            query.setInt(1, appId);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                result.add(parseEventMsg(resultSet));
            }
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("findByAppId[??] fail!, ??", appId, ex.getMessage()), ex);
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "SELECT");
        }
        return result;
    }

    public static List<EventMsg> findAndGetBasicInfo(int appId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();
        List<EventMsg> result = new ArrayList<EventMsg>();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(EventMsgDb.class, "SELECT");
        try {
            String fields = "id,app_id,event_name,status,delay_time,"
                    + "portrait_width,portrait_height,landscape_width,landscape_height, "
                    + "start_date,end_date, "
                    + "period_type, visible_times, "
                    + "ios_html_hash, android_html_hash, wphone_html_hash,"
                    + "platforms,"
                    + "package_names, bundle_ids, guids,"
                    + "sdk_versions, app_versions,"
                    + "zalo_ids, app_users, logged_in ";
            PreparedStatement query = con.prepareStatement("SELECT " + fields + " FROM " + TB_MSGS + " WHERE app_id=?;");
            query.setInt(1, appId);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                result.add(parseEventMsg(resultSet));
            }
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("findByAppId[??] fail!, ??", appId, ex.getMessage()), ex);
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "SELECT");
        }
        return result;
    }

    public static void update(EventMsg eventMsg) throws BackendServiceException, EventMsgException {
        String validateResult = validate(eventMsg);
        if (validateResult != null) {
            throw new EventMsgException(validateResult);
        }
        Connection con = Clients.sqlDb.getDbConnection();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(EventMsgDb.class, "UPDATE");
        try {
            PreparedStatement query = con.prepareStatement("UPDATE " + TB_MSGS + " SET " + getEventMsgValues() + " WHERE id=" + eventMsg.getId());
            setEventMsgValues(eventMsg, query, 1);
            query.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("update[??] fail!, ??", eventMsg.getId(), ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("update[??] fail!, ??", eventMsg.getId(), ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "UPDATE");
        }
    }

    public static EventMsg find(long eventMsgId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();
        EventMsg result = null;
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(EventMsgDb.class, "SELECT");
        try {
            PreparedStatement query = con.prepareStatement("SELECT * FROM " + TB_MSGS + " WHERE id=?;");
            query.setLong(1, eventMsgId);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                result = parseEventMsg(resultSet);
                break;
            }
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("find[eventMsgId = ??] fail!, ??", eventMsgId, ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("find[??] fail!, ??", eventMsgId, ex.getMessage()));
        } finally {
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "SELECT");
            Clients.sqlDb.releaseDbConnection(con);
        }
        return result;
    }

    public static long create(EventMsg eventMsg) throws BackendServiceException, EventMsgException {
        String validateResult = validate(eventMsg);
        if (validateResult != null) {
            throw new EventMsgException(validateResult);
        }
        Connection con = Clients.sqlDb.getDbConnection();
        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        Profiler.getThreadProfiler().push(EventMsgDb.class, "INSERT");
        List<EventMsg> result = new ArrayList<EventMsg>();
        try {
            PreparedStatement query = con.prepareStatement("INSERT INTO msgs("
                    + "app_id, event_name, status, delay_time, portrait_width, portrait_height, landscape_width, landscape_height,"
                    + "start_date, end_date, period_type, visible_times, ios_html, android_html, wphone_html,"
                    + "ios_html_hash, android_html_hash, wphone_html_hash, platforms,"
                    + "package_names, bundle_ids, guids, sdk_versions, app_versions, zalo_ids, app_users, logged_in"
                    + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            setEventMsgValues(eventMsg, query, 1);
            int resultCode = query.executeUpdate();
            if (resultCode == 1) {
                query = con.prepareStatement(" SELECT LAST_INSERT_ID() AS id;");
                ResultSet resultSet = query.executeQuery();
                while (resultSet.next()) {
                    return resultSet.getLong("id");
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("create EventMsg fail!, ??", ex.getMessage()), ex);
            throw new BackendServiceException(MsgBuilder.format("INSERT fail!, ??", ex.getMessage()));
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
            Profiler.getThreadProfiler().pop(EventMsgDb.class, "INSERT");
        }
        return -1L;
    }

    public static List<EventMsg> getActiveMsg(int appId) throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();
        List<EventMsg> result = new ArrayList<EventMsg>();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        try {
            PreparedStatement query = con.prepareStatement("SELECT * FROM " + TB_MSGS + " WHERE app_id=? AND status=?;");
            query.setInt(1, appId);
            query.setInt(2, MsgStatus.ACTIVE.getValue());
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                result.add(parseEventMsg(resultSet));
            }
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("getActiveMsg[??] fail!, ??", appId, ex.getMessage()), ex);
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
        }
        return result;
    }

    public static List<EventMsg> getActiveMsg() throws BackendServiceException {
        Connection con = Clients.sqlDb.getDbConnection();
        List<EventMsg> result = new ArrayList<EventMsg>();

        if (con == null) {
            throw new BackendServiceException("No Sql Connection.");
        }
        try {
            PreparedStatement query = con.prepareStatement("SELECT * FROM " + TB_MSGS + " WHERE status=? AND start_date<=? AND ?<=end_date;");
            query.setInt(1, MsgStatus.ACTIVE.getValue());
            query.setLong(2, System.currentTimeMillis());
            query.setLong(3, System.currentTimeMillis());
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                result.add(parseEventMsg(resultSet));
            }
        } catch (SQLException ex) {
            LOGGER.error(MsgBuilder.format("getActiveMsg fail!, ??", ex.getMessage()), ex);
        } finally {
            Clients.sqlDb.releaseDbConnection(con);
        }
        return result;
    }

    public static String getEventMsgValues() {
        return " app_id=?, event_name=?, status=?, delay_time=?, "
                + "portrait_width=?,portrait_height=?,landscape_width=?,landscape_height=?, "
                + "start_date=?,end_date=?, "
                + "period_type=?, visible_times=?, "
                + "ios_html=?, android_html=?, wphone_html=?, "
                + "ios_html_hash=?, android_html_hash=?, wphone_html_hash=?,"
                + "platforms=?,"
                + "package_names=?, bundle_ids=?, guids=?,"
                + "sdk_versions=?, app_versions=?,"
                + "zalo_ids=?, app_users=?, logged_in=? ";
    }

    private static String validate(EventMsg msg) {
        if (msg.getAppId() == 0) {
            return "appId can't be empty.";
        }
        if (msg.getEventName() == null) {
            msg.setEventName("");
        }
        if (msg.getStatus() == null) {
            msg.setStatus(MsgStatus.INACTIVE);
        }
//        if (msg.getEventType() == null) {
//            msg.setEventType(EventType.NOTI);
//        }
        if (msg.getPortrait() == null) {
            PopupSize size = new PopupSize();
            size.setWidth(0);
            size.setHeight(0);
            msg.setPortrait(size);
        }
        if (msg.getLandscape() == null) {
            PopupSize size = new PopupSize();
            size.setWidth(0);
            size.setHeight(0);
            msg.setLandscape(size);
        }
        if (msg.getPeriodType() == null) {
            msg.setPeriodType(PeriodType.NONE);
        }

        if (msg.getIosHtml() != null) {
            msg.setIosHtmlHash(DigestUtils.md5Hex(msg.getIosHtml()));
        }
        if (msg.getAndroidHtml() != null) {
            msg.setAndroidHtmlHash(DigestUtils.md5Hex(msg.getAndroidHtml()));
        }
        if (msg.getWphoneHtml() != null) {
            msg.setWphoneHtmlHash(DigestUtils.md5Hex(msg.getWphoneHtml()));
        }
        return null;
    }

    private static void setEventMsgValues(EventMsg msg, PreparedStatement query, int i) throws SQLException {
        query.setInt(i++, msg.getAppId());
        query.setString(i++, msg.getEventName());
        query.setInt(i++, msg.getStatus().getValue());
        query.setLong(i++, msg.getDelayTime());

        query.setInt(i++, msg.getPortrait().getWidth());
        query.setInt(i++, msg.getPortrait().getHeight());
        query.setInt(i++, msg.getLandscape().getWidth());
        query.setInt(i++, msg.getLandscape().getHeight());

        query.setLong(i++, msg.getStartDate());
        query.setLong(i++, msg.getEndDate());

        query.setInt(i++, msg.getPeriodType().getValue());
        query.setInt(i++, msg.getVisibleTimes());

        query.setString(i++, msg.getIosHtml());
        query.setString(i++, msg.getAndroidHtml());
        query.setString(i++, msg.getWphoneHtml());

        query.setString(i++, msg.getIosHtmlHash());
        query.setString(i++, msg.getAndroidHtmlHash());
        query.setString(i++, msg.getWphoneHtmlHash());

        StringBuilder platforms = new StringBuilder();
        if (msg.getPlatforms() != null && msg.getPlatforms().size() > 0) {
            platforms.append(msg.getPlatforms().get(0).name());
            for (int k = 1; k < msg.getPlatforms().size(); k++) {
                platforms.append(";").append(msg.getPlatforms().get(k).name());
            }
        }
        query.setString(i++, platforms.toString());

        query.setString(i++, toString(msg.getPackageNames()));
        query.setString(i++, toString(msg.getBundleIds()));
        query.setString(i++, toString(msg.getGuids()));

        query.setString(i++, toString(msg.getSdkVersions()));
        query.setString(i++, toString(msg.getAppVersions()));
        query.setString(i++, toString(msg.getZaloIds()));
        query.setString(i++, toString(msg.getAppUsers()));
        query.setBoolean(i++, msg.isLoggedIn());
//        query.setString(i++, msg.isLoggedIn() ? 1: 0);
    }

    public static EventMsg parseEventMsg(ResultSet row) throws SQLException {
        String temp;

        EventMsg msg = new EventMsg();
        msg.setId(row.getLong("id"));
        msg.setAppId(row.getInt("app_id"));
        msg.setEventName(row.getString("event_name"));
        msg.setStatus(MsgStatus.findByValue(row.getInt("status")));
//        msg.setEventType(EventType.findByValue(row.getInt("event_type")));
        try {
            msg.setDelayTime(row.getLong("delay_time"));
        } catch (SQLException e) {
            msg.setDelayTime(-1L);
        }

        PopupSize portrait = new PopupSize();
        portrait.setWidth(row.getInt("portrait_width"));
        portrait.setHeight(row.getInt("portrait_height"));
        msg.setPortrait(portrait);

        PopupSize landscape = new PopupSize();
        portrait.setWidth(row.getInt("landscape_width"));
        portrait.setHeight(row.getInt("landscape_height"));
        msg.setLandscape(landscape);

        msg.setStartDate(row.getLong("start_date"));
        msg.setEndDate(row.getLong("end_date"));

        msg.setPeriodType(PeriodType.findByValue(row.getInt("period_type")));
        msg.setVisibleTimes(row.getInt("visible_times"));

        try {
            msg.setIosHtml(row.getString("ios_html"));
            msg.setAndroidHtml(row.getString("android_html"));
            msg.setWphoneHtml(row.getString("wphone_html"));
        } catch (SQLException e) {
            msg.setIosHtml("");
            msg.setAndroidHtml("");
            msg.setWphoneHtml("");
        }

        msg.setIosHtmlHash(row.getString("ios_html_hash"));
        msg.setAndroidHtmlHash(row.getString("android_html_hash"));
        msg.setWphoneHtmlHash(row.getString("wphone_html_hash"));

        temp = row.getString("platforms");
        List<MobilePlatform> platforms = new ArrayList<MobilePlatform>();
        for (String x : temp.split(";")) {
            if (x != null && x.trim().length() > 0) {
                platforms.add(MobilePlatform.valueOf(x.trim()));
            }
        }
        msg.setPlatforms(platforms);

        msg.setPackageNames(toStringList(row.getString("package_names")));
        msg.setBundleIds(toStringList(row.getString("bundle_ids")));
        msg.setGuids(toStringList(row.getString("guids")));

        msg.setSdkVersions(toStringList(row.getString("sdk_versions")));
        msg.setAppVersions(toStringList(row.getString("app_versions")));

        temp = row.getString("zalo_ids");
        List<Long> zaloIds = new ArrayList<Long>();
        for (String x : temp.split(";")) {
            if (x != null && x.trim().length() > 0) {
                try {
                    zaloIds.add(Long.valueOf(x));
                } catch (NumberFormatException e) {
                }
            }
        }
        msg.setZaloIds(zaloIds);

        msg.setAppUsers(toStringList(row.getString("app_users")));
        msg.setLoggedIn(row.getBoolean("logged_in"));

        return msg;
    }

    public static String toString(List value) {
        StringBuilder result = new StringBuilder();
        if (value != null && value.size() > 0) {
            result.append(value.get(0));
            for (int i = 1; i < value.size(); i++) {
                result.append(";").append(value.get(i));
            }
        }
        return result.toString();
    }

    private static List<String> toStringList(String value) {
        List<String> result = new ArrayList<String>();
        for (String x : value.split(";")) {
            if (x != null && x.trim().length() > 0) {
                result.add(x.trim());
            }
        }
        return result;
    }
}
