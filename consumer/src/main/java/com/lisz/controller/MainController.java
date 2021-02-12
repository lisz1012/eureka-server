package com.lisz.controller;

import com.lisz.entity.Person;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EurekaServiceInstance;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
public class MainController {
	@Autowired
	private DiscoveryClient discoveryClient;
	private static final Random RANDOM = new Random();

	// Springboot 2.2.0之后似乎用org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient
	// 替换了ribbon，其所在包的spring.factories里面有
	// org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration,\这一项
	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private LoadBalancerClient loadBalancerClient;

	@Autowired
	private RestTemplate restTemplate;

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
				restTemplate.setUriTemplateHandler(factory);
//				ResponseEntity<String> entity = restTemplate.getForEntity("/hello", String.class);
//				return entity.getBody();
				return restTemplate.getForObject("/hello", String.class);
			}
		}
		return "ERROR";
	}

	@GetMapping("/helloFromClient3")
	public String helloFormClient3(){

		// 客户端的负载均衡，choose里面有负载均衡的策略。启动两个provider的程序之后会交替访问不同的机器
		ServiceInstance provider = loadBalancerClient.choose("provider");

		/* 引入Ribbon依赖的话loadBalancerClient就是org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient
		 但这会使得 loadBalancerClient 拿到的provider为null，导致无法调用服务端

		 在 spring-cloud-netflix-ribbon:2.2.6.RELEASE 里的spring.factories文件里写了：
		 org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
		 org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration

		 而在RibbonAutoConfiguration里面写了：
		    @Bean
			@ConditionalOnMissingBean(LoadBalancerClient.class)
			public LoadBalancerClient loadBalancerClient() {
				return new RibbonLoadBalancerClient(springClientFactory());
			}
		 返回了RibbonLoadBalancerClient

		 而不引入Ribbon依赖的话，loadBalancerClient的实现类是：
		 org.springframework.cloud.loadbalancer.blocking.client.BlockingLoadBalancerClient

		 按理说是Ribbon和Eureka的 Client一起做到的负载均衡：Eureka Client负责向Eureka Server拉取服务列表，Ribbon再根据此列表，
		 负载均衡地 choose server
		 */
		System.out.println(loadBalancerClient.getClass().getName());
		if (provider != null) {
			String host = provider.getHost();
			int port = provider.getPort();
			String url = "http://" + host + ":" + port + "/hello";
//				ResponseEntity<String> entity = restTemplate.getForEntity("/hello", String.class);
//				return entity.getBody();
			return restTemplate.getForObject(url, String.class) + " from: " + url;
		}
		return "ERROR";
	}

	@GetMapping("/helloFromClient4")
	public String helloFromClient4(){
		String baseUrl = "http://192.168.1.102:82";
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
		restTemplate.setUriTemplateHandler(factory);
//				ResponseEntity<String> entity = restTemplate.getForEntity("/hello", String.class);
//				return entity.getBody();
		return restTemplate.getForObject("/hello", String.class);
	}

	//自定义负载均衡策略（轮询）
	@GetMapping("/helloFromClient5")
	public String helloFromClient5(){
		List<ServiceInstance> provider = discoveryClient.getInstances("provider");
		int index = RANDOM.nextInt(provider.size());
		ServiceInstance serviceInstance = provider.get(index);
		String scheme = serviceInstance.getScheme();
		String host = serviceInstance.getHost();
		int port = serviceInstance.getPort();
		String url = scheme + "://" + host + ":" + port + "/hello";
		return restTemplate.getForObject(url, String.class);
	}

	// 自动处理URL，运用LB，调用其LB算法，找到instance，发起http请求。默认轮询
	@GetMapping("/helloFromClient6")
	public String helloFromClient6(){
		String url = "http://provider/hello"; //provider是服务名，端口号不要写，要在restTemplate的@Bean生成的时候同时标注@LoadBalanced
		return restTemplate.getForObject(url, String.class);
	}

	// 返回ResponseEntity
	@GetMapping("/helloFromClient7")
	public ResponseEntity<String> helloFromClient7(){
		String url = "http://provider/hello"; //provider是服务名，端口号不要写，要在restTemplate的@Bean生成的时候同时标注@LoadBalanced
		ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
		System.out.println(entity);

		return entity;
	}

	@GetMapping("/helloFromClient8")
	public ResponseEntity<Map> helloFromClient8(){
		String url = "http://provider/getMap";
		ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class);
		System.out.println(entity);
		return entity;
	}

	@GetMapping("/helloFromClient9")
	public Map<String, String> helloFromClient9(){
		String url = "http://provider/getMap";
		Map<String, String> map = restTemplate.getForObject(url, Map.class);
		System.out.println(map);
		return map;
	}

	@GetMapping("/helloFromClient10")
	public Object helloFromClient10(){
		String url = "http://provider/getPerson";
		// 直接返回给前端那还好，如果需要处理Person的数据，则还需要拷贝Person类过来
		return restTemplate.getForObject(url, Object.class);
	}

	@GetMapping("/helloFromClient11")
	public Person helloFromClient11() {
		String url = "http://provider/getPerson";
		return restTemplate.getForObject(url, Person.class);
	}

	// Url 直接传参数
	@GetMapping("/helloFromClient12")
	public Person helloFromClient12() {
		String url = "http://provider/getPerson2?name=hahaha";
		return restTemplate.getForObject(url, Person.class);
	}

	@GetMapping("/helloFromClient13")
	public Person helloFromClient13() {
		String url = "http://provider/getPerson2?name={1}";
		return restTemplate.getForObject(url, Person.class, "hahahaha");
	}

	@GetMapping("/helloFromClient14")
	public Person helloFromClient14() {
		Map<String, String> map = new HashMap<>();
		map.put("name1", "hahahahahahahahaha");
		String url = "http://provider/getPerson2?name={name1}";
		return restTemplate.getForObject(url, Person.class, map);
	}

	@GetMapping("/helloFromClient15")
	public Person helloFromClient15() {
		Map<String, String> map = new HashMap<>();
		map.put("name2", "posted Hahaha");
		String url = "http://provider/postPerson2?name={name2}";
		return restTemplate.postForObject(url, null, Person.class, map);
	}

	// 用restTemplate.postForEntity发Post请求
	@GetMapping("/helloFromClient16")
	public ResponseEntity<Person> helloFromClient16() {
		// Header没必要
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("name", "posted Hahahahahahahaha - 16");
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, null);

		String url = "http://provider/postPerson2";
		return restTemplate.postForEntity(url, requestEntity, Person.class);
	}

	// 用restTemplate.postForObject发Post请求
	@GetMapping("/helloFromClient17")
	public Object helloFromClient17() {
		// Header没必要
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("name", "posted Hahahahahahahaha-17");
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, null);

		String url = "http://provider/postPerson2";
		return restTemplate.postForObject(url, requestEntity, Person.class);
	}

	@GetMapping("/postPersonForPerson")
	public Object postPersonForPerson() {
		Person person = new Person(3,"zhangsan", null);
		String url = "http://provider/postPerson3"; //provider的postPerson3这个Controller里面要接受Person，且其前面要有@RequestBody注解
		return restTemplate.postForObject(url, person, Person.class);
	}

	// 用restTemplate.postForLocation 发Post请求，得到URI 然后跳转，需要HttpServletResponse
	@GetMapping("/helloFromClient18")
	public void helloFromClient18(HttpServletResponse response) throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("name", "Spring");
		String url = "http://provider/postLocation";
		URI uri = restTemplate.postForLocation(url, map, Person.class);
		System.out.println(uri);
		response.sendRedirect(uri.toString());
	}

	// 用restTemplate.postForEntity发Post请求
	@GetMapping("/helloFromClient19")
	public void helloFromClient19(HttpServletResponse response) throws Exception {
		Person person = new Person(1, "666", null);
		String url = "http://provider/postLocation";
		URI uri = restTemplate.postForLocation(url, person, Person.class);
		System.out.println(uri);
		response.sendRedirect(uri.toString());
	}
}
