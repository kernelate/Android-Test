package com.ntek.wallpad.Screens.Fragment;

import java.util.List;

import org.doubango.ngn.model.NgnHistoryEvent;
import org.doubango.ngn.utils.NgnUriUtils;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.Filter.FilterListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.HistoryOptionDialogFragment;
import com.ntek.wallpad.Utils.WallPadHistoryAdapter;
import com.ntek.wallpad.Utils.WallPadHistoryAdapter.FilterType;

public class FragmentPhoneHistoryCalls extends Fragment implements OnClickListener, OnItemClickListener
{
	private static final String TAG = FragmentPhoneHistoryCalls.class.getCanonicalName();

	private View view;
	
	private Button btnHistoryCall;
	private Button btnHistorySecurity;
	private ImageButton btnHistoryAll;
	private ImageButton btnHistoryIncoming;
	private ImageButton btnHistoryOutgoing;
	private ImageButton btnHistoryMissed;
	private ImageButton btnCurrentTab;
	private TextView noHistoryCallsCaptionTextView;
	private ListView lvHistoryCall;
	private BroadcastReceiver broadcastReceiver;
	private NgnHistoryEvent selectedHistory;
	private WallPadHistoryAdapter adapter; 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		view = inflater.inflate(R.layout.fragment_phone_history_calls, container, false);
		
		initializeUi();
		
		return view;
	}
	
	private void initializeUi() 
	{
		btnHistoryCall = (Button) view.findViewById(R.id.phone_history_call_calls);
		btnHistorySecurity = (Button) view.findViewById(R.id.phone_history_call_security);
		btnHistoryAll = (ImageButton) view.findViewById(R.id.phone_history_call_all);
		btnHistoryIncoming = (ImageButton) view.findViewById(R.id.phone_history_call_incoming);
		btnHistoryOutgoing = (ImageButton) view.findViewById(R.id.phone_history_call_outgoing);
		btnHistoryMissed = (ImageButton) view.findViewById(R.id.phone_history_call_missed);
		noHistoryCallsCaptionTextView = (TextView) view.findViewById(R.id.no_history_calls_caption_text_view);
		
		lvHistoryCall = (ListView) view.findViewById(R.id.phone_history_call_listview);
		
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean setSelected = false;
				List<NgnHistoryEvent> historyList = adapter.getList();
				for(int i = 0; i < historyList.size(); i++) {
					if(selectedHistory.getRemoteParty().equals(historyList.get(i).getRemoteParty())) {
						lvHistoryCall.performItemClick(lvHistoryCall.getChildAt(i), i, lvHistoryCall.getItemIdAtPosition(i));
						setSelected = true;
						break;
					}
				}
				if(!setSelected) setSelected(intent.getIntExtra("count", -1));
			}
		};
		
		btnHistoryCall.setOnClickListener(this);
		adapter = WallPadHistoryAdapter.getInstance(getActivity());
		lvHistoryCall.setAdapter(adapter);
		
		lvHistoryCall.setOnItemLongClickListener(hisotyListItemLongClick);
		
		btnHistoryAll.setOnClickListener(this);
		btnHistoryIncoming.setOnClickListener(this);
		btnHistoryMissed.setOnClickListener(this);
		btnHistoryOutgoing.setOnClickListener(this);
		btnHistorySecurity.setOnClickListener(this);
		lvHistoryCall.setOnItemClickListener(this);
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		noHistoryCallsCaptionTextView.setTypeface(font);
		btnHistorySecurity.setTypeface(font);
		btnHistoryCall.setTypeface(font);
		btnCurrentTab = btnHistoryAll;
		btnHistoryAll.performClick();
	}
	
	private void setSelectedTab(ImageButton button)
	{
		btnCurrentTab.setSelected(false);
		btnCurrentTab = button;
		btnCurrentTab.setSelected(true);
	}
	
	public void setSelected(int count) {
		if(count > 0) lvHistoryCall.performItemClick(lvHistoryCall.getChildAt(0), 0, lvHistoryCall.getItemIdAtPosition(0));
		else setContactInfo(-1);
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.phone_history_call_all:
			setSelectedTab(btnHistoryAll);
			adapter.getFilter().filter(FilterType.AllAV.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;
		case R.id.phone_history_call_incoming:
			setSelectedTab(btnHistoryIncoming);
			adapter.getFilter().filter(FilterType.IncomingAV.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;
		case R.id.phone_history_call_outgoing:
			setSelectedTab(btnHistoryOutgoing);
			adapter.getFilter().filter(FilterType.OutgoingAV.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;
		case R.id.phone_history_call_missed:
			setSelectedTab(btnHistoryMissed);
			adapter.getFilter().filter(FilterType.MissedAndFailedAV.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					setSelected(count);
				}
			});
			break;
			
		case R.id.phone_history_call_security:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.phone_leftpanel, new FragmentPhoneHistorySecurity());
			ft.replace(R.id.phone_rightpanel, new FragmentSecurityMotionDetectDetail());
			ft.commit();
			break;

		default:
			break;
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
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		adapter.setSelectedItem(position);
		adapter.notifyDataSetChanged();
		setContactInfo(position);
	}
	
	public void setContactInfo(int position) {
		String remoteParty;
		if(position < 0) {
			showCallHistoryList(false);
			remoteParty = null;
		} else {
			showCallHistoryList(true);
			selectedHistory = (NgnHistoryEvent) adapter.getItem(position);
			remoteParty = NgnUriUtils.getDisplayName(selectedHistory.getRemoteParty());
		}
		
		Fragment rightPanel = getFragmentManager().findFragmentById(R.id.phone_rightpanel);
		if(rightPanel instanceof FragmentPhoneContactInformation) {
			FragmentPhoneContactInformation contactInfoFragment = (FragmentPhoneContactInformation) rightPanel;
			contactInfoFragment.setContactInformation(remoteParty);
		} else {
			Bundle bundle = new Bundle();
			bundle.putString("remoteParty", remoteParty);
			
			FragmentPhoneContactInformation fragment = new FragmentPhoneContactInformation();
			fragment.setArguments(bundle);
			
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.phone_rightpanel, fragment);
			ft.commit();
		}
	}
	
	private void showCallHistoryList(boolean show) {
		lvHistoryCall.setVisibility(show ? View.VISIBLE: View.INVISIBLE);
		noHistoryCallsCaptionTextView.setVisibility(show ? View.INVISIBLE: View.VISIBLE);
		noHistoryCallsCaptionTextView.setText("When you contact a person or a security device, you'll see your recent activity here.");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.ntek.wallpad.CONTACT_UPDATE");
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause()");
		if (broadcastReceiver != null) {
			getActivity().unregisterReceiver(broadcastReceiver);
		}
	}
}
