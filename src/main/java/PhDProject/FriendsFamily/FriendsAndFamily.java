package PhDProject.FriendsFamily;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import com.firebase.client.Firebase;

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
import PhDProject.Managers.FirebaseManager;

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

	public static void saveUserList(ArrayList<User> users){
		ArrayList<String> userStrings = new ArrayList<>();
		for(User user: users){
			try {
				userStrings.add(FirebaseManager.convertUserToString(user));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for(String userString: userStrings){
			Firebase ref = FirebaseManager.getDatabase().child("Friends&Family/users/").push();
			ref.setValue(userString);
		}
		
		/*for(User user: users){
			String baseRef = "FriendsFamily/"+user.getId()+"/";
			FirebaseManager.getDatabase().child(baseRef+"favoriteApps/").setValue(user.getFavoriteApps());
			FirebaseManager.getDatabase().child(baseRef+"activities/").setValue(user.getActivities());
			FirebaseManager.getDatabase().child(baseRef+"events/").setValue(user.getEvents());
			FirebaseManager.getDatabase().child(baseRef+"randomChoice/").setValue(user.getRandomChoice());
			FirebaseManager.getDatabase().child(baseRef+"student/").setValue(user.isStudent());
			FirebaseManager.getDatabase().child(baseRef+"stranger/").setValue(user.isStranger());
			FirebaseManager.getDatabase().child(baseRef+"personality/").setValue(user.getPersonality());
			FirebaseManager.getDatabase().child(baseRef+"percentageHome/").setValue(user.getPercentageHome());
			FirebaseManager.getDatabase().child(baseRef+"percentageWork/").setValue(user.getPercentageWork());
			FirebaseManager.getDatabase().child(baseRef+"percentageSocial/").setValue(user.getPercentageSocial());
			FirebaseManager.getDatabase().child(baseRef+"sendingUserIds/").setValue(user.getSendingUserIds());
			String notificationBaseRef = baseRef+"notifications/";
			int id = 0;
			for(Notification n: user.getNotifications()){
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"sender").setValue(n.getSender());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"subject").setValue(n.getSubject());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"app").setValue(n.getApp());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"body").setValue(n.getBody());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"date").setValue(n.getDate());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"senderRank").setValue(n.getSenderRank());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"subjectRank").setValue(n.getSubjectRank());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"appRank").setValue(n.getAppRank());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"bodyRank").setValue(n.getBodyRank());
				FirebaseManager.getDatabase().child(notificationBaseRef+"/"+id+"/"+"dateRank").setValue(n.getDateRank());
				id++;
			}			
		}*/
	}

	public ArrayList<User> getUsers() {
		return users;
	}
	
}
