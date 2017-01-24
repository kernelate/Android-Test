package com.ntek.wallpad.Screens.Fragment;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.ntek.wallpad.R;
import com.ntek.wallpad.automation.tcp.TcpConnectClient;
import com.ntek.wallpad.automation.utils.DeviceListPreferences;
import com.ntek.wallpad.automation.utils.RingProgressDialogManager;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentAutomationSettingManual extends Fragment implements OnClickListener{
	private static final String TAG = FragmentAutomationSettingManual.class.getCanonicalName();
	private EditText ip_address_edit_text, port_edit_text;
	private Button add_device_button;
	private TextView tvManual;
	private TextView tvIP;
	private TextView tvPort;
	private DeviceListPreferences sharedPref;
	private Context mContext;
	private String message, displayName, ipAddress, portStr, deviceList;
	private BroadcastReceiver mBroadCastRecv;
	private View view;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_setting_manual, container, false);
		initializeUI();
		
		return view;
	}

	private void initializeUI()
	{
		
		mContext = getActivity();
		sharedPref = new DeviceListPreferences(mContext);

		deviceList =sharedPref.getString(DeviceListPreferences.DEVICE_LIST,
						DeviceListPreferences.DEFAULT_DEVICE_LIST);

		ip_address_edit_text = (EditText) view.findViewById(R.id.fragment_setting_manual_edittext_ip_address);
		port_edit_text = (EditText) view.findViewById(R.id.fragment_setting_manual_edittext_port);
		add_device_button = (Button) view.findViewById(R.id.fragment_setting_manual_button_connect);
		tvManual = (TextView) view.findViewById(R.id.automation_setting_manual);
		tvIP = (TextView) view.findViewById(R.id.automation_setting_manual_ip);
		tvPort = (TextView) view.findViewById(R.id.automation_setting_manual_port);
		
		Typeface tfOpenSansRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		tvManual.setTypeface(tfOpenSansRegular);
		ip_address_edit_text.setTypeface(tfOpenSansRegular);
		port_edit_text.setTypeface(tfOpenSansRegular);
		
		Typeface tfOpenSansSemibold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		add_device_button.setTypeface(tfOpenSansSemibold);
		tvIP.setTypeface(tfOpenSansSemibold);
		tvPort.setTypeface(tfOpenSansSemibold);
		
		add_device_button.setText("Add");
		add_device_button.setOnClickListener(this);
		
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);

				RingProgressDialogManager.hide();
				if (action.equals("com.smartbean.dtcontrol.TCP_CONNECT_CALLBACK")) {
					final String response = intent.getStringExtra("connect");
					Log.d(TAG, "getStringExtra() : " + response);

					RingProgressDialogManager.hide();
					if (!response.equals("failed")) {
						displayName = response;

						try {
							JSONObject message = new JSONObject();

							if (!deviceList.isEmpty()) {
								JSONObject json = new JSONObject(deviceList);

								@SuppressWarnings("rawtypes")
								Iterator i = json.keys();
								while (i.hasNext()) {
									String key = i.next().toString();
									message.put(key, json.getString(key));
								}
							}

							message.put(displayName, ipAddress);
							deviceList = message.toString();
							sharedPref.putString(DeviceListPreferences.DEVICE_LIST, deviceList,
									true);
						}
						catch (JSONException e) {
							e.printStackTrace();
						}
						message = "Successfully added " + displayName + " device. Now, You have control of it";
					}
					else {
						message = "Failed to add the device, please try again";
					}
					
					final AlertDialog.Builder notificationDialogBuilder =
							new AlertDialog.Builder(mContext);
					notificationDialogBuilder.setTitle("Notification");
					notificationDialogBuilder.setMessage(message);
					
					notificationDialogBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int index) {
									dialog.dismiss();
								}
							});

					notificationDialogBuilder.show();
				}
			}
		};
		
		
	}

	@Override
	public void onResume() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.smartbean.dtcontrol.TCP_CONNECT_CALLBACK");
		getActivity().registerReceiver(mBroadCastRecv, intentFilter);
		super.onResume();
	}

	@Override
	public void onPause() {
		if (mBroadCastRecv != null) {
			getActivity().unregisterReceiver(mBroadCastRecv);
		}
		super.onPause();
	}

	private void showToast(String message) {
		Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.fragment_setting_manual_button_connect:
			int port = 0;
			ipAddress = ip_address_edit_text.getText().toString().trim();
			portStr = port_edit_text.getText().toString().trim();
			
			if(ipAddress.isEmpty() || portStr.isEmpty()) {
				return;
			}
			else {
				try {
					port = Integer.parseInt(portStr);
				}
				catch (NumberFormatException e) {
					showToast("Input a valid port");
					return;
				}
				
				RingProgressDialogManager.show(mContext);
				new Thread(new TcpConnectClient(mContext, ipAddress, port)).start();
			}
		
			break;

		default:
			break;
		}
	}
}
