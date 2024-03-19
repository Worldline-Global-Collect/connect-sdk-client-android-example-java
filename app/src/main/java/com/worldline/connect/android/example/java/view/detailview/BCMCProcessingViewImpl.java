/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.view.detailview;

import android.app.Activity;
import androidx.annotation.IdRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.worldline.connect.android.example.java.R;
/**
 * View that shows the progress of a BCMC payment that is processed in the BCMC app
 *
 */
public class BCMCProcessingViewImpl implements BCMCProcessingView {

    private final View rootView;

    public BCMCProcessingViewImpl (Activity activity, @IdRes int id) {
        rootView = activity.findViewById(id);
    }

    @Override
    public void renderAuthorized() {
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarAuthorized);
        progressBar.setVisibility(View.GONE);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.greenTickmarkAuthorized);
        imageView.setVisibility(View.VISIBLE);

        imageView = (ImageView) rootView.findViewById(R.id.greyTickmarkCompleted);
        imageView.setVisibility(View.GONE);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarCompleted);
        progressBar.setVisibility(View.VISIBLE);
        imageView = (ImageView) rootView.findViewById(R.id.greenTickmarkCompleted);
        imageView.setVisibility(View.GONE);
    }

    @Override
    public void renderCompleted() {
        ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarAuthorized);
        progressBar.setVisibility(View.GONE);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.greenTickmarkAuthorized);
        imageView.setVisibility(View.VISIBLE);

        imageView = (ImageView) rootView.findViewById(R.id.greyTickmarkCompleted);
        imageView.setVisibility(View.GONE);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarCompleted);
        progressBar.setVisibility(View.GONE);
        imageView = (ImageView) rootView.findViewById(R.id.greenTickmarkCompleted);
        imageView.setVisibility(View.VISIBLE);
    }
}
