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

import MastersProject.Utilities.DateUtility;


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
    
    /**
     * Get the next n events from the date and time input
     * @param n - number of events
     * @param date - the date to compare
     * @return An array list of calendar events 
     * @throws ParseException 
     * @throws IOException 
     */
    public static ArrayList<CalendarEvent> getNextNEvents(int n, Date dateFrom) 
    		throws ParseException, IOException {
    	com.google.api.services.calendar.Calendar service = getCalendarService();
    	
    	calendarIDS = new ArrayList<String>();
    	calendars = new ArrayList<Calendar>();
    	//DateTime date = new DateTime(dateFrom.getTime());
    	DateTime date = new DateTime(dateFrom, TimeZone.getDefault());
        Events events = service.events().list("primary")
            .setTimeMin(date)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        ArrayList<CalendarEvent> requiredEvents = new ArrayList<CalendarEvent>();
        
        for(int i=0; i<n; i++){
        	Date convertedStartDate = DateUtility.convertEventDateTimeToDate(items.get(i).getStart());
        	Date convertedEndDate = DateUtility.convertEventDateTimeToDate(items.get(i).getEnd());
        	CalendarEvent event = new CalendarEvent(items.get(i).getDescription(),
        			convertedStartDate, convertedEndDate, items.get(i).getLocation(), items.get(i).getSummary());
        	requiredEvents.add(event);
        }
        return requiredEvents;
    }
    
    /**
     * Get the next event - to be used for the user context
     * @param dateFrom
     * @return
     * @throws ParseException
     * @throws IOException
     */
   public static CalendarEvent getNextEvent(Date dateFrom)
	   throws ParseException, IOException {
   		com.google.api.services.calendar.Calendar service = getCalendarService();
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
    			convertedStartDate, convertedEndDate, items.get(0).getLocation(), items.get(0).getSummary());
        return event;
    }
    
      
}
