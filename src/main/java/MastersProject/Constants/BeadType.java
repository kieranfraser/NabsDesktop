package MastersProject.Constants;

public enum BeadType {
	ALERT,
	SENDER,
	SUBJECT,
	USER, 
	NOTIFICATION,
	LOCATION, 
	APPLICATION,
	BODY,
	DATE;
	
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
		case "LOCATION":
			return BeadType.LOCATION;
		case "APPLICATION":
			return BeadType.APPLICATION;
		case "BODY":
			return BeadType.BODY;
		case "DATE":
			return BeadType.DATE;
		default:
			return null;
		}
	}
}
