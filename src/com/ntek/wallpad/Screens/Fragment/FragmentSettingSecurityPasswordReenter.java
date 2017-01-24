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

public class FragmentSettingSecurityPasswordReenter extends Fragment implements OnClickListener {

	private final static String TAG = FragmentSettingSecurityPasswordReenter.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	private Button pwcancelBtn;
	private Button pwokBtn;
	private String getValueNgnPW;
	private String getValueNgnPattern;
	private EditText securityPw;
	String inputpwTxt;
	
	
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
		Log.d(TAG, " FragmentSettingSecurityPasswordReenter ");
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_security_password, container, false); 
		initializeui();
		context = getActivity();
		return view;
	}

	protected void initializeui()
	{
		securityPw = (EditText) view.findViewById(R.id.security_password_et);
		securityPw.requestFocus();
		pwcancelBtn = (Button) view.findViewById(R.id.security_password_cancel);
		pwokBtn = (Button) view.findViewById(R.id.security_password_confirm);
		
		pwcancelBtn.setOnClickListener(this);
		pwokBtn.setOnClickListener(this);
		
		
		TextWatcher watch = new TextWatcher() 
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				inputpwTxt = securityPw.getText().toString();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		};
		securityPw.addTextChangedListener(watch);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.security_password_cancel:
			mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);
			hideInputMethod();
			break;
			
		case R.id.security_password_confirm :
			inputpwTxt = securityPw.getText().toString();
			getValueNgnPW = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, NgnConfigurationEntry.SCREEN_LOCK_PASSWORD_DEFAULT);
			getValueNgnPattern = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT); 
			
			
			
			Log.d(TAG, " inputpwTxt " + inputpwTxt + " getValueNgnPW " + getValueNgnPW + " getValueNgnPattern " + getValueNgnPattern);
			if (inputpwTxt.equals(getValueNgnPW))
			{
				hideInputMethod();
				mOnFragmentClick.changeFragment(null, new FragmentSettingSecurity(), false);
			}
			else
			{
				securityPw.setText("");
				Toast.makeText(getActivity(), " Please try again ", Toast.LENGTH_SHORT).show();
			}
			
			break;

		default:
			break;
		}
	}
	
	public void hideInputMethod() 
	{
		Log.d(TAG, "hideInputMethod");
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
