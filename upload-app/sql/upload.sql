

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for access
-- ----------------------------
DROP TABLE IF EXISTS `access`;
CREATE TABLE `access` (
  `access_key` varchar(255) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL,
  `openid` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`openid`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of access
-- ----------------------------
INSERT INTO `access` VALUES ('1c47c3be44a56b234fa699bd864e2', 'f1a4fb32f9150e0dffebd09c5c7eda4', '1001');

-- ----------------------------
-- Table structure for code_dbinfo
-- ----------------------------
DROP TABLE IF EXISTS `code_dbinfo`;
CREATE TABLE `code_dbinfo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL COMMENT '别名',
  `db_driver` varchar(100) NOT NULL COMMENT '数据库驱动',
  `db_url` varchar(200) NOT NULL COMMENT '数据库地址',
  `db_user_name` varchar(100) NOT NULL COMMENT '数据库账户',
  `db_password` varchar(100) NOT NULL COMMENT '连接密码',
  `db_type` varchar(10) DEFAULT NULL COMMENT '数据库类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='数据库链接信息';

-- ----------------------------
-- Records of code_dbinfo
-- ----------------------------

-- ----------------------------
-- Table structure for upload
-- ----------------------------
DROP TABLE IF EXISTS `upload`;
CREATE TABLE `upload` (
  `open_id` int(11) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `txid` varchar(255) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of upload
-- ----------------------------
INSERT INTO `upload` VALUES ('1001', '征收土地公告2019年第318号.pdf', '33321467b47b412a8337fe37889bfa4e', null);
INSERT INTO `upload` VALUES ('1001', 'upload文档.txt', '95d45cb42ec6081f5712b662bff54dd9f17db78b326b03ebbdef7f359d562573', null);
INSERT INTO `upload` VALUES (null, 'upload文档.txt', '4803dcfc1056e7db930ee3cf1e91dc32ae37f7690ab53b45be7a07de2cd5584a', null);
INSERT INTO `upload` VALUES (null, 'upload文档.txt', '3800ec9a14a5682f2a0058a24def1834c706e7d1c80d55ac79c420f6f1a84fcd', null);
INSERT INTO `upload` VALUES (null, 'upload文档.txt', '3e4539e86af1218698d53a82caa9281c2310953eebc0d2ff33c2b1a8a631b8dd', null);
INSERT INTO `upload` VALUES (null, 'upload文档.txt', '1517c16107d6a7d6b52667d574a6b18f94f88c8a01334bac86dc60402ab4c910', null);
INSERT INTO `upload` VALUES ('1001', '微信图片_20181008173657.png', 'ab16f70c58dd88f99e7dc84cd3499c474169829ccd52b71e98ad382cc503f08e', 'png');
INSERT INTO `upload` VALUES ('1001', 'upload文档.txt', 'fbeadffc87cd99b819d255b085d1455c583b83707e4c9a7ff930610bdaff8075', 'txt');
INSERT INTO `upload` VALUES ('1001', 'upload文档.txt', 'bf85d0b42bc745c3457c93a791ecef11d1abf1be28c351fd8fccb3cad2e7961b', 'txt');
INSERT INTO `upload` VALUES ('1001', '文档upload.txt', '943b5c8953b7e8c36f9dfcfb4d868c541f3d2d8b98570780fb700ee417f26eb3', 'txt');
INSERT INTO `upload` VALUES ('1001', '1.jpg', '7d1b0e9b15941e190f9b72a511060d17791aa14bbd08f0058790847334611276', 'jpg');
INSERT INTO `upload` VALUES ('1001', '2123.mp4', '5fda73e9291bb4d973f5579e84589f8b094f4b9fe7699e657f5ca2740396c425', 'mp4');
INSERT INTO `upload` VALUES ('1001', '_java_upload_data_征收土地公告2019年第318号93514.pdf', 'f17c8b546c6f38882be5e848362258901071d9e00cb9d3f72c58585779440a05', 'pdf');
INSERT INTO `upload` VALUES ('1001', 'fchwallet.rar', '1f6dc4adf42047b18b7e8282cd17375c41bca7c166e5d72f27b50faaa57831ce', 'rar');
