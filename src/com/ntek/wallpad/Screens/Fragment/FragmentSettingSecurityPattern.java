package com.ntek.wallpad.Screens.Fragment;

import java.util.List;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Screens.Fragment.MaterialLockView.Cell;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.lockscreen.LockScreenPatternActivity;
import com.smarttalk.sip.AutoProvisionXmlReader;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSettingSecurityPattern extends Fragment implements OnClickListener {

	private final static String TAG = FragmentSettingSecurityPattern.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	private MaterialLockView materialLockView;
	private Button cancelPattern;
	private Button confirmPattern;
	private TextView patternLabel;
	String getSimplePatternValue;
	String setSimplePatternValue;
	public boolean isConfirm = true;
	Boolean isPattern = false;
	Boolean isCancel = false;
	
	
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
		cancelPattern = (Button) view.findViewById(R.id.pattern_cancel);
		confirmPattern = (Button) view.findViewById(R.id.pattern_confirm);
		patternLabel = (TextView) view.findViewById(R.id.pattern_label);
 		
		confirmPattern.setEnabled(false);
//		qwe = (Button) view.findViewById(R.id.qwe);
//		qwe.setOnClickListener(this);
		cancelPattern.setOnClickListener(this);
		confirmPattern.setOnClickListener(this);
		
		materialLockView.setOnPatternListener(new MaterialLockView.OnPatternListener() 
		{
			@Override
			public void onPatternDetected(List<MaterialLockView.Cell> pattern, String SimplePattern) 
			{
				Log.e("SimplePattern detected", SimplePattern + " zxc " + setSimplePatternValue + " asd " + getSimplePatternValue);
				if (SimplePattern.length() > 3) {
					
					getSimplePatternValue = SimplePattern;
					confirmPattern.setEnabled(true);
					cancelPattern.setText("Retry ");
					patternLabel.setText("Pattern Recorded");
					
				}
				
				
				if (!SimplePattern.equals(setSimplePatternValue))
				{
					materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Wrong);
					materialLockView.clearPattern();
				} 
				else 
				{
					materialLockView.setDisplayMode(MaterialLockView.DisplayMode.Correct);

					Log.d(TAG, " asdasdasdasdasdasdasdasd "+ SimplePattern + " zxc " + setSimplePatternValue + " asd " + getSimplePatternValue);
					patternLabel.setText("Your new unlock pattern");
					confirmPattern.setText("Confirm");
					cancelPattern.setText("Cancel");
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

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.pattern_cancel :
			Log.d(TAG, " back back back " + " asd " + getSimplePatternValue + " cancelPattern " + cancelPattern.getText());
			materialLockView.clearPattern();
			cancelPattern.setText("Cancel");
			patternLabel.setText("Draw pattern to unlock");
			confirmPattern.setEnabled(false);
			if (!isCancel && getSimplePatternValue == null) 
			{
				Log.d(TAG, " asd is null ");
				mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);
			}
			else
			{
				Log.d(TAG, " asd is not null ");
				getSimplePatternValue = null;
				isCancel = false;
			}
			
			break;
			
		case R.id.pattern_confirm :
			Log.d(TAG, " asd " + getSimplePatternValue);
			
			if (setSimplePatternValue == null)
			{
				Log.d(TAG, " false false false false  " + getSimplePatternValue + " " + setSimplePatternValue);
				mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, getSimplePatternValue, true);
				materialLockView.clearPattern();
				patternLabel.setText("Draw pattern again to confirm");
				setSimplePatternValue = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT);
				confirmPattern.setEnabled(false);
				isPattern = true;
			}
			else
			{
				Log.d(TAG, " true true true true  " + getSimplePatternValue + " " + setSimplePatternValue);
				
				
				if(getSimplePatternValue.equals(setSimplePatternValue.toString()))
				{
					Log.d(TAG, " oooooooooooooooooooooooooooooooooooooooooo ");
					mOnFragmentClick.changeFragment(null, new FragmentSettingScreenLock(), false);
					mConfigurationService.putBoolean(NgnConfigurationEntry.SCREEN_LOCK_ASD, isConfirm, true);
				}
				else
				{
					materialLockView.clearPattern();
				}
				
				mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PIN, null);
				mConfigurationService.putString(NgnConfigurationEntry.SCREEN_LOCK_PASSWORD, null);

//				mConfigurationService.putInt(NgnConfigurationEntry.SCREEN_LOCK, 1, true);
				isPattern = false;
			}
			break;
			
//		case R.id.qwe :
//			zxc = mConfigurationService.getString(NgnConfigurationEntry.SCREEN_LOCK_PATTERN, NgnConfigurationEntry.SCREEN_LOCK_PATTERN_DEFAULT);
//			Log.d(TAG, " zxc " + zxc);
//			Intent intent = new Intent(getActivity(), LockScreenActivity.class);
//			startActivity(intent);
//			break;

		default:
			break;
		}
	}
	

	
	
}
