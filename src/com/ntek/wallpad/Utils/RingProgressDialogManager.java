package com.ntek.wallpad.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.ntek.wallpad.R;

public class RingProgressDialogManager {
	private static ProgressDialog ringProgressDialog;
	private static boolean isOnProgressShow = false;
	
	/**
	 * Function to display simple ring type Progress Dialog
	 * @param context - Application context
	 * @param title - Progress dialog title
	 * @param message - Progress message
	 */
	public static void show(Context context, String title, String message) {
		synchronized (RingProgressDialogManager.class) {
			try {
				if (!isOnProgressShow)
				{
					Log.d("RingProgressDialogManager", "show(context,title,message)");
					ringProgressDialog = new ProgressDialog(context);
					ringProgressDialog.setTitle(title);
					ringProgressDialog.setMessage(message);
					ringProgressDialog.setCancelable(false);
					ringProgressDialog.show();
					isOnProgressShow = true;		
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Function to display ring type Progress Dialog, 
	 * without title and message just ring on progress
	 * @param context - Application context
	 */
	public static void show(Context context) {
		synchronized (RingProgressDialogManager.class) {
			if (!isOnProgressShow)
			{
				Log.d("RingProgressDialogManager", "show(context)");
				ringProgressDialog = new ProgressDialog(context,R.style.MyTheme);
				ringProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
				ringProgressDialog.setCancelable(false);
				ringProgressDialog.show();
				isOnProgressShow = true;	
			}
		}
	}
	
	/**
	 * Function to hide or destroy the Progress Dialog
	 */
	public static void hide() {
		synchronized (RingProgressDialogManager.class) {
			try {
				if(ringProgressDialog != null) {
					if (isOnProgressShow)
					{
						Log.d("RingProgressDialogManager", "hide()");
						ringProgressDialog.dismiss();
						ringProgressDialog = null;
						isOnProgressShow = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	}
	
	public static boolean isOnProgress()
	{
		return isOnProgressShow;
	}
}
