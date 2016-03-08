package MastersProject.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
}
