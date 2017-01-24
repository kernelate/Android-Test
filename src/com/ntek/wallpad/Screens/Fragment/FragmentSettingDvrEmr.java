package com.ntek.wallpad.Screens.Fragment;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class FragmentSettingDvrEmr extends Fragment implements OnClickListener {

	private static final String TAG = FragmentSettingDvrEmr.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	
	private Button btnDvr;
	private Button btnEmergency;
	
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
		
		view = inflater.inflate(R.layout.fragment_security, container, false);
		initializeUi();
//		context = getActivity();
		return view;
		
	}
	
	
	protected void initializeUi()
	{
		btnDvr = (Button) view.findViewById(R.id.fragment_security_dvr_btn);
		btnEmergency = (Button) view.findViewById(R.id.fragment_security_emergency_btn);
//		btnEmergency.setText(Html.fromHtml("String I am testing<br/><small>(with a sub comment)</small>")); 
		
		btnDvr.setOnClickListener(this);
		btnEmergency.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.fragment_security_dvr_btn :
//			mOnFragmentClick.changeFragment(null, new SettingFragment(), false);
			Log.d(TAG, " settings fragment ");
			break;

		case R.id.fragment_security_emergency_btn :

			break;

		default:
			break;
		}

	}
	
	
}
