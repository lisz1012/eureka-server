package com.lisz.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {
	@Autowired
	private RestTemplate restTemplate;

	@HystrixCommand(defaultFallback = "aliveFallback") // 把正经方法和fallback联系起来，一般正经方法里面是个service call
	public String alive(){
		String url = "http://user-provider/user/alive"; // 这个/user要写上，因为user-api中的UserApi脑袋上有这么一句：@RequestMapping("/user")
		String result = restTemplate.getForObject(url, String.class);
		return result;
	}

	public String aliveFallback(){
		return "调用远程方法出错了，服务降级，用我来代替把！";
	}
}
