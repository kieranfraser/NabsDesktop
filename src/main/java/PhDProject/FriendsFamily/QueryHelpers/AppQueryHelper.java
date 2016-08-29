package PhDProject.FriendsFamily.QueryHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import PhDProject.FriendsFamily.Models.MobileApp;
import PhDProject.FriendsFamily.Models.User;


/**
 * Helper class to determine a user's top applications. (only for use with sp user's,
 * for fa user's must use running/installed apps?)
 * @author kfraser
 *
 */
public class AppQueryHelper {

	private static String selectQuery = 
			"select distinct app, round(avg(rank)), cat from ( "+
					"select enjoy_app_1 as app, 1 as rank, enjoy_cat_1 as cat from survey_monthly where id = ? "+
					"union  "+
					"select enjoy_app_2 as app, 2 as rank, enjoy_cat_2 as cat from survey_monthly where id = ? "+
					"union  "+
					"select enjoy_app_3 as app, 3 as rank, enjoy_cat_3 as cat from survey_monthly where id = ? "+
					"union  "+
					"select enjoy_app_4 as app, 4 as rank, enjoy_cat_4 as cat from survey_monthly where id = ? "+
					"union  "+
					"select enjoy_app_5 as app, 5 as rank, enjoy_cat_5 as cat from survey_monthly where id = ? "+
					"union  "+
					"select fav1 as app, 1 as rank, fav_cat_1 as cat from survey_monthly where id = ? "+
					"union  "+
					"select fav2 as app, 2 as rank, fav_cat_2 as cat from survey_monthly where id = ? "+
					"union  "+
					"select fav3 as app, 3 as rank, fav_cat_3 as cat from survey_monthly where id = ? "+
					"union  "+
					"select fav4 as app, 4 as rank, fav_cat_4 as cat from survey_monthly where id = ? "+
					"union  "+
					"select fav5 as app, 5 as rank, fav_cat_5 as cat from survey_monthly where id = ? "+
				") where app is not null and app is not '' group by app; ";
	
	public static ArrayList<User> setUserApps(ArrayList<User> users, Connection c) throws SQLException{
		for(User user : users){
			user.setFavoriteApps(getUserApps(user.getId(), c));
		}
		return users;
	}
	
	public static ArrayList<MobileApp> getUserApps(String id, Connection c) throws SQLException {
		
    	PreparedStatement preparedStatement = c.prepareStatement(selectQuery);
    	preparedStatement.setString(1, id);
    	preparedStatement.setString(2, id);
    	preparedStatement.setString(3, id);
    	preparedStatement.setString(4, id);
    	preparedStatement.setString(5, id);
    	preparedStatement.setString(6, id);
    	preparedStatement.setString(7, id);
    	preparedStatement.setString(8, id);
    	preparedStatement.setString(9, id);
    	preparedStatement.setString(10, id);
    	
    	ResultSet rs = preparedStatement.executeQuery();
    	
    	ArrayList<MobileApp> appList = new ArrayList<MobileApp>();
    	
    	while (rs.next()) {
    		MobileApp app = new MobileApp();
    		app.setName(rs.getString(1));
    		app.setCategory(rs.getString(3));
    		app.setRank(rs.getInt(2));
    		appList.add(app);
    	}
    	
    	return appList;
	}

}
