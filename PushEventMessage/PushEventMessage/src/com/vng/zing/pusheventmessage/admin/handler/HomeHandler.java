/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.handler;

import com.vng.zing.pusheventmessage.AppConfig;
import com.vng.zing.pusheventmessage.admin.common.SessionUtils;
import com.vng.zing.pusheventmessage.admin.common.Templates;
import com.vng.zing.pusheventmessage.common.HttpUtils;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.common.UtilHelper;
import hapax.TemplateDataDictionary;
import hapax.TemplateException;
import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author root
 */
public class HomeHandler {

    private static final Logger LOGGER = Logger.getLogger(HomeHandler.class);

    public static void doGet(HttpServletRequest req, HttpServletResponse resp, TemplateDataDictionary dic) throws ServletException, IOException {
        try {
            long userId = SessionUtils.getUserId(req);
            System.out.printf("USER ID : " + userId);
            dic.setVariable("logout_url", AppConfig.ZALO_ID_DOMAIN + "/logout?" + MsgBuilder.format("f=??&key=??&cburl=??", AppConfig.APP_ID, UtilHelper.md5(String.valueOf(userId)), AppConfig.APP_DOMAIN + "/admin/login"));
            String str = Templates.apply("layout", dic);
            HttpUtils.responseHTML(str, resp);
        } catch (TemplateException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } catch (InvalidRequestException ex) {
            ex.printStackTrace();
        }
    }

}
