package com.ntek.wallpad.Screens.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaRouter.VolumeCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.LoginGlobal;
import com.ntek.wallpad.network.SocClient;

public class FragmentSettingsDeviceConfiguration extends Fragment implements OnClickListener {
	

	private static final String TAG = FragmentSettingsDeviceConfiguration.class.getCanonicalName();
	
	private View view;

//	private Button btnNetworkSetUp;
	private Button btnSip;
	private Button btnOutboundCall;
	private Button btnInboundCall;
	private Button btnSecurityFuntion;
	private Button btnEventServer;
	private Button btnEventSettings;
	private Button btnEmailNotification;
	private Button btnSetup;
	private TextView selectedDeviceTextView;
	
	private BroadcastReceiver mBroadCastRecv;
	
	private OnChangeFragmentListener mOnChangeFrag;
	
	private Dialog loginChangeDialog, sendDeviceInfoDialog;
	private static boolean laterFlag = false;
	private String mDeviceName;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mOnChangeFrag = (OnChangeFragmentListener) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);
				RingProgressDialogManager.hide();
				if(action.equals(LoginGlobal.TCP_LOGIN_CHANGE_CALLBACK)) {
					String response = intent.getStringExtra("login_change");
					Log.d(TAG, "getStringExtra() : " + response);
					
					if (response.equals("success")) {
						loginChangeDialog.dismiss();
					}
					else {
						showToast(getString(R.string.string_connection_lost));
					}
				}
				else if(action.equals("com.smartbean.servertalk.action.TCP_SEND_DEVICE_INFO_CALLBACK")) {
					String response = intent.getStringExtra("response");
					Log.d(TAG, "getStringExtra() : " + response);

					if (response.equals("inserted")) {
						showNotificationDialog("Notification", "Inserting Successful");
					}
					else if (response.equals("updated")) {
						showNotificationDialog("Notification", "Updating Successful");
					}
					else if (response.equals("record not updated")) {
						showNotificationDialog("Notification", "Entry is already saved");
					}
					else {
						showNotificationDialog("Warning", response);
					}
					if (response.equals("inserted") || response.equals("updated") || response.equals("record not updated")) {
						Global.getInstance().setFlagEventRegistered(true);
						sendDeviceInfoDialog.dismiss();
					}
				}
			}
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView()");
		view = inflater.inflate(R.layout.fragment_setting_device_configuration,container, false);

		initializeUi();

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LoginGlobal.TCP_LOGIN_CHANGE_CALLBACK);
		intentFilter.addAction("com.smartbean.servertalk.action.TCP_SEND_DEVICE_INFO_CALLBACK");
		intentFilter.addAction("com.smartbean.servertalk.action.CHECK_EVENT_SERVER_ONLINE");
		getActivity().registerReceiver(mBroadCastRecv, intentFilter);
	}

	
	@Override
	public void onPause() {
		super.onPause();
		if (mBroadCastRecv != null)
		{
			getActivity().unregisterReceiver(mBroadCastRecv);
		}

	}
	
	private void initializeUi() 
	{
		selectedDeviceTextView = (TextView) view.findViewById(R.id.selectedDeviceTextView);
		btnSip = (Button) view.findViewById(R.id.phone_setting_button_sip);
		btnOutboundCall = (Button) view.findViewById(R.id.phone_setting_button_outbound_call);
		btnInboundCall = (Button) view.findViewById(R.id.phone_setting_button_inbound_call);
		btnSecurityFuntion = (Button) view.findViewById(R.id.phone_setting_button_security_function);
		btnEventServer = (Button) view.findViewById(R.id.phone_setting_button_event_server);
		btnEventSettings = (Button) view.findViewById(R.id.phone_setting_button_event_setting);
		btnEmailNotification = (Button) view.findViewById(R.id.phone_setting_button_email_notif);
//		btnSetup = (Button) view.findViewById(R.id.setup);
		
		Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		Typeface fontSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		
		selectedDeviceTextView.setTypeface(fontSemiBold);
		btnSip.setTypeface(fontSemiBold);
		btnOutboundCall.setTypeface(fontSemiBold);
		btnInboundCall.setTypeface(fontSemiBold);
		btnSecurityFuntion.setTypeface(fontSemiBold);
		btnEventServer.setTypeface(fontSemiBold);
		btnEventSettings.setTypeface(fontSemiBold);
		btnEmailNotification.setTypeface(fontSemiBold);
//		btnSetup.setTypeface(fontSemiBold);
		
		if (Global.getInstance().getName().equals("IDENTITY_NAME")) {
			selectedDeviceTextView.setText(getString(R.string.UNNAMED_DOORTALK));
		}
		else {
			selectedDeviceTextView.setText(Global.getInstance().getName());
		}
		
		mDeviceName = selectedDeviceTextView.getText().toString();
		
//		btnSetup.setOnClickListener(this);
		btnSip.setOnClickListener(this);
		btnOutboundCall.setOnClickListener(this);
		btnInboundCall.setOnClickListener(this);
		btnSecurityFuntion.setOnClickListener(this);
		btnEventServer.setOnClickListener(this);
		btnEventSettings.setOnClickListener(this);
		btnEmailNotification.setOnClickListener(this);
		selectedDeviceTextView.setOnClickListener(this);
	} 

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.phone_setting_button_sip:
			if (mOnChangeFrag != null)
			{
				Log.d(TAG, " sipsipsipsipsipsipsipsisispp  ");
				mOnChangeFrag.changeFragment(null, new FragmentSettingSipConfiguration(), true);
			}
			break;
			
		case R.id.phone_setting_button_outbound_call:
			if (mOnChangeFrag != null)
			{
				mOnChangeFrag.changeFragment(null, new FragmentSettingOutboundCall(mDeviceName), true);
			}
			break;
		case R.id.phone_setting_button_inbound_call:
			if (mOnChangeFrag != null)
			{
				mOnChangeFrag.changeFragment(null, new FragmentSettingsInboundCall(mDeviceName), true);
			}
			break;
		case R.id.phone_setting_button_security_function:
			if (mOnChangeFrag != null)
			{
//				mOnChangeFrag.changeFragment(null, new FragmentSettingSecurityFunction(mDeviceName), true);
				mOnChangeFrag.changeFragment(null, new FragmentSettingSecurityFunction(), true);
			}
			break;
			
		case R.id.phone_setting_button_event_server:
//			showSendDeviceInfoDialog();
			if (mOnChangeFrag != null)
			{
				mOnChangeFrag.changeFragment(null, new FragmentSettingEventServer(), true);
			}
			break;
			
		case R.id.phone_setting_button_event_setting:
			if (mOnChangeFrag != null)
			{
//				mOnChangeFrag.changeFragment(null, new FragmentSettingEventNotification(mDeviceName), true);
				mOnChangeFrag.changeFragment(null, new FragmentSettingEventNotification(), true);
			}
			break;
			
		case R.id.phone_setting_button_email_notif:
			if (mOnChangeFrag != null)
			{
//				mOnChangeFrag.changeFragment(null, new FragmentSettingEmailNotification(mDeviceName), true);
				mOnChangeFrag.changeFragment(null, new FragmentSettingEmailNotification(), true);
			}
			break;
		case R.id.setup:
//			showLoginChangeDialog();
			break;
			
		default:
			break;
		}
	}
	
	
	
	private void showNotificationDialog(String title, String message) {
		final AlertDialog.Builder warningDialogBuilder =
				new AlertDialog.Builder(getActivity());
		warningDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		warningDialogBuilder.setTitle(title);
		warningDialogBuilder.setMessage(message);
		warningDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		warningDialogBuilder.show();
	}
	
	protected void showToast(String message) {
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

//	private void showLoginChangeDialog() {
//
//		loginChangeDialog = new Dialog(getActivity());
//		loginChangeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		loginChangeDialog.setContentView(R.layout.dialog_account_settings);
//		loginChangeDialog.setCancelable(true);
//		loginChangeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
//		
//
//		final TextView oldIdTextView = (TextView) loginChangeDialog.findViewById(R.id.oldIdTextView);
//		final EditText newIdEditText = (EditText) loginChangeDialog.findViewById(R.id.newIdEditText);
//		final EditText oldPasswordEditText = (EditText) loginChangeDialog.findViewById(R.id.oldPasswordEditText);
//		final EditText newPasswordEditText = (EditText) loginChangeDialog.findViewById(R.id.newPasswordEditText);
//		final EditText retypePasswordEditText = (EditText) loginChangeDialog.findViewById(R.id.retypePasswordEditText);
//
//		final Button saveButton = (Button) loginChangeDialog.findViewById(R.id.saveButton);
//
//		final Button cancelButton = (Button) loginChangeDialog.findViewById(R.id.cancelButton);
//
//		oldIdTextView.setText(LoginGlobal.getInstance().getLoginID().toString());
//		cancelButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				loginChangeDialog.dismiss();
//			}
//		});
//		
//		saveButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (!oldPasswordEditText.getText().toString().equals(LoginGlobal.getInstance().getLoginPassword())) {
//					showToast(getString(R.string.string_invalid));
//					return;
//				}
//
//				if (!newPasswordEditText.getText().toString().equals(retypePasswordEditText.getText().toString())) {
//					showToast(getString(R.string.string_error));
//					return;
//				}
//
//				if(newIdEditText.getText().toString() != null && newIdEditText.getText().toString().length() > 0) {
//					LoginGlobal.getInstance().setLoginID(newIdEditText.getText().toString());
//				}
//				LoginGlobal.getInstance().setLoginPassword(newPasswordEditText.getText().toString());
//
//				RingProgressDialogManager.show(getActivity());
//				new Thread(new SocClient("login_change", CommonUtilities.soc_port)).start();
//			}
//		});
//		loginChangeDialog.show();
//	}
	
	private void showSendDeviceInfoDialog() {
		sendDeviceInfoDialog = new Dialog(getActivity());
		sendDeviceInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		sendDeviceInfoDialog.setContentView(R.layout.dialog_setup_event_server);
		sendDeviceInfoDialog.setCancelable(false);

		final EditText eventServerUrl = (EditText) sendDeviceInfoDialog.findViewById(R.id.editText_eventServer);
		final EditText eventServerPort = (EditText) sendDeviceInfoDialog.findViewById(R.id.editText_eventPort);

		final Button connectButton = (Button) sendDeviceInfoDialog.findViewById(R.id.btn_connect);
		final Button laterButton = (Button) sendDeviceInfoDialog.findViewById(R.id.btn_later);
		
		laterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendDeviceInfoDialog.dismiss();
				laterFlag = true;
			}
		});
		
		// default port is 8080
		eventServerPort.setText(Integer.toString(Global.getInstance().getEventServerPort()));
		connectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Global.getInstance().setEventServerUrl(eventServerUrl.getText().toString().trim());
				Global.getInstance().setEventServerPort(Integer.parseInt(eventServerPort.getText().toString().trim()));
				RingProgressDialogManager.show( getActivity(), "Please Wait...", "Server device connecting to Event Server...");
				new Thread(new SocClient("send_device_info", CommonUtilities.soc_port, getActivity())).start();
			}
		});
		
		if(!sendDeviceInfoDialog.isShowing())
			sendDeviceInfoDialog.show();
		
	}
	
	
}
