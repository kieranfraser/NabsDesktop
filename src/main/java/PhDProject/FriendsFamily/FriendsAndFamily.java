package PhDProject.FriendsFamily;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import PhDProject.FriendsFamily.Models.Event;
import PhDProject.FriendsFamily.Models.Notification;
import PhDProject.FriendsFamily.Models.User;
import PhDProject.FriendsFamily.QueryHelpers.AppQueryHelper;
import PhDProject.FriendsFamily.QueryHelpers.BaseNotificationQueryHelper;
import PhDProject.FriendsFamily.QueryHelpers.Big5QueryHelper;
import PhDProject.FriendsFamily.QueryHelpers.DistinctUsersQueryHelper;
import PhDProject.FriendsFamily.QueryHelpers.EventQueryHelper;
import PhDProject.FriendsFamily.QueryHelpers.QueryActivitySubjects;
import PhDProject.FriendsFamily.QueryHelpers.TitleTimeQueryHelper;
import PhDProject.FriendsFamily.QueryHelpers.UniqueSendersQueryHelper;

public class FriendsAndFamily {
	
	private Connection c;
	private ArrayList<User> users;

	public FriendsAndFamily() throws SQLException, ParseException, IOException{
		c = null;
        try {
          Class.forName("org.sqlite.JDBC");
          c = DriverManager.getConnection("jdbc:sqlite:friends&family.db");
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
        System.out.println("Opened database successfully");
        
        users = DistinctUsersQueryHelper.getDistinctUserList(c);
        users = Big5QueryHelper.setPersonalities(users, c);
        
        // work to do on the personality statistics
        users = TitleTimeQueryHelper.setTitleAndTime(users, c);
        users = EventQueryHelper.setAllUserEvents(users, c);        
        users = AppQueryHelper.setUserApps(users, c);
        users = QueryActivitySubjects.setActivitySubjects(users, c);
        users = UniqueSendersQueryHelper.setUserSenderList(users, c);
        
        for(User user: users){
        	ArrayList<Notification> notifications = new ArrayList<Notification>();
        	for(String id : user.getSendingUserIds()){
        		User userB = findUserFromList(users, id);
        		notifications.addAll(BaseNotificationQueryHelper.getSenderAndRankingQuery(c, user, userB));
        	}
        	user.setNotifications(notifications);
        }
        /*User userTest = users.get(5);
        //userTest.printEvents();
        System.out.println(userTest.getId());
        System.out.println(userTest.getEvents().size());
        userTest.printEvents();*/
	}
	
	private User findUserFromList(ArrayList<User> users, String id){
		for(User user: users){
			if(user.getId().contains(id)){
				return user;
			}
		}
		User userB = new User();
		userB.setStranger(true);
		return userB;
	}

	public void saveUserList(){
		for(User user: users){
			user.saveUser();
		}
	}

	public ArrayList<User> getUsers() {
		return users;
	}
	
}
