# ied_ufls低频减载项目

## 请在父工程下的pom文件进行版本控制方便日后查看升级修改

### 模块功能
* ied-ufls-common
    * 公共模块
* ied-ufls-filesend
    * 部署在三区，把二区传来的文件拼接上传到云端
    * 10s一次推送消息到kafka总线
    * 1s一次扫描response目录
* ied-ufls-mail
    * 小邮件模块，把地调数据分片从二区传送到三区
    * 10s一次发送RESPONSE目录文件，接收文件存入COMMAND
* ied-ufls-receive
    * 云上接收三区传来的数据入库
* ied-ufls-web
    * 云上服务提供者
* ied-ufls-web-common
    * 云上web公共模块service、entity
* ied-ufls-web-gateway
    * 云上服务消费者通过服务总线调用提供者的服务


### RPC调用方式
* com/kedong/ieduflswebgateway/config/ServiceLocator.java
    * 远程调用服务的全限定名
    * ServiceLocator调用

### 配置文件
* src/main/resources/BusRegistry.properties
    * 是zookeeper地址配置文件
* src/main/resources/zkClient.properties
    * zookeeper地址
* src/main/resources/application.yml Spring
    * Spring配置文件
* src/main/resources/application-data.yml
    * 读写分离配置文件
* src/main/resources/app.properties
    * 总线配置文件，有主题、RPC端口、RPC服务名、邮件服务中的IP用户名密码等


### 记录

2022.11.16日记录：

* 目前web和gateway模块框架搭建完毕并上传至git，私有项目
* git地址：https://github.com/abeleins/ied-ufls
* 对版本jar管理做了详细的分类统一在工程下的pom文件管理



