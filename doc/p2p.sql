create database p2p_crm default character set utf8 collate utf8_general_ci;
use p2p_crm;
create table p2p_crm_info (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  name varchar(64) NOT NULL DEFAULT '' COMMENT '姓名',
  gender varchar(1) NOT NULL DEFAULT '' COMMENT '性别',
  birthday date NOT NULL COMMENT '生日',
  id_card varchar(64) NOT NULL DEFAULT '' COMMENT '身份证号码',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户信息表';

INSERT INTO p2p_crm_info VALUES (1, '李鹏', '1', '1985-7-18', '220112198507172211', '2020-3-29 10:44:54', '2020-3-17 10:44:54', 12, 12);
INSERT INTO p2p_crm_info VALUES (2, '王玲', '0', '1990-3-29', '151112198507172211', '2020-3-10 13:00:28', '2020-3-5 13:00:35', 12, 13);
INSERT INTO p2p_crm_info VALUES (3, '李雨', '1', '2004-6-1', '152112198507173211', '2019-2-5 13:01:21', '2020-1-5 13:01:21', 11, 11);
INSERT INTO p2p_crm_info VALUES (4, '赵四', '1', '1988-11-6', '432236222343242423', '2020-3-16 15:36:57', '2020-3-11 15:37:01', 11, 11);
INSERT INTO p2p_crm_info VALUES (5, '李珑', '0', '1985-7-4', '624353453535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11);
INSERT INTO p2p_crm_info VALUES (6, '李小鹏', '1', '1985-7-18', '233112198507172211', '2020-3-29 10:44:54', '2020-3-17 10:44:54', 12, 12);
INSERT INTO p2p_crm_info VALUES (7, '王小玲', '0', '1990-3-29', '150112198507172211', '2020-3-10 13:00:28', '2020-3-5 13:00:35', 12, 13);
INSERT INTO p2p_crm_info VALUES (8, '李小雨', '1', '2004-6-1', '152112198507172811', '2019-2-5 13:01:21', '2020-1-5 13:01:21', 11, 11);
INSERT INTO p2p_crm_info VALUES (9, '赵小四', '1', '1988-11-6', '43436222343242423', '2020-3-16 15:36:57', '2020-3-11 15:37:01', 11, 11);
INSERT INTO p2p_crm_info VALUES (10,'李小珑', '0', '1985-7-4', '324353453535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11);
INSERT INTO p2p_crm_info VALUES (11, '李霄鹏', '1', '1985-7-18', '215112198507172211', '2020-3-29 10:44:54', '2020-3-17 10:44:54', 12, 12);
INSERT INTO p2p_crm_info VALUES (12, '王霄玲', '0', '1990-3-29', '152112198607174211', '2020-3-10 13:00:28', '2020-3-5 13:00:35', 12, 13);
INSERT INTO p2p_crm_info VALUES (13, '李霄雨', '2', '2004-6-1', '152112198507174211', '2019-2-5 13:01:21', '2020-1-5 13:01:21', 11, 11);
INSERT INTO p2p_crm_info VALUES (14, '赵霄四', '1', '1988-11-6', '431236222343242423', '2020-3-16 15:36:57', '2020-3-11 15:37:01', 11, 11);
INSERT INTO p2p_crm_info VALUES (15, '李霄珑', '0', '1985-7-4', '129953453535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11);
INSERT INTO p2p_crm_info VALUES (16, '姚霄鹏', '1', '1985-7-18', '210112198507172211', '2020-3-29 10:44:54', '2020-3-17 10:44:54', 12, 12);
INSERT INTO p2p_crm_info VALUES (17, '钱霄玲', '0', '1990-3-29', '154112198507172211', '2020-3-10 13:00:28', '2020-3-5 13:00:35', 12, 13);
INSERT INTO p2p_crm_info VALUES (18, '孙霄雨', '1', '2004-6-1', '152112198507272211', '2019-2-5 13:01:21', '2020-1-5 13:01:21', 11, 11);
INSERT INTO p2p_crm_info VALUES (19, '周霄四', '1', '1988-11-6', '432736222343242423', '2020-3-16 15:36:57', '2020-3-11 15:37:01', 11, 11);
INSERT INTO p2p_crm_info VALUES (20, '吴霄珑', '0', '1985-7-4', '156353453535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11);
INSERT INTO p2p_crm_info VALUES (21, '郑霄珑', '0', '1985-7-4', '123435353535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11);

create database p2p_invest default character set utf8 collate utf8_general_ci;
use p2p_invest;
create table p2p_invest_product (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  name varchar(64) NOT NULL DEFAULT '' COMMENT '理财产品名称',
  begin_amt decimal(10,2) DEFAULT NULL COMMENT '起投金额',
  year_irr decimal(4,2) DEFAULT NULL COMMENT '年化收益率',
  product_type varchar(4) NOT NULL DEFAULT '' COMMENT '产品类型：1-季度投,2-双季投,3-投资宝',
  invest_type varchar(4) NOT NULL DEFAULT '' COMMENT '投资类型:1-固定期限,2-非固定期限',
  day_count int(4) DEFAULT NULL COMMENT '投资天数(投资类型:1-固定期限)/封闭期(投资类型:2-非固定期限)',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='投资产品表';

INSERT INTO p2p_invest_product VALUES (1, '季度投', 5000.00, 0.05, '1', '1', 90, '2020-4-6 17:21:20', '2020-4-6 17:21:22', 11, 11);
INSERT INTO p2p_invest_product VALUES (2, '双季投', 8000.00, 0.06, '2', '1', 180, '2020-4-6 17:22:16', '2020-4-6 17:22:20', 11, 11);
INSERT INTO p2p_invest_product VALUES (3, '投资宝', 10000.00, 0.08, '3', '1', 365, '2020-4-6 17:23:00', '2020-4-6 17:23:08', 11, 11);
