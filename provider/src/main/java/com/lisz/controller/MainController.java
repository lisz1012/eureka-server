package com.lisz.controller;

import com.lisz.entity.Address;
import com.lisz.entity.Person;
import com.lisz.service.HealthStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

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

	@GetMapping("/getMap")
	public Map<String, String> getMap(){
		return Collections.singletonMap("id", "100");
	}

	@GetMapping("/getPerson")
	public Person getPerson(){
		Address address = new Address("WA", "Seattle", "Westlake");
		Person person = new Person(1, "lisz", address);
		return person;
	}

	@GetMapping("/getPerson2")
	public Person getPerson2(@RequestParam("name") String name){
		Address address = new Address("WA", "Seattle", "Westlake");
		Person person = new Person(1, name, address);
		return person;
	}

	@PostMapping("/postPerson2")
	public Person postPerson2(@RequestParam("name") String name){
		Address address = new Address("WA", "Seattle", "Westlake");
		Person person = new Person(1, name, address);
		return person;
	}

	@PostMapping("/postLocation")
	public URI postLocation(@RequestBody Person person, HttpServletResponse response) throws Exception{
		URI uri = new URI("https://baidu.com/s?wd=" + person.getName());
		response.addHeader("Location", uri.toString()); // 必须加这一句，否则空指针
		return uri;
	}
}
