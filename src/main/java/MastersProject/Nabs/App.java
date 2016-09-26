package MastersProject.Nabs;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;

import MastersProject.BeadRepo.AlertInfoBead;
import MastersProject.BeadRepo.AppInfoBead;
import MastersProject.BeadRepo.BodyInfoBead;
import MastersProject.BeadRepo.DateInfoBead;
import MastersProject.BeadRepo.NotificationInfoBead;
import MastersProject.BeadRepo.SenderInfoBead;
import MastersProject.BeadRepo.SubjectInfoBead;
import MastersProject.BeadRepo.UserLocationInfoBead;
import MastersProject.GoogleData.CalendarEvent;
import MastersProject.GoogleData.GoogleCalendarData;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Utilities.DateUtility;
import MastersProject.Utilities.ResultCallback;
import PhDProject.FriendsFamily.Models.MobileApp;
import PhDProject.FriendsFamily.Models.Notification;
import PhDProject.FriendsFamily.Models.Subject;
import PhDProject.FriendsFamily.Models.User;
import PhDProject.FriendsFamily.Utilities.DateFormatUtility;
import PhDProject.Managers.BeadRepoManager;
import PhDProject.Managers.FirebaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class App extends Application
{
	private static final String PERSISTENCE_UNIT_NAME = "informationBead";
	private static EntityManagerFactory factory;
	
	private static EntityManager em;
	private static AlertInfoBead alertInfoBead;
	private static NotificationInfoBead notificationInfoBead;
	private static SenderInfoBead senderInfoBead;
	private static SubjectInfoBead subjectInfoBead;
	private static BodyInfoBead bodyInfoBead;
	private static DateInfoBead dateInfoBead;
	private static UserLocationInfoBead userLocationInfoBead;
	private static AppInfoBead appInfoBead;
	
	private static ArrayList<UpliftedNotification> notifications;
	private static UpliftedNotification notification;
	private static int notificationNumber;
	
	private static String notificationsExcelInput = "kieranJan20.xlsx";
	private static int notificationSize;
	
	private static boolean userNabbed = true;
	private static String userLocation;
	private static String userEvent;
	
	// temp variables for getting contextual timings for alert
	private static Date nextBreak = new Date();
	private static Date nextFreePeriod = new Date();
	private static Date nextContextRelevant = new Date();
	
	public static String result;
	
	private static ArrayList<User> users;
	
	private static User selectedUser;
	
	public static ResultCallback resultCallback;

	public static void main( String[] args ) throws SQLException, ParseException, IOException
    {

        /*factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    	em = factory.createEntityManager();*/
    	
    	/*FriendsAndFamily ff = new FriendsAndFamily();
    	ff.saveUserList();
    	users = ff.getUsers();
    	selectedUser = getUserFromId("sp10-01-05");
    	System.out.println(selectedUser.getId());*/
    	//FirebaseManager.getDatabase().child("FriendsFamily/users/").setValue(selectedUser);
    	    	
    	initNabsServer();
    	
    	//users = User.getAllUsers(em);
    }
	
	private static void initNabsServer(){
		FirebaseManager.getDatabase().child("Friends&Family/users/").addValueEventListener(new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			users = new ArrayList<>();
	  			Map<String, String> result = snapshot.getValue(HashMap.class);
	  			for (Map.Entry<String, String> entry : result.entrySet())
	  			{
	  			    try {
						User user = FirebaseManager.convertStringToUser(entry.getValue());
						if(user!=null){
							users.add(user);
						}
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	  			}
	  			
	  			//convertDataToCSV();
	  			
	  			//setWebUserList(users);
	  			subscribeToWebEvents();
	  			
	  			selectedUser = getUserFromId("sp10-01-19");
	  			
	  			try {
					selectedUser.printEvents();
		  			selectedUser.printNotifications();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	  	    	
	  	    	System.out.println("The total number of users: "+users.size());
	  	    	int countNotifications = 0;
	  	    	int countEvents = 0;
	  	    	int countUsersWithoutNotifications = 0;
	  	    	for(User user: users){
	  	    		countNotifications += user.getNotifications().size();
	  	    		countEvents += user.getEvents().size();
	  	    		if(user.getNotifications().size() == 0)
	  	    			countUsersWithoutNotifications++;
	  	    	}
	  	    	System.out.println("The total number of notifications: "+countNotifications);
	  	    	System.out.println("The total number of events: "+countEvents);
	  	    	System.out.println("The total number of events: "+countUsersWithoutNotifications);
	  	    	
	  	    	
	  	    	
	  	    	BeadRepoManager repo = new BeadRepoManager();
	  	    	repo.activateBead("SenderInfoBead");
	  	    	repo.activateBead("SubjectInfoBead");
	  	    	repo.activateBead("AlertInfoBead");
	  	    	repo.activateBead("UserLocationInfoBead");
	  	    	repo.activateBead("NotificationInfoBead");
	  	    	repo.activateBead("AppInfoBead");
	  	    	repo.initialize();
	  	    	repo.saveRepoInstance();
	  	    	repo.activateNotificationListener();
	  	    	
	  	    	//launch(args);
	  	    	//javafx.application.Application.launch(App.class);
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) { }
		});
	}
	
	private static void convertDataSetToJSON(){

			/**
			 * Storing the upliftedNotification in the Triplet as a json string.
			 */
		
		for(User user: users){
			int id = 1;
			for(Notification notification: user.getNotifications()){
				notification.setId(id);
				id++;
				
				notification.setAppRank(notification.getApp().getRank());
			}
		}
			ObjectMapper mapper = new ObjectMapper();
			Gson gson = new Gson();
			try {
				gson.toJson(users, new FileWriter("jsonUsers.json"));
				
				 
		        mapper.writeValue(new FileWriter("jsonUsersJackson.json"), users);
		        
			} catch (JsonIOException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Conversion and export finished.");
			
	}
	
	private static void convertDataToCSV(){
		CSVWriter csvWriter = null;
		try
		{
			//Create CSVWriter for writing to Employee.csv 
			csvWriter = new CSVWriter(new FileWriter("UsersInferred.csv"));
            BeanToCsv bc = new BeanToCsv();
          //Creating Employee objects
        	
        	
        	 //mapping of columns with their positions
            ColumnPositionMappingStrategy mappingStrategy = 
            		new ColumnPositionMappingStrategy();
            //Set mappingStrategy type to Employee Type
            mappingStrategy.setType(User.class);
            //Fields in Employee Bean
            String[] columns = new String[]{"id","favoriteApps","activities","events", "randomChoice", "student", "stranger", "personality", "percentageHome","percentageWork", "percentageSocial", "sending User Ids", "notifications"};
            //Setting the colums for mappingStrategy
            mappingStrategy.setColumnMapping(columns);
            //Writing empList to csv file
            bc.write(mappingStrategy,csvWriter,users);
            System.out.println("CSV File written successfully!!!");
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		finally
		{
			try
			{
				//closing the writer
				csvWriter.close();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
	
	private static void subscribeToWebEvents(){
		//selectedUserEvent();
		selectedNotificationEvent();
		notificationToFire();
	}
	
	private static void setUserObjectsInFirebase(){
		for(User user: users){
			int id = 1;
			for(Notification notification: user.getNotifications()){
				notification.setId(id);
				id++;
				
				notification.setAppRank(notification.getApp().getRank());
			}
		}
		FirebaseManager.getDatabase().child("web/test/").setValue(users);
	}
	
	private static void notificationToFire(){
		FirebaseManager.getDatabase().child("web/fire/").addValueEventListener( new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			if(snapshot.getValue()!=null){
	  				HashMap result = snapshot.getValue(HashMap.class);
	  				UpliftedNotification n = new UpliftedNotification();
	  				HashMap app = (HashMap) result.get("app");
	  				HashMap subject = (HashMap) result.get("subject");
	  				
	  				n.setNotificationId((Integer) result.get("id"));
	  				
	  				n.setSender((String) result.get("sender"));
	  				n.setSubject((String) subject.get("subject"));
	  				n.setApp((String) app.get("name"));
	  				n.setDate(DateUtility.stringToDate((String) result.get("date")));
	  				
	  				n.setSenderRank((Integer) result.get("senderRank"));
	  				n.setAppRank((Integer) result.get("appRank"));
	  				n.setSubjectRank((Integer) result.get("subjectRank"));
	  				n.setBodyRank((Integer) result.get("bodyRank"));
	  				n.setDateRank((Integer) result.get("dateRank"));
	  				fireNotification(n, "Custom");
	  				
	  			}
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) {}
		});
	}
	
	private static void selectedNotificationEvent(){
		FirebaseManager.getDatabase().child("web/selectedNotification/").addValueEventListener( new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			  if(snapshot.getValue()!=null){
	  				HashMap result = snapshot.getValue(HashMap.class);
	  				ArrayList<CalendarEvent> events = null;
					try {
						events = GoogleCalendarData.getNextNEvents((Integer) result.get("id"), DateUtility.stringToDate((String) result.get("date")));
					} catch (NumberFormatException | ParseException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	  				
	  				System.out.println(events.get(0).toString());
	  				FirebaseManager.getDatabase().child("web/calendarEvents/").setValue(events);
	  			  }
	  			
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) { 
	  			  System.out.println("error adding user in firebase");
	  		  }
		});
	}
    
    /**
     * For Nabsim we are getting this information from the friends and family database as opposed 
     * .xlsx files. - all analysis can be done here. 
     * @return
     */
    public static ArrayList<UpliftedNotification> readNotifications(){
    	ArrayList<UpliftedNotification> notifications = new ArrayList<>();
    	int id = 0;
    	for(Notification notification: selectedUser.getNotifications()){
        	UpliftedNotification n = new UpliftedNotification();
        	n.setNotificationId(id);
        	n.setApp(notification.getApp().getName());
        	n.setAppRank(notification.getAppRank());
        	
        	if(notification.getSender() == null){
        		n.setSender("kieran");
        	}
        	else{
            	n.setSender(notification.getSender());        		
        	}
        	n.setSenderRank(notification.getSenderRank());
        	try {
				n.setDate(DateFormatUtility.stringToUpliftedNotificationDate(notification.getDate()));
			} catch (ParseException e) {}
        	
        	n.setDateRank(notification.getDateRank());
        	n.setSubject(notification.getSubject().getSubject());
        	n.setSubjectRank(notification.getSubjectRank());
        	n.setBody("fixed value");
        	n.setBodyRank(0);
        	n.setDateImportance("not significant");
        	notifications.add(n);
        	id++;
    	}
    	
    	return notifications;
    	/*ImportUplift helper = new ImportUplift();
    	ArrayList<UpliftedNotification> notifications = new ArrayList<>();
    	try {
			notifications =  helper.importFromExcel(notificationsExcelInput);
			System.out.println(notifications.get(0).getDate());
			return notifications;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}*/
    }
    
    public static EntityManager getEntityManager(){
    	return em;
    }
    
    public static void closeEntityManager(){
    	em.close();
    	factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    	em = factory.createEntityManager();
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		// constructing our scene
	    URL url = getClass().getResource("/MastersProject/GUI/NabsDesktop.fxml");
	    AnchorPane pane = FXMLLoader.load( url );
	    Scene scene = new Scene( pane );
	    
	    notificationNumber = -1;
	    /**
		 * Take in the uplifted notifications (this will be received via an external
		 * push at the demo.
		 */
		notifications = readNotifications();
		setNextNotificaiton();
		notificationSize = notifications.size();
		
	    // setting the stage
	    primaryStage.setScene( scene );
	    primaryStage.setTitle( "Demo" );
	    //primaryStage.setFullScreen(true);
	    primaryStage.show();
	}
	
	/**
	 * Will be modified to read from the list of notifications from user of Friends & Family 
	 * data-set.
	 */
	public static void refreshNotificationExcelInput(){
		notificationNumber = -1;
		notifications = readNotifications();
		setNextNotificaiton();
		notificationSize = notifications.size();
	}
	
	/**
	 * After the rank's have been updated, must update the notification 
	 * uplift list as rank is inserted at the uplift point. Also refresh the 
	 * currently selected notification.
	 */
	public static void refreshNotificationsOnRankUpdate(){
		notifications = readNotifications();
		notification = notifications.get(notificationNumber);
	}
	
	public static boolean setNextNotificaiton(){
		if(notificationNumber<notifications.size()-1){
			notificationNumber++;
			notification = notifications.get(notificationNumber);
			return true;
		}
		else	return false;
	}
	
	public static boolean setPrevNotification(){
		if(notificationNumber>0){
			notificationNumber--;
			notification = notifications.get(notificationNumber);
			return true;
		}
		else return false;
	}
	
	/**
	 * Fire the notification based on whether you want to fire a nabbed and uplifted "real-world"
	 * notification or a custom notification. This function is called from the controller when the 
	 * "send" button is pressed.
	 * @param customNotification
	 * @param type
	 * @return
	 */
	public static void fireNotification(UpliftedNotification customNotification, String type){
		
		result = null;
		switch(type){
		case "Nabbed":
			/**
			 * For a given notification create a bead group
			 */
			setNewNotification(notification);
			break;
		case "Custom":
			/**
			 * For a given notification create a bead group
			 */
			setNewNotification(customNotification);
			break;
		}
	}
	
	private static void setNewNotification(UpliftedNotification notification){
		FirebaseManager.getDatabase().child("CurrentNotification/").setValue(notification);
	}

	private static User getUserFromId(String id){
		for(User user: users){
			if(user.getId().equals(id)){
				return user;
			}
		}
		return null;
	}
	
	public static UpliftedNotification getNotification() {
		return notification;
	}	
	
    public static void setNotificationsExcelInput(String notificationsExcelInput) {
		App.notificationsExcelInput = notificationsExcelInput;
	}

	public static int getNotificationSize() {
		return notificationSize;
	}

	public static int getNotificationNumber() {
		return notificationNumber;
	}

	public static boolean isUserNabbed() {
		return userNabbed;
	}

	public static void setUserNabbed(boolean userNabbed) {
		App.userNabbed = userNabbed;
	}

	public static String getUserLocation() {
		return userLocation;
	}

	public static void setUserLocation(String userLocation) {
		App.userLocation = userLocation;
	}

	public static String getUserEvent() {
		return userEvent;
	}

	public static void setUserEvent(String userEvent) {
		App.userEvent = userEvent;
	}

	public static Date getNextBreak() {
		return nextBreak;
	}

	public static void setNextBreak(Date nextBreak) {
		App.nextBreak = nextBreak;
	}

	public static Date getNextFreePeriod() {
		return nextFreePeriod;
	}

	public static void setNextFreePeriod(Date nextFreePeriod) {
		App.nextFreePeriod = nextFreePeriod;
	}

	public static Date getNextContextRelevant() {
		return nextContextRelevant;
	}

	public static void setNextContextRelevant(Date nextContextRelevant) {
		App.nextContextRelevant = nextContextRelevant;
	}
	
	/**
	 * Called from within the Google Calendar class to get the inferred 
	 * events associated with the current user from the friends & family data-set.
	 * @return
	 */
	public static User getSelectedUser(){
		return selectedUser;
	}
	
	public static ArrayList<String> getUserStringList(){
		ArrayList<String> userStringList = new ArrayList<>();
		for(User user: users){
			//userStringList.add(user.getId()+"---"+user.getNotifications().size()+"---"+user.getEvents().size());
			userStringList.add(user.getId());
		}
		return userStringList;
	}
	
	public static void setSelectedUser(String userId){
		for(User user: users){
			if(user.getId().equals(userId)){
				selectedUser = user;
			}
		}
	}
	
}
