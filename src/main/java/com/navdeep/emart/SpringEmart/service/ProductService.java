package com.navdeep.emart.SpringEmart.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.navdeep.emart.SpringEmart.model.Product;
import com.navdeep.emart.SpringEmart.repositories.ProductRepository;

@Service
public class ProductService {
	
	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	EntityManager em;

	public Page<Product> findBySearchAndLowToHigh(String search, Pageable pageable) {
		return productRepository.findBySearchAndLowToHigh(search, pageable);
	}

	public Page<Product> findBySearchAndHighToLow(String search, Pageable pageable) {
		return productRepository.findBySearchAndHighToLow(search, pageable);
	}

	public Page<Product> findBySearchAndarrival(String search, Pageable pageable) {
		return productRepository.findBySearchAndarrival(search, pageable);
	}

	public Product save(Product product) {
		return productRepository.save(product);
	}

	public List<Product> findBetweenDates(String startDateString, String endDateString, Long id) {
		Query query = em.createNativeQuery("select product.id as id, product.create_date as create_date, product.description as description, product.discount as discount, product.likes as likes, product.modify_date as modify_date, product.name as name, product.pic as pic, product.price as price, product.quantity_posted as quantity_posted, product.sales as sales, product.user_id as user_id from product product where modify_date BETWEEN ?1 AND ?2 and product.user_id=?3", Product.class);
		query.setParameter(1, startDateString);
		query.setParameter(2, endDateString);
		query.setParameter(3, id);
		try {
			@SuppressWarnings("unchecked")
			List<Product> products = query.getResultList();
			return products;
		}
		catch(Exception e) {
			return null;
		}
	}
}
