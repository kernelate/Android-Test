package com.ntek.wallpad.network;

public class LoginGlobal {
	public static final String TCP_LOGIN_CHANGE_CALLBACK = "com.smartbean.servertalk.soc.TCP_LOGIN_CHANGE_CALLBACK";
	public static final String TCP_LOGIN_CALLBACK = "com.smartbean.servertalk.soc.TCP_LOGIN_CALLBACK";
	
	private String loginID, loginPassword;

	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	private LoginGlobal() {
	}

	private static LoginGlobal instance = null;

	public static synchronized LoginGlobal getInstance() {
		if (null == instance) {
			instance = new LoginGlobal();
		}
		return instance;
	}
}
