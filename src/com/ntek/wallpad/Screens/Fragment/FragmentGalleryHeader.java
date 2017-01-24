package com.ntek.wallpad.Screens.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ntek.wallpad.R;

public class FragmentGalleryHeader extends Fragment{

	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_gallery_main, container, false);
		
		initializeUi();
		
		return view;
	}
	
	private void initializeUi() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.gallery_panel, new FragmentGalleryMainScreen());
		ft.commit();
		
	}
}
