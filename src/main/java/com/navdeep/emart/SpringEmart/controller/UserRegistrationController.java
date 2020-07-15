package com.navdeep.emart.SpringEmart.controller;

import java.util.Arrays;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.navdeep.emart.SpringEmart.dto.SellerRegistrationDto;
import com.navdeep.emart.SpringEmart.dto.UserRegistrationDto;
import com.navdeep.emart.SpringEmart.model.Role;
import com.navdeep.emart.SpringEmart.model.User;
import com.navdeep.emart.SpringEmart.service.UserService;


@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@ModelAttribute("user")
	public UserRegistrationDto userRegistrationDto() {
		return new UserRegistrationDto();
	}

	@GetMapping("/sellerReg")
	public String showSellerRegistrationForm(Model model) {
		model.addAttribute("seller", new SellerRegistrationDto());
		return "seller/sellerRegistration";
	}
	
	@GetMapping("/buyerReg")
	public String showBuyerRegistrationForm(Model model) {
		return "buyer/buyerRegistration";
	}

	@PostMapping("/sellerReg")
	public String registerUserAccount(@ModelAttribute("seller") @Valid SellerRegistrationDto registration,
			BindingResult result) {

		User existing = userService.findByEmail(registration.getEmail());
		if (existing != null) {
			result.rejectValue("email", null, "There is already an Seller account registered with that email");
		}

		if (result.hasErrors()) {
			return "seller/sellerRegistration";
		}
		
		User user = new User();
		user.setFirstName(registration.getFirstName());
		user.setLastName(registration.getLastName());
		user.setEmail(registration.getEmail());
		user.setPassword(passwordEncoder.encode(registration.getPassword()));
		user.setRoles(Arrays.asList(new Role("ROLE_SELLER")));
		user.setCompany(registration.getCompanyName());
		user.setAddress(registration.getPostalAddress());
		user.setWebsite(registration.getWebsite());
		user.setPhone(registration.getContact());
		userService.update(user);
		
		return "redirect:/login";
	}
	
	@PostMapping("/buyerReg")
	public String registerBuyerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
			BindingResult result) {

		System.out.println(userDto.getTerms());
		User existing = userService.findByEmail(userDto.getEmail());
		if (existing != null) {
			result.rejectValue("email", null, "There is already a Buyer account registered with that email");
		}

		if (result.hasErrors()) {
			return "registration";
		}

		userService.save(userDto, "BUYER");
		return "redirect:/login";
	}

}
