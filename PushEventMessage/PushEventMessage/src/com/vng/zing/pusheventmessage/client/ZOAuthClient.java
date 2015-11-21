//package com.vng.zing.pusheventmessage.client;
//
//import com.vng.zing.common.ZCommonDef;
//import com.vng.zing.common.ZErrorDef;
//import com.vng.zing.logger.ZLogger;
//import com.vng.zing.thriftpool.TClientPool;
//import com.vng.zing.thriftpool.ZClientPoolUtil;
//import com.vng.zing.zalooauthmw.thrift.*;
//import com.vng.zing.zocommon.thrift.ZOAccessTokEmbed;
//import java.util.List;
//import org.apache.log4j.Logger;
//import org.apache.log4j.Priority;
//import org.apache.thrift.TException;
//import org.apache.thrift.transport.TTransportException;
//
///**
// *
// * @author nghiatc
// */
//public class ZOAuthClient {
//
//    private static final Class _ThisClass = ZOAuthClient.class;
//    private static final Logger _Logger = ZLogger.getLogger(_ThisClass);
//    private final String _name;
//    private TClientPool.BizzConfig _bizzCfg;
//    private ZOAService.Client _aclient; //unused
//
//    public ZOAuthClient(String name) {
//        assert (name != null && !name.isEmpty());
//        _name = name;
//        _initialize();
//
//    }
//
//    private void _initialize() {
//        ZClientPoolUtil.SetDefaultPoolProp(_ThisClass //clazzOfCfg
//                , _name //instName
//                , null //host
//                , null //auth
//                , ZCommonDef.TClientTimeoutMilisecsDefault //timeout
//                , ZCommonDef.TClientNRetriesDefault //nretry
//                , ZCommonDef.TClientMaxRdAtimeDefault //maxRdAtime
//                , ZCommonDef.TClientMaxWrAtimeDefault //maxWrAtime
//        );
//        ZClientPoolUtil.GetListPools(_ThisClass, _name, new ZOAService.Client.Factory()); //auto create pools
//        _bizzCfg = ZClientPoolUtil.GetBizzCfg(_ThisClass, _name);
//    }
//
//    private TClientPool<ZOAService.Client> getClientPool() {
//        return (TClientPool<ZOAService.Client>) ZClientPoolUtil.GetPool(_ThisClass, _name);
//    }
//
//    private TClientPool.BizzConfig getBizzCfg() {
//        return _bizzCfg;
//    }
//	///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    ///error objects
//    ///
//    ///e1001: NO_CONNECTION
//    public static final TCreateAppResult TCreateAppResult_NO_CONNECTION = new TCreateAppResult(ZErrorDef.NO_CONNECTION);
//    public static final TApiQuotaResult TApiQuotaResult_NO_CONNECTION = new TApiQuotaResult(ZErrorDef.NO_CONNECTION);
//    public static final TListAPIResult TListAPIResult_NO_CONNECTION = new TListAPIResult(ZErrorDef.NO_CONNECTION);
//    public static final TListAppIDResult TListAppIDResult_NO_CONNECTION = new TListAppIDResult(ZErrorDef.NO_CONNECTION);
//    public static final TTotalAppIDResult TTotalAppIDResult_NO_CONNECTION = new TTotalAppIDResult(ZErrorDef.NO_CONNECTION);
//    public static final TAccessTokEmbedResult TAccessTokEmbedResult_NO_CONNECTION = new TAccessTokEmbedResult(ZErrorDef.NO_CONNECTION);
//    public static final TAppInfoResult TAppInfoResult_NO_CONNECTION = new TAppInfoResult(ZErrorDef.NO_CONNECTION);
//    public static final TListUserAppResult TListUserAppResult_NO_CONNECTION = new TListUserAppResult(ZErrorDef.NO_CONNECTION);
//    public static final TAppSecretKeyResult TAppSecretKeyResult_NO_CONNECTION = new TAppSecretKeyResult(ZErrorDef.NO_CONNECTION);
//    public static final TPaymentSecretKeyResult TPaymentSecretKeyResult_NO_CONNECTION = new TPaymentSecretKeyResult(ZErrorDef.NO_CONNECTION);
//    public static final TPaymentPrivKeyResult TPaymentPrivKeyResult_NO_CONNECTION = new TPaymentPrivKeyResult(ZErrorDef.NO_CONNECTION);
//    public static final TSMSQuotaResult TSMSQuotaResult_NO_CONNECTION = new TSMSQuotaResult(ZErrorDef.NO_CONNECTION);
//    public static final TCheckPageLinkResult TCheckPageLinkResult_NO_CONNECTION = new TCheckPageLinkResult(ZErrorDef.NO_CONNECTION);
//    public static final TMapAppInfoResult TMapAppInfoResult_NO_CONNECTION = new TMapAppInfoResult(ZErrorDef.NO_CONNECTION);
//    public static final TPaymentStatusResult TPaymentStatusResult_NO_CONNECTION = new TPaymentStatusResult(ZErrorDef.NO_CONNECTION);
//    public static final TCreatePublisherResult TCreatePublisherResult_NO_CONNECTION = new TCreatePublisherResult(ZErrorDef.NO_CONNECTION);
//    public static final TKeyEncodedIDResult TKeyEncodedIDResult_NO_CONNECTION = new TKeyEncodedIDResult(ZErrorDef.NO_CONNECTION);
//    public static final TPublisherInfoResult TPublisherInfoResult_NO_CONNECTION = new TPublisherInfoResult(ZErrorDef.NO_CONNECTION);
//    public static final TMapPublisherInfoResult TMapPublisherInfoResult_NO_CONNECTION = new TMapPublisherInfoResult(ZErrorDef.NO_CONNECTION);
//
//    ///e1002: BAD_CONNECTION
//    public static final TCreateAppResult TCreateAppResult_BAD_CONNECTION = new TCreateAppResult(ZErrorDef.BAD_CONNECTION);
//    public static final TApiQuotaResult TApiQuotaResult_BAD_CONNECTION = new TApiQuotaResult(ZErrorDef.BAD_CONNECTION);
//    public static final TListAPIResult TListAPIResult_BAD_CONNECTION = new TListAPIResult(ZErrorDef.BAD_CONNECTION);
//    public static final TListAppIDResult TListAppIDResult_BAD_CONNECTION = new TListAppIDResult(ZErrorDef.BAD_CONNECTION);
//    public static final TTotalAppIDResult TTotalAppIDResult_BAD_CONNECTION = new TTotalAppIDResult(ZErrorDef.BAD_CONNECTION);
//    public static final TAccessTokEmbedResult TAccessTokEmbedResult_BAD_CONNECTION = new TAccessTokEmbedResult(ZErrorDef.BAD_CONNECTION);
//    public static final TAppInfoResult TAppInfoResult_BAD_CONNECTION = new TAppInfoResult(ZErrorDef.BAD_CONNECTION);
//    public static final TListUserAppResult TListUserAppResult_BAD_CONNECTION = new TListUserAppResult(ZErrorDef.BAD_CONNECTION);
//    public static final TAppSecretKeyResult TAppSecretKeyResult_BAD_CONNECTION = new TAppSecretKeyResult(ZErrorDef.BAD_CONNECTION);
//    public static final TPaymentSecretKeyResult TPaymentSecretKeyResult_BAD_CONNECTION = new TPaymentSecretKeyResult(ZErrorDef.BAD_CONNECTION);
//    public static final TPaymentPrivKeyResult TPaymentPrivKeyResult_BAD_CONNECTION = new TPaymentPrivKeyResult(ZErrorDef.BAD_CONNECTION);
//    public static final TSMSQuotaResult TSMSQuotaResult_BAD_CONNECTION = new TSMSQuotaResult(ZErrorDef.BAD_CONNECTION);
//    public static final TCheckPageLinkResult TCheckPageLinkResult_BAD_CONNECTION = new TCheckPageLinkResult(ZErrorDef.BAD_CONNECTION);
//    public static final TMapAppInfoResult TMapAppInfoResult_BAD_CONNECTION = new TMapAppInfoResult(ZErrorDef.BAD_CONNECTION);
//    public static final TPaymentStatusResult TPaymentStatusResult_BAD_CONNECTION = new TPaymentStatusResult(ZErrorDef.BAD_CONNECTION);
//    public static final TCreatePublisherResult TCreatePublisherResult_BAD_CONNECTION = new TCreatePublisherResult(ZErrorDef.BAD_CONNECTION);
//    public static final TKeyEncodedIDResult TKeyEncodedIDResult_BAD_CONNECTION = new TKeyEncodedIDResult(ZErrorDef.BAD_CONNECTION);
//    public static final TPublisherInfoResult TPublisherInfoResult_BAD_CONNECTION = new TPublisherInfoResult(ZErrorDef.BAD_CONNECTION);
//    public static final TMapPublisherInfoResult TMapPublisherInfoResult_BAD_CONNECTION = new TMapPublisherInfoResult(ZErrorDef.BAD_CONNECTION);
//    ///e1003: BAD_REQUEST
//    public static final TCreateAppResult TCreateAppResult_BAD_REQUEST = new TCreateAppResult(ZErrorDef.BAD_REQUEST);
//    public static final TApiQuotaResult TApiQuotaResult_BAD_REQUEST = new TApiQuotaResult(ZErrorDef.BAD_REQUEST);
//    public static final TListAPIResult TListAPIResult_BAD_REQUEST = new TListAPIResult(ZErrorDef.BAD_REQUEST);
//    public static final TListAppIDResult TListAppIDResult_BAD_REQUEST = new TListAppIDResult(ZErrorDef.BAD_REQUEST);
//    public static final TTotalAppIDResult TTotalAppIDResult_BAD_REQUEST = new TTotalAppIDResult(ZErrorDef.BAD_REQUEST);
//    public static final TAccessTokEmbedResult TAccessTokEmbedResult_BAD_REQUEST = new TAccessTokEmbedResult(ZErrorDef.BAD_REQUEST);
//    public static final TAppInfoResult TAppInfoResult_BAD_REQUEST = new TAppInfoResult(ZErrorDef.BAD_REQUEST);
//    public static final TListUserAppResult TListUserAppResult_BAD_REQUEST = new TListUserAppResult(ZErrorDef.BAD_REQUEST);
//    public static final TAppSecretKeyResult TAppSecretKeyResult_BAD_REQUEST = new TAppSecretKeyResult(ZErrorDef.BAD_REQUEST);
//    public static final TPaymentSecretKeyResult TPaymentSecretKeyResult_BAD_REQUEST = new TPaymentSecretKeyResult(ZErrorDef.BAD_REQUEST);
//    public static final TPaymentPrivKeyResult TPaymentPrivKeyResult_BAD_REQUEST = new TPaymentPrivKeyResult(ZErrorDef.BAD_REQUEST);
//    public static final TSMSQuotaResult TSMSQuotaResult_BAD_REQUEST = new TSMSQuotaResult(ZErrorDef.BAD_REQUEST);
//    public static final TCheckPageLinkResult TCheckPageLinkResult_BAD_REQUEST = new TCheckPageLinkResult(ZErrorDef.BAD_REQUEST);
//    public static final TMapAppInfoResult TMapAppInfoResult_BAD_REQUEST = new TMapAppInfoResult(ZErrorDef.BAD_REQUEST);
//    public static final TPaymentStatusResult TPaymentStatusResult_BAD_REQUEST = new TPaymentStatusResult(ZErrorDef.BAD_REQUEST);
//    public static final TCreatePublisherResult TCreatePublisherResult_BAD_REQUEST = new TCreatePublisherResult(ZErrorDef.BAD_REQUEST);
//    public static final TKeyEncodedIDResult TKeyEncodedIDResult_BAD_REQUEST = new TKeyEncodedIDResult(ZErrorDef.BAD_REQUEST);
//    public static final TPublisherInfoResult TPublisherInfoResult_BAD_REQUEST = new TPublisherInfoResult(ZErrorDef.BAD_REQUEST);
//    public static final TMapPublisherInfoResult TMapPublisherInfoResult_BAD_REQUEST = new TMapPublisherInfoResult(ZErrorDef.BAD_REQUEST);
//
//	///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    /// util functions
//    ///
//    private static void Log(Priority level, Throwable t) {
//        _Logger.log(level, null, t);
//    }
//
//    private static void Log(Priority level, Throwable t, int retry) {
//        if (retry > 0) {
//            String message = "Request's still failed at retry " + retry;
//            _Logger.log(level, message, t);
//        } else {
//            _Logger.log(level, null, t);
//        }
//    }
//	///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    /// error objects: spec ZOAuthClient
//    ///
//    ///e1001: NO_CONNECTION
//    //..
//    ///e1002: BAD_CONNECTION
//    //..
//    ///e1003: BAD_REQUEST
//    //..
//    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    /// util functions: spec ZOAuthClient
//    ///
//    //..
//    ///~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//    ///common methods
//    ///
//
//    public int ping() {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        ZOAService.Client cli = pool.borrowClient();
//        if (cli == null) {
//            return ZErrorDef.NO_CONNECTION;
//        }
//        try {
//            cli.ping();
//            pool.returnClient(cli);
//            return ZErrorDef.SUCCESS;
//        } catch (TTransportException ex) {
//            pool.invalidateClient(cli, ex);
//            Log(Priority.ERROR, ex);
//            return ZErrorDef.BAD_CONNECTION;
//        } catch (TException ex) {
//            pool.invalidateClient(cli, ex);
//            Log(Priority.ERROR, ex);
//            return ZErrorDef.BAD_REQUEST;
//        }
//    }
//
//    public int verifyAccessTok(ZOAccessTokEmbed zote) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int ret = cli.verifyAccessTok(zote);
//                pool.returnClient(cli);
//                return ret;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int getUserAppPerm(int userId, int appId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                TPermissionResult result = cli.getUserAppPerm(userId, appId);
//                int ret = -1;
//                if (result != null && (result.error == 0 || result.error == -9) && result.isSetPermission()) {
//                    ret = result.getPermission();
//                }
//                pool.returnClient(cli);
//                return ret;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_addAppApi(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_addAppApi(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int hasAppApi(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.hasAppApi(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_removeAppApi(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_removeAppApi(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_setAppApiQuota(int appId, int apiId, TApiQuota taq) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_setAppApiQuota(appId, apiId, taq);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_setAppApiQuotaNotify(int appId, int apiId, TApiQuota taq) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_setAppApiQuotaNotify(appId, apiId, taq);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_setAppUserApiQuota(int appId, int apiId, TApiQuota taq) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_setAppUserApiQuota(appId, apiId, taq);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_setAppUserApiReceiveQuota(int appId, int apiId, TApiQuota taq) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_setAppUserApiReceiveQuota(appId, apiId, taq);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_setAppUserApiQuotaNotify(int appId, int apiId, TApiQuota taq) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_setAppUserApiQuotaNotify(appId, apiId, taq);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_setAppApiLatestQuota(int appId, int apiId, TApiQuota taq) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_setAppApiLatestQuota(appId, apiId, taq);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public TApiQuotaResult getAppApiQuota(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TApiQuotaResult_NO_CONNECTION;
//            }
//            try {
//                TApiQuotaResult result = cli.getAppApiQuota(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            }
//        }
//        return TApiQuotaResult_BAD_CONNECTION;
//    }
//
//    public TApiQuotaResult getAppApiQuotaNotify(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TApiQuotaResult_NO_CONNECTION;
//            }
//            try {
//                TApiQuotaResult result = cli.getAppApiQuotaNotify(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            }
//        }
//        return TApiQuotaResult_BAD_CONNECTION;
//    }
//
//    public TApiQuotaResult getAppUserApiQuota(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TApiQuotaResult_NO_CONNECTION;
//            }
//            try {
//                TApiQuotaResult result = cli.getAppUserApiQuota(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            }
//        }
//        return TApiQuotaResult_BAD_CONNECTION;
//    }
//
//    public TApiQuotaResult getAppUserApiReceiveQuota(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TApiQuotaResult_NO_CONNECTION;
//            }
//            try {
//                TApiQuotaResult result = cli.getAppUserApiReceiveQuota(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            }
//        }
//        return TApiQuotaResult_BAD_CONNECTION;
//    }
//
//    public TApiQuotaResult getAppUserApiQuotaNotify(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TApiQuotaResult_NO_CONNECTION;
//            }
//            try {
//                TApiQuotaResult result = cli.getAppUserApiQuotaNotify(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            }
//        }
//        return TApiQuotaResult_BAD_CONNECTION;
//    }
//
//    public TApiQuotaResult getAppApiLatestQuota(int appId, int apiId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TApiQuotaResult_NO_CONNECTION;
//            }
//            try {
//                TApiQuotaResult result = cli.getAppApiLatestQuota(appId, apiId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TApiQuotaResult_BAD_REQUEST;
//            }
//        }
//        return TApiQuotaResult_BAD_CONNECTION;
//    }
//
//    public TListAPIResult getListApi(int appId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TListAPIResult_NO_CONNECTION;
//            }
//            try {
//                TListAPIResult result = cli.getListAppApi(appId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TListAPIResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TListAPIResult_BAD_REQUEST;
//            }
//        }
//        return TListAPIResult_BAD_CONNECTION;
//    }
//
//    public TAccessTokEmbedResult getAccessTok(int appId, String outhCode) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TAccessTokEmbedResult_NO_CONNECTION;
//            }
//            try {
//                TAccessTokEmbedResult result = cli.getAccessTok(appId, outhCode);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TAccessTokEmbedResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TAccessTokEmbedResult_BAD_REQUEST;
//            }
//        }
//        return TAccessTokEmbedResult_BAD_CONNECTION;
//    }
//
//    public TCreateAppResult syn_createApp(TAppInfo appInfo) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TCreateAppResult_NO_CONNECTION;
//            }
//            try {
//                TCreateAppResult result = cli.syn_createApp(appInfo);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TCreateAppResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TCreateAppResult_BAD_REQUEST;
//            }
//        }
//        return TCreateAppResult_BAD_CONNECTION;
//    }
//
//    public int syn_updateApp(Integer appId, TAppInfo appInfo) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int result = cli.syn_updateApp(appId, appInfo);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public TAppInfoResult getApp(int appId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TAppInfoResult_NO_CONNECTION;
//            }
//            try {
//                TAppInfoResult result = cli.getApp(appId);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TAppInfoResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TAppInfoResult_BAD_REQUEST;
//            }
//        }
//        return TAppInfoResult_BAD_CONNECTION;
//    }
//
//    public TMapAppInfoResult multiGetApp(List<Integer> appIds) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TMapAppInfoResult_NO_CONNECTION;
//            }
//            try {
//                TMapAppInfoResult result = cli.multiGetApp(appIds);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TMapAppInfoResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TMapAppInfoResult_BAD_REQUEST;
//            }
//        }
//        return TMapAppInfoResult_BAD_CONNECTION;
//    }
//
//    public TTotalAppIDResult getTotalAppId() {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TTotalAppIDResult_NO_CONNECTION;
//            }
//            try {
//                TTotalAppIDResult result = cli.getTotalAppId();
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TTotalAppIDResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TTotalAppIDResult_BAD_REQUEST;
//            }
//        }
//        return TTotalAppIDResult_BAD_CONNECTION;
//    }
//
//    public TListAppIDResult getSliceAppId(int indexBegin, int numObject) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TListAppIDResult_NO_CONNECTION;
//            }
//            try {
//                TListAppIDResult result = cli.getSliceAppId(indexBegin, numObject);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TListAppIDResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TListAppIDResult_BAD_REQUEST;
//            }
//        }
//        return TListAppIDResult_BAD_CONNECTION;
//    }
//
//    public TListUserAppResult multiIsAuthorizedApp(List<TUserApp> list) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TListUserAppResult_NO_CONNECTION;
//            }
//            try {
//                TListUserAppResult result = cli.multiIsAuthorizedApp(list);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TListUserAppResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TListUserAppResult_BAD_REQUEST;
//            }
//        }
//        return TListUserAppResult_BAD_CONNECTION;
//    }
//
//    public TAppSecretKeyResult genNewAppSecretKey(int id) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TAppSecretKeyResult_NO_CONNECTION;
//            }
//            try {
//                TAppSecretKeyResult result = cli.syn_genNewAppSecretKey(id);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TAppSecretKeyResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TAppSecretKeyResult_BAD_REQUEST;
//            }
//        }
//        return TAppSecretKeyResult_BAD_CONNECTION;
//    }
//
//    public TPaymentSecretKeyResult genNewPaymentSecretKey(int id) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TPaymentSecretKeyResult_NO_CONNECTION;
//            }
//            try {
//                TPaymentSecretKeyResult result = cli.syn_genNewPaymentSecretKey(id);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TPaymentSecretKeyResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TPaymentSecretKeyResult_BAD_REQUEST;
//            }
//        }
//        return TPaymentSecretKeyResult_BAD_CONNECTION;
//    }
//
//    public TPaymentPrivKeyResult genNewPaymentPrivateKey(int id) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TPaymentPrivKeyResult_NO_CONNECTION;
//            }
//            try {
//                TPaymentPrivKeyResult result = cli.syn_genNewPaymentPrivKey(id);
//                pool.returnClient(cli);
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TPaymentPrivKeyResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TPaymentPrivKeyResult_BAD_REQUEST;
//            }
//        }
//        return TPaymentPrivKeyResult_BAD_CONNECTION;
//    }
//
//    public TCheckPageLinkResult checkPageIsLinked(int pageId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TCheckPageLinkResult_NO_CONNECTION;
//            }
//            try {
//                TCheckPageLinkResult result = cli.checkPageLink(pageId);
//                if (result != null && result.isSetError() && result.getError() < 0) {
////                    result.setError(result.getError() + Common.PLUS_OAUTH);
//                    result.setError(result.getError());
//                }
//
//                return result;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TCheckPageLinkResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TCheckPageLinkResult_BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return TCheckPageLinkResult_BAD_CONNECTION;
//    }
//
//    public int linkAppPage(int appId, int pageId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                return cli.syn_linkAppPage(appId, pageId);
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int unlinkAppPage(int appId, int pageId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                return cli.syn_unlinkAppPage(appId, pageId);
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_increaseAppLogoVersion(Integer appId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                return cli.syn_increaseAppLogoVersion(appId);
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public int syn_unauthorizeApp(Integer appId, Integer uid) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                return cli.syn_unauthorizeApp(appId, uid);
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public TPaymentStatusResult getPaymentStatus(Integer appId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TPaymentStatusResult_NO_CONNECTION;
//            }
//            try {
//                TPaymentStatusResult ret = cli.getPaymentStatus(appId);
//                return ret;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TPaymentStatusResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TPaymentStatusResult_BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return TPaymentStatusResult_BAD_CONNECTION;
//    }
//
//    public TCreatePublisherResult syn_createPublisher(TPublisherInfo publisher) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TCreatePublisherResult_NO_CONNECTION;
//            }
//            try {
//                TCreatePublisherResult ret = cli.syn_createPublisher(publisher);
//                return ret;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TCreatePublisherResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TCreatePublisherResult_BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return TCreatePublisherResult_BAD_CONNECTION;
//    }
//
//    public int syn_updatePublisher(Integer publisherId, TPublisherInfo publisher) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return ZErrorDef.NO_CONNECTION;
//            }
//            try {
//                int ret = cli.syn_updatePublisher(publisherId, publisher);
//                return ret;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return ZErrorDef.BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return ZErrorDef.BAD_CONNECTION;
//    }
//
//    public TKeyEncodedIDResult getKeyEncodedIdByAppId(Integer appId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TKeyEncodedIDResult_NO_CONNECTION;
//            }
//            try {
//                TKeyEncodedIDResult ret = cli.getKeyEncodedIdByAppId(appId);
//                return ret;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TKeyEncodedIDResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TKeyEncodedIDResult_BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return TKeyEncodedIDResult_BAD_CONNECTION;
//    }
//
//    public TPublisherInfoResult getPublisherInfo(Integer publisherId) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TPublisherInfoResult_NO_CONNECTION;
//            }
//            try {
//                TPublisherInfoResult ret = cli.getPublisherInfo(publisherId);
//                return ret;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TPublisherInfoResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TPublisherInfoResult_BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return TPublisherInfoResult_BAD_CONNECTION;
//    }
//
//    public TMapPublisherInfoResult multiGetPublisherInfo(List<Integer> publisherIds) {
//        TClientPool<ZOAService.Client> pool = getClientPool();
//        for (int retry = 0; retry < pool.getNRetry(); ++retry) {
//            ZOAService.Client cli = pool.borrowClient();
//            if (cli == null) {
//                return TMapPublisherInfoResult_NO_CONNECTION;
//            }
//            try {
//                TMapPublisherInfoResult ret = cli.multiGetPublisherInfo(publisherIds);
//                return ret;
//            } catch (TTransportException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                continue;
//            } catch (TException ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TMapPublisherInfoResult_BAD_REQUEST;
//            } catch (Exception ex) {
//                pool.invalidateClient(cli, ex);
//                Log(Priority.ERROR, ex, retry);
//                return TMapPublisherInfoResult_BAD_REQUEST;
//            } finally {
//                pool.returnClient(cli);
//            }
//        }
//        return TMapPublisherInfoResult_BAD_CONNECTION;
//    }
//}
