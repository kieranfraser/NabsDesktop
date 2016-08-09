package MastersProject.GoogleData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.util.ServiceException;


/**
 * Class to get all the google contacts for sensor input
 * @author kfraser
 *
 */
public class GoogleContactsData {

	/** Application name. */
    private static final String APPLICATION_NAME = "NotifySimulator";
    
    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/NotifySimulator/Contacts");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/contacts.readonly");

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
            GoogleContactsData.class.getResourceAsStream("client_secret.json");
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
	
    public static ContactsService getContactsService() throws IOException{
    	ContactsService contactS = new ContactsService(APPLICATION_NAME);
    	Credential cred = authorize();
    	contactS.setOAuth2Credentials(cred);
    	return contactS;
    }
    
    
    public static ArrayList getContacts() throws IOException, ServiceException{
    	
    	ContactsService myService = getContactsService();
    	return getAllContacts(myService);
    }
    
      
    public static ArrayList getAllContacts(ContactsService myService)
    	    throws ServiceException, IOException {
    	ArrayList<String> contactList = new ArrayList();
    	// Request the feed
    	  URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
    	  ContactFeed resultFeed = myService.getFeed(feedUrl, ContactFeed.class);
    	  // Print the results
    	  System.out.println(resultFeed.getTitle().getPlainText());
    	  for (ContactEntry entry : resultFeed.getEntries()) {
    	    if (entry.hasName()) {
    	      Name name = entry.getName();
    	      if (name.hasFullName()) {
    	        String fullNameToDisplay = name.getFullName().getValue();
    	        if (name.getFullName().hasYomi()) {
    	          fullNameToDisplay += " (" + name.getFullName().getYomi() + ")";
    	        }
    	      } 
    	      contactList.add(name.getFullName().getValue());
    	    }
    	  }
    	  return contactList;
    }
}