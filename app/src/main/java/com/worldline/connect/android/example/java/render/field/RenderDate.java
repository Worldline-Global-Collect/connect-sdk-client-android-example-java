/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.field;

import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.worldline.connect.android.example.java.render.persister.InputDataPersister;
import com.worldline.connect.sdk.client.android.model.paymentcontext.PaymentContext;
import com.worldline.connect.sdk.client.android.model.accountonfile.AccountOnFile;
import com.worldline.connect.sdk.client.android.model.accountonfile.attributes.KeyValuePair;
import com.worldline.connect.sdk.client.android.model.paymentproduct.field.PaymentProductField;

import java.security.InvalidParameterException;
import java.util.Calendar;

/**
 * This class handles all rendering of Date fields
 *
 */
public class RenderDate implements RenderInputFieldInterface {

    @Override
    public View renderField(PaymentProductField field, InputDataPersister inputDataPersister,
                            ViewGroup rowView, PaymentContext paymentContext) {

        if (field == null) {
            throw new InvalidParameterException("Error rendering datefield, field may not be null");
        }
        if (rowView == null) {
            throw new InvalidParameterException("Error rendering datefield, rowView may not be null");
        }
        if (inputDataPersister == null) {
            throw new InvalidParameterException("Error rendering datefield, inputDataPersister may not be null");
        }

        AccountOnFile accountOnFile = inputDataPersister.getAccountOnFile();

        DatePicker datePicker = new DatePicker(rowView.getContext());
        datePicker.setCalendarViewShown(false);
        datePicker.setSpinnersShown(true);

        // Create a listener here, which we can use to provide to all init calls
        OnDateChangedListener listener = createOnDateChangedListener(inputDataPersister, field.getId());

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        datePicker.init(year, month, day, listener);

        // Set values from account on file
        if (accountOnFile != null) {
            for (KeyValuePair attribute : accountOnFile.getAttributes()) {
                if (attribute.getKey().equals(field.getId())) {
                    String date = attribute.getValue();
                    setDateFromString(datePicker, date, listener);
                }

                if (!attribute.isEditingAllowed()) {
                    datePicker.setEnabled(false);
                }
            }
        }

        // get input information from inputDataPersister
        String setDate = inputDataPersister.getValue(field.getId());
        if (setDate != null) {
            setDateFromString(datePicker, setDate, listener);
        }

        rowView.addView(datePicker);

        return datePicker;
    }

    private void setDateFromString(DatePicker datePicker, String dateString, OnDateChangedListener listener) {
        // The date is in YYYYMMDD format, so it can be decomposed like so
        int year = Integer.parseInt(dateString.substring(0,4));
        int month = Integer.parseInt(dateString.substring(4,6));
        int day = Integer.parseInt(dateString.substring(6,8));

        month--; // Months are 0-based in Android
        datePicker.init(year, month, day, listener);
    }

    private OnDateChangedListener createOnDateChangedListener(final InputDataPersister inputDataPersister, final String fieldId) {
        return new OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                month++; // Months are 0 based in Android
                String monthString = Integer.toString(month);
                if (monthString.length() == 1) {
                    monthString = "0" + month;
                }

                String dayString = Integer.toString(day);
                if (dayString.length() == 1) {
                    dayString = "0" + dayString;
                }
                String newDate = "" + year + monthString + dayString;
                inputDataPersister.setValue(fieldId, newDate);
                inputDataPersister.setFocusFieldId(fieldId);
            }
        };
    }
}
