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
import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.InfoItemFields;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Utilities.FirebaseManager;

@Entity
@DiscriminatorValue("App")
public class AppInfoBead extends InformationBead implements BeadInputInterface,
BeadOutputInterface, Runnable{

	private static final long serialVersionUID = -8726952894217614500L;
	
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
					System.out.println("AppInfoBead - sendToConsumer - error");
				}
		}
	}

	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		this.setAttributeValueType(BeadType.APPLICATION);
		
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
	  			} catch (IOException | ParseException e) {
	  				// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			}
	  					
	  			run();
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) { }
  		});
				
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
