package com.lisz.controller;

import com.lisz.service.HealthStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	@Value("${server.port}")
	private int port;

	@Autowired
	private HealthStatusService healthStatusService;

	@GetMapping("/hello")
	public String hello(){
		String result = "hello from " + port;
		System.out.println(result);
		return result;
	}

	// 下线一台机器之后，可以反复刷新：http://192.168.1.102:81/helloFormClient3 并观察输出，看可提供服务的机器是否便少了
	// 再次上线那台机器，再观察她是否有能提供服务了
	@GetMapping("/health")
	public String health(@RequestParam("status") Boolean status) {
		healthStatusService.setStatus(status);
		return healthStatusService.getStatus().toString();
	}
}
