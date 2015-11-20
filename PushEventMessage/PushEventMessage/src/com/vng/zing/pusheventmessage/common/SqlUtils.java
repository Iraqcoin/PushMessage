/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class SqlUtils {

    private static final String SPLITTER = String.valueOf("\0007");

    private static final Logger LOGGER = LogManager.getLogger(SqlUtils.class);

    public static void setVariables(PreparedStatement query, int offset, Object... objects) throws SQLException {
        for (Object obj : objects) {
            if (obj instanceof List) {
                query.setString(offset++, toString((List) obj));
            } else if (obj instanceof Integer) {
                query.setInt(offset++, (Integer) obj);
            } else if (obj instanceof Long) {
                query.setLong(offset++, (Long) obj);
            } else if (obj instanceof String) {
                query.setString(offset++, (String) obj);
            } else {
                LOGGER.error("Unsupported data type " + (obj == null ? obj : obj.getClass()));
            }
        }
    }

    public static String toString(Collection list) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : list) {
            builder.append(String.valueOf(obj)).append(SPLITTER);
        }
        return builder.toString();
    }

    public static List<String> toList(String str) {
        String[] split = str.split(SPLITTER);
        return Arrays.asList(split);
    }

}
