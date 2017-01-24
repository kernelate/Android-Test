package com.ntek.wallpad.Screens.Fragment;


import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter.FilterListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.ContactAdapter;
import com.ntek.wallpad.Utils.ContactAdapter.FilterType;
import com.ntek.wallpad.Utils.Contacts;

public class FragmentPhoneSecurityList extends Fragment{

	private ListView lvDeviceList;
	private TextView noDeviceCaptionTextView;
	private View view;
	private ContactAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_phone_device_list, container, false);
		
		initializeUI();
		return view;
	}
	
	public void setSelected(int count) {
		if(count > 0) lvDeviceList.performItemClick(lvDeviceList.getChildAt(0), 0, lvDeviceList.getItemIdAtPosition(0));
		else setContactInfo(-1);
	}
	
	public void setContactInfo(int position) {
		Fragment rightPanel = getFragmentManager().findFragmentById(R.id.phone_rightpanel);
		if(rightPanel instanceof FragmentPhoneContactInformation) {
			FragmentPhoneContactInformation contactInfoFragment = (FragmentPhoneContactInformation) rightPanel;
			if(position < 0) {
				showContactList(false);
				contactInfoFragment.setContactInformation(null);
			} else {
				showContactList(true);
				Contacts contact = (Contacts) adapter.getItem(position);;
				contactInfoFragment.setContactInformation(Integer.toString(contact.getPhoneNumber()));
			}
		}
	}
	
	private void showContactList(boolean show) {
		lvDeviceList.setVisibility(show ? View.VISIBLE: View.INVISIBLE);
		noDeviceCaptionTextView.setVisibility(show ? View.INVISIBLE: View.VISIBLE);
		noDeviceCaptionTextView.setText("When you add a security device, you'll see your contacts here.");
	}
	
	private void initializeUI()
	{
		lvDeviceList = (ListView) view.findViewById(R.id.phone_security_devicelist);
		noDeviceCaptionTextView = (TextView) view.findViewById(R.id.no_device_caption_text_view);
		
		lvDeviceList.setOnItemClickListener(onItemClickListener);
		
		adapter = ContactAdapter.getContactAdapter(getActivity());
		adapter.getFilter().filter(FilterType.Security.toString(), new FilterListener() {
			@Override
			public void onFilterComplete(int count) {
				setSelected(count);
			}
		});
		lvDeviceList.setAdapter(adapter);
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		noDeviceCaptionTextView.setTypeface(font);
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			adapter.setSelectedItem(position);
			adapter.notifyDataSetChanged();
			setContactInfo(position);
		}
	};
}
