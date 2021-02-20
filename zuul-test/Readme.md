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

Zuul后面的各个服务都能以`http://zuul网关地址/服务名/API`的方式访问到。 Zuul就是一个web服务，用的是Filter拦截用户请求、找服务列表，
并且向后端转发。问题：zuul也是泡在Tomcat上，性能会不会很差？谈不上差不差，还好，因为一般来说Zuul是业务网关，其前面还有个流量网关，是专门
处理高并发的。最前面的接入层可以用LVS或者nginx，为Zuul做负载。nginx、lvs对于请求的处理比Tomcat轻量级，后者每一个请求都要从头到尾看一遍，
不管是握手，还要建立会话。Tomcat接入之后会形成一个隧道模式，配置容易，但是必须是请求和响应都要经过她，而不是直接连接，好处是可以灵活处理，
可以看看URL，是不是合法访问。在zuul网关先检查一下上传file的大小，然后决定要不要往后转发请求，还有前后端分离的服务器端渲染：把后端微服务返回的
JSON组成html网页返回去。这个zuul网关可以放爬虫、SQL注入，合法爬虫有User-Agent，表示其是个搜索引擎。如何判断爬虫？同时刷一个页面，可以认为
是抢购，但是同时刷新500个，就不是抢购了。对于同一用户、同一会话，访问频次过高就是爬虫，这样就可以在网关防火墙里禁止掉。还可以做负载均衡，
也是用Ribbon。统计哪些链路负载比较高、限流、服务熔断、统一的登录（网关这里不做session共享，直接入SSO服务，取token回来，请求带着token
传到后端）、动态路由（服务上线就可以向他转发请求了）、提前服务降级... 只有隧道模式，才能有以上功能。这叫业务网关，代表就是现在讲的这个Zuul，
nginx（Kong也可以做业务网关，他也是基于nginx的）  

如何让Zuul稍微高效一点？1. 把它从有状态的变成无状态的：单点登录 2. 优化线程，异步。Zuul跟nginx的区别就是语言，他俩干的活是一模一样的。  
在一个问题：性能比不上nginx问什么还有人用？因为Zuul是业务网关，写业务逻辑非常方便，其实没多少情况对性能要求高的。微服务的调用是在干啥？
除了写就是读。吞吐量过大的应用，可以把读和写分开来做，只是让写请求通过Zuul；读请求尽量做成缓存，别让他走较为复杂的调用链路，也就是读+缓存
不在这套微服务系统里。  

最前面是lvs，然后是一组隧道模式的网关，lvs向这些业务网关服务器做负载均衡，后者再去找具体的服务。  

隧道网关：
- 分析业务请求
- 流量网关： AWF防火墙
- 业务网关：缓存文件，Html/JSON、熔断
流量网关在前面，先鉴权和防火，所有拒绝策略尽可能前置，减小性能开销 。然后架构就成了：lvs -> 流量网关 -> 业务网关