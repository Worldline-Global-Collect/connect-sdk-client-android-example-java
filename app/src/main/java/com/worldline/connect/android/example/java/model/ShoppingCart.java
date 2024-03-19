/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.model;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart implements Serializable {
	
	private static final long serialVersionUID = 7301578426204735531L;
	
	/** List with all ShoppingCartItems **/
	private List<ShoppingCartItem> shoppingCartItems = new ArrayList<ShoppingCartItem>();

	/**
	 * Add a shoppingcartitem to the shoppingcart  
	 * @param item
	 */
	public void addItemToShoppingCart(ShoppingCartItem item) {
		
		if (item == null) { 
			throw new InvalidParameterException("Error adding ShoppingCartItem, it may not be null");
		}
		
		shoppingCartItems.add(item);
	}

	/**
	 * Get all shoppingcart items
	 * @return
	 */
	public List<ShoppingCartItem> getShoppingCartItems() {
		return shoppingCartItems;
	}
	
	
	/**
	 * Gets the total amount of all items in the shoppingcart
	 * @return
	 */
	public Long getTotalAmount() {

		Long totalAmount = 0L;
		for (ShoppingCartItem item : shoppingCartItems) {
			totalAmount += item.getAmountInCents();
		}
		return totalAmount;
	}

}
