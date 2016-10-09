/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 60005
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 60005
File Encoding         : 65001

Date: 2016-10-09 17:13:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `home`
-- ----------------------------
DROP TABLE IF EXISTS `home`;
CREATE TABLE `home` (
  `homeId` int(9) NOT NULL AUTO_INCREMENT,
  `home_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`homeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of home
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userid` int(9) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `age` int(9) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `boy` tinyint(1) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `time` time DEFAULT NULL,
  `enumint` int(11) DEFAULT NULL,
  `enumstring` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=498084 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
