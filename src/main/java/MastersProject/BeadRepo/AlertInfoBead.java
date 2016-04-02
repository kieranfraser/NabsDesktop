package MastersProject.BeadRepo;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Transient;

import MastersProject.Controllers.NabsDesktopController;
import MastersProject.FuzzyLogic.AlertFuzzy;
import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Nabs.App;
import MastersProject.Utilities.DateUtility;

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
		
		System.out.println("Sender input in alert bead: "+senderInput);
		System.out.println("Subject input in alert bead: "+subjectInput);
		System.out.println("Application input in alert bead: "+appInput);
		System.out.println("Location input in alert bead: "+userLocation);
		
		// Mamdami inferrence controller 
		AlertFuzzy alertFuzzy = new AlertFuzzy();
		double inferredValue = alertFuzzy.processalert(senderInput, subjectInput, appInput);
		System.out.println("Inferred minutes: "+inferredValue);
		String result = "Fire Notification "+this.getPartNumber()+" in: "+DateUtility.cleanMinutes(inferredValue)+"\n";
		
		if(inferredValue<30.0){ //now - interrupt
			result = result + "Notify now"+"\n";
		}else if(inferredValue<120){ // verysoon - next break
			if(userLocation == 0.0){
				result = result + "at next break"+"\n";
			}
			else{
				result = result + "Notify now"+"\n";
			}
		}else if(inferredValue<400){ // soon - next free period
			if(userLocation == 0.0){
				result = result + "Notify next free period"+"\n";
			}
			else{
				result = result + "Notify now"+"\n";
			}
		}else{ // Later & Much Later
			result = result + "Notify next contextual relevant event"+"\n";
		}
		System.out.println(result);
		App.result = result;
	}

	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {}

	/**
	 * The triplets received are from the sender and subject beads.
	 */
	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		
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
	 */
	@Override
	public void storeInfoBeadAttr() {
		// TODO Auto-generated method stub
		super.storeInfoBeadAttr();
		EntityManager em = App.getEntityManager();
    	em.getTransaction().begin();
    	em.persist(this);
		em.getTransaction().commit();
	}

	@Override
	public void run() {
		inferInfoBeadAttr();
		storeInfoBeadAttr();		
	}
	
	

}
