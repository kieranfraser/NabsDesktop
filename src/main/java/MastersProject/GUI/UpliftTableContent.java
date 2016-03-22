package MastersProject.GUI;

public class UpliftTableContent {
	
	private String value;
	private Integer rank;
	
	public UpliftTableContent(String value, int rank) {
		this.value = value;
		this.rank = rank;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
	
}
