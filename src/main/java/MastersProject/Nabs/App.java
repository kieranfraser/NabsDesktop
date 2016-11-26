package MastersProject.Nabs;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.mortbay.log.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
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
import MastersProject.DBHelper.ImportUplift;
import MastersProject.GoogleData.CalendarEvent;
import MastersProject.GoogleData.GoogleCalendarData;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Utilities.DateUtility;
import MastersProject.Utilities.ResultCallback;
import PhDProject.FriendsFamily.Models.MobileApp;
import PhDProject.FriendsFamily.Models.Notification;
import PhDProject.FriendsFamily.Models.Params;
import PhDProject.FriendsFamily.Models.Subject;
import PhDProject.FriendsFamily.Models.User;
import PhDProject.FriendsFamily.PSO.Particle;
import PhDProject.FriendsFamily.Utilities.DateFormatUtility;
import PhDProject.Managers.BeadRepoManager;
import PhDProject.Managers.FirebaseManager;
import PhDProject.Managers.ParameterManager;
import PhDProject.Managers.StatisticsManager;
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
	private static ArrayList<ArrayList<UpliftedNotification>> realNotifications;
	
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
	
	private static BeadRepoManager repo;
	private static int paramId;
	
	private static ArrayList<Params> paramList;

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
    	System.out.println("Have actually started..");
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
	  			
	  			ArrayList<String> subjects = new ArrayList<String>();
	  			
	  			
	  			for(User user: users){
	  				int id = 1;
	  				for(Notification notification: user.getNotifications()){
	  					notification.setId(id);
	  					id++;
	  					
	  					notification.setAppRank(notification.getApp().getRank());
	  					
	  					if(!subjects.contains(notification.getSubject().getSubject().trim())){
	  						subjects.add(notification.getSubject().getSubject());
	  					}
	  				}
	  			}
	  			repo = new BeadRepoManager();
	  	    	repo.activateBead("SenderInfoBead");
	  	    	repo.activateBead("SubjectInfoBead");
	  	    	repo.activateBead("AlertInfoBead");
	  	    	repo.activateBead("UserLocationInfoBead");
	  	    	repo.activateBead("NotificationInfoBead");
	  	    	repo.activateBead("AppInfoBead");
	  	    	repo.initialize();
	  	    	

	  			//getClosestUsers();
	  			
	  			/*String subjectOutput = "";
	  			for(String subject: subjects){
	  				subjectOutput = subjectOutput+subject+"\n";
	  			}
	  			PrintWriter pr = null;
				try {
					pr = new PrintWriter("subjectOutput.txt");
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}    

	  		    pr.println(subjectOutput);
	  		    pr.close();*/
	  			//convertDataToCSV();
	  			
	  	    	realNotifications = useRealWorldData();
	  	    	ArrayList<User> realUsers = new ArrayList<User>();
	  	    	int i = 0;
	  	    	for(ArrayList<UpliftedNotification> notificationList: realNotifications){
	  	    		User user = users.get(i);
	  	    		ArrayList<Notification> userNotifications = new ArrayList<Notification>();
	  	    		int counter = 1;
	  	    		for(UpliftedNotification notification: notificationList){
	  	    			Notification n = new Notification();
	  	    			n.setSender(notification.getSender());
	  	    			n.setSenderRank(notification.getSenderRank());
	  	    			Subject subject = new Subject();
	  	    			subject.setSubject(notification.getSubject());
	  	    			n.setSubject(subject);
	  	    			n.setSubjectRank(notification.getSubjectRank());
	  	    			MobileApp app = new MobileApp();
	  	    			app.setName(notification.getApp());
	  	    			n.setApp(app);
	  	    			n.setAppRank(notification.getAppRank());
						n.setDate(DateFormatUtility.convertDateToStringUTC(notification.getDate()));
						n.setId(counter);
						n.setBody("");
						n.setBodyRank(0);
						n.setDateRank(0);
						
						userNotifications.add(n);
						counter++;
	  	    		}
	  	    		user.setNotifications(userNotifications);
	  	    		if(i<3){
	  	    			user.setId("kieran"+i);
	  	    		}
	  	    		else{
	  	    			user.setId("owen"+i);
	  	    		}
	  	    		realUsers.add(user);
	  	    		i++;
	  	    	}
	  			users = realUsers;
	  			System.out.println("Number of realUsers: "+users.size());
	  			for(User user: users){
	  				System.out.println(user.getNotifications().size());
	  			}
	  			

	  			repo.saveRepoInstance();
	  	    	repo.activateNotificationListener();
	  			setUserObjectsInFirebase();
	  			FirebaseManager.getDatabase().child("web/fire/").removeValue();
	  			getParams();
	  			
				/*selectedUser.printEvents();
				selectedUser.printNotifications();*/
	  	    	
	  	    	/*System.out.println("The total number of users: "+users.size());
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
	  	    	System.out.println("The total number of events: "+countUsersWithoutNotifications);*/
	  	    	
	  	    	
	  	    	
	  	    	
	  	    	
	  	    	
	  	    	//experiment1();
	  	    	//experiment2();
	  	    	
	  	    	//launch(args);
	  	    	//javafx.application.Application.launch(App.class);
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) { }
		});
	}
	
	private static void getOptimalParams(){
		double globalError = 10000;
		int paramIndex = 0;
		String pUser = "";
		int counter = 0;
		for(Params p: paramList){
			String error = p.getOptimalError().substring(1, p.getOptimalError().length()-1);
			double[] numbers = Arrays.asList(error.split(","))
                    .stream()
                    .map(String::trim)
                    .mapToDouble(Double::parseDouble).toArray();
			double totalError = 0;
			for(double value: numbers){
				totalError += value;
			}
			if(totalError < globalError){
				globalError = totalError;
				paramIndex = counter;
				pUser = p.getUser();
			}
			counter++;
		}
		System.out.println(paramIndex);
		System.out.println(pUser);
	}
	
	private static void getClosestUsers(){
		relevantUsers = findRelevantUsers();
		double kieranClosest = 1000;
		double owenClosest = 1000;
		String kieranClosestUser = "";
		String owenClosestUser = "";
		for(User user: relevantUsers){
			if(Math.abs(user.getNotifications().size() - 36) < kieranClosest && user.getNotifications().size() > 10){
				kieranClosest = Math.abs(user.getNotifications().size() - 36);
				kieranClosestUser = user.getId();
			}
			if(Math.abs(user.getNotifications().size() - 26) < owenClosest && user.getNotifications().size() > 10){
				owenClosest = Math.abs(user.getNotifications().size() - 26);
				owenClosestUser = user.getId();
			}
		}
		System.out.println(kieranClosest);
		System.out.println(kieranClosestUser);
		

		System.out.println(owenClosest);
		System.out.println(owenClosestUser);
	}
	
	private static void getParams(){
		FirebaseManager.getDatabase().child("Exp1/").addValueEventListener( new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			  if(snapshot.getValue() != null){

		  			  paramList = new ArrayList<Params>();
	  				  HashMap result = snapshot.getValue(HashMap.class);
	  				  Iterator it = result.entrySet().iterator();
	  				  while (it.hasNext()) {
		  			      Params param = new Params();
	  					  Map.Entry pair = (Map.Entry) it.next();
		  			      param.setUser((String) pair.getKey());
		  			      Map paramObject = (LinkedHashMap) pair.getValue();
		  			      param.setParams((ArrayList) paramObject.get("optimalParams"));
		  			      param.setOptimalError((String) paramObject.get("optimalResult"));
		  			      paramList.add(param);
		  			      it.remove(); // avoids a ConcurrentModificationException
	  				  }
	  	  			//getOptimalParams();
	  	  			//subscribeToWebEvents();
	  			  }
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) {}
		});
	}
	
	private static ArrayList<ArrayList<UpliftedNotification>> useRealWorldData(){
		ImportUplift upliftManager = new ImportUplift();
		ArrayList<ArrayList<UpliftedNotification>> userNotifications = new ArrayList<ArrayList<UpliftedNotification>>();
		System.out.println("Getting real world data..");
		for(String file: ImportUplift.realWorldFiles){
			try {
				notifications = upliftManager.importFromExcel(file+".xlsx");
				userNotifications.add(notifications);
				System.out.println("notifications: "+notifications.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return userNotifications;
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
		subscribeToVariableValues();
		selectedNotificationEvent();
		notificationToFire();
		singleNotificationToFire();
	}
	
	private static void setUserObjectsInFirebase(){
		FirebaseManager.getDatabase().child("web/test/").setValue(users);
	}
	
	private static int family = 0;
	private static int work = 0;
	private static int social = 0;
	
	private static void subscribeToVariableValues(){
		FirebaseManager.getDatabase().child("web/variable").addValueEventListener( new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			  if(snapshot.getValue() != null){
		  				HashMap subjectValues = snapshot.getValue(HashMap.class);
		  				family = (int) subjectValues.get("family");
		  				work = (int) subjectValues.get("work");
		  				social = (int) subjectValues.get("social");
	  			  }
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) {}
		});
	}
	
	private static void notificationToFire(){
		FirebaseManager.getDatabase().child("web/fire").addValueEventListener( new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			  ParameterManager paramManager = ParameterManager.getParamManager();
	  			  ArrayList<String> newParams = paramManager.convertBestToParamArray(paramList.get(family).getParams());
	  			  paramManager.setSenderParams(newParams);
	  			  paramManager.setSubjectParams(newParams);
	  			  paramManager.setAlertParams(newParams);
	  			  
	  			  System.out.println("*******************Firing notification******************************");
	  			  if(snapshot.getValue() != null){
		  				User user = getUserFromId((String) snapshot.getValue());
		  				System.out.println(user.getNotifications().size());
			  			for(Notification n: user.getNotifications()){
			  				UpliftedNotification nToSend = new UpliftedNotification();
			  				nToSend.setSender(n.getSender());
			  				nToSend.setSubject(n.getSubject().getSubject());
			  				nToSend.setApp(n.getApp().getName());
			  				nToSend.setNotificationId(n.getId());
			  				nToSend.setSenderRank(n.getSenderRank());
			  				nToSend.setSubjectRank(n.getSubjectRank());
			  				nToSend.setAppRank(n.getAppRank());
			  				nToSend.setDate(DateUtility.stringToDate(n.getDate()));
			  				/*switch(n.getSubject().getSubject()){
			  				case "family":
			  					nToSend.setSubjectRank(family);
			  					break;
			  				case "work":
			  					nToSend.setSubjectRank(work);
			  					break;
			  				case "social":
			  					nToSend.setSubjectRank(social);
			  					break;
			  				case "interest":
			  					nToSend.setSubjectRank(social);
			  					break;
			  				}*/
			  				fireNotification(nToSend, "Custom");
			  			}
	  			  }
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) {}
		});
	}
	
	private static void singleNotificationToFire(){
		FirebaseManager.getDatabase().child("web/fireSingle").addValueEventListener( new ValueEventListener() {
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
						System.out.println("kieran fraser" + DateUtility.stringToDate((String) result.get("date")));
						events = GoogleCalendarData.getNextNEvents(10, DateUtility.stringToDate((String) result.get("date")));
					} catch (NumberFormatException | ParseException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for(CalendarEvent event: events){
						System.out.println("kieran " +event.getStartDate() );
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
	
	public static ArrayList<Integer> gBestPosition;
	public static double[] gBestPercent;
	private static ArrayList<Particle> particles;
	private static PrintWriter pr;
	private static String textName = "PSO.txt";
	private static ArrayList<User> relevantUsers;
	private static User relevantUser;
	
	private static ArrayList<double[]> particleFitness;
	
	public static void experiment2(){
		Integer[] optimal = {3, 3, 1, 2, 2, 1, 2, 3, 2, 2, 1, 1, 3, 3, 3, 1, 1, 1, 3, 1, 4, 4, 5, 2, 5, 3, 3, 4, 5, 3, 2, 4, 2, 3, 5, 3, 3, 5, 3, 1, 4, 4, 5, 3, 2};
		relevantUsers = findRelevantUsers();
		
		ArrayList<User> givenUser = new ArrayList<User>();
		System.out.println("notification size: "+relevantUsers.get(1).getNotifications().size());
		givenUser.add(relevantUsers.get(1));
		relevantUsers = givenUser;
		
		List<Integer> converting = Arrays.asList(optimal);
		ArrayList<Integer> converted = new ArrayList<Integer>();
		for(int val:converting){
			converted.add(val);
		}
		gBestPosition = converted;
		testFinalResult();
	}
	
	public static void experiment1(){
		System.out.println("Experiment PSO");
		pr = null;
		   
		
		relevantUsers = findRelevantUsers();
		/*ArrayList<User> givenUser = new ArrayList<User>();
		System.out.println("notification size: "+relevantUsers.get(1).getNotifications().size());
		givenUser.add(relevantUsers.get(1));
		relevantUsers = givenUser;*/
		for(User user: relevantUsers){
			try {
				pr = new PrintWriter(user.getId()+".txt");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			
			relevantUser = user;
			particleFitness = new ArrayList<double[]>();
			
			particles = new ArrayList<Particle>();
			for(int i=0; i<15; i++){
				particles.add(new Particle());
			}
			gBestPosition = new ArrayList<Integer>();
			gBestPercent = null;
			
			for(int iterations=0; iterations<5; iterations++){
				
				iteration(iterations);
				pr.println(Arrays.toString(gBestPercent));
				pr.println(gBestPosition);
			}
			StatisticsManager.saveOptimalStats(relevantUser.getId(), gBestPosition, Arrays.toString(gBestPercent));
		    pr.close();
		}
		
	}
	
	private static void printStats(int iterations){
		    pr.println("Iteration "+iterations);
		    pr.println("Best Fitness\n"+Arrays.toString(gBestPercent));
		    pr.println("Best Position\n"+gBestPosition);
		    pr.println("\n-------------\n");
		    int i=0;
		    for(double[] value : particleFitness){
		    	pr.println("Current fitness of particle "+i+"\n: "+Arrays.toString(value)+"\n");
		    	i++;
		    }
	}
	
	private static void iteration(int iteration){
		int i = 0;
		particleFitness = new ArrayList<double[]>();
		for(Particle p:particles){
			double[] fitnessValue = fitnessFunction(p);
			ArrayList<Integer> position = p.getCurrentPosition();
			p.setCurrentFitness(fitnessValue);
			particleFitness.add(i, fitnessValue);
			if(StatisticsManager.checkLessThanEqual(fitnessValue, p.getpBestPercentage())){
				p.setpBestPercentage(fitnessValue);
				p.setpBestPosition(p.getCurrentPosition());
			}
			if(StatisticsManager.checkLessThanEqual(fitnessValue, gBestPercent)){
				pr.println("particle "+i+" is updating position");
				pr.println("due to fitness value of: \n"+Arrays.toString(fitnessValue));
				pr.println("which is less than: \n"+Arrays.toString(gBestPercent));
				pr.println("\n");
				
				gBestPercent = fitnessValue;
				gBestPosition = new ArrayList<Integer>();
				gBestPosition = position;
				
				pr.println(gBestPosition);
				pr.println(p.getCurrentPosition());
			}
			i++;
		}
		printStats(iteration);
		
		for(Particle p: particles){
			if(!p.checkPositionSameAs(gBestPosition)){
				p.updateVelocity();
			}
			pr.println(gBestPosition);
		}
	}
	
	private static double[] fitnessFunction(Particle p){
		// Set current particles params to the fuzzy classes
		ParameterManager paramManager = ParameterManager.getParamManager();
		paramManager.setSenderParams(p.getSenderParams().toArray(new String[p.getSenderParams().size()]));
		paramManager.setSubjectParams(p.getSubjectParams().toArray(new String[p.getSubjectParams().size()]));
		paramManager.setAlertParams(p.getAlertParams().toArray( new String[p.getAlertParams().size()] ) );
		
		//for(User possibleUser: relevantUsers){
		selectedUser = relevantUser;
		for(Notification n: relevantUser.getNotifications()){
				UpliftedNotification nToSend = new UpliftedNotification();
				nToSend.setSender(n.getSender());
				nToSend.setSubject(n.getSubject().getSubject());
				nToSend.setApp(n.getApp().getName());
				nToSend.setNotificationId(n.getId());
				nToSend.setSenderRank(n.getSenderRank());
				nToSend.setSubjectRank(n.getSubjectRank());
				nToSend.setAppRank(n.getAppRank());
				nToSend.setDate(DateUtility.stringToDate(n.getDate()));
				switch(n.getSubject().getSubject()){
				case "family":
					nToSend.setSubjectRank(family);
					break;
				case "work":
					nToSend.setSubjectRank(work);
					break;
				case "social":
					nToSend.setSubjectRank(social);
					break;
				}
				repo.activateNotification(nToSend);
		}
		//}
		double[] fitnessValue = StatisticsManager.getStatsManager().workFunction();
		StatisticsManager.getStatsManager().reset();
		return fitnessValue;
	}
	
	private static ArrayList<User> findRelevantUsers(){
		ArrayList<User> foundUsers = new ArrayList<User>();
		for(User possibleUser: users){
			for(Notification n: possibleUser.getNotifications()){
				if(n.getSubject().getSubject().contains("social") || n.getSubject().getSubject().contains("work")){

					foundUsers.add(possibleUser);
					break;
					/*if(foundUsers.size()<1){
						foundUsers.add(possibleUser);
						break;
					}*/
				}
			}
		}
		System.out.println("Size of found users: "+foundUsers.size());
		return foundUsers;
	}
	
	private static void testFinalResult(){

		StatisticsManager.getStatsManager().reset();
		// Set current particles params to the fuzzy classes
		ArrayList<String> gBest = ParameterManager.convertBestToParamArray(gBestPosition);
		ParameterManager paramManager = ParameterManager.getParamManager();
		paramManager.setSenderParams(ParameterManager.getSenderParams(gBest));
		paramManager.setSubjectParams(ParameterManager.getSubjectParams(gBest));
		paramManager.setAlertParams(ParameterManager.getAlertParams(gBest));
		
		for(User possibleUser: relevantUsers){
			selectedUser = possibleUser;
			for(Notification n: possibleUser.getNotifications()){
					UpliftedNotification nToSend = new UpliftedNotification();
					nToSend.setSender(n.getSender());
					nToSend.setSubject(n.getSubject().getSubject());
					nToSend.setApp(n.getApp().getName());
					nToSend.setNotificationId(n.getId());
					nToSend.setSenderRank(n.getSenderRank());
					nToSend.setSubjectRank(n.getSubjectRank());
					nToSend.setAppRank(n.getAppRank());
					nToSend.setDate(DateUtility.stringToDate(n.getDate()));
					switch(n.getSubject().getSubject()){
					case "family":
						nToSend.setSubjectRank(family);
						break;
					case "work":
						nToSend.setSubjectRank(work);
						break;
					case "social":
						nToSend.setSubjectRank(social);
						break;
					}
					repo.activateNotification(nToSend);
			}
		}
		double[] fitnessValue = StatisticsManager.getStatsManager().workFunction();
		System.out.print("Fitness value: "+Arrays.toString(fitnessValue));
		StatisticsManager.getStatsManager().printStats();
		StatisticsManager.getStatsManager().reset();
	}
}
