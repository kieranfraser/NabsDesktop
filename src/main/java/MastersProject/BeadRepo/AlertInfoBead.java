package MastersProject.BeadRepo;

import java.io.IOException;
import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import MastersProject.Constants.BeadType;
import MastersProject.FuzzyLogic.AlertFuzzy;
import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.AlertOutput;
import MastersProject.Models.InfoItemFields;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Nabs.App;
import MastersProject.Utilities.FirebaseManager;

@Entity
@DiscriminatorValue("Alert")
public class AlertInfoBead extends InformationBead implements BeadInputInterface, BeadOutputInterface,
Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4795515184165812011L;

	@Transient
	private double senderInput;
	
	@Transient
	private double subjectInput;
	
	@Transient
	private double appInput;
	
	@Transient
	private double userLocation;
	
	@Transient
	private int notificationId;
	
	@Transient
	private String result;
	
	@Transient
	private boolean finished;
	
	@Transient
	private AlertOutput alert;
	
	/**
	 * Must be initialized to ~0 for fuzzy controller.
	 */
	public AlertInfoBead() {
		result = null;
		this.senderInput = 0.0001;
		this.subjectInput = 0.0001;
		this.appInput = 0.0001;
		this.finished = false;
	}
	
	@Override
	public void inferInfoBeadAttr() {
		
		System.out.println("Sender input in alert bead: "+senderInput);
		System.out.println("Subject input in alert bead: "+subjectInput);
		System.out.println("Application input in alert bead: "+appInput);
		System.out.println("Location input in alert bead: "+userLocation);
		
		// Mamdami inferrence controller 
		AlertFuzzy alertFuzzy = new AlertFuzzy();
		double inferredValue = alertFuzzy.processalert(senderInput, subjectInput, appInput);
		System.out.println("Inferred minutes: "+inferredValue);
		//String result = "Receive Notification "+this.getPartNumber()+" in: "+DateUtility.cleanMinutes(inferredValue)+"\n";
		
		result = "Receive Notification "+this.getPartNumber()+" at: \n";
		
		
		// now - interrupt
		if(inferredValue<5.0){ 
			result = result + "Notify now "+this.getPartNumber()+"\n";
		
		// verysoon - next break
		}else if(inferredValue<15){ 
			
			if(userLocation == 1.0){ // if there's an event on
				result = result + "at next break - "+App.getNextBreak()+" - "+this.getPartNumber()+"\n";
				
			}
			else{
				result = result + "Notify now "+this.getPartNumber()+"\n";
			}
		
		// soon - next free period
		}else if(inferredValue<40){ 
			
			
			if(userLocation == 1.0){
				result = result + "Notify next free period - "+App.getNextFreePeriod()+" - "+this.getPartNumber()+"\n";
			}
			else{
				result = result + "Notify now "+this.getPartNumber()+"\n";
			}
			
		// Later & Much Later	
		}else if(inferredValue<60){ 
			result = result + "Notify  Later-  next contextual relevant event - "+App.getNextContextRelevant()+" - "+"\n";
		}
		else{
			result = result + "Notify Much Later - next contextual relevant event - "+App.getNextContextRelevant()+" - "+"\n";
		}
		
		createInfoBeadTriplet();
		//System.out.println(result);
		//App.resultCallback.resultCallback(Integer.valueOf(this.getPartNumber()), result);
		
	}

	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {}

	/**
	 * The triplets received are from the sender and subject beads.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		
		notificationId = Integer.valueOf(inputData.getInformationItem().getEvidenceSource());
		
		FirebaseManager.getDatabase().child("InfoBead/"+
				notificationId+"/"+
				senderId+"/operational/informationItem/informationValue/").addValueEventListener(new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {

	  			double value = 0.0001;
	  			value = snapshot.getValue(Double.class);

	  			getPersistedData(senderId, value);
	  			
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) { }
  		});
	}

	/**
	 * In this store - close the entity manager. Must ensure to open it again for the next notification.
	 * em.close();
	 */
	@Override
	public void storeInfoBeadAttr() {
		this.setAttributeValueType(BeadType.ALERT);
		FirebaseManager.getDatabase().child("InfoBead/"+
				notificationId+"/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
	}

	@Override
	public void run() {
		inferInfoBeadAttr();

		checkNoChange(alert);
		if(!finished){
			storeInfoBeadAttr();		
		}
	}
	
	private void createInfoBeadTriplet(){
		Triplet triplet = new Triplet();
		InfoItemFields information = new InfoItemFields();
		try {
			information.setInformationValue(FirebaseManager.
					convertAlertOutputToString(new AlertOutput(senderInput, subjectInput, appInput, userLocation, result)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		triplet.setInformationItem(information);
		this.setOperational(triplet);
	}
	
	private void getPersistedData(String senderId, double newValue){
		System.out.println("\n Persisted:\n"+notificationId+"\n");
		FirebaseManager.getDatabase().child("InfoBead/"+
				notificationId+"/"+
				"ALERT/operational/informationItem/informationValue/").addValueEventListener(new ValueEventListener() {
	  		  @Override
	  		  public void onDataChange(DataSnapshot snapshot) {

	  			  alert = null;
	  			  if(snapshot.getValue() != null){
			  			try {
			  				alert = FirebaseManager.convertStringToAlertOutput((String) snapshot.getValue());
			  				
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

			  			
			  			if(alert!= null){
			  				System.out.println("setting values");
			  				senderInput = alert.getSenderInput();
			  				subjectInput = alert.getSubjectInput();
			  				appInput = alert.getAppInput();
			  				userLocation = alert.getUserLocation();
			  			}
	  			  }

		  			switch(senderId){
		  			case "SUBJECT":
		  				subjectInput = newValue;
		  				break;
		  			case "SENDER":
		  				senderInput = newValue;
		  				break;
		  			case "APPLICATION":
		  				appInput = newValue;
		  			case "LOCATION":
		  				userLocation = newValue;
		  			}
	  			  run();
	  		  }
	  		  @Override public void onCancelled(FirebaseError error) { }
  		});
	}
	
	private void checkNoChange(AlertOutput alert){
		if(this.result == alert.getDeliveryResult()){
			
			finished = true;
		}
	}

}
