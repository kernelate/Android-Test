package com.ntek.wallpad.Screens.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentAutomationSettingAutomatic;
import com.ntek.wallpad.Screens.Fragment.FragmentAutomationSettingManual;

public class FragmentAutomationSetting extends Fragment implements OnClickListener
{
	private View view;
	private Button btnDeviceList;
	private Button btnAutomation;
	private Button btnManual;
	private TextView tvDoorDevice;
	private TextView tvDeviceConfig;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_automation_setting, container, false);
		
		initializeUi();
		
		return view;
	}

	private void initializeUi() 
	{
		btnDeviceList = (Button) view.findViewById(R.id.automation_settings_button_device_list);
		btnAutomation = (Button) view.findViewById(R.id.automation_settings_button_automatic);
		btnManual = (Button) view.findViewById(R.id.automation_settings_button_manual);
		tvDeviceConfig = (TextView) view.findViewById(R.id.automation_setting_textview_doordevices);
		tvDoorDevice = (TextView) view.findViewById(R.id.automation_setting_textview_mydoordevice);
		
		
		Typeface tfOpenSansRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		tvDeviceConfig.setTypeface(tfOpenSansRegular);
		tvDoorDevice.setTypeface(tfOpenSansRegular);
		
		Typeface tfOpenSansSemibold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		btnAutomation.setTypeface(tfOpenSansSemibold);
		btnDeviceList.setTypeface(tfOpenSansSemibold);
		btnManual.setTypeface(tfOpenSansSemibold);
		
		btnDeviceList.setOnClickListener(this);
		btnAutomation.setOnClickListener(this);
		btnManual.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) 
	{
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		switch (v.getId()) {
		case R.id.automation_settings_button_device_list:
			
			replaceFragment(ft, new FragmentAutomationSetting(), new FragmentAutomationSettingsDeviceList());
			
			break;
			
		case R.id.automation_settings_button_automatic:
			replaceFragment(ft, new FragmentAutomationSetting(), new FragmentAutomationSettingAutomatic());
			break;
			
		case R.id.automation_settings_button_manual:
			
			replaceFragment(ft, new FragmentAutomationSetting(), new FragmentAutomationSettingManual());
			
			break;

		default:
			break;
		}
	}

	private void replaceFragment(FragmentTransaction ft,Fragment frmList, Fragment frmView) 
	{
		ft.replace(R.id.automation_rightpanel, frmView);
		ft.commit();
	}
	
}
