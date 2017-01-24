package com.ntek.wallpad.lockscreen;

import com.ntek.wallpad.R;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LockscreenPatternService extends Service {

	private BroadcastReceiver mReceiver = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		  IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
          filter.addAction(Intent.ACTION_SCREEN_OFF);
          mReceiver = new LockscreenPatternIntentReceiver();
          registerReceiver(mReceiver, filter);
	}
	
	// Register for Lockscreen event intents
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		mReceiver = new LockscreenPatternIntentReceiver();
		registerReceiver(mReceiver, filter);
		startForeground();
//		this.stopSelf();
		return START_STICKY;
	}

	// Run service in foreground so it is less likely to be killed by system
	private void startForeground() {
		Log.d("LockscreenService", "LockscreenService");
		Notification notification = new NotificationCompat.Builder(this)
		 .setContentTitle(getResources().getString(R.string.app_name))
		 .setTicker(getResources().getString(R.string.app_name))
		 .setContentText("Running")
		 .setSmallIcon(R.drawable.wallpad_icon)
		 .setContentIntent(null)
		 .setOngoing(true)
		 .build();
		 startForeground(9999,notification);		
	}

	
	// Unregister receiver
	@Override
	public void onDestroy() {
		Log.d("LockscreenService ", " ondestroy ");
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	
//	public class ScreenReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//		    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//		    	Log.d(" LockscreenPatternService ", " tulog tulog tulog tulog  ");
////	            wasScreenOn = false;
//	        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//	            // and do whatever you need to do here
////	            wasScreenOn = true;
//	        }
//			
//		}
//	}
	
}
