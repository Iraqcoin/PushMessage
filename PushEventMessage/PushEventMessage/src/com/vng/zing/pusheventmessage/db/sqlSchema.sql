

CREATE TABLE IF NOT EXISTS msgs (
  id INT NOT NULL AUTO_INCREMENT,
  app_id INT NOT NULL,
  event_name VARCHAR(255),
  status INT,
  
  event_type INT,
  portrait_width INT,
  portrait_height INT,
  landscape_width INT,
  landscape_height INT,
  start_date BIGINT,
  end_date BIGINT,
  
  period_type INT,
  visible_times INT,
  
  ios_html LONGTEXT,
  android_html LONGTEXT,
  wphone_html LONGTEXT,
  
  ios_html_hash TEXT,
  android_html_hash TEXT,
  wphone_html_hash TEXT,
  
  platforms TEXT,
  package_names TEXT,
  bundle_ids TEXT,
  guids TEXT,
  sdk_versions TEXT,
  app_versions TEXT,
  zalo_ids TEXT,
  app_users TEXT,

  logged_in INT,
  
  PRIMARY KEY (id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


CREATE TABLE IF NOT EXISTS `noti` (
    id INT NOT NULL AUTO_INCREMENT,
    messgae VARCHAR(255),
    badge INT,
    sound VARCHAR(255),
    icon VARCHAR(255),
    extraData VARCHAR(255),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET utf8 COLLATE utf8_general_ci;


CREATE TABLE IF NOT EXISTS `push_noti_job` (
	id  INT NOT NULL AUTO_INCREMENT,
	appId  INT NOT NULL,
	osVersion VARCHAR(1024),
	sdkVersion VARCHAR(1024),
	zaloId VARCHAR(1024),
	appUser VARCHAR(1024),
	bundleIds VARCHAR(1024),
	packageNames VARCHAR(1024),
	guids VARCHAR(1024),
    android INT,
    ios     INT,
    wphone  INT,

    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET utf8 COLLATE utf8_general_ci;


	-- 1:optional i32 id;
	-- 2:optional string message;
	-- 3:optional i32 badge;
	-- 4:optional string sound;
	-- 5:optional string icon;
	-- 6:optional string extraData;


CREATE TABLE IF NOT EXISTS `noti` (
	id  INT NOT NULL AUTO_INCREMENT,
	message VARCHAR(1024),
	badge INT,
	sound VARCHAR(1024),
	icon VARCHAR(1024),
	extraData VARCHAR(1024),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET utf8 COLLATE utf8_general_ci;


-- appId,osVersion,sdkVersion,zaloId,appUser,bundleIds,packageNames,guids,android,ios,wphone


--         __ORIENTATION__
--        __SCREEN_WIDTH__
--        __SCREEN_HEIGHT__


CREATE TABLE IF NOT EXISTS out_of_quota (
    msg_id BIGINT,
    sdkids LONGTEXT,
    PRIMARY KEY (msg_id)
);


CREATE TABLE IF NOT EXISTS scheduled_task (
    id INT NOT NULL AUTO_INCREMENT,
    app_id INT NOT NULL ,
    time BIGINT NOT NULL,
    model TEXT,
    type INT DEFAULT 0, 
    status INT DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

CREATE TABLE IF NOT EXISTS app_owner (
    app_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (app_id, user_id)
);










CREATE TABLE IF NOT EXISTS `tbl_10418_ios_query` (
  `sdk_id` bigint(20) NOT NULL,
  `app_version` char(16) DEFAULT NULL,
  `os_version` char(16) DEFAULT NULL,
  `sdk_version` char(16) DEFAULT NULL,
  `package_name` varchar(128) DEFAULT NULL,
  `app_user` varchar(255) DEFAULT NULL,
  `zalo_id` varchar(20) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`sdk_id`),
  KEY `base_idx` (`app_version`,`os_version`,`sdk_version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_<app_id>_<platform>_token`
--

CREATE TABLE IF NOT EXISTS `tbl_10418_ios_token` (
  `sdk_id` bigint(20) NOT NULL,
  `token` varchar(155) NOT NULL,
  `active` char(2) DEFAULT 'Y',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`sdk_id`),
  UNIQUE KEY `Unique_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE IF NOT EXISTS `tbl_10418_android_query` (
  `sdk_id` bigint(20) NOT NULL,
  `app_version` char(16) DEFAULT NULL,
  `os_version` char(16) DEFAULT NULL,
  `sdk_version` char(16) DEFAULT NULL,
  `package_name` varchar(128) DEFAULT NULL,
  `app_user` varchar(255) DEFAULT NULL,
  `zalo_id` varchar(20) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`sdk_id`),
  KEY `base_idx` (`app_version`,`os_version`,`sdk_version`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_<app_id>_<platform>_token`
--

CREATE TABLE IF NOT EXISTS `tbl_10418_android_token` (
  `sdk_id` bigint(20) NOT NULL,
  `token` varchar(155) NOT NULL,
  `active` char(2) DEFAULT 'Y',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`sdk_id`),
  UNIQUE KEY `Unique_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_token`
--

CREATE TABLE IF NOT EXISTS `tbl_token` (
  `app_id` varchar(32) NOT NULL,
  `platform` char(8) NOT NULL,
  `token` varchar(155) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  UNIQUE KEY `unique` (`app_id`,`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
