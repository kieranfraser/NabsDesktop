package PhDProject.Managers;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import MastersProject.Models.UpliftedNotification;

public class StatisticsManager {
	
	private static boolean firstRun = true;
	private static StatisticsManager instance;
	
	private static UpliftedNotification lastNotification;
	private static UpliftedNotification currentNotification;
	
	private static double totalNotifications;
	private static double totalWorkNotifications;
	private static double totalSocialNotifications;
	private static double totalInterestNotifications;
	private static double totalFamilyNotifications;
	
	private static double workNow;
	private static double workBreak;
	private static double workPeriod;
	private static double workLaterL;
	private static double workLaterM;
	
	private static double socialNow;
	private static double socialBreak;
	private static double socialPeriod;
	private static double socialLaterL;
	private static double socialLaterM;
	
	private static double familyNow;
	private static double familyBreak;
	private static double familyPeriod;
	private static double familyLaterL;
	private static double familyLaterM;
	
	private static double interestNow;
	private static double interestBreak;
	private static double interestPeriod;
	private static double interestLaterL;
	private static double interestLaterM;
	
	private static ArrayList<String> sanityCheckSubjects;
	
	private static String outcome;
	private static String lastOutcome;
	
	private static double[] work9To5Ratio = {40.0, 30.0, 20.0, 5.0, 5.0};
	private static double[] social9To5Ratio = {5.0, 10.0, 20.0, 30.0, 35.0};
	private static double[] idealBalance = {35, 20, 0, -25, -30};	

	public static synchronized StatisticsManager getStatsManager(){
		if(instance!=null){
			return instance;
		}
		else{
			instance = new StatisticsManager();
			return instance;
		}
	}
	
	private StatisticsManager(){
		totalNotifications = 0.0;
		totalWorkNotifications = 0.0;
		totalSocialNotifications = 0.0;
		totalInterestNotifications = 0.0;
		totalFamilyNotifications = 0.0;
		
		sanityCheckSubjects = new ArrayList<String>();
	}
	
	public void updateStats(String result){
		if(firstRun || currentNotification != lastNotification){
			totalNotifications += 1;
			calcSubjectSplit();
			lastNotification = currentNotification;
			
			outcome = result;
			calcImpact();
			firstRun = false;
		}
		else if(currentNotification == lastNotification){
			lastOutcome = outcome;
			outcome = result;
			undoLastImpact();
			calcImpact();
		}
	}
	
	private void calcImpact(){
		switch(currentNotification.getSubject()){
		case "family":
			if(outcome.contains("Now")){
				familyNow +=1;
			} else if(outcome.contains("break")){
				familyBreak +=1;
			} else if(outcome.contains("period")){
				familyPeriod +=1;
			} else if(outcome.contains("Little Later")){
				familyLaterL +=1;
			} else if(outcome.contains("Much Later")){
				familyLaterM +=1;
			}
			break;
		case "interest":
			if(outcome.contains("Now")){
				interestNow +=1;
			} else if(outcome.contains("break")){
				interestBreak +=1;
			} else if(outcome.contains("period")){
				interestPeriod +=1;
			} else if(outcome.contains("Little Later")){
				interestLaterL +=1;
			} else if(outcome.contains("Much Later")){
				interestLaterM +=1;
			}
			break;
		case "social":
			if(outcome.contains("Now")){
				socialNow +=1;
			} else if(outcome.contains("break")){
				socialBreak +=1;
			} else if(outcome.contains("period")){
				socialPeriod +=1;
			} else if(outcome.contains("Little Later")){
				socialLaterL +=1;
			} else if(outcome.contains("Much Later")){
				socialLaterM +=1;
			}
			break;
		case "work":
			if(outcome.contains("Now")){
				workNow +=1;
			} else if(outcome.contains("break")){
				workBreak +=1;
			} else if(outcome.contains("period")){
				workPeriod +=1;
			} else if(outcome.contains("Little Later")){
				workLaterL +=1;
			} else if(outcome.contains("Much Later")){
				workLaterM +=1;
			}
			break;			
		}
	}
	
	
	
	private void undoLastImpact(){
		switch(currentNotification.getSubject()){
		case "family":
			if(outcome.contains("Now")){
				familyNow -=1;
			} else if(outcome.contains("break")){
				familyBreak -=1;
			} else if(outcome.contains("period")){
				familyPeriod -=1;
			} else if(outcome.contains("Little Later")){
				familyLaterL -=1;
			} else if(outcome.contains("Much Later")){
				familyLaterM -=1;
			}
			break;
		case "interest":
			if(outcome.contains("Now")){
				interestNow -=1;
			} else if(outcome.contains("break")){
				interestBreak -=1;
			} else if(outcome.contains("period")){
				interestPeriod -=1;
			} else if(outcome.contains("Little Later")){
				interestLaterL -=1;
			} else if(outcome.contains("Much Later")){
				interestLaterM -=1;
			}
			break;
		case "social":
			if(outcome.contains("Now")){
				socialNow -=1;
			} else if(outcome.contains("break")){
				socialBreak -=1;
			} else if(outcome.contains("period")){
				socialPeriod -=1;
			} else if(outcome.contains("Little Later")){
				socialLaterL -=1;
			} else if(outcome.contains("Much Later")){
				socialLaterM -=1;
			}
			break;
		case "work":
			if(outcome.contains("Now")){
				workNow -=1;
			} else if(outcome.contains("break")){
				workBreak -=1;
			} else if(outcome.contains("period")){
				workPeriod -=1;
			} else if(outcome.contains("Little Later")){
				workLaterL -=1;
			} else if(outcome.contains("Much Later")){
				workLaterM -=1;
			}
			break;			
		}
	}
	
	private void calcSubjectSplit(){
		sanityCheckSubjects.add(currentNotification.getSubject());
		
		switch(currentNotification.getSubject()){
		case "family":
			totalFamilyNotifications += 1;
			break;
		case "interest":
			totalInterestNotifications += 1;
			break;
		case "social":
			totalSocialNotifications += 1;
			break;
		case "work":
			totalWorkNotifications += 1;
			break;			
		}
	}

	public void setCurrentNotification(UpliftedNotification currentNotification) {
		StatisticsManager.currentNotification = currentNotification;
	}
	
	public static void printStats(){
		System.out.println("Stats:");
		System.out.println("totalNotifications: "+totalNotifications);
		System.out.println("totalWorkNotifications: "+totalWorkNotifications);
		System.out.println("totalSocialNotifications: "+totalSocialNotifications);
		System.out.println("totalInterestNotifications: "+totalInterestNotifications);
		System.out.println("totalFamilyNotifications: "+totalFamilyNotifications);
		
		System.out.println("Family: "+familyNow + " "+familyBreak+ " "+familyPeriod+ " "+familyLaterL+ " "+familyLaterM);
		System.out.println("Social: "+socialNow + " "+socialBreak+ " "+socialPeriod+ " "+socialLaterL+ " "+socialLaterM);
		System.out.println("Work: "+workNow + " "+workBreak+ " "+workPeriod+ " "+workLaterL+ " "+workLaterM);
		System.out.println("Interest: "+interestNow + " "+interestBreak+ " "+interestPeriod+ " "+interestLaterL+ " "+interestLaterM);
		
		/*for(String subject: sanityCheckSubjects){
			System.out.println(subject);
		}*/
	}
	
	/*public static double workFunction(){
		//family
		double[] workPercentages = {(workNow/totalWorkNotifications)*100, (workBreak/totalWorkNotifications)*100, 
				(workPeriod/totalWorkNotifications)*100, (workLaterL/totalWorkNotifications)*100, (workLaterM/totalWorkNotifications)*100};
		double[] socialPercentages = {(socialNow/totalSocialNotifications)*100, (socialBreak/totalSocialNotifications)*100, (socialPeriod/totalSocialNotifications)*100, 
				(socialLaterL/totalSocialNotifications)*100, (socialLaterM/totalSocialNotifications)*100};
		
		System.out.println("WorkPercentages: "+Arrays.toString(workPercentages));
		System.out.println("SocialPercentages: "+Arrays.toString(socialPercentages));
		
		
		double[] differenceWork = subtractArray(work9To5Ratio, workPercentages);
		double[] differenceSocial = subtractArray(social9To5Ratio, socialPercentages);
		

		System.out.println("differenceWork: "+Arrays.toString(differenceWork));
		System.out.println("differenceSocial: "+Arrays.toString(differenceSocial));
		
		double[] addedDifference = addArray(differenceWork, differenceSocial);
		System.out.println("addedDifference: "+Arrays.toString(addedDifference));
		return addedDifference[0]+addedDifference[1]+addedDifference[2]+addedDifference[3]+addedDifference[4];
	}*/
	
	public static double[] workFunction(){
		
		double[] workPercentages = {(workNow/totalWorkNotifications)*100, (workBreak/totalWorkNotifications)*100, 
				(workPeriod/totalWorkNotifications)*100, (workLaterL/totalWorkNotifications)*100, (workLaterM/totalWorkNotifications)*100};
		double[] socialPercentages = {(socialNow/totalSocialNotifications)*100, (socialBreak/totalSocialNotifications)*100, (socialPeriod/totalSocialNotifications)*100, 
				(socialLaterL/totalSocialNotifications)*100, (socialLaterM/totalSocialNotifications)*100};
		
		System.out.println("WorkPercentages: "+Arrays.toString(workPercentages));
		System.out.println("SocialPercentages: "+Arrays.toString(socialPercentages));
		
		
		double[] differenceWork = subtractArray(work9To5Ratio, workPercentages);
		double[] differenceSocial = subtractArray(social9To5Ratio, socialPercentages);
		

		System.out.println("differenceWork: "+Arrays.toString(differenceWork));
		System.out.println("differenceSocial: "+Arrays.toString(differenceSocial));
		
		double[] combinedArray = ArrayUtils.addAll(differenceWork, differenceSocial);
		System.out.println("the combined array: "+Arrays.toString(combinedArray));
		return combinedArray;
	}
	
	private static double[] subtractArray(double[] x, double[] y){
		double[] result = {Math.abs(x[0]-y[0]), Math.abs(x[1]-y[1]), Math.abs(x[2]-y[2]), Math.abs(x[3]-y[3]), Math.abs(x[4]-y[4])};
		return result;
	}
	
	private static double[] addArray(double[] x, double[] y){
		double[] result = {x[0]+y[0], x[1]+y[1], x[2]+y[2], x[3]+y[3],x[4]+y[4]};
		return result;
	}
	
	public void reset(){
		firstRun = true;
		lastNotification = null;
		currentNotification = null;
		
		totalNotifications = 0;
		totalWorkNotifications = 0;
		totalSocialNotifications = 0;
		totalInterestNotifications = 0;
		totalFamilyNotifications = 0;
		
		workNow = 0;
		workBreak = 0;
		workPeriod = 0;
		workLaterL = 0;
		workLaterM = 0;
		
		socialNow = 0;
		socialBreak = 0;
		socialPeriod = 0;
		socialLaterL = 0;
		socialLaterM = 0;
		
		familyNow = 0;
		familyBreak = 0;
		familyPeriod = 0;
		familyLaterL = 0;
		familyLaterM = 0;
		
		interestNow = 0;
		interestBreak = 0;
		interestPeriod = 0;
		interestLaterL = 0;
		interestLaterM = 0;
		
		outcome = "";
		lastOutcome ="";
	}
	
	public static boolean checkLessThanEqual(double[] array1, double[] best){
		boolean lessEqual = false;
		int counter = 0;
		if(best != null){
			for(int i=0; i<array1.length; i++){
				if(array1[i] < best[i]){
					counter++;
					if(i == 0 || i == 5){
						counter+=2;
					}
				}
			}
			if(counter > (array1.length*0.8)){
				lessEqual = true;
			}
		}
		else{
			lessEqual = true;
		}
		return lessEqual;
	}
}
