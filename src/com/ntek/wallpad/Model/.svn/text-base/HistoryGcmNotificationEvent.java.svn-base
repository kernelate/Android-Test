package com.ntek.wallpad.Model;

import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.model.NgnHistoryEvent;
import org.doubango.ngn.utils.NgnPredicate;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class HistoryGcmNotificationEvent extends NgnHistoryEvent {
	
	public enum NotificationType{
		DoorControl,
		MotionDetect
	}
	
	@Element(data=true, name = "imagePath", required=false)
	protected String mImagePath;
	@Element(data=true, name = "textSummary", required=false)
	protected String mTextSummary;
	@Element(data=true, name = "notificationType", required=false)
	protected NotificationType mNotificationType;
	
	public HistoryGcmNotificationEvent() {
		super(NgnMediaType.None, null);
	}
	
	public HistoryGcmNotificationEvent(String remoteParty, NotificationType notificationType) {
		super(NgnMediaType.None, remoteParty);
		super.setStatus(StatusType.Incoming);
		this.mNotificationType = notificationType;
	}

	public void setTextSummary(String textSummary){
		this.mTextSummary = textSummary;
	}
	
	public String getTextSummary(){
		return this.mTextSummary;
	}

	public void setImagePath(String imagePath){
		this.mImagePath = imagePath;
	}
	
	public String getImagePath(){
		return this.mImagePath;
	}
	
	public void setNotificationType(NotificationType notificationType){
		this.mNotificationType = notificationType;
	}
	
	public NotificationType getNotificationType(){
		return this.mNotificationType;
	}
	
	/**
	 * HistoryGcmNotificationEventFilter
	 */
	public static class HistoryEventGcmNotificationFilter implements NgnPredicate<NgnHistoryEvent>{
		protected NotificationType type;
		
		public HistoryEventGcmNotificationFilter(NotificationType type) {
			this.type = type;
		}
		
		@Override
		public boolean apply(NgnHistoryEvent event) {
			return (event != null && event.getMediaType() == NgnMediaType.None && ((HistoryGcmNotificationEvent) event).getNotificationType() == type);
		}
	}
	
}
