package com.ntek.wallpad.Screens.Fragment;


import java.io.File;

import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;

import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.Engine;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.ScreenAV;
import com.ntek.wallpad.Utils.BitmapDecoder;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.ContactManager;
import com.ntek.wallpad.Utils.Contacts;

public class FragmentPhoneContactInformation extends DialogFragment{

	private static final String TAG = FragmentPhoneContactInformation.class.getCanonicalName();
	private ImageView ivContactImage;
	private TextView tvContactName;
	private TextView tvContactDetails;
	private ImageButton btnVideoCall;
	private ImageButton btnVoiceCall;
	private ImageButton shiftingImageButton;
	private INgnSipService sipService;
	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		sipService = Engine.getInstance().getSipService();
		view = inflater.inflate(R.layout.fragment_phone_contact_details, container, false);
		initializeUI();
		return view;
		
	}
	
	private void initializeUI()
	{
		ivContactImage = (ImageView) view.findViewById(R.id.phone_contacts_imageview_image);
		tvContactName = (TextView) view.findViewById(R.id.phone_contacts_textview_contactname);
		tvContactDetails = (TextView) view.findViewById(R.id.phone_contacts_textview_contactdetails);
		btnVideoCall = (ImageButton) view.findViewById(R.id.phone_contacts_button_videocall);
		btnVoiceCall = (ImageButton) view.findViewById(R.id.phone_contacts_button_voicecall);
//		shiftingImageButton = (ImageButton) view.findViewById(R.id.shifting_image_button);
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		
		tvContactDetails.setTypeface(font);
		tvContactName.setTypeface(font);
		
//		Fragment leftPanel = getFragmentManager().findFragmentById(R.id.phone_leftpanel);
//		if(leftPanel instanceof FragmentPhoneHistoryCalls) {
//			shiftingImageButton.setBackgroundResource(R.drawable.history_empty_bg);
//		}
//		else if(leftPanel instanceof FragmentPhoneContactList) {
//			shiftingImageButton.setBackgroundResource(R.drawable.phone_empty_bg);
//			shiftingImageButton.setOnClickListener(ClickListener);
//		} 
//		else if(leftPanel instanceof FragmentPhoneSecurityList) {
//			shiftingImageButton.setBackgroundResource(R.drawable.security_device_empty_bg);
//		}
		
		btnVideoCall.setOnClickListener(ClickListener);
		btnVoiceCall.setOnClickListener(ClickListener);
		
		Bundle bundle = getArguments();
		if(bundle != null) {
			final String remoteParty = bundle.getString("remoteParty", null);
			setContactInformation(remoteParty);
		}
	}

	public void showContactInfo(boolean show) {
//		((LinearLayout) view.findViewById(R.id.llContactInfo)).setVisibility(show ? View.VISIBLE : View.INVISIBLE);
//		((LinearLayout) view.findViewById(R.id.hideToShowContacts)).setVisibility(show ? View.INVISIBLE : View.VISIBLE);
	}
	
	public void setContactInformation(String phoneNumber)
	{
		Log.d(TAG,"setContactInformation(" + phoneNumber + ")");
		boolean show = phoneNumber != null;
//		showContactInfo(show);
		if(show) {
			Contacts contacts = ContactManager.getInstance(getActivity()).getContactByNumber(Integer.parseInt(phoneNumber));
			String displayName = phoneNumber;
			
			if (contacts != null) {
				displayName = CommonUtilities.checkIfEmpty(contacts.getDisplayName()) ? "unknown" : contacts.getDisplayName();
				final File file = new File(contacts.getPhoto().getFilename());
				if(file.exists()) {
					final Bitmap bitmap = BitmapDecoder.decodeSampledBitmapFromFile(contacts.getPhoto().getFilename(),100,100);
					ivContactImage.setImageBitmap(bitmap);
				} else {
					
					ivContactImage.setImageResource(CommonUtilities.getImage(contacts.getDeviceType()));
				}
			}
			tvContactName.setText(displayName);
			tvContactDetails.setText(phoneNumber);
		}
	}
	private OnClickListener ClickListener = new OnClickListener() {
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.phone_contacts_button_videocall:
			if (sipService.isRegistered()) {
				
				if(!tvContactDetails.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
					Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
					return;
				}
				ScreenAV.makeCall(tvContactDetails.getText().toString(), NgnMediaType.AudioVideo);
				view.setVisibility(View.INVISIBLE);
			} else {
				Toast.makeText(getActivity(), "Register to sip", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.phone_contacts_button_voicecall:
			if (sipService.isRegistered()) {
				
				if(!tvContactDetails.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
					Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
					return;
				}
				ScreenAV.makeCall(tvContactDetails.getText().toString(), NgnMediaType.Audio);		
				view.setVisibility(View.INVISIBLE);
			} else {
				Toast.makeText(getActivity(), "Register to sip", Toast.LENGTH_SHORT).show();
			}
			break;

//		case R.id.shifting_image_button:
//			Fragment leftPanel = getFragmentManager().findFragmentById(R.id.phone_leftpanel);
//			
//			int selectedTab =  1;
//			if(leftPanel instanceof FragmentPhoneContactList) {
//				FragmentPhoneContactList contactListFragment = (FragmentPhoneContactList) leftPanel;
//				selectedTab = contactListFragment.getSelectedTab() > 0 ? 1 : 0;
//			} 
//			
//			Bundle bundle = new Bundle();
//			bundle.putString("title", "Add Contact");
//			bundle.putInt("selectedTab", selectedTab);
//			
//			DialogPhoneContactsAddEdit dialogContacts = new DialogPhoneContactsAddEdit();
//			dialogContacts.setArguments(bundle);
//			dialogContacts.show(getFragmentManager(), "add");
//			break;
		default:
			break;
		}
	}
	};

}
