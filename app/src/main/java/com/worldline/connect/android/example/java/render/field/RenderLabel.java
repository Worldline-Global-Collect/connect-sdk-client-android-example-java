/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.field;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldline.connect.android.example.java.R;
import com.worldline.connect.android.example.java.translation.Translator;
import com.worldline.connect.sdk.client.android.constants.Constants;
import com.worldline.connect.sdk.client.android.model.paymentproduct.field.PaymentProductField;
import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItem;


/** 
 * This class implements the RenderLabelInterface and 
 * handles the rendering of the label for one paymentproductfield
 *
 */
public class RenderLabel implements RenderLabelInterface {

	@Override
	public TextView renderLabel(PaymentProductField field, BasicPaymentItem selectedPaymentProduct, ViewGroup rowView) {

		TextView label = new TextView(rowView.getContext());

		// Get the labeltext
		Translator translator = Translator.getInstance(rowView.getContext());
		String labelText = translator.getPaymentProductFieldLabel(selectedPaymentProduct.getId(), field.getId());
		if (labelText.contains(Constants.LINK_PLACEHOLDER) && field.getDisplayHints() != null) {
			String linkLabel = translator.getPaymentProductFieldLabel(selectedPaymentProduct.getId(), field.getId() + ".link");
			String link = "<a href=\"" + field.getDisplayHints().getLink() + "\">" + linkLabel + "</a>";
			labelText = labelText.replace(Constants.LINK_PLACEHOLDER, link);
			label.setMovementMethod(LinkMovementMethod.getInstance());
		}

		// Create new label
		label.setTextAppearance(rowView.getContext(), R.style.ListViewTextView);
		label.setText(Html.fromHtml(labelText));
		rowView.addView(label);

		return label;
	}
	
}
