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

public class DoorControlDialogFragment extends Dialog implements
		android.view.View.OnClickListener {

	private Context context;
	private static final String TAG = MotionDetectionDialogFragment.class
			.getCanonicalName();
	private Button btnCancelDialog;
	private Button btnOkDialog;

	public static String DOORCONTROL_DETECT_KEY = "DOORCONTROL_DETECT_KEY";
	private String doorcontrol_onoff = "";
	private TextView tvTittleDialog;
	private TextView tvEnableDisableDoorControl;
	private Bundle bundle;

	public DoorControlDialogFragment(Context context,
			TextView tvEnableDisableDoorControl, Bundle bundle) {
		super(context);
		this.context = context;
		this.tvEnableDisableDoorControl = tvEnableDisableDoorControl;
		this.bundle = bundle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_enable_disable_notification);

		btnCancelDialog = (Button) findViewById(R.id.dialog_button_cancel_option);
		btnOkDialog = (Button) findViewById(R.id.dialog_button_ok_option);
		tvTittleDialog = (TextView) findViewById(R.id.dialog_setting_textview_tittle);

		tvTittleDialog.setText("Door Control");

		doorcontrol_onoff = bundle.getString("DoorControlStatus");
		
		btnCancelDialog.setOnClickListener(this);
		btnOkDialog.setOnClickListener(this);

		RadioGroup doorControlDialogRadioGroup = (RadioGroup) findViewById(R.id.customdialog_alarm_a_detect_radio_group);

		if (doorcontrol_onoff.
				equals("enabled"))
			doorControlDialogRadioGroup.check(R.id.dialog_radiobutton_enable); // disable
		else if (doorcontrol_onoff.equals("disabled"))
			doorControlDialogRadioGroup.check(R.id.dialog_radiobutton_disable); // enable
		else
			doorControlDialogRadioGroup.check(R.id.dialog_radiobutton_disable);

		doorControlDialogRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.dialog_radiobutton_enable:
							doorcontrol_onoff = "enabled"; // enable
							break;
						case R.id.dialog_radiobutton_disable:
							doorcontrol_onoff = "disabled"; // disable
							break;
						default:
							break;
						}
						Log.d(TAG, "doorcontrol_onoff : " + doorcontrol_onoff);
					}
				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_button_cancel_option:

			DoorControlDialogFragment.this.dismiss();

			break;

		case R.id.dialog_button_ok_option:

			Global.getInstance().set_Doorcontrol_onoff(doorcontrol_onoff);

			if (doorcontrol_onoff.equals("disabled"))
				tvEnableDisableDoorControl.setText(context.getString(R.string.string_doorcontrol_type_disable)); // DOORCONTROL// ENABLED
			else
				tvEnableDisableDoorControl.setText(context.getString(R.string.string_doorcontrol_type_enable)); // DOORCONTROL// DISABLED
			
			
			NgnEngine.getInstance().getConfigurationService()
					.putString(DOORCONTROL_DETECT_KEY, doorcontrol_onoff);
			NgnEngine.getInstance().getConfigurationService().commit();

			DoorControlDialogFragment.this.dismiss();

			break;

		default:
			break;
		}
	}

}
