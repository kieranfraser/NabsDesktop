package MastersProject.GoogleData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.gdata.util.ServiceException;


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
    
    public static ArrayList<CalendarEvent> getCalendarResults(int nextNoEvents) throws IOException, ServiceException, ParseException{
    	
    	com.google.api.services.calendar.Calendar service =
	            getCalendarService();
    	
    	calendarIDS = new ArrayList<String>();
    	calendars = new ArrayList<Calendar>();
    	 
    	// Iterate through entries in calendar list
    	/*String pageToken = null;
    	do {
    	  CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
    	  List<CalendarListEntry> items = calendarList.getItems();

    	  for (CalendarListEntry calendarListEntry : items) {
    	    System.out.println("Summary: "+calendarListEntry.getSummary());
    	    System.out.println("id: "+calendarListEntry.getId());
    	    calendarIDS.add(calendarListEntry.getId());
    	  }
    	  pageToken = calendarList.getNextPageToken();
    	} while (pageToken != null);
    	
    	for(String id : calendarIDS){
    		// Retrieve a specific calendar list entry
    		calendars.add(service.calendars().get(id).execute());
    	}*/
    	
    // set min time to be September
    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    	Date dateSept = formatter.parse("27/09/2015");
    	DateTime september = new DateTime(dateSept.getTime());
	// List all events from the primary calendar from september (start of notifications)
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
            .setTimeMin(september)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        ArrayList<CalendarEvent> requiredEvents = new ArrayList<CalendarEvent>();
        
        for(int i=0; i<nextNoEvents; i++){
        	EventDateTime eventDateTime = items.get(i).getStart();
        	String date = eventDateTime.getDateTime().toString();
        	System.out.println(date);
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        	Date convertedDate = sdf.parse(date);
        	System.out.println(convertedDate);
        	sdf.applyPattern("dd/MM/yyyy HH:mm:ss");
        	System.out.println(sdf.format(convertedDate));
        	/*sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'BST' yyyy");
        	Date convertedString = sdf.parse(convertedDate.toString());
        	System.out.println(convertedString);*/
        	CalendarEvent event = new CalendarEvent(items.get(i).getDescription(),
        			convertedDate, items.get(i).getLocation());
        	requiredEvents.add(event);
        }
        
        return requiredEvents;
        /*if (items.size() == 0) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }*/
    }
      
}
