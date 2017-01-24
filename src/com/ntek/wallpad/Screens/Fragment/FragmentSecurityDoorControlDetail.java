package com.ntek.wallpad.Screens.Fragment;


import java.text.DateFormat;
import java.util.Date;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnUriUtils;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Model.HistoryGcmNotificationEvent;
import com.ntek.wallpad.Screens.ScreenAV;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.ContactManager;
import com.ntek.wallpad.Utils.Contacts;
import com.ntek.wallpad.Utils.DateTimeUtils;

public class FragmentSecurityDoorControlDetail extends Fragment
{
	private View mView;
	private ImageView doorControlImage;
	private ImageButton videoCallImageButton;
	private ImageButton audioCallImageButton;
	private TextView displayNameTextView;
	private TextView remotePartyTextView;
	private TextView dateTextView;
	private TextView statusAndDateTextView;
	private TextView elapsedTimeTextView;
	
	private String remoteParty;
	private String displayName;
	private Long ocurrenceTime;
	private String textSummary;
	
	private HistoryGcmNotificationEvent gcmEvent;
	
	public void setGcmEvent(HistoryGcmNotificationEvent gcmEvent){
		this.gcmEvent = gcmEvent;
	}
	
	public HistoryGcmNotificationEvent getGcmEvent(){
		return this.gcmEvent;
	}
	
	public void displayView() {
		if (this.gcmEvent != null)
		{
			remoteParty = NgnUriUtils.getDisplayName(gcmEvent.getRemoteParty());
			displayName = gcmEvent.getDisplayName();
			ocurrenceTime = gcmEvent.getStartTime();
			textSummary = gcmEvent.getTextSummary();
			
			final Contacts contact = ContactManager.getInstance(getActivity()).getContactByNumber(CommonUtilities.parseInt(remoteParty));
			if (contact != null) {
				displayName = contact.getDisplayName();
			}
			
			displayNameTextView.setText(displayName);
			String[] doorStatus = textSummary.split("\\|");
			
			String status = "";
			if(doorStatus[1].equals("Unlocked")) {
				status = "Locked -> Unlocked";
				doorControlImage.setImageResource(R.drawable.unlock_normal);
			} 
			else if(doorStatus[1].equals("Locked")) {
				status = "Unlocked -> Locked";
				doorControlImage.setImageResource(R.drawable.unlock_normal_02);
			}
			
			Date date = new Date(ocurrenceTime);
			dateTextView.setText(DateTimeUtils.getFriendlyDateString(date));
			
			elapsedTimeTextView.setText(!doorStatus[0].equals("0") ? doorStatus[0] : "0");
			statusAndDateTextView.setText(status + " | " + DateFormat.getDateTimeInstance(
		            DateFormat.MEDIUM, DateFormat.SHORT).format(date));
			
			remotePartyTextView.setText(remoteParty);
		}
		else
		{
			
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.security_door_control_detail, null);
		doorControlImage = (ImageView) mView.findViewById(R.id.security_door_control_detail_image_view);
		videoCallImageButton = (ImageButton) mView.findViewById(R.id.security_door_control_detail_video_call_image_button);
		audioCallImageButton = (ImageButton) mView.findViewById(R.id.security_door_control_detail_audio_call_image_button);
		displayNameTextView = (TextView) mView.findViewById(R.id.security_door_control_detail_device_name_tv);
		remotePartyTextView = (TextView) mView.findViewById(R.id.security_door_control_detail_sip_number_tv);
		dateTextView = (TextView) mView.findViewById(R.id.security_door_control_detail_date_tv);
		statusAndDateTextView = (TextView) mView.findViewById(R.id.security_door_control_status_and_date);
		elapsedTimeTextView = (TextView) mView.findViewById(R.id.security_door_control_elapsed_time);
		
		audioCallImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(NgnEngine.getInstance().getSipService().isRegistered() && !NgnStringUtils.isNullOrEmpty(remoteParty)) {
					if(!remoteParty.matches("-?\\d+(\\.\\d+)?")) {
						Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
						return;
					}
					ScreenAV.makeCall(remoteParty, NgnMediaType.Audio);
				}
			}
		});
		
		videoCallImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(NgnEngine.getInstance().getSipService().isRegistered() && !NgnStringUtils.isNullOrEmpty(remoteParty)) {	
					if(!remoteParty.matches("-?\\d+(\\.\\d+)?")) {
						Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
						return;
					}
					ScreenAV.makeCall(remoteParty, NgnMediaType.AudioVideo);
				}
			}
		});
		
		displayView();
		return mView;
	}
	
	
}
