package com.ntek.wallpad.lockscreen;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.Engine;
import com.ntek.wallpad.FragmentBaseScreenSettings;
import com.ntek.wallpad.FragmentPhone;
import com.ntek.wallpad.Main;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingSecurity;
import com.ntek.wallpad.Services.IScreenService;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.lockscreen.MaterialLockView;
import com.ntek.wallpad.lockscreen.MaterialLockView.Cell;

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
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class LockScreenPatternActivity extends Activity implements LockscreenPatternUtils.OnLockStatusChangedListener {

	private final static String TAG = LockScreenPatternActivity.class.getCanonicalName();
	// User-interface
	private Button btnUnlock;

	// Member variables
	private LockscreenPatternUtils mLockscreenUtils;
	private MaterialLockView materialLockView;
	private Button testbtn;
	private INgnConfigurationService mConfigurationService;
	private String getValueNgn;
	private OnChangeFragmentListener mOnFragmentClick;
	public boolean isLock = false;
	static int action_setting;
	private TextView DateTv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.pattern);

		Log.d(TAG, " oncreate " + getValueNgn );
		init();

		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
			//			mScreenService = ((Engine)Engine.getInstance()).getScreenService();    	 
		} 
		getValueNgn = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT);
//		Log.d(TAG, " 1212111212teststr " + getValueNgn + " ///////// " + getValueNgn.length() + mConfigurationService.getBoolean(NgnConfigurationEntry.SCREEN_LOCK_ASD, false));
		if (getValueNgn.length() > 3)
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

			Log.d(TAG, " teststr is not null " + testbtn.getText().toString() + " ****** " + action_setting);

			if (!mConfigurationService.getBoolean(NgnConfigurationEntry.SCREEN_LOCK_ASD, false)) 
			{
				Log.d(TAG, " SCREEN_LOCK_ASD ");
				// disable keyguard
				disableKeyguard();
				// lock home button
				lockHomeButton();
				// start service for observing intents

			}
			else
			{
				if (action_setting == 7)
				{
					Log.d(TAG, " action_setting " + action_setting);
					Intent intent = new Intent (LockScreenPatternActivity.this, FragmentBaseScreenSettings.class);
					startActivity(intent);
					finish();
				}
				else if (action_setting == 6)
				{
					Intent intent = new Intent (LockScreenPatternActivity.this, FragmentPhone.class);
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
			Intent intent = new Intent (LockScreenPatternActivity.this, FragmentBaseScreenSettings.class);
			startActivity(intent);
			finish();
		}

		//Set up our Lockscreen
		makeFullScreen();

		final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

		// unlock screen in case of app get killed by system
		if (getIntent() != null && getIntent().hasExtra("kill") 	&& getIntent().getExtras().getInt("kill") == 1) 
		{
			Log.d(TAG, " enableKeyguard unlockHomeButton");
			enableKeyguard();
			unlockHomeButton();
		} 
		else 
		{
			//
			try {
				//
				//				Log.d(TAG, " disableKeyguard lockHomeButton " + asd);
				//				// disable keyguard
				//				disableKeyguard();
				//
				//				// lock home button
				////				lockHomeButton();
				//
				//				// start service for observing intents
//				startService(new Intent(this, LockscreenPatternService.class));
				//
				//				// listen the events get fired during the call
				StateListener phoneStateListener = new StateListener();
				TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				telephonyManager.listen(phoneStateListener,	PhoneStateListener.LISTEN_CALL_STATE);
				//
			} 
			catch (Exception e)
			{
			}
			//
		}
		
		
		
		
		try {           
			Log.d(TAG, "su.waitFor() 1111111111111");
	        Process su;

	        su = Runtime.getRuntime().exec("su");

//	        String cmd = "dd if=/mnt/sdcard/test.dat of=/mnt/sdcard/test1.dat \n"+ "exit\n";   
	        String cmd = "service call activity 42 s16 com.android.systemui \n";
	        su.getOutputStream().write(cmd.getBytes());

	        Log.d(TAG, "su.waitFor() 222222222222");
	        
	        if ((su.waitFor() != 0)) {
	        	Log.d(TAG, "su.waitFor()  333333333333333");
	            throw new SecurityException();
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        //throw new SecurityException();
	    }
		
		
		
//		try
//		{
//			Log.d(TAG, " asdasdasdas ");
//			
//		    Process su = Runtime.getRuntime().exec("su");
//		    DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
//
////		    outputStream.writeBytes("service call activity 42 s16 com.android.systemui");
//		    outputStream.writeBytes("service call activity 42 s16 com.android.systemui\n");
//		    outputStream.flush();
//
//		    su.waitFor();
//		    
////			Process process = Runtime.getRuntime().exec("service call activity 42 s16 com.android.systemui");
////			InputStreamReader reader = new InputStreamReader(process.getInputStream());
//			
//		    Log.d(TAG, " zxczxczxczxc ");
//		}
		
//		catch(IOException e)
//		{
//			Log.d(TAG, " eeeeeeeeeeeeeeeeee " + e.getMessage());
//		    throw new Exception(e);
//		}
//		catch(InterruptedException e)
//		{
//			Log.d(TAG, " e2 " + e.getMessage());
////		    throw new Exception(e);
//		}
// catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	private void init() {
		mLockscreenUtils = new LockscreenPatternUtils();

		materialLockView = (MaterialLockView) findViewById(R.id.pattern_screenlock);
		testbtn = (Button) findViewById(R.id.testbtn);
		DateTv = (TextView) findViewById(R.id.tvDate);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format;
		format = new SimpleDateFormat("MMMM dd, yyyy");
		DateTv.setText(format.format(c.getTime()));
		
		materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() 
		{
			@Override
			public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) 
			{
				Log.e("SimplePattern detected111 ", SimplePattern);

				if (!SimplePattern.equals(getValueNgn))
				{
					materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);
					materialLockView.clearPattern();
				} 
				else 
				{
					materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Correct);
					isLock = true;
					mConfigurationService.putBoolean(NgnConfigurationEntry.SCREEN_LOCK_ASD, isLock, true);
					unlockHomeButton();
					//					unlockDevice();
					finish();
					Log.d(TAG, " onPatternDetected " + mConfigurationService.getBoolean(NgnConfigurationEntry.SCREEN_LOCK_ASD, false));
				}

				super.onPatternDetected(pattern, SimplePattern);
			}

			@Override
			public void onPatternCellAdded(List<Cell> pattern,String SimplePattern)
			{
				if(SimplePattern.length()==10)
				{
					Log.e("SimplePattern", SimplePattern);
					materialLockView.clearPattern();
					Toast.makeText(LockScreenPatternActivity.this, "Exceeded the maximum number of pattern input", Toast.LENGTH_SHORT).show();
				}
				super.onPatternCellAdded(pattern, SimplePattern);
			}


		});

		testbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				getValueNgn = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT);
				Log.d(TAG, " teststr " + getValueNgn);

				//				unlockDevice();
				unlockHomeButton();
			}
		});

		//		btnUnlock = (Button) findViewById(R.id.btnUnlock);
		//		btnUnlock.setOnClickListener(new View.OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				// unlock home button and then screen on button press
		//				unlockHomeButton();
		//			}
		//		});
	}

	// Handle events of calls and unlock screen if necessary
	private class StateListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				unlockHomeButton();
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				break;
			}
		}
	};

	// Don't finish Activity on Back press
	@Override
	public void onBackPressed() {
		return;
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

	// Lock home button
	public void lockHomeButton() {
		Log.d(TAG, "lockHomeButton");
		mLockscreenUtils.lock(LockScreenPatternActivity.this);
	}

	// Unlock home button and wait for its callback
	public void unlockHomeButton() {
		mLockscreenUtils.unlock();
	}

	// Simply unlock device when home button is successfully unlocked
	@Override
	public void onLockStatusChanged(boolean isLocked) {
		if (!isLocked) {
			unlockDevice();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		unlockHomeButton();
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

	//Simply unlock device by finishing the activity
	private void unlockDevice()
	{
		finish();
	}



	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, " 222222222222 ");
		//		stopService(new Intent(LockScreenActivity.this, LockscreenService.class));

	}

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

}