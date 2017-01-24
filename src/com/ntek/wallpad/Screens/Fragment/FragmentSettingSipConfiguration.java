package com.ntek.wallpad.Screens.Fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.SocClient;

public class FragmentSettingSipConfiguration extends Fragment
{
	private static final String TAG = FragmentSettingSipConfiguration.class.getCanonicalName();

	private View view;
	
	private Button btnSaveSip;
	private EditText etDoorTalkName;
	private EditText etDoorTalkCallID;
	private EditText etDoorTalkPassword;
	private EditText etDoorTalkAddress;
	private EditText etDoorTalkPortNumber;
//	private Button volumeControlButton;
	
	private Button okBtn;
	private SeekBar callSeekBar;
	private SeekBar soundSeekBar;
	private EditText soundET;
	private EditText callET;
	private Button pwShowSip; 
	
	int nSpeaker_Volume_Max = 15;
	int nCall_Volume_Max = 5;
	
	int nCurrent_Speaker_Volume = 0;
	int nPre_Speaker_Volume = 0;
	
	int nCurrent_Call_Volume = 0;
	int nPre_Call_Volume = 0;
	
	private BroadcastReceiver mBroadcastReceiver;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		nPre_Speaker_Volume = Global.getInstance().getServer_Speaker_volume();
		if(nPre_Speaker_Volume == 0) 
		{
			nCurrent_Speaker_Volume = 1;
		}
		else 
		{
			nCurrent_Speaker_Volume = nPre_Speaker_Volume;
		}

		nPre_Call_Volume = Global.getInstance().getServer_Call_volume();
		if(nPre_Call_Volume == 0)
		{
			nCurrent_Call_Volume = 1;
		}
		else 
		{
			nCurrent_Call_Volume = nPre_Call_Volume;
		}
		
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				final String response = intent.getStringExtra("sip");
				Log.d(TAG, "onReceive() : " + action);
				Log.d(TAG, "getStringExtra() : " + response);

				RingProgressDialogManager.hide();
//				if (response.equals("success")) 
//				{
//					//					back(); FIXME CHANGE THIS
//					showToast("Success");
//				}
//				else 
//				{
//					showToast(getString(R.string.string_connection_fail));
//				}

//				if (action.equals("com.smartbean.servertalk.action.SENDING_VOLUME")) 
//				{
//					String responsevol = intent.getStringExtra("response");
//					Log.d(TAG, "getStringExtra() : " + response);
//
//					if (responsevol.equals("success")) {
//						showToast("Volume Value is successfully send to the server");
//					}
//					else 
//					{
//						nCurrent_Speaker_Volume = nPre_Speaker_Volume;
//						Global.getInstance().setServer_Speaker_volume(nCurrent_Speaker_Volume);
//						nCurrent_Call_Volume = nPre_Call_Volume;
//						Global.getInstance().setServer_Call_volume(nCurrent_Call_Volume);
//						showToast(getString(R.string.string_connection_lost));
//					}
//					//					dismiss();
//				}
			}
		};
		
//		mBroadCastRecv = new BroadcastReceiver() {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				String action = intent.getAction();
//				Log.d(TAG, "onReceive() : " + action);
//				RingProgressDialogManager.hide();
//				if (action.equals("com.smartbean.servertalk.action.SENDING_VOLUME")) {
//					String response = intent.getStringExtra("response");
//					Log.d(TAG, "getStringExtra() : " + response);
//
//					if (response.equals("success")) {
//						showToast("Volume Value is successfully send to the server");
//					}
//					else {
//						nCurrent_Speaker_Volume = nPre_Speaker_Volume;
//						Global.getInstance().setServer_Speaker_volume(nCurrent_Speaker_Volume);
//						nCurrent_Call_Volume = nPre_Call_Volume;
//						Global.getInstance().setServer_Call_volume(nCurrent_Call_Volume);
//						showToast(getString(R.string.string_connection_lost));
//					}
//					dismiss();
//				}
//			}
//		};
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_sip, container, false);
		
		initializedUI();
		
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Global.TCP_SETUP_SIP_CALLBACK);
		intentFilter.addAction("com.smartbean.servertalk.action.SENDING_VOLUME");
		getActivity().registerReceiver(mBroadcastReceiver, intentFilter);

		
		super.onResume();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		if (mBroadcastReceiver != null) {
			getActivity().unregisterReceiver(mBroadcastReceiver);
		}
		super.onPause();
	}

	private void initializedUI() 
	{
		btnSaveSip = (Button) view.findViewById(R.id.fragment_settings_sip_button_save);
		pwShowSip = (Button) view.findViewById(R.id.pwShopSip);
		etDoorTalkName = (EditText) view.findViewById(R.id.fragment_settings_sip_et_display_name);
		etDoorTalkCallID = (EditText) view.findViewById(R.id.fragment_settings_sip_et_callid);
		etDoorTalkPassword = (EditText) view.findViewById(R.id.fragment_settings_sip_et_password);
		etDoorTalkAddress = (EditText) view.findViewById(R.id.fragment_settings_sip_et_ipadd);
		etDoorTalkPortNumber = (EditText) view.findViewById(R.id.fragment_settings_sip_et_port);
//		volumeControlButton = (Button) view.findViewById(R.id.phone_settings_button_device_volume_control);
		pwShowSip.setBackgroundResource(R.drawable.ic_visibility_off);
		etDoorTalkName.setEnabled(false);
		
		String name = Global.getInstance().getName();
		String server_impi = Global.getInstance().get_server_Impi();
		String password = Global.getInstance().getPw();
		String proxy = Global.getInstance().getProxyHost();
		int proxyPort = Global.getInstance().getConfPort();
		
		Log.d(TAG, " server_impi " + server_impi + " password " + password + " proxy " + proxy );
//		if ((!CommonUtilities.checkIfEmpty(name))
//				&& ((name.length() > 8 && !name.substring(0, 9).equals("IDENTITY_")) || (name.length() < 9)))
//		{
//			Log.d(TAG, " etDoorTalkName ");
//			etDoorTalkName.setText(name);
//		}
//
//		if ((!CommonUtilities.checkIfEmpty(server_impi))
//				&& ((server_impi.length() > 8 && !server_impi.substring(0, 9).equals("IDENTITY_")) || (server_impi.length() < 9)))
//		{
//			Log.d(TAG, " etDoorTalkCallID ");
//			etDoorTalkCallID.setText(server_impi);
//		}
//
//		if ((CommonUtilities.checkIfEmpty(password))
//				&& ((password.length() > 8 && !password.substring(0, 9).equals("IDENTITY_")) || (password.length() < 9))
//				)
//		{
//			Log.d(TAG, " pwpwpwwpwpwpwpwwpwpwpwpw " + password + " pwwpwpwp ");
//			etDoorTalkPassword.setText(password);
//		}
//		if ((password != null && password.length() > 0)
//				&& ((password.length() > 8 && !password.substring(0, 9).equals("IDENTITY_")) || (password.length() < 9)))
//		{
//			etDoorTalkPassword.setText(password);
//		}
//
//		if ((!CommonUtilities.checkIfEmpty(proxy)) && ((proxy.length() > 8 && !proxy.substring(0, 9).equals("IDENTITY_")) || (proxy.length() < 9)))
//		{
//			Log.d(TAG, " etDoorTalkAddress ");
//			etDoorTalkAddress.setText(proxy);
//		}
		
		if ((name != null && name.length() > 0)
				&& ((name.length() > 8 && !name.substring(0, 9).equals("IDENTITY_")) || (name.length() < 9)))
		{
			Log.d(TAG, " etDoorTalkName ");
			etDoorTalkName.setText(name);
		}

		if ((server_impi != null && server_impi.length() > 0)
				&& ((server_impi.length() > 8 && !server_impi.substring(0, 9).equals("IDENTITY_")) || (server_impi.length() < 9)))
		{
			Log.d(TAG, " etDoorTalkCallID ");
			etDoorTalkCallID.setText(server_impi);
		}

		if ((password != null && password.length() > 0)
				&& ((password.length() > 8 && !password.substring(0, 9).equals("IDENTITY_")) || (password.length() < 9)))
		{
			Log.d(TAG, " pwpwpwwpwpwpwpwwpwpwpwpw " + password + " pwwpwpwp ");
			etDoorTalkPassword.setText(password);
		}

		if ((proxy != null && proxy.length() > 0)
				&& ((proxy.length() > 8 && !proxy.substring(0, 9).equals("IDENTITY_")) || (proxy.length() < 9)))
		{
			Log.d(TAG, " etDoorTalkAddress ");
			etDoorTalkAddress.setText(proxy);
		}
		
		etDoorTalkPortNumber.setText(String.valueOf(proxyPort));
		
		btnSaveSip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if ((etDoorTalkCallID.getText().toString() != null && etDoorTalkCallID.getText()
						.toString().length() > 0)
						&& (etDoorTalkAddress.getText().toString() != null && etDoorTalkAddress
								.getText().toString().length() > 0)
						&& (etDoorTalkName.getText().toString() != null && etDoorTalkName.getText()
								.toString().length() > 0)
						&& (etDoorTalkPassword.getText().toString() != null && etDoorTalkPassword
								.getText().toString().length() > 0)
						&& (etDoorTalkPortNumber.getText().toString() != null && etDoorTalkPortNumber
								.getText().toString().length() > 0)		
						)
				{
					Global.getInstance().setName(etDoorTalkName.getText().toString());
					Global.getInstance().set_server_Impi(etDoorTalkCallID.getText().toString());
					Global.getInstance().setPw(etDoorTalkPassword.getText().toString());
					Global.getInstance().setAdress(etDoorTalkAddress.getText().toString());
					Global.getInstance().setProxyHost(etDoorTalkAddress.getText().toString());
					Global.getInstance().setConfPort(Integer.parseInt(etDoorTalkPortNumber.getText().toString()));
					
					RingProgressDialogManager.show(getActivity(), getString(R.string.string_please_wait), getString(R.string.string_send_data));
					Thread cThread = new Thread(new SocClient("sip", CommonUtilities.soc_port, getActivity()));
					cThread.start();

				}
				else
				{ // 2014.07.18 SmartBean_SHCHO : CHECK INPUTBOX DATA IS WRONG
					if (etDoorTalkName.getText().toString() == null
							|| etDoorTalkName.getText().toString().length() < 1)
					{
						showToast(getString(R.string.string_setupsipdoortalk_displayname_empty));
					}
					else if (etDoorTalkCallID.getText().toString() == null
							|| etDoorTalkCallID.getText().toString().length() < 1)
					{
						showToast(getString(R.string.string_setupsipdoortalk_doortalknumber_empty));
					}
					else if (etDoorTalkPassword.getText().toString() == null
							|| etDoorTalkPassword.getText().toString().length() < 1)
					{
						showToast(getString(R.string.string_setupsipdoortalk_password_empty));
					}
					else if (etDoorTalkAddress.getText().toString() == null
							|| etDoorTalkAddress.getText().toString().length() < 1)
					{
						showToast(getString(R.string.string_setupsipdoortalk_ipaddress_empty));
					}
					else if (etDoorTalkPortNumber.getText().toString() == null
							|| etDoorTalkPortNumber.getText().toString().length() < 1)
					{
						showToast(getString(R.string.string_setupsipdoortalk_port_empty));
					}
				}
				
				saveVolume();
//				showToast(getString(R.string.string_save));
				Toast.makeText(getActivity(), getString(R.string.string_config_save), Toast.LENGTH_SHORT).show();
			}
			
		});
		
		
		
		
//		okBtn = (Button) view.findViewById(R.id.dialog_volume_control_ok_btn);
		soundSeekBar = (SeekBar) view.findViewById(R.id.soundSeekBar);
		callSeekBar = (SeekBar) view.findViewById(R.id.callSeekBar);
		soundET = (EditText) view.findViewById(R.id.dialog_volume_control_edittext_sound);
		callET = (EditText) view.findViewById(R.id.dialog_volume_control_edittext_speech);

		soundET.setText(String.valueOf(nCurrent_Speaker_Volume));
		soundSeekBar.setMax(nSpeaker_Volume_Max);
		soundSeekBar.setProgress(nCurrent_Speaker_Volume);
		soundSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				int MIN = 0;
				if (progress > MIN) {
					nCurrent_Speaker_Volume = progress;
					soundET.setText(String.valueOf(nCurrent_Speaker_Volume));
					//	    				SeekBar_Speaker_Number.setText(String.valueOf(nCurrent_Speaker_Volume));
				}
				else {
					soundSeekBar.setProgress(1);
				}
			}
		});

		callET.setText(String.valueOf(nCurrent_Call_Volume));
		callSeekBar.setMax(nCall_Volume_Max);
		callSeekBar.setProgress(nCurrent_Call_Volume);
		callSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
				Log.d(TAG, " soundseeker soundseeker soundseeker  ");
				int MIN = 0;
				if (progress > MIN) {
					nCurrent_Call_Volume = progress;
					callET.setText(String.valueOf(nCurrent_Call_Volume));
					//	    				SeekBar_Call_Number.setText(String.valueOf(nCurrent_Call_Volume));
				}
				else {
					callSeekBar.setProgress(1);
				}
			}
		});

		soundET.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int progress = 1;
				if (isNumerical(s.toString()))
				{
					progress = Integer.valueOf(s.toString());
				}
				if (progress > nSpeaker_Volume_Max)
				{
					progress = nSpeaker_Volume_Max;
					soundET.setText(String.valueOf(progress));
				}
				else if (progress < 0)
				{
					progress = 1;
					soundET.setText(String.valueOf(progress));
				}
				soundSeekBar.setProgress(progress);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		callET.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int progress = 1;
				if (isNumerical(s.toString()))
				{
					progress = Integer.valueOf(s.toString());
				}
				if (progress > nCall_Volume_Max)
				{
					progress = nCall_Volume_Max;
					callET.setText(String.valueOf(progress));
				}
				else if (progress < 0)
				{
					progress = 1;
					callET.setText(String.valueOf(progress));
				}
				callSeekBar.setProgress(progress);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

//		okBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				Global.getInstance().setServer_Speaker_volume(nCurrent_Speaker_Volume);
//				Global.getInstance().setServer_Call_volume(nCurrent_Call_Volume);
//				if(Global.getInstance().getServer_Speaker_volume() > 0 || Global.getInstance().getServer_Call_volume() > 0) {
//					RingProgressDialogManager.show(getActivity(), "Please wait...", "Sending Volume Value...");
//					new Thread(new SocClient("VOLUME_CONTROL", CommonUtilities.soc_port, getActivity())).start();
//				}
//			}
//		});
				
		pwShowSip.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{

				if(String.valueOf(pwShowSip.getTag()) == "unhidden"){
					etDoorTalkPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());					
					pwShowSip.setTag("hidden");	
					pwShowSip.setBackgroundResource(R.drawable.ic_visibility_off);
				}
				else
				{
					etDoorTalkPassword.setTransformationMethod(null);					
					pwShowSip.setTag("unhidden");	
					pwShowSip.setBackgroundResource(R.drawable.ic_visibility_on);
				}
			}
		});
		
	}
	
	private boolean isNumerical(String number)
	{
		try {
			Integer.valueOf(number);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	protected void showToast(String message)
	{
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
	
	private void saveVolume(){
		//volume control
		if(Global.getInstance().getServer_Speaker_volume() > 0 || Global.getInstance().getServer_Call_volume() > 0) {
//			RingProgressDialogManager.show(SetUpSipDoorTalk.this, "Please wait...", "Sending Volume Value...");
			Global.getInstance().setServer_Speaker_volume(nCurrent_Speaker_Volume);
			Global.getInstance().setServer_Call_volume(nCurrent_Call_Volume);
//			new Thread(new SocClient("VOLUME_CONTROL", CommonUtilities.soc_port, SetUpSipDoorTalk.this)).start();
			Thread vThread = new Thread(new SocClient("VOLUME_CONTROL", CommonUtilities.soc_port));
			vThread.start();
			Log.d(TAG, "saveVolume()");
		}
		
	}
	
}