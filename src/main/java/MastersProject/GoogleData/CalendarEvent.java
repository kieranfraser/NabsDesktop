package MastersProject.GoogleData;

import java.io.Serializable;
import java.util.Date;

public class CalendarEvent implements Serializable{
	
	private static final long serialVersionUID = 4891968553707444654L;
	
	private String description;
	private Date startDate;
	private Date endDate;
	private String location;
	private String summary;
	
	public CalendarEvent(){};
	
	public CalendarEvent(String description, Date startDate, Date endDate, String location, String summary){
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.location = location;
		this.summary = summary;
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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}	
	
}
