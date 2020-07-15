package com.navdeep.emart.SpringEmart.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.navdeep.emart.SpringEmart.dto.UserRegistrationDto;
import com.navdeep.emart.SpringEmart.model.Order;
import com.navdeep.emart.SpringEmart.model.Role;
import com.navdeep.emart.SpringEmart.model.User;
import com.navdeep.emart.SpringEmart.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	EntityManager em;

	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public User save(UserRegistrationDto registration, String role) {
		User user = new User();
		user.setFirstName(registration.getFirstName());
		user.setLastName(registration.getLastName());
		user.setEmail(registration.getEmail());
		user.setPassword(passwordEncoder.encode(registration.getPassword()));
		user.setRoles(Arrays.asList(new Role("ROLE_"+role)));
		return userRepository.save(user);
	}

	@Override
	public void updatePassword(String password, Long userId) {
		userRepository.updatePassword(password, userId);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
				mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	@Override
	public User update(User user) {
		return userRepository.save(user);
	}

	@Override
	public List<String> findAllNames() {
		return userRepository.findAllNames();
	}

	@Override
	public User findById(Long id) {
		// TODO Auto-generated method stub
		return userRepository.findById(id).orElse(null);
	}

	@Override
	public Order getLatestOrder(Long id) {
		Query query = em.createQuery("select o from Order o where o.user.id = :id order by o.dateCreated desc", Order.class);
		query.setParameter("id", id);
		try {
			@SuppressWarnings("unchecked")
			List<Order> order= query.setMaxResults(1).getResultList();
			return order.get(0);
		}catch(Exception e) {
			return null;
		}
	}

	@Override
	public List<Order> findAllOrdersByDate(Long id) {
		Query query = em.createQuery("select o from Order o where o.user.id = :id order by o.dateCreated desc", Order.class);
		query.setParameter("id", id);
		try {
			@SuppressWarnings("unchecked")
			List<Order> orders= query.getResultList();
			return orders;
		}catch(Exception e) {
			return null;
		}
	}
}
