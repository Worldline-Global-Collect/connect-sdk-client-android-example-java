/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.product;

import android.view.ViewGroup;

import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItem;

/**
 * Defines the rendering of paymentproducts interface
 *
 */
public interface RenderPaymentItemInterface {
	
	
	/***
	 * Renders a PaymentProduct by the data in the product.
	 * This PaymentProduct is added to the given parent
	 * 
	 * @param product, PaymentProduct containing all data for the PaymentProduct
	 * @param parent, the ViewGroup to which the rendered PaymentProduct is added
	 */
	public void renderPaymentItem(BasicPaymentItem product, ViewGroup parent);
	
}
