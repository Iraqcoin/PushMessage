package com.vng.zing.pusheventmessage.entity;


import com.vng.zing.exception.ExpiredException;
import com.vng.zing.jni.zcommonx.wrapper.ZCommonX;
import com.vng.zing.logger.ZLogger;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author anhlh
 */
public class SdkModel {

    private static final Logger _logger = ZLogger.getLogger(SdkModel.class);
    private static final SdkModel Instance = new SdkModel();
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    private static final Xeger xeger = new Xeger("[a-zA-Z0-9]{16}");
	public static final String HASH_KEY = "@zoauth!2$%^@13";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private SdkModel() {
    }

    public static SdkModel getInstance() {
        return Instance;
    }
    
    public String getEncodeSdkId(SdkInfo sdkInfo) {

        List<String> data = new ArrayList<String>();

        data.add(String.valueOf(sdkInfo.getSdkId()));
        data.add(String.valueOf(sdkInfo.getAppId()));
        data.add(sdkInfo.getSdkVersion());
        data.add(sdkInfo.getPlatform());
        data.add(sdkInfo.getOsVerrsion());
        data.add(sdkInfo.getModel());
        data.add(sdkInfo.getScreenSize());
        data.add(sdkInfo.getJsonStringMeta());

        String encodeData = ZCommonX.zma_encode(data, HASH_KEY, 0);
        return encodeData;
    }

    public SdkInfo validateSdkId(String encodeSdkId) {
        SdkInfo sdkInfo = null;
        try {
            String[] encodeData = ZCommonX.zma_decode(encodeSdkId, HASH_KEY, 0, 0);

            sdkInfo = new SdkInfo();
            int i = 0;

            sdkInfo.setSdkId(Long.valueOf(encodeData[i++]));
            sdkInfo.setAppId(Integer.valueOf(encodeData[i++]));
            sdkInfo.setSdkVersion(encodeData[i++]);
            sdkInfo.setPlatform(encodeData[i++]);
            sdkInfo.setOsVerrsion(encodeData[i++]);
            sdkInfo.setModel(encodeData[i++]);
            sdkInfo.setScreenSize(encodeData[i++]);
            sdkInfo.setJsonStringMeta(encodeData[i++]);
        } catch (ExpiredException ex) {
            _logger.error(ex.getMessage(), ex);
        }
        return sdkInfo;
    }

//    public String zenSdkPrivateKey() {
//        String result = xeger.generate();
//        return result;
//    }
}
