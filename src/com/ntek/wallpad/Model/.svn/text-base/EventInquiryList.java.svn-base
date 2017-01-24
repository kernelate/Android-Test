package com.ntek.wallpad.Model;

import java.util.ArrayList;
import java.util.List;

import org.doubango.ngn.utils.NgnObservableList;

public class EventInquiryList extends NgnObservableList<EventInquiryModel> {
	public EventInquiryList()
	{
		super(true);
	}
	
   public List<EventInquiryModel> getApproved()
   {
	   final List<EventInquiryModel> eventList = new ArrayList<EventInquiryModel>();
	   for (EventInquiryModel eventmodel : getList())
	   {
		   if (eventmodel != null)
		   {
			   if (eventmodel.getActiveStatus().equals("active"))
			   {
				   eventList.add(eventmodel);
			   }
		   }
	   }
  
	   return eventList;
   }
   
   public List<EventInquiryModel> getPending(){
	   final List<EventInquiryModel> eventList = new ArrayList<EventInquiryModel>();
	   for (EventInquiryModel eventmodel : getList())
	   {
		   if (eventmodel != null)
		   {
			   if (eventmodel.getActiveStatus().equals("inactive"))
			   {
				   eventList.add(eventmodel);
			   }
		   }
	   }
	   return eventList;
   }
   
   public int getSize(){
	   return getList().size();
   }
   
   public EventInquiryModel get(Object model)
   {
	   if (model instanceof EventInquiryModel)
	   {
		   final EventInquiryModel modelDecoy = (EventInquiryModel) model;
		   
		   for (EventInquiryModel data : getList()) 
		   {
			   if (data.equals(modelDecoy))
			   {
				   return data;
			   }
		   }
	   }
	   return null;
   }
   
   public void changeStatus(String gcmID, String status){
	   for (EventInquiryModel model : getList()) 
	   {
		 if (model != null)
		 {
			 if (model.getGcmID().contentEquals(gcmID))
			 {
				 model.setActiveStatus(status);
			 }
		 }
	   }
   }
   
   public void deleteEventInquiry(String gcmID)
   {
	   final List<EventInquiryModel> handlerList = new ArrayList<EventInquiryModel>(getList());
	   for (EventInquiryModel model : handlerList)
	   {
		   if (model.getGcmID().equals(gcmID))
		   {
			   getList().remove(model);
		   }
	   }
   }
   
   public void deleteEventInquiry(EventInquiryModel data)
   {
	   final List<EventInquiryModel> handlerList = new ArrayList<EventInquiryModel>(getList());
	   for (EventInquiryModel model : handlerList)
	   {
		   if (model.equals(data))
		   {
			   getList().remove(model);
		   }
	   }
   }
}
