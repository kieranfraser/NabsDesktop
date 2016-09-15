package MastersProject.BeadRepo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.InfoItemFields;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Utilities.FirebaseManager;

@Entity
@DiscriminatorValue("Notification")
public class NotificationInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface{

	private static final long serialVersionUID = -8451356944207798106L;
	
	private int notificationId;
	
	public void notificationReceived(UpliftedNotification notification){
		
		this.activate();

		// Might not be unique accross different user notification data-sets (use username + id?)
		notificationId = notification.getNotificationId();
		
		createInfoBeadTriplet();
		
		// Update the bead in database
		storeInfoBeadAttr();
		
		// push triplet to listening beads - sender, subject
		sendToConsumer(this.getId(), new Date(), createTripletToSend());
	}

	
	@Override
	public void storeInfoBeadAttr() {
		FirebaseManager.getDatabase().child("InfoBead/"+
				this.getOperational().getId()+"/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
	}

	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {
		ArrayList<String> consumerBeadList = new ArrayList<String>();
		consumerBeadList.add("SenderInfoBead");
		consumerBeadList.add("SubjectInfoBead");
		consumerBeadList.add("AppInfoBead");
		for(String bead: consumerBeadList){
				try{
					Constructor<?> constructor = Class.forName("MastersProject.BeadRepo."+bead).getConstructor();
					Object myObj = (InformationBead) constructor.newInstance();
					
					Method myObjMethod = myObj.getClass().getMethod("getEvidence", String.class, Date.class, Triplet.class);
					myObjMethod.invoke(myObj, senderId, sentTime, outputData); 
				} catch(Exception e){
					System.out.println("NotificationInfoBead - sendToConsumer - error");
				}
		}
	}

	/**
	 * Called to get sensor evidence or push events.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {}
	
	private void createInfoBeadTriplet(){
		
		Date receivedNotificationDate = new Date();
		Triplet operational = new Triplet();
		operational.setDetectionTime(receivedNotificationDate);
		InfoItemFields information = new InfoItemFields();
		information.setInformationValue(String.valueOf(notificationId));
		information.setInfoAccuracy(100.0);
		information.setInfoConfidenceLevel(100.0);
		information.setInfoValidFrom(receivedNotificationDate);
		operational.setInformationItem(information);
		this.setOperational(operational);
	}
	
	private Triplet createTripletToSend(){
		Triplet triplet = new Triplet();
		
		return triplet;
	}

}
