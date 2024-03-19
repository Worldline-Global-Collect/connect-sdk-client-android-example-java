/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.view.selectionview;

import android.content.DialogInterface.OnClickListener;

import com.worldline.connect.android.example.java.view.GeneralView;
import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItems;

/**
 * Interface for the Product selection view
 *
 */
public interface ProductSelectionView extends GeneralView {

    void renderDynamicContent(BasicPaymentItems paymentItems);

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void showTechnicalErrorDialog(OnClickListener listener);

    void showNoInternetDialog(OnClickListener listener);

    void showSpendingLimitExceededErrorDialog(OnClickListener positiveListener, OnClickListener negativeListener);

    void hideAlertDialog();
}
