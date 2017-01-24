package com.ntek.wallpad.sip.registration;

import org.doubango.ngn.NgnApplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

public class RegistrationTrigger extends ResultReceiver {
	private final static String TAG = RegistrationTrigger.class.getCanonicalName();
	
	private static Context mContext = NgnApplication.getContext();
	private static RegistrationTrigger mRegistrationTrigger = new RegistrationTrigger(new Handler());
	private static RegistrationApplication application = new RegistrationApplication();
	private static boolean isLock = false;
	
	public RegistrationTrigger(Handler handler) {
		super(handler);
	}
	
    public static void register() {
		register(false);
    }

    public static void lock(boolean lock) {
    	isLock = lock;
    }
    
    public static boolean isLocked() {
    	return isLock;
    }
	
	public static void register(boolean userAction) {
		if (userAction) {
			lock(false);
		}
		Log.d(TAG, "register lock : " + isLock);
		if (!isLocked()) {
			StatusMessageNotifier.cancel(mContext);
			RegistrationTrigger.lock(true);
			Intent intent = new Intent(mContext, RegistrationIntentService.class);
			intent.putExtra(RegistrationIntentService.APPLICATION_DATA, application);
			intent.putExtra(RegistrationIntentService.RESULT_RECEIVER, mRegistrationTrigger);
			intent.putExtra(RegistrationIntentService.USER_ACTION_REGISTER, userAction);
			mContext.startService(intent);
		}
	}
	
	public static void disableReceiver() {
		Log.d(TAG,"Disable StatusReceiver");
		ComponentName component = new ComponentName(mContext, StatusReceiver.class);
		mContext.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
	}
	
	public static void enableReceiver() {
	    Log.d(TAG, "Enable StatusReceiver");
		ComponentName component = new ComponentName(mContext, StatusReceiver.class);
		mContext.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
	}
	
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
    	RegistrationApplication new_application = (RegistrationApplication)resultData.getParcelable(RegistrationIntentService.APPLICATION_DATA);
    	
    	if (new_application!=null) {
    		// replace old to new one for next comparison that sees if any of its data has changed    		
    		application = new_application;
    		
    		// Log messages for debugging
    		Log.d(TAG, "replace old application data by new_application: \n" + application.toString());
    	}
    }	
	
}
