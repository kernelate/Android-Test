package com.ntek.wallpad.network;

import java.util.List;

public class NetworkGlobal {
	public static final String TCP_SETUP_NETWORK_CALLBACK = "com.smartbean.servertalk.network.SETUP_NETWORK_CALLBACK";
	private String ssid;
	private String pw;
	private String security;
	private String type;
	private String ipSettings;
	private String ip;
	private String netmask;
	private String gateway;
	private String dns1;
	private String dns2;
	private List<String> al;
	private boolean provisionMode;
	private String provisionDeviceKey;
	private String provisionType;
	private String provisionIpAddress;
	private String provisionPort;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getIpSettings() {
		return ipSettings;
	}
	public void setIpSettings(String ipSettings) {
		this.ipSettings = ipSettings;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public String getNetmask() {
		return netmask;
	}
	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}
	
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	
	public String getDns1() {
		return dns1;
	}
	public void setDns1(String dns1) {
		this.dns1 = dns1;
	}
	
	public String getDns2() {
		return dns2;
	}
	public void setDns2(String dns2) {
		this.dns2 = dns2;
	}

	public String getSsid()
	{
		return ssid;
	}
	public void setSsid(String ssid)
	{
		this.ssid = ssid;
	}

	public String getPw()
	{
		return pw;
	}
	public void setPw(String pw)
	{
		this.pw = pw;
	}

	public String getSecurity()
	{
		return security;
	}
	public void setSecurity(String security)
	{
		this.security = security;
	}

	public List<String> getList()
	{
		return al;
	}
	public void setList(List<String> mList)
	{
		this.al = mList;
	}

	private static NetworkGlobal instance = null;
	
	private NetworkGlobal() { }

	public static synchronized NetworkGlobal getInstance(){
		if(null == instance){
			instance = new NetworkGlobal();
		}
		return instance;
	}
	
	//Auto Provision
	public boolean getProvisionMode()
	{
		return provisionMode;
	}
	public void setProvisionMode(boolean provisionMode)
	{
		this.provisionMode = provisionMode;
	}
	
	public String getProvisionSerialKey()
	{
		return provisionDeviceKey;
	}
	public void setProvisionSerialKey(String provisionSerialKey)
	{
		this.provisionDeviceKey = provisionSerialKey; 
	}
	
	public String getProvisionType()
	{
		return provisionType;
	}
	public void setProvisionType(String provisionType)
	{
		this.provisionType = provisionType; 
	}
	
	public String getProvisionIpAddress()
	{
		return provisionIpAddress;
	}
	public void setProvisionIpAddress(String provisionIpAddress)
	{
		this.provisionIpAddress = provisionIpAddress; 
	}
	
	public String getProvisionPort()
	{
		return provisionPort;
	}
	public void setProvisionPort (String provisionPort)
	{
		this.provisionPort = provisionPort; 
	}
} 