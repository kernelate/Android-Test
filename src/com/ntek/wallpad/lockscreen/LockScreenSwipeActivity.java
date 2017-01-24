package com.ntek.wallpad.lockscreen;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ntek.wallpad.R;
import com.ntek.wallpad.glowpadview.GlowPadView;
import com.ntek.wallpad.glowpadview.GlowPadView.OnTriggerListener;

import android.app.Activity;
import android.app.KeyguardManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class LockScreenSwipeActivity extends Activity {

	private final static String TAG = LockScreenSwipeActivity.class.getCanonicalName();
	GlowPadView mGlowPadView;
	private TextView DateTv;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_setting_security_swipe);
		
		DateTv = (TextView) findViewById(R.id.swipetvDate);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat format;
		format = new SimpleDateFormat("MMMM dd, yyyy");
		DateTv.setText(format.format(c.getTime()));
		
		mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);
		mGlowPadView.setOnTriggerListener(new OnTriggerListener() {

			@Override
			public void onTrigger(View v, int target) {
				Log.d(TAG, " onTriggeronTriggeronTriggeronTriggeronTrigger ");
				finish();

			}

			@Override
			public void onReleased(View v, int handle) {
			}

			@Override
			public void onGrabbedStateChange(View v, int handle) {
			}

			@Override
			public void onGrabbed(View v, int handle) {
			}

			@Override
			public void onFinishFinalAnimation() {
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
