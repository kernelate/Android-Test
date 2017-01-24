package com.ntek.wallpad.Screens.Fragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnNetworkService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;

import com.ntek.wallpad.Engine;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.ScreenAV;
import com.smarttalk.sip.AutoProvisionDialog;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class FragmentImmediateForward extends Fragment implements OnClickListener {
	private static final String TAG = FragmentImmediateForward.class.getCanonicalName();
	private INgnSipService mSipService;
	private Button[] btnDialpad = new Button[12];
	private Button btnEnable;
	private Button btnCancel;
	private Button btnDisable;
	private Button btnUpdate;
	private Button btnDelete;
	private EditText etDialedNumber;
	private Context context;
	private ProgressDialog prgDialog;
	private INgnConfigurationService mConfigurationService;
	private INgnNetworkService mNetworkService;
	
	View view;
	private Typeface font;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_immediate_forward, container, false);
		mSipService = Engine.getInstance().getSipService();
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 

		if(mNetworkService == null)
		{
			mNetworkService = NgnEngine.getInstance().getNetworkService();
		}
		initializeUi();
		
		String rrr = mConfigurationService.getString("ENABLECFI", null);

		if (rrr != null)
		{
			Log.d(TAG, "rrr != null");
			etDialedNumber.setText(rrr);
			etDialedNumber.setFocusable(true);
			btnEnable.setVisibility(View.GONE);
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.LEFT_OF, R.id.phone_dialpad_popup_update);
			params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.phone_dialpad_popup_update);
			params.height = 72;
			params.width = 144;
			btnCancel.setLayoutParams(params);
			
			if ( mConfigurationService.getBoolean("isCFI", false))
			{
				Log.d(TAG, " ISCHECKEDISCHECKEDISCHECKED ");
				btnEnable.setVisibility(View.VISIBLE);
				btnDisable.setVisibility(View.GONE);
				btnUpdate.setVisibility(View.GONE);
				
				RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params1.addRule(RelativeLayout.LEFT_OF, R.id.phone_dialpad_popup_enable);
				params1.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.phone_dialpad_popup_enable);
				params1.height = 72;
				params1.width = 144;
				btnCancel.setLayoutParams(params1);
			}
		}
		else  
		{
			Log.d(TAG, "rrr == null ");
			btnDisable.setVisibility(View.GONE);
			btnUpdate.setVisibility(View.GONE);
		}
		context = getActivity();
		return view;
	}

	private void initializeUi() 
	{
		btnEnable = (Button)view.findViewById(R.id.phone_dialpad_popup_enable);
		btnCancel = (Button)view.findViewById(R.id.phone_dialpad_popup_cancel);
		btnDisable = (Button)view.findViewById(R.id.phone_dialpad_popup_disable);
		btnUpdate = (Button)view.findViewById(R.id.phone_dialpad_popup_update);
		btnDelete = (Button)view.findViewById(R.id.phone_dialpad_button_back_popup);
		etDialedNumber = (EditText) view.findViewById(R.id.phone_dialpad_edittext_dialed_number_popup);
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
		btnDialpad[0] = (Button) view.findViewById(R.id.phone_dialpad_button_0_popup);
		btnDialpad[1] = (Button) view.findViewById(R.id.phone_dialpad_button_1_popup);
		btnDialpad[2] = (Button) view.findViewById(R.id.phone_dialpad_button_2_popup);
		btnDialpad[3] = (Button) view.findViewById(R.id.phone_dialpad_button_3_popup);
		btnDialpad[4] = (Button) view.findViewById(R.id.phone_dialpad_button_4_popup);
		btnDialpad[5] = (Button) view.findViewById(R.id.phone_dialpad_button_5_popup);
		btnDialpad[6] = (Button) view.findViewById(R.id.phone_dialpad_button_6_popup);
		btnDialpad[7] = (Button) view.findViewById(R.id.phone_dialpad_button_7_popup);
		btnDialpad[8] = (Button) view.findViewById(R.id.phone_dialpad_button_8_popup);
		btnDialpad[9] = (Button) view.findViewById(R.id.phone_dialpad_button_9_popup);
		btnDialpad[10] = (Button) view.findViewById(R.id.phone_dialpad_button_asterisk_popup);
		btnDialpad[11] = (Button) view.findViewById(R.id.phone_dialpad_button_hash_popup);

		for (int i = 0; i < btnDialpad.length; i++)
		{
			btnDialpad[i].setOnClickListener(this);
			btnDialpad[i].setTypeface(font);
			btnDialpad[i].setTag(i);
		}

		btnEnable.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnDisable.setOnClickListener(this);
		btnUpdate.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.phone_dialpad_button_0_popup:
		case R.id.phone_dialpad_button_1_popup:
		case R.id.phone_dialpad_button_2_popup:
		case R.id.phone_dialpad_button_3_popup:
		case R.id.phone_dialpad_button_4_popup:
		case R.id.phone_dialpad_button_5_popup:
		case R.id.phone_dialpad_button_6_popup:
		case R.id.phone_dialpad_button_7_popup:
		case R.id.phone_dialpad_button_8_popup:
		case R.id.phone_dialpad_button_9_popup:
		case R.id.phone_dialpad_button_asterisk_popup:
		case R.id.phone_dialpad_button_hash_popup:
			appendText(Integer.parseInt(v.getTag().toString()) == 10 ? "*" : (Integer.parseInt(v.getTag().toString()) == 11 ? "#" : v.getTag().toString()));
			break;
			
		case R.id.phone_dialpad_button_back_popup :
			decrementText();
			break;
			
		case R.id.phone_dialpad_popup_enable :
			if(mSipService.isRegistered() && etDialedNumber.getText().toString().trim().isEmpty() == false )
			{
				mConfigurationService.putBoolean("isCFI", false, true);
				new enableCallForward().execute();
			}
			else
			{ 
				if (!mSipService.isRegistered() && etDialedNumber.getText().toString().trim().isEmpty() == false )
				{
					AutoProvisionDialog.getInstance().showErrorMessage(context, " Not register sip ", null);
				}
				else if (etDialedNumber.getText().toString().trim().isEmpty() == true)
				{
					AutoProvisionDialog.getInstance().showErrorMessage(context, " No input Numbers ", null);
					
				}
			}
			break;
			
		case R.id.phone_dialpad_popup_cancel :
			Log.d(TAG, " cancel " + etDialedNumber.getText().toString().trim());
			
			Fragment fr = new FragmentNewPhoneDialpad();
			FragmentManager fm = getFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.phone_mainpanel, fr);
			fragmentTransaction.commit();
			
			break;
			
		case R.id.phone_dialpad_popup_disable :
//			if(mSipService.isRegistered() && !NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString()) || NgnStringUtils.isNullOrEmpty(etDialedNumber.getText().toString()))
//			{
//				new disableCallForward().execute();
//			}
			if(mSipService.isRegistered() && etDialedNumber.getText().toString().trim().isEmpty() == false )
			{
				mConfigurationService.putBoolean("isCFI", true, true);
				new disableCallForward().execute();
			}
			else
			{ 
				if (!mSipService.isRegistered() && etDialedNumber.getText().toString().trim().isEmpty() == false )
				{
					AutoProvisionDialog.getInstance().showErrorMessage(context, " Not register sip ", null);
				}
				else if (etDialedNumber.getText().toString().trim().isEmpty() == true)
				{
					AutoProvisionDialog.getInstance().showErrorMessage(context, " No input Numbers ", null);
					
				}
			}
			break;
			
		case R.id.phone_dialpad_popup_update :
			if(mSipService.isRegistered() && etDialedNumber.getText().toString().trim().isEmpty() == false )
			{
				new updateCallForward().execute();
			}
			else
			{ 
				if (!mSipService.isRegistered() && etDialedNumber.getText().toString().trim().isEmpty() == false )
				{
					AutoProvisionDialog.getInstance().showErrorMessage(context, " Not register sip ", null);
				}
				else if (etDialedNumber.getText().toString().trim().isEmpty() == true)
				{
					AutoProvisionDialog.getInstance().showErrorMessage(context, " No input Numbers ", null);
					
				}
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

	class enableCallForward extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			prgDialog = new ProgressDialog(context);
			prgDialog.setMessage("Enabling...");
			prgDialog.setCancelable(false);
			prgDialog.show();
			
			NgnAVSession mAVSession;
			mAVSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);
			mConfigurationService.putString("ENABLECFI", etDialedNumber.getText().toString().trim(), true);
			
			mAVSession.makeCall("sip:*21*" + etDialedNumber.getText().toString().trim() + "@" + 
			mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
			Log.d(TAG, " bvbxcvbbcvbcvbcvb ");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
//			String register = "*21*";
//			String aaa;
//			aaa = register + etDialedNumber.getText().toString().trim();
//			mConfigurationService.putString("ENABLE", etDialedNumber.getText().toString().trim(), true);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			prgDialog.dismiss();
			Fragment fr = new FragmentNewPhoneDialpad();
			FragmentManager fm = getFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.phone_mainpanel, fr);
			fragmentTransaction.commit();

			AutoProvisionDialog.getInstance().showNotifMessage(context, " Enabling Success ", null);
		}
	}
	
	
	class disableCallForward extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			prgDialog = new ProgressDialog(context);
			prgDialog.setMessage("Disabling...");
			prgDialog.setCancelable(false);
			prgDialog.show();
			
			NgnAVSession mAVSession;
			mAVSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);
			mConfigurationService.putString("ENABLECFI", etDialedNumber.getText().toString().trim(), true);
			
			mAVSession.makeCall("sip:**21" + "@" + 
			mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
//			String unregister = "**21";
//			mConfigurationService.putString("ENABLE", etDialedNumber.getText().toString().trim(), true);
//			ScreenAV.makeCall(unregister, NgnMediaType.AudioVideo);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			prgDialog.dismiss();
			Fragment frDisable = new FragmentNewPhoneDialpad();
			FragmentManager fmDisable = getFragmentManager();
			FragmentTransaction fragmentTransactionDisable = fmDisable.beginTransaction();
			fragmentTransactionDisable.replace(R.id.phone_mainpanel, frDisable);
			fragmentTransactionDisable.commit();
			
			AutoProvisionDialog.getInstance().showNotifMessage(context, " Disabling finish ", null);

		}
	}
	
	
	class updateCallForward extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			prgDialog = new ProgressDialog(context);
			prgDialog.setMessage("Updating...");
			prgDialog.setCancelable(false);
			prgDialog.show();
			
			NgnAVSession mAVSession;
			mAVSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);
			mConfigurationService.putString("ENABLECFI", etDialedNumber.getText().toString().trim(), true);
			
			mAVSession.makeCall("sip:*21*" + etDialedNumber.getText().toString().trim() + "@" + 
			mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
			Log.d(TAG, " bvbxcvbbcvbcvbcvb ");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
//			String register = "*21*";
//			String aaa;
//			aaa = register + etDialedNumber.getText().toString().trim();
//			mConfigurationService.putString("ENABLE", etDialedNumber.getText().toString().trim(), true);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			prgDialog.dismiss();
			Fragment fr = new FragmentNewPhoneDialpad();
			FragmentManager fm = getFragmentManager();
			FragmentTransaction fragmentTransaction = fm.beginTransaction();
			fragmentTransaction.replace(R.id.phone_mainpanel, fr);
			fragmentTransaction.commit();

			AutoProvisionDialog.getInstance().showNotifMessage(context, " Updating Success ", null);
		}
	}
			
}
