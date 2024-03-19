/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.activities;

import static com.worldline.connect.sdk.client.android.constants.Constants.PAYMENTPRODUCTID_BanContact;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.worldline.connect.android.example.java.configuration.Constants;
import com.worldline.connect.android.example.java.exception.IinStatusNotKnownException;
import com.worldline.connect.android.example.java.render.iinlookup.IinLookupTextWatcher;
import com.worldline.connect.android.example.java.render.persister.IinDetailsPersister;
import com.worldline.connect.android.example.java.view.detailview.DetailInputViewCreditCard;
import com.worldline.connect.android.example.java.view.detailview.DetailInputViewCreditCardImpl;
import com.worldline.connect.sdk.client.android.ConnectSDK;
import com.worldline.connect.android.example.java.R;
import com.worldline.connect.sdk.client.android.model.iindetails.IinDetail;
import com.worldline.connect.sdk.client.android.model.iindetails.IinDetailsResponse;
import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItem;
import com.worldline.connect.sdk.client.android.model.paymentproduct.PaymentProduct;
import com.worldline.connect.sdk.client.android.network.ApiErrorResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Extension to DetailInputActivity that adds extra functionality for credit card payment products
 *

 */
public class DetailInputActivityCreditCards extends DetailInputActivity
        implements IinLookupTextWatcher.IinLookupTextWatcherAfterTextChangedListener,
        View.OnClickListener {

    private DetailInputViewCreditCard fieldView;

    private IinDetailsPersister iinDetailsPersister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView will be called by super

        fieldView = new DetailInputViewCreditCardImpl(this, R.id.detail_input_view_layout_fields_and_buttons);

        iinDetailsPersister = new IinDetailsPersister();

        if (savedInstanceState != null) {
            initializeSavedInstanceStateData(savedInstanceState);
        }
    }

    private void initializeSavedInstanceStateData(Bundle savedInstanceState) {
        if (savedInstanceState.getSerializable(Constants.BUNDLE_IINDETAILSPERSISTER) != null) {
            iinDetailsPersister = (IinDetailsPersister) savedInstanceState.getSerializable(Constants.BUNDLE_IINDETAILSPERSISTER);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        IinLookupTextWatcher iinLookupTextWatcher = new IinLookupTextWatcher(this, ConnectSDK.INSTANCE.getPaymentConfiguration().getPaymentContext());
        fieldView.initializeCreditCardField(iinLookupTextWatcher);

        if (inputDataPersister.isPaymentProduct()) {
            fieldView.renderPaymentProductLogoInCreditCardField(inputDataPersister.getPaymentItem().getDisplayHints().getLogoUrl());
        }

        if (iinDetailsPersister.getIinDetailsResponse() != null) {
            getCoBrandProductsAndRenderNotification(iinDetailsPersister.getIinDetailsResponse());
        }
    }

    // This function alters the view so that it matches the IinStatus "UNKNOWN"
    private void handleIinStatusUnknown() {
        fieldView.renderLuhnValidationMessage(inputValidationPersister);
        fieldView.removeDrawableInEditText();
        fieldView.removeCoBrandNotification();
        fieldView.deactivatePayButton();
    }

    // This function alters the view so that it matches the IinStatus "NOT_ENOUGH_DIGITS"
    private void handleIinStatusNotEnoughDigits() {
        fieldView.removeCreditCardValidationMessage(inputValidationPersister);
        fieldView.removeCoBrandNotification();
        if (!inputDataPersister.isPaymentProduct()) {
            fieldView.deactivatePayButton();
        }
    }

    // This function alters the. view so that it matches the IinStatus "EXISTING_BUT_NOT_ALLOWED"
    private void handleIinStatusExistingButNotAllowed() {
        fieldView.renderNotAllowedInContextValidationMessage(inputValidationPersister);
        fieldView.removeDrawableInEditText();
        fieldView.removeCoBrandNotification();
        fieldView.deactivatePayButton();
    }

    // This function alters the view so that it is compliable with the IinStatus "EXISTING_BUT_NOT_ALLOWED"
    private void handleIinStatusSupported(IinDetailsResponse response) {
        fieldView.removeCreditCardValidationMessage(inputValidationPersister);
        fieldView.activatePayButton();

        // Find whether the brand, chosen by the user on the payment product selection screen, is in the IinResponse
        // and whether the chosen product can be payed with in the current paymentcontext
        List<IinDetail> coBrands = response.getCoBrands();
        if (coBrands != null) {
            for (IinDetail coBrand : coBrands) {
                if (coBrand.isAllowedInContext() && inputDataPersister.getPaymentItem().getId().equals(coBrand.getPaymentProductId())) {

                    // Show the corresponding logo for the Payement Product
                    fieldView.renderPaymentProductLogoInCreditCardField(inputDataPersister.getPaymentItem().getDisplayHints().getLogoUrl());

                    // Show the user that he can possibly switch to an other brand with the same card number
                    getCoBrandProductsAndRenderNotification(response);
                    return;
                }
            }
        }

        // We now know that the user entered a completely different payment product, retrieve this
        // product and then rerender the view from the getPaymentProduct callback
        retrieveNewPaymentProduct(response.getPaymentProductId());
    }

    private void getCoBrandProductsAndRenderNotification(IinDetailsResponse response) {

        // If the currently known iinDetailsResponse within this activity is not null, check whether cobrand notifications need to be shown.
        if (response != null) {

            // Retrieve the cobrands from the iinDetailsResponse
            final List<IinDetail> coBrands = response.getCoBrands();

            // Remove all cobrands that cannot be payed with
            if (coBrands != null && !coBrands.isEmpty()) {

                // Create a list to store all allowed paymentProducts
                final List<BasicPaymentItem> paymentProductsAllowedInContext = new ArrayList<>(4);

                // Counter
                final AtomicInteger count = new AtomicInteger(coBrands.size());
                // Add the allowed paymentProducts to the list
                for (IinDetail iinDetail : coBrands) {
                    if (iinDetail.isAllowedInContext()) {

                        ConnectSDK.INSTANCE.getClientApi().getPaymentProduct(
                                iinDetail.getPaymentProductId(),
                                (paymentProduct) -> {
                                    paymentProductsAllowedInContext.add(paymentProduct);
                                    if (count.decrementAndGet() < 1) {
                                        fieldView.renderCoBrandNotification(paymentProductsAllowedInContext, DetailInputActivityCreditCards.this);
                                    }
                                },
                                this::apiError,
                                this::DetailInputActivityFailure
                        );
                    }
                }
            }
        }
    }

    /**
     * This method is invoked when the user selects a different payment product from the CoBrand
     * notification view.
     */
    @Override
    public void onClick(View view) {

        // Retrieve the PaymentProduct from the view
        PaymentProduct paymentProduct = (PaymentProduct) view.getTag();

        // Update the logo in the edit text
        fieldView.renderPaymentProductLogoInCreditCardField(paymentProduct.getDisplayHints().getLogoUrl());

        // Update the request to use the new paymentProduct
        inputDataPersister.setPaymentItem(paymentProduct);

        retrieveNewPaymentProduct(paymentProduct.getId());
    }

    private void retrieveNewPaymentProduct(String paymentProductId) {
        if (paymentProductId.equals(PAYMENTPRODUCTID_BanContact)) {
            ConnectSDK.INSTANCE.getPaymentConfiguration().getPaymentContext().setForceBasicFlow(true);
        }

        ConnectSDK.INSTANCE.getClientApi().getPaymentProduct(
                paymentProductId,
                this::paymentProductSuccess,
                this::apiError,
                this::DetailInputActivityFailure
        );

    }

    private void paymentProductSuccess(PaymentProduct paymentProduct) {
        inputDataPersister.setPaymentItem(paymentProduct);
        View v = fieldView.getViewWithFocus();
        if (v instanceof EditText) {
            inputDataPersister.setFocusFieldId((String) v.getTag());
            inputDataPersister.setCursorPosition(((EditText) v).getSelectionStart());
        }

        fieldView.removeAllFieldViews();
        rendered = false;

        // Call renderDynamicContent and onStart to rerender all views
        renderDynamicContent();
        onStart();
    }

    private void getIINDetailsSuccess(IinDetailsResponse iinDetailsResponse) {
        iinDetailsPersister.setIinDetailsResponse(iinDetailsResponse);


        switch (iinDetailsResponse.getStatus()) {
            case UNKNOWN:
                handleIinStatusUnknown();
                break;
            case NOT_ENOUGH_DIGITS:
                handleIinStatusNotEnoughDigits();
                break;
            case EXISTING_BUT_NOT_ALLOWED:
                handleIinStatusExistingButNotAllowed();
                break;
            case SUPPORTED:
                handleIinStatusSupported(iinDetailsResponse);
                break;
            default:
                throw new IinStatusNotKnownException("This IinStatus is not known");
        }
    }

    private void apiError(ApiErrorResponse apiErrorResponse) {
        Log.e("ActivityCreditCards", "PaymentProductFailure: " + apiErrorResponse.getErrors().get(0).getMessage());
    }

    private void DetailInputActivityFailure(Throwable throwable) {
        Log.e("ActivityCreditCards", "PaymentProductFailure: ", throwable);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constants.BUNDLE_IINDETAILSPERSISTER, iinDetailsPersister);
    }

    @Override
    public void issuerIdentificationNumberChanged(String currentCardNumber) {
        ConnectSDK.INSTANCE.getClientApi().getIINDetails(
                currentCardNumber,
                this::getIINDetailsSuccess,
                this::apiError,
                this::DetailInputActivityFailure
        );
    }
}
