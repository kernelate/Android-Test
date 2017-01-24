package com.ntek.wallpad.automation.utils;


import com.ntek.wallpad.R;

import android.app.ProgressDialog;
import android.content.Context;


public class RingProgressDialogManager {
	private static ProgressDialog ringProgressDialog;

	/**
	 * Function to display simple ring type Progress Dialog
	 * 
	 * @param context
	 *            - Application context
	 * @param title
	 *            - Progress dialog title
	 * @param message
	 *            - Progress message
	 */
	public static void show(Context context, String title, String message) {
		ringProgressDialog = new ProgressDialog(context);
		ringProgressDialog.setTitle(title);
		ringProgressDialog.setMessage(message);
		ringProgressDialog.setCancelable(false);
		ringProgressDialog.show();
	}

	/**
	 * Function to display ring type Progress Dialog, without title and message
	 * just ring on progress
	 * 
	 * @param context
	 *            - Application context
	 */
	public static void show(Context context) {
		ringProgressDialog = new ProgressDialog(context, R.style.MyTheme);
		ringProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
		ringProgressDialog.setCancelable(false);
		ringProgressDialog.show();
	}

	/**
	 * Function to hide or destroy the Progress Dialog
	 */
	public static void hide() {
		if (ringProgressDialog != null) {
			ringProgressDialog.dismiss();
			ringProgressDialog = null;
		}
	}
}
