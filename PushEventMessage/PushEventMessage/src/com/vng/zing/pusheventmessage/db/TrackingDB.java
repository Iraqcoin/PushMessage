/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.db;

import com.vng.zing.pusheventmessage.client.BackendServiceException;
import com.vng.zing.pusheventmessage.common.MsgBuilder;
import com.vng.zing.pusheventmessage.common.Utils;
import com.vng.zing.pusheventmessage.thrift.EventMsg;
import com.vng.zing.pusheventmessage.thrift.PeriodType;
import com.vng.zing.strncounter64.thrift.DateTimeBitField;
import com.vng.zing.strncounter64.thrift.TListDataByDateResult;
import com.vng.zing.strncounter64.thrift.TPairIndexValue;
import com.vng.zing.strncounter64.thrift.TPairKeyDate;
import com.vng.zing.strncounter64.thrift.TPairKeyDateValueResult;
import com.vng.zing.strncounter64.thrift.wrapper.StrNCounter64Client;
import com.vng.zing.zcommon.thrift.ECode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author namnt3
 */
public class TrackingDB {

    private static final Logger LOGGER = Logger.getLogger(TrackingDB.class);

    private static final StrNCounter64Client CounterClient = new StrNCounter64Client("counterClient");

    private static final SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");

//    private static final HasPrefixKeyStringsMap LastViewTimeDb = HasPrefixKeyStringsMap.getInst(KeyPrefixes.LAST_VIEW_TIME_DB);
    public static void init() {

    }

    public static Long getCounter(String key, int days) throws BackendServiceException {
        if (days <= 0) {
            throw new IllegalArgumentException("days param must be greater than 0");
        }
        Calendar cal = Calendar.getInstance();
        List<TPairKeyDate> keys = new ArrayList<TPairKeyDate>(days);

        for (int i = 0; i < days; i++) {
            TPairKeyDate keyDate = new TPairKeyDate();
            keyDate.setKey(key);
            keyDate.setDate(StrNCounter64Client.buildTDate(cal, DateTimeBitField.DAY_IN_MONTH));

            keys.add(keyDate);
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        TListDataByDateResult result = CounterClient.multiGetByDate(keys);

        if (result.getError() < 0 && result.getError() != -ECode.NOT_EXIST.getValue() && result.getError() != -ECode.OVER_FLOW.getValue()) {
            throw new BackendServiceException(MsgBuilder.format("CounterClient.mmultiGetCache, key[??], days[??] responses error[??]", key, days, result.getError()));
        }

        Long counter = 0L;
        List<Long> values;
        for (TPairKeyDateValueResult t : result.getDataList()) {
            if (t.getValueResult().getError() >= 0) {
                values = t.getValueResult().getValue();
                if (values != null && values.size() > 0) {
                    counter += values.get(0);
                }
            }
        }
        return counter;
    }

    public static void increaseCounter(String key) throws BackendServiceException {
        long resultCode = CounterClient.increaseByDate(key, false, false, true, false, 1, 0, true);
        if (resultCode < 0) {
            throw new BackendServiceException(MsgBuilder.format("CounterClient.increaseByDate responses error[??]", resultCode));
        }
    }

    public static void increaseCounters(List<String> keys) throws BackendServiceException {
        LOGGER.info("INCREASE view " + keys);
        List<StrNCounter64Client.TupleKeyTimeIndexValues> list = new ArrayList<StrNCounter64Client.TupleKeyTimeIndexValues>();
        List<TPairIndexValue> value = new ArrayList<TPairIndexValue>();
        value.add(new TPairIndexValue(0, 1));
        for (String key : keys) {
            StrNCounter64Client.TupleKeyTimeIndexValues keyValue = new StrNCounter64Client.TupleKeyTimeIndexValues(key, false, false, true, false, value);
            list.add(keyValue);
        }
        long resultCode = CounterClient.multiCountsByDate(list, true);
        if (resultCode < 0) {
            throw new BackendServiceException(MsgBuilder.format("CounterClient.multiCountsByDate responses error[??]", resultCode));
        }
    }

    public static Map<String, Long> getCounters(Map<String, EventMsg> map) throws BackendServiceException {
        List<TPairKeyDate> keys = new ArrayList<TPairKeyDate>();
        for (Map.Entry<String, EventMsg> keyMsg : map.entrySet()) {
            EventMsg msg = keyMsg.getValue();
            Calendar cal = Calendar.getInstance();
            int days;
            if (msg.getPeriodType() == PeriodType.DAY) {
                days = 1;
            } else {
                long endDate = msg.getEndDate() < System.currentTimeMillis() ? msg.getEndDate(): System.currentTimeMillis();
                cal.setTimeInMillis(endDate);
                days = Utils.getDays(msg.getStartDate(), endDate);
            }
            LOGGER.info(MsgBuilder.format("getCounters counter[??] timer[??], days[??]", keyMsg.getKey(), cal.getTimeInMillis(), days));

            for (int i = 0; i < days; i++) {
                TPairKeyDate keyDate = new TPairKeyDate();
                keyDate.setKey(keyMsg.getKey());
                keyDate.setDate(StrNCounter64Client.buildTDate(cal, DateTimeBitField.DAY_IN_MONTH));
                LOGGER.warn(MsgBuilder.format("find date[??]", cal.getTime()));

                keys.add(keyDate);
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
        }
        TListDataByDateResult multiGetByDateResult = CounterClient.multiGetByDate(keys);

        if (multiGetByDateResult.getError() < 0) {
            if (multiGetByDateResult.getError() != -9) {
                LOGGER.warn(MsgBuilder.format("CounterClient.multiGetByDate responses error[??]", multiGetByDateResult.getError()));
            }
        }
        Map<String, Long> result = new ConcurrentHashMap<String, Long>();
        Iterator<TPairKeyDateValueResult> dataListIterator = multiGetByDateResult.getDataListIterator();
        while (dataListIterator.hasNext()) {
            TPairKeyDateValueResult next = dataListIterator.next();
            String key = next.getKeyDate().getKey();
            Long value = result.get(key);
            if (value == null) {
                value = 0L;
            }
            if (next.getValueResult().getError() < 0) {
                if (next.getValueResult().getError() != -9 && next.getValueResult().getError() != -ECode.OVER_FLOW.getValue()) {
                    LOGGER.warn(MsgBuilder.format("CounterClient.multiGetByDate responses error[??]", next.getValueResult().getError()));
                }
            } else {
                for (long count : next.getValueResult().getValue()) {
                    value += count;
                    LOGGER.info(next.getKeyDate().getDate() + " - " + count);
                }
            }
            result.put(key, value);
        }
        return result;
    }

    public static long getViewCount(long eventMsgId, String clientIdentifier, int days) throws BackendServiceException {
        String key = "v" + eventMsgId + "_" + clientIdentifier;
        return getCounter(key, days);
    }
//
//    public static void increaseViewCount(long eventMsgId, String clientIdentifier) throws BackendServiceException {
//        String key = "vc_" + eventMsgId + "_" + clientIdentifier;
//        increaseCounter(key);
//    }

}
