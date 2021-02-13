package com.lisz.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

/*
不结合eureka，就是定义一个client名字。就用url属性置顶服务器列表。url="http://ip:port/"
Feign能够识别"/alive"并且拼到"http://user-provider"后面
一旦调用到下面的抽象方法，则拦截下来，得到注解中的url和api，然后拼接在一起，再用RestTemplate发送http请求，并拿到返回结果
Dubbo也是这样的原理，只不过用的是socket，而这里用的是http 
 */
@FeignClient(name = "ooxx", url = "http://192.168.1.102:82")
@Service
public interface UserConsumerService{
	@GetMapping("/alive")
	public String alive();

	@GetMapping("/register")
	public String register();
}
