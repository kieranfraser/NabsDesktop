package MastersProject.BeadRepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import com.fasterxml.jackson.databind.ObjectMapper;

import MastersProject.Interface.BeadInputInterface;
import MastersProject.Interface.BeadOutputInterface;
import MastersProject.Models.InformationBead;
import MastersProject.Models.Triplet;
import MastersProject.Models.UpliftedNotification;

@Entity
@DiscriminatorValue("App")
public class AppInfoBead extends InformationBead implements BeadInputInterface,
BeadOutputInterface, Runnable{

	private static final long serialVersionUID = -8726952894217614500L;
	private List<BeadInputInterface> appListeners = new ArrayList<BeadInputInterface>();
	
	@Transient
	private UpliftedNotification  notification;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

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

	@Override
	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) {
		for(BeadInputInterface listener : appListeners){
			listener.getEvidence(senderId, sentTime, outputData);
		}
		
	}

	@Override
	public void getEvidence(String senderId, Date sentTime, Triplet inputData) {
		System.out.println("App");
		ObjectMapper mapper = new ObjectMapper();
		try {
			notification = mapper.readValue(inputData.getInformationItem().getInformationValue(),
					UpliftedNotification.class);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.run();		
	}

	/**
	 * SL for ranking importance of applications
	 */
	@Override
	public void inferInfoBeadAttr() {
		// TODO Auto-generated method stub
		super.inferInfoBeadAttr();
	}

	@Override
	public void storeInfoBeadAttr() {
		// TODO Auto-generated method stub
		super.storeInfoBeadAttr();
	}
	
	

	
}
