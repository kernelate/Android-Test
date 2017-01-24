package com.ntek.wallpad.Screens.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Utils.ContactManager;
import com.ntek.wallpad.Utils.Contacts;

public class DialogPhoneContactsRemove extends Dialog implements android.view.View.OnClickListener {
	private Button removeButton;
	private Button cancelButton;
	private Context context;
	private Bundle bundle;
	private Contacts contact;
	private ContactManager contactManager;
	public DialogPhoneContactsRemove(Context context, Bundle bundle) {
		super(context);
		this.context = context;
		this.bundle = bundle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    setContentView(R.layout.dialog_phone_contact_remove);
	    
	    removeButton = (Button) findViewById(R.id.phone_contact_button_remove);
	    cancelButton = (Button) findViewById(R.id.phone_contact_button_cancel);
	    
	    removeButton.setOnClickListener(this);
	    cancelButton.setOnClickListener(this);
	    contactManager = ContactManager.getInstance(context);
	    contact = bundle.getParcelable("contact");
	    
	    TextView messageTextView = (TextView) findViewById(R.id.dialog_remove_textview_message);
	    messageTextView.setText("Are you sure you want to remove " + contact.getDisplayName() + " as one of your contacts?");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.phone_contact_button_remove:
			if (contactManager.removeContact(contact.getId())) {
				Toast.makeText(context, contact.getDisplayName() + " is remove from contacts", Toast.LENGTH_LONG).show();
				DialogPhoneContactsRemove.this.dismiss();
			} else {
				Toast.makeText(context, "Error on remove contact", Toast.LENGTH_LONG).show();
			}
			
			Intent intent = new Intent();
			intent.setAction("com.ntek.wallpad.Screens.Fragment.REFRESH_CONTACT_INFO");
			context.sendBroadcast(intent);
			break;
		case R.id.phone_contact_button_cancel:
			DialogPhoneContactsRemove.this.dismiss();
			break;
		default:
			break;
		}
	}
}
