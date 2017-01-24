package com.ntek.wallpad.Screens.Fragment;

import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnStringUtils;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.Engine;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.ScreenAV;

public class FragmentPhoneDialpad extends Fragment implements OnClickListener{
	private static final String TAG = FragmentPhoneDialpad.class.getCanonicalName();
	private Button[] btnDialpad = new Button[12];
	
	private TextView tvABC;
	private TextView tvDEF;
	private TextView tvGHI;
	private TextView tvJKL;
	private TextView tvMNO;
	private TextView tvPQRS;
	private TextView tvTUV;
	private TextView tvWXYZ;
	private TextView tvPlus;
	
	private ImageButton btnDialpadVideoCall;
	private ImageButton btnDialpadVoiceCall;
	private ImageButton btnDialpadDelete;
	
	private EditText etDialedNumber;
	
	private Typeface font;
	
	private INgnSipService mSipService;
	
	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_new_phone_dialpad, container, false);
		
		mSipService = Engine.getInstance().getSipService();
//		initializeUi();
		
		return view;
	}

	private void initializeUi() 
	{
		btnDialpadVideoCall = (ImageButton) view.findViewById(R.id.phone_dialpad_button_video_call);
		btnDialpadVoiceCall = (ImageButton) view.findViewById(R.id.phone_dialpad_button_voice_call);
		btnDialpadDelete = (ImageButton) view.findViewById(R.id.phone_dialpad_button_delete);
		tvABC = (TextView) view.findViewById(R.id.phone_dialpad_textview_abc);
		tvDEF = (TextView) view.findViewById(R.id.phone_dialpad_textview_def);
		tvGHI = (TextView) view.findViewById(R.id.phone_dialpad_textview_ghi);
		tvJKL = (TextView) view.findViewById(R.id.phone_dialpad_textview_jkl);
		tvMNO = (TextView) view.findViewById(R.id.phone_dialpad_textview_mno);
		tvPQRS = (TextView) view.findViewById(R.id.phone_dialpad_textview_pqrs);
		tvTUV = (TextView) view.findViewById(R.id.phone_dialpad_textview_tuv);
		tvWXYZ = (TextView) view.findViewById(R.id.phone_dialpad_textview_wxyz);
		tvPlus = (TextView) view.findViewById(R.id.phone_dialpad_textview_plus);
		
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
		tvABC.setTypeface(font);
		tvDEF.setTypeface(font);
		tvGHI.setTypeface(font);
		tvJKL.setTypeface(font);
		tvMNO.setTypeface(font);
		tvPQRS.setTypeface(font);
		tvTUV.setTypeface(font);
		tvWXYZ.setTypeface(font);
		tvPlus.setTypeface(font);
		etDialedNumber.setTypeface(font);
		
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
				if(!etDialedNumber.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
					Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
					return;
				}
				ScreenAV.makeCall(etDialedNumber.getText().toString(), NgnMediaType.AudioVideo);
				etDialedNumber.setText(NgnStringUtils.emptyValue());
			}
			break;
		case R.id.phone_dialpad_button_voice_call:
			if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString())){
				if(!etDialedNumber.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
					Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
					return;
				}
				ScreenAV.makeCall(etDialedNumber.getText().toString(), NgnMediaType.Audio);
				etDialedNumber.setText(NgnStringUtils.emptyValue());
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
	}
	
	public void setContactNumber(String contactNumber) {
		Log.d(TAG, "setContactNumber(" + contactNumber + ")");
		etDialedNumber.setText(contactNumber);
	}
}
