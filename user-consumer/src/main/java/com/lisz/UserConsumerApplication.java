package com.lisz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
@EnableCircuitBreaker //如果想用Hystrix熔断降级Consumer端用RestTemplate发出去的Service call，则必须加上这个注解。不论404还是500都会执行fallback
@EnableHystrixDashboard //开启图形化的Hystrix： http://192.168.1.102:81/hystrix Hystrix ping数据：http://192.168.1.102:81/actuator/hystrix.stream 要抛一次异常才能看见数据
// 把上面第二个URL粘到第一个网页中的第一个input field中就能查看了
public class UserConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserConsumerApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
