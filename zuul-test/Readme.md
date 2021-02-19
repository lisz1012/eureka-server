# Zuul 网关服务的配置方法

## pom.xml文件 
引入SpringCloud、zuul、eureka-client的依赖
这是因为zuul服务要向eureka注册。别忘了写 dependencyManagement 标签

## 配置文件 application.yml
配置应用名、端口和eureka集群的各台机器的url

## 启动类
在启动类上面加上@EnableZuulProxy注解  
注：一般来说新的模块的加入都要在这三者上面做修改

## 验证
启动两台Eureka机器、user-provider、user-consumer和zuul-test服务（4个服务6个进程）
访问：`http://192.168.1.102/user-consumer/helloZuul` 其实是浏览器通过zuul访问到了user-consumer的helloZuul这个API，而后者又通过他
这里的ribbon（客户端的LB）调用了user-provider的helloZuul。所以可以观察到负载均衡饿的轮询效果。Zuul作为Eureka的客户端，除了
user-consumer，也有user-provider的服务列表，所以也可以通过zuul，不经过user-consumer，直接访问user-provider：
http://192.168.1.102/user-provider/user/helloZuul （注意：这里由于user-provider中的UserController继承了UserApi，而后者有注解：
@RequestMapping("/user")，所以访问的时候要把中间的"/user"带上）  

Zuul后面的各个服务都能以`http://zuul网关地址/服务名/API`的方式访问到