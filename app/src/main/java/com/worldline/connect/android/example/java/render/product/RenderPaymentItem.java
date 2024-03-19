/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.product;

import java.security.InvalidParameterException;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldline.connect.android.example.java.R;
import com.worldline.connect.android.example.java.translation.Translator;
import com.worldline.connect.sdk.client.android.ConnectSDK;
import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItem;
import com.worldline.connect.sdk.client.android.model.paymentproduct.BasicPaymentProduct;


/**
 * Renders paymentproducts
 *
 */
public class RenderPaymentItem implements RenderPaymentItemInterface {
		
	
	@SuppressWarnings("deprecation")
	@Override
	public void renderPaymentItem(BasicPaymentItem product, ViewGroup parent) {
		
		if (product == null) {
			throw new InvalidParameterException("Error renderingPaymentProduct, product may not be null");
		}
		if (parent == null) {
			throw new InvalidParameterException("Error renderingPaymentProduct, parent may not be null");
		}
		
		// Inflate the activity_select_payment_product_render layout
		LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View paymentProductLayout = inflater.inflate(R.layout.activity_render_payment_product, parent, false);
		
		// Set the belonging accountOnFile on the tag of the row so we can retrieve it when clicked
		paymentProductLayout.findViewById(R.id.paymentProductRow).setTag(product);			
		
		// Get the TextView and ImageView which will be filled
		TextView paymentProductNameTextView = 		(TextView) paymentProductLayout.findViewById(R.id.paymentProductName);
		ImageView paymentProductNameLogoImageView = (ImageView)paymentProductLayout.findViewById(R.id.paymentProductLogo);

		// Set the translated value
		Translator translator = Translator.getInstance(parent.getContext());
		String translatedValue = (product instanceof BasicPaymentProduct) ? translator.getPaymentProductName(product.getId()) : translator.getPaymentProductGroupName(product.getId());
		paymentProductNameTextView.setText(translatedValue);

		ConnectSDK.INSTANCE.getClientApi().getDrawableFromUrl(
				product.getDisplayHints().getLogoUrl(),
				paymentProductNameLogoImageView::setBackground,
				(failure) -> Log.e("renderPaymentItem", "Error message: " + failure.getMessage())
		);
		
		parent.addView(paymentProductLayout);
	}
}
