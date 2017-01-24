package com.ntek.wallpad.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.model.NgnHistoryAVCallEvent;
import org.doubango.ngn.model.NgnHistoryEvent;
import org.doubango.ngn.model.NgnHistoryEvent.StatusType;
import org.doubango.ngn.services.INgnHistoryService;
import org.doubango.ngn.utils.NgnUriUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterListener;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ntek.wallpad.R;
import com.ntek.wallpad.Model.HistoryGcmNotificationEvent;
import com.ntek.wallpad.Model.HistoryGcmNotificationEvent.NotificationType;

public class WallPadHistoryAdapter  extends BaseAdapter implements Observer, Filterable {
	
	private static final int AV_DOORTALK_HISTORY = 0;
	private static final int AV_CLIENT_HISTORY = 1;
	private static final int DOOR_CONTOL_LOCK_HISTORY = 2;
	private static final int DOOR_CONTOL_UNLOCK_HISTORY = 3;
	private static final int MOTION_HISTORY = 4;
	
	public static enum FilterType{
		None,
		AllAV,
		OutgoingAV,
		IncomingAV,
		MissedAndFailedAV,
		DoorControl,
		MotionDetect
	}

	private static final String TAG = WallPadHistoryAdapter.class.getCanonicalName();
	
	private static WallPadHistoryAdapter instance = null;
	
	private List<NgnHistoryEvent> originalData;
    private List<NgnHistoryEvent> filteredData;
	private final Handler mHandler;
	private final INgnHistoryService mHistorytService;
	private Context context;
	private EventFilter mFilter = new EventFilter();
	private String constraint = FilterType.None.toString();
	private int selectedItem = -1;
	private ContactManager contactManager;
	private LayoutInflater mInflater;
	private LruCache<String, Bitmap> mMemoryCache;
	private BroadcastReceiver br;
	private Map<String, String> contactCache;
	private Typeface font; 
	
	public static WallPadHistoryAdapter getInstance(Context context) {
		if(instance == null){
			instance = new WallPadHistoryAdapter(context);
		}
		return instance;
	}
	
	private WallPadHistoryAdapter(Context context) {
		Log.d(TAG, "WallPadHistoryAdapter(context)");
		mHandler = new Handler();
		originalData = NgnEngine.getInstance().getHistoryService().getObservableEvents().getList();
		filteredData = originalData;
		this.context = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHistorytService = NgnEngine.getInstance().getHistoryService();
		mHistorytService.getObservableEvents().addObserver(this);
		contactManager = ContactManager.getInstance(context);
		contactCache = new HashMap<String, String>();
		
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
 
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than number of items.
				return bitmap.getByteCount();
			}
		};
		
		br = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				contactCache.clear();
				getFilter().filter(constraint);
			}
		};
		font = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSansRegular.ttf");
		this.context.registerReceiver(br, new IntentFilter("com.ntek.wallpad.CONTACT_UPDATE"));
	}

	@Override
	protected void finalize() throws Throwable {
		mHistorytService.getObservableEvents().deleteObserver(this);
		this.context.unregisterReceiver(br);
		super.finalize();
	}

	@Override
	public int getCount() {
		return this.filteredData.size();
	}

	@Override
	public NgnHistoryEvent getItem(int position) {
		return this.filteredData.get(position);
	}

	public List<NgnHistoryEvent> getList() {
		return this.filteredData;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setSelectedItem(int selectedItem){
		this.selectedItem = selectedItem;
		notifyDataSetChanged();
	}
	
	private void highlightItem(int position, View view){
		if (position == selectedItem) {
			view.setBackgroundColor(Color.parseColor("#66009587"));
		}
		else {
			view.setBackground(context.getResources().getDrawable(R.color.color_white));
		}
	}
	@Override
	public void update(Observable observable, Object data) {
		if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
			originalData = NgnEngine.getInstance().getHistoryService().getObservableEvents().getList();
			getFilter().filter(constraint, new FilterListener() {
				@Override
				public void onFilterComplete(int count) {
					context.sendBroadcast(new Intent("com.ntek.wallpad.HISTORY_UPDATE").putExtra("count", count));
				}
			});
		}
		else {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					originalData = NgnEngine.getInstance().getHistoryService().getObservableEvents().getList();
					getFilter().filter(constraint, new FilterListener() {
						@Override
						public void onFilterComplete(int count) {
							context.sendBroadcast(new Intent("com.ntek.wallpad.HISTORY_UPDATE").putExtra("count", count));
						}
					});
				}
			});
		}
	}
	
	private static class ViewHolder 
	{
		public ImageView historyImageImageView;
		public TextView displayNameTextView;
		public TextView eventOccurrenceTextView;
		public TextView deviceNumberTextView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder viewHolder = null;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.child_phone_history_list, null);
			viewHolder = new ViewHolder();
			
			viewHolder.displayNameTextView = (TextView) convertView.findViewById(R.id.device_display_name_text_view);
			viewHolder.eventOccurrenceTextView = (TextView) convertView.findViewById(R.id.event_occurrence_time_text_view);
			viewHolder.deviceNumberTextView = (TextView) convertView.findViewById(R.id.device_number_text_view);
			viewHolder.historyImageImageView = (ImageView) convertView.findViewById(R.id.history_image_image_view);
			
			viewHolder.displayNameTextView.setTypeface(font);
			viewHolder.eventOccurrenceTextView.setTypeface(font);
			viewHolder.deviceNumberTextView.setTypeface(font);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(getList() != null && getCount() > 0 ) {
			final NgnHistoryEvent event = getItem(position);
			highlightItem(position, convertView);
			
			String remoteParty = NgnUriUtils.getDisplayName(event.getRemoteParty());
			String displayName = event.getDisplayName();
			String date = DateTimeUtils.getFriendlyDateString(new Date(event.getStartTime()));
			String imagePath = "client";
			int type = 1;
			
			if(contactCache.containsKey(remoteParty)) {
				type = Integer.parseInt(contactCache.get(remoteParty).split("\\|")[0]);
				displayName = contactCache.get(remoteParty).split("\\|")[1];
				imagePath = contactCache.get(remoteParty).split("\\|")[2];
			} else {
				int phoneNumber = 0;
				try {
					phoneNumber = Integer.parseInt(remoteParty);
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
				}
				
				Contacts contacts = contactManager.getContactByNumber(phoneNumber);
				displayName = contacts == null? displayName : contacts.getDisplayName();
				imagePath = contacts == null ? imagePath : contacts.getPhoto().getFilename();
				type = contacts == null ? type : contacts.getType();
				contactCache.put(remoteParty, type + "|" + displayName + "|" + imagePath);
			}
			
			if(event.getMediaType() == NgnMediaType.None) {
				if(event instanceof HistoryGcmNotificationEvent) {
					final HistoryGcmNotificationEvent gcmEvent = (HistoryGcmNotificationEvent) event;
					imagePath = gcmEvent.getImagePath() == null ? imagePath : gcmEvent.getImagePath();
					if(gcmEvent.getNotificationType() == NotificationType.MotionDetect ) {
						type = MOTION_HISTORY;
					} else {
						type = imagePath.equals("Unlocked") ? DOOR_CONTOL_UNLOCK_HISTORY : DOOR_CONTOL_LOCK_HISTORY;
					}
				}
			}
			
			viewHolder.eventOccurrenceTextView.setText(date);
			viewHolder.deviceNumberTextView.setText(remoteParty);
			viewHolder.displayNameTextView.setText(displayName);
			loadHistoryPhotoBitmap(imagePath, viewHolder.historyImageImageView, type);
		}
		
		return convertView;
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}
	
	private class EventFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
        	WallPadHistoryAdapter.this.constraint = String.valueOf(constraint);
        	FilterType type = FilterType.valueOf(constraint.toString());
            FilterResults results = new FilterResults();

            final List<NgnHistoryEvent> list = originalData;

            int count = list.size();
            final ArrayList<NgnHistoryEvent> nlist = new ArrayList<NgnHistoryEvent>(count);

            switch (type) {
            case AllAV:
            case IncomingAV:
            case OutgoingAV:
            case MissedAndFailedAV:
            	for (NgnHistoryEvent filterableEvent : list) {
            		if(filterableEvent instanceof NgnHistoryAVCallEvent) {
            			NgnHistoryAVCallEvent event = (NgnHistoryAVCallEvent) filterableEvent;
            			if(type == FilterType.AllAV && (event.getStatus() == StatusType.Incoming || event.getStatus() == StatusType.Outgoing || event.getStatus() == StatusType.Missed || event.getStatus() == StatusType.Failed)) {
            				nlist.add(filterableEvent);
            			}
            			else if(type == FilterType.IncomingAV && event.getStatus() == StatusType.Incoming) {
            				nlist.add(filterableEvent);
            			}
            			else if(type == FilterType.OutgoingAV && event.getStatus() == StatusType.Outgoing) {
            				nlist.add(filterableEvent);
            			}
            			else if(type == FilterType.MissedAndFailedAV && (event.getStatus() == StatusType.Missed || event.getStatus() == StatusType.Failed)) {
            				nlist.add(filterableEvent);
            			}
            		}
            	}
            	break;
            case DoorControl:
            case MotionDetect:
            	for (NgnHistoryEvent filterableEvent : list) {
            		if(filterableEvent instanceof HistoryGcmNotificationEvent) {
            			HistoryGcmNotificationEvent event = (HistoryGcmNotificationEvent) filterableEvent;
            			if(type == FilterType.DoorControl && event.getNotificationType() == NotificationType.DoorControl) {
            				nlist.add(filterableEvent);
            			}
            			else if(type == FilterType.MotionDetect && event.getNotificationType() == NotificationType.MotionDetect) {
            				nlist.add(filterableEvent);
            			}
            		}
            	}
            	break;
            case None:
            default:
            	nlist.addAll(list);
            	break;
            }


            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<NgnHistoryEvent>) results.values;
            Log.d(TAG,"publishResults");
            notifyDataSetChanged();
        }
    }
	
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		synchronized (mMemoryCache) {
			if (getBitmapFromMemCache(key) == null) {
				mMemoryCache.put(key, bitmap);
			}
		}
	}

	public void loadHistoryPhotoBitmap(String imagePath, ImageView imageView, int type) {
		final Bitmap bitmap = getBitmapFromMemCache(imagePath);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView, type);
			task.execute(imagePath);
		}
	}
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		private String dataPath = "";
		private int type = 1;
		private final WeakReference<ImageView> imageViewReference;
		
		public BitmapWorkerTask(ImageView imageView, int type) {
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
			this.type = type;
		}
		
		public int getDefaultImage(int type) {
			switch (type) {
			default: 
			case AV_CLIENT_HISTORY: return R.drawable.ic_thumbnail_xlarge;
			case AV_DOORTALK_HISTORY: return R.drawable.ic_doortalk_thumbnail;
			case DOOR_CONTOL_UNLOCK_HISTORY: return R.drawable.unlock_normal;
			case DOOR_CONTOL_LOCK_HISTORY: return R.drawable.unlock_normal_02;
			case MOTION_HISTORY: return R.drawable.event_history_ic;
			}
		}
		
		// Decode image in background or get it from memory cache. for either client or DoorTalk
		@Override
		protected Bitmap doInBackground(String... params) {
			dataPath = params[0];
			final Bitmap bitmap;
			
			File file = new File(dataPath);
			if(file.exists()) {
				bitmap = BitmapDecoder.decodeSampledBitmapFromFile(dataPath, 100, 100);
			} else {
				bitmap = BitmapDecoder.decodeSampledBitmapFromResource(
						context.getResources(), getDefaultImage(type), 100, 100);
			}
			addBitmapToMemoryCache(dataPath, bitmap);
			
			return bitmap;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null) {
				final ImageView imageView = imageViewReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}
}
