package com.ntek.wallpad.Screens.Fragment;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnNetworkService;
import org.doubango.ngn.sip.NgnSipSession.ConnectionState;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.json.JSONException;
import org.json.JSONObject;

import com.ntek.wallpad.Engine;
import com.ntek.wallpad.R;
import com.ntek.wallpad.WallPad;
import com.ntek.wallpad.Services.IScreenService;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.networkmanager.NetworkManager1;
import com.smarttalk.sip.AutoProvision;
import com.smarttalk.sip.AutoProvisionDialog;
import com.smarttalk.sip.AutoProvisionXmlReader;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

public class FragmentCommonSetting extends Fragment implements OnClickListener{

	private final static String TAG = FragmentCommonSetting.class.getCanonicalName();

	private Button btnMySip;
	private Button btnEventAutomatic;
	private Button btnEventManual;
	private Button btnServerAutomatic;
	private Button btnServerManual;
	private Button btnCurrentTab;

	private TextView tvSipSetting;
	private TextView tvEventSetting;
	private TextView tvServerSetting;

	public static final int SERVER_CONFIG = 1;
	public static final int EVENT_SETTING = 2;
	public static int selectType;
	public static boolean isDoorDeviceApMode;


	private Button btnSipsetting;
	private Button btnMyprofile;
	private Button btnDoortalk;
	private Button btnOthers;
	private Button btnSecurity;

	private INgnNetworkService mNetworkService;
	private INgnConfigurationService mConfigurationService;
	private Context context;

	private DialogHolderSetupGuide setupguideHolder ;
	private Dialog dialog_setupGuide;
	private Dialog dialog_setupGuide_manualOnline;
	private DialogHolderManualOnline setupGuideMO_Holder ;
	private Dialog dialog_manual_setup;
	private DialogHolderManual manualHolder ;
	private Dialog progCircular;
	private DialogHolderProgressCircular progressCircular;
	private DialogHolderClientDevice clientdevicekeyHolder;
	private Dialog dialog_client;

	private ProgressDialog prgDialog;
	private Boolean isCtr = false;
	private Boolean isClient;
	private Boolean isAutoprovi = false;
	public  String productid = "";
	private final String USERS_TAG = "users";
	private final String SIP_SETUP_TAG = "sipsetup";
	private final String EXTENSIONS_TAG = "extensions";
	File path = Environment.getExternalStorageDirectory();;
	java.util.Date mDate;
	String type;
	private OnChangeFragmentListener mOnFragmentClick;

	View view;

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		Log.d(TAG, " onAttach ");
		try {
			mOnFragmentClick = (OnChangeFragmentListener) activity;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState)
	{

		Log.d(TAG, " onCreateView ");
		view = inflater.inflate(R.layout.fragment_common_setting, container, false);
		initializeUI();
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 

		if(mNetworkService == null)
		{
			mNetworkService = NgnEngine.getInstance().getNetworkService();
		}


		NgnEngine.getInstance().start();
		context = getActivity();
		return view;
	}


	private void initializeUI()
	{
		btnEventAutomatic = (Button) view.findViewById(R.id.fragment_common_setting_event_setting_button_automatic);
		btnMySip = (Button) view.findViewById(R.id.fragment_common_setting_sip_setting_button_my_sip);
		btnEventManual = (Button) view.findViewById(R.id.fragment_common_setting_event_setting_button_manual);
		btnServerAutomatic = (Button) view.findViewById(R.id.fragment_common_setting_server_configuration_button_automatic);
		btnServerManual = (Button) view.findViewById(R.id.fragment_common_setting_server_configuration_button_manual);
		tvSipSetting = (TextView) view.findViewById(R.id.common_settings_sipsetting);
		tvEventSetting = (TextView) view.findViewById(R.id.common_setting_eventsetting);
		tvServerSetting = (TextView) view.findViewById(R.id.common_setting_serversetting);


		btnSipsetting = (Button) view.findViewById(R.id.common_setting_sipsetting);
		btnMyprofile = (Button) view.findViewById(R.id.common_setting_myprofile);
		btnDoortalk = (Button) view.findViewById(R.id.common_setting_dtsetting);
		btnOthers = (Button) view.findViewById(R.id.common_setting_others);
		btnSecurity = (Button) view.findViewById(R.id.common_setting_security);
		
		btnSipsetting.setOnClickListener(this);
		btnMyprofile.setOnClickListener(this);
		btnDoortalk.setOnClickListener(this);
		btnOthers.setOnClickListener(this);
		btnSecurity.setOnClickListener(this);

	}

	private void setSelectedTab(Button button)
	{
		btnCurrentTab.setSelected(false);
		btnCurrentTab = button;
		btnCurrentTab.setSelected(true);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.common_setting_sipsetting :
			Log.d(TAG, " sip setting ");

			if (mOnFragmentClick != null)
			{
				isClient = false;
				if ( !isClient)
				{
					if((mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME)).equals("John Doe"))
					{
						Log.d(TAG, " isClient " + isClient + " ifif ");
						setupGuide();
					}
					else
					{
						Log.d(TAG, " isClient " + isClient + " ifelse ");
						setupGuide_manualOnline();
					}					
				}
				else
				{
					if((mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME)).equals("John Doe"))
					{
						Log.d(TAG, " isClient " + isClient + " elseif ");
						setupGuide();
					}
					else
					{
						Log.d(TAG, " isClient " + isClient + " elseelse ");
						setupGuide_manualOnline();
					}
				}
			}
			break;

		case R.id.common_setting_myprofile :

			if (mOnFragmentClick != null)
			{
				Log.d(TAG, " my profile shit");
				mOnFragmentClick.changeFragment(null, new FragmentSettingMyProfile(), false);
			}
			break;

		case R.id.common_setting_dtsetting :
			Log.d(TAG, " doortalk setting ");
			if (mOnFragmentClick != null)
			{
				mOnFragmentClick.changeFragment(null, new FragmentSettingDoortalk(), false);
			}
			break;
			
		case R.id.common_setting_security :
			if (mOnFragmentClick != null)
			{
				Log.d(TAG, " my security ");
//				mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);  FragmentSettingDvrEmr
				mOnFragmentClick.changeFragment(null, new FragmentSettingDvrEmr(), false);
			}
			break;

		case R.id.common_setting_others :
			Log.d(TAG, " others 111");
			if (mOnFragmentClick != null)
			{
				Log.d(TAG, " my profile ");
				mOnFragmentClick.changeFragment(null, new FragmentSettingOthers(), false);
			}
			break;
			
			
//		case R.id.fragment_common_setting_sip_setting_button_my_sip:
//			setSelectedTab(btnMySip);
//			if (mOnFragmentClick != null)
//			{
//				mOnFragmentClick.changeFragment(null, new FragmentSettingsSip(), false);
//			}
//			break;
//		case R.id.fragment_common_setting_event_setting_button_automatic:
//			setSelectedTab(btnEventAutomatic);
//			if (mOnFragmentClick != null)
//			{
//				selectType = EVENT_SETTING;
//				mOnFragmentClick.changeFragment(null, new FragmentSettingAuto(), false);
//			}
//			break;
//		case R.id.fragment_common_setting_event_setting_button_manual:
//			setSelectedTab(btnEventManual);
//			if (mOnFragmentClick != null)
//			{
//				selectType = EVENT_SETTING;
//				mOnFragmentClick.changeFragment(null, new FragmentSettingManual(), false);
//			}
//			break;
//		case R.id.fragment_common_setting_server_configuration_button_automatic:
//			setSelectedTab(btnServerAutomatic);
//			if (mOnFragmentClick != null)
//			{
//				selectType = SERVER_CONFIG;
//				mOnFragmentClick.changeFragment(null, new FragmentSettingAuto(), false);
//			}
//			break;
//		case R.id.fragment_common_setting_server_configuration_button_manual:
//			setSelectedTab(btnServerManual);
//			if (mOnFragmentClick != null)
//			{
//				selectType = SERVER_CONFIG;
//				mOnFragmentClick.changeFragment(null, new FragmentSettingManual(), false);
//			}
//			break;
		}
	}

	private class DialogHolderSetupGuide 
	{
		private Button btnNo;
		private Button btnYes;
	}

	private class DialogHolderManualOnline
	{
		private Button btnCancel;
		private Button btnOk;
		private RadioButton rbtnOnline;
		private RadioButton rbtnManual;
	}

	private class DialogHolderManual
	{
		private Button btnpbxsetup;
		private Button btnclientsetup;
		private Button btnback;
		private Button btnreset;
	}

	private class DialogHolderProgressCircular 
	{
		private ProgressBar preloader; 
	}
	
	private static class DialogHolderClientDevice
	{
		Button btnCancelClient;
		Button btnOKclient;
		static EditText etClient;
	}

	private void setupGuide()
	{
		dialog_setupGuide = new Dialog(context);
		dialog_setupGuide.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_setupGuide.setCancelable(true);
		dialog_setupGuide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		dialog_setupGuide.setContentView(R.layout.sipsetting_setupguide_pbxsetting);

		setupguideHolder = new DialogHolderSetupGuide();
		setupguideHolder.btnNo = (Button) dialog_setupGuide.findViewById(R.id.setupguide_pbxsetting_cancel);
		setupguideHolder.btnYes = (Button) dialog_setupGuide.findViewById(R.id.setupguide_pbxsetting_ok);
		

		setupguideHolder.btnNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				isClient = true;
				setupGuide_manualOnline();
				dialog_setupGuide.dismiss();
			}
		});

		setupguideHolder.btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				isClient = false;
				setupGuide_manualOnline();
				dialog_setupGuide.dismiss();
			}
		});

		dialog_setupGuide.show();
		dialog_setupGuide.setCancelable(false);
		dialog_setupGuide.setCanceledOnTouchOutside(false);
		hideInputMethod();
	}



	@Override
	public void onResume() {
		
		if(mNetworkService == null)
		{
			mNetworkService = NgnEngine.getInstance().getNetworkService();
		}
		String type = mConfigurationService.getString("provisionType", null);
		
		if (mConfigurationService.getString("PRODUCT_ID", null) != null && type == "pbx")
		{
			new updateIPaddress().execute();
		}
		else
		{
			Log.d(TAG, " type onresume2 " + type + " " + mConfigurationService.getString("PRODUCT_ID", null));
		}
		super.onResume();
	}

	private void setupGuide_manualOnline()
	{
//				Log.d(TAG, " asdadds 22" + productid + " isclient " + isClient);
		final String type = mConfigurationService.getString("provisionType", null);
		dialog_setupGuide_manualOnline = new Dialog(context);
		dialog_setupGuide_manualOnline.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_setupGuide_manualOnline.setCancelable(true);
		dialog_setupGuide_manualOnline.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		dialog_setupGuide_manualOnline.setContentView(R.layout.sipsetting_setupguide_online_manual);

		setupGuideMO_Holder = new DialogHolderManualOnline();
		setupGuideMO_Holder.btnCancel = (Button) dialog_setupGuide_manualOnline.findViewById(R.id.setupguide_cancel);
		setupGuideMO_Holder.btnOk = (Button) dialog_setupGuide_manualOnline.findViewById(R.id.setupguide_ok);
		setupGuideMO_Holder.rbtnOnline = (RadioButton) dialog_setupGuide_manualOnline.findViewById(R.id.setupguide_online);
		setupGuideMO_Holder.rbtnManual = (RadioButton) dialog_setupGuide_manualOnline.findViewById(R.id.setupguide_manual);
		setupGuideMO_Holder.btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if (setupGuideMO_Holder.rbtnOnline.isChecked() )
				{
					if (NetworkManager1.INSTANCE.isWiFiConnected() || NetworkManager1.INSTANCE.isNetworkConnected() || NetworkManager1.INSTANCE.isEthernetConnected())
					{
						Log.d(TAG, " 12121212112121212121212 ");
						if (type != null)
						{
							Log.d(TAG, " type 1" + type + " isclient " + isClient + " " + NgnEngine.getInstance().getSipService().isRegistered());
							if (type.contains("pbx"))
							{
								Log.d(TAG, " type 2" + type);
								isAutoprovi = false;
								new CheckInternetAccessTask().execute();

							}
							else if (type.contains("c"))
							{
								Log.d(TAG, " type 3" + type);
								client_setup_device_key();
							}
						}
						// newly start
						else
						{
							Log.d(TAG, " type 4" + type);

							if (isClient)
							{
								Log.d(TAG, " type 5" + type);
								client_setup_device_key();
							}
							else
							{
								Log.d(TAG, " type 6" + type);
								isAutoprovi = false;
								new CheckInternetAccessTask().execute();
							}
						}
					}
					else
					{
						AutoProvisionDialog.getInstance().showErrorMessage(context, "No Network Connection", null);
					}
					dialog_setupGuide_manualOnline.dismiss();
				}
				else
				{
					Log.d(TAG, " askldjhasjkdhdkaskdhakdhajkh ");
					mOnFragmentClick.changeFragment(null, new FragmentSettingManualSetup(), false);
					dialog_setupGuide_manualOnline.dismiss();
				}
			}
		});

		setupGuideMO_Holder.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				dialog_setupGuide_manualOnline.dismiss();
			}
		});
		dialog_setupGuide_manualOnline.show();
		dialog_setupGuide_manualOnline.setCancelable(false);
		dialog_setupGuide_manualOnline.setCanceledOnTouchOutside(false);

		//		dialog_setupGuide_manualOnline.setOnKeyListener(new Dialog.OnKeyListener() {
		//
		//			@Override
		//			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) 
		//			{
		//				if (keyCode == KeyEvent.KEYCODE_BACK) 
		//				{
		//					dialog.dismiss();
		//				}
		//				return true;			
		//			}
		//		});
	}


	class CheckInternetAccessTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			        	prgDialog = new ProgressDialog(context);
			        	prgDialog.setMessage("Checking if there is internet connection...");
			        	prgDialog.setCancelable(false);
			        	prgDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				HttpURLConnection urlc = (HttpURLConnection)
						(new URL("http://clients3.google.com/generate_204")
						.openConnection());
				urlc.setRequestProperty("User-Agent", "Android");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1000);
				urlc.connect();
				return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
			} catch (IOException e) {
				Log.e(TAG, "Error checking internet connection", e);
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {

			        	prgDialog.dismiss();
			if (result) 
			{
				        		prgDialog.dismiss();
				if (!isAutoprovi) 
				{
					new autoprovisionSetup().execute();
				}
				else 
				{
					//        			new switchCallId().execute();
				}
			} 
			else 
			{
				AutoProvisionDialog.getInstance().showErrorMessage(context, "No Internet Connection", null);
				//        		prgDialog.dismiss();
			}
		}
	}

	private class autoprovisionSetup extends AsyncTask<Void, Void, Boolean>  {
		String provisionType;
		String port;
		String localIp;
		String external_ip_address;
		int myProgressCount;

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();

			progCircular = new Dialog(context);
			progCircular.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progCircular.setCancelable(true);
			progCircular.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			progCircular.setContentView(R.layout.sipsetting_setupguide_online_manual_preloader);

			progressCircular = new DialogHolderProgressCircular();
			progressCircular.preloader = (ProgressBar) progCircular.findViewById(R.id.progressBar);

			progCircular.show();
			progCircular.setCancelable(false);
			progCircular.setCanceledOnTouchOutside(false);


			myProgressCount = 0;

			if(mNetworkService == null)
			{
				mNetworkService = NgnEngine.getInstance().getNetworkService();
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) 
		{
			File file = Environment.getExternalStorageDirectory();
			String FilePath = file.toString();	

			if (!isClient)
			{
				productid = NetworkManager1.INSTANCE.getLocalWifiMacAddress();
				provisionType = "pbx";
			}
			else
			{
				try 
				{
					productid = new JSONObject(AutoProvision.CreateInstance().getProductId(productid)).getString("product_id");
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
				provisionType = "c";
			}
			autoprovision();
			external_ip_address = AutoProvision.CreateInstance().getExternalIp();
			port = String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT));
			localIp = mNetworkService.getLocalIP(false);

			Log.e(TAG,"deviceUniqueKey:" + productid + " port: " + port + " ip: " + localIp + " external_ip_address " + external_ip_address + " FilePath:" + FilePath + " type " + type);

			return AutoProvisionXmlReader.getInstance().autoProvisionSetup(productid, provisionType, localIp, port, FilePath, " ", NetworkManager1.INSTANCE.getLocalWifiMacAddress(), external_ip_address, port);
		}


		protected void onPostExecute(final Boolean result) 
		{
			if(result) 
			{
				
				mConfigurationService.putString("PRODUCT_ID", productid, true);
				mConfigurationService.putString("provisionType", provisionType, true);
				productid = "";
				progCircular.dismiss();
				dialog_setupGuide_manualOnline.dismiss();
				view.setEnabled(true);
				view.setClickable(true);
				isCtr = true;
				isAutoprovi = false;


				AutoProvision.CreateInstance().moveConfigFile("users", Environment.getExternalStorageDirectory());
				AutoProvision.CreateInstance().moveConfigFile("sipsetup", Environment.getExternalStorageDirectory());
				AutoProvision.CreateInstance().moveConfigFile("extensions", Environment.getExternalStorageDirectory());
				storeSipSettings();
				NgnEngine.getInstance().start();
				NgnEngine.getInstance().getSipService().register(context);
				AutoProvisionDialog.getInstance().showNotifMessage(context, " Successfully downloaded ", new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						if (mOnFragmentClick != null)
						{
							Log.d(TAG, " my profile ");
							mOnFragmentClick.changeFragment(null, new FragmentSettingMyProfile(), false);
						}
					}
				});
			}
			else
			{
				AutoProvisionDialog.getInstance().showErrorMessage(context, AutoProvisionXmlReader.getInstance().getFailedResponse(), null);
				progCircular.dismiss();
			}

		}
	};

	public void storeSipSettings()
	{
		HashMap<String, String> sipSetupHashMap = AutoProvisionXmlReader.getInstance().getSipData();
		
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID));
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_CLIENT_IMPI, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID));
		Global.getInstance().set_client_Impi(sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID));
		
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_DISPLAY_NAME));
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID), sipSetupHashMap.get(AutoProvisionXmlReader.SIP_IP_ADDRESS)));
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_PASSWORD));
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_IP_ADDRESS));
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_IP_ADDRESS));
		mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, Integer.parseInt(sipSetupHashMap.get(AutoProvisionXmlReader.SIP_PORT)));
		
		mConfigurationService.commit();
	}

	
	private void autoprovision(){
		autoProvisionDownload(USERS_TAG);
		autoProvisionDownload(SIP_SETUP_TAG);
		autoProvisionDownload(EXTENSIONS_TAG);
	}
	
	private void autoProvisionDownload(final String configSelected)
	{
		AsyncTask<Void, Void, Boolean> taskConfig = new AsyncTask<Void, Void, Boolean>() {

			@Override
		    protected void onPreExecute() {
		        super.onPreExecute();        
		        Log.d(TAG,configSelected);
		    }
			  
			@Override
			protected Boolean doInBackground(Void... params) {
				Log.d(TAG, "doInBackground: ");
				return AutoProvision.CreateInstance().downloadConfigFile(configSelected, path, configSelected);
			}
			
			protected void onPostExecute(final String s) {
				AutoProvision.CreateInstance().moveConfigFile(configSelected, path);
		    }
		};
		taskConfig.execute();
	}
	
	private void client_setup_device_key()
	{
		dialog_client = new Dialog(context);
		dialog_client.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_client.setCancelable(true);
		dialog_client.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		dialog_client.setContentView(R.layout.client_device_input);
		
		clientdevicekeyHolder = new DialogHolderClientDevice();
		clientdevicekeyHolder.btnOKclient  = (Button) dialog_client.findViewById(R.id.client_input_ok);
		clientdevicekeyHolder.btnCancelClient = (Button) dialog_client.findViewById(R.id.client_input_cancel);
		clientdevicekeyHolder.etClient = (EditText) dialog_client.findViewById(R.id.client_input_et);
		
		clientdevicekeyHolder.btnOKclient.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if (NetworkManager1.INSTANCE.isWiFiConnected() || NetworkManager1.INSTANCE.isNetworkConnected() || NetworkManager1.INSTANCE.isEthernetConnected())
				{
					if (clientdevicekeyHolder.etClient.getText().toString().trim().length() > 0)
					{
						isAutoprovi = false;
						new CheckInternetAccessTask().execute();
						dialog_client.dismiss();
					}
					else
					{
						AutoProvisionDialog.getInstance().showErrorMessage(context, " Device Key is Empty ", null);
					}
				}
				else
				{
					AutoProvisionDialog.getInstance().showErrorMessage(context, " network not connected ", null);
				}
			}
		}); 
		
		clientdevicekeyHolder.btnCancelClient.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				dialog_client.dismiss();
			}
		});
		
		dialog_client.show();
		dialog_client.setCancelable(false);
		dialog_client.setCanceledOnTouchOutside(false);
		
	}
	
	private class updateIPaddress extends AsyncTask<Void, Void, String>
	{
		String provisionType;
		String port;
		String localIp;
		String external_ip_address;
		String calliD = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI);
		
		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
			
			if(mNetworkService == null)
			{
				mNetworkService = NgnEngine.getInstance().getNetworkService();
			}
			
		}

		@Override
		protected String doInBackground(Void... params) 
		{
			File file = Environment.getExternalStorageDirectory();
			String FilePath = file.toString();	
			
			
			if ( mNetworkService.getLocalIP(false) != null)
			{
				Log.d(TAG, " onresume " + mNetworkService.getLocalIP(false));
			}
			else
			{
				return "No Ipaddress";
			}

			productid = NetworkManager1.INSTANCE.getLocalWifiMacAddress();

			external_ip_address = AutoProvision.CreateInstance().getExternalIp();
			port = String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT));
			localIp = mNetworkService.getLocalIP(false);


			return AutoProvision.CreateInstance().updateIpAddressPort(localIp, port, external_ip_address, port, productid);
		}

		@Override
		protected void onPostExecute(String result) 
		{
			if (localIp == null)
			{
				Log.d(TAG, "localIp == null ");
			}
			else
			{
				mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", calliD , mNetworkService.getLocalIP(false)));
				mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, mNetworkService.getLocalIP(false));
				mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, mNetworkService.getLocalIP(false));
				mConfigurationService.commit();
				NgnEngine.getInstance().getSipService().stop();
				NgnEngine.getInstance().getSipService().start();
				NgnEngine.getInstance().getSipService().register(context);
			}
		}
	}
	
	public void showInputMethod() 
	{
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	
	public void hideInputMethod() 
	{
		Log.d(TAG, "hideInputMethod");
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}