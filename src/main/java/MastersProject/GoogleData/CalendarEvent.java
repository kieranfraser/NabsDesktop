package MastersProject.GoogleData;

import java.io.Serializable;
import java.util.Date;

public class CalendarEvent implements Serializable{
	
	private static final long serialVersionUID = 4891968553707444654L;
	
	private String description;
	private Date startDate;
	private String location;
	private Date date;
	
	public CalendarEvent(String description, Date startDate, String location){
		this.description = description;
		this.startDate = startDate;
		this.location = location;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
}
