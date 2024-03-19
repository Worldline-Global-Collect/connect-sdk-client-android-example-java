/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.iinlookup;

import android.text.Editable;
import android.text.TextWatcher;

import com.worldline.connect.sdk.client.android.model.paymentcontext.PaymentContext;

import java.security.InvalidParameterException;

/**
 * Android TextWatcher that is put on Creditcardnumber fields so an IIN lookup can be done
 *
 */
public class IinLookupTextWatcher implements TextWatcher {

	private IinLookupTextWatcherAfterTextChangedListener listener;

	// Payment context information that is sent with the IIN lookup
	private PaymentContext paymentContext;

	// Workaround for having twice called the afterTextChanged
	private String previousEnteredValue = "";


	/**
	 * Constructor
	 *
	 * @param listener,       OnIinLookupComplete which will be called by the AsyncTask
	 * @param paymentContext, Payment context that will be used in the request for getting IinDetails
	 */
	public IinLookupTextWatcher(IinLookupTextWatcherAfterTextChangedListener listener, PaymentContext paymentContext) {

		if (listener == null) {
			throw new InvalidParameterException("Error creating IinLookupTextWatcher, listener may not be null");
		}
		if (paymentContext == null) {
			throw new InvalidParameterException("Error creating IinLookupTextWatcher, paymentContext may not be null");
		}

		this.listener = listener;
		this.paymentContext = paymentContext;
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}


	@Override
	public void afterTextChanged(Editable s) {

		// Strip the spaces that are added by Masking
		String currentEnteredValue = s.toString().replace(" ", "");

		// Retrieve IIN Details if the first 8 digits have changed, and the length of the current
		// value is at least six.
		// The IIN Lookup may return different results depending on the length of the initial
		// digits, between length 6 and 8.
		if (currentEnteredValue.length() >= 6 && isOneOfFirst8DigitsChanged(currentEnteredValue)) {
			listener.issuerIdentificationNumberChanged(currentEnteredValue);
		}

		// Update the previousEnteredValue
		previousEnteredValue = currentEnteredValue;
	}

	private boolean isOneOfFirst8DigitsChanged(String currentEnteredValue) {
		// Add some padding to make sure there are 8 characters to compare
		String currentPadded = currentEnteredValue + "xxxxxxxx";
		String previousPadded = previousEnteredValue + "xxxxxxxx";

		return !currentPadded.substring(0, 8).equals(previousPadded.substring(0, 8));
	}

	public interface IinLookupTextWatcherAfterTextChangedListener {
		void issuerIdentificationNumberChanged(String currentCardNumber);
	}
}
