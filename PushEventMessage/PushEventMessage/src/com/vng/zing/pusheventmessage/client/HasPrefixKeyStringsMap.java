/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.client;

import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.stats.Profiler;
import com.vng.zing.strliststr.thrift.FindMed;
import com.vng.zing.strliststr.thrift.PutEntryOption;
import com.vng.zing.strliststr.thrift.TValue;
import com.vng.zing.strliststr.thrift.TValueResult;
import com.vng.zing.strliststr.thrift.wrapper.StrListStrClient;
import com.vng.zing.zcommon.thrift.ECode;
import com.vng.zing.zcommon.thrift.PutPolicy;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author namnt3
 */
public class HasPrefixKeyStringsMap {

    private static final Logger LOGGER = Logger.getLogger(HasPrefixKeyStringsMap.class);

    public static final StrListStrClient StrListClient = new StrListStrClient("strListClient");
    private static final PutEntryOption DEFAULT_PUT_ENTRY_OPTION = new PutEntryOption();

    public static void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static final Map<KeyPrefixes, HasPrefixKeyStringsMap> instanceMap = new EnumMap<KeyPrefixes, HasPrefixKeyStringsMap>(KeyPrefixes.class);

    public static synchronized HasPrefixKeyStringsMap getInst(KeyPrefixes type) {
        HasPrefixKeyStringsMap inst = instanceMap.get(type);
        if (inst == null) {
            inst = new HasPrefixKeyStringsMap(type.getKeyPrefix());
            instanceMap.put(type, inst);
        }
        return inst;
    }

    private final String keyPrefix;

    private HasPrefixKeyStringsMap(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public List<String> getSlice(String key, int position, int count) throws BackendServiceException {
        key = keyPrefix + key;

        Profiler.getThreadProfiler().push(HasPrefixKeyStringsMap.class, "StrListClient.getSlice");
        TValueResult res = StrListClient.getSlice(key, position, count);
        Profiler.getThreadProfiler().pop(HasPrefixKeyStringsMap.class, "StrListClient.getSlice");

        if (res == null || res.getError() == -ECode.NOT_EXIST.getValue()) {
            return null;
        }

        if (res.getError() < 0) {
            throw new BackendServiceException(MsgBuilder.format("StrListClient.getSlice respones error[??] for key[??]", res.getError(), key));
        }
        return res.getValue().getEntries();
    }

    public List<String> get(String key) throws BackendServiceException {
        key = keyPrefix + key;

        Profiler.getThreadProfiler().push(HasPrefixKeyStringsMap.class, "StrListClient.get");
        TValueResult res = StrListClient.get(key);
        Profiler.getThreadProfiler().pop(HasPrefixKeyStringsMap.class, "StrListClient.get");

        if (res == null || res.getError() == -ECode.NOT_EXIST.getValue()) {
            return null;
        }

        if (res.getError() < 0) {
            throw new BackendServiceException(MsgBuilder.format("StrListClient.get respones error[??] for key[??]", res.getError(), key));
        }
        return res.getValue().getEntries();
    }

    public void putEntry(String key, String value) throws BackendServiceException {
        key = keyPrefix + key;
        Profiler.getThreadProfiler().push(HasPrefixKeyStringsMap.class, "StrListClient.putEntry");
        long resultCode = StrListClient.putEntry(key, value, DEFAULT_PUT_ENTRY_OPTION);
        Profiler.getThreadProfiler().pop(HasPrefixKeyStringsMap.class, "StrListClient.putEntry");
        if (resultCode < 0) {
            throw new BackendServiceException(MsgBuilder.format("StrListClient.putEntry respones error[??] for key[??], value[??]", resultCode, key, value));
        }
    }

    public void put(String key, List<String> values) throws BackendServiceException {
        key = keyPrefix + key;
        Profiler.getThreadProfiler().push(HasPrefixKeyStringsMap.class, "StrListClient.put");
        com.vng.zing.strliststr.thrift.TValue tvalue = new TValue();
        tvalue.setEntries(values);
        long resultCode = StrListClient.put(key, tvalue, PutPolicy.ADD_OR_UDP);;
        Profiler.getThreadProfiler().pop(HasPrefixKeyStringsMap.class, "StrListClient.put");
        
        if (resultCode < 0) {
            throw new BackendServiceException(MsgBuilder.format("StrListClient.put respones error[??] for key[??], value[??]", resultCode, key, values));
        }
    }

    public void removeEntry(String key, String value) throws BackendServiceException {
        key = keyPrefix + key;

        Profiler.getThreadProfiler().push(HasPrefixKeyStringsMap.class, "StrListClient.removeEntry");
        long resultCode = StrListClient.removeEntry(key, value, FindMed.SEQ_BEG);
        Profiler.getThreadProfiler().pop(HasPrefixKeyStringsMap.class, "StrListClient.removeEntry");

        if (resultCode < 0) {
            throw new BackendServiceException(MsgBuilder.format("StrListClient.putEntry respones error[??] for key[??], value[??]", resultCode, key, value));
        }
    }
}
