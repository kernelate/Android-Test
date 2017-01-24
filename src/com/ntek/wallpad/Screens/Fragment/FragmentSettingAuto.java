package com.ntek.wallpad.Screens.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Database.DoorTalkDatabaseDao;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.DoorTalkDevice;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.AsyncClient;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.LoginGlobal;
import com.ntek.wallpad.network.NetworkGlobal;
import com.ntek.wallpad.network.ServerData;
import com.ntek.wallpad.network.SocClient;

public class FragmentSettingAuto extends Fragment{
	
	private final static String TAG = FragmentSettingAuto.class.getCanonicalName();

	private BroadcastReceiver mBroadCastRecv;
	private OnChangeFragmentListener mOnSettingClick;
	
	private Button btnScan;
	private Button btnRescan;
	private ListView lvDeviceList;
	private Dialog loginDialog;
	private String loginID, loginPassword;
	private LinearLayout layoutScanButton;
	private LinearLayout layoutDeviceList;
	private Button btnManual;
	private Dialog loginManual;
	private Context mContext;
	private DialogHolderManual manualHolder ;
	private Dialog dialog_manual;
	private Context context;
	private DoorTalkDevice selectedDevice;
	View view;
	private DoorTalkDatabaseDao database;
	private ArrayList<DoorTalkDevice> deviceList;
	private static FragmentSettingAuto mThis;
	private CustomArrayAdapter adapter;
	private TextView messageDeviceList;
	private Boolean isListDeviceClick;
	
	public FragmentSettingAuto()
	{
		Log.d(TAG, TAG);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnSettingClick = (OnChangeFragmentListener)activity;
			
			mContext = activity;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);
				RingProgressDialogManager.hide();
				if(action.equals(Global.TCP_CONNECT_CALLBACK)) {
					final String response = intent.getStringExtra("connect");
					Log.d(TAG, "getStringExtra() : " + response + " selecttype " + FragmentCommonSetting.selectType);
					if (response.equals("success")) {
						if(FragmentCommonSetting.selectType == FragmentCommonSetting.SERVER_CONFIG) {
							loginDialog.show();
						}
						else if(FragmentCommonSetting.selectType == FragmentCommonSetting.EVENT_SETTING) {
							if (mOnSettingClick != null)
							{
								mOnSettingClick.changeFragment(null, new FragmentSettingEventNotificationForm(), true);
							};
						}
					}
//					if (response.equals("success")) {
//						if (FragmentCommonSetting.selectType == FragmentCommonSetting.SERVER_CONFIG) {
//							if (selectedDevice != null && selectedDevice.isSaved()) {
//								LoginGlobal.getInstance().setLoginID(selectedDevice.getLoginId());
//								LoginGlobal.getInstance().setLoginPassword(selectedDevice.getLoginPassword());
//
//								RingProgressDialogManager.show(getActivity(), "Please wait...", "Trying to Login...");
//								new Thread(new SocClient("login", CommonUtilities.soc_port)).start();
//							}
//							else {
//								loginDialog.show();
//							}
//						}
//					}
					else {
						showToast(getString(R.string.string_connection_fail));
					}
				}
				else if(action.equals(LoginGlobal.TCP_LOGIN_CALLBACK)) {
					final String response = intent.getStringExtra("login");
					Log.d(TAG, "getStringExtra() : " + response);

					if (response.equals("true") || response.equals("false") || response.equals("success")) {
						new Thread(new SocClient("doortalk", CommonUtilities.soc_port,getActivity())).start();
						FragmentCommonSetting.isDoorDeviceApMode = Boolean.parseBoolean(response);
					}
					else {
						RingProgressDialogManager.hide();
						if (response.equals("invalid_id"))
							showToast(getString(R.string.string_invalid_login_id));
						else if (response.equals("invalid_password"))
							showToast(getString(R.string.string_invalid_login_password));
						else if (response.equals("connection_lost"))
							showToast(getString(R.string.string_connection_lost));
					}
				}
//				else if (action.equals(Global.TCP_GET_DOORTALK_DATA_CALLBACK)) {
//					final String response = intent.getStringExtra("doortalk");
//					Log.d(TAG, "getStringExtra() : " + response);
//
//					RingProgressDialogManager.hide();
//					if (response.equals("success")) 
//					{
//						DoorTalkDevice newDevice = new DoorTalkDevice();
//						String platformVersion = Global.getInstance().getPlatformVersion();
//						newDevice.setPlatformVersion(platformVersion);
//						Log.d(TAG, " newDevice " + newDevice.getPlatformVersion() + " platformVersion " + platformVersion);
//						database.Open();
//						if (mOnSettingClick != null)
//						{
//							Log.d(TAG, " pasok sa success ");
//							mOnSettingClick.changeFragment(null, new FragmentSetUpDoorTalkSelectUser(), true);
//						}
//						database.Close();
//					}
//					else
//						showToast(getString(R.string.string_connection_lost));
//				}
				else if (action.equals(Global.TCP_GET_DOORTALK_DATA_CALLBACK)) {
					final String response = intent.getStringExtra("doortalk");
					Log.d(TAG, "getStringExtra() : " + response);

					RingProgressDialogManager.hide();
					if (response.equals("success")) {
						DoorTalkDevice newDevice = new DoorTalkDevice();

						newDevice.setMacAddress(Global.getInstance().getMacAddress());
						String platformVersion = Global.getInstance().getPlatformVersion();
						if(platformVersion == null && selectedDevice != null) {
							platformVersion = selectedDevice.getPlatformVersion();
						}
						newDevice.setPlatformVersion(platformVersion);
						newDevice.setIpAddress(Global.getInstance().getData());
						String displayName = Global.getInstance().getName();
						
						if (displayName.contains("IDENTITY_")) {
							displayName = getString(R.string.UNNAMED_DOORTALK);
						}
						Global.getInstance().setName(displayName);
						newDevice.setDisplayName(displayName);
						newDevice.setCallId(Global.getInstance().get_server_Impi());
						String networkType = NetworkGlobal.getInstance().getType();
						String ssid = NetworkGlobal.getInstance().getSsid();
						
//						if(networkType.equals("ethernet")) {
//							ssid = null;
//						}
						newDevice.setNetwork(ssid);
						newDevice.setLoginId(LoginGlobal.getInstance().getLoginID());
						newDevice.setLoginPassword(LoginGlobal.getInstance().getLoginPassword());

						// d2 nag sasave nag devicelist
						if(!FragmentCommonSetting.isDoorDeviceApMode) {
							database.Open();
							Log.d(TAG, " newDevice " + newDevice.getMacAddress() + " platformVersion " + platformVersion);
							if(newDevice.getMacAddress() != null) {
								if (!database.isDeviceExist(newDevice.getMacAddress())) {
									newDevice.setSaved(true);
									database.addDoorTalkDevice(newDevice);
									Log.d(TAG, "  addDoorTalkDevice1 ");
								}
								else {
									database.updateDeviceInfo(newDevice, false);
								}
							} 
//							else 
//							{
//								if (!database.isDeviceExist(newDevice.getIpAddress(), newDevice.getNetwork())) {
//									newDevice.setSaved(true);
//									database.addDoorTalkDevice(newDevice);
//									Log.d(TAG, "  addDoorTalkDevice2 ");
//								}
//								else {
//									database.updateDeviceInfo(newDevice, true);
//								}
//							}
							database.Close();
						}
						mOnSettingClick.changeFragment(null, new FragmentSetUpDoorTalkSelectUser(), true);
//						mScreenService.show(SetUpDoorTalk.class);
					}
					else
//						ToastManager.showToast(SetUpDoorTalkList.this, getString(R.string.string_connection_lost),Toast.LENGTH_SHORT);
					
					showToast(getString(R.string.string_connection_lost));
				}
				else if(action.equals(ServerData.UPD_SEARCH_SERVER_END_CALLBACK)) {
					if (ServerData.getList().size() > 0)
					{
						Log.d(TAG, " 11111111111 ");
						layoutScanButton.setVisibility(View.GONE);
						layoutDeviceList.setVisibility(View.VISIBLE);
						displayListOfDevices();
					}
					else
					{
						Log.d(TAG, " 222222222222 ");
						layoutScanButton.setVisibility(View.VISIBLE);
						layoutDeviceList.setVisibility(View.GONE);
						messageDeviceList.setVisibility(View.VISIBLE);
					}
				}
			}
		};
		
//		mBroadCastRecv = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				final String action = intent.getAction();
//				Log.d(TAG, "onReceive() : " + action);
//
//				RingProgressDialogManager.hide();
//				if (action.equals(Global.TCP_CONNECT_CALLBACK)) {
//					final String response = intent.getStringExtra("connect");
//					Log.d(TAG, "getStringExtra() : " + response);
//					if (response.equals("success")) {
//						if (FragmentCommonSetting.selectType == FragmentCommonSetting.SERVER_CONFIG) {
//							if (selectedDevice != null && selectedDevice.isSaved()) {
//								LoginGlobal.getInstance().setLoginID(selectedDevice.getLoginId());
//								LoginGlobal.getInstance().setLoginPassword(selectedDevice.getLoginPassword());
//
//								RingProgressDialogManager.show(getActivity(), "Please wait...", "Trying to Login...");
//								new Thread(new SocClient("login", CommonUtilities.soc_port)).start();
//							}
//							else {
//								loginDialog.show();
//							}
//						}
//						
//						else if (FragmentCommonSetting.selectType == FragmentCommonSetting.EVENT_SETTING) {
//							mOnSettingClick.changeFragment(null, new FragmentSettingEventNotificationForm(), true);
////							mScreenService.show(SetUpClientEventInquiryDetail.class);
//						}
//					}
//					else {
////						ToastManager.showToast(SetUpDoorTalkList.this, getString(R.string.string_connection_fail),Toast.LENGTH_SHORT);
//						showToast(getString(R.string.string_connection_fail));
//					}
//				}
//				else if (action.equals(LoginGlobal.TCP_LOGIN_CALLBACK)) {
//					final String response = intent.getStringExtra("login");
//					Log.d(TAG, "getStringExtra() : " + response);
//
//					if (response.equals("true") || response.equals("false") || response.equals("success")) {
//						new Thread(new SocClient("doortalk", CommonUtilities.soc_port)).start();
//						FragmentCommonSetting.isDoorDeviceApMode = Boolean.parseBoolean(response);
//					}
//					else {
//						RingProgressDialogManager.hide();
//						if (response.equals("invalid_id"))
////							ToastManager.showToast(SetUpDoorTalkList.this, getString(R.string.string_invalid_login_id),	Toast.LENGTH_SHORT);
//						showToast(getString(R.string.string_invalid_login_id));
//						else if (response.equals("invalid_password"))
////							ToastManager.showToast(SetUpDoorTalkList.this, getString(R.string.string_invalid_login_password), Toast.LENGTH_SHORT);
//						showToast(getString(R.string.string_invalid_login_password));
//						else if (response.equals("connection_lost"))
////							ToastManager.showToast(SetUpDoorTalkList.this, getString(R.string.string_connection_lost),	Toast.LENGTH_SHORT);
//						showToast(getString(R.string.string_connection_lost));
//					}
//				}
//				else if (action.equals(Global.TCP_GET_DOORTALK_DATA_CALLBACK)) {
		
//					final String response = intent.getStringExtra("doortalk");
//					Log.d(TAG, "getStringExtra() : " + response);
//
//					RingProgressDialogManager.hide();
//					if (response.equals("success")) {
//						DoorTalkDevice newDevice = new DoorTalkDevice();
//
//						newDevice.setMacAddress(Global.getInstance().getMacAddress());
//						String platformVersion = Global.getInstance().getPlatformVersion();
//						if(platformVersion == null && selectedDevice != null) {
//							platformVersion = selectedDevice.getPlatformVersion();
//						}
//						newDevice.setPlatformVersion(platformVersion);
//						newDevice.setIpAddress(Global.getInstance().getData());
//						String displayName = Global.getInstance().getName();
//						if (displayName.contains("IDENTITY_")) {
//							displayName = getString(R.string.UNNAMED_DOORTALK);
//						}
//						Global.getInstance().setName(displayName);
//						newDevice.setDisplayName(displayName);
//						newDevice.setCallId(Global.getInstance().get_server_Impi());
//						
//						String networkType = NetworkGlobal.getInstance().getType();
//						String ssid = NetworkGlobal.getInstance().getSsid();
//						if(networkType.equals("ethernet")) {
//							ssid = null;
//						}
//						newDevice.setNetwork(ssid);
//
//						newDevice.setLoginId(LoginGlobal.getInstance().getLoginID());
//						newDevice.setLoginPassword(LoginGlobal.getInstance().getLoginPassword());
//
//						if(!FragmentCommonSetting.isDoorDeviceApMode) {
//							database.Open();
//							if(newDevice.getMacAddress() != null) {
//								if (!database.isDeviceExist(newDevice.getMacAddress())) {
//									newDevice.setSaved(true);
//									database.addDoorTalkDevice(newDevice);
//								}
//								else {
//									database.updateDeviceInfo(newDevice, false);
//								}
//							} else {
//								if (!database.isDeviceExist(newDevice.getIpAddress(), newDevice.getNetwork())) {
//									newDevice.setSaved(true);
//									database.addDoorTalkDevice(newDevice);
//								}
//								else {
//									database.updateDeviceInfo(newDevice, true);
//								}
//							}
//							database.Close();
//						}
//						mOnSettingClick.changeFragment(null, new FragmentSetUpDoorTalkSelectUser(), true);
////						mScreenService.show(SetUpDoorTalk.class);
//					}
//					else
////						ToastManager.showToast(SetUpDoorTalkList.this, getString(R.string.string_connection_lost),Toast.LENGTH_SHORT);
//					
//					showToast(getString(R.string.string_connection_lost));
//				}
//				else if (action.equals(ServerData.UPD_SEARCH_SERVER_END_CALLBACK)) {
//					displayListOfDevices();
//				}
//			}
//		};
		
		loginDialog = new Dialog(getActivity());
		loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loginDialog.setContentView(R.layout.dialog_login);

		final EditText loginIDEditText = (EditText) loginDialog.findViewById(R.id.loginIDEditText);
		final EditText loginPasswordEditText = (EditText) loginDialog.findViewById(R.id.loginPasswordEditText);
		final Button loginButton = (Button) loginDialog.findViewById(R.id.loginButton);

		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loginID = loginIDEditText.getText().toString();
				loginPassword = loginPasswordEditText.getText().toString();

				if (!loginID.trim().isEmpty()) {
					if (!loginPassword.trim().isEmpty()) {
						LoginGlobal.getInstance().setLoginID(loginID);
						LoginGlobal.getInstance().setLoginPassword(loginPassword);

						loginDialog.dismiss();
						Context context = NgnApplication.getContext();
						RingProgressDialogManager.show(context, "Please wait...", "Trying to Login...");
						new Thread(new SocClient("login", CommonUtilities.soc_port,getActivity())).start();
					}
					else {
						showToast(getString(R.string.string_password_empty));
					}
				}
				else {
					showToast(getString(R.string.string_id_empty));
				}
			}
		});
		
		context = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_setting_devicelist, container, false);
		initializeUI();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewStateRestored(savedInstanceState);
		Log.d(TAG, "onViewStateRestored(Bundle savedInstanceState)");
	}
	
	protected void initializeUI()
	{
		btnScan = (Button) view.findViewById(R.id.automation_button_scan);
		btnRescan = (Button) view.findViewById(R.id.automation_button_listview_scan);
		lvDeviceList = (ListView) view.findViewById(R.id.automation_listview_devicelist);
		layoutScanButton = (LinearLayout) view.findViewById(R.id.hideScanButton);
		layoutDeviceList = (LinearLayout) view.findViewById(R.id.deviceListLayout);
		btnManual = (Button) view.findViewById(R.id.automation_button_listview_manual);
		messageDeviceList = (TextView) view.findViewById(R.id.messageDeviceList);
//		btnScan.setVisibility(View.INVISIBLE);
//		scanForDevices();   
		btnRescan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, " rescan ");
				scanForDevices();
			}
		});

		btnScan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isListDeviceClick = true;
				scanForDevices();
			}
		});
		
		btnManual.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				mOnSettingClick.changeFragment(null, new FragmentSettingManual(), false);
				manualSetup();
//				loginDialog = new Dialog(getActivity());
//				loginDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//				loginDialog.setContentView(R.layout.fragment_setting_manual);
//				loginManual.show();
				
			}
		});

		lvDeviceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				
				if (!isListDeviceClick) 
				{
					Log.d(TAG, " aaaaaaaaaaaaaaaaaaaaa " + " deviceList " + deviceList.get(arg2).getDisplayName() );
					Global.getInstance().setData(deviceList.get(arg2).getIpAddress());
					RingProgressDialogManager.show(getActivity(), getString(R.string.string_please_wait), getString(R.string.string_connecting_device));

					new Thread(new SocClient("connect", CommonUtilities.soc_port, getActivity())).start();
//					if(deviceList.size() > 1)
//					{
//						Log.d(TAG, " qweqweqweqw1111 ");
//					}
//					else
//					{
//						Log.d(TAG, " qweqweqwe2222 ");
//						new Thread(new SocClient("connect", CommonUtilities.soc_port, getActivity())).start();
//					}
				}
				else
				{
					Log.d(TAG, " bbbbbbbbbbbbbbbbbb ");
					Global.getInstance().setData(ServerData.getList().get(arg2).getIp());
					RingProgressDialogManager.show(getActivity(), getString(R.string.string_please_wait), getString(R.string.string_connecting_device));

					new Thread(new SocClient("connect", CommonUtilities.soc_port, getActivity())).start();
					Log.d(TAG, " ServerData.getList().get(arg2).getIp() " + ServerData.getList().get(arg2).getIp());
				}
			}
		});
		
		database = new DoorTalkDatabaseDao(getActivity());
		
		database.Open();
		deviceList = new ArrayList<DoorTalkDevice>();
//		
		deviceList = database.getDoorTalkDeviceList();
//		
//		adapter = new CustomArrayAdapter(mContext, deviceList);
//		lvDeviceList.setAdapter(adapter);
//		
//		Log.d(TAG, " deviceList " + deviceList.size() );
		database.Close();
		Log.d(TAG, "zzzzzzzzzzzzzzz ");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Global.TCP_CONNECT_CALLBACK);
		intentFilter.addAction(ServerData.UPD_SEARCH_SERVER_END_CALLBACK);
		intentFilter.addAction(LoginGlobal.TCP_LOGIN_CALLBACK);
		intentFilter.addAction(Global.TCP_GET_DOORTALK_DATA_CALLBACK);
		intentFilter.addAction("com.ntek.wallpad.thread.message");
		getActivity().registerReceiver(mBroadCastRecv, intentFilter);
		
		adapter = new CustomArrayAdapter(mContext, deviceList);
		lvDeviceList.setAdapter(adapter);
		layoutDeviceList.setVisibility(View.VISIBLE);
		messageDeviceList.setVisibility(View.INVISIBLE);
		displayListOfDevices();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(mBroadCastRecv);
	}
	
	public void scanForDevices() {
//		DialogRingProgressManager.getInstance(getActivity()).show(getString(R.string.string_please_wait), getString(R.string.string_scanning_network));
//		new Thread(new Client(CommonUtilities.port)).start();
		new AsyncClient(CommonUtilities.port, getActivity()).execute();
	}
//	SEARCHED DEVICE LIST fragment_setting_devicelist
	protected void displayListOfDevices() {
//		CustomArrayAdapter adapter = new CustomArrayAdapter(getActivity(), ServerData.getList());
//		lvDeviceList.setAdapter(adapter);
		Log.d(TAG, "cccccccccccccccc");
		isListDeviceClick = false;
		database.Open();
		ArrayList<DoorTalkDevice> databaseDeviceList = database.getDoorTalkDeviceList();
		database.Close();

		deviceList.clear();
		for (DoorTalkDevice data : databaseDeviceList) {
			deviceList.add(data);
			Log.d(TAG, " deviceList11  " + deviceList.size() + " data " + data);
		}

		for (ServerData data : ServerData.getList()) {
			DoorTalkDevice device = new DoorTalkDevice();
			device.setPlatformVersion(data.getPlatformVersion());
			device.setIpAddress(data.getIp());
			device.setDisplayName(data.getName());
			deviceList.add(device);
			Log.d(TAG, " ServerData data " + ServerData.getList().size());
		}
		adapter.notifyDataSetChanged();
	}
	
	private void showToast(String message) {
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	private class CustomArrayAdapter extends ArrayAdapter<DoorTalkDevice> {
		private Context  context;
		private List<DoorTalkDevice> serverDataList;
		
		public CustomArrayAdapter(Context context, List<DoorTalkDevice> serverDataList) {
			super(context, 0, serverDataList);
			this.context = context;
			this.serverDataList = serverDataList;
		}
		
		@Override
		public DoorTalkDevice getItem(int position) {
			return serverDataList.get(position);
		}
		
		public class ViewHolder {
			TextView displayName;
			TextView ipAddr;
			ImageView dtImage;
		}
		
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = new ViewHolder();
			if (view == null) {
				
				Log.d(TAG, " wwwwwwwwwwwwwwwwwww ");
				LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				view = layoutInflater.inflate(R.layout.list_single2, null);
				viewHolder.displayName = (TextView) view.findViewById(R.id.displayNameTextView);
				viewHolder.ipAddr = (TextView) view.findViewById(R.id.ipAddressTextView);
//				viewHolder.dtImage = (ImageView) view.findViewById(R.id.dtIconImageView);
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) view.getTag();
			}

			final DoorTalkDevice serverData = getItem(position);
			if (serverData != null) {
				if (viewHolder.displayName != null) {
					viewHolder.displayName.setText(serverData.getDisplayName());
				}
				if (viewHolder.ipAddr != null) {
					viewHolder.ipAddr.setText(serverData.getIpAddress());
				}
//				if (viewHolder.dtImage != null) {
//					viewHolder.dtImage.setImageResource(R.drawable.dt_doortalk_device_icon_rev1); //FIXME IMPORT DRAWABLE
//				}
				Log.d(TAG, " serverData.getDisplayName() " + serverData.getDisplayName());
			}
			return view;
		}
	}
	
	private class DialogHolderManual 
	{
		private Button btncancel;
		private Button btnconnect;
		private EditText etIPAddress;
		private EditText etPort;
	}
	
	private void manualSetup()
	{
		dialog_manual = new Dialog(context);
		dialog_manual.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_manual.setCancelable(true);
		dialog_manual.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		dialog_manual.setContentView(R.layout.dialog_manual);

		manualHolder = new DialogHolderManual();
		manualHolder.btncancel = (Button) dialog_manual.findViewById(R.id.login_manual_cancel);
		manualHolder.btnconnect = (Button) dialog_manual.findViewById(R.id.login_manual_connect);
		manualHolder.etIPAddress = (EditText) dialog_manual.findViewById(R.id.login_manual_ipadd_et);
		manualHolder.etPort = (EditText) dialog_manual.findViewById(R.id.login_manual_pw_et);
		
		manualHolder.btnconnect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isValidIp(manualHolder.etIPAddress.getText().toString())) {
					Global.getInstance().setData(manualHolder.etIPAddress.getText().toString());
					RingProgressDialogManager.show(getActivity(), getString(R.string.string_please_wait),
							getString(R.string.string_connecting_device));
					
					new Thread(new SocClient("connect", CommonUtilities.soc_port, getActivity())).start();
				} else {
					showToast("Not a valid Ip Address");
				}
				dialog_manual.dismiss();
			}
		});
		
		manualHolder.btncancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				dialog_manual.dismiss();
			}
		});

		dialog_manual.show();
		dialog_manual.setCancelable(false);
		dialog_manual.setCanceledOnTouchOutside(false);

	}
	
	private static boolean isValidIp(final String ip) {
	    return InetAddressUtils.isIPv4Address(ip) || InetAddressUtils.isIPv6Address(ip);
	}
}
