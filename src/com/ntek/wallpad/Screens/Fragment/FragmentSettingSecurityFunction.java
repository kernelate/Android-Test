package com.ntek.wallpad.Screens.Fragment;

import org.doubango.ngn.NgnEngine;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Model.EventInquiryModel;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.DoorControlDialogFragment;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.SocClient;

@SuppressLint("ValidFragment")
public class FragmentSettingSecurityFunction extends Fragment
{
	
	private final static String TAG = FragmentSettingSecurityFunction.class.getCanonicalName();
	
	private View view;
	private Context context;
	private Button btnSaveMotionDetect;
	private Button btnSaveSecuritySensor;
	
	private EditText tvSensitivityControlLevel;
	private TextView tvSecuritySensorStatus;
	private ToggleButton tbMotionDetectionStatus;
	private ToggleButton tbDoorControlStatus;
	private Button sensorTypeBtn;
//	private Button notificationTypeBtn;
	
	private String motion_onoff = "";
	private String door_control_onoff = "";
	private String relay_sensor_nickname = "";
	private String signal1_transaction_value = "";
	private String signal1_printed_name = "";
	private String signal1_duration_value = "";
	private String signal1_transaction_noti_YN = "";
	private String signal1_duration_notification_yn = "";
	private String signal2_transaction_value = "";
	private String signal2_printed_name = "";
	private String signal2_duration_value = "";
	private String signal2_transaction_notification_YN = "";
	private String signal2_duration_notification_yn = "";
	private String mDeviceName;
	private boolean mSend_EventSensorsSettings_chk = false;
	
	private int mMinValue = 0;
	private int mMaxValue = 100;
	private int mMotionSensitivityValue;
	
	private BroadcastReceiver mBroadCastReceiver;
	
	private	HolderView mHolder;
	
	private SeekBar motionSensitivitySeekBar;
	private RelativeLayout mSettingLayoutSensitivityLevel;
//	private RelativeLayout mSettingLayoutDoorControl;
	
	private Dialog sensorTypeDialog;
	
	private TextView motionDetectionTitleText;
	private TextView motionDetectionTitleTextDescOne;
	private TextView settingTextViewMotionDetectStatus;
	private TextView sensitivityControlTitle;
	private TextView securitySensorsTitleText;
	private Button autotimer;
	
	private CheckBox lockedOptionCheckBox;
	private CheckBox unlockedOptionCheckBox;
	private EditText lockedTimerOptionEditText;
	private EditText unlockedTimerOptionEditText;
	private EditText autolockTimerOptionEditText;
	private String mAuto_lock_time = "";
	private String mAuto_lock_onoff = "";
	private String mDoor_lock_status = "";
	private DialogHolderSetupGuide autotimerHolder ;
	private Dialog dialog_autotimer;
	private TextView securitySensorTypeTV;
//	private TextView securityNotificationTV;
	private ToggleButton no_nc;
	

	public FragmentSettingSecurityFunction()
	{
		Log.i(TAG, "FragmentSettingSecurityFunction");
//		this.mDeviceName = deviceName;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		motion_onoff = Global.getInstance().get_Motion_ONOFF();// NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.IDENTITY_MOTION_ONOFF,
		mMotionSensitivityValue = Global.getInstance().getMotionSensitivity();
		door_control_onoff = Global.getInstance().get_Doorcontrol_onoff();
		mAuto_lock_time = Global.getInstance().get_Auto_lock_time();
		mAuto_lock_onoff = Global.getInstance().get_Auto_lock_onoff();
		mDoor_lock_status = Global.getInstance().get_Door_lock_status();
		
		signal1_transaction_value = Global.getInstance().getSignal1_transaction_value();
		signal1_printed_name = Global.getInstance().getSignal1_printed_name();
		relay_sensor_nickname = Global.getInstance().getRelay_sensor_nickname();
		signal1_duration_value = Global.getInstance().getSignal1_duration_value();
		signal1_transaction_noti_YN = Global.getInstance().getSignal1_transaction_notification_YN();
		signal1_duration_notification_yn = Global.getInstance().getSignal1_duration_notification_yn();
		signal2_transaction_value = Global.getInstance().getSignal2_transaction_value();
		signal2_printed_name = Global.getInstance().getSignal2_printed_name();
		signal2_duration_value = Global.getInstance().getSignal2_duration_value();
		signal2_transaction_notification_YN = Global.getInstance().getSignal2_transaction_notification_YN();
		signal2_duration_notification_yn = Global.getInstance().getSignal2_duration_notification_yn();
		mBroadCastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals("com.smartbean.event_sensors_motion_update.result") ||
						action.equals("com.smartbean.doorcontrol.update.result")){
					String result = intent.getStringExtra("result");
					if (result != null)
						if (result.equals("success")){
						Toast.makeText(getActivity(), "Update Success", Toast.LENGTH_LONG).show();
						}else{
						Toast.makeText(getActivity(), "Update Failed", Toast.LENGTH_LONG).show();
						}
				}else if (action.equals("com.smartbean.servertalk.action.EVENT_SENSORS_ERROR")){
					String result = intent.getStringExtra("error");
					if (result == null) result = "Undetermined";
					Toast.makeText(getActivity(), "A following Error Occured " + result, Toast.LENGTH_LONG).show();
				}
				RingProgressDialogManager.hide();
			}
		};
		Log.d(TAG, " mDoor_lock_status000 " + mDoor_lock_status);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_security_function, container, false);
		
		initializedUI();
		NgnEngine.getInstance().start();
		context = getActivity();
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.smartbean.doorcontrol.update.result");
		intentFilter.addAction("com.smartbean.event_sensors_motion_update.result");
		intentFilter.addAction("com.smartbean.servertalk.action.EVENT_SENSORS_ERROR");
		getActivity().registerReceiver(mBroadCastReceiver, intentFilter);
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mBroadCastReceiver != null)
		{
			getActivity().unregisterReceiver(mBroadCastReceiver);	
		}
	}
	
	private void initializedUI() 
	{
//		tvHeaderTitle = (TextView) view.findViewById(R.id.fragment_setting_security_function_titleheader_tv);
		motionSensitivitySeekBar = (SeekBar) view.findViewById(R.id.sensitivity_control);
		btnSaveMotionDetect = (Button) view.findViewById(R.id.fragment_setting_security_function_button_motion_detection_save);
		btnSaveSecuritySensor = (Button) view.findViewById(R.id.fragment_setting_security_function_button_security_sensor_save);
		
		lockedOptionCheckBox = (CheckBox) view.findViewById(R.id.fragment_setting_security_function_cb_locked);
		lockedTimerOptionEditText = (EditText) view.findViewById(R.id.fragment_setting_security_function_et_locked);
		unlockedOptionCheckBox = (CheckBox) view.findViewById(R.id.fragment_setting_security_function_cb_unlocked);
		unlockedTimerOptionEditText = (EditText) view.findViewById(R.id.fragment_setting_security_function_et_unlocked);
		autolockTimerOptionEditText = (EditText) view.findViewById(R.id.fragment_setting_security_function_et_auto);
		
		sensorTypeBtn = (Button) view.findViewById(R.id.fragment_setting_security_function_button_sensor_type);
//		notificationTypeBtn = (Button) view.findViewById(R.id.fragment_setting_security_function_button_notification_settings);
		
		tvSensitivityControlLevel = (EditText) view.findViewById(R.id.fragment_setting_security_function_button_number);
//		tvSecuritySensorStatus = (TextView) view.findViewById(R.id.setting_textview_security_sensor_status);
		
//		tbMotionDetectionStatus = (ToggleButton) view.findViewById(R.id.fragment_setting_security_function_toggle_motion_detection);
		mSettingLayoutSensitivityLevel = (RelativeLayout) view.findViewById(R.id.setting_layout_doorcontrol);
//		mSettingLayoutDoorControl = (RelativeLayout) view.findViewById(R.id.setting_layout_notification_door_control);
		tbDoorControlStatus = (ToggleButton) view.findViewById(R.id.fragment_setting_security_function_toggle_door_control);
		
//		securityNotificationTV = (TextView) view.findViewById(R.id.fragment_setting_security_function_textview_notification_setting);
		securitySensorTypeTV = (TextView) view.findViewById(R.id.fragment_setting_security_function_textview_sensor_type);
		no_nc = (ToggleButton) view.findViewById(R.id.fragment_setting_security_function_toggle_door_control);
		
//		settingTextViewMotionDetectStatus = (TextView) view.findViewById(R.id.setting_textview_motion_detect_status);
//		motionDetectionTitleText = (TextView) view.findViewById(R.id.motion_detection_title_text);
		motionDetectionTitleTextDescOne = (TextView) view.findViewById(R.id.motion_detection_title_text_desc_one);
		sensitivityControlTitle = (TextView) view.findViewById(R.id.sensitivity_control_title);
		securitySensorsTitleText = (TextView) view.findViewById(R.id.security_sensors_title_text);
		autotimer = (Button) view.findViewById(R.id.fragment_setting_security_function_btn_auto);
		
		Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		Typeface fontSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		
//		btnSaveMotionDetect.setTypeface(fontSemiBold);
		
		
		
		// motion on or off for startup
		if (motion_onoff.equals("enabled")) 
		{
//			tbMotionDetectionStatus.setChecked(true);
			motionSensitivitySeekBar.setEnabled(true);
			tvSensitivityControlLevel.setEnabled(true);
		}
		else 
		{
//			tbMotionDetectionStatus.setChecked(false);
//			motionSensitivitySeekBar.setEnabled(false);
			tvSensitivityControlLevel.setEnabled(false);
		}
		
		if (door_control_onoff.equals("enabled"))
		{
			tbDoorControlStatus.setChecked(true);
			sensorTypeBtn.setEnabled(true);
//			notificationTypeBtn.setEnabled(true);
			mSettingLayoutSensitivityLevel.setEnabled(true);
//			mSettingLayoutDoorControl.setEnabled(true);
//			securityNotificationTV.setTextColor(Color.BLACK);
			securitySensorTypeTV.setTextColor(Color.BLACK);
		}
		else 
		{
			tbDoorControlStatus.setChecked(false);
			sensorTypeBtn.setEnabled(false);
//			notificationTypeBtn.setEnabled(false);
			mSettingLayoutSensitivityLevel.setEnabled(false);
//			mSettingLayoutDoorControl.setEnabled(false);
//			securityNotificationTV.setTextColor(Color.GRAY);
			securitySensorTypeTV.setTextColor(Color.GRAY);
		}
		motionSensitivitySeekBar.setOnSeekBarChangeListener(motionSensitivityChangeListener);
		motionSensitivitySeekBar.setMax(mMaxValue);
		motionSensitivitySeekBar.setProgress(Global.getInstance().getMotionSensitivity());
		
		tvSensitivityControlLevel.setText(String.valueOf(mMotionSensitivityValue));
		tvSensitivityControlLevel.addTextChangedListener(motionTextChangedListener);
//		tvSensitivityControlLevel.setFilters(new InputFilter[] { new InputFilterMinMax(	mMinValue, mMaxValue) });
		
		
		autolockTimerOptionEditText.setText(mAuto_lock_time);
		lockedOptionCheckBox.setChecked(signal1_transaction_noti_YN.equals("enabled"));
		unlockedOptionCheckBox.setChecked(signal2_transaction_notification_YN.equals("enabled"));
		
		lockedTimerOptionEditText.setText(signal1_duration_value);
		unlockedTimerOptionEditText.setText(signal2_duration_value);
		
		
		
//		tvHeaderTitle.setText(mDeviceName + " / Security Functions"); //FIXME CONVERT IT TO @STRING RESOURCE IN VALUES 
		
//		tbMotionDetectionStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
//				if (isChecked)
//				{
//					motion_onoff = "enabled";
//					motionSensitivitySeekBar.setEnabled(true);
//					tvSensitivityControlLevel.setEnabled(true);
//				}
//				else
//				{
//					motion_onoff = "disabled";
//					motionSensitivitySeekBar.setEnabled(false);
//					tvSensitivityControlLevel.setEnabled(false);
//				}
//				
//			}
//		});
		
//		if (door_control_onoff.equals("")) {
//			door_control_onoff = "disabled";
//			signal1_duration_value = "0";
//			signal2_duration_value = "0";
//		}
//
//		if (relay_sensor_nickname.equals("")) {
//			relay_sensor_nickname = "Door Control";
//		}
//
//		if (signal1_transaction_value.equals("")) {
//			signal1_transaction_value = "1";
//		}
//
//		if (signal1_printed_name.equals("")) {
//			signal1_printed_name = "Locked";
//		}
//
//		if (signal1_duration_value.equals("")) { 
//			signal1_duration_value = "0"; // 0 sec
//		}
//
//		if (signal1_duration_notification_yn.equals("")) {
//			signal1_duration_notification_yn = "disabled";
//		}
//
//		if (signal1_transaction_noti_YN.equals("")) {
//			signal1_transaction_noti_YN = "disabled";
//		}
//
//		if (signal2_transaction_value.equals("")) {
//			signal2_transaction_value = "0"; 
//		}
//
//		if (signal2_printed_name.equals("")) {
//			signal2_printed_name = "Unlocked";
//		}
//
//		if (signal2_duration_value.equals("")) {
//			signal2_duration_value = "0"; // 0 sec
//		}
//
//		if (signal2_duration_notification_yn.equals("")) {
//			signal2_duration_notification_yn = "enabled";
//		}
//
//		if (signal2_transaction_notification_YN.equals("")) {
//			signal2_transaction_notification_YN = "enabled";
//		}
//
//		if (mAuto_lock_onoff.equals("")) {
//			mAuto_lock_onoff = "disabled";
//			mAuto_lock_time = "0";
//		}
		
		autolockTimerOptionEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// do nothing
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// do nothing
			}

			@Override
			public void afterTextChanged(Editable s) {
				mAuto_lock_time = autolockTimerOptionEditText.getText().toString();

				int autoLockTime = 0;
				try {
					autoLockTime = Integer.parseInt(mAuto_lock_time);
					if (autoLockTime >= 5) {
						mAuto_lock_onoff = "enabled";
					}
					else {
						mAuto_lock_onoff = "disabled";
					}
				}
				catch (NumberFormatException e) {
					mAuto_lock_time = "0";
					mAuto_lock_onoff = "disabled";
				}
				
				autolockTimerOptionEditText.setSelection(autolockTimerOptionEditText.getText().toString().length());
			}
		});
		
		autotimer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				setupGuide();
			}
		});
		
		tbDoorControlStatus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked)
				{
					door_control_onoff = "enabled";
					sensorTypeBtn.setEnabled(true);
//					notificationTypeBtn.setEnabled(true);
					mSettingLayoutSensitivityLevel.setEnabled(true);
//					mSettingLayoutDoorControl.setEnabled(true);
//					securityNotificationTV.setTextColor(Color.BLACK);
					securitySensorTypeTV.setTextColor(Color.BLACK);
				}
				else
				{
					door_control_onoff = "disabled";
					sensorTypeBtn.setEnabled(false);
//					notificationTypeBtn.setEnabled(false);
					mSettingLayoutSensitivityLevel.setEnabled(false);
//					mSettingLayoutDoorControl.setEnabled(false);
//					securityNotificationTV.setTextColor(Color.GRAY);
					securitySensorTypeTV.setTextColor(Color.GRAY);
				}
			}
		});
		
		btnSaveSecuritySensor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) 
			{
				Global.getInstance().set_Doorcontrol_onoff(door_control_onoff);
				Global.getInstance().setRelay_sensor_nickname(relay_sensor_nickname);
				Global.getInstance().setSignal1_transaction_value(signal1_transaction_value);
				Global.getInstance().setSignal1_printed_name(signal1_printed_name);
				Global.getInstance().setSignal1_transaction_notification_YN(signal1_transaction_noti_YN);
				Global.getInstance().setSignal1_duration_value(signal1_duration_value);
				Global.getInstance().setSignal1_duration_notification_yn(signal1_duration_notification_yn);
				Global.getInstance().setSignal2_transaction_value(signal2_transaction_value);
				Global.getInstance().setSignal2_printed_name(signal2_printed_name);
				Global.getInstance().setSignal2_duration_value(signal2_duration_value);
				Global.getInstance().setSignal2_duration_notification_yn(signal2_duration_notification_yn);
				Global.getInstance().setSignal2_transaction_notification_YN(signal2_transaction_notification_YN);
				
				new Thread(new SocClient("event_sensors_doorcontrol_update", CommonUtilities.soc_port, getActivity())).start();
				RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
			}
		});
		
		btnSaveMotionDetect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				if (!mSend_EventSensorsSettings_chk) {
//					mSend_EventSensorsSettings_chk = true;
//					
//					Global.getInstance().setMotionSensitivity(mMotionSensitivityValue);
//					Global.getInstance().set_Motion_ONOFF(motion_onoff);
//
//					Thread cThread = new Thread(new SocClient("event_sensors_motion_update", CommonUtilities.soc_port, getActivity()));
//					cThread.start();
//					mSend_EventSensorsSettings_chk = false;
//					RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
//				}

				String lockedTimeString = lockedTimerOptionEditText.getText().toString();
				signal1_duration_value = lockedTimeString;
				signal1_duration_notification_yn = "disabled";
				if(signal1_transaction_noti_YN.equals("enabled")) {
					int lockedTime = 0;
					try {
						lockedTime = Integer.parseInt(lockedTimeString);
					}
					catch(NumberFormatException e) {
						// do nothing
					}
					
					if(lockedTime > 5) {
						signal1_duration_notification_yn = "enabled";
					}
				}
				
				String unlockedTimeString = unlockedTimerOptionEditText.getText().toString();
				signal2_duration_value = unlockedTimeString;
				signal2_duration_notification_yn = "disabled";
				if(signal2_transaction_notification_YN.equals("enabled")) {
					int unlockedTime = 0;
					try {
						unlockedTime = Integer.parseInt(unlockedTimeString);
					}
					catch(NumberFormatException e) {
						// do nothing
					}
					
					if(unlockedTime > 5) {
						signal2_duration_notification_yn = "enabled";
					}
				}
				sendMotionDetectData();
//				sendSecuritySensorData();
			}
		});
		
		sensorTypeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showSensorTypeDialog(relay_sensor_nickname);
			}
		});
		
//		notificationTypeBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				final DialogFragment nDialog = new NotificationSettingDialog();
//				nDialog.show(getFragmentManager(), TAG);
//			}
//		});
		
		// Door Lock Status
//				if (mDoor_lock_status.equals("")) {
//					mDoor_lock_status = "open"; // default
//				}
		Log.d(TAG, " mDoor_lock_status11 " + mDoor_lock_status);
		
//		if (mDoor_lock_status.equals("open")) 
//		{
//			Log.d(TAG, " openopenopenopen ");
//			no_nc.setBackgroundResource(R.drawable.toggle_no);
//			mDoor_lock_status = "open";
//		}
//		else
//		{
//			Log.d(TAG, " closeclose ");
//			no_nc.setBackgroundResource(R.drawable.toggle_nc);
//			mDoor_lock_status = "close";
//		}
		
		no_nc.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				if (isChecked) 
				{
					Log.d(TAG, " 11111 ncncncnccn");
					no_nc.setBackgroundResource(R.drawable.toggle_nc);
					mDoor_lock_status = "close";
				}
				else
				{
					Log.d(TAG, " 22222 nononono");
					no_nc.setBackgroundResource(R.drawable.toggle_no);
					mDoor_lock_status = "open";
				}
			}
		});
		
	}
	//dialog_security_sensor 
	//dialog_sensitivity_control
	
	TextWatcher motionTextChangedListener = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.toString().trim().isEmpty())
				{ 
					 motionSensitivitySeekBar.setProgress(0);
				}
				else
				{
					int progress = 0;
					if (isNumerical(s.toString()))
					{
						progress = Integer.parseInt(s.toString());
					}
					if (progress < 0)
					{
						progress = 0;
					}
					else if (progress > motionSensitivitySeekBar.getMax())
					{
						progress = motionSensitivitySeekBar.getMax();
					}
					motionSensitivitySeekBar.setProgress(progress);
				}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void afterTextChanged(Editable s) {}
	};
	
	OnSeekBarChangeListener motionSensitivityChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			Log.d(TAG, " sense sense sense ");
			mMotionSensitivityValue = progress;
			tvSensitivityControlLevel.setText(Integer.toString(mMotionSensitivityValue));
		}
	};// motionSensitivityChangeListener
	
	private boolean isNumerical(String number)
	{
		try {
			Integer.parseInt(number);
		} catch (NumberFormatException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
	
//	protected void showCustomDialog_Motion() {
//		// TODO Auto-generated method stub
//		final Dialog mMotionDialog = new Dialog(getActivity());
//		mMotionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		mMotionDialog.setCancelable(false);
//		mMotionDialog.setContentView(R.layout.dialog_sensitivity_control);
//
//		motionSensitivitySeekBar = (SeekBar) mMotionDialog.findViewById(R.id.motion_sensitivity_seekbar);
//		motionSensitivitySeekBar.setOnSeekBarChangeListener(motionSensitivityChangeListener);
//		motionSensitivitySeekBar.setMax(mMaxValue - mMinValue);
//		motionSensitivitySeekBar.setProgress(Global.getInstance().getMotionSensitivity());
//
//		Button okBtn = (Button) mMotionDialog.findViewById(R.id.motionsensitivity_okbutton);
//		okBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Global.getInstance().setMotionSensitivity(mMotionSensitivityValue);
//				mMotionDialog.dismiss();
//			}
//		});
//
//		Button cancelBtn = (Button) mMotionDialog.findViewById(R.id.setting_motionsensitivity_cancel);
//		cancelBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				mMotionDialog.dismiss();
//			}
//		});
//		mMotionDialog.show();
//	}
	
	private class NotificationSettingDialog extends DialogFragment
	{
		
		private String nPreCurrent_signal1_transaction_noti_YN;
		private String nPreCurrent_signal1_printed_name;
		private String nPreCurrent_signal1_duration_value;
		private String nPreCurrent_signal2_transaction_noti_YN;
		private String nPreCurrent_signal2_printed_name;
		private String nPreCurrent_signal2_duration_value;
		private ToggleButton lockedEnabledButton;
		private EditText lockedEventDescriptionET;
		private EditText lockedEventTime;
		private ToggleButton unLockedEnabledButton;
		private EditText unLockedEventDescriptionET;
		private EditText unLockedEventTime;
		private Button notificationOkButton;
		private TextView lockedDescriptionTV;
		private TextView lockedNotifyTV;
		private TextView unlockedDescriptionTV;
		private TextView unlockedNotifyTV;
		private View mView;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			nPreCurrent_signal1_transaction_noti_YN = signal1_transaction_noti_YN;
			nPreCurrent_signal1_printed_name = signal1_printed_name;
			nPreCurrent_signal1_duration_value = signal1_duration_value;
			nPreCurrent_signal2_transaction_noti_YN = signal2_transaction_notification_YN;
			nPreCurrent_signal2_printed_name = signal2_printed_name;
			nPreCurrent_signal2_duration_value = signal2_duration_value;
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
			mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_notification_settings, null);
			lockedEnabledButton = (ToggleButton) mView.findViewById(R.id.dialog_notification_settings_locked_tb);
			lockedEventDescriptionET = (EditText) mView.findViewById(R.id.dialog_notification_settings_locked_event_et);
			lockedEventTime = (EditText) mView.findViewById(R.id.dialog_notification_settings_locked_event_time);
			unLockedEnabledButton = (ToggleButton) mView.findViewById(R.id.dialog_notification_settings_unlocked_tb);
			unLockedEventDescriptionET = (EditText) mView.findViewById(R.id.dialog_notification_settings_unlocked_event_et);
			unLockedEventTime = (EditText) mView.findViewById(R.id.dialog_notification_settings_unlocked_event_time);
			notificationOkButton = (Button) mView.findViewById(R.id.dialog_notification_settings_ok_button);
			lockedDescriptionTV = (TextView) mView.findViewById(R.id.dialog_notification_settings_event_locked_description_tv);
			lockedNotifyTV = (TextView) mView.findViewById(R.id.dialog_notification_settings_event_locked_notify_tv);
			unlockedDescriptionTV = (TextView) mView.findViewById(R.id.dialog_notification_settings_unlocked_event_description_tv);
			unlockedNotifyTV = (TextView) mView.findViewById(R.id.dialog_notification_settings_unlocked_notify_tv);
			
			if (signal1_transaction_noti_YN.equals("enabled"))
			{
				lockedEnabledButton.setChecked(true);
				enabledLocked(true);
			}
			else
			{
				lockedEnabledButton.setChecked(false);
				enabledLocked(false);
			}
			
			if (signal2_transaction_notification_YN.equals("enabled"))
			{
				unLockedEnabledButton.setChecked(true);
				enabledUnLocked(true);
			}
			else 
			{
				unLockedEnabledButton.setChecked(false);
				enabledUnLocked(false);
			}
			
			lockedEventDescriptionET.setText(nPreCurrent_signal1_printed_name);
			lockedEventTime.setText(nPreCurrent_signal1_duration_value);
			unLockedEventDescriptionET.setText(nPreCurrent_signal2_printed_name);
			unLockedEventTime.setText(nPreCurrent_signal2_duration_value);
			
			lockedEnabledButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
					if (isChecked)
					{
						nPreCurrent_signal1_transaction_noti_YN = "enabled";
						enabledLocked(true);
					}
					else 
					{
						nPreCurrent_signal1_transaction_noti_YN = "disabled";
						enabledLocked(false);
					}
				}
			});
			
			unLockedEnabledButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked)
					{
						nPreCurrent_signal2_transaction_noti_YN = "enabled";
						enabledUnLocked(true);
					}
					else
					{
						nPreCurrent_signal2_transaction_noti_YN = "disabled";
						enabledUnLocked(false);
					}
				}
			});
			
			notificationOkButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					signal1_transaction_noti_YN = nPreCurrent_signal1_transaction_noti_YN;
					signal1_duration_value = lockedEventTime.getText().toString();
					signal1_printed_name = lockedEventDescriptionET.getText().toString();
					signal2_transaction_notification_YN = nPreCurrent_signal2_transaction_noti_YN;
					signal2_printed_name = unLockedEventDescriptionET.getText().toString();
					signal2_duration_value = unLockedEventTime.getText().toString();
					dismiss();
				}
			});
		}
		
		private void enabledLocked(boolean enabled)
		{
			if (enabled)
			{
				lockedEventDescriptionET.setEnabled(true);
				lockedEventTime.setEnabled(true);
				lockedDescriptionTV.setTextColor(Color.BLACK);
				lockedNotifyTV.setTextColor(Color.BLACK);
			}
			else 
			{
				lockedEventDescriptionET.setEnabled(false);
				lockedEventTime.setEnabled(false);
				lockedDescriptionTV.setTextColor(Color.GRAY);
				lockedNotifyTV.setTextColor(Color.GRAY);
			}
		}
		
		private void enabledUnLocked(boolean enabled)
		{
			if (enabled)
			{
				unLockedEventDescriptionET.setEnabled(true);
				unLockedEventTime.setEnabled(true);
				unlockedDescriptionTV.setTextColor(Color.BLACK);
				unlockedNotifyTV.setTextColor(Color.BLACK);
			}
			else 
			{
				unLockedEventDescriptionET.setEnabled(false);
				unLockedEventTime.setEnabled(false);
				unlockedDescriptionTV.setTextColor(Color.GRAY);
				unlockedNotifyTV.setTextColor(Color.GRAY);
			}
		}
		
	}
	
	
	protected void showCustomDoorDialog(){
		final Dialog mDoorControlDialog = new Dialog(getActivity());
		mDoorControlDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDoorControlDialog.setCancelable(false);
		mDoorControlDialog.setContentView(R.layout.dialog_security_sensor);
		
		if (mHolder ==  null) mHolder = new HolderView();
		mHolder.toggleButtonDoorControl = (ToggleButton) mDoorControlDialog.findViewById(R.id.setting_togglebutton_motion_detect);
		mHolder.notificationCheckBox1 = (CheckBox) mDoorControlDialog.findViewById(R.id.notification_checkbox_locked);
		mHolder.notificationCheckBox2 = (CheckBox) mDoorControlDialog.findViewById(R.id.notification_checkbox_locked_exceed_30secs);
		mHolder.notificationCheckBox3 = (CheckBox) mDoorControlDialog.findViewById(R.id.notification_checkbox_unlocked);
		mHolder.notificationCheckBox4 = (CheckBox) mDoorControlDialog.findViewById(R.id.notification_checkbox_unlocked_exceed_30secs);
		mHolder.editText_sensortype = (EditText) mDoorControlDialog.findViewById(R.id.doorcontrol_et);
		
		if (door_control_onoff.equals(""))
			door_control_onoff = "enabled";
		
		mHolder.toggleButtonDoorControl.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked)
				{
					door_control_onoff = "enabled";
					disableEnableViews(true);
				}
				else
				{
					door_control_onoff = "disabled";
					disableEnableViews(false);
				}
			}
		});
		
		mHolder.load();
		mHolder.loadAllCheckBox();
		
		Button okButton = (Button) mDoorControlDialog.findViewById(R.id.dialog_security_sensor_ok);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (CommonUtilities.checkIfEmpty(mHolder.editText_sensortype.getText().toString())){
					Toast.makeText(getActivity(), "Please provide a name for the Security Sensors", Toast.LENGTH_LONG).show();
					if (mHolder.editText_sensortype.requestFocus()){
						getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					}
					return;
				}
				
				relay_sensor_nickname = mHolder.editText_sensortype.getText().toString();
				
				Global.getInstance().set_Doorcontrol_onoff(door_control_onoff);
				
				Global.getInstance().setRelay_sensor_nickname(relay_sensor_nickname);
				Global.getInstance().setSignal1_transaction_value(signal1_transaction_value);
				Global.getInstance().setSignal1_printed_name(signal1_printed_name);
				Global.getInstance().setSignal1_transaction_notification_YN(signal1_transaction_noti_YN);
				Global.getInstance().setSignal1_duration_value(signal1_duration_value);
				Global.getInstance().setSignal1_duration_notification_yn(signal1_duration_notification_yn);
				Global.getInstance().setSignal2_transaction_value(signal2_transaction_value);
				Global.getInstance().setSignal2_printed_name(signal2_printed_name);
				Global.getInstance().setSignal2_duration_value(signal2_duration_value);
				Global.getInstance().setSignal2_duration_notification_yn(signal2_duration_notification_yn);
				Global.getInstance().setSignal2_transaction_notification_YN(signal2_transaction_notification_YN);

				if (door_control_onoff.equals("enabled")){
					tvSecuritySensorStatus.setText(getString(R.string.Enabled));
				}else{
					tvSecuritySensorStatus.setText(getString(R.string.Disabled));
				}
				
				mDoorControlDialog.dismiss();
			}
		});
		
		Button cancelButton = (Button) mDoorControlDialog.findViewById(R.id.dialog_security_sensor_cancel);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDoorControlDialog.dismiss();
			}
		});
		
		mDoorControlDialog.show();
	}
	
	protected void showSensorTypeDialog(final String sensorName)
	{
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_sensor_type, null);
		mBuilder.setView(view).setCancelable(true);
		final EditText sensorNameET = (EditText) view.findViewById(R.id.dialog_sensor_type_sensorname_et);
		final Button   sensorOKButton = (Button) view.findViewById(R.id.dialog_sensor_type_ok_button);
		
		sensorNameET.setText(sensorName);
		sensorOKButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				relay_sensor_nickname = sensorNameET.getText().toString();
				sensorTypeDialog.dismiss();
			}
		});
		
		sensorTypeDialog = mBuilder.create();
		sensorTypeDialog.show();
	}
	
	private void disableEnableViews(boolean enable){
		if (enable){
			mHolder.editText_sensortype.setEnabled(true);
			mHolder.notificationCheckBox1.setEnabled(true);
			mHolder.notificationCheckBox2.setEnabled(true);
			mHolder.notificationCheckBox3.setEnabled(true);
			mHolder.notificationCheckBox4.setEnabled(true);
		}else{
			mHolder.editText_sensortype.setEnabled(false);
			mHolder.notificationCheckBox1.setEnabled(false);
			mHolder.notificationCheckBox2.setEnabled(false);
			mHolder.notificationCheckBox3.setEnabled(false);
			mHolder.notificationCheckBox4.setEnabled(false);
		}
	}
	
	private class HolderView{
		
		private ToggleButton toggleButtonDoorControl;
		private EditText editText_sensortype;
		private CheckBox notificationCheckBox1;
		private CheckBox notificationCheckBox2;
		private CheckBox notificationCheckBox3;
		private CheckBox notificationCheckBox4;
		
		public void load(){
			editText_sensortype.setText(relay_sensor_nickname);
			
			if (door_control_onoff.equals("enabled")){
				toggleButtonDoorControl.setChecked(true);
				disableEnableViews(true);
			}else{
				toggleButtonDoorControl.setChecked(false);
				disableEnableViews(false);
			}
			
			if (signal1_transaction_noti_YN.equals("enabled")){
				notificationCheckBox1.setChecked(true);
			}else{
				notificationCheckBox1.setChecked(false);
			}
			
			if (signal1_duration_notification_yn.equals("enabled")){
				notificationCheckBox2.setChecked(true);
			}else{
				notificationCheckBox2.setChecked(false);
			}
				
			if (signal2_transaction_notification_YN.equals("enabled")){
				notificationCheckBox3.setChecked(true);
			}else{
				notificationCheckBox3.setChecked(false);
			}
			
			if (signal2_duration_notification_yn.equals("enabled")){
				notificationCheckBox4.setChecked(true);
			}else{
				notificationCheckBox4.setChecked(false);
			}
		}
		
		public void loadAllCheckBox(){
			mHolder.notificationCheckBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (mHolder.notificationCheckBox1.isChecked()){
						signal1_transaction_noti_YN = "enabled";
					}else{
						signal1_transaction_noti_YN = "disabled";
					}
				}
			});
			
			mHolder.notificationCheckBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (mHolder.notificationCheckBox3.isChecked()){
						signal2_transaction_notification_YN = "enabled";
					}else{
						signal2_transaction_notification_YN = "disabled";
					}
				}
			});
			
			mHolder.notificationCheckBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (mHolder.notificationCheckBox2.isChecked()){
						signal1_duration_notification_yn = "enabled";
					}else{
						signal1_duration_notification_yn = "disabled";
					}
				}
			});
			
			mHolder.notificationCheckBox4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (mHolder.notificationCheckBox4.isChecked()){
						signal2_duration_notification_yn = "enabled";
					}else{
						signal2_duration_notification_yn = "disabled";
					}
				}
			});
		}
	}
	
	private class DialogHolderSetupGuide 
	{
		private Button btnNo;
		private Button btnYes;
	}
	
	private void setupGuide()
	{
		dialog_autotimer = new Dialog(context);
		dialog_autotimer.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_autotimer.setCancelable(true);
		dialog_autotimer.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		dialog_autotimer.setContentView(R.layout.fragment_autotimer);

		autotimerHolder = new DialogHolderSetupGuide();
//		autotimerHolder.btnNo = (Button) dialog_autotimer.findViewById(R.id.autotimer);
		autotimerHolder.btnYes = (Button) dialog_autotimer.findViewById(R.id.autotimer_btn_ok);

//		autotimerHolder.btnNo.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) 
//			{
//				dialog_autotimer.dismiss();
//			}
//		});

		autotimerHolder.btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				dialog_autotimer.dismiss();
			}
		});

		dialog_autotimer.show();
		dialog_autotimer.setCancelable(false);
		dialog_autotimer.setCanceledOnTouchOutside(false);

	}
	
	
	private void sendMotionDetectData() {
		if( mMotionSensitivityValue == 0) {
			mMotionSensitivityValue = 1;
		}
		Log.d(TAG, " mDoor_lock_status44 " + mDoor_lock_status);
		Global.getInstance().setMotionSensitivity(mMotionSensitivityValue);
		Global.getInstance().set_Motion_ONOFF(motion_onoff);
		
		Global.getInstance().set_Doorcontrol_onoff(door_control_onoff);
		Global.getInstance().set_Auto_lock_time(mAuto_lock_time);
		Global.getInstance().set_Auto_lock_onoff(mAuto_lock_onoff);
		Global.getInstance().set_Door_lock_status(mDoor_lock_status);
		Global.getInstance().setRelay_sensor_nickname(relay_sensor_nickname);
		Global.getInstance().setSignal1_transaction_value(signal1_transaction_value);
		Global.getInstance().setSignal1_printed_name(signal1_printed_name);
		Global.getInstance().setSignal1_transaction_notification_YN(signal1_transaction_noti_YN);
		Global.getInstance().setSignal1_duration_value(signal1_duration_value);
		Global.getInstance().setSignal1_duration_notification_yn(signal1_duration_notification_yn);
		Global.getInstance().setSignal2_transaction_value(signal2_transaction_value);
		Global.getInstance().setSignal2_printed_name(signal2_printed_name);
		Global.getInstance().setSignal2_transaction_notification_YN(signal2_transaction_notification_YN);
		Global.getInstance().setSignal2_duration_value(signal2_duration_value);
		Global.getInstance().setSignal2_duration_notification_yn(signal2_duration_notification_yn);
		
		Log.d(TAG, " mDoor_lock_status55 " + mDoor_lock_status);
		new Thread(new SocClient("event_sensors_motion_update", CommonUtilities.soc_port)).start();
		new Thread(new SocClient("event_sensors_doorcontrol_update", CommonUtilities.soc_port)).start();
		RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
	}
	
//	private void sendSecuritySensorData() {
//		Global.getInstance().set_Doorcontrol_onoff(door_control_onoff);
//		Global.getInstance().set_Auto_lock_time(mAuto_lock_time);
//		Global.getInstance().set_Auto_lock_onoff(mAuto_lock_onoff);
//
//		Global.getInstance().set_Door_lock_status(mDoor_lock_status);
//		Global.getInstance().setRelay_sensor_nickname(relay_sensor_nickname);
//		
//		Global.getInstance().setSignal1_transaction_value(signal1_transaction_value);
//		Global.getInstance().setSignal1_printed_name(signal1_printed_name);
//		Global.getInstance().setSignal1_transaction_notification_YN(signal1_transaction_noti_YN);
//		Global.getInstance().setSignal1_duration_value(signal1_duration_value);
//		Global.getInstance().setSignal1_duration_notification_yn(signal1_duration_notification_yn);
//
//		Global.getInstance().setSignal2_transaction_value(signal2_transaction_value);
//		Global.getInstance().setSignal2_printed_name(signal2_printed_name);
//		Global.getInstance().setSignal2_transaction_notification_YN(signal2_transaction_notification_YN);
//		Global.getInstance().setSignal2_duration_value(signal2_duration_value);
//		Global.getInstance().setSignal2_duration_notification_yn(signal2_duration_notification_yn);
//
//		new Thread(new SocClient("event_sensors_doorcontrol_update", CommonUtilities.soc_port)).start();
//		RingProgressDialogManager.show(getActivity(), "Loading", "Sending Data To Server Device");
//	}
	
	public class InputFilterMinMax implements InputFilter {
		private int min, max;

		public InputFilterMinMax(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public InputFilterMinMax(String min, String max) {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			try {
				int input = Integer.parseInt(dest.toString() + source.toString());
				if (isInRange(min, max, input))
					return null;
			}
			catch (NumberFormatException nfe) {
			}

			return "";
		}

		private boolean isInRange(int a, int b, int c) {
			return b > a ? c >= a && c <= b : c >= b && c <= a;
		}
	}
}