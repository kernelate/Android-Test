package com.ntek.wallpad.Screens.Fragment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ntek.wallpad.R;
import com.ntek.wallpad.automation.tcp.TcpConnectClient;
import com.ntek.wallpad.automation.utils.DeviceListPreferences;
import com.ntek.wallpad.automation.utils.RingProgressDialogManager;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class FragmentAutomationSettingAutomatic extends Fragment implements OnClickListener{
	private final static String TAG = FragmentAutomationSettingAutomatic.class.getCanonicalName();
	private Button btn_scan;
	private ListView lv_doortalk;
	private BroadcastReceiver mBroadCastRecv;
	private Context mContext;
 	private String deviceList;
	private DeviceListPreferences sharedPref;
    private String message;
    private int selected;
    private View view;
    private LinearLayout layoutScanButton;
	private LinearLayout layoutDeviceList;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_setting_devicelist, container, false);
		
		initializeUI();
		
		return view;
	}

	@Override
	public void onResume() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ServerData.UPD_SEARCH_SERVER_END_CALLBACK);
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

	private void scanForDevices() {
		RingProgressDialogManager.show(mContext, getString(R.string.string_please_wait),
				getString(R.string.string_search_for_doortalk));
		new Thread(new UdpClient()).start();
	}

	private void displayListOfDevices() {
		CustomArrayAdapter adapter = new CustomArrayAdapter(mContext, ServerData.getList());
		lv_doortalk.setAdapter(adapter);
	}

	private class CustomArrayAdapter extends ArrayAdapter<ServerData> {
		private Context context;
		private List<ServerData> serverDataList;
		
		public CustomArrayAdapter(Context context, List<ServerData> serverDataList) {
			super(context, 0, serverDataList);
			this.context = context;
			this.serverDataList = serverDataList;
		}

		@Override
		public ServerData getItem(int position) {
			return serverDataList.get(position);
		}

		public class ViewHolder {
			TextView displayName;
			TextView ipAddr;
			Button add;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = new ViewHolder();
			
			if (view == null) {
				LayoutInflater layoutInflater =
						(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				view = layoutInflater.inflate(R.layout.list_single2_dtcontrol, null);
				viewHolder.displayName = (TextView) view.findViewById(R.id.displayNameTextView);
				viewHolder.ipAddr = (TextView) view.findViewById(R.id.ipAddressTextView);
				viewHolder.add = (Button) view.findViewById(R.id.add_or_delete_button);
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) view.getTag();
			}
			
			viewHolder.add.setText("Add");
			viewHolder.add.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					selected = position;
					RingProgressDialogManager.show(mContext);
					new Thread(new TcpConnectClient(mContext, serverDataList.get(position).getIpAddress())).start();
				}
			});
			
			final ServerData serverData = getItem(position);
			if (serverData != null) {
				if (viewHolder.displayName != null) {
					viewHolder.displayName.setText(serverData.getDisplayName());
				}
				if (viewHolder.ipAddr != null) {
					viewHolder.ipAddr.setText(serverData.getIpAddress());
				}
			}
			return view;
		}
	}

	private class UdpClient implements Runnable {
		private static final int UDP_PORT = 6789;

		@Override
		public void run() {
			Log.d(TAG, "C: Thread Start!!!");
			try {
				Log.d(TAG, "C: Connecting");
				DatagramSocket socket = new DatagramSocket();
				socket.setBroadcast(true);

				byte SendBuf[] = new byte[17];
				SendBuf = ("doortalk").getBytes();
				InetAddress Addr = InetAddress.getByName("255.255.255.255");
				DatagramPacket sPacket =
						new DatagramPacket(SendBuf, SendBuf.length, Addr, UDP_PORT);

				Log.d(TAG, "C: Sending");
				socket.send(sPacket);
				socket.setSoTimeout(7000);

				// Neil 2014-07-31
				/**
				 * Add infinite loop to receive many packets from many server
				 * allow SocketTimeoutException to end the loop at
				 * Socket.receive(rPacket);
				 */
				try {
					ServerData.getList().clear();
					while (true) {
						byte RecBuf[] = new byte[17];
						DatagramPacket rPacket = new DatagramPacket(RecBuf, RecBuf.length);

						Log.d("UDP", "C: Receiving");
						socket.receive(rPacket);

						InetAddress serverAddr = rPacket.getAddress();
						String serverAddrStr = serverAddr.toString().substring(1);
						String serverDisplayName = new String(rPacket.getData());
						
						Log.d(TAG, "C: serverAddrStr - "  + serverAddrStr);
						Log.d(TAG, "C: serverDisplayName - "  + serverDisplayName.substring(rPacket.getOffset(), rPacket.getLength()));

						ServerData.getList().add(new ServerData(serverDisplayName.substring(rPacket.getOffset(), rPacket.getLength()), serverAddrStr, ""));
					}
				}
				catch (SocketTimeoutException e) {
					Log.e(TAG, "C: Search end here");
				}

				/** Close socket to prevent leaks **/
				socket.close();
				Log.d(TAG, "C: Socket close");
			}
			catch (IOException e) {
				Log.e(TAG, "C: Error", e);
			}

			final Intent intent = new Intent(ServerData.UPD_SEARCH_SERVER_END_CALLBACK);
			mContext.sendBroadcast(intent);

			Log.d(TAG, "C: Thread End!!!");
		}
	}

	public static class ServerData {
		public static final String UPD_SEARCH_SERVER_END_CALLBACK =
				"com.smartbean.servertalk.network.UPD_SEARCH_SERVER_END_CALLBACK";
		String displayName;
		String ipAddress;
		String lastAccess;
		
		private static List<ServerData> dataList = null;

		public ServerData(String displayName, String ipAddress, String lastAccess) {
			super();
			this.displayName = displayName;
			this.ipAddress = ipAddress;
			this.lastAccess = lastAccess;
		}

		public String getDisplayName() {
			return displayName;
		}
		
		public String getLastAccess() {
			return lastAccess;
		}
		
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getIpAddress() {
			return ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public static synchronized List<ServerData> getList() {
			if (null == dataList) {
				dataList = new ArrayList<ServerData>();
			}
			return dataList;
		}
	}
	
	private void initializeUI()
	{
		btn_scan = (Button) view.findViewById(R.id.automation_button_scan);
		lv_doortalk = (ListView) view.findViewById(R.id.automation_listview_devicelist);
		layoutScanButton = (LinearLayout) view.findViewById(R.id.hideScanButton);
		layoutDeviceList = (LinearLayout) view.findViewById(R.id.deviceListLayout);
		
		btn_scan.setOnClickListener(this);

		mContext = getActivity();
		sharedPref = new DeviceListPreferences(mContext);

		deviceList = sharedPref.getString(DeviceListPreferences.DEVICE_LIST,
						DeviceListPreferences.DEFAULT_DEVICE_LIST);
		scanForDevices();
		
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);

				RingProgressDialogManager.hide();
				if (action.equals(ServerData.UPD_SEARCH_SERVER_END_CALLBACK)) {
					
					if (ServerData.getList().size() > 0)
					{
						layoutScanButton.setVisibility(View.GONE);
						layoutDeviceList.setVisibility(View.VISIBLE);
						displayListOfDevices();
					}
					else
					{
						layoutScanButton.setVisibility(View.VISIBLE);
						layoutDeviceList.setVisibility(View.GONE);
					}
				}
				else if (action.equals("com.smartbean.dtcontrol.TCP_CONNECT_CALLBACK")) {
					final String response = intent.getStringExtra("connect");
					Log.d(TAG, "getStringExtra() : " + response);

					RingProgressDialogManager.hide();
					if (!response.equals("failed")) {
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

							message.put(ServerData.getList().get(selected).getDisplayName(), ServerData.getList().get(selected).getIpAddress());
							deviceList = message.toString();
							sharedPref.putString(DeviceListPreferences.DEVICE_LIST, deviceList,
									true);
						}
						catch (JSONException e) {
							e.printStackTrace();
						}
						message = "Successfully added " +  ServerData.getList().get(selected).getDisplayName() + " device. Now, You have control of it";
					}
					else {
						message = "Failed to add " + ServerData.getList().get(selected).getDisplayName() + " device, please try again";
					}
					
					final AlertDialog.Builder notificationDialogBuilder =
							new AlertDialog.Builder(mContext);
//					notificationDialogBuilder.setIcon(R.drawable.ic_launcher);
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
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.automation_button_scan:
			scanForDevices();
			break;

		default:
			break;
		}
	}
}
