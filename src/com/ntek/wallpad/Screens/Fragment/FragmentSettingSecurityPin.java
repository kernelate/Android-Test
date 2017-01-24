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
import android.widget.TextView;

public class FragmentSettingSecurityPin extends Fragment implements OnClickListener {

	private final static String TAG = FragmentSettingSecurityPin.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	private EditText securityPin;
	private Button cancelPin;
	private Button okPin; 
	private TextView pinLabel;
	String inputpwTxt;
	String getValueNgn;
	Boolean isFirstClick = false;
	
	
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
		cancelPin = (Button) view.findViewById(R.id.security_pin_cancel);
		okPin = (Button) view.findViewById(R.id.security_pin_confirm);
		pinLabel = (TextView) view.findViewById(R.id.security_pin_label);
		securityPin = (EditText) view.findViewById(R.id.security_pin_et);
		securityPin.requestFocus();
		
		okPin.setEnabled(false);
		okPin.setOnClickListener(this);
		cancelPin.setOnClickListener(this);
		
		
		TextWatcher watch = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				inputpwTxt = securityPin.getText().toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (start >= 3) {
					
					okPin.setEnabled(true);
					pinLabel.setText("Touch Continue when done");
				}
				else
				{
//					pinLabel.setText("Password must be at least 4 characters");
				}
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		};
		securityPin.addTextChangedListener(watch);
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

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.security_pin_cancel :
			mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);
			hideInputMethod();
			break;

		case R.id.security_pin_confirm :
			if ( securityPin.getText().toString().length() > 0 ) 
			{
				
				if (!isFirstClick) 
				{
					mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PIN, inputpwTxt, true);
					pinLabel.setText("Confirm your PIN");
					okPin.setText("OK");
					okPin.setEnabled(false);
					getValueNgn = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PIN, NgnConfigurationEntry.SCREEN_LOCK_PIN_DEFAULT);
					
					Log.d(TAG, " ifififififififi " + securityPin.getText() + " inputtext " + inputpwTxt + " getValueNgn " + getValueNgn);
					securityPin.setText("");
					isFirstClick = true;
				}
				else
				{
					inputpwTxt = securityPin.getText().toString();
					if (inputpwTxt.equals(getValueNgn)) 
					{
						Log.d(TAG, " ang bagal ng net ");
						mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);
						
						mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, null);
						mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, null);
						
						hideInputMethod();
					}
					else
					{
						Log.d(TAG, " ffffffffffffffff ");
						pinLabel.setText("Password don't match");
						
					}
					securityPin.setText("");
					isFirstClick = false;
				}
			}
			else
			{
				Log.d(TAG, " elsesleslesleleslelslee ");
				
			}
			
			break;

		default:
			break;
		}
	}

}
