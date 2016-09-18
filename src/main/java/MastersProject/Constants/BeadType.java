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
	
	public static BeadType classNameToType(String string){
		switch(string){
		case "AlertInfoBead":
			return BeadType.ALERT;
		case "SenderInfoBead":
			return BeadType.SENDER;
		case "SubjectInfoBead":
			return BeadType.SUBJECT;
		case "UserInfoBead":
			return BeadType.USER;
		case "NotificationInfoBead":
			return BeadType.NOTIFICATION;
		case "UserLocationInfoBead":
			return BeadType.LOCATION;
		case "AppInfoBead":
			return BeadType.APPLICATION;
		case "BodyInfoBead":
			return BeadType.BODY;
		case "DateInfoBead":
			return BeadType.DATE;
		default:
			return null;
		}
	}
}
