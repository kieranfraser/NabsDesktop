package MastersProject.GUI;

import java.util.Date;

public class DetailTableContent {

	private int notificationId;
	private String sender;
	private String subject;
	private String app;
	private Date date;
	
	public DetailTableContent(int id, String sender, String subject, String app, Date date){
		this.notificationId = id;
		this.sender = sender;
		this.subject = subject;
		this.app = app;
		this.date = date;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
