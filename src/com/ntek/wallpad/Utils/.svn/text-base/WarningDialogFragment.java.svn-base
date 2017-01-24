package com.ntek.wallpad.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ntek.wallpad.R;

public class WarningDialogFragment extends Dialog {
	
	private Button okButton;
	private TextView messageTextView;
	private TextView titleTextView;
	
	public void setMessage(String message) {
		messageTextView.setText(message);
	}
	
	public void setTitle(CharSequence title) {
		titleTextView.setText(title);
	}

	public WarningDialogFragment(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_phone_contact_add_warning);
		
		okButton = (Button) findViewById(R.id.phone_contact_button_warning_ok);
		messageTextView = (TextView) findViewById(R.id.phone_textview_warning_statement);
		titleTextView = (TextView) findViewById(R.id.phone_textview_warning);
		
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				WarningDialogFragment.this.dismiss();
			}
		});
	}
}
