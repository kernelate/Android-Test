package com.ntek.wallpad.Screens.Fragment;

import java.util.List;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Screens.Fragment.MaterialLockView.Cell;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSettingSecurityPatternReenter extends Fragment {

	private final static String TAG = FragmentSettingSecurityPatternReenter.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	private MaterialLockView materialLockView;
	private Button cancelPatternbtn;
	private Button confirmPatternbtn;
	private TextView patternLabelbtn;
	String zxc;
	private String getValueNgn;
	
	
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
		view = inflater.inflate(R.layout.activity_main, container, false); 
		initializeui();
		context = getActivity();
		return view;
	}

	protected void initializeui()
	{
		materialLockView = (MaterialLockView) view.findViewById(R.id.pattern);
		cancelPatternbtn = (Button) view.findViewById(R.id.pattern_cancel);
		confirmPatternbtn = (Button) view.findViewById(R.id.pattern_confirm);
		patternLabelbtn = (TextView) view.findViewById(R.id.pattern_label);
		
		patternLabelbtn.setText("Confirm saved pattern");
		cancelPatternbtn.setVisibility(View.GONE);
		confirmPatternbtn.setVisibility(View.GONE);
		
		materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() 
		{
			
			@Override
			public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) 
			{
				getValueNgn = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT);
				Log.d(TAG, " simplefuckingpatern " + " SimplePattern " + SimplePattern + " getValueNgn " + getValueNgn);
				if (SimplePattern.length() > 3) {
					
					
				}
				
				
				if (!SimplePattern.equals(getValueNgn))
				{
					materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);
					materialLockView.clearPattern();
				} 
				else 
				{
					materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Correct);
					mOnFragmentClick.changeFragment(null, new FragmentSettingSecurity(), false);
				}
				super.onPatternDetected(pattern, SimplePattern);
			}
			
			@Override
			public void onPatternCellAdded(List<Cell> pattern,String SimplePattern)
			{
				if(SimplePattern.length()==10)
				{
					Log.e("SimplePattern", SimplePattern);
					materialLockView.clearPattern();
					Toast.makeText(getActivity(), "Exceeded the maximum number of pattern input", Toast.LENGTH_SHORT).show();
				}
				super.onPatternCellAdded(pattern, SimplePattern);
			}
			
			
		});
		
	}

}

