package MastersProject.Models;

/**
 * Object stored in the AlertInfoBead informationItemField
 * 
 * @author Kieran
 *
 */
public class AlertOutput {
	
	private double sender;
	private double subject;
	private double app;
	private double userLocation;
	
	private String notificationResult;

	public double getSender() {
		return sender;
	}

	public void setSender(double sender) {
		this.sender = sender;
	}

	public double getSubject() {
		return subject;
	}

	public void setSubject(double subject) {
		this.subject = subject;
	}

	public double getApp() {
		return app;
	}

	public void setApp(double app) {
		this.app = app;
	}

	public double getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(double userLocation) {
		this.userLocation = userLocation;
	}

	public String getNotificationResult() {
		return notificationResult;
	}

	public void setNotificationResult(String notificationResult) {
		this.notificationResult = notificationResult;
	}
}
