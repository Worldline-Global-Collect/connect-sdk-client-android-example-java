/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.activities;

import static com.worldline.connect.android.example.java.configuration.Constants.APPLICATION_IDENTIFIER;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.worldline.connect.android.example.java.BuildConfig;
import com.worldline.connect.android.example.java.R;
import com.worldline.connect.android.example.java.configuration.Constants;
import com.worldline.connect.android.example.java.dialog.DialogUtil;
import com.worldline.connect.android.example.java.model.ShoppingCart;
import com.worldline.connect.android.example.java.model.ShoppingCartItem;
import com.worldline.connect.android.example.java.view.ValidationEditText;
import com.worldline.connect.sdk.client.android.ConnectSDK;
import com.worldline.connect.sdk.client.android.configuration.ConnectSDKConfiguration;
import com.worldline.connect.sdk.client.android.configuration.PaymentConfiguration;
import com.worldline.connect.sdk.client.android.configuration.SessionConfiguration;
import com.worldline.connect.sdk.client.android.model.paymentcontext.AmountOfMoney;
import com.worldline.connect.sdk.client.android.model.paymentcontext.PaymentContext;

import java.util.Currency;
import java.util.Locale;

/**
 * Dummy startpage to start payment
 *
 */
public class StartPageActivity extends Activity {

	ValidationEditText clientSessionIdentifierEditText;
	ValidationEditText customerIdentifierEditText;
	ValidationEditText clientApiUrlEditText;
	ValidationEditText assetUrlEditText;
	ValidationEditText countryCodeEditText;
	ValidationEditText currencyCodeEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startpage);

		clientSessionIdentifierEditText = findViewById(R.id.client_session_identifier);
		customerIdentifierEditText = findViewById(R.id.customer_identifier);
		clientApiUrlEditText = findViewById(R.id.client_api_url);
		assetUrlEditText = findViewById(R.id.asset_url);
		countryCodeEditText = findViewById(R.id.country_code);
		currencyCodeEditText = findViewById(R.id.currency_code);

		loadData();
	}

	public void payButtonPressed(View view) {

		if (!clientSessionIdentifierEditText.isValid()) {
			return;
		}
		String clientSessionIdentifier = clientSessionIdentifierEditText.getValue();

		if (!customerIdentifierEditText.isValid()) {
			return;
		}
		String customerId = customerIdentifierEditText.getValue();

		EditText merchantIdentifierEditText = ((EditText) findViewById(R.id.merchant_identifier));
		String merchantId = merchantIdentifierEditText.getText().toString();

		EditText merchantNameText = ((EditText) findViewById(R.id.merchant_name));
		String merchantName = merchantNameText.getText().toString();

		if (!clientApiUrlEditText.isValid()) {
			return;
		}
		String clientApiUrl = clientApiUrlEditText.getValue();

		if (!assetUrlEditText.isValid()) {
			return;
		}
		String assetUrl = assetUrlEditText.getValue();

		ValidationEditText amountEditText = findViewById(R.id.amount);
		if (!amountEditText.isValid()) {
			return;
		}
		String amount = amountEditText.getValue();

		if (!countryCodeEditText.isValid()) {
			return;
		}
		String countryCode = countryCodeEditText.getValue();

		if (!currencyCodeEditText.isValid()) {
			return;
		}
		String currencyCode = currencyCodeEditText.getValue();


		boolean isRecurring = ((CheckBox) findViewById(R.id.payment_is_recurring)).isChecked();

		// Use this property to indicate whether the payment will be paid in instalments.
		//	This will be taken into account when determining the availability of credit cards when making an IIN details call.
		boolean isInstallments = ((CheckBox) findViewById(R.id.payment_is_in_installments)).isChecked();

		boolean groupPaymentProducts = ((CheckBox) findViewById(R.id.group_paymentproducts)).isChecked();

		ShoppingCart cart = new ShoppingCart();
		cart.addItemToShoppingCart(new ShoppingCartItem("Something", Long.parseLong(amount), 1));

		//create the SessionConfiguration object
		SessionConfiguration sessionConfiguration = new SessionConfiguration(clientSessionIdentifier, customerId, clientApiUrl, assetUrl);

		// Create the PaymentContext object
		AmountOfMoney amountOfMoney = new AmountOfMoney(cart.getTotalAmount(), currencyCode);
		PaymentContext paymentContext = new PaymentContext(
				amountOfMoney,
				countryCode,
				isRecurring,
				Locale.US,
				isInstallments
		);

		// and show the PaymentProductSelectionActivity
		Intent paymentIntent = new Intent(this, PaymentProductSelectionActivity.class);

		// Attach the following objects to the paymentIntent
		paymentIntent.putExtra(Constants.INTENT_SHOPPINGCART, cart);
		paymentIntent.putExtra(Constants.MERCHANT_MERCHANT_IDENTIFIER, merchantId);
		paymentIntent.putExtra(Constants.MERCHANT_NAME, merchantName);
		initializeConnectSDK(sessionConfiguration, paymentContext, groupPaymentProducts);

		// Start paymentIntent
		startActivity(paymentIntent);
	}

	public void parseJsonButtonPressed(View view) {
		DialogUtil.showJsonAlertDialog(this, sessionDetails -> {

			assetUrlEditText.setText(sessionDetails.assetUrl);
			clientApiUrlEditText.setText(sessionDetails.clientApiUrl);
			clientSessionIdentifierEditText.setText(sessionDetails.clientSessionId);
			customerIdentifierEditText.setText(sessionDetails.customerId);

			assetUrlEditText.isValid();
			clientApiUrlEditText.isValid();
			clientSessionIdentifierEditText.isValid();
			customerIdentifierEditText.isValid();
		});
	}

	private void loadData() {
		// prefill country and currency
		Locale locale = Locale.getDefault();
		countryCodeEditText.setText(locale.getCountry());
		currencyCodeEditText.setText(Currency.getInstance(locale).toString());
	}

	private void initializeConnectSDK(SessionConfiguration sessionConfiguration, PaymentContext paymentContext, boolean groupsPaymentProducts) {
		ConnectSDKConfiguration connectSDKConfiguration = new ConnectSDKConfiguration.Builder(
				sessionConfiguration,
				getApplicationContext()
		)
				.enableNetworkLogs(BuildConfig.LOGGING_ENABLED)
				.preLoadImages(false)
				.applicationId(APPLICATION_IDENTIFIER)
				.build();

		PaymentConfiguration paymentConfiguration = new PaymentConfiguration.Builder(
				paymentContext)
				.groupPaymentProducts(groupsPaymentProducts)
				.build();

		ConnectSDK.INSTANCE.initialize(connectSDKConfiguration, paymentConfiguration);
	}
}
