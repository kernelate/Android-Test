package com.ntek.wallpad.Screens.Fragment;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter.FilterListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.ContactAdapter;
import com.ntek.wallpad.Utils.ContactAdapter.FilterType;
import com.ntek.wallpad.Utils.ContactOptionDialogFragment;
import com.ntek.wallpad.Utils.Contacts;

public class FragmentNewPhoneContacts extends Fragment implements OnClickListener {

	private static final String TAG = FragmentNewPhoneContacts.class.getCanonicalName();
	public static final int ALL_TAB = 2; 
	public static final int CLIENT_TAB = 1; 
	public static final int DOORTALK_TAB = 0;
	private Button btnAll;
	private Button btnPhone;
	private Button btnSecurity;
	private Button btnAdd;
	private EditText txtsearch;
	private ListView lstContactList;
	private TextView noContactCaptionTextView;
	private ContactAdapter adapter;
	private static int selectedTab = 1;
	public static Contacts selectedContact;
	private BroadcastReceiver broadcastReceiver;
	private View view;
	private Contacts contact;
	final Context context = getActivity();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		Log.d(TAG, "onCreateView()");
		view = inflater.inflate(R.layout.fragment_new_phone_contacts, container, false);
		initializeUI();
		return view;
	}

	private void initializeUI()
	{
		btnAll = (Button) view.findViewById(R.id.phone_contacts_button_all);
		btnAdd = (Button) view.findViewById(R.id.add);
		btnPhone = (Button) view.findViewById(R.id.phone_contacts_button_phone);
		btnSecurity = (Button) view.findViewById(R.id.phone_contacts_button_security);
		lstContactList = (ListView) view.findViewById(R.id.phone_contacts_listview_contactlist);
		txtsearch = (EditText) view.findViewById(R.id.search_bar);
		noContactCaptionTextView = (TextView) view.findViewById(R.id.no_contact_caption_text_view);
		adapter =  ContactAdapter.getContactAdapter(getActivity());
		lstContactList.setAdapter(adapter);
		
		lstContactList.setOnItemClickListener(onItemClickListener);
		lstContactList.setOnItemLongClickListener(contactListItemLongClick);
		
		btnAll.setOnClickListener(this);
		btnPhone.setOnClickListener(this);
		btnSecurity.setOnClickListener(this);
		
		btnAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, " bxcbmzbczxcbmbcxmczxcncbnx ");
//				 TODO Auto-generated method stub
				Bundle bundle = new Bundle();
				bundle.putString("title", "Add Contact");				
				DialogPhoneContactsAddEdit dialogContacts = new DialogPhoneContactsAddEdit();
				dialogContacts.setArguments(bundle);
				dialogContacts.show(getFragmentManager(), "add");
				dialogContacts.setCancelable(false);
				
			}
			
		});
		
		txtsearch.addTextChangedListener(new TextWatcher() {

		   public void beforeTextChanged(CharSequence s, int start, 
		     int count, int after) {
		   }

		   public void onTextChanged(CharSequence s, int start, 
		     int before, int count) {
			   adapter.getFilter().filter(s.toString());
		   }

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
            }
        });
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		noContactCaptionTextView.setTypeface(font);
		btnAll.setTypeface(font);
		btnPhone.setTypeface(font);
		btnSecurity.setTypeface(font);
	}

	public void setSelected(int count) {
		if(count > 0) lstContactList.performItemClick(lstContactList.getChildAt(0), 0, lstContactList.getItemIdAtPosition(0));
		else setContactInfo(-1);
	}
	
	public int getSelectedTab() {
		return selectedTab; 
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.phone_contacts_button_all:
//			setSelectedTab(btnAll, ALL_TAB);
			Log.d(TAG, " all all all all ");
			adapter.getFilter().filter(FilterType.All.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;
			
		case R.id.phone_contacts_button_phone:
			
			Log.d(TAG, "phone phone phone phone phone ");
//			setSelectedTab(btnPhone, CLIENT_TAB);
			adapter.getFilter().filter(FilterType.Phone.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;
			
		case R.id.phone_contacts_button_security:
			Log.d(TAG, "security security security security security ");
//			setSelectedTab(btnSecurity, DOORTALK_TAB);
			adapter.getFilter().filter(FilterType.Security.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;

		default:
			break;
		}
	}
	
	public void setContactInfo(int position) {		
	
		Log.d(TAG, "setContactInfo");
		Bundle bundle = new Bundle();
		bundle.putParcelable("contact", (Contacts) adapter.getItem(position));
		bundle.putString("title", "Edit Contact");
		DialogPhoneContactInformation optionDialogFragment = new DialogPhoneContactInformation();
		optionDialogFragment.setArguments(bundle);
		optionDialogFragment.show(getFragmentManager(), "edit");
		}
	
	private void showContactList(boolean show) {
		lstContactList.setVisibility(show ? View.VISIBLE: View.INVISIBLE);
		noContactCaptionTextView.setVisibility(show ? View.INVISIBLE: View.VISIBLE);
		switch (getSelectedTab()) {
		default:
		case 1:
			noContactCaptionTextView.setText("When you add a client, you'll see your contacts here.");
			break;
		case 2:
			noContactCaptionTextView.setText("When you add a client or security device, you'll see your contacts here.");
			break;
		case 0:
			noContactCaptionTextView.setText("When you add a security device, you'll see your contacts here.");
			break;
		}
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			Log.d(TAG, " onitemclitlistener ");
			adapter.setSelectedItem(position);
			adapter.notifyDataSetChanged();
			setContactInfo(position);					
		}
	};
	
	AdapterView.OnItemLongClickListener contactListItemLongClick = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3) {
			Bundle bundle = new Bundle();
			bundle.putParcelable("contact", (Contacts) adapter.getItem(arg2));
			ContactOptionDialogFragment optionDialogFragment = new ContactOptionDialogFragment();
			optionDialogFragment.setArguments(bundle);
			optionDialogFragment.show(getFragmentManager(), "option");
			return false;
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.ntek.wallpad.CONTACT_UPDATE");
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
		txtsearch.setText("");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause()");
		if (broadcastReceiver != null) {
			getActivity().unregisterReceiver(broadcastReceiver);
		}
	}
	
}
