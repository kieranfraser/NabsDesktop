package PhDProject.FriendsFamily.QueryHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import PhDProject.FriendsFamily.Models.Subject;
import PhDProject.FriendsFamily.Models.User;


public class QueryActivitySubjects {
	
	public final static int STRANGER_INTEREST = 0;
	public final static int FRIEND_SOCIAL = 1;
	public final static int COLLEAGUE_SOCIALWORK = 2;
	public final static int FAMILY_FAMILY = 3;
	
	private static String selectQuery = 
			"select alone, friends, colleague, partner || family as family from activities "+
					"where id = ? ;";
	
	public static ArrayList<User> setActivitySubjects(ArrayList<User> users, Connection c) throws SQLException{
		for(User user: users){
			user.setActivities(getActivitySubjects(user.getId(), c));
		}
		return users;
	}

	private static ArrayList<Subject> getActivitySubjects(String id, Connection c) throws SQLException{
		ArrayList<Subject> activityList = new ArrayList<Subject>();
				
		PreparedStatement preparedStatement = c.prepareStatement(selectQuery);
    	preparedStatement.setString(1, id);
		
    	ResultSet rs = preparedStatement.executeQuery();
    	while (rs.next()) {
    		// stranger (alone - interest) 
    		Subject subject = new Subject();
    		subject.setDataset("activities");
    		subject.setGround_truth(rs.getString(1));
    		subject.setSubject("interest");
    		activityList.add(STRANGER_INTEREST, subject);
    		
    		// friends - social
    		subject = new Subject();
    		subject.setDataset("activities");
    		subject.setGround_truth(rs.getString(2));
    		subject.setSubject("social");
    		activityList.add(FRIEND_SOCIAL, subject);
    		
    		// work - colleague - this is actually social - may have to come back to it 
    		subject = new Subject();
    		subject.setDataset("activities");
    		subject.setGround_truth(rs.getString(3));
    		subject.setSubject("work");
    		activityList.add(COLLEAGUE_SOCIALWORK, subject);
    		
    		// family 
    		subject = new Subject();
    		subject.setDataset("activities");
    		subject.setGround_truth(rs.getString(4));
    		subject.setSubject("family");
    		activityList.add(FAMILY_FAMILY, subject);
    	}
    	
		return activityList;
	}
}
