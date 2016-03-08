package MastersProject.Constants;

public enum ActivationType {
	ON,
	OFF;
	
	public static ActivationType stringToAct(String string){
		switch(string){
		case "ON":
			return ActivationType.ON;
		case "OFF":
			return ActivationType.OFF;
		default:
			return null;
		}
	}
	
}
