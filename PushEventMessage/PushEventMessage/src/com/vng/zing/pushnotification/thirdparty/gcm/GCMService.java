package com.vng.zing.pushnotification.thirdparty.gcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vng.zing.configer.ZConfig;
import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.model.NotiInfoModel;
import com.vng.zing.pushnotification.service.Service;
import com.vng.zing.pushnotification.utils.Utils;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class GCMService {

    private static final Logger logger = ZLogger.getLogger(GCMService.class);

    private GCMService() {
    }

    public GCMCallback push(GCMRequest requestBody) {
        GCMCallback callback = new GCMCallback();
        HttpPost request = null;
        try {
            request = new HttpPost(gcmSendUrl);
            request.addHeader(HEADER_NAME_AUTHORIZATION, "key=" + requestBody.senderId);
            request.addHeader(HEADER_NAME_CONTENT_TYPE, CONTENT_TYPE_JSON);
            String body = objectMapper.writeValueAsString(requestBody);
            if (logger.isDebugEnabled()) {
                logger.debug("Sending body: " + body);
            }
            request.setEntity(new StringEntity(body, Charset.forName("UTF-8")));
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            if (400 == statusCode) {
                logger.error(String.format("Got '400' response code with status message '%s'\n"
                        + "Check that the JSON message is properly formatted and contains valid fields", response.getStatusLine().getReasonPhrase()));
                logger.error("Sending body: " + body);
                callback.failure(GCMCallback.FailureCode.BAD_REQUEST);
            } else if (401 == statusCode) {
                logger.error(String.format("Got '401' response code with status message '%s'\n"
                        + "Check that the token you're sending inside the Authentication header is the correct API key associated with your project", response.getStatusLine().getReasonPhrase()));
                callback.failure(GCMCallback.FailureCode.UNAUTHORIZED);
            } else if (500 <= statusCode && 600 > statusCode) {
                logger.error(String.format("Got '500' response code with status message '%s'", response.getStatusLine().getReasonPhrase()));
                callback.failure(GCMCallback.FailureCode.SERVICE_UNAVAILABLE);
                callback.setRetryTime(getRetryDelay(response));
                callback.markedRetry();
            } else if (200 != statusCode) {
                logger.error(String.format("Got unknown status code {} with status message %s", statusCode, response.getStatusLine().getReasonPhrase()));
                callback.failure(GCMCallback.FailureCode.UNKNOWN_ERROR);
            } else {  // statusCode == 200
                GCMResponse data = objectMapper.readValue(response.getEntity().getContent(), GCMResponse.class);
                if (0 == data.getFailure() && 0 == data.getCanonicalIds()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Push successful");
                    }
                    callback.successful();
                } else {
                    parseResponse200(requestBody, data, callback);
                }
            }
        } catch (JsonProcessingException ex) {
            logger.error(ex.getMessage(), ex);
            callback.failure(GCMCallback.FailureCode.INTERNAL_ERROR.withCause(ex));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            callback.failure(GCMCallback.FailureCode.INTERNAL_ERROR.withCause(ex));
        } finally {
            if (null != request && !request.isAborted()) {
                request.abort();
            }
        }
        return callback;
    }

    private short getRetryDelay(HttpResponse response) {
        Header header = response.getFirstHeader(HEADER_NAME_RETRY_AFTER);
        short delay = 0;
        if (null != header) {
            try {
                delay = Short.valueOf(header.getValue());
            } catch (NumberFormatException e) {
//                    logger.warn(e.getMessage(),e);
            }
        }
        return delay;
    }

    private void parseResponse200(GCMRequest requestBody, GCMResponse data, GCMCallback callback) {
//      logger.info("Push with failures and/or canonical ids; handling result now");
        int index = 0;
        int totalSucess = 0;
        int totalFail = 0;
        Map<String, GCMResponse.ErrorCode> invalidIds = new HashMap<>();
        Map<String, String> canonicalIds = new HashMap<>();
        for (GCMResponse.Result result : data.getResults()) {
            if (result.isSuccess()) {
//              logger.info("request {} resulted in message id {}", index, result.getMessageId());
                totalSucess++;
            } else if (result.isFailure()) {
                switch (result.getError()) {
                    case InvalidRegistration:
                        String registrationId = requestBody.getRegistrationId(index);
//                      logger.debug("Invalid registration for registration id {}",registrationId);
                        invalidIds.put(registrationId, GCMResponse.ErrorCode.InvalidRegistration);
                        break;
                    case NotRegistered:
                        registrationId = requestBody.getRegistrationId(index);
//                      logger.debug("Not registered for registration id {}",registrationId);
                        invalidIds.put(registrationId, GCMResponse.ErrorCode.NotRegistered);
                        break;
                    case MissingRegistration:
                        logger.error("Missing registration!");
                        break;
                    case MismatchSenderId:
                        logger.error("Mismatch Sender Id!");
                        break;
                    case MessageTooBig:
                        logger.error("Message too big!");
                        break;
                    case InvalidDataKey:
                        logger.error("Invalid data key!");
                        break;
                    case InvalidTtl:
                        logger.error("Invalid time to live!");
                        break;
                    case Unavailable:
                    case InternalServerError:
                        logger.error("Unavailable / Internal Server Error - setting retry and reading retry-after header!");
                        break;
                }   // end switch
                totalFail++;
//              logger.info("Got failure result with error {} for request {}", result.getError(), index);
            } else if (result.isCanonicalId()) {
                String canonicalId = result.getRegistrationId();
                String registrationId = requestBody.getRegistrationId(index);
//              logger.info("Got canonical result with canonical id {} for registration id {}", canonicalId, registrationId);
                canonicalIds.put(registrationId, canonicalId);
                totalSucess++;
            }
            index++;
        } // end for loop
        if (0 < invalidIds.size()) {
            callback.failures(invalidIds);
        }
        if (0 < canonicalIds.size()) {
            callback.canonicals(canonicalIds);
        }
        callback.updateTotalSuccess(totalSucess);
        callback.updateTotalFail(totalFail);
    }

    public static class Builder {

        private final GCMService service;

        public Builder() {
            this.service = new GCMService();
        }

        public GCMService build() {
            this.service.postConstruct();
            return this.service;
        }

        public Builder setHttpClient(HttpClient httpClient) {
            this.service.httpClient = httpClient;
            return this;
        }

        public Builder setGcmSendUrl(String gcmSendUrl) {
            this.service.gcmSendUrl = gcmSendUrl;
            return this;
        }

    }

    private void postConstruct() {
        if (null == httpClient) {
            PoolingHttpClientConnectionManager poolConnectionManager = new PoolingHttpClientConnectionManager();
            poolConnectionManager.setMaxTotal(16);  // [TODO] maxTotal should be a configurable number

            List<HttpHost> proxies = new ArrayList<>();
            boolean isHttpProxy = ZConfig.Instance.getBoolean(Service.class, "pushnotification", "use_proxy", false);
            if (isHttpProxy) {
                String proxyHost = ZConfig.Instance.getString(Service.class, "pushnotification", "http_proxy_host", null);
                int proxyPort = ZConfig.Instance.getInt(Service.class, "pushnotification", "http_proxy_port", 0);
                if (!Utils.isNullOrEmpty(proxyHost) && proxyPort != 0) {
                    proxies.add(new HttpHost(proxyHost, proxyPort, "http"));
                }
                proxyHost = ZConfig.Instance.getString(Service.class, "pushnotification", "https_proxy_host", null);
                proxyPort = ZConfig.Instance.getInt(Service.class, "pushnotification", "https_proxy_port", 0);
                if (!Utils.isNullOrEmpty(proxyHost) && proxyPort != 0) {
                    proxies.add(new HttpHost(proxyHost, proxyPort, "https"));
                }
            }

            HttpClientBuilder builder = HttpClients.custom()
                    .setConnectionManager(poolConnectionManager);
            for (HttpHost proxy : proxies) {
                builder.setProxy(proxy);
            }
            httpClient = builder.build();
        }
        if (null == gcmSendUrl || gcmSendUrl.isEmpty()) {
            gcmSendUrl = DEFAULT_SEND_URL;
        }
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
    }

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String HEADER_NAME_AUTHORIZATION = "Authorization";
    private static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_NAME_RETRY_AFTER = "Retry-After";
    private static final String DEFAULT_SEND_URL = "https://android.googleapis.com/gcm/send";

    private HttpClient httpClient;
    private String gcmSendUrl;
    private ObjectMapper objectMapper;
}
