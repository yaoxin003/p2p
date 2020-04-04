# p2p
互联网金融点对点借贷平台


系统环境
开发工具：IntelliJ IDEA
Java版本：JDK1.8.0_131
服务器：Tomcat8.5.15
数据库：MySQL5.1.42
系统技术：SpringBoot1.4.7+Mybatis3.5.3+thymeleaf1.4.7+EasyUI1.7.0+JQuery1.12.4，
Dubbo2.6.0+Zookeeper3.4.8
Redis+Elastic Search
Nginx+RocketMQ

Dubbo：对方及时作出相应。
MQ:可用于对消息实时性要求不高的场景。
dubbo控制台：
    war包路径：/usr/local/bin/dubbo-admin-2.6.0
    控制台地址：http://192.168.1.121:8080/dubbo-admin/
    控制台账户：root root
    tomcat位置：/usr/local/apache-tomcat-9.0.27

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
p2p-invest：投资管理，端口：9084
p2p-match：撮合管理，端口：9085
p2p-borrow-sale：借款销售管理，端口：9086
p2p-borrow：借款管理，端口：9087
p2p-pay：支付管理，端口：9088
p2p-account：账户管理，端口：9089


ms:微服务代码文件夹
：新投资服务
：投资增值服务
：回款再投服务
：债权转让服务
：统计分析服务
invest-bill：账单服务
invest-timer：定时任务服务