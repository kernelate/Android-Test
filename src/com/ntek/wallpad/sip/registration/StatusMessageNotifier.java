package com.ntek.wallpad.sip.registration;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;

import com.ntek.wallpad.R;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;

public class StatusMessageNotifier {
	//private final static String TAG = RegistrationMessageNotifier.class.getCanonicalName();
	
	private static NotificationManager mNotifManager;
	
	private static final int NOTIF_REGISTRATION_ID = 19833897;
	private static final int ICONID_DOORTALK = R.drawable.ic_notify_error;
	
	private StatusMessageNotifier() {
	}
	
	private static PendingIntent createNewNotifIntent() {
        Intent intent = new Intent(NgnApplication.getContext(), NgnEngine.getInstance().getMainActivity().getClass());
    	intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("notif-type", "reg");
		return PendingIntent.getActivity(NgnApplication.getContext(), 
				NOTIF_REGISTRATION_ID/*requestCode*/, 
				intent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	@SuppressWarnings("deprecation")
	private static Notification createNotification() {
		final Notification notification = new Notification(ICONID_DOORTALK, "", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		return notification;
	}
	
	@SuppressWarnings("deprecation")
	public static void notify(Context context, String message) {
		mNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notification = createNotification();
		notification.setLatestEventInfo(context, "Doortalk", message, createNewNotifIntent());
		mNotifManager.notify(NOTIF_REGISTRATION_ID, notification);
	}
	
	public static void cancel(Context context) {
		mNotifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifManager.cancel(NOTIF_REGISTRATION_ID);
	}
	
	public static void notifyWithDialogWindow(String title, String message) {
		notifyWithDialogWindow(title, message, null);
	}
	public static void notifyWithDialogWindow(String title, String message, OnDismissListener listener) {
		AlertDialog alertDialog = new AlertDialog.Builder(NgnApplication.getContext()).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
		    new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) {
		            dialog.dismiss();
		        }
		    });
		if (listener!=null) {
			alertDialog.setOnDismissListener(listener);
		}
		alertDialog.show();
	}		
}
