package PhDProject.FriendsFamily.QueryHelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import PhDProject.FriendsFamily.Models.Event;
import PhDProject.FriendsFamily.Models.User;


public class EventQueryHelper {
	
	
	private static String query = " SELECT * FROM "+
	 "("+
		"select id, (substr(startDate, 7, 4) || '-' || substr(startDate, 4, 2) || '-' || substr(startDate, 1, 2)) as 'start',"+
		"(substr(endDate, 7, 4) || '-' || substr(endDate, 4, 2) || '-' || substr(endDate, 1, 2)) as 'end', eventForward, efDay, efWith, eventEnjoyed,"+
		"eeDay, eeWith, movie1, movie2, movie3, movie1Day, movie2Day, movie3Day, movie1With, movie2With, movie3With, movieNot1, movieNot2,"+
		"movieNot3, mn1Day, mn2Day, mn3Day, mn1With, mn2With, mn3With, tv1, tv2, tv3, tv1Day, tv2Day, tv3Day, tv1With, tv2With, tv3With,"+
		"rest1, rest2, rest3, rest1Day, rest2Day, rest3Day, rest1With, rest2With, rest3With "+
		"from events where id = ?"+
	 ") "+
	 "WHERE ? BETWEEN start AND end;";
	
	private static String query2 = "SELECT * FROM "+
 "( "+
	 "select id, (substr(startDate, 7, 4) || '-' || substr(startDate, 4, 2) || '-' || substr(startDate, 1, 2)) as 'start', "+
	"(substr(endDate, 7, 4) || '-' || substr(endDate, 4, 2) || '-' || substr(endDate, 1, 2)) as 'end', eventForward, efDay, efWith, eventEnjoyed, "+
	"eeDay, eeWith, movie1, movie2, movie3, movie1Day, movie2Day, movie3Day, movie1With, movie2With, movie3With, movieNot1, movieNot2,  "+
	"movieNot3, mn1Day, mn2Day, mn3Day, mn1With, mn2With, mn3With, tv1, tv2, tv3, tv1Day, tv2Day, tv3Day, tv1With, tv2With, tv3With, "+
	"rest1, rest2, rest3, rest1Day, rest2Day, rest3Day, rest1With, rest2With, rest3With "+
	 "from events where id = ? "+
 ");";
	
	public static ArrayList<User> setAllUserEvents(ArrayList<User> users, Connection c) throws SQLException{
		for(User user: users){
			user.setEvents(userEvents(user.getId(), c));
		}
		return users;
	}
	
	private static ArrayList<Event> userEvents(String userId, Connection c) throws SQLException{
		ArrayList<Event> events = new ArrayList<Event>();
		
		PreparedStatement preparedStatement = c.prepareStatement(query2);
    	preparedStatement.setString(1, userId);
    	ResultSet rs = preparedStatement.executeQuery();
    	Event event;
    	int count = 0;
    	
    	while (rs.next()) {
    		count++;
    		if(!createForwardEvent(rs).checkEmpty()){
    			event = createForwardEvent(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createEnjoyedEvent(rs).checkEmpty()){
    			event = createEnjoyedEvent(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createMovie1Event(rs).checkEmpty()){
    			event = createMovie1Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createMovie2Event(rs).checkEmpty()){
    			event = createMovie2Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createMovie3Event(rs).checkEmpty()){
    			event = createMovie3Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createMovieNT1Event(rs).checkEmpty()){
    			event = createMovieNT1Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createMovieNT2Event(rs).checkEmpty()){
    			event = createMovieNT2Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createMovieNT3Event(rs).checkEmpty()){
    			event = createMovieNT3Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createTv1Event(rs).checkEmpty()){
    			event = createTv1Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createTv2Event(rs).checkEmpty()){
    			event = createTv2Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createTv3Event(rs).checkEmpty()){
    			event = createTv3Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createRestaurant1Event(rs).checkEmpty()){
    			event = createRestaurant1Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createRestaurant2Event(rs).checkEmpty()){
    			event = createRestaurant2Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    		if(!createRestaurant3Event(rs).checkEmpty()){
    			event = createRestaurant3Event(rs);
    			event.calcExactEventDate();
    			event.calcEventDescription();
    			events.add(event);
    		}
    	}
		return events;
	}

	public static List<Event> getUserEventsForDate(String userId, String date, Connection c) throws SQLException{
		PreparedStatement preparedStatement = c.prepareStatement(query);
    	preparedStatement.setString(1, userId);
    	preparedStatement.setString(2, date);
		
    	ResultSet rs = preparedStatement.executeQuery();
    	    	
    	List<Event> weekEventList = new ArrayList<Event>();
    			
    	while (rs.next()) {
    		if(!createForwardEvent(rs).checkEmpty()){
        		weekEventList.add(createForwardEvent(rs));
    		}
    		if(!createEnjoyedEvent(rs).checkEmpty()){
        		weekEventList.add(createEnjoyedEvent(rs));
    		}
    		if(!createMovie1Event(rs).checkEmpty()){
        		weekEventList.add(createMovie1Event(rs));
    		}
    		if(!createMovie2Event(rs).checkEmpty()){
        		weekEventList.add(createMovie2Event(rs));
    		}
    		if(!createMovie3Event(rs).checkEmpty()){
        		weekEventList.add(createMovie3Event(rs));
    		}
    		if(!createMovieNT1Event(rs).checkEmpty()){
        		weekEventList.add(createMovieNT1Event(rs));
    		}
    		if(!createMovieNT2Event(rs).checkEmpty()){
        		weekEventList.add(createMovieNT2Event(rs));
    		}
    		if(!createMovieNT3Event(rs).checkEmpty()){
        		weekEventList.add(createMovieNT3Event(rs));
    		}
    		if(!createTv1Event(rs).checkEmpty()){
        		weekEventList.add(createTv1Event(rs));
    		}
    		if(!createTv2Event(rs).checkEmpty()){
        		weekEventList.add(createTv2Event(rs));
    		}
    		if(!createTv3Event(rs).checkEmpty()){
        		weekEventList.add(createTv3Event(rs));
    		}
    		if(!createRestaurant1Event(rs).checkEmpty()){
        		weekEventList.add(createRestaurant1Event(rs));
    		}
    		if(!createRestaurant2Event(rs).checkEmpty()){
        		weekEventList.add(createRestaurant2Event(rs));
    		}
    		if(!createRestaurant3Event(rs).checkEmpty()){
        		weekEventList.add(createRestaurant3Event(rs));
    		}
    	}
    	
    	return weekEventList;
	}
	
	private static Event createForwardEvent(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(4));
		event.setDayGT(rs.getString(5));
		event.setWithGT(rs.getString(6));
		event.setEventType(Event.FORWARD);
		return event;
	}

	private static Event createEnjoyedEvent(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(7));
		event.setDayGT(rs.getString(8));
		event.setWithGT(rs.getString(9));
		event.setEventType(Event.ENJOYED);
		return event;
	}

	private static Event createMovie1Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(10));
		event.setDayGT(rs.getString(13));
		event.setWithGT(rs.getString(16));
		event.setEventType(Event.CINEMA);
		return event;
	}
	
	private static Event createMovie2Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(11));
		event.setDayGT(rs.getString(14));
		event.setWithGT(rs.getString(17));
		event.setEventType(Event.CINEMA);
		return event;
	}
	
	private static Event createMovie3Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(12));
		event.setDayGT(rs.getString(15));
		event.setWithGT(rs.getString(18));
		event.setEventType(Event.CINEMA);
		return event;
	}
	
	private static Event createMovieNT1Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(19));
		event.setDayGT(rs.getString(22));
		event.setWithGT(rs.getString(25));
		event.setEventType(Event.MOVIE);
		return event;
	}
	
	private static Event createMovieNT2Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(20));
		event.setDayGT(rs.getString(23));
		event.setWithGT(rs.getString(26));
		event.setEventType(Event.MOVIE);
		return event;
	}
	
	private static Event createMovieNT3Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(21));
		event.setDayGT(rs.getString(24));
		event.setWithGT(rs.getString(27));
		event.setEventType(Event.MOVIE);
		return event;
	}

	private static Event createTv1Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(28));
		event.setDayGT(rs.getString(31));
		event.setWithGT(rs.getString(34));
		event.setEventType(Event.TV);
		return event;
	}
	
	private static Event createTv2Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(29));
		event.setDayGT(rs.getString(32));
		event.setWithGT(rs.getString(35));
		event.setEventType(Event.TV);
		return event;
	}
	
	private static Event createTv3Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(30));
		event.setDayGT(rs.getString(33));
		event.setWithGT(rs.getString(36));
		event.setEventType(Event.TV);
		return event;
	}

	private static Event createRestaurant1Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(37));
		event.setDayGT(rs.getString(40));
		event.setWithGT(rs.getString(43));
		event.setEventType(Event.RESTAURANT);
		return event;
	}
	
	private static Event createRestaurant2Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(38));
		event.setDayGT(rs.getString(41));
		event.setWithGT(rs.getString(44));
		event.setEventType(Event.RESTAURANT);
		return event;
	}
	
	private static Event createRestaurant3Event(ResultSet rs) throws SQLException{
		Event event = new Event();
		event.setStartDate(rs.getString(2));
		event.setEndDate(rs.getString(3));
		event.setNameGT(rs.getString(39));
		event.setDayGT(rs.getString(42));
		event.setWithGT(rs.getString(45));
		event.setEventType(Event.RESTAURANT);
		return event;
	}
}
