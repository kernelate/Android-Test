package com.ntek.wallpad.Screens.Fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ntek.wallpad.R;
import com.ntek.wallpad.automation.tcp.TcpClient;
import com.ntek.wallpad.automation.utils.DeviceListPreferences;
import com.ntek.wallpad.automation.utils.RingProgressDialogManager;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class FragmentAutomationMain extends Fragment {
	private final static String TAG = FragmentAutomationMain.class.getCanonicalName();
	private final static String LOCK = "lock";
	private final static String UNLOCK = "unlock";
	private final static String CHECK = "check";

	private BroadcastReceiver mBroadCastRecv;
	
	private Context mContext;
	private List<String> items = new ArrayList<String>();
	private List<String> ipAddressList = new ArrayList<String>();
	private DeviceListPreferences sharedPref;
	private ArrayAdapter<String> adapter;
	private String ipAddress, id;
	private int previousState;
	private String deviceName;
	
	private View view;
	
	private TextView tvDeviceName;
	private TextView tvStatus;
	private ToggleButton tbDeviceStatus;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_automation_device_status, container, false);
		
		tvDeviceName = (TextView) view.findViewById(R.id.automation_device_status_textview_name);
		tbDeviceStatus = (ToggleButton) view.findViewById(R.id.automation_device_status_toggle_onoff);
		tvStatus = (TextView) view.findViewById(R.id.automation_device_textview_status);
		
		Typeface tfOpenSansRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		tvDeviceName.setTypeface(tfOpenSansRegular);
		tbDeviceStatus.setTypeface(tfOpenSansRegular);
		tvStatus.setTypeface(tfOpenSansRegular);

		mContext = getActivity();
		sharedPref = new DeviceListPreferences(mContext);

		adapter = new ArrayAdapter<String>(getActivity(), R.layout.custom_listview_item_dtcontrol,
						R.id.display_name_text_view, items);
		adapter.setDropDownViewResource(R.layout.custom_listview_item_dtcontrol);

		
		tbDeviceStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if (previousState != 0 && !ipAddress.isEmpty())
				{
					previousState = 0;
					id = UNLOCK;
					RingProgressDialogManager.show(mContext);
					new Thread(new TcpClient(mContext, ipAddress, id)).start();
				}
				else if (previousState != 1 && !ipAddress.isEmpty())
				{
					previousState = 1;
					id = LOCK;
					RingProgressDialogManager.show(mContext);
					new Thread(new TcpClient(mContext, ipAddress, id)).start();
				}
			}
		});

		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);

				RingProgressDialogManager.hide();
				if (action.equals("com.smartbean.dtcontrol.connect.action")) {
					final String response = intent.getStringExtra("response");
					Log.d(TAG, "getStringExtra() : " + response);

					// response from checking status
                	if(response.equals(getString(R.string.getrelay_lock))) {
                		previousState = 1;
                		tvStatus.setText(getString(R.string.getrelay_lock));
                		tbDeviceStatus.setChecked(true);
                	}
                	else if(response.equals(getString(R.string.getrelay_unlock))) {
                		previousState = 0;
                		tvStatus.setText(getString(R.string.getrelay_unlock));
                		tbDeviceStatus.setChecked(false);
                	}
                	else if(response.equals(getString(R.string.getrelay_error))) {
                		tvStatus.setText(getString(R.string.getrelay_error));
                	}
					
                	else if (response.equals(getString(R.string.lock_setrelay_success))) {
						previousState = 1;
						tvStatus.setText(getString(R.string.lock_setrelay_success));
						tbDeviceStatus.setChecked(true);
					}
					else if (response.equals(getString(R.string.lock_setrelay_failed))) {
						tvStatus.setText(getString(R.string.lock_setrelay_failed));
						tbDeviceStatus.setChecked(false);
					}
					
					else if (response.equals(getString(R.string.unlock_setrelay_success))) {
						previousState = 0;
						tvStatus.setText(getString(R.string.unlock_setrelay_success));
						tbDeviceStatus.setChecked(false);
					}
					else if (response.equals(getString(R.string.unlock_setrelay_failed))) {
						tvStatus.setText(getString(R.string.unlock_setrelay_failed));
						tbDeviceStatus.setChecked(true);
					}
					
					else {
						previousState = -1;
						showToast("Connection Failed");
						tvStatus.setText("");
					}
				}
			}
		};
		
		if (deviceName != null) {
			tvDeviceName.setText(deviceName);
		}
		return view;
	}


	@Override
	public void onResume() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.smartbean.dtcontrol.connect.action");
		getActivity().registerReceiver(mBroadCastRecv, intentFilter);

		items.clear();
		ipAddressList.clear();

		String deviceList =
				sharedPref.getString(DeviceListPreferences.DEVICE_LIST,
						DeviceListPreferences.DEFAULT_DEVICE_LIST);
		Log.d(TAG, deviceList);

		if (!deviceList.isEmpty()) {
			try {
				JSONObject json = new JSONObject(deviceList);

				@SuppressWarnings("rawtypes")
				Iterator i = json.keys();
				while (i.hasNext()) {
					String key = i.next().toString();
					items.add(key);
					ipAddressList.add(json.getString(key));
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

//		if (items.size() != 0 && items.size() < 5) {
//			android.view.ViewGroup.LayoutParams params = contact_items_list_view.getLayoutParams();
//			params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
//			contact_items_list_view.setLayoutParams(params);
//		}

		adapter.notifyDataSetChanged();
//		selected_text_view.setText("Select Device");
		ipAddress = "";
//		setDoorImageStatus("");
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

	public void setDeviceInformation(String name, String ipAddress)
	{
		Log.d(TAG, "setDeviceInformation:" + name + " : " + ipAddress);
		this.ipAddress = ipAddress;
		deviceName = name;
		id = CHECK;
		RingProgressDialogManager.show(mContext);
		new Thread(new TcpClient(mContext, this.ipAddress, id)).start();
		tvDeviceName.setText(name);
	}
	
}
