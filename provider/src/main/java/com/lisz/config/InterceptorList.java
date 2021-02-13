package com.lisz.config;

import com.lisz.interceptor.IPInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorList implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(ipInterceptor());
	}

	// 关键，将拦截器作为bean写入配置中, 这样Spring就可以将IPInterceptor中被@Value、@Autowired注解的属性注入了
	// https://blog.csdn.net/wmh13262227870/article/details/77005920
	@Bean
	public HandlerInterceptor ipInterceptor() {
		return new IPInterceptor();
	}
}
