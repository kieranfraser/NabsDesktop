package MastersProject.GoogleData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;

import MastersProject.Nabs.App;
import PhDProject.FriendsFamily.Models.Event;
import PhDProject.FriendsFamily.Models.User;
import PhDProject.FriendsFamily.Utilities.DateFormatUtility;


public class GoogleCalendarData {
	/** Application name. */
    private static final String APPLICATION_NAME = "NotifySimulator";
    
    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/NotifySimulator/Calendar");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR_READONLY, "https://www.googleapis.com/auth/contacts.readonly");
    
    private static ArrayList<String> calendarIDS;
    private static ArrayList<Calendar> calendars;
    
    private static int counter = 0;
	private static ArrayList<PhDProject.FriendsFamily.Models.Event> possibleEvents = new ArrayList<>();

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            GoogleCalendarData.class.getResourceAsStream("client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }
	
	/**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    /**
     * Get the next n events from the date and time input
     * 
     * This needs to be changed for Nabsim - the events are generated 
     * from the friends&family data-set. - N always being 10, events ordered by start
     * time/date
     * 
     * @param n - number of events
     * @param date - the date to compare
     * @return An array list of calendar events 
     * @throws ParseException 
     * @throws IOException 
     */
    public static ArrayList<CalendarEvent> getNextNEvents(int n, Date dateFrom) 
    		throws ParseException, IOException {
    	counter = 0;
    	possibleEvents = new ArrayList<>();
    	boolean greaterDate = false;
    	
    	ArrayList<CalendarEvent> requiredEvents = new ArrayList<>();
    	
    	User user = App.getSelectedUser();
    	
		// check day - if day of week, get work/study
		LocalDateTime notificationDate = DateFormatUtility.convertDateToLDT(dateFrom);
		DayOfWeek dayOfWeek = notificationDate.getDayOfWeek();
		
		while(counter <10){

    		if(dayOfWeek.equals(DayOfWeek.MONDAY) || dayOfWeek.equals(DayOfWeek.TUESDAY) ||
    				dayOfWeek.equals(DayOfWeek.WEDNESDAY) || dayOfWeek.equals(DayOfWeek.THURSDAY) || 
    						dayOfWeek.equals(DayOfWeek.FRIDAY)){
    			if(counter < 10){
    				addMorningEvent(user, notificationDate, greaterDate);
    			} else break;
    			if(counter < 10){
    				addLunchEvent(user, notificationDate, greaterDate);
    			} else break;
    			if(counter < 10){
    				addAfternoonEvent(user, notificationDate, greaterDate);
    			} else break;
    		}
    		ArrayList<PhDProject.FriendsFamily.Models.Event> events = User.getTodaysEvents(notificationDate, user);
    		for(PhDProject.FriendsFamily.Models.Event event :events){
    			if(counter<10){
    				possibleEvents.add(event);
    				counter++;
    			}
    		}
    		notificationDate = notificationDate.plusDays(1);
    		notificationDate = notificationDate.withHour(0);
    		notificationDate = notificationDate.withMinute(0);
    		notificationDate = notificationDate.withMinute(0);
    		dayOfWeek = notificationDate.getDayOfWeek();
    		greaterDate = true;
		}
		
		for(PhDProject.FriendsFamily.Models.Event event: possibleEvents){
			CalendarEvent calEvent = new CalendarEvent();
			calEvent.setDescription(event.getInferredDescription());
			calEvent.setEndDate(DateFormatUtility.convertLDTToCalendarEventDate(event.getInferredEndDate()));
			calEvent.setLocation("unknown");
			calEvent.setStartDate(DateFormatUtility.convertLDTToCalendarEventDate(event.getInferredStartDate()));
			calEvent.setSummary(event.getNameGT());
			requiredEvents.add(calEvent);
		}
		
		// if notification before 5 add 1-5 event increment counter
		
		// if notification before 1 add lunch event increment counter
		
		// if notification before 12 add morning event to possible events increment counter
		
		// get all other events for today. add counter for each.
		
		// check counter - repeat until 10 met.
    	
    	/*Thread t = new Thread(new Runnable(){
    	    @Override
    	    public void run() {
    	    	try {
					Event.printListEvents(requiredEvents);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	});
    	t.start();*/
    	
    	return requiredEvents;
    }
    
    private static void addMorningEvent(User user, LocalDateTime notificationDate, boolean greaterDate){
    	PhDProject.FriendsFamily.Models.Event newEvent = new PhDProject.FriendsFamily.Models.Event();
    	System.out.println(user.isStudent());
		newEvent.setFixedEventMorning(user.isStudent(), notificationDate);
		if(DateFormatUtility.checkBeforeHour(notificationDate, newEvent.getInferredEndDate())){
			possibleEvents.add(newEvent);
			counter++;
		};
    }
    
    private static void addLunchEvent(User user, LocalDateTime notificationDate, boolean greaterDate){
    	PhDProject.FriendsFamily.Models.Event newEvent = new PhDProject.FriendsFamily.Models.Event();
		newEvent.setFixedLunchEvent(user.isStudent(), notificationDate);
		if(DateFormatUtility.checkBeforeHour(notificationDate, newEvent.getInferredEndDate()) || greaterDate){
			possibleEvents.add(newEvent);
			counter++;
		};
    }
    
    private static void addAfternoonEvent(User user, LocalDateTime notificationDate, boolean greaterDate){
    	PhDProject.FriendsFamily.Models.Event newEvent = new PhDProject.FriendsFamily.Models.Event();
		newEvent.setFixedAfternoonEvent(user.isStudent(), notificationDate);
		if(DateFormatUtility.checkBeforeHour(notificationDate, newEvent.getInferredEndDate())){
			possibleEvents.add(newEvent);
			counter++;
		};
    }
    
    /**
     * Get the next event - to be used for the user context - 
     * now generated based on the inferred values from Friends & Family data-set
     * @param dateFrom
     * @return
     * @throws ParseException
     * @throws IOException
     */
   public static CalendarEvent getNextEvent(Date dateFrom)
	   throws ParseException, IOException {
   		/*com.google.api.services.calendar.Calendar service = getCalendarService();
    	calendarIDS = new ArrayList<String>();
    	calendars = new ArrayList<Calendar>();

    	DateTime date = new DateTime(dateFrom, TimeZone.getDefault());
        Events events = service.events().list("primary")
            .setTimeMin(date)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
    	Date convertedStartDate = DateUtility.convertEventDateTimeToDate(items.get(0).getStart());
    	Date convertedEndDate = DateUtility.convertEventDateTimeToDate(items.get(0).getEnd());
    	CalendarEvent event = new CalendarEvent(items.get(0).getDescription(),
    			convertedStartDate, convertedEndDate, items.get(0).getLocation(), items.get(0).getSummary());*/
	    ArrayList<CalendarEvent> events = getNextNEvents(10, dateFrom);
	    CalendarEvent nextEvent = events.get(0);
        return nextEvent;
    }
    
      
}
