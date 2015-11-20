package com.vng.zing.pusheventmessage.common;

import com.vng.zing.logger.ZLogger;
import com.vng.zing.pusheventmessage.model.ZaloIDModel;
import com.vng.zing.zaloidmw.thrift.TZProfile;
import com.vng.zing.zpstdprofile.thrift.TValue;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author anhlh
 */
public class Auth {

	private static final Logger _log = ZLogger.getLogger(Auth.class);
	public static final String AUTH = "zsid";
	public int userId = 0;
	public String userName = "";
	public String displayName = "";
	public long dayOfBirth = 0;
	public int gender = 0;
	public int avatarVersion = 0;
	public String avatarURL = "";
	public boolean isLogged = false;
	public int role = 0;

	public static Auth getIdentity(HttpServletRequest req) {
		Auth auth = new Auth();
		String zsession = "";

		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(AUTH)) {
					zsession = cookie.getValue();
					break;
				}
			}
			if (!zsession.isEmpty()) {
				try {
					TZProfile profile = ZaloIDModel.getInstance().getProfileBySession(zsession);
					if (profile != null) {
						TValue value = profile.getProfile();
						auth.userId = profile.getUserId();
						auth.userName = value.getUserName();
						auth.displayName = value.getDisplayName();
						auth.dayOfBirth = value.getBirthDate();
						auth.avatarVersion = value.getAvatarVersion();
						auth.isLogged = true;
						auth.avatarURL = ZaloIDModel.getInstance().getAvatarUrl(auth.userId, 50);
					}
				} catch (Exception ex) {
                    ex.printStackTrace();
					_log.error(ex.getMessage(), ex);
				}
			}
		}
		req.setAttribute(AUTH, auth);
		return auth;
	}

    @Override
    public String toString() {
        return "Auth{" + "userId=" + userId + ", userName=" + userName + ", displayName=" + displayName + ", dayOfBirth=" + dayOfBirth + ", gender=" + gender + ", avatarVersion=" + avatarVersion + ", avatarURL=" + avatarURL + ", isLogged=" + isLogged + ", role=" + role + '}';
    }
    
    
}
