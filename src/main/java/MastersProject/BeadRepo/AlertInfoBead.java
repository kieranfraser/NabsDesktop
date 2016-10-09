package MastersProject.BeadRepo;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.firebase.client.Firebase;

import MastersProject.FuzzyLogic.AlertFuzzy;
import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.InfoItemFields;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Nabs.App;
import PhDProject.FriendsFamily.Models.Result;
import PhDProject.Managers.FirebaseManager;

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
	
	/**
	 * Used for identifying the results in NabSim
	 */
	private int notificationIdPath;

	/**
	 * Must be initialized to ~0 for fuzzy controller.
	 */
	public AlertInfoBead() {
		this.senderInput = 0.0001;
		this.subjectInput = 0.0001;
		this.appInput = 0.0001;
	}
	
	@Override
	public void inferInfoBeadAttr() {
		super.inferInfoBeadAttr();
		
		
		// Mamdami inferrence controller 
		AlertFuzzy alertFuzzy = new AlertFuzzy();
		double inferredValue = alertFuzzy.processalert(senderInput, subjectInput, appInput);
		
		//String result = "Receive Notification "+this.getPartNumber()+" in: "+DateUtility.cleanMinutes(inferredValue)+"\n";
		
		String result = "";
		
		
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
		//System.out.println(result);
		//App.resultCallback.resultCallback(Integer.valueOf(this.getPartNumber()), result);
		Triplet triplet = new Triplet();
		InfoItemFields infoItem = new InfoItemFields();
		infoItem.setInformationValue(result);
		triplet.setInformationItem(infoItem);
		setOperational(triplet);
	}

	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {}

	/**
	 * The triplets received are from the sender and subject beads.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		notificationIdPath = inputData.getInformationItem().getInfoBeadId();
		switch(senderId){
		case "SUBJECT":
			this.subjectInput = Double.valueOf(inputData.getInformationItem().getInformationValue());
			break;
		case "SENDER":
			this.senderInput = Double.valueOf(inputData.getInformationItem().getInformationValue());
			break;
		case "APPLICATION":
			this.appInput = Double.valueOf(inputData.getInformationItem().getInformationValue());
		case "LOCATION":
			this.userLocation = Double.valueOf(inputData.getInformationItem().getInformationValue());
		}
		this.run();
	}

	/**
	 * In this store - close the entity manager. Must ensure to open it again for the next notification.
	 * em.close();
	 * Push the value to a FireBase reference - for comparison of events in NabSim.
	 */
	@Override
	public void storeInfoBeadAttr() {
		FirebaseManager.getDatabase().child("BeadRepo/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
		
		FirebaseManager.getDatabase().child("web/results/"+notificationIdPath).
		setValue(new Result(notificationIdPath, this.getOperational().getInformationItem().getInformationValue()));
	}

	@Override
	public void run() {
		inferInfoBeadAttr();
		storeInfoBeadAttr();		
	}
	
	

}
