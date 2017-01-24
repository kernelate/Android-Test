package com.ntek.wallpad;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.ntek.wallpad.Screens.BaseScreen;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;

public class FragmentBaseScreenSettings extends BaseScreen implements OnAddConfigurationListener, OnChangeFragmentListener
{
	private final static String TAG = FragmentBaseScreenSettings.class.getCanonicalName();
	private FragmentTransaction ft;
	
	public FragmentBaseScreenSettings() {
		super(SCREEN_TYPE.SETTINGS_T, TAG);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.screen_fragment_main);
		Log.d(TAG, " fragmentbasesetting ");
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, FragmentSettingsHeader.getInstance()).commit();
		}
	}

	@Override
	public void addConfigListener(CheckBox[] views) {
		for (CheckBox checkBox : views) 
		{
			addConfigurationListener(checkBox);
		}
	}

	@Override
	public void addConfigListener(EditText[] views) {
		for (EditText editText : views) 
		{
			addConfigurationListener(editText);
		}
	}

	@Override
	public void addConfigListener(Spinner[] views) {
		for (Spinner spinner : views) 
		{
			addConfigurationListener(spinner);
		}
	}

	@Override
	public void addConfigListener(RadioButton[] views) {
		for (RadioButton radioButton : views) 
		{
			addConfigurationListener(radioButton);
		}
	}

	@Override
	public void changeFragment(Fragment fragList, Fragment fragView, boolean addToBackStack) {
		// TODO Auto-generated method stub
		replaceFragment(fragList, fragView, addToBackStack);
	}
	
	protected void addFragment(Fragment fragList, Fragment fragView){
		ft = getFragmentManager().beginTransaction();
		if (fragList != null)
		{
			ft.add(R.id.setting_screen_left, fragList);
		}
		
		if (fragView != null)
		{
			ft.add(R.id.setting_screen_right, fragView);
		}
		
		ft.addToBackStack(null);
		ft.commit();
	}
	
	protected void replaceFragment(Fragment fragList, Fragment fragView, boolean addToBackStack){
		ft = getFragmentManager().beginTransaction();
		if (fragList != null)
		{
			ft.replace(R.id.setting_screen_left, fragList);
		}
		
		if (fragView != null)
		{
			ft.replace(R.id.setting_screen_right, fragView);
		}
		
		if (addToBackStack)
		{
			ft.addToBackStack(null);
		}
		else
		{
//			//clear the back stack
			if (getFragmentManager().getBackStackEntryCount() > 0)
			{
			    FragmentManager.BackStackEntry first = getFragmentManager().getBackStackEntryAt(0);
		        getFragmentManager().popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		}
		
		ft.commit();
	}
}
