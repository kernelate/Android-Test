package com.ntek.wallpad.Screens.Fragment;

import java.util.Iterator;
import java.util.List;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.json.JSONException;
import org.json.JSONObject;

import com.hudomju.swipe.ListViewAdapter;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingOutboundCall.PriorityInfo;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsInboundCall.BlockInfo;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.SocClient;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSettingsCallList extends Fragment {

	private final static String TAG = FragmentSettingsCallList.class.getCanonicalName();

	private View view;
	private String outboundDeviceList;
	private String inboundDeviceList;
	private SwipeToDismissTouchListener<ListViewAdapter> touchListener;
	private ListView device_list_view;
	private OutboundListArrayAdapter outboundAdapter;
	private InboundListArrayAdapter inboundAdapter;
	private Button outboundCallsButton;
	private Button inboundCallsButton;
	private Button saveListButton;
	private Button addNumberButton;
	private boolean isOutbound;
	private LinearLayout inboundLabelLinearLayout;
	private LinearLayout outboundLabelLinearLayout;
	private INgnConfigurationService mConfigurationService;
	private OnChangeFragmentListener mOnFragmentClick;
	
//	private DialogHolderSetupGuide outboundHolder ;
//	private Dialog dialog_outbound;
	private BroadcastReceiver mBroadCastRecv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		
		
		String[] priorityList = new String[5];
		outboundDeviceList = Global.getInstance().getOutboundPriorityList();
		//		Log.d(TAG, "outboundDeviceList > " + outboundDeviceList);
		PriorityInfo.getList().clear();
		if (outboundDeviceList != null && !outboundDeviceList.isEmpty()) {
			try {
				JSONObject json = new JSONObject(outboundDeviceList);

				@SuppressWarnings("rawtypes")
				Iterator i = json.keys();
				while (i.hasNext()) {
					String key = i.next().toString();
					if (key.equals("1")) {
						priorityList[0] = json.getString(key);
					}
					if (key.equals("2")) {
						priorityList[1] = json.getString(key);
					}
					if (key.equals("3")) {
						priorityList[2] = json.getString(key);
					}
					if (key.equals("4")) {
						priorityList[3] = json.getString(key);
					}
					if (key.equals("5")) {
						priorityList[4] = json.getString(key);
					}
				}

				for (int j = 0; j < priorityList.length; j++) {
					if (priorityList[j] != null && priorityList[j].length() > 0) {
						String partyUri = priorityList[j].substring(0, priorityList[j].indexOf("@"));
						String displayName = priorityList[j].substring(priorityList[j].indexOf("@") + 1,
								priorityList[j].length());
						PriorityInfo.getList().add(new PriorityInfo(Integer.toString(j + 1), partyUri, displayName));
					}
				}
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

		inboundDeviceList = Global.getInstance().getInboundBlockList();
		//		Log.d(TAG, "inboundDeviceList > " + inboundDeviceList);
		BlockInfo.getList().clear();
		if (inboundDeviceList != null && !inboundDeviceList.isEmpty()) {
			try {
				JSONObject json = new JSONObject(inboundDeviceList);

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

	}
	
	@Override
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
		view = inflater.inflate(R.layout.fragment_setting_calllist, container, false);
		initializedUI();
		return view;
	}

	private void initializedUI()
	{
		device_list_view = (ListView) view.findViewById(R.id.outbound_priorty_listView);
		outboundAdapter = new OutboundListArrayAdapter(getActivity(), PriorityInfo.getList());
		inboundAdapter = new InboundListArrayAdapter(getActivity(), BlockInfo.getList());

		inboundLabelLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_call_list_label_inbound);
		outboundLabelLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_call_list_label_outbound);

		inboundCallsButton = (Button) view.findViewById(R.id.button_call_list_inbound_calls);
		outboundCallsButton = (Button) view.findViewById(R.id.button_call_list_outbound_calls);

		saveListButton = (Button) view.findViewById(R.id.button_call_list_save_list);
		addNumberButton = (Button) view.findViewById(R.id.button_call_list_add_number);

		isOutbound = true;
		if (isOutbound) {
			inboundLabelLinearLayout.setVisibility(View.GONE);
			outboundLabelLinearLayout.setVisibility(View.VISIBLE);

			device_list_view.setAdapter(outboundAdapter);
			outboundAdapter.notifyDataSetChanged();
		}
		else {
			inboundLabelLinearLayout.setVisibility(View.VISIBLE);
			outboundLabelLinearLayout.setVisibility(View.GONE);

			device_list_view.setAdapter(inboundAdapter);
			inboundAdapter.notifyDataSetChanged();
		}

		inboundCallsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//				inboundCallsButton.setBackgroundResource(R.drawable.dt_tabs_bg_btn_new_selected);
				//				outboundCallsButton.setBackgroundResource(R.drawable.dt_tabs_bg_btn_new_normal);

				inboundLabelLinearLayout.setVisibility(View.VISIBLE);
				outboundLabelLinearLayout.setVisibility(View.GONE);
				inboundCallsButton.setBackgroundResource(R.color.color_lightgray);
				outboundCallsButton.setBackgroundResource(R.color.transparent);
				
				isOutbound = false;

				device_list_view.setAdapter(inboundAdapter);
				inboundAdapter.notifyDataSetChanged();
			}
		});

		outboundCallsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//				outboundCallsButton.setBackgroundResource(R.drawable.dt_tabs_bg_btn_new_selected);
				//				inboundCallsButton.setBackgroundResource(R.drawable.dt_tabs_bg_btn_new_normal);

				inboundLabelLinearLayout.setVisibility(View.GONE);
				outboundLabelLinearLayout.setVisibility(View.VISIBLE);
				inboundCallsButton.setBackgroundResource(R.color.transparent);
				outboundCallsButton.setBackgroundResource(R.color.color_lightgray);
				
				isOutbound = true;

				device_list_view.setAdapter(outboundAdapter);
				outboundAdapter.notifyDataSetChanged();
			}
		});

		addNumberButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isOutbound) {
					if (PriorityInfo.getList().size() == 5) {
						showToast("Call priority is limited to 5");
						return;
					}
					//					showCreatePriorityCalleeDialog();
					mOnFragmentClick.changeFragment(null, new FragmentSettingOutboundAdd(), true);
					
//					if (PriorityInfo.getList().size() == 5) {
//						showToast("Call priority is limited to 5");
//						return;
//					} 
////					showCreateDialog();
////					Log.d(TAG, "azxcxczxczxczxczc");
//					mOnFragmentClick.changeFragment(null, new FragmentSettingOutboundAdd(), true);
					
				}
				else {
					//					showCreateAllowedCallerDialog();
					mOnFragmentClick.changeFragment(null, new FragmentSettingsInboundAdd(), true);
				}
			}
		});
		
		saveListButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isOutbound) {
					boolean displayMessage = mConfigurationService.getBoolean(NgnConfigurationEntry.OUTBOUND_LIST_MESSAGE, NgnConfigurationEntry.DEFAULT_OUTBOUND_LIST_MESSAGE);
					
					if(displayMessage) {
//						DialogManager.showDisplayAgainDialog(ScreenCallList.this, DialogManager.HIDE_ICON, 
//								"Outbound List", "Oubound list are priority callee list that the DoorTalk will contact once DoorTalk button is pressed.",
//								NgnConfigurationEntry.OUTBOUND_LIST_MESSAGE, 
//								new OnDismissListener() {
//										@Override
//										public void onDismiss(DialogInterface dialog) {
//											if(!DialogManager.displayAgainDialogCancelled) {
//												sendOutboundList();
//											}
//										}
//									}
//								);
//						outboundDialog();
						AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
						mBuilder.setMessage("Oubound list are priority callee list that the DoorTalk will contact once DoorTalk button is pressed").setTitle("Outbound List")
//								.setOnDismissListener(new OnDismissListener() {
//									
//									@Override
//									public void onDismiss(DialogInterface dialog) {
//										sendOutboundList();
//									}
//								})
						
								.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
											sendOutboundList();
									}
									
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								});
						mBuilder.show();
//						RingProgressDialogManager.hide();
					}
					else {
						sendOutboundList();
					}
				}
				else {
					boolean displayMessage = mConfigurationService.getBoolean(NgnConfigurationEntry.INBOUND_LIST_MESSAGE, NgnConfigurationEntry.DEFAULT_INBOUND_LIST_MESSAGE);
					
					if(displayMessage) {
//						DialogManager.showDisplayAgainDialog(ScreenCallList.this, DialogManager.HIDE_ICON, 
//								"Inbound List", "Inbound list are caller list that are allowed to contact DoorTalk.",
//								NgnConfigurationEntry.INBOUND_LIST_MESSAGE, 
//								new OnDismissListener() {
//										@Override
//										public void onDismiss(DialogInterface dialog) {
//											if(!DialogManager.displayAgainDialogCancelled) {
//												sendInboundList();
//											}
//										}
//									}
//								);
						
						AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
						mBuilder.setMessage("Inbound list are caller list that are allowed to contact DoorTalk.").setTitle("Inbound List")
								.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										sendInboundList();
									}
									
								})
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								});
						mBuilder.show();
						RingProgressDialogManager.hide();
					}
					else {
						sendInboundList();
					}
				}
			}
		});
		
		
		mBroadCastRecv = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);
				RingProgressDialogManager.hide();
				if (action.equals("com.smartbean.servertalk.action.SENDING_OUTBOUND_PRIORITYLIST_CALLBACK")) {
					String response = intent.getStringExtra("response");
					Log.d(TAG, "getStringExtra() : " + response);

					if (response.equals("success")) {
						showToast("Outbound priority list is successfully send to the server");
					}
					else {
						PriorityInfo.getList().clear();
						if (!outboundDeviceList.isEmpty()) {
							try {
								JSONObject json = new JSONObject(outboundDeviceList);

								@SuppressWarnings("rawtypes")
								Iterator i = json.keys();
								while (i.hasNext()) {
									String key = i.next().toString();

								}
							}
							catch (JSONException e) {
								e.printStackTrace();
							}
						}

						outboundAdapter.notifyDataSetChanged();
						showToast(getString(R.string.string_connection_lost));
					}
				}
				else
				{
					if (action.equals("com.smartbean.servertalk.action.SENDING_INBOUND_BLOCKLIST_CALLBACK")) {
						String response = intent.getStringExtra("response");
						Log.d(TAG, "getStringExtra() : " + response);

						if (response.equals("success")) {
							showToast("Inbound allowed list is successfully send to the server");
						}
						else {
							BlockInfo.getList().clear();
							if (!inboundDeviceList.isEmpty()) {
								try {
									JSONObject json = new JSONObject(inboundDeviceList);

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

							inboundAdapter.notifyDataSetChanged();
							showToast(getString(R.string.string_connection_lost));
						}
					}
				}
				
			}
			
		};
		
		
		touchListener = new SwipeToDismissTouchListener<ListViewAdapter>(new ListViewAdapter(device_list_view),
				new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>()
				{
			@Override
			public boolean canDismiss(int position)
			{
				return true;
			}

			@Override
			public void onDismiss(ListViewAdapter view, int position)
			{
				if (isOutbound) {
					outboundAdapter.notifyDataSetChanged();
				}
				else {
					inboundAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onPendingDismiss(ListViewAdapter recyclerView, int position)
			{
				final int index = position;

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setMessage("Are you sure you want to delete this item?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) 
					{
						if (isOutbound) {
							String deletedPriority = "0";
							deletedPriority = PriorityInfo.getList().get(index).getPriority();
							PriorityInfo.getList().remove(index);

							for (int i = Integer.parseInt(deletedPriority) - 1; i < PriorityInfo
									.getList().size(); i++) {
								PriorityInfo.getList().get(i).setPriority(Integer.toString(i + 1));
							}
							outboundAdapter = new OutboundListArrayAdapter(getActivity(), PriorityInfo.getList());
							device_list_view.setAdapter(outboundAdapter);
							touchListener.undoPendingDismiss();
//							outboundAdapter.notifyDataSetChanged();
						}
						else {
							BlockInfo.getList().remove(index);
							inboundAdapter.notifyDataSetChanged();
						}
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) 
					{
						dialog.cancel();
						touchListener.undoPendingDismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();


				new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						if (touchListener.existPendingDismisses())
						{
							touchListener.undoPendingDismiss();
						}
					}
				};
			}

				});
		device_list_view.setOnTouchListener(touchListener);
		device_list_view.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
		
		outboundCallsButton.performClick();
	}

	protected void showToast(String message) {
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	private class OutboundListArrayAdapter extends ArrayAdapter<PriorityInfo> {
		private Context context;
		private List<PriorityInfo> serverDataList;

		public OutboundListArrayAdapter(Context context, List<PriorityInfo> serverDataList) {
			super(context, 0, serverDataList);
			this.context = context;
			this.serverDataList = serverDataList;
		}

		@Override
		public PriorityInfo getItem(int position) {
			return serverDataList.get(position);
		}

		public class ViewHolder {
			TextView displayName;
			TextView partyUri;
			TextView priority;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = new ViewHolder();
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				view = layoutInflater.inflate(
						R.layout.item_server_configuration_doortalk_device_outbound_call_list_information, null);
				viewHolder.displayName = (TextView) view.findViewById(R.id.outbound_name);
				viewHolder.partyUri = (TextView) view.findViewById(R.id.outbound_number);
				viewHolder.priority = (TextView) view.findViewById(R.id.outbound_priority);
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) view.getTag();
			}
			PriorityInfo priorityInfo = getItem(position);
			if (priorityInfo != null) {
				if (viewHolder.displayName != null) {
					viewHolder.displayName.setText(priorityInfo.getDisplayName());
				}
				if (viewHolder.partyUri != null) {
					viewHolder.partyUri.setText(priorityInfo.getPartyUri());
				}
				if (viewHolder.priority != null) {
					viewHolder.priority.setText(priorityInfo.getPriority());
				}
			}
			return view;
		}
	}

	private class InboundListArrayAdapter extends ArrayAdapter<BlockInfo> {
		private Context context;
		private List<BlockInfo> serverDataList;

		public InboundListArrayAdapter(Context context, List<BlockInfo> serverDataList) {
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
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = new ViewHolder();
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				view = layoutInflater.inflate(R.layout.item_server_configuration_doortalk_device_inbound_call_list_information, null);
				viewHolder.displayName = (TextView) view.findViewById(R.id.inbound_name);
				viewHolder.partyUri = (TextView) view.findViewById(R.id.inbound_number);
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

			return view;
		}
	}
	
	private void sendOutboundList() {
		JSONObject json = new JSONObject();
		try {
			for (int i = 0; i < PriorityInfo.getList().size(); i++) {
				json.put(PriorityInfo.getList().get(i).getPriority(), PriorityInfo.getList().get(i)
						.getPartyUri()
						+ "@" + PriorityInfo.getList().get(i).getDisplayName());
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		Global.getInstance().setOutboundPriorityList(json.toString());
		if (Global.getInstance().getOutboundPriorityList() != null) {
			RingProgressDialogManager.show(getActivity(), "Please wait...",
					"Sending outbound priority list...");
			new Thread(	new SocClient("outbound_prioritylist", CommonUtilities.soc_port, getActivity())).start();
		}
	}
	
	
	private void sendInboundList() { 
		JSONObject json = new JSONObject();
		try {
			for (int i = 0; i < BlockInfo.getList().size(); i++) {
				json.put(BlockInfo.getList().get(i).getPartyUri(), BlockInfo.getList().get(i)
						.getDisplayName());
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		Global.getInstance().setInboundBlockList(json.toString());
		if (Global.getInstance().getInboundBlockList() != null) {
			RingProgressDialogManager.show(getActivity(), "Please wait...",
					"Sending inbound allowed list...");
			new Thread(new SocClient("inbound_blocklist", CommonUtilities.soc_port, getActivity()))
					.start();
		}

	}
	

//	private class DialogHolderSetupGuide 
//	{
//		private Button btnNo;
//		private Button btnYes;
//	}
//	
//	private void outboundDialog()
//	{
//		dialog_outbound = new Dialog(getActivity());
//		dialog_outbound.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		dialog_outbound.setCancelable(true);
//		dialog_outbound.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//		dialog_outbound.setContentView(R.layout.fragment_setting_calllist_inbound_outbound_save);
//
//		outboundHolder = new DialogHolderSetupGuide();
//		outboundHolder.btnNo = (Button) dialog_outbound.findViewById(R.id.fragment_setting_inbound_outbound_cancel);
//		outboundHolder.btnYes = (Button) dialog_outbound.findViewById(R.id.fragment_setting_inbound_outbound_save);
//
//		outboundHolder.btnNo.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) 
//			{
//				dialog_outbound.dismiss();
//			}
//		});
//
//		outboundHolder.btnYes.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) 
//			{
//				sendOutboundList();
//				dialog_outbound.dismiss();
//			}
//		});
//
//		dialog_outbound.show();
//		dialog_outbound.setCancelable(false);
//		dialog_outbound.setCanceledOnTouchOutside(false);
////		RingProgressDialogManager.hide();
//
//	}
	

	@Override
	public void onResume() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.smartbean.servertalk.action.SENDING_OUTBOUND_PRIORITYLIST_CALLBACK");
		intentFilter.addAction("com.smartbean.servertalk.action.SENDING_INBOUND_BLOCKLIST_CALLBACK");
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
	
}
