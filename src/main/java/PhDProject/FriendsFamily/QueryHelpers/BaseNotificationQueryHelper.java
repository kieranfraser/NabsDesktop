package PhDProject.FriendsFamily.QueryHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import PhDProject.FriendsFamily.Models.MobileApp;
import PhDProject.FriendsFamily.Models.Notification;
import PhDProject.FriendsFamily.Models.Sender;
import PhDProject.FriendsFamily.Models.Subject;
import PhDProject.FriendsFamily.Models.User;
import PhDProject.FriendsFamily.Utilities.DateFormatUtility;


public class BaseNotificationQueryHelper {

	/**
	 * Query to get the sender and the corresponding current ranking for that sender (at the time
	 * of the notification).
	 */
	static String senderAndRankingQuery = 
			
		"select notificationDate, "+
		"max((substr(friendshipDate, 7, 4) || '-' || substr(friendshipDate, 4, 2) || '-' || substr(friendshipDate, 1, 2)))  "+
		"as 'friendshipDate', ranking  from "+
		"( "+
			"select notificationDate, friendshipDate, personA, personB, ranking from ( "+
				"select notifications.personA as 'personA', notifications.personB as 'personB', "+
				 "notifications.date as 'notificationDate', senderFriendship.friendshipRanking as 'ranking', friendshipDate from  "+
				"( "+
					"select * from sms_log  "+
					"where  "+
						"personA = ?  "+
						"and personB = ?	 "+
						"and personB is not ''  "+
						"and (type = 'incoming' or type = 'incoming+')  "+
						
						"UNION "+
						
					"select call_log.id, call_log.personA, call_log.personB, call_log.date, call_log.type, call_log.hash, call_log.cat from call_log  "+
					"where  "+
						"personA = ?  "+
						"and personB = ? 	 "+
						"and personB is not ''  "+
						"and (type = 'incoming' or type = 'incoming+') "+
				") as notifications JOIN "+
		
				"( "+
					"select friendshipDate, weight as 'friendshipRanking' from ( "+
					"SELECT DISTINCT call_log.personA, friendship.personA, friendship.personB, friendship.weight, friendship.date as 'friendshipDate' FROM friendship "+ 
					"JOIN call_log  "+
					"ON call_log.personA = friendship.personA "+
					"WHERE call_log.personA = ? "+
					"and friendship.personB = ?) "+
				") as senderFriendship  "+
			") as rankingTable "+
			"where  "+
			"(substr(friendshipDate, 7, 4) || '-' || substr(friendshipDate, 4, 2) || '-' || substr(friendshipDate, 1, 2))  "+
			"<  "+
			"(substr(notificationDate, 7, 4) || '-' || substr(notificationDate, 4, 2) || '-' || substr(notificationDate, 1, 2))  "+
			"ORDER BY notificationDate "+
		") GROUP BY notificationDate ORDER BY notificationDate;";
	
	 /**
     * Set the individual notification values with results from the query
     * @param c
     * @param userA
     * @param userB
     * @return
     * @throws SQLException
     * @throws ParseException 
     */
    public static ArrayList<Notification> getSenderAndRankingQuery(Connection c, User userA, User userB) throws SQLException, ParseException {
    	PreparedStatement preparedStatement = c.prepareStatement(senderAndRankingQuery);
    	
    	ArrayList<Notification> notifications = new ArrayList<Notification>();
    	
    	preparedStatement.setString(1, userA.getId());
    	preparedStatement.setString(2, userB.getId());
    	preparedStatement.setString(3, userA.getId());
    	preparedStatement.setString(4, userB.getId());
    	preparedStatement.setString(5, userA.getId());
    	preparedStatement.setString(6, userB.getId());
    	ResultSet rs = preparedStatement.executeQuery();
    	    	
    	while (rs.next()) {
    		
    		Notification n = new Notification();
    		n.setId(1234);
    		//n.setSendingUser(userB);
    		
    		// Sender value and rank
    		String senderIdentity = senderReceiverRelationship(userA.getId(), userB.getId(), rs.getInt(3), c);
    		senderIdentity = evaluateIdentity(senderIdentity);
    		
    		int rank = rs.getInt(3);
    		int scaledRank = (rank * 10)/ 7;
    		    		
    		n.setSender(senderIdentity);
    		n.setSenderRank(scaledRank);
    		
    		// Date value
    		
    		Date notificationDate = null;
    		notificationDate = DateFormatUtility.convertStringToDate(rs.getString(1));
    		
    		n.setDate(DateFormatUtility.convertDateToStringUTC(notificationDate));
    		
    		/**
    		 * Subject - value and rank
    		 * - the subject value is derived from a number of ground-truths
    		 * - the final definitive value is chosen randomly from a pool
    		 * 
    		 * - based on the sender take a value from activities
    		 * - based on contextually relevant events
    		 * 
    		 */
    		Subject chosenSubject = chooseASubject(userA, userB, senderIdentity);
    		n.setSubject(chosenSubject);
    		
    		// App value and rank
    		MobileApp chosenApp = chooseApp(userA, chosenSubject);
    		n.setApp(chosenApp);
    		
    		
    		notifications.add(n);
    	}
    	return notifications;
    }
    
    /**
     * Helper function to determine the relationship between the receiver and sender of the notification.
     * @param userA
     * @param userB
     * @param friendRanking
     * @param c
     * @return
     * @throws SQLException
     */
    private static String senderReceiverRelationship(String userA, String userB, int friendRanking, Connection c) throws SQLException{
    	
    	String coupleQuery = 
    			"select couple.coupleId from couple where couple.personId = ?;";
    	
    	PreparedStatement preparedStatement = c.prepareStatement(coupleQuery);
    	preparedStatement.setString(1, userA);
    	ResultSet rs = preparedStatement.executeQuery();
    	
    	if(rs.next() == true){
    		int coupleIdA = rs.getInt(1);
    		String personBCouple = 
        			"select couple.coupleId from couple where couple.personId = ?;";
        	
        	preparedStatement = c.prepareStatement(personBCouple);
        	preparedStatement.setString(1, userB);
        	rs = preparedStatement.executeQuery();
        	
        	if(rs.next() == true){
        		int coupleIdB = rs.getInt(1);
            	if(coupleIdA == coupleIdB){
            		return "partner";
            	}
            	else {
            		if(friendRanking > 0){
                		return "friend";
            		}
            		else{
            			return "stranger";
            		}
            	}
        	}
        	else {
        		if(friendRanking > 0){
        			return "friend";
        		}
        		else{
        			return "stranger";
        		}
        	}
    	}
    	else{
	    	if(friendRanking > 0){
	    		return "friend";
	    	}
	    	else {
	    		return "stranger";
	    	}
    	}
    }
    
    /**
     * The "friend" identity is split up into "friend" and "colleague" for greater granularity. 
     * Argument against this as a back and forth conversation may assign the same person different
     * values hence treat them differently, however, over time it can also be argued that person can
     * change their identity i.e. this was previously a colleague, now they are a friend.
     * Fails to discern between friend and colleague. 
     * @param senderIdentity
     * @return
     */
    private static String evaluateIdentity(String senderIdentity){
    	if(senderIdentity == "partner"){
    		return "family";
    	}
    	if(senderIdentity == "friend"){ 
    		int value = (int) (Math.random() * 2);
    		if(value == 0){
    			return "friend";
    		} else return "colleague";
    	}
    	if(senderIdentity == "stranger"){
    		return "stranger";
    	}
    	return "";
    }
    
    private static Subject chooseASubject(User userA, User userB, String senderIdentity){
    	return getSenderRelevantActivitySubject(userA, senderIdentity);
    }
    
    private static void getContextuallyRelevantEventSubjects(){
    	
    }
    
    /**
     * Depending on the sender, subject is chosen which fits an activity the receiving user does 
     * with the sending group.
     * @param userA
     * @param senderIdentity
     * @return - A relevant activity subject
     */
    private static Subject getSenderRelevantActivitySubject(User userA, String senderIdentity){
    	if(userA.getActivities().isEmpty()){
    		Subject subject = new Subject();
    		subject.setSubject(Subject.FAMILY);
    		return subject;
    	}
    	switch(senderIdentity){
    	case Sender.STRANGER:
    		return userA.getActivities().get(QueryActivitySubjects.STRANGER_INTEREST);
    	case Sender.COLLEAGUE:
    		return userA.getActivities().get(QueryActivitySubjects.COLLEAGUE_SOCIALWORK);
    	case Sender.FRIEND:
    		return userA.getActivities().get(QueryActivitySubjects.FRIEND_SOCIAL);
    	case Sender.FAMILY:
    		return userA.getActivities().get(QueryActivitySubjects.FAMILY_FAMILY);
    	default:
    		return null;
    	}
    }
    
    /**
     * Choose the app from which the notification originated based on the receiving user's favorite apps,
     * the relationship of the sender and the chosen subject of the notification.
     * 
     * App category to subject mappings:
	 * 
	 * Games -	Family Interest	
	 * News and weather -	Interest Work	
	 * Entertainment -	Interest Social	
	 * Communication -	Social	
	 * Social - Social	
	 * Other - Random	
	 * Lifestyle -	Interest Family 	
	 * Productivity and Tools - Work	
	 * Phone Personalization - Interest	
	 * Shopping - Social Family Interest
	 * 
     * @param userA
     * @param chosenSubject
     * @param senderIdentity
     * @return MobileApp object
     */
    private static MobileApp chooseApp(User userA, Subject chosenSubject){
    	MobileApp app = new MobileApp();
    	
    	ArrayList<String> categories = new ArrayList<String>();
    	
    	switch(chosenSubject.getSubject()){
    	case Subject.FAMILY:
    		categories = new ArrayList<String>();
    		categories.add(MobileApp.GAMES);
    		categories.add(MobileApp.LIFESTYLE);
    		categories.add(MobileApp.SHOPPING);
    		categories.add(MobileApp.COMMUNICATION);
    		return userA.getAppForCat(categories);
    	case Subject.INTEREST:
    		categories = new ArrayList<String>();
    		categories.add(MobileApp.GAMES);
    		categories.add(MobileApp.LIFESTYLE);
    		categories.add(MobileApp.SHOPPING);
    		categories.add(MobileApp.ENTERTAINMENT);
    		categories.add(MobileApp.PHONE_PERSONALIZATION);
    		categories.add(MobileApp.ENTERTAINMENT);
    		categories.add(MobileApp.OTHER);
    		categories.add(MobileApp.COMMUNICATION);
    		return userA.getAppForCat(categories);
    	case Subject.SOCIAL:
    		categories = new ArrayList<String>();
    		categories.add(MobileApp.COMMUNICATION);
    		categories.add(MobileApp.ENTERTAINMENT);
    		categories.add(MobileApp.SOCIAL);
    		return userA.getAppForCat(categories);
    	case Subject.WORK:
    		categories = new ArrayList<String>();
    		categories.add(MobileApp.PRODUCTIVITY);
    		categories.add(MobileApp.COMMUNICATION);
    		return userA.getAppForCat(categories);
    	}
    	return app;
    }    
}
