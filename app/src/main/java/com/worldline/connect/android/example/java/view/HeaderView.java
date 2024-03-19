/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.view;

import com.worldline.connect.android.example.java.model.ShoppingCart;
import com.worldline.connect.sdk.client.android.model.paymentcontext.PaymentContext;

/**
 * ViewInterface for the Header ShoppinCartView
 *
 */
public interface HeaderView extends GeneralView {

    void renderShoppingCart(ShoppingCart shoppingCart, PaymentContext paymentContext);

    void showDetailView();

    void hideDetailView();
}
