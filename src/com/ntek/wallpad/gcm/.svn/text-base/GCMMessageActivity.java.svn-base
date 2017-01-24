package com.ntek.wallpad.gcm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.utils.NgnStringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ntek.wallpad.GCMIntentService;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Screens.ScreenAV;

public class GCMMessageActivity extends Activity {
	private static final String TAG = GCMMessageActivity.class.getCanonicalName();
	Button back_button;
	Button video_button, audio_button;
	TextView display_name_text_view, date_time_control_text_view, title_text_view;  
	LinearLayout door_control_dispaly_linear_layout; 
	WebView motion_detect_image_web_view; 
	// Asyntask
	AsyncTask<Void, Void, String> mImageTask;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Connection detector
	ConnectionDetector cd;

	boolean onResume_chk = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		onResume_chk = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("SHCHO", "onResume()");
		if (getIntent() != null && onResume_chk != true) {
			updateScreenFromIntent(getIntent());
			onResume_chk = true;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i("SHCHO", "onNewIntent()");
		if (onResume_chk != false) {
			updateScreenFromIntent(intent);
		}
	}

	private void updateScreenFromIntent(Intent intent) {
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(GCMMessageActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		try {
			Bundle extras = intent.getExtras();
			final String base_url = extras.getString("base_url");
			final String id = extras.getString("event_id");
			final String unique_key = extras.getString("unique_key");
			final String device_number = extras.getString("device_number");
			final String displayName = extras.getString("displayName");
			final String event_occurrence_time = extras.getString("event_occurrence_time");
			final String text_summary = extras.getString("text_summary");
			final int type = extras.getInt("type");
			
			setContentView(R.layout.event_door_control);
			
			title_text_view = (TextView) findViewById(R.id.textView1);
			display_name_text_view  = (TextView) findViewById(R.id.displayName);
			door_control_dispaly_linear_layout = (LinearLayout) findViewById(R.id.door_control_display_linear_layout);
			motion_detect_image_web_view  = (WebView) findViewById(R.id.motion_detect_display_web_view);
			date_time_control_text_view = (TextView) findViewById(R.id.date_time_control);
			video_button = (Button) findViewById(R.id.video_btn);
			audio_button = (Button) findViewById(R.id.audio_btn);
			
			back_button = (Button) findViewById(R.id.backButton);
			back_button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					GCMMessageActivity.this.finish();

				}
			});
			
			if (extras != null) {
				audio_button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(NgnEngine.getInstance().getSipService().isRegistered() && !NgnStringUtils.isNullOrEmpty(device_number)) {					
							ScreenAV.makeCall(device_number, NgnMediaType.Audio);
						}
					}
				});
				
				video_button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if(NgnEngine.getInstance().getSipService().isRegistered() && !NgnStringUtils.isNullOrEmpty(device_number)) {					
							ScreenAV.makeCall(device_number, NgnMediaType.AudioVideo);
						}
					}
				});
				
				if(type == GCMIntentService.SENSOR_RELAY_TYPE) {
					String status = "";
					door_control_dispaly_linear_layout.setVisibility(View.VISIBLE);
					motion_detect_image_web_view.setVisibility(View.GONE);
					title_text_view.setText("Door Control");
					display_name_text_view.setText(displayName);

					TextView door_status  = (TextView) findViewById(R.id.door_status);
					LinearLayout door_status_color  = (LinearLayout) findViewById(R.id.door_status_color);
					LinearLayout duration_linear_layout = (LinearLayout) findViewById(R.id.duration_linear_layout);
					ImageView image_lock = (ImageView) findViewById(R.id.image_lock);
					TextView control_time  = (TextView) findViewById(R.id.control_time);
					TextView control_sec  = (TextView) findViewById(R.id.control_sec);

					String[] doorStatus = text_summary.split("\\|");
					if(doorStatus[1].equals("Unlocked")) {
						door_status.setText("Unlocked");
						status = "Locked -> Unlocked";
						door_status_color.setBackgroundColor(Color.parseColor("#8EC63F"));
						image_lock.setImageResource(R.drawable.unlock_normal);
					} 
					else if(doorStatus[1].equals("Locked")) {
						door_status.setText("Locked");
						door_status_color.setBackgroundColor(Color.parseColor("#F6454C"));
						image_lock.setImageResource(R.drawable.unlock_normal_02);
						status = "Unlocked -> Locked";
					}

					control_time.setText(event_occurrence_time.substring(event_occurrence_time.indexOf(" ") + 1, event_occurrence_time.length()));
					if(doorStatus.equals("0")) {
						duration_linear_layout.setVisibility(View.GONE);
					} else {
						duration_linear_layout.setVisibility(View.VISIBLE);
						control_sec.setText(doorStatus[0]);
					}
					date_time_control_text_view.setText(status + " | " + event_occurrence_time);
				}
				else if(type == GCMIntentService.MOTION_DETECT_TYPE) {
					door_control_dispaly_linear_layout.setVisibility(View.GONE);
					motion_detect_image_web_view.setVisibility(View.VISIBLE);
					title_text_view.setText("Motion Detect");
					display_name_text_view.setText(displayName);
					date_time_control_text_view.setText(event_occurrence_time);

					mImageTask = new AsyncTask<Void, Void, String>() {
						HttpResponse response = null;					
						HttpPost httpPost = null;

						@Override
						protected String doInBackground(Void... params) {
							String result = null;
							StringBuilder sImageUrl = new StringBuilder();
							sImageUrl.append(base_url);
							sImageUrl.append("motion/fetch_image");
							
							Log.d(TAG, sImageUrl.toString());
							try {
								httpPost = new HttpPost(sImageUrl.toString());
								JSONObject message = new JSONObject();
								message.put("event_id", id);
								MultipartEntity entity = new MultipartEntity();
								entity.addPart("event_id",new StringBody(message.toString()));
								httpPost.setEntity( entity );

								DefaultHttpClient httpClient = new DefaultHttpClient();
								response = httpClient.execute(httpPost);

								if(response != null && response.getEntity() != null) {
									result = EntityUtils.toString(response.getEntity());
								}
							} catch (ClientProtocolException e2) {
								e2.printStackTrace();
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							} catch (IOException e2) {
								e2.printStackTrace();
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							Log.d(TAG, "result: "+ result);
							return result;
						}

						@Override
						protected void onPostExecute(String result) {
							mImageTask = null;						
							if(result !=null && !result.equals("")) {
								try {
									JSONObject json = new JSONObject(result);
									String image_url = json.getString("image_url"); 
									Log.d(TAG, "Image Url: "+image_url);
									if(image_url != null) {
										Log.d(TAG, "Connected... Loading Image........");
										motion_detect_image_web_view.loadUrl(image_url);
									}
								}
								catch (JSONException e) {
									e.printStackTrace();
								}
							}						
						}
					};
					mImageTask.execute();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 private class ImageLoader extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			return null;
		}
		 
		
		
	 }
}