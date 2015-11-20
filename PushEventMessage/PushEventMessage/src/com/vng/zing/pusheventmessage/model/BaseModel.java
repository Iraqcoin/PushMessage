/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.model;

import com.vng.zing.logger.ZLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * @Note: Class base model xử lý business chung & hàm tiện ích cho tất cả
 * handler/controller, các model chính được thừa kế từ base model này
 *
 * @author namnq
 */
public abstract class BaseModel {

    private static final Logger _Logger = ZLogger.getLogger(BaseModel.class);

    public abstract void process(HttpServletRequest req, HttpServletResponse resp);

    /**
     * outAndClose: print data to client
     *
     * @param req
     * @param resp
     * @param content: String will be produced by content.toString()
     * @return
     */
    protected boolean outAndClose(HttpServletRequest req, HttpServletResponse resp, Object content) {
        boolean result = false;
//        PrintWriter out = null;
        try {
//            out = resp.getWriter();
            resp.getWriter().print(content);
            result = true;
        } catch (Exception ex) {
            _Logger.error(ex.getMessage() + " while processing URI \"" + req.getRequestURI() + "?" + req.getQueryString() + "\"", ex);
        } finally {
//            if (out != null) {
//                out.close();
//            }
        }
        return result;
    }

    /**
     * prepareHeaderHtml: set http header for html content (text/html;
     * charset=UTF-8)
     *
     * @param resp
     */
    protected void prepareHeaderHtml(HttpServletResponse resp) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=UTF-8");
    }

    protected void responseJSON(JSONObject json, HttpServletResponse resp) {
        responseJSON(json.toJSONString(), resp);
    }

    protected void responseJSON(String json, HttpServletResponse resp) {
        try {
            resp.setContentType("application/json;charset=UTF-8");
            resp.setHeader("Connection", "Close");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(json);
        } catch (IOException ex) {
            _Logger.error(ex.getMessage(), ex);
        } finally {
//            if (printer != null) {
//                printer.close();
//            }
        }
    }

    public boolean isMultipart(HttpServletRequest req) throws Exception {
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (isMultipart) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List items = upload.parseRequest(req);
            java.util.Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    String name = item.getFieldName();
                    if (name.lastIndexOf("[]") >= 0) {
                        List<FileItem> l = (List<FileItem>) req.getAttribute(name);
                        if (l == null) {
                            l = new ArrayList<FileItem>();

                        }
                        l.add(item);
                        req.setAttribute(name, l);
                    } else {
                        req.setAttribute(name, item);
                    }
                } else {
                    String name = item.getFieldName();
                    if (!name.isEmpty()) {
                        req.setAttribute(item.getFieldName(), item.getString("utf-8"));
                    }
                }
            }
        }
        return isMultipart;
    }
}
