/*
 * Copyright (c) 2022 Worldline Global Collect B.V
 */

package com.worldline.connect.android.example.java.configuration;

public class Constants {

	/** Application Identifier, used for identifying the application in network calls **/
	public static String APPLICATION_IDENTIFIER = "Android Example Application/v2.0.1";

	/** Intent data keys **/
	public final static String INTENT_PAYMENT_ENCRYPTED_FIELDS   = "encryptedFields";

	public final static String INTENT_SHOPPINGCART 			 	 = "shoppingcart";
	public final static String INTENT_SELECTED_ITEM			 	 = "selected_item";
	public final static String INTENT_SELECTED_ACCOUNT_ON_FILE   = "selected_account_on_file";
	public final static String INTENT_ERRORMESSAGE				 = "errorMessage";
	public final static String INTENT_URL_WEBVIEW				 = "url";
	public final static String INTENT_BCMC_SHOWDATA				 = "bcmc_showdata";

	/** Bundle data keys **/

	public final static String BUNDLE_INPUTDATAPERSISTER = "bundle_inputdatapersister";
	public final static String BUNDLE_INPUTVALIDATIONPERSISTER	= "bundle_inputvalidationpersister";
	public final static String BUNDLE_IINDETAILSPERSISTER		= "bundle_iindetailspersister";
	public final static String BUNDLE_RENDERED					= "bundle_rendered";
	public final static String BUNDLE_THIRDPARTYSTATUS			= "bundle_thirdpartystatus";

	/** Information from the merchant **/
	public final static String MERCHANT_MERCHANT_IDENTIFIER			= "merchant_merchant_identifier";
	public final static String MERCHANT_NAME						= "merchant_name";
}
