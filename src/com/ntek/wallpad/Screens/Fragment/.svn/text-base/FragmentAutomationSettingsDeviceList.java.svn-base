package com.ntek.wallpad.Screens.Fragment;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentAutomationSettingAutomatic.ServerData;
import com.ntek.wallpad.automation.utils.DeviceListPreferences;

public class FragmentAutomationSettingsDeviceList extends Fragment 
{
	private View view;
	private Context mContext;

	private CustomArrayAdapter adapter;
	private DeviceListPreferences sharedPref;
	private String deviceList;
	private TextView tvDeviceList;
	private ListView lvSettingsDeviceList;
	Typeface tfOpenSansRegular;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_automation_settings_device_list, container, false);
			
		initializeUi();	

		return view;
	}
	private void initializeUi() 
	{
		lvSettingsDeviceList = (ListView) view.findViewById(R.id.automation_listview_device_list);
		tvDeviceList = (TextView) view.findViewById(R.id.automation_setting_devicelist);
		
		mContext = getActivity();
		sharedPref = new DeviceListPreferences(mContext);

		deviceList = sharedPref.getString(DeviceListPreferences.DEVICE_LIST,
						DeviceListPreferences.DEFAULT_DEVICE_LIST);
		
		tfOpenSansRegular  = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		tvDeviceList.setTypeface(tfOpenSansRegular);
		
		adapter = new CustomArrayAdapter(mContext, ServerData.getList());
		lvSettingsDeviceList.setAdapter(adapter);
		
		ServerData.getList().clear();
		if (!deviceList.isEmpty()) {
			try {
				JSONObject json = new JSONObject(deviceList);

				@SuppressWarnings("rawtypes")
				Iterator i = json.keys();
				while (i.hasNext()) {
					String key = i.next().toString();
					String ipAdd = json.getString(key);
					String lastAccess = json.getString(key);
					if(ipAdd.contains("@")) {
						lastAccess = ipAdd.substring(ipAdd.indexOf("@"));
						ipAdd = ipAdd.substring(0, ipAdd.indexOf("@"));
					}
					ServerData.getList().add(new ServerData(key, ipAdd, lastAccess));
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
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
			Button delete;
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
				viewHolder.delete = (Button) view.findViewById(R.id.add_or_delete_button);
				
				viewHolder.displayName.setTypeface(tfOpenSansRegular);
				viewHolder.ipAddr.setTypeface(tfOpenSansRegular);
				viewHolder.delete.setTypeface(tfOpenSansRegular);
				
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) view.getTag();
			}

			final ServerData serverData = getItem(position);
			if (serverData != null) {
				if (viewHolder.displayName != null) {
					viewHolder.displayName.setText(serverData.getDisplayName());
				}
				if (viewHolder.ipAddr != null) {
					viewHolder.ipAddr.setText(serverData.getIpAddress());
				}
			}
			viewHolder.delete.setText("Delete");
			viewHolder.delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					final AlertDialog.Builder removeDeviceDialogBuilder =
							new AlertDialog.Builder(mContext);
//					removeDeviceDialogBuilder.setIcon(R.drawable.);
					removeDeviceDialogBuilder.setTitle("Remove");
					removeDeviceDialogBuilder.setMessage("Are you sure you want to remove this device from the list?");
					
					removeDeviceDialogBuilder.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int index) {
									ServerData.getList().remove(position);
									
									JSONObject json = new JSONObject();
									try {
										for(int i = 0; i < ServerData.getList().size(); i++) {
											json.put(ServerData.getList().get(i).getDisplayName(), ServerData.getList().get(i).getIpAddress() + ServerData.getList().get(i).getLastAccess());
										}
									}
									catch (JSONException e) {
										e.printStackTrace();
									}
									deviceList = json.toString();
									adapter.notifyDataSetChanged();
									
									sharedPref.putString(DeviceListPreferences.DEVICE_LIST,
											deviceList, true);
//									showToast("Device is remove from the list");
									dialog.dismiss();
								}
							});

					removeDeviceDialogBuilder.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int index) {
									dialog.dismiss();
								}
							});

					removeDeviceDialogBuilder.show();
				}
			});

			return view;
		}
	}
	
}
