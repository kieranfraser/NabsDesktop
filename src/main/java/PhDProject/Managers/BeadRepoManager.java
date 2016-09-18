package PhDProject.Managers;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import MastersProject.BeadRepo.NotificationInfoBead;
import MastersProject.Constants.ActivationType;
import MastersProject.Constants.BeadType;
import MastersProject.Interface.BeadInputInterface;
import MastersProject.Models.InformationBead;

public class BeadRepoManager {
	
	private final String BEAD_REPO_PACKAGE = "MastersProject.BeadRepo";

	private List<String> allInfoBeads;
	private Map<String, Boolean> activeInfoBeads;
	private Map<String, Object> activeBeadObjects;
	
	public BeadRepoManager(){
		Reflections reflections = new Reflections(BEAD_REPO_PACKAGE);
		allInfoBeads = new ArrayList<String>();
		activeInfoBeads = new HashMap<String, Boolean>();
		activeBeadObjects = new HashMap<String, Object>();

		 Set<Class<? extends InformationBead>> allClasses = 
		     reflections.getSubTypesOf(InformationBead.class);
		 
		 for (Class<? extends InformationBead> c : allClasses) {
			 String bead = c.getSimpleName();
			 allInfoBeads.add(bead);
			 activeInfoBeads.put(bead, false);
		}
	}
	
	public void activateBead(String beadName){
		if(activeInfoBeads.get(beadName)!=null){
			activeInfoBeads.put(beadName, true);
			System.out.println(beadName+" activated.");
		} else {
			System.out.println("Bead ("+beadName+") not found in repo.");
		}
	}
	
	public void initialize(){
		activeInfoBeads.forEach((bead,active)->{
			System.out.println("Bead : " + bead + " Active : " + active);
			
			if(active){
				try{
					Constructor<?> constructor = Class.forName(BEAD_REPO_PACKAGE+"."+bead).getConstructor();
					Object beadObject = (InformationBead) constructor.newInstance();
					
					Method setAttributeValueType = beadObject.getClass().getMethod("setAttributeValueType", BeadType.class);
					setAttributeValueType.invoke(beadObject, BeadType.classNameToType(bead)); 
					
					Method setName = beadObject.getClass().getMethod("setSingleAttributeName", String.class);
					setName.invoke(beadObject, bead); 
					
					Method setActive = beadObject.getClass().getMethod("setOnOff", ActivationType.class);
					setActive.invoke(beadObject, ActivationType.ON); 
					
					Method setVersion = beadObject.getClass().getMethod("setVersion", String.class);
					setVersion.invoke(beadObject, "1"); 	

					activeBeadObjects.put(bead, beadObject);
					
				} catch(Exception e){
					System.out.println("Initialization error: "+bead);
					e.printStackTrace();
				}
			}
			
		});
		connectBeads();
	}
	
	private void connectBeads(){
		activeBeadObjects.forEach((name, bead)->{
			try{
				Method getAuthorizedListeners = bead.getClass().getMethod("getAuthorizationToSendToID", null);
				ArrayList<String> authorizedListeners = (ArrayList<String>) getAuthorizedListeners.invoke(bead, null);	
				
				for(String beadName: authorizedListeners){
					Object listenerBead = activeBeadObjects.get(beadName);
					if(listenerBead != null){
						Method addListener = bead.getClass().getMethod("addListener", BeadInputInterface.class);
						addListener.invoke(bead, (BeadInputInterface) listenerBead);
					}
				}
				
			} catch(Exception e){
				System.out.println("Connect bead error: "+bead);
			}
		});
	}
	
	public void saveRepoInstance(){
		LocalDateTime timestamp = LocalDateTime.now();
		ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
		long epoch = timestamp.atZone(zoneId).toEpochSecond();
		
		activeBeadObjects.forEach((name, bead)->{
			BeadType beadType = null;
			try{
				Method getBeadType = bead.getClass().getMethod("getAttributeValueType", null);
				beadType = (BeadType) getBeadType.invoke(bead, null);	
				
			} catch(Exception e){
				System.out.println("Save bead error: "+name);
			}
			if(beadType!=null){
				FirebaseManager.getDatabase().child("BeadRepoInstance/"+epoch+"/"+
						beadType.toString()+"/").setValue((InformationBead) bead);
			}
		});		
	}
	
	public void activateNotificationListener(){
		if(activeBeadObjects.get(NotificationInfoBead.NAME)!=null){
			NotificationInfoBead bead = (NotificationInfoBead) activeBeadObjects.get(NotificationInfoBead.NAME);
			bead.notificationReceived();
		}
	}
}
