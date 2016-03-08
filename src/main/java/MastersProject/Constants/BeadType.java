package MastersProject.Constants;

public enum BeadType {
	ALERT,
	SENDER,
	SUBJECT,
	USER, 
	NOTIFICATION;
	
	public static BeadType stringToType(String string){
		switch(string){
		case "ALERT":
			return BeadType.ALERT;
		case "SENDER":
			return BeadType.SENDER;
		case "SUBJECT":
			return BeadType.SUBJECT;
		case "USER":
			return BeadType.USER;
		case "NOTIFICATION":
			return BeadType.NOTIFICATION;
		default:
			return null;
		}
	}
}
