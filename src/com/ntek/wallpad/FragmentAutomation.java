package com.ntek.wallpad;

import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnPredicate;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

import com.ntek.wallpad.Screens.BaseScreen;
import com.ntek.wallpad.Screens.ScreenAV;
import com.ntek.wallpad.Screens.ScreenAVQueue;
import com.ntek.wallpad.Screens.Fragment.FragmentAutomationHeader;
import com.ntek.wallpad.Services.IScreenService;

public class FragmentAutomation extends BaseScreen{
	private static final String TAG = FragmentAutomation.class.getCanonicalName();
	private boolean fromScreenAV;
	public FragmentAutomation() {
		super(SCREEN_TYPE.AUTOMATION_FRAG_T, TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.screen_fragment_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new FragmentAutomationHeader()).commit();
		}
		
		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			fromScreenAV = Boolean.valueOf(bundle.getString("fromScreenAV", "false"));
			Log.d(TAG,"fromScreenAV: " + fromScreenAV );
		}
	}

	@Override
	protected void onDestroy() {
		if(fromScreenAV) {
			((Engine)Engine.getInstance()).getScreenService().bringToFront(Main.ACTION_SHOW_AVSCREEN);
		}
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(0, 0);
	}
}
