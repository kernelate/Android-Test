package com.ntek.wallpad.Screens.Fragment;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.lockscreen.LockScreenPasswordActivity;
import com.ntek.wallpad.lockscreen.LockScreenPatternActivity;
import com.ntek.wallpad.lockscreen.LockScreenPinActivity;
import com.ntek.wallpad.lockscreen.LockScreenSwipeActivity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class FragmentSettingScreenLock extends Fragment implements OnClickListener {

	private final static String TAG = FragmentSettingScreenLock.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	private Button screenlockBtn;
	private String getValueNgnPW;
	private String getValueNgnPattern;
	private String getValueNgnPIN;
	
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
		Log.d(TAG, " selectype " + FragmentSettingSecurity.selectType);
		getValueNgnPW = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, NgnConfigurationEntry.SCREEN_LOCK_PASSWORD_DEFAULT);
		getValueNgnPattern = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT);
		getValueNgnPIN = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PIN, NgnConfigurationEntry.SCREEN_LOCK_PIN_DEFAULT);
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.screen_lock, container, false); 
		initializeui();
		context = getActivity();
		hideInputMethod();
		return view;
	}

	protected void initializeui()
	{
		screenlockBtn = (Button) view.findViewById(R.id.screen_lock_btn);
		
		screenlockBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.screen_lock_btn :
			Log.d(TAG, " getValueNgnPW " + getValueNgnPW + " getValueNgnPattern " + getValueNgnPattern + " getValueNgnPIN " + getValueNgnPIN);
			
			
			if (FragmentSettingSecurity.selectType == 1 ) 
			{
				Log.d(TAG, " 11111 ");
				mOnFragmentClick.changeFragment(null, new FragmentSettingSecurity(), false);
			}
			else if ( FragmentSettingSecurity.selectType == 2 && !getValueNgnPattern.isEmpty()) 
			{
				Log.d(TAG, " 22222 pattern ");
				mOnFragmentClick.changeFragment(null, new FragmentSettingSecurityPatternReenter(), false);
			}
			else if ( FragmentSettingSecurity.selectType == 3  && !getValueNgnPIN.isEmpty()) 
			{
				Log.d(TAG, " 3333 pin");
				mOnFragmentClick.changeFragment(null, new FragmentSettingSecurityPinReenter(), false);
				
			}
			else if ( FragmentSettingSecurity.selectType == 4 && !getValueNgnPW.isEmpty())
			{
				Log.d(TAG, " 4444 pw");
				mOnFragmentClick.changeFragment(null, new FragmentSettingSecurityPasswordReenter(), false);
			}
			else
			{
				Log.d(TAG, " 55555555 ");
				mOnFragmentClick.changeFragment(null, new FragmentSettingSecurity(), false);
			}
			
//			Intent intent = new Intent (getActivity(), LockScreenSwipeActivity.class);
//			startActivity(intent);
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
