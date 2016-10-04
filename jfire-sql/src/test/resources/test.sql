/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 60005
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 60005
File Encoding         : 65001

Date: 2016-10-04 20:14:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(32) CHARACTER SET gbk DEFAULT NULL COMMENT '账号名称',
  `password` varchar(32) CHARACTER SET gbk DEFAULT NULL COMMENT '用户密码，保存的是密码的md5值',
  `age` int(11) DEFAULT NULL,
  `birthday` datetime DEFAULT NULL,
  `boy` tinyint(4) DEFAULT NULL,
  `weight` float DEFAULT NULL,
  `time` time DEFAULT NULL,
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=248044 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
