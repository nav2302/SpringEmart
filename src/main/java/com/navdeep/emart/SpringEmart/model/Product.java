package com.navdeep.emart.SpringEmart.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
public class Product {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "create_date")
	private Date createDate;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modify_date")
	private Date modifyDate;
	
	
	private String description;
	
	@Lob
	@Column(name = "pic")
	private byte[] pic;
	
	private String name;
	private Double price;
	private Long likes;
	private Integer quantityPosted;
	private Integer sales;
	private Double discount;
	private String code;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	public Product(){}
	
	public Product(Date createDate, Date modifyDate, String description, byte[] pic, String name,
			Double price, Integer quantityPosted, Double discount) {
		super();
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.description = description;
		this.pic = pic;
		this.name = name;
		this.price = price;
		this.quantityPosted = quantityPosted;
		this.discount = discount;
	}

	public Long getId() {
		return id;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getModifyDate() {
		return modifyDate;
	}
	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public byte[] getPic() {
		return pic;
	}
	public void setPic(byte[] pic) {
		this.pic = pic;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getLikes() {
		return likes;
	}
	public void setLikes(Long likes) {
		this.likes = likes;
	}

	public Integer getQuantityPosted() {
		return quantityPosted;
	}

	public void setQuantityPosted(Integer quantityPosted) {
		this.quantityPosted = quantityPosted;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getSales() {
		return sales;
	}

	public void setSales(Integer sales) {
		this.sales = sales;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
