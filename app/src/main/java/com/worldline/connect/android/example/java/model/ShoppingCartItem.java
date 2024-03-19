/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.model;

import java.io.Serializable;

/**
 * Pojo which contains a shoppingcartitem
 *
 */
public class ShoppingCartItem implements Serializable {
	
	private static final long serialVersionUID = 8101681369595663857L;
	
	private String description;
	private Long amountInCents;
	private Integer quantity;
	
	
	public ShoppingCartItem() {
	}
	
	public ShoppingCartItem(String description, Long amountInCents, Integer quantity) {
		this.description = description;
		this.amountInCents = amountInCents;
		this.quantity = quantity;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Long getAmountInCents() {
		return amountInCents;
	}
	
	public Integer getQuantity() {
		return quantity;
	}
}
