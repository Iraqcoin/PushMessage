/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.handler;

import com.vng.zing.pusheventmessage.AppConfig;
import com.vng.zing.pusheventmessage.admin.common.Params;
import com.vng.zing.pusheventmessage.admin.common.SessionUtils;
import com.vng.zing.pusheventmessage.admin.common.Templates;
import com.vng.zing.pusheventmessage.common.Auth;
import com.vng.zing.pusheventmessage.common.HttpUtils;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import com.vng.zing.pusheventmessage.model.ZaloIDModel;
import hapax.TemplateDataDictionary;
import hapax.TemplateException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class UserHandler {

    private static final Logger LOGGER = Logger.getLogger(UserHandler.class);

    public static void getProfile(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, IOException {
        try {
            Auth identity = Auth.getIdentity(req);
            long userId = SessionUtils.getUserId(req);
            JSONObject profile = new JSONObject();
            profile.put("name", identity.displayName);
            profile.put("avatar", identity.avatarURL);//http://placehold.it/24x24"

            JSONObject json = new JSONObject();
            json.put("code", 0);
            json.put("value", profile);

            HttpUtils.responseJSON(json, resp);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InvalidRequestException(-2, "Loggin is required!");
        }
    }

    public static void login(HttpServletRequest req, HttpServletResponse resp) throws InvalidRequestException, IOException {
//        System.out.println("### ATTRIBUTE ###");
//        Enumeration<String> attributeNames = req.getAttributeNames();
//        while (attributeNames.hasMoreElements()) {
//            String att = attributeNames.nextElement();
//            System.out.println(att + " : " + req.getAttribute(att));
//        }
//
//        System.out.println("### PARAM ###");
//        Enumeration<String> params = req.getParameterNames();
//        while (params.hasMoreElements()) {
//            String att = params.nextElement();
//            System.out.println(att + " : " + req.getParameter(att));
//        }

        long userId = SessionUtils.getUserId(req);
        String password = Params.getPassword(req);
        if (password.equals(String.valueOf(userId))) {
            SessionUtils.setUserId(req, userId);
            HttpUtils.responseJsonCode(resp, 0, "Logged In.");
            return;
        }
        HttpUtils.responseJsonCode(resp, 2, "UserId/Password not match.");
    }

    public static void logout(HttpServletRequest req, HttpServletResponse resp) {
        req.getSession().invalidate();
        HttpUtils.responseJsonCode(resp, 0, "Logged out.");
    }

    public static void loginByZaloId(HttpServletRequest req, HttpServletResponse resp, TemplateDataDictionary dic) throws InvalidRequestException, IOException {
        String loginForm = ZaloIDModel.getInstance().getLoginTemplateHtml(AppConfig.LOGIN_TEMPLATE);
//        System.out.println(loginForm);
        dic.setVariable("CONTENT", loginForm);
        try {
            String str = Templates.apply("login", dic);
            HttpUtils.responseHTML(str, resp);
        } catch (TemplateException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

}
