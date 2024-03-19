/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.accountonfile;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldline.connect.sdk.client.android.ConnectSDK;
import com.worldline.connect.android.example.java.R;
import com.worldline.connect.sdk.client.android.formatter.StringFormatter;
import com.worldline.connect.sdk.client.android.model.accountonfile.AccountOnFile;
import com.worldline.connect.sdk.client.android.model.accountonfile.displayhints.AccountOnFileDisplay;
import com.worldline.connect.sdk.client.android.model.accountonfile.attributes.KeyValuePair;

import java.security.InvalidParameterException;


/**
 * Renders the accounts on file on screen
 *
 */
public class RenderAccountOnFile implements RenderAccountOnFileInterface {
	
	@Override
	public void renderAccountOnFile(AccountOnFile accountOnFile, String productId, ViewGroup parent) {
	
		if (accountOnFile == null) {
			throw new InvalidParameterException("Error renderingAccountOnFile, accountOnFile may not be null");
		}
		if (productId == null) {
			throw new InvalidParameterException("Error renderingAccountOnFile, productId may not be null");
		}
		if (parent == null) {
			throw new InvalidParameterException("Error renderingAccountOnFile, parent may not be null");
		}
		
		// Inflate the activity_select_payment_product_render layout
		LayoutInflater inflater 	= (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View paymentProductLayout 	= inflater.inflate(R.layout.activity_render_payment_product, parent, false);
		
		// Set the belonging accountOnFile on the tag of the row so we can retrieve it when clicked
		paymentProductLayout.findViewById(R.id.paymentProductRow).setTag(accountOnFile);
		
		// Get the TextView and ImageView which will be filled
		TextView accountOnFileTextView 		 = (TextView) paymentProductLayout.findViewById(R.id.paymentProductName);
		ImageView accountOnFileLogoImageView = (ImageView)paymentProductLayout.findViewById(R.id.paymentProductLogo);
		
		// Set the correct value 
		String formattedValue = null;
		for (KeyValuePair attribute : accountOnFile.getAttributes()) {
			
			for (AccountOnFileDisplay displayEntry : accountOnFile.getDisplayHints().getLabelTemplate()) {
				
				if (attribute.getKey().equals(displayEntry.getKey())) {
					
					// Format the value if there is a mask in the accountonfile text
					if (displayEntry.getMask() != null ) {
						StringFormatter stringFormatter = new StringFormatter();
						String maskedValue = stringFormatter.applyMask(displayEntry.getMask().replace("9", "*"), attribute.getValue());
						formattedValue = maskedValue;
					} else {
						formattedValue = attribute.getValue();
					}
				}
			}

		}
		accountOnFileTextView.setText(formattedValue);

		ConnectSDK.INSTANCE.getClientApi().getDrawableFromUrl(
				accountOnFile.getDisplayHints().getLogo(),
				accountOnFileLogoImageView::setBackground,
				(failure) -> Log.e("renderAccountOnFile", "Error message: " + failure.getMessage())
		);

		parent.addView(paymentProductLayout);
	}	
	
}
