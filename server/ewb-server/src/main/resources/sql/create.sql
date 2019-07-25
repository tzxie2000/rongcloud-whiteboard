/** 创建database */
CREATE DATABASE ewb;
/** 使用database */
use ewb;

/** 创建房间表 */
CREATE TABLE `room` (
  `roomkey` varchar(255) NOT NULL,
  `appid` varchar(255) DEFAULT NULL,
  `roomnr` varchar(255) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  PRIMARY KEY (`roomkey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/** 创建页面表 */
CREATE TABLE `page` (
  `pageid` bigint(20) NOT NULL AUTO_INCREMENT,
  `roomkey` varchar(255) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `filegroup` varchar(255) DEFAULT NULL,
  `filename` varchar(255) DEFAULT NULL,
  `fileurl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`pageid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/** 创建笔画表 */
CREATE TABLE `print` (
  `printid` bigint(20) NOT NULL AUTO_INCREMENT,
  `pageid` bigint(20) DEFAULT NULL,
  `roomkey` varchar(255) DEFAULT NULL,
  `data` text,
  `createtime` datetime DEFAULT NULL,
  PRIMARY KEY (`printid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/** 页面表创建索引 */
CREATE INDEX `idx_page` ON `page` (`roomkey`);

/** 笔画表创建索引 */
CREATE INDEX `idx_print` ON `print` (`roomkey`, `pageid`);

/** print表增加userid字段 */
ALTER TABLE `print` ADD column userid varchar(255) DEFAULT NULL;

/** 创建激光笔表 */
CREATE TABLE `laserprint` (
  `printid` bigint(20) NOT NULL AUTO_INCREMENT,
  `pageid` bigint(20) DEFAULT NULL,
  `roomkey` varchar(255) DEFAULT NULL,
  `data` text,
  `createtime` datetime DEFAULT NULL,
  PRIMARY KEY (`printid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

/** 激光笔表创建索引 */
CREATE INDEX `idx_laserprint` ON `laserprint` (`roomkey`, `pageid`);

/** 页面表增加缩略图字段 */
ALTER TABLE `page` ADD column thumbnail mediumtext DEFAULT NULL;