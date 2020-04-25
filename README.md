# p2p
互联网金融个人对个人借贷平台
#--------------------bug--------------------
2.1.修改客户信息中的身份证号码不能同步修改account系统身份证号码
2.2.删除客户信息不能删除account系统数据
3.快捷支付流程

#--------------------系统环境--------------------
开发工具：IntelliJ IDEA
Java版本：JDK1.8.0_131
服务器：Tomcat8.5.15
数据库：MySQL5.1.42
系统技术：
SpringBoot2.1.0.RELEASE+Mybatis3.5.3+thymeleaf1.4.7+EasyUI1.7.0+JQuery1.12.4，
Dubbo2.6.0+Zookeeper3.4.8
Redis4.0.14+Elastic Search
Nginx+RocketMQ4.5.1
SHA加签验签，RSA非对称加密解密

#--------------------linux环境下服务安装路径和启动方法--------------------
Dubbo：对方及时作出相应。
MQ:可用于对消息实时性要求不高的场景。
Dubbo控制台：
    war包路径：/usr/local/bin/dubbo-admin-2.6.0
    启动方式：cd /usr/local/apache-tomcat-9.0.27/
                bin/startup.sh
    控制台地址：http://192.168.1.121:7081/dubbo-admin/
    控制台账户：root root
    tomcat位置：/usr/local/apache-tomcat-9.0.27
Redis-4.0.14
    路径：cd /etc/redis/
    启动方式：redis-server ./redis6379.conf
RocketMQ4.5.1（NameSrv和Broker）
    路径：cd /usr/local/rocketmq-all-4.4.0-bin-release
    启动NameSrv方式：nohup sh mqnamesrv > nohup_namesrv.out &
    启动Broker方式：nohup sh mqbroker -n 192.168.1.121:9876 > nohup_broker.out &
    控制台路径：cd /usr/local/rocketmq-externals-master/rocketmq-console/target
    #启动方式：java -jar ./rocketmq-console-ng-1.0.1.jar
    后台启动方式：nohup java -jar ./rocketmq-console-ng-1.0.1.jar > nohup_rocketmq.out &
    控制台地址：http://192.168.1.121:7082
    

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


ms:微服务代码文件夹
：新投资服务
：投资增值服务
：回款再投服务
：债权转让服务
：统计分析服务
invest-bill：账单服务
invest-timer：定时任务服务

#--------------------缓存key规则--------------------
key=crm_info_idcard_*** value=json(crm对象)



#--------------------待开发功能--------------------
1.后台按钮：查询Crm表加入缓存，



#--------------------功能列表--------------------
一.客户管理（数据库+缓存）：
添加，修改，删除，查询，分页列表
二.理财产品管理：
列表
三.银行管理：
添加银行，银行列表
四.投资管理
添加投资，收益计算（前后台）
五.模拟第三方支付
加签加密
六.支付管理
1.添加，2.接收第三方支付结果通知（MQ Listener）
七.账户管理
开户（MQ），添加投资支付成功（MQ、事务操作）
八、出借单管理
1.添加新出借单
九.撮合管理


一.客户管理（数据库+缓存）
1.添加客户：使用身份证号查询客户是否存在，不存在向数据库添加数据，同时加入缓存中。
2.修改客户：1.使用新身份证号查询客户（缓存和数据库）是否存在，
2.存在，不处理；3.不存在，3.1删除旧缓存，3.2修改数据库数据，3.3查询数据库（获得全部字段），加入缓存中。
六.支付管理
2.接收第三方支付结果通知（MQ Listener）
解密验签
更新支付单状态
发送支付结果到MQ（给Invest和Account系统）

七.账户管理
1.开户（MQ）
验证是否已经开户
未开户则添加主账户
2.添加投资支付成功（MQ、事务操作）
活期分户流水是否插入
若未插入，累加主账户的活期金额，插入活期分户，插入活期分户流水


