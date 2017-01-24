package com.ntek.wallpad.sip.registration;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.events.NgnStackEventArgs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.ntek.wallpad.R;
import com.ntek.wallpad.sip.registration.SipResponseValidator.SipResponseObject;
import com.smarttalk.sip.AutoProvisionDialog;

public class StatusReceiver extends BroadcastReceiver {
	private final static String TAG = StatusReceiver.class.getCanonicalName();
	
	final static Context mContext = NgnApplication.getContext();
	
	private static SipResponseObject sipResponse;
	
	
	private void register() {
		RegistrationTrigger.register();
	}	

	private void notifShow(String message) {
		StatusMessageNotifier.notify(mContext, message);
	}

	private void notifyWithDialogWindow(String message) {
		String title = mContext.getString(R.string.notification);
		StatusMessageNotifier.notifyWithDialogWindow(title, message, new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				StatusMessageNotifier.cancel(mContext);
				sipResponse = null;
			}
		});
	}	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		
		if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
			NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
			if(args == null){ 
				Log.e(TAG, "Invalid event args");
				return;
			}
			Log.d(TAG, "ACTION_REGISTRATION_EVENT : " + args.getEventType());
			switch(args.getEventType()){
				case REGISTRATION_INPROGRESS:
				case UNREGISTRATION_INPROGRESS:
					RegistrationTrigger.lock(true);
					break;
				case REGISTRATION_OK:
//					notifyWithDialogWindow("Configuration Saved"); //TODO change the way dialog is displayed
					Log.d(TAG, " good ");
//						AutoProvisionDialog.getInstance().showNotifMessage(NgnApplication.getContext(), " Successfully save ", null);
					break;
				case REGISTRATION_NOK:
					Log.d(TAG, "  bad ");
//					notifyWithDialogWindow("Configuration Saved"); //TODO change the way dialog is displayed
//					AutoProvisionDialog.getInstance().showNotifMessage(NgnApplication.getContext(), " Please check your SIP profile ", null);
					
					break;
				case UNREGISTRATION_OK:
					String message = mContext.getString(R.string.sipresponses_unregistration_ok);
					
					sipResponse = SipResponseValidator.validate(args.getSipCode());
					if (sipResponse!= null && sipResponse.belongToErrorCodes()) {
						Log.d(TAG, "code : " + sipResponse.getCode() + ", message : " + sipResponse.getPhrase());
						RegistrationTrigger.lock(true);
//						notifShow(message);
//						notifyWithDialogWindow(message);
						NgnEngine.getInstance().getSipService().unRegister();
					}
					break;
				default:
					break;
			}
		} else if(NgnStackEventArgs.ACTION_STACK_EVENT.equals(action)) {
			NgnStackEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
			if(args == null){
				Log.e(TAG, "Invalid event args");
				return;
			}
			Log.d(TAG, "ACTION_STACK_EVENT : " + args.getEventType());
			switch(args.getEventType()){
				case START_OK: 
				case STOP_NOK:
					break;					
				case START_NOK:
				case STOP_OK:
					if (sipResponse == null || !sipResponse.belongToErrorCodes()) {
						RegistrationTrigger.lock(false);
						register(); 
					}
				default:
					break;
			}
		} else if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
			//Log.d(TAG, "CONNECTIVITY_ACTION");
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			
			if ((mobile != null && mobile.isConnected()) || ( wifi!= null && wifi.isConnected())){
				Log.d(TAG, "Network connect success");
				RegistrationTrigger.lock(false);
				register();
			}else{
				Log.d(TAG, "Network connect fail");
				// Weirdly, sometimes Ngn-stack does not unregister even though there is no active network found
				// so change it to unregistered status
				final boolean isRegistered = NgnEngine.getInstance().getSipService().isRegistered();
				if (isRegistered) {
					NgnEngine.getInstance().getSipService().unRegister();
				}
				// inform user to be noticed they need to check network connection
//				notifShow(mContext.getString(R.string.error_internet_connection));
			}			
		}
	}
}
