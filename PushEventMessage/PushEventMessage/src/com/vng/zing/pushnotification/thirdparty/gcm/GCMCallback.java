package com.vng.zing.pushnotification.thirdparty.gcm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ngandk2@vng.com.vn
 */
public class GCMCallback {
    
    public enum FailureCode {
            BAD_REQUEST,UNAUTHORIZED,SERVICE_UNAVAILABLE,INTERNAL_ERROR,UNKNOWN_ERROR;

            private FailureCode() {}

            private Throwable throwable;

            public boolean hasCause() {
                return null != throwable;
            }

            public Throwable getCause() {
                return throwable;
            }

            public FailureCode withCause(Throwable throwable) {
                this.throwable = throwable;
                return this;
            }
    }
    
    //***  Public method  ***\\
    public boolean isMarkedRetry(){
        return this.retry || this.retryTimeInSecond > 0;
    }
    
    public short retryTime(){
        return retryTimeInSecond;
    }
    
    public FailureCode getFailureCode() {
        return failureCode;
    }
    
    public List<String> getInvalidIds(){
        return new ArrayList<>(failures.keySet());
    }
    
    public Map<String, GCMResponse.ErrorCode> getFailures() {
        return failures;
    }

    public Map<String, String> getCanonicals() {
        return canonicals;
    }
    
    public int totalSuccess(){
        return this.totalPush;
    }
    
    public int totalFail(){
        return this.totalFails;
    }
    
    public int totalInput(){
        return this.totalInput;
    }
    
    public void setTotalInput(int totalInput){
        this.totalInput = totalInput;
    }
    //******* Methods in package scope ******\\
    void markedRetry(){
        this.retry = true;
    }
    
    public boolean isSuccess(){
        return success;
    }
    
    void successful(){
        this.success = true;
    };
    
    void setRetryTime(short timeInSecond){
        this.retryTimeInSecond = timeInSecond;
        this.retry = true;
    }

    void failure(FailureCode failureCode){
        this.failureCode = failureCode;
    };
    
    void failures(Map<String, GCMResponse.ErrorCode> failures){
        this.failures.putAll(failures);
    };
    
    void canonicals(Map<String,String> canonicals){
        this.canonicals.putAll(canonicals);
    };
    
    void updateTotalSuccess(int totalSucc){
        this.totalPush = totalSucc;
    }
    
    void updateTotalFail(int totalFail){
        this.totalFails = totalFail;
    }
    
    private boolean retry = false;
    private short retryTimeInSecond = 0;
    private boolean success = false;
    private FailureCode failureCode;
    private final Map<String,GCMResponse.ErrorCode> failures = new HashMap<>();
    private final Map<String, String> canonicals = new HashMap<>();
    private int totalInput;      // number of total input devices
    private int totalPush;      // number of total devices which are pushed success
    private int totalFails;     // number of total devices which are pushed fail. 
}
