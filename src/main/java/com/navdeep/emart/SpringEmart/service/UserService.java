package com.navdeep.emart.SpringEmart.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.navdeep.emart.SpringEmart.dto.UserRegistrationDto;
import com.navdeep.emart.SpringEmart.model.Order;
import com.navdeep.emart.SpringEmart.model.User;

public interface UserService extends UserDetailsService {

	User findByEmail(String email);

	User save(UserRegistrationDto registration, String role);

	User update(User user);

	void updatePassword(String password, Long userId);

	List<String> findAllNames();

	User findById(Long id);

	Order getLatestOrder(Long id);

	List<Order> findAllOrdersByDate(Long id);
}
