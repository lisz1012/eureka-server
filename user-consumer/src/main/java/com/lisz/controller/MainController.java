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
	private UserApi api; // 写 private ConsumerApi api; 也可以

	@GetMapping("/alive")
	public String alive(){
		// return userConsumerService.alive();
		System.out.println("alive2");
		return api.alive();
	}
}
