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

import org.eclipse.persistence.annotations.Multitenant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gdata.util.ServiceException;

import MastersProject.FuzzyLogic.SenderFuzzy;
import MastersProject.FuzzyLogic.SubjectFuzzy;
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
import MastersProject.Utilities.DateUtility;

@Entity
@DiscriminatorValue("Subject")
public class SubjectInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface,
Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1253534945156596424L;

	@Transient
	ArrayList<CalendarEvent> events;
	private List<BeadInputInterface> subjectListeners = new ArrayList<BeadInputInterface>();
	private UpliftedNotification  notification;

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.subjectListeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.subjectListeners.remove(bead);
	}

	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {
		for(BeadInputInterface listener : subjectListeners){
			listener.getEvidence(senderId, sentTime, outputData);
		}
	}

	/**
	 * This will only be fired when information is pushed from the notification 
	 * info bead. This will then activate this bead and run the process. 
	 * TODO: Check the authorized list to ensure the bead can be sent to
	 * TODO: Get the next 5 events (need to send the notification date)
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		System.out.println("Subject");
		ObjectMapper mapper = new ObjectMapper();
		try {
			notification = mapper.readValue(inputData.getInformationItem().getInformationValue(),
					UpliftedNotification.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// get the calendar data for the next 10 events
		try {
			events = GoogleCalendarData.getNextNEvents(10, notification.getDate());
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.run();
	}
	
	@Override
	public void inferInfoBeadAttr() {	
		
		double eventInput = EventInference.getEventImportanceValue("Subject", events, notification);
		
		if(eventInput == 0.0){
			eventInput = 0.00001;
		}
		
		// Mamdami inferrence controller 
		SubjectFuzzy senderFuzzy = new SubjectFuzzy();
		double subjectInput = (double) notification.getSubjectRank()/10.0;
		System.out.println("event relevance = "+ eventInput);
		double inferredValue = senderFuzzy.processSubject(subjectInput, eventInput);
		System.out.println("MastersProject.BeadRepo.SubjectInfoBead: Inferred value = "+inferredValue);
		
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
