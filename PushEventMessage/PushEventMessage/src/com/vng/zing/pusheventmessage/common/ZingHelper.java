/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import com.vng.zing.configer.ZConfig;
import com.vng.zing.jni.zcommonx.wrapper.ZCommonX;
import com.vng.zing.jni.zcommonx.zcommon__IntegralHolder;
import com.vng.zing.jni.zcommonx.zcommon__StrHolder;
import com.vng.zing.logger.ZLogger;
import com.vng.zing.stats.Profiler;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class ZingHelper {

    private static final Logger _Logger = ZLogger.getLogger(ZingHelper.class);
    private static final int _miliseconds_in_days = 86400000;
    private static final int _seconds_expire = 62208000;//2*12*30*24*60*60//2 years
    private static final String _za_private_key = "zatW@1#$T!%";
    private static final String _za_trackingcode_private_key = "zaccW@1#$T!%";

    public static String getCookie(HttpServletRequest request, String name) {
        Profiler.getThreadProfiler().push(ZingHelper.class, "getCookie");

        Cookie[] cookies = request.getCookies();
        String value = null;
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                if (cookies[i].getName().equals(name)) {
                    value = cookies[i].getValue();
                    break;
                }
            }
        }

        Profiler.getThreadProfiler().pop(ZingHelper.class, "getCookie");
        return value;
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value) {
        Cookie[] cookies = request.getCookies();
        boolean foundCookie = false;
        if (cookies != null) {
            for (int i = 0; i < cookies.length; ++i) {
                if (cookies[i].getName().equals(name)) {
                    cookies[i].setValue(value);
                    cookies[i].setMaxAge(_seconds_expire);
                    foundCookie = true;
                    response.addCookie(cookies[i]);
                    break;
                }
            }
        }
        if (foundCookie == false) {
            Cookie cookie = new Cookie(name, value);
            cookie.setMaxAge(_seconds_expire);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(_seconds_expire);
        cookie.setDomain(domain);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

//    public static long encodeVisitorId(long visitorId) {
//        Profiler.getThreadProfiler().push(ZingHelper.class, "encodeVisitorId");  
//
//        long hashResult = -1;
//        try {
//            int hash = ZCommonX.hash(_za_private_key + visitorId + _za_private_key, 0);
//            hashResult = (visitorId & 0xffffffffl) << 32 | (hash & 0xffffffffl);
//        } catch (Exception ex) {
//            _Logger.error(null, ex);
//        }
//
//        Profiler.getThreadProfiler().pop(ZingHelper.class, "encodeVisitorId");
//        return hashResult;
//    }
//    public static long decodeVisitorId(long hashId) {
//        long visitorId = -1;
//        try {
//            visitorId = ((hashId >> 32) & 0xffffffffl);
//            int hash = (int) (hashId & 0xffffffffl);
//            if (hash != ZCommonX.hash(_za_private_key + visitorId + _za_private_key, 0)) {
//                visitorId = -1;
//            }
//        } catch (Exception ex) {
//            _Logger.error(null, ex);
//        }
//        return visitorId;
    public static long decodeTrackingCode(long trackingCodeEncode) {
        long trackingCode = -1;
        try {
            trackingCode = ((trackingCodeEncode >> 32) & 0xffffffffl);
            int hash = (int) (trackingCodeEncode & 0xffffffffl);
            if (hash != ZCommonX.hash(_za_trackingcode_private_key + trackingCode + _za_trackingcode_private_key, 0)) {
                trackingCode = -1;
            }
        } catch (Exception ex) {
            _Logger.error(null, ex);
        }
        return trackingCode;
    }

    public static String md5(String data) {
        try {
            byte[] byteData = data.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteData);
            byte[] rs = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rs.length; ++i) {
                sb.append(Integer.toHexString((rs[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception ex) {
            _Logger.error(null, ex);
        }
        return "";
    }

    public static String hashZaloIdLogin(int zaloId, long timestamp) {
        try {
            String data = zaloId + ZConfig.Instance.getString("system.private_key_zsid_login", "") + timestamp;
            String encodeData = md5(data);
            if (!encodeData.isEmpty()) {
                return encodeData.toString().substring(0, 8);
            }
        } catch (Exception ex) {
            _Logger.error(null, ex);
        }
        return "";
    }

//   public static String keyHash(long visitorId, long timestamp) {     
//        Profiler.getThreadProfiler().push(ZingHelper.class, "keyHash");
//
//        try {
//            String data = visitorId + ZConfig.Instance.getString("system.private_key", "") + timestamp;
//            String encodeData = md5(data);
//            if (!encodeData.isEmpty()) {
//                return encodeData.toString().substring(0, 8);
//            }
//        } catch (Exception ex) {
//            _Logger.error(null, ex);
//        } finally {
//            Profiler.getThreadProfiler().pop(ZingHelper.class, "keyHash");
//        }
//        return "";
    private static int getNumDayFromTime(long preTime, long currTime) {
        int numDay = 0;
        try {
            long disTime = currTime - preTime;
            if (disTime < 0) {
                return -1;
            }
            if (disTime < _miliseconds_in_days) {
                return numDay;
            } else if (disTime < _miliseconds_in_days * 2) {
                return 1;
            } else if (disTime < _miliseconds_in_days * 3) {
                return 2;
            } else if (disTime < _miliseconds_in_days * 4) {
                return 3;
            } else if (disTime < _miliseconds_in_days * 5) {
                return 4;
            } else if (disTime < _miliseconds_in_days * 6) {
                return 5;
            } else if (disTime < _miliseconds_in_days * 7) {
                return 6;
            } else if (disTime < _miliseconds_in_days * 8) {
                return 7;
            } else if (disTime < _miliseconds_in_days * 9) {
                return 8;
            } else if (disTime < _miliseconds_in_days * 10) {
                return 9;
            } else if (disTime < _miliseconds_in_days * 11) {
                return 10;
            } else {
                return 11;
            }
        } catch (Exception ex) {
            _Logger.error(null, ex);
        }
        return numDay;
    }

    public static int getNumDayFromPeriod(long preTime, long currTime) {
        int numDay = 0;
        try {
            long disTime = currTime - preTime;
            if (disTime < 0) {
                return -1;
            }
            if (disTime < _miliseconds_in_days) {
                return numDay;
            } else {
                return (int) (disTime / _miliseconds_in_days);
            }
        } catch (Exception ex) {
            _Logger.error(null, ex);
        }
        return numDay;
    }

    /**
     *
     * @param trackingCode ZA-354351354
     * @return
     */
    public static long validTrackingCode(String trackingCode) {
        Profiler.getThreadProfiler().push(ZingHelper.class, "validTrackingCode");

        long account = -1;
        try {
            String[] dataCode = trackingCode.split("-");
            if (dataCode.length == 2 && dataCode[0].equals("ZA")) {
                long code = Long.valueOf(dataCode[1]);
                account = ZingHelper.decodeTrackingCode(code);
            }
        } catch (Exception ex) {
            _Logger.error(null, ex);
        }

        Profiler.getThreadProfiler().pop(ZingHelper.class, "validTrackingCode");
        return account;
    }

//    public static List<Long> verifyAndGetNumDays(String visitorId, long currTime) {
    //       Profiler.getThreadProfiler().push(ZingHelper.class, "verifyAndGetNumDays"); 
//
//        List<Long> rs = new ArrayList<>();
//        long uuidEncode = -1;
//        long uuid = -1;
//        long numDays = -1;
//        try {
//            String[] arrVisitors = visitorId.split("\\.");
//            if (arrVisitors.length == 3) {
//                uuidEncode = Long.valueOf(arrVisitors[0]);
//                if (uuidEncode > 0) {
//                    uuid = ZingHelper.decodeVisitorId(uuidEncode);
//                    if (uuid > 0) {
//                        long preTime = Long.valueOf(arrVisitors[1]);
//                        if (ZingHelper.keyHash(uuid, preTime).equals(arrVisitors[2])) {
//                            numDays = ZingHelper.getNumDayFromPeriod(preTime, currTime);
//                        } else {
//                            uuid = -1;
//                        }
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            _Logger.error(null, ex);
//        }
//        rs.add(uuid);
//        rs.add(uuidEncode);
//        rs.add(numDays);
//
//        Profiler.getThreadProfiler().pop(ZingHelper.class, "verifyAndGetNumDays");
//        return rs;
    public static int hashDomain(String domain) {
        int hash = 1;
        int c = 0;
        int o;
        if (domain != null) {
            hash = 0;
            for (int index = domain.length() - 1; index >= 0; index--) {
                o = domain.charAt(index);
                hash = (hash << 6 & 268435455) + o + (o << 14);
                c = hash & 266338304;
                hash = c != 0 ? hash ^ c >> 21 : hash;
            }
        }
        return hash;
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) {
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-1");
            byte[] sha1hash = new byte[40];
            try {
                md.update(text.getBytes("iso-8859-1"), 0, text.length());
            } catch (UnsupportedEncodingException ex) {
                _Logger.error(ex.getMessage(), ex);
            }
            sha1hash = md.digest();
            return convertToHex(sha1hash);
        } catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(ZingHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static String noise64(long itemId) {
        long secretKey = 509674831;
        zcommon__StrHolder data = new zcommon__StrHolder();
        com.vng.zing.jni.zcommonx.ZCommonX.zcommon__noise64(data, itemId, secretKey);
        return data.getValue();
    }

    public static long denoise64(String id) {
        try {
            long secretKey = 509674831;
            zcommon__IntegralHolder value = new zcommon__IntegralHolder();
            zcommon__StrHolder data = new zcommon__StrHolder();
            data.setValue(id);
            com.vng.zing.jni.zcommonx.ZCommonX.zcommon__denoise64(value, data, secretKey);
            return value.getValue();
        } catch (Exception ex) {
            return -1;
        }
    }

    public static String listToString(List<String> list, String separate) {
        if (null != list && !list.isEmpty()) {
            return StringUtils.join(list, separate);
        }
        return "";
    }

}
