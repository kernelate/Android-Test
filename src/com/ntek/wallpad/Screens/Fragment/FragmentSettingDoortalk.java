package com.ntek.wallpad.Screens.Fragment;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class FragmentSettingDoortalk extends Fragment implements OnClickListener {

	private static final String TAG = FragmentSettingDoortalk.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	
	private Button btnClient;
	private Button btnDt;
	private Button btnSmart;
	
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
		
		view = inflater.inflate(R.layout.doortalk_setting, container, false);
		initializeUi();
		context = getActivity();
		hideInputMethod();
		return view;
		
	}
	
	protected void initializeUi()
	{
		btnClient = (Button) view.findViewById(R.id.dt_setting_client_setup_btn);
		btnDt = (Button) view.findViewById(R.id.dt_setting_dt_setup);
		btnSmart = (Button) view.findViewById(R.id.dt_setting_smart_setup);
		
		
		btnClient.setOnClickListener(this);
		btnDt.setOnClickListener(this);
		btnSmart.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.dt_setting_client_setup_btn :
			mOnFragmentClick.changeFragment(null, new FragmentSettingDTClientSetup(), false);
			break;

		case R.id.dt_setting_dt_setup :
			FragmentCommonSetting.selectType = FragmentCommonSetting.SERVER_CONFIG;
			mOnFragmentClick.changeFragment(null, new FragmentSettingAuto(), false);
			break;

		case R.id.dt_setting_smart_setup :

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
