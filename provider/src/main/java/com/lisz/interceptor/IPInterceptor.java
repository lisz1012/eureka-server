package com.lisz.interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Component // 最好不写，有时候会报重复定义Bean的错误，那样还需要改名
public class IPInterceptor implements HandlerInterceptor {
	@Value("${server.port}")
	private int port;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		System.out.println("This is: " + port);
		return true;
	}
}
