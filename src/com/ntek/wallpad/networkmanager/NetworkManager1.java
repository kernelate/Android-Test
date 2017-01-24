package com.ntek.wallpad.networkmanager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;

import org.doubango.ngn.NgnApplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public enum NetworkManager1 {
	INSTANCE;
	
	private final ConnectivityManager mConnectivityManager;
	private final ArrayList<OnChangeListener> mListeners;
	
	private NetworkInfo mConnectedNetworkInfo;
	
	private long mDisconnectedTime;	
	
	public interface OnChangeListener {
		public void onConnected();
		public void onDisconnected();
	}
	
	private NetworkManager1() {
		final Context context = NgnApplication.getContext();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		context.registerReceiver(networkBroadcastReceiver, filter);
		
		mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		mListeners = new ArrayList<OnChangeListener>();
		
		final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			mConnectedNetworkInfo = networkInfo;
		}
		
		mDisconnectedTime = -1;
	}
	
	public void addOnChangeListener(OnChangeListener listener) {
		if (!mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	public void removeOnChangeListener(OnChangeListener listener) {
		mListeners.remove(listener);
	}	
	
	public boolean isNetworkConnected() {
		final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}
		return networkInfo.isConnected();
	}
	
		
	public boolean isWiFiConnected() {
		final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {		
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEthernetConnected() {
		final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {		
			return true;
		} else {
			return false;
		}
	}
	
	public String getCurrentNetworkInfoString() {
		final NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
		    return "No connected network";
		}
		return networkInfo.toString();
	}
	
	public String getIpAddress(boolean ipv6) {
		try {
	        for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements();) {
	            NetworkInterface networkInterface = networkInterfaces.nextElement();
	            for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements();) {
	                InetAddress inetAddress = addresses.nextElement();
	                if (inetAddress.isLoopbackAddress()) {
	                    continue;
	                }
	                if (ipv6 && inetAddress instanceof Inet6Address) {
	                    return inetAddress.getHostAddress();
	                } else if (ipv6 == false && inetAddress instanceof Inet4Address) {
	                    return inetAddress.getHostAddress();
	                }
	            }
	        }
	    } catch (SocketException e) {
	    	e.printStackTrace();
	    }
	    return null;
	}
	
    public String getLocalWifiMacAddress() {
        WifiManager wifiManager = (WifiManager) NgnApplication.getContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getMacAddress();        
    }
    public String getLocalEthernetMacAddress() {
        try {
            return loadFileAsString("/sys/class/net/eth0/address").toUpperCase(Locale.US).substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
    
    private void handleNetworkChange(final NetworkInfo networkInfo) {
		if (networkInfo == null) {
			return;
		}		
		final boolean sameType = mConnectedNetworkInfo != null && mConnectedNetworkInfo.getType() == networkInfo.getType();
		final boolean connected = networkInfo.isConnected();
		if (!sameType && connected) {
		    if (mDisconnectedTime > 0) {
    		    mDisconnectedTime = -1;
		    }
			for (OnChangeListener listener : mListeners) {
				listener.onConnected();
			}
			mConnectedNetworkInfo = networkInfo;
			
		} else if (sameType && !connected) {
		    mDisconnectedTime = System.currentTimeMillis();
			for (OnChangeListener listener : mListeners) {
				listener.onDisconnected();
			}
			mConnectedNetworkInfo = null;
		}
	}
    
	private BroadcastReceiver networkBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent == null) {
				return;
			}
			final String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				handleNetworkChange((NetworkInfo)intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO));
			}
		}
	};
	
} 