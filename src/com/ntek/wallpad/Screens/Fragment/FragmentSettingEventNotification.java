package com.ntek.wallpad.Screens.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Pattern;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Database.DbHandler;
import com.ntek.wallpad.Database.DbHelper.EventInquiry;
import com.ntek.wallpad.Model.EventInquiryModel;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.SocClient;

public class FragmentSettingEventNotification extends Fragment
{
	private final static String TAG = FragmentSettingEventNotification.class.getCanonicalName();
	
	private View view;
	private Button btnSaveEvent;
//	private Button btnAllEvent;
//	private Button btnActiveEvent;
//	private Button btnPendingEvent;
//	private TextView tvTitleHeader;
	private ListView lvEventList;
	private ToggleButton etHttp;
	private EditText etAdd;
	private EditText etPort;
	private Button btnAll;
	private Button btnActive;
	private Button btnPending;
	private DbHandler mDbHandler;
	private String eventServerUrl = "";
	private int eventServerPort = -1;
	private String eventServerProtocol = "";
	
	private boolean onProcess = false;
	private String mDeviceName;
	
	private BroadcastReceiver mBroadCastReceiver;
	private EventInquiryAdapter mEventAdapter;
	private OnChangeFragmentListener mChangeFragment;
	
	public FragmentSettingEventNotification()
	{
		Log.i(TAG, "FragmentSettingEventNofication");
//		this.mDeviceName = deviceName;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			mChangeFragment = (OnChangeFragmentListener) activity; 
		} catch (ClassCastException e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mBroadCastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
//				if (action.equals("com.smartbean.servertalk.action.EVENT_INQUIRY_ERROR")) {
//					String error = intent.getStringExtra("error");
//					if (error == null)
//						error = "Undetermined";
//					Toast.makeText(getActivity(), "A following Error Occured " + error, Toast.LENGTH_LONG).show();
//				}
//				else if (action.equals("com.smartbean.setupeventinquiry.result"))
//				{ 
//					String response = intent.getStringExtra("result");
//					if (response.equals("success"))
//					{
//						String gcmID = intent.getStringExtra("gcmID");
//						Global.getInstance().getEventInquiryObservableList().deleteEventInquiry(gcmID);
//						mEventAdapter.notifyDataSetChanged();
//					}
//					else
//					{
//						Toast.makeText(getActivity(), "Sending to Server " + response, Toast.LENGTH_LONG).show();
//					}
//				}
				if (action.equals("com.smartbean.servertalk.action.EVENT_INQUIRY_ERROR")) {
					String error = intent.getStringExtra("error");
					Log.d(TAG, " error " + error );
					if (error == null)
						error = "Undetermined";
					Toast.makeText(getActivity(), "A following Error Occured " + error, Toast.LENGTH_LONG).show();
				}
				else if (action.equals("com.smartbean.servertalk.action.TCP_SEND_DEVICE_INFO_CALLBACK")) {
					String response = intent.getStringExtra("response");
					String error = intent.getStringExtra("error");
					Log.d(TAG, " error " + error );
					Log.d(TAG, " response " + response);
					if(response.equals("success")) {
//						DialogManager.showNotificationDialog(SetUPServerEventInquiry.this, true, "Save Successful");
						Toast.makeText(getActivity(), "Save Successful", Toast.LENGTH_LONG).show();
					}
					else {
//						DialogManager.showNotificationDialog(SetUPServerEventInquiry.this, false, response);
						Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
					}
				}
				RingProgressDialogManager.hide();
			}
		};
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_event_notification, container, false);
		initializeUI();
		return view;
	}
	
	protected void initializeUI() 
	{
//		tvTitleHeader = (TextView) view.findViewById(R.id.fragment_setting_event_notification_titleheader_tv);
		btnSaveEvent = (Button) view.findViewById(R.id.setting_button_event_notif);
		etHttp = (ToggleButton) view.findViewById(R.id.fragment_settings_event_et1);
		etAdd = (EditText) view.findViewById(R.id.fragment_settings_event_et2);
		etPort = (EditText) view.findViewById(R.id.fragment_settings_event_et3);
		btnAll = (Button) view.findViewById(R.id.fragment_setting_event_all);
		btnActive = (Button) view.findViewById(R.id.fragment_setting_event_active);
		btnPending = (Button) view.findViewById(R.id.fragment_setting_event_pending);
		
//		btnAllEvent = (Button) view.findViewById(R.id.setting_button_event_all);
//		btnActiveEvent = (Button) view.findViewById(R.id.setting_button_event_active);
//		btnPendingEvent = (Button) view.findViewById(R.id.setting_button_event_pending);
		lvEventList = (ListView) view.findViewById(R.id.setting_listview_event_notification);
		mDbHandler = DbHandler.open(getActivity());
		ArrayList<EventInquiryModel> mData = setToEventInquiryArray(mDbHandler.get_all_event_inquiry());
		mEventAdapter = new EventInquiryAdapter(getActivity(), mData, "All");
		lvEventList.setAdapter(mEventAdapter);
//		tvTitleHeader.setText("EVENT CLIENTS"); //FIXME CONVERT THIS TO @STRING RESOURCE 
		
//		etHttp.setEnabled(false);
		
		eventServerUrl = Global.getInstance().getEventServerUrl();
		eventServerPort = Global.getInstance().getEventServerPort();
		eventServerProtocol = Global.getInstance().getEventServerProtocol();
		
		if(eventServerProtocol == null) {
			eventServerProtocol = "http://";
		}
		
		etAdd.setText(eventServerUrl);
		// default port is 8080
		etPort.setText(Integer.toString(eventServerPort));
		Log.d(TAG, " eventServerUrl " + eventServerUrl);
		
		
		btnSaveEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
//				if (Global.getInstance().getEventInquiryObservableList().getSize() > 0)
//				{
//					new Thread(new SocClient("event_inquiry_submit_list_changes", CommonUtilities.soc_port, getActivity())).start();
//					RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
//				}
				
				
				final String url = etAdd.getText().toString().trim();
				final String port = etPort.getText().toString().trim();

				if (!validateInputs(url, port)) {
//					DialogManager.showNotificationDialog(SetUPServerEventInquiry.this, false, "Invalid URL");
					Toast.makeText(getActivity(), "Invalid URL", Toast.LENGTH_LONG).show();
					return;
				}

				if (url.equals(eventServerUrl)) {
					Log.d(TAG, " Please Wait ");
//					Global.getInstance().setEventServerProtocol(eventServerProtocol);
//					Global.getInstance().setEventServerUrl(url);
//					Global.getInstance().setEventServerPort(Integer.parseInt(port));
//					NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.EVENT_SERVER_URL, url);
//					NgnEngine.getInstance().getConfigurationService().putInt(NgnConfigurationEntry.EVENT_SERVER_PORT, Integer.parseInt(port));
//					NgnEngine.getInstance().getConfigurationService().commit();
//					RingProgressDialogManager.show(getActivity(), "Please Wait...","Server device connecting to Event Server...");
//					if (mDbHandler.check_if_data_exist()) {
//						new Thread(new SocClient("event_inquiry_submit_list_changes", CommonUtilities.soc_port)).start();
//						RingProgressDialogManager.show(getActivity(), "Loading",	"Sending Data To Server Device");
//					}
//					new Thread(	new SocClient("send_device_info", CommonUtilities.soc_port, getActivity())).start();
					
					Global.getInstance().setEventServerProtocol(eventServerProtocol);
					Global.getInstance().setEventServerUrl(url);
					Global.getInstance().setEventServerPort(Integer.parseInt(port));
					NgnEngine.getInstance().getConfigurationService()
							.putString(NgnConfigurationEntry.EVENT_SERVER_URL, url);
					NgnEngine.getInstance().getConfigurationService()
							.putInt(NgnConfigurationEntry.EVENT_SERVER_PORT, Integer.parseInt(port));
					NgnEngine.getInstance().getConfigurationService().commit();
					new Thread(new SocClient("send_device_info", CommonUtilities.soc_port, getActivity())).start();
					Log.d(TAG, " aaaaaaaaaaaaaaaaa ");
					new Thread(new SocClient("event_inquiry_submit_list_changes", CommonUtilities.soc_port)).start();
					Log.d(TAG, " bbbbbbbbbbbbbbbbbb ");
					RingProgressDialogManager.show(getActivity(), "Please Wait...",	"Server device connecting to Event Server...");
				}
				else 
				{
//					DialogManager.showOkCancelDialog(SetUPServerEventInquiry.this, DialogManager.HIDE_ICON, "Warning","Changing Event server details may cause loss of event history data.", "OK",
//							new View.OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							Global.getInstance().setEventServerProtocol(eventServerProtocol);
//							Global.getInstance().setEventServerUrl(url);
//							Global.getInstance().setEventServerPort(Integer.parseInt(port));
//							RingProgressDialogManager.show(SetUPServerEventInquiry.this, "Please Wait...",
//									"Server device connecting to Event Server...");
//							new Thread(new SocClient("send_device_info", CommonUtilities.soc_port,
//									SetUPServerEventInquiry.this)).start();
//							DialogManager.hideOkCancelDialog();
//						}
//
//					}, "Cancel", new View.OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							DialogManager.hideOkCancelDialog();
//						}
//
//					}, null);
					
					AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
					mBuilder.setMessage("Changing Event server details may cause loss of event history data").setTitle("EventInquiry")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Global.getInstance().setEventServerProtocol(eventServerProtocol);
									Global.getInstance().setEventServerUrl(url);
									Global.getInstance().setEventServerPort(Integer.parseInt(port));
									
									new Thread(new SocClient("send_device_info", CommonUtilities.soc_port,getActivity())).start();
									RingProgressDialogManager.show(getActivity(), "Please Wait...","Server device connecting to Event Server...");
									dialog.dismiss();
									RingProgressDialogManager.hide();
//									Toast.makeText(getActivity(), "Save!", Toast.LENGTH_LONG).show();
								}
								
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					mBuilder.show();
					RingProgressDialogManager.hide();
				}
				RingProgressDialogManager.hide();
				
				
				///////////////////////////////////////////////////
//				Log.d(TAG, " Sending Data To Server Device ");
//				if (mDbHandler == null)
//					mDbHandler = DbHandler.open(getActivity());
//				if (mDbHandler.check_if_data_exist()) {
//					new Thread(new SocClient("event_inquiry_submit_list_changes", CommonUtilities.soc_port)).start();
//					RingProgressDialogManager.show(getActivity(), "Loading",	"Sending Data To Server Device");
//				}
				
//				Toast.makeText(getActivity(), "Save!", Toast.LENGTH_LONG).show();
			}
		});
		
//		btnAllEvent.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				mEventAdapter.setData(Global.getInstance().getEventInquiryObservableList().getList(), "All");
//			}
//		});
//		
//		btnActiveEvent.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				mEventAdapter.setData(Global.getInstance().getEventInquiryObservableList().getApproved(), "Approved");
//			}
//		});
//		
//		btnPendingEvent.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				mEventAdapter.setData(Global.getInstance().getEventInquiryObservableList().getPending(), "Pending");
//			}
//		});
		
		btnAll.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "btnAllbtnAllbtnAll");
				mEventAdapter.setmAction("All");
				final ArrayList<EventInquiryModel> mData = setToEventInquiryArray(mDbHandler.get_all_event_inquiry());
				// if (mData != null)
				mEventAdapter.setData(mData, eventServerProtocol);
				
//				Log.d(TAG, " mdata " + mData );
				btnAll.setBackgroundResource(R.color.color_lightgray);
				btnPending.setBackgroundResource(R.color.transparent);
				btnActive.setBackgroundResource(R.color.transparent);
			}
		});
		
		
		btnActive.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "btnActivebtnActivebtnActive");
				mEventAdapter.setmAction("Approved");
				final ArrayList<EventInquiryModel> mData = setToEventInquiryArray(mDbHandler.get_all_aprroved_event_inquiry());
				mEventAdapter.setData(mData, eventServerProtocol);
				btnActive.setBackgroundResource(R.color.color_lightgray);
				btnPending.setBackgroundResource(R.color.transparent);
				btnAll.setBackgroundResource(R.color.transparent);
			}
		});
		
		
		btnPending.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "btnPendingbtnPending");
				mEventAdapter.setmAction("Pending");
				final ArrayList<EventInquiryModel> mData = setToEventInquiryArray(mDbHandler.get_all_pending_event_inquiry());
				mEventAdapter.setData(mData, eventServerProtocol);
				btnPending.setBackgroundResource(R.color.color_lightgray);
				btnActive.setBackgroundResource(R.color.transparent);
				btnAll.setBackgroundResource(R.color.transparent);
			}
		});
		
		
		List<EventInquiryModel> mList = Global.getInstance().getEventInquiryObservableList().getList();
		if (mList == null) mList = new ArrayList<EventInquiryModel>();
		mEventAdapter = new EventInquiryAdapter(getActivity(), mList, "All");
		lvEventList.setAdapter(mEventAdapter);
		
		btnAll.performClick();
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		final IntentFilter intentFilter = new IntentFilter("com.smartbean.servertalk.action.EVENT_INQUIRY_ERROR");
//		intentFilter.addAction("com.smartbean.setupeventinquiry.result");
//		getActivity().registerReceiver(mBroadCastReceiver, intentFilter);
		IntentFilter intentFilter = new IntentFilter("com.smartbean.servertalk.action.EVENT_INQUIRY_ERROR");
		intentFilter.addAction("com.smartbean.setupeventinquiry.result");
		intentFilter.addAction("com.smartbean.servertalk.action.TCP_SEND_DEVICE_INFO_CALLBACK");
		getActivity().registerReceiver(mBroadCastReceiver, intentFilter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mBroadCastReceiver != null)
		{
			getActivity().unregisterReceiver(mBroadCastReceiver);
		}
	}
	
	public class EventInquiryAdapter extends BaseAdapter implements Observer {

		private Context mContext;
		private List<EventInquiryModel> mEventHolder;

		private String mAction;

		public EventInquiryAdapter(Context context, List<EventInquiryModel> eventList) {
			this.mContext = context;
			this.mEventHolder = eventList;
			mDbHandler.addObserver(this);
		}

		public EventInquiryAdapter(Context context, List<EventInquiryModel> eventList, String action) {
			this.mContext = context;
			this.mAction = action;
			this.mEventHolder = eventList;
			mDbHandler.addObserver(this);
//			Global.getInstance().getEventInquiryObservableList().addObserver(this);
		}

		@Override
		public int getCount() {
			if (mEventHolder != null) return mEventHolder.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return mEventHolder.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Viewgroup viewHolder;
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.setting_event_notification_adapter, null);
				viewHolder = new Viewgroup();
				viewHolder.emailText = (TextView) convertView.findViewById(R.id.setting_event_textview_list);
				viewHolder.detailText = (TextView) convertView.findViewById(R.id.setting_event_textview_motion_status_list);
//				viewHolder.approvedBtn = (Button) convertView.findViewById(R.id.setting_event_button_pending);
				viewHolder.tgActivate = (Button) convertView.findViewById(R.id.setting_togglebutton_event_notification);
				viewHolder.discardBtn = (Button) convertView.findViewById(R.id.setting_event_button_discard);
				convertView.setTag(viewHolder);
			}
			else {
				viewHolder = (Viewgroup) convertView.getTag();
			}

			if (mEventHolder != null && mEventHolder.size() > 0) {
				viewHolder.emailText.setText(mEventHolder.get(position).getEmail());
				final int itemPosition = position;
//				viewHolder.emailText.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						final EventInquiryModel data = (EventInquiryModel) mEventHolder.get(itemPosition);
//						DialogFragment nDialog = DialogEventNotificationDetail.createInstance(data);
//						nDialog.show(getFragmentManager(), TAG);
//					}
//				});

				StringBuilder ability = new StringBuilder();
				if (mEventHolder.get(position).getMotion_detect_status().equals("enabled")) {
					Log.d(TAG, " " + mEventHolder);
					ability.append("Motion Detect ");
				}
				if (mEventHolder.get(position).getRelay_detect_status().equals("enabled")) {
					Log.d(TAG, " " + mEventHolder + " 1 ");
					ability.append("Door Control ");
				}
				Log.d(TAG, "ability " + ability + " **** " + (mEventHolder.get(position).getMotion_detect_enable()));
//				viewHolder.tgActivate.setOnCheckedChangeListener(new OnCheckedClick(mEventHolder.get(position).getGcmID(), mEventHolder.get(position).getActiveStatus()));
//				viewHolder.discardBtn.setOnClickListener(new OnButtonClick(mEventHolder.get(position).getGcmID(), mEventHolder.get(position).getActiveStatus()));
				
//				if (mEventHolder.get(position).getActiveStatus().equals("active"))
//				{
//					viewHolder.tgActivate.setChecked(true);
//				}
//				else
//				{
//					viewHolder.tgActivate.setChecked(false);
//				}
				
				viewHolder.detailText.setText(ability.toString());

				if (mAction.equals("All")) { // for All tabs
					String status = mEventHolder.get(position).getStatus();
					if (status.equals("active")) {
						viewHolder.tgActivate.setText("Deactivate");
						viewHolder.tgActivate.setOnClickListener(new OnButtonClick(mEventHolder.get(position).getGcmID(), "inactive"));
						viewHolder.discardBtn.setText(getString(R.string.Event_Inquiry_Discard));
						viewHolder.discardBtn.setOnClickListener(new OnButtonClick(mEventHolder.get(position).getGcmID(),
								"discard"));
					}
					else {
						viewHolder.tgActivate.setText("Activate");
						viewHolder.discardBtn.setText(getString(R.string.Event_Inquiry_Discard));
						viewHolder.tgActivate.setOnClickListener(new OnButtonClick(	mEventHolder.get(position).getGcmID(), "active"));
						viewHolder.discardBtn.setOnClickListener(new OnButtonClick(mEventHolder.get(position).getGcmID(),
								"discard"));
					}
				}
				else if (mAction.equals("Approved")) { // for Approved tabs
					viewHolder.tgActivate.setText("Deactivate");
					viewHolder.tgActivate.setOnClickListener(new OnButtonClick(mEventHolder.get(position).getGcmID(),
							"inactive"));
					viewHolder.discardBtn.setText(getString(R.string.Event_Inquiry_Discard));
					viewHolder.discardBtn.setOnClickListener(new OnButtonClick(mEventHolder.get(position).getGcmID(),
							"discard"));
				}
				else if (mAction.equals("Pending")) { // for pending tabs
					viewHolder.tgActivate.setText("Activate");
					viewHolder.tgActivate.setOnClickListener(new OnButtonClick(mEventHolder.get(position).getGcmID(),
							"active"));
					viewHolder.discardBtn.setText(getString(R.string.Event_Inquiry_Discard));
					viewHolder.discardBtn.setOnClickListener(new OnButtonClick(mEventHolder.get(position).getGcmID(),
							"discard"));
				}
			}
			return convertView;
		}

		public void setData(List<EventInquiryModel> list, String action) {
//			this.mEventHolder = list;
//			this.mAction = action;
//			this.notifyDataSetChanged();
			
			if (list == null)
				mEventHolder = null;
			mDbHandler.addObserver(this);
			this.mEventHolder = list;
			this.notifyDataSetChanged();
		}

		public String getmAction() {
			return mAction;
		}

		public void setmAction(String mAction) {
			this.mAction = mAction;
		}

		@Override
		public void update(Observable observable, Object data) {
			// TODO Auto-generated method stub
			if (mAction.equals("All")) {
//				mEventHolder = Global.getInstance().getEventInquiryObservableList().getList();
				mEventHolder = setToEventInquiryArray(mDbHandler.get_all_event_inquiry());
			}
			else if (mAction.equals("Approved")) {
//				mEventHolder = Global.getInstance().getEventInquiryObservableList().getApproved();
				mEventHolder = setToEventInquiryArray(mDbHandler.get_all_aprroved_event_inquiry());
			}
			else {
//				mEventHolder = Global.getInstance().getEventInquiryObservableList().getPending();
				mEventHolder = setToEventInquiryArray(mDbHandler.get_all_pending_event_inquiry());
			}
			this.notifyDataSetChanged();
		}
		
		private class OnCheckedClick implements OnCheckedChangeListener
		{
			private String mStatus;
			private String mGcmID;

			public OnCheckedClick(String mGcmID, String status) {
				this.mGcmID = mGcmID;
				this.mStatus = status;
			}

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (Global.getInstance().getEventInquiryObservableList() != null)
				{
					if (isChecked)
					{
						Global.getInstance().getEventInquiryObservableList().changeStatus(mGcmID, "active");
					}
					else
					{
						Global.getInstance().getEventInquiryObservableList().changeStatus(mGcmID, "inactive");
					}
				}
				Global.getInstance().setClient_inquiry_gcmID(this.mGcmID);
				Global.getInstance().setClient_inquiry_status(this.mStatus);
//				new Thread(new SocClient("event_inquiry_client_list_update", CommonUtilities.soc_port)).start();
			}
			
		}
		
		private class OnButtonClick implements OnClickListener {
			private String mStatus;
			private String mGcmID;

			public OnButtonClick(String mGcmID, String status) {
				this.mGcmID = mGcmID;
				this.mStatus = status;
			}

			@Override
			public void onClick(View v) {
				Log.d(TAG, " qweqeqwe,mqnwem,qwnem,n");
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				String nowDate = dateFormat.format(date);
				if (this.mStatus.equals("discard")) 
				{
				showDiscardAlertDialog(this.mGcmID, mStatus);
				}
				else
				{
					mDbHandler.update_event_inquiry_status(this.mGcmID, this.mStatus, nowDate);
					Global.getInstance().setClient_inquiry_gcmID(this.mGcmID);
					Global.getInstance().setClient_inquiry_status(this.mStatus);
				}
			}
		}
	}
	
	
	private void showDiscardAlertDialog(final String mGcmID, final String mStatus) {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		mBuilder.setMessage("Are you sure you want to discard this request?").setTitle("EventInquiry")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Global.getInstance().setClient_inquiry_gcmID(mGcmID);
						Global.getInstance().setClient_inquiry_status(mStatus);
						new Thread(new SocClient("event_inquiry_delete_event_inquiry_list", CommonUtilities.soc_port, getActivity())).start();
						RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		mBuilder.show();
	}

	private class Viewgroup {
		private TextView emailText;
		private TextView detailText;
//		private Button approvedBtn;
		private Button discardBtn;
		private Button tgActivate;
	}
	
	public static class DialogEventNotificationDetail extends DialogFragment implements Observer
	{
		protected View mView;
		
		private ToggleButton motionDetectTB;
		private ToggleButton doorControlTB;
		private Button okButton;
		private TextView userStatusTV;
		private TextView titleTV;
		
		private EventInquiryModel nModel;
		private String nUserEmail;
		private String nCurrentMotion_Detect_Status;
		private String nPre_CurrentMotion_Detect_Status;
		private String nCurrentDoorControl_Detect_Status;
		private String nPre_CurrentDoorControl_Detect_Status;
		private String nCurrentStatus;
		
		private DialogEventNotificationDetail(EventInquiryModel model)
		{
			this.nModel = model;
		}
		
		public static DialogEventNotificationDetail createInstance(EventInquiryModel model)
		{
			DialogEventNotificationDetail nDialog = new DialogEventNotificationDetail(model);
			return nDialog;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			if (nModel != null)
			{
				nUserEmail = nModel.getEmail();
				nPre_CurrentMotion_Detect_Status = nModel.getMotion_detect_enable();
				nPre_CurrentDoorControl_Detect_Status = nModel.getRelay_sensors_enable();
				nCurrentStatus = nModel.getActiveStatus();
				
				nCurrentMotion_Detect_Status = nPre_CurrentMotion_Detect_Status;
				nCurrentDoorControl_Detect_Status = nPre_CurrentDoorControl_Detect_Status;
			}
		
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
			mBuilder.setCancelable(true);
			initializeUI();
			mBuilder.setView(mView);
			return mBuilder.create();
		}
		
		protected void initializeUI()
		{
			LayoutInflater inflater = getActivity().getLayoutInflater();
			mView = inflater.inflate(R.layout.dialog_setting_event_notification_detail, null);
			
			titleTV = (TextView) mView.findViewById(R.id.dialog_setting_event_notification_detail_title_tv);
			motionDetectTB = (ToggleButton) mView.findViewById(R.id.dialog_setting_event_notification_detail_toggle_motion_detection);
			doorControlTB = (ToggleButton) mView.findViewById(R.id.dialog_setting_event_notification_detail_toggle_door_control);
			userStatusTV = (TextView) mView.findViewById(R.id.dialog_setting_event_notification_detail_status_tv);
			okButton = (Button) mView.findViewById(R.id.dialog_setting_event_notification_detail_button_ok);
			
			titleTV.setText(nUserEmail);
			userStatusTV.setText(nCurrentStatus);
			
			if (nPre_CurrentMotion_Detect_Status.equals("enabled"))
			{
				motionDetectTB.setChecked(true);
			}
			else
			{
				motionDetectTB.setChecked(false);
			}
			
			if (nPre_CurrentDoorControl_Detect_Status.equals("enabled"))
			{
				doorControlTB.setChecked(true);
			}
			else
			{
				doorControlTB.setChecked(false);
			}
			
			motionDetectTB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
					if (isChecked)
					{
						nCurrentMotion_Detect_Status = "enabled";
					}
					else 
					{
						nCurrentMotion_Detect_Status = "disabled";
					}
				}
			});
			
			doorControlTB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked)
					{
						nCurrentDoorControl_Detect_Status = "enabled";
					}
					else
					{
						nCurrentDoorControl_Detect_Status = "disabled";
					}
				}
			});
			
			okButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (nModel != null)
					{
						nModel.setMotion_detect_enable(nCurrentMotion_Detect_Status);
						nModel.setRelay_sensors_enable(nCurrentDoorControl_Detect_Status);
					}
				}
			});
		}

		@Override
		public void update(Observable observable, Object data) 
		{
			
		}
	} //DialogEventNotificationDetail
	
	
	private ArrayList<EventInquiryModel> setToEventInquiryArray(Cursor cursor) {
		ArrayList<EventInquiryModel> eventInquiry = null;

		if (cursor != null) {
			Log.d(TAG, " eventInquiry " + eventInquiry + " cursor " + cursor.moveToFirst());
			if (cursor.moveToFirst()) {
				Log.d(TAG, " eventInquiry33 " + eventInquiry + " cursor " + cursor.getInt(cursor.getColumnIndex(EventInquiry.FIELD_EVENTINQUIRY_ID)));
				eventInquiry = new ArrayList<EventInquiryModel>();
				do {
					EventInquiryModel data = new EventInquiryModel();
					
					data.setEventID(cursor.getInt(cursor.getColumnIndex(EventInquiry.FIELD_EVENTINQUIRY_ID)));
					data.setGcmID(cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_CLIENTGCMID)));
					data.setEmail(cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_CLIENTEMAIL)));
					data.setMotion_detect_status(cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_MOTIONDETECT_STATUS)));
					data.setRelay_detect_status(cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_RELAYSENSOR_STATUS)));
					data.setCreated_date(cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_CREATEDDATE)));
					data.setStatus(cursor.getString(cursor.getColumnIndex(EventInquiry.FIELD_STATUS)));

					eventInquiry.add(data);
				}
				while (cursor.moveToNext());
				Log.d(TAG, " eventInquiry22 " + eventInquiry + " cursor " + cursor);
			}
		}
		return eventInquiry;
	}
	
	
//	private class OnButtonClick implements OnClickListener {
//		private String mGcmID;
//		private String mStatus;
//
//		public OnButtonClick(String gcmID, String status) {
//			this.mGcmID = gcmID;
//			this.mStatus = status;
//		}
//
//		@Override
//		public void onClick(View v) {
//			System.out.println("onClick(View v) { " + mStatus);
//			if (!onProcess) {
//				onProcess = true;
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				Date date = new Date();
//				String nowDate = dateFormat.format(date);
//				if (this.mStatus.equals("discard")) {
////					DialogManager.showOkCancelDialog(SetUPServerEventInquiry.this, DialogManager.HIDE_ICON, "Discard",
////							"Are you sure you want to discard this request?", "OK", new View.OnClickListener() {
////								@Override
////								public void onClick(View v) {
////									Global.getInstance().setClient_inquiry_gcmID(mGcmID);
////									Global.getInstance().setClient_inquiry_status(mStatus);
////
////									new Thread(new SocClient("event_inquiry_delete_event_inquiry_list",
////											CommonUtilities.soc_port)).start();
////									DialogManager.hideOkCancelDialog();
////									RingProgressDialogManager.show(SetUPServerEventInquiry.this, "Loading",
////											"Sending Data To Server Device");
////								}
////							}, "Cancel", new View.OnClickListener() {
////								@Override
////								public void onClick(View v) {
////									DialogManager.hideOkCancelDialog();
////								}
////							}, null);
//					
//					AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
//					mBuilder.setMessage("Are you sure you want to discard this request?").setTitle("EventInquiry")
//							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									Global.getInstance().setClient_inquiry_gcmID(mGcmID);
//									Global.getInstance().setClient_inquiry_status(mStatus);
//									new Thread(new SocClient("event_inquiry_delete_event_inquiry_list", CommonUtilities.soc_port, getActivity())).start();
//									RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
//								}
//							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									dialog.cancel();
//								}
//							});
//					mBuilder.show();
//				}
//				else {
//					mDbHandler.update_event_inquiry_status(this.mGcmID, this.mStatus, nowDate);
//					Global.getInstance().setClient_inquiry_gcmID(this.mGcmID);
//					Global.getInstance().setClient_inquiry_status(this.mStatus);
//					// new Thread(new SocClient("event_inquiry_client_list_update", CommonUtilities.soc_port)).start();
//				}
//				onProcess = false;
//			}
//		}
//	}
	private boolean validateInputs(String url, String port) {
		final String urlRegex = "^(http?|https?):\\/\\/([^:\\/\\s]+)";
		final String portRegex = "^[0-9]{1,5}$";

		if (url.equals("")) {
			Log.d(TAG, "The value of url is empty");
			return false;
		}

		if (port.equals("")) {
			Log.d(TAG, "The value of port is empty");
			return false;
		}

		if (!Pattern.matches(urlRegex, eventServerProtocol + url)) {
			Log.d(TAG, "Invalid type of url : " + eventServerProtocol + url);
			return false;
		}

		if (!Pattern.matches(portRegex, String.valueOf(port))) {
			Log.d(TAG, "Invalid type of port :  " + port);
			return false;
		}

		if (Integer.parseInt(port) < 0 || Integer.parseInt(port) > 65535) {
			Log.d(TAG, "Invalid value of port range : " + port);
			return false;
		}

		return true;
	}
}