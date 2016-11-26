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
import PhDProject.Managers.StatisticsManager;

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
			result = result + "1 "+"\n";
		
		// verysoon - next break
		}else if(inferredValue<15){ 
			
			if(userLocation == 1.0){ // if there's an event on
				result = result + "2"+"\n";
				
			}
			else{
				result = result + "1"+"\n";
			}
		
		// soon - next free period
		}else if(inferredValue<40){ 
			
			
			if(userLocation == 1.0){
				result = result + "3"+"\n";
			}
			else{
				result = result + "1 "+"\n";
			}
			
		// Later & Much Later	
		}else if(inferredValue<60){ 
			result = result + "4"+"\n";
		}
		else{
			result = result + "5"+"\n";
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
	 * Changed for experiment 1 whereby the results are being stored in firebase under individual paramId, userId
	 * and 
	 */
	@Override
	public void storeInfoBeadAttr() {
		FirebaseManager.getDatabase().child("BeadRepo/"+
				this.getAttributeValueType()+"/").setValue((InformationBead) this);
		
		FirebaseManager.getDatabase().child("web/results/"+notificationIdPath).
		setValue(new Result(notificationIdPath, this.getOperational().getInformationItem().getInformationValue()));
		
		
		/*FirebaseManager.getDatabase().child("Exp1/"+App.getCurrentParamId()+"/"+
				App.getCurrentUserId()+"/"+notificationIdPath).
		setValue(this.getOperational().getInformationItem().getInformationValue());*/
		
		//StatisticsManager.getStatsManager().updateStats(this.getOperational().getInformationItem().getInformationValue());
	}

	@Override
	public void run() {
		inferInfoBeadAttr();
		storeInfoBeadAttr();		
	}
	
	

}
