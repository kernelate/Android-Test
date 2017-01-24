package com.ntek.wallpad.Screens.Fragment;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.smarttalk.sip.AutoProvision;
import com.smarttalk.sip.AutoProvisionDialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FragmentSettingManualSetup extends Fragment implements OnClickListener{

	private static final String TAG = FragmentSettingManualSetup.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private Button btnPBX;
	private Button btnClient;
	private Button btnReset;
	private Context context;
	
	private Dialog progCircular;
	private DialogHolderProgressCircular progressCircular;
	private OnChangeFragmentListener mOnFragmentClick;
	private DialogHolderReset resetHolder;
	private Dialog dialog_reset;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnAddConfig = (OnAddConfigurationListener)activity;
			mOnFragmentClick = (OnChangeFragmentListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.manual_setup, container, false);
		initializeUi();
		context = getActivity();
		return view;
	}
	
	protected void initializeUi()
	{
		btnPBX = (Button) view.findViewById(R.id.manual_setup_tv_pbxsetup);
		btnClient = (Button) view.findViewById(R.id.manual_setup_tv_clientsetup);
		btnReset = (Button) view.findViewById(R.id.manual_setup_tv_reset);
		
		btnPBX.setOnClickListener(this);
		btnClient.setOnClickListener(this);
		btnReset.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId())
		{

		case R.id.manual_setup_tv_pbxsetup :
			Log.d(TAG, " pbx ");
			Intent intent = new Intent(context, com.ntek.wallpad.WebViewActivity.class);
			startActivity(intent);
			
			break;

		case R.id.manual_setup_tv_clientsetup :
			Log.d(TAG, " client setup ");
			Log.d(TAG, " my profile ");
			mOnFragmentClick.changeFragment(null, new FragmentSettingClientSetup(), false);
			
			break;

		case R.id.manual_setup_tv_reset :
			Log.d(TAG, " reset ");
			Reset();
			break;
		
		}
	}
	
	
	private class DialogHolderReset 
	{
		private Button btnNo;
		private Button btnYes;
	} 
	
	private class DialogHolderProgressCircular 
	{
		private ProgressBar preloader; 
	}
	
	private void Reset()
	{
		dialog_reset = new Dialog(context);
		dialog_reset.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_reset.setCancelable(true);
		dialog_reset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		dialog_reset.setContentView(R.layout.setting_reset);

		resetHolder = new DialogHolderReset();
		resetHolder.btnNo = (Button) dialog_reset.findViewById(R.id.reset_cancel);
		resetHolder.btnYes = (Button) dialog_reset.findViewById(R.id.reset_ok);
		
		resetHolder.btnYes.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				
				new resetDevice().execute();
				dialog_reset.dismiss();
			}
		});
		
		
		
		resetHolder.btnNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				dialog_reset.dismiss();
			}
		});
		
		dialog_reset.show();
		dialog_reset.setCancelable(false);
		dialog_reset.setCanceledOnTouchOutside(false);
	}
	
	 class resetDevice extends AsyncTask<Void, Void, Boolean> {

			@Override
			protected void onPreExecute() 
			{
				progCircular = new Dialog(context);
				progCircular.requestWindowFeature(Window.FEATURE_NO_TITLE);
				progCircular.setCancelable(true);
				progCircular.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				progCircular.setContentView(R.layout.reset_preloader);
	
				progressCircular = new DialogHolderProgressCircular();
				progressCircular.preloader = (ProgressBar) progCircular.findViewById(R.id.progressBar);
	
				progCircular.show();
				progCircular.setCancelable(false);
				progCircular.setCanceledOnTouchOutside(false);
			}

			@Override
			protected Boolean doInBackground(Void... arg0) 
			{
//				String productid = NetworkManager1.INSTANCE.getLocalWifiMacAddress();
				String productid = mConfigurationService.getString("PRODUCT_ID", null);
				String call_id = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI);
				String provision_type = mConfigurationService.getString("provisionType", null);
				
				return AutoProvision.CreateInstance().resetDevice(productid, call_id, provision_type);
			}

			@Override
			protected void onPostExecute(Boolean result) 
			{
//				prgDialog.dismiss();
				progCircular.dismiss();
				NgnApplication.getContext().getSharedPreferences(NgnConfigurationEntry.SHARED_PREF_NAME, 0).edit().clear().commit();
//				isCtr = false;
				dialog_reset.dismiss();
//				type = null;
				NgnEngine.getInstance().getSipService().unRegister();
				AutoProvisionDialog.getInstance().showNotifMessage(context, "Settings Successfully reset", null);
			}
	    }

}
