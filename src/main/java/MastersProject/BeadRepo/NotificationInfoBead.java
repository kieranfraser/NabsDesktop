package MastersProject.BeadRepo;

import java.util.ArrayList;
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

@Entity
@DiscriminatorValue("Notification")
public class NotificationInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface{

	private static final long serialVersionUID = -8451356944207798106L;
	
	private List<BeadInputInterface> listeners = new ArrayList<BeadInputInterface>();
	
	public void notificationReceived(UpliftedNotification notification){
		
		Date receivedNotificationDate = new Date();
		this.activate();
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
		
		operational.setInformationItem(information);
		this.setOperational(operational);
		
		// Update the bead in database
		storeInfoBeadAttr();
		
		// push triplet to listening beads - sender, subject
		sendToConsumer(this.getId(), receivedNotificationDate, operational);
	}

	
	@Override
	public void storeInfoBeadAttr() {
		super.storeInfoBeadAttr();
		EntityManager em = App.getEntityManager();
    	em.getTransaction().begin();
    	em.persist(this);
		em.getTransaction().commit();
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
