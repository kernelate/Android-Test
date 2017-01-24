package com.ntek.wallpad.Screens.Fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ntek.wallpad.R;

public class FragmentEventSettingsAutomatic extends Fragment{
	View view;

	private ListView availableDoortalk;
	private Button scan;
	private TextView availableDoortalktextview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_automatic, container, false);

		initializeUi();


		return view;
	}

	private void initializeUi()
	{
		availableDoortalk = (ListView) view.findViewById(R.id.fragment_setting_automatic_availableDoortalkListview);
		scan = (Button) view.findViewById(R.id.fragment_setting_automatic_button_scan);
		availableDoortalktextview = (TextView) view.findViewById(R.id.fragment_setting_automatic_textview_available_doortalk);
		Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		Typeface fontSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		
		availableDoortalktextview.setTypeface(fontSemiBold);
		scan.setTypeface(fontSemiBold);
	}

}
