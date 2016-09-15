package PhD.Firebase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Base64;

import com.firebase.client.Firebase;

import MastersProject.Models.UpliftedNotification;

public class FirebaseHelper {

	private static Firebase database;
	
	public FirebaseHelper(){
		if(database == null){
			database = new Firebase("https://nabs-79ba2.firebaseio.com/");
		}
	}
	
	
	/**
	 * Return an active connection to Firebase.
	 * If none available then initialize a connection.
	 * @return
	 */
	public static Firebase getDatabase(){
		if(database != null){
			return database;
		}
		else{
			database = new Firebase("https://nabs-79ba2.firebaseio.com/");
			return database;
		}
	}
	
	public static String convertNotificationToString(UpliftedNotification n) throws IOException{
		String result = null;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] array = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(n);
		  array = bos.toByteArray();
		} finally {
		  try {
		    if (out != null) {
		      out.close();
		    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		  try {
		    bos.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		
		result = Base64.getEncoder().encodeToString(array);
		
		return result;
	}
	
	public static UpliftedNotification convertStringToNotification(String base64String) throws IOException, ClassNotFoundException{
		UpliftedNotification result = null;
		
		byte[] array = Base64.getDecoder().decode(base64String);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(array);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
		  result = (UpliftedNotification) in.readObject(); 
		} finally {
		  try {
		    bis.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		  try {
		    if (in != null) {
		      in.close();
		    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		
		return result;
	}
}
