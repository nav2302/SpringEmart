package com.navdeep.emart.SpringEmart.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.navdeep.emart.SpringEmart.model.OrderProduct;
import com.navdeep.emart.SpringEmart.model.OrderProductPK;

public interface OrderProductRepository extends JpaRepository<OrderProduct, OrderProductPK> {
	
}