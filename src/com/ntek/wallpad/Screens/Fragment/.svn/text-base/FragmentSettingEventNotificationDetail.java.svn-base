package com.ntek.wallpad.Screens.Fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Model.EventInquiryModel;

public class FragmentSettingEventNotificationDetail extends Fragment
{
	private final static String TAG = FragmentSettingEventNotificationDetail.class.getCanonicalName();
	
	private View view;
	private TextView tvStatusView;
	private TextView tvClientInfoEmail;
	private ToggleButton tbMotionDetect;
	private ToggleButton tbDoorControl;
	
	private String motion_detect_status;
	private String relay_sensors_enable;
	private EventInquiryModel model;
	
	public FragmentSettingEventNotificationDetail(EventInquiryModel model)
	{
		Log.d(TAG, "FragmentSettingEventNotificationDetail(EventInquiryModel model)");
		this.model = model;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_event_notification_detail, container, false);
		intializeUI();
		return view;
	}
	
	protected void intializeUI() 
	{
		tvStatusView = (TextView) view.findViewById(R.id.setting_textview_status);
		tvClientInfoEmail = (TextView) view.findViewById(R.id.setting_textview_email_info);
		tbMotionDetect = (ToggleButton) view.findViewById(R.id.setting_togglebutton_motion_detect);
		tbDoorControl = (ToggleButton) view.findViewById(R.id.setting_togglebutton_door_control);
		
		tvStatusView.setText(model.getActiveStatus());
		tvClientInfoEmail.setText(model.getEmail());
		
		motion_detect_status = model.getMotion_detect_enable();
		relay_sensors_enable = model.getRelay_sensors_enable();
		
		if (model.getMotion_detect_enable().equals("enabled"))
		{
			tbMotionDetect.setChecked(true);
		}
		else
		{
			tbMotionDetect.setChecked(false);
		}
		
		if (model.getRelay_sensors_enable().equals("enabled"))
		{
			tbDoorControl.setChecked(true);
		}
		else
		{
			tbDoorControl.setChecked(false);
		}
		
		tbMotionDetect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked)
				{
					motion_detect_status = "enabled";
				}
				else 
				{
					motion_detect_status = "disabled";
				}
				model.setMotion_detect_enable(motion_detect_status);
			}
		});
		
		tbDoorControl.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				if (isChecked)
				{
					relay_sensors_enable = "enabled";
				}
				else
				{
					relay_sensors_enable = "disabled";
				}
				model.setRelay_sensors_enable(relay_sensors_enable);
			}
		});
	}
	
}
