package com.ntek.wallpad.Screens.Fragment;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingOutboundCall.PriorityInfo;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;

public class FragmentSettingOutboundAdd extends Fragment {

	private final static String TAG = FragmentSettingOutboundAdd.class.getCanonicalName();

	private View view;

	private EditText receiveNumEt;
	private EditText priorityEt;
	private EditText infoEt;
	private Button saveEt;
	private CustomArrayAdapter adapter;
	private OnChangeFragmentListener mOnFragmentClick;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, " onAttach ");
		try {
			mOnFragmentClick = (OnChangeFragmentListener) activity;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_outbound, container, false);

		initializedUI();

		return view;
	}


	private void initializedUI()
	{
		receiveNumEt = (EditText) view.findViewById(R.id.fragment_outbound_receive_num);
		priorityEt = (EditText) view.findViewById(R.id.fragment_outbound_priority);
		infoEt = (EditText) view.findViewById(R.id.fragment_outbound_info);
		saveEt = (Button) view.findViewById(R.id.fragment_setting_calllist_save);
		adapter = new CustomArrayAdapter(getActivity(), PriorityInfo.getList());
		
		priorityEt.setText(Integer.toString(PriorityInfo.getList().size() + 1));
		saveEt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				if (infoEt.getText().toString().length() < 1 || receiveNumEt.getText().toString().length() < 1) {
					showToast("Don't leave any fields empty");
					return;
				}
				
				for(int i = 0; i < PriorityInfo.getList().size(); i++ ) {
					if(receiveNumEt.getText().toString().equals(PriorityInfo.getList().get(i).getPartyUri())) {
						showToast("Callee is already a priority");
						return;
					}
				}
				
				PriorityInfo.getList().add(new PriorityInfo(priorityEt.getText().toString(), receiveNumEt.getText().toString(), infoEt.getText().toString()));
//				Log.d(TAG, " number " + callNumberEditText.getText().toString() + " info " + informationEditText.getText().toString());
				adapter.notifyDataSetChanged();
				showToast("Save");
//				mOnFragmentClick.changeFragment(null, new FragmentSettingOutboundCall(), true);
			}
		});
		
	}

	private class CustomArrayAdapter extends ArrayAdapter<PriorityInfo> {
		private Context context;
		private List<PriorityInfo> serverDataList;

		public CustomArrayAdapter(Context context, List<PriorityInfo> serverDataList) {
			super(context, 0, serverDataList);
			this.context = context;
			this.serverDataList = serverDataList;
		}

		@Override
		public PriorityInfo getItem(int position) {
			return serverDataList.get(position);
		}

		public class ViewHolder {
			TextView displayName;
			TextView partyUri;
			TextView priority;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder viewHolder = new ViewHolder();
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				view = layoutInflater.inflate(R.layout.setting_outbound_calllist_info, null);
				viewHolder.displayName = (TextView) view.findViewById(R.id.outbound_name);
				viewHolder.partyUri = (TextView) view.findViewById(R.id.outbound_number);
				viewHolder.priority = (TextView) view.findViewById(R.id.outbound_priority);
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) view.getTag();
			}
			PriorityInfo serverData = getItem(position);
			if (serverData != null) {
				if (viewHolder.displayName != null) {
					viewHolder.displayName.setText(serverData.getDisplayName());
				}
				if (viewHolder.partyUri != null) {
					viewHolder.partyUri.setText(serverData.getPartyUri());
				}
				if (viewHolder.priority != null) {
					viewHolder.priority.setText(serverData.getPriority());
				}
			}
			return view;
		}
	}
	
	protected void showToast(String message) {
		Toast toast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

}
