package com.lisz.controller;

import com.lisz.api.UserApi;
import com.lisz.entity.Address;
import com.lisz.entity.Person;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class UserController implements UserApi {
	@Value("${server.port}")
	private int port;

	private AtomicInteger count = new AtomicInteger(0);
	//@GetMapping("/alive") // 可以注释掉，因为UserApi.alive()上面已经有一样的注解了
	public String alive(){
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		int i = count.incrementAndGet();
//		System.out.println("Port: " + port + " 第" + i + "次调用");
		int i = 1/0;
		return "okk";
	}

	@Override
	public String getById(Integer id) {
		return "The ID is: " + id;
	}

	@Override
	public Person postPerson(@RequestBody Person person) { //可以传Person对象，这个对像写在api里面，为的是两边都看得见
		System.out.println(person);
		Address address = new Address("WA", "Seattle", "Westlake Union");
		person.setAddress(address);
		return person;
	}

	@GetMapping("/getMap")
	public Map<Integer, String> getMap(@RequestParam("id") Integer id){
		System.out.println("ID: " + id);
		return Collections.singletonMap(id, "hahaha");
	}

	@GetMapping("/getMap2")
	public Map<Integer, String> getMap(Integer id, String name){
		System.out.println("ID: " + id);
		return Collections.singletonMap(id, name);
	}

	@GetMapping("/getMap3")
	public Map<Integer, String> getMap(@RequestParam Map<String, Object> map){
		return Collections.singletonMap(Integer.parseInt(map.get("id").toString()), map.get("name").toString());
	}

	@PostMapping("/postMap")
	public Map<Integer, String> postMap(@RequestBody Map<String, Object> map){ // 这里写@RequestParam也可以，但要跟ConsumerApi或者UserApi里的声明对上，那里写的是什么这里也写什么
		System.out.println("POST");
		return Collections.singletonMap(Integer.parseInt(map.get("id").toString()), map.get("name").toString());
	}
}
