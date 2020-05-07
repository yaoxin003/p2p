# Mybatis-Reverse

Mybatis逆向工程使用步骤:

1. 在main方法中指定generatorConfig.xml配置文件的位置
2. 在generatorConfig.xml中指定如下信息
   1. 在配置文件中指定数据库的连接信息
   2. 确定生成的PO类所在包名和位置
   3. 确定生成的Mapper包名和生成的位置
   4. 确定生成的Mapper配置文件包名和位置
   5. 指定对哪些表进行逆向处理
3. 运行main方法