/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.validation;
import android.view.ViewGroup;

/**
 * Defines the rendering of tooltip interface methods
 *
 */
public interface RenderValidationMessageInterface {
	
	
	/**
	 * Renders validation message by the data in the PaymentProductField.
	 * This validation message is added to the given ViewGroup
	 * 
	 * @param validationMessage, the message that must be shown
	 * @param rowView, the ViewGroup to which the validationMessage is added
	 * @param fieldId, PaymentProductFieldid used for rendering validationmessage
	 */
	public void renderValidationMessage(String validationMessage, ViewGroup rowView, String fieldId);
	
	
	/**
	 * Removes a validationmessage for the given PaymentProductField
	 * @param rowView, the row which contains the showing validationMessage
	 * @param fieldId, the PaymentProductField for which the validationMessage will be removed
	 */
	public void removeValidationMessage(ViewGroup rowView, String fieldId);
}
