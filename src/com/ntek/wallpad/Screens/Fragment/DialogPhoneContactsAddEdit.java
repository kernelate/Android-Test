package com.ntek.wallpad.Screens.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.FragmentPhone;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Database.DbHandler;
import com.ntek.wallpad.Database.DbHelper;
import com.ntek.wallpad.Utils.BitmapDecoder;
import com.ntek.wallpad.Utils.CommonUtilities;
import com.ntek.wallpad.Utils.ContactManager;
import com.ntek.wallpad.Utils.Contacts;
import com.ntek.wallpad.Utils.Photo;
import com.ntek.wallpad.Utils.WarningDialogFragment;

public class DialogPhoneContactsAddEdit extends DialogFragment {

	private static final String TAG = DialogPhoneContactsAddEdit.class.getCanonicalName();
	private static final	String[] CONTACT_TYPE = {"DoorTalk", "Client"};
	private static final int YOUR_SELECT_PICTURE_REQUEST_CODE = 2;
	private View rootView;
	private TextView titleTextView;
	private EditText contactNameEditText;
	private EditText contactNumberEditText;
	private Button doneContactButton;
	private Button CancelContactButton;
	private Spinner contactTypeSpinner; 
	private Context context;
	private Contacts contact;
	private boolean isUpdate;
	private Bitmap selectedBitmap;
	private String selectedImagePath;
	private ImageView phoneContactImageView;

	private Bundle bundle;
	private String initialDisplayName;
	private int initialPhoneNumber;

	private ContactManager contactManager;
	private BroadcastReceiver imagePickedBroadcastReceiver;
	private WarningDialogFragment warningDialog;
	public static Uri outputFileUri;

	private SQLiteDatabase db=null;
	private  DbHelper mdb=null;
	//Camera Image
	Uri cameraImageUri;
	String cameraImageName;
	Bitmap image = null;
//	 private static final int CAMERA_REQUEST = 1888; 
	 int REQUEST_IMAGE = 1;
	 
//	 private static final int PICK_FROM_CAMERA = 1;
//		private static final int CROP_FROM_CAMERA = 2;
//		private static final int PICK_FROM_FILE = 3;
//		private Uri mImageCaptureUri;
//		private ImageView mImageView;
		private String selectedImagePath1;
	 DbHandler dbHelper;
//		private AlertDialog dialog;
	 private Bitmap selectedBitmap2;
	 
	 
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

		rootView = inflater.inflate(R.layout.dialog_phone_contact_edit_add, container, false);

		this.context = getActivity();
		Log.d(TAG, getActivity().toString());
		contactManager = ContactManager.getInstance(context);
		initializeUI();
//		captureImageInitialization();
//		dbHelper = new DbHandler(this);
		return rootView;
	}

	private void initializeUI()
	{
		titleTextView = (TextView) rootView.findViewById(R.id.contact_add_edit_title_text_view);
		contactNumberEditText = (EditText) rootView.findViewById(R.id.phone_contact_edit_text_phone);
		contactNameEditText= (EditText) rootView.findViewById(R.id.phone_contact_edit_text_name);
		phoneContactImageView = (ImageView) rootView.findViewById(R.id.phone_contact_image_View);
		doneContactButton = (Button) rootView.findViewById(R.id.phone_contact_button_done);
		CancelContactButton = (Button) rootView.findViewById(R.id.phone_contact_button_cancel);
		contactTypeSpinner = (Spinner) rootView.findViewById(R.id.contact_type_spinner);

		doneContactButton.setOnClickListener(SaveClickListener);
		CancelContactButton.setOnClickListener(CancelClickListener);
		phoneContactImageView.setOnClickListener(SelectImageClickListener);

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
			Log.d(TAG, " editcontact if");
			isUpdate = true;
			contact = bundle.getParcelable("contact");
			initialPhoneNumber = contact.getPhoneNumber();
			initialDisplayName = contact.getDisplayName();
			contactNameEditText.setText(initialDisplayName);
			contactNumberEditText.setText(Integer.toString(initialPhoneNumber));
			doneContactButton.setText("Done");

			if(contact.getPhotoFileId() > 0) {
				selectedBitmap = BitmapFactory.decodeFile(contact.getPhoto().getFilename());
				if(selectedBitmap != null) phoneContactImageView.setImageBitmap(selectedBitmap);
				else setDefaultImage(contact.getType());
			}

			contactTypeSpinner.setSelection(contact.getType());
		} else {
			
			Log.d(TAG, " editcontact else");
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
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						selectedBitmap = null;
					}
				}
				else
				{
					Log.d(TAG, " cameraImageUri " + cameraImageUri);
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					BitmapFactory.decodeFile(getPath(getActivity(), cameraImageUri), options);
					options.inSampleSize = calculateInSampleSize(options, 100, 100);
	
					Bitmap bitmap = BitmapFactory.decodeFile(getPath(getActivity(), cameraImageUri), options);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
					phoneContactImageView.setImageBitmap(bitmap);
					
					
					selectedImagePath1 = getRealPathFromURI(cameraImageUri);
					selectedBitmap2 = BitmapFactory.decodeFile(selectedImagePath1);
				}
			}
		};

		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.ntek.wallpad.action.CONTACT_PHOTO_SELECTED");
		intentFilter.addAction("com.ntek.wallpad.action.CONTACT_PHOTO_SELECTED2");
		context.registerReceiver(imagePickedBroadcastReceiver, intentFilter);
		
		
	}

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
		public void onClick(View v) 
		{
			//			selectImageFromGallery();
			//openImageIntent();

//			dialog.show();
			final Dialog dialog = new Dialog(getActivity());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.addcontactimg);
			dialog.setCancelable(false);
			TextView tvDialog1 = (TextView) dialog.findViewById(R.id.textDialog1);
			TextView tvDialog2 = (TextView) dialog.findViewById(R.id.textDialog2);
			Button btnCancel = (Button) dialog.findViewById(R.id.declineButton);

			dialog.show();

			tvDialog1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) 
				{
					Log.d(TAG, "take photo");
					cameraImageName = ""+System.currentTimeMillis();
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, cameraImageName);
					values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
					cameraImageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					Log.d(TAG, " cameraImageUri " + cameraImageUri);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
					getActivity().startActivityForResult(intent, 2);
//					******************************************
//					 Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
//					 getActivity().startActivityForResult(cameraIntent, REQUEST_IMAGE); 
//					 **********************************************
					dialog.dismiss();
				}
			});

			tvDialog2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) 
				{
					selectImageFromGallery();
					dialog.dismiss();
				}
			});

			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) 
				{
					dialog.dismiss();
				}
			});

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
			Log.d(TAG, " na click add btn ");
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
			if(selectedBitmap != null) 
			{
				contactPhoto = new Photo(0, selectedImagePath, selectedBitmap.getHeight(), selectedBitmap.getWidth(), BitmapDecoder.byteSizeOf(selectedBitmap));
			}
			else 
			{
//				contactPhoto = type == 1? new Photo(0, "client", 0, 0, 0) : new Photo(0, "doortalk", 0, 0, 0);
				contactPhoto = new Photo(0, selectedImagePath1, selectedBitmap2.getHeight(), selectedBitmap2.getWidth(), BitmapDecoder.byteSizeOf(selectedBitmap2));
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
			}
			//			age_button:
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
			
			DialogPhoneContactsAddEdit.this.dismiss();
		}
	};
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.d(TAG, " onActivityResult ");
		if(requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK){

			Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
			phoneContactImageView.setImageBitmap(capturedImage);
		}
	}
	

//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//		//        if(resultCode == RESULT_CANCELED){
//		if(resultCode == 0){
//			if(requestCode == 100) {
//				ContentResolver cr = context.getContentResolver();
//				cr.delete(cameraImageUri, "title=" + cameraImageName, null);
//			}
//		}else {
//			if (requestCode == 999) {
//				String fileType = context.getContentResolver().getType(intent.getData());
//				if (fileType == null) {
//					Toast.makeText(getActivity(), "Invalid image! Try again.", Toast.LENGTH_SHORT).show();
//					return;
//				}
//				if (fileType.equals("image/png") || fileType.equals("image/jpeg") || fileType.equals("image/jpg")) {
//					if (resultCode == -1) {
//						//get image Uri
//						Uri pickedImage = intent.getData();
//
//						//check image size
//						BitmapFactory.Options options = new BitmapFactory.Options();
//						options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//						BitmapFactory.decodeFile(getPath(getActivity(), pickedImage), options);
//						options.inSampleSize = calculateInSampleSize(options, 100, 100);
//
//						Bitmap bitmap = BitmapFactory.decodeFile(getPath(getActivity(), pickedImage), options);
//						ByteArrayOutputStream baos = new ByteArrayOutputStream();
//						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
//						phoneContactImageView.setImageBitmap(bitmap);
//						image = bitmap;
//					}
//				} else {
//					Toast.makeText(getActivity(), "Invalid image! Try again.", Toast.LENGTH_SHORT).show();
//				}
//			} else if (requestCode == 100 && resultCode == -1) {
//				//check image size
//				BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//				BitmapFactory.decodeFile(getPath(getActivity(), cameraImageUri), options);
//				options.inSampleSize = calculateInSampleSize(options, 100, 100);
//
//				Bitmap bitmap = BitmapFactory.decodeFile(getPath(getActivity(), cameraImageUri), options);
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
//				phoneContactImageView.setImageBitmap(bitmap);
//				image = bitmap;
//			}
//		}
//	}


	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause()11111111111111");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()222222222222");
	}
	
	
}
