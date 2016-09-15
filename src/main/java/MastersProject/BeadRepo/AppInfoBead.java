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

import MastersProject.Constants.BeadType;
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

@Entity
@DiscriminatorValue("App")
public class AppInfoBead extends InformationBead implements BeadInputInterface,
BeadOutputInterface, Runnable{

	private static final long serialVersionUID = -8726952894217614500L;
	
	@Transient
	ArrayList<CalendarEvent> events;
	private UpliftedNotification  notification;
	private List<BeadInputInterface> appListeners = new ArrayList<BeadInputInterface>();

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.appListeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.appListeners.remove(bead);
	}
	
	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {
		for(BeadInputInterface listener : appListeners){
			listener.getEvidence(senderId, sentTime, outputData);
		}
		
	}

	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		System.out.println("App");

    	this.setAttributeValueType(BeadType.APPLICATION);
    	
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

	/**
	 * SL for ranking importance of applications
	 */
	@Override
	public void inferInfoBeadAttr() {
				
		double inferredValue = (double) notification.getAppRank()/10.0;
		System.out.println("MastersProject.BeadRepo.AppInfoBead: Inferred value = "+inferredValue);
		
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
