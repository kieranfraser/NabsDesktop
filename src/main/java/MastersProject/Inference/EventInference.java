package MastersProject.Inference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import MastersProject.GoogleData.CalendarEvent;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Utilities.DateUtility;

public class EventInference {
	
	public static double getEventImportanceValue(String attribute, ArrayList<CalendarEvent> events,
			UpliftedNotification notification){
		
		ArrayList<CalendarEvent> unfinishedEvents = new ArrayList<>();
		
		LocalDateTime notificationDate = DateUtility.dateToLocalDateTime(notification.getDate());
		long maxStartTimeDiff = 4320;
		
		for(CalendarEvent event : events){
			if(getAllUnfinishedEvents(event, notification)){
				unfinishedEvents.add(event);
				LocalDateTime eventStartDate = DateUtility.dateToLocalDateTime(event.getStartDate());
				long eventStartTimeDiff = DateUtility.getDifferenceBetweenDatesInMinutes(notificationDate, eventStartDate);
				/*if(eventStartTimeDiff >= maxStartTimeDiff ){  			can be put back in for calendars that don't have sparse events 
					maxStartTimeDiff = eventStartTimeDiff;					spread out over a number of days (scews the context relevance of event)
				}*/
			}
		}
		
		ArrayList<Double> rankingValue = new ArrayList<Double>();
		rankingValue.add(0.0);
		for(CalendarEvent event : unfinishedEvents){
			
			if(hasContextMatch(event.getDescription(), notification.getSubject(), attribute)){
				
				LocalDateTime eventStartDate = DateUtility.dateToLocalDateTime(event.getStartDate());
				long eventStartTimeDiff = DateUtility.getDifferenceBetweenDatesInMinutes(notificationDate, eventStartDate);
				System.out.println(event.getSummary());
				rankingValue.add(applyRating(event, maxStartTimeDiff, eventStartTimeDiff));
			}
		}
		return Collections.max(rankingValue);
	}
	
	private static boolean getAllUnfinishedEvents(CalendarEvent event, UpliftedNotification notification){
				
		LocalDateTime notificationDate = DateUtility.dateToLocalDateTime(notification.getDate());
		LocalDateTime eventEndDate = DateUtility.dateToLocalDateTime(event.getEndDate());
		
		long endDiff = DateUtility.getDifferenceBetweenDatesInMinutes(notificationDate, eventEndDate);
		
		if(endDiff >= 0){ 
			return true;
		}
		else {
			return false;
		}
	}
		
	private static boolean hasContextMatch(String calendarDescr, String notification, String attribute){
		if(calendarDescr.contains(notification)){
			return true;
		}
		else return false;
	}

	private static double applyRating(CalendarEvent event, 
			long maxTimeDiff, long incomingTimeDiff){
		if(incomingTimeDiff < 0 ){ // ensure that the ongoing event is calculated correctly
			incomingTimeDiff = 0;
		}
		double result = (double) (maxTimeDiff - incomingTimeDiff)/maxTimeDiff;
		System.out.println("**Result**"+result);
		return result;
	}
	
	/**
	 * Get the current events location and summary if it exists
	 * @param event
	 * @param notification
	 * @return StringArray with the summary at index 0 and location at index 1
	 */
	public static ArrayList<String> getCurrentLocationAndEventName(CalendarEvent event, UpliftedNotification notification){
		ArrayList<String> result = new ArrayList<String>();
		
		LocalDateTime notificationDate = DateUtility.dateToLocalDateTime(notification.getDate());
		long maxStartTimeDiff = 4320;
		
		if(getAllUnfinishedEvents(event, notification)){
			LocalDateTime eventStartDate = DateUtility.dateToLocalDateTime(event.getStartDate());
			long eventStartTimeDiff = DateUtility.getDifferenceBetweenDatesInMinutes(notificationDate, eventStartDate);
			System.out.println(event.getSummary());
			if(applyRating(event, maxStartTimeDiff, eventStartTimeDiff) == 1){
				String location =  event.getLocation();
				String summary = event.getSummary();
				result.add(summary);
				if(location == null){
					result.add("unkown");
				}else{
					result.add(location);
				}
			}
			else{
				result.add("none");
				result.add("unkown");
			}
		}
		else{
			result.add("none");
			result.add("unkown");
		}
		return result;
	}
}
