package com.ntek.wallpad.lockscreen;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.FragmentBaseScreenSettings;
import com.ntek.wallpad.FragmentPhone;
import com.ntek.wallpad.Main;
import com.ntek.wallpad.R;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class LockScreenPinActivity extends Activity implements OnClickListener {

	private final static String TAG = LockScreenPinActivity.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private LockscreenPatternUtils mLockscreenUtils;
	private EditText pinLockLauncherEt;
	private String getPinStr;
	private String getValueNgnPin;
	private String getValueNgnPattern;
	static int action_setting;
	private TextView DateTv;
	private Button pinOk;
	private Button pinBack;
	private Button[] btnDialpad = new Button[12];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_setting_security_pin_launcher);
		init();
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
		getValueNgnPin = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PIN, NgnConfigurationEntry.SCREEN_LOCK_PIN_DEFAULT);
		getValueNgnPattern = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT); 

		//Set up our Lockscreen
		makeFullScreen();

		if (getValueNgnPin.length() > 3 || getValueNgnPattern.length() > 3 )
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

			//					Log.d(TAG, " teststr is not null " + testbtn.getText().toString() + " ****** " + action_setting);

			if (!mConfigurationService.getBoolean(NgnConfigurationEntry.SCREEN_LOCK_PIN, false)) 
			{
				Log.d(TAG, " SCREEN_LOCK_PIN ");
				// disable keyguard
				disableKeyguard();
				// lock home button
				//						lockHomeButton();
				// start service for observing intents

			}
			else
			{
				if (action_setting == 7)
				{
					Log.d(TAG, " action_setting " + action_setting);
					Intent intent = new Intent (LockScreenPinActivity.this, FragmentBaseScreenSettings.class);
					startActivity(intent);
					finish();
				}
				else if (action_setting == 6)
				{
					Intent intent = new Intent (LockScreenPinActivity.this, FragmentPhone.class);
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
			Intent intent = new Intent (LockScreenPinActivity.this, FragmentBaseScreenSettings.class);
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
		//		pinBack = (Button) findViewById(R.id.pin_back);
		DateTv = (TextView) findViewById(R.id.pintvDate);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format;
		format = new SimpleDateFormat("MMMM dd, yyyy");
		DateTv.setText(format.format(c.getTime()));

		btnDialpad[0] = (Button) findViewById(R.id.pin_zero);
		btnDialpad[1] = (Button) findViewById(R.id.pin_one);
		btnDialpad[2] = (Button) findViewById(R.id.pin_two);
		btnDialpad[3] = (Button) findViewById(R.id.pin_three);
		btnDialpad[4] = (Button) findViewById(R.id.pin_four);
		btnDialpad[5] = (Button) findViewById(R.id.pin_five);
		btnDialpad[6] = (Button) findViewById(R.id.pin_six);
		btnDialpad[7] = (Button) findViewById(R.id.pin_seven);
		btnDialpad[8] = (Button) findViewById(R.id.pin_eight);
		btnDialpad[9] = (Button) findViewById(R.id.pin_nine);
		btnDialpad[10] = (Button) findViewById(R.id.pin_ok);
		btnDialpad[11] = (Button) findViewById(R.id.pin_back);

		for (int i = 0; i < btnDialpad.length; i++)
		{
			btnDialpad[i].setOnClickListener(this);
			//			btnDialpad[i].setTypeface(font);
			btnDialpad[i].setTag(i);
		}

		pinOk = (Button) findViewById(R.id.pin_ok);

		pinLockLauncherEt = (EditText) findViewById(R.id.pinEt);
		pinLockLauncherEt.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);
		//		pinLockLauncherEt.setInputType(InputType.TYPE_NULL);
		pinLockLauncherEt.setTextIsSelectable(true);
		pinLockLauncherEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					getPinStr = pinLockLauncherEt.getText().toString();

					if (getPinStr.equals(getValueNgnPin)) {
						finish();
					}
					else
					{
						finish();
					}

				} 
				return false;
			}
		});

		pinOk.setOnClickListener(this);

	}

	private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
		@Override
		public CharSequence getTransformation(CharSequence source, View view) {
			return source;
		}
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

	@Override
	public void onClick(View v) {

		switch (v.getId()) 
		{
			case R.id.pin_one  :
			case R.id.pin_two  :
			case R.id.pin_three  :
			case R.id.pin_four  :
			case R.id.pin_five  :
			case R.id.pin_six  :
			case R.id.pin_seven  :
			case R.id.pin_eight  :
			case R.id.pin_nine  :
			case R.id.pin_zero  :
				appendText(Integer.parseInt(v.getTag().toString()) == 10 ? "*" : (Integer.parseInt(v.getTag().toString()) == 11 ? "#" : v.getTag().toString()));
				break;
	
			case R.id.pin_ok :
				if (getValueNgnPin.equals(pinLockLauncherEt.getText().toString())) {
					Log.d(TAG, " getValueNgnPin " + getValueNgnPin + " pinLockLauncherEt " + pinLockLauncherEt.getText());
					finish();
				}
				else
				{
					Toast.makeText(LockScreenPinActivity.this, "wrong pin", Toast.LENGTH_SHORT).show();
				}
				break;
	
			case R.id.pin_back  :
				decrementText();
				break;
	
	
			default:
				break;
		}

	}
	
	
	private void appendText(String textToAppend){
		final int selStart = pinLockLauncherEt.getSelectionStart();
		final StringBuffer sb = new StringBuffer(pinLockLauncherEt.getText().toString());
		sb.insert(selStart, textToAppend);
		pinLockLauncherEt.setText(sb.toString());
		pinLockLauncherEt.setSelection(selStart+1);
	}
	
	private void decrementText(){
		final int selStart = pinLockLauncherEt.getSelectionStart();
		if(selStart >0) {
			final StringBuffer sb = new StringBuffer(pinLockLauncherEt.getText().toString());
			sb.delete(selStart-1, selStart);
			pinLockLauncherEt.setText(sb.toString());
			pinLockLauncherEt.setSelection(selStart-1);
		}
		else
		{
			try
			{
				String text = pinLockLauncherEt.getText().toString();
				pinLockLauncherEt.setText(text.substring(0, text.length() - 1));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				
			}
		}
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		Log.d(TAG, " touch touch touch touch touch ");
//		
//		View decorView = getWindow().getDecorView();
//		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//		                              | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//		                              | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//		                              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//		                              | View.SYSTEM_UI_FLAG_FULLSCREEN
//		                              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//		
//		return super.onTouchEvent(event);
//	}
	
	
	
}
