package com.ntek.wallpad;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

public class RegularSipRegister {
	private static String TAG = RegularSipRegister.class.getCanonicalName();
	
	private Context m_context;
	
	AsyncTask<Void, Void, Void> mRegisterTask = null;
	
	private static boolean mFlag = false;
	
	public RegularSipRegister(Context context) {
		this.m_context = context;
	}
	
	public void start()
	{
		Log.e(TAG, "start()");		
		
		INgnConfigurationService mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		
		String proxy = "";
		String impi = "";
		String password = "";
		
		impi = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI);
		password = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnConfigurationEntry.DEFAULT_IDENTITY_PASSWORD);
		proxy = mConfigurationService.getString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_HOST);
		if((proxy!=null && !proxy.equals(NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_HOST)) &&
			(impi!=null && !impi.equals(NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI)) &&	
			(password!=null && !password.equals(NgnConfigurationEntry.DEFAULT_IDENTITY_PASSWORD))) {
			Log.e(TAG, "impi : " + impi);
			Log.e(TAG, "proxy : " + proxy);
			/*if(!mFlag) {
				mFlag = true;
				mHandler.sendEmptyMessageDelayed(0, 1000);
				SIP_REGISTER();
			}*/
			SIP_REGISTER();
		}
	}
	
	public void SIP_REGISTER() {
		Log.e(TAG, "SIP_REGISTER()");	
		PowerManager pm = (PowerManager) m_context.getSystemService( Context.POWER_SERVICE );				
		final PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "DOORTALK" );		
 		final INgnSipService mSipService = NgnEngine.getInstance().getSipService();
 		mRegisterTask = new AsyncTask<Void, Void, Void>() {
				@Override
				protected void onPreExecute() {
					// TODO Auto-generated method stub
					super.onPreExecute();
					wakeLock.acquire();
				}

				@Override
				protected Void doInBackground(Void... data) {
					mSipService.unRegister();
					try {
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) { }
					mSipService.register(m_context);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);
					wakeLock.release();
				}
			};
			mRegisterTask.execute(null, null, null);
	}
	
	/*Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0) {
				mFlag = false;
			}
		}
	};*/
}
