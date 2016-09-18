package MastersProject.BeadRepo;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
import MastersProject.Utilities.FirebaseManager;

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
		ArrayList<String> consumerBeadList = new ArrayList<String>();
		consumerBeadList.add("AlertInfoBead");
		for(String bead: consumerBeadList){
  				try{
					Constructor<?> constructor = Class.forName("MastersProject.BeadRepo."+bead).getConstructor();
					Object myObj = (InformationBead) constructor.newInstance();
					
					Method myObjMethod = myObj.getClass().getMethod("getEvidence", String.class, Date.class, Triplet.class);
					myObjMethod.invoke(myObj, senderId, sentTime, outputData); 
				} catch(Exception e){
					System.out.println("SenderInfoBead - sendToConsumer - error");
				}			
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
		this.setAttributeValueType(BeadType.SENDER);
		
		int notificationId = Integer.valueOf(inputData.getInformationItem().getInformationValue());
		
		FirebaseManager.getDatabase().child("InfoBead/"+
				notificationId+"/"+
				senderId+"/operational/informationItem/informationValue/").addValueEventListener(new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {
	  			System.out.println("kieran "+snapshot.getValue(String.class));
	  			try {
					notification = FirebaseManager.convertStringToNotification((String) snapshot.getValue());
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
		FirebaseManager.getDatabase().child("InfoBead/"+
				this.notification.getNotificationId()+"/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
	}

	/**
	 * Called once the bead has been activated (data has been pushed to it)
	 */
	@Override
	public void run() {		
		this.activate();		
		inferInfoBeadAttr();
		storeInfoBeadAttr();
		sendToConsumer(this.getAttributeValueType().toString(), new Date(), createTripletToSend());
	}
	
	private Triplet createTripletToSend(){
		Triplet triplet = new Triplet();
		InfoItemFields information = new InfoItemFields();
		information.setEvidenceSource(String.valueOf(notification.getNotificationId()));
		information.setInformationValue(String.valueOf(notification.getNotificationId()));
		triplet.setInformationItem(information);
		return triplet;
	}

}
