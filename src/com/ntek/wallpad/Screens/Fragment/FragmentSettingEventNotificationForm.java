package com.ntek.wallpad.Screens.Fragment;

import static com.ntek.wallpad.Utils.CommonUtilities.SENDER_ID;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gcm.GCMRegistrar;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Database.WallpadDatabaseDao;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingEmailNotification.EmailFormatValidator;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.SocClient;

public class FragmentSettingEventNotificationForm extends Fragment implements
		OnClickListener {
	private static final String TAG = FragmentSettingEventNotificationForm.class
			.getCanonicalName();

	public static String DOORCONTROL_DETECT_KEY = "DOORCONTROL_DETECT_KEY";
	public static String MOTION_DETECT_KEY = "MOTION_DETECT_KEY";
	public static String CLIENT_OS_TYPE_ANDROID = "a";

	private View view;

//	private Button btnSaveMyInfo;
	private Button btnSave;
	private ToggleButton tglbtnMotionDetect;
	private ToggleButton tglbtnDoorControl;
	private Button btnSubmit;
	private String email;
	private Button btnDelete;

//	private TextView tvEnableDisableMotionDectect;
//	private TextView tvDeviceName;
//	private TextView tvEventStatus;

	private EditText etEmailInfo;

	private BroadcastReceiver broadCastReceiver;

	private String mRegID;
	private String clientStatus = "";
	private String doorcontrolOnoff = "";
	private String motionOnoff = "";
	
	private String saveMotionStatus = "";
	private String saveDoorStatus = "";
	
	private boolean btnsave_accounts_notification_chk;
	private AsyncTask<Void, Void, Boolean> mRegisterTask;
	
	private String motion_onoff = "";
	private String doorcontrol_onoff = "";
	private String client_status = "";
	private TextView approvalStatusText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_event_notification_form, container, false);

		initializeUI();

		return view;
	}
	
	private void initializeUI() {

		checkIfRegistered();
		btnSave = (Button) view.findViewById(R.id.event_setting_button_save);
		tglbtnMotionDetect = (ToggleButton) view.findViewById(R.id.setting_togglebutton_motion_detect);
		tglbtnDoorControl = (ToggleButton) view.findViewById(R.id.setting_togglebutton_door_control);
		btnSubmit = (Button) view.findViewById(R.id.fragment_setting_event_notification_form_button_submit);
		approvalStatusText = (TextView) view.findViewById(R.id.approval_status_txt);
		btnDelete = (Button) view.findViewById(R.id.fragment_setting_event_notification_form_button_delete);
		btnDelete.setVisibility(View.INVISIBLE);
//		tvEventStatus = (TextView) view
//				.findViewById(R.id.fragment_setting_event_notification_form_textview_title_status);
//		tvDeviceName = (TextView) view.findViewById(R.id.fragment_setting_event_notification_form_my_device_textview_user);
		
		etEmailInfo = (EditText) view.findViewById(R.id.setting_edittext_email_info);

		broadCastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				WallpadDatabaseDao cldb = new WallpadDatabaseDao(getActivity().getBaseContext());
				Log.e(TAG, "onReceive() : " + action);
				if (action.equals("com.smartbean.setupeventinquiry.register.result")) 
				{
					final String result = intent.getStringExtra("result");
					final String gcmID = Global.getInstance().getRegisterId();
					final String server_event_inquiry_id = intent.getStringExtra("event_inquiry_id");
					final String unique_device_key = intent.getStringExtra("unique_device_key");

					SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
					String date = dateformat.format(new Date());

					Log.d(TAG, "result : " + result);
					if (result.equals("success")) {Toast.makeText(getActivity().getBaseContext(),"Update Success", Toast.LENGTH_LONG).show();
						cldb.Open();
						Log.d(TAG, "insert(" + unique_device_key + ", " + gcmID + ", " + date + ", " + server_event_inquiry_id);
						cldb.insertEventInquiry(unique_device_key, gcmID, date, server_event_inquiry_id);
						cldb.Close();
					} else if (result.equals("failed")) {
						String message = getString(R.string.string_registration_update_failed);
						Toast.makeText(getActivity(), message,Toast.LENGTH_LONG).show();
					}
					RingProgressDialogManager.hide();
				} else if (action.equals("com.smartbean.setupeventinquiry.sync.end")) {
					String result = intent.getStringExtra("result");
					String unique_device_key = Global.getInstance().getServer_unique_device_id();
					String gcmId = Global.getInstance().getRegisterId();
					RingProgressDialogManager.hide();

					cldb.Open();
					Log.d(TAG, "result : " + result);
					if (result.equals("success")) {
						if (!cldb.inquiryExist(unique_device_key)) {
							new Thread(new SocClient("event_inquiry_delete_event_inquiry",CommonUtilities.soc_port, getActivity())).start();
							RingProgressDialogManager.show(getActivity());
						}
					} else if (result.equals("failed")) {
						Log.d(TAG, "else if (result.equals == failed");
						if (cldb.inquiryExist(unique_device_key)) {
							Log.d(TAG, "if (cldb.inquiryExist(unique_device_key)) {");
							displayDiscardedNotificationAlertDialog(getActivity());
						}
						cldb.deleteInquiry(unique_device_key, gcmId);
					} else if (result.equals("initial")) {
						Log.d(TAG, "Initial");
					}
					cldb.Close();

				} else if (action.equals("com.smartbean.setupeventinquiry.delete.result")) {
					String result = intent.getStringExtra("result");
					String unique_device_key = Global.getInstance().getServer_unique_device_id();
					String gcmId = Global.getInstance().getRegisterId();

					RingProgressDialogManager.hide();

					Log.d(TAG, "result : " + result);
					if (result.equals("success")) {
						cldb.Open();
						cldb.deleteInquiry(unique_device_key, gcmId);
						cldb.Close();

						clientStatus = "Initial";
						Global.getInstance().setClient_inquiry_status(clientStatus);
						Global.getInstance().set_Doorcontrol_onoff(doorcontrolOnoff);
						Global.getInstance().setMotionclient_onoff(motionOnoff);
					} else if (result.equals("failed")) {
						// do nothing
					} else {
						Toast.makeText(getActivity(), result,Toast.LENGTH_LONG).show();
					}

				} else if (action.equals("com.smartbean.setupeventinquiry.result")) {String result = intent.getStringExtra("result");

					Log.d(TAG, "result : " + result);
					if (result.equals("success")) {
						Toast.makeText(getActivity(),"Update Success", Toast.LENGTH_LONG).show();
					} 
					else 
					{
						Toast.makeText(getActivity(),"Update Failed", Toast.LENGTH_LONG).show();
					}
					RingProgressDialogManager.hide();
				} else if (action.equals("com.smartbean.servertalk.action.EVENT_INQUIRY_ERROR")) {
					String error = intent.getStringExtra("error");
					if (error == null)
						error = "Undetermined";
					Toast.makeText(getActivity(),"A following Error Occured: " + error,Toast.LENGTH_LONG).show();
					RingProgressDialogManager.hide();
				}
				updateUIScreen();
			}
		};
		
		etEmailInfo.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String newText = etEmailInfo.getText().toString();
				if (!client_status.equals("Initial")) {
					if(newText.equals(email)) {
//						updateInformationButton.setVisibility(View.INVISIBLE);
						btnSubmit.setVisibility(View.GONE);
					}
					else {
//						updateInformationButton.setVisibility(View.VISIBLE);
						btnSubmit.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		
		tglbtnMotionDetect.setOnCheckedChangeListener(onCheckChangeMotionDetect);
		tglbtnDoorControl.setOnCheckedChangeListener(onCheckChangeDoorControl);
		btnSubmit.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
	}
	
	private OnCheckedChangeListener onCheckChangeMotionDetect = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked) 
			{
				saveMotionStatus = "enabled";
			}
			else
			{
				saveMotionStatus = "disabled";
			}
			
			Global.getInstance().setMotionclient_onoff(saveMotionStatus);

			NgnEngine.getInstance().getConfigurationService().putString(MOTION_DETECT_KEY, saveMotionStatus);
			NgnEngine.getInstance().getConfigurationService().commit();
		}
	};

	private OnCheckedChangeListener onCheckChangeDoorControl = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (isChecked)
			{
				saveDoorStatus = "enabled";
			}
			else
			{
				saveDoorStatus = "disabled";
			}
			
			
			Global.getInstance().set_Doorcontrol_onoff(saveDoorStatus);
			
			NgnEngine.getInstance().getConfigurationService()
					.putString(DOORCONTROL_DETECT_KEY, saveDoorStatus);
			NgnEngine.getInstance().getConfigurationService().commit();
		}
	};
	
	private void checkIfRegistered() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				mRegID = GCMRegistrar.getRegistrationId(getActivity());
				Log.d(TAG, "mRegID: " + mRegID);
				if (mRegID == null || mRegID.equals("")) {
					GCMRegistrar.register(getActivity(), SENDER_ID);
					mRegID = GCMRegistrar.getRegistrationId(getActivity());
					Log.d(TAG, ">> mRegID: " + mRegID);
					if (mRegID != null) {
						Global.getInstance().setRegisterId(mRegID);
						return true;
					}
				}
				else {
					Global.getInstance().setRegisterId(mRegID);
					return true;
				}
				return false;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				Log.d(TAG, "checkifRegistered - onPostExecute(" + result + ")");
				if(result) {
					onSync();
				} else {
					displayGcmEmailNotificationAlertDialog("Please register a google account on your Device, or re-run the application");
//						new DialogInterface.OnDismissListener() {
//							@Override
//							public void onDismiss(DialogInterface dialog) {
//								back();
//							}
//						};
				}
			};
		}.execute();
	}
	
	// CHECK IF THIS HAVE GCMID
//	private void checkIfRegistered() {
//		try {
//			GCMRegistrar.checkDevice(getActivity());
//			GCMRegistrar.checkManifest(getActivity());
//		} catch (UnsupportedOperationException e) {
//			Log.d(TAG, "This device does not support GCM");
//			displayGcmEmailNotificationAlertDialog("This device does not support GCM");
//			return;
//		} catch (IllegalStateException c) {
//			Log.d(TAG, "Android Manifest File is not properly configured");
//			return;
//		}
//		finally
//		{
//			mRegisterTask = new AsyncTask<Void, Void, Boolean>() {
//				@Override
//				protected void onPreExecute() 
//				{
////					showDialog(getActivity(), "Registering your Device", "Please Wait");
//				};
//				
//				@Override
//				protected Boolean doInBackground(Void... params) {
//						mRegID = GCMRegistrar.getRegistrationId(getActivity());
//						Log.d(TAG, "mRegID: " + mRegID);
//						if (mRegID == null || mRegID.equals("")) 
//						{
//							GCMRegistrar.register(getActivity(), SENDER_ID);
////							while(true)
////							{
////								if (getActivity() == null || GCMRegistrar.isRegistered(getActivity()))
////								{
////									break;
////								}
////								//wait for the registeredID
//////								Log.d(TAG, "Registering GCMID in server");
////							}
//							mRegID = GCMRegistrar.getRegistrationId(getActivity());
//							Log.d(TAG, ">> mRegID: " + mRegID);
//							if (mRegID != null && !mRegID.equals("")) 
//							{
//								Global.getInstance().setRegisterId(mRegID);
//								return true;
//							}
//						} 
//						else 
//						{
//							Global.getInstance().setRegisterId(mRegID);
//							return true;
//						}
//					return false;
//				}
//				
//
//				@Override
//				protected void onPostExecute(Boolean result) {
//					Log.d(TAG, "checkifRegistered - onPostExecute(" + result + ")");
//					if (result) {
//						onSync();
//					} 
//					else 
//					{
//						displayGcmEmailNotificationAlertDialog("Cannot registered your device in GCMServer....\n " +
//								"Please Register a E-Mail on your Device");
//					}
//				};
//			}.execute();
//		}
//	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		if (mRegisterTask != null)
		{
			mRegisterTask.cancel(true);
		}
	}

	private void displayDiscardedNotificationAlertDialog(Context context) {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
		mBuilder.setMessage("This request was discarded by Administrator")
				.setTitle("Notification")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		Dialog mDialog = mBuilder.create();
		mDialog.show();
	}

	public void onSync() {
		Log.d(TAG, "Sync");
		new Thread(new SocClient("event_inquiry_get_client_data",
				CommonUtilities.soc_port, getActivity())).start(); // get data to the server
		RingProgressDialogManager.show(getActivity(),
				getString(R.string.Loading),
				getString(R.string.string_client_event_inquiry_data_loading));
	}

	private void displayGcmEmailNotificationAlertDialog(String message) {
		Log.d(TAG, "displayAlertDialog");
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		mBuilder.setMessage(message)
				.setTitle("Event Inquiry")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						getFragmentManager().popBackStack();
					}
				});
		Dialog mDialog = mBuilder.create();
		mDialog.show();
	}

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.smartbean.setupeventinquiry.register.result");
		intentFilter.addAction("com.smartbean.setupeventinquiry.sync.end");
		intentFilter.addAction("com.smartbean.setupeventinquiry.delete.result");
		intentFilter.addAction("com.smartbean.setupeventinquiry.result");
		intentFilter.addAction("com.smartbean.servertalk.action.EVENT_INQUIRY_ERROR");
		getActivity().registerReceiver(broadCastReceiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (broadCastReceiver != null)
			getActivity().unregisterReceiver(broadCastReceiver);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.fragment_setting_event_notification_form_button_submit:
//			saveEmailDetails();
//			sendInformation();
			
			String input = etEmailInfo.getText().toString();
			
			if (CommonUtilities.checkIfEmpty(input)) {
//				ToastManager.showToast(getActivity(), "Please fill up the email for sending the notifications", Toast.LENGTH_LONG);
				Toast.makeText(getActivity(), "Please fill up the email for sending the notifications", Toast.LENGTH_LONG).show();
				Log.d(TAG, "1111111111111111111111");
			}
			else if (!EmailFormatValidator.getInstance().validate(input)){
//				ToastManager.showToast(getActivity(), "Please enter a valid email", Toast.LENGTH_LONG);
				Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_LONG).show();
				Log.d(TAG, "22222222222222222222");
			}
			else
			{
				if(client_status.equals("active") || client_status.equals("inactive"))
				{
					Log.d(TAG, " akldjaskldjkdjilw ");
					
					if (CommonUtilities.checkIfEmpty(input)) {
//						ToastManager.showToast(SetUpClientEventInquiryDetail.this, "Please fill up the email for sending the notifications", Toast.LENGTH_LONG);
						Toast.makeText(getActivity(), "Please fill up the email for sending the notifications", Toast.LENGTH_LONG).show();
					}
					else if (!EmailFormatValidator.getInstance().validate(input)){
//						ToastManager.showToast(SetUpClientEventInquiryDetail.this, "Please enter a valid email", Toast.LENGTH_LONG);
						Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_LONG).show();
					}
					else
					{
						NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.IDENTITY_CLIENT_EMAIL, etEmailInfo.getText().toString());
						NgnEngine.getInstance().getConfigurationService().commit();

						Global.getInstance().setClient_inquiry_status(client_status);
						Global.getInstance().set_Doorcontrol_onoff(doorcontrol_onoff);
						Global.getInstance().setMotionclient_onoff(motion_onoff);
						
						Thread cThread = new Thread(new SocClient("event_inquiry_email_update", CommonUtilities.soc_port));
						cThread.start();

						RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
					}
					
				}
				else
				{
					Log.d(TAG, "Inquire111111");
					
					NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.IDENTITY_CLIENT_EMAIL, etEmailInfo.getText().toString());
					NgnEngine.getInstance().getConfigurationService().commit();
					client_status = "inactive";
					motion_onoff = tglbtnMotionDetect.isChecked() ? "enabled" : "disabled";
					doorcontrol_onoff = tglbtnDoorControl.isChecked() ? "enabled" : "disabled";
					Global.getInstance().setClient_inquiry_status(client_status);
					Global.getInstance().set_Doorcontrol_onoff(doorcontrol_onoff);
					Global.getInstance().setMotionclient_onoff(motion_onoff);
					Global.getInstance().setClient_os_type(CLIENT_OS_TYPE_ANDROID);
					Global.getInstance().setRegisterId(mRegID);

					Thread cThread = new Thread(new SocClient("event_inquiry", CommonUtilities.soc_port));
					cThread.start();
					RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
				}
			}
//			sendEventInq();
			break;
			
		case R.id.fragment_setting_event_notification_form_button_delete:
			sendEventInq();
			break;
			
		case R.id.event_setting_button_save:
//qweqeqwe
			saveNotifation();
			break;

		default:
			break;
		}
	}

	private void saveNotifation() 
	{
		System.out.println("SAVING...");
		System.out.println("@SAVENOTIF values of motion and doorcontrol "+ "motionOnoff " + motionOnoff + " saveDoorStatus " + saveDoorStatus);
		
		Global.getInstance().setRegisterId(mRegID);
		Thread cThread = new Thread(new SocClient("event_inquiry_motion_door_update", CommonUtilities.soc_port, getActivity()));
		cThread.start();
		RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
	}


	private void saveEmailDetails() 
	{
		String input = etEmailInfo.getText().toString();
		
		if (CommonUtilities.checkIfEmpty(input)) {
			Toast.makeText(getActivity(), "Please fill up the email for sending the notifications", Toast.LENGTH_LONG).show();
		}
		else if (!EmailFormatValidator.getInstance().validate(input)){
			Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_LONG).show();
		}
		
		NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.IDENTITY_CLIENT_EMAIL, etEmailInfo.getText().toString());
		NgnEngine.getInstance().getConfigurationService().commit();

		Global.getInstance().setClient_inquiry_status(clientStatus);
		
		Thread cThread = new Thread(new SocClient("event_inquiry_email_update", CommonUtilities.soc_port, getActivity()));
		cThread.start();

		RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
	
	}

	private void sendInformation() 
	{
		String input = etEmailInfo.getText().toString();
		
		// stop process if device failed to get gcm id
		if (!GCMRegistrar.isRegistered(getActivity())){
			Toast.makeText(getActivity(), "Please Wait while we register your device", Toast.LENGTH_LONG).show();
			return ;
		}
		
		if (CommonUtilities.checkIfEmpty(input) || !EmailFormatValidator.getInstance().validate(input)) 
		{
			Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_LONG).show();
		}
		else if (btnsave_accounts_notification_chk == false) 
		{
			btnsave_accounts_notification_chk = true;
			if (clientStatus.equals("inactive") || clientStatus.equals("active")) {
				showDiscardAlertDialog();
			}
			else 
			{
				Log.d(TAG, "Inquire");
				NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.IDENTITY_CLIENT_EMAIL, etEmailInfo.getText().toString());
				NgnEngine.getInstance().getConfigurationService().commit();

				clientStatus = "inactive";
				Global.getInstance().setClient_inquiry_status(clientStatus);
//				Global.getInstance().set_Doorcontrol_onoff(");
//				Global.getInstance().setMotionclient_onoff(motionOnoff);
				Global.getInstance().setClient_os_type(CLIENT_OS_TYPE_ANDROID);
				Global.getInstance().setRegisterId(mRegID);

				Thread cThread = new Thread(new SocClient("event_inquiry", CommonUtilities.soc_port, getActivity()));
				cThread.start();
				
				RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");	
			}
		}
	}
	
	private void showDiscardAlertDialog() {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		mBuilder.setMessage("Are you sure you want to discard this request?").setTitle("EventInquiry")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Discard");
						
						new Thread(new SocClient("event_inquiry_delete_event_inquiry", CommonUtilities.soc_port, getActivity())).start();
						RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
						dialog.dismiss();
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		mBuilder.show();
	}
	
	
	public void updateUIScreen() {
		motion_onoff = Global.getInstance().getMotionclient_onoff();
		doorcontrol_onoff = Global.getInstance().get_Doorcontrol_onoff();
		client_status = Global.getInstance().getClient_inquiry_status();
		
		approvalStatusText.setText("");
		etEmailInfo.setText("");
		
		if (client_status.equals("active")) {
			approvalStatusText.setTextColor(Color.GREEN);
//			btnSubmit.setBackgroundResource(R.drawable.ic_delete_bluegray);
			btnDelete.setVisibility(View.VISIBLE);
			btnSubmit.setVisibility(View.GONE);
			approvalStatusText.setText(getString(R.string.Status_approved));
		}
		else if (client_status.equals("inactive")) {
			approvalStatusText.setTextColor(Color.BLUE);
//			btnSubmit.setBackgroundResource(R.drawable.ic_delete_bluegray);
			btnSubmit.setVisibility(View.GONE);
			btnDelete.setVisibility(View.VISIBLE);
			approvalStatusText.setText(getString(R.string.Status_approval));
		}
		else {
//			btnSubmit.setBackgroundResource(R.drawable.ic_save_grey_diskette_normal);
//			updateInformationButton.setVisibility(View.INVISIBLE);
//			updateNotificationButton.setVisibility(View.INVISIBLE);
			btnDelete.setVisibility(View.INVISIBLE);
			btnSubmit.setVisibility(View.VISIBLE);
			client_status = "Initial";
			approvalStatusText.setText(getString(R.string.Status_information));
			approvalStatusText.setTextColor(Color.RED);
			etEmailInfo.setText("");
		}
		
		if (motion_onoff.equals("")) motion_onoff = "enabled";
		if (doorcontrol_onoff.equals("")) doorcontrol_onoff = "enabled";
		 
		tglbtnMotionDetect.setChecked(motion_onoff.equals("enabled"));
		tglbtnDoorControl.setChecked(doorcontrol_onoff.equals("enabled"));

//		updateInformationButton.setVisibility(View.INVISIBLE);
//		updateNotificationButton.setVisibility(View.INVISIBLE);
		
		email = "";
		if (!client_status.equals("Initial")) {
			email = NgnEngine.getInstance().getConfigurationService()
					.getString(NgnConfigurationEntry.IDENTITY_CLIENT_EMAIL, "");
		}
		etEmailInfo.setText(email);
	}
	
	private void sendEventInq()
	{
		String input = etEmailInfo.getText().toString();
		
		if (CommonUtilities.checkIfEmpty(input)) {
//			ToastManager.showToast(getActivity(), "Please fill up the email for sending the notifications", Toast.LENGTH_LONG);
			Toast.makeText(getActivity(), "Please fill up the email for sending the notifications", Toast.LENGTH_LONG).show();
		}
		else if (btnsave_accounts_notification_chk == false || !CommonUtilities.checkIfEmpty(input)) {
			btnsave_accounts_notification_chk = true;
			
			if (client_status.equals("inactive") || client_status.equals("active")) 
			{
				Log.d(TAG, " delete delate delate ");
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Discard");
				builder.setMessage("Are you sure you want to discard this request?");
				builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

				    public void onClick(DialogInterface dialog, int which) {
				        // Do nothing but close the dialog
				    	new Thread(new SocClient("event_inquiry_delete_event_inquiry", CommonUtilities.soc_port)).start();
						RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
				        dialog.dismiss();
				    }
				});

				builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

				    @Override
				    public void onClick(DialogInterface dialog, int which) {

				        // Do nothing
				        dialog.dismiss();
				    }
				});

				AlertDialog alert = builder.create();
				alert.show();
				
			}
			else 
			{
				if (!EmailFormatValidator.getInstance().validate(input)){
//					ToastManager.showToast(getActivity(), "Please enter a valid email", Toast.LENGTH_LONG);
					Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_LONG).show();
					Log.d(TAG, "22222222222222222222");
				}
				else
				{
					Log.d(TAG, "Inquire");

					NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.IDENTITY_CLIENT_EMAIL, etEmailInfo.getText().toString());
					NgnEngine.getInstance().getConfigurationService().commit();
					client_status = "inactive";
					motion_onoff = tglbtnMotionDetect.isChecked() ? "enabled" : "disabled";
					doorcontrol_onoff = tglbtnDoorControl.isChecked() ? "enabled" : "disabled";
					Global.getInstance().setClient_inquiry_status(client_status);
					Global.getInstance().set_Doorcontrol_onoff(doorcontrol_onoff);
					Global.getInstance().setMotionclient_onoff(motion_onoff);
					Global.getInstance().setClient_os_type(CLIENT_OS_TYPE_ANDROID);
					Global.getInstance().setRegisterId(mRegID);

					Thread cThread = new Thread(new SocClient("event_inquiry", CommonUtilities.soc_port));
					cThread.start();
					RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");

				}
			}
		}
	}
}