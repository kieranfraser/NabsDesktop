package PhDProject.FriendsFamily.QueryHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import PhDProject.FriendsFamily.Models.User;

public class TitleTimeQueryHelper {
	
	private static String query = "select title, home, work, social from title_time where id = ?;";
	
	private static final int STUDENT = 0;
	private static final int HOME = 1;
	private static final int WORK = 2;
	private static final int SOCIAL = 3;
	
	public static ArrayList<User> setTitleAndTime(ArrayList<User> users, Connection c) throws SQLException{
		for(User user: users){
			Object[] object = getTitleAndTime(user.getId(), c);
			user.setStudent((boolean) object[0]);
			user.setPercentageHome((int) object[1]);
			user.setPercentageWork((int) object[2]);
			user.setPercentageSocial((int) object[3]);
		}
		return users;
	}
	
	private static Object[] getTitleAndTime(String userId, Connection c) throws SQLException{
		PreparedStatement preparedStatement = c.prepareStatement(query);
    	preparedStatement.setString(1, userId);
    	
    	boolean student = false;
    	int home = 0;
    	int work = 0;
    	int social = 0;
		
    	ResultSet rs = preparedStatement.executeQuery();
    	
    	while (rs.next()) {
    		if(rs.getInt(1) == 1){
    			student = true;
    		}
    		home = calcPercentage(rs.getString(2));
    		work = calcPercentage(rs.getString(3));
    		social = calcPercentage(rs.getString(4));
    	}
    	return new Object[]{student, home, work ,social};    	
	}
	
	private static int calcPercentage(String percentage){
		if(percentage.contains("0%")){
			return 0;
		}
		else if(percentage.contains("10%")){
			return 10;
		}
		else if(percentage.contains("30%")){
			return 30;
		}
		else if(percentage.contains("50%")){
			return 50;
		}
		else if(percentage.contains("70%")){
			return 70;
		}
		else if(percentage.contains("90%")){
			return 90;
		}
		else{
			return 100;
		}
	}

}
