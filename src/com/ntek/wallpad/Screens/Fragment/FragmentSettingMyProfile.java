package com.ntek.wallpad.Screens.Fragment;

import java.util.HashMap;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnNetworkService;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.network.Global;
import com.smarttalk.sip.AutoProvisionDialog;
import com.smarttalk.sip.AutoProvisionXmlReader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSettingMyProfile extends Fragment {
	
	private final static String TAG = FragmentSettingMyProfile.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private INgnNetworkService mNetworkService;
	private HashMap<String,String> sipSetupHashMap = new HashMap<String, String>();
	private View view;
	private Context context;
	private OnAddConfigurationListener mOnAddConfig;
	private Button btnShowPass;
	private EditText etPass;
	private TextView tvDisplayname;
	private TextView tvSIPNumber;
	private TextView tvIPAddress;
	private TextView tvPort;
	private TextView tvPassword;
	private EditText etDisplayname;
	private EditText etSIPNumber;
	private EditText etIPAddress;
	private EditText etPort;
	
	private OnChangeFragmentListener mOnFragmentClick;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnAddConfig = (OnAddConfigurationListener)activity;
			mOnFragmentClick = (OnChangeFragmentListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.myprofile, container, false);
		initializeUi();
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
		
		if(mNetworkService == null)
		{
			mNetworkService = NgnEngine.getInstance().getNetworkService();
		}
		context = getActivity();
		hideInputMethod();
		return view;
	}
	
	protected void initializeUi()
	{
		tvDisplayname = (TextView) view.findViewById(R.id.myprofile_tv_displayname);
		tvSIPNumber = (TextView) view.findViewById(R.id.myprofile_tv_sip);
		tvIPAddress = (TextView) view.findViewById(R.id.myprofile_tv_ipadd);
		tvPort = (TextView) view.findViewById(R.id.myprofile_tv_port);
		tvPassword = (TextView) view.findViewById(R.id.myprofile_tv_password);
		
		etDisplayname = (EditText) view.findViewById(R.id.myprofile_et_displayname);
		etSIPNumber = (EditText) view.findViewById(R.id.myprofile_et_sip);
		etIPAddress = (EditText) view.findViewById(R.id.myprofile_et_ipadd);
		etPort = (EditText) view.findViewById(R.id.myprofile_et_port);
		etPass = (EditText) view.findViewById(R.id.myprofile_et_password);
		
		btnShowPass = (Button) view.findViewById(R.id.showpass);
		btnShowPass.setBackgroundResource(R.drawable.ic_visibility_off);
		
		btnShowPass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				
				if(String.valueOf(btnShowPass.getTag()) == "unhidden"){
					etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());					
					btnShowPass.setTag("hidden");	
					btnShowPass.setBackgroundResource(R.drawable.ic_visibility_off);
				}
				else
				{
					etPass.setTransformationMethod(null);					
					btnShowPass.setTag("unhidden");	
					btnShowPass.setBackgroundResource(R.drawable.ic_visibility_on);
				}
			}
		});
		
		etDisplayname.setEnabled(false);
		etSIPNumber.setEnabled(false);
		etIPAddress.setEnabled(false);
		etPort.setEnabled(false);
		etPass.setEnabled(false);
		
		
		
		if((mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME)).equals("John Doe"))
		{
			AutoProvisionDialog.getInstance().showNotifMessage(getActivity(), getString(R.string.string_no_profile), null);
			Log.d(TAG, " john doe 1" + mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME).equals("John Doe") );
		}
		else
		{
			loadConfig1();
			Log.d(TAG, " john doe 2" + mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME) );
		}
	}
	
	private void loadConfig1()
	{
		Log.d(TAG, " loadconfig ");
		etDisplayname.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME));
		etSIPNumber.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI));
		etIPAddress.setText(mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
		etPass.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnStringUtils.emptyValue()));
		etPort.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT)));
	}
	
	public void showInputMethod() 
	{
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	
	public void hideInputMethod() 
	{
		Log.d(TAG, "hideInputMethod");
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
}
