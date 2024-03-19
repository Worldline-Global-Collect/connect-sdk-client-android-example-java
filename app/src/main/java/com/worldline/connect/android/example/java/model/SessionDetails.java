/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Pojo that contains session details
 *
 */
public class SessionDetails {

    @Nullable
    @SerializedName("assetUrl")
    public String assetUrl = null;

    @Nullable
    @SerializedName("clientApiUrl")
    public String clientApiUrl = null;

    @Nullable
    @SerializedName("clientSessionId")
    public String clientSessionId = null;

    @Nullable
    @SerializedName("customerId")
    public String customerId = null;

    public boolean isFullyParsed() {
        return assetUrl != null && clientApiUrl != null && clientSessionId != null && customerId != null;
    }

    public List<String> getMissingValues() {
        List<String> missingValues = new ArrayList<>();
        if (assetUrl == null) missingValues.add("assetUrl");
        if (clientApiUrl == null) missingValues.add("clientApiUrl");
        if (clientSessionId == null) missingValues.add("clientSessionId");
        if (customerId == null) missingValues.add("customerId");
        return missingValues;
    }

}
