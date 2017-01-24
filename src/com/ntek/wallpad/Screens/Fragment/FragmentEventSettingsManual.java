package com.ntek.wallpad.Screens.Fragment;

import com.ntek.wallpad.R;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FragmentEventSettingsManual extends Fragment{
	View view;
	private Button connect;
	private EditText ipAddress;
	private EditText port;
	private TextView manualText;
	private TextView ipAddressText;
	private TextView portText;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_setting_manual, container, false);
		initializeUi();

		return view;
	}

	private void initializeUi()
	{
		connect = (Button) view.findViewById(R.id.fragment_setting_manual_button_connect);
		ipAddress = (EditText) view.findViewById(R.id.fragment_setting_manual_edittext_ip_address);
		port = (EditText) view.findViewById(R.id.fragment_setting_manual_edittext_port);

		Typeface fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		Typeface fontSemiBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");

		manualText.setTypeface(fontSemiBold);
		ipAddressText.setTypeface(fontSemiBold);
		ipAddress.setTypeface(fontRegular);
		portText.setTypeface(fontSemiBold);
		port.setTypeface(fontRegular);




	}

}
