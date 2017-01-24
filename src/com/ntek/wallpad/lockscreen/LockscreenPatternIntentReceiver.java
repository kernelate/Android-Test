package com.ntek.wallpad.lockscreen;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.Screens.Fragment.FragmentSettingScreenLockBase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LockscreenPatternIntentReceiver extends BroadcastReceiver {

	private INgnConfigurationService mConfigurationService;
	
	// Handle actions and display Lockscreen
	@Override
	public void onReceive(Context context, Intent intent) {

		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
		
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) )
		{
			Log.d("LockscreenIntentReceiver", "ACTION_SCREEN_OFF");
//			start_lockscreen(context);
//			mConfigurationService.putBoolean(NgnConfigurationEntry.SCREEN_LOCK_ASD, false, true);
		}
		else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
		{
			Log.d("LockscreenIntentReceiver", "ACTION_SCREEN_ON");
//			start_lockscreen(context);
		}

		else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
		{
			Log.d("LockscreenIntentReceiver", "ACTION_BOOT_COMPLETED");
//			start_lockscreen(context);
		}

	}
	
	

	// Display lock screen
//	private void start_lockscreen(Context context) {
//		Log.d("LockscreenIntentReceiver", "LockscreenIntentReceiver");
////		Intent mIntent = new Intent(context, LockScreenPasswordActivity.class);
//		Intent mIntent = new Intent(context, FragmentSettingScreenLockBase.class);
//		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(mIntent);
//	}

}
