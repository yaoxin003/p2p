# p2p
互联网金融个人对个人借贷平台


#--------------------系统环境--------------------
开发工具：IntelliJ IDEA
Java版本：JDK1.8.0_131
服务器：Tomcat8.5.15
数据库：MySQL5.1.42
系统技术：
SpringBoot2.1.0.RELEASE+Mybatis3.5.3+thymeleaf1.4.7+EasyUI1.7.0+JQuery1.12.4
Dubbo2.6.0+Zookeeper3.4.8
MySQL5.6+Mycat1.6.7
Redis4.0.14+RocketMQ4.5.1
#ElasticSearch6.3.1+Nginx3.6.3+MongoDB3.4
SHA加签验签，RSA非对称加密解密


#--------------------linux环境下服务安装路径和启动方法--------------------
Dubbo：对方及时作出相应
MQ：可用于对消息实时性要求不高的场景
Dubbo控制台：
    war包路径：/usr/local/bin/dubbo-admin-2.6.0
    启动方式：cd /usr/local/apache-tomcat-9.0.27/
                bin/startup.sh
    控制台地址：http://192.168.1.121:7081/dubbo-admin/
    控制台账户：root root
    tomcat位置：/usr/local/apache-tomcat-9.0.27
Zookeeper：
 启动方式：cd /usr/local/zookeeper-3.4.11/bin
                ./zkServer.sh start
Redis-4.0.14
    路径：cd /etc/redis/
    启动方式：redis-server ./redis6379.conf
RocketMQ4.5.1（NameSrv和Broker）
    路径：cd /usr/local/rocketmq-all-4.4.0-bin-release
    启动NameSrv方式：nohup sh mqnamesrv > nohup_namesrv.out &
    启动Broker方式：nohup sh mqbroker -n 192.168.1.121:9876 > nohup_broker.out &
    控制台路径：cd /usr/local/rocketmq-externals-master/rocketmq-console/target
    后台启动方式：nohup java -jar rocketmq-console-ng-1.0.1.jar > nohup_rocketmq.out &
    控制台地址：http://192.168.1.121:7082
启动Mycat
    路径：cd /usr/local/mycat/bin
    启动：./mycat start
#启动mongodb
#     路径：cd /usr/local/mongodb-linux-x86_64-3.4.18/bin/
#     启动: ./mongod -f mdb.conf


#--------------------服务名称和端口号--------------------
ds:分布式代码文件夹
p2p-api：model,vo和接口
p2p-parent：
p2p-common-util：
p2p-service-util：
p2p-web-util：
p2p-manage：后台管理，端口：9081
http://localhost:9081/
p2p-crm：客户关系管理，端口：9082
http://localhost:9082/crm/init
p2p-invest-sale：投资销售管理，端口：9083
http://localhost:9083/crm/init
p2p-invest：投资管理，端口：9084
p2p-match：撮合管理，端口：9085
p2p-borrow-sale：借款销售管理，端口：9086
p2p-borrow：借款管理，端口：9087
p2p-payment：支付管理，端口：9088
p2p-account：账户管理，端口：9089
p2p-timer：定时任务：端口：9090

ms:微服务代码文件夹
：新投资服务
：投资增值服务
：回款再投服务
：债权转让服务
：统计分析服务

invest-bill：账单服务

p2p-timer：定时任务


#--------------------相对亮点--------------------
使用RocketMQ事务消息保证生产者不丢失消息
使用RocketMQ最大努力通知


#--------------------缓存key规则--------------------
key=crm_info_idcard_*** value=json(crm对象)


#--------------------待开发功能--------------------
    0、使用RocketMQ最大努力通知
    1.上传附件
    2.账单


#--------------------功能列表--------------------
一.p2p-manage
1.客户管理（数据库+缓存）
添加，修改，删除，查询，分页列表

四.p2p-invest
1.理财产品管理：
2.添加投资，收益计算（前后台）
3.投资充值
    实现：投资充值（RocketMQ服务）MQInvestPayListener
4.出借单管理
5.放款通知
    实现： 借款放款（RocketMQ服务）MQLoanPayListener
6.投资转让申请
计算转让费用：
    转让金额
    加急费
    折扣费
    服务费
    赎回金额
7.投资转让交割
    实现：债权转让交割（RocketMQ服务）MQChangeClaimListener

五.p2p-match
1.投资撮合
    实现：投资撮合（RocketMQ服务）MQInvestMatchReqListener
2.放款通知
    实现：借款放款（RocketMQ服务）MQLoanPayListener
3.转让撮合
    实现：转让撮合（RocketMQ服务）MQTransferMatchReqListener
七.p2p-borrow
1.借款
计算月供、计算借款费用、借款结束日期（15/28）
2.使用线程池批量插入：还款计划、债务每日价值
3.借款签约
4.申请放款
5.放款通知
    实现： 借款放款（RocketMQ服务）MQLoanPayListener

八.p2p-payment
1.银行管理：
添加银行，银行列表
2.模拟第三方支付
加签加密
3.支付管理
1.添加，2.接收第三方支付结果通知（MQ Listener）

九.p2p-account
1.开户：添加主账户
    实现：开户（RocketMQ服务）MQOpenAccountListener
2.充值：投资人：活期户加
    实现：投资充值（RocketMQ服务）MQInvestPayListener
3.放款：借款人：现金户加，债务户加；投资人：活期户减，债权户加
    实现： 借款放款（RocketMQ服务）MQLoanPayListener
4.债务增值：借款人：债务户加
5.债务还款：借款人：债务户减
6.投资增值：投资人：债权户加
7.投资回款：投资人：活期户加，债权户减
8.投资转让：转让人：现金户加，债权户减；受让人：活期户减，债权户加
    实现：债权转让交割（RocketMQ服务）MQChangeClaimListener
9.提现：现金户减

十.p2p-timer：定时任务
计算债务每日价值：
    1.分页查询债务每日价值
    2.债务增值记账，债务还款记账
计算投资每日价值：
    1.分页查询债务每日价值
    2.查询投资债权，插入投资债权价值明细
    3.插入投资债权价值
    4.投资增值记账， 投资回款记账
    5.投资债权价值的回款大于0，添加回款出借单，发送回款撮合数据
借款人还款支付
    1.分页查询还款日现金流
    2.发送事务消息MQ：借款人还款支付
    3.更新现金流状态

#--------------------业务描述--------------------
二.p2p-crm（数据库+缓存）
1.添加客户：使用身份证号查询客户是否存在，不存在向数据库添加数据，同时加入缓存中。
2.修改客户：1.使用新身份证号查询客户（缓存和数据库）是否存在，
2.存在，不处理；3.不存在，3.1删除旧缓存，3.2修改数据库数据，3.3查询数据库（获得全部字段），加入缓存中。

四.p2p-invest
1.理财产品管理：
2.添加投资，收益计算（前后台）
3.投资充值
    实现：投资充值（RocketMQ服务）MQInvestPayListener
4.出借单管理
5.放款通知（RocketMQ服务）
5.1.获得借款撮合结果
5.2.插入投资债权明细、投资债权明细历史
5.3.验证出借单是否满额：
        若出借单金额和债权金额相等，则更新出借单状态为满额；
        若出借单类型为新出借单，则更新投资状态为满额。
6.投资转让申请
6.1.查询投资债权关系
6.2.查询投资债权价值明细
6.3.更新投资状态为转让中
6.4.计算转让费用：
    转让金额=现金+债权
    加急费：暂未开发
    折扣费：投资类型为固定期限则“转让金额-投资金额-收益金额”；投资类型为非固定期限则折扣费为0。
    服务费：投资类型为固定期限则为0；投资类型为非固定期限则“客户投资总金额计算，<10万为转让金额2%；>=10万且<100万为转让金额1.5%,>=100万为1转让金额%”
    赎回金额：投资类型为固定期限则“投资金额+收益金额”；投资类型为非固定期限则“转让金额-加急费-服务费”
6.5.添加转让协议和转让协议售前数据
6.6.发送转让撮合
7.投资转让交割（RocketMQ服务）
7.1.添加转让协议明细
7.2.变更投资持有债权关系（删除：转让投资的持有债权，添加：受让投资的持有债权）
7.3.插入投资持有债权明细历史
7.4.更新转让协议状态为转让成功
7.5.更新投资状态为转让成功
7.6.验证投资满额出借：若满额则更新投资状态为满额，出借单状态为满额

五.p2p-match
1.投资撮合
    实现：投资撮合（RocketMQ服务）MQInvestMatchReqListener
2.放款通知
    实现：借款放款（RocketMQ服务）MQLoanPayListener
3.转让撮合
    实现：转让撮合（RocketMQ服务）MQTransferMatchReqListener

七.p2p-borrow
1.计算月供:月供=[借款金额×月利率×(1+月利率)^借款月数]÷[(1+月利率)^借款月数-1]
借款费用=月供*借款月数-借款金额
借款结束日期：
借款开始日期1-15日则还款日为“借款开始日期”的28日，
借款开始日期16-31则还款日为“借款开始日期”下月的15日
2.生成还款计划
    生成等额本息（已知条件：借款金额，借款利率，月供，借款期数，首期还款日期，还款日）
3.借款签约
    添加借款、现金流（Mycat）、债务每日价值（Mycat）
    发送【借款撮合】Dubbo服务
    添加借款明细
    更新借款状态（已签约）
4.申请放款
    调用【payment】放款（Dubbo服务）
    调用放款通知（RocketMQ客户端）
5.放款通知（RocketMQ服务）
    更新借款状态（借款成功）
    实现： 借款放款（RocketMQ服务）MQLoanPayListener
八.p2p-payment
1.接收第三方支付结果通知（MQ Listener）
    解密验签：SHA加签验签，RSA非对称加密解密
    更新支付单状态
    发送支付结果到MQ（给Invest和Account系统）
2.支付管理
    付款：借款人放款√，投资人提现×
    收款：投资人充值√，借款人回款×

九.p2p-account
1.开户：添加主账户
    实现：开户（RocketMQ服务）MQOpenAccountListener
2.充值：投资人：活期户加
    2.1.更新主账户的活期金额，插入活期分户，插入活期分户流水
    实现：投资充值（RocketMQ服务）MQInvestPayListener
3.放款通知（RocketMQ服务）
    放款：借款人：现金户加，债务户加；投资人：活期户减，债权户加
    3.1.借款人：
    更新主账户的现金金额，插入现金分户，插入现金分户流水
    更新主账户的债务金额，插入债务分户，插入债务分户流水
    3.2.投资人：
    更新主账户的活期金额（负数），更新活期分户（负数），插入活期分户流水（负数）
    更新主账户的债权金额，插入债权分户，插入债权分户流水
    实现： 借款放款（RocketMQ服务）MQLoanPayListener
4.债务增值：借款人：债务户加
    更新主账户的债权金额，更新债务分户，插入债务分户流水
5.债务还款：借款人：债务户减
    更新主账户的债务金额（负数），更新债务分户（负数），插入债务分户流水（负数）
6.投资增值：投资人：债权户加
    更新主账户的债权金额，更新债权分户，插入债权分户流水
7.投资回款：投资人：活期户加，债权户减
     更新主账户的活期金额，更新活期分户，插入活期分户流水   
     更新主账户的债权金额（负数），更新债权分户（负数），插入债权分户流水（负数）
8.投资转让交割（RocketMQ服务）
    投资转让：转让人：现金户加，债权户减；受让人：活期户减，债权户加
    8.1.转让人：
    更新主账户的现金金额，插入现金分户，插入现金分户流水
    更新主账户的债权金额（负数），更新债权分户（负数），插入债权分户流水（负数）
    8.2.受让人：
    更新主账户的活期金额（负数），更新活期分户（负数），插入活期分户流水（负数）
    更新主账户的债权金额，插入债权分户，插入债权分户流水
    实现：债权转让交割（RocketMQ服务）MQChangeClaimListener
9.提现（放款/转让）：现金户减
    更新主账户的现金金额（负数），插入现金分户（负数），插入现金分户流水（负数）

