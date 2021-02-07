package com.lisz.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

@RestController
public class MainController {
	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private EurekaClient eurekaClient;

	@GetMapping("/hi")
	public String hi(){
		return "hi";
	}

	@GetMapping("/client")
	public List<String> client(){
		List<String> services = discoveryClient.getServices();
		return services;
	}

	@GetMapping("/client2")
	public List<ServiceInstance> client2(){
		List<ServiceInstance> provider = discoveryClient.getInstances("provider");
		return provider;
	}

	@GetMapping("/helloFormClient")
	public String helloFormClient(){
		List<ServiceInstance> provider = discoveryClient.getInstances("provider");
		if (!provider.isEmpty()){
			EurekaServiceInstance serviceInstance = (EurekaServiceInstance)provider.get(0);
			if (InstanceInfo.InstanceStatus.UP.equals(serviceInstance.getInstanceInfo().getStatus())){
				String scheme = serviceInstance.getScheme();
				String host = serviceInstance.getHost();
				String serviceId = serviceInstance.getServiceId(); // "provider"
				int port = serviceInstance.getPort();
				String baseUrl = scheme + "://" + host + ":" + port;
				DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
				factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.setUriTemplateHandler(factory);
				ResponseEntity<String> entity = restTemplate.getForEntity("/hello", String.class);
				return entity.getBody();
			}
		}
		return "ERROR";
	}

	@GetMapping("/helloFormClient2")
	public String helloFormClient2(){
		// 用服务名找
		List<InstanceInfo> provider = eurekaClient.getInstancesByVipAddress("provider", false);
		if (!provider.isEmpty()){
			InstanceInfo instanceInfo = provider.get(0);
			if (InstanceInfo.InstanceStatus.UP.equals(instanceInfo.getStatus())){
				String host = instanceInfo.getHostName();
				int port = instanceInfo.getPort();
				String baseUrl = "http://" + host + ":" + port;
				DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
				factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.setUriTemplateHandler(factory);
//				ResponseEntity<String> entity = restTemplate.getForEntity("/hello", String.class);
//				return entity.getBody();
				return restTemplate.getForObject("/hello", String.class);
			}
		}
		return "ERROR";
	}
}
