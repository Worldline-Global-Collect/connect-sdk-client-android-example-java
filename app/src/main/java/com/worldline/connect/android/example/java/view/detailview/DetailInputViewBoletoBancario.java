/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.view.detailview;

import android.text.TextWatcher;

/**
 * Interface for a DetailInputView, that has extra functionality for rendering the Boleto Bancario
 * payment product
 *
 */
public interface DetailInputViewBoletoBancario extends DetailInputView {

    void initializeFiscalNumberField(TextWatcher textWatcher);

    void setBoletoBancarioPersonalFiscalNumber();

    void setBoletoBancarioCompanyFiscalNumber();
}
