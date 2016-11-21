package PhDProject.FriendsFamily.PSO;

import java.util.ArrayList;
import java.util.Random;

import MastersProject.Nabs.App;
import PhDProject.Managers.ParameterManager;

public class Particle {
	
	private static final int DIMENSION = 45;
	private static final int POSSIBLE_VALUES = 2;
	private static final double C1 = 2;
	private static final double C2 = 2;
	private static final double VELOCITY_MIN = -2.0;
	private static final double VELOCITY_MAX = 2.0;
	private static final int POSITION_MIN = 1;
	private static final int POSITION_MAX = 3;
	
	private static final int RULE_CROSSOVER = 18;
	
	private static final int POSSIBLE_VALUES_OUTPUT = 5;
	private static final double VELOCITY_MIN_OUTPUT = -4.0;
	private static final double VELOCITY_MAX_OUTPUT = 4.0;
	private static final int POSITION_MIN_OUTPUT = 1;
	private static final int POSITION_MAX_OUTPUT = 5;
	
	private ArrayList<Integer> currentPosition;
	private ArrayList<Double> currentVelocity;
	
	private ArrayList<Integer> pBestPosition;
	private double[] pBestPercentage;
	
	private double[] currentFitness;
	
	private Random rand;
	
	public Particle(){
		rand = new Random(); 
		this.currentPosition = new ArrayList<Integer>();
		this.currentVelocity = new ArrayList<Double>();
		
		this.pBestPosition = new ArrayList<Integer>();
		
		for(int i = 0; i<DIMENSION; i++){
			if(i<RULE_CROSSOVER){ 
				currentPosition.add(rand.nextInt(POSSIBLE_VALUES) + 1);
				currentVelocity.add(rand.nextInt(3) - 1.0); // gives a value in the range of -1 to 1
			}
			else{
				currentPosition.add(rand.nextInt(POSSIBLE_VALUES_OUTPUT) + 1);
				currentVelocity.add(rand.nextInt(3) - 1.0); // gives a value in the range of -1 to 1
			}
		}
	}
	
	public void updateVelocity(){
		double newVelocity = 0;
		for(int i = 0; i<DIMENSION; i++){
			if(i<RULE_CROSSOVER){
				newVelocity = currentVelocity.get(i) + (C1 * rand.nextDouble() * (pBestPosition.get(i)-currentPosition.get(i))) + (C2 * rand.nextDouble() * (App.gBestPosition.get(i) - currentPosition.get(i)));
				if(newVelocity<VELOCITY_MIN){
					newVelocity = VELOCITY_MIN;
				} else if(newVelocity>VELOCITY_MAX){
					newVelocity=VELOCITY_MAX;
				}
			}
			else{
				newVelocity = currentVelocity.get(i) + (C1 * rand.nextDouble() * (pBestPosition.get(i)-currentPosition.get(i))) + (C2 * rand.nextDouble() * (App.gBestPosition.get(i) - currentPosition.get(i)));
				if(newVelocity<VELOCITY_MIN_OUTPUT){
					newVelocity = VELOCITY_MIN_OUTPUT;
				} else if(newVelocity>VELOCITY_MAX_OUTPUT){
					newVelocity=VELOCITY_MAX_OUTPUT;
				}
			}
			
			currentVelocity.set(i, newVelocity);
			updatePosition(i, newVelocity);
		}
		
		// if the current position is equal to the global best - randomly switch values
	}
	
	private void updatePosition(int i, double velocity){
		int newPositionAddition = 0;
		if(velocity > 0){
			newPositionAddition = 1;
		} else if(velocity < 0){
			newPositionAddition = -1;
		}
		else{
			newPositionAddition = 0;
		}
		int newPosition = currentPosition.get(i) + newPositionAddition;	
		if(i<RULE_CROSSOVER){
			if(newPosition<POSITION_MIN){
				newPosition = POSITION_MIN;
			} else if(newPosition>POSITION_MAX){
				newPosition = POSITION_MAX;
			}
		}
		else{
			if(newPosition<POSITION_MIN_OUTPUT){
				newPosition = POSITION_MIN_OUTPUT;
			} else if(newPosition>POSITION_MAX_OUTPUT){
				newPosition = POSITION_MAX_OUTPUT;
			}
		}
		currentPosition.set(i, newPosition);
	}
	
	public void explore(){
		int randomExplore = rand.nextInt(currentPosition.size()/2);
		for(int i = 0; i<randomExplore; i++){
			int randomPosition = rand.nextInt(currentPosition.size());
			if(randomPosition<RULE_CROSSOVER){
				int randomValue = rand.nextInt(POSSIBLE_VALUES) + 1;
				currentPosition.set(randomPosition, randomValue);
			} 
			else {
				int randomValue = rand.nextInt(POSSIBLE_VALUES_OUTPUT) + 1;
				currentPosition.set(randomPosition, randomValue);
			}
		}
	}
	
	public boolean checkPositionSameAs(ArrayList<Integer> otherPosition){
		int counter = 0;
		int i = 0;
		for(int val: currentPosition){
			if(val == otherPosition.get(i)){
				counter++;
			}
			i++;
		}
		if(counter == currentPosition.size()){
			return true;
		}
		else{
			return false;
		}		
	}
	
	
	public double[] getCurrentFitness() {
		return currentFitness;
	}

	public void setCurrentFitness(double[] currentPercent) {
		this.currentFitness = currentPercent;
	}

	public ArrayList<String> getSenderParams(){
		ArrayList<String> senderParams = new ArrayList<String>();
		for(int i=0; i<9; i++){
			switch(currentPosition.get(i)){
			case 1:
				senderParams.add(ParameterManager.LOW);
				break;
			case 2:
				senderParams.add(ParameterManager.MEDIUM);
				break;
			case 3:
				senderParams.add(ParameterManager.HIGH);
				break;
			}
		}
		return senderParams;
	}
	
	public ArrayList<String> getSubjectParams(){
		ArrayList<String> subjectParams = new ArrayList<String>();
		for(int i=9; i<18; i++){
			switch(currentPosition.get(i)){
			case 1:
				subjectParams.add(ParameterManager.LOW);
				break;
			case 2:
				subjectParams.add(ParameterManager.MEDIUM);
				break;
			case 3:
				subjectParams.add(ParameterManager.HIGH);
				break;
			}
		}
		return subjectParams;
	}
	
	public ArrayList<String> getAlertParams(){
		ArrayList<String> alertParams = new ArrayList<String>();
		for(int i=18; i<45; i++){
			switch(currentPosition.get(i)){
			case 1:
				alertParams.add(ParameterManager.NOW);
				break;
			case 2:
				alertParams.add(ParameterManager.VERYSOON);
				break;
			case 3:
				alertParams.add(ParameterManager.SOON);
				break;
			case 4:
				alertParams.add(ParameterManager.LATER);
				break;
			case 5:
				alertParams.add(ParameterManager.MUCHLATER);
				break;
			}
		}
		return alertParams;
	}
	
	public double[] getpBestPercentage() {
		return pBestPercentage;
	}
	public void setpBestPercentage(double[] pBestPercentage) {
		this.pBestPercentage = pBestPercentage;
	}

	public ArrayList<Integer> getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(ArrayList<Integer> currentPosition) {
		this.currentPosition = currentPosition;
	}

	public ArrayList<Double> getCurrentVelocity() {
		return currentVelocity;
	}

	public void setCurrentVelocity(ArrayList<Double> currentVelocity) {
		this.currentVelocity = currentVelocity;
	}

	public ArrayList<Integer> getpBestPosition() {
		return pBestPosition;
	}

	public void setpBestPosition(ArrayList<Integer> pBestPosition) {
		this.pBestPosition = pBestPosition;
	}
}
