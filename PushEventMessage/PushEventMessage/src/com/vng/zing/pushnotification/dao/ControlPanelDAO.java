/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.dao;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public interface ControlPanelDAO {
    
    public List<Map<String, Object>> getAllDevices(String appId, String platform) throws BackendServiceException;
}
