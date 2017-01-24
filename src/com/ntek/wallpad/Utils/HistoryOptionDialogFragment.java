package com.ntek.wallpad.Utils;

import org.doubango.ngn.services.INgnHistoryService;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ntek.wallpad.R;

public class HistoryOptionDialogFragment extends Dialog implements android.view.View.OnClickListener {
	private Button removeButton;
	private Button cancelButton;
	private TextView tvRemoveTitle;
	private TextView tvMessage;
	private Context context;
	private Bundle bundle;
	
	int position;
	
	INgnHistoryService ngn = com.ntek.wallpad.Engine.getInstance().getHistoryService();
	public HistoryOptionDialogFragment(Context context, Bundle bundle) {
		super(context);
		this.context = context;
		this.bundle = bundle;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.dialog_phone_contact_remove);
	    
	    removeButton = (Button) findViewById(R.id.phone_contact_button_remove);
	    cancelButton = (Button) findViewById(R.id.phone_contact_button_cancel);
	    tvRemoveTitle = (TextView) findViewById(R.id.phone_textview_remove);
	    tvMessage = (TextView) findViewById(R.id.dialog_remove_textview_message);
	    tvRemoveTitle.setText("Remove from history?");
	    tvMessage.setText("Do you want to remove the Contact name from your history?");
	    removeButton.setOnClickListener(this);
	    cancelButton.setOnClickListener(this);
	    
	    position = bundle.getInt("position");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.phone_contact_button_remove:
			System.out.println("position "+ position);
			
			ngn.deleteEvent(position);
			Toast.makeText(context,  "remove from history", Toast.LENGTH_LONG).show();
			
			HistoryOptionDialogFragment.this.dismiss();
			
			break;
		case R.id.phone_contact_button_cancel:
			HistoryOptionDialogFragment.this.dismiss();
			break;
		default:
			break;
		}
	}
	
}
