package com.ntek.wallpad.Screens.Fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingClientSetup.checkingSIP;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;
import com.ntek.wallpad.networkmanager.NetworkManager1;
import com.smarttalk.sip.AutoProvisionDialog;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class FragmentSettingDTMyProfile extends Fragment implements OnClickListener {

	private static final String TAG = FragmentSettingDTMyProfile.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private OnChangeFragmentListener mOnFragmentClick;
	private Context context;
	
	private EditText etCallId;
	private EditText etPassword;
	private EditText etIPAdd;
	private EditText etPort;
	private Button btnShowPass;
	private Button btnSave;
	
	
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
		view = inflater.inflate(R.layout.doortalk_client_myprofile, container, false);
		initializeUi();
		context = getActivity();
		return view;
	}
	
	protected void initializeUi()
	{
		etCallId = (EditText) view.findViewById(R.id.dt_et_callid);
		etIPAdd = (EditText) view.findViewById(R.id.dt_et_ipadd);
		etPassword = (EditText) view.findViewById(R.id.dt_et_password);
		etPort = (EditText) view.findViewById(R.id.dt_et_port);
		btnSave = (Button) view.findViewById(R.id.dt_client_myprofile_btn_save);
		btnShowPass = (Button) view.findViewById(R.id.dt_showpass);
		btnShowPass.setBackgroundResource(R.drawable.ic_visibility_off);
		
		btnSave.setOnClickListener(this);
		btnShowPass.setOnClickListener(this);
		
		loadConfig1();
		
		
		if (mConfigurationService.getString("PRODUCT_ID", null) != null )
		{
			Log.d(TAG, " ccccccccccccccccccc ");
			btnSave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) 
				{
					Log.d(TAG, " zzzzzzzzzz ");
					String callId_new = etCallId.getText().toString();
					String calliD_old = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI);
					String sipAdd_new = etIPAdd.getText().toString();
					String port_new = etPort.getText().toString();
					String password_new = etPassword.getText().toString();
					String sipAdd_old = mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM);
					String password_old = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnStringUtils.emptyValue());
					String port_old = String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT));
					
					if (NetworkManager1.INSTANCE.isWiFiConnected() || NetworkManager1.INSTANCE.isNetworkConnected() || NetworkManager1.INSTANCE.isEthernetConnected())
					{
						if ((etCallId.getText().toString() != null && etCallId.getText().toString().length() > 0)
								&& (etPassword.getText().toString() != null && etPassword.getText().toString().length() > 0)
								&& (etIPAdd.getText().toString() != null && etIPAdd.getText().toString().length() > 0))

						{
							if (NetworkManager1.INSTANCE.isWiFiConnected() || NetworkManager1.INSTANCE.isNetworkConnected() || NetworkManager1.INSTANCE.isEthernetConnected())
							{
								if ( calliD_old.equals(callId_new)  &&  !sipAdd_old.equals(sipAdd_new) &&  port_old.equals(port_new) &&  password_old.equals(password_new) )
								{
									if (validate(etIPAdd.getText().toString().trim()) || isValidUrl(etIPAdd.getText().toString().trim()))
									{
										mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, etIPAdd.getText().toString().trim());
										mConfigurationService.commit();
										NgnEngine.getInstance().stop();
										NgnEngine.getInstance().start();
										NgnEngine.getInstance().getSipService().register(context);
										AutoProvisionDialog.getInstance().showNotifMessage(context, " Successfully updated SIP Address ", null);
									}
									else
									{
										AutoProvisionDialog.getInstance().showNotifMessage(context, " Please check your SIP IP Address format ", null);
									}
								}
								else if ( calliD_old.equals(callId_new)  &&  sipAdd_old.equals(sipAdd_new) &&  !port_old.equals(port_new) &&  password_old.equals(password_new) )
								{
									
									int port = 5060;
									try 
									{
										port = Integer.parseInt(etPort.getText().toString().trim());
									} 
									catch (NumberFormatException e) 
									{
										port = 0;
										AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_empty), null);
									}
									if (etPort.getText().toString() != null)
									{
										if (port != 0)
										{
											mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_PORT, etPort.getText().toString().trim());
											mConfigurationService.commit();
											NgnEngine.getInstance().stop();
											NgnEngine.getInstance().start();
											NgnEngine.getInstance().getSipService().register(context);
											AutoProvisionDialog.getInstance().showNotifMessage(context, " Successfully updated Port ", null);
										}
									}
								}
								else if ( calliD_old.equals(callId_new)  &&  sipAdd_old.equals(sipAdd_new) &&  port_old.equals(port_new) &&  !password_old.equals(password_new) )
								{
									
									mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, etPassword.getText().toString().trim());
									mConfigurationService.commit();
									NgnEngine.getInstance().stop();
									NgnEngine.getInstance().start();
									NgnEngine.getInstance().getSipService().register(context);
									AutoProvisionDialog.getInstance().showNotifMessage(context, " Successfully updated Password ", null);
								}
								else if ( calliD_old.equals(callId_new)  &&  sipAdd_old.equals(sipAdd_new) &&  port_old.equals(port_new) &&  password_old.equals(password_new) )
								{
									Log.d(TAG, " mmmmmmmmmmmmmmmmmm ");
//									new checkingSIP().execute();
								}
								else
								{
									Log.d(TAG, "  ooooooooooooo  ");
//									isAutoprovi = true;
//									new CheckInternetAccessTask().execute();
//									new switchCallId().execute();
								}
							}
							else
							{
								AutoProvisionDialog.getInstance().showErrorMessage(context, " network not connected ", null);
							}
						}

						else
						{
//							// ala laman lagi si Display name
//							 if (etDisplayname.getText().toString() == null && etCallId.getText().toString() == null
//										|| etDisplayname.getText().toString().length() < 1 && etCallId.getText().toString().length() < 1 ) {
//									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_mynumber_empty), null);
//									NgnEngine.getInstance().getSipService().unRegister();
//							 }
//							 else if (etDisplayname.getText().toString() == null && etIPAdd.getText().toString() == null
//										|| etDisplayname.getText().toString().length() < 1 && etIPAdd.getText().toString().length() < 1) {
//									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_ipaddress_empty), null);
//									NgnEngine.getInstance().getSipService().unRegister();
//							} 
//							 else if (etDisplayname.getText().toString() == null && etPort.getText().toString() == null
//										|| etDisplayname.getText().toString().length() < 1 && etPort.getText().toString().length() < 1 ) {
//									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_port_empty), null);
//									NgnEngine.getInstance().getSipService().unRegister();
//							} 
//							 else if (etDisplayname.getText().toString() == null && etPasswordword.getText().toString() == null
//										|| etDisplayname.getText().toString().length() < 1 && etPasswordword.getText().toString().length() < 1) {
//									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_password_empty), null);
//									NgnEngine.getInstance().getSipService().unRegister();
//							} 
							 
							// ala laman lagi si SIP Call ID
							 if (etCallId.getText().toString() == null && etIPAdd.getText().toString() == null
										|| etCallId.getText().toString().length() < 1 && etIPAdd.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_ipaddress_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etCallId.getText().toString() == null && etPort.getText().toString() == null
										|| etCallId.getText().toString().length() < 1 && etPort.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etCallId.getText().toString() == null && etPassword.getText().toString() == null
										|| etCallId.getText().toString().length() < 1 && etPassword.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							 
							// ala laman lagi si IP address
							 else if (etIPAdd.getText().toString() == null && etPort.getText().toString() == null
										|| etIPAdd.getText().toString().length() < 1 && etPort.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etIPAdd.getText().toString() == null && etPassword.getText().toString() == null
										|| etIPAdd.getText().toString().length() < 1 && etPassword.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							// ala laman lagi si Port
							 else if (etPort.getText().toString() == null && etPassword.getText().toString() == null
										|| etPort.getText().toString().length() < 1 && etPassword.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							 
							 
							 
							 
//							 else if (etDisplayname.getText().toString() == null
//									|| etDisplayname.getText().toString().length() < 1) {
//								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_empty), null);
//								NgnEngine.getInstance().getSipService().unRegister();
//							} 
							else if (etCallId.getText().toString() == null
									|| etCallId.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} 
							else if (etPort.getText().toString() == null
									|| etPort.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							}
							else if (etPassword.getText().toString() == null
									|| etPassword.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_password_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							}
							else if (etIPAdd.getText().toString() == null
									|| etIPAdd.getText().toString().length() < 1) {
								NgnEngine.getInstance().getSipService().unRegister();
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_empty), null);
							}
						}
					}
					else
					{
						AutoProvisionDialog.getInstance().showErrorMessage(context, "No Network Connection", null);
					}
				
				}
			});
		}
		
		else
		{
			Log.d(TAG, " xxxxxxxxxxxxxxx ");

			btnSave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) 
				{
					if (NetworkManager1.INSTANCE.isWiFiConnected() || NetworkManager1.INSTANCE.isNetworkConnected() || NetworkManager1.INSTANCE.isEthernetConnected())
					{
						if ( (etCallId.getText().toString() != null && etCallId.getText().toString().length() > 0)
								&& (etPassword.getText().toString() != null && etPassword.getText().toString().length() > 0)
								&& (etIPAdd.getText().toString() != null && etIPAdd.getText().toString().length() > 0))

						{

//							mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, etDisplayname.getText().toString().trim());
							mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, etIPAdd.getText().toString().trim());
//							mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", etCallId.getText().toString() , mNetworkService.getLocalIP(false)));
							mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, etIPAdd.getText().toString().trim());

							int port = 5060;
							try 
							{
								port = Integer.parseInt(etPort.getText().toString().trim());
							} 
							catch (NumberFormatException e) 
							{
								port = 0;
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_empty), null);
							}
//							mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,Integer.parseInt(clientsetupHolder.etPort.getText().toString().trim()));
							mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,port);
//							mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_PORT, clientsetupHolder.etPort.getText().toString().trim());
							mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, etPassword.getText().toString().trim());
							mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI,  etCallId.getText().toString());
							mConfigurationService.commit();

							if (etPort.getText().toString() != null)
							{
								if (port != 0)
								{
//									new checkingSIP().execute();
								}
								else
								{
//									new checkingSIP().execute();
								}
							}
						}

						else
						{
							
//							// ala laman lagi si Display name
//							 if (etDisplayname.getText().toString() == null && etCallId.getText().toString() == null
//										|| etDisplayname.getText().toString().length() < 1 && etCallId.getText().toString().length() < 1 ) {
//									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_mynumber_empty), null);
//									NgnEngine.getInstance().getSipService().unRegister();
//							 }
//							 else if (etDisplayname.getText().toString() == null && etIPAdd.getText().toString() == null
//										|| etDisplayname.getText().toString().length() < 1 && etIPAdd.getText().toString().length() < 1) {
//									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_ipaddress_empty), null);
//									NgnEngine.getInstance().getSipService().unRegister();
//							} 
//							 else if (etDisplayname.getText().toString() == null && etPort.getText().toString() == null
//										|| etDisplayname.getText().toString().length() < 1 && etPort.getText().toString().length() < 1 ) {
//									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_port_empty), null);
//									NgnEngine.getInstance().getSipService().unRegister();
//							} 
//							 else if (etDisplayname.getText().toString() == null && etPassword.getText().toString() == null
//										|| etDisplayname.getText().toString().length() < 1 && etPassword.getText().toString().length() < 1) {
//									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_password_empty), null);
//									NgnEngine.getInstance().getSipService().unRegister();
//							} 
//							 
							// ala laman lagi si SIP Call ID
							 if (etCallId.getText().toString() == null && etIPAdd.getText().toString() == null
										|| etCallId.getText().toString().length() < 1 && etIPAdd.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_ipaddress_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etCallId.getText().toString() == null && etPort.getText().toString() == null
										|| etCallId.getText().toString().length() < 1 && etPort.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etCallId.getText().toString() == null && etPassword.getText().toString() == null
										|| etCallId.getText().toString().length() < 1 && etPassword.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							 
							// ala laman lagi si IP address
							 else if (etIPAdd.getText().toString() == null && etPort.getText().toString() == null
										|| etIPAdd.getText().toString().length() < 1 && etPort.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etIPAdd.getText().toString() == null && etPassword.getText().toString() == null
										|| etIPAdd.getText().toString().length() < 1 && etPassword.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							// ala laman lagi si Port
							 else if (etPort.getText().toString() == null && etPassword.getText().toString() == null
										|| etPort.getText().toString().length() < 1 && etPassword.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							
							
							 else if (etCallId.getText().toString() == null
									|| etCallId.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} else if (etPort.getText().toString() == null
									|| etPort.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} else if (etPassword.getText().toString() == null
									|| etPassword.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_password_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} else if (etIPAdd.getText().toString() == null
									|| etIPAdd.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							}
						}
					}
					else
					{
						AutoProvisionDialog.getInstance().showErrorMessage(context, "No Network Connection", null);
					}

				}
			});

		}
		
		
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.dt_showpass :
			if(String.valueOf(btnShowPass.getTag()) == "unhidden"){
				etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());					
				btnShowPass.setTag("hidden");	
				btnShowPass.setBackgroundResource(R.drawable.ic_visibility_off);

			}
			else
			{
				etPassword.setTransformationMethod(null);					
				btnShowPass.setTag("unhidden");	
				btnShowPass.setBackgroundResource(R.drawable.ic_visibility_on);
			}
			break;
			
		default:
			break;
		}
		
	}
	
	private void loadConfig1()
	{
//		etDisplayname.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME));
		etCallId.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI));
		etIPAdd.setText(mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
		etPassword.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnStringUtils.emptyValue()));
		etPort.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT)));
	}

	private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static boolean validate(final String ip) {
        return PATTERN.matcher(ip).matches();
    }
    
    /** 
    * This is used to check the given URL is valid or not.
    * @param url
    * @return true if url is valid, false otherwise.
    */
    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url);
        if(m.matches())
            return true;
        else
            return false;
    }
	
}
