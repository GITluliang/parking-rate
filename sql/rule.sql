/*
Navicat MySQL Data Transfer

Source Server         : luliang888.top
Source Server Version : 50729
Source Host           : luliang888.top:3306
Source Database       : rule

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2020-07-06 19:52:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `rule`
-- ----------------------------
DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `market_id` int(11) NOT NULL COMMENT '市场id',
  `free_time` int(11) DEFAULT NULL COMMENT '免费停车时间',
  `day_cost` decimal(10,2) DEFAULT NULL COMMENT '24小时最高收费',
  `type` tinyint(4) NOT NULL COMMENT '0:分时计费 1：分时分段计费',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='停车计费规则';

-- ----------------------------
-- Records of rule
-- ----------------------------
INSERT INTO `rule` VALUES ('1', '10', '59', '900.00', '0', '2020-06-30 15:30:05', null);
INSERT INTO `rule` VALUES ('2', '11', '60', '800.00', '1', null, null);

-- ----------------------------
-- Table structure for `rule_detail`
-- ----------------------------
DROP TABLE IF EXISTS `rule_detail`;
CREATE TABLE `rule_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `market_id` int(11) NOT NULL COMMENT '市场id',
  `unit_type` tinyint(4) DEFAULT NULL COMMENT '0：分钟 1：小时',
  `time_interval` int(11) DEFAULT NULL COMMENT '时段',
  `unit_price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `interval_start` int(11) DEFAULT NULL COMMENT '分时分段,时段开始',
  `interval_end` int(11) DEFAULT NULL COMMENT '分时分段,时段结束',
  `interval_cost` decimal(10,2) DEFAULT NULL COMMENT '分时分段,时段最高收费',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='停车计费规则详情';

-- ----------------------------
-- Records of rule_detail
-- ----------------------------
INSERT INTO `rule_detail` VALUES ('1', '10', '0', '15', '3.00', null, null, null, '2020-06-30 15:30:54', '2020-06-30 15:30:58');
INSERT INTO `rule_detail` VALUES ('2', '11', '0', '13', '4.00', '22', '23', '1000.00', null, null);
INSERT INTO `rule_detail` VALUES ('3', '11', '0', '13', '5.00', '0', '12', '1000.00', null, null);
INSERT INTO `rule_detail` VALUES ('4', '11', '0', '13', '7.00', '13', '19', '1000.00', null, null);
INSERT INTO `rule_detail` VALUES ('5', '11', '0', '13', '6.00', '20', '21', '1000.00', null, null);
