/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.worldline.connect.android.example.java.R;
import com.worldline.connect.android.example.java.view.detailview.DetailInputViewBoletoBancario;
import com.worldline.connect.android.example.java.view.detailview.DetailInputViewBoletoBancarioImpl;
import com.worldline.connect.sdk.client.android.constants.Constants;

/**
 * DetailInputActivity which has added logic for the Boleto Bancario Payment Product
 *

 */
public class DetailInputActivityBoletoBancario extends DetailInputActivity {

    private DetailInputViewBoletoBancario fieldView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView will be called by super

        fieldView = new DetailInputViewBoletoBancarioImpl(this, R.id.detail_input_view_layout_fields_and_buttons);

    }

    @Override
    public void onStart() {
        super.onStart();
        addBoletoBancarioTextWatcher();
        performBoletoLogic();
    }


    private void addBoletoBancarioTextWatcher() {

        TextWatcher textWatcher = new TextWatcher() {

            private boolean was14;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int fiscalNumberLength = getFiscalNumberLength();
                was14 = fiscalNumberLength == 14;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not implemented
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int fiscalNumberLength = getFiscalNumberLength();
                if (fiscalNumberLength < 14 && was14 || fiscalNumberLength == 14) {
                    performBoletoLogic();
                }
            }
        };

        fieldView.initializeFiscalNumberField(textWatcher);

    }

    private void performBoletoLogic() {

        int fiscalNumberLength = getFiscalNumberLength();

        if (fiscalNumberLength < 14) {
            // The length is under 14, this means that this is a personal number, therefore we need to
            // hide the company name field
            fieldView.setBoletoBancarioPersonalFiscalNumber();
        } else {
            // Otherwise we have the fiscal number of a company
            fieldView.setBoletoBancarioCompanyFiscalNumber();
        }
    }

    private int getFiscalNumberLength() {
        if (inputDataPersister.getUnmaskedValue(Constants.FISCAL_NUMBER_FIELD_ID) != null) {
            return inputDataPersister.getUnmaskedValue(Constants.FISCAL_NUMBER_FIELD_ID).length();
        } else {
            return 0;
        }
    }
}
