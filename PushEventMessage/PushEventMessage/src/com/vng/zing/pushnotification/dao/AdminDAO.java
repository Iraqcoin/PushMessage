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
public interface AdminDAO {
    
    public boolean initNewApp(String appId, String platform) throws BackendServiceException;
    
    public int saveOrUpdateApiKey(String appId, String platform, String token) throws BackendServiceException;
    
    public String getApiKey(String appId, String platform) throws BackendServiceException;
    
    public List<Map<String, String>> getAllApiKey(String appId) throws BackendServiceException;
}
