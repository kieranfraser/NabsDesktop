package MastersProject.Nabs;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import MastersProject.BeadRepo.AlertInfoBead;
import MastersProject.BeadRepo.NotificationInfoBead;
import MastersProject.BeadRepo.SenderInfoBead;
import MastersProject.BeadRepo.SubjectInfoBead;
import MastersProject.Constants.ActivationType;
import MastersProject.Constants.BeadType;
import MastersProject.Constants.ConnectionType;
import MastersProject.DBHelper.ImportUplift;
import MastersProject.Models.InformationBead;
import MastersProject.Models.UpliftedNotification;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * Hello world!
 *
 */
public class App extends Application
{
	private static final String PERSISTENCE_UNIT_NAME = "informationBead";
	private static EntityManagerFactory factory;
	
	private static EntityManager em;
	private static AlertInfoBead alertInfoBead;
	private static NotificationInfoBead notificationInfoBead;
	private static SenderInfoBead senderInfoBead;
	private static SubjectInfoBead subjectInfoBead;
	
	private static ArrayList<UpliftedNotification> notifications;
	private static UpliftedNotification notification;
	private static int notificationNumber;
	
	public static String result;
	
    public static void main( String[] args )
    {

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    	em = factory.createEntityManager();
    	
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
    	
    	senderInfoBead.addListener(alertInfoBead);
    	subjectInfoBead.addListener(alertInfoBead);
    	
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
     * Get the list of uplifted notifications
     * @return
     */
    private static ArrayList<UpliftedNotification> readNotifications(){
    	ImportUplift helper = new ImportUplift();
    	try {
			return helper.importFromExcel();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }
    
    public static EntityManager getEntityManager(){
    	return em;
    }
    
    public static void closeEntityManager(){
    	em.close();
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
		
	    // setting the stage
	    primaryStage.setScene( scene );
	    primaryStage.setTitle( "Demo" );
	    primaryStage.show();
	}
	
	public static String fireNotification(){
		result = null;
		if(notificationNumber<notifications.size()){
			notificationNumber++;
			notification = notifications.get(notificationNumber);
			
			/**
			 * For a given notification create a bead group
			 */
			initBeadRepoForNotification(notification);
			System.out.println("Notification fired.");
			notificationInfoBead.notificationReceived(notification);
			
		}
		else{
			System.out.println("No more notifications!");
		}
		while(result == null){}
		return result;
	}
}
