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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class FragmentSettingDTClientSetup extends Fragment implements OnClickListener {

	private static final String TAG = FragmentSettingDTClientSetup.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	
//	private Button btnMyprofile;
	private Button btnEventsetting;
	
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
		
		view = inflater.inflate(R.layout.doortalk_client_setup, container, false);
		initializeUi();
		context = getActivity();
		return view;
	}
	
	protected void initializeUi()
	{
		btnEventsetting = (Button) view.findViewById(R.id.dt_event_setting);
//		btnMyprofile = (Button) view.findViewById(R.id.dt_myprofile);
		
		btnEventsetting.setOnClickListener(this);
//		btnMyprofile.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
//		case R.id.dt_myprofile :
//			mOnFragmentClick.changeFragment(null, new FragmentSettingDTMyProfile(), false);
//			break;

		case R.id.dt_event_setting :
			Log.d(TAG, "nmnmzxnvmzcnvxcm,vnm,");
			FragmentCommonSetting.selectType = FragmentCommonSetting.EVENT_SETTING;
			mOnFragmentClick.changeFragment(null, new FragmentSettingAuto(), false);
			break;

		default:
			break;
		}
	}
	
	

}
