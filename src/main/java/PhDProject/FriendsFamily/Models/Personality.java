package PhDProject.FriendsFamily.Models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Score for the Big Five personality test. 
 * 
 * http://fetzer.org/sites/default/files/images/stories/pdf/selfmeasures/Personality-BigFiveInventory.pdf
 * https://www.ocf.berkeley.edu/~johnlab/pdfs/2008chapter.pdf
 * 
 * @author kfraser
 *
 */
@Entity
public class Personality implements Serializable{
	
	private static final long serialVersionUID = 7747304420054930589L;
	
	private int extraversion;
	private int agreeableness;
	private int conscientiousness;
	private int neuroticism;
	private int openness;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	public int getExtraversion() {
		return extraversion;
	}
	public void setExtraversion(int extraversion) {
		this.extraversion = extraversion;
	}
	public int getAgreeableness() {
		return agreeableness;
	}
	public void setAgreeableness(int agreeableness) {
		this.agreeableness = agreeableness;
	}
	public int getConscientiousness() {
		return conscientiousness;
	}
	public void setConscientiousness(int conscientiousness) {
		this.conscientiousness = conscientiousness;
	}
	public int getNeuroticism() {
		return neuroticism;
	}
	public void setNeuroticism(int neuroticism) {
		this.neuroticism = neuroticism;
	}
	public int getOpenness() {
		return openness;
	}
	public void setOpenness(int openness) {
		this.openness = openness;
	}
	@Override
	public String toString() {
		
		String personality = "extraversion: "+extraversion+"\n"+
				"agreeableness: "+agreeableness+"\n"+
				"conscientiousness: "+conscientiousness+"\n"+
				"neuroticism: "+neuroticism+"\n"+
				"openness: "+openness;
		
		return personality;
	}
	
	
}
