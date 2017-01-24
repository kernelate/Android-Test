package com.ntek.wallpad.Screens.Fragment;

import com.ntek.wallpad.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentPhoneCall extends Fragment{

	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_call_layout, container, false);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.phone_call_panel_callinfo, new FragmentPhoneCallVoice());
		ft.add(R.id.phone_call_panel_menu, new FragmentPhoneCallMenu());
		ft.commit();
		
		return view;
	}
	

}
