package com.ntek.wallpad.Screens.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.FragmentSettingsHeader.OnAddConfigurationListener;
import com.ntek.wallpad.Utils.OnChangeFragmentListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FragmentSettingOthers extends Fragment implements OnClickListener {

	private final static String TAG = FragmentSettingOthers.class.getCanonicalName();
	private INgnConfigurationService mConfigurationService;
	private View view;
	private OnAddConfigurationListener mOnAddConfig;
	private Button btnVideos;
	private Button btnRingtone;
	private Button btnScreenlock;
	SharedPreferences prefs;
	Uri defaultRintoneUri;
	Uri urii;
	SharedPreferences.Editor editor;
	private OnChangeFragmentListener mOnFragmentClick;
	private DialogHolderVideo videoHolder ;
	private Dialog dialog_video;
	private Context context;
	private String btnChecked = "bntChecked";
	private String btnHeight = "btnHeight";
	private String btnWeight = "btnWeight";
	int aheight = 0;
	int awidth = 0;
	Boolean cameraFront = false;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mOnAddConfig = (OnAddConfigurationListener)activity;
			mOnFragmentClick = (OnChangeFragmentListener) activity;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (mConfigurationService == null)
		{
			mConfigurationService = NgnEngine.getInstance().getConfigurationService();
		} 
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.other, container, false); 
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = prefs.edit();
		initializeui();
		context = getActivity();
		hideInputMethod();
		return view;
	}

	protected void initializeui()
	{
		btnRingtone = (Button) view.findViewById(R.id.other_ringtone);
		btnVideos = (Button) view.findViewById(R.id.other_videos);
		btnScreenlock = (Button) view.findViewById(R.id.other_screen_lock);
		
		btnRingtone.setOnClickListener(this);
		btnVideos.setOnClickListener(this);
		btnScreenlock.setOnClickListener(this);

		if(prefs.getString("SET_RINGTONE", null) == null) {
			try
			{
				defaultRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE);
				RingtoneChanged(defaultRintoneUri);
			} 
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Uri defaultRintoneUri = Uri.parse(prefs.getString("SET_RINGTONE", null));
				RingtoneChanged(defaultRintoneUri);
			}
			catch(Exception e)
			{
				e.printStackTrace();

			}
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {
		case R.id.other_ringtone :

			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);

			if(prefs.getString("SET_RINGTONE", null) == null)
			{
				try
				{
					urii = RingtoneManager.getActualDefaultRingtoneUri(getActivity(), RingtoneManager.TYPE_RINGTONE);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					return;
				}
			}
			else
			{
				try
				{
					urii = Uri.parse(prefs.getString("SET_RINGTONE", null));
				}
				catch(Exception e)
				{
					e.printStackTrace();
					return;
				}
			}
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urii);
			startActivityForResult(intent, 0);
			break;

		case R.id.other_videos :
			Log.d(TAG, " adasdjsdjakdjak ");
			othervideo();
			
			break;
			
		case R.id.other_screen_lock :
			
			mOnFragmentClick.changeFragment(null, new FragmentSettingSecurityPattern(), false);
			break;

		default:
			break;
		}
	}

	private void RingtoneChanged(Uri uri){
		Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), uri);
		if(ringtone.getTitle(getActivity()).equals("Flutey Phone")||ringtone.getTitle(getActivity()).equals("Default ringtone (Flutey Phone)")){
			//	        	othersHolder.txtRingtone.setText(String.valueOf(ringtone.getTitle(getActivity())));
		}else {
			//	        	othersHolder.txtRingtone.setText(String.valueOf(ringtone.getTitle(getActivity())));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getActivity();
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 0:
				defaultRintoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("SET_RINGTONE", String.valueOf(defaultRintoneUri));
				editor.commit();
				RingtoneChanged(defaultRintoneUri);
				break;

			default:
				break;
			}
		}
	}
	

	private class DialogHolderVideo
	{
		RadioButton sqcif;
		RadioButton qcif;
		RadioButton qvga;
		RadioButton cif;
		RadioButton hvga;
		RadioButton vga;
		RadioButton fourcif;
		RadioButton	svga;
		RadioButton seventwentyp;
		RadioGroup setupguide_rg;
		
		RadioButton radioTestBtn;
		Button OK;
		Button Cancel;
		Camera camera;
		Camera.Parameters cameraParameters;
	}
	
	private void othervideo()
	{
		dialog_video = new Dialog(context);
		dialog_video.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_video.setCancelable(true);
		dialog_video.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		dialog_video.setContentView(R.layout.other_videos);

		videoHolder = new DialogHolderVideo();
		videoHolder.sqcif = (RadioButton) dialog_video.findViewById(R.id.other_SQCIF);
		videoHolder.qcif = (RadioButton) dialog_video.findViewById(R.id.other_QCIF);
		videoHolder.qvga = (RadioButton) dialog_video.findViewById(R.id.other_QVGA);
		videoHolder.cif = (RadioButton) dialog_video.findViewById(R.id.other_CIF);
		videoHolder.hvga = (RadioButton) dialog_video.findViewById(R.id.other_HVGA);
		videoHolder.vga = (RadioButton) dialog_video.findViewById(R.id.other_VGA);
		videoHolder.fourcif = (RadioButton) dialog_video.findViewById(R.id.other_4CIF);
		videoHolder.svga = (RadioButton) dialog_video.findViewById(R.id.other_SVGA);
		videoHolder.seventwentyp = (RadioButton) dialog_video.findViewById(R.id.other_720P);
		videoHolder.setupguide_rg = (RadioGroup) dialog_video.findViewById(R.id.setupguide_rg);
		videoHolder.OK = (Button) dialog_video.findViewById(R.id.other_ok);
		videoHolder.Cancel = (Button) dialog_video.findViewById(R.id.other_cancel);

		 
		videoHolder.camera = Camera.open(0);
//		findFrontFacingCamera();
		videoHolder.cameraParameters = videoHolder.camera.getParameters();
        
		List<Size> sizes = videoHolder.cameraParameters.getSupportedPictureSizes();
		 List<Size> listStrSupportedPictureSizes = new ArrayList<Size>();
		 
		Size mSize = null;
		for (Size size : sizes) {
		    Log.i(TAG, "Available resolution: "+size.width+" "+size.height);
//		    if (wantToUseThisResolution(size)) {
		        mSize = size;
//		        break;
		        listStrSupportedPictureSizes.add(mSize);
//		    }
		}
		
		ArrayAdapter<Size> adapter = new ArrayAdapter<Size>(getActivity(), android.R.layout.simple_spinner_item, listStrSupportedPictureSizes);
		Log.i(TAG, "Chosen resolution: "+mSize.width+" "+mSize.height);
		videoHolder.cameraParameters.setPictureSize(mSize.width, mSize.height);
		videoHolder.camera.setParameters(videoHolder.cameraParameters);
		
		mConfigurationService.getInt(btnChecked, NgnConfigurationEntry.RADIOBTN);
		int checkedId = mConfigurationService.getInt(btnChecked, NgnConfigurationEntry.RADIOBTN);
		
		if (checkedId == R.id.other_SQCIF) {
			videoHolder.sqcif.setChecked(true);
		}
		else if (checkedId == R.id.other_QCIF) {
			videoHolder.qcif.setChecked(true);
		}
		else if (checkedId == R.id.other_QVGA) {
			videoHolder.qvga.setChecked(true);
		}
		else if (checkedId == R.id.other_CIF) {
			videoHolder.cif.setChecked(true);
		}
		else if (checkedId == R.id.other_HVGA) {
			videoHolder.hvga.setChecked(true);
		}
		else if (checkedId == R.id.other_VGA) {
			videoHolder.vga.setChecked(true);
		}
		else if (checkedId == R.id.other_4CIF) {
			videoHolder.fourcif.setChecked(true);
		}
		else if (checkedId == R.id.other_SVGA) {
			videoHolder.svga.setChecked(true);
		}
		else if (checkedId == R.id.other_720P) {
			videoHolder.seventwentyp.setChecked(true);
		}
		
		videoHolder.setupguide_rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) 
			{
				
				
				if (checkedId == R.id.other_SQCIF) {
					aheight = 98;
					awidth = 128;
//					dialog_video.dismiss();
				}
				else if (checkedId == R.id.other_QCIF) {
					aheight = 144;
					awidth = 176;
//					dialog_video.dismiss();
				}
				else if (checkedId == R.id.other_QVGA) {
					aheight = 240;
					awidth = 320;
//					dialog_video.dismiss();
				}
				else if (checkedId == R.id.other_CIF) {
					aheight = 288;
					awidth = 352;
//					dialog_video.dismiss();
				}
				else if (checkedId == R.id.other_HVGA) {
					aheight = 320;
					awidth = 480;
//					dialog_video.dismiss();
				}
				else if (checkedId == R.id.other_VGA) {
					aheight = 480;
					awidth = 640;
//					dialog_video.dismiss();
				}
				else if (checkedId == R.id.other_4CIF) {
					aheight = 576;
					awidth = 704;
//					dialog_video.dismiss();
				}
				else if (checkedId == R.id.other_SVGA) {
					aheight = 600;
					awidth = 800;
//					dialog_video.dismiss();
				}
				else if (checkedId == R.id.other_720P) {
					aheight = 720;
					awidth = 1280;
//					dialog_video.dismiss();
				}
				
				mConfigurationService.putInt(btnChecked, checkedId, true);
				mConfigurationService.putInt(btnHeight, aheight, true);
				mConfigurationService.putInt(btnWeight, awidth, true);
				videoHolder.cameraParameters.setPictureSize(awidth, aheight);
//				videoHolder.camera.setParameters(videoHolder.cameraParameters);
				
//				Log.d(TAG, " camera " + videoHolder.camera.getParameters().getPictureSize().height + " ****** "  + videoHolder.camera.getParameters().getPictureSize().width  );
//				videoHolder.camera.setParameters(videoHolder.cameraParameters);
//				videoHolder.  camera.release();
			}
		});
		
		videoHolder.OK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				videoHolder.cameraParameters.setPictureSize(awidth, aheight);
				videoHolder.camera.setParameters(videoHolder.cameraParameters);
				videoHolder.  camera.release();
				dialog_video.dismiss();
			}
		});

		videoHolder.Cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				videoHolder.camera.setParameters(videoHolder.cameraParameters);
				videoHolder.  camera.release();
				dialog_video.dismiss();
			}
		});

		dialog_video.show();
		dialog_video.setCancelable(false);
		dialog_video.setCanceledOnTouchOutside(false);
	}
	
	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true;
				Log.d(TAG, " camera front ");
				break;
			}
		}
		return cameraId;
	}
	
	public void showInputMethod() 
	{
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	
	public void hideInputMethod() 
	{
		Log.d(TAG, "hideInputMethod");
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
}
