package com.lisz.controller;

import com.lisz.api.ConsumerApi;
import com.lisz.api.UserApi;
import com.lisz.entity.Person;
import com.lisz.service.RestService;
import com.lisz.service.UserConsumerService;
import com.lisz.service.UserConsumerService2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RefreshScope
public class MainController {
	@Value("${server.port}")
	private int port;

	@Value("${myconfig}")
	private String myconfig;

	@Autowired
	//private UserConsumerService userConsumerService;
	//private UserConsumerService2 userConsumerService;
	// 写 private ConsumerApi api; 也可以.但是ConsumerApi不能生，要给OpenFeign一个切入的机会
	// @FeignClient(name = "user-provider")这个注解要有地方放置。Feign侵入的是Consumer端
	//private UserApi api;

	private ConsumerApi api;

	// Hystrix降级处理RestTemplate发送的微服务请求
	@Autowired
	private RestService restService;

	@GetMapping("/alive")
	public String alive(){
		// return userConsumerService.alive();
		System.out.println("Consumer alive， " + port);
		//return api.alive();
		return restService.alive();
	}


	@GetMapping("/get-by-id/{id}")
	public String getById(@PathVariable("id") Integer id){
		return api.getById(id);
	}

	@GetMapping("/getMap")
	public Map<Integer, String> getMap(@RequestParam("id") Integer id){ //必须得写@RequestParam，否则报错，说POST请求有问题，除非引入feign-httpclient依赖
		return api.getMap(3);
	}

	@GetMapping("/getMap2")
	public Map<Integer, String> getMap(Integer id, String name){ // 加了feign-httpclient依赖之后可以不写@RequestParam，甚至变量名都可以对不上：Integer idd
		return api.getMap(3, name);
	}

	@GetMapping("/getMap3")
	public Map<Integer, String> getMap(@RequestParam Map<String, Object> map){ //传Map是为了个数可变的参数. 传Map的话，就必须加上@RequestParam，但是无需写各个参数名
		System.out.println(map);
		return api.getMap(map);
	}

	@PostMapping("/postMap")
	public Map<Integer, String> postMap(@RequestParam Map<String, Object> map){ // POST请求会把参数写在http请求的body里
		System.out.println(map);
		return api.postMap(map);
	}

	@GetMapping("/person")
	public Person postPerson(@RequestParam Map<String, Object> map){
		Person person = new Person(Integer.parseInt(map.get("id").toString()), map.get("name").toString(), null);
		return api.postPerson(person);
	}

	@GetMapping("/helloZuul")
	public String postPerson(){
		return "Consumer alive， " + port + " " + api.helloZuul() + " my config is: " + myconfig;
	}
}
