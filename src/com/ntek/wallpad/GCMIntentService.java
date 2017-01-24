package com.ntek.wallpad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.ntek.wallpad.R;
import com.ntek.wallpad.Database.WallpadDatabaseDao;
import com.ntek.wallpad.Model.HistoryGcmNotificationEvent;
import com.ntek.wallpad.Model.HistoryGcmNotificationEvent.NotificationType;
import com.ntek.wallpad.gcm.GCMMessageActivity;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = GCMIntentService.class.getCanonicalName();

	private static final int DETECT = 0;
	private static final int OTHER = 1;
	//
	public static final String TAKE_PICTURE = "com.ntek.DTtalk.TAKE_PICTURE";
	public static final int FLAG_INCLUDE_STOPPED_PACKAGES = 32;
	public static final int FLAG_EXCLUDE_STOPPED_PACKAGES = 16;
	public static final String URL = "URL";
	
	public static String GCMRegisteredId;
	boolean send_gcm_chk = false;
	//
	Context m_context = null;
	
	public static final int MOTION_DETECT_TYPE = 1;
	public static final int SENSOR_RELAY_TYPE = 2;
	
	boolean statusChange = false;
	
	public static final String name="dttalk";
	public static final String SENDER_ID = "772474932686";
	
	public static final String DISPLAY_MESSAGE_ACTION =
            "com.androidhive.pushnotifications.DISPLAY_MESSAGE";
	public static final String EXTRA_MESSAGE = "message";
	
    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, "Your device registred with GCM");
        //Log.d("NAME", MainActivity.name);
        register(context, name, registrationId);
    }

    /**
     * Method called on device unregistred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        if(m_context==null) {
			m_context = context;
		}
        WallpadDatabaseDao cldb = new WallpadDatabaseDao(m_context);
        
		String motion_detect_eventhistory_message = intent.getExtras().getString("motion_id");
		String motion_detect_eventhistory_images_message = intent.getExtras().getString("motion_id");
		
		String sensor_relay_message = intent.getExtras().getString("relay_id");
		
		String base_url_message = intent.getExtras().getString("base_url");
         
		String unique_key = intent.getExtras().getString("unique_key");
		String device_number = intent.getExtras().getString("device_no");
		String displayName = intent.getExtras().getString("displayName");
		String event_occurrence_time = intent.getExtras().getString("occurrence");
		String text_summary = intent.getExtras().getString("summary");
		String event_inquiry_ids = intent.getExtras().getString("inquiry_ids");
		String status = intent.getExtras().getString("status");
		
		Log.i(TAG, "motion_detect_eventhistory_message " + motion_detect_eventhistory_message);
		Log.i(TAG, "motion_detect_eventhistory_images_message " + motion_detect_eventhistory_images_message);
		Log.i(TAG, "sensor_relay_message " + sensor_relay_message);
		Log.i(TAG, "base_url_message " + base_url_message);
		Log.i(TAG, "unique_key " + unique_key);
		Log.i(TAG, "device_number " + device_number);
		Log.i(TAG, "displayName " + displayName);
		Log.i(TAG, "event_occurrence_time " + event_occurrence_time);
		Log.i(TAG, "text_summary " + text_summary);
	
		String[] event_inquiry_id_array = event_inquiry_ids.split("\\|");
		String[] event_inquiry_status_array = status.split("\\|");
		
		for(int i = 0; i<event_inquiry_status_array.length ; i++) {
			Log.i(TAG, "(event id/ status) " + event_inquiry_id_array[i] + " : " + event_inquiry_status_array[i]);
		}
		
		String event_inquiry_id = "";
		String event_inquiry_status = "";
	
		cldb.Open();
		event_inquiry_id = cldb.getServerEventInquiryId(unique_key);
		String myGcm_id = GCMRegistrar.getRegistrationId(m_context);
		if(myGcm_id == null) {
			myGcm_id = cldb.getGcmId();
		}
		cldb.Close();
		
		Log.i(TAG, "myGcm_id " + myGcm_id);
		Log.i(TAG, "event_inquiry_id " + event_inquiry_id);
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String date = dateformat.format(new Date());
		
		for(int i = 0; i < event_inquiry_id_array.length; i++) {
			if(event_inquiry_id_array[i].equals(event_inquiry_id)) {
				cldb.Open();
				boolean isUniqueExist = cldb.inquiryExist(unique_key, myGcm_id);
				Log.i(TAG, "isUniqueExist :" + isUniqueExist);
				if(isUniqueExist) {
					event_inquiry_status = cldb.getInquiryStatus(unique_key, myGcm_id);
					if(!event_inquiry_status_array[i].equals(event_inquiry_status)) {
						statusChange = true;
						cldb.updateInquiry(unique_key, event_inquiry_status_array[i], date);
					}
					else {
						statusChange = false;
					}
					event_inquiry_status = event_inquiry_status_array[i];
					Log.i(TAG, "statusChange :" + statusChange);
				}
				cldb.Close();
			}
		}
		
		long occurrence_time = 0;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		try {
			Date d = simpleDateFormat.parse(event_occurrence_time);
			occurrence_time = d.getTime(); // long value of event_occurrence_time
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		Log.i(TAG, "status :" + event_inquiry_status);
		if(event_inquiry_status.equals("active")) {
			if(base_url_message!=null && motion_detect_eventhistory_message!=null && motion_detect_eventhistory_images_message!=null) {
				//TODO Modify saving option, to be client's choice to be saved not automatic
				new MotionDetectImageDownloader(base_url_message, motion_detect_eventhistory_images_message, device_number, displayName, occurrence_time, text_summary).execute();
				
				NgnApplication.acquirePowerLock();
				displayMessage(context, getString(R.string.string_get_gcm_msg_obj_detect));
				generateNotification(context, base_url_message, motion_detect_eventhistory_images_message, unique_key, device_number, displayName, event_occurrence_time, text_summary, MOTION_DETECT_TYPE);
				NgnApplication.releasePowerLock();
			}
			if(base_url_message!=null && sensor_relay_message!=null) {
				final HistoryGcmNotificationEvent mHistoryEvent = new HistoryGcmNotificationEvent();
				mHistoryEvent.setNotificationType(NotificationType.DoorControl);
				mHistoryEvent.setTextSummary(text_summary);
				mHistoryEvent.setDisplayName(displayName);
				mHistoryEvent.setRemoteParty(device_number);
				mHistoryEvent.setStartTime(occurrence_time);
				mHistoryEvent.setEndTime(new Date().getTime());
				mHistoryEvent.setImagePath(text_summary.split("\\|")[1]);
				NgnEngine.getInstance().getHistoryService().addEvent(mHistoryEvent);
				
				NgnApplication.acquirePowerLock();
				displayMessage(context, getString(R.string.string_get_gcm_msg_obj_detect));
				generateNotification(context, base_url_message, sensor_relay_message, unique_key, device_number, displayName, event_occurrence_time, text_summary, SENSOR_RELAY_TYPE);
				NgnApplication.releasePowerLock();
			}
		}
    }
    
    private class MotionDetectImageDownloader extends AsyncTask<Void, Void, Bitmap>{
    	private String mBaseUrl;
    	private String mEventID;
    	private String device_number;
    	private String displayName;
    	private long occurrence_time;
    	private String text_summary;
    	private String file_name;
    	
    	HttpResponse response = null;
    	HttpPost httpPost = null;
    	
    	public MotionDetectImageDownloader(String baseUrl, String eventID,
    			String device_number, String displayName, long occurrence_time, String text_summary){
    		this.mBaseUrl = baseUrl;
    		this.mEventID = eventID;
    		this.device_number = device_number;
    		this.displayName = displayName;
    		this.occurrence_time = occurrence_time;
    		this.text_summary = text_summary;
    	}
    	
		@Override
		protected Bitmap doInBackground(Void... params) {
			String image_url = null;
			Bitmap img = null;
			URL myFileurl = null;
			
			StringBuilder sImageUrl = new StringBuilder();
			sImageUrl.append(mBaseUrl);
			sImageUrl.append("motion/fetch_image");

			Log.d(TAG, sImageUrl.toString());
			try {
				httpPost = new HttpPost(sImageUrl.toString());
				JSONObject message = new JSONObject();
				message.put("event_id", mEventID);
				MultipartEntity entity = new MultipartEntity();
				entity.addPart("event_id", new StringBody(message.toString()));
				httpPost.setEntity(entity);

				DefaultHttpClient httpClient = new DefaultHttpClient();
				response = httpClient.execute(httpPost);
			}
			catch (ClientProtocolException e2) {
				e2.printStackTrace();
			} 
			catch (IOException io) {
				io.printStackTrace();
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			
			try {
				if (response != null) {
					if (response.getEntity() != null && response.getStatusLine().getStatusCode() == 200) {
						String resultUrl = EntityUtils.toString(response.getEntity());
						JSONObject json = new JSONObject(resultUrl);
						if (checkDataOnJson(json, "image_url")) {
							image_url = json.getString("image_url");
						}
						if (checkDataOnJson(json, "img_file_name")){
							file_name = json.getString("img_file_name");
						}
					}
				}
			}
			catch (org.apache.http.ParseException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			
			Log.d(TAG, "Image URL " + image_url);
			if(image_url != null) {
				try {
					myFileurl = new URL(image_url);
				}
				catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
			
			try {
				if(myFileurl != null) {
					HttpURLConnection conn = (HttpURLConnection) myFileurl.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					Log.d(TAG, "Connected... Downloading Image........");
					img = BitmapFactory.decodeStream(is);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			return img;
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			String imagePathStr = "motion";
			if (bitmap != null) {
				try {
					if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
						File parentDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Event History Image");
						if(!parentDirectory.exists()) {
							parentDirectory.mkdirs();
						}
						
						try {
							File imagePath = new File(parentDirectory, file_name);
							imagePathStr = imagePath.getAbsolutePath();
							FileOutputStream fos;
							fos = new FileOutputStream(imagePath);
							if (bitmap.compress(CompressFormat.PNG, 100, fos)){
								Log.d(TAG, "Motion Detect Image Saved");
							}
							fos.flush();
							fos.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (NullPointerException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							bitmap.recycle();
						}
					
						File targetMhLocation = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Event History Image/event_history_icon.png");
						targetMhLocation.setLastModified(System.currentTimeMillis() + 1000);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			final HistoryGcmNotificationEvent mHistoryEvent = new HistoryGcmNotificationEvent();
			mHistoryEvent.setImagePath(imagePathStr);
			mHistoryEvent.setNotificationType(NotificationType.MotionDetect);
			mHistoryEvent.setTextSummary(text_summary);
			mHistoryEvent.setDisplayName(displayName);
			mHistoryEvent.setRemoteParty(device_number);
			mHistoryEvent.setStartTime(occurrence_time);
			mHistoryEvent.setEndTime(new Date().getTime());
			NgnEngine.getInstance().getHistoryService().addEvent(mHistoryEvent);
		}
    }
    
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        //displayMessage(context, message);
        // notifies user
        //generateNotification(context, message, OTHER);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String base_url, String event_id, 
    		String unique_key, String device_number, String displayName, String event_occurrence_time, String text_summary, int type) {
		
        int icon = R.drawable.ic_launcher_doorcontrol;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        Notification notification = null;
        
        String tickerText = "";
        if(type == SENSOR_RELAY_TYPE) {
        	tickerText =  context.getString(R.string.string_get_gcm_msg_door_status_change);
        } else if (type == MOTION_DETECT_TYPE) {
        	tickerText =  context.getString(R.string.string_get_gcm_msg_obj_detect);
        }
        
        notification = new Notification(icon, tickerText, when);
        
        String title = context.getString(R.string.app_name);
        
        Intent notificationIntent = new Intent(context, GCMMessageActivity.class);
        
        Log.i("SHCHO generateNotification()", "event_id: " + event_id);
        
        Bundle bundle = new Bundle();
        
        bundle.putString("base_url", base_url);
        bundle.putString("event_id", event_id);
        bundle.putString("unique_key", unique_key);
        bundle.putString("device_number", device_number);
        bundle.putString("displayName", displayName);
        bundle.putString("event_occurrence_time", event_occurrence_time);
        bundle.putString("text_summary", text_summary);
        bundle.putInt("type", type);
        
        notificationIntent.putExtras(bundle);
        
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        PendingIntent intent =
                PendingIntent.getActivity(context, type, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.setLatestEventInfo(context, title, tickerText, intent);
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound+
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(type, notification);      

    }
    
  //--------------ADDED TO AVOID JSONEXCEPTION IF DATA IS NOT EXISTED-----------------------
  	private boolean checkDataOnJson(JSONObject json, String key){
  		try {
  			json.getString(key);
  		} catch (JSONException e) {
  			return false;
  		}
  		return true;
  	}
  	
  	public static void register(final Context context, String name, final String regId) {
        //Log.i(TAG, "registering device (regId = " + regId + ")");
    	Log.i("SHCHO GCM", "registering device (regId = " + regId + ")");
    	Log.i("SHCHO GCM", "registering device (name = " + name + ")");
    	GCMRegistrar.setRegisteredOnServer(context, true);
    	Log.i("SHCHO GCM", "Global.getInstance().setRegisterId()");
    	Log.i("SHCHO GCM", "regId : "+regId);
    	GCMRegisteredId = regId;
//    	Global.getInstance().setRegisterId(regId);
//        String serverUrl = SERVER_URL;
    	// 2014.07.18 SmartBean_SHCHO : SET SERVER URL
    	//String url = NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.EVENT_SERVER_URL, eventServerUrl);
    	//int port = NgnEngine.getInstance().getConfigurationService().getInt(NgnConfigurationEntry.EVENT_SERVER_PORT, eventServerPort);
    	
    	/*String ServerURL = "";
    	if(url==null) {
    		ServerURL = "ntekcam2.iptime.org";
    	}
    	else {
    		ServerURL = url;
    	}
    	
    	if(port>-1) {
    		ServerURL = ServerURL+":"+String.valueOf(port);
    	}
    	
    	if(ServerURL!=null && ServerURL.length()>0) {
        	if(ServerURL.length()>8 && !ServerURL.substring(0, 9).equals("IDENTITY_")) {
        		ServerURL = "http://"+ServerURL+"/gcm/register.php";
        	}
        	else {
        		ServerURL = DEFAULT_SERVER_URL;
        	}
        }
        else {
        	ServerURL = DEFAULT_SERVER_URL;
        }*/
    	
        /*Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("name", name);
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            try {
                displayMessage(context, context.getString(
                        R.string.server_registering, i, MAX_ATTEMPTS));
            	if(ServerURL!=null&&ServerURL.length()>0){
            		//post(ServerURL, params);
                    GCMRegistrar.setRegisteredOnServer(context, true);
            	}
            	GCMRegistrar.setRegisteredOnServer(context, true);
                //String message = context.getString(R.string.server_registered);
                //CommonUtilities.displayMessage(context, message);
                return;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
                // increase backoff exponentially
                backoff *= 2;
            } finally {
            	Log.i("SHCHO GCM", "Global.getInstance().setRegisterId()");
            	Log.i("SHCHO GCM", "regId : "+regId);
            	Global.getInstance().setRegisterId(regId);
            }
        }
        String message = context.getString(R.string.server_register_error, MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);*/
    }
  	
  	public static void unregister(final Context context, final String regId) {
        Log.i(TAG, "unregistering device (regId = " + regId + ")");
        /*String url = NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.EVENT_SERVER_URL, eventServerUrl);
    	int port = NgnEngine.getInstance().getConfigurationService().getInt(NgnConfigurationEntry.EVENT_SERVER_PORT, eventServerPort);
    	
    	String ServerURL = "";
    	if(url==null) {
    		ServerURL = "ntekcam2.iptime.org";
    	}
    	else {
    		ServerURL = url;
    	}
    	
    	if(port>-1) {
    		ServerURL = ServerURL+":"+String.valueOf(port);
    	}
    	
    	if(ServerURL!=null && ServerURL.length()>0) {
        	if(!(ServerURL.length()>8 && !ServerURL.substring(0, 9).equals("IDENTITY_"))) {
        		ServerURL = DEFAULT_SERVER_URL;
        	}
        }
        else {
        	ServerURL = DEFAULT_SERVER_URL;
        }
        String serverUrl = ServerURL + "/unregister";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
        	
            //post(serverUrl, params);
            GCMRegistrar.setRegisteredOnServer(context, false);
            //String message = context.getString(R.string.server_unregistered);
            //CommonUtilities.displayMessage(context, message);
        } catch (IOException e) {
            // At this point the device is unregistered from GCM, but still
            // registered in the server.
            // We could try to unregister again, but it is not necessary:
            // if the server tries to send a message to the device, it will get
            // a "NotRegistered" error message and should unregister the device.
            String message = context.getString(R.string.server_unregister_error,
                    e.getMessage());
            CommonUtilities.displayMessage(context, message);
        }*/
        GCMRegistrar.setRegisteredOnServer(context, false);
    }
  	
  	public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
