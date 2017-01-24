package com.ntek.wallpad.Screens.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ntek.wallpad.R;

public class FragmentNewPhoneHeader extends Fragment implements OnClickListener {
	private RelativeLayout btnContacts;
	private RelativeLayout btnDialpad;
	private RelativeLayout btnHistory;
	private RelativeLayout btnCurrentTab;
	
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_new_phone_header, container,
				false);

		initializeUI();
		btnCurrentTab = btnDialpad;
		return view;
	}

	private void initializeUI() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.add(R.id.phone_mainpanel, new FragmentNewPhoneDialpad());
		ft.commit();

		btnContacts = (RelativeLayout) view
				.findViewById(R.id.new_phone_header_contacts);
		btnDialpad = (RelativeLayout) view.findViewById(R.id.new_phone_header_dialpad);
		btnHistory = (RelativeLayout) view.findViewById(R.id.new_phone_header_history);
		
		
		
		
		btnContacts.setOnClickListener(this);
		btnDialpad.setOnClickListener(this);
		btnHistory.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.new_phone_header_dialpad:
			replaceFragment(new FragmentNewPhoneDialpad());
			setSelectedTab(btnDialpad);
			
			break;

		case R.id.new_phone_header_history:
			replaceFragment(new FragmentNewPhoneHistoryCalls());
			setSelectedTab(btnHistory);
			
			break;
			
		case R.id.new_phone_header_contacts:
			replaceFragment(new FragmentNewPhoneContacts());
			setSelectedTab(btnContacts);
			
			break;
			
		

		default:
			break;
		}
	}
	
	private void replaceFragment(Fragment frmList) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.phone_mainpanel, frmList);
		ft.commit();
	}


	private void setSelectedTab(RelativeLayout relativeLayout) {
		btnCurrentTab.setBackgroundColor(0x4caf50);
		btnCurrentTab = relativeLayout;
		btnCurrentTab.setBackgroundResource(R.drawable.borded_selected_tab);
	}
}
