package com.ntek.wallpad;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.ntek.wallpad.Screens.Fragment.FragmentPhoneCall;
import com.ntek.wallpad.Screens.Fragment.FragmentPhoneHeader;

public class FragmentCall extends Activity{
private static FragmentCall activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.screen_fragment_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new FragmentPhoneCall()).commit();
		}
		activity = this;
	}
	
	public static Activity getPhoneActivity() {
		return activity;
	}
}
