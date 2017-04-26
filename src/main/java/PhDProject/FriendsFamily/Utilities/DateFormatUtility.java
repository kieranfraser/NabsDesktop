package PhDProject.FriendsFamily.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class DateFormatUtility {

	public static String convertDateToStringUTC(Date date){
		String result = null;
		
		DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		result = formatter.format(date);
		
		return result;
	}
	
	public static LocalDateTime convertStringToLocalDateTime(String string){
		DateTimeFormatter formatter;
		LocalDateTime dateTime;
		try{
			formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss"); 
			dateTime = LocalDateTime.parse(string, formatter);
		} catch(Exception e){
			formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); 
			dateTime = LocalDateTime.parse(string, formatter);
		}
		return dateTime;

	}
	
	public static Date convertStringToDate(String date){
		Date result = null;			

		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			result = sdf.parse(date);
		} catch (Exception e){
			System.out.println("error converting string to date");
		}
		
		return result;
	}
	
	public static String convertDateToStringYearMonthDay(Date date){
		String result = null;
		
		DateFormat formatter = new SimpleDateFormat("yy-MM-dd");
		
		result = formatter.format(date);
		
		
		return "20"+result;
	}
	
	public static Date convertLDTToCalendarEventDate(LocalDateTime localDateTime) throws ParseException{
		Date out = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
		return out;
	}
	
	public static LocalDateTime convertDateToLDT(Date date){
		LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		return ldt;
	}
	
	/**
	 * Convert string to LocalDate
	 * @param date
	 * @return
	 */
	public static LocalDate stringToLocalDate(String date){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.parse(date, formatter);
	}
	
	/**
	 * Gets the number of days between two dates
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int numberOfDaysBetweenDates(LocalDate date1, LocalDate date2){
		long days = ChronoUnit.DAYS.between(date1, date2);
		return (int) days;
	}
	
	public static Date stringToUpliftedNotificationDate(String date) throws ParseException{
		Date newDate = new Date();

		DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		newDate = (Date) formatter.parse(date);   
		
		return newDate;
	}
	
	public static boolean checkBeforeHour(LocalDateTime notifDate, LocalDateTime eventDate){
		long seconds = notifDate.until(eventDate, ChronoUnit.SECONDS);
		if(seconds < 0)
			return false;
		else
			return true;
	}
}
