/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 60005
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 60005
File Encoding         : 65001

Date: 2015-05-12 19:41:02
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
  PRIMARY KEY (`userid`),
  UNIQUE KEY `uni_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
