namespace java com.vng.zing.pusheventmessage.thrift

enum EventType{
	NOTI = 1;
	CLOSING = 2;
}

struct PopupSize {
	1:optional i32 width;
	2:optional i32 height;
}

enum PeriodType {
	NONE = 0;
	DAY = 1;
}

enum MobilePlatform {
	ANDROID = 0;
	IOS = 1;
	WPHONE = 2;
}

enum MsgStatus {
	INACTIVE = 0;
	ACTIVE = 1;
}

struct EventMsg{
    1:optional i64 id,
    2:optional i32 appId,
    3:optional string eventName;
    //4:optional EventType eventType;

    5:optional PopupSize portrait;
    6:optional PopupSize landscape;

    7:optional i64 startDate;
    8:optional i64 endDate;

    9:optional PeriodType periodType;
    11:optional i32 visibleTimes;

    12:optional string iosHtml;
    13:optional string androidHtml;
    14:optional string wphoneHtml;

    15:optional list<MobilePlatform> platforms;

    16:optional list<string> packageNames;
    17:optional list<string> bundleIds;
    18:optional list<string> guids;

    19:optional list<string> sdkVersions;
    20:optional list<string> appVersions;

    21:optional list<i64> zaloIds;
    22:optional list<string> appUsers;

    24:optional MsgStatus status;

    25:optional string iosHtmlHash;
    26:optional string androidHtmlHash;
    27:optional string wphoneHtmlHash;
    28:optional bool loggedIn;
    29:optional i64 delayTime;
}

struct EventMsgResult {
	1:i32 resultCode;	
	2:EventMsg eventMsg;
}

struct ListEventMsgResult {
	1:i32 resultCode;	
	2:list<EventMsg> eventMsg;
}


struct EventMsgRequestInfo {
	1:optional i32 responseCode;
	2:optional MobilePlatform platform;
	3:optional string osVersion;
	4:optional i64 viewCounter;
	5:optional i64 viewLimit;
	6:optional string appVersion;
	7:optional string sdkVersion;
	8:optional i64 sdkId;
	9:optional i64 msgId;
	10:optional string reason;
	11:optional i64 timestamp;
}

struct GetLastRequestResult {
	1:i32 resultCode;	
	2:list<EventMsgRequestInfo> eventMsgRequests;
}

struct Noti {
	1:optional i32 id;
	2:optional string message;
	3:optional i32 badge;
	4:optional string sound;
	5:optional string icon;
	6:optional string extraData;
	7:optional i32 expireDate;
	8:optional string title;
}

struct PushNotiJob {
	1:optional i32 id;
	2:optional i32 appId;
	3:optional list<string> osVersion;
	4:optional list<string> sdkVersion;
	5:optional list<string> zaloId;
	6:optional list<string> appUser;
	7:optional list<string> bundleIds;
	8:optional list<string> packageNames;
	9:optional list<string> guids;

	10:optional Noti androidNoti;
	11:optional Noti iosNoti;
	12:optional Noti wphoneNoti;
}

enum PushNotiJobStatus {
	PEND = 1;
	PROCESS = 2;
	PAUSE = 3;
	COMPLETE = 4;
}

struct  PushNotiJobDetail {
	1:optional PushNotiJobStatus status;
	2:optional i64 totalNoti;
	3:optional i64 pushedNumber;
	4:optional i64 successNumber;
	5:optional i64 failNumber;
	6:optional i32 lastId;
}

struct PushNotiResult {
	1:optional i32 code;
	2:optional string requestId;
}

//request_id, app_id, platform, found, pushed, request, state
struct PushNotiInfo {
	1:optional string requestId;
	2:optional i32 appId;
	3:optional string platform,
	4:optional i32 found;
	5:optional i32 pushed;
	6:optional i32 request;
	7:optional string state;
	8:optional i32 fail;
}

struct GetPushNotiTask {
	1:optional i32 code;
	2:optional PushNotiInfo info;
}

struct GetPushNotiTaskPage {
	1:optional i32 code;
	2:optional list<PushNotiInfo> info;
}

struct CountResult {
	1:optional i32 code;
	2:optional i64 count;
}

struct ScheduledTask {
	1:optional i32 id;
	2:optional i64 time;
	3:optional string model;
	4:optional i32 type;
	5:optional i32 status;
}

struct GetAllScheduledTaskResult {
	1:optional i32 code;
	2:optional list<ScheduledTask> task;
}

struct GetAppOwnerResult {
	1:optional i32 code;
	2:optional list<i64> user;
}


service PushEventMsgService {
	i64 createMsg(1:EventMsg eventMsg);

	EventMsgResult getEventMsg(1:i64 eventMsgId);
	
	i32 updateEventMsg(1:EventMsg eventMsg);

	ListEventMsgResult findEventMsg(1:i32 appId);

	i64 forceReloadEventMsg(1:i32 appId);

	i32 deleteEventMsg(1:i64 eventMsgId);

	// i64 getViewCount(1:i64 msgId, 2:string appUser, 3:i64 zaloId, 4:string deviceId);

	i64 getViewCount(1:i64 msgId, 2:string identifier);

	GetLastRequestResult getLastRequest(1:i32 appId);

	PushNotiResult pushAndroidNoti(1:i32 appId, 2:Noti noti, 3:list<string> appVersion, 4:list<string> osVersion, 5:list<string> sdkVersion, 6:list<string> packageNames, 7:list<i64> zaloId, 8:list<string> appuser, 9:i64 time);

	PushNotiResult pushIosNoti(1:i32 appId, 2:Noti noti, 3:list<string> appVersion, 4:list<string> osVersion, 5:list<string> sdkVersion, 6:list<string> packageNames, 7:list<i64> zaloId, 8:list<string> appuser, 9:i64 time);

	i32 saveAndroidDeviceInfo(1:i64 sdkId, 2:i32 appId, 3:string platform, 4:string token, 5:string appVersion, 6:string osVersion, 7:string sdkVersion, 8:string packageName);

 	GetPushNotiTask getPushNotiTask(1:string requestId);

 	i32 setApnsKeyFile(1:i32 appId, 2:binary file, 3:string keyPass);

 	i32 createAppTable(1:i32 appId);

 	i32 setGoogleApiKey(1:i32 appId, 2:string apiKey);

 	GetPushNotiTaskPage getPushNotiTaskPage(1:i32 appId, 2:i32 offset, 3:i32 size);

 	CountResult countPushNotiTask(1:i32 appId);

 	i32 hasApnsKeyFile(1:i32 appId);

 	i32 hasGcmKey(1:i32 appId);

 	GetAllScheduledTaskResult getAllScheduledTask(1:i32 appId);

 	i32 setAppOwner(1:i32 appId, 2:i64 userId);

 	i32 removeAppOwner(1:i32 appId, 2:i64 userId);

 	GetAppOwnerResult getAppOwner(1:i32 appId);
}
