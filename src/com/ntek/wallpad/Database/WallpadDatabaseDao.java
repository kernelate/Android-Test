package com.ntek.wallpad.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.doubango.ngn.model.NgnContact;
import org.doubango.ngn.services.INgnContactService;
import org.doubango.ngn.services.impl.NgnContactService;
import org.doubango.ngn.utils.NgnObservableList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.Contacts;
import com.ntek.wallpad.Utils.DeviceType;
import com.ntek.wallpad.Utils.OnChangeDataListener;
import com.ntek.wallpad.Utils.Photo;


public class WallpadDatabaseDao {
	private WallpadDatabaseHelper wallpadDbHelper;
	private SQLiteDatabase wallpadSqliteDb;
	private INgnContactService mContactService;
	
	private OnChangeDataListener onChange;
	
	public WallpadDatabaseDao(Context context) {
		wallpadDbHelper = new WallpadDatabaseHelper(context);
	}

	private class WallpadDatabaseHelper extends SQLiteOpenHelper {
		private static final String DATABASE_NAME = "Wallpad.db";
		private static final int DATABASE_VERSION = 1;

		public WallpadDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(ContactsTable.CREATE_DATABASE);
			db.execSQL(PhotoFilesTable.CREATE_DATABASE);
			db.execSQL(ClientEventInquiryTable.CREATE_DATABASE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(ContactsTable.DROP_DATABASE);
			db.execSQL(PhotoFilesTable.DROP_DATABASE);
			db.execSQL(ClientEventInquiryTable.DROP_DATABASE);
			onCreate(db);
		}	
	}
	
	private class ClientEventInquiryTable{
		public static final String TABLE_NAME = "wp_event_inquiry_table";
		
		public static final String FIELD_SERVER_DEVICE_UNIQUE_KEY = "dt_device_unique_key";
		public static final String FIELD_EVENT_INQUIRY_ID = "dt_event_inquiry_id";
		public static final String FIELD_REQUESTED_GCM_ID = "requested_gcm_id";
		public static final String FIELD_INQUIRY_STATUS = "inquiry_status";
		public static final String FIELD_UPDATEDATE = "update_date";
		public static final String FIELD_CREATEDATE = "created_date";
		public static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_NAME + "(" 
				+ FIELD_SERVER_DEVICE_UNIQUE_KEY + " TEXT PRIMARY KEY, " 
				+ FIELD_REQUESTED_GCM_ID + " TEXT NOT NULL, " 
				+ FIELD_INQUIRY_STATUS + " TEXT, " 
				+ FIELD_EVENT_INQUIRY_ID + " INTEGER, " 
				+ FIELD_UPDATEDATE + " TEXT, " 
				+ FIELD_CREATEDATE + " TEXT);"; 
		
		public static final String DROP_DATABASE = "DROP TABLE IF EXIST " + TABLE_NAME;
	}
	
	private class ContactsTable { //Contacts Table
		public static final String TABLE_NAME = "wp_contacts_table";
		public static final String CONTACT_ID = "_id"; // ID
		public static final String DISPLAY_NAME = "display_name"; // Name
		public static final String CONTACT_NUMBER = "phone_number"; // Number
		public static final String PHOTO_FILE_ID = "photo_file_id"; // int
		public static final String CUSTOM_RINGTONE = "custom_ringtone"; // text
		public static final String LAST_TIME_CONTACTED = "last_time_contacted"; // integer
		public static final String TYPE = "type"; // integer
		public static final String FAVORITE = "favorite"; // integer
		
		public static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_NAME  + "(" 
				+ CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ DISPLAY_NAME + " TEXT UNIQUE NOT NULL, "
				+ CONTACT_NUMBER + " INTEGER UNIQUE NOT NULL, " 
				+ PHOTO_FILE_ID + " INTEGER, "
				+ CUSTOM_RINGTONE + " TEXT, "
				+ LAST_TIME_CONTACTED + " TEXT, " 
				+ TYPE + " INTEGER NOT NULL, " 
				+ FAVORITE + " INTEGER DEFAULT 0); ";
		
		public static final String DROP_DATABASE = "DROP TABLE IF EXIST " + TABLE_NAME;
	}
	
	private class PhotoFilesTable { //Photo Table
		public static final String TABLE_NAME = "wp_photo_files_table";
		public static final String FILE_ID = "_id";
		public static final String FILE_NAME = "file_name";
		public static final String FILE_SIZE = "file_size";
		public static final String HEIGHT = "height";
		public static final String WIDTH = "width";

		public static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_NAME + "(" 
				+ FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ FILE_NAME + " TEXT NOT NULL, " 
				+ HEIGHT + " INTEGER NOT NULL, " 
				+ WIDTH + " INTEGER NOT NULL, " 
				+ FILE_SIZE + " INTEGER NOT NULL);";
		
		public static final String DROP_DATABASE = "DROP TABLE IF EXIST " + TABLE_NAME;
	}
	
	private final String[] contactsAllColl = 
		{
			ContactsTable.CONTACT_ID,
			ContactsTable.DISPLAY_NAME,
			ContactsTable.CONTACT_NUMBER,
			ContactsTable.PHOTO_FILE_ID,
			ContactsTable.CUSTOM_RINGTONE,
			ContactsTable.LAST_TIME_CONTACTED,
			ContactsTable.TYPE, 
			ContactsTable.FAVORITE
		};

	private final String[] photoAllColl = 
		{
			PhotoFilesTable.FILE_ID,
			PhotoFilesTable.FILE_NAME,
			PhotoFilesTable.FILE_SIZE,
			PhotoFilesTable.HEIGHT,
			PhotoFilesTable.WIDTH
		};
	

	public void Open() {
		wallpadSqliteDb = wallpadDbHelper.getWritableDatabase();
	}	

	public void Close() {
		if(wallpadSqliteDb!=null) {
			wallpadSqliteDb.close();
		}		
	}

	public ArrayList<Contacts> getAllContacts() {
		Cursor cursor = null;
		ArrayList<Contacts> deviceList = null;

		try {
			cursor = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, contactsAllColl, null, null, null, null, ContactsTable.DISPLAY_NAME);
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
			return null;
		}

		if (cursor != null) {
			deviceList = new ArrayList<Contacts>();
			if (cursor.moveToFirst()){
				while (!cursor.isAfterLast()) {
					final int id = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_ID));
					final String name = cursor.getString(cursor.getColumnIndex(ContactsTable.DISPLAY_NAME));
					final int number = cursor.getInt(cursor .getColumnIndex(ContactsTable.CONTACT_NUMBER));
					final int photoID = cursor.getInt(cursor.getColumnIndex(ContactsTable.PHOTO_FILE_ID));
					final String customTone = cursor.getString(cursor.getColumnIndex(ContactsTable.CUSTOM_RINGTONE));
					final String mLastTimeContacted = cursor.getString(cursor.getColumnIndex(ContactsTable.LAST_TIME_CONTACTED));
					final int mDoorTalkTypeID = cursor.getInt(cursor.getColumnIndex(ContactsTable.TYPE));
					final int mFavorite = cursor.getInt(cursor.getColumnIndex(ContactsTable.FAVORITE));
					
					Contacts newContact = new Contacts();
					newContact.setId(id);
					newContact.setDisplayName(name);
					newContact.setPhoneNumber(number);
					newContact.setPhotoFileId(photoID);
					newContact.setPhoto(getPhoto(photoID));  //get the photo info
					newContact.setCustomRingTone(customTone);
					newContact.setLastTimeContacted(mLastTimeContacted);
					newContact.setFavorite(mFavorite);
					newContact.setType(mDoorTalkTypeID);
					deviceList.add(newContact);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		
		mContactService = CommonUtilities.getContactService();
		mContactService.start();
		mContactService.load();
		mContactService.stop();

		final NgnObservableList<NgnContact> observableContacts = mContactService.getObservableContacts();
		final List<NgnContact> contactList  = observableContacts.getList();
		
		for (NgnContact ngnContact : contactList) {
			Contacts contact = new Contacts();
			contact.setNgnContact(ngnContact);                                                                                                                                                                                                                                                                                                
			deviceList.add(contact);
		}
		
		Collections.sort(deviceList, CommonUtilities.sortToNameAscend); //sort name ascending
		
		return deviceList;
	}
	
	public ArrayList<Contacts> getPhoneContacts() {
		ArrayList<Contacts> deviceList = new ArrayList<Contacts>();
		mContactService = CommonUtilities.getContactService();
		mContactService.start();
		mContactService.load();
		mContactService.stop();

		final NgnObservableList<NgnContact> observableContacts = mContactService.getObservableContacts();
		final List<NgnContact> contactList  = observableContacts.getList();
		
		for (NgnContact ngnContact : contactList) {
			Contacts contact = new Contacts();
			contact.setNgnContact(ngnContact);                                                                                                                                                                                                                                                                                                
			deviceList.add(contact);
		}
		
		Collections.sort(deviceList, CommonUtilities.sortToNameAscend); //sort name ascending
		
		return deviceList;
	}
	
	public ArrayList<Contacts> getDoorTalkandClientsContacts() {
		Cursor cursor = null;
		ArrayList<Contacts> deviceList = null;
		
		try {
			cursor = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, contactsAllColl, null, null, null, null, ContactsTable.DISPLAY_NAME);
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
			return null;
		}

		if (cursor != null) {
			deviceList = new ArrayList<Contacts>();
			if (cursor.moveToFirst()){
				while (!cursor.isAfterLast()) {
					final int id = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_ID));
					final String name = cursor.getString(cursor.getColumnIndex(ContactsTable.DISPLAY_NAME));
					final int number = cursor.getInt(cursor .getColumnIndex(ContactsTable.CONTACT_NUMBER));
					final int photoID = cursor.getInt(cursor.getColumnIndex(ContactsTable.PHOTO_FILE_ID));
					final String customTone = cursor.getString(cursor.getColumnIndex(ContactsTable.CUSTOM_RINGTONE));
					final String mLastTimeContacted = cursor.getString(cursor.getColumnIndex(ContactsTable.LAST_TIME_CONTACTED));
					final int mDoorTalkTypeID = cursor.getInt(cursor.getColumnIndex(ContactsTable.TYPE));
					final int mFavorite = cursor.getInt(cursor.getColumnIndex(ContactsTable.FAVORITE));
					
					Contacts newContact = new Contacts();
					newContact.setId(id);
					newContact.setDisplayName(name);
					newContact.setPhoneNumber(number);
					newContact.setPhotoFileId(photoID);
					newContact.setPhoto(getPhoto(photoID));  //get the photo info
					newContact.setCustomRingTone(customTone);
					newContact.setLastTimeContacted(mLastTimeContacted);
					newContact.setType(mDoorTalkTypeID);
					newContact.setFavorite(mFavorite);
					deviceList.add(newContact);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		return deviceList;
	}
	
	public ArrayList<Contacts> getDoorTalkandClientsContactsFavorites() {
		int favorite = 1;
		Cursor cursor = null;
		ArrayList<Contacts> deviceList = null;
		
		try {
			final String where = ContactsTable.FAVORITE + "=" + favorite; 
			cursor = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, contactsAllColl, where, null, null, null, ContactsTable.DISPLAY_NAME);
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
			return null;
		}

		if (cursor != null) {
			deviceList = new ArrayList<Contacts>();
			if (cursor.moveToFirst()){
				while (!cursor.isAfterLast()) {
					final int id = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_ID));
					final String name = cursor.getString(cursor.getColumnIndex(ContactsTable.DISPLAY_NAME));
					final int number = cursor.getInt(cursor .getColumnIndex(ContactsTable.CONTACT_NUMBER));
					final int photoID = cursor.getInt(cursor.getColumnIndex(ContactsTable.PHOTO_FILE_ID));
					final String customTone = cursor.getString(cursor.getColumnIndex(ContactsTable.CUSTOM_RINGTONE));
					final String mLastTimeContacted = cursor.getString(cursor.getColumnIndex(ContactsTable.LAST_TIME_CONTACTED));
					final int mDoorTalkTypeID = cursor.getInt(cursor.getColumnIndex(ContactsTable.TYPE));
					final int mFavorite = cursor.getInt(cursor.getColumnIndex(ContactsTable.FAVORITE));
					
					Contacts newContact = new Contacts();
					newContact.setId(id);
					newContact.setDisplayName(name);
					newContact.setPhoneNumber(number);
					newContact.setPhotoFileId(photoID);
					newContact.setPhoto(getPhoto(photoID));  //get the photo info
					newContact.setCustomRingTone(customTone);
					newContact.setLastTimeContacted(mLastTimeContacted);
					newContact.setType(mDoorTalkTypeID);
					newContact.setFavorite(mFavorite);
					deviceList.add(newContact);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		
		return deviceList;
	}
	
	public ArrayList<Contacts> getDoorTalkContacts() {
		int doorTypeID = 0;
		Cursor cursor = null;
		ArrayList<Contacts> deviceList = null;
		
		try {
			final String where = ContactsTable.TYPE + "=" + doorTypeID; 
			cursor = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, contactsAllColl, where, null, null, null, ContactsTable.DISPLAY_NAME);
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
			return null;
		}

		if (cursor != null) {
			deviceList = new ArrayList<Contacts>();
			if (cursor.moveToFirst()){
				while (!cursor.isAfterLast()) {
					final int id = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_ID));
					final String name = cursor.getString(cursor.getColumnIndex(ContactsTable.DISPLAY_NAME));
					final int number = cursor.getInt(cursor .getColumnIndex(ContactsTable.CONTACT_NUMBER));
					final int photoID = cursor.getInt(cursor.getColumnIndex(ContactsTable.PHOTO_FILE_ID));
					final String customTone = cursor.getString(cursor.getColumnIndex(ContactsTable.CUSTOM_RINGTONE));
					final String mLastTimeContacted = cursor.getString(cursor.getColumnIndex(ContactsTable.LAST_TIME_CONTACTED));
					final int mDoorTalkTypeID = cursor.getInt(cursor.getColumnIndex(ContactsTable.TYPE));
					
					Contacts newContact = new Contacts();
					newContact.setId(id);
					newContact.setDisplayName(name);
					newContact.setPhoneNumber(number);
					newContact.setPhotoFileId(photoID);
					newContact.setPhoto(getPhoto(photoID));  //get the photo info
					newContact.setCustomRingTone(customTone);
					newContact.setLastTimeContacted(mLastTimeContacted);
					newContact.setType(mDoorTalkTypeID);
					deviceList.add(newContact);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		
		return deviceList;
	}
	
	public long addNewContact(Contacts contact) {
		ContentValues cv = new ContentValues();
		long rowId = -1;
		cv.put(ContactsTable.DISPLAY_NAME, contact.getDisplayName());
		cv.put(ContactsTable.CONTACT_NUMBER, contact.getPhoneNumber());
		cv.put(ContactsTable.PHOTO_FILE_ID, contact.getPhotoFileId());
		cv.put(ContactsTable.CUSTOM_RINGTONE, contact.getCustomRingTone());
		cv.put(ContactsTable.LAST_TIME_CONTACTED, contact.getLastTimeContacted());
		cv.put(ContactsTable.TYPE, contact.getType());
		cv.put(ContactsTable.FAVORITE, contact.getFavorite());
		
		wallpadSqliteDb.beginTransaction();
		try {
			rowId = wallpadSqliteDb.insert(ContactsTable.TABLE_NAME, null, cv);
			wallpadSqliteDb.setTransactionSuccessful();
		}  catch (SQLiteConstraintException e) {
			e.printStackTrace(); // TODO remove
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			wallpadSqliteDb.endTransaction();
		}
		
		if (rowId > 0) {
			if (onChange != null) onChange.onSync();
		}
		
		return rowId;
	}
	
	public boolean editContact(Contacts contact) {
		ContentValues cv = new ContentValues();
		int affectedRows = 0;
		cv.put(ContactsTable.DISPLAY_NAME, contact.getDisplayName());
		cv.put(ContactsTable.CONTACT_NUMBER, contact.getPhoneNumber());
		cv.put(ContactsTable.PHOTO_FILE_ID, contact.getPhotoFileId());
		cv.put(ContactsTable.CUSTOM_RINGTONE, contact.getCustomRingTone());
		cv.put(ContactsTable.LAST_TIME_CONTACTED, contact.getLastTimeContacted());
		cv.put(ContactsTable.TYPE, contact.getType());
		cv.put(ContactsTable.FAVORITE, contact.getFavorite());
		
		wallpadSqliteDb.beginTransaction();
		try {
			final String where = ContactsTable.CONTACT_ID + "=" + contact.getId();
			affectedRows = wallpadSqliteDb.update(ContactsTable.TABLE_NAME, cv, where, null);
			wallpadSqliteDb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
		} finally {
			wallpadSqliteDb.endTransaction();
		}
		
		if(affectedRows > 0) {
			if(onChange != null) onChange.onSync();
			return true;
		}
		
		return false;
	}
	
	public boolean isContactFavorite(int id) {
		boolean isFavorite = false;
		Cursor cursor = null;
		try {
			final String where = ContactsTable.CONTACT_ID + "=\"" + id + "\"";
			cursor  = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, new String[] { ContactsTable.FAVORITE }, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
		if (cursor != null){
			if (cursor.moveToFirst()){
				final int favorite = cursor.getInt(cursor.getColumnIndex(ContactsTable.FAVORITE));
				isFavorite = favorite == 1 ? true : false;
			}
			cursor.close();
		}
		
		return isFavorite;
	}
	
	public boolean toggleContactFavorite(int id) {
		int favorite = isContactFavorite(id) == true ? 1 : 0;
		
		ContentValues cv = new ContentValues(); 
		long affectedRows = -1;
		final String where = ContactsTable.CONTACT_ID + "=" + id;
		cv.put(ContactsTable.FAVORITE, favorite);
		wallpadSqliteDb.beginTransaction();
		try {
			affectedRows =  wallpadSqliteDb.update(ContactsTable.TABLE_NAME, cv, where, null);
			wallpadSqliteDb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wallpadSqliteDb.endTransaction();
		}
		
		if(affectedRows > 0) {
			if (onChange != null) onChange.onSync();
			return true;
		}
		
		return false;
	}
	
	public Contacts getContactByName(String contactName){
		Contacts newContact = null;
		Cursor cursor = null;
		try {
			final String where = ContactsTable.DISPLAY_NAME + "=\"" + contactName + "\"";
			cursor  = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, contactsAllColl, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (cursor != null){
			if (cursor.moveToFirst()){
				final int contact_id = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_ID));
				final String name = cursor.getString(cursor.getColumnIndex(ContactsTable.DISPLAY_NAME));
				final int number = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_NUMBER));
				final int photoID = cursor.getInt(cursor.getColumnIndex(ContactsTable.PHOTO_FILE_ID));
				final String customTone = cursor.getString(cursor.getColumnIndex(ContactsTable.CUSTOM_RINGTONE));
				final String mLastTimeContacted = cursor.getString(cursor.getColumnIndex(ContactsTable.LAST_TIME_CONTACTED));
				final int mDoorTalkTypeID = cursor.getInt(cursor.getColumnIndex(ContactsTable.TYPE));
				
				newContact = new Contacts();
				newContact.setId(contact_id);
				newContact.setDisplayName(name);
				newContact.setPhoneNumber(number);
				newContact.setPhotoFileId(photoID);
				newContact.setPhoto(getPhoto(photoID));  //get the photo info
				newContact.setCustomRingTone(customTone);
				newContact.setLastTimeContacted(mLastTimeContacted);
				newContact.setType(mDoorTalkTypeID);
			}
			cursor.close();
		}
		
		return newContact;
	}
	
	public int getPhotoId(int contactId){
		Cursor cursor = null;
		int resultId = 0;
		try {
			final String where = ContactsTable.CONTACT_ID + "=" + contactId;
			cursor = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, new String[] {  ContactsTable.PHOTO_FILE_ID }, where, null, null, null, null);	
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
		}

		if (cursor != null){
			if (cursor.moveToFirst()){
				resultId = cursor.getInt(cursor.getColumnIndex(ContactsTable.PHOTO_FILE_ID));
			}
			cursor.close();
		}
		
		return resultId;
	}
	
	public boolean removeContact(int id){
		int affectedRows = -1;
		wallpadSqliteDb.beginTransaction();
		try {
			final String where = ContactsTable.CONTACT_ID + "=" + id;
			affectedRows = wallpadSqliteDb.delete(ContactsTable.TABLE_NAME, where, null);
			wallpadSqliteDb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wallpadSqliteDb.endTransaction();
		}
		
		if (affectedRows > 0) {
			if (onChange != null) onChange.onSync();
			return true;
		}
		
		return false;
	}
	
	public boolean isNumberExist(final String number){
	    boolean isExist = false;
		Cursor cursor = null;
		try {
			final String where = ContactsTable.CONTACT_NUMBER + "=" + number;
			cursor  = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, new String[] { ContactsTable.CONTACT_ID }, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace(); //TODO remove this
		}

		if (cursor != null) {
			if (cursor.moveToFirst()){ 
				isExist = true; //check if true
			}
			cursor.close();
		}
		
		return isExist;
	}
	
	public boolean isNameExist(String name){
		boolean isExist = false;
		Cursor cursor = null;
		try {
			final String where = ContactsTable.DISPLAY_NAME + "=\"" + name + "\"";
			cursor  = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, new String[] { ContactsTable.CONTACT_ID }, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace(); //TODO remove this
		}
		
		if (cursor != null) {
			if (cursor.moveToFirst()){ 
				isExist = true; //check if true
			}
			cursor.close();
		}
		
		return isExist;
	}
	
	
	public Contacts getContactById(int id){
		Contacts newContact = null;
		Cursor cursor = null;
		try {
			final String where = ContactsTable.CONTACT_ID + "=" + id;
			cursor  = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, contactsAllColl, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace(); //TODO remove this
		}
		
		if (cursor != null){
			if (cursor.moveToFirst()){
				final int contact_id = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_ID));
				final String name = cursor.getString(cursor.getColumnIndex(ContactsTable.DISPLAY_NAME));
				final int number = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_NUMBER));
				final int photoID = cursor.getInt(cursor.getColumnIndex(ContactsTable.PHOTO_FILE_ID));
				final String customTone = cursor.getString(cursor.getColumnIndex(ContactsTable.CUSTOM_RINGTONE));
				final String mLastTimeContacted = cursor.getString(cursor.getColumnIndex(ContactsTable.LAST_TIME_CONTACTED));
				final int mDoorTalkTypeID = cursor.getInt(cursor.getColumnIndex(ContactsTable.TYPE));
				
				newContact = new Contacts();
				newContact.setId(contact_id);
				newContact.setDisplayName(name);
				newContact.setPhoneNumber(number);
				newContact.setPhotoFileId(photoID);
				newContact.setPhoto(getPhoto(photoID));  //get the photo info
				newContact.setCustomRingTone(customTone);
				newContact.setLastTimeContacted(mLastTimeContacted);
				newContact.setType(mDoorTalkTypeID);
			}
			cursor.close();
		}
		
		return newContact;
	}
	
	public Contacts getContactByNumber(int phoneNumber){
		Contacts newContact = null;
		Cursor cursor = null;
		try {
			final String where = ContactsTable.CONTACT_NUMBER + "=" + phoneNumber;
			cursor  = wallpadSqliteDb.query(ContactsTable.TABLE_NAME, contactsAllColl, where, 
					null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace(); //TODO remove this
		}
		
		if (cursor != null){
			if (cursor.moveToFirst()){
				final int contact_id = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_ID));
				final String name = cursor.getString(cursor.getColumnIndex(ContactsTable.DISPLAY_NAME));
				final int number = cursor.getInt(cursor.getColumnIndex(ContactsTable.CONTACT_NUMBER));
				final int photoId = cursor.getInt(cursor.getColumnIndex(ContactsTable.PHOTO_FILE_ID));
				final String customTone = cursor.getString(cursor.getColumnIndex(ContactsTable.CUSTOM_RINGTONE));
				final String lastTimeContacted = cursor.getString(cursor.getColumnIndex(ContactsTable.LAST_TIME_CONTACTED));
				final int type = cursor.getInt(cursor.getColumnIndex(ContactsTable.TYPE));
				
				newContact = new Contacts();
				newContact.setId(contact_id);
				newContact.setDisplayName(name);
				newContact.setPhoneNumber(number);
				newContact.setPhotoFileId(photoId);
				newContact.setPhoto(getPhoto(photoId));  //get the photo info
				newContact.setCustomRingTone(customTone);
				newContact.setLastTimeContacted(lastTimeContacted);
				newContact.setType(type);
			}
			cursor.close();
		}
		
		return newContact;
	}

	/**
	 * Get Photo by photo id
	 * 
	 * @param photoID id of photo on photo database table
	 * @return photo data
	 */
	public Photo getPhoto(long photoID){
		Photo photo = null;
		Cursor cursor = null;
		
		try {
			final String where = PhotoFilesTable.FILE_ID + "=" + photoID;
			cursor = wallpadSqliteDb.query(PhotoFilesTable.TABLE_NAME, photoAllColl, where, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
		}
		
		if (cursor != null){
			if (cursor.moveToFirst()){
				final int id = cursor.getInt(cursor.getColumnIndex(PhotoFilesTable.FILE_ID));
				final String filename = cursor.getString(cursor.getColumnIndex(PhotoFilesTable.FILE_NAME));
				final int height = cursor.getInt(cursor.getColumnIndex(PhotoFilesTable.HEIGHT));
				final int width = cursor.getInt(cursor.getColumnIndex(PhotoFilesTable.WIDTH));
				final int filesize = cursor.getInt(cursor.getColumnIndex(PhotoFilesTable.FILE_SIZE));
				photo = new Photo(id, filename, height, width, filesize);
			}
			cursor.close();
		}
		
		return photo;
	}
	/**
	 * Add new photo item in the database
	 * 
	 * @param photo
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long addNewPhoto(Photo photo){
		ContentValues cv = new ContentValues();
		long rowId = -1;
		wallpadSqliteDb.beginTransaction();
		try {
			cv.put(PhotoFilesTable.FILE_NAME, photo.getFilename());
			cv.put(PhotoFilesTable.FILE_SIZE, photo.getFilesize());
			cv.put(PhotoFilesTable.HEIGHT, photo.getHeight());
			cv.put(PhotoFilesTable.WIDTH, photo.getWidth());
			rowId = wallpadSqliteDb.insert(PhotoFilesTable.TABLE_NAME, null, cv);
			wallpadSqliteDb.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove
		}finally {
			wallpadSqliteDb.endTransaction();
		}
		
		if(rowId > 0) {
			if (onChange != null) onChange.onSync();
		}
		
		return rowId;
	}
	
	public boolean removePhoto(long id){
		int result = -1;
		wallpadSqliteDb.beginTransaction();
		try {
			final String where = PhotoFilesTable.FILE_ID + "=" + id;
			result = wallpadSqliteDb.delete(PhotoFilesTable.TABLE_NAME, where, null);
			wallpadSqliteDb.setTransactionSuccessful();
		}
		catch (Exception e) {
			e.printStackTrace(); // TODO remove
		}
		finally{
			wallpadSqliteDb.endTransaction();
		}
		
		if(result > 0) {
			if (onChange != null) onChange.onSync();
			return true;
		}

		return false;
	}
	
	public boolean editPhoto(Photo photo){
		ContentValues cv = new ContentValues();
		int result = -1;
		wallpadSqliteDb.beginTransaction();
		try {
			final String where = PhotoFilesTable.FILE_ID + "=" + photo.getId();
			cv.put(PhotoFilesTable.FILE_NAME, photo.getFilename());
			cv.put(PhotoFilesTable.FILE_SIZE, photo.getFilesize());
			cv.put(PhotoFilesTable.HEIGHT, photo.getHeight());
			cv.put(PhotoFilesTable.WIDTH, photo.getWidth());
			
			result = wallpadSqliteDb.update(PhotoFilesTable.TABLE_NAME, cv, where, null);
			wallpadSqliteDb.setTransactionSuccessful();
		}
		catch (Exception e) {
			e.printStackTrace(); // TODO remove
		}
		finally{
			wallpadSqliteDb.endTransaction();
		}
		
		if(result > 0) {
			if (onChange != null) onChange.onSync();
			return true;
		}
		
		return false;
	}
	
	public boolean insertEventInquiry(String server_unique_key, String gcmID, String date, String server_event_inquiry_id){
		ContentValues cv = new ContentValues();
		long result = -1;
		wallpadSqliteDb.beginTransaction();
		try {
			cv.put(ClientEventInquiryTable.FIELD_SERVER_DEVICE_UNIQUE_KEY, server_unique_key);
			cv.put(ClientEventInquiryTable.FIELD_REQUESTED_GCM_ID, gcmID);
			cv.put(ClientEventInquiryTable.FIELD_INQUIRY_STATUS, "inactive");
			cv.put(ClientEventInquiryTable.FIELD_EVENT_INQUIRY_ID, server_event_inquiry_id);
			cv.put(ClientEventInquiryTable.FIELD_UPDATEDATE, date);
			cv.put(ClientEventInquiryTable.FIELD_CREATEDATE, date);
			result = wallpadSqliteDb.insert(ClientEventInquiryTable.TABLE_NAME, null, cv);
			wallpadSqliteDb.setTransactionSuccessful();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			wallpadSqliteDb.endTransaction();
		}
		
		if(result > 1) {
			if (onChange != null) onChange.onSync();
			return true;
		}
		
		return false;
	}
	public boolean inquiryExist(String server_unique_key){
		Cursor cursor = null;
		boolean result = false;
		try {
			final String where = ClientEventInquiryTable.FIELD_SERVER_DEVICE_UNIQUE_KEY + "='" + server_unique_key + "'";
			cursor = wallpadSqliteDb.query(ClientEventInquiryTable.TABLE_NAME, null, where, null, null, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (cursor != null){
			result = cursor.moveToFirst();
			cursor.close();
		}
		return result;
	}
	
	public String getServerEventInquiryId(String server_unique_key){
		Cursor cursor = null;
		String event_inquiry_id = "";
		wallpadSqliteDb.beginTransaction();
		try {
			final String where = ClientEventInquiryTable.FIELD_SERVER_DEVICE_UNIQUE_KEY + "='" + server_unique_key + "'";
			cursor = wallpadSqliteDb.query(ClientEventInquiryTable.TABLE_NAME, null, where, null, null, null, null);
			wallpadSqliteDb.setTransactionSuccessful();
			if (cursor != null && cursor.moveToFirst()) {
				event_inquiry_id = cursor.getString(cursor.getColumnIndex(ClientEventInquiryTable.FIELD_EVENT_INQUIRY_ID));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wallpadSqliteDb.endTransaction();
		}
		cursor.close();
		return event_inquiry_id;
	}
	
	public String getGcmId() {
		Cursor cursor = null;
		String gcmId = "";
		wallpadSqliteDb.beginTransaction();
		try {
			cursor = wallpadSqliteDb.query(ClientEventInquiryTable.TABLE_NAME, null, null, null, null, null, null);
			wallpadSqliteDb.setTransactionSuccessful();
			if (cursor != null && cursor.moveToFirst()) {
				gcmId = cursor.getString(cursor.getColumnIndex(ClientEventInquiryTable.FIELD_REQUESTED_GCM_ID));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wallpadSqliteDb.endTransaction();
		}
		cursor.close();
		return gcmId;
	}
	
	public String getInquiryStatus(String server_unique_key, String gcmID) {
		Cursor cursor = null;
		String status = "";
		wallpadSqliteDb.beginTransaction();
		try {
			final String where = ClientEventInquiryTable.FIELD_SERVER_DEVICE_UNIQUE_KEY + "='" + server_unique_key + "' AND " 
					+ ClientEventInquiryTable.FIELD_REQUESTED_GCM_ID + "='" + gcmID + "'";
			cursor = wallpadSqliteDb.query(ClientEventInquiryTable.TABLE_NAME, null, where, null, null, null, null);
			wallpadSqliteDb.setTransactionSuccessful();
			if (cursor != null && cursor.moveToFirst()) {
				status = cursor.getString(cursor.getColumnIndex(ClientEventInquiryTable.FIELD_INQUIRY_STATUS));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wallpadSqliteDb.endTransaction();
		}
		cursor.close();
		return status;
	}
	
	public boolean inquiryExist(String server_unique_key, String gcmID){
		Cursor cursor = null;
		boolean result = false;
		try {
			final String where = ClientEventInquiryTable.FIELD_SERVER_DEVICE_UNIQUE_KEY + "='" + server_unique_key + "' AND " + ClientEventInquiryTable.FIELD_REQUESTED_GCM_ID + "='" + gcmID + "'";
			cursor = wallpadSqliteDb.query(ClientEventInquiryTable.TABLE_NAME, null, where, null, null, null, null);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		if (cursor != null){
			result = cursor.moveToFirst();
			cursor.close();
		}
		return result;
	}
	
	public boolean updateInquiry(String server_unique_key, String status, String date){
		ContentValues cv = new ContentValues();
		wallpadSqliteDb.beginTransaction();
		try {
			final String where = ClientEventInquiryTable.FIELD_SERVER_DEVICE_UNIQUE_KEY + "='" + server_unique_key + "'";
			cv.put(ClientEventInquiryTable.FIELD_INQUIRY_STATUS, status);
			cv.put(ClientEventInquiryTable.FIELD_UPDATEDATE, date);
			wallpadSqliteDb.update(ClientEventInquiryTable.TABLE_NAME, cv, where, null);
			wallpadSqliteDb.setTransactionSuccessful();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			wallpadSqliteDb.endTransaction();
		}
		if (onChange != null) onChange.onSync();
		return true;
	}
	
	public int deleteInquiry(String server_unique_key, String gcmID){
		int result = 0;
		wallpadSqliteDb.beginTransaction();
		try {
			final String where = ClientEventInquiryTable.FIELD_SERVER_DEVICE_UNIQUE_KEY + "='" + server_unique_key + "' AND "
					+ ClientEventInquiryTable.FIELD_REQUESTED_GCM_ID + "='" + gcmID + "'";
			result = wallpadSqliteDb.delete(ClientEventInquiryTable.TABLE_NAME, where, null);
			wallpadSqliteDb.setTransactionSuccessful();
		}
		catch (Exception e) {
			e.printStackTrace();
			result = 0;
		}finally{
			wallpadSqliteDb.endTransaction();
		}
		return result;
	}
	
	public INgnContactService getPhoneContactService(){
		if (mContactService == null) mContactService = new NgnContactService();
		return mContactService;
	}
	   
	public NgnObservableList<NgnContact> getAllObservablePhoneContacts(){
		if (mContactService == null) mContactService = new NgnContactService();
		final NgnObservableList<NgnContact> phoneContactList = mContactService.getObservableContacts();
		return phoneContactList;
	}

	public void setOnChangeListener(OnChangeDataListener onChange){
		this.onChange = onChange;
	}
}