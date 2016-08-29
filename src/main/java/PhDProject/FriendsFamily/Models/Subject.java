package PhDProject.FriendsFamily.Models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Class to describe the possible subject which could be chosen for the
 * notification. The subject "uplift" value varies depending on various 
 * aspects such as the sender, the dataset chosen.
 * @author kfraser
 *
 */
@Entity
public class Subject implements Serializable{
	
	private static final long serialVersionUID = 3465057190443401403L;
	
	public final static String WORK = "work";
	public final static String SOCIAL = "social";
	public final static String INTEREST = "interest";
	public final static String FAMILY = "family";

	private String ground_truth;
	private String subject;
	private String dataset;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	public String getGround_truth() {
		return ground_truth;
	}
	public void setGround_truth(String ground_truth) {
		this.ground_truth = ground_truth;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDataset() {
		return dataset;
	}
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	@Override
	public String toString() {
		return "\n\n Ground Truth: "+this.ground_truth+"\n Subject: "+this.subject+"\n Data-set: "+this.dataset+"\n";
	}	
}
