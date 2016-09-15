package MastersProject.BeadRepo;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.InfoItemFields;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Nabs.App;
import PhD.Firebase.FirebaseHelper;

@Entity
@DiscriminatorValue("Notification")
public class NotificationInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface{

	private static final long serialVersionUID = -8451356944207798106L;
	
	private List<BeadInputInterface> listeners = new ArrayList<BeadInputInterface>();
	private String partNumber;
	
	public void notificationReceived(UpliftedNotification notification) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException, IOException{
		
		Date receivedNotificationDate = new Date();
		this.activate();
		partNumber = this.getPartNumber();
		
		Triplet operational = new Triplet();
		operational.setDetectionTime(receivedNotificationDate);
		
		InfoItemFields information = new InfoItemFields();
		
		information.setInformationValue(FirebaseHelper.convertNotificationToString(notification));
		information.setInfoAccuracy(100.0);
		information.setInfoConfidenceLevel(100.0);
		information.setInfoValidFrom(receivedNotificationDate);
		
		operational.setInformationItem(information);
		operational.setId(this.partNumber);
		this.setOperational(operational);
		
		// Update the bead in database
		storeInfoBeadAttr();
		
		// push triplet to listening beads - sender, subject
		sendToConsumer(this.getId(), receivedNotificationDate, operational);
	}

	
	@Override
	public void storeInfoBeadAttr() {
		/*super.storeInfoBeadAttr();
		EntityManager em = App.getEntityManager();
    	em.getTransaction().begin();
    	em.persist(this);
		em.getTransaction().commit();*/
		
		FirebaseHelper.getDatabase().child("InfoBead/"+
				this.getOperational().getId()+"/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
	}

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.listeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.listeners.remove(bead);
	}

	/**
	 * Called when updates need to be pushed to other beads.
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InstantiationException 
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException {
		/*for(BeadInputInterface listener : listeners){
			listener.getEvidence(senderId, sentTime, outputData);
		}*/
		ArrayList<String> consumerBeadList = new ArrayList<String>();
		consumerBeadList.add("SenderInfoBead");
		consumerBeadList.add("SubjectInfoBead");
		consumerBeadList.add("AppInfoBead");
		for(String bead: consumerBeadList){
			
				Constructor<?> constructor = Class.forName("MastersProject.BeadRepo."+bead).getConstructor();
				Object myObj = (InformationBead) constructor.newInstance();
				
				Method myObjMethod = myObj.getClass().getMethod("getEvidence", String.class, Date.class, Triplet.class);
				myObjMethod.invoke(myObj, senderId, sentTime, outputData); 
	    	
		}
	}

	/**
	 * Called to get sensor evidence or push events.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {}

}
