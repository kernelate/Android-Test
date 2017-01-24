package com.ntek.wallpad.Screens.Fragment;

import java.util.Date;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnUriUtils;

import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Model.HistoryGcmNotificationEvent;
import com.ntek.wallpad.Screens.ScreenAV;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.ContactManager;
import com.ntek.wallpad.Utils.Contacts;

public class FragmentSecurityMotionDetectDetail extends Fragment {
	
	private View mView;
	private WebView motionDetectImageWebView;
	private Button videoCallButton;
	private Button audioCallButton;
	private TextView displayNameTextView;
	private TextView remotePartyTextView;
	private TextView ocurrenceTimeTextView;
	
	private String remoteParty;
	private String displayName;
	private Long ocurrenceTime;
	private String imagetPath;
	
	private HistoryGcmNotificationEvent gcmEvent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.security_motion_detect_detail, null);
		motionDetectImageWebView = (WebView) mView.findViewById(R.id.security_motion_detect_detail_web_view);
		videoCallButton = (Button) mView.findViewById(R.id.security_motion_detect_detail_video_call_button);
		audioCallButton = (Button) mView.findViewById(R.id.security_motion_detect_detail_audio_call_button);
		displayNameTextView = (TextView) mView.findViewById(R.id.security_motion_detect_detail_device_name_tv);
		remotePartyTextView = (TextView) mView.findViewById(R.id.security_motion_detect_detail_sip_number_tv);
		ocurrenceTimeTextView = (TextView) mView.findViewById(R.id.security_motion_detect_detail_date_tv);
		
		audioCallButton.setOnClickListener(new OnClickListener() {
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
		
		videoCallButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
	
	
	public void setGcmEvent(HistoryGcmNotificationEvent gcmEvent) {
		this.gcmEvent = gcmEvent;
	}
	
	public HistoryGcmNotificationEvent getGcmEvent() {
		return this.gcmEvent;
	}
	
	public void displayView() {
		if (this.gcmEvent != null)
		{
			remoteParty = NgnUriUtils.getDisplayName(gcmEvent.getRemoteParty());
			displayName = gcmEvent.getDisplayName();
			ocurrenceTime = gcmEvent.getStartTime();
			imagetPath = gcmEvent.getImagePath();
			
			final Contacts contact = ContactManager.getInstance(getActivity()).getContactByNumber(CommonUtilities.parseInt(remoteParty));
			if (contact != null) {
				displayName = contact.getDisplayName();
			}
			
			displayNameTextView.setText(displayName); // set the default name
			remotePartyTextView.setText(remoteParty);
			
			Date date = new Date(ocurrenceTime);
			ocurrenceTimeTextView.setText(DateFormat.getTimeFormat(getActivity()).format(date));
			
			final String displayImagePath = "file://" + imagetPath;
			final String htmlPath = "<html><head></head><body><img src=\""+ displayImagePath + "\" width=\"100%\" height=\"100%\"></body></html>";
			motionDetectImageWebView.loadDataWithBaseURL("", htmlPath, "text/html","utf-8", "");  
		} 
		else {
			
		}
	}
}
