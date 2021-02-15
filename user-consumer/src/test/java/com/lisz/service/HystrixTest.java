package com.lisz.service;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HystrixTest extends HystrixCommand {

	protected HystrixTest(HystrixCommandGroupKey group) {
		super(group);
	}

	public static void main(String[] args) {
		Future<String> futureResult = new HystrixTest(HystrixCommandGroupKey.Factory.asKey("ext")).queue();
		String result = null;
		try {
			result = futureResult.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("程序结果：" + result);
	}

	// try
	@Override
	protected Object run() throws Exception {
		System.out.println("执行逻辑");
		int i = 1/0;
		return "abc";
	}

	// catch, 备用逻辑
	@Override
	protected Object getFallback() {
		return "getFallbackgetFallback";
	}
}
