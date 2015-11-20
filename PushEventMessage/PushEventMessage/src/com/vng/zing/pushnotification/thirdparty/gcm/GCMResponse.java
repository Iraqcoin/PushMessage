package com.vng.zing.pushnotification.thirdparty.gcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class GCMResponse {
    public static enum ErrorCode {
        MissingRegistration,
        InvalidRegistration,
        MismatchSenderId,
        NotRegistered,
        MessageTooBig,
        InvalidDataKey,
        InvalidTtl,
        Unavailable,
        InternalServerError,
        InvalidPackageName;
    }
    
    @JsonProperty(
            value = "multicast_id")
    protected long multicastId;

    @JsonProperty(
            value = "success")
    protected short success;

    @JsonProperty(
            value = "failure")
    protected short failure;

    @JsonProperty(
            value = "canonical_ids")
    protected short canonicalIds;
    
    @JsonProperty(
            value = "results")
    protected List<Result> results;
    
    public long getMulticastId() {
        return multicastId;
    }

    public short getSuccess() {
        return success;
    }

    public short getFailure() {
        return failure;
    }

    public short getCanonicalIds() {
        return canonicalIds;
    }

    public List<Result> getResults() {
        return Collections.unmodifiableList(results);
    }
    
    public static class Result {
        
        @JsonProperty(
                value = "message_id")
        protected String messageId;

        @JsonProperty(
                value = "registration_id")
        protected String registrationId;

        @JsonProperty(
                value = "error")
        protected ErrorCode error;
        
        public String getMessageId() {
            return messageId;
        }

        public String getRegistrationId() {
            return registrationId;
        }

        public ErrorCode getError() {
            return error;
        }

        public boolean isFailure() {
            return null == messageId;
        }

        public boolean isCanonicalId() {
            return null != messageId && null != registrationId;
        }

        public boolean isSuccess() {
            return null != messageId && null == registrationId;
        }
    }
}
