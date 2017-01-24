package com.ntek.wallpad.Screens;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.RingProgressDialogManager;
import com.ntek.wallpad.network.Global;
import com.ntek.wallpad.network.SocClient;

public class ScreenEventServer extends Fragment {

	private Button save, back;
	private EditText eventServerUrlEditText, eventServerPortEditText;
	private final static String TAG = ScreenEventServer.class.getCanonicalName();
	private BroadcastReceiver mBroadCastRecv;
	private String eventServerUrl;
	private int eventServerPort;

	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.screen_event_server, container, false);

		save = (Button) view.findViewById(R.id.btnsave);
//		back = (Button) findViewById(R.id.back);
		eventServerUrlEditText = (EditText) view.findViewById(R.id.event_server_url);
		eventServerPortEditText = (EditText) view.findViewById(R.id.event_server_port);

		eventServerUrl = Global.getInstance().getEventServerUrl();
		eventServerPort = Global.getInstance().getEventServerPort();
		
		eventServerUrlEditText.setText(eventServerUrl);
		// default port is 8080
		eventServerPortEditText.setText(Integer.toString(eventServerPort));
		
		save.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(eventServerUrlEditText.getText().toString().trim().equals(eventServerUrl)) {
					Global.getInstance().setEventServerUrl(eventServerUrlEditText.getText().toString().trim());
					Global.getInstance().setEventServerPort(Integer.parseInt(eventServerPortEditText.getText().toString().trim()));
					NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.EVENT_SERVER_URL, eventServerUrlEditText.getText().toString().trim());
					NgnEngine.getInstance().getConfigurationService().putInt(NgnConfigurationEntry.EVENT_SERVER_PORT, Integer.parseInt(eventServerPortEditText.getText().toString().trim()));
					RingProgressDialogManager.show(getActivity(), "Please Wait...", "Server device connecting to Event Server...");
					new Thread(new SocClient("send_device_info", CommonUtilities.soc_port, getActivity())).start();
				}
				else {
					final AlertDialog.Builder warningDialogBuilder =
							new AlertDialog.Builder(getActivity());
					warningDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
					warningDialogBuilder.setTitle("WARNING");
					warningDialogBuilder.setMessage("Changing Event server details may cause loss of event history data.");
					warningDialogBuilder.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Global.getInstance().setEventServerUrl(eventServerUrlEditText.getText().toString().trim());
							Global.getInstance().setEventServerPort(Integer.parseInt(eventServerPortEditText.getText().toString().trim()));
							RingProgressDialogManager.show(getActivity(), "Please Wait...", "Server device connecting to Event Server...");
							new Thread(new SocClient("send_device_info", CommonUtilities.soc_port, getActivity())).start();
							dialog.dismiss();
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
			}
		});
		
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
		
		return view;
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

	protected void showToast(String message) {
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}
}
