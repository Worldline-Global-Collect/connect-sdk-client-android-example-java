/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.field;

import android.view.View;
import android.view.ViewGroup;

import com.worldline.connect.android.example.java.render.persister.InputDataPersister;
import com.worldline.connect.sdk.client.android.model.paymentcontext.PaymentContext;
import com.worldline.connect.sdk.client.android.model.paymentproduct.field.PaymentProductField;

/**
 * Defines the rendering of inputfields interface
 *
 */
public interface RenderInputFieldInterface {


	/***
	 * Renders an inputfield by the data in the PaymentProductField.
	 * This inputfield is added to the given ViewGroup
	 *
	 * @param field, PaymentProductField containing all data for the inputfield
	 * @param inputDataPersister, the selected PaymentProduct, used for getting the correct translations
	 * @param rowView, the ViewGroup to which the rendered inputfield is added
	 * @param paymentContext, the paymentContext of the current payment
	 *
	 * @return the rendered view
	 */
	View renderField(PaymentProductField field, InputDataPersister inputDataPersister,
							ViewGroup rowView, PaymentContext paymentContext);

}
