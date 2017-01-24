package com.ntek.wallpad.Screens.Fragment;

import java.util.ArrayList;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.ScreenEventServer;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.SocClient;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class FragmentSettingEventServer extends Fragment implements OnClickListener, OnItemSelectedListener{

	private View view;
	
	private EditText eventServerUrl;
	private EditText eventServerPort;
	private Button connectButton;
	private Button laterButton;
	private Spinner spnURL;
	private String selectedURL;
	private Boolean isEmptyURL = false;
	
	private BroadcastReceiver mBroadCastRecv;
	private final static String TAG = FragmentSettingEventServer.class.getCanonicalName();
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.dialog_setup_event_server, container, false);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE); 
		initializeUI();
		
		return view;
	}
	
	private void initializeUI()
	{
		eventServerUrl = (EditText) view.findViewById(R.id.editText_eventServer);
		eventServerPort = (EditText) view.findViewById(R.id.editText_eventPort);
		connectButton = (Button) view.findViewById(R.id.btn_connect);
		laterButton = (Button) view.findViewById(R.id.btn_later);
		spnURL = (Spinner) view.findViewById(R.id.choose_protocol_spinner);
		
		spnURL.setOnItemSelectedListener(this);
		connectButton.setOnClickListener(this);
		String eventUrl = Global.getInstance().getEventServerUrl();
		if (eventUrl == null) eventUrl = "";
		eventServerUrl.setText(eventUrl);
		eventServerPort.setText(Integer.toString(Global.getInstance().getEventServerPort()));
		
		selectedURL = "http://";
		
		ArrayList<String> values = new ArrayList<String>();
		values.add("http://");
		values.add("https://");
		
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnURL.setAdapter(spinnerAdapter);
		
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				String response = intent.getStringExtra("response");

				Log.d(TAG, "onReceive() : " + action);
				Log.d(TAG, "getStringExtra() : " + response);

				RingProgressDialogManager.hide();
				if (response.equals("inserted")) {
					showNotificationDialog("NOTIFICATION", "Inserting Successful");
				}
				else if (response.equals("updated")) {
					showNotificationDialog("NOTIFICATION", "Updating Successful");
				}
				else if (response.equals("record not updated")) {
					showNotificationDialog("NOTIFICATION", "Already Updated");
				}
				else {
					showNotificationDialog("WARNING", response);
				}
			}
		};
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.btn_later:
			
			break;
		case R.id.btn_connect:
//			Global.getInstance().setEventServerUrl(selectedURL+eventServerUrl.getText().toString().trim());
//			Global.getInstance().setEventServerPort(Integer.parseInt(eventServerPort.getText().toString().trim()));
//			RingProgressDialogManager.show( getActivity(), "Please Wait...", "Server device connecting to Event Server...");
//			new Thread(new SocClient("send_device_info", CommonUtilities.soc_port, getActivity())).start();

			if(eventServerUrl.getText().toString().trim().equals(Global.getInstance().getEventServerUrl())) {
				
				Global.getInstance().setEventServerUrl(eventServerUrl.getText().toString().trim());
				Global.getInstance().setEventServerPort(Integer.parseInt(eventServerPort.getText().toString().trim()));
				NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.EVENT_SERVER_URL, eventServerUrl.getText().toString().trim());					
				NgnEngine.getInstance().getConfigurationService().putInt(NgnConfigurationEntry.EVENT_SERVER_PORT, Integer.parseInt(eventServerPort.getText().toString().trim()));
				RingProgressDialogManager.show( getActivity(), "Please Wait...", "Server device connecting to Event Server...");
				new Thread(new SocClient("send_device_info", CommonUtilities.soc_port, getActivity())).start();
				
				eventServerUrl.setText(Global.getInstance().getEventServerUrl());
			}
			else {
				final AlertDialog.Builder warningDialogBuilder =
						new AlertDialog.Builder(getActivity());
				warningDialogBuilder.setTitle("WARNING");
				warningDialogBuilder.setMessage("Changing Event server details may cause loss of event history data.");
				warningDialogBuilder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Global.getInstance().setEventServerUrl(eventServerUrl.getText().toString().trim());
//						Global.getInstance().setEventServerUrl(selectedURL+eventServerUrl.getText().toString().trim());
						Global.getInstance().setEventServerPort(Integer.parseInt(eventServerPort.getText().toString().trim()));
						RingProgressDialogManager.show( getActivity(), "Please Wait...", "Server device connecting to Event Server...");
						new Thread(new SocClient("send_device_info", CommonUtilities.soc_port, getActivity())).start();
						dialog.dismiss();
						
						eventServerUrl.setText(Global.getInstance().getEventServerUrl());
					}
				});
				warningDialogBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				warningDialogBuilder.show();
			}
		
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		selectedURL = parent.getItemAtPosition(position).toString();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		
	}
	
	@Override
	public void onResume() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.smartbean.servertalk.action.TCP_SEND_DEVICE_INFO_CALLBACK");
		getActivity().registerReceiver(mBroadCastRecv, intentFilter);
		super.onResume();
	}

	@Override
	public void onPause() {
		if (mBroadCastRecv != null) {
			getActivity().unregisterReceiver(mBroadCastRecv);
		}
		super.onPause();
	}
	
	private void showNotificationDialog(String title, String message) {
		final AlertDialog.Builder notificationDialogBuilder =
				new AlertDialog.Builder(getActivity());
		notificationDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		notificationDialogBuilder.setTitle(title);
		notificationDialogBuilder.setMessage(message);
		notificationDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		notificationDialogBuilder.show();
	}

}
