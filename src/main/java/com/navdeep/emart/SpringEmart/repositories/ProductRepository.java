package com.navdeep.emart.SpringEmart.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.navdeep.emart.SpringEmart.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

	@Query("select p from Product p where p.name like %:search% order by p.price asc")
	Page<Product> findBySearchAndLowToHigh(@Param("search") String search, Pageable pageable);
	
	@Query("select p from Product p where p.name like %:search% order by p.price desc")
	Page<Product> findBySearchAndHighToLow(@Param("search") String search, Pageable pageable);

	@Query("select p from Product p where p.name like %:search% order by p.modifyDate desc")
	Page<Product> findBySearchAndarrival(@Param("search") String search, Pageable pageable);

	@Query("select p from Product p where p.name like %:search%")
	Page<Product> findByName(@Param("search") String search, Pageable pageable);

	@Query("select p from Product p where p.user.id = :id order by p.modifyDate desc")
	Page<Product> getAllProductsByDate(@Param("id")Long id, Pageable pageable);

	@Query("select p from Product p where p.user.id = :id order by p.modifyDate desc")
	List<Product> getAllProductsByDate(@Param("id")Long id);
}
