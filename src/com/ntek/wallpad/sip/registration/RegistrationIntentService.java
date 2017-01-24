package com.ntek.wallpad.sip.registration;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class RegistrationIntentService extends IntentService {
	private final static String TAG = RegistrationIntentService.class.getCanonicalName();
	
	public final static String APPLICATION_DATA = "APPLICATION_DATA";
	public final static String RESULT_RECEIVER = "RESULT_RECEIVER";
	public final static String USER_ACTION_REGISTER = "USER_ACTION_REGISTER";
	
	public RegistrationIntentService() {
		super(RegistrationIntentService.class.getCanonicalName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//Log.d(TAG, "onHandleIntent");
		RegistrationApplication application = (RegistrationApplication)intent.getParcelableExtra(APPLICATION_DATA);
		
		// possibly, before initial application data initiates, CONNECTIVITY_CHANGED passes through 
		if (application == null) {
			Log.d(TAG, "a given application data is null");
			return;
		}
		
		final ResultReceiver resultReceiver = (ResultReceiver)intent.getParcelableExtra(RESULT_RECEIVER);
		final RegistrationApplication new_application = new RegistrationApplication();
		
		final boolean hasChanged = (application.compareTo(new_application) == -1)?true:false;
		final boolean isRegistered = NgnEngine.getInstance().getSipService().isRegistered();
		final boolean userActionRegister = intent.getBooleanExtra(USER_ACTION_REGISTER, false);
		
		Log.d(TAG, "APPLICATION DATA : \n===================\n" + application.toString());
		Log.d(TAG, "NEW_APPLICATION DATA : \n===================\n" + new_application.toString());			
		
		// mostly means no active network found(as long as validation of application form of UI works fine)
		if (!new_application.validate()) {
			Log.d(TAG, "Lack of application data for registration");
			return ;
		}
		
		if (!isRegistered) {
			Log.d(TAG, "unregistered so do register()");
			NgnEngine.getInstance().getSipService().register(NgnApplication.getContext());	
		} else if (userActionRegister) {
			Log.d(TAG, "userActionRegister is true and registered status so unregister()");
			NgnEngine.getInstance().getSipService().unRegister();
		} else if (hasChanged) {
			Log.d(TAG, "unregistered/application data changed, so unregister()");
			NgnEngine.getInstance().getSipService().unRegister();
		}

		
		Bundle bundle = new Bundle();		
		if (resultReceiver!=null && hasChanged) {
			bundle.putParcelable(APPLICATION_DATA, new_application);
			resultReceiver.send(0, bundle);
		} else {
			bundle.putParcelable(APPLICATION_DATA, null);
			resultReceiver.send(0, bundle);
		}
	}
	
}
