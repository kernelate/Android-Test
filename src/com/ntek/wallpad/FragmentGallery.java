package com.ntek.wallpad;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;

import com.ntek.wallpad.Screens.BaseScreen;

public class FragmentGallery extends BaseScreen {

	private final static String TAG = FragmentPhone.class.getCanonicalName();
	private final static int KITKAT = 19;

	public FragmentGallery() {
		super(SCREEN_TYPE.GALLERY_FRAG_T, TAG);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Intent intent1 = new Intent(Intent.ACTION_VIEW,
				Uri.parse("content://media/internal/images/media"));
		refresh();
		startActivity(intent1);
	}

	private void refresh()
	{
		if (Build.VERSION.SDK_INT >= KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File pictureDirectory = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
			Uri contentUri = Uri.fromFile(pictureDirectory); //out is your output file
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
	}

}
