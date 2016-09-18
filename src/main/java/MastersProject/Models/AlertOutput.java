package MastersProject.Models;

import java.io.Serializable;

public class AlertOutput implements Serializable{
	
	private static final long serialVersionUID = -6045774227814951971L;
		
	private double senderInput;
	private double subjectInput;
	private double appInput;
	private double userLocation;
	
	private String deliveryResult;
	
	public AlertOutput(double senderInput, double subjectInput, double appInput, double userLocation, String deliveryResult){
		this.senderInput = senderInput;
		this.subjectInput = subjectInput;
		this.appInput = appInput;
		this.userLocation = userLocation;
		this.deliveryResult = deliveryResult;
	}
	
	public double getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(double userLocation) {
		this.userLocation = userLocation;
	}

	public double getSenderInput() {
		return senderInput;
	}

	public void setSenderInput(double senderInput) {
		this.senderInput = senderInput;
	}

	public double getSubjectInput() {
		return subjectInput;
	}

	public void setSubjectInput(double subjectInput) {
		this.subjectInput = subjectInput;
	}

	public double getAppInput() {
		return appInput;
	}

	public void setAppInput(double appInput) {
		this.appInput = appInput;
	}

	public String getDeliveryResult() {
		return deliveryResult;
	}

	public void setDeliveryResult(String deliveryResult) {
		this.deliveryResult = deliveryResult;
	}
	
	

}
