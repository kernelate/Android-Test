package com.ntek.wallpad.Database;

import java.util.Observable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ntek.wallpad.Database.DbHelper.DeviceInfo;
import com.ntek.wallpad.Database.DbHelper.DeviceMotionDetect;
import com.ntek.wallpad.Database.DbHelper.DeviceRelaySensor;
import com.ntek.wallpad.Database.DbHelper.DeviceSip;
import com.ntek.wallpad.Database.DbHelper.EventInquiry;
import com.ntek.wallpad.Database.DbHelper.InBoundCallBlocking;
import com.ntek.wallpad.Database.DbHelper.MotionDetectEventHistory;
import com.ntek.wallpad.Database.DbHelper.MotionDetectEventHistoryImages;
import com.ntek.wallpad.Database.DbHelper.NotificationChannelAccounts;
import com.ntek.wallpad.Database.DbHelper.OutBoundCalls;
import com.ntek.wallpad.Database.DbHelper.RelaySensorsEventHistory;

public class DbHandler extends Observable{  //DML ����
	@SuppressWarnings("unused")
	private Context ctx;
	static SQLiteDatabase db = null;
	
	static DbHandler dbhandler = null;
	static DbHelper dbHelper = null;
	
	private static final String TAG = "DbHandler";
	
	public DbHandler(Context ctx) {
		this.ctx = ctx;
		if(dbHelper==null){
			dbHelper = new DbHelper(ctx);
		}
		if(db==null){
			db = dbHelper.getWritableDatabase(); //DB�� open ��
		}
	}
	
	public static synchronized DbHandler open(Context ctx) throws SQLException{
		if(dbhandler==null) {
			dbhandler = new DbHandler(ctx);
		}
		else{
			db = dbHelper.getWritableDatabase(); //DB�� open ��
		}
		if (db == null) db = dbHelper.getWritableDatabase();
		return dbhandler;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	//SELECT select_all
	public Cursor select_all(){
		Log.e(TAG,"select_all()");
		Cursor cursor = db.query(true, DbHelper.TABLE_DOORTALK, 
			new String[]{DbHelper.FIELD_NUM, DbHelper.FIELD_EMAIL, DbHelper.FIELD_OSTYPE, DbHelper.FIELD_REGID, DbHelper.FIELD_FACEBOOKID, DbHelper.FIELD_FACEBOOKPASS, DbHelper.FIELD_FACEBOOKFRIEND}, 
	// 2014.07.18 SmartBean_SHCHO : CHANGE SELECT BY NAME > NUM(CLIENT'S NUMBER)
			null, null, null, null, null, null);
		/*if(cursor.getCount()>0) {
			cursor.moveToFirst();
			do {
				Log.e(TAG,"cursor.getString(0) : "+cursor.getString(0));
				Log.e(TAG,"cursor.getString(1) : "+cursor.getString(1));
			} while(cursor.moveToNext());
		}*/
		return cursor;
	}	
	
	//SELECT select_by_impi
	// 2014.08.25 SmartBean_SHCHO : CHANGE METHOD NAME
	public int select_by_impi(String impi){
		Log.e(TAG,"select_by_impi()");
		Log.e(TAG,"impi : "+impi);
		int result = 0;
		Cursor cursor = db.query(true, DbHelper.TABLE_DOORTALK, 
			new String[]{DbHelper.FIELD_NUM}, 
			DbHelper.FIELD_NUM+" = '"+impi+"'", null, null, null, null, null);
		if(cursor.getCount()>0){
        	//Log.e(TAG,"cursor.getString(0) : "+cursor.getString(0));
        	result = cursor.getCount(); 
        }
		return result;
	}
	
	public Cursor get_all_device_sip(){
		Cursor cursor = null;
		try {
			cursor = db.query(DeviceSip.TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public Cursor get_all_device_info(){
		Cursor cursor = null;
		try {
			cursor = db.query(DeviceInfo.TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public Cursor get_all_device_inboundcall(){
		Cursor cursor = null;
		try {
			cursor = db.query(InBoundCallBlocking.TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public Cursor get_all_device_outboundcall(){
		Cursor cursor = null;
		try {
			cursor = db.query(OutBoundCalls.TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public Cursor get_all_notification_channel_accounts(){
		Cursor cursor = null;
		try {
			cursor = db.query(NotificationChannelAccounts.TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public Cursor get_all_device_motion_detect(){
		Cursor cursor = null;
		try {
			cursor = db.query(DeviceMotionDetect.TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	public Cursor getDeviceRelaySensorData(){
		Cursor cursor = null;
		try {
			cursor = db.query(DeviceRelaySensor.TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	// 2014.07.18 SmartBean_SHCHO : DELETE SELECT BY GCMID
	//SELECT select_by_regid
	/*public int select_by_regid(String gcmid){
		Log.e(TAG,"select_by_regid()");
		Log.e(TAG,"gcmid : "+gcmid);
		int result = 0;
		Cursor cursor = db.query(true, DbHelper.TABLE_DOORTALK, 
			new String[]{DbHelper.FIELD_REGID}, 
			DbHelper.FIELD_REGID+" = '"+gcmid+"'", null, null, null, null, null);
		if(cursor.getCount()>0){
        	//Log.e(TAG,"cursor.getString(0) : "+cursor.getString(0));
        	result = cursor.getCount(); 
        }
		return result;
	}*/
	
	//INSERT
	// 2014.07.18 SmartBean_SHCHO : ADD FIELDS FOR EMAIL, OSTYPE, FACEBOOK, ETC
	public long info_insert(String impi, String ostype, String regid, String email, String facebookid, String facebookpassword, String facebookfriend){
		Log.e(TAG,"info_insert()");
		Log.e(TAG,"impi : "+impi);
		Log.e(TAG,"gcmid : "+regid);
		        
		long result = 0;
		
		if(impi==null || regid==null) {
			return result;
		}
		
		ContentValues values = new ContentValues();
		values.put(DbHelper.FIELD_NUM, impi);
		if(email!=null && checkEmail(email)) {
			values.put(DbHelper.FIELD_EMAIL, email);
		}		
		// 2014.07.29 SmartBean_SHCHO : NULL CHECK ONLY
		if(ostype==null) {
			ostype = "a";
		}
		values.put(DbHelper.FIELD_OSTYPE, ostype);
		values.put(DbHelper.FIELD_REGID, regid);
		if(Check_Facebook(facebookid, facebookpassword, facebookfriend)) {
			values.put(DbHelper.FIELD_FACEBOOKID, facebookid);
			values.put(DbHelper.FIELD_FACEBOOKPASS, facebookpassword);
			values.put(DbHelper.FIELD_FACEBOOKFRIEND, facebookfriend);
		}

		/*Cursor cursor = db.query(true, DbHelper.TABLE_DOORTALK, 
				new String[]{DbHelper.FIELD_NUM, DbHelper.FIELD_REGID}, 
				null, null, null, null, null, null);
		
		int total = cursor.getCount();
		Log.e(TAG, "total : "+total);
		if(total>0){
			Log.e(TAG, "select_by_impi(impi) : "+select_by_impi(impi));
			if(select_by_impi(impi)>0){
				if(select_by_gcmid(gcmid)<1){
					info_update_by_gcmid(impi, gcmid);
				}
				else{
					result = db.insert(DbHelper.TABLE_DOORTALK, null, values);
				}
			}
			else {
				if(select_by_gcmid(gcmid)<1){
					info_update_by_gcmid(impi, gcmid);
				}
				else{
					result = db.insert(DbHelper.TABLE_DOORTALK, null, values);
				}
			}
		}
		else{
			result = db.insert(DbHelper.TABLE_DOORTALK, null, values);
		}
		cursor.close();*/
		result = db.insert(DbHelper.TABLE_DOORTALK, null, values);
		return result;
	}
	//INSERT CLIENT INFORMATION FOR INQUIRYING FOR NOTIFICATION VIA GCM
	//2014.23.10e
	public long insert_inquiry(String gcmID, String email, String status, String motionDetectStatus, String relayDetectStatus, String client_os_type, String updateDate, String createdDate){
		System.out.println("insert_inquiry()");
		System.out.println();
		long result = 0;
		if (gcmID == null) return result;
		ContentValues cv = new ContentValues();
		try {
			cv.put(EventInquiry.FIELD_CLIENTGCMID, gcmID);
			if(email!=null && checkEmail(email)) {
				cv.put(EventInquiry.FIELD_CLIENTEMAIL, email);
			}		
			cv.put(EventInquiry.FIELD_STATUS, status);
			cv.put(EventInquiry.FIELD_MOTIONDETECT_STATUS, motionDetectStatus);
			cv.put(EventInquiry.FIELD_RELAYSENSOR_STATUS, relayDetectStatus);
			cv.put(EventInquiry.FIELD_CLIENT_OS_TYPE, client_os_type);
			cv.put(EventInquiry.FIELD_CREATEDDATE, createdDate);
			if (updateDate != null) cv.put(EventInquiry.FIELD_UPDATEDATE, updateDate);
			db.beginTransaction();
			result = db.insert(EventInquiry.TABLE_NAME, null, cv);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			System.out.println("Error Message : " + e.getMessage());
		}finally{
			db.endTransaction();
//			db.close();
			triggerObservers();
		}
		
		return result;
	}
	
	//UPDATE CLIENT INFORMATION FOR INQUIRYING FOR NOTIFICATION VIA GCM
		//2014.23.10
		public long update_inquiry(String gcmID, String email, String status, String motionDetectStatus, String relayDetectStatus, String clientOSType, String updateDate, String createdDate){
			System.out.println("update_inquiry");
			System.out.println();
			long result = 0;
			if (gcmID == null) return result;
			ContentValues cv = new ContentValues();
			try {
				cv.put(EventInquiry.FIELD_CLIENTGCMID, gcmID);
				if(email!=null && checkEmail(email)) {
					cv.put(EventInquiry.FIELD_CLIENTEMAIL, email);
				}		
				cv.put(EventInquiry.FIELD_STATUS, status);
				cv.put(EventInquiry.FIELD_MOTIONDETECT_STATUS, motionDetectStatus);
				cv.put(EventInquiry.FIELD_RELAYSENSOR_STATUS, relayDetectStatus);
				cv.put(EventInquiry.FIELD_CLIENT_OS_TYPE, clientOSType);
				if (createdDate != null) cv.put(EventInquiry.FIELD_CREATEDDATE, createdDate);
				if (updateDate != null) cv.put(EventInquiry.FIELD_UPDATEDATE, updateDate);
				db.beginTransaction();
				result = db.update(EventInquiry.TABLE_NAME, cv, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"", null);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				System.out.println("Error Message : " + e.getMessage());
			}finally{	
				db.endTransaction();
//				db.close();
				triggerObservers();
			}
			return result;
		}
		
		public long update_inquiry(String gcmID, String email, String motionDetectStatus, String relayDetectStatus, String client_os_type, String updateDate){
			System.out.println("update_inquiry");
			System.out.println();
			long result = 0;
			if (gcmID == null) return result;
			ContentValues cv = new ContentValues();
			try {
				cv.put(EventInquiry.FIELD_CLIENTGCMID, gcmID);
				if(email!=null && checkEmail(email)) {
					cv.put(EventInquiry.FIELD_CLIENTEMAIL, email);
				}		
				cv.put(EventInquiry.FIELD_MOTIONDETECT_STATUS, motionDetectStatus);
				cv.put(EventInquiry.FIELD_RELAYSENSOR_STATUS, relayDetectStatus);
				cv.put(EventInquiry.FIELD_CLIENT_OS_TYPE, client_os_type);
				if (updateDate != null) cv.put(EventInquiry.FIELD_UPDATEDATE, updateDate);
				db.beginTransaction();
				result = db.update(EventInquiry.TABLE_NAME, cv, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"", null);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				System.out.println("Error Message : " + e.getMessage());
			}finally{	
				db.endTransaction();
				triggerObservers();
			}
			return result;
		}
		
		public long update_inquiry(String gcmID, String motionDetectStatus, String relayDetectStatus, String updateDate){
			System.out.println("update_inquiry()");
			System.out.println();
			long result = 0;
			if (gcmID == null) return result;
			ContentValues cv = new ContentValues();
			try {
				cv.put(EventInquiry.FIELD_MOTIONDETECT_STATUS, motionDetectStatus);
				cv.put(EventInquiry.FIELD_RELAYSENSOR_STATUS, relayDetectStatus);
				if (updateDate != null) cv.put(EventInquiry.FIELD_UPDATEDATE, updateDate);
				db.beginTransaction();
				result = db.update(EventInquiry.TABLE_NAME, cv, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"", null);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				System.out.println("Error Message : " + e.getMessage());
			}finally{
				db.endTransaction();
				triggerObservers();
			}
			return result;
	   	}
		
		public long update_inquiry_motion(String gcmID, String motionDetectStatus){
			System.out.println("update_inquiry()");
			System.out.println();
			long result = 0;
			if (gcmID == null) return result;
			ContentValues cv = new ContentValues();
			try {
				cv.put(EventInquiry.FIELD_MOTIONDETECT_STATUS, motionDetectStatus);
				db.beginTransaction();
				result = db.update(EventInquiry.TABLE_NAME, cv, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"", null);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				System.out.println("Error Message : " + e.getMessage());
			}finally{
				db.endTransaction();
				triggerObservers();
			}
			return result;
	   	}
		
		public long update_inquiry_status(String gcmID, String relaySensorStatus){
			System.out.println("update_inquiry()");
			System.out.println();
			long result = 0;
			if (gcmID == null) return result;
			ContentValues cv = new ContentValues();
			try {
				cv.put(EventInquiry.FIELD_RELAYSENSOR_STATUS, relaySensorStatus);
				db.beginTransaction();
				result = db.update(EventInquiry.TABLE_NAME, cv, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"", null);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				System.out.println("Error Message : " + e.getMessage());
			}finally{
				db.endTransaction();
//				db.close();
				triggerObservers();
			}
			
			return result;
	   	}
		
		public long update_inquiry_status(String gcmID, String status, String  updateDate){
			System.out.println("update_inquiry()");
			System.out.println();
			long result = 0;
			if (gcmID == null) return result;
			ContentValues cv = new ContentValues();
			try {
				cv.put(EventInquiry.FIELD_STATUS, status);
				if (updateDate != null) cv.put(EventInquiry.FIELD_UPDATEDATE, updateDate);
				db.beginTransaction();
				result = db.update(EventInquiry.TABLE_NAME, cv, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"", null);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				System.out.println("Error Message : " + e.getMessage());
			}finally{
				db.endTransaction();
//				db.close();
				triggerObservers();
			}
			
			return result;
		}
		
		public Integer insert_motion_detect_event_history(String device_uniquekey, String displayname, 
				String event_occurence_time){
			Integer result = 0;
			try {
				ContentValues cv = new ContentValues();
				cv.put(MotionDetectEventHistory.FIELD_SOURCE_DEVICE_DISPLAYNAME, displayname);
				cv.put(MotionDetectEventHistory.FIELD_SOURCE_DEVICENUMBER, device_uniquekey);
				cv.put(MotionDetectEventHistory.FIELD_EVENT_OCCURENCE_TIME, event_occurence_time);
				db.beginTransaction();
				result = (int) db.insert(MotionDetectEventHistory.TABLE_NAME, null, cv);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}finally{
				db.endTransaction();
				triggerObservers();
//				if (result > 0) insert_motion_detect_event_history_image(result , filename, image_path, text_summary);
			}
			return result;
		}
		
		public long insert_motion_detect_event_history_image(Integer eventHistoryID,
				String filename, String imagePath, String text_summary){
			long result = 0;
			try {
				ContentValues cv = new ContentValues();
				cv.put(MotionDetectEventHistoryImages.FIELD_EVENTHISTORY_ID, eventHistoryID);
				cv.put(MotionDetectEventHistoryImages.FIELD_FILENAME, filename);
				cv.put(MotionDetectEventHistoryImages.FIELD_IMAGE_PATH, imagePath);
				db.beginTransaction();
				result = db.insert(MotionDetectEventHistory.TABLE_NAME, null, cv);
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.endTransaction();
				triggerObservers();
			}
			return result;
		}
	
	public long insert_notification_channel_accounts(int notificationID, String channel_code,String account,String motionDetect_enable_YN, String sensors_enable_YN){
		long result = 0;
		ContentValues cv = new ContentValues();
		try {
			cv.put(NotificationChannelAccounts.FIELD_CHANNEL_CODE, channel_code);
			cv.put(NotificationChannelAccounts.FIELD_CHANNEL_ACCOUNT, account);
			cv.put(NotificationChannelAccounts.FIELD_MOTIONDETECT_ENABLE_YN, motionDetect_enable_YN);
			cv.put(NotificationChannelAccounts.FIELD_SENSORS_ENABLE_YN, sensors_enable_YN);
			db.beginTransaction();
			if (check_if_notification_account_exist(notificationID) || check_if_notification_account_exist(account)){
				final String where = NotificationChannelAccounts.FIELD_NOTIFICATION_ID + "=" + notificationID;
				result = db.update(NotificationChannelAccounts.TABLE_NAME, cv, where, null);
			}else{
				result = db.insert(NotificationChannelAccounts.TABLE_NAME, null, cv);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}finally{
			db.endTransaction();
			triggerObservers();
		}
		return result;
	}
	
	private boolean check_if_notification_account_exist(String email){
		Cursor cursor = null;
		boolean result = false;
		try {
			final String where = NotificationChannelAccounts.FIELD_CHANNEL_ACCOUNT + "='" + email + "'";
			cursor = db.query(NotificationChannelAccounts.TABLE_NAME, null, where,
					null, null, null, null);
			if (cursor != null){
				result = cursor.moveToFirst();
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private boolean check_if_notification_account_exist(int id){
		Cursor cursor = null;
		boolean result = false;
		try {
			final String where = NotificationChannelAccounts.FIELD_NOTIFICATION_ID + "=" + id;
			cursor = db.query(NotificationChannelAccounts.TABLE_NAME, null, where,
					null, null, null, null);
			if (cursor != null){
				result = cursor.moveToFirst();
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
		
	public Cursor get_client_event_inquiry(String gcmID){
		Cursor cursor = null;
		final String where = EventInquiry.FIELD_CLIENTGCMID + "='" + gcmID + "'";
		try {
			cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return cursor;
	}
		
	public boolean check_if_gcmID_if_exist(String gcmID){
		boolean result = false;
		Cursor cursor = null;	
		cursor = db.query(EventInquiry.TABLE_NAME, null, EventInquiry.FIELD_CLIENTGCMID + "=\'" + gcmID + "\'", null, null, null, null);
		if (cursor != null){
			result = cursor.moveToFirst();
			cursor.close();
		}
		return result;
	}
	
	public int delete_all_event_inquiry(){
		System.out.println("delete_all_event_inquiry()");
		int result = 0;
		try {
			  result = db.delete(EventInquiry.TABLE_NAME, null, null);	
			  triggerObservers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int delete_event_inquiry(String gcmID){
		int result = 0;
		String where = EventInquiry.FIELD_CLIENTGCMID + "='" + gcmID + "'";
		try {
			  result = db.delete(EventInquiry.TABLE_NAME, where, null);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		triggerObservers();
		return result;
	}
	
	public int delete_notification_email(int id){
		int result = 0;
		String where = NotificationChannelAccounts.FIELD_NOTIFICATION_ID + "=" + id;
		try {
			result = db.delete(NotificationChannelAccounts.TABLE_NAME, where, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean check_if_data_exist(){
		Cursor cursor = null;	
		cursor = db.query(EventInquiry.TABLE_NAME, null, null, null, null, null, null);
		if (cursor != null){
			if (cursor.getCount() > 0)
				return true;
		}
		return false;
	}
	
	public boolean check_record_if_exist(String gcmID, String email, String status, String motionDetectStatus, String relayDetectStatus, String updateDate, String createdDate){
		boolean result = false;
		Cursor cursor = null;
		final String where = EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\" AND " + EventInquiry.FIELD_CLIENTEMAIL + "=\"" + email + "\" AND " 
		+ EventInquiry.FIELD_STATUS + "=\"" + status + "\" AND " + EventInquiry.FIELD_MOTIONDETECT_STATUS + "=\"" + motionDetectStatus + "\" AND "
		+ EventInquiry.FIELD_RELAYSENSOR_STATUS + "=\"" + relayDetectStatus + "\"";
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		
		if (cursor != null){
			result = cursor.moveToFirst();
			cursor.close();
		}
			
		
		return result;
	}

	public Cursor get_all_security_motion_detection_values(){
		Cursor cursor = null;
		cursor = db.query(DeviceMotionDetect.TABLE_NAME, null, null, null, null, null, null);
		return cursor;		
	}
	
	public Cursor get_all_security_relay_detection_values(){
		Cursor cursor = null;
		cursor = db.query(DeviceRelaySensor.TABLE_NAME, null, null, null, null, null, null);
		return cursor;		
	}
	
	public Cursor get_event_inquiry_info(String gcmID){
		Cursor cursor = null;
		cursor = db.query(EventInquiry.TABLE_NAME, null, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"",
				null, null, null, null);
		return cursor;
	}
	
	public boolean check_if_data_exist(String tableName){
		boolean result = false;
		Cursor cursor = null;
		try {
			cursor = db.query(tableName, null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (cursor != null){
			if (cursor.moveToFirst())
				result = cursor.moveToFirst();
			cursor.close();
		}
			
		return result;
	}
	
	public long insert_motion_security_functions(String enable, int sensitivity){
		System.out.println("insert_motion_security_functions(");
		long result = 0;
		ContentValues cv = new ContentValues();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date date = new Date();
		try {
			cv.put(DeviceMotionDetect.FIELD_ENABLE_YN, enable);
			cv.put(DeviceMotionDetect.FIELD_SENSITIVITY, sensitivity);
			db.beginTransaction();
			if (check_if_data_exist(DeviceMotionDetect.TABLE_NAME)){
//				cv.put(DeviceMotionDetect.FIELD_UPDATEDATE, dateFormat.format(date));
				result = db.update(DeviceMotionDetect.TABLE_NAME, cv, null, null);
			}else{
//				cv.put(DeviceMotionDetect.FIELD_CREATEDDATE, dateFormat.format(date));
				result = db.insert(DeviceMotionDetect.TABLE_NAME, null, cv);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			System.out.println("Error Message : " + e.getMessage());
		}finally{
			db.endTransaction();
			triggerObservers();
		}
		return result;
	}
	public String check_motion_security(){		
		String motionSevurity = null;
		Cursor cursor = db.query(DeviceMotionDetect.TABLE_NAME, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			motionSevurity =	cursor.getString(cursor.getColumnIndex(DeviceMotionDetect.FIELD_SENSITIVITY));
		}
		cursor.close();
		return motionSevurity;
	}
	public String check_motion_enable(){		
		String motionEnable = null;
		Cursor cursor = db.query(DeviceMotionDetect.TABLE_NAME, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			motionEnable =	cursor.getString(cursor.getColumnIndex(DeviceMotionDetect.FIELD_ENABLE_YN));
		}
		cursor.close();
		return motionEnable;
	}
	
	public String getRelaySensorEnableStatus() {
		String sensorEnable = null;
		Cursor cursor = db.query(DeviceRelaySensor.TABLE_NAME, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			sensorEnable =	cursor.getString(cursor.getColumnIndex(DeviceRelaySensor.FIELD_ENABLE_YN));
		}
		cursor.close();
		return sensorEnable;
	}
	
	public long insertOrUpdateRelaySensorFunctions(String uniqueId, String relay_name, String enable_YN, 
			String signal1_transaction_value, String signal1_printed_name, String signal1_duration_value,
			String signal1_transaction_noti_YN, String signal1_duration_noti_YN, String signal2_transaction_value,
			String signal2_printed_name, String signal2_duration_value, String signal2_transaction_notification_YN,
			String signal2_duration_notification_YN, String created_date) {
		
		long result = 0;
		ContentValues cv = new ContentValues();
		cv.put(DeviceRelaySensor.FIELD_NICKNAME, relay_name);
		cv.put(DeviceRelaySensor.FIELD_ENABLE_YN, enable_YN);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL1_TRANSACTION_VALUE, signal1_transaction_value);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL1_PRINTED_NAME, signal1_printed_name);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL1_DURATION_VALUE, signal1_duration_value);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL1_TRANSACTION_NOTIFICATION_YN, signal1_transaction_noti_YN);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL1_DURATION_NOTIFICATION_YN, signal1_duration_noti_YN);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL2_TRANSACTION_VALUE, signal2_transaction_value);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL2_PRINTED_NAME, signal2_printed_name);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL2_DURATION_VALUE, signal2_duration_value);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL2_TRANSACTION_NOTIFICATION_YN, signal2_transaction_notification_YN);
		cv.put(DeviceRelaySensor.FIELD_SIGNAL2_DURATION_NOTFICATION_YN, signal2_duration_notification_YN);
		cv.put(DeviceRelaySensor.FIELD_UPDATEDATE, created_date);
		try {
			db.beginTransaction();
			if (check_if_data_exist(DeviceRelaySensor.TABLE_NAME)) {
				String where = DeviceRelaySensor.FIELD_DEVICE_UNIQUE_KEY + " = '" +uniqueId + "'";
				result = db.update(DeviceRelaySensor.TABLE_NAME, cv, where, null);
			}
			else {
				cv.put(DeviceRelaySensor.FIELD_DEVICE_UNIQUE_KEY, uniqueId);
				result = db.insert(DeviceRelaySensor.TABLE_NAME, null, cv);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			System.out.println("Error Message : " + e.getMessage());
		}finally{
			db.endTransaction();
			triggerObservers();
		}
		return result;
	}
	
	public Cursor get_event_inquiry_info(long rowID){
		Cursor cursor = null;
		cursor = db.query(EventInquiry.TABLE_NAME, null, EventInquiry.FIELD_EVENTINQUIRY_ID + "=" + rowID,
				null, null, null, null);
		return cursor;
	}
	
	public Cursor get_all_event_inquiry(){
		Cursor cursor = null;
		cursor = db.query(EventInquiry.TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	
	public Cursor get_all_aprroved_event_inquiry(){
		Cursor cursor = null;
		final String where = EventInquiry.FIELD_STATUS + "=\'active\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		return cursor;
	}
	
	public Cursor getAllActiveEventInquiryEnabledRelaySensor(){
		Cursor cursor = null;
		final String where = EventInquiry.FIELD_STATUS + "=\'active\' AND " + EventInquiry.FIELD_RELAYSENSOR_STATUS + "=\'enabled\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		return cursor;
	}
	
	public String getEventInquiryId(String gcmId){
		Cursor cursor = null;
		String eventInquiryId = "";
		final String where = EventInquiry.FIELD_CLIENTGCMID + "=\'" + gcmId + "\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			eventInquiryId = cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_EVENTINQUIRY_ID));
		}
		cursor.close();
		return eventInquiryId;
	}
	
	public String getAllEventUsersOSType() {
		Cursor cursor = null;
		String osType = "";
		final String where = EventInquiry.FIELD_MOTIONDETECT_STATUS + "=\'enabled\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				osType += cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_CLIENT_OS_TYPE)) + "|";
			} while (cursor.moveToNext());
		}
		cursor.close();
		return osType;
	}
	
	public String getAllGcmIdMotionDetect(){
		Cursor cursor = null;
		String gcmIds = "";
		final String where = EventInquiry.FIELD_MOTIONDETECT_STATUS + "=\'enabled\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				gcmIds += cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_CLIENTGCMID)) + "|";
			} while (cursor.moveToNext());
		}
		cursor.close();
		return gcmIds;
	}
	
	public String getAllStatusMotionDetect(){
		Cursor cursor = null;
		String gcmIdStatus = "";
		final String where = EventInquiry.FIELD_MOTIONDETECT_STATUS + "=\'enabled\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				gcmIdStatus += cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_STATUS)) + "|";
			} while (cursor.moveToNext());
		}
		cursor.close();
		return gcmIdStatus;
	}
	
	public String getAllEventInquiryIdMotionDetect(){
		Cursor cursor = null;
		String eventInquiryIds = "";
		final String where = EventInquiry.FIELD_MOTIONDETECT_STATUS + "=\'enabled\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				eventInquiryIds += cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_EVENTINQUIRY_ID)) + "|";
			} while (cursor.moveToNext());
		}
		cursor.close();
		return eventInquiryIds;
	}
	
	public String getAllGcmIdRelaySensor(){
		Cursor cursor = null;
		String gcmIds = "";
		final String where = EventInquiry.FIELD_RELAYSENSOR_STATUS + "=\'enabled\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				gcmIds += cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_CLIENTGCMID)) + "|";
			} while (cursor.moveToNext());
		}
		cursor.close();
		return gcmIds;
	}
	
	public String getAllStatusRelaySensor(){
		Cursor cursor = null;
		String gcmIdStatus = "";
		final String where = EventInquiry.FIELD_RELAYSENSOR_STATUS + "=\'enabled\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				gcmIdStatus += cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_STATUS)) + "|";
			} while (cursor.moveToNext());
		}
		cursor.close();
		return gcmIdStatus;
	}
	
	public String getAllEventInquiryIdRelaySensor(){
		Cursor cursor = null;
		String eventInquiryIds = "";
		final String where = EventInquiry.FIELD_RELAYSENSOR_STATUS + "=\'enabled\'"; 
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				eventInquiryIds += cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_EVENTINQUIRY_ID)) + "|";
			} while (cursor.moveToNext());
		}
		cursor.close();
		return eventInquiryIds;
	}
	
	public Cursor get_all_pending_event_inquiry(){
		Cursor cursor = null;
		final String where = EventInquiry.FIELD_STATUS + "=\'inactive\'";
		cursor = db.query(EventInquiry.TABLE_NAME, null, where, null, null, null, null);
		return cursor;
	}
	
	public Cursor get_all_notification_motion_detect_enable(){
		Cursor cursor = null;
		final String where = NotificationChannelAccounts.FIELD_MOTIONDETECT_ENABLE_YN + "=\'enabled\'";
		cursor = db.query(NotificationChannelAccounts.TABLE_NAME, null, where, null, null, null, null);
		return cursor;
	}

	public String getAllChannelAccountsRelaySensorEnable(){
		Cursor cursor = null;
		String channelAccounts = "";
		final String where = NotificationChannelAccounts.FIELD_SENSORS_ENABLE_YN + "=\'enabled\'";
		cursor = db.query(NotificationChannelAccounts.TABLE_NAME, null, where, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				channelAccounts += cursor.getString(cursor.getColumnIndex(NotificationChannelAccounts.FIELD_CHANNEL_ACCOUNT)) + "|";
			} while (cursor.moveToNext());
		}
		cursor.close();
		return channelAccounts;
	}
	
	//UPDATE
	// 2014.07.18 SmartBean_SHCHO : CHANGE UPDATE NAME > NUM(CLIENT'S NUMBER)
	// 2014.08.25 SmartBean_SHCHO : CHANGE METHOD NAME
	public long info_update_by_impi(String impi, String ostype, String regid, String email, String facebookid, String facebookpassword, String facebookfriend){
		Log.e(TAG,"info_update_by_impi()");	
		long result = 0;
		
		if(impi==null || regid==null) {
			return result;
		}

		ContentValues values = new ContentValues();
		values.put(DbHelper.FIELD_NUM, impi);
		if(email!=null && checkEmail(email)) {
			values.put(DbHelper.FIELD_EMAIL, email);
		}		
		// 2014.07.29 SmartBean_SHCHO : NULL CHECK ONLY
		if(ostype==null) {
			ostype = "a";
		}
		values.put(DbHelper.FIELD_OSTYPE, ostype);
		values.put(DbHelper.FIELD_REGID, regid);
		if(Check_Facebook(facebookid, facebookpassword, facebookfriend)) {
			values.put(DbHelper.FIELD_FACEBOOKID, facebookid);
			values.put(DbHelper.FIELD_FACEBOOKPASS, facebookpassword);
			values.put(DbHelper.FIELD_FACEBOOKFRIEND, facebookfriend);
		}

		String where = DbHelper.FIELD_NUM+" = '"+impi+"'";
		result = db.update(DbHelper.TABLE_DOORTALK, values, where, new String[]{});

		return result;
	}
	
	public long update_event_inquiry_status(String gcmID, String status, String date){
		long result = 0;
		ContentValues values = new ContentValues();
		values.put(EventInquiry.FIELD_STATUS, status);
		try{
			db.beginTransaction();
			result = db.update(EventInquiry.TABLE_NAME, values, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"", null);
			db.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("update_event_inquiry_status(String gcmID, String status){ : " + e.getMessage());
		}finally{
			db.endTransaction();
			triggerObservers();
		}
		return result;
	}
	
	public long update_event_inquiry_email(String gcmID, String email, String date){
		long result = 0;
		ContentValues values = new ContentValues();
		values.put(EventInquiry.FIELD_CLIENTEMAIL, email);
		try{
			db.beginTransaction();
			result = db.update(EventInquiry.TABLE_NAME, values, EventInquiry.FIELD_CLIENTGCMID + "=\"" + gcmID + "\"", null);
			db.setTransactionSuccessful();
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("update_event_inquiry_status(String gcmID, String status){ : " + e.getMessage());
		}finally{
			db.endTransaction();
			triggerObservers();
		}
		return result;
	}
	
	//UPDATE
	// 2014.07.18 SmartBean_SHCHO : DELETE UPDATE BY GCMID
	/*public long info_update_by_gcmid(String impi, String ostype, String regid, String email, String facebookid, String facebookpassword, String facebookfriend){
		Log.e(TAG,"info_update_by_gcmid()");
			        
		long result = 0;

		ContentValues values = new ContentValues();
		values.put(DbHelper.FIELD_NUM, impi);
		values.put(DbHelper.FIELD_EMAIL, email);
		values.put(DbHelper.FIELD_OSTYPE, ostype);
		values.put(DbHelper.FIELD_REGID, regid);		
		values.put(DbHelper.FIELD_FACEBOOKID, facebookid);
		values.put(DbHelper.FIELD_FACEBOOKPASS, facebookpassword);
		values.put(DbHelper.FIELD_FACEBOOKFRIEND, facebookfriend);

		String where = DbHelper.FIELD_REGID+" = '"+regid+"'";
		result = db.update(DbHelper.TABLE_DOORTALK, values, where, new String[]{});

		return result;
	}*/
	
	//DELETE
	// 2014.08.25 SmartBean_SHCHO : CHANGE METHOD NAME
	public long info_delete_by_impi(String impi){
		Log.e(TAG,"info_delete_by_impi()");
		        
		long result = 0;
				
		Cursor cursor = db.query(true, DbHelper.TABLE_DOORTALK, 
				new String[]{DbHelper.FIELD_NUM, DbHelper.FIELD_REGID}, 
				null, null, null, null, null, null);
	        			        
		int total = cursor.getCount();
		Log.e(TAG, "total : "+total);
		if(total>0){
			result = db.delete(DbHelper.TABLE_DOORTALK, DbHelper.FIELD_NUM+" = '"+impi+"'", null);	
		}
		
		cursor.close();
		return result;
	}
	
	//DELETE
	public long info_delete_by_gcmid(String gcmid){
		Log.e(TAG,"info_delete_by_gcmid()");
		        
		long result = 0;
				
		Cursor cursor = db.query(true, DbHelper.TABLE_DOORTALK, 
				new String[]{DbHelper.FIELD_NUM, DbHelper.FIELD_REGID}, 
				null, null, null, null, null, null);
	        			        
		int total = cursor.getCount();
		Log.e(TAG, "total : "+total);
		if(total>0){
			result = db.delete(DbHelper.TABLE_DOORTALK, DbHelper.FIELD_REGID+" = '"+gcmid+"'", null);	
		}
		
		cursor.close();
		return result;
	}
	
	// 2014.07.18 SmartBean_SHCHO : ADD EMAIL CHECK PATTERN
	public boolean checkEmail(String email){
		 String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
		 Pattern p = Pattern.compile(regex);
		 Matcher m = p.matcher(email);
		 boolean isNormal = m.matches();
		 return isNormal;
	}
	
	public String getDeviceUniqueId() {
		String uniqueId = null;
		Cursor cursor = db.query(DeviceInfo.TABLE_NAME, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			uniqueId =	cursor.getString(cursor.getColumnIndex(DeviceInfo.FIELD_UNIQUE_KEY));
		}
		cursor.close();
		return uniqueId;
	}
	
	public long updateDeviceInfo(String uniqueId, String versionId, String displayName, String updateDate) {
		final String whereClause = DeviceInfo.FIELD_UNIQUE_KEY + " = ?";
		final String[] whereArgs = new String[] { uniqueId };
		long result = -1;
		ContentValues cv = new ContentValues();
		cv.put(DeviceInfo.FIELD_DISPLAYNAME, displayName);
		cv.put(DeviceInfo.FIELD_VERSION_ID, versionId);
		cv.put(DeviceInfo.FIELD_UPDATEDATE, updateDate);
		try {
			db.beginTransaction();
			result = db.update(DeviceInfo.TABLE_NAME, cv, whereClause, whereArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			System.out.println("Error Message : " + e.getMessage());
		} finally {
			db.endTransaction();
			triggerObservers();
		}
		
		return result;
	}
	
	public long insertNewDeviceInfo(String uniqueId, String versionId, String displayName, String createdDate) {
		Log.d(TAG,"insertNewDeviceInfo()");
		db.delete(DeviceInfo.TABLE_NAME, null, null);
		long result = -1;
		
		ContentValues cv = new ContentValues();
		cv.put(DeviceInfo.FIELD_UNIQUE_KEY, uniqueId);
		cv.put(DeviceInfo.FIELD_DISPLAYNAME, displayName);
		cv.put(DeviceInfo.FIELD_VERSION_ID, versionId);
		cv.put(DeviceInfo.FIELD_CREATEDDATE, createdDate);
		cv.put(DeviceInfo.FIELD_UPDATEDATE, createdDate);
		
		try {
			db.beginTransaction();
			result = db.insert(DeviceInfo.TABLE_NAME, null, cv);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG,"insertNewDeviceInfo()",e);
		}finally{
			db.endTransaction();
			triggerObservers();
		}
		
		return result;
	}
	
	public boolean isDeviceInfoExist() {
		boolean isExist = false;
		Cursor mCursor = db.query(DeviceInfo.TABLE_NAME, null, null, null, null, null, null);
		if (mCursor.getCount() > 0) {
			isExist = true;
		}
		mCursor.close();
		
		return isExist;
	}
	
	public int deleteInboundCallList(){
		System.out.println("deleteInboundCallList()");
		int result = 0;
		try {
			  result = db.delete(InBoundCallBlocking.TABLE_NAME, null, null);	
			  triggerObservers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public long insertNewInboundCall(String callee, String information, String createdDate) {
		Log.d(TAG,"insertNewInboundCall()");
		long result = -1;
		
		ContentValues cv = new ContentValues();
		cv.put(InBoundCallBlocking.FIELD_CALLED, callee);
		cv.put(InBoundCallBlocking.FIELD_INFORMATION, information);
		cv.put(InBoundCallBlocking.FIELD_CREATEDDATE, createdDate);
		cv.put(InBoundCallBlocking.FIELD_UPDATEDATE, createdDate);
		
		try {
			db.beginTransaction();
			result = db.insert(InBoundCallBlocking.TABLE_NAME, null, cv);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG,"insertNewDeviceInfo()",e);
		}finally{
			db.endTransaction();
			triggerObservers();
		}
		
		return result;
	}
	
	public int deleteOutboundCallList(){
		System.out.println("deletOutboundCallList()");
		int result = 0;
		try {
			  result = db.delete(OutBoundCalls.TABLE_NAME, null, null);	
			  triggerObservers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public long insertNewOutboundCall(int priority, String callee, String information, String createdDate) {
		long result = -1;
		
		ContentValues cv = new ContentValues();
		cv.put(OutBoundCalls.FIELD_PRIORITY, priority);
		cv.put(OutBoundCalls.FIELD_CALLED, callee);
		cv.put(OutBoundCalls.FIELD_INFORMATION, information);
		cv.put(OutBoundCalls.FIELD_CREATEDDATE, createdDate);
		cv.put(OutBoundCalls.FIELD_UPDATEDATE, createdDate);
		
		try {
			db.beginTransaction();
			result = db.insert(OutBoundCalls.TABLE_NAME, null, cv);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG,"insertNewDeviceInfo()",e);
		}finally{
			db.endTransaction();
			triggerObservers();
		}
		
		return result;
	}
	
	// 2014.07.18 SmartBean_SHCHO : ADD FACEBOOK CHECK
	boolean Check_Facebook(String id, String pass, String idlist) {
		if(id!=null && id.length()>0) {
//        	if(id.equals(NgnConfigurationEntry.DEFAULT_IDENTITY_FACEBOOK_ID)) {
//        		return false;
//        	}
        }
        else {
        	return false;
        }
		
		if(pass!=null && pass.length()>0) {
//        	if(pass.equals(NgnConfigurationEntry.DEFAULT_IDENTITY_FACEBOOK_PASSWORD)) {
//        		return false;
//        	}
        }
        else {
        	return false;
        }
		
		if(idlist!=null && idlist.length()>0) {
//        	if(idlist.equals(NgnConfigurationEntry.DEFAULT_IDENTITY_FACEBOOK_ID_LIST)) {
//        		return false;
//        	}
        }
        else {
        	return false;
        }
		return true; 
	}
	
	private void triggerObservers(){
		setChanged();
		notifyObservers();
	}
	
	public long insert_MotionDetect_EventHistory(String devicenumber, String devicename, String createdate) {
		Log.d(TAG,"insert_MotionDetect_EventHistory()");
		long result = -1;
		
		ContentValues cv = new ContentValues();
		cv.put(MotionDetectEventHistory.FIELD_SOURCE_DEVICENUMBER, devicenumber);
		cv.put(MotionDetectEventHistory.FIELD_SOURCE_DEVICE_DISPLAYNAME,  devicename);
		cv.put(MotionDetectEventHistory.FIELD_CREATEDDATE, createdate);
		
		try {
			db.beginTransaction();
			result = db.insert(MotionDetectEventHistory.TABLE_NAME, null, cv);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG,"insert_MotionDetect_EventHistory()",e);
		} finally {
			db.endTransaction();
			triggerObservers();
		}
		
		return result;
	}
	
	public String getEventHistoryId() {
		String EventHistoryId = null;
		Cursor cursor = db.query(MotionDetectEventHistory.TABLE_NAME, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			EventHistoryId =	cursor.getString(cursor.getColumnIndex(MotionDetectEventHistory.FIELD_EVENTHISTORY_ID));
		}
		cursor.close();
		return EventHistoryId;
	}
	
	public Cursor get_all_MotionDetect_EventHistory(){
		Cursor cursor = null;
		cursor = db.query(MotionDetectEventHistory.TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	
	public Cursor get_all_RelaySensors_EventHistory(){
		Cursor cursor = null;
		cursor = db.query(RelaySensorsEventHistory.TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
	
	public long insert_RelaySensors_EventHitory(String sipNumber, String displayName, String textSummary, String createdate) {
		long result = -1;
		
		ContentValues cv = new ContentValues();
		cv.put(RelaySensorsEventHistory.FIELD_SOURCE_DEVICE_SIPNUMBER, sipNumber);
		cv.put(RelaySensorsEventHistory.FIELD_SOURCE_DISPLAYNAME,  displayName);
		cv.put(RelaySensorsEventHistory.FIELD_TEXT_SUMMARY, displayName);
		cv.put(RelaySensorsEventHistory.FIELD_CREATEDDATE, textSummary);
		
		try {
			db.beginTransaction();
			result = db.insert(RelaySensorsEventHistory.TABLE_NAME, null, cv);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG,"insert_RelaySensors_EventHitory()",e);
		} finally {
			db.endTransaction();
			triggerObservers();
		}
		
		return result;
	}
	
	public long insert_MotionDetect_EventHitoryImage(String filepath, String text, String createdate) {
		Log.d(TAG,"insert_MotionDetect_EventHitoryImage()");
		long result = -1;
		
		ContentValues cv = new ContentValues();
		cv.put(MotionDetectEventHistoryImages.FIELD_EVENTHISTORY_ID, getEventHistoryId());
		cv.put(MotionDetectEventHistoryImages.FIELD_IMAGE_PATH,  filepath);
		cv.put(MotionDetectEventHistoryImages.FIELD_TEXT_SUMMARY, text);
		cv.put(MotionDetectEventHistoryImages.FIELD_CREATEDDATE, createdate);
		
		try {
			db.beginTransaction();
			result = db.insert(MotionDetectEventHistoryImages.TABLE_NAME, null, cv);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG,"insert_MotionDetect_EventHitoryImage()",e);
		} finally {
			db.endTransaction();
			triggerObservers();
		}
		
		return result;
	}
	
	public Cursor get_all_MotionDetect_EventHistoryImages(){
		Cursor cursor = null;
		cursor = db.query(MotionDetectEventHistoryImages.TABLE_NAME, null, null, null, null, null, null);
		return cursor;
	}
}