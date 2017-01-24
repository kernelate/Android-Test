package com.ntek.wallpad.sip.registration;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnNetworkService;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class RegistrationApplication implements Comparable<RegistrationApplication>, Parcelable {
	//private final static String TAG = RegistrationApplication.class.getCanonicalName();
	
	private static INgnNetworkService mNetworkService;
	private static INgnConfigurationService mConfigurationService;
	private static NgnSipPrefrences mPreferences;
	
	private String client_impl;
	private String netwokr_realm;
	private String network_pcscf_port;
	private String local_ip;
	private String password;
	
	public RegistrationApplication() {
		mNetworkService = NgnEngine.getInstance().getNetworkService();
		mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		mPreferences = new NgnSipPrefrences();
		
		if (NgnEngine.getInstance().isStarted()) {
			load();	
		}
	}
	
	private RegistrationApplication(Parcel in) {
		readFromParcel(in);
	}

	private void load() {
		client_impl = getConfigurationValue(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI);
		netwokr_realm = getConfigurationValue(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM);
		network_pcscf_port = String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT));
		local_ip = getLocalIP();
		password = getConfigurationValue(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnConfigurationEntry.DEFAULT_IDENTITY_PASSWORD);
	}
	
	// Getting ip address has a dependency with network so should do this task on a independent thread, not on main-thread   
	private String getLocalIP() {
		boolean ipv6 = NgnStringUtils.equals(mPreferences.getIPVersion(), "ipv6", true);
		mPreferences.setLocalIP(mNetworkService.getLocalIP(ipv6));
		return mNetworkService.getLocalIP(ipv6);
	}
	
	private String getConfigurationValue(String identity, String defValue) {
		String value = mConfigurationService.getString(identity, defValue);
		//Log.d(TAG, "value : " + value + ", defValue : " + defValue);
		
		if (value != null && value.equals(defValue)) {
			value = "";
		}
		return value;
	}	

	public boolean validate() {
		return (!isValueNull(local_ip)
				&& !isValueNull(client_impl)
				&& !isValueNull(netwokr_realm)
				&& !isValueNull(network_pcscf_port));
	}
	
	private boolean isValueNull(String value) {
		return (value == null || value.equals(""));
	}	
	
	// All values are the exact same then return 0, otherwise -1
	@Override
	public int compareTo(RegistrationApplication application) {
		
		// lack of data means should stop process so intentionally specify this as otherwise case 
		if (!validate() || !application.validate()) {
			return -1;
		}
		
		return ((this.local_ip.equals(application.local_ip)
				&& this.client_impl.equals(application.client_impl)
				&& this.netwokr_realm.equals(application.netwokr_realm)
				&& this.network_pcscf_port.equals(application.network_pcscf_port)
				&& this.password.equals(application.password)) == true?0:-1);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("local_ip : ").append(local_ip).append("\n");
		sb.append("client_impl : ").append(client_impl).append("\n");
		sb.append("netwokr_realm : ").append(netwokr_realm).append("\n");
		sb.append("network_pcscf_port : ").append(network_pcscf_port).append("\n");
		// for debugging only, should be deleted when published
		//sb.append("password : ").append(password);
		
		return sb.toString();
	}
	
    public static final Parcelable.Creator<RegistrationApplication> CREATOR = new Parcelable.Creator<RegistrationApplication>() {
        public RegistrationApplication createFromParcel(Parcel in) {
            return new RegistrationApplication(in);
        }

        public RegistrationApplication[] newArray(int size) {
            return new RegistrationApplication[size];
        }
    }; 
	
	@Override
	public int describeContents() {
		return 0;
	}	
	
	private void readFromParcel(Parcel in) {
		client_impl = in.readString();
		netwokr_realm = in.readString();
		network_pcscf_port = in.readString();
		local_ip = in.readString();
		password = in.readString();
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(client_impl);
		dest.writeString(netwokr_realm);
		dest.writeString(network_pcscf_port);
		dest.writeString(local_ip);
		dest.writeString(password);
	}
}
