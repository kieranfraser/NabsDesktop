package MastersProject.Nabs;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import MastersProject.BeadRepo.AlertInfoBead;
import MastersProject.BeadRepo.AppInfoBead;
import MastersProject.BeadRepo.BodyInfoBead;
import MastersProject.BeadRepo.DateInfoBead;
import MastersProject.BeadRepo.NotificationInfoBead;
import MastersProject.BeadRepo.SenderInfoBead;
import MastersProject.BeadRepo.SubjectInfoBead;
import MastersProject.BeadRepo.UserLocationInfoBead;
import MastersProject.Constants.ActivationType;
import MastersProject.Constants.BeadType;
import MastersProject.Constants.ConnectionType;
import MastersProject.Models.InformationBead;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Utilities.ResultCallback;
import PhDProject.FriendsFamily.Models.Notification;
import PhDProject.FriendsFamily.Models.User;
import PhDProject.FriendsFamily.Utilities.DateFormatUtility;
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

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    	em = factory.createEntityManager();
    	
    	//FriendsAndFamily ff = new FriendsAndFamily();
    	//ff.saveUserList();
    	//ArrayList<User> users = ff.getUsers();
    	
    	users = User.getAllUsers(em);
    	
    	selectedUser = getUserFromId("sp10-01-05");
    	
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
    	
    	launch(args);
    }

	/**
	 * Init the bead repo
	 */
    private static void initBeadRepoForNotification(UpliftedNotification notification){
    	
    	alertInfoBead = new AlertInfoBead();
    	alertInfoBead.setAttributeValueType(BeadType.ALERT);
    	alertInfoBead.setComMode(ConnectionType.PUSH);
		alertInfoBead.setOnOff(ActivationType.ON);
		alertInfoBead.setAuthorizationToSendToID(new ArrayList<String>());
		alertInfoBead.setInfoBeadName("Alert Info Bead");
		alertInfoBead.setInputInterfaces(new ArrayList<String>());
		alertInfoBead.setOutputInterface("output");
		alertInfoBead.setVersion("1");
		
    	senderInfoBead = new SenderInfoBead();
    	senderInfoBead.setAttributeValueType(BeadType.SENDER);
    	senderInfoBead.setComMode(ConnectionType.PUSH);
    	senderInfoBead.setOnOff(ActivationType.ON);
    	senderInfoBead.setAuthorizationToSendToID(new ArrayList<String>());
    	senderInfoBead.setInfoBeadName("Sender Info Bead");
    	senderInfoBead.setInputInterfaces(new ArrayList<String>());
    	senderInfoBead.setOutputInterface("output");
    	senderInfoBead.setVersion("1");
    	
    	subjectInfoBead = new SubjectInfoBead();
    	subjectInfoBead.setAttributeValueType(BeadType.SUBJECT);
    	subjectInfoBead.setComMode(ConnectionType.PUSH);
    	subjectInfoBead.setOnOff(ActivationType.ON);
    	subjectInfoBead.setAuthorizationToSendToID(new ArrayList<String>());
    	subjectInfoBead.setInfoBeadName("Subject Info Bead");
    	subjectInfoBead.setInputInterfaces(new ArrayList<String>());
    	subjectInfoBead.setOutputInterface("output");
    	subjectInfoBead.setVersion("1");
    	
    	appInfoBead = new AppInfoBead();
    	appInfoBead.setAttributeValueType(BeadType.APPLICATION);
    	appInfoBead.setComMode(ConnectionType.PUSH);
    	appInfoBead.setOnOff(ActivationType.ON);
    	appInfoBead.setAuthorizationToSendToID(new ArrayList<String>());
    	appInfoBead.setInfoBeadName("Application Info Bead");
    	appInfoBead.setInputInterfaces(new ArrayList<String>());
    	appInfoBead.setOutputInterface("output");
    	appInfoBead.setVersion("1");
    	
    	bodyInfoBead = new BodyInfoBead();
    	bodyInfoBead.setAttributeValueType(BeadType.BODY);
    	bodyInfoBead.setComMode(ConnectionType.PUSH);
    	bodyInfoBead.setOnOff(ActivationType.ON);
    	bodyInfoBead.setInfoBeadName("Body Info Bead");
    	bodyInfoBead.setInputInterfaces(new ArrayList<String>());
    	bodyInfoBead.setOutputInterface("output");
    	bodyInfoBead.setVersion("1");
    	
    	dateInfoBead = new DateInfoBead();
    	dateInfoBead.setAttributeValueType(BeadType.DATE);
    	dateInfoBead.setComMode(ConnectionType.PUSH);
    	dateInfoBead.setOnOff(ActivationType.ON);
    	dateInfoBead.setInfoBeadName("Date Info Bead");
    	dateInfoBead.setInputInterfaces(new ArrayList<String>());
    	dateInfoBead.setOutputInterface("output");
    	dateInfoBead.setVersion("1");    	
    	
    	userLocationInfoBead = new UserLocationInfoBead();
    	userLocationInfoBead.setAttributeValueType(BeadType.LOCATION);
    	userLocationInfoBead.setComMode(ConnectionType.PUSH);
    	userLocationInfoBead.setOnOff(ActivationType.ON);
    	userLocationInfoBead.setAuthorizationToSendToID(new ArrayList<String>());
    	userLocationInfoBead.setInfoBeadName("User Location Info Bead");
    	userLocationInfoBead.setInputInterfaces(new ArrayList<String>());
    	userLocationInfoBead.setOutputInterface("output");
    	userLocationInfoBead.setVersion("1");  
		
    	notificationInfoBead = new NotificationInfoBead();
    	notificationInfoBead.setAttributeValueType(BeadType.NOTIFICATION);
    	notificationInfoBead.setComMode(ConnectionType.PUSH);
    	notificationInfoBead.setOnOff(ActivationType.ON);
    	notificationInfoBead.setAuthorizationToSendToID(new ArrayList<String>());
    	notificationInfoBead.setInfoBeadName("Notification Info Bead");
    	notificationInfoBead.setInputInterfaces(new ArrayList<String>());
    	notificationInfoBead.setOutputInterface("output");
    	notificationInfoBead.setVersion("1");
    	
    	notificationInfoBead.addListener(subjectInfoBead);
    	notificationInfoBead.addListener(senderInfoBead);
    	notificationInfoBead.addListener(appInfoBead);
    	notificationInfoBead.addListener(dateInfoBead);
    	notificationInfoBead.addListener(bodyInfoBead);
    	notificationInfoBead.addListener(userLocationInfoBead);
    	
    	senderInfoBead.addListener(alertInfoBead);
    	subjectInfoBead.addListener(alertInfoBead);
    	appInfoBead.addListener(alertInfoBead);
    	dateInfoBead.addListener(alertInfoBead);
    	bodyInfoBead.addListener(alertInfoBead);
    	userLocationInfoBead.addListener(alertInfoBead);
    	
    	saveBead(alertInfoBead, notification.getNotificationId());
    	saveBead(notificationInfoBead, notification.getNotificationId());
    	saveBead(senderInfoBead, notification.getNotificationId());
    	saveBead(subjectInfoBead, notification.getNotificationId());
    	
    }
    
    /**
     * Init beads and save in database (needed for generation of id) 
     * Note: info bead part number refers to the bead group
     * associated with (one group per notification)
     * @param inputBead
     */
    private static void saveBead(Object inputBead,int id){
    	em.getTransaction().begin();
    	InformationBead bead = (InformationBead) inputBead;
    	bead.setPartNumber(String.valueOf(id));
    	em.persist(bead);
		em.getTransaction().commit();
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
			initBeadRepoForNotification(notification);
			notificationInfoBead.notificationReceived(notification);
			break;
		case "Custom":
			/**
			 * For a given notification create a bead group
			 */
			initBeadRepoForNotification(customNotification);
			notificationInfoBead.notificationReceived(customNotification);
			break;
		}
		
		/**
		 * Must wait for the process to finish before alerting the receiving phone of the 
		 * result.	
		 */
		//while(result == null){}
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
