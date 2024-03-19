/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.field;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldline.connect.sdk.client.android.model.paymentproduct.field.PaymentProductField;
import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItem;


/**
 * Defines the rendering of label interface methods
 *
 */
public interface RenderLabelInterface {
	
	
	/**
	 * Renders a tooltip by the data in the PaymentProductField.
	 * This label is added to the given rowView
	 * 
	 * @param selectedPaymentProduct, the selected PaymentProduct, used for getting the correct translations
	 * @param rowView, the ViewGroup to which the rendered label is added
	 */
	public TextView renderLabel(PaymentProductField field, BasicPaymentItem selectedPaymentProduct, ViewGroup rowView);
	
}
