package com.navdeep.emart.SpringEmart.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.navdeep.emart.SpringEmart.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	@Modifying
	@Query("update User u set u.password = :password where u.id = :id")
	void updatePassword(@Param("password") String password, @Param("id") Long id);

	@Query(value = "SELECT email FROM user", nativeQuery = true)
	List<String> findAllNames();
	
}
