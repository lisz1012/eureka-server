package com.lisz.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	@Value("${server.port}")
	private int port;

	@GetMapping("/alive")
	public String alive(){
		System.out.println("alive, port " + port);
		return "alive, port " + port;
	}
}
