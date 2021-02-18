package com.lisz.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {
	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(defaultFallback = "aliveFallback")
	public String alive(){
		String url = "http://user-provider/user/alive";
		String result = restTemplate.getForObject(url, String.class);
		return result;
	}

	public String aliveFallback(){
		return "调用远程方法出错了，服务降级，用我来代替把！";
	}
}
