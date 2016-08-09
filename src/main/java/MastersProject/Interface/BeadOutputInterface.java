package MastersProject.Interface;

import java.util.Date;

import MastersProject.Models.Triplet;

public interface BeadOutputInterface {

	public void sendToConsumer(String senderId, Date sentTime, Triplet outputData);
}
