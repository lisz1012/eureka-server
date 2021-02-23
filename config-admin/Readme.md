# 搭建配置管理服务

## 搭建
0. 建立公共配置文件的Github repo，他可以自由一个根目录，在下面就是一堆配置文件，但是配置文件的文件名有规则：  
服务名-profile.yml/properties 分支的指定是各个微服务在其自己的bootstrap.yml中做的（见"其他服务对他的使用"的第2项）
1. 依赖：
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
2. 启动类上加注解：@EnableConfigServer
3. 配置文件
```
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/lisz1012/SpringCloud-Config-Center.git
      label: master
  application:
    name: config-admin
eureka:
  client:
    service-url:
      defaultZone: http://lisz:123@eureka-7900:7900/eureka/,http://lisz:123@eureka-7901:7901/eureka/
server:
  port: 9999
``` 
其中，`https://github.com/lisz1012/SpringCloud-Config-Center.git` 是真实的公共配置文件放置的项目的github地址URL。

4. 验证：
启动当前管理配置文件的微服务，查看Eureka有没有发现它，然后访问：
`http://192.168.1.102:9999/master/user-consumer.yml` 或者 `http://192.168.1.102:9999/user-consumer.yml` 这里只有一个
master分支, 如果还有个test分支，则可以通过`http://192.168.1.102:9999/test/user-consumer.yml` 取得不同的版本的配置文件

## 其他服务对他的使用
注意：以下配置是在其他的，配置管理服务的消费者（客户）中进行的
1. 依赖：
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-client</artifactId>
</dependency>
```
2. 配置文件
注意：1。本地应用如果需要拉取云端的配置，则要把以下的配置内容要放在`bootstrap.yml` `bootstrap.properties` `bootstrap*.yml`或者
`bootstrap*.properties`中，而不是名为类似application.yml的文件中，否则启动时Bean的注入会报错 BeanCreationException 2。想要给
配置管理服务做高可用，就要让当前的微服务去Eureka中寻找它，则要把eureka相关的配置也从application.yml中移到bootstrap.yml这里来。
```
eureka:
  client:
    service-url:
      defaultZone: http://lisz:123@eureka-7900:7900/eureka/,http://lisz:123$eureka-7901:7901/eureka/
spring:
  cloud:
    config:
      # 查找配置文件有两种方式：
      # 1. 直接URL/URI方式查找
      # uri: http://192.168.1.102:9999/
      # 2. 通过注册中心查找
      # 通过注册中心找, 这样config-admin服务可以多起几个实例做高可用，用这个的时候，把上面的uri注释掉
      discovery:
        service-id: config-admin
        enabled: true
      # 哪个profile的配置文件
      profile: dev
      # git上的哪个分支
      label: master
      # 配置中心还有个配置：
      # myconfig: test_abc_v1 在想用的地方做一下@Value注入就好
```
3. 代码：在想用这个配置的地方做一下依赖注入：
```
@Value("${myconfig}")
private String myconfig;
```

## 一旦配置文件在其Github中有改动
此时配置服务会感受到Github中文件的变化，但是还是需要重启各个微服务，加载一遍配置服务中已经更新的配置才可以，但这样的话，服务多了就会很麻烦。
要让各个服务自己惹更新/加载最新的配置，则要：
1. 在需要用到热更新配置的Contrller类上面加上@RefreshScope注解
2. 保证当前服务类有引入Actuator依赖：
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
3. application.yml文件里要打开Actuator：
```
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```
4. Postman发送请求：http://192.168.1.102:83/actuator/refresh (http://192.168.1.102:83 指定了那个微服务进程刷新一下配置中心的内容）
返回：
```
[
    "config.client.version",
    "user-consumer.ribbon.NFLoadBalancerRuleClassName",
    "myconfig"
]
```
这么发只是单机实现了更新。  
想更新所有服务，让最新的配置都在上面生效，要用企业级消息总线：Bus。Bus用MQ实现，MQ还必须要支持amqp协议，统一化接口标准，Rabbit和Kafka都支持。
Rabbit面向应用，Kafka面向数据中转。