package com.lisz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//老版本这里需要加注解EnableEureka，新版本不需要，只需要在创建项目的时候，勾选Eureka Discovery Client 选项即可.启动服务，会发现打印如下信息：
/*
2021-02-06 22:02:26.350  INFO 35370 --- [           main] com.netflix.discovery.DiscoveryClient    : Saw local status change event StatusChangeEvent [timestamp=1612677746350, current=UP, previous=STARTING]
2021-02-06 22:02:26.351  INFO 35370 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_PROVIDER/192.168.1.102:provider:80: registering service...
2021-02-06 22:02:26.376  INFO 35370 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 80 (http) with context path ''
2021-02-06 22:02:26.376  INFO 35370 --- [nfoReplicator-0] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_PROVIDER/192.168.1.102:provider:80 - registration status: 204
2021-02-06 22:02:26.377  INFO 35370 --- [           main] .s.c.n.e.s.EurekaAutoServiceRegistration : Updating port to 80
2021-02-06 22:02:26.386  INFO 35370 --- [           main] com.lisz.ProviderApplication             : Started ProviderApplication in 1.678 seconds (JVM running for 2.239)

就是靠服务名provider来找服务。Eureka可以跨平台，只要遵循Rest，都可以注册和拉取到服务列表
http://localhost:7900/eureka/apps 可以用来获取服务列表
http://localhost:7900/eureka/apps/provider 可以用来得到某个特定服务的信息
http://localhost:7900/eureka/instances/192.168.1.102:provider:80 可以用来得到instance的信息
http://localhost:7900/eureka/status 查看元数据信息
请求头里面加上键值对 Accept:application/json
 */
@SpringBootApplication
public class ProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProviderApplication.class, args);
	}

}
