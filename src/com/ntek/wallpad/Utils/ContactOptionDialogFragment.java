package com.ntek.wallpad.Utils;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.Fragment.DialogPhoneContactsAddEdit;
import com.ntek.wallpad.Screens.Fragment.DialogPhoneContactsRemove;

public class ContactOptionDialogFragment extends DialogFragment {
	private Button editContactButton;
	private Button removeContactButton;
	private Contacts contact;
	public ContactOptionDialogFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Bundle bundle = getArguments();
		if(bundle != null) {
			contact = getArguments().getParcelable("contact");
		}
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		View rootView = inflater.inflate(R.layout.dialog_phone_contact, container,
				false);
		editContactButton = (Button) rootView.findViewById(R.id.phone_contact_button_edit);
		removeContactButton = (Button) rootView.findViewById(R.id.phone_contact_button_remove);
		editContactButton.setOnClickListener(onClick);
		removeContactButton.setOnClickListener(onClick);
		
		return rootView;
	}
	
	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Bundle bundle = new Bundle();
			switch (v.getId()) {
			case R.id.phone_contact_button_edit:
				Log.d(" contact ", " onclick shit ");
				bundle.putString("title", "Edit Contact");
				bundle.putParcelable("contact", contact);
				DialogPhoneContactsAddEdit editContactDialogFragment = new DialogPhoneContactsAddEdit();
				editContactDialogFragment.setArguments(bundle);
				editContactDialogFragment.show(getFragmentManager(), "edit");
				
				break;
			case R.id.phone_contact_button_remove:
				bundle.putParcelable("contact", contact);
				DialogPhoneContactsRemove removeContactDialogFragment = new DialogPhoneContactsRemove(getActivity(), bundle);
				removeContactDialogFragment.show();
				break;
			default:
				break;
			}
			ContactOptionDialogFragment.this.dismiss();
		}
	};
}
