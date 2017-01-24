package com.ntek.wallpad.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.ntek.wallpad.Database.WallpadDatabaseDao;


public class ContactManager {
	
	private static WallpadDatabaseDao cldb;
	private static ContactManager instance;
	private Context context;
	
	private ContactManager(Context context) 
	{ 
		cldb = new WallpadDatabaseDao(context);
		this.context = context;
	}

	public static ContactManager getInstance(Context context){
		if(instance == null){
			instance = new ContactManager(context);
		}
		return instance;
	}

	public long addContact(String name, int number, Photo photo, String ringTone, int type) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		cldb.Open();
		long photoId = cldb.addNewPhoto(photo);
		cldb.Close();
		
		cldb.Open();
		final long result = cldb.addNewContact(new Contacts(name, number, photoId, ringTone, dateFormat.format(date), type, 0));
		cldb.Close();
		return result;
	}

	public long addContact(Contacts contact, Photo photo) {
		cldb.Open();
		long photoId = cldb.addNewPhoto(photo);
		contact.setPhotoFileId(photoId);
		cldb.Close();
		
		cldb.Open();
		final long result = cldb.addNewContact(contact);
		cldb.Close();
		return result;
	}

	public boolean editContact(int id, String name, int number, int photoId, String ringTone, int type) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		cldb.Open();
		final boolean result = cldb.editContact(new Contacts(name, number, photoId, ringTone, dateFormat.format(date), type, 0));
		cldb.Close();
		return result;
	}

	public boolean editContact(Contacts contact, Photo photo) {
		boolean result = false;
		long rowId = 0;
		
		cldb.Open();
		if(contact.getPhotoFileId() > 0) {
			photo.setId(contact.getPhotoFileId());
			result = cldb.editPhoto(photo);
		} else {
			rowId = cldb.addNewPhoto(photo);
			if(rowId > 0) {
				contact.setPhotoFileId(rowId);
			}
		}
		cldb.Close();
		
		cldb.Open();
		result = cldb.editContact(contact);
		cldb.Close();
		return result;
	}

	public boolean toggleContactFavorite(int contactId) {
		cldb.Open();
		final boolean result = cldb.toggleContactFavorite(contactId);
		cldb.Close();
		return result;
	}

	public Contacts getContactByNumber(int phoneNumber){
		cldb.Open();
		final Contacts contact = cldb.getContactByNumber(phoneNumber);
		cldb.Close();
		return contact;
	}

	public Contacts getContactByName(String name){
		cldb.Open();
		final Contacts contact = cldb.getContactByName(name);
		cldb.Close();
		return contact;
	}
	
	public Contacts getContactById(int id){
		cldb.Open();
		final Contacts contact = cldb.getContactById(id);
		cldb.Close();
		return contact;
	}
	
	public boolean removeContact(int id){
		cldb.Open();
		long photoId = cldb.getPhotoId(id);
		cldb.Close();

		cldb.Open();
		final boolean result = cldb.removeContact(id);
		cldb.Close();
		
		cldb.Open();
		cldb.removePhoto(photoId);
		cldb.Close();
		
		return result;
	}

	public String getAndroidContactsPhoneNumber(String name) {
		String ret = null;
		String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "='" + name + "'";
		String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
		Cursor c = this.context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, null, null);
		if (c.moveToFirst()) {
			ret = c.getString(0);
		}
		c.close();
		if(ret==null)
			ret = "Unsaved";
		return ret;
	}

	public boolean isNumberExist(final String number){
		cldb.Open();
		final boolean result = cldb.isNumberExist(number);
		cldb.Close();
		return result;
	}

	public boolean isNameExist(final String name){
		cldb.Open();
		final boolean result = cldb.isNameExist(name);
		cldb.Close();
		return result;
	}

	public long addPhoto(String file_name, int height, int width, int filesize){
		cldb.Open();
		final long result = cldb.addNewPhoto(new Photo(file_name, height, width, filesize));
		cldb.Close();
		return result;
	}

	public long addPhoto(Photo photo){
		cldb.Open();
		final long result = cldb.addNewPhoto(photo);
		cldb.Close();
		return result;
	}

	public boolean editPhoto(int id, String filename, int height, int width, int filesize){
		cldb.Open();
		final boolean result = cldb.editPhoto(new Photo(id, filename, height, width, filesize));
		cldb.Close();
		return result;
	}

	public boolean editPhoto(Photo photo){
		cldb.Open();
		final boolean result = cldb.editPhoto(photo);
		cldb.Close();
		return result;
	}

	public Photo getPhoto(long photoId){
		cldb.Open();
		final Photo resultPhoto = cldb.getPhoto(photoId);
		cldb.Close();
		return resultPhoto;
	}

	public void setOnChangeDataListener(OnChangeDataListener onChange){
		if (cldb == null) return; cldb.setOnChangeListener(onChange);
	}

	/**
	 * Get all contacts, including phone contacts and custom contacts
	 */
	 public ArrayList<Contacts> getAllContacts(){
		 cldb.Open();
		 final ArrayList<Contacts> contactList = cldb.getAllContacts();
		 cldb.Close();
		 return contactList;	
	 }

	 /**
	  * Get all doortalk type contacts
	  */
	 public ArrayList<Contacts> getDoorTalkContacts(){
		 cldb.Open();
		 final ArrayList<Contacts> contactList = cldb.getDoorTalkContacts();
		 cldb.Close();
		 return contactList;	
	 }

	 public ArrayList<Contacts> getPhoneContacs(){
		 cldb.Open();
		 final ArrayList<Contacts> contactList = cldb.getPhoneContacts();
		 cldb.Close();
		 return contactList;	
	 }


	 public ArrayList<Contacts> getDoorTalkandClientContacts(){
		 cldb.Open();
		 final ArrayList<Contacts> contactList = cldb.getDoorTalkandClientsContacts();
		 cldb.Close();
		 return contactList;	
	 }

	 public ArrayList<Contacts> getDoorTalkandClientContactsFavorite(){
		 cldb.Open();
		 final ArrayList<Contacts> contactList = cldb.getDoorTalkandClientsContactsFavorites();
		 cldb.Close();
		 return contactList;	
	 }
}
