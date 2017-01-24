package com.ntek.wallpad.Screens.Fragment;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FragmentSettingSecurityPinReenter extends Fragment implements OnClickListener {

	private final static String TAG = FragmentSettingSecurityPinReenter.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	private Button cancelPinbtn;
	private Button okPinbtn; 
	private EditText securityPin;
	String inputpwTxt;
	String getValueNgn;
	private String getValueNgnPin;
	private String getValueNgnPattern;
	
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
		view = inflater.inflate(R.layout.fragment_setting_security_pin, container, false); 
		initializeui();
		showInputMethod();
		context = getActivity();
		return view;
	}

	protected void initializeui()
	{
		cancelPinbtn = (Button) view.findViewById(R.id.security_pin_cancel);
		okPinbtn = (Button) view.findViewById(R.id.security_pin_confirm);
		securityPin = (EditText) view.findViewById(R.id.security_pin_et);
		securityPin.requestFocus();
		cancelPinbtn.setOnClickListener(this);
		okPinbtn.setOnClickListener(this);
		
		TextWatcher watch = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				inputpwTxt = securityPin.getText().toString();				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		};
		securityPin.addTextChangedListener(watch);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.security_pin_cancel :
			mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);
			hideInputMethod();
			break;
			
		case R.id.security_pin_confirm :
			getValueNgnPin = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PIN, NgnConfigurationEntry.SCREEN_LOCK_PIN_DEFAULT);
			getValueNgnPattern = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT); 
			inputpwTxt = securityPin.getText().toString();			
			Log.d(TAG, " inputpwTxt " + inputpwTxt + " getValueNgnPin " + getValueNgnPin + " getValueNgnPattern " + getValueNgnPattern);
			
			if (inputpwTxt.equals(getValueNgnPin)) 
			{
				hideInputMethod();
				mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, null);
				mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, null);
				mOnFragmentClick.changeFragment(null, new FragmentSettingSecurity(), false);
			}
			else
			{
				securityPin.setText("");
				Toast.makeText(getActivity(), " Try again ", Toast.LENGTH_SHORT).show();
			}
			
			break;

		default:
			break;
		}
		
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
