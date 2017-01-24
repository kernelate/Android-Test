package com.ntek.wallpad.Utils;

public class DoorTalkDevice {

	private int id;
	private String macAddress;
	private String platformVersion;
	private String ipAddress;
	private String callId;
	private String displayName;
	private String network;
	private String loginId;
	private String loginPassword;
	private boolean saved;

	public DoorTalkDevice() {
		super();
	}

	public DoorTalkDevice(String macAddress, String platformVersion, String ipAddress, String callId,
			String displayName, String network, String loginId, String loginPassword, boolean saved) {
		super();
		this.macAddress = macAddress;
		this.platformVersion = platformVersion;
		this.ipAddress = ipAddress;
		this.callId = callId;
		this.displayName = displayName;
		this.network = network;
		this.loginId = loginId;
		this.loginPassword = loginPassword;
		this.saved = saved;
	}

	public DoorTalkDevice(int id, String macAddress, String platformVersion, String ipAddress, String callId,
			String displayName, String network, String loginId, String loginPassword, boolean saved) {
		super();
		this.id = id;
		this.macAddress = macAddress;
		this.platformVersion = platformVersion;
		this.ipAddress = ipAddress;
		this.callId = callId;
		this.displayName = displayName;
		this.network = network;
		this.loginId = loginId;
		this.loginPassword = loginPassword;
		this.saved = saved;
	}

	@Override
	public String toString() {
		return displayName;
	}

	@Override
	public boolean equals(Object o) {
		return this.toString().equals(o.toString());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getCallId() {
		return callId;
	}

	public void setCallId(String callId) {
		this.callId = callId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public boolean isSaved() {
		return saved;
	}

	public void setSaved(boolean saved) {
		this.saved = saved;
	}
	
	
}
