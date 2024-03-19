/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.render.persister;

import com.worldline.connect.sdk.client.android.model.iindetails.IinDetailsResponse;
import com.worldline.connect.sdk.client.android.model.paymentitem.BasicPaymentItem;

import java.io.Serializable;
import java.util.List;

/**
 * Will persist IinDetails information
 *
 */
public class IinDetailsPersister implements Serializable {

    private static final long serialVersionUID = -5586773582374607503L;

    private IinDetailsResponse iinDetailsResponse;

    private List<BasicPaymentItem> paymentProducts;

    public void setIinDetailsResponse(IinDetailsResponse iinDetailsResponse) {
        this.iinDetailsResponse = iinDetailsResponse;
    }

    public IinDetailsResponse getIinDetailsResponse() {
        return iinDetailsResponse;
    }

    public List<BasicPaymentItem> getPaymentProducts() {
        return paymentProducts;
    }

}
