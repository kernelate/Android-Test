package com.ntek.wallpad.network;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Class to be used to connect to a certain network
 * passed by the client application
 */
public class NetworkManager {
	private static final String TAG = NetworkManager.class.getCanonicalName();
	private static NetworkManager mInstance = null;
	private WifiManager mWifiManager; 
	
	// Constants used for different security types
	public static final String AP_WPA2 = "WPA2";
	public static final String AP_WPA = "WPA";
	public static final String AP_WEP = "WEP";
	public static final String AP_OPEN = "Open";
	// For EAP Enterprise fields
	public static final String AP_WPA_EAP = "WPA-EAP";
	public static final String AP_IEEE8021X = "IEEE8021X";
	public static final String[] AP_SECURITY_MODES = { AP_WEP, AP_WPA, AP_WPA2, AP_WPA_EAP, AP_IEEE8021X };
	
	/**
	 * Create a NetworkManager object 
	 * 
	 * @param context class that will send up-calls for 
	 * application-level operations such as launching activities, 
	 * broadcasting and receiving intents, etc.
	 */
	private NetworkManager(Context context) {	
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	/**
	 * Enable wifi network with the given network ssid, password and security. Call
	 * this function if wifi is enable. @see WifiManager.WIFI_STATE_CHANGED_ACTION
	 * @param ssid  Network SSID
	 * @param password Network password
	 * @param security Network Security
	 */
	public void setupNetwork(String ssid, String password, String security) {
		Log.d(TAG, "ssid : " + String.format("\"%s\"", ssid));
		Log.d(TAG, "password : " + String.format("\"%s\"", password));
		Log.d(TAG, "security : " + security);
		
		WifiConfiguration mWifiConfig = new WifiConfiguration();
		
		mWifiConfig.SSID = String.format("\"%s\"", ssid);
		if(security.equals(AP_WEP)){
			mWifiConfig.wepKeys[0] = String.format("\"%s\"", password); //hex not quoted
			mWifiConfig.wepTxKeyIndex = 0;
            mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            mWifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		}
		else if(security.equals(AP_WPA) || security.equals(AP_WPA2)) {
			mWifiConfig.preSharedKey = String.format("\"%s\"", password);
		}
		else if(security.equals(AP_OPEN)) {
			mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		}
		else if(security.equals(AP_WPA_EAP)) {
			mWifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
		}
		
		int networkId = mWifiManager.addNetwork(mWifiConfig);
		Log.d(TAG, "addNetwork networkId: " + networkId);
		
		mWifiManager.disconnect();
		mWifiManager.enableNetwork(networkId, true);
		mWifiManager.reconnect();     

		mWifiManager.saveConfiguration();
	}
	
	/**
	 * Get the security type of a network
	 * @param scanResult  ScanResult of the network required to check the security
	 * @return scanResult security type 
	 * @see a
	 * NetworkManager.AP_WEP <br>
	 * NetworkManager.AP_WPA <br>
	 * NetworkManager.AP_WPA2  <br>
	 * NetworkManager.AP_WPA_EAP <br>
	 * NetworkManager.AP_IEEE8021X <br>
	 * NetworkManager.AP_OPEN <br>
	 */
	public String getScanResultSecurity(ScanResult scanResult) {
		if (scanResult != null) {
			final String cap = scanResult.capabilities;
			for (int i = AP_SECURITY_MODES.length - 1; i >= 0; i--) {
				if (cap.contains(AP_SECURITY_MODES[i])) {
					return AP_SECURITY_MODES[i];
				}
			}
		}
		return AP_OPEN;
	}
	
	/**
	 * Create an NetworkManager object to be used to connect to a certain network
	 * passed by the client application
	 * 
	 * @param context class that will send up-calls for 
	 * application-level operations such as launching activities, 
	 * broadcasting and receiving intents, etc.
	 * @return an instance of NetworkManager
	 */
	public static synchronized NetworkManager getInstance(Context context) {
		if(null == mInstance) {
			mInstance = new NetworkManager(context);
			Log.d(TAG, "new NetworkManager()");
		}
		return mInstance;
	}
}
