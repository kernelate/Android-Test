package com.ntek.wallpad.Screens.Fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.GalleryAdapter;
import com.ntek.wallpad.Utils.ImageAdapter;

public class FragmentGalleryMainScreen extends Fragment{

	protected static final int SCREEN_SHOT_VIDEO_SCREEN = 0;
	protected static final int MOTION_DETECT = 1;
	protected static final int BASE_ANDROID_GALLERY = 2;

	private View view;
	
	private GridView gvMain;
	
	private GalleryAdapter galAdapter;
	private ImageAdapter imgAdapterScreenCapture;
	private ImageAdapter imgAdapterMotionHistoryImage;
	private ImageAdapter imgAdapterCamera;
	private FragmentGalleryImageHolder fgImageHolder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_gallery_main_screen, container, false);
		
		initializeUi();
		
		return view;
	}

	private void initializeUi(){
		
		ArrayList<String> items = new ArrayList<String>();
		 
        // adding 100 images
        for (int p = 0; p < 100; p++) {
            items.add("photo");
        }
		
		fgImageHolder = new FragmentGalleryImageHolder();
		
		imgAdapterScreenCapture = new ImageAdapter(getActivity(), "DoorTalk/Screen Capture", items);
		imgAdapterMotionHistoryImage = new ImageAdapter(getActivity(), "DoorTalk/MotionHistoryImage", items);
		imgAdapterCamera = new ImageAdapter(getActivity(), "Camera", items);
		
		galAdapter = new GalleryAdapter(getActivity());

		gvMain = (GridView) view.findViewById(R.id.gallery_main_gridview);
		gvMain.setAdapter(galAdapter);
		
		gvMain.setOnItemClickListener(onFolderSelectListener);
	}
	
	private OnItemClickListener onFolderSelectListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case SCREEN_SHOT_VIDEO_SCREEN:
				setGridImageAdapter(imgAdapterScreenCapture);
				break;
				
			case MOTION_DETECT:
				setGridImageAdapter(imgAdapterMotionHistoryImage);
				break;
				
			case BASE_ANDROID_GALLERY:
				setGridImageAdapter(imgAdapterCamera);
				break;

			default:
				break;
			}
		}
	};
	
	private void setGridImageAdapter(ImageAdapter imgAdapter)
	{
		fgImageHolder.setImageAdapter(imgAdapter);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.gallery_panel, fgImageHolder);
		ft.addToBackStack(null);
		ft.commit();
	}
	
}
