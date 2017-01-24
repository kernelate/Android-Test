package com.ntek.wallpad.automation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DeviceListPreferences {
	private static final String TAG = DeviceListPreferences.class.getCanonicalName();
	private static final String SHARED_PREF_NAME = TAG;
	
	public static final String DEVICE_LIST = TAG + "DEVICE_LIST";
	public static final String DEFAULT_DEVICE_LIST = "";
	
	
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mSettingsEditor;

	public DeviceListPreferences(Context context) {
		mSettings = context.getSharedPreferences(DeviceListPreferences.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		mSettingsEditor = mSettings.edit();
	}

	public boolean putString(final String entry, String value, boolean commit) {
		if(mSettingsEditor == null){
			Log.e(TAG, "Settings are null");
			return false;
		}
		mSettingsEditor.putString(entry.toString(), value);
		if(commit) {
			return mSettingsEditor.commit();
		}
		return true;
	}
	
	public boolean putString(final String entry, String value) {
		return putString(entry, value, false);
	}

	public String getString(final String entry, String defaultValue) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null");
			return defaultValue;
		}
		try{
			return mSettings.getString(entry.toString(), defaultValue);
		}
		catch(Exception e){
			e.printStackTrace();
			return defaultValue;
		}
	}

	public boolean commit() {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null");
			return false;
		}
		return mSettingsEditor.commit();
	}
}
