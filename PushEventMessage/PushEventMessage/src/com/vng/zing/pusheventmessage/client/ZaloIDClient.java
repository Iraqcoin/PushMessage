/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.client;

import com.vng.zing.common.ZCommonDef;
import com.vng.zing.common.ZErrorDef;
import com.vng.zing.common.ZErrorHelper;
import com.vng.zing.logger.ZLogger;
import com.vng.zing.thriftpool.TClientPool;
import com.vng.zing.thriftpool.ZClientPoolUtil;
import com.vng.zing.zaloidmw.thrift.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

/**
 *
 * @author namnq
 */
public class ZaloIDClient {

    private static final Class _ThisClass = ZaloIDClient.class;
    private static final Logger _Logger = ZLogger.getLogger(_ThisClass);
    private final String _name;
    private TClientPool.BizzConfig _bizzCfg;
    private ZaloIdMW.Client _aclient; //unused

    public ZaloIDClient(String name) {
        assert (name != null && !name.isEmpty());
        _name = name;
        _initialize();
    }

    private void _initialize() {
        ZClientPoolUtil.SetDefaultPoolProp(_ThisClass //clazzOfCfg
                , _name //instName
                , null //host
                , null //auth
                , ZCommonDef.TClientTimeoutMilisecsDefault //timeout
                , ZCommonDef.TClientNRetriesDefault //nretry
                , ZCommonDef.TClientMaxRdAtimeDefault //maxRdAtime
                , ZCommonDef.TClientMaxWrAtimeDefault //maxWrAtime
        );
        ZClientPoolUtil.GetListPools(_ThisClass, _name, new ZaloIdMW.Client.Factory()); //auto create pools
        _bizzCfg = ZClientPoolUtil.GetBizzCfg(_ThisClass, _name);
    }

    private TClientPool<ZaloIdMW.Client> getClientPool() {
        return (TClientPool<ZaloIdMW.Client>) ZClientPoolUtil.GetPool(_ThisClass, _name);
    }

    private TClientPool.BizzConfig getBizzCfg() {
        return _bizzCfg;
    }
    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///error objects
    ///
    ///e1001: NO_CONNECTION
    ///e1002: BAD_CONNECTION
    ///e1003: BAD_REQUEST
    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /// util functions
    ///

    private static void Log(Priority level, Throwable t) {
        _Logger.log(level, null, t);
    }

    private static void Log(Priority level, Throwable t, int retry) {
        if (retry > 0) {
            String message = "Request's still failed at retry " + retry;
            _Logger.log(level, message, t);
        } else {
            _Logger.log(level, null, t);
        }
    }

    public static List<List<Integer/*
			 * KType
             */>> SplitListKeys(List<Integer/*
					 * KType
                     */> listKeys, int mkeysAtime) {
        if (listKeys == null) {
            return null;
        }
        //correct mkeysAtime
        if (mkeysAtime < 1) {
            mkeysAtime = 1;
        }
        List<List<Integer/*
				 * KType
                 */>> ret = new LinkedList<List<Integer/*
				 * KType
                 */>>();
        int size = listKeys.size();
        if (size <= mkeysAtime) {
            ret.add(listKeys);
            return ret;
        }
        int nsubList = size / mkeysAtime;
        for (int i = 0; i < nsubList; ++i) {
            int startIndex = i * mkeysAtime;
            ret.add(listKeys.subList(startIndex, startIndex + mkeysAtime));
        }
        int lastListSz = size % mkeysAtime;
        if (lastListSz > 0) {
            ret.add(listKeys.subList(size - lastListSz, size));
        }
        return ret;
    }

    public static int MergeError(int out, int error) {
        if (ZErrorHelper.isFail(error)) {
            out = error;
        }
        return out;
    }

    public static Map<Integer/*
			 * KType
             */, String/*
			 * VType
             */> MergeDataMap(Map<Integer/*
					 * KType
                     */, String/*
					 * VType
                     */> out, Map<Integer/*
					 * KType
                     */, String/*
					 * VType
                     */> dataMap) {
        if (dataMap != null && !dataMap.isEmpty()) {
            if (out == null) {
                out = new HashMap<Integer/*
						 * KType
                         */, String/*
						 * VType
                         */>();
            }
            out.putAll(dataMap);
        }
        return out;
    }

    public static Map<Integer/*
			 * KType
             */, Integer> MergeErrorMap(Map<Integer/*
					 * KType
                     */, Integer> out, Map<Integer/*
					 * KType
                     */, Integer> errorMap) {
        if (errorMap != null && !errorMap.isEmpty()) {
            if (out == null) {
                out = new HashMap<Integer/*
						 * KType
                         */, Integer>();
            }
            out.putAll(errorMap);
        }
        return out;
    }
    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /// error objects: spec ZPRawApiPageCommunication
    ///
    ///e1001: NO_CONNECTION
    //..
    public static final TZProfileResult TZProfileResult_NO_CONNECTION = new TZProfileResult(ZErrorDef.NO_CONNECTION);
    public static final TMapCompactZProfile TMapCompactZProfile_NO_CONNECTION = new TMapCompactZProfile(ZErrorDef.NO_CONNECTION);
    public static final TTemplateHtmlResult TTemplateHtmlResult_NO_CONNECTION = new TTemplateHtmlResult(ZErrorDef.NO_CONNECTION);
    public static final TAvatarResult TAvatarResult_NO_CONNECTION = new TAvatarResult(ZErrorDef.NO_CONNECTION);
    ///e1002: BAD_CONNECTION
    //..
    public static final TZProfileResult TZProfileResult_BAD_CONNECTION = new TZProfileResult(ZErrorDef.BAD_CONNECTION);
    public static final TMapCompactZProfile TMapCompactZProfile_BAD_CONNECTION = new TMapCompactZProfile(ZErrorDef.BAD_CONNECTION);
    public static final TTemplateHtmlResult TTemplateHtmlResult_BAD_CONNECTION = new TTemplateHtmlResult(ZErrorDef.BAD_CONNECTION);
    public static final TAvatarResult TAvatarResult_BAD_CONNECTION = new TAvatarResult(ZErrorDef.BAD_CONNECTION);
    ///e1003: BAD_REQUEST
    //..
    public static final TZProfileResult TZProfileResult_BAD_REQUEST = new TZProfileResult(ZErrorDef.BAD_REQUEST);
    public static final TMapCompactZProfile TMapCompactZProfile_BAD_REQUEST = new TMapCompactZProfile(ZErrorDef.BAD_REQUEST);
    public static final TTemplateHtmlResult TTemplateHtmlResult_BAD_REQUEST = new TTemplateHtmlResult(ZErrorDef.BAD_REQUEST);
    public static final TAvatarResult TAvatarResult_BAD_REQUEST = new TAvatarResult(ZErrorDef.BAD_REQUEST);
    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /// util functions: spec ZPRawApiPageCommunication
    ///
    //..
    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///common methods
    ///

    public int ping() {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        ZaloIdMW.Client cli = pool.borrowClient();
        if (cli == null) {
            return ZErrorDef.NO_CONNECTION;
        }
        try {
            cli.ping();
            pool.returnClient(cli);
            return ZErrorDef.SUCCESS;
        } catch (TTransportException ex) {
            pool.invalidateClient(cli, ex);
            Log(Priority.ERROR, ex);
            return ZErrorDef.BAD_CONNECTION;
        } catch (TException ex) {
            pool.invalidateClient(cli, ex);
            Log(Priority.ERROR, ex);
            return ZErrorDef.BAD_REQUEST;
        }
    }

    public TTemplateHtmlResult getLoginTemplateHtml(String tmplId) {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
            ZaloIdMW.Client cli = pool.borrowClient();
            if (cli == null) {
                return TTemplateHtmlResult_NO_CONNECTION;
            }
            try {
                TTemplateHtmlResult ret = cli.getLoginTemplateHtml(tmplId);
                pool.returnClient(cli);
                return ret;
            } catch (TTransportException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                continue;
            } catch (TException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TTemplateHtmlResult_BAD_REQUEST;
            } catch (Exception ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TTemplateHtmlResult_BAD_REQUEST;
            }
        }
        return TTemplateHtmlResult_BAD_CONNECTION;
    }

    public TZProfileResult getZProfile(Integer uid) {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
            ZaloIdMW.Client cli = pool.borrowClient();
            if (cli == null) {
                return TZProfileResult_NO_CONNECTION;
            }
            try {
                TZProfileResult ret = cli.getZProfile(uid);
                pool.returnClient(cli);
                return ret;
            } catch (TTransportException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                continue;
            } catch (TException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TZProfileResult_BAD_REQUEST;
            } catch (Exception ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TZProfileResult_BAD_REQUEST;
            }
        }
        return TZProfileResult_BAD_CONNECTION;
    }

    public TMapCompactZProfile multiGetCompactZProfile(List<Integer> uids) {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
            ZaloIdMW.Client cli = pool.borrowClient();
            if (cli == null) {
                return TMapCompactZProfile_NO_CONNECTION;
            }
            try {
                TMapCompactZProfile ret = cli.multiGetCompactZProfile(uids);
                pool.returnClient(cli);
                return ret;
            } catch (TTransportException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                continue;
            } catch (TException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TMapCompactZProfile_BAD_REQUEST;
            } catch (Exception ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TMapCompactZProfile_BAD_REQUEST;
            }
        }
        return TMapCompactZProfile_BAD_CONNECTION;
    }

    public TZProfileResult getZProfileBySessionId(String session) {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
            ZaloIdMW.Client cli = pool.borrowClient();
            if (cli == null) {
                return TZProfileResult_NO_CONNECTION;
            }
            try {
                TZProfileResult ret = cli.getZProfileBySessionId(session);
                pool.returnClient(cli);
                return ret;
            } catch (TTransportException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                continue;
            } catch (TException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TZProfileResult_BAD_REQUEST;
            } catch (Exception ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TZProfileResult_BAD_REQUEST;
            }
        }
        return TZProfileResult_BAD_CONNECTION;
    }

    public TZProfileResult getZProfileByUsername(String uname) {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
            ZaloIdMW.Client cli = pool.borrowClient();
            if (cli == null) {
                return TZProfileResult_NO_CONNECTION;
            }
            try {
                TZProfileResult ret = cli.getZProfileByUsername(uname);
                pool.returnClient(cli);
                return ret;
            } catch (TTransportException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                continue;
            } catch (TException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TZProfileResult_BAD_REQUEST;
            } catch (Exception ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TZProfileResult_BAD_REQUEST;
            }
        }
        return TZProfileResult_BAD_CONNECTION;
    }

    public TZProfileResult getZProfileByPhone(long phone) {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
            ZaloIdMW.Client cli = pool.borrowClient();
            if (cli == null) {
                return TZProfileResult_NO_CONNECTION;
            }
            try {
                TZProfileResult ret = cli.getZProfileByPhone(phone);
                pool.returnClient(cli);
                return ret;
            } catch (TTransportException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                continue;
            } catch (TException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TZProfileResult_BAD_REQUEST;
            } catch (Exception ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TZProfileResult_BAD_REQUEST;
            }
        }
        return TZProfileResult_BAD_CONNECTION;
    }

    public int existZProfile(int uid) {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
            ZaloIdMW.Client cli = pool.borrowClient();
            if (cli == null) {
                return ZErrorDef.NO_CONNECTION;
            }
            try {
                int ret = cli.existZProfile(uid);
                pool.returnClient(cli);
                return ret;
            } catch (TTransportException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                continue;
            } catch (TException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return ZErrorDef.BAD_REQUEST;
            } catch (Exception ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return ZErrorDef.BAD_REQUEST;
            }
        }
        return ZErrorDef.BAD_CONNECTION;
    }

    public TAvatarResult getAvatarUrl(Integer uid, Integer size) {
        TClientPool<ZaloIdMW.Client> pool = getClientPool();
        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
            ZaloIdMW.Client cli = pool.borrowClient();
            if (cli == null) {
                return TAvatarResult_NO_CONNECTION;
            }
            try {
                TAvatarResult ret = cli.getAvatarUrl(uid, size);
                pool.returnClient(cli);
                return ret;
            } catch (TTransportException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                continue;
            } catch (TException ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TAvatarResult_BAD_REQUEST;
            } catch (Exception ex) {
                pool.invalidateClient(cli, ex);
                Log(Priority.ERROR, ex, retry);
                return TAvatarResult_BAD_REQUEST;
            }
        }
        return TAvatarResult_BAD_CONNECTION;
    }
    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    ///spec for ZPRawApiPageCommunication
    ///
    //..
}
