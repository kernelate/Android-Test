package com.ntek.wallpad.Utils;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable{
	
	private long id;
	private String filename;
	private int height;
	private int width;
	private int filesize;

	public Photo() {
		// default
	}

	/**
	 * Constructor for adding photo
	 */
	public Photo(String filename, int height, int width, int filesize) {
		this.filename = filename;
		this.height = height;
		this.width = width;
		this.filesize = filesize;
	}
	
	/**
	 * Constructor for editing photo
	 */
	public Photo(long id, String filename, int height, int width, int filesize) {
		this.id = id;
		this.filename = filename;
		this.height = height;
		this.width = width;
		this.filesize = filesize;
	}
	
	public Photo(Parcel dest){
		readFromParcel(dest);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getFilesize() {
		return filesize;
	}

	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	private void readFromParcel(Parcel dest){
		this.id = dest.readLong();
		this.filename = dest.readString();
		this.height = dest.readInt();
		this.width = dest.readInt();
		this.filesize = dest.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeString(this.filename);
		dest.writeInt(this.height);
		dest.writeInt(this.width);
		dest.writeInt(this.filesize);
	}
	
	public static final Parcelable.Creator<Photo> CREATOR = new Creator<Photo>(){

		@Override
		public Photo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Photo(source);
		}

		@Override
		public Photo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new Photo[size];
		}
		
	};
}
