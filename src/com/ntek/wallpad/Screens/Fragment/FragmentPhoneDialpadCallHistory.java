package com.ntek.wallpad.Screens.Fragment;

import java.util.ArrayList;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.model.NgnHistoryEvent;
import org.doubango.ngn.services.INgnHistoryService;
import org.doubango.ngn.utils.NgnUriUtils;

import android.app.Fragment;
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
import android.widget.Filter.FilterListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.Contacts;
import com.ntek.wallpad.Utils.HistoryOptionDialogFragment;
import com.ntek.wallpad.Utils.WallPadHistoryAdapter;
import com.ntek.wallpad.Utils.WallPadHistoryAdapter.FilterType;

public class FragmentPhoneDialpadCallHistory extends Fragment implements OnClickListener, OnItemClickListener{
	
	private static final String TAG = FragmentPhoneDialpadCallHistory.class.getCanonicalName();
	private ImageButton btnHistoryAll;
	private ImageButton btnHistoryIncoming;
	private ImageButton btnHistoryOutgoing;
	private ImageButton btnHistoryMissed;
	private ImageButton btnCurrentTab;
	private TextView tvTabSelected;
	private TextView noHistoryCaptionTextView;
	
	private static final String strAll = "ALL";
	private static final String strIncoming = "INCOMING";
	private static final String strOutgoing = "OUTGOING";
	private static final String strFailedMissed = "FAILED/ MISSED";
	private static final String strCallHistory = " CALL HISTORY";
	
	private ListView lvHistory;
	private View view;
	private WallPadHistoryAdapter adapter; 
	private static final long REPEAT_TIME = 1000 * 100;
	
	private INgnHistoryService mNgnHistoryService;
	
	private BroadcastReceiver broadcastReceiver;
	
	public FragmentPhoneDialpadCallHistory()
	{
		mNgnHistoryService = NgnEngine.getInstance().getHistoryService();
		mNgnHistoryService.checkHistory();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_phone_dialpad_tabs, container, false);
		
		initializeUi();
		
		return view;
	}

	private void initializeUi() 
	{
		btnHistoryAll = (ImageButton) view.findViewById(R.id.phone_dialpad_button_tab_all);
		btnHistoryIncoming = (ImageButton) view.findViewById(R.id.phone_dialpad_button_tab_incoming);
		btnHistoryOutgoing = (ImageButton) view.findViewById(R.id.phone_dialpad_button_tab_outgoing);
		btnHistoryMissed = (ImageButton) view.findViewById(R.id.phone_dialpad_tab_missed);
		tvTabSelected = (TextView) view.findViewById(R.id.phone_dialpad_textview_tabssummary);
		noHistoryCaptionTextView = (TextView) view.findViewById(R.id.no_call_history_caption_text_view);
		
		lvHistory = (ListView) view.findViewById(R.id.phone_listview_dialpad_tab);
		lvHistory.setOnItemClickListener(this);
		lvHistory.setOnItemLongClickListener(hisotyListItemLongClick);
	
		adapter = WallPadHistoryAdapter.getInstance(getActivity());
		showHistoryList(adapter.getCount() > 0);
		lvHistory.setAdapter(adapter);
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSansSemibold.ttf");
		tvTabSelected.setTypeface(font);
		noHistoryCaptionTextView.setTypeface(font);
		
		btnHistoryAll.setOnClickListener(this);
		btnHistoryIncoming.setOnClickListener(this);
		btnHistoryMissed.setOnClickListener(this);
		btnHistoryOutgoing.setOnClickListener(this);
		
		btnCurrentTab = btnHistoryAll;
		btnHistoryAll.performClick();
		
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				showHistoryList(intent.getIntExtra("count", 0) > 0);
			}
		};
	}
	
	private void setSelectedTab(ImageButton button, String mText)
	{
		btnCurrentTab.setSelected(false);
		btnCurrentTab = button;
		btnCurrentTab.setSelected(true);
		tvTabSelected.setText(mText + strCallHistory);
	}
	
	private void showHistoryList(boolean show) {
		lvHistory.setVisibility(show ? View.VISIBLE: View.INVISIBLE);
		noHistoryCaptionTextView.setVisibility(show ? View.INVISIBLE: View.VISIBLE);
		noHistoryCaptionTextView.setText("When you contact or contacted by a person or a security device, you'll see your recent activity here.");
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.phone_dialpad_button_tab_all:
			setSelectedTab(btnHistoryAll, strAll);
			adapter.getFilter().filter(FilterType.AllAV.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					showHistoryList(count > 0);
				}
			});
			
			break;
		case R.id.phone_dialpad_button_tab_incoming:
			setSelectedTab(btnHistoryIncoming, strIncoming);
			adapter.getFilter().filter(FilterType.IncomingAV.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					showHistoryList(count > 0);
				}
			});
			break;
		case R.id.phone_dialpad_button_tab_outgoing:
			setSelectedTab(btnHistoryOutgoing, strOutgoing);
			adapter.getFilter().filter(FilterType.OutgoingAV.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					showHistoryList(count > 0);
				}
			});
			break;
		case R.id.phone_dialpad_tab_missed:
			setSelectedTab(btnHistoryMissed, strFailedMissed);
			adapter.getFilter().filter(FilterType.MissedAndFailedAV.toString(), new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					showHistoryList(count > 0);
				}
			});
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId())
		{
		case R.id.phone_listview_dialpad_tab:
			Log.d(TAG, "lvHistory.onItemClick()");
			adapter.setSelectedItem(arg2);
			Fragment rightPanel = getFragmentManager().findFragmentById(R.id.phone_rightpanel);
			if(rightPanel instanceof FragmentPhoneDialpad) {
				FragmentPhoneDialpad dialpadFragment = (FragmentPhoneDialpad) rightPanel;
				NgnHistoryEvent event = (NgnHistoryEvent) adapter.getItem(arg2);
				final String remoteParty = NgnUriUtils.getDisplayName(event.getRemoteParty());
				dialpadFragment.setContactNumber(remoteParty);
			}
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
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.ntek.wallpad.HISTORY_UPDATE");
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
