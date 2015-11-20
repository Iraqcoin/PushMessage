/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.AppConfig;
import com.vng.zing.pusheventmessage.admin.handler.HomeHandler;
import com.vng.zing.pusheventmessage.admin.handler.UserHandler;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author root
 */
public class AdminServlet extends HttpServlet {

    private static final org.apache.log4j.Logger LOGGER = ZLogger.getLogger(AdminServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        TemplateDataDictionary dic = getDictionary();
        LOGGER.info("GET " + pathInfo);

        System.out.println("GET : " + pathInfo);
        //if (pathInfo == null) {
            HomeHandler.doGet(req, resp, dic);
            return;
       // }
//        if (pathInfo.equals("/login")) {
//            try {
//                UserHandler.loginByZaloId(req, resp, dic);
//                return;
//            } catch (InvalidRequestException ex) {
//                LOGGER.error(ex.getMessage(), ex);
//            } catch (Exception ex) {
//                LOGGER.error(ex.getMessage(), ex);
//            }
//        }

    }

    protected TemplateDataDictionary getDictionary() {
        TemplateDataDictionary dic = TemplateDictionary.create();
        dic.setVariable("app_domain", AppConfig.APP_DOMAIN);
        return dic;
    }

}
