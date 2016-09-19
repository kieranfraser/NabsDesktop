package PhDProject.FriendsFamily.Models;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import MastersProject.GoogleData.CalendarEvent;
import PhDProject.FriendsFamily.Utilities.DateFormatUtility;

@Entity
public class Event implements Serializable{
		
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	public final static int FIXED = -1;
	public final static int FORWARD = 0;
	public final static int ENJOYED = 1;
	public final static int CINEMA = 2;
	public final static int MOVIE = 3;
	public final static int TV = 4;
	public final static int RESTAURANT = 5;
	
	/**
	 * Event with possibilities
	 * friends
	 * partner
	 * children
	 * strangers
	 * guests
	 * coworkers
	 * colleagues
	 * alone
	 */
	public final static String FRIENDS = "friends";
	public final static String PARTNER = "partner";
	public final static String CHILDREN = "children";
	public final static String STRANGERS = "strangers";
	public final static String GUESTS = "guests";
	public final static String COWORKERS = "coworkers";
	public final static String COLLEAGUES = "colleagues";
	public final static String ALONE = "alone";	
	
	private String nameGT;
	private String withGT; // based on who with - determine type i.e. social, work etc.
	private String dayGT;
	
	private String startDate;
	private String endDate;
	private int eventType;
	
	private LocalDateTime inferredStartDate;
	private LocalDateTime inferredEndDate;
	private String inferredDescription;
	
	public LocalDateTime getInferredStartDate() {
		return inferredStartDate;
	}

	public void setInferredStartDate(LocalDateTime inferredStartDate) {
		this.inferredStartDate = inferredStartDate;
	}

	public LocalDateTime getInferredEndDate() {
		return inferredEndDate;
	}

	public void setInferredEndDate(LocalDateTime inferredEndDate) {
		this.inferredEndDate = inferredEndDate;
	}

	public String getInferredDescription() {
		return inferredDescription;
	}

	public void setInferredDescription(String inferredDescription) {
		this.inferredDescription = inferredDescription;
	}

	public boolean checkEmpty(){
		boolean empty = false;
		if(withGT.isEmpty()){
			empty = true;
		}
		return empty;
	}
	
	public int getEventType() {
		return eventType;
	}
	public void setEventType(int eventType) {
		this.eventType = eventType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getNameGT() {
		return nameGT;
	}
	public void setNameGT(String nameGT) {
		this.nameGT = nameGT;
	}
	public String getWithGT() {
		return withGT;
	}
	public void setWithGT(String withGT) {
		this.withGT = withGT;
	}
	public String getDayGT() {
		return dayGT;
	}
	public void setDayGT(String dayGT) {
		this.dayGT = dayGT;
	}
	@Override
	public String toString() {
		return "Event\n"
				+ " Name: "+this.nameGT+"\n"
				+ " With: "+this.withGT+"\n"
				+ " Day: "+this.dayGT+"\n"
				+ " Event Type: "+this.eventType+"\n"
				+ " Start Date: "+this.startDate+"\n"
				+ " End Date: "+this.endDate+"\n"
				+ " Inferred Start Date: "+this.inferredStartDate+"\n"
				+ " Inferred End Date: "+this.inferredEndDate+"\n"
				+ " Inferred Description: "+this.inferredDescription+"\n\n******************\n";
	}	
	
	/**
	 * 1) Determine the number of days between that min and max date as diff
	 * 2) random date = the min date + new Random().netxInt(diff)
	 */
	public void calcExactEventDate(){
		LocalDate start = DateFormatUtility.stringToLocalDate(this.startDate);
		LocalDate end = DateFormatUtility.stringToLocalDate(this.endDate);
		int diff = DateFormatUtility.numberOfDaysBetweenDates(start, end);
		Random r = new Random();
		int randomDay = r.nextInt(diff+1);
		LocalDate exactDate = start.plusDays(randomDay);
		switch(this.eventType){
		case FORWARD:
			this.inferredStartDate = exactDate.atTime(17, 0, 0);
			this.inferredEndDate = exactDate.atTime(20, 0, 0);
			break;
		case ENJOYED:
			this.inferredStartDate = exactDate.atTime(17, 0, 0);
			this.inferredEndDate = exactDate.atTime(19, 0, 0);
			break;
		case CINEMA:
			this.inferredStartDate = exactDate.atTime(20, 0, 0);
			this.inferredEndDate = exactDate.atTime(21, 0, 0);
			break;
		case MOVIE:
			this.inferredStartDate = exactDate.atTime(21, 0, 0);
			this.inferredEndDate = exactDate.atTime(22, 0, 0);
			break;
		case TV:
			this.inferredStartDate = exactDate.atTime(17, 0, 0);
			this.inferredEndDate = exactDate.atTime(20, 0, 0);
			break;
		case RESTAURANT:
			this.inferredStartDate = exactDate.atTime(19, 0, 0);
			this.inferredEndDate = exactDate.atTime(21, 0, 0);
			break;
		}
	}

	/**
	 * Function to calculate the description (context) of the event with relation to the people
	 * and the subject of the notification. The value is inferred using the withGT value of the event.
	 * 
	 * can set the event subject based on personality also
	 */
	public void calcEventDescription(){
		if(this.withGT.contains(PARTNER) || this.withGT.contains(CHILDREN)){
			addToDescription(Sender.FAMILY);
		}
		if(this.withGT.contains(FRIENDS) || this.withGT.contains(GUESTS)){
			addToDescription(Sender.FRIEND);
			if(this.withGT.contains(GUESTS)){
				addToDescription(Subject.SOCIAL);
			}
		}
		if(this.withGT.contains(STRANGERS)){
			addToDescription(Sender.STRANGER);
		}
		if(this.withGT.contains(COWORKERS) || this.withGT.contains(COLLEAGUES)){
			addToDescription(Sender.COLLEAGUE);
			addToDescription(Subject.WORK);
		}
		if(this.withGT.contains(ALONE)){
			addToDescription(Subject.INTEREST);
		}
		if(this.eventType == CINEMA){
			addToDescription(Subject.INTEREST);
			addToDescription(Subject.SOCIAL);
		}
		if(this.eventType == TV){
			addToDescription(Subject.INTEREST);
			if(this.withGT.contains(FRIENDS) || this.withGT.contains(GUESTS)){
				addToDescription(Subject.SOCIAL);
			}
		}
		if(this.eventType == MOVIE){
			addToDescription(Subject.INTEREST);
			if(this.withGT.contains(FRIENDS)  || this.withGT.contains(GUESTS)){
				addToDescription(Subject.SOCIAL);
			}
		}
		if(this.eventType == RESTAURANT){
			addToDescription(Subject.SOCIAL);
		}
		if(this.eventType == ENJOYED){
			addToDescription(Subject.SOCIAL);
		}
		if(this.eventType == FORWARD){
			addToDescription(Subject.INTEREST);
		}
	}
	
	/**
	 * Function to add a string value to the event description.
	 * @param value
	 */
	private void addToDescription(String value){
		if(this.inferredDescription == null){
			this.inferredDescription = value;
		}
		else{
			this.inferredDescription = this.inferredDescription+" "+value;
		}
	}
	
	public void setFixedEventMorning(boolean student, LocalDateTime date){
		if(student){
			this.nameGT = "College morning lectures.";
			this.withGT = Sender.COLLEAGUE+" "+Sender.FRIEND+" "+Sender.STRANGER;
			this.dayGT = "M T W TH F";
			this.inferredStartDate = date.withHour(9);
			this.inferredStartDate = this.inferredStartDate.withMinute(0);
			this.inferredStartDate = this.inferredStartDate.withSecond(0);
			this.inferredEndDate = date.withHour(12);
			this.inferredEndDate = this.inferredEndDate.withMinute(0);
			this.inferredEndDate = this.inferredEndDate.withSecond(0);
			this.inferredDescription = this.withGT+" "+Subject.WORK;			
		}
		else{
			this.nameGT = "Morning work.";
			this.withGT = Sender.COLLEAGUE+" "+Sender.FRIEND+" "+Sender.STRANGER;
			this.dayGT = "M T W TH F";
			this.inferredStartDate = date.withHour(9);
			this.inferredStartDate = this.inferredStartDate.withMinute(0);
			this.inferredStartDate = this.inferredStartDate.withSecond(0);
			this.inferredEndDate = date.withHour(12);
			this.inferredEndDate = this.inferredEndDate.withMinute(0);
			this.inferredEndDate = this.inferredEndDate.withSecond(0);
			this.inferredDescription = this.withGT+" "+Subject.WORK;	
		}
	}
	
	public void setFixedLunchEvent(boolean student, LocalDateTime date){
		this.nameGT = "Lunch";
		this.withGT = Sender.COLLEAGUE+" "+Sender.FRIEND+" "+Sender.FAMILY;
		this.dayGT = "M T W TH F";
		this.inferredStartDate = date.withHour(12);
		this.inferredStartDate = this.inferredStartDate.withMinute(0);
		this.inferredStartDate = this.inferredStartDate.withSecond(0);
		this.inferredEndDate = date.withHour(13);
		this.inferredEndDate = this.inferredEndDate.withMinute(0);
		this.inferredEndDate = this.inferredEndDate.withSecond(0);
		this.inferredDescription = this.withGT+" "+Subject.FAMILY+" "+Subject.SOCIAL+" "+Subject.INTEREST;
	}
	
	public void setFixedAfternoonEvent(boolean student, LocalDateTime date){
		if(student){
			this.nameGT = "College afternoon lectures.";
			this.withGT = Sender.COLLEAGUE+" "+Sender.FRIEND+" "+Sender.STRANGER;
			this.dayGT = "M T W TH F";
			this.inferredStartDate = date.withHour(13);
			this.inferredStartDate = this.inferredStartDate.withMinute(0);
			this.inferredStartDate = this.inferredStartDate.withSecond(0);
			this.inferredEndDate = date.withHour(17);
			this.inferredEndDate = this.inferredEndDate.withMinute(0);
			this.inferredEndDate = this.inferredEndDate.withSecond(0);
			this.inferredDescription = this.withGT+" "+Subject.WORK;			
		}
		else{
			this.nameGT = "Afternoon work.";
			this.withGT = Sender.COLLEAGUE+" "+Sender.FRIEND+" "+Sender.STRANGER;
			this.dayGT = "M T W TH F";
			this.inferredStartDate = date.withHour(13);
			this.inferredStartDate = this.inferredStartDate.withMinute(0);
			this.inferredStartDate = this.inferredStartDate.withSecond(0);
			this.inferredEndDate = date.withHour(17);
			this.inferredEndDate = this.inferredEndDate.withMinute(0);
			this.inferredEndDate = this.inferredEndDate.withSecond(0);
			this.inferredDescription = this.withGT+" "+Subject.WORK;	
		}
	}
	
	public static void printListEvents(ArrayList<CalendarEvent> events) throws IOException{
		System.out.println("printing events...");
		String content = "";
		for(CalendarEvent event : events){
			content = content+"\n\n"+event.toString();
		}
		FileWriter fileWriter = new FileWriter("events.txt");
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.print(content);
	    printWriter.close();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
