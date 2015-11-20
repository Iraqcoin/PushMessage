-- phpMyAdmin SQL Dump
-- version 4.0.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Sep 28, 2015 at 02:53 PM
-- Server version: 5.5.44-0ubuntu0.14.04.1
-- PHP Version: 5.5.9-1ubuntu4.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `dbtest`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_api_key`
--

CREATE TABLE IF NOT EXISTS `tbl_api_key` (
  `app_id` varchar(32) NOT NULL,
  `platform` char(8) NOT NULL,
  `api_key` varchar(155) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  UNIQUE KEY `unique` (`app_id`,`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_<app_id>_<platform>_query`
--

CREATE TABLE IF NOT EXISTS `tbl_<app_id>_<platform>_query` (
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
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_<app_id>_<platform>_token`
--

CREATE TABLE IF NOT EXISTS `tbl_<app_id>_<platform>_token` (
  `sdk_id` bigint(20) NOT NULL,
  `token` varchar(155) NOT NULL,
  `active` char(2) DEFAULT 'Y',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`sdk_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tbl_history`
--

CREATE TABLE IF NOT EXISTS `tbl_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `request_id` char(32) NOT NULL,
  `app_id` varchar(32) NOT NULL,
  `platform` char(8) NOT NULL,
  `requester` char(32) DEFAULT NULL,
  `found` int(11) NOT NULL DEFAULT '0',
  `pushed` int(11) NOT NULL DEFAULT '0',
  `failed` int(11) NOT NULL DEFAULT '0',
  `paused_at` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `request` varchar(512) CHARACTER SET utf8 NOT NULL,
  `state` char(10) NOT NULL,
  `error` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `request_id` (`request_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

