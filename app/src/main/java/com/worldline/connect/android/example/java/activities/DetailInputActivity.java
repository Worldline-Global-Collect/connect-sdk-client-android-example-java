/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.worldline.connect.android.example.java.R;
import com.worldline.connect.android.example.java.configuration.Constants;
import com.worldline.connect.android.example.java.model.ShoppingCart;
import com.worldline.connect.android.example.java.render.persister.InputDataPersister;
import com.worldline.connect.android.example.java.render.persister.InputValidationPersister;
import com.worldline.connect.android.example.java.view.detailview.DetailInputViewImpl;
import com.worldline.connect.sdk.client.android.ConnectSDK;
import com.worldline.connect.sdk.client.android.model.paymentrequest.EncryptedPaymentRequest;
import com.worldline.connect.sdk.client.android.model.accountonfile.AccountOnFile;
import com.worldline.connect.sdk.client.android.model.paymentitem.PaymentItem;
import com.worldline.connect.sdk.client.android.model.paymentproduct.PaymentProduct;
import com.worldline.connect.sdk.client.android.model.validation.ValidationErrorMessage;

import java.util.List;


/**
 * DetailInputActivity in which users will be asked for their payment details.
 * <p>

 */
public class DetailInputActivity extends ShoppingCartActivity {

    protected DetailInputViewImpl fieldView;

    // The PaymentItem and possibly Account On File for which the input details are requested in this Activity
    protected PaymentItem paymentItem;
    protected AccountOnFile accountOnFile;

    protected InputDataPersister inputDataPersister;
    protected ShoppingCart shoppingCart;

    protected InputValidationPersister inputValidationPersister;

    // Boolean that will determine whether the view has already been rendered, or needs to be rendered again
    protected boolean rendered;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_render_payment_input);
        // Initialize the shoppingcart
        super.initialize(this);

        // Set 'rendered' to false, as we know that nothing is rendered since onCreate has been called
        rendered = false;

        fieldView = new DetailInputViewImpl(this, R.id.detail_input_view_layout_fields_and_buttons);

        loadIntentData();

        inputDataPersister = new InputDataPersister(paymentItem);
        if (accountOnFile != null) {
            inputDataPersister.setAccountOnFile(accountOnFile);
        }

        inputValidationPersister = new InputValidationPersister();

        if (savedInstanceState != null) {
            initializeSavedInstanceStateData(savedInstanceState);
        }

        renderDynamicContent();
    }

    private void loadIntentData() {
        Intent intent   = getIntent();
        paymentItem     = (PaymentItem)    intent.getSerializableExtra(Constants.INTENT_SELECTED_ITEM);
        accountOnFile   = (AccountOnFile)  intent.getSerializableExtra(Constants.INTENT_SELECTED_ACCOUNT_ON_FILE);
        shoppingCart    = (ShoppingCart)   intent.getSerializableExtra(Constants.INTENT_SHOPPINGCART);
    }

    private void initializeSavedInstanceStateData(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.getSerializable(Constants.BUNDLE_INPUTDATAPERSISTER) != null) {
            inputDataPersister = (InputDataPersister) savedInstanceState.getSerializable(Constants.BUNDLE_INPUTDATAPERSISTER);
        }
        if (savedInstanceState.getSerializable(Constants.BUNDLE_INPUTVALIDATIONPERSISTER) != null) {
            inputValidationPersister = (InputValidationPersister) savedInstanceState.getSerializable(Constants.BUNDLE_INPUTVALIDATIONPERSISTER);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    protected void renderDynamicContent() {
        if (!rendered) {
            fieldView.renderDynamicContent(inputDataPersister, ConnectSDK.INSTANCE.getPaymentConfiguration().getPaymentContext(), inputValidationPersister);
            initializeRememberMeCheckBox();
            if (inputDataPersister.isPaymentProduct()) {
                fieldView.activatePayButton();
            } else {
                fieldView.deactivatePayButton();
            }
            fieldView.setFocusAndCursorPosition(inputDataPersister.getFocusFieldId(), inputDataPersister.getCursorPosition());
            rendered = true;
        }
    }

    private void initializeRememberMeCheckBox() {
        // Show remember me checkbox when storing as account on file is allowed
        if (inputDataPersister.isPaymentProduct()) {
            PaymentProduct paymentProduct = (PaymentProduct) inputDataPersister.getPaymentItem();
            if (!paymentProduct.autoTokenized() && 		// Not already automatically tokenized?
                    paymentProduct.allowsTokenization() && // Tokenization allowed?
                    inputDataPersister.getAccountOnFile() == null) {			// AccountOnFile not already set?

                fieldView.renderRememberMeCheckBox(inputDataPersister.isRememberMe());
            }
        }
    }

    // The callback method for when the user presses "pay"
    public void submitInputFields(View v) {
        // Remove all validation error messages and tooltip texts, maybe re-render them when we know
        // that the input is still not correct
        hideTooltipAndErrorViews();

        // Validate the input
        List<ValidationErrorMessage> errorMessages = inputValidationPersister.storeAndValidateInput(inputDataPersister);

        // If there is invalid input, render error messages
        if (errorMessages.isEmpty()) {

            fieldView.showLoadDialog();

            ConnectSDK.INSTANCE.encryptPaymentRequest(
                    inputValidationPersister.getPaymentRequest(),
                    this::encryptedPaymentRequestSuccess,
                    this::failure
            );
        } else {
            // Render validation messages for the invalid fields
            renderValidationMessages();
        }
    }

    private void encryptedPaymentRequestSuccess(EncryptedPaymentRequest encryptedPaymentRequest) {
        navigateToPaymentResultActivity(encryptedPaymentRequest, null);
    }

    private void failure(Throwable throwable) {
        navigateToPaymentResultActivity(null, throwable.getMessage());
    }

    private void navigateToPaymentResultActivity(EncryptedPaymentRequest encryptedPaymentRequest, String errorMessage) {
        fieldView.hideLoadDialog();
        Intent paymentResultIntent = new Intent(this, PaymentResultActivity.class);

        //put shopping cart and payment request inside the intent
        paymentResultIntent.putExtra(Constants.INTENT_SHOPPINGCART, shoppingCart);

        if (encryptedPaymentRequest != null) {
            paymentResultIntent.putExtra(Constants.INTENT_PAYMENT_ENCRYPTED_FIELDS, encryptedPaymentRequest.getEncryptedFields());
        } else {
            paymentResultIntent.putExtra(Constants.INTENT_PAYMENT_ENCRYPTED_FIELDS, "");
            paymentResultIntent.putExtra(Constants.INTENT_ERRORMESSAGE, errorMessage);
        }

        startActivity(paymentResultIntent);
    }

    protected void hideTooltipAndErrorViews() {
        fieldView.hideTooltipAndErrorViews(inputValidationPersister);
    }

    protected void renderValidationMessages() {
        fieldView.renderValidationMessages(inputValidationPersister, inputDataPersister.getPaymentItem());
    }

    // The callback method for when the user alters the rememberMe checkbox
    public void rememberMeClicked(View v) {
        CheckBox checkBox = (CheckBox) v;
        inputDataPersister.setRememberMe(checkBox.isChecked());
    }

    // The callback method for when the user presses "cancel"
    public void backToPaymentProductScreen(View v) {
        this.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the current active field and cursorposition
        View v = fieldView.getViewWithFocus();
        if (v instanceof EditText) {
            inputDataPersister.setFocusFieldId((String) v.getTag());
            inputDataPersister.setCursorPosition(((EditText) v).getSelectionStart());
        }

        outState.putSerializable(Constants.BUNDLE_INPUTDATAPERSISTER, inputDataPersister);
        outState.putSerializable(Constants.BUNDLE_INPUTVALIDATIONPERSISTER, inputValidationPersister);
        outState.putBoolean(Constants.BUNDLE_RENDERED, rendered);

        super.onSaveInstanceState(outState);
    }
}
