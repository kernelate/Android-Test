package com.ntek.wallpad.Utils;

import org.doubango.ngn.model.NgnContact;

import android.os.Parcel;
import android.os.Parcelable;

public class Contacts implements Parcelable{

	private int mContactId;
	private String mDisplayName;
	private int mPhonenumber;
	private String mCustomRingTone;
	private String mLastTimeContacted;
	private int mType;
	private long mPhotoFileId;
	private Photo mPhoto; // photo
	private NgnContact ngnContact;
	private int mFavorite;

	public int getFavorite() {
		return mFavorite;
	}

	public void setFavorite(int isFavorite) {
		this.mFavorite = isFavorite;
	}

	public Contacts() {
		// default
	}
	
	/**
	 * Contacts complete params
	 */
	public Contacts(int mContactId, String mDisplayName, int mPhonenumber,
			long mPhotoFileId, Photo mPhoto, 
			String mCustomRingTone, String mLastTimeContacted, int mType,
			int mFavorite, NgnContact ngnContact) {
		super();
		this.mContactId = mContactId;
		this.mDisplayName = mDisplayName;
		this.mPhonenumber = mPhonenumber;
		this.mPhotoFileId = mPhotoFileId;
		this.mPhoto = mPhoto;
		this.mCustomRingTone = mCustomRingTone;
		this.mLastTimeContacted = mLastTimeContacted;
		this.mType = mType;
		this.mFavorite = mFavorite;
		this.ngnContact = ngnContact;
	}

	/**
	 * Contacts w/ Id for edit contact
	 */
	public Contacts(int mContactId, String mDisplayName, int mPhonenumber,
			long mPhotoFileId, String mCustomRingTone, String mLastTimeContacted, int mType,
			int mFavorite) {
		super();
		this.mContactId = mContactId;
		this.mDisplayName = mDisplayName;
		this.mPhonenumber = mPhonenumber;
		this.mCustomRingTone = mCustomRingTone;
		this.mLastTimeContacted = mLastTimeContacted;
		this.mType = mType;
		this.mPhotoFileId = mPhotoFileId;
		this.mFavorite = mFavorite;
	}

	/**
	 * Contacts w/o Id for new contact
	 * 
	 * Contacts(String, String, long, String, String, int, int) 
	 */
	public Contacts(String mDisplayName, int mPhonenumber,
			long mPhotoFileId, String mCustomRingTone, String mLastTimeContacted, int mType,
			int mFavorite) {
		super();
		this.mDisplayName = mDisplayName;
		this.mPhonenumber = mPhonenumber;
		this.mCustomRingTone = mCustomRingTone;
		this.mLastTimeContacted = mLastTimeContacted;
		this.mType = mType;
		this.mPhotoFileId = mPhotoFileId;
		this.mFavorite = mFavorite;
	}
	
	public Contacts(Parcel dest){
		readFromParcel(dest);
	}

	public int getPhoneNumber() {
		return mPhonenumber;
	}

	public void setPhoneNumber(int number) {
		this.mPhonenumber = number;
	}

	public String getDisplayName() {
		if (ngnContact != null) return ngnContact.getDisplayName();
		return mDisplayName;
	}

	public void setDisplayName(String name) {
		this.mDisplayName = name;
	}

	public int getId() {
		if (ngnContact != null) return ngnContact.getId();
		return mContactId;
	}

	public void setId(int id) {
		this.mContactId = id;
	}

	public String getCustomRingTone() {
		return mCustomRingTone;
	}

	public void setCustomRingTone(String customRingTone) {
		this.mCustomRingTone = customRingTone;
	}

	public String getLastTimeContacted() {
		return mLastTimeContacted;
	}

	public void setLastTimeContacted(String mLastTimeContacted) {
		this.mLastTimeContacted = mLastTimeContacted;
	}
	
	public DeviceType getDeviceType(){
		if (ngnContact != null) return DeviceType.CLIENT_TALK;
		return this.mType != 0 ? DeviceType.CLIENT_TALK : DeviceType.SERVER_TALK;
	}

	public int getType() {
		return mType;
	}
	
	public void setType(int type) {
		this.mType = type;
	}

	public Photo getPhoto() {
		return mPhoto;
	}

	public void setPhoto(Photo mPhoto) {
		if (mPhoto == null)
			return;
		this.mPhoto = mPhoto;
	}
	
	public long getPhotoFileId() {
		return mPhotoFileId;
	}

	public void setPhotoFileId(long mPhotoFileID) {
		this.mPhotoFileId = mPhotoFileID;
	}
	
	public NgnContact getNgnContact() {
		return ngnContact;
	}

	public void setNgnContact(NgnContact ngnContact) {
		this.ngnContact = ngnContact;
	}

	@Override
	public String toString() {
		return this.mDisplayName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.mContactId);
		dest.writeString(this.mDisplayName);
		dest.writeInt(this.mPhonenumber);
		dest.writeLong(this.mPhotoFileId);
		dest.writeParcelable(mPhoto, flags);
		dest.writeString(this.mCustomRingTone);
		dest.writeString(this.mLastTimeContacted);
		dest.writeInt(this.mType);
		dest.writeInt(this.mFavorite);
	}
	
	private void readFromParcel(Parcel dest){
		this.mContactId = dest.readInt();
		this.mDisplayName = dest.readString();
		this.mPhonenumber = dest.readInt();
		this.mPhotoFileId = dest.readLong();
		this.mPhoto = (Photo) dest.readParcelable(Photo.class.getClassLoader());
		this.mCustomRingTone = dest.readString();
		this.mLastTimeContacted = dest.readString();
		this.mType = dest.readInt();
		this.mFavorite = dest.readInt();
	}
	
	public static final Parcelable.Creator<Contacts> CREATOR = new Creator<Contacts>() {
		@Override
		public Contacts[] newArray(int size) {
			return new Contacts[size];
		}
		
		@Override
		public Contacts createFromParcel(Parcel source) {
			return new Contacts(source);
		}
	};
	
	
}
