package MastersProject.BeadRepo;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gdata.util.ServiceException;

import MastersProject.Constants.BeadType;
import MastersProject.FuzzyLogic.SenderFuzzy;
import MastersProject.GoogleData.CalendarEvent;
import MastersProject.GoogleData.GoogleCalendarData;
import MastersProject.Inference.EventInference;
import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.InfoItemFields;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Nabs.App;

@Entity
@DiscriminatorValue("Sender")
public class SenderInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface,
Runnable{
	
	private static final long serialVersionUID = -8514150933203047825L;

	@Transient
	ArrayList<CalendarEvent> events;
	
	private UpliftedNotification  notification;

	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {
		
	}

	/**
	 * This will only be fired when information is pushed from the notification 
	 * info bead. This will then activate this bead and run the process. 
	 * TODO: Check the authorized list to ensure the bead can be sent to
	 * TODO: Get the next 5 events (need to send the notification date)
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		this.setAttributeValueType(BeadType.SENDER);
		
		final ObjectMapper mapper = new ObjectMapper();		

		try {
			notificationId = mapper.readValue(inputData.getId(),
					String.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	FirebaseHelper.getDatabase().child("InfoBead/"+
				notificationId+"/"+
				"NOTIFICATION/operational/informationItem/informationValue/").addValueEventListener(new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			System.out.println("kieran "+snapshot.getValue(String.class));
	  			try {
					notification = FirebaseHelper.convertStringToNotification((String) snapshot.getValue());
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	  		    
	  		// get the calendar data for the next 10 events
	  			try {
	  				events = GoogleCalendarData.getNextNEvents(10, notification.getDate());
	  			} catch (IOException |  ParseException e) {
	  				// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			}
	  			
	  			run();
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) { }
  		});
	}
	
	/**
	 * Infer whether the notification sender is important based on event and sender importance.
	 * The importance of the event is dependent on whether it matches with the event "sender"
	 * and is also based on how close the event is to the current time. Get the next 10 events.
	 */
	@Override
	public void inferInfoBeadAttr() {
		
		double eventInput = EventInference.getEventImportanceValue("Sender", events, notification);

		if(eventInput == 0.0){
			eventInput = 0.00001;
		}
		
		// Mamdami inferrence controller 
		SenderFuzzy senderFuzzy = new SenderFuzzy();
		double senderInput = (double) notification.getSenderRank()/10.0;
		System.out.println("SenderInput: "+senderInput);
		System.out.println("EventInput: "+eventInput);
		double inferredValue = senderFuzzy.processSender(senderInput, eventInput);
		System.out.println("MastersProject.BeadRepo.SenderInfoBead: Inferred value = "+inferredValue);
		
		Triplet operational = new Triplet();
		InfoItemFields info = new InfoItemFields();
		info.setInformationValue(String.valueOf(inferredValue));
		operational.setInformationItem(info);
		operational.setDetectionTime(new Date());
		this.setOperational(operational);
	}

	@Override
	public void storeInfoBeadAttr() {
		EntityManager em = App.getEntityManager();
    	em.getTransaction().begin();
    	em.persist(this);
		em.getTransaction().commit();
	}

	/**
	 * Called once the bead has been activated (data has been pushed to it)
	 */
	@Override
	public void run() {		
		this.activate();		
		inferInfoBeadAttr();
		sendToConsumer(this.getAttributeValueType().toString(), new Date(), this.getOperational());
		storeInfoBeadAttr();
	}

}
