package PhDProject.Managers;

import java.util.ArrayList;

public class ParameterManager {
	
	private static ParameterManager instance;

	public static final String LOW = "LOW";
	public static final String MEDIUM = "MEDIUM";
	public static final String HIGH = "HIGH";
	
	public static final String NOW = "NOW";
	public static final String VERYSOON = "VERYSOON";
	public static final String SOON = "SOON";
	public static final String LATER = "LATER";
	public static final String MUCHLATER = "MUCHLATER";
	
	
	private String[] senderParams = {LOW, MEDIUM, MEDIUM, LOW, MEDIUM, HIGH, MEDIUM, HIGH, HIGH};
	private String[] subjectParams = {LOW, MEDIUM, MEDIUM, MEDIUM, HIGH, HIGH, MEDIUM, HIGH, HIGH};
	private String[] alertParams = {MUCHLATER, MUCHLATER, LATER, MUCHLATER, LATER, SOON, SOON, SOON, VERYSOON,
			LATER, SOON, SOON, SOON, VERYSOON, VERYSOON, SOON, NOW, NOW, VERYSOON, VERYSOON, VERYSOON, VERYSOON, VERYSOON, VERYSOON, NOW, NOW, NOW};
	
	public static synchronized ParameterManager getParamManager(){
		if(instance!=null){
			return instance;
		}
		else{
			instance = new ParameterManager();
			return instance;
		}
	}
	
	public ParameterManager(){}

	public String[] getSenderParams() {
		return senderParams;
	}

	public String[] getSubjectParams() {
		return subjectParams;
	}

	public String[] getAlertParams() {
		return alertParams;
	}

	public void setSenderParams(String[] senderParams) {
		this.senderParams = senderParams;
	}

	public void setSubjectParams(String[] subjectParams) {
		this.subjectParams = subjectParams;
	}

	public void setAlertParams(String[] alertParams) {
		this.alertParams = alertParams;
	}	
	
	public static ArrayList<String> convertBestToParamArray(ArrayList<Integer> gBest){
		ArrayList<String> convertedArray = new ArrayList<String>();
		int position = 0;
		for(int value: gBest){
			switch(value){
			case 1:
				if(position<18){
					convertedArray.add("LOW");
				}
				else{
					convertedArray.add("NOW");
				}
				break;
			case 2:
				if(position<18){
					convertedArray.add("MEDIUM");
				}
				else{
					convertedArray.add("VERYSOON");
				}
				break;
			case 3:
				if(position<18){
					convertedArray.add("HIGH");
				}
				else{
					convertedArray.add("SOON");
				}
				break;
			case 4:
				convertedArray.add("LATER");
				break;
			case 5:
				convertedArray.add("MUCHLATER");
				break;
			}
			position++;
		}
		return convertedArray;
	}
	
	public static String[] getSenderParams(ArrayList<String> gBest){
		ArrayList<String> senderParams = new ArrayList<String>();
		for(int i=0; i<9; i++){
			senderParams.add(gBest.get(i));
		}
		String[] result = new String[senderParams.size()];
		return senderParams.toArray(result);
	}
	
	public static String[] getSubjectParams(ArrayList<String> gBest){
		ArrayList<String> subjectParams = new ArrayList<String>();
		for(int i=9; i<18; i++){
			subjectParams.add(gBest.get(i));
		}
		String[] result = new String[subjectParams.size()];
		return subjectParams.toArray(result);
	}
	
	public static String[] getAlertParams(ArrayList<String> gBest){
		ArrayList<String> alertParams = new ArrayList<String>();
		for(int i=18; i<45; i++){
			alertParams.add(gBest.get(i));
		}
		String[] result = new String[alertParams.size()];
		return alertParams.toArray(result);
	}
	
}