/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.activities;

import static com.worldline.connect.sdk.client.android.constants.Constants.GOOGLE_PAY_TOKEN_FIELD_ID;
import static com.worldline.connect.sdk.client.android.constants.Constants.PAYMENTPRODUCTGROUPID_CARDS;
import static com.worldline.connect.sdk.client.android.constants.Constants.PAYMENTPRODUCTID_BOLETOBANCARIO;
import static com.worldline.connect.sdk.client.android.constants.Constants.PAYMENTPRODUCTID_BanContact;
import static com.worldline.connect.sdk.client.android.constants.Constants.PAYMENTPRODUCTID_GOOGLEPAY;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.gson.Gson;
import com.worldline.connect.android.example.java.R;
import com.worldline.connect.android.example.java.configuration.CheckCommunication;
import com.worldline.connect.android.example.java.configuration.Constants;
import com.worldline.connect.android.example.java.model.MerchantAction;
import com.worldline.connect.android.example.java.model.ShoppingCart;
import com.worldline.connect.android.example.java.util.GooglePay;
import com.worldline.connect.android.example.java.view.selectionview.ProductSelectionView;
import com.worldline.connect.android.example.java.view.selectionview.ProductSelectionViewImpl;
import com.worldline.connect.sdk.client.android.ConnectSDK;
import com.worldline.connect.sdk.client.android.model.paymentrequest.EncryptedPaymentRequest;
import com.worldline.connect.sdk.client.android.model.paymentrequest.PaymentRequest;
import com.worldline.connect.sdk.client.android.model.accountonfile.AccountOnFile;
import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItems;
import com.worldline.connect.sdk.client.android.model.paymentproduct.BasicPaymentProduct;
import com.worldline.connect.sdk.client.android.model.paymentproductgroup.BasicPaymentProductGroup;
import com.worldline.connect.sdk.client.android.model.paymentitem.PaymentItem;
import com.worldline.connect.sdk.client.android.model.paymentproduct.PaymentProduct;
import com.worldline.connect.sdk.client.android.model.paymentproductgroup.PaymentProductGroup;
import com.worldline.connect.sdk.client.android.network.ApiErrorResponse;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * Activity that lists all the available payment options
 */
public class PaymentProductSelectionActivity extends ShoppingCartActivity implements DialogInterface.OnClickListener {

    private static final String TAG = PaymentProductSelectionActivity.class.getName();

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;

    // The view belonging to this activity
    private ProductSelectionView selectionView;

    private ShoppingCart shoppingCart;
    private AccountOnFile accountOnFile;
    private String merchantId;
    private String merchantName;
    private PaymentProduct paymentProduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment_product);
        // Initialize the shoppingcart
        super.initialize(this);

        selectionView = new ProductSelectionViewImpl(this, R.id.payment_product_selection_view_layout);

        // This activity won't work without internet, so show an error message if there is no
        // connection
        if (!CheckCommunication.isOnline(this)) {
            selectionView.showNoInternetDialog(this);
            return;
        }

        loadIntentData();

        selectionView.showLoadingIndicator();
        ConnectSDK.INSTANCE.getClientApi().getPaymentItems(
                this::basicPaymentItemsSuccess,
                this::apiError,
                this::failure
        );
    }

    private void basicPaymentItemsSuccess(BasicPaymentItems basicPaymentItems) {
        // Loaded payment product and selected Account On File information
        selectionView.renderDynamicContent(basicPaymentItems);
        selectionView.hideLoadingIndicator();
    }

    private void apiError(ApiErrorResponse apiErrorResponse) {
        selectionView.hideLoadingIndicator();
        selectionView.showTechnicalErrorDialog(this);
    }

    private void failure(Throwable throwable) {
        selectionView.hideLoadingIndicator();
        selectionView.showTechnicalErrorDialog(this);
    }

    private void loadIntentData() {
        Intent intent = getIntent();
        merchantId = intent.getStringExtra(Constants.MERCHANT_MERCHANT_IDENTIFIER);
        merchantName = intent.getStringExtra(Constants.MERCHANT_NAME);
        shoppingCart = (ShoppingCart) intent.getSerializableExtra(Constants.INTENT_SHOPPINGCART);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // The callback method for when a user selects a payment product
    public void onPaymentProductSelected(View v) {
        selectionView.showLoadingIndicator();
        if (v.getTag() instanceof BasicPaymentProduct) {
            String paymentProductId = ((BasicPaymentProduct) v.getTag()).getId();
            ConnectSDK.INSTANCE.getClientApi().getPaymentProduct(
                    paymentProductId,
                    this::paymentProductSuccess,
                    this::apiError,
                    this::failure
            );

        } else if (v.getTag() instanceof BasicPaymentProductGroup) {
            String paymentProductGroupId = ((BasicPaymentProductGroup) v.getTag()).getId();
            ConnectSDK.INSTANCE.getClientApi().getPaymentProductGroup(
                    paymentProductGroupId,
                    this::paymentProductGroupSuccess,
                    this::apiError,
                    this::failure
            );

        } else if (v.getTag() instanceof AccountOnFile) {
            // Store the Account on file so it can be added to the intent later on.
            accountOnFile = (AccountOnFile) v.getTag();
            ConnectSDK.INSTANCE.getClientApi().getPaymentProduct(
                    accountOnFile.getPaymentProductId(),
                    this::paymentProductSuccess,
                    this::apiError,
                    this::failure
            );
        } else {
            throw new InvalidParameterException("Tag in view is not of a valid type");
        }
    }

    private void paymentProductSuccess(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
        handlePaymentItemCallBack(paymentProduct);
    }

    private void paymentProductGroupSuccess(PaymentProductGroup paymentProductGroup) {
        handlePaymentItemCallBack(paymentProductGroup);
    }

    private void encryptPaymentRequestSuccess(EncryptedPaymentRequest encryptedPaymentRequest) {
        Intent paymentResultIntent = new Intent(this, PaymentResultActivity.class);

        //put shopping cart inside the intent
        paymentResultIntent.putExtra(Constants.INTENT_SHOPPINGCART, shoppingCart);
        startActivity(paymentResultIntent);
    }

    // Determine what view should be served next, based on whether the product has inputfields.
    // For some products special rules apply, which are also handled here.
    private void handlePaymentItemCallBack(PaymentItem paymentItem) {
        selectionView.hideLoadingIndicator();
        if (PAYMENTPRODUCTID_GOOGLEPAY.equals(paymentItem.getId())) {
            // Google pay requires a different flow even though it has fields. The fields should
            // not be shown to the user, but they will be filled after the user has completed the
            // Google pay flow.
            startGooglePay((PaymentProduct) paymentItem);

        } else if (PAYMENTPRODUCTID_BanContact.equals(paymentItem.getId())) {
            // The getPaymentProduct call does not yield fields for the BCMC payment product,
            // however there are inputfields for BCMC, that should be retrieved via your payment
            // server.
            retrievePaymentProductFieldsBcmc(paymentItem);

        } else if (paymentItem.getPaymentProductFields().isEmpty()) {
            // For payment products that do not have fields configured other actions are required to
            // complete the payment. These actions may be a redirect to an external payment products'
            // website, or showing instructions to complete the payment.
            // Currently this app does not contain examples for these kinds of products. Instead we
            // will go to the result activity directly.
            startNextActivity(new Intent(this, PaymentResultActivity.class));

        } else {
            // Ask the user for its payment details in the next activity
            determineAndStartDetailInputActivity(paymentItem);

        }
    }

    private void startGooglePay(PaymentProduct paymentProduct) {
        GooglePay googlePay = new GooglePay(this, ConnectSDK.INSTANCE.getPaymentConfiguration().getPaymentContext(), paymentProduct, merchantId, merchantName);
        googlePay.start(false);
        selectionView.hideLoadingIndicator();
    }

    private void retrievePaymentProductFieldsBcmc(PaymentItem bcmc) {
        if (PAYMENTPRODUCTID_BanContact.equals(bcmc.getId())) {
            // Because the BanContact payment product (BCMC, PPid 3012) has, apart from regular
            // credit card inputFields, other ways for the customer to complete the transaction, it
            // is required that a payment is created first, before rendering the Input Fields. A create
            // payment call cannot be made via this Client 2 Server API, but should be made from your
            // payment server instead. You should add code here that triggers that payment and then
            // have the result sent back to the Client.
            // Since this is an example application, we will simply pretend that the call has been
            // made and load an example JSON response from the resources. This resource is not the
            // full createPaymentResponse, but only the MerchantAction, which contains the fields and
            // showdata required to render the qrCode/button for BCMC.
            Reader reader = new InputStreamReader(getResources().openRawResource(R.raw.bcmc_merchantaction_example));
            MerchantAction bcmcMerchantActionExample = new Gson().fromJson(reader, MerchantAction.class);

            // Add the payment product fields from the merchantAction to the payment item, so that the
            // DetailInputActivity can render them as usual.
            ((PaymentProduct) bcmc).setPaymentProductFields(bcmcMerchantActionExample.getFormFields());
            Intent bcmcDetailInputActivityIntent = new Intent(this, DetailInputActivityBCMC.class);
            bcmcDetailInputActivityIntent.putExtra(Constants.INTENT_SELECTED_ITEM, bcmc);
            bcmcDetailInputActivityIntent.putExtra(Constants.INTENT_BCMC_SHOWDATA, ((Serializable) bcmcMerchantActionExample.getShowData()));
            startNextActivity(bcmcDetailInputActivityIntent);

        } else {
            throw new InvalidParameterException("Can not retrieve fields for BCMC, payment item is not BCMC");
        }
    }

    private void determineAndStartDetailInputActivity(PaymentItem paymentItem) {
        // Determine what DetailInputActivity to load. The base-class just renders the fields and
        // performs default validation on the fields. In some cases this is not enough however. In
        // these cases a subclass of the DetailInputActivity will be loaded that has additional
        // functionality for these specific products/methods.
        Intent detailInputActivityIntent = null;
        if (paymentItem instanceof PaymentProductGroup && PAYMENTPRODUCTGROUPID_CARDS.equals(paymentItem.getId())
                || ((PaymentProduct) paymentItem).getPaymentMethod().equals("card")) {
            detailInputActivityIntent = new Intent(this, DetailInputActivityCreditCards.class);

        } else if (PAYMENTPRODUCTID_BOLETOBANCARIO.equals(paymentItem.getId())) {
            detailInputActivityIntent = new Intent(this, DetailInputActivityBoletoBancario.class);

        } else {
            detailInputActivityIntent = new Intent(this, DetailInputActivity.class);

        }
        detailInputActivityIntent.putExtra(Constants.INTENT_SELECTED_ITEM, paymentItem);
        startNextActivity(detailInputActivityIntent);
    }

    private void startNextActivity(Intent detailInputActivityIntent) {
        // Add data to intent for the detail activity input
        detailInputActivityIntent.putExtra(Constants.INTENT_SELECTED_ACCOUNT_ON_FILE, accountOnFile);
        detailInputActivityIntent.putExtra(Constants.INTENT_SHOPPINGCART, shoppingCart);

        // Start the intent
        startActivity(detailInputActivityIntent);
        selectionView.hideLoadingIndicator();
    }

    // Handle the result returned by the Google Pay client. In case the payment is successful,
    // the payment result page is loaded.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectionView.showLoadingIndicator();
        // value passed in AutoResolveHelper
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    PaymentData paymentData = PaymentData.getFromIntent(data);

                    String jsonString = paymentData.toJson();
                    try {
                        JSONObject jsonJSON = new JSONObject(jsonString);

                        JSONObject paymentMethodData = jsonJSON.getJSONObject("paymentMethodData");
                        JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
                        String encryptedPaymentData = tokenizationData.getString("token");

//                            String encryptedPaymentData = token;

                        PaymentRequest paymentRequest = new PaymentRequest();
                        paymentRequest.setPaymentProduct(paymentProduct);
                        paymentRequest.setValue(GOOGLE_PAY_TOKEN_FIELD_ID, encryptedPaymentData);
                        paymentRequest.validate();

                        ConnectSDK.INSTANCE.encryptPaymentRequest(
                                paymentRequest,
                                this::encryptPaymentRequestSuccess,
                                this::failure
                        );
                    } catch (Exception e) {
                        Log.e(TAG, "Could not parse malformed JSON: \"" + jsonString + "\"");
                    }

                    break;
                case RESULT_CANCELED:
                    Log.i(TAG, "Google Pay payment was cancelled");
                    selectionView.hideLoadingIndicator();
                    break;
                case AutoResolveHelper.RESULT_ERROR:
                    Status status = AutoResolveHelper.getStatusFromIntent(data);
                    Log.e(TAG, "Something went wrong whilst making a Google Pay payment; errorCode: " + status);
                    selectionView.hideLoadingIndicator();
                    selectionView.showTechnicalErrorDialog(this);
                    break;
                default:
                    selectionView.hideLoadingIndicator();
                    selectionView.showTechnicalErrorDialog(this);
                    Log.e(TAG, "Something went wrong whilst making a Google Pay payment");
            }
        }
    }

    @Override
    // Callback for the the ok button on the error dialogs
    public void onClick(DialogInterface dialogInterface, int which) {
        // When an error has occurred the Activity is no longer valid. Destroy it and go one step back
        finish();
    }
}
