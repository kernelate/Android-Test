package com.ntek.wallpad.Model;


import org.doubango.ngn.utils.NgnObservableObject;

import com.ntek.wallpad.Utils.CommonUtilities;


public class EventInquiryModel extends NgnObservableObject{
	private int eventID;
	private String gcmID = "";
	private String email = "";
	private String activeStatus = "";
	private String motion_detect_enable = "";
	private String relay_sensors_enable = "";
	private String client_os_type = "";
	private String created_date = "";
	private boolean isUpdated;
	private String motion_detect_status;
	private String relay_detect_status;
	private String status;
	private String os_type;
	
	public int getEventID() {
		return eventID;
	}

	public void setEventID(int eventID) {
		this.eventID = eventID;
	}
	

	public String getMotion_detect_status() {
		return motion_detect_status;
	}

	public void setMotion_detect_status(String motion_detect_status) {
		this.motion_detect_status = motion_detect_status;
	}

	public String getRelay_detect_status() {
		return relay_detect_status;
	}

	public void setRelay_detect_status(String relay_detect_status) {
		this.relay_detect_status = relay_detect_status;
	}
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

//	public EventInquiryModel() {
//		// TODO Auto-generated constructor stub
//	}
	
	public String getGcmID() {
		return gcmID;
	}

	public void setGcmID(String gcmID) {
		if(CommonUtilities.checkIfEmpty(gcmID)) return; 
		if (!this.gcmID.equals(gcmID)){
			this.gcmID = gcmID;
			isUpdated = true;
			setChangedAndNotifyObservers(this);
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if(CommonUtilities.checkIfEmpty(email)) return; 
		if (!this.email.equals(email))
		{
			this.email = email;
			isUpdated = true;
			setChangedAndNotifyObservers(this);
		}
	
	}

	public String getMotion_detect_enable() {
		return motion_detect_enable;
	}

	public void setMotion_detect_enable(String motion_detect_enable) {
		if(CommonUtilities.checkIfEmpty(motion_detect_enable)) return; 
		if (!this.motion_detect_enable.equals(motion_detect_enable))
		{
			this.motion_detect_enable = motion_detect_enable;
			isUpdated = true;
			setChangedAndNotifyObservers(this);
		}
	}

	public String getActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(String activeStatus) {
		if(CommonUtilities.checkIfEmpty(activeStatus)) return; 
		if (!this.activeStatus.equals(activeStatus))
		{
			this.activeStatus = activeStatus;
			isUpdated = true;
			setChangedAndNotifyObservers(this);
		}
	}

	public String getRelay_sensors_enable() {
		return relay_sensors_enable;
	}

	public void setRelay_sensors_enable(String relay_sensors_enable) {
		if(CommonUtilities.checkIfEmpty(relay_sensors_enable)) return; 
		if (!this.relay_sensors_enable.equals(relay_sensors_enable))
		{
			this.relay_sensors_enable = relay_sensors_enable;
			isUpdated = true;
			setChangedAndNotifyObservers(this);
		}
	}

	public String getClient_os_type() {
		return client_os_type;
	}

	public void setClient_os_type(String client_os_type) {
		if(CommonUtilities.checkIfEmpty(client_os_type)) return; 
		if (!this.client_os_type.equals(client_os_type))
		{
			this.client_os_type = client_os_type;
			isUpdated = true;
			setChangedAndNotifyObservers(this);
		}
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		if(CommonUtilities.checkIfEmpty(created_date)) return; 
		if (!this.created_date.equals(created_date))
		{
			this.created_date = created_date;
			isUpdated = true;
			setChangedAndNotifyObservers(this);
		}
	}

	public boolean isUpdated() {
		return isUpdated;
	}
	
	public String getOs_type() {
		return os_type;
	}
	
	public void setOs_type(String os_type) {
		this.os_type = os_type;
	}
}