/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.configuration;
import java.security.InvalidParameterException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**

 * 
 */
public class CheckCommunication {
	
	/**
	 * Checks if there is internet connectivity on this device
	 * @param activity
	 * @return true if there is internet connectivity
	 */
	public static boolean isOnline(Activity activity) {
		
		// Null check
		if (activity == null) {
			throw new InvalidParameterException("Error checking connection capabilities, activity may not be null");
		}
		
		// Check whether internet connection
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
