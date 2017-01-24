package com.ntek.wallpad.watcher;

import java.lang.Thread.UncaughtExceptionHandler;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.anrwatchdog.ANRError;
import com.github.anrwatchdog.ANRWatchDog;

/**
 * it observe two kinds of errors that causes application crash
 * 1. Exceptions
 * 2. ANR 
 */
public class ApplicationStateWatcher {
	private static String TAG = ApplicationStateWatcher.class.getCanonicalName();
	
	private Context mContext;
	private final int defaultRequestCode = 192837;
	
	public static final long SEC = 1000;
	public static final long MINUTES = 60 * SEC;
	public static final long HOURS = 60 * MINUTES;
	public static final long DAYS = 24 * HOURS;
	
	public ApplicationStateWatcher(Context newContext) 
	{	this.mContext = newContext.getApplicationContext();
	}
	
	public void startWatching() 
	{	new UncaughtExceptionWatcher().startWatching();
		new ANRExceptionWatcher().startWatching();
	}
	
	private void createAlarmService(Context context) 
	{	Log.d(TAG, "---- uncaughtException : createAlarmService()");
		Intent myIntent = new Intent(context, CrashApplicationRestarter.class);
		myIntent.putExtra("ANR_DETECT", true);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, defaultRequestCode, myIntent, PendingIntent.FLAG_ONE_SHOT);
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
	}
	
	private void exitProcess() 
	{	android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(10);
	}
	
	// watching for exception
	private class UncaughtExceptionWatcher 
	{	private UncaughtExceptionHandler defaultUEH;
		private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler;
		
		public UncaughtExceptionWatcher() 
		{	Log.d(TAG,"---- UncaughtExceptionWatcher");
		}
		
		private void prepare() 
		{	_unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() 
			{	@Override 
				public void uncaughtException(Thread thread, Throwable ex) 
				{	Log.d(TAG, "---- uncaughtException");
					// re-throw critical exception further to the os (important)
					 defaultUEH.uncaughtException(thread, ex);
					 createAlarmService(mContext);
					exitProcess();
				}
			};

			defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
			// setup handler for uncaught exception
			Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);			
			
		}
		
		public void startWatching() 
		{	Log.d(TAG,"---- startWataching UncaughtExceptionWatcher");
			prepare();
		}
	}
	
	// watching for ANR
	private class ANRExceptionWatcher 
	{	private ANRWatchDog watchDoc;
		
		public ANRExceptionWatcher() 
		{	Log.d(TAG,"---- ANRExceptionWatcher");
			watchDoc = new ANRWatchDog();
		}

		public void prepare() 
		{	watchDoc.setANRListener(new ANRWatchDog.ANRListener() 
			{	@Override
	            public void onAppNotResponding(ANRError error) 
				{	Log.d(TAG,"---- onAppNotResponding");
		          	createAlarmService(mContext);
					exitProcess();
	            }
	        });
		}
		
		public void startWatching() 
		{	Log.d(TAG,"---- startWataching ANRExceptionWatcher");
			prepare();
			watchDoc.start();
		}
		
	}
	
	
}
