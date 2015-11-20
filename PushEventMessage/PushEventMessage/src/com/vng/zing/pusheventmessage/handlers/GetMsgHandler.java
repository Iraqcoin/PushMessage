/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.handlers;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.model.EventMsgModel;
import com.vng.zing.stats.Profiler;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author namnt3
 */
public class GetMsgHandler extends HttpServlet {

    private static final Logger LOGGER = ZLogger.getLogger(TrackHandler.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doProcess(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doProcess(req, resp);
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse resp) {
        Profiler.createThreadProfilerInHttpProc("GetMsgHandler", req);
        try {
            EventMsgModel.getInst().process(req, resp);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            Profiler.closeThreadProfiler();
        }
    }

}
