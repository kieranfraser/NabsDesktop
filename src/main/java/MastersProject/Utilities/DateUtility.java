package MastersProject.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.google.api.services.calendar.model.EventDateTime;

public class DateUtility {

	public String dateToString(Date date){
    	SimpleDateFormat sdf = new SimpleDateFormat();
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	sdf.applyPattern("dd/MM/yyyy HH:mm:ss");
    	return sdf.format(date);
	}
	
	public static String cleanMinutes(double mins){
		String time = null;
		String unit = null;
		int i = 0;
		while(mins>60.0){
			mins = mins/60.0;
			i++;
		}
		time = String.valueOf(mins);
		switch(i){
		case 0:
			unit = "mins";
			break;
		case 1:
			unit = "hours";
			break;
		case 2:
			unit = "days";
			break;
		}
		return time+" "+unit;
	}
	
	/**
	 * Convert java date to localdate (used for javafx date picker)
	 * @param date
	 * @return
	 */
	public static LocalDate dateToLocalDate(Date date){
		Instant instant = date.toInstant();
		ZonedDateTime zdt = instant.atZone(ZoneId.of("Z"));
		return zdt.toLocalDate();
	}
	
	/**
	 * Convert from localdate to java Date
	 * @param date
	 * @return
	 */
	public static Date localDateToDate(LocalDate date){
		return Date.from(date.atStartOfDay(ZoneId.of("Z")).toInstant());
	}
	
	public static LocalDateTime dateToLocalDateTime(Date date){
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("Z"));
		
	}
	
	/**
	 * Create a java date given date and time (as string)
	 * Used for taking form input and transforming to notification
	 * value.
	 * @param date
	 * @param time
	 * @return
	 */
	public static Date createDateAndTime(Date date, String time){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		String[] timeParts = time.split(":");
		String hours = timeParts[0]; 
		String minutes = timeParts[1]; 
		String seconds = timeParts[2];
		
		cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hours));
		cal.set(Calendar.MINUTE, Integer.valueOf(minutes));
		cal.set(Calendar.SECOND, Integer.valueOf(seconds));

		return cal.getTime();
	}
	
	/**
	 * Get the time of a java.Date in a string of format HH:mm:ss 
	 * (Used for the time input field)
	 * @param date
	 * @return
	 */
	public static String getTimeAsString(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
	    return new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());
	}

	public static Date convertEventDateTimeToDate(EventDateTime inputDate) throws ParseException{
		EventDateTime eventDateTime = inputDate;
    	String date = eventDateTime.getDateTime().toString();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    	return sdf.parse(date);
    	//sdf.applyPattern("dd/MM/yyyy HH:mm:ss");
	}
	
	public static int getDifferenceBetweenDatesInHours(Date notificationDate, Date eventDate){
	    long timeDiff = eventDate.getTime() - notificationDate.getTime();
	    return (int) TimeUnit.MILLISECONDS.toHours(timeDiff);
	}
	
	/**
	 * Get the differences between two dates (down to the exact minute/second)
	 * @param notificationDate
	 * @param eventDate
	 * @return
	 */
	public static long getDifferenceBetweenDatesInMinutes(LocalDateTime notificationDate, LocalDateTime eventDate){
			    
	    int diffInNano = java.time.Duration.between(notificationDate, eventDate).getNano();
	    long diffInSeconds = java.time.Duration.between(notificationDate, eventDate).getSeconds();
	    long diffInMilli = java.time.Duration.between(notificationDate, eventDate).toMillis();
	    long diffInMinutes = java.time.Duration.between(notificationDate, eventDate).toMinutes();
	    long diffInHours = java.time.Duration.between(notificationDate, eventDate).toHours();
	    
	    return diffInMinutes;
	}
	
}
