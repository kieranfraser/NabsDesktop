package MastersProject.BeadRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.InfoItemFields;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Models.UpliftedNotification;
import PhDProject.Managers.FirebaseManager;
import PhDProject.Managers.StatisticsManager;

@Entity
@DiscriminatorValue("Notification")
public class NotificationInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface{

	public static final String NAME = "NotificationInfoBead";
	private static final long serialVersionUID = -8451356944207798106L;
	
	private List<BeadInputInterface> listeners = new ArrayList<BeadInputInterface>();
	
	public NotificationInfoBead(){
		ArrayList<String> sendToList = new ArrayList<String>();
		sendToList.add("SenderInfoBead");
		sendToList.add("SubjectInfoBead");
		sendToList.add("AppInfoBead");
		sendToList.add("UserLocationInfoBead");
		this.setAuthorizationToSendToID(sendToList);
	}
	
	public void notificationReceived(){
		FirebaseManager.getDatabase().child("CurrentNotification/").addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				System.out.println("Subscribe to current notification");
				if(snapshot.getValue()!=null){
					UpliftedNotification notification = snapshot.getValue(UpliftedNotification.class);
					Date receivedNotificationDate = new Date();
					activate();
					Triplet operational = new Triplet();
					operational.setDetectionTime(receivedNotificationDate);
					
					InfoItemFields information = new InfoItemFields();
					ObjectMapper mapper = new ObjectMapper();
					String notificationString = null;
					try {
						 notificationString = mapper.writeValueAsString(notification);
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					information.setInformationValue(notificationString);
					information.setInfoAccuracy(100.0);
					information.setInfoConfidenceLevel(100.0);
					information.setInfoValidFrom(receivedNotificationDate);
					information.setInfoBeadId(notification.getNotificationId());
					
					operational.setInformationItem(information);
					setOperational(operational);
					
					// Update the bead in database
					storeInfoBeadAttr();
					
					// push triplet to listening beads - sender, subject
					sendToConsumer(getId(), receivedNotificationDate, operational);
				}	  			
			}
			@Override public void onCancelled(FirebaseError error) { }
  		});
	}
	
	public void notificationToCompute(UpliftedNotification notification){
		Date receivedNotificationDate = new Date();
		activate();
		Triplet operational = new Triplet();
		operational.setDetectionTime(receivedNotificationDate);
		
		InfoItemFields information = new InfoItemFields();
		ObjectMapper mapper = new ObjectMapper();
		String notificationString = null;
		try {
			 notificationString = mapper.writeValueAsString(notification);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		information.setInformationValue(notificationString);
		information.setInfoAccuracy(100.0);
		information.setInfoConfidenceLevel(100.0);
		information.setInfoValidFrom(receivedNotificationDate);
		information.setInfoBeadId(notification.getNotificationId());
		
		operational.setInformationItem(information);
		setOperational(operational);
		
		// For experimental purposes - need the time/date of current notification
		StatisticsManager.getStatsManager().setCurrentNotification(notification);
		
		// Update the bead in database
		storeInfoBeadAttr();
		
		// push triplet to listening beads - sender, subject
		sendToConsumer(getId(), receivedNotificationDate, operational);
	}

	
	@Override
	public void storeInfoBeadAttr() {
		FirebaseManager.getDatabase().child("BeadRepo/"+
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
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {
		for(BeadInputInterface listener : listeners){
			listener.getEvidence(senderId, sentTime, outputData);
		}
	}

	/**
	 * Called to get sensor evidence or push events.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {}

}
