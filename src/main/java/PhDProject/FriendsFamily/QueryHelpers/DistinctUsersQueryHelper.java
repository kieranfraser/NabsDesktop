package PhDProject.FriendsFamily.QueryHelpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import PhDProject.FriendsFamily.Models.User;

public class DistinctUsersQueryHelper {
	
	/**
	 * Gets a distinct list of all sp-xx-xx users
	 * @param c
	 * @return
	 * @throws SQLException
	 */
	public static ArrayList<User> getDistinctUserList(Connection c) throws SQLException{
		ArrayList<User> users = new ArrayList<User>();
		
		String query = "SELECT DISTINCT personA FROM call_log WHERE personA is not 'personA' and personA not like '%f%' "
	    		+ "ORDER BY personA ASC;";
		
		Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        
        while (rs.next()) {
        	User user = new User();
        	user.setId(rs.getString(1));
        	users.add(user);
        }
		return users;
	}

}
