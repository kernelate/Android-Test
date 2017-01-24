package com.ntek.wallpad.network;

import java.util.ArrayList;
import java.util.List;

public class ServerData {
	public static final String UPD_SEARCH_SERVER_END_CALLBACK = "com.smartbean.servertalk.network.UPD_SEARCH_SERVER_END_CALLBACK";
	private String name;
	private String ip;
	private String platformVersion;
	
	public ServerData() {
		super();
	}
	
	public ServerData(String name, String ip) {
		super();
		this.name = name;
		this.ip = ip;
	}

	public ServerData(String name, String ip, String platformVersion) {
		super();
		this.name = name;
		this.ip = ip;
		this.platformVersion = platformVersion;
	}

	public String getPlatformVersion() {
		return platformVersion;
	}

	public void setPlatformVersion(String platformVersion) {
		this.platformVersion = platformVersion;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getIp() {
		return ip;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	private static List<ServerData> dataList = null;

	/**
	 * Create a new list of server data that includes device display name and ip address.
	 * Note: create a new list if and only if data is not yet created
	 */
	public static synchronized List<ServerData> getList() {
		if(null == dataList) {
			dataList = new ArrayList<ServerData>();
		}
		return dataList;
	}
}