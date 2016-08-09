package MastersProject.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class InfoItemFields implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6628181865586598430L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int infoBeadId; // database purposes
	
	private String informationValue;
	private double infoAccuracy;
	private String infoResolution;
	private double infoConfidenceLevel;
	private String timeUnits;
	private Date infoValidFrom;
	private Date infoExpiration;
	private Date infoInferenceTime;
	private Date timeResolution;
	private Date timeAccuracy;
	
	@ElementCollection
	private ArrayList<Triplet> evidenceData;
	private String evidenceSource;
	private String evidenceStartTime;
	private String evidenceEndTime;
	
	
	public int getInfoBeadId() {
		return infoBeadId;
	}
	public void setInfoBeadId(int infoBeadId) {
		this.infoBeadId = infoBeadId;
	}
	public String getInformationValue() {
		return informationValue;
	}
	public void setInformationValue(String informationValue) {
		this.informationValue = informationValue;
	}
	public double getInfoAccuracy() {
		return infoAccuracy;
	}
	public void setInfoAccuracy(double infoAccuracy) {
		this.infoAccuracy = infoAccuracy;
	}
	public String getInfoResolution() {
		return infoResolution;
	}
	public void setInfoResolution(String infoResolution) {
		this.infoResolution = infoResolution;
	}
	public double getInfoConfidenceLevel() {
		return infoConfidenceLevel;
	}
	public void setInfoConfidenceLevel(double infoConfidenceLevel) {
		this.infoConfidenceLevel = infoConfidenceLevel;
	}
	public String getTimeUnits() {
		return timeUnits;
	}
	public void setTimeUnits(String timeUnits) {
		this.timeUnits = timeUnits;
	}
	public Date getInfoValidFrom() {
		return infoValidFrom;
	}
	public void setInfoValidFrom(Date infoValidFrom) {
		this.infoValidFrom = infoValidFrom;
	}
	public Date getInfoExpiration() {
		return infoExpiration;
	}
	public void setInfoExpiration(Date infoExpiration) {
		this.infoExpiration = infoExpiration;
	}
	public Date getInfoInferenceTime() {
		return infoInferenceTime;
	}
	public void setInfoInferenceTime(Date infoInferenceTime) {
		this.infoInferenceTime = infoInferenceTime;
	}
	public Date getTimeResolution() {
		return timeResolution;
	}
	public void setTimeResolution(Date timeResolution) {
		this.timeResolution = timeResolution;
	}
	public Date getTimeAccuracy() {
		return timeAccuracy;
	}
	public void setTimeAccuracy(Date timeAccuracy) {
		this.timeAccuracy = timeAccuracy;
	}
	public ArrayList<Triplet> getEvidenceData() {
		return evidenceData;
	}
	public void setEvidenceData(ArrayList<Triplet> evidenceData) {
		this.evidenceData = evidenceData;
	}
	public String getEvidenceSource() {
		return evidenceSource;
	}
	public void setEvidenceSource(String evidenceSource) {
		this.evidenceSource = evidenceSource;
	}
	public String getEvidenceStartTime() {
		return evidenceStartTime;
	}
	public void setEvidenceStartTime(String evidenceStartTime) {
		this.evidenceStartTime = evidenceStartTime;
	}
	public String getEvidenceEndTime() {
		return evidenceEndTime;
	}
	public void setEvidenceEndTime(String evidenceEndTime) {
		this.evidenceEndTime = evidenceEndTime;
	}
	
	
}
