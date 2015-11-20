package com.vng.zing.pushnotification.dao;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public interface EventDAO {

    public enum State {

        PROCESSING, REJECTED, PAUSED, ERROR, DONE, SCHEDULED
    }

    public String create(String appCode, String platform, String userId, String requestId, String request, EventDAO.State state) throws BackendServiceException;

    public Map<String, Object> get(String requestId) throws BackendServiceException;

    public List<Map<String, Object>> getAll() throws BackendServiceException;

    public int update(String requestId, Integer found, Integer pushed, Integer failed, EventDAO.State state) throws BackendServiceException;

    public int error(String requestId, EventDAO.State state, String message) throws BackendServiceException;

    public int pause(String requestId, long deviceId) throws BackendServiceException;

    public List<Map<String, Object>> getPage(int appId, int offset, int pageSize) throws BackendServiceException;

    public long countByApp(int appId) throws BackendServiceException;

}
