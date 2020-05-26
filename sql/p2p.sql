drop database p2p_crm;
drop database p2p_invest;
drop database p2p_payment;
drop database p2p_account;
drop database p2p_match;
drop database p2p_borrow;

#--------------------p2p_crm--------------------
create database p2p_crm default character set utf8 collate utf8_general_ci;
use p2p_crm;

create table p2p_customer (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  name varchar(64) NOT NULL COMMENT '姓名',
  gender varchar(1) NOT NULL COMMENT '性别',
  birthday date NOT NULL COMMENT '生日',
  id_card varchar(64) NOT NULL COMMENT '身份证号码',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户表';

#--------------------p2p_invest--------------------
create database p2p_invest default character set utf8 collate utf8_general_ci;
use p2p_invest;

create table p2p_invest_product (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  name varchar(64) NOT NULL COMMENT '理财产品名称:季度投,双季投,投资宝',
  begin_amt decimal(10,2) NOT NULL COMMENT '起投金额',
  year_irr decimal(6,4) NOT NULL COMMENT '年化收益率',
  invest_type varchar(4) NOT NULL COMMENT '投资类型:1-固定期限,2-无固定期限',
  day_count int(4) NOT NULL COMMENT '投资天数(投资类型:1-固定期限)/封闭期(投资类型:2-无固定期限)',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='投资产品表';

create table p2p_invest (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  invest_product_id int(16) NOT NULL COMMENT '投资产品编号',
  invest_product_name varchar(64) NOT NULL COMMENT '理财产品名称',
  invest_year_irr decimal(6,4) NOT NULL COMMENT '年化收益率',
  invest_type varchar(4) NOT NULL COMMENT '投资类型:1-固定期限,2-无固定期限',
  invest_day_count int(4) NOT NULL COMMENT '投资天数(投资类型:1-固定期限)/封闭期(投资类型:2-无固定期限)',
  customer_bank_id int(16) NOT NULL COMMENT '客户银行编号',
  bank_code varchar(64) NOT NULL COMMENT '银行编号',
  base_bank_name varchar(64) NOT NULL COMMENT '银行总行名称',
  bank_account varchar(64) NOT NULL COMMENT '银行卡号',
  phone varchar(32) NOT NULL COMMENT '绑定手机号',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '客户姓名',
  customer_id_card varchar(64) NOT NULL COMMENT '客户身份证号码',
  invest_amt decimal(10,2) NOT NULL COMMENT '投资金额',
  profit decimal(10,2) NOT NULL COMMENT '收益',
  start_date date NOT NULL COMMENT '投资开始日期',
  end_date date COMMENT '投资结束日期',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='投资表';

create table p2p_lending (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  invest_id int(16) NOT NULL COMMENT '投资编号',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  amount decimal(10,2) NOT NULL COMMENT '金额',
  lending_type varchar(1) NOT NULL comment '出借单类型:1-新出借，2-回款再出借',
  order_sn varchar(64) NOT NULL COMMENT '订单编号,投资充值业务单号,作为payment系统幂等性验证',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出借单表';

create table p2p_invest_claim (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  invest_id int(16) NOT NULL COMMENT '投资编号',
  lending_id int(16) NOT NULL COMMENT '出借单编号',
  borrow_id int(16) NOT NULL COMMENT '借款编号',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '客户姓名',
  buy_amt decimal(10,2) NOT NULL COMMENT '买入金额',
  claim_amt decimal(10,2) NOT NULL COMMENT '债权金额',
  hold_share decimal(4,2) NOT NULL COMMENT '持有比例',
  borrow_product_id int(16) NOT NULL COMMENT '借款产品编号',
  borrow_product_name varchar(64) NOT NULL COMMENT '借款产品名称',
  borrow_year_rate decimal(6,4) NOT NULL COMMENT '贷款年利率',
  parent_id int(16) NOT NULL COMMENT '父投资债权编号：新借款为0/转让为父编号',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='投资债权表';

create table p2p_invest_claim_history (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  invest_id int(16) NOT NULL COMMENT '投资编号',
  lending_id int(16) NOT NULL COMMENT '出借单编号',
  borrow_id int(16) NOT NULL COMMENT '借款编号',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '客户姓名',
  buy_amt decimal(10,2) NOT NULL COMMENT '买入金额',
  claim_amt decimal(10,2) NOT NULL COMMENT '债权金额',
  hold_share decimal(4,2) NOT NULL COMMENT '持有比例',
  borrow_product_id int(16) NOT NULL COMMENT '借款产品编号',
  borrow_product_name varchar(64) NOT NULL COMMENT '借款产品名称',
  borrow_year_rate decimal(6,4) NOT NULL COMMENT '贷款年利率',
  parent_id int(16) NOT NULL COMMENT '父投资债权编号：新借款为0/转让为父编号',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='投资债权历史表';

create table p2p_transfer (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  invest_id int(16) NOT NULL COMMENT '投资编号',
  invest_product_id int(16) NOT NULL COMMENT '投资产品编号',
  invest_product_name varchar(64) NOT NULL COMMENT '理财产品名称',
  invest_year_irr decimal(6,4) NOT NULL COMMENT '年化收益率',
  invest_type varchar(4) NOT NULL COMMENT '投资类型:1-固定期限,2-无固定期限',
  invest_day_count int(4) NOT NULL COMMENT '投资天数(投资类型:1-固定期限)/封闭期(投资类型:2-无固定期限)',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '客户姓名',
  customer_id_card varchar(64) NOT NULL COMMENT '客户身份证号码',
  customer_bank_id int(16) NOT NULL COMMENT '客户银行编号',
  bank_code varchar(64) NOT NULL COMMENT '银行编号',
  base_bank_name varchar(64) NOT NULL COMMENT '银行总行名称',
  bank_account varchar(64) NOT NULL COMMENT '银行卡号',
  phone varchar(32) NOT NULL COMMENT '绑定手机号',
  invest_amt decimal(10,2) NOT NULL COMMENT '投资金额',
  profit decimal(10,2) NOT NULL COMMENT '收益',
  start_date date NOT NULL COMMENT '投资开始日期',
  end_date date COMMENT '投资结束日期',
  transfer_amt decimal(10,2) NOT NULL COMMENT '转让金额（现金+债权）',
  cash_amt decimal(10,2) NOT NULL COMMENT '现金',
  claim_amt decimal(10,2) NOT NULL COMMENT '债权金额',
  express_fee decimal(10,2) NOT NULL COMMENT '加急费',
  discount_fee decimal(10,2) NOT NULL COMMENT '折扣费',
  service_fee decimal(10,2) NOT NULL COMMENT '服务费',
  redeem_amt decimal(10,2) NOT NULL COMMENT '赎回金额:投资类型为固定期限=投资金额+收益;投资类型为非固定期限=转让金额-加急费-服务费',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='转让表';

create table p2p_transfer_dtl_sale_before (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  transfer_id int(16) NOT NULL COMMENT '转让编号',
  invest_claim_id int(16) NOT NULL COMMENT '投资债权编号',
  invest_id int(16) NOT NULL COMMENT '投资编号',
  lending_id int(16) NOT NULL COMMENT '出借单编号',
  borrow_id int(16) NOT NULL COMMENT '借款编号',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '客户姓名',
  buy_amt decimal(10,2) NOT NULL COMMENT '买入金额',
  claim_amt decimal(10,2) NOT NULL COMMENT '债权金额',
  hold_share decimal(4,2) NOT NULL COMMENT '持有比例',
  borrow_product_id int(16) NOT NULL COMMENT '借款产品编号',
  borrow_product_name varchar(64) NOT NULL COMMENT '借款产品名称',
  borrow_year_rate decimal(6,4) NOT NULL COMMENT '贷款年利率',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）:1-有效,0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='转让售前明细表';

create table p2p_transfer_dtl (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  transfer_id int(16) NOT NULL COMMENT '转让编号',
  invest_claim_id int(16) NOT NULL COMMENT '投资债权编号',
  invest_id int(16) NOT NULL COMMENT '投资编号',
  lending_id int(16) NOT NULL COMMENT '出借单编号',
  invest_customer_id int(16) NOT NULL COMMENT '投资客户编号',
  invest_customer_name varchar(64) NOT NULL COMMENT '投资客户姓名',
  borrow_id int(16) NOT NULL COMMENT '借款编号',
  borrow_customer_id int(16) NOT NULL COMMENT '借款客户编号',
  borrow_customer_name varchar(64) NOT NULL COMMENT '借款客户姓名',
  buy_amt decimal(10,2) NOT NULL COMMENT '买入金额',
  hold_share decimal(4,2) NOT NULL COMMENT '持有比例',
  borrow_product_id int(16) NOT NULL COMMENT '借款产品编号',
  borrow_product_name varchar(64) NOT NULL COMMENT '借款产品名称',
  borrow_year_rate decimal(6,4) NOT NULL COMMENT '贷款年利率',
  borrow_customer_id_card varchar(64) NOT NULL COMMENT '借款身份证号码',
  borrow_amt decimal(10,2) NOT NULL COMMENT '借款金额',
  borrow_month_count int(2) NOT NULL COMMENT '借款期限',
  borrow_total_borrow_fee decimal(10,2) NOT NULL COMMENT '总借款费用=总利息+总管理费=月供*借款月数-借款金额',
  borrow_total_interest decimal(10,2) NOT NULL COMMENT '总利息',
  borrow_total_manage_fee decimal(10,2) NOT NULL COMMENT '总管理费',
  borrow_start_date date NOT NULL COMMENT '借款开始日期',
  borrow_end_date date COMMENT '借款结束日期',
  borrow_first_return_date date NOT NULL comment '首期还款日期',
  borrow_month_return_day int(2) NOT NULL comment '月还款日15/28',
  borrow_month_payment decimal(10,2) NOT NULL COMMENT '月供=月本息+月管理费=[借款金额×月利率×(1+月利率)^借款月数]÷[(1+月利率)^借款月数-1]',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='转让明细表';


#--------------------p2p_payment--------------------
create database p2p_payment default character set utf8 collate utf8_general_ci;
use p2p_payment;

create table p2p_payment_base_bank (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  name varchar(64) NOT NULL COMMENT '银行名称',
  bank_code varchar(64) NOT NULL COMMENT '银行编号',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='基础银行表';

create table p2p_payment_customer_bank (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  bank_code varchar(64) NOT NULL COMMENT '银行编号',
  base_bank_name varchar(64) NOT NULL COMMENT '银行总行名称',
  bank_account varchar(64) NOT NULL COMMENT '银行卡号',
  phone varchar(32) NOT NULL COMMENT '绑定手机号',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户银行表';

create table p2p_payment (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  order_sn varchar(64) NOT NULL COMMENT '订单编号',
  system_source varchar(16) NOT NULL COMMENT '系统来源',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '姓名',
  id_card varchar(64) NOT NULL COMMENT '身份证号码',
  phone varchar(32) NOT NULL COMMENT '绑定手机号',
  bank_code varchar(64) NOT NULL COMMENT '银行总行编号',
  base_bank_name varchar(64) NOT NULL COMMENT '银行总行名称',
  bank_account varchar(64) NOT NULL COMMENT '银行账户',
  amount decimal(10,2) NOT NULL COMMENT '金额',
  type int(2) NOT NULL COMMENT '支付单类型',
  remark varchar(512)  COMMENT '备注',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='支付表';


#--------------------p2p_account--------------------
create database p2p_account default character set utf8 collate utf8_general_ci;
use p2p_account;

create table p2p_master_acc (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '姓名',
  id_card varchar(64) NOT NULL COMMENT '身份证号码',
  cash_amt decimal(10,2) NOT NULL  COMMENT '现金金额',
  current_amt decimal(10,2) NOT NULL  COMMENT '活期金额',
  claim_amt decimal(10,2) NOT NULL  COMMENT '债权金额',
  debt_amt decimal(10,2) NOT NULL  COMMENT '债务金额',
  profit_amt decimal(10,2) NOT NULL  COMMENT '收益金额',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='主账户表';

create table p2p_cash_sub_acc (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  master_acc_id int(16) NOT NULL COMMENT '主账户主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  amount decimal(10,2) NOT NULL  COMMENT '金额',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='现金分户';

create table p2p_current_sub_acc (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  master_acc_id int(16) NOT NULL COMMENT '主账户主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  amount decimal(10,2) NOT NULL  COMMENT '金额',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='活期分户';

create table p2p_current_sub_acc_flow (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  current_sub_id int(16) NOT NULL COMMENT '活期分户主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  order_sn varchar(64) NOT NULL COMMENT '订单编号',
  amount decimal(10,2) NOT NULL  COMMENT '金额',
  remark varchar(512)  COMMENT '备注',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='活期分户流水表';

create table p2p_claim_sub_acc (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  master_acc_id int(16) NOT NULL COMMENT '主账户主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  amount decimal(10,2) NOT NULL  COMMENT '金额',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='债权分户';


create table p2p_claim_sub_acc_flow (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  claim_sub_id int(16) NOT NULL COMMENT '债权分户主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  order_sn varchar(64) NOT NULL COMMENT '订单编号',
  amount decimal(10,2) NOT NULL  COMMENT '金额',
  remark varchar(512)  COMMENT '备注',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='债权分户流水表';

create table p2p_debt_sub_acc (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  master_acc_id int(16) NOT NULL COMMENT '主账户主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  amount decimal(10,2) NOT NULL  COMMENT '金额',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='债务分户';


create table p2p_debt_sub_acc_flow (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  debt_sub_id int(16) NOT NULL COMMENT '债务分户主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  order_sn varchar(64) NOT NULL COMMENT '订单编号',
  amount decimal(10,2) NOT NULL  COMMENT '金额',
  remark varchar(512)  COMMENT '备注',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='债务分户流水表';

create table p2p_profit_sub_acc (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  master_acc_id int(16) NOT NULL COMMENT '主账户主键',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  biz_id varchar(64) NOT NULL COMMENT '业务编号',
  amount decimal(10,2) NOT NULL  COMMENT '金额',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收益分户';


#--------------------p2p_match--------------------
create database p2p_match default character set utf8 collate utf8_general_ci;
use p2p_match;

create table p2p_invest_match_req (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  invest_customer_id int(16) NOT NULL COMMENT '融资客户编号',
  invest_customer_name varchar(64) NOT NULL COMMENT '融资客户姓名',
  invest_biz_id varchar(64) NOT NULL COMMENT '业务编号',
  invest_order_sn varchar(64) NOT NULL COMMENT '订单编号',
  invest_amt decimal(10,2) NOT NULL COMMENT '金额',
  wait_amt decimal(10,2) NOT NULL COMMENT '待撮合金额',
  product_id int(16) NOT NULL COMMENT '产品编号',
  product_name varchar(64) NOT NULL COMMENT '产品名称',
  year_irr decimal(6,4) NOT NULL COMMENT '年化收益率',
  level int(2) NOT NULL COMMENT '优先级:值越大优先级越高',
  remark varchar(512) NOT NULL COMMENT '备注',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='投资撮合请求';

create table p2p_finance_match_req (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  finance_customer_id int(16) NOT NULL COMMENT '融资客户编号',
  finance_customer_name varchar(64) NOT NULL COMMENT '融资客户姓名',
  finance_biz_id varchar(64) NOT NULL COMMENT '融资业务编号:借款编号/转让协议编号',
  finance_ext_biz_id varchar(64) NOT NULL COMMENT '融资扩展业务编号:借款编号/转让投资编号',
  finance_order_sn varchar(64) NOT NULL COMMENT '融资订单编号:借款编号/转让协议明细编号',
  finance_amt decimal(10,2) NOT NULL COMMENT '融资金额:借款金额/转让协议明细金额',
  wait_amt decimal(10,2) NOT NULL COMMENT '待撮合金额',
  level int(2) NOT NULL COMMENT '优先级:值越大优先级越高',
  borrow_product_id int(16) NOT NULL COMMENT '借款产品编号',
  borrow_product_name varchar(64) NOT NULL COMMENT '借款产品名称',
  borrow_year_rate decimal(6,4) NOT NULL COMMENT '贷款年利率',
  remark varchar(512) NOT NULL COMMENT '备注:借款/转让',
  type int(1) NOT NULL COMMENT '融资撮合类型：1-借款，2-转让',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='融资撮合请求:借款/转让';

create table p2p_finance_match_res (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  invest_match_id int(16) NOT NULL COMMENT '投资撮合编号',
  finance_match_id int(16) NOT NULL COMMENT '融资撮合编号',
  invest_biz_id varchar(64) NOT NULL COMMENT '投资业务编号',
  finance_biz_id varchar(64) NOT NULL COMMENT '融资业务编号:借款编号/转让协议编号',
  finance_ext_biz_id varchar(64) NOT NULL COMMENT '融资扩展业务编号:借款编号/转让投资编号',
  invest_order_sn varchar(64) NOT NULL COMMENT '投资订单编号',
  finance_order_sn varchar(64) NOT NULL COMMENT '融资订单编号:借款编号/转让协议明细编号',
  invest_customer_id int(16) NOT NULL COMMENT '融资客户编号',
  invest_customer_name varchar(64) NOT NULL COMMENT '融资客户姓名',
  finance_customer_id int(16) NOT NULL COMMENT '融资客户编号',
  finance_customer_name varchar(64) NOT NULL COMMENT '融资客户姓名',
  trade_amt decimal(10,2) NOT NULL COMMENT '交易金额',
  finance_amt decimal(10,2) NOT NULL COMMENT '融资金额:借款金额/转让协议明细金额',
  match_share decimal(4,2) NOT NULL COMMENT '撮合比例:交易金额/融资金额',
  borrow_product_id int(16) NOT NULL COMMENT '借款产品编号',
  borrow_year_rate decimal(6,4) NOT NULL COMMENT '贷款年利率',
  borrow_product_name varchar(64) NOT NULL COMMENT '借款产品名称',
  remark varchar(512) COMMENT '备注',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='融资撮合结果:借款/转让';


#--------------------p2p_borrow--------------------
create database p2p_borrow default character set utf8 collate utf8_general_ci;
use p2p_borrow;

create table p2p_borrow_product (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  name varchar(64) NOT NULL COMMENT '借款产品名称:学生培训贷，惠农贷，工薪贷，社保贷',
  month_fee_rate decimal(4,2)  NOT NULL COMMENT '月费率(扩大100倍)=月利率+月管理费率',
  month_rate decimal(4,2) NOT NULL COMMENT '月利率(扩大100倍)',
  month_manage_rate decimal(4,2) NOT NULL COMMENT '月管理费率(扩大100倍)',
  loan_day int(4) NOT NULL COMMENT '最快放款天数',
  plan_type int(1) NOT NULL  DEFAULT 1 COMMENT '还款计划生成方式:1-等额本息,2-等额本金,3-等本等息',
  remark varchar(512) COMMENT '备注',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='借款产品表';

create table p2p_borrow (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  borrow_product_id int(16) NOT NULL COMMENT '借款产品编号',
  borrow_product_name varchar(64) NOT NULL COMMENT '借款产品名称',
  month_fee_rate decimal(4,2)  NOT NULL COMMENT '月费率(扩大100倍)=月利率+月管理费率',
  month_rate decimal(4,2) NOT NULL COMMENT '月利率(扩大100倍)',
  month_manage_rate decimal(4,2) NOT NULL COMMENT '月管理费率(扩大100倍)',
  customer_bank_id int(16) NOT NULL COMMENT '客户银行卡编号',
  bank_code varchar(64) NOT NULL COMMENT '银行编号',
  base_bank_name varchar(64) NOT NULL COMMENT '银行总行名称',
  bank_account varchar(64) NOT NULL COMMENT '银行卡号',
  phone varchar(32) NOT NULL COMMENT '绑定手机号',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '客户姓名',
  customer_id_card varchar(64) NOT NULL COMMENT '客户身份证号码',
  borrow_amt decimal(10,2) NOT NULL COMMENT '借款金额',
  borrow_month_count int(2) NOT NULL COMMENT '借款期限',
  year_rate decimal(6,4) NOT NULL COMMENT '贷款年利率',
  total_borrow_fee decimal(10,2) NOT NULL COMMENT '总借款费用=总利息+总管理费=月供*借款月数-借款金额',
  total_interest decimal(10,2) NOT NULL COMMENT '总利息',
  total_manage_fee decimal(10,2) NOT NULL COMMENT '总管理费',
  start_date date NOT NULL COMMENT '借款开始日期',
  end_date date COMMENT '借款结束日期',
  first_return_date date NOT NULL comment '首期还款日期',
  month_return_day int(2) NOT NULL comment '月还款日15/28',
  month_payment decimal(10,2) NOT NULL COMMENT '月供=月本息+月管理费=[借款金额×月利率×(1+月利率)^借款月数]÷[(1+月利率)^借款月数-1]',
  month_principal_interest decimal(10,2) NOT NULL COMMENT '月本息',
  month_manage_fee decimal(10,2) NOT NULL COMMENT '月管理费',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='借款表';

create table p2p_borrow_dtl (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  borrow_id int(16) NOT NULL COMMENT '借款编号',
  invest_biz_id varchar(64) NOT NULL COMMENT '投资业务编号',
  invest_order_sn varchar(64) NOT NULL COMMENT '投资订单编号',
  trade_amt decimal(10,2) NOT NULL COMMENT '交易金额',
  match_share decimal(4,2) NOT NULL COMMENT '撮合比例:交易金额/融资金额',
  customer_id int(16) NOT NULL COMMENT '客户编号',
  customer_name varchar(64) NOT NULL COMMENT '客户姓名',
  customer_id_card varchar(64) NOT NULL COMMENT '客户身份证号码',
  customer_bank_id int(16) NOT NULL COMMENT '客户银行编号',
  bank_code varchar(64) NOT NULL COMMENT '银行编号',
  base_bank_name varchar(64) NOT NULL COMMENT '银行总行名称',
  bank_account varchar(64) NOT NULL COMMENT '银行卡号',
  phone varchar(32) NOT NULL COMMENT '绑定手机号',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='借款明细表';

create table p2p_cash_flow (
  id int(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  borrow_id int(16) unsigned NOT NULL COMMENT '借款编号',
  return_date_no int(2) NOT NULL COMMENT '还款日期编号',
  trade_date date NOT NULL COMMENT '交易日期：借款日期/还款日期',
  month_payment decimal(10,2) NOT NULL COMMENT '月供=月本息+月管理费',
  principal decimal(10,2) NOT NULL COMMENT '月本金',
  interest decimal(10,2) NOT NULL COMMENT '月利息',
  manage_fee decimal(10,2) NOT NULL COMMENT '月管理费',
  remain_principal decimal(10,2) NOT NULL COMMENT '剩余本金',
  paid_principal decimal(10,2) NOT NULL COMMENT '已还本金',
  create_time datetime NOT NULL COMMENT '创建时间',
  update_time datetime NOT NULL COMMENT '修改时间',
  creator int(16) NOT NULL COMMENT '创建人',
  reviser int(16) NOT NULL COMMENT '修改人',
  logic_state varchar(1) NOT NULL DEFAULT '1' comment '逻辑状态（暂未使用）： 1-有效，0-无效',
  biz_state  varchar(4) NOT NULL DEFAULT '1' comment '业务状态：1-新增',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='现金流表';


#--------------------初始化数据SQL--------------------
INSERT INTO p2p_invest.p2p_invest_product VALUES (1, '季度投', 5000.00, 0.05, '1', 90, '2020-4-6 17:21:20', '2020-4-6 17:21:22', 11, 11,'1','1');
INSERT INTO p2p_invest.p2p_invest_product VALUES (2, '双季投', 8000.00, 0.06, '1', 180, '2020-4-6 17:22:16', '2020-4-6 17:22:20', 11, 11,'1','1');
INSERT INTO p2p_invest.p2p_invest_product VALUES (3, '投资宝', 10000.00, 0.08,'2', 365, '2020-4-6 17:23:00', '2020-4-6 17:23:08', 11, 11,'1','1');

INSERT INTO p2p_payment.p2p_payment_base_bank VALUES (1, '中国银行', '11011', '2020-4-8 14:42:09', '2020-4-8 14:42:09', 11, 11,'1','1');
INSERT INTO p2p_payment.p2p_payment_base_bank VALUES (2, '农业银行', '11021', '2020-4-8 14:42:09', '2020-4-8 14:42:09', 11, 11,'1','1');
INSERT INTO p2p_payment.p2p_payment_base_bank VALUES (3, '工业银行', '11022', '2020-4-8 14:42:09', '2020-4-8 14:42:09', 11, 11,'1','1');
INSERT INTO p2p_payment.p2p_payment_base_bank VALUES (4, '建设银行', '11033', '2020-4-8 14:42:09', '2020-4-8 14:42:09', 11, 11,'1','1');
INSERT INTO p2p_payment.p2p_payment_base_bank VALUES (5, '北京银行', '21201', '2020-4-8 14:42:09', '2020-4-8 14:42:09', 11, 11,'1','1');
INSERT INTO p2p_payment.p2p_payment_base_bank VALUES (6, '招商银行', '12021', '2020-4-8 14:42:09', '2020-4-8 14:42:09', 11, 11,'1','1');

INSERT INTO p2p_borrow.p2p_borrow_product VALUES (1, '学生培训贷', 1.02, 0.57, 0.45, 3,1, '学生证和培训证明', '2020-4-26 17:35:41', '2020-4-26 17:35:44', 12, 12, '1', '1');
INSERT INTO p2p_borrow.p2p_borrow_product VALUES (2, '惠农贷', 1.20, 0.60, 0.60, 5, 1,'同村五位农户担保', '2020-3-16 17:37:00', '2020-3-16 17:37:00', 12, 12, '1', '1');
INSERT INTO p2p_borrow.p2p_borrow_product VALUES (3, '工薪贷', 1.34, 0.69, 0.65, 7, 1,'6个月工资流水，工作证明材料', '2020-3-16 17:37:00', '2020-3-16 17:37:00', 12, 12, '1', '1');
INSERT INTO p2p_borrow.p2p_borrow_product VALUES (4, '社保贷', 1.46, 0.75, 0.71, 10, 1,'连续1年社保流水', '2020-3-16 17:37:00', '2020-3-16 17:37:00', 12, 12, '1', '1');

INSERT INTO p2p_crm.p2p_customer VALUES (1, '李鹏', '1', '1985-7-18', '220112198507172211', '2020-3-29 10:44:54', '2020-3-17 10:44:54', 12, 12,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (2, '王玲', '0', '1990-3-29', '151112198507172211', '2020-3-10 13:00:28', '2020-3-5 13:00:35', 12, 13,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (3, '李雨', '1', '2004-6-1', '152112198507173211', '2019-2-5 13:01:21', '2020-1-5 13:01:21', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (4, '赵四', '1', '1988-11-6', '432236222343242423', '2020-3-16 15:36:57', '2020-3-11 15:37:01', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (5, '李珑', '0', '1985-7-4', '624353453535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (6, '李小鹏', '1', '1985-7-18', '233112198507172211', '2020-3-29 10:44:54', '2020-3-17 10:44:54', 12, 12,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (7, '王小玲', '0', '1990-3-29', '150112198507172211', '2020-3-10 13:00:28', '2020-3-5 13:00:35', 12, 13,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (8, '李小雨', '1', '2004-6-1', '152112198507172811', '2019-2-5 13:01:21', '2020-1-5 13:01:21', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (9, '赵小四', '1', '1988-11-6', '43436222343242423', '2020-3-16 15:36:57', '2020-3-11 15:37:01', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (10,'李小珑', '0', '1985-7-4', '324353453535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (11, '李霄鹏', '1', '1985-7-18', '215112198507172211', '2020-3-29 10:44:54', '2020-3-17 10:44:54', 12, 12,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (12, '王霄玲', '0', '1990-3-29', '152112198607174211', '2020-3-10 13:00:28', '2020-3-5 13:00:35', 12, 13,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (13, '李霄雨', '2', '2004-6-1', '152112198507174211', '2019-2-5 13:01:21', '2020-1-5 13:01:21', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (14, '赵霄四', '1', '1988-11-6', '431236222343242423', '2020-3-16 15:36:57', '2020-3-11 15:37:01', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (15, '李霄珑', '0', '1985-7-4', '129953453535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (16, '姚霄鹏', '1', '1985-7-18', '210112198507172211', '2020-3-29 10:44:54', '2020-3-17 10:44:54', 12, 12,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (17, '钱霄玲', '0', '1990-3-29', '154112198507172211', '2020-3-10 13:00:28', '2020-3-5 13:00:35', 12, 13,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (18, '孙霄雨', '1', '2004-6-1', '152112198507272211', '2019-2-5 13:01:21', '2020-1-5 13:01:21', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (19, '周霄四', '1', '1988-11-6', '432736222343242423', '2020-3-16 15:36:57', '2020-3-11 15:37:01', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (20, '吴霄珑', '0', '1985-7-4', '156353453535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11,'1','1');
INSERT INTO p2p_crm.p2p_customer VALUES (21, '郑霄珑', '0', '1985-7-4', '123435353535234243', '2019-1-7 15:38:06', '2019-1-7 15:38:06', 11, 11,'1','1');

INSERT INTO p2p_account.p2p_master_acc VALUES (1, 1, '李鹏', '220112198507172211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (2, 2, '王玲', '151112198507172211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (3, 3, '李雨', '152112198507173211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (4, 4, '赵四', '432236222343242423', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (5, 5, '李珑', '624353453535234243', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (6, 6, '李小鹏', '233112198507172211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (7, 7, '王小玲', '150112198507172211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (8, 8, '李小雨', '152112198507172811', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (9, 9, '赵小四', '43436222343242423', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (10, 10, '李小珑', '324353453535234243', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (11, 11, '李霄鹏', '215112198507172211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (12, 12, '王霄玲', '152112198607174211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (13, 13, '李霄雨', '152112198507174211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (14, 14, '赵霄四', '431236222343242423', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (15, 15, '李霄珑', '129953453535234243', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (16, 16, '姚霄鹏', '210112198507172211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (17, 17, '钱霄玲', '154112198507172211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (18, 18, '孙霄雨', '152112198507272211', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (19, 19, '周霄四', '432736222343242423', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (20, 20, '吴霄珑', '156353453535234243', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');
INSERT INTO p2p_account.p2p_master_acc VALUES (21, 21, '郑霄珑', '123435353535234243', 0.00, 0.00, 0.00, 0.00, 0.00, '2020-4-25 00:00:00', '2020-4-24 00:00:00', 12, 12, '1', '1');

INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('1', '1', '11021', '农业银行', '4363421', '13235122351', '2020-05-16 10:05:19', '2020-05-16 10:05:19', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('2', '2', '11022', '工业银行', '435123324235436416', '66164123235', '2020-05-16 10:06:01', '2020-05-16 10:06:01', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('3', '3', '11022', '工业银行', '43514256456546', '15652423428', '2020-05-16 10:06:40', '2020-05-16 10:06:40', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('4', '4', '11033', '建设银行', '453451547567567', '16222323234', '2020-05-16 10:07:36', '2020-05-16 10:07:36', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('5', '21', '12021', '招商银行', '45742342367567', '15754457745', '2020-05-16 10:08:27', '2020-05-16 10:08:27', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('6', '20', '11021', '农业银行', '345311432425435', '13412345341', '2020-05-25 10:58:15', '2020-05-25 10:58:15', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('7', '5', '11022', '工业银行', '6623124436561234000', '15122454543', '2020-05-25 10:59:02', '2020-05-25 10:59:02', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('8', '6', '11022', '工业银行', '82573453643536540', '15222342888', '2020-05-25 11:00:29', '2020-05-25 11:00:29', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('9', '19', '11021', '农业银行', '673412536452342400', '17723472452', '2020-05-25 11:03:24', '2020-05-25 11:03:24', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('10', '18', '12021', '招商银行', '75231267734523', '17712343372', '2020-05-25 11:12:29', '2020-05-25 11:12:29', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('11', '7', '11022', '工业银行', '61236783457657', '17234126733', '2020-05-25 11:13:23', '2020-05-25 11:13:23', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('12', '17', '11021', '农业银行', '6895481789664985', '17763232341', '2020-05-25 11:14:13', '2020-05-25 11:14:13', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('13', '16', '11022', '工业银行', '823424474671536500', '18522342341', '2020-05-25 11:15:28', '2020-05-25 11:15:28', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('14', '8', '11033', '建设银行', '82365568324242', '17723261244', '2020-05-25 11:16:33', '2020-05-25 11:16:33', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('15', '9', '11022', '工业银行', '843245475672432400', '17573457642', '2020-05-25 11:20:17', '2020-05-25 11:20:17', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('16', '15', '11021', '农业银行', '8234221231435435000', '16677231232', '2020-05-25 11:21:03', '2020-05-25 11:21:03', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('17', '10', '11033', '建设银行', '934521235464563600', '18534242267', '2020-05-25 11:22:10', '2020-05-25 11:22:10', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('18', '14', '21201', '北京银行', '5203434547242342', '14534456452', '2020-05-25 11:22:51', '2020-05-25 11:22:51', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('19', '11', '11022', '工业银行', '512312412411241200', '15512315231', '2020-05-25 11:24:36', '2020-05-25 11:24:36', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('20', '12', '11022', '工业银行', '72312436336123120', '16333523423', '2020-05-25 11:28:01', '2020-05-25 11:28:01', '12', '12', '1', '1');
INSERT INTO p2p_payment.p2p_payment_customer_bank (id, customer_id, bank_code, base_bank_name, bank_account, phone, create_time, update_time, creator, reviser, logic_state, biz_state) VALUES ('21', '13', '11021', '农业银行', '6123436131123', '14662342365', '2020-05-25 11:29:08', '2020-05-25 11:29:08', '12', '12', '1', '1');

#清空数据SQL
