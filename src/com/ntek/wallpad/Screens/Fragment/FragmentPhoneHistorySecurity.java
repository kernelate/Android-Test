package com.ntek.wallpad.Screens.Fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.Filter.FilterListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Model.HistoryGcmNotificationEvent;
import com.ntek.wallpad.Model.HistoryGcmNotificationEvent.NotificationType;
import com.ntek.wallpad.Utils.HistoryOptionDialogFragment;
import com.ntek.wallpad.Utils.WallPadHistoryAdapter;
import com.ntek.wallpad.Utils.WallPadHistoryAdapter.FilterType;

public class FragmentPhoneHistorySecurity extends Fragment implements OnClickListener{
	
	
	private Button btnHistoryCall;
	private Button btnHistorySecurity;
	private Button btnHistoryMotionDetect;
	private Button btnHistoryDoorControl;
	private Button btnCurrentTab;
	private TextView noHistorySecurityCaptionTextView;
	private ListView lvHistorySecurity;
	private View view;
	private WallPadHistoryAdapter adapter; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_phone_history_security, container, false);
		initializeUi();
		btnCurrentTab = btnHistoryMotionDetect;
		return view;
	}
	
	private void initializeUi() 
	{ 
		btnHistoryCall = (Button) view.findViewById(R.id.phone_history_calls);
		btnHistorySecurity = (Button) view.findViewById(R.id.phone_history_security);
		btnHistoryMotionDetect = (Button) view.findViewById(R.id.phone_history_motion_detect);
		btnHistoryDoorControl = (Button) view.findViewById(R.id.phone_history_door_control);
		noHistorySecurityCaptionTextView = (TextView) view.findViewById(R.id.no_history_security_caption_text_view);
		
		lvHistorySecurity = (ListView) view.findViewById(R.id.phone_history_list_view);
		
		btnHistoryCall.setOnClickListener(this);
		btnHistoryDoorControl.setOnClickListener(this);
		btnHistoryMotionDetect.setOnClickListener(this);
		btnHistorySecurity.setOnClickListener(this);
		lvHistorySecurity.setOnItemClickListener(mOnItemClickListener);
		
		adapter = WallPadHistoryAdapter.getInstance(getActivity());
		lvHistorySecurity.setAdapter(adapter);
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		noHistorySecurityCaptionTextView.setTypeface(font);
		btnHistorySecurity.setTypeface(font);
		btnHistoryCall.setTypeface(font);
		btnHistoryMotionDetect.setTypeface(font);
		btnHistoryDoorControl.setTypeface(font);
		
		btnCurrentTab = btnHistoryMotionDetect;
		btnHistoryMotionDetect.performClick();
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.phone_history_calls:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.phone_leftpanel, new FragmentPhoneHistoryCalls());
			ft.replace(R.id.phone_rightpanel, new FragmentPhoneContactInformation());
			ft.commit();
			break;
		case R.id.phone_history_motion_detect:
			setSelectedTab(btnHistoryMotionDetect);
			adapter.getFilter().filter(FilterType.MotionDetect.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;
		case R.id.phone_history_door_control:
			setSelectedTab(btnHistoryDoorControl);
			adapter.getFilter().filter(FilterType.DoorControl.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;
			
		default:
			break;
		}
	}
	
	private void setSelectedTab(Button button)
	{
		btnCurrentTab.setSelected(false);
		btnCurrentTab = button;
		btnCurrentTab.setSelected(true);
	}
	
	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			adapter.setSelectedItem(position);
			adapter.notifyDataSetChanged();
			setHistoryInfo(position);
		}
	};
	
	private void showMotionDoorControlHistoryList(boolean show) {
		lvHistorySecurity.setVisibility(show ? View.VISIBLE: View.INVISIBLE);
		noHistorySecurityCaptionTextView.setVisibility(show ? View.INVISIBLE: View.VISIBLE);
		noHistorySecurityCaptionTextView.setText("You'll see recent Motion Detect and Door Control notications here.");
	}
	
	public void setSelected(int count) {
		if(count > 0) lvHistorySecurity.performItemClick(lvHistorySecurity.getChildAt(0), 0, lvHistorySecurity.getItemIdAtPosition(0));
		else setHistoryInfo(-1);
	}
	
	public void setHistoryInfo(int position) {
		HistoryGcmNotificationEvent gcmEvent;
		if(position < 0) {
			gcmEvent = null;
			showMotionDoorControlHistoryList(false);
		}
		else {
			gcmEvent = (HistoryGcmNotificationEvent) adapter.getItem(position);
			showMotionDoorControlHistoryList(true);
		}
		
		final Fragment rightPanel = getFragmentManager().findFragmentById(R.id.phone_rightpanel);
		
		if (gcmEvent == null) {
			if(rightPanel instanceof FragmentSecurityMotionDetectDetail) {
				FragmentSecurityMotionDetectDetail motionDetectDetailFragment = (FragmentSecurityMotionDetectDetail) rightPanel;
				motionDetectDetailFragment.setGcmEvent(gcmEvent);
				motionDetectDetailFragment.displayView();
			}
			else if(rightPanel instanceof FragmentSecurityDoorControlDetail) {
				FragmentSecurityDoorControlDetail doorControlDetailFragment = (FragmentSecurityDoorControlDetail) rightPanel;
				doorControlDetailFragment.setGcmEvent(gcmEvent);
				doorControlDetailFragment.displayView();
			}
		}
		else if (gcmEvent.getNotificationType() == NotificationType.MotionDetect)
		{
			if(rightPanel instanceof FragmentSecurityMotionDetectDetail) {
				FragmentSecurityMotionDetectDetail motionDetectDetailFragment = (FragmentSecurityMotionDetectDetail) rightPanel;
				motionDetectDetailFragment.setGcmEvent(gcmEvent);
				motionDetectDetailFragment.displayView();
			} else {
				FragmentSecurityMotionDetectDetail motionDetectDetailFragment = new FragmentSecurityMotionDetectDetail();
				motionDetectDetailFragment.setGcmEvent(gcmEvent);
				
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.phone_rightpanel, motionDetectDetailFragment);
				ft.commit();
			}
		}
		else if (gcmEvent.getNotificationType() == NotificationType.DoorControl)
		{
			if(rightPanel instanceof FragmentSecurityDoorControlDetail) {
				FragmentSecurityDoorControlDetail doorControlDetailFragment = (FragmentSecurityDoorControlDetail) rightPanel;
				doorControlDetailFragment.setGcmEvent(gcmEvent);
				doorControlDetailFragment.displayView();
			} else {
				FragmentSecurityDoorControlDetail doorControlDetailFragment = new FragmentSecurityDoorControlDetail();
				doorControlDetailFragment.setGcmEvent(gcmEvent);
				
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.phone_rightpanel, doorControlDetailFragment);
				ft.commit();
			}
		}
	}
	
	
	AdapterView.OnItemLongClickListener hisotyListItemLongClick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) 
		{
			Bundle bundle = new Bundle();
			bundle.putInt("position", position);
			HistoryOptionDialogFragment historyDialogFragment = new HistoryOptionDialogFragment(getActivity(), bundle);
			historyDialogFragment.show();
			return false;
		}
	};
}
