package MastersProject.Models;

import java.io.Serializable;
import java.util.ArrayList;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToOne;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ExistenceChecking;
import org.eclipse.persistence.annotations.ExistenceType;
import org.eclipse.persistence.annotations.ObjectTypeConverter;
import org.eclipse.persistence.annotations.ObjectTypeConverters;

import MastersProject.Constants.ActivationType;
import MastersProject.Constants.BeadType;
import MastersProject.Constants.ConnectionType;

@Entity
@ObjectTypeConverters({ 
	@ObjectTypeConverter(name = "beadType", objectType = BeadType.class, dataType = String.class, conversionValues = {
			@ConversionValue(objectValue = "ALERT", dataValue = "ALERT"),
			@ConversionValue(objectValue = "SENDER", dataValue = "SENDER"),
			@ConversionValue(objectValue = "SUBJECT", dataValue = "SUBJECT"),
			@ConversionValue(objectValue = "APPLICATION", dataValue = "APPLICATION"),
			@ConversionValue(objectValue = "DATE", dataValue = "DATE"),
			@ConversionValue(objectValue = "BODY", dataValue = "BODY"),
			@ConversionValue(objectValue = "LOCATION", dataValue = "LOCATION"),
			@ConversionValue(objectValue = "USER", dataValue = "USER"),
			@ConversionValue(objectValue = "NOTIFICATION", dataValue = "NOTIFICATION") }),
	@ObjectTypeConverter(name = "connectionType", objectType = ConnectionType.class, dataType = String.class, conversionValues = {
			@ConversionValue(objectValue = "PUSH", dataValue = "PUSH"),
			@ConversionValue(objectValue = "PULL", dataValue = "PULL"),
			@ConversionValue(objectValue = "NOTIFY", dataValue = "NOTIFY") }),
	@ObjectTypeConverter(name = "activationType", objectType = ActivationType.class, dataType = String.class, conversionValues = {
			@ConversionValue(objectValue = "ON", dataValue = "ON"),
			@ConversionValue(objectValue = "OFF", dataValue = "OFF") })
	}) 
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TYPE", discriminatorType=DiscriminatorType.STRING,length=20)
public class InformationBead implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2697034321795109248L;

	// info bead
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;
	
	private String singleAttributeName;

	@Basic 
	@Convert("beadType")
	private BeadType attributeValueType;
	
	// control part
	@Basic 
	@Convert("connectionType")
	private ConnectionType comMode;
	
	@Basic 
	@Convert("activationType")
	private ActivationType onOff;
	
	@ElementCollection
	private ArrayList<String> authorizationToSendToID;
	
	// metadata part
	private String infoBeadName;
	
	@ElementCollection
	private ArrayList<String> keywords;
	
	@ElementCollection
	private ArrayList<String> infoUnits;
	
	//private File help;
	
	@ElementCollection
	private ArrayList<String> inputInterfaces;
	
	private String outputInterface;
	private String partNumber;
	private String version;
	private String backwardCompatibility;
	private String resource;
	private String contact;
	private String trustworthiness;
	// Also Metadata tag - xml (possibly RDF?)

	@OneToOne(cascade = CascadeType.ALL)
	private Triplet operational;
	
	
	// operational part 
	public void inferInfoBeadAttr(){}
	public void storeInfoBeadAttr(){}
	
	// control part
	public void activate(){
		this.onOff = ActivationType.ON;
	}
	public void deactivate(){
		this.onOff = ActivationType.OFF;
	}
	private void authorize(String id){}
	private void deAuthorize(String id){}
	private void explain(Triplet infoBeadData){}
	private void addInformation(Triplet infoBeadData){}
	private void deleteInformation(Triplet infoBeadData){}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSingleAttributeName() {
		return singleAttributeName;
	}
	public void setSingleAttributeName(String singleAttributeName) {
		this.singleAttributeName = singleAttributeName;
	}
	public BeadType getAttributeValueType() {
		return attributeValueType;
	}
	public void setAttributeValueType(BeadType attributeValueType) {
		this.attributeValueType = attributeValueType;
	}
	public ConnectionType getComMode() {
		return comMode;
	}
	public void setComMode(ConnectionType comMode) {
		this.comMode = comMode;
	}
	public ActivationType getOnOff() {
		return onOff;
	}
	public void setOnOff(ActivationType onOff) {
		this.onOff = onOff;
	}
	public ArrayList<String> getAuthorizationToSendToID() {
		return authorizationToSendToID;
	}
	public void setAuthorizationToSendToID(ArrayList<String> authorizationToSendToID) {
		this.authorizationToSendToID = authorizationToSendToID;
	}
	public String getInfoBeadName() {
		return infoBeadName;
	}
	public void setInfoBeadName(String infoBeadName) {
		this.infoBeadName = infoBeadName;
	}
	public ArrayList<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}
	public ArrayList<String> getInfoUnits() {
		return infoUnits;
	}
	public void setInfoUnits(ArrayList<String> infoUnits) {
		this.infoUnits = infoUnits;
	}
	public ArrayList<String> getInputInterfaces() {
		return inputInterfaces;
	}
	public void setInputInterfaces(ArrayList<String> inputInterfaces) {
		this.inputInterfaces = inputInterfaces;
	}
	public String getOutputInterface() {
		return outputInterface;
	}
	public void setOutputInterface(String outputInterface) {
		this.outputInterface = outputInterface;
	}
	public String getPartNumber() {
		return partNumber;
	}
	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getBackwardCompatibility() {
		return backwardCompatibility;
	}
	public void setBackwardCompatibility(String backwardCompatibility) {
		this.backwardCompatibility = backwardCompatibility;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getTrustworthiness() {
		return trustworthiness;
	}
	public void setTrustworthiness(String trustworthiness) {
		this.trustworthiness = trustworthiness;
	}
	public Triplet getOperational() {
		return operational;
	}
	public void setOperational(Triplet operational) {
		this.operational = operational;
	}
	
	
	
	
}
