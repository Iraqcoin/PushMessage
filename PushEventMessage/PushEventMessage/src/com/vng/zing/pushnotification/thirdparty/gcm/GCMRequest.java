package com.vng.zing.pushnotification.thirdparty.gcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class GCMRequest {
    
    protected String senderId;
    
    @JsonProperty(value = "registration_ids")
    protected List<String> registrationIds = new ArrayList<>();

    @JsonProperty(
        value = "collapse_key")
    protected String collapseKey;

    @JsonProperty(
            value = "data")
    protected Map<String,String> data = new HashMap<>();

    @JsonProperty(
            value = "delay_while_idle")
    protected boolean delayWhileIdle = Boolean.FALSE;

    @JsonProperty(
            value = "time_to_live")
    protected long timeToLive = 4l * 7l * 24l * 60l * 60l; // 4 weeks

    @JsonProperty(
            value = "category")
    protected String category;

    @JsonProperty(
            value = "dry_run")
    protected boolean dryRun = Boolean.FALSE;

    public String getRegistrationId(int index) {
        if (index < registrationIds.size()) {
            return registrationIds.get(index);
        }
        throw new IllegalArgumentException("'index' is out of bounds!");
    }

    private GCMRequest(){}

    public static class Builder {

        GCMRequest request;

        public GCMRequest build(){
            return request;
        }

        public Builder(String senderId){
            this.request = new GCMRequest();
            this.request.senderId = senderId;
        }

        //***************
        // Builder method
        //***************
        public Builder withRecipientId(String recipientId) {
            if(!request.registrationIds.contains(recipientId)){
                request.registrationIds.add(recipientId);
            }
            return this;
        }
        
        public Builder withListRecipientId(List<String> recipientIdList) {
            for(String recipientId : recipientIdList){
                if(!request.registrationIds.contains(recipientId)){
                    request.registrationIds.add(recipientId);
                }
            }
            return this;
        }

        public Builder withCollapseKey(String collapseKey) {
            request.collapseKey = collapseKey;
            return this;
        }

        public Builder withDataField(String key, String value){
            request.data.put(key, value);
            return this;
        }

        public Builder withData(Map<String,String> data){
            request.data.clear();
            request.data.putAll(data);
            return this;
        }

        public Builder withDelayWhileIdle(){
            request.delayWhileIdle = true;
            return this;
        }

        public Builder withoutDelayWhileIdle(){
            request.delayWhileIdle = false;
            return this;
        }

        public Builder withTimeToLive(long timeToLive) {
            request.timeToLive = timeToLive;
            return this;
        }

        public Builder withDryRun(){
            request.dryRun = true;
            return this;
        }

        public Builder withoutDryRun(){
            request.dryRun = false;
            return this;
        }

    } // end Builder inner class
}
