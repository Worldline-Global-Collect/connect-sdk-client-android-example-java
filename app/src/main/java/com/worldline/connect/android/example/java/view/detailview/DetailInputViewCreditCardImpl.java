/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.view.detailview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.IdRes;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.worldline.connect.android.example.java.exception.ViewNotInitializedException;
import com.worldline.connect.android.example.java.render.iinlookup.IinLookupTextWatcher;
import com.worldline.connect.android.example.java.render.iinlookup.RenderIinCoBranding;
import com.worldline.connect.android.example.java.render.persister.InputValidationPersister;
import com.worldline.connect.sdk.client.android.ConnectSDK;
import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItem;
import com.worldline.connect.sdk.client.android.model.validation.ValidationErrorMessage;

import java.util.List;

/**
 * View for the DetailInputActivity with added functionality for credit card payment products
 *
 */
public class DetailInputViewCreditCardImpl extends DetailInputViewImpl implements DetailInputViewCreditCard {

    public static final String CREDIT_CARD_NUMBER_FIELD_ID = "cardNumber";

    private EditText creditCardField;
    private final RenderIinCoBranding coBrandRenderer;

    public DetailInputViewCreditCardImpl(Activity activity, @IdRes int id) {
        super (activity, id);

        coBrandRenderer = new RenderIinCoBranding();
    }

    @Override
    public void initializeCreditCardField(IinLookupTextWatcher iinLookupTextWatcher) {
        creditCardField = (EditText) rootView.findViewWithTag(CREDIT_CARD_NUMBER_FIELD_ID);
        if (creditCardField == null) {
            throw new ViewNotInitializedException("CreditCardField has not been found, did you forget to render the inputfields?");
        }
        creditCardField.addTextChangedListener(iinLookupTextWatcher);
    }

    @Override
    public void renderLuhnValidationMessage(InputValidationPersister inputValidationPersister) {
        renderValidationHelper.renderValidationMessage(new ValidationErrorMessage("luhn", CREDIT_CARD_NUMBER_FIELD_ID, null), null);
    }

    @Override
    public void renderNotAllowedInContextValidationMessage(InputValidationPersister inputValidationPersister) {
        renderValidationHelper.renderValidationMessage(new ValidationErrorMessage("allowedInContext", CREDIT_CARD_NUMBER_FIELD_ID, null), null);
    }

    @Override
    public void removeCreditCardValidationMessage(InputValidationPersister inputValidationPersister) {
        renderValidationHelper.removeValidationMessage(renderInputFieldsLayout, CREDIT_CARD_NUMBER_FIELD_ID, inputValidationPersister);
    }

    @Override
    public void renderPaymentProductLogoInCreditCardField(String logoUrl) {

        ConnectSDK.INSTANCE.getClientApi().getDrawableFromUrl(
                logoUrl,
                (drawable) -> {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    if (bitmapDrawable != null) {

                    int scaledHeight = (int) creditCardField.getTextSize();
                    int scaledWidth = (int) (bitmapDrawable.getIntrinsicWidth() * ((double) scaledHeight / (double) bitmapDrawable.getIntrinsicHeight()));

                    Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), scaledWidth, scaledHeight, true);
                    Drawable resizedDrawable = new BitmapDrawable(rootView.getContext().getResources(), resizedBitmap);

                    // Set compoundDrawables allow you to place a image at a certain position
                    creditCardField.setCompoundDrawablesWithIntrinsicBounds(null, null, resizedDrawable, null);
                }},
                (failure) -> Log.e("renderPPLogoInCCField", "Error message: " + failure.getMessage())
        );
    }

    @Override
    public void removeDrawableInEditText() {
        creditCardField.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    @Override
    public void renderCoBrandNotification(List<BasicPaymentItem> paymentProductsAllowedInContext, View.OnClickListener listener) {
        // Show the user he can choose another cobrand if there are indeed more cobrands available
        if (paymentProductsAllowedInContext.size() > 1) {

            coBrandRenderer.renderIinCoBrandNotification(rootView.getContext(),
                    paymentProductsAllowedInContext,
                    renderInputFieldsLayout,
                    CREDIT_CARD_NUMBER_FIELD_ID,
                    listener);
        }
    }

    @Override
    public void removeCoBrandNotification() {
        coBrandRenderer.removeIinCoBrandNotification(renderInputFieldsLayout, CREDIT_CARD_NUMBER_FIELD_ID);
    }
}
