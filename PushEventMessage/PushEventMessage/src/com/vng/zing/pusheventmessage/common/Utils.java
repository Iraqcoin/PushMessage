/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import java.util.Calendar;

/**
 *
 * @author namnt3
 */
public class Utils {

    public static boolean isCurrentDay(long milliseconds) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (milliseconds < cal.getTimeInMillis()) {
            return false;
        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        if (milliseconds > cal.getTimeInMillis()) {
            return false;
        }
        return true;
    }

    public static int getDays(long startDate, long endDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long diffInMilis = endDate - cal.getTimeInMillis();
        Long diffInDays = diffInMilis / (24 * 60 * 60 * 1000);
        if (diffInMilis % (24 * 60 * 60 * 1000) > 0) {
            diffInDays++;
        }
        return diffInDays.intValue();
    }

    public static String getFirstNotEmptyValue(String... strings) {
        for (String str : strings) {
            if (str != null && !str.trim().isEmpty()) {
                return str;
            }
        }
        return null;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
