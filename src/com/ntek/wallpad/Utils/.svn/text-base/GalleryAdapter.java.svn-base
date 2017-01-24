package com.ntek.wallpad.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntek.wallpad.FragmentPhone;
import com.ntek.wallpad.R;

public class GalleryAdapter extends BaseAdapter{
	private final static String TAG = FragmentPhone.class.getCanonicalName();

	private Context mContext;

	protected final static int SCREEN_SHOT_VIDEO_SCREEN = 0;
	protected final static int MOTION_DETECT = 1;
	protected final static int BASE_ANDROID_GALLERY = 2;

	public GalleryAdapter (Context c){
		mContext = c;
	}

	private class ViewHolder{
		ImageView thumbnail;
		TextView caption;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {

			convertView = inflate.inflate(R.layout.fragment_gallery_tile, parent, false);
			viewHolder = new ViewHolder();

			viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.gallery_tile_imagebutton_thumbnail);
			viewHolder.caption = (TextView) convertView.findViewById(R.id.gallery_tile_textview_caption);


			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		switch (position) {
		case SCREEN_SHOT_VIDEO_SCREEN:
			viewHolder.caption.setText("Screen Shot Video Screen");
			break;
			
		case MOTION_DETECT:
			viewHolder.caption.setText("Motion Detect");
			break;
			
		case BASE_ANDROID_GALLERY:
			viewHolder.caption.setText("Base Android Gallery");
			break;

		default:
			break;
		}
		
		return convertView;
	}

}
