package com.ntek.wallpad;

import org.doubango.ngn.NgnEngine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.ntek.wallpad.Screens.BaseScreen;
import com.ntek.wallpad.Screens.Fragment.DialogPhoneContactsAddEdit;
import com.ntek.wallpad.Screens.Fragment.FragmentNewPhoneHeader;
import com.ntek.wallpad.lockscreen.LockscreenPatternService;

public class FragmentPhone extends BaseScreen {
	
	public static final int SELECT_PICTURE_REQUEST_CODE = 1;
	
	private final static String TAG = FragmentPhone.class.getCanonicalName();
	
	public FragmentPhone() {
		super(SCREEN_TYPE.HOME_T,TAG);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d(TAG, " oncreate fragment phone ");
		setContentView(R.layout.screen_fragment_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new FragmentNewPhoneHeader()).commit();
		}
//		Intent intent = new Intent(getApplicationContext(), LockscreenPatternService.class);	
//		stopService(intent);
//		startService(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		 
		Log.d(TAG, " qowieuqoweuiuwieuioqeuioquiweuio ");
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
				Log.d(TAG, " Activity.RESULT_OK ");
				Uri selectedImage = data.getData();
				Intent intent = new Intent("com.ntek.wallpad.action.CONTACT_PHOTO_SELECTED");
				intent.putExtra("selectedImage", selectedImage);
				sendBroadcast(intent);
				
				final boolean isCamera;
				if (data == null) {
					isCamera = true;
				} else {
					final String action = data.getAction();
					if (action == null) {
						isCamera = false;
					} else {
						isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					}
				}

				final Uri selectedImageUri;
				Log.d(TAG, " Uri selectedImageUri ");
				if (isCamera) {
					Log.d(TAG, " Uri selectedImageUri 11");
					selectedImageUri = DialogPhoneContactsAddEdit.outputFileUri;
				} else {
					Log.d(TAG, " Uri selectedImageUri 22");
					selectedImageUri = data == null ? null : data.getData();
				}

			}
			else
			{
//				Uri selectedImage = data.getData();
//				Log.d(TAG, "selectedImage " + selectedImage);
				Intent intent = new Intent("com.ntek.wallpad.action.CONTACT_PHOTO_SELECTED2");
				intent.putExtra("selectedImage", "");
				sendBroadcast(intent);
				
			}
		}
	}
	
}
