/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.entity;

import com.vng.zing.pusheventmessage.admin.common.BeanUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import org.json.simple.JSONObject;

/**
 *
 * @author root
 */
public class DeviceInfo {

    public long sdkId;
    public String appVersion;
    public String osVersion;
    public String sdkVersion;
    public String packageName;
    public String appUser;
    public String zaloId;
    public String token;
    public Date ctime;
    public Date utime;

    public static DeviceInfo parse(ResultSet resultSet) throws SQLException {
        DeviceInfo info = new DeviceInfo();
        info.sdkId = resultSet.getLong("sdk_id");
        info.appVersion = resultSet.getString("app_version");
        info.osVersion = resultSet.getString("os_version");
        info.sdkVersion = resultSet.getString("sdk_version");
        info.packageName = resultSet.getString("package_name");
        info.appUser = resultSet.getString("app_user");
        info.zaloId = resultSet.getString("zalo_id");
        info.token = resultSet.getString("token");
        info.ctime = resultSet.getDate("created_at");
        info.utime = resultSet.getDate("updated_at");
        return info;
    }

    public JSONObject toJson() {
        return BeanUtils.newJson()
                .set("sdkId", sdkId)
                .set("appVersion", appVersion)
                .set("osVersion", osVersion)
                .set("sdkVersion", sdkVersion)
                .set("packageName", packageName)
                .set("appUser", appUser)
                .set("zaloId", zaloId)
                .set("token", token)
                .set("ctime", ctime.getTime())
                .set("utime", utime.getTime())
                .build();
    }

    @Override
    public String toString() {
        return toJson().toJSONString();
    }

}
