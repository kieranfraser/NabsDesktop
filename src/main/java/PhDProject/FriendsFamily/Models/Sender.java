package PhDProject.FriendsFamily.Models;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Sender implements Serializable{
	
	private static final long serialVersionUID = 6347777367001306674L;
	
	/**
	 * Sender identities
	 */
	public final static String STRANGER = "stranger";
	public final static String FRIEND = "friend";
	public final static String COLLEAGUE = "colleague";
	public final static String FAMILY = "family";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	private String identity;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	
	
}
