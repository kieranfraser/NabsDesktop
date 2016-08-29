package MastersProject.Models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import MastersProject.Nabs.App;

public class UpliftedNotification implements Serializable{
	
	private static final long serialVersionUID = -1790527382364136594L;
	
	private int notificationId;
	private String sender;
	private String app;
	private String subject;
	private String body;
	private Date date;
	private String dateImportance;
	
	private int senderRank;
	private int appRank;
	private int subjectRank;
	private int bodyRank;
	private int dateRank;
	
	public int getSenderRank() {
		return senderRank;
	}
	public void setSenderRank(int senderRank) {
		this.senderRank = senderRank;
	}
	public int getAppRank() {
		return appRank;
	}
	public void setAppRank(int appRank) {
		this.appRank = appRank;
	}
	public int getSubjectRank() {
		return subjectRank;
	}
	public void setSubjectRank(int subjectRank) {
		this.subjectRank = subjectRank;
	}
	public int getBodyRank() {
		return bodyRank;
	}
	public void setBodyRank(int bodyRank) {
		this.bodyRank = bodyRank;
	}
	public int getDateRank() {
		return dateRank;
	}
	public void setDateRank(int dateRank) {
		this.dateRank = dateRank;
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
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDateImportance() {
		return dateImportance;
	}
	public void setDateImportance(String dateImportance) {
		this.dateImportance = dateImportance;
	}
	
	public static int getRank(String table, String value){
		EntityManager em = App.getEntityManager();
		Query q = em.createQuery("select t.rank from "+table+" t where t.value = '"+value+"'");
		int result = (int) q.getSingleResult();
		return result;
	}	
}
