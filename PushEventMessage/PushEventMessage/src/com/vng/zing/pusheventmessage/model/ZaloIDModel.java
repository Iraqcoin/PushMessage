package com.vng.zing.pusheventmessage.model;

import com.vng.zing.zaloidmw.thrift.*;
import com.vng.zing.zpstdprofile.thrift.TValue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.vng.zing.pusheventmessage.client.Clients;
import com.vng.zing.pusheventmessage.client.ZaloIDClient;

/**
 *
 * @author anhlh
 */
public class ZaloIDModel {
	public static final ZaloIDClient zaloIDClient = new ZaloIDClient("mw");

	private static final ZaloIDModel Instance = new ZaloIDModel();

	private ZaloIDModel() {
	}

	public static ZaloIDModel getInstance() {
		return Instance;
	}

	public TValue getProfile(int userid) {
		TValue result = null;
		TZProfileResult resultTemp = zaloIDClient.getZProfile(userid);
		if (resultTemp != null && resultTemp.error >= 0) {
			TZProfile profile = resultTemp.getProfile();
			if (profile != null && profile.getUserId() > 0) {
				result = profile.getProfile();
			}
		}
		return result;
	}

	public Map<Integer, TCompactZProfile> multiGetCompactZProfile(List<Integer> userids) {
		Map<Integer, TCompactZProfile> result = new HashMap<Integer, TCompactZProfile>();
		TMapCompactZProfile resultTemp = zaloIDClient.multiGetCompactZProfile(userids);
		if (resultTemp != null && resultTemp.error >= 0) {
			result = resultTemp.dataMap;
		}
		return result;
	}

	public TZProfile getProfileBySession(String key) {
		TZProfile session = null;
		TZProfileResult tResult = zaloIDClient.getZProfileBySessionId(key);
		if (tResult != null && tResult.getError() >= 0) {
			session = tResult.getProfile();
		}

		return session;
	}

	public TZProfile getProfileByUsername(String username) {
		TZProfile session = null;
		TZProfileResult tResult = zaloIDClient.getZProfileByUsername(username);
		if (tResult != null && tResult.getError() >= 0) {
			session = tResult.getProfile();
		}
		return session;
	}

	public TZProfile getZProfileByPhone(long phone) {
		TZProfile session = null;
		TZProfileResult tResult = zaloIDClient.getZProfileByPhone(phone);
		if (tResult != null && tResult.getError() >= 0) {
			session = tResult.getProfile();
		}
		return session;
	}

	public String getAvatarUrl(int uid, int size) {
		String result = "";
		TAvatarResult tResult = zaloIDClient.getAvatarUrl(uid, size);
		if (tResult.getError() >= 0 && tResult.isSetAvatars()) {
			result = tResult.avatars.get(size);
			if (result == null) {
				result = "";
			}
		}
		return result;
	}

	public int existZProfile(int userId) {
		return zaloIDClient.existZProfile(userId);
	}

	public String getLoginTemplateHtml(String tmplId) {
		String html = "";
		TTemplateHtmlResult res = zaloIDClient.getLoginTemplateHtml(tmplId);
		if (res.error >= 0) {
			html = res.html;
		}
		return html;
	}
}
