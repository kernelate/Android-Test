package com.ntek.wallpad.Utils;

import org.doubango.ngn.NgnEngine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.network.Global;

public class MotionDetectionDialogFragment extends Dialog implements android.view.View.OnClickListener{

	private Context context;
	private static final String TAG = MotionDetectionDialogFragment.class.getCanonicalName();
	private Button btnCancelDialog;
	private Button btnOkDialog;
	
	private TextView tvTittleDialog;
	private String motion_onoff = "";
	private Bundle bundle;
	
	TextView tvEnableDisableMotionDectect;

	public static String MOTION_DETECT_KEY = "MOTION_DETECT_KEY";
	
	public MotionDetectionDialogFragment(Context context, TextView tvEnableDisableMotionDectect, Bundle bundle) 
	{
		super(context);
		this.context = context;
		this.tvEnableDisableMotionDectect = tvEnableDisableMotionDectect;
		this.bundle = bundle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_enable_disable_notification);
		
		btnCancelDialog = (Button) findViewById(R.id.dialog_button_cancel_option);
		btnOkDialog = (Button) findViewById(R.id.dialog_button_ok_option);
		tvTittleDialog = (TextView) findViewById(R.id.dialog_setting_textview_tittle);
		
		btnCancelDialog.setOnClickListener(this);
		btnOkDialog.setOnClickListener(this);
		
		motion_onoff = bundle.getString("MotionDetectStatus");
		
		RadioGroup motionDialogRadioGroup = (RadioGroup) findViewById(R.id.customdialog_alarm_a_detect_radio_group);

		// motion on or off for startup
		if (motion_onoff.equals("enabled")) {
			motionDialogRadioGroup.check(R.id.dialog_radiobutton_enable); // disable
		}
		else if (motion_onoff.equals("disabled")) {
			motionDialogRadioGroup.check(R.id.dialog_radiobutton_disable); // enable
		}
		else {
			motionDialogRadioGroup.check(R.id.dialog_radiobutton_disable);
		}

		motionDialogRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.e(TAG, "RadioButtonId : " + checkedId);
				switch (checkedId) {
					case R.id.dialog_radiobutton_disable:
						motion_onoff = "disabled"; // enable
						break;
					case R.id.dialog_radiobutton_enable:
						motion_onoff = "enabled"; // disable
						break;
					default:
						break;
				}
				Log.d(TAG, "motion_onoff : " + motion_onoff);
			}
		});
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.dialog_button_cancel_option:
			
			MotionDetectionDialogFragment.this.dismiss();
			
			break;
			
		case R.id.dialog_button_ok_option:
			
			Global.getInstance().setMotionclient_onoff(motion_onoff);
			System.out.println("Enter " + Global.getInstance().getMotionclient_onoff());
			if (motion_onoff.equals("disabled"))
				tvEnableDisableMotionDectect.setText((context.getString(R.string.string_motion_type_disable)));
			
			else
				tvEnableDisableMotionDectect.setText((context.getString(R.string.string_motion_type_enable)));

			NgnEngine.getInstance().getConfigurationService().putString(MOTION_DETECT_KEY, motion_onoff);
			NgnEngine.getInstance().getConfigurationService().commit();
			
			MotionDetectionDialogFragment.this.dismiss();
			
			break;

		default:
			break;
		}
	}

	
	
}
