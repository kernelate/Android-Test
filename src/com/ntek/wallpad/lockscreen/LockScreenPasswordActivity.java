package com.ntek.wallpad.lockscreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.Engine;
import com.ntek.wallpad.FragmentBaseScreenSettings;
import com.ntek.wallpad.FragmentPhone;
import com.ntek.wallpad.Main;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingSecurity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewSwitcher;

public class LockScreenPasswordActivity extends Activity {

	private final static String TAG = LockScreenPasswordActivity.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private LockscreenPatternUtils mLockscreenUtils;
	private String getValueNgnPW;
	private String getValueNgnPattern;
	private EditText pwLockLauncher;
	private String getPwStr;
	private TextView DateTv;
	Button btntest;
	static int action_setting;
	
	ViewSwitcher switcher;
	Button Next, Previous;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fragment_setting_security_password_launcher);
		
		init();

		Log.d(TAG, " oncreate  LockScreenPasswordActivity " + FragmentSettingSecurity.selectType);
		
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
		getValueNgnPW = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, NgnConfigurationEntry.SCREEN_LOCK_PASSWORD_DEFAULT);
		getValueNgnPattern = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT); 
		
		
		//Set up our Lockscreen
		makeFullScreen();
		
		if (getValueNgnPW.length() > 3 || getValueNgnPattern.length() > 3 )
		{
			try
			{
				Bundle bundle = savedInstanceState;
				if(bundle == null){
					Intent intent = getIntent();
					bundle = intent == null ? null : intent.getExtras();
					action_setting = bundle.getInt("action", Main.ACTION_NONE);
				}
			}
			catch (NullPointerException e) 
			{

			}


			if (!mConfigurationService.getBoolean(NgnConfigurationEntry.SCREEN_LOCK_ASD, false)) 
			{
				Log.d(TAG, " SCREEN_LOCK_ASD ");
				// disable keyguard
				disableKeyguard();
				// lock home button
//				lockHomeButton();
				// start service for observing intents

			}
			else
			{
				if (action_setting == 7)
				{
					Log.d(TAG, " action_setting " + action_setting);
					Intent intent = new Intent (LockScreenPasswordActivity.this, FragmentBaseScreenSettings.class);
					startActivity(intent);
					finish();
				}
				else if (action_setting == 6)
				{
					Intent intent = new Intent (LockScreenPasswordActivity.this, FragmentPhone.class);
					startActivity(intent);
					finish();
				}
				else
				{
					finish();
				}
			}
		}
		else
		{
			Log.d(TAG, " teststr is null " + action_setting);
			Intent intent = new Intent (LockScreenPasswordActivity.this, FragmentBaseScreenSettings.class);
			startActivity(intent);
			finish();
		}

		// unlock screen in case of app get killed by system
		if (getIntent() != null && getIntent().hasExtra("kill") 	&& getIntent().getExtras().getInt("kill") == 1) 
		{
			enableKeyguard();
		} 
		else 
		{
			try {
				//				// start service for observing intents
//				startService(new Intent(this, LockscreenPatternService.class));
				//				// listen the events get fired during the call
				StateListener phoneStateListener = new StateListener();
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				telephonyManager.listen(phoneStateListener,	PhoneStateListener.LISTEN_CALL_STATE);
			} 
			catch (Exception e)
			{

			}
		}
	}

	private void init() 
	{
		mLockscreenUtils = new LockscreenPatternUtils();
		
		pwLockLauncher = (EditText) findViewById(R.id.pwEt);
		btntest = (Button) findViewById(R.id.btntest);
		
		DateTv = (TextView) findViewById(R.id.pwtvDate);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format;
		format = new SimpleDateFormat("MMMM dd, yyyy");
		DateTv.setText(format.format(c.getTime()));
		
		pwLockLauncher.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					getPwStr = pwLockLauncher.getText().toString();
					Log.d(TAG, " getPwStr " + getPwStr + " getValueNgn " + getValueNgnPW);
					
					if (getPwStr.equals(getValueNgnPW)) {
						Log.d(TAG, "xxxxxxxxxxxx");
						finish();
					}
					else
					{
						Log.d(TAG, "yyyyyyyy");
						pwLockLauncher.setText("");
						Intent intent = new Intent (LockScreenPasswordActivity.this, FragmentBaseScreenSettings.class);
						startActivity(intent);
						finish();
					}
					
	            } 
				return false;
			}
		});
		
	}
	
	// Don't finish Activity on Back press
	@Override
	public void onBackPressed() {
		return;
	}

	//Simply unlock device by finishing the activity
	private void unlockDevice()
	{
		finish();
	}

	@SuppressWarnings("deprecation")
	private void disableKeyguard() {
		KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
		mKL.disableKeyguard();
	}

	@SuppressWarnings("deprecation")
	private void enableKeyguard() {
		KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
		mKL.reenableKeyguard();
	}

	// Handle events of calls and unlock screen if necessary
	private class StateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			}
		}
	};


	public void makeFullScreen() {
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(Build.VERSION.SDK_INT < 19)
		{ 
			//View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
			this.getWindow().getDecorView().setSystemUiVisibility(View.GONE);
		} 
		else
		{
			this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
		}
	}

	// Handle button clicks
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
				|| (keyCode == KeyEvent.KEYCODE_POWER)
				|| (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
				|| (keyCode == KeyEvent.KEYCODE_CAMERA)) {
			return true;
		}
		if ((keyCode == KeyEvent.KEYCODE_HOME)) {

			return true;
		}

		return false;

	}

	// handle the key press events here itself
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
				|| (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
				|| (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
			return false;
		}
		if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {

			return true;
		}
		return false;
	}
}
