package PhDProject.FriendsFamily.QueryHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import PhDProject.FriendsFamily.Models.User;

/**
 * Helper class to get a list of distinct senders for a given receiving user.
 * @author kfraser
 *
 */
public class UniqueSendersQueryHelper {

	private static String selectSQL = "select distinct personB from call_log "+
			"where personA = ? "+
			"and personA is not 'personA' "+
			"and personB is not null "+
			"and personB is not '' "+
			"and (type = 'incoming' or type = 'incoming+') "+
			"union "+
			"select distinct personB from sms_log "+
			"where personA = ? "+
			"and personA is not 'personA' "+
			"and personB is not null "+
			"and personB is not '' "+
			"and (type = 'incoming' or type = 'incoming+') "+
			"order by personB asc; ";
	
	public static ArrayList<User> setUserSenderList(ArrayList<User> users, Connection c) throws SQLException{
		for(User user: users){
			user.setSendingUserIds(getUniqueSenders(c, user.getId()));
		}
		return users;
	}
	
	/**
     * Get the unique list of senders of incoming notifications
     * - used for input into the prepared statement when analysing each particular
     * notification.
     * @param c
     * @return
     * @throws SQLException 
     */
    private static ArrayList<String> getUniqueSenders(Connection c, String user) throws SQLException{
    	ArrayList<String> senders = new ArrayList<String>();
    	
    	PreparedStatement preparedStatement = c.prepareStatement(selectSQL);
    	preparedStatement.setString(1, user);
    	preparedStatement.setString(2, user);
    	ResultSet rs = preparedStatement.executeQuery();
    	while (rs.next()) {
    		senders.add(rs.getString(1));	
    	}

    	return senders;
    }
}
