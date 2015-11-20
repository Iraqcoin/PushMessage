package com.vng.zing.pusheventmessage.handlers;

import com.vng.zing.common.HReqParam;
import com.vng.zing.configer.ZConfig;
import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.common.TemplateManager;
import com.vng.zing.pushnotification.common.Constant;
import com.vng.zing.pushnotification.common.InvalidRequestException;
import com.vng.zing.pushnotification.dao.AdminDAO;
import com.vng.zing.pushnotification.dao.ControlPanelDAO;
import com.vng.zing.pushnotification.dao.DeviceDAO;
import com.vng.zing.pushnotification.dao.EventDAO;
import com.vng.zing.pushnotification.dao.impl.AdminHandler;
import com.vng.zing.pushnotification.dao.impl.ControlPanelHandler;
import com.vng.zing.pushnotification.dao.impl.DeviceHandler;
import com.vng.zing.pushnotification.dao.impl.EventHandler;
import com.vng.zing.pushnotification.service.RequestMessage;
import com.vng.zing.pushnotification.service.Service;
import com.vng.zing.pushnotification.service.ServiceFactory;
import com.vng.zing.pushnotification.utils.DeviceHelper;
import com.vng.zing.pushnotification.utils.Utils;
import com.vng.zing.pushnotification.utils.ZExpression;
import hapax.Template;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class PushNotificationHandler extends HttpServlet{

    private static final Logger _Logger = LoggerFactory.getLogger(PushNotificationHandler.class);
    private static final String[] requiredParams = new String[]{Constant.Request.PARAM_MESSAGE};
    private final EventDAO eventHandler = new EventHandler();
    private final AdminDAO adminHandler = new AdminHandler();
    private final DeviceDAO deviceHandler = new DeviceHandler();
    private final ControlPanelDAO cpHandler = new ControlPanelHandler();
    
    private Template template;
    private TemplateDictionary dictionary = TemplateDictionary.create();

    private static final String PARAM_ACTION = "action";
    private static final String appId = ZConfig.Instance.getString(Service.class, "pushnotification", "app_id_default", "test");
    private static final String platform = ZConfig.Instance.getString(Service.class, "pushnotification", "platform_default", "android");
    
    private String buildFlashMessage(String message){
        if(Utils.isNullOrEmpty(message) || "null".equalsIgnoreCase(message)){
            return "<br/>";
        }else {
            String _template = "<div class=\"message\"><p>%s</p></div>";
            return String.format(_template, message);
        }
    }

    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = HReqParam.getString(req, PARAM_ACTION, "history");
        try{
            HttpSession session = req.getSession(true);
            String flashMessage = buildFlashMessage(String.valueOf(session.getAttribute("flash_message")));
            session.removeAttribute("flash_message");
            dictionary.setVariable("flash_message", flashMessage);
            
            if("history".equalsIgnoreCase(action)){
                template = TemplateManager.getTemplate("view/", "base");
                Template historyTemplate = TemplateManager.getTemplate("view/", "history");
                TemplateDictionary historyDictionary = TemplateDictionary.create();
                
                List<Map<String, Object>> allHistory = eventHandler.getAll();
                if(allHistory == null || allHistory.isEmpty()){
                    historyDictionary.setVariable("history_list", "<tr><td colspan='9'>Not found any histories</td></tr>");
                    dictionary.setVariable("content", historyTemplate.renderToString(historyDictionary));
                    responseHtml(template.renderToString(dictionary), resp);
                    return;
                }
                StringBuilder tableBody = new StringBuilder();
                int count = 1;
                for(Map<String, Object> history : allHistory){
                    tableBody.append("<tr class=\"alt\">")
                            .append("<td>").append(count++).append("</td>")
                            .append("<td>").append(history.get("request_id")).append("</td>")
                            .append("<td>").append(history.get("app_id")).append("</td>")
                            .append("<td>").append(history.get("platform")).append("</td>")
                            .append("<td>").append(history.get("found")).append("</td>")
                            .append("<td>").append(history.get("pushed")).append("</td>")
                            .append("<td>").append(history.get("failed")).append("</td>")
                            .append("<td>").append(history.get("state")).append("</td>")
                            .append("<td>").append(history.get("request")).append("</td>")
                            .append("</tr>");
                }
                historyDictionary.setVariable("history_list", tableBody.toString());
                dictionary.setVariable("content", historyTemplate.renderToString(historyDictionary));
                responseHtml(template.renderToString(dictionary), resp);
            }
            else if("register".equalsIgnoreCase(action)){
                template = TemplateManager.getTemplate("view/", "base");
                Template registerTemplate = TemplateManager.getTemplate("view/", "register");
                TemplateDictionary registerDictionary = TemplateDictionary.create();
                dictionary.setVariable("content", registerTemplate.renderToString(registerDictionary));
                responseHtml(template.renderToString(dictionary), resp);
            }
            else if("admincp".equalsIgnoreCase(action)){
                template = TemplateManager.getTemplate("view/", "base");
                Template admincpTemplate = TemplateManager.getTemplate("view/", "admincp");
                TemplateDictionary admincpDictionary = TemplateDictionary.create();
                
                String apiKey = adminHandler.getApiKey(appId, platform);
                admincpDictionary.setVariable("api_key", apiKey);
                
                List<Map<String, Object>> devices = cpHandler.getAllDevices(appId, platform);
                if(devices == null || devices.isEmpty()){
                    admincpDictionary.setVariable("device_list", "<tr><td colspan='10'>Not found any device</td></tr>");
                    dictionary.setVariable("content", admincpTemplate.renderToString(admincpDictionary));
                    responseHtml(template.renderToString(dictionary), resp);
                    return;
                }
                StringBuilder tableBody = new StringBuilder();
                int count = 1;
                for(Map<String, Object> history : devices){
                    tableBody.append("<tr class=\"alt\">")
                            .append("<td>").append(count++).append("</td>")
                            .append("<td>").append(history.get("sdk_id")).append("</td>")
                            .append("<td>").append(history.get("app_version")).append("</td>")
                            .append("<td>").append(history.get("os_version")).append("</td>")
                            .append("<td>").append(history.get("sdk_version")).append("</td>")
                            .append("<td>").append(history.get("package_name")).append("</td>")
                            .append("<td>").append(history.get("app_user")).append("</td>")
                            .append("<td>").append(history.get("zalo_id")).append("</td>")
                            .append("<td>").append(history.get("created_at")).append("</td>")
                            .append("<td>").append(history.get("updated_at")).append("</td>")
                            .append("</tr>");
                }
                admincpDictionary.setVariable("device_list", tableBody.toString());
                dictionary.setVariable("content", admincpTemplate.renderToString(admincpDictionary));
                responseHtml(template.renderToString(dictionary), resp);
            }
        }catch(BackendServiceException e){
            
        } catch (TemplateException ex) {
            java.util.logging.Logger.getLogger(PushNotificationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HttpSession session = req.getSession(true);
            String action = HReqParam.getString(req, PARAM_ACTION, "");
            if("add_key".equals(action)){
                    String apiKey = HReqParam.getString(req, "api_key", "");
                    if(!Utils.isNullOrEmpty(apiKey)){
                        adminHandler.saveOrUpdateApiKey(appId, platform, apiKey);
                        session.setAttribute("flash_message", "Add API key success");
                    }else {
                        session.setAttribute("flash_message", "API key is null or empty");
                    }
                    resp.sendRedirect("/push?action=admincp");

            }
            else if("add_device".equals(action)) {
                String sdkId = HReqParam.getString(req, "sdk_id", "");
                if(!Utils.isNullOrEmpty(sdkId)){
                    String token = HReqParam.getString(req, "token", "");
                    Map<String, String> extra = new HashMap<>();
                    for(String extraCol : DeviceHelper.extraColumns){
                        String value = HReqParam.getString(req, extraCol, "");
                        if(!Utils.isNullOrEmpty(value)){
                            extra.put(extraCol, value);
                        }
                    }
                    deviceHandler.addOrUpdate(appId, platform, Long.parseLong(sdkId), token, extra);
                    session.setAttribute("flash_message", "Add device success");
                }else {
                    session.setAttribute("flash_message", "SDK Id is null or empty");
                }
                resp.sendRedirect("/push?action=admincp");
            }
            else{
                pushnotification(req, resp);
                resp.sendRedirect("/push?action=history");
            }
        }
        catch (InvalidRequestException ex) {
            responseJSON(501, ex.getMessage(), resp);
        } 
        catch (BackendServiceException ex) {
            responseJSON(502, ex.getMessage(), resp);  
        }
    }
    
    private void pushnotification(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
            , BackendServiceException, InvalidRequestException {
        HttpSession session = req.getSession(true);
        for(String requiredParam : requiredParams){
            String value = HReqParam.getString(req, requiredParam, null);
            if(value == null){
                responseJSON(401, "Param " + requiredParam + " is missed", resp);
                session.setAttribute("flash_message", "Param " + requiredParam + " is missed");
                return;
            }
        }
        String _appId = HReqParam.getString(req, Constant.Request.PARAM_APP_ID, PushNotificationHandler.appId);
        String _platform = HReqParam.getString(req, Constant.Request.PARAM_PLATFORM, PushNotificationHandler.platform);
        
        String senderId = adminHandler.getApiKey(_appId, _platform);
        if(senderId == null || "".equals(senderId)){
            responseJSON(402, _appId + " has not registered " + _platform + " platform", resp);
            session.setAttribute("flash_message", _appId + " has not registered " + _platform + " platform");
            return;
        }

        String message = HReqParam.getString(req, Constant.Request.PARAM_MESSAGE, null);
        String title = HReqParam.getString(req, Constant.Request.PARAM_TITLE, null);
        String requestId = String.valueOf(Calendar.getInstance().getTimeInMillis());

        RequestMessage.Builder builder = new RequestMessage.Builder(_appId, _platform)
                .setRequiredParams("test@vng.com.vn", requestId, senderId)
                .addData("message", message)
                .addData("title", title);
        addExtraData(req, builder);
        RequestMessage request = builder.build();
        eventHandler.create(_appId, _platform, "test@vng.com.vn", requestId, request.valueAsJson()
                , EventDAO.State.PROCESSING);
        ServiceFactory.getInstance().deliver(request);
        session.setAttribute("flash_message", "Deliver success request id " + requestId);
//        responseJSON(200, requestId, resp);
    }
    
    protected void responseHtml(String html, HttpServletResponse resp){
        try {
            resp.setContentType("text/html");
            resp.setHeader("Connection", "Close");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(html);
        } catch (IOException ex) {
            _Logger.error(ex.getMessage(), ex);
        }
    }
    
    protected void responseJSON(int statusCode, String message, HttpServletResponse resp) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", statusCode);
            jsonObject.put("msg", message);
            
            resp.setContentType("application/json;charset=UTF-8");
            resp.setHeader("Connection", "Close");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(jsonObject.toJSONString());
        } catch (IOException ex) {
            _Logger.error(ex.getMessage(), ex);
        }
    }

    private static final String[] extraFields = new String[]{
            Constant.Request.PARAM_APP_VERSION
            , Constant.Request.PARAM_OS_VERSION
            , Constant.Request.PARAM_SDK_VERSION
            , Constant.Request.PARAM_PACKAGE_NAME
    };
    
    private void addExtraData(HttpServletRequest req, RequestMessage.Builder builder) {
        for(String extraField : extraFields){
            String value = HReqParam.getString(req, extraField, null);
            if(!Utils.isNullOrEmpty(value)){
                builder.addExpression(new ZExpression(extraField, value
                        , ZExpression.ZOperator.AND, ZExpression.ZComparisonOperator.EQUAL));
            }
        }
    }
    
}

