package com.lisz.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

/*
结合eureka, @FeignClient注解中的name属性写成对方的spring.application.name url就不写了.
这样由于url这个具体的ip:port不写了，所以负载均衡也就又能用了，而且还没有代码侵入，方便做异构系统
Java -> Java，拷接口，异构系统，写文档
 */
//@FeignClient(name = "user-provider") // 有拷包的时候，为了不影响其测试，暂时将这里注释掉, 地位相当于ConsumerApi
@Service
public interface UserConsumerService2 {
	@GetMapping("/alive")
	public String alive();

	@GetMapping("/register")
	public String register();
}
