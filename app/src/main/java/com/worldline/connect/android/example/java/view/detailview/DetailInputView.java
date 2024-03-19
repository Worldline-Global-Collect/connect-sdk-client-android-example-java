/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.view.detailview;


import android.view.View;

import com.worldline.connect.android.example.java.render.persister.InputDataPersister;
import com.worldline.connect.android.example.java.render.persister.InputValidationPersister;
import com.worldline.connect.android.example.java.view.GeneralView;
import com.worldline.connect.sdk.client.android.model.paymentcontext.PaymentContext;
import com.worldline.connect.sdk.client.android.model.paymentitem.PaymentItem;
import com.worldline.connect.sdk.client.android.model.validation.ValidationErrorMessage;

/**
 * Interface for the DetailInputView
 *
 */
public interface DetailInputView extends GeneralView {

    void renderDynamicContent(InputDataPersister inputDataPersister,
                              PaymentContext paymentContext,
                              InputValidationPersister inputValidationPersister);

    void renderRememberMeCheckBox(boolean isChecked);

    void removeAllFieldViews();

    void activatePayButton();

    void deactivatePayButton();

    void showLoadDialog();

    void hideLoadDialog();

    void renderValidationMessage(ValidationErrorMessage validationResult, PaymentItem paymentItem);

    void renderValidationMessages(InputValidationPersister inputValidationPersister, PaymentItem paymentItem);

    void hideTooltipAndErrorViews(InputValidationPersister inputValidationPersister);

    void setFocusAndCursorPosition(String fieldId, int cursorPosition);

    View getViewWithFocus();
}
