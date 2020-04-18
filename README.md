# p2p
互联网金融个人对个人借贷平台
#--------------------bug--------------------
1.快捷支付流程
2.加签加密，解密验签（SHA加签验签，RSA非对称加密）


#--------------------系统环境--------------------
开发工具：IntelliJ IDEA
Java版本：JDK1.8.0_131
服务器：Tomcat8.5.15
数据库：MySQL5.1.42
系统技术：
SpringBoot1.4.7+Mybatis3.5.3+thymeleaf1.4.7+EasyUI1.7.0+JQuery1.12.4，
Dubbo2.6.0+Zookeeper3.4.8
Redis4.0.14+Elastic Search
Nginx+RocketMQ
SHA加签验签，RSA非对称加密解密

#--------------------linux环境下服务安装路径和启动方法--------------------
Dubbo：对方及时作出相应。
MQ:可用于对消息实时性要求不高的场景。
Dubbo控制台：
    war包路径：/usr/local/bin/dubbo-admin-2.6.0
    启动方式：cd /usr/local/apache-tomcat-9.0.27/
                bin/startup.sh
    控制台地址：http://192.168.1.121:8080/dubbo-admin/
    控制台账户：root root
    tomcat位置：/usr/local/apache-tomcat-9.0.27
Redis-4.0.14
    路径：cd /etc/redis/
    启动方式：redis-server ./redis6379.conf
    
    

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
1.添加客户：使用身份证号查询客户是否存在，不存在向数据库添加数据，同时加入缓存中。
2.修改客户：1.使用新身份证号查询客户（缓存和数据库）是否存在，
2.存在，不处理；3.不存在，3.1删除旧缓存，3.2修改数据库数据，3.3查询数据库（获得全部字段），加入缓存中。
