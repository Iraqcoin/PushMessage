/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.servers;

import com.vng.zing.jettyserver.WebServers;
import com.vng.zing.pusheventmessage.admin.AdminServlet;
import com.vng.zing.pusheventmessage.admin.AdminServiceServlet;
import com.vng.zing.pusheventmessage.handlers.GetMsgHandler;
import com.vng.zing.pusheventmessage.handlers.NotiInfoHandler;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.IncludableGzipFilter;

/**
 *
 * @author namnq
 */
public class HServers {

    public boolean setupAndStart() {
        ServletContextHandler handler = new ServletContextHandler();
//        HashSessionIdManager sessionManager = new HashSessionIdManager();

        FilterHolder holder = new FilterHolder(IncludableGzipFilter.class);
        holder.setInitParameter("deflateCompressionLevel", "9");
        holder.setInitParameter("minGzipSize", "0");
        holder.setInitParameter("mimeTypes", "application/json");

        handler.addFilter(holder, "/*", EnumSet.of(DispatcherType.REQUEST));

        handler.addServlet(GetMsgHandler.class, "/getmsg");
        handler.addServlet(NotiInfoHandler.class, "/noti-info");

        ServletContextHandler adminHandler = new ServletContextHandler();
        HashSessionManager manager = new HashSessionManager();
        adminHandler.setSessionHandler(new SessionHandler(manager));

        adminHandler.addServlet(AdminServlet.class, "/admin/*");
        adminHandler.addServlet(AdminServiceServlet.class, "/admin-service/*");

        ContextHandler resource_handler = new ContextHandler("/public");
        resource_handler.setResourceBase("./public/");
        resource_handler.setHandler(new ResourceHandler());
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, handler, adminHandler});
//        servers.setup(handler);

        WebServers servers = new WebServers("pusheventmessage");
        servers.setup(handlers);
        return servers.start();
    }
}
