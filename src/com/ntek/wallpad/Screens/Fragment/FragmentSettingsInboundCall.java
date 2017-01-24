package com.ntek.wallpad.Screens.Fragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.SocClient;

public class FragmentSettingsInboundCall extends Fragment
{
	private final static String TAG = FragmentSettingsInboundCall.class.getCanonicalName();
	
	private View view;
	private TextView nameTitleHeader;
	private Button btnAdd;
	private Button btnSave;
	private ListView lvInboundCall;
	
	private String deviceList;
	private String nDeviceName;

	private CustomArrayAdapter adapter;
	private BroadcastReceiver mBroadCastRecv;
	
	public FragmentSettingsInboundCall(String devicename)
	{
		Log.i(TAG, "FragmentSettingsInboundCall");
		this.nDeviceName = devicename;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		deviceList = Global.getInstance().getInboundBlockList();
		
		BlockInfo.getList().clear();
		if (deviceList != null && !deviceList.isEmpty()) {
			try {
				JSONObject json = new JSONObject(deviceList);

				@SuppressWarnings("rawtypes")
				Iterator i = json.keys();
				while (i.hasNext()) {
					String key = i.next().toString();
					BlockInfo.getList().add(new BlockInfo(key, json.getString(key)));
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);
				RingProgressDialogManager.hide();
				if (action.equals("com.smartbean.servertalk.action.SENDING_INBOUND_BLOCKLIST_CALLBACK")) {
					String response = intent.getStringExtra("response");
					Log.d(TAG, "getStringExtra() : " + response);

					if (response.equals("success")) {
						showToast("Inbound Blocklist is successfully send to the server");
					}
					else {
						BlockInfo.getList().clear();
						if (!deviceList.isEmpty()) {
							try {
								JSONObject json = new JSONObject(deviceList);

								@SuppressWarnings("rawtypes")
								Iterator i = json.keys();
								while (i.hasNext()) {
									String key = i.next().toString();
									BlockInfo.getList().add(new BlockInfo(key, json.getString(key)));
								}
							}
							catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						adapter.notifyDataSetChanged();
						showToast(getString(R.string.string_connection_lost));
					}
				}
			}
		};
	}
	
 	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_outbound_call, container,false);
		initializeUi();
		return view;
	}
 	
 	@Override
 	public void onResume() {
 		// TODO Auto-generated method stub
 		super.onResume();
 		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.smartbean.servertalk.action.SENDING_INBOUND_BLOCKLIST_CALLBACK");
		getActivity().registerReceiver(mBroadCastRecv, intentFilter);
 	}
 	
 	@Override
 	public void onPause() {
 		// TODO Auto-generated method stub
 		super.onPause();
 		if (mBroadCastRecv != null) 
 		{
			getActivity().unregisterReceiver(mBroadCastRecv);
		}
 	}
 	
	protected void initializeUi() 
	{
		nameTitleHeader = (TextView) view.findViewById(R.id.fragment_setting_outbound_call_unnamed_doortalk_outbound_call);
		btnAdd = (Button) view.findViewById(R.id.setting_button_add_outbound_call);
		lvInboundCall = (ListView) view.findViewById(R.id.setting_listview_outbound_call);
		btnSave = (Button) view.findViewById(R.id.setting_button_save_outbound_call);
		adapter = new CustomArrayAdapter(getActivity(), BlockInfo.getList());
		lvInboundCall.setAdapter(adapter);
		
		nameTitleHeader.setText("INBOUND CALL-BLOCKING LIST");
		
		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (BlockInfo.getList().size() == 5) {
					showToast("Call block list is limited to 5");
					return;
				} 
				showCreateDialog();
			}
		});
		
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				JSONObject json = new JSONObject();
				try {
					for (int i = 0; i < BlockInfo.getList().size(); i++) 
					{
						json.put(BlockInfo.getList().get(i).getPartyUri(), BlockInfo.getList().get(i).getDisplayName());
					}
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
				
				Global.getInstance().setInboundBlockList(json.toString());
				if(Global.getInstance().getInboundBlockList() != null) 
				{
					RingProgressDialogManager.show(getActivity(), "Please wait...", "Sending inbound blocklist...");
					new Thread(new SocClient("inbound_blocklist", CommonUtilities.soc_port, getActivity())).start();
				}	
			}
		});
	}
	
	
	private void showCreateDialog() {
		final Dialog createDialog = new Dialog(getActivity());
		createDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		createDialog.setContentView(R.layout.dialog_add_inbound_calls);
		createDialog.setCancelable(true);

		final EditText callNumberEditText = (EditText) createDialog.findViewById(R.id.dialog_add_inbound_calls_edittext_receive_number);
		final EditText informationEditText = (EditText) createDialog.findViewById(R.id.dialog_add_inbound_calls_edittext_email);
		final Button saveButton = (Button) createDialog.findViewById(R.id.dialog_add_inbound_calls_button_save);
	
		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (informationEditText.getText().toString().length() < 1 || callNumberEditText.getText().toString().length() < 1) {
					showToast("Don't leave any fields empty");
					return;
				}
				
				for(int i = 0; i < BlockInfo.getList().size(); i++ ) {
					if(callNumberEditText.getText().toString().equals(BlockInfo.getList().get(i).getPartyUri())) {
						showToast("Callee is already blocked");
						return;
					}
				}
				
				BlockInfo.getList().add(new BlockInfo(callNumberEditText.getText().toString(), informationEditText.getText().toString()));
				adapter.notifyDataSetChanged();
				createDialog.dismiss();
			}
		});
		createDialog.show();
	}
	
	public static class BlockInfo {
		String displayName;
		String partyUri;

		private static List<BlockInfo> dataList = null;

		public BlockInfo(String partyUri, String displayName) {
			super();
			this.displayName = displayName;
			this.partyUri = partyUri;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getPartyUri() {
			return partyUri;
		}

		public void setPartyUri(String partyUri) {
			this.partyUri = partyUri;
		}

		public static synchronized List<BlockInfo> getList() {
			if (null == dataList) {
				dataList = new ArrayList<BlockInfo>();
			}
			return dataList;
		}
	}
	
	private class CustomArrayAdapter extends ArrayAdapter<BlockInfo> {
		private Context context;
		private List<BlockInfo> serverDataList;

		public CustomArrayAdapter(Context context, List<BlockInfo> serverDataList) {
			super(context, 0, serverDataList);
			this.context = context;
			this.serverDataList = serverDataList;
		}

		@Override
		public BlockInfo getItem(int position) {
			return serverDataList.get(position);
		}

		public class ViewHolder {
			TextView displayName;
			TextView partyUri;
			Button delete;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = new ViewHolder();
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				view = layoutInflater.inflate(R.layout.setting_outbound_call_adapter, null);
				viewHolder.displayName = (TextView) view.findViewById(R.id.setting_textview_outbound_information);
				viewHolder.partyUri = (TextView) view.findViewById(R.id.setting_textview_number_outbound);
				viewHolder.delete = (Button) view.findViewById(R.id.setting_imagebutton_delete_outbound);
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) view.getTag();
			}
			BlockInfo serverData = getItem(position);
			if (serverData != null) {
				if (viewHolder.displayName != null) {
					viewHolder.displayName.setText(serverData.getDisplayName());
				}
				if (viewHolder.partyUri != null) {
					viewHolder.partyUri.setText(serverData.getPartyUri());
				}
			}
			viewHolder.delete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					final AlertDialog.Builder removeDeviceDialogBuilder = new AlertDialog.Builder(getActivity());
					removeDeviceDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
					removeDeviceDialogBuilder.setTitle("Delete");
					removeDeviceDialogBuilder.setMessage("Are you sure you want to remove this device from the list?");

					removeDeviceDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int index) {
							BlockInfo.getList().remove(position);
							adapter.notifyDataSetChanged();
							dialog.dismiss();
						}
					});

					removeDeviceDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
	
	protected void showToast(String message) {
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

}
