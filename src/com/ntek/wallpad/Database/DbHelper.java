package com.ntek.wallpad.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
    // 2014.07.18 SmartBean_SHCHO : CHANGE DB FILENAME & TABLE NAME, ADD FIELDS
	private static final String DB_NAME = "doortalk.db";
    private static final int    DB_VER  = 1;
    
    //--------------------DoorTalk Table-----------------------------
    public static final String TABLE_DOORTALK		= "doortalk";
    
    public static final String FIELD_NUM			= "num";			// impi(Phone number)
    public static final String FIELD_EMAIL			= "email";			// user E-Mail
    public static final String FIELD_OSTYPE			= "ostype";			// android : a, ios : i, default : android
    public static final String FIELD_REGID			= "regid";			// android gcm id & ios token 
    public static final String FIELD_FACEBOOKID		= "facebookid";		// user facebook ID
    public static final String FIELD_FACEBOOKPASS	= "facebookpass";	// user facebook password
    public static final String FIELD_FACEBOOKFRIEND	= "facebookfriend";	// user facebook friend list
    
    //------------------Based Table--------------------------------- 2014.23.10 ADDED
    public abstract class BasedTable{
    	public static final String FIELD_UPDATEDATE 				= "update_date";
    	public static final String FIELD_CREATEDDATE 				= "created_date";
    }
    
    //----------------BaseTableBoundCall Table-------------2014.23.10 ADDED
    public abstract class BaseTableBoundCall extends BasedTable{
    	public static final String FIELD_CALLED   				   = "called";
    	public static final String FIELD_INFORMATION 			   = "information";
    }
    
    public abstract class BasedTableDetect extends BasedTable{
    	public static final String FIELD_DEVICE_UNIQUE_KEY         = "device_unique_key";
    	public static final String FIELD_NAME 			   		   = "name";
    	public static final String FIELD_NICKNAME                  = "nickname";
    	public static final String FIELD_ENABLE_YN 	               = "enable_YN";
    }
    
    //------------------Event Inquiry Table------------------------- 2014.23.10 ADDED
    public class EventInquiry extends BasedTable{
    	public static final String TABLE_NAME  			 			= "EventInquiry";
    	    
    	public static final String FIELD_EVENTINQUIRY_ID 	 		= "eventInquiry_id"; 
    	public static final String FIELD_CLIENTGCMID 		 		= "client_GCMID";
    	public static final String FIELD_CLIENTEMAIL 	 	 		= "client_email";
    	public static final String FIELD_STATUS 					= "activeStatus";
    	public static final String FIELD_MOTIONDETECT_STATUS 		= "motionDetect_enable_YN";
    	public static final String FIELD_RELAYSENSOR_STATUS		    = "relaySensors_enable_YN";
    	public static final String FIELD_CLIENT_OS_TYPE	 	 		= "client_os_type";
    }
   
    //----------------Motion Detect History Table-------------------2014.23.10 ADDED
    public class MotionDetectEventHistory extends BasedTable{
    	public static final String TABLE_NAME 	   			       = "MotionDetect_EventHistory";

    	public static final String FIELD_EVENTHISTORY_ID           = "eventHistory_id";
    	public static final String FIELD_EVENTSERVER_HISTORYID     = "eventServer_eventHistoryID";
    	public static final String FIELD_SOURCE_DEVICENUMBER  	   = "source_device_number";
    	public static final String FIELD_SOURCE_DEVICE_DISPLAYNAME = "source_device_displayName";
    	public static final String FIELD_EVENT_OCCURENCE_TIME      = "event_occurence_time";
    }
    
    //----------------Motion Detect History Image Table------------------2014.23.10 ADDED
    public class MotionDetectEventHistoryImages extends MotionDetectEventHistory{
    	public static final String TABLE_NAME                      = "MotionDetect_EventHistory_Images";
    
    	public static final String FIELD_FILENAME           	   = "file_name";
    	public static final String FIELD_HISTORYIMAGE_ID 		   = "historyImage_id";
    	public static final String FIELD_IMAGE_PATH 			   = "image_path";
    	public static final String FIELD_TEXT_SUMMARY 			   = "text_summary";
    }
    
    //----------------RelaySensors Event History Table-------------------2014.23.10 ADDED
    public class RelaySensorsEventHistory extends BasedTable{
    	public static final String TABLE_NAME 				   	   = "RelaySensors_EventHistory";
    	
    	public static final String FIELD_EVENTHISTORY_ID 		   = "eventHistory_id";
    	public static final String FIELD_SOURCE_DEVICE_SIPNUMBER   = "source_device_sipNumber";
    	public static final String FIELD_SOURCE_DISPLAYNAME 	   = "source_device_displayName";
    	public static final String FIELD_TEXT_SUMMARY 			   = "text_summary";
    }
    
    //----------------OutBoundCalls Table----------------------------------2014.23.10 ADDED
    public class OutBoundCalls extends BaseTableBoundCall{
    	public static final String TABLE_NAME 					   = "OutboundCalls";
    	
    	public static final String FIELD_OUTBOUND_ID 			   = "outbound_id";
    	public static final String FIELD_PRIORITY 				   = "priority";
    }
    
    //----------------InBoundCallBlocking Table---------------------------2014.23.10 ADDED
    public class InBoundCallBlocking extends BaseTableBoundCall{
    	public static final String TABLE_NAME 					   = "InboundCallBlocking";
    	
    	public static final String FIELD_INBOUND_ID 			   = "inbound_id";
    }
    
    //----------------DeviceInfo Table---------------------------2014.23.10 ADDED
    public class DeviceInfo extends BasedTable{
    	public static final String TABLE_NAME 					   = "DeviceInfo";
    	
    	public static final String FIELD_DISPLAYNAME 			   = "display_name";
    	public static final String FIELD_UNIQUE_KEY				   = "unique_key";
    	public static final String FIELD_VERSION_ID 			   = "version_id";
    }
    
    //----------------DeviceSip Table---------------------------2014.23.10 ADDED
    public class DeviceSip extends BasedTable{
    	public static final String TABLE_NAME 					   = "DeviceSip";

    	public static final String FIELD_CALLNUMBER 			   = "call_number";
    	public static final String FIELD_DISPLAY_NAME 			   = "display_name";
    	public static final String FIELD_SERVER_ADDRESS			   = "server_address";
    	public static final String FIELD_SERVER_PORT 			   = "server_port";
    	public static final String FIELD_ACTUAL_IP_ADDRESS		   = "actual_ip_address";
    }
    
    //------------------NotificationChannelAccounts Table--------------------2014.30.10 ADDED
    public class NotificationChannelAccounts extends BasedTable{
    	public static final String TABLE_NAME 					   = "NotificationChannelAccounts";
    	
    	public static final String FIELD_DEVICE_UNIQUE_KEY 		   = "device_unique_key";
    	public static final String FIELD_NOTIFICATION_ID 		   = "notification_account_id";
    	public static final String FIELD_CHANNEL_CODE 			   = "channel_code";
    	public static final String FIELD_CHANNEL_ACCOUNT 		   = "channel_account";
    	public static final String FIELD_MOTIONDETECT_ENABLE_YN    = "motionDetect_enable_YN";
    	public static final String FIELD_SENSORS_ENABLE_YN  	   = "sensors_enable_YN";
    }
    
    public class DeviceMotionDetect extends BasedTableDetect{
    	public static final String TABLE_NAME 					   = "Device_MotionDetect";

    	public static final String FIELD_SENSITIVITY 			   = "sensitivity";
    }
    
    public class DeviceRelaySensor extends BasedTableDetect{
    	public static final String TABLE_NAME 					  			   = "Device_RelaySensor";
    	
    	public static final String FIELD_SIGNAL1_TRANSACTION_VALUE             = "signal1_transaction_value";
    	public static final String FIELD_SIGNAL1_PRINTED_NAME                  = "signal1_printed_name";
    	public static final String FIELD_SIGNAL1_DURATION_VALUE         	   = "signal1_duration_value";
    	public static final String FIELD_SIGNAL1_TRANSACTION_NOTIFICATION_YN   = "signal1_transaction_noti_YN";
    	public static final String FIELD_SIGNAL1_DURATION_NOTIFICATION_YN 	   = "signal1_duration_noti_YN";
    	public static final String FIELD_SIGNAL2_TRANSACTION_VALUE             = "signal2_transcation_value";
    	public static final String FIELD_SIGNAL2_PRINTED_NAME                  = "signal2_printed_name";
    	public static final String FIELD_SIGNAL2_DURATION_VALUE          	   = "signal2_duration_value";
    	public static final String FIELD_SIGNAL2_TRANSACTION_NOTIFICATION_YN   = "signal2_transcation_notification_YN";
    	public static final String FIELD_SIGNAL2_DURATION_NOTFICATION_YN 	   = "signal2_duration_notification_YN";    	
    }
    
    public class DeviceRelaySensorHeader extends BasedTable{
    	public static final String TABLE_NAME 						= "NotificationChannel_HEADER";
    	
    	public static final String FIELD_CHANNEL_CODE 				= "channel_code";
    	public static final String FIELD_CHANNEL_NAME 				= "channel_name";
    }
    
    
    // Contacts table name
    private static final String TABLE_IMAGE = "images";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE_PATH = "name";
    public static final String IMAGE = "image";
    
    
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String mTableDoorTalksql = "";
        String mEventInquirysql = "";
        String mMotionDetectEventHistory = "";
        String mMotionDetectEventHitoryImage = "";
        String mRelaySensorEventHistory = "";	
        String mOutBoundCallssql = "";
        String mInboundCallsql = "";
        String mDeviceInfosql = "";
        String mDeviceSipsql = "";
        String mNotificationAccountsql = "";
        String mDeviceMotionDetect = "";
        String mDeviceRelaySensor = "";
        String mDeviceRelaySensorHeader = "";
 
        mTableDoorTalksql = String.format(
                "CREATE TABLE IF NOT EXISTS %s(%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
                TABLE_DOORTALK, FIELD_NUM, FIELD_EMAIL, FIELD_OSTYPE, FIELD_REGID, FIELD_FACEBOOKID, FIELD_FACEBOOKPASS, FIELD_FACEBOOKFRIEND);
        
        mEventInquirysql = String.format(
        		"CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
        		EventInquiry.TABLE_NAME, 
        		EventInquiry.FIELD_EVENTINQUIRY_ID,
        		EventInquiry.FIELD_CLIENTGCMID, 
        		EventInquiry.FIELD_CLIENTEMAIL,
        		EventInquiry.FIELD_STATUS, 
        		EventInquiry.FIELD_MOTIONDETECT_STATUS,
        		EventInquiry.FIELD_RELAYSENSOR_STATUS,
        		EventInquiry.FIELD_CLIENT_OS_TYPE,
        		EventInquiry.FIELD_UPDATEDATE, 
        		EventInquiry.FIELD_CREATEDDATE);
        
        mMotionDetectEventHistory = String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
        			MotionDetectEventHistory.TABLE_NAME,
        			MotionDetectEventHistory.FIELD_EVENT_OCCURENCE_TIME,
        			MotionDetectEventHistory.FIELD_SOURCE_DEVICENUMBER, 
        			MotionDetectEventHistory.FIELD_SOURCE_DEVICE_DISPLAYNAME,
         			MotionDetectEventHistory.FIELD_EVENTHISTORY_ID,
        			MotionDetectEventHistory.FIELD_CREATEDDATE);
        
        mMotionDetectEventHitoryImage = String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
        		MotionDetectEventHistoryImages.TABLE_NAME,
        		MotionDetectEventHistoryImages.FIELD_HISTORYIMAGE_ID,
        		MotionDetectEventHistoryImages.FIELD_EVENTHISTORY_ID,
        		MotionDetectEventHistoryImages.FIELD_FILENAME,
        		MotionDetectEventHistoryImages.FIELD_IMAGE_PATH,
        		MotionDetectEventHistoryImages.FIELD_TEXT_SUMMARY, 
        		MotionDetectEventHistoryImages.FIELD_CREATEDDATE);
        
        mRelaySensorEventHistory = String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);", 
        		RelaySensorsEventHistory.TABLE_NAME,
        		RelaySensorsEventHistory.FIELD_EVENTHISTORY_ID, 
        		RelaySensorsEventHistory.FIELD_SOURCE_DEVICE_SIPNUMBER,
        		RelaySensorsEventHistory.FIELD_SOURCE_DISPLAYNAME,
        		RelaySensorsEventHistory.FIELD_TEXT_SUMMARY, 
        		RelaySensorsEventHistory.FIELD_CREATEDDATE);
        
        mOutBoundCallssql = String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT);", 
        		OutBoundCalls.TABLE_NAME,
        		OutBoundCalls.FIELD_OUTBOUND_ID,
        		OutBoundCalls.FIELD_PRIORITY, 
        		OutBoundCalls.FIELD_CALLED, 
        		OutBoundCalls.FIELD_INFORMATION,
        		OutBoundCalls.FIELD_UPDATEDATE,
        		OutBoundCalls.FIELD_CREATEDDATE);
        
        mInboundCallsql = String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
        		InBoundCallBlocking.TABLE_NAME,
        		InBoundCallBlocking.FIELD_INBOUND_ID,
        		InBoundCallBlocking.FIELD_CALLED,
        		InBoundCallBlocking.FIELD_INFORMATION,
        		InBoundCallBlocking.FIELD_UPDATEDATE,
        		InBoundCallBlocking.FIELD_CREATEDDATE);
        
        mDeviceInfosql = String.format("CREATE TABLE IF NOT EXISTS %s(%s TEXT PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
        		DeviceInfo.TABLE_NAME, 
        		DeviceInfo.FIELD_UNIQUE_KEY, 
        		DeviceInfo.FIELD_VERSION_ID,
        		DeviceInfo.FIELD_DISPLAYNAME,
        		DeviceInfo.FIELD_UPDATEDATE, 
        		DeviceInfo.FIELD_CREATEDDATE);
        
        mDeviceSipsql = String.format("CREATE TABLE IF NOT EXISTS %s(%s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT);", 
        		DeviceSip.TABLE_NAME,
        		DeviceSip.FIELD_CALLNUMBER, 
        		DeviceSip.FIELD_DISPLAY_NAME, 
        		DeviceSip.FIELD_SERVER_ADDRESS,
        		DeviceSip.FIELD_SERVER_PORT, 
        		DeviceSip.FIELD_ACTUAL_IP_ADDRESS,
        		DeviceSip.FIELD_UPDATEDATE, 
        		DeviceSip.FIELD_CREATEDDATE);
        
        mNotificationAccountsql = String.format("CREATE TABLE IF NOT EXISTS %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER);",
        		NotificationChannelAccounts.TABLE_NAME, 
        		NotificationChannelAccounts.FIELD_NOTIFICATION_ID,
        		NotificationChannelAccounts.FIELD_CHANNEL_CODE, 
        		NotificationChannelAccounts.FIELD_CHANNEL_ACCOUNT,
        		NotificationChannelAccounts.FIELD_MOTIONDETECT_ENABLE_YN,
        		NotificationChannelAccounts.FIELD_SENSORS_ENABLE_YN,
        		NotificationChannelAccounts.FIELD_UPDATEDATE,
        		NotificationChannelAccounts.FIELD_CREATEDDATE);
        
        mDeviceMotionDetect = String.format("CREATE TABLE IF NOT EXISTS %s(%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER);",
        	    DeviceMotionDetect.TABLE_NAME,
        	    DeviceMotionDetect.FIELD_DEVICE_UNIQUE_KEY,
        	    DeviceMotionDetect.FIELD_NAME,
        	    DeviceMotionDetect.FIELD_NICKNAME,
        	    DeviceMotionDetect.FIELD_ENABLE_YN,
        	    DeviceMotionDetect.FIELD_SENSITIVITY,
        	    DeviceMotionDetect.FIELD_UPDATEDATE,
        	    DeviceMotionDetect.FIELD_CREATEDDATE);
        
        mDeviceRelaySensor = String.format("CREATE TABLE IF NOT EXISTS %s(%s TEXT, %s TEXT, %s TEXT, " +
        		" %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT,%s INTEGER, %s INTEGER)",
        	    DeviceRelaySensor.TABLE_NAME,
        	    DeviceRelaySensor.FIELD_DEVICE_UNIQUE_KEY,
        	    DeviceRelaySensor.FIELD_NAME,
        	    DeviceRelaySensor.FIELD_NICKNAME,
        	    DeviceRelaySensor.FIELD_ENABLE_YN,
        	    DeviceRelaySensor.FIELD_SIGNAL1_TRANSACTION_VALUE,
        	    DeviceRelaySensor.FIELD_SIGNAL1_PRINTED_NAME,
        	    DeviceRelaySensor.FIELD_SIGNAL1_DURATION_VALUE,
        	    DeviceRelaySensor.FIELD_SIGNAL1_DURATION_NOTIFICATION_YN,
        	    DeviceRelaySensor.FIELD_SIGNAL1_TRANSACTION_NOTIFICATION_YN,
        	    DeviceRelaySensor.FIELD_SIGNAL2_TRANSACTION_VALUE,
        	    DeviceRelaySensor.FIELD_SIGNAL2_PRINTED_NAME,
        	    DeviceRelaySensor.FIELD_SIGNAL2_DURATION_VALUE,
        	    DeviceRelaySensor.FIELD_SIGNAL2_TRANSACTION_NOTIFICATION_YN,
        	    DeviceRelaySensor.FIELD_SIGNAL2_DURATION_NOTFICATION_YN,
        	    DeviceRelaySensor.FIELD_UPDATEDATE,
        	    DeviceRelaySensor.FIELD_CREATEDDATE);
        
        mDeviceRelaySensorHeader = String.format("CREATE TABLE IF NOT EXISTS %s(%s TEXT, %s TEXT, %s TEXT, %s TEXT);", 
        		DeviceRelaySensorHeader.TABLE_NAME,
        		DeviceRelaySensorHeader.FIELD_CHANNEL_CODE,
        		DeviceRelaySensorHeader.FIELD_CHANNEL_NAME,
        		DeviceRelaySensorHeader.FIELD_UPDATEDATE,
        		DeviceRelaySensorHeader.FIELD_CREATEDDATE);
        
        db.execSQL(mTableDoorTalksql);
        db.execSQL(mEventInquirysql);
        db.execSQL(mMotionDetectEventHistory);
        db.execSQL(mMotionDetectEventHitoryImage);
        db.execSQL(mRelaySensorEventHistory);
        db.execSQL(mOutBoundCallssql);
        db.execSQL(mInboundCallsql);
        db.execSQL(mDeviceInfosql);
        db.execSQL(mDeviceSipsql);
        db.execSQL(mNotificationAccountsql);
        db.execSQL(mDeviceMotionDetect);
        db.execSQL(mDeviceRelaySensor);
        db.execSQL(mDeviceRelaySensorHeader);
        
        Log.e("SHCHO","DB onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_DOORTALK);
        db.execSQL("DROP TABLE IF EXISTS "+ EventInquiry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MotionDetectEventHistory.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MotionDetectEventHistoryImages.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ RelaySensorsEventHistory.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ InBoundCallBlocking.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ OutBoundCalls.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DeviceInfo.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DeviceSip.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ NotificationChannelAccounts.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DeviceMotionDetect.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DeviceRelaySensor.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DeviceRelaySensorHeader.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
        
        
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMINFO);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMERGENCY);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOOR);
        
        Log.e("SHCHO","DB onUpgrade");
        onCreate(db);
    }
    
//    public class Image{
//        String imagePath; //it is your absolute image file path
//    }
    
 // Adding new image
//    public void addImage(Image image) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_IMAGE_PATH, image.imagePath); // Image path
//
//        // Inserting Row
//        db.insert(TABLE_IMAGE, null, values);
//        db.close(); // Closing database connection
//    }


    // Getting single image
//    public Image getImage(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_IMAGE, new String[] { KEY_ID, KEY_IMAGE_PATH }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        Image image = new Image();
//        // return image
//        return image;
//    }
    
    
//    public void insertImage(byte[] imageBytes) {
//        ContentValues cv = new ContentValues();
//        cv.put(IMAGE, imageBytes);
//        mDb.insert(IMAGES_TABLE, null, cv);
//    }

}
