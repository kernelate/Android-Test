package com.ntek.wallpad.Screens.Fragment;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Database.DoorTalkDatabaseDao;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.DoorTalkDevice;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.LoginGlobal;
import com.ntek.wallpad.network.NetworkGlobal;
import com.ntek.wallpad.network.SocClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSettingsAccount extends Fragment {

	private final static String TAG = FragmentSettingsAccount.class.getCanonicalName();
	private View view;
	private EditText oldIdTextView;
	private EditText newIdEditText;
	private EditText oldPasswordEditText;
	private EditText newPasswordEditText;
	private EditText retypePasswordEditText;
	private Button saveBtn;
	private BroadcastReceiver mBroadCastRecv;
	
	private OnChangeFragmentListener mOnFragmentClick;
	
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
//						loginChangeDialog.dismiss();
						
						DoorTalkDatabaseDao database = new DoorTalkDatabaseDao(getActivity());
						
						database.Open();
						DoorTalkDevice selectedDevice = null;
//						if(Global.getInstance().getMacAddress() != null) {
//							selectedDevice = database.getDoorTalkDevice(Global.getInstance().getMacAddress());
//						}
//						else {
//							String networkType = NetworkGlobal.getInstance().getType();
//							String ssid = NetworkGlobal.getInstance().getSsid();
//							if(networkType.equals("ethernet")) {
//								ssid = null;
//							}
//							selectedDevice = database.getDoorTalkDevice(Global.getInstance().getData(), ssid);
//						}
						
						selectedDevice.setLoginId(LoginGlobal.getInstance().getLoginID());
						selectedDevice.setCallId(LoginGlobal.getInstance().getLoginPassword());
						
						if(selectedDevice.getMacAddress() != null) {
							database.updateDeviceInfo(selectedDevice, false);
						} else {
							database.updateDeviceInfo(selectedDevice, true);
						}
						database.Close();
						
					}
					else {
//						ToastManager.showToast(SetUpDoorTalk.this, getString(R.string.string_connection_lost), Toast.LENGTH_SHORT);
					}
				}
				else if(action.equals("com.smartbean.servertalk.action.TCP_SEND_DEVICE_INFO_CALLBACK")) {
					String response = intent.getStringExtra("response");
					Log.d(TAG, "getStringExtra() : " + response);

//					if (response.equals("inserted")) {
//						showNotificationDialog("Notification", "Inserting Successful");
//					}
//					else if (response.equals("updated")) {
//						showNotificationDialog("Notification", "Updating Successful");
//					}
//					else if (response.equals("record not updated")) {
//						showNotificationDialog("Notification", "Entry is already saved");
//					}
					if(response.equals("success"))
					{
						showNotificationDialog("NOTIFICATION", "Save Successful");
					}
					else {
						showNotificationDialog("Warning", response);
					}
//					if (response.equals("inserted") || response.equals("updated") || response.equals("record not updated")) {
//						Global.getInstance().setFlagEventRegistered(true);
//						sendDeviceInfoDialog.dismiss();
//					}
				}
			}

		};
	}

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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_account, container, false);
		initializeUi();
		return view;
	}


	private void initializeUi()
	{
		oldIdTextView = (EditText) view.findViewById(R.id.oldIdTextView);
		newIdEditText = (EditText) view.findViewById(R.id.newIdEditText);
		oldPasswordEditText = (EditText) view.findViewById(R.id.oldPasswordEditText);
		newPasswordEditText = (EditText) view.findViewById(R.id.newPasswordEditText);
		retypePasswordEditText = (EditText) view.findViewById(R.id.retypePasswordEditText);
		saveBtn = (Button) view.findViewById(R.id.fragment_setting_account_save_btn);
		
		oldIdTextView.setText(LoginGlobal.getInstance().getLoginID().toString());
		oldIdTextView.setEnabled(false);
		
		saveBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if (!oldPasswordEditText.getText().toString().equals(LoginGlobal.getInstance().getLoginPassword())) {
					showToast(getString(R.string.string_invalid));
					return;
				}

				if (!newPasswordEditText.getText().toString().equals(retypePasswordEditText.getText().toString())) {
					showToast(getString(R.string.string_error));
					return;
				}

				if(newIdEditText.getText().toString() != null && newIdEditText.getText().toString().length() > 0) {
					LoginGlobal.getInstance().setLoginID(newIdEditText.getText().toString());
				}
				LoginGlobal.getInstance().setLoginPassword(newPasswordEditText.getText().toString());

				RingProgressDialogManager.show(getActivity());
				new Thread(new SocClient("login_change", CommonUtilities.soc_port, getActivity())).start();
				RingProgressDialogManager.hide();
				Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
				
				mOnFragmentClick.changeFragment(null, new FragmentSetUpDoorTalkSelectUser(), false);
			}
		});
		
	}



	protected void showToast(String message) {
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	@Override
	public void onResume() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LoginGlobal.TCP_LOGIN_CHANGE_CALLBACK);
		intentFilter.addAction("com.smartbean.servertalk.action.TCP_SEND_DEVICE_INFO_CALLBACK");
		intentFilter.addAction("com.smartbean.servertalk.action.CHECK_EVENT_SERVER_ONLINE");
		registerReceiver(mBroadCastRecv, intentFilter);
		super.onResume();
	}

	private void registerReceiver(BroadcastReceiver mBroadCastRecv2,
			IntentFilter intentFilter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		if (mBroadCastRecv != null) {
			unregisterReceiver(mBroadCastRecv);
		}
		super.onPause();
	}

	private void unregisterReceiver(BroadcastReceiver mBroadCastRecv2) {
		// TODO Auto-generated method stub
		
	}

	private void showNotificationDialog(String title, String message) {
		final AlertDialog.Builder warningDialogBuilder = new AlertDialog.Builder(getActivity());
		warningDialogBuilder.setIcon(R.drawable.ic_launcher);
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
}
