package com.lisz.controller;

import com.lisz.api.UserApi;
import com.lisz.service.UserConsumerService;
import com.lisz.service.UserConsumerService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	@Autowired
	//private UserConsumerService userConsumerService;
	//private UserConsumerService2 userConsumerService;
	// 写 private ConsumerApi api; 也可以.但是ConsumerApi不能生，要给OpenFeign一个切入的机会
	// @FeignClient(name = "user-provider")这个注解要有地方放置。Feign侵入的是Consumer端
	private UserApi api;

	@GetMapping("/alive")
	public String alive(){
		// return userConsumerService.alive();
		System.out.println("alive2");
		return api.alive();
	}
}
