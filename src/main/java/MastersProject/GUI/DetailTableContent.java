package MastersProject.GUI;

public class DetailTableContent {

	private int notificationId;
	private String sender;
	private String subject;
	private String app;
	
	public DetailTableContent(int id, String sender, String subject, String app){
		this.notificationId = id;
		this.sender = sender;
		this.subject = subject;
		this.app = app;
	}
	
	public int getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
}
