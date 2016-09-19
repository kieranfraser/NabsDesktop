package PhDProject.FriendsFamily.Utilities;

import java.util.Random;

public class RandomUtility {

	private Random randomGenerator;
	
	public static Random getRandomGenerator() {
		return new Random();
	}

	public void setRandomGenerator(Random randomGenerator) {
		this.randomGenerator = randomGenerator;
	}
}
