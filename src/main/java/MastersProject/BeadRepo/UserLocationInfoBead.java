package MastersProject.BeadRepo;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import PhDProject.Managers.FirebaseManager;

@Entity
@DiscriminatorValue("UserLocation")
public class UserLocationInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface,
Runnable{
	
	private static final long serialVersionUID = 6974917675221221989L;
	
	@Transient
	private UpliftedNotification  notification;
	
	@Transient
	private String userLocation;
	private String calendarLocation;
		
	private List<BeadInputInterface> locationListeners = new ArrayList<BeadInputInterface>();
	
	public UserLocationInfoBead(){
		ArrayList<String> sendToList = new ArrayList<String>();
		sendToList.add("AlertInfoBead");
		this.setAuthorizationToSendToID(sendToList);
	}

	/**
	 * Add a bead which will listen for push requests.
	 * @param addListener
	 */
	public void addListener(BeadInputInterface bead){
		this.locationListeners.add(bead);
	}
	
	/**
	 * Remove a bead from the listening list.
	 * @param bead
	 */
	public void removeListener(BeadInputInterface bead){
		this.locationListeners.remove(bead);
	}

	/**
	 * Called when updates need to be pushed to other beads.
	 */
	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {
		for(BeadInputInterface listener : locationListeners){
			listener.getEvidence(senderId, sentTime, outputData);
		}
	}

	/**
	 * Changed for NAbSim - get Next Event is drawn from the inferred events generated using
	 * the friends & family data-set.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		System.out.println("Location");
		ObjectMapper mapper = new ObjectMapper();
		try {
			notification = mapper.readValue(inputData.getInformationItem().getInformationValue(),
					UpliftedNotification.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Get the location data
		userLocation = App.getUserLocation();
		
		CalendarEvent event = null;
		try {
			event = GoogleCalendarData.getNextEvent(notification.getDate());
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> userDetails = EventInference.getCurrentLocationAndEventName(event, notification);
		calendarLocation = userDetails.get(1);
				
		this.run();
	}
	
	@Override
	public void inferInfoBeadAttr() {	
		double inferredValue = -1.0;
		System.out.println(userLocation);
		System.out.println(calendarLocation);
		if(calendarLocation.contains(userLocation) && !userLocation.contains("unknown")){ // if there's an event occurring and the user is attending it
			inferredValue = 1.0;
		}
		else{ 
			inferredValue = 0.0;
		}
		
		Triplet operational = new Triplet();
		InfoItemFields info = new InfoItemFields();
		info.setInformationValue(String.valueOf(inferredValue));
		operational.setInformationItem(info);
		operational.setDetectionTime(new Date());
		this.setOperational(operational);
	}

	@Override
	public void storeInfoBeadAttr() {
		FirebaseManager.getDatabase().child("BeadRepo/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
	}

	@Override
	public void run() {
		this.activate();		
		inferInfoBeadAttr();
		sendToConsumer(this.getAttributeValueType().toString(), new Date(), this.getOperational());
		storeInfoBeadAttr();
	}

}
