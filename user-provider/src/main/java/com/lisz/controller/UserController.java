package com.lisz.controller;

import com.lisz.api.UserApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {
	@GetMapping("/alive")
	public String alive(){
		return "okk";
	}
}
