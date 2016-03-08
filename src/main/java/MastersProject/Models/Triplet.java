package MastersProject.Models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Triplet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4554567158991473317L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	private Date detectionTime;
	
	@OneToOne(cascade = CascadeType.ALL)
	private InfoItemFields informationItem;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getDetectionTime() {
		return detectionTime;
	}

	public void setDetectionTime(Date detectionTime) {
		this.detectionTime = detectionTime;
	}

	public InfoItemFields getInformationItem() {
		return informationItem;
	}

	public void setInformationItem(InfoItemFields informationItem) {
		this.informationItem = informationItem;
	}
	
	
}
