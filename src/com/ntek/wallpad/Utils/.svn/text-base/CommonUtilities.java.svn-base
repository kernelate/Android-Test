package com.ntek.wallpad.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;

import org.doubango.ngn.services.INgnContactService;
import org.doubango.ngn.services.impl.NgnContactService;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Services.IScreenService;
import com.ntek.wallpad.Services.Impl.ScreenService;

public final class CommonUtilities {
	
	// give your server registration url here
	//public static final String SERVER_URL = "http://"+Engine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.IDENTITY_URL, NgnConfigurationEntry.DEFAULT_IDENTITY_URL)+"/gcm/register.php"; 
    //static final String SERVER_URL = "";//"http://"+NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.IDENTITY_GCM_URL, NgnConfigurationEntry.DEFAULT_IDENTITY_GCM_URL)+"/gcm/register.php"; 
	
	// 2014.07.18 SmartBean_SHCHO : SET DEFAULT SERVER URL
    public static final String DEFAULT_SERVER_URL = "http://ntekcam2.iptime.org/gcm/register.php";
    
    // Google project id
    public static final String SENDER_ID = "772474932686";
    //public static final String SENDER_ID = "10641546301"; // Change GCM ID

    public static final String name="dttalk";
    
    /**
     * Tag used on log messages.
     */
    static final String TAG = "AndroidHive GCM";

    public static final String DISPLAY_MESSAGE_ACTION =
            "com.androidhive.pushnotifications.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    public static IScreenService mScreenService;
    public static INgnContactService mContactService;
    private static String imageLink;
    
    public static final int port = 6789;
    public static final int soc_port = 5555;
    public static final int net_port = 6666;
    
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
    
    public static boolean higherVersion3dot1(){
		String version = Build.VERSION.RELEASE;
		Log.d("VERSION", "before version = " + version);
		Log.d("VERSION", "before version length = " + version.length());
		// ex 3.0.1 5?�쎌쥙�볩쭗���??�쎌쥙猷?�옙�용?�占?�늿�뺧?�醫롫짗?�쏙?�占?�쥙?�욑?��?�쐻�좑??
		if(version != null && version.length() >= 5){
			Log.d("VERSION", "version length = " + version.length());
			version = version.substring(0, 3);
			Log.d("VERSION", "version = " + version);
			float fversion = Float.parseFloat(version);
			Log.d("VERSION", "float version = " + fversion);
			if(fversion >= 3.1f){
				return true;
			}else{
				return false;
			}
		}else{
			if(version != null && version.length() >= 3){
				version = version.substring(0, version.length());
				float fversion = Float.parseFloat(version);
				Log.d("VERSION", "else float version = " + fversion);
				if(fversion >= 3.1f){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}
	}
    
    public static boolean checkIfEmpty(String word){
    	return word == null || word.toString().length() == 0 || word.contains("null") || word.equals(" ");
    }
    
    public static IScreenService getScreenService(){
		if(mScreenService == null){
			mScreenService = new ScreenService();
		}
		return mScreenService;
	}
    
    public static INgnContactService getContactService(){
		if(mContactService == null){
			mContactService = new NgnContactService();
		}
		return mContactService;
	}
    
	public static Comparator<Contacts> sortToNameAscend = new Comparator<Contacts>() {
		@Override
		public int compare(Contacts contact1, Contacts contact2) {
			final String contact1Name = contact1.getDisplayName();
			final String contact2Name = contact2.getDisplayName();
			return contact1Name.compareToIgnoreCase(contact2Name);
		}
	};
	
	public static int getImage(DeviceType type){
		switch (type) {
		case CLIENT_TALK:
			return R.drawable.ic_thumbnail_xlarge;
		case SERVER_TALK:
		default:
			return R.drawable.ic_doortalk_thumbnail;
		}
	}
	
	public static int getImage(int type){
		switch (type) {
		case 0:
			return R.drawable.wp_wallpad_user_icon_rev1;
		case 1:
		default:
			return R.drawable.wp_wallpad_device_icon_rev1;
		}
	}
	
	public static int parseInt(String number){
		int phoneNumber = 0;
		try {
			phoneNumber = Integer.parseInt(number);
		} catch (Exception e) {
			return 0;
		}
		return phoneNumber;
	}
	
	public static String getImageLink() {
		return imageLink;
	}

	public static void setImageLink(String imageLink) {
		CommonUtilities.imageLink = imageLink;
	}
	
	public static boolean hasActiveInternetConnection(Context context) {
		Log.d(TAG, "hasActiveInternetConnection(Context context)");
	    if (isNetworkAvailable(context)) {
	        try {
	            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
	            urlc.setRequestProperty("User-Agent", "Test");
	            urlc.setRequestProperty("Connection", "close");
	            urlc.setConnectTimeout(1500); 
	            urlc.connect();
	            return (urlc.getResponseCode() == 200);
	        } catch (IOException e) {
	            Log.e(TAG, "Error checking internet connection", e);
	        }
	    } else {
	        Log.d(TAG, "No network available!");
	    }
	    return false;
	}
	
	private static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null  && activeNetworkInfo.isConnectedOrConnecting();
	}
}
