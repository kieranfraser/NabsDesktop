package MastersProject.Nabs;

import javax.persistence.*;

@Entity
public class Kieran {

	@Id
	private int one;
	
	private String two;

	public int getOne() {
		return one;
	}

	public void setOne(int one) {
		this.one = one;
	}

	public String getTwo() {
		return two;
	}

	public void setTwo(String two) {
		this.two = two;
	}
	
	
}
