package MastersProject.Interface;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import MastersProject.Models.Triplet;

public interface BeadOutputInterface {

	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, InstantiationException;
}
