package com.lisz.api;

import com.lisz.entity.Person;
import org.springframework.web.bind.annotation.*;

/*
中间类：接口和Controller的混合体：interface，无方法实现,供服务提供方的Controller实现；但在类和方法上面却有@RequestMapping("/user")或
@RequestMapping("/user")这样类似Controller的注解，这个API类头顶上的@RequestMapping("/user")也会被其实现的Controller类继承，
所以访问的时候要注意在url里面加上这个"/user"。同理，这里alive方法上的@GetMapping("/alive")会被实现类改写，这样consumer在调用的时候，
其实是看不到实现的，拿到注解之后仍然调用"/user/alive",就会在后端收到404错误
1. 接口和REST 的 URL的特点都是只知道入参和返回值，调用方无需关心具体实现
2。RestTemplate的方式是提供方写java代码和文档，调用方根据文档再写Java：Java -> 文档 -> Java，既然都是java，那就可以省去中间的文档
3。文档有可能跟java代码不同步了，但是接口和它的实现一定是同步的
基于以上三点，我们可以把接口所在项目打包（maven install）成公共的library，跟provider做成聚合项目，并在maven中引入：
		<dependency>
			<groupId>com.lisz</groupId>
			<artifactId>user-api</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
然后调用方和实现方都看得见。调用方的
继承接口类脑袋顶上要有：
@FeignClient(name = "user-provider")，以标记到底该调用那个应用。这么些是最想的，但是唯一的坏处是没办法跨语言，异构平台不行。
在provider和consumer中将会各有一个这个接口的实现类，provider中是controller，而consumer中一般是service层的类
https://github.com/bjmashibing/InternetArchitect/blob/master/20%20架构师三期%20SpringCloud微服务架构/SpringCloud05.md
这个API里的注解，比较特殊都是给Feign看的，用来组装成URL发到服务端。
这里不写@FeignClient是因为服务提供端户籍成这个接口，其所有的方法会都带上@FeignClient的功能。接口里的注解会被实现类拿到
 */
@RequestMapping("/user") // 这里可以注释掉，只要重启Provider和Consumer，则可以屏蔽掉URL的各种变化，不用知道他是怎么变的，重启（构建）就好
public interface UserApi {
	@GetMapping("/alive")
	public String alive();

	@GetMapping("/get-by-id/{id}")
	public String getById(@PathVariable("id") Integer id);

	@PostMapping("/postPerson")
	Person postPerson(@RequestBody Person person); //Person类只写在Api这里就能被双方都看见
}
