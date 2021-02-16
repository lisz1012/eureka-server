package com.lisz.api;

import com.lisz.factory.HystrixConsumerApiFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

// 这个类只是提供了一个让Feign介入的机会
@FeignClient(name = "user-provider", fallbackFactory = HystrixConsumerApiFactory.class)
public interface ConsumerApi extends UserApi {
	/**
	 * 这里的getMap 和 @RequestParam("id") 是给Feign看的，必须写，否则启动的时候报错。跟上面这个 "user-provider"
	 * 组装成一个完整的请求，会组装成： "user-provider/getMap?id=3"
	 * @param id
	 * @return
	 */
	@GetMapping("/getMap")
	Map<Integer, String> getMap(@RequestParam("id") Integer id);
	@GetMapping("/getMap2")
	Map<Integer, String> getMap(@RequestParam("id") Integer id, @RequestParam("name") String name);
	@GetMapping("/getMap3")
	Map<Integer, String> getMap(@RequestParam Map<String, Object> map);
	@PostMapping("/postMap") // 请求的类型取决于这里的设置
	Map<Integer, String> postMap(@RequestBody Map<String, Object> map);
}
