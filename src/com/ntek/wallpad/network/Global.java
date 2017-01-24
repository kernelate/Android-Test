package com.ntek.wallpad.network;

import java.util.ArrayList;

import com.ntek.wallpad.Model.EventInquiryList;
import com.ntek.wallpad.Utils.NotificationEmailFacebookData;

public class Global {
	// 2014.07.18 SmartBean_SHCHO : CHANGE NAME > NUM(CLIENT'S NUMBER)
	private String name;
	private String client_impi;
	private String server_impi;
	private String pw;
	private String adress;
	private String proxyhost;
	private String callnumber;
	private String data;
	// private String email;
	private String gcm_url;
	private String registerid;
	private int confPort;
	private String eventServerProtocol;
	private String eventServerUrl;
	private int eventServerPort;
	private boolean flagEventRegistered;
	private int Server_Speaker_volume;
	private int Server_Call_volume;
	
	private String inboundBlockList;
	
    private ArrayList<NotificationEmailFacebookData> mNotificationChannelAccounts;
    private EventInquiryList mEventInquiryAccounts;
	
	public String getInboundBlockList() {
		return inboundBlockList;
	}

	public void setInboundBlockList(String inboundBlockList) {
		this.inboundBlockList = inboundBlockList;
	}
	
	private String outboundPriorityList;
	
	public String getOutboundPriorityList() {
		return outboundPriorityList;
	}

	public void setOutboundPriorityList(String outboundPriorityList) {
		this.outboundPriorityList = outboundPriorityList;
	}
	
	private int motion_sensitivity;

	// 2014.08.25 SmartBean_SHCHO : ADD FLAG FOR MOTION, ALARM A, ALARM B ON/OFF
	// FLAG
	private String motion_onoff = ""; // Motion onoff type
	private String alarm_a_onoff = ""; // Alarm A onoff flag
	private String alarm_b_onoff = ""; // Alarm B onoff flag
	private String door_lock_status = "";
	private String auto_lock_time = "";
	private String auto_lock_onoff = "";
	private String motionclient_onoff = ""; //motion client on or off
	
	private String doorcontrol_onoff = ""; // DoorControl onoff flag 2014.10.24 Added
	//--------SECURITY FUNCTIONS FLAG-------------------
	private String relay_sensor_nickname = "";
	private String signal1_transaction_value = "";
	private String signal1_printed_name = "";
	private String signal1_duration_value = "";
	private String signal1_transaction_notification_YN = "";
	private String signal1_duration_notification_yn = "";
	private String signal2_transaction_value = "";
	private String signal2_printed_name = "";
	private String signal2_duration_value = "";
	private String signal2_transaction_notification_YN = "";
	private String signal2_duration_notification_yn = "";
	//--------EVENT CLIENT INQUIRY DETAIL----------------
	private String client_inquiry_status = ""; 
	private String client_inquiry_gcmID = "";
	private String server_unique_device_id = "";
	private String client_os_type = "";
	
	private String motion_emailCheck;
	private String motion_GCMCheck;
	private String motion_FACEBOOKCheck;

	private String alarm_a_EmailCheck;
	private String alarm_a_GCMCheck;
	private String alarm_a_FACEBOOKCheck;

	private String alarm_b_EmailCheck;
	private String alarm_b_GCMCheck;
	private String alarm_b_FACEBOOKCheck;

	private String facebook_friend_name_list;
	private String facebook_friend_id_list;

	private String facebookID;

	private String facebookPassWord;

	public final static String TCP_CONNECT_CALLBACK = "com.smartbean.servertalk.soc.TCP_CONNECT_CALLBACK";
	public final static String TCP_SETUP_SIP_CALLBACK = "com.smartbean.servertalk.soc.TCP_SETUP_SIP_CALLBACK";
	public final static String TCP_GET_DOORTALK_DATA_CALLBACK = "com.smartbean.servertalk.soc.TCP_GET_DOORTALK_DATA_CALLBACK";
	
	public boolean isFlagEventRegistered() {
		return flagEventRegistered;
	}

	public void setFlagEventRegistered(boolean flagEventRegistered) {
		this.flagEventRegistered = flagEventRegistered;
	}
	
	public String getEventServerProtocol() {
		return eventServerProtocol;
	}

	public void setEventServerProtocol(String eventServerProtocol) {
		this.eventServerProtocol = eventServerProtocol;
	}

	public String getEventServerUrl() {
		return eventServerUrl;
	}

	public void setEventServerUrl(String eventServerUrl) {
		this.eventServerUrl = eventServerUrl;
	}
	
	public int getEventServerPort() {
		return eventServerPort;
	}

	public void setEventServerPort(int eventServerPort) {
		this.eventServerPort = eventServerPort;
	}
	public String getFaceBookID() {
		return facebookID;
	}

	public void setFaceBookID(String facebookID) {
		this.facebookID = facebookID;
	}

	public int getMotionSensitivity() {
		return motion_sensitivity;
	}

	public void setMotionSensitivity(int sensitivity) {
		this.motion_sensitivity = sensitivity;
	}

	public String getFaceBookPassWord() {
		return facebookPassWord;
	}

	public void setFaceBookPassWord(String facebookPassWord) {
		this.facebookPassWord = facebookPassWord;
	}

	public String getFaceBook_Friend_Name_List() {
		return facebook_friend_name_list;
	}

	public void setFaceBook_Friend_Name_List(String facebook_friend_name) {
		this.facebook_friend_name_list = facebook_friend_name;
	}

	public String getFaceBook_Friend_Id_List() {
		return facebook_friend_id_list;
	}

	public void setFaceBook_Friend_Id_List(String facebook_friend_id) {
		this.facebook_friend_id_list = facebook_friend_id;
	}

	// 2014.08.25 SmartBean_SHCHO : ADD METHOD FOR MOTION, ALARM A, ALARM B
	// ON/OFF
	public String get_Motion_ONOFF() {
		if (motion_onoff.equals("")) motion_onoff = "enabled"; 
		return motion_onoff;
	}

	public void set_Motion_ONOFF(String motion_onoff) {
		this.motion_onoff = motion_onoff;
	}

	public String get_Alarm_A_ONOFF() {
		return alarm_a_onoff;
	}

	public void set_Alarm_A_ONOFF(String alarm_a_onoff) {
		this.alarm_a_onoff = alarm_a_onoff;
	}

	public String get_Alarm_B_ONOFF() {
		return alarm_b_onoff;
	}

	public void set_Alarm_B_ONOFF(String alarm_b_onoff) {
		this.alarm_b_onoff = alarm_b_onoff;
	}

	public String getAlarm_A_GCMCheck() {
		return alarm_a_GCMCheck;
	}

	public void setAlarm_A_GCMCheck(String alarm_a_GCMCheck) {
		this.alarm_a_GCMCheck = alarm_a_GCMCheck;
	}

	public String getAlarm_A_FACEBOOKCheck() {
		return alarm_a_FACEBOOKCheck;
	}

	public void setAlarm_A_FACEBOOKCheck(String alarm_a_FACEBOOKCheck) {
		this.alarm_a_FACEBOOKCheck = alarm_a_FACEBOOKCheck;
	}

	public String getAlarm_A_EmailCheck() {
		return alarm_a_EmailCheck;
	}

	public void setAlarm_A_EmailCheck(String alarm_a_EmailCheck) {
		this.alarm_a_EmailCheck = alarm_a_EmailCheck;
	}

	public String getAlarm_B_GCMCheck() {
		return alarm_b_GCMCheck;
	}

	public void setAlarm_B_GCMCheck(String alarm_b_GCMCheck) {
		this.alarm_b_GCMCheck = alarm_b_GCMCheck;
	}

	public String getAlarm_B_FACEBOOKCheck() {
		return alarm_b_FACEBOOKCheck;
	}

	public void setAlarm_B_FACEBOOKCheck(String alarm_b_FACEBOOKCheck) {
		this.alarm_b_FACEBOOKCheck = alarm_b_FACEBOOKCheck;
	}

	public String getAlarm_B_EmailCheck() {
		return alarm_b_EmailCheck;
	}

	public void setAlarm_B_EmailCheck(String alarm_b_EmailCheck) {
		this.alarm_b_EmailCheck = alarm_b_EmailCheck;
	}

	public String get_Motion_FACEBOOKCheck() {
		return motion_FACEBOOKCheck;
	}

	public void set_Motion_FACEBOOKCheck(String motion_FACEBOOKCheck) {
		this.motion_FACEBOOKCheck = motion_FACEBOOKCheck;
	}

	public String get_Motion_GCMCheck() {
		return motion_GCMCheck;
	}

	public void set_Motion_GCMCheck(String motion_GCMCheck) {
		this.motion_GCMCheck = motion_GCMCheck;
	}

	public String get_Motion_EmailCheck() {
		return motion_emailCheck;
	}

	public void set_Motion_EmailCheck(String motion_emailCheck) {
		this.motion_emailCheck = motion_emailCheck;
	}

	public int getConfPort() {
		return confPort;
	}

	public void setConfPort(int confPort) {
		this.confPort = confPort;
	}

	// 2014.07.18 SmartBean_SHCHO : DELETE SET & GET EMAIL
	/*
	 * public String getEmail() { return email; } public void setEmail(String
	 * email) { this.email = email; }
	 */

	public String getGcmUrl() {
		return gcm_url;
	}

	public void setGcmUrl(String gcm_url) {
		this.gcm_url = gcm_url;
	}

	public String getRegisterId() {
		return registerid;
	}

	public void setRegisterId(String registerid) {
		this.registerid = registerid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String get_client_Impi() {
		return client_impi;
	}

	public void set_client_Impi(String client_impi) {
		this.client_impi = client_impi;
	}

	public String get_server_Impi() {
		return server_impi;
	}

	public void set_server_Impi(String server_impi) {
		this.server_impi = server_impi;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public String getProxyHost() {
		return proxyhost;
	}

	public void setProxyHost(String proxyhost) {
		this.proxyhost = proxyhost;
	}

	public String getCallNumber() {
		return callnumber;
	}

	public void setCallNumber(String callnumber) {
		this.callnumber = callnumber;
	}

	public String get_Doorcontrol_onoff() { //ADDED 2014.10.22 for Door Control
		if (this.doorcontrol_onoff.equals("")) doorcontrol_onoff = "enabled";
		return doorcontrol_onoff;
	}

	public void set_Doorcontrol_onoff(String doorcontrol_onoff) { //ADDED 2014.10.22 for Door Control
		this.doorcontrol_onoff = doorcontrol_onoff;
	}

	private static Global instance = null;

	public static synchronized Global getInstance() {
		if (null == instance) {
			instance = new Global();
		}
		return instance;
	}

	public String getMotionclient_onoff() {
		return motionclient_onoff;
	}

	public void setMotionclient_onoff(String motionclient_onoff) {
		this.motionclient_onoff = motionclient_onoff;
	}

	public String getClient_os_type() {
		return client_os_type;
	}
	
	public void setClient_os_type(String client_os_type) {
		this.client_os_type = client_os_type;
	}
	
	public String getClient_inquiry_status() {
		return client_inquiry_status;
	}

	public void setClient_inquiry_status(String client_inquiry_status) {
		this.client_inquiry_status = client_inquiry_status;
	}

	public String getClient_inquiry_gcmID() {
		return client_inquiry_gcmID;
	}

	public void setClient_inquiry_gcmID(String client_inquiry_gcmID) {
		this.client_inquiry_gcmID = client_inquiry_gcmID;
	}

	public String getServer_unique_device_id() {
		return server_unique_device_id;
	}

	public void setServer_unique_device_id(String server_unique_device_id) {
		this.server_unique_device_id = server_unique_device_id;
	}

	public String getSignal1_transaction_value() {
		if (this.signal1_transaction_value.equals("")) this.signal1_transaction_value = "1";
		return signal1_transaction_value;
	}

	public void setSignal1_transaction_value(String signal1_transaction_value) {
		this.signal1_transaction_value = signal1_transaction_value;
	}
	
	public String getSignal1_printed_name() {
		if (signal1_printed_name.equals("")) signal1_printed_name = "Locked";
		return signal1_printed_name;
	}
	
	public void setSignal1_printed_name(String signal1_printed_name) {
		this.signal1_printed_name = signal1_printed_name;
	}

	public String getSignal1_duration_value() {
		if (signal1_duration_value.equals("")) signal1_duration_value = "30";
		return signal1_duration_value;
	}

	public void setSignal1_duration_value(String signal1_duration_value) {
		this.signal1_duration_value = signal1_duration_value;
	}

	public String getSignal1_transaction_notification_YN() {
		if (signal1_duration_notification_yn.equals("")) signal1_duration_notification_yn = "enabled";
		return signal1_transaction_notification_YN;
	}

	public void setSignal1_transaction_notification_YN(
			String signal1_transaction_notification_YN) {
		this.signal1_transaction_notification_YN = signal1_transaction_notification_YN;
	}

	public String getSignal1_duration_notification_yn() {
		if (signal1_duration_notification_yn.equals("")) signal1_duration_notification_yn = "enabled";
		return signal1_duration_notification_yn;
	}

	public void setSignal1_duration_notification_yn(
			String signal1_duration_notification_yn) {
		this.signal1_duration_notification_yn = signal1_duration_notification_yn;
	}

	public String getSignal2_transaction_value() {
		if (signal2_transaction_value.equals("")) signal2_transaction_value = "0";
		return signal2_transaction_value;
	}

	public void setSignal2_transaction_value(String signal2_transaction_value) {
		this.signal2_transaction_value = signal2_transaction_value;
	}
	
	public String getSignal2_printed_name() {
		if (signal2_printed_name.equals("")) signal2_printed_name = "Unlocked";
		return signal2_printed_name;
	}
	
	public void setSignal2_printed_name(String signal2_printed_name) {
		this.signal2_printed_name = signal2_printed_name;
	}

	public String getSignal2_duration_value() {
		if (signal2_duration_value.equals("")) signal2_duration_value = "30";
		return signal2_duration_value;
	}

	public void setSignal2_duration_value(String signal2_duration_value) {
		this.signal2_duration_value = signal2_duration_value;
	}

	public String getSignal2_transaction_notification_YN() {
		if (signal2_transaction_notification_YN.equals(""))
			signal2_transaction_notification_YN = "enabled";
		return signal2_transaction_notification_YN;
	}

	public void setSignal2_transaction_notification_YN(
			String signal2_transaction_notification_YN) {
		this.signal2_transaction_notification_YN = signal2_transaction_notification_YN;
	}

	public String getSignal2_duration_notification_yn() {
		if (signal2_duration_notification_yn.equals("")) signal2_duration_notification_yn = "enabled";
		return signal2_duration_notification_yn;
	}

	public void setSignal2_duration_notification_yn(
			String signal2_duration_notification_yn) {
		this.signal2_duration_notification_yn = signal2_duration_notification_yn;
	}

	public String getRelay_sensor_nickname() {
		if (this.relay_sensor_nickname.equals("")) relay_sensor_nickname = "Door Control";
		return relay_sensor_nickname;
	}

	public void setRelay_sensor_nickname(String relay_sensor_nickname) {
		this.relay_sensor_nickname = relay_sensor_nickname;
	}

	public ArrayList<NotificationEmailFacebookData> getNotificationChannelAccounts() {
		return mNotificationChannelAccounts;
	}

	public void setNotificationChannelAccounts(ArrayList<NotificationEmailFacebookData> mNotificationChannelAccounts) {
		this.mNotificationChannelAccounts = mNotificationChannelAccounts;
	}

	public void setServer_Speaker_volume(int volume) {
		this.Server_Speaker_volume = volume;
	}
	
	public int getServer_Speaker_volume() {
		return Server_Speaker_volume;
	}
	
	public void setServer_Call_volume(int volume) {
		this.Server_Call_volume = volume;
	}
	
	public int getServer_Call_volume() {
		return Server_Call_volume;
	}
	
	public void set_Auto_lock_time(String auto_lock_time) { //ADDED 2014.10.22 for Door Control
		this.auto_lock_time = auto_lock_time;
	}
	
	public String get_Auto_lock_time() { //ADDED 2014.10.22 for Door Control
		return auto_lock_time;
	}
	
	public void set_Auto_lock_onoff(String auto_lock_onoff) { //ADDED 2014.10.22 for Door Control
		this.auto_lock_onoff = auto_lock_onoff;
	}
	
	public String get_Auto_lock_onoff() { //ADDED 2014.10.22 for Door Control
		return auto_lock_onoff;
	}
	
	public void set_Door_lock_status(String door_lock_status) { //ADDED 2014.10.22 for Door Control
		this.door_lock_status = door_lock_status;
	}
	
	public String get_Door_lock_status() { //ADDED 2014.10.22 for Door Control
		return door_lock_status;
	}
	
	public EventInquiryList getEventInquiryObservableList(){
		if (mEventInquiryAccounts == null) 
		{
			mEventInquiryAccounts = new EventInquiryList();
		}
		return mEventInquiryAccounts;
	}

	public void setEventInquiryAccounts(EventInquiryList mEventInquiryAccounts) {
		this.mEventInquiryAccounts = mEventInquiryAccounts;
	}
	
	private String macAddress;

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	private String platformVersion;

	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}
}