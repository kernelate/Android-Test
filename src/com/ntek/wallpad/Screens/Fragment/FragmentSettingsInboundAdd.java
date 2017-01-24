package com.ntek.wallpad.Screens.Fragment;

import java.util.List;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingOutboundCall.PriorityInfo;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsInboundCall.BlockInfo;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentSettingsInboundAdd extends Fragment {

	private final static String TAG = FragmentSettingsInboundAdd.class.getCanonicalName();

	private View view;
	private Button inboundSaveBtn;
	private EditText inboundReceiveEt;
	private EditText inboundInfoEt;
	private CustomArrayAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_inbound, container, false);

		initializedUI();

		return view;
	}

	private void initializedUI()
	{
		inboundSaveBtn = (Button) view.findViewById(R.id.fragment_setting_calllist_save_inbound);
		inboundReceiveEt = (EditText) view.findViewById(R.id.fragment_inbound_receive_num);
		inboundInfoEt = (EditText) view.findViewById(R.id.fragment_inbound_info);
		adapter = new CustomArrayAdapter(getActivity(), BlockInfo.getList());
		
		inboundSaveBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if (inboundInfoEt.getText().toString().length() < 1 || inboundReceiveEt.getText().toString().length() < 1) {
					showToast("Don't leave any fields empty");
					return;
				}
				
				for(int i = 0; i < BlockInfo.getList().size(); i++ ) {
					if(inboundReceiveEt.getText().toString().equals(BlockInfo.getList().get(i).getPartyUri())) {
						showToast("Callee is already blocked");
						return;
					}
				}
				
				BlockInfo.getList().add(new BlockInfo(inboundReceiveEt.getText().toString(), inboundInfoEt.getText().toString()));
				adapter.notifyDataSetChanged();
				showToast("Save!");
			}
		});
	}

	
	private class CustomArrayAdapter extends ArrayAdapter<BlockInfo> {
		private Context context;
		private List<BlockInfo> serverDataList;

		public CustomArrayAdapter(Context context, List<BlockInfo> serverDataList) {
			super(context, 0, serverDataList);
			this.context = context;
			this.serverDataList = serverDataList;
		}

		@Override
		public BlockInfo getItem(int position) {
			return serverDataList.get(position);
		}

		public class ViewHolder {
			TextView displayName;
			TextView partyUri;
			Button delete;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = new ViewHolder();
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				view = layoutInflater.inflate(R.layout.setting_inbound_calllist_info, null);
				viewHolder.displayName = (TextView) view.findViewById(R.id.inbound_name);
				viewHolder.partyUri = (TextView) view.findViewById(R.id.inbound_number);
//				viewHolder.delete = (Button) view.findViewById(R.id.btn_delete_inbound);
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) view.getTag();
			}
			BlockInfo serverData = getItem(position);
			if (serverData != null) {
				if (viewHolder.displayName != null) {
					viewHolder.displayName.setText(serverData.getDisplayName());
				}
				if (viewHolder.partyUri != null) {
					viewHolder.partyUri.setText(serverData.getPartyUri());
				}
			}
//			viewHolder.delete.setText(getString(R.string.string_delete));
//			viewHolder.delete.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					final AlertDialog.Builder removeDeviceDialogBuilder = new AlertDialog.Builder(getActivity());
//					removeDeviceDialogBuilder.setIcon(R.drawable.ic_launcher);
//					removeDeviceDialogBuilder.setTitle("Delete");
//					removeDeviceDialogBuilder.setMessage("Are you sure you want to remove this device from the list?");
//
//					removeDeviceDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int index) {
//							BlockInfo.getList().remove(position);
//							adapter.notifyDataSetChanged();
//							dialog.dismiss();
//						}
//					});
//
//					removeDeviceDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int index) {
//							dialog.dismiss();
//						}
//					});
//
//					removeDeviceDialogBuilder.show();
//				}
//			});

			return view;
		}
	}

	
	protected void showToast(String message) {
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

}
