/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.OnBackPressedCallback;

import com.worldline.connect.android.example.java.R;
import com.worldline.connect.android.example.java.configuration.Constants;
import com.worldline.connect.android.example.java.exception.ThirdPartyStatusNotAllowedException;
import com.worldline.connect.android.example.java.model.ShoppingCart;
import com.worldline.connect.android.example.java.view.detailview.BCMCProcessingView;
import com.worldline.connect.android.example.java.view.detailview.BCMCProcessingViewImpl;
import com.worldline.connect.sdk.client.android.model.thirdpartystatus.ThirdPartyStatus;


public class BCMCProcessingActivity extends ShoppingCartActivity {

    private static final Handler delay = new Handler();

    private BCMCProcessingView processingView;

    private ShoppingCart shoppingCart;

    private ThirdPartyStatus currentThirdPartyStatus;

    // Boolean that will prevent running several polling calls at once.
    private boolean pollInProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bcmc_processing);
        // Initialize the shoppingcart
        super.initialize(this);

        processingView = new BCMCProcessingViewImpl(this, R.id.bcmc_processing_view_layout);

        initializeIntentData();

        if (savedInstanceState != null) {
            currentThirdPartyStatus = (ThirdPartyStatus) savedInstanceState.getSerializable(Constants.BUNDLE_THIRDPARTYSTATUS);
            if (currentThirdPartyStatus != null) {
                processThirdPartyStatus(currentThirdPartyStatus);
            }
        }

        setOnBackPressedListener();
    }

    private void initializeIntentData() {
        Intent intent = getIntent();
        shoppingCart = (ShoppingCart) intent.getSerializableExtra(Constants.INTENT_SHOPPINGCART);
    }

    @Override
    public void onStart() {
        super.onStart();
        /**
         * This activity needs to be constantly polling for updates.
         */
        pollForThirdPartyStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * When the user returns to your app, you immediately want to start polling again
         */
        pollForThirdPartyStatus();
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
            currentThirdPartyStatus = thirdPartyStatus;
            processThirdPartyStatus(thirdPartyStatus);
        }
    }

    private void processThirdPartyStatus(ThirdPartyStatus thirdPartyStatus) {
        switch (thirdPartyStatus) {
            case WAITING:
                throw new ThirdPartyStatusNotAllowedException("ThirdPartyStatus can not be WAITING.");
            case INITIALIZED:
                // Just poll again
                pollForThirdPartyStatus();
                break;
            case AUTHORIZED:
                processingView.renderAuthorized();
                pollForThirdPartyStatus();
                break;
            case COMPLETED:
                processingView.renderCompleted();
                Intent resultActivityIntent = new Intent(this, PaymentResultActivity.class);
                resultActivityIntent.putExtra(Constants.INTENT_SHOPPINGCART, shoppingCart);
                startActivity(resultActivityIntent);
                finish();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constants.BUNDLE_THIRDPARTYSTATUS, currentThirdPartyStatus);
    }
}
