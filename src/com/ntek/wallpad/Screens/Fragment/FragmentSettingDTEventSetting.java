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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

public class FragmentSettingDTEventSetting extends Fragment {
	
	private static final String TAG = FragmentSettingDTEventSetting.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	
	private ListView lvEvent;

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
		view = inflater.inflate(R.layout.doortalk_eventsetting, container, false);
		initializeUi();
		context = getActivity();
		return view;
	}
	
	protected void initializeUi()
	{
		lvEvent = (ListView) view.findViewById(R.id.dt_list);
	}

}
