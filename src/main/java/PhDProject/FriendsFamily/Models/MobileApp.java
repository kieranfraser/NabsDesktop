package PhDProject.FriendsFamily.Models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * App class which describes an application used by a receiver of
 * a notification.
 * @author kfraser
 *
 */
@Entity
public class MobileApp implements Serializable{
	
	private static final long serialVersionUID = 8221787023554314446L;
	
	public final static String GAMES = "Games";
	public final static String NEWS_AND_WEATHER = "News and weather";
	public final static String ENTERTAINMENT = "Entertainment";
	public final static String COMMUNICATION = "Communication";
	public final static String SOCIAL = "Social";
	public final static String OTHER = "Other";
	public final static String LIFESTYLE = "Lifestyle";
	public final static String PRODUCTIVITY = "Productivity and Tools";
	public final static String PHONE_PERSONALIZATION = "Phone Personalization";
	public final static String SHOPPING = "Shopping";
	
	private String name;
	private String category;
	private int rank;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	@Override
	public String toString() {
		return "\n Name: "+this.name+"\n Cat: "+this.category+"\n Rank: "+this.rank+"\n";
	}
	
	
}
