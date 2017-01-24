package com.ntek.wallpad.Database;

import java.util.ArrayList;

import com.ntek.wallpad.Utils.DoorTalkDevice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DoorTalkDatabaseDao {

	
	private DoorTalkDeviceDatabaseHelper uContactDbs;
	private SQLiteDatabase contactDb;

	private final String[] deviceListAllColl = 
		{
			DeviceListTable.ID,
			DeviceListTable.MAC_ADDRESS,
			DeviceListTable.PLATFORM_VERSION,
			DeviceListTable.IP_ADDRESS,
			DeviceListTable.CALL_ID,
			DeviceListTable.DISPLAY_NAME,
			DeviceListTable.NETWORK, 
			DeviceListTable.LOGIN_ID,
			DeviceListTable.LOGIN_PASSWORD,
			DeviceListTable.SAVED
		};
	

	public DoorTalkDatabaseDao(Context context) {
		uContactDbs = new DoorTalkDeviceDatabaseHelper(context);
	}

	public void Open() {
		contactDb = uContactDbs.getWritableDatabase();
	}	

	public void Close() {
		if(contactDb!=null) {
			contactDb.close();
		}		
	}

	private class DoorTalkDeviceDatabaseHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "DoorTalkDevice.db";
		private static final int DATABASE_VERSION = 1;

		public DoorTalkDeviceDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DeviceListTable.CREATE_DATABASE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DeviceListTable.DROP_DATABASE);
			onCreate(db);
		}	
	}
	
	private class DeviceListTable { //Contacts Table
		public static final String TABLE_NAME = "dt_devices";
		public static final String ID = "_id"; // ID
		public static final String MAC_ADDRESS = "mac_address"; // text
		public static final String PLATFORM_VERSION = "platform_version"; // text
		public static final String IP_ADDRESS = "ip_address"; // text
		public static final String DISPLAY_NAME = "display_name"; // text
		public static final String CALL_ID = "call_id"; // text
		public static final String NETWORK = "network"; // text
		public static final String LOGIN_ID = "login_id"; // text
		public static final String LOGIN_PASSWORD = "login_password"; // text
		public static final String SAVED = "saved"; // integer (0/1)
		
		public static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_NAME + "(" 
				+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ MAC_ADDRESS + " TEXT, "
				+ PLATFORM_VERSION + " TEXT, "
				+ IP_ADDRESS + " TEXT, " 
				+ CALL_ID + " TEXT, "
				+ DISPLAY_NAME + " TEXT, "
				+ NETWORK + " TEXT, " 
				+ LOGIN_ID + " TEXT, " 
				+ LOGIN_PASSWORD + " TEXT, " 
				+ SAVED + " INTEGER NOT NULL); ";
		
		public static final String DROP_DATABASE = "DROP TABLE IF EXIST " + TABLE_NAME;
		
		public DeviceListTable() {
			//default
		}
	}
	
	public DoorTalkDevice getDoorTalkDevice(int id){
		DoorTalkDevice newContact = null;
		Cursor cursor = null;
		try {
			final String where = DeviceListTable.ID + "=" + id;
			cursor  = contactDb.query(DeviceListTable.TABLE_NAME, deviceListAllColl, where, 
					null, null, null, null);
		} catch (Exception e) {
		}
		
		if (cursor != null){
			if (cursor.moveToFirst()){
				final String macAddress = cursor.getString(cursor.getColumnIndex(DeviceListTable.MAC_ADDRESS));
				final String platformVersion = cursor.getString(cursor.getColumnIndex(DeviceListTable.PLATFORM_VERSION));
				final String ipAddress = cursor.getString(cursor.getColumnIndex(DeviceListTable.IP_ADDRESS));
				final String callId = cursor.getString(cursor.getColumnIndex(DeviceListTable.CALL_ID));
				final String displayName = cursor.getString(cursor.getColumnIndex(DeviceListTable.DISPLAY_NAME));
				final String network = cursor.getString(cursor.getColumnIndex(DeviceListTable.NETWORK));
				final String loginId = cursor.getString(cursor.getColumnIndex(DeviceListTable.LOGIN_ID));
				final String loginPassword = cursor.getString(cursor.getColumnIndex(DeviceListTable.LOGIN_PASSWORD));
				final boolean saved = cursor.getInt(cursor.getColumnIndex(DeviceListTable.SAVED)) == 1;
				
				newContact = new DoorTalkDevice(id, macAddress, platformVersion, ipAddress, callId, displayName, network, loginId, loginPassword, saved);
			}
			cursor.close();
		}
		
		return newContact;
	}
	
	public DoorTalkDevice getDoorTalkDevice(String macAddress){
		DoorTalkDevice newContact = null;
		Cursor cursor = null;
		try {
			final String where = DeviceListTable.MAC_ADDRESS + "=\"" + macAddress + "\"";
			cursor  = contactDb.query(DeviceListTable.TABLE_NAME, deviceListAllColl, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (cursor != null){
			if (cursor.moveToFirst()){
				final int id = cursor.getInt(cursor.getColumnIndex(DeviceListTable.ID));
				final String platformVersion = cursor.getString(cursor.getColumnIndex(DeviceListTable.PLATFORM_VERSION));
				final String ipAddress = cursor.getString(cursor.getColumnIndex(DeviceListTable.IP_ADDRESS));
				final String callId = cursor.getString(cursor.getColumnIndex(DeviceListTable.CALL_ID));
				final String displayName = cursor.getString(cursor.getColumnIndex(DeviceListTable.DISPLAY_NAME));
				final String network = cursor.getString(cursor.getColumnIndex(DeviceListTable.NETWORK));
				final String loginId = cursor.getString(cursor.getColumnIndex(DeviceListTable.LOGIN_ID));
				final String loginPassword = cursor.getString(cursor.getColumnIndex(DeviceListTable.LOGIN_PASSWORD));
				final boolean saved = cursor.getInt(cursor.getColumnIndex(DeviceListTable.SAVED)) == 1;
				
				newContact = new DoorTalkDevice(id, macAddress, platformVersion, ipAddress, callId, displayName, network, loginId, loginPassword, saved);
			}
			cursor.close();
		}
		
		return newContact;
	}
	
	public DoorTalkDevice getDoorTalkDevice(String ipAddress, String network){
		DoorTalkDevice newContact = null;
		Cursor cursor = null;
		try {
			final String where = DeviceListTable.IP_ADDRESS + "=\"" + ipAddress + "\" AND " +
					DeviceListTable.NETWORK + "=\"" + network + "\"";
			cursor  = contactDb.query(DeviceListTable.TABLE_NAME, deviceListAllColl, where, 
					null, null, null, null);
		} catch (Exception e) {
		}
		
		if (cursor != null){
			if (cursor.moveToFirst()){
				final int id = cursor.getInt(cursor.getColumnIndex(DeviceListTable.ID));
				final String macAddress = cursor.getString(cursor.getColumnIndex(DeviceListTable.MAC_ADDRESS));
				final String platformVersion = cursor.getString(cursor.getColumnIndex(DeviceListTable.PLATFORM_VERSION));
				final String callId = cursor.getString(cursor.getColumnIndex(DeviceListTable.CALL_ID));
				final String displayName = cursor.getString(cursor.getColumnIndex(DeviceListTable.DISPLAY_NAME));
				final String loginId = cursor.getString(cursor.getColumnIndex(DeviceListTable.LOGIN_ID));
				final String loginPassword = cursor.getString(cursor.getColumnIndex(DeviceListTable.LOGIN_PASSWORD));
				final boolean saved = cursor.getInt(cursor.getColumnIndex(DeviceListTable.SAVED)) == 1;
				
				newContact = new DoorTalkDevice(id, macAddress, platformVersion, ipAddress, callId, displayName, network, loginId, loginPassword, saved);
			}
			cursor.close();
		}
		
		return newContact;
	}
	public boolean addDoorTalkDevice(DoorTalkDevice device) {
		ContentValues cv = new ContentValues();
		int resultRow = 0;
		
		cv.put(DeviceListTable.MAC_ADDRESS, device.getMacAddress());
		cv.put(DeviceListTable.PLATFORM_VERSION, device.getPlatformVersion());
		cv.put(DeviceListTable.IP_ADDRESS, device.getIpAddress());
		cv.put(DeviceListTable.CALL_ID, device.getCallId());
		cv.put(DeviceListTable.DISPLAY_NAME, device.getDisplayName());
		cv.put(DeviceListTable.NETWORK, device.getNetwork());
		cv.put(DeviceListTable.LOGIN_ID, device.getLoginId());
		cv.put(DeviceListTable.LOGIN_PASSWORD, device.getLoginPassword());
		cv.put(DeviceListTable.SAVED, device.isSaved());
		
		try {
			contactDb.beginTransaction();
			resultRow = (int) contactDb.insert(DeviceListTable.TABLE_NAME, null, cv);
			contactDb.setTransactionSuccessful();
		}
		catch (SQLiteConstraintException e) {
			e.printStackTrace(); // TODO remove
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			contactDb.endTransaction();
		}
		
		if (resultRow > 0) return true;
		
		return false;
	}
	
	public boolean updateDeviceInfo(DoorTalkDevice device, boolean noMac){
		ContentValues cv = new ContentValues();
		try {
			String where = DeviceListTable.MAC_ADDRESS + "=\"" + device.getMacAddress() + "\"";
			
			if(noMac) {
				where = DeviceListTable.IP_ADDRESS + "=\"" + device.getIpAddress() + "\" AND " + 
				DeviceListTable.NETWORK + "=\"" + device.getNetwork() + "\"";
			}
			
			cv.put(DeviceListTable.DISPLAY_NAME , device.getDisplayName());
			cv.put(DeviceListTable.IP_ADDRESS , device.getIpAddress());
			cv.put(DeviceListTable.PLATFORM_VERSION , device.getPlatformVersion());
			cv.put(DeviceListTable.CALL_ID , device.getCallId());
			cv.put(DeviceListTable.NETWORK , device.getNetwork());
			cv.put(DeviceListTable.LOGIN_ID , device.getLoginId());
			cv.put(DeviceListTable.LOGIN_PASSWORD, device.getLoginPassword());
			
			contactDb.beginTransaction();
			contactDb.update(DeviceListTable.TABLE_NAME, cv, where, null);
			contactDb.setTransactionSuccessful();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally{
			contactDb.endTransaction();
		}
		return true;
	}
	
	public boolean forgetDoorTalkDevice(int id){
		try {
			final String where = DeviceListTable.ID + "='" + id + "'";
			
			contactDb.beginTransaction();
			contactDb.delete(DeviceListTable.TABLE_NAME, where, null);
			contactDb.setTransactionSuccessful();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally{
			contactDb.endTransaction();
		}
		return true;
	}
	
	public ArrayList<DoorTalkDevice> getDoorTalkDeviceList() {
		Cursor cursor = null;
		ArrayList<DoorTalkDevice> deviceList = new ArrayList<DoorTalkDevice>();
		Log.d(" DoorTalkDatabaseDao ", " getDoorTalkDeviceListgetDoorTalkDeviceListgetDoorTalkDeviceList ");
		try {
			cursor = contactDb.query(DeviceListTable.TABLE_NAME, deviceListAllColl, null, null, null, null, DeviceListTable.DISPLAY_NAME);
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
		}

		if (cursor != null) {
			if (cursor.moveToFirst()){
				while (!cursor.isAfterLast()) {
					final int id = cursor.getInt(cursor.getColumnIndex(DeviceListTable.ID));
					final String macAddress = cursor.getString(cursor.getColumnIndex(DeviceListTable.MAC_ADDRESS));
					final String platformVersion = cursor.getString(cursor.getColumnIndex(DeviceListTable.PLATFORM_VERSION));
					final String ipAddress = cursor.getString(cursor .getColumnIndex(DeviceListTable.IP_ADDRESS));
					final String callId = cursor.getString(cursor.getColumnIndex(DeviceListTable.CALL_ID));
					final String displayName = cursor.getString(cursor .getColumnIndex(DeviceListTable.DISPLAY_NAME));
					final String network = cursor.getString(cursor.getColumnIndex(DeviceListTable.NETWORK));
					final String loginId = cursor.getString(cursor .getColumnIndex(DeviceListTable.LOGIN_ID));
					final String loginPassword = cursor.getString(cursor.getColumnIndex(DeviceListTable.LOGIN_PASSWORD));
					final boolean saved = cursor.getInt(cursor .getColumnIndex(DeviceListTable.SAVED)) == 1;
					
					DoorTalkDevice newContact = new DoorTalkDevice(id, macAddress, platformVersion, ipAddress, callId, displayName, network, loginId, loginPassword, saved);
					
					deviceList.add(newContact);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		
		return deviceList;
	}
	
	public boolean isDeviceExist(final String macAddress){
		Cursor cursor = null;
		try {
			final String where = DeviceListTable.MAC_ADDRESS + "=\"" + macAddress + "\"";
			cursor  = contactDb.query(DeviceListTable.TABLE_NAME, deviceListAllColl, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace(); //TODO remove this
			return false;
		}

		if (cursor != null) 
			if (cursor.moveToFirst()) { 
				cursor.close();
				return true; //check if true
			}
		
		return false;
	}
	
	public boolean isDeviceExist(final String ipAddress, final String ssid){
		Cursor cursor = null;
		try {
			final String where = DeviceListTable.IP_ADDRESS + "=\"" + ipAddress + "\" AND " + 
					DeviceListTable.NETWORK + "=\"" + ssid + "\"";
			cursor  = contactDb.query(DeviceListTable.TABLE_NAME, deviceListAllColl, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace(); //TODO remove this
			return false;
		}

		if (cursor != null) 
			if (cursor.moveToFirst()) { 
				cursor.close();
				return true; //check if true
			}
		
		return false;
	}
	
	
	
}