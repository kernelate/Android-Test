package com.ntek.wallpad.Screens.Fragment;

import java.util.ArrayList;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.GalleryAdapter;
import com.ntek.wallpad.Utils.ImageAdapter;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

public class FragmentGalleryImageHolder extends Fragment {
	
	private View view;
	
	private GridView gvMain;
	
	private ImageAdapter imgAdapter;
	
	private FragmentGalleryImagePreview fgImagePreview;
	private FragmentTransaction ft;
	
	private ArrayList<Bitmap> arrImage;
	
	public void setImageAdapter(ImageAdapter imgAdapter) {
		this.imgAdapter = imgAdapter;
		imgAdapter.notifyDataSetChanged();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_gallery_main_screen, container, false);
		
		initializeUi();
		
		return view;
	}
	
	private void initializeUi()
	{
		ft = getFragmentManager().beginTransaction();
		fgImagePreview = new FragmentGalleryImagePreview();
		
//		arrImage = imgAdapter.getArrayListImagePreview();
		
		gvMain = (GridView) view.findViewById(R.id.gallery_main_gridview);
		gvMain.setAdapter(imgAdapter);
		
		gvMain.setOnItemClickListener(onItemImagePreview);
	}
	
	private OnItemClickListener onItemImagePreview = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			fgImagePreview.setBitmapImage(arrImage.get(position));
			ft.replace(R.id.gallery_panel, fgImagePreview);
			ft.addToBackStack(null);
			ft.commit();
		}
	};
	
}
