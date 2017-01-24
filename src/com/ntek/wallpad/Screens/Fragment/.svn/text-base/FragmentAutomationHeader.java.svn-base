package com.ntek.wallpad.Screens.Fragment;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.Screens.Fragment.FragmentAutomationMain;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;

public class FragmentAutomationHeader extends Fragment{

	public Button btnSetting;
	
	FragmentTransaction ft;
	public View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_automation_header, container, false);
		initializeUI();
		
		return view;
	}
	
	private void initializeUI()
	{
		ft = getFragmentManager().beginTransaction();
		ft.add(R.id.automation_leftpanel, new FragmentAutomationSetting());
		ft.add(R.id.automation_rightpanel, new FragmentAutomationSettingsDeviceList());
		ft.commit();
		
		btnSetting = (Button) view.findViewById(R.id.automation_header_setting);
		btnSetting.setVisibility(View.GONE);
	}
	
}
