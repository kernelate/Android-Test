package com.ntek.wallpad.Screens.Fragment;

import java.util.HashMap;
import java.util.List;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.model.NgnHistoryEvent;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.tinyWRAP.SipUri;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ntek.wallpad.Engine;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.ScreenAV;
import com.ntek.wallpad.network.Global;
import com.smarttalk.sip.AutoProvisionDialog;
import com.smarttalk.sip.AutoProvisionXmlReader;

public class FragmentNewPhoneDialpad extends Fragment implements OnClickListener{
	private static final String TAG = FragmentNewPhoneDialpad.class.getCanonicalName();
	private Button[] btnDialpad = new Button[12];
	
	private ImageButton btnDialpadVideoCall;
	private ImageButton btnDialpadVoiceCall;
	private ImageButton btnDialpadDelete;
	private ImageButton btnDialpadForwardImmediate;
	private ImageButton btnDialpadForwardBusy;
	private ToggleButton btnDialpadDonotDisturb;
	private ImageButton btnDialpadCallPickup;
	private ImageButton btnDialpadVoiceMail;
	private ImageButton btnDialpadRedial;
	private EditText etDialedNumber;
	private INgnConfigurationService mConfigurationService;
	private Typeface font;
	
	private INgnSipService mSipService;
	private HashMap<String,String> sipSetupHashMap = new HashMap<String, String>();
	
	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_new_phone_dialpad, container, false);
		
		mSipService = Engine.getInstance().getSipService();
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
		initializeUi();
		return view;
	}

	private void initializeUi() 
	{
		btnDialpadVideoCall = (ImageButton) view.findViewById(R.id.phone_dialpad_button_video_call);
		btnDialpadVoiceCall = (ImageButton) view.findViewById(R.id.phone_dialpad_button_voice_call);
		btnDialpadDelete = (ImageButton) view.findViewById(R.id.phone_dialpad_button_delete);
		btnDialpadForwardImmediate = (ImageButton) view.findViewById(R.id.phone_dialpad_button_immediate_forward);
		btnDialpadForwardBusy = (ImageButton) view.findViewById(R.id.phone_dialpad_button_busy_forward);
		btnDialpadDonotDisturb = (ToggleButton) view.findViewById(R.id.phone_dialpad_button_donotdisturb);
		btnDialpadCallPickup = (ImageButton) view.findViewById(R.id.phone_dialpad_button_call_pickup);
		btnDialpadVoiceMail = (ImageButton) view.findViewById(R.id.phone_dialpad_button_voicemail);
		btnDialpadRedial = (ImageButton) view.findViewById(R.id.phone_dialpad_button_redial);
		etDialedNumber = (EditText) view.findViewById(R.id.phone_dialpad_edittext_dialed_number);
		etDialedNumber.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.onTouchEvent(event);
				InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return true;
			}
		});
		
		font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansRegular.ttf");
		initializeButtons();
		etDialedNumber.setTypeface(font);
		etDialedNumber.setInputType(InputType.TYPE_NULL);
		
	}
	
	private void initializeButtons()
	{
		btnDialpad[0] = (Button) view.findViewById(R.id.phone_dialpad_button_0);
		btnDialpad[1] = (Button) view.findViewById(R.id.phone_dialpad_button_1);
		btnDialpad[2] = (Button) view.findViewById(R.id.phone_dialpad_button_2);
		btnDialpad[3] = (Button) view.findViewById(R.id.phone_dialpad_button_3);
		btnDialpad[4] = (Button) view.findViewById(R.id.phone_dialpad_button_4);
		btnDialpad[5] = (Button) view.findViewById(R.id.phone_dialpad_button_5);
		btnDialpad[6] = (Button) view.findViewById(R.id.phone_dialpad_button_6);
		btnDialpad[7] = (Button) view.findViewById(R.id.phone_dialpad_button_7);
		btnDialpad[8] = (Button) view.findViewById(R.id.phone_dialpad_button_8);
		btnDialpad[9] = (Button) view.findViewById(R.id.phone_dialpad_button_9);
		btnDialpad[10] = (Button) view.findViewById(R.id.phone_dialpad_button_asterisk);
		btnDialpad[11] = (Button) view.findViewById(R.id.phone_dialpad_button_hash);
		
		for (int i = 0; i < btnDialpad.length; i++)
		{
			btnDialpad[i].setOnClickListener(this);
			btnDialpad[i].setTypeface(font);
			btnDialpad[i].setTag(i);
		}
		
		btnDialpadDelete.setOnClickListener(this);
		btnDialpadVideoCall.setOnClickListener(this);
		btnDialpadVoiceCall.setOnClickListener(this);
		btnDialpadForwardImmediate.setOnClickListener(this);
		btnDialpadForwardBusy.setOnClickListener(this);
		btnDialpadDonotDisturb.setOnClickListener(this);
		btnDialpadCallPickup.setOnClickListener(this);
		btnDialpadVoiceMail.setOnClickListener(this);
		btnDialpadRedial.setOnClickListener(this);
		
		if (mConfigurationService.getBoolean("ISCHECKED", false))
		{
			btnDialpadDonotDisturb.setChecked(true);
		}
		else
		{
			btnDialpadDonotDisturb.setChecked(false);
		}
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.phone_dialpad_button_0:
		case R.id.phone_dialpad_button_1:
		case R.id.phone_dialpad_button_2:
		case R.id.phone_dialpad_button_3:
		case R.id.phone_dialpad_button_4:
		case R.id.phone_dialpad_button_5:
		case R.id.phone_dialpad_button_6:
		case R.id.phone_dialpad_button_7:
		case R.id.phone_dialpad_button_8:
		case R.id.phone_dialpad_button_9:
		case R.id.phone_dialpad_button_asterisk:
		case R.id.phone_dialpad_button_hash:
			appendText(Integer.parseInt(v.getTag().toString()) == 10 ? "*" : (Integer.parseInt(v.getTag().toString()) == 11 ? "#" : v.getTag().toString()));
			break;
		case R.id.phone_dialpad_button_delete:
			decrementText();
			break;
		case R.id.phone_dialpad_button_video_call:
			if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString())){
//				if(!etDialedNumber.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
//					Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
//					return;
//				}
				ScreenAV.makeCall(etDialedNumber.getText().toString(), NgnMediaType.AudioVideo);
				etDialedNumber.setText(NgnStringUtils.emptyValue());
			}
			break;
		case R.id.phone_dialpad_button_voice_call:
			if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString())){
//				if(!etDialedNumber.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
//					Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
//					return;
//				}
				ScreenAV.makeCall(etDialedNumber.getText().toString(), NgnMediaType.Audio);
				etDialedNumber.setText(NgnStringUtils.emptyValue());
			}
			break;
			
			case R.id.phone_dialpad_button_immediate_forward:
				if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString()) || NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString())){
					Log.d(TAG, " skdfhsjkfhshfsdfjhshjh ");
//					
					Fragment fr = new FragmentImmediateForward();
					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					fragmentTransaction.replace(R.id.phone_mainpanel, fr);
					fragmentTransaction.commit();
				}
				break;
			case R.id.phone_dialpad_button_busy_forward:
				if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString()) || NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString())){
					Log.d(TAG, " cvbcbcvbvcbvcbvb ");
					Fragment fr = new FragmentBusyForward();
					FragmentManager fm = getFragmentManager();
					FragmentTransaction fragmentTransaction = fm.beginTransaction();
					fragmentTransaction.replace(R.id.phone_mainpanel, fr);
					fragmentTransaction.commit();
				}
				break;
				
			case R.id.phone_dialpad_button_call_pickup:
				if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString()) || NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString())){
					Log.d(TAG, " fffffff ");
					String callpickup = "*8";
					
					ScreenAV.makeCall(callpickup, NgnMediaType.AudioVideo);
					
//					NgnAVSession mAVSession;
//					mAVSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);
//					mAVSession.makeCall("sip:*8@" );
				}
				break;
			case R.id.phone_dialpad_button_voicemail:
				sipSetupHashMap = AutoProvisionXmlReader.getInstance().getSipData();
				
				
				if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString()) || NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString())){
					String impi = mConfigurationService.getString
							(NgnConfigurationEntry.IDENTITY_IMPU, 
									NgnConfigurationEntry.IDENTITY_IMPU); 
					
					
					ScreenAV.makeCall(impi, NgnMediaType.AudioVideo);
//					Log.d(TAG, " gggggg " + NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID)));
					
				}
				break;
			case R.id.phone_dialpad_button_donotdisturb :
				
				NgnAVSession mAVSession;
				if(mSipService.isRegistered() ){
					if (btnDialpadDonotDisturb.isChecked())
					{
						Log.d(TAG, "gagana ");
//						mConfigurationService.putBoolean("ISCHECKED", true, true);
//						ScreenAV.makeCall(dnd, NgnMediaType.AudioVideo);
						
						mAVSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);
						mAVSession.makeCall("sip:*78@" + mConfigurationService.putBoolean("ISCHECKED", true, true));
					}
					else
					{
						Log.d(TAG, "d gagana ");
//						mConfigurationService.putBoolean("ISCHECKED", false, true);
//						ScreenAV.makeCall(dnd2, NgnMediaType.AudioVideo);
						
						mAVSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);
						mAVSession.makeCall("sip:*79@" + mConfigurationService.putBoolean("ISCHECKED", false, true));
					}
				}
				else
				{
					AutoProvisionDialog.getInstance().showErrorMessage(getActivity(), " SIP is not register ", null);
				}
				break;
			case R.id.phone_dialpad_button_redial:
				if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString()) || NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString())){
					
					try 
					{
						List<NgnHistoryEvent> historyList = NgnEngine.getInstance().getHistoryService().getEvents();
						String remoteParty = "";
						SipUri uri = new SipUri(historyList.get(0).getRemoteParty());
						if(historyList.size() > 0) {
							remoteParty = historyList.get(0).getRemoteParty();
							if(SipUri.isValid(historyList.get(0).getRemoteParty())) {
								
								Log.d(TAG, " 11111 " +uri.getHost() + "  " +
										uri.getDisplayName() +  "  " +
										uri.getUserName());
							}
							Log.d(TAG, " hhhhhhhhhh " + remoteParty);
						}
						
//						ScreenAV.makeCall(uri.getUserName(), NgnMediaType.AudioVideo);
						etDialedNumber.setText(uri.getUserName());
					}
					catch(IndexOutOfBoundsException e)
					{
						AutoProvisionDialog.getInstance().showNotifMessage(getActivity(), " Nothing on the Log history ", null);
					}
				}
				else
				{
					AutoProvisionDialog.getInstance().showNotifMessage(getActivity(), " Register sip ", null);
				}
				break;
		default:
			break;
		}
	}
	
	private void appendText(String textToAppend){
		final int selStart = etDialedNumber.getSelectionStart();
		final StringBuffer sb = new StringBuffer(etDialedNumber.getText().toString());
		sb.insert(selStart, textToAppend);
		etDialedNumber.setText(sb.toString());
		etDialedNumber.setSelection(selStart+1);
	}
	
	private void decrementText(){
		final int selStart = etDialedNumber.getSelectionStart();
		if(selStart >0) {
			final StringBuffer sb = new StringBuffer(etDialedNumber.getText().toString());
			sb.delete(selStart-1, selStart);
			etDialedNumber.setText(sb.toString());
			etDialedNumber.setSelection(selStart-1);
		}
		else
		{
			try
			{
				String text = etDialedNumber.getText().toString();
				etDialedNumber.setText(text.substring(0, text.length() - 1));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				
			}
		}
	}
	
	public void setContactNumber(String contactNumber) {
		Log.d(TAG, "setContactNumber(" + contactNumber + ")");
		etDialedNumber.setText(contactNumber);
	}
}