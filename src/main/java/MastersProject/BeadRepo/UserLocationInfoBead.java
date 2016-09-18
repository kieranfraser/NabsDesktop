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
import MastersProject.Utilities.FirebaseManager;

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
					System.out.println("UserLocationInfoBead - sendToConsumer - error");
				}
		}
	}

	/**
	 * Changed for NAbSim - get Next Event is drawn from the inferred events generated using
	 * the friends & family data-set.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		this.setAttributeValueType(BeadType.LOCATION);
		
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
	  					
	  			run();
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) { }
  		});
		
		
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
		FirebaseManager.getDatabase().child("InfoBead/"+
				this.notification.getNotificationId()+"/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
	}

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
