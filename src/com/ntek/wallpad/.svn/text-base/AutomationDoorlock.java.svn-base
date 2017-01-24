package com.ntek.wallpad;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ntek.wallpad.Screens.BaseScreen;
import com.ntek.wallpad.Screens.Fragment.FragmentAutomationMain;
import com.ntek.wallpad.automation.tcp.TcpClient;
import com.ntek.wallpad.automation.utils.DeviceListPreferences;
import com.ntek.wallpad.automation.utils.RingProgressDialogManager;

public class AutomationDoorlock extends BaseScreen{

	private ListView lvDeviceList;
	private DeviceListPreferences sharedPref;
	private BroadcastReceiver mBroadCastRecv;
	private Context mContext;
	
	private List<DeviceAndLastAccess> items = new ArrayList<DeviceAndLastAccess>();
	private List<String> ipAddressList = new ArrayList<String>();
	
	private final static String TAG = FragmentAutomationMain.class.getCanonicalName();
	private final static String LOCK = "lock";
	private final static String UNLOCK = "unlock";
	private final static String CHECK = "check";

	private CustomArrayAdapter newAdapter;
//	private ArrayAdapter<String> adapter;
	private String ipAddress, id;
	private int previousState;
	private String deviceName;
	private boolean fromScreenAV;
	private TextView tvDeviceList;
	private TextView tvDeviceName;
	private TextView tvStatus;
	private ToggleButton tbDeviceStatus;
	private Button btnSettings;
	Typeface tfOpenSansRegular;
	
	public AutomationDoorlock() {
		super(SCREEN_TYPE.AUTOMATION_FRAG_T, TAG);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.layout_automation_main_screen);
		
		initializeUI();
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			fromScreenAV = Boolean.valueOf(bundle.getString("fromScreenAV", "false"));
			Log.d(TAG,"fromScreenAV: " + fromScreenAV );
		}
	}
	
	private void initializeUI()
	{
		lvDeviceList = (ListView) findViewById(R.id.doorlock_listview_devicelist);
		tvDeviceList = (TextView) findViewById(R.id.doorlock_textview_devicelist);

		tvDeviceName = (TextView) findViewById(R.id.doorlock_device_status_textview_name);
		tbDeviceStatus = (ToggleButton) findViewById(R.id.doorlock_device_status_toggle_onoff);
		tvStatus = (TextView) findViewById(R.id.doorlock_device_textview_status);
		btnSettings = (Button) findViewById(R.id.doorlock_header_setting);
		
		tfOpenSansRegular = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSansRegular.ttf");
		tvDeviceName.setTypeface(tfOpenSansRegular);
		tbDeviceStatus.setTypeface(tfOpenSansRegular);
		tvStatus.setTypeface(tfOpenSansRegular);
		tvDeviceList.setTypeface(tfOpenSansRegular);

		mContext = this;
		sharedPref = new DeviceListPreferences(mContext);

//		adapter	 = new ArrayAdapter<String>(this, R.layout.custom_listview_item_dtcontrol,
//						R.id.display_name_text_view, items);

		newAdapter = new CustomArrayAdapter(this, items);
		
		lvDeviceList.setAdapter(newAdapter);
		lvDeviceList.setOnItemClickListener(onItemClickListener);
		
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
		
		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(AutomationDoorlock.this, FragmentAutomation.class);
				startActivity(intent);
				overridePendingTransition(0, 0);
			}
		});
		
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);

				RingProgressDialogManager.hide();
				if (action.equals("com.smartbean.dtcontrol.connect.action")) {
					boolean success = false;
					final String response = intent.getStringExtra("response");
					Log.d(TAG, "getStringExtra() : " + response);

					// response from checking status
                	if(response.equals(getString(R.string.getrelay_lock))) {
                		previousState = 1;
                		tvStatus.setText(getString(R.string.getrelay_lock));
                		setToggleValue(true);
                		
                	}
                	else if(response.equals(getString(R.string.getrelay_unlock))) {
                		previousState = 0;
                		tvStatus.setText(getString(R.string.getrelay_unlock));
                		setToggleValue(false);
                	}
                	else if(response.equals(getString(R.string.getrelay_error))) {
                		tvStatus.setText(getString(R.string.getrelay_error));
                	}
					
                	else if (response.equals(getString(R.string.lock_setrelay_success))) {
						previousState = 1;
						tvStatus.setText(getString(R.string.lock_setrelay_success));
						setToggleValue(true);
						success = true;
						// success from unlock > lock
					}
					else if (response.equals(getString(R.string.lock_setrelay_failed))) {
						tvStatus.setText(getString(R.string.lock_setrelay_failed));
						setToggleValue(false);
					}
					
					else if (response.equals(getString(R.string.unlock_setrelay_success))) {
						previousState = 0;
						tvStatus.setText(getString(R.string.unlock_setrelay_success));
						setToggleValue(false);
						success = true;
						// success from lock > unlock
					}
					else if (response.equals(getString(R.string.unlock_setrelay_failed))) {
						tvStatus.setText(getString(R.string.unlock_setrelay_failed));
						setToggleValue(true);
					}
					
					else {
						previousState = -1;
						showToast("Connection Failed");
						tvStatus.setText("");
						
						if (tbDeviceStatus.isChecked())
						{
							tbDeviceStatus.setChecked(false);
						}
						else
						{
							tbDeviceStatus.setChecked(true);
						}
					}
                	
					if(success) {
						Log.d(TAG,"Repopulate device list	");
                		// Repopulate device list	
                		JSONObject message = new JSONObject();
                		String origDeviceList = sharedPref.getString(DeviceListPreferences.DEVICE_LIST, DeviceListPreferences.DEFAULT_DEVICE_LIST);
                		try {
                			Log.d(TAG,"origDeviceList: " + origDeviceList);
                			JSONObject json = new JSONObject(origDeviceList);
                			@SuppressWarnings("rawtypes")
                			Iterator i = json.keys();
                			while (i.hasNext()) {
                				String key = i.next().toString();
                				String msg = json.getString(key);
                				Log.d(TAG,"deviceName: " + deviceName);
                				Log.d(TAG,"key: " + key);
                				Log.d(TAG,"deviceName.equals(key): " + deviceName.equals(key));
                				if(deviceName.equals(key)) {
                					if(msg.contains("@")) {
                    					msg = msg.substring(0, msg.indexOf("@"));
                    				}
                					msg += "@" + getDateString();
                					Log.d(TAG,"msg: " + msg);
                				}
                				message.put(key, msg);
                			}
                		} catch (JSONException e) {
                			e.printStackTrace();
                		}
                		Log.d(TAG,"----------------------------------");
                		Log.d(TAG,"" +  message.toString());
                		Log.d(TAG,"----------------------------------");
                		sharedPref.putString(DeviceListPreferences.DEVICE_LIST, message.toString(), true);
                		
                		updateDeviceList();
                	}
				}
			}
		};
		
		if (deviceName != null) {
		}
	}
	
	@Override
	protected void onDestroy() {
		if(fromScreenAV) {
			((Engine)Engine.getInstance()).getScreenService().bringToFront(Main.ACTION_SHOW_AVSCREEN);
		}
		super.onDestroy();
	}
	
	public void performClick(int position) {
		if(newAdapter.getCount() > 0) {
			lvDeviceList.performItemClick(lvDeviceList.getChildAt(position), position, lvDeviceList.getItemIdAtPosition(position));
			tbDeviceStatus.setEnabled(true);
			tvDeviceName.setTextColor(Color.parseColor("#ffffff"));
		}
		else
		{
			tbDeviceStatus.setEnabled(false);
			tvStatus.setText(R.string.Disabled);
			tvDeviceName.setText(R.string.string_device_name);
			tvStatus.setTextColor(Color.parseColor("#616161"));
			tvDeviceName.setTextColor(Color.parseColor("#616161"));
		}
	}

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			setDeviceInformation(items.get(position).getDeviceName(), ipAddressList.get(position));
		}
	};
		
	private String getDateString() {
        SimpleDateFormat sdf =  new SimpleDateFormat("MMMM dd, yyyy|hh:mm a", Locale.getDefault());
        Date d = new Date(); //Get system date
       
        //Convert Date object to string
        String strDate = sdf.format(d);
        Log.d(TAG,"strDate: " + strDate);
		return strDate;
	}

	private void updateDeviceList() {
		String deviceList = sharedPref.getString(DeviceListPreferences.DEVICE_LIST,
						DeviceListPreferences.DEFAULT_DEVICE_LIST);
		
		if (!deviceList.isEmpty()) {
			try {
				items.clear();
				ipAddressList.clear();
				JSONObject json = new JSONObject(deviceList);

				@SuppressWarnings("rawtypes")
				Iterator i = json.keys();
				while (i.hasNext()) {
					String key = i.next().toString();
					String ipAdd = json.getString(key);
					String lastTimeAccess = "";
					String lastDateAccess = "";
    				if(ipAdd.contains("@")) {
    					lastDateAccess = ipAdd.substring(ipAdd.indexOf("@")+1, ipAdd.indexOf("|"));
    					lastTimeAccess = ipAdd.substring(ipAdd.indexOf("|")+1);
    					ipAdd = ipAdd.substring(0, ipAdd.indexOf("@"));
    				}
    				
					items.add(new DeviceAndLastAccess(key, lastDateAccess, lastTimeAccess));
					ipAddressList.add(ipAdd);
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		newAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onResume() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.smartbean.dtcontrol.connect.action");
		registerReceiver(mBroadCastRecv, intentFilter);
		
		updateDeviceList();
		ipAddress = "";
		performClick(0);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		if (mBroadCastRecv != null) {
			unregisterReceiver(mBroadCastRecv);
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
	
	private void setToggleValue(Boolean onoff)
	{
		if (onoff)
		{
			tvStatus.setTextColor(Color.parseColor("#8bc34a"));
		}
		else
		{
			tvStatus.setTextColor(Color.parseColor("#f44336"));
		}
		tbDeviceStatus.setChecked(onoff);
		
	}
	
	private class CustomArrayAdapter extends ArrayAdapter<DeviceAndLastAccess> {
		private Context context;
		private List<DeviceAndLastAccess> itemList;

		public CustomArrayAdapter(Context context, List<DeviceAndLastAccess> itemList) {
			super(context, 0, itemList);
			this.context = context;
			this.itemList = itemList;
		}

		public class ViewHolder {
			TextView tvDeviceName;
			TextView tvAccessedDate;
			TextView tvAccessedTime;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = new ViewHolder();
			if (view == null) {
				LayoutInflater layoutInflater =
						(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				view = layoutInflater.inflate(R.layout.child_automation_list, null);
				viewHolder.tvDeviceName = (TextView) view.findViewById(R.id.doorlock_display_name_text_view);
				viewHolder.tvAccessedDate = (TextView) view.findViewById(R.id.doorlock_number_text_view);
				viewHolder.tvAccessedTime = (TextView) view.findViewById(R.id.doorlock_occurrence_time_text_view);
				
				
				viewHolder.tvDeviceName.setTypeface(tfOpenSansRegular);
				viewHolder.tvAccessedDate.setTypeface(tfOpenSansRegular);
				viewHolder.tvAccessedTime.setTypeface(tfOpenSansRegular);
				
				viewHolder.tvDeviceName.setTextColor(Color.parseColor("#ffffff"));
				viewHolder.tvAccessedDate.setTextColor(Color.parseColor("#b5b5b5"));
				viewHolder.tvAccessedTime.setTextColor(Color.parseColor("#b5b5b5"));
				
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) view.getTag();
			}

			if (itemList != null) {
				if (viewHolder.tvDeviceName != null) {
					viewHolder.tvDeviceName.setText(itemList.get(position).getDeviceName());
				}
				if (viewHolder.tvAccessedDate != null) {
					viewHolder.tvAccessedDate.setText(itemList.get(position).getLastDateAccess());
				}
				if (viewHolder.tvAccessedTime != null) {
					viewHolder.tvAccessedTime.setText(itemList.get(position).getLastTimeAccess());
				}
			}

			return view;
		}
	}
	
	public class DeviceAndLastAccess {
		private String deviceName;
		private String lastDateAccess;
		private String lastTimeAccess;
		
		public DeviceAndLastAccess(String deviceName, String lastDateAccess, String lastTimeAccess) {
			super();
			this.deviceName = deviceName;
			this.lastDateAccess = lastDateAccess;
			this.lastTimeAccess = lastTimeAccess;
		}
		public String getDeviceName() {
			return deviceName;
		}
		public String getLastDateAccess() {
			return lastDateAccess;
		}
		public String getLastTimeAccess() {
			return lastTimeAccess;
		}
	}
	
}