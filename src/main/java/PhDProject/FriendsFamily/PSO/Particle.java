package PhDProject.FriendsFamily.PSO;

import java.util.ArrayList;
import java.util.Random;

import MastersProject.Nabs.App;
import PhDProject.Managers.ParameterManager;

public class Particle {
	
	private static final int DIMENSION = 27;
	private static final double C1 = 2;
	private static final double C2 = 2;
	private static final double ZERO = 0;
	private static final double POSITION_MIN = 0.001;
	private static final double POSITION_MAX_ONE = 1;
	private static final double POSITION_MAX_ONE_HUNDRED = 100;
	private static final int CROSSOVER = 16;
	
	private ArrayList<Double> currentPosition;
	private ArrayList<Double> currentVelocity;
	
	private ArrayList<Double> pBestPosition;
	private double[] pBestPercentage;
	
	private double[] currentFitness;
	
	private Random rand;
	
	public Particle(){
		rand = new Random(); 
		this.currentPosition = new ArrayList<Double>();
		this.currentVelocity = new ArrayList<Double>();
		
		this.pBestPosition = new ArrayList<Double>();
		
		for(int i = 0; i<DIMENSION; i++){
			if(i<CROSSOVER){
				double random = rand.nextDouble(); 
				currentPosition.add(random == ZERO ? POSITION_MIN : random);
				random = rand.nextDouble();
				currentVelocity.add(random); 
			}
			else{
				double random = (double) rand.nextInt(101); 
				currentPosition.add(random == ZERO ? POSITION_MIN : random);
				random = (double) rand.nextInt(101); 
				currentVelocity.add(random); 
			}
		}
	}
	
	public void updateVelocity(){
		double newVelocity = 0;
		for(int i = 0; i<DIMENSION; i++){
			newVelocity = currentVelocity.get(i) + (C1 * rand.nextDouble() * (pBestPosition.get(i)-currentPosition.get(i))) + (C2 * rand.nextDouble() * (App.gBestPosition.get(i) - currentPosition.get(i)));
			
			currentVelocity.set(i, newVelocity);
			updatePosition(i, newVelocity);
		}
	}
	
	private void updatePosition(int i, double velocity){
		double newPosition = POSITION_MIN;
		newPosition = currentPosition.get(i) + velocity;
		if(i < CROSSOVER){
			if(newPosition>POSITION_MAX_ONE){
				newPosition = POSITION_MAX_ONE;
			}
			else if(newPosition <= POSITION_MIN){
				newPosition = POSITION_MIN;
			}
		}
		else {
			if(newPosition>POSITION_MAX_ONE_HUNDRED){
				newPosition = POSITION_MAX_ONE_HUNDRED;
			}
			else if(newPosition <= POSITION_MIN){
				newPosition = POSITION_MIN;
			}
		}
		currentPosition.set(i, newPosition);
	}
	
	public boolean checkPositionSameAs(ArrayList<Double> otherPosition){
		int counter = 0;
		int i = 0;
		for(double val: currentPosition){
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

	public double[] getpBestPercentage() {
		return pBestPercentage;
	}
	public void setpBestPercentage(double[] pBestPercentage) {
		this.pBestPercentage = pBestPercentage;
	}

	public ArrayList<Double> getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(ArrayList<Double> currentPosition) {
		this.currentPosition = currentPosition;
	}

	public ArrayList<Double> getCurrentVelocity() {
		return currentVelocity;
	}

	public void setCurrentVelocity(ArrayList<Double> currentVelocity) {
		this.currentVelocity = currentVelocity;
	}

	public ArrayList<Double> getpBestPosition() {
		return pBestPosition;
	}

	public void setpBestPosition(ArrayList<Double> pBestPosition) {
		this.pBestPosition = pBestPosition;
	}
}
