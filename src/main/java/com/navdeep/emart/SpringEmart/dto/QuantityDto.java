package com.navdeep.emart.SpringEmart.dto;

import javax.validation.constraints.NotNull;

public class QuantityDto {
	
	@NotNull
    private Integer number;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
	
}
