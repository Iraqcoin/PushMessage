/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import com.vng.zing.logger.ZLogger;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class HttpUtils {

    private static final Logger LOGGER = ZLogger.getLogger(HttpUtils.class);

    public static void responseJSON(JSONObject json, HttpServletResponse resp) {
        responseJSON(json.toJSONString(), resp);
    }

    public static void responseJSON(String json, HttpServletResponse resp) {
        try {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setHeader("Connection", "Close");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(json);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
//            if (printer != null) {
//                printer.close();
//            }
        }
    }

    public static void responseHTML(String html, HttpServletResponse resp) {
        PrintWriter out = null;
        try {
            resp.setContentType("text/html;charset=UTF-8");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Connection", "Close");
            resp.setStatus(HttpServletResponse.SC_OK);
            out = resp.getWriter();
            out.print(html);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void responseJsonCode(HttpServletResponse resp, int code, String desc) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("desc", desc);
        responseJSON(json, resp);
    }

    public static void responseJsonCode(HttpServletResponse resp, int code) {
        responseJsonCode(resp, code, "");
    }

    public static void responseJsonValues(HttpServletResponse resp, int code, Object value) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("value", value);
        responseJSON(json, resp);
    }
}
