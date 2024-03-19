/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;

import com.worldline.connect.android.example.java.R;
import com.worldline.connect.android.example.java.configuration.Constants;
import com.worldline.connect.android.example.java.model.KeyValuePair;
import com.worldline.connect.android.example.java.view.detailview.DetailInputViewBCMC;
import com.worldline.connect.android.example.java.view.detailview.DetailInputViewBCMCImpl;
import com.worldline.connect.sdk.client.android.model.thirdpartystatus.ThirdPartyStatus;

import android.net.Uri;

import androidx.activity.OnBackPressedCallback;

import java.util.List;

/**
 * DetailInputActivity which has added logic for the BCMC Payment Product
 *

 */
public class DetailInputActivityBCMC extends DetailInputActivity {

    private static final String QRCODE = "QRCODE";
    private static final String URLINTENT = "URLINTENT";

    private static final Handler delay = new Handler();

    private DetailInputViewBCMC fieldView;

    private List<KeyValuePair> showData;

    private boolean pollInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView will be called by super

        fieldView = new DetailInputViewBCMCImpl(this, R.id.detail_input_view_layout_fields_and_buttons);

        showData = (List<KeyValuePair>) getIntent().getSerializableExtra(Constants.INTENT_BCMC_SHOWDATA);
        Bitmap qrCode = loadQrCode();

        fieldView.renderBCMCIntroduction(qrCode);

        setOnBackPressedListener();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Start the continuous polling sequence that will check for the current payment status and
        // update the view if something changes
        pollForThirdPartyStatus();
    }

    private Bitmap loadQrCode() {
        for (KeyValuePair kvp : showData) {
            if (kvp.getKey().equals(QRCODE)) {
                byte[] decodedString = Base64.decode(kvp.getValue(), Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
        }
        return null;
    }

    public void openBCMCApp(View v) {
        for (KeyValuePair kvp : showData) {
            if (kvp.getKey().equals(URLINTENT)) {
                Uri intentUri = Uri.parse(kvp.getValue());
                Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
                startActivity(intent);
                break;
            }
        }
    }

    private void setOnBackPressedListener() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                delay.removeCallbacksAndMessages(null);
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * When the user returns to your app, you should perform a ThirdPartyStatus call, in order to
         * verify that the user has indeed completed his/her payment in the BCMC app.
         */
        pollForThirdPartyStatus();
    }

    private void pollForThirdPartyStatus() {
        if (!pollInProgress) {
            pollInProgress = true;
            delay.postDelayed(new Runnable() {

                @Override
                public void run() {
                /**
                 * Here you will want to perform a poll to retrieve the ThirdPartyStatus
                 *     ConnectSDK.INSTANCE.getClientApi().getThirdPartyStatus(yourPaymentId, onSuccess, onApiError, onFailure);
                 * As this example app does not have a valid paymentId however, we will spoof the
                 * getThirdPartyStatusCall here.
                 */
                simulateThirdPartyProgress();
                }
            }, 3000); // Poll once every three seconds
        }
    }

    /**
     * Helper function that simulates progress with the 3rd party payment. In the end it ensures
     * that the ThirdPartyStatus is COMPLETED.
     */
    private int counter = 0;
    private void simulateThirdPartyProgress() {
        counter++;
        if (counter < 4) {
            onThirdPartyStatusCallSuccess(ThirdPartyStatus.INITIALIZED);
        } else if (counter < 8) {
            onThirdPartyStatusCallSuccess(ThirdPartyStatus.AUTHORIZED);
        } else {
            onThirdPartyStatusCallSuccess(ThirdPartyStatus.COMPLETED);
        }
    }

    private void onThirdPartyStatusCallSuccess(ThirdPartyStatus thirdPartyStatus) {
        pollInProgress = false;
        if (thirdPartyStatus != null) {
            switch (thirdPartyStatus) {
                case WAITING:
                    // Do nothing and perform the polling call again.
                    pollForThirdPartyStatus();
                    break;
                case INITIALIZED: // Fall through
                case AUTHORIZED:
                    Intent processingActivityIntent = new Intent(this, BCMCProcessingActivity.class);
                    processingActivityIntent.putExtra(Constants.INTENT_SHOPPINGCART, shoppingCart);
                    startActivity(processingActivityIntent);
                    delay.removeCallbacksAndMessages(null);
                    break;
                case COMPLETED:
                    Intent resultActivityIntent = new Intent(this, PaymentResultActivity.class);
                    resultActivityIntent.putExtra(Constants.INTENT_SHOPPINGCART, shoppingCart);
                    startActivity(resultActivityIntent);
                    delay.removeCallbacksAndMessages(null);
                    break;
            }
        } else {
            pollForThirdPartyStatus();
        }
    }
}
