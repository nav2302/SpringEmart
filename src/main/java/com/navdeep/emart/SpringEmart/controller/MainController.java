package com.navdeep.emart.SpringEmart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.navdeep.emart.SpringEmart.service.UserService;

@Controller
public class MainController {

	@Autowired
	UserService userService;

	@GetMapping("/")
	public ModelAndView root() {
		ModelAndView model = new ModelAndView();
		model.setViewName("index");
		return model;
	}

	@GetMapping("/login")
	public String login(Model model) {
		return "login";
	}
}