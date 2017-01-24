package com.ntek.wallpad.Screens.Fragment;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.FragmentPhone;
import com.ntek.wallpad.R;
import com.ntek.wallpad.lockscreen.LockScreenPasswordActivity;
import com.ntek.wallpad.lockscreen.LockScreenPatternActivity;
import com.ntek.wallpad.lockscreen.LockScreenPinActivity;
import com.ntek.wallpad.lockscreen.LockScreenSwipeActivity;
import com.ntek.wallpad.lockscreen.LockscreenPatternUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class FragmentSettingScreenLockBase extends Activity {

	
	private final static String TAG = FragmentSettingScreenLockBase.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private LockscreenPatternUtils mLockscreenUtils;
	private String getValueNgn;
	private String getValueNgnPW;
	private String getValueNgnPin;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
			//			mScreenService = ((Engine)Engine.getInstance()).getScreenService();    	 
		} 
		getValueNgn = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT);
		getValueNgnPW = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, NgnConfigurationEntry.SCREEN_LOCK_PASSWORD_DEFAULT);
		getValueNgnPin = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PIN, NgnConfigurationEntry.SCREEN_LOCK_PIN_DEFAULT);
		
		Log.d(TAG, " dnasndsandm " + FragmentSettingSecurity.selectType + " 1 " + getValueNgn + " 2 " + getValueNgnPW + " 3 " + getValueNgnPin);
		
		if (FragmentSettingSecurity.selectType == 1) {
			Log.d(TAG, " 1111111111111 ");
			Intent intent = new Intent (FragmentSettingScreenLockBase.this, LockScreenSwipeActivity.class);
			startActivity(intent);
			finish();
		}
		else if (FragmentSettingSecurity.selectType == 2) {
			Log.d(TAG, " 22222222222 ");
			Intent intent = new Intent (FragmentSettingScreenLockBase.this, LockScreenPatternActivity.class);
			startActivity(intent);
			finish();
		}
		else if (FragmentSettingSecurity.selectType == 3) {
			Log.d(TAG, " 3333333333333333 ");
			Intent intent = new Intent (FragmentSettingScreenLockBase.this, LockScreenPinActivity.class);
			startActivity(intent);
			finish();
			
		}
		else if (FragmentSettingSecurity.selectType == 4) {
			Log.d(TAG, " 444444444444444 ");
			Intent intent = new Intent (FragmentSettingScreenLockBase.this, LockScreenPasswordActivity.class);
			startActivity(intent);
			finish();
		}
		else
		{
			Log.d(TAG, " 00000000000000000000 ");
			if (!getValueNgn.isEmpty() ) {
				Log.d(TAG, " meron laman pattern meron laman pattern ");
				Intent intent = new Intent (FragmentSettingScreenLockBase.this, LockScreenSwipeActivity.class);
				startActivity(intent);
				finish();
				
				
				
				Log.d(TAG, " meron laman pattern 111 meron laman pattern 111");
			}
//			else if (!getValueNgnPW.isEmpty()) {
//				Log.d(TAG, " meron laman pw meron laman pw ");
//				Intent intent = new Intent (FragmentSettingScreenLockBase.this, LockScreenPasswordActivity.class);
//				startActivity(intent);
//				finish();
//			}
//			else if (!getValueNgnPin.isEmpty()) {
//				Log.d(TAG, " meron laman pin meron laman pin ");
//				Intent intent = new Intent (FragmentSettingScreenLockBase.this, LockScreenPinActivity.class);
//				startActivity(intent);
//				finish();
//			}
			else
			{
				Log.d(TAG, " ala laman ala laman ala laman ala laman  ");
				Intent intent;
				intent = this.getPackageManager().getLaunchIntentForPackage("com.ntek.wallpad.launcher");
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.putExtra("action", 7);
				startActivity(intent);
				
			}
//			Intent intent = new Intent (FragmentSettingScreenLockBase.this, LockScreenSwipeActivity.class);
//			startActivity(intent);
//			finish();
			
		}
	}

	
	
}
