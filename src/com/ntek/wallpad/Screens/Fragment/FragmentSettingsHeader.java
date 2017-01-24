package com.ntek.wallpad.Screens.Fragment;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.ntek.wallpad.R;

public class FragmentSettingsHeader extends Fragment
{
	//I NEED TO CREATE A ADDCONFIGURATIONLISTENER BECAUSE I CANT CALL THE ACTUAL METHOD IN BASESCREEN BECAUSE ITS
	//A PROTECTED METHOD SO THATS WHY... ITS REALLY HUSTLE
	public interface OnAddConfigurationListener
	{
		public void addConfigListener(CheckBox[] views);
		public void addConfigListener(EditText[] views);
		public void addConfigListener(Spinner[] views);
		public void addConfigListener(RadioButton[] views);
	}
	
	private static FragmentSettingsHeader mThis;
	
	private View view;
	private ImageView btnBack;
	
	
	public FragmentSettingsHeader() 
	{
		
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_header, container, false);
		addFragment(new FragmentSettingsSip(), new FragmentCommonSetting());
		
		
		btnBack = (ImageView) view.findViewById(R.id.settings_back);
		btnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Log.d("fragsetting", " fragsettings " + " asdopiasdopiasdopdopiaopsidopasidaopsiodpai ");
				getActivity().onBackPressed();
			}
		});
		
		return view;
	}
	
	

	protected void addFragment(Fragment fragList, Fragment fragView){
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (fragList != null)
		{
			ft.add(R.id.setting_screen_right, fragList);
		}
		
		if (fragView != null)
		{
			ft.add(R.id.setting_screen_left, fragView);
		}
		
		ft.commit();
	}
	
	public static FragmentSettingsHeader getInstance()
	{
		if (mThis == null)
		{
			mThis = new FragmentSettingsHeader();
		}
		return mThis;
	}
	
//	private void displayFragment(int position) {
//	    // update the main content by replacing fragments
//	    Fragment fragment = null;
//	    String title = "";
//	    switch (position) {
//	    case 0:
//	        fragment = new FragmentSettingsHeader();
//	        title = "Home";
//	        break;
////	    case 1:
////	        fragment = new Fragmentsetting();
////	        title = "Login";
////	        break;
////	    case 2:
////	        fragment = new RestorePass();
////	        title = "Restore Password";
////	        break;
//
//	    default:
//	        break;
//	    }
//
//	    // update selected fragment and title
//	    if (fragment != null) {
//	        getChildFragmentManager().beginTransaction()
//	                .replace(R.id.tvtitle, fragment).commit();
//	        getSupportActionBar().setTitle(title);
////	        // change icon to arrow drawable
////	        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow);
//	    }
//	}
	
	
}