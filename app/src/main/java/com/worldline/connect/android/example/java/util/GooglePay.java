/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.worldline.connect.android.example.java.activities.PaymentProductSelectionActivity;
import com.worldline.connect.sdk.client.android.constants.Constants;
import com.worldline.connect.sdk.client.android.model.paymentcontext.PaymentContext;
import com.worldline.connect.sdk.client.android.model.paymentproduct.PaymentProduct;
import com.worldline.connect.sdk.client.android.model.paymentproduct.specificdata.PaymentProduct320SpecificData;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Code required to successfully complete a Google Pay payment
 *
 */
public class GooglePay {

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;
    private static final String TAG = GooglePay.class.getName();

    private final Activity activity;
    private final PaymentContext paymentContext;
    private final PaymentProduct paymentProduct;
    private final String merchantId;
    private final String merchantName;

    public GooglePay(Activity activity, PaymentContext paymentContext, PaymentProduct paymentProduct,
                     String merchantId, String merchantName) {

        if (activity == null) {
            throw new InvalidParameterException("Could not create GooglePay. Activity may not be null.");
        }
        if (paymentContext == null) {
            throw new InvalidParameterException("Could not create GooglePay. PaymentContext may not be null.");
        }
        if (paymentProduct == null || !Constants.PAYMENTPRODUCTID_GOOGLEPAY.equals(paymentProduct.getId())) {
            throw new InvalidParameterException("Could not create GooglePay. PaymentProduct must be set and must be the Google Pay payment product.");
        }
        if (merchantId == null || merchantId.isEmpty()) {
            throw new InvalidParameterException("Could not create GooglePay. MerchantId must be provided.");
        }
        if (merchantName == null || merchantName.isEmpty()) {
            throw new InvalidParameterException("Could not create GooglePay. MerchantName must be provided.");
        }

        this.activity = activity;
        this.paymentContext = paymentContext;
        this.paymentProduct = paymentProduct;
        this.merchantId = merchantId;
        this.merchantName = merchantName;
    }

    /**
     * Set up a Google payments client, create a Google pay payment request and use them to bring up
     * the Google Pay payment sheet.
     * Result handling occurs in the {@link PaymentProductSelectionActivity#onActivityResult(int, int, Intent)} method.
     */
    public void start(boolean isEnvironmentProduction) {

        PaymentsClient mPaymentsClient =
                Wallet.getPaymentsClient(
                        activity,
                        new Wallet.WalletOptions.Builder()
                                .setEnvironment(isEnvironmentProduction ? WalletConstants.ENVIRONMENT_PRODUCTION : WalletConstants.ENVIRONMENT_TEST)
                                .build());

        JSONObject paymentDataRequestJson = createGooglePayRequest(paymentContext, getNetworks(), getGatewayId(), merchantId, merchantName);

        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), activity, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }


    /**
     * This method assembles the Google Pay payment Request.
     *
     * @param paymentContext,   payment-related data
     * @param networks,   Determines the card schemes that the user can use for the payment. Use
     *          the list that is provided in the Google Pay payment product response.
     * @param merchantId,   Your Connect merchant ID
     * @param merchantName,   Your company name in Human Readable form. May be shown to the user.
     */
    private JSONObject createGooglePayRequest(PaymentContext paymentContext, List<String> networks, String gatewayId, String merchantId, String merchantName) {

        JSONObject paymentRequest = new JSONObject();
        // Assemble payment request
        try {
            // Insert API version
            paymentRequest.put("apiVersion", Constants.GOOGLE_API_VERSION);
            paymentRequest.put("apiVersionMinor", 0);

            // Insert merchant info (only for full payment requests)
            JSONObject merchantInfo = new JSONObject()
                    .put("merchantName", merchantName);
            paymentRequest.put("merchantInfo", merchantInfo);

            JSONArray allowedPaymentMethods = new JSONArray();
            JSONObject allowedPaymentMethodsContent = getAllowedPaymentMethodsJson(networks);

            // Insert tokenization specification (only for full payment requests)
            JSONObject tokenizationSpecification = new JSONObject();
            tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
            tokenizationSpecification.put(
                    "parameters",
                    new JSONObject()
                            .put("gateway", gatewayId)
                            .put("gatewayMerchantId", merchantId));
            allowedPaymentMethodsContent.put("tokenizationSpecification", tokenizationSpecification);

            allowedPaymentMethods.put(allowedPaymentMethodsContent);
            paymentRequest.put("allowedPaymentMethods", allowedPaymentMethods);

            // Insert transaction info (only for full payment requests)
            JSONObject transactionInfo = new JSONObject();
            transactionInfo.put("totalPriceStatus", "FINAL");

            /*
             IMPORTANT!
             This code assumes that the payment will be processed in a currency that uses two decimal places.
             Currencies that have a different amount of decimal places, like JPY or KRW, must not be used with this example code.
            */
            String totalPrice = String.valueOf((paymentContext.getAmountOfMoney().getAmount() / 100f));
            transactionInfo.put("totalPrice", totalPrice);

            transactionInfo.put("currencyCode", paymentContext.getAmountOfMoney().getCurrencyCode());
            if (paymentProduct.getAcquirerCountry() != null) {
                transactionInfo.put("countryCode", paymentProduct.getAcquirerCountry());
            }
            paymentRequest.put("transactionInfo", transactionInfo);
        } catch (JSONException e) {
            Log.e(TAG, "Exception occurred while creating JSON object: " + e);
        }

        return paymentRequest;
    }

    private static JSONObject getAllowedPaymentMethodsJson(List<String> networks) {

        JSONArray cardPaymentMethod = new JSONArray();
        JSONArray allowedNetworks = new JSONArray();
        JSONArray allowedAuthMethods = new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");

        // Convert networks and authMethods to JSON objects
        for( String s : networks) {
            allowedNetworks.put(s);
        }

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("type", "CARD");
            parameters.put(
                    "parameters",
                    new JSONObject()
                            .put("allowedAuthMethods", allowedAuthMethods)
                            .put("allowedCardNetworks", allowedNetworks)
            );
            cardPaymentMethod.put(parameters);

        } catch (JSONException e) {
            Log.e(TAG, "Exception occurred while creating JSON object: " + e);
        }

        return parameters;
    }

    private List<String> getNetworks() {

        PaymentProduct320SpecificData paymentProductSpecificData = paymentProduct.getPaymentProduct320SpecificData();

        List<String> networks = paymentProductSpecificData != null ? paymentProductSpecificData.getNetworks() : null;

        if (networks != null && !networks.isEmpty()) {
            return networks;
        } else {
            throw new IllegalStateException("No networks found");
        }
    }

    private String getGatewayId() {

        PaymentProduct320SpecificData paymentProductSpecificData = paymentProduct.getPaymentProduct320SpecificData();

        String gatewayId = paymentProductSpecificData != null ? paymentProductSpecificData.getGateway() : null;

        if (gatewayId != null && !gatewayId.isEmpty()) {
            return gatewayId;
        } else {
            throw new IllegalStateException("No gatewayId found");
        }
    }
}
