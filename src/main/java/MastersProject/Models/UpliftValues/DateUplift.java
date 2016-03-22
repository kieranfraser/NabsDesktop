package MastersProject.Models.UpliftValues;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;

import MastersProject.GUI.UpliftTableContent;
import MastersProject.Nabs.App;

@Entity
public class DateUplift {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String value;
	private int rank;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	/**
	 * Get all Date Uplift values
	 * @return a list of date uplift values
	 */
	public static List<DateUplift> getUpliftValues(){
		EntityManager em = App.getEntityManager();
		Query query = em.createQuery("Select t FROM DateUplift t");
		List<DateUplift> result = query.getResultList();
		return result;
	}
	
	/**
	 * Update all Date Uplift rank values
	 * @param newUplift
	 */
	public static void updateUpliftValues(List<UpliftTableContent> newUplift){
		EntityManager em = App.getEntityManager();
		em.getTransaction().begin();
		for(UpliftTableContent upliftValue : newUplift){
			Query query = em.createQuery("UPDATE DateUplift t SET t.rank = "+upliftValue.getRank()+" WHERE t.value = '"+upliftValue.getValue()+"'");
			query.executeUpdate();
		}
		em.getTransaction().commit();
		App.closeEntityManager();
	}
}
