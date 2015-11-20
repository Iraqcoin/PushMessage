/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.common;

import com.vng.zing.pusheventmessage.common.Auth;
import com.vng.zing.pusheventmessage.common.InvalidRequestException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author root
 */
public class SessionUtils {

    public static long getUserId(HttpServletRequest req) throws InvalidRequestException {
        try {
         //   Auth identity = Auth.getIdentity(req);
            Auth identity  = new Auth();
            identity.isLogged = true;
            identity.userId = 123;
            identity.userName = "Hoài Bảo";
            if (identity.isLogged) {
                return identity.userId;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InvalidRequestException(-2, "Loggin is required!");
        }
        throw new InvalidRequestException(-2, "Loggin is required!");
//        try {
//            return (long) req.getSession().getAttribute("userId");
//        } catch (NumberFormatException | NullPointerException ex) {
//            throw new InvalidRequestException(-2, "Loggin is required!");
//        }
    }

    public static void setUserId(HttpServletRequest req, long userId) {
        req.getSession().setAttribute("userId", userId);
    }
}
