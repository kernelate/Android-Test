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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class FragmentSettingSecurity extends Fragment implements OnClickListener {

	private final static String TAG = FragmentSettingSecurity.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	private Button noneBtn;
	private Button slideBtn;
	private Button patternBtn;
	private Button pinBtn;
	private Button passwordBtn;
	public static final int LOCKSCREEN_NONE = 0;
	public static final int LOCKSCREEN_SLIDE = 1;
	public static final int LOCKSCREEN_PATTERN = 2;
	public static final int LOCKSCREEN_PIN = 3;
	public static final int LOCKSCREEN_PASSWORD = 4;
	public static int selectType;
	
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
		
		view = inflater.inflate(R.layout.security, container, false); 
		initializeui();
		context = getActivity();
		return view;
		
	}
	
	protected void initializeui()
	{
		noneBtn = (Button) view.findViewById(R.id.security_none);
		slideBtn = (Button) view.findViewById(R.id.security_slide);
		patternBtn = (Button) view.findViewById(R.id.security_pattern);
		pinBtn = (Button) view.findViewById(R.id.security_pin);
		passwordBtn = (Button) view.findViewById(R.id.security_password);
		
		noneBtn.setOnClickListener(this);
		slideBtn.setOnClickListener(this);
		patternBtn.setOnClickListener(this);
		pinBtn.setOnClickListener(this);
		passwordBtn.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.security_none :
			selectType = LOCKSCREEN_NONE;
			mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, null);
			mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, null);
			mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PIN, null);
			
			mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);
			break;

		case R.id.security_slide :
			selectType = LOCKSCREEN_SLIDE;
			
			mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, null);
			mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PIN, null);
			mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, null);
			
			mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);
			break;

		case R.id.security_pattern :
			selectType = LOCKSCREEN_PATTERN;
			mOnFragmentClick.changeFragment(null, new FragmentSettingSecurityPattern(), false);
			break;

		case R.id.security_pin :
			selectType = LOCKSCREEN_PIN;
			mOnFragmentClick.changeFragment(null, new FragmentSettingSecurityPin(), false);
			break;

		case R.id.security_password :
			selectType = LOCKSCREEN_PASSWORD;
		mOnFragmentClick.changeFragment(null, new FragmentSettingSecurityPassword(), false);
			break;

		default:
			break;
		}
	}

}
