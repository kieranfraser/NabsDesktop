package PhDProject.FriendsFamily.Models;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TypedQuery;

import MastersProject.Nabs.App;
import PhDProject.FriendsFamily.Utilities.DateFormatUtility;

/**
 * User class which contains information on the receiver of the 
 * notification. Data is based on the survey information available and
 * is sometimes assumed. 
 * 
 * Contains a number of "subject" attributes from which, once a notification
 * is fired at the user,  a random choice is made between them to select the
 * subject for the notification.
 * 
 * @author kfraser
 *
 */
@Entity
public class User implements Serializable{
	
	private static final long serialVersionUID = -1123854112534962383L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	@OneToMany(cascade = CascadeType.ALL)
	private ArrayList<MobileApp> favoriteApps;
	
	@OneToMany(cascade = CascadeType.ALL)
	private ArrayList<Subject> activities; 
	
	@OneToMany(cascade = CascadeType.ALL)
	private ArrayList<Event> events;
	
	@OneToMany(cascade = CascadeType.ALL)
	private ArrayList<Subject> randomChoice;
	
    private Random randomGenerator;
    
    // Individual Detail
    private boolean student;
    private boolean stranger;

	@OneToOne(cascade = CascadeType.ALL)
    private Personality personality;  
	
    private int percentageHome;
    private int percentageWork;
    private int percentageSocial;
    
    private ArrayList<String> sendingUserIds;
    
	@OneToMany(cascade = CascadeType.ALL)
    private ArrayList<Notification> notifications;
	
	public User(){
		randomChoice = new ArrayList<Subject>();
		activities = new ArrayList<Subject>();
		stranger = false;
		
        randomGenerator = new Random();
	}
		
	public boolean isStranger() {
		return stranger;
	}

	public void setStranger(boolean stranger) {
		this.stranger = stranger;
	}

	public ArrayList<String> getSendingUserIds() {
		return sendingUserIds;
	}

	public void setSendingUserIds(ArrayList<String> sendingUserIds) {
		this.sendingUserIds = sendingUserIds;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<MobileApp> getFavoriteApps() {
		return favoriteApps;
	}
	public void setFavoriteApps(ArrayList<MobileApp> favoriteApps) {
		this.favoriteApps = favoriteApps;
	}	
	
	/**
	 * Sender identity being the relationship between the 
	 * receiver and the sender of the notification. 
	 * e.g. family, friend, colleague, stranger
	 * @param senderIdentity
	 * @return
	 */
	public Subject getSubject(String senderIdentity){
		Subject chosenSubject = new Subject();
		populateList(senderIdentity);
		
		if(!randomChoice.isEmpty()){
			int index = randomGenerator.nextInt(randomChoice.size());
	        chosenSubject = randomChoice.get(0);
		}
		else{
			chosenSubject.setSubject("no subject found");
			chosenSubject.setGround_truth("no subject found");
		}
		
		return chosenSubject;
	}
	
	/**
	 * resets the list of possible subjects (one per type) and 
	 * adds new randomly selected subjects (removes events that have 
	 * already been chosen)
	 */
	private void populateList(String senderIdentity){

		randomChoice = new ArrayList<Subject>();
		Subject chosenSubject = new Subject();
		int index;
        //index = randomGenerator.nextInt(groups.size());
		
		// Select one activity subject (depending on sender identity - i.e. friend, stranger, family or partner)
		if(!activities.isEmpty()){
			if(senderIdentity.equals("stranger")){
				chosenSubject = activities.get(0);
			}
			if(senderIdentity.equals("friend")){
				int rand = (int)(Math.random()*2);
				switch(rand){
				case 0:
					chosenSubject = activities.get(1);
					break;
				case 1:
					chosenSubject = activities.get(2);
					break;
				}
			}
			if(senderIdentity.equals("partner")){
				chosenSubject = activities.get(3);
			}
	        randomChoice.add(0,chosenSubject);
		}
	}

	public ArrayList<Subject> getActivities() {
		return activities;
	}

	public void setActivities(ArrayList<Subject> activities) {
		this.activities = activities;
	}

	public Personality getPersonality() {
		return personality;
	}

	public void setPersonality(Personality personality) {
		this.personality = personality;
	}

	public ArrayList<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(ArrayList<Notification> notifications) {
		this.notifications = notifications;
	}

	public boolean isStudent() {
		return student;
	}

	public void setStudent(boolean student) {
		this.student = student;
	}

	public int getPercentageHome() {
		return percentageHome;
	}

	public void setPercentageHome(int percentageHome) {
		this.percentageHome = percentageHome;
	}

	public int getPercentageWork() {
		return percentageWork;
	}

	public void setPercentageWork(int percentageWork) {
		this.percentageWork = percentageWork;
	}

	public int getPercentageSocial() {
		return percentageSocial;
	}

	public void setPercentageSocial(int percentageSocial) {
		this.percentageSocial = percentageSocial;
	}

	public ArrayList<Event> getEvents() {
		return events;
	}

	public void setEvents(ArrayList<Event> events) {
		this.events = events;
	}	
	
	public MobileApp getAppForCat(ArrayList<String> categories){
		ArrayList<MobileApp> apps = new ArrayList<MobileApp>();
		
		for(String category : categories){
			for(MobileApp app : this.favoriteApps){
				if(app.getCategory().contains(category)){
					apps.add(app);
				}
			}
		}
		int value = (int) (Math.random() * apps.size());
		
		if(apps.size() == 0){
			MobileApp newApp = new MobileApp();
			newApp.setName("blank");
			newApp.setCategory("blank");
			return newApp;
		}
		else
		return apps.get(value);
	}
	
	public void printNotifications() throws IOException{
		System.out.println("printing notifications...");
		String content = "";
		for(Notification notification : this.notifications){
			content = content+"\n\n"+notification.toString();
		}
		FileWriter fileWriter = new FileWriter("notifications.txt");
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.print(content);
	    printWriter.close();
	}
	
	public void printEvents() throws IOException{
		System.out.println("printing events...");
		String content = "";
		for(Event event : this.events){
			content = content+"\n\n"+event.toString();
		}
		FileWriter fileWriter = new FileWriter("events_User.txt");
	    PrintWriter printWriter = new PrintWriter(fileWriter);
	    printWriter.print(content);
	    printWriter.close();
	}
	
	/**
	 * Persist the user in database
	 */
	public void saveUser(){
		EntityManager em = App.getEntityManager();
    	em.getTransaction().begin();
    	em.persist(this);
		em.getTransaction().commit();		
	}
	
	/**
	 * Get all users
	 * @return
	 */
	public static ArrayList<User> getAllUsers(EntityManager em){
		// Query for a List of objects.
		TypedQuery<User> query =
			      em.createQuery("SELECT c FROM User c", User.class);
			  List<User> results = query.getResultList();
		//ArrayList<User> result = (ArrayList<User>) query.getResultList();
		return new ArrayList<User>(results);
	}
	
	public ArrayList<Event> getNextTenEvents(Date notificationDate){
		ArrayList<Event> events = new ArrayList<>();
		
		LocalDate localDate = DateFormatUtility.stringToLocalDate(notificationDate.toString());
		
		return events;
	}
	
	public static ArrayList<Event> getTodaysEvents(LocalDateTime today, User user){
		ArrayList<Event> todaysEvents = new ArrayList<>();
		for(Event event: user.getEvents()){
			LocalDateTime eventDate = event.getInferredStartDate();
			if(eventDate.getYear() == today.getYear() && eventDate.getDayOfYear() == today.getDayOfYear()){
				todaysEvents.add(event);
			}
		}
		return todaysEvents;
	}
}
