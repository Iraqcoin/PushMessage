/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pushnotification.dao;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.entity.DeviceInfo;
import com.vng.zing.pushnotification.service.RequestMessage;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public interface DeviceDAO {

    //*****    add/insert   ****\\
    public int addOrUpdate(String appId, String platform, long sdkId, String token, Map<String, String> extra) throws BackendServiceException;

    //****    remove/delete    ****\\
    public int remove(String appId, String platform, long deviceId) throws BackendServiceException;

    public int remove(String appId, String platform, String token) throws BackendServiceException;

    public int batchRemove(String appId, String platform, List<Long> deviceId) throws BackendServiceException;

    public int remove(String appId, String platform, List<String> token) throws BackendServiceException;

    //****    update    ****\\
    public int update(String appId, String platform, String oldToken, String newToken) throws BackendServiceException;

    public int batchUpdateByToken(String appId, String platform, Map<String, String> mapOldToNewToken) throws BackendServiceException;

    //****    select/retrieve    ****\\
    public List<Long> getDeviceIds(RequestMessage requestMessage) throws BackendServiceException;

    public List<String> getTokensByDeviceId(String appId, String platform, List<Long> deviceId) throws BackendServiceException;

    public long count(int appId, String platform) throws BackendServiceException;
    
    public List<DeviceInfo> getDevicePage(int appId, String platform, int offset, int size) throws BackendServiceException;
    
    public long count(RequestMessage requestMessage) throws BackendServiceException;
}
