package com.ntek.wallpad.Screens.Fragment;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnNetworkService;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnStringUtils;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.networkmanager.NetworkManager1;
import com.smarttalk.sip.AutoProvisionDialog;
import com.smarttalk.sip.AutoProvisionXmlReader;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.TextView;

public class FragmentSettingClientSetup extends Fragment implements OnClickListener {
	
	private final static String TAG = FragmentSettingClientSetup.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private INgnNetworkService mNetworkService;
	private HashMap<String,String> sipSetupHashMap = new HashMap<String, String>();
	private View view;
	private Context context;
	private OnAddConfigurationListener mOnAddConfig;
	private Button btnShowPass;
	private Button btnSave;
	private EditText etPass;
	private TextView tvDisplayname;
	private TextView tvSIPNumber;
	private TextView tvIPAddress;
	private TextView tvPort;
	private TextView tvPassword;
	private EditText etDisplayname;
	private EditText etSIPNumber;
	private EditText etIPAddress;
	private EditText etPort;
	
	private ProgressDialog prgDialog;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnAddConfig = (OnAddConfigurationListener)activity;
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
		context = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.client_setup, container, false);
		initializeUi();
		
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
		
		if(mNetworkService == null)
		{
			mNetworkService = NgnEngine.getInstance().getNetworkService();
		}
		return view;
	}

	protected void initializeUi()
	{
		tvDisplayname = (TextView) view.findViewById(R.id.client_setup_tv_displayname);
		tvSIPNumber = (TextView) view.findViewById(R.id.client_setup_tv_sip);
		tvIPAddress = (TextView) view.findViewById(R.id.client_setup_tv_ipadd);
		tvPort = (TextView) view.findViewById(R.id.client_setup_tv_port);
		tvPassword = (TextView) view.findViewById(R.id.client_setup_tv_password);
		
		etDisplayname = (EditText) view.findViewById(R.id.client_setup_et_displayname);
		etSIPNumber = (EditText) view.findViewById(R.id.client_setup_et_sip);
		etIPAddress = (EditText) view.findViewById(R.id.client_setup_et_ipadd);
		etPort = (EditText) view.findViewById(R.id.client_setup_et_port);
		etPass = (EditText) view.findViewById(R.id.client_setup_et_password);

		btnSave = (Button) view.findViewById(R.id.client_setup_btn_save);
		btnShowPass = (Button) view.findViewById(R.id.showpass);
		btnShowPass.setBackgroundResource(R.drawable.ic_visibility_off);
		
		tvDisplayname.setOnClickListener(this);
		tvSIPNumber.setOnClickListener(this);
		tvIPAddress.setOnClickListener(this);
		tvPort.setOnClickListener(this);
		tvPassword.setOnClickListener(this);
		btnShowPass.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		
		loadConfig1();
//		storeSipSettings();
		
		if (mConfigurationService.getString("PRODUCT_ID", null) != null )
		{
			btnSave.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) 
				{
					Log.d(TAG, " zzzzzzzzzz ");
					String callId_new = etSIPNumber.getText().toString();
					String calliD_old = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI);
					String sipAdd_new = etIPAddress.getText().toString();
					String port_new = etPort.getText().toString();
					String password_new = etPass.getText().toString();
					String sipAdd_old = mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM);
					String password_old = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnStringUtils.emptyValue());
					String port_old = String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT));
					
					if (NetworkManager1.INSTANCE.isWiFiConnected() || NetworkManager1.INSTANCE.isNetworkConnected() || NetworkManager1.INSTANCE.isEthernetConnected())
					{
						if ((etDisplayname.getText().toString() != null && etDisplayname.getText().toString().length() > 0)
								&& (etSIPNumber.getText().toString() != null && etSIPNumber.getText().toString().length() > 0)
								&& (etPass.getText().toString() != null && etPass.getText().toString().length() > 0)
								&& (etIPAddress.getText().toString() != null && etIPAddress.getText().toString().length() > 0))

						{
							if (NetworkManager1.INSTANCE.isWiFiConnected() || NetworkManager1.INSTANCE.isNetworkConnected() || NetworkManager1.INSTANCE.isEthernetConnected())
							{
								if ( calliD_old.equals(callId_new)  &&  !sipAdd_old.equals(sipAdd_new) &&  port_old.equals(port_new) &&  password_old.equals(password_new) )
								{
									if (validate(etIPAddress.getText().toString().trim()) || isValidUrl(etIPAddress.getText().toString().trim()))
									{
										mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, etIPAddress.getText().toString().trim());
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
									
									mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, etPass.getText().toString().trim());
									mConfigurationService.commit();
									NgnEngine.getInstance().stop();
									NgnEngine.getInstance().start();
									NgnEngine.getInstance().getSipService().register(context);
									AutoProvisionDialog.getInstance().showNotifMessage(context, " Successfully updated Password ", null);
								}
								else if ( calliD_old.equals(callId_new)  &&  sipAdd_old.equals(sipAdd_new) &&  port_old.equals(port_new) &&  password_old.equals(password_new) )
								{
									Log.d(TAG, " mmmmmmmmmmmmmmmmmm ");
									new checkingSIP().execute();
								}
								else
								{
									Log.d(TAG, "  ooooooooooooo  ");
//									isAutoprovi = true;
//									new CheckInternetAccessTask().execute();
									new switchCallId().execute();
								}
							}
							else
							{
								AutoProvisionDialog.getInstance().showErrorMessage(context, " network not connected ", null);
							}
						}

						else
						{
							// ala laman lagi si Display name
							 if (etDisplayname.getText().toString() == null && etSIPNumber.getText().toString() == null
										|| etDisplayname.getText().toString().length() < 1 && etSIPNumber.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_mynumber_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							 }
							 else if (etDisplayname.getText().toString() == null && etIPAddress.getText().toString() == null
										|| etDisplayname.getText().toString().length() < 1 && etIPAddress.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_ipaddress_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etDisplayname.getText().toString() == null && etPort.getText().toString() == null
										|| etDisplayname.getText().toString().length() < 1 && etPort.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etDisplayname.getText().toString() == null && etPass.getText().toString() == null
										|| etDisplayname.getText().toString().length() < 1 && etPass.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							// ala laman lagi si SIP Call ID
							 else if (etSIPNumber.getText().toString() == null && etIPAddress.getText().toString() == null
										|| etSIPNumber.getText().toString().length() < 1 && etIPAddress.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_ipaddress_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etSIPNumber.getText().toString() == null && etPort.getText().toString() == null
										|| etSIPNumber.getText().toString().length() < 1 && etPort.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etSIPNumber.getText().toString() == null && etPass.getText().toString() == null
										|| etSIPNumber.getText().toString().length() < 1 && etPass.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							 
							// ala laman lagi si IP address
							 else if (etIPAddress.getText().toString() == null && etPort.getText().toString() == null
										|| etIPAddress.getText().toString().length() < 1 && etPort.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etIPAddress.getText().toString() == null && etPass.getText().toString() == null
										|| etIPAddress.getText().toString().length() < 1 && etPass.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							// ala laman lagi si Port
							 else if (etPort.getText().toString() == null && etPass.getText().toString() == null
										|| etPort.getText().toString().length() < 1 && etPass.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							 
							 
							 
							 
							 else if (etDisplayname.getText().toString() == null
									|| etDisplayname.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} 
							else if (etSIPNumber.getText().toString() == null
									|| etSIPNumber.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} 
							else if (etPort.getText().toString() == null
									|| etPort.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							}
							else if (etPass.getText().toString() == null
									|| etPass.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_password_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							}
							else if (etIPAddress.getText().toString() == null
									|| etIPAddress.getText().toString().length() < 1) {
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
						if ((etDisplayname.getText().toString() != null && etDisplayname.getText().toString().length() > 0)
								&& (etSIPNumber.getText().toString() != null && etSIPNumber.getText().toString().length() > 0)
								&& (etPass.getText().toString() != null && etPass.getText().toString().length() > 0)
								&& (etIPAddress.getText().toString() != null && etIPAddress.getText().toString().length() > 0))

						{

							mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, etDisplayname.getText().toString().trim());
							mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, etIPAddress.getText().toString().trim());
							mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", etSIPNumber.getText().toString() , mNetworkService.getLocalIP(false)));
							mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, etIPAddress.getText().toString().trim());

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
							mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, etPass.getText().toString().trim());
							mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI,  etSIPNumber.getText().toString());
							mConfigurationService.commit();

							if (etPort.getText().toString() != null)
							{
								if (port != 0)
								{
									new checkingSIP().execute();
								}
								else
								{
									new checkingSIP().execute();
								}
							}
						}

						else
						{
							
							// ala laman lagi si Display name
							 if (etDisplayname.getText().toString() == null && etSIPNumber.getText().toString() == null
										|| etDisplayname.getText().toString().length() < 1 && etSIPNumber.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_mynumber_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							 }
							 else if (etDisplayname.getText().toString() == null && etIPAddress.getText().toString() == null
										|| etDisplayname.getText().toString().length() < 1 && etIPAddress.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_ipaddress_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etDisplayname.getText().toString() == null && etPort.getText().toString() == null
										|| etDisplayname.getText().toString().length() < 1 && etPort.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etDisplayname.getText().toString() == null && etPass.getText().toString() == null
										|| etDisplayname.getText().toString().length() < 1 && etPass.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							// ala laman lagi si SIP Call ID
							 else if (etSIPNumber.getText().toString() == null && etIPAddress.getText().toString() == null
										|| etSIPNumber.getText().toString().length() < 1 && etIPAddress.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_ipaddress_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etSIPNumber.getText().toString() == null && etPort.getText().toString() == null
										|| etSIPNumber.getText().toString().length() < 1 && etPort.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etSIPNumber.getText().toString() == null && etPass.getText().toString() == null
										|| etSIPNumber.getText().toString().length() < 1 && etPass.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							 
							// ala laman lagi si IP address
							 else if (etIPAddress.getText().toString() == null && etPort.getText().toString() == null
										|| etIPAddress.getText().toString().length() < 1 && etPort.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_port_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 else if (etIPAddress.getText().toString() == null && etPass.getText().toString() == null
										|| etIPAddress.getText().toString().length() < 1 && etPass.getText().toString().length() < 1 ) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_ipaddress_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							 
							// ala laman lagi si Port
							 else if (etPort.getText().toString() == null && etPass.getText().toString() == null
										|| etPort.getText().toString().length() < 1 && etPass.getText().toString().length() < 1) {
									AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_password_empty), null);
									NgnEngine.getInstance().getSipService().unRegister();
							} 
							
							 else if (etDisplayname.getText().toString() == null
									|| etDisplayname.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_name_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} else if (etSIPNumber.getText().toString() == null
									|| etSIPNumber.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_mynumber_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} else if (etPort.getText().toString() == null
									|| etPort.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_port_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} else if (etPass.getText().toString() == null
									|| etPass.getText().toString().length() < 1) {
								AutoProvisionDialog.getInstance().showErrorMessage(context, getString(R.string.string_identity_password_empty), null);
								NgnEngine.getInstance().getSipService().unRegister();
							} else if (etIPAddress.getText().toString() == null
									|| etIPAddress.getText().toString().length() < 1) {
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
		switch (v.getId())
		{
		case R.id.client_setup_tv_displayname :

			Log.d(TAG, " dispayname ");
			break;

		case R.id.client_setup_tv_sip :

			Log.d(TAG, " sip ");
			break;

		case R.id.client_setup_tv_ipadd :

			Log.d(TAG, " ipadd ");
			break;

		case R.id.client_setup_tv_port :

			Log.d(TAG, " port ");
			break;

		case R.id.client_setup_tv_password :

			Log.d(TAG, " passwrod ");
			break;
			
		case R.id.showpass :
			
			Log.d(TAG, " showpass ");
			
			if(String.valueOf(btnShowPass.getTag()) == "unhidden"){
				etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());					
				btnShowPass.setTag("hidden");	
				btnShowPass.setBackgroundResource(R.drawable.ic_visibility_off);

			}
			else
			{
				etPass.setTransformationMethod(null);					
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
		etDisplayname.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME));
		etSIPNumber.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI));
		etIPAddress.setText(mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
		etPass.setText(mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, NgnStringUtils.emptyValue()));
		etPort.setText(String.valueOf(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT,NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT)));
	}
	

	public void storeSipSettings()
	{
		sipSetupHashMap = AutoProvisionXmlReader.getInstance().getSipData();
		
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID));
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_CLIENT_IMPI, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID));
		Global.getInstance().set_client_Impi(sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID));
		
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_DISPLAY_NAME, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_DISPLAY_NAME));
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", sipSetupHashMap.get(AutoProvisionXmlReader.SIP_CALL_ID), sipSetupHashMap.get(AutoProvisionXmlReader.SIP_IP_ADDRESS)));
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_PASSWORD));
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_IP_ADDRESS));
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, sipSetupHashMap.get(AutoProvisionXmlReader.SIP_IP_ADDRESS));
		mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, Integer.parseInt(sipSetupHashMap.get(AutoProvisionXmlReader.SIP_PORT)));
		
		mConfigurationService.commit();
		loadConfig1();
	}
	
	
	private class switchCallId extends AsyncTask<Void, Void, Boolean>  
	{
		String callId_new = etSIPNumber.getText().toString();
		String productID = mConfigurationService.getString("PRODUCT_ID", null);
		String calliD_old = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI);
		//	String ipadd = mNetworkService.getLocalIP(false);
		//	String calliD = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI);

		@Override
		protected void onPreExecute() 
		{
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Void... params) 
		{
			Log.d(TAG, " doinback calliD_old " + calliD_old + " callId_new " + callId_new + " productID " + productID);
			return AutoProvisionXmlReader.getInstance().switchCallId(productID, calliD_old, callId_new );
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			calliD_old = callId_new;
			if (result)
			{
//				isAutoprovi = false;
				mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI,  calliD_old);
				mConfigurationService.commit();
				NgnEngine.getInstance().stop();
				NgnEngine.getInstance().start();
				NgnEngine.getInstance().getSipService().register(context);
				AutoProvisionDialog.getInstance().showNotifMessage(context, " Successfully updated ", null);
			}
			else
			{
				AutoProvisionDialog.getInstance().showErrorMessage(context, AutoProvisionXmlReader.getInstance().getFailedResponse(), null);
			}
		}
	}
	
	 class checkingSIP extends AsyncTask<Void, Void, Boolean> {

	    	int myProgressCount;
	    	 @Override
	         protected void onPreExecute() {
	    		Log.d(TAG, " preexe ");
				
	 		  	prgDialog = new ProgressDialog(context);
	        	prgDialog.setMessage("Registering...");
	        	prgDialog.setCancelable(false);
	        	prgDialog.show();
	 			
	    	 }
			@Override
			protected Boolean doInBackground(Void... params) 
			{
				NgnEngine.getInstance().getSipService().unRegister();

				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				NgnEngine.getInstance().getSipService().register(context);

				try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				return NgnEngine.getInstance().getSipService().isRegistered();
			}

			@Override
			protected void onPostExecute(Boolean result) 
			{
				Log.d(TAG, " postexe ");
			
//				dialog_progress11.dismiss();
				prgDialog.dismiss();
				
				Log.d(TAG, " pagkaregister " + NgnEngine.getInstance().getSipService().isRegistered() + "  " + NgnEngine.getInstance().getSipService().getRegistrationState() + " " + 	NgnEngine.getInstance().isStarted());
				if (result)
				{
					Log.d(TAG, " qqqqqqq ");
					if (NgnEngine.getInstance().getSipService().getRegistrationState().toString().equals("CONNECTED"))
					{
						AutoProvisionDialog.getInstance().showNotifMessage(context, " Successfully save ", null);
					}
				}
				else
				{
					if (NgnEngine.getInstance().getSipService().getRegistrationState().toString().equals("CONNECTING"))
					{
						AutoProvisionDialog.getInstance().showNotifMessage(context, " Connecting failed, Please try again ", null);
//						new checkingSIP().execute();
					}
					else if (NgnEngine.getInstance().getSipService().getRegistrationState().toString().equals("TERMINATED"))
					{
						AutoProvisionDialog.getInstance().showNotifMessage(context, " Please check your SIP profile  ", null);
					}
					else if (NgnEngine.getInstance().getSipService().getRegistrationState().toString().equals("NONE"))
					{
						AutoProvisionDialog.getInstance().showNotifMessage(context, " Please check your SIP profile  ", null);
					}
				}
			}
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
