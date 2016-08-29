package PhDProject.FriendsFamily.QueryHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import PhDProject.FriendsFamily.Models.Personality;
import PhDProject.FriendsFamily.Models.User;


/**
 * Class to get the answers to the 44 big5 questions from the database and
 * calculate the individual personality traits.
 * @author kfraser
 *
 */
public class Big5QueryHelper {
	
	private static String selectQuery = 
			"select * from big5 where id=upper(?);";
	
	private static ArrayList<Integer> answers;
	
	public static ArrayList<User> setPersonalities(ArrayList<User> users, Connection c) throws SQLException{
		for(User user : users){
			Personality userPersonality = getPersonality(user.getId(), c);
			if(userPersonality != null){
				user.setPersonality(userPersonality);
			}
		}
		return users;
	}
	
	private static Personality getPersonality(String userId, Connection c) throws SQLException{
		answers = new ArrayList<Integer>();
		PreparedStatement preparedStatement = c.prepareStatement(selectQuery);
    	preparedStatement.setString(1, userId);
		
    	ResultSet rs = preparedStatement.executeQuery();
    	
    	while (rs.next()) {
    		for(int i=2; i<46; i++){
        		answers.add(rs.getInt(i));
    		}
    	}
    	
		if(!answers.isEmpty()){
			return calculatePersonality(answers);
		}
		else return null;
	}
	
	private static Personality calculatePersonality(ArrayList<Integer> answers){
		Personality personality = new Personality();
		
		int extraversion = -1;
		int agreeableness = -1;
		int conscientiousness = -1;
		int neuroticism = -1;
		int openness = -1;
		
		extraversion = answers.get(0) + rScore(answers.get(5)) + answers.get(10) + answers.get(15) + rScore(answers.get(20)) +
				answers.get(25) + rScore(answers.get(30)) + answers.get(35);
		
		agreeableness = rScore(answers.get(1)) + answers.get(6) + rScore(answers.get(11)) + answers.get(16) + answers.get(21) +
				rScore(answers.get(26)) + answers.get(31) + rScore(answers.get(36)) + answers.get(41);
		
		conscientiousness = answers.get(2) + rScore(answers.get(7)) + answers.get(12) + rScore(answers.get(17)) + 
				rScore(answers.get(22)) + answers.get(27) + answers.get(32) + answers.get(37) + rScore(answers.get(42));
		
		neuroticism = answers.get(3) + rScore(answers.get(8)) + answers.get(13) + answers.get(18) + rScore(answers.get(23)) + 
				answers.get(28) + rScore(answers.get(33)) + answers.get(38);
		
		openness = answers.get(4) + answers.get(9) + answers.get(14) + answers.get(19) + answers.get(24) + answers.get(29) +
				rScore(answers.get(34)) + answers.get(39) + rScore(answers.get(40)) +answers.get(43);
										
		personality.setExtraversion(extraversion/8);
		personality.setAgreeableness(agreeableness/9);
		personality.setConscientiousness(conscientiousness/9);
		personality.setNeuroticism(neuroticism/8);
		personality.setOpenness(openness/10);		
		
		return personality;
	}
	
	private static int rScore(int value){
		return 6 - value;
	}

}
