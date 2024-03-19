/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.view.detailview;

import android.app.Activity;
import android.graphics.Bitmap;
import androidx.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;

import com.worldline.connect.android.example.java.R;
/**
 * View for the DetailInputActivity with added functionality for the BCMC payment product
 *
 */
public class DetailInputViewBCMCImpl extends  DetailInputViewImpl implements DetailInputViewBCMC {

    public DetailInputViewBCMCImpl(Activity activity, @IdRes int id) {
        super (activity, id);
    }

    @Override
    public void renderBCMCIntroduction(Bitmap qrCode) {
        View bcmcLayout = View.inflate(rootView.getContext(), R.layout.layout_bcmc, null);
        ImageView qrCodeView = (ImageView) bcmcLayout.findViewById(R.id.qrcode);
        if (qrCodeView != null) {
            qrCodeView.setImageBitmap(qrCode);
        }
        renderInputFieldsLayout.addView(bcmcLayout, 0);
    }
}
