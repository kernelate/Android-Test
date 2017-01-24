package com.ntek.wallpad.Screens.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.Engine;
import com.ntek.wallpad.FragmentPhone;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.ScreenAV;
import com.ntek.wallpad.Utils.BitmapDecoder;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.ContactManager;
import com.ntek.wallpad.Utils.Contacts;
import com.ntek.wallpad.Utils.Photo;
import com.ntek.wallpad.Utils.WarningDialogFragment;

public class DialogPhoneContactInformation extends DialogFragment {
	
	private static final String TAG = DialogPhoneContactInformation.class.getCanonicalName();
	private static final	String[] CONTACT_TYPE = {"DoorTalk", "Client"};
	
	private View rootView;
	private TextView titleTextView;
	private TextView contactNameEditText;
	private TextView contactNumberEditText;
	private Button doneContactButton;
	private Button CancelContactButton;
	private Spinner contactTypeSpinner; 
	private Context context;
	private Contacts contact;
	private boolean isUpdate;
	private Bitmap selectedBitmap;
	private String selectedImagePath;
	private ImageView phoneContactImageView;
	private ImageButton btnVideoCall;
	private ImageButton btnVoiceCall;
	private INgnSipService sipService;
	
	private Bundle bundle;
	private String initialDisplayName;
	private int initialPhoneNumber;
	
	private ContactManager contactManager;
	private BroadcastReceiver imagePickedBroadcastReceiver;
	private WarningDialogFragment warningDialog;
	public static Uri outputFileUri;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		sipService = Engine.getInstance().getSipService();
		
		rootView = inflater.inflate(R.layout.fragment_phone_contact_details, container, false);
		
		this.context = getActivity();
		Log.d(TAG, getActivity().toString());
		contactManager = ContactManager.getInstance(context);
		
		initializeUI();
		
		return rootView;
	}

	private void initializeUI()
	{
	    titleTextView = (TextView) rootView.findViewById(R.id.contact_add_edit_title_text_view1);
		contactNumberEditText = (TextView) rootView.findViewById(R.id.phone_contacts_textview_contactdetails);
		contactNameEditText= (TextView) rootView.findViewById(R.id.phone_contacts_textview_contactname);
		phoneContactImageView = (ImageView) rootView.findViewById(R.id.phone_contacts_imageview_image);
		doneContactButton = (Button) rootView.findViewById(R.id.phone_contact_button_done1);
		CancelContactButton = (Button) rootView.findViewById(R.id.phone_contact_button_cancel1);
		contactTypeSpinner = (Spinner) rootView.findViewById(R.id.contact_type_spinner1);
		btnVideoCall = (ImageButton) rootView.findViewById(R.id.phone_contacts_button_videocall);
		btnVoiceCall = (ImageButton) rootView.findViewById(R.id.phone_contacts_button_voicecall);
		
		doneContactButton.setOnClickListener(SaveClickListener);
		CancelContactButton.setOnClickListener(CancelClickListener);
		phoneContactImageView.setOnClickListener(SelectImageClickListener);
		btnVideoCall.setOnClickListener(ClickListener);
		btnVoiceCall.setOnClickListener(ClickListener);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.contact_type_spinner_item, CONTACT_TYPE);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		contactTypeSpinner.setAdapter(adapter);
		contactTypeSpinner.setOnItemSelectedListener(itemSelectedListener);
		
		bundle = getArguments();
		warningDialog = new WarningDialogFragment(context);
		
		String title = "Add Contact";
		isUpdate = false;
		if(bundle != null) {
			title = bundle.getString("title");
		}
		
		if(title.equals("Edit Contact")) {
			isUpdate = true;
			contact = bundle.getParcelable("contact");
			
			if(contact == null) {
				String displayName = bundle.getString("displayName");
				String callId = bundle.getString("callId");
				
				initialPhoneNumber = Integer.parseInt(callId);
				initialDisplayName = displayName;
			} else {
				initialPhoneNumber = contact.getPhoneNumber();
				initialDisplayName = contact.getDisplayName();
			}
			
			contactNameEditText.setText(initialDisplayName);
			contactNumberEditText.setText(Integer.toString(initialPhoneNumber));
			
			if(contact != null) {
				if(contact.getPhotoFileId() > 0) {
					selectedBitmap = BitmapFactory.decodeFile(contact.getPhoto().getFilename());
					if(selectedBitmap != null) phoneContactImageView.setImageBitmap(selectedBitmap);
					else setDefaultImage(contact.getType());
				}
			}
			
		} else {
			int selectedTab = bundle.getInt("selectedTab", 1);
			setDefaultImage(selectedTab);
			contactTypeSpinner.setSelection(selectedTab);
		}
		
		titleTextView.setText(title);
		
		imagePickedBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d(TAG, "onReceive() : " + action);
				if (action.equals("com.ntek.wallpad.action.CONTACT_PHOTO_SELECTED")) {
					try {
						Uri selectedImage = intent.getParcelableExtra("selectedImage");
						selectedImagePath = getRealPathFromURI(selectedImage);
						selectedBitmap = BitmapFactory.decodeFile(selectedImagePath);
						phoneContactImageView.setImageBitmap(selectedBitmap);
					} catch (Exception e) {
						e.printStackTrace();
						selectedBitmap = null;
					}
				}
			}
		};

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.ntek.wallpad.action.CONTACT_PHOTO_SELECTED");
		context.registerReceiver(imagePickedBroadcastReceiver, intentFilter);
	}
	
	private OnClickListener ClickListener = new OnClickListener() {
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.phone_contacts_button_videocall:
				if (sipService.isRegistered()) {
					
					if(!contactNumberEditText.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
						Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
						return;
					}
					ScreenAV.makeCall(contactNumberEditText.getText().toString(), NgnMediaType.AudioVideo);
					getDialog().dismiss();
				} else {
					Toast.makeText(getActivity(), "Register to sip", Toast.LENGTH_SHORT).show();
				}
				break;
				
			case R.id.phone_contacts_button_voicecall:
				if (sipService.isRegistered()) {
					
					if(!contactNumberEditText.getText().toString().matches("-?\\d+(\\.\\d+)?")) {
						Toast.makeText(getActivity(), "not Numeric phone number", Toast.LENGTH_SHORT).show();
						return;
					}
					ScreenAV.makeCall(contactNumberEditText.getText().toString(), NgnMediaType.Audio);		
					getDialog().dismiss();
				} else {
					Toast.makeText(getActivity(), "Register to sip", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
		};
	
	@Override
	public void dismiss() {
		super.dismiss();
		if (imagePickedBroadcastReceiver != null) {
			context.unregisterReceiver(imagePickedBroadcastReceiver);
		}
	}

	private void setDefaultImage(int position) {
		if(position == 0) {
			phoneContactImageView.setImageResource(R.drawable.ic_doortalk_select_image_thumbnail);
		} else {
			phoneContactImageView.setImageResource(R.drawable.ic_select_image_thumbnail);
		}
	}
	private OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			arg0.setSelection(arg2);
			if(selectedBitmap == null) {
				setDefaultImage(arg2);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	};
	
	private String getRealPathFromURI(Uri contentURI) {
	    String result= contentURI.getPath();
	    String[] filePathColumn = { MediaStore.Images.Media.DATA };
	    try {
	    	Cursor cursor = context.getContentResolver().query(contentURI, filePathColumn, null, null, null);
	    	cursor.moveToFirst(); 
	    	int colIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	    	result = cursor.getString(colIndex);
	    	cursor.close();
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
	    return result;
	}
	
	private OnClickListener SelectImageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			selectImageFromGallery();
			//openImageIntent();
		}
	};

	public void selectImageFromGallery() {
		Intent _intent = new Intent();
		_intent.setType("image/*");
		_intent.setAction(Intent.ACTION_PICK);
		getActivity().startActivityForResult(
				Intent.createChooser(_intent, "Select Picture"), 
				FragmentPhone.SELECT_PICTURE_REQUEST_CODE
				);
	}

	private void openImageIntent() {
		Log.d(TAG,"openImageIntent()");

		// Determine Uri of camera image to save.
		File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
		root.mkdirs();

		final String fname = System.currentTimeMillis() + ".jpg";
		final File sdImageMainDirectory = new File(root, fname);
		outputFileUri = Uri.fromFile(sdImageMainDirectory);

		// Camera.
		final List<Intent> cameraIntents = new ArrayList<Intent>();
		final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		final PackageManager packageManager = getActivity().getPackageManager();
		final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
		for(ResolveInfo res : listCam) {
			final String packageName = res.activityInfo.packageName;
			final Intent intent = new Intent(captureIntent);
			intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			intent.setPackage(packageName);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			cameraIntents.add(intent);
		}

		// Filesystem.
		final Intent galleryIntent = new Intent();
		galleryIntent.setType("image/*");
		galleryIntent.setAction(Intent.ACTION_PICK);

		// Chooser of filesystem options.
		final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

		// Add the camera options.
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
		
		getActivity().startActivityForResult(chooserIntent, FragmentPhone.SELECT_PICTURE_REQUEST_CODE);
	}
	
	private OnClickListener CancelClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dismiss();
		}
		
	
};
	private OnClickListener SaveClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean sameNumber = false, sameName = false;
			boolean isSaved = false;
			String dialogMessage = "";
			final String name = contactNameEditText.getText().toString();
			final String numberTxt = contactNumberEditText.getText().toString();
			final String contactType = (String) contactTypeSpinner.getSelectedItem();
			final int type = contactType.equals(CONTACT_TYPE[1]) ? 1 : 0; //get which one is clicked
			
			int number = 0;
			try {
				number = Integer.parseInt(numberTxt); //parse int
			}
			catch(NumberFormatException e) {
				e.printStackTrace();
			}
			
			if (CommonUtilities.checkIfEmpty(name) || (CommonUtilities.checkIfEmpty(numberTxt))){	
				Toast.makeText(context, "Don't leave any fields empty", Toast.LENGTH_LONG).show();
				return;
			}
			if (!(number > 0)){
				Toast.makeText(context,  "Contact number should be numeric", Toast.LENGTH_LONG).show();
				return;
			}
			
			Photo contactPhoto = null;
			if(selectedBitmap != null) {
				contactPhoto = new Photo(0, selectedImagePath, selectedBitmap.getHeight(), selectedBitmap.getWidth(), BitmapDecoder.byteSizeOf(selectedBitmap));
			} else {
				contactPhoto = type == 1? new Photo(0, "client", 0, 0, 0) : new Photo(0, "doortalk", 0, 0, 0);
			}
			
			if (isUpdate) {
				Contacts edittedContact = contactManager.getContactById(contact.getId());
				edittedContact.setDisplayName(name);
				edittedContact.setPhoneNumber(number);
				edittedContact.setType(type);
				
				sameNumber = initialPhoneNumber != number && contactManager.isNumberExist(numberTxt);
				sameName = !initialDisplayName.equals(name) && contactManager.isNameExist(name);
				isSaved = !sameName && !sameNumber && contactManager.editContact(edittedContact, contactPhoto);
				dialogMessage = "Contact has been successfully editted";
			}
			else{
				dialogMessage = "Contact has been successfully added";
				sameNumber = contactManager.isNumberExist(numberTxt);
				sameName = contactManager.isNameExist(name);
				isSaved = !sameName && !sameNumber && (contactManager.addContact(new Contacts(name, number, -1, "", "", type, 0), contactPhoto) > 0);
			}age_button:
//				Fragment leftPanel = getFragmentManager().findFragmentById(R.id.phone_leftpanel);
//				
//				int selectedTab =  1;
//				if(leftPanel instanceof FragmentPhoneContactList) {
//					FragmentPhoneContactList contactListFragment = (FragmentPhoneContactList) leftPanel;
//					selectedTab = contactListFragment.getSelectedTab() > 0 ? 1 : 0;
//				} 
//				
//				Bundle bundle = new Bundle();
//				bundle.putString("title", "Add Contact");
//				bundle.putInt("selectedTab", selectedTab);
//				
//				DialogPhoneContactsAddEdit dialogContacts = new DialogPhoneContactsAddEdit();
//				dialogContacts.setArguments(bundle);
//				dialogContacts.show(getFragmentManager(), "add");
//				break;
			
			if(isSaved) {
				FragmentNewPhoneContacts.selectedContact = contactManager.getContactByNumber(number);
				warningDialog.setTitle("Information");
				warningDialog.setMessage(dialogMessage);
				warningDialog.show();
			} else {
				warningDialog.setTitle("Warning");
				warningDialog.setMessage(sameName ? "Same contact name already exists" : sameNumber ? "Same contact number already exists": "An error occured while saving the contact");
				warningDialog.show();
				return;
			}
			DialogPhoneContactInformation.this.dismiss();
		}
	};
}
