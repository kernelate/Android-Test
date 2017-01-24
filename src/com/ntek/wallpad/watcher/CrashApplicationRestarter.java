package com.ntek.wallpad.watcher;

import java.util.ArrayList;

import org.doubango.ngn.NgnApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.ntek.wallpad.Main;

public class CrashApplicationRestarter  extends BroadcastReceiver {
	private static String TAG = ApplicationStateWatcher.class.getCanonicalName();
	
	private Context mContext;
	private final static ArrayList<Handler> mHandlers = new ArrayList<Handler>();
	
	public CrashApplicationRestarter() 
	{	Log.d(TAG,"---- CrashApplicationRestarter()");
		mContext = NgnApplication.getContext();
	}
	
	@Deprecated
	public static void registerTerminableHandler(Handler handler) 
	{	Log.d(TAG,"---- registerTerminableHandler()");
		mHandlers.add(handler);
	}
	
	@Deprecated
	private void terminateHandler() 
	{	Log.d(TAG,"---- terminateHandler");
		for (Handler h : mHandlers) 
		{	h.removeCallbacks(null);
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{	Log.d(TAG,"---- CrashApplicationRestarter - onReceive()");
		
		if(intent == null || intent.getExtras() == null) 
		{	Log.d(TAG, "CrashApplicationRestarter - App cannot restart");
			return;
		}
		
		if ( intent.getExtras().getBoolean("ANR_DETECT", false) ) 
		{	Log.d(TAG, "App restarts");

			Intent main = new Intent(mContext, Main.class);
			main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
	                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
	                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			mContext.startActivity(main);
		}
	}
}