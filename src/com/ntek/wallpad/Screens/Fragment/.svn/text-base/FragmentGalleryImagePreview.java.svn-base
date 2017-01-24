package com.ntek.wallpad.Screens.Fragment;

import com.ntek.wallpad.R;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FragmentGalleryImagePreview extends Fragment {

	private View view;
	
	private ImageView imgImagePreview;
	
	public Bitmap bitmapImage;
	
	public void setBitmapImage(Bitmap bitmapImage) {
		this.bitmapImage = bitmapImage;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_gallery_image_preview, container, false);
		// TODO Auto-generated method stub
		initializeUi();
		
		return view;
	}
	
	private void initializeUi()
	{
		imgImagePreview = (ImageView) view.findViewById(R.id.gallery_image_preview);
		imgImagePreview.setImageBitmap(bitmapImage);
	}

}
