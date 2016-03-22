/**
 * Sample Skeleton for 'NabsDesktop.fxml' Controller Class
 */

package MastersProject.Controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import MastersProject.GUI.UpliftTableContent;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Models.UpliftValues.AppUplift;
import MastersProject.Models.UpliftValues.BodyUplift;
import MastersProject.Models.UpliftValues.DateUplift;
import MastersProject.Models.UpliftValues.SenderUplift;
import MastersProject.Models.UpliftValues.SubjectUplift;
import MastersProject.Nabs.App;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.util.converter.IntegerStringConverter;

public class NabsDesktopController implements Initializable{

	@FXML // fx:id="androidImg"
    private ImageView androidImg; // Value injected by FXMLLoader

    @FXML // fx:id="tabPaneReceiver"
    private TabPane tabPaneReceiver; // Value injected by FXMLLoader

    @FXML // fx:id="NotificationsTab"
    private Tab NotificationsTab; // Value injected by FXMLLoader

    @FXML // fx:id="consoleTextArea"
    private TextArea consoleTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="userContextTab"
    private Tab userContextTab; // Value injected by FXMLLoader

    @FXML // fx:id="locationInput"
    private TextField locationInput; // Value injected by FXMLLoader

    @FXML // fx:id="eventInput"
    private TextField eventInput; // Value injected by FXMLLoader

    @FXML // fx:id="nabbedUserRB"
    private RadioButton nabbedUserRB; // Value injected by FXMLLoader

    @FXML // fx:id="userContextInput"
    private ToggleGroup userContextInput; // Value injected by FXMLLoader

    @FXML // fx:id="customUserRB"
    private RadioButton customUserRB; // Value injected by FXMLLoader

    @FXML // fx:id="androidImg2"
    private ImageView androidImg2; // Value injected by FXMLLoader

    @FXML // fx:id="tabPaneSender"
    private TabPane tabPaneSender; // Value injected by FXMLLoader

    @FXML // fx:id="upliftTab1"
    private Tab upliftTab1; // Value injected by FXMLLoader

    @FXML // fx:id="enterBtn"
    private Button enterBtn; // Value injected by FXMLLoader

    @FXML // fx:id="inputTab"
    private Tab inputTab; // Value injected by FXMLLoader

    @FXML // fx:id="sendBtn"
    private Button sendBtn; // Value injected by FXMLLoader

    @FXML // fx:id="nabbedRB"
    private RadioButton nabbedRB; // Value injected by FXMLLoader

    @FXML // fx:id="notificationInput"
    private ToggleGroup notificationInput; // Value injected by FXMLLoader

    @FXML // fx:id="customRB"
    private RadioButton customRB; // Value injected by FXMLLoader

    @FXML // fx:id="senderInput"
    private TextField senderInput; // Value injected by FXMLLoader

    @FXML // fx:id="subjectInput"
    private TextField subjectInput; // Value injected by FXMLLoader

    @FXML // fx:id="bodyInput"
    private TextField bodyInput; // Value injected by FXMLLoader

    @FXML // fx:id="applicationInput"
    private TextField applicationInput; // Value injected by FXMLLoader

    @FXML // fx:id="dateInput"
    private TextField dateInput; // Value injected by FXMLLoader

    @FXML // fx:id="nextBtn"
    private Button nextBtn; // Value injected by FXMLLoader

    @FXML // fx:id="prevBtn"
    private Button prevBtn; // Value injected by FXMLLoader

    @FXML // fx:id="upliftTab"
    private Tab upliftTab; // Value injected by FXMLLoader

    @FXML // fx:id="senderRB"
    private RadioButton senderRB; // Value injected by FXMLLoader

    @FXML // fx:id="upliftValueRB"
    private ToggleGroup upliftValueRB; // Value injected by FXMLLoader

    @FXML // fx:id="subjectRB"
    private RadioButton subjectRB; // Value injected by FXMLLoader

    @FXML // fx:id="bodyRB"
    private RadioButton bodyRB; // Value injected by FXMLLoader

    @FXML // fx:id="appRB"
    private RadioButton appRB; // Value injected by FXMLLoader

    @FXML // fx:id="dateRB"
    private RadioButton dateRB; // Value injected by FXMLLoader

    @FXML // fx:id="upliftTable"
    private TableView<UpliftTableContent> upliftTable; // Value injected by FXMLLoader

    @FXML // fx:id="upliftValueColumn"
    private TableColumn<UpliftTableContent, String> upliftValueColumn; // Value injected by FXMLLoader

    @FXML // fx:id="rankColumn"
    private TableColumn<UpliftTableContent, Integer> rankColumn; // Value injected by FXMLLoader
    
    @FXML // fx:id="updateBtn"
    private Button updateBtn; // Value injected by FXMLLoader
    
    private int senderRows;
    private int subjectRows;
    private int bodyRows;
    private int appRows;
    private int dateRows;

    @FXML
    void enterSender(ActionEvent event) {
    	this.tabPaneSender.getSelectionModel().select(1);
    	
    	/**
    	 * Init the notification input form
    	 */
		setFieldsFromNotification();
		
		/**
		 * Init the user context input form
		 */
		this.locationInput.setText("Calendar data to do!");
		this.locationInput.setEditable(false);
		this.eventInput.setText("Calendar data to do!");
		this.eventInput.setEditable(false);
		
		/**
		 * Init the uplift values lists
		 */

        this.upliftTable.setEditable(true);
        
		this.upliftValueColumn.setCellValueFactory(new PropertyValueFactory<UpliftTableContent, String>("value"));
    	this.rankColumn.setCellValueFactory(new PropertyValueFactory<UpliftTableContent, Integer>("rank"));
    	this.rankColumn.setCellFactory(TextFieldTableCell.<UpliftTableContent, Integer>forTableColumn(new IntegerStringConverter()));
    	rankColumn.setOnEditCommit(
    		    new EventHandler<CellEditEvent<UpliftTableContent, Integer>>() {
    		        @Override
    		        public void handle(CellEditEvent<UpliftTableContent, Integer> t) {
    		            ((UpliftTableContent) t.getTableView().getItems().get(
    		                t.getTablePosition().getRow())
    		                ).setRank(t.getNewValue());
    		        }
    		    }
    		);
		
		List<SenderUplift> senderList = SenderUplift.getUpliftValues();
		this.senderRows = senderList.size();
		ObservableList<UpliftTableContent> data =FXCollections.observableArrayList ();
		for(SenderUplift uplift : senderList){
			UpliftTableContent content = new UpliftTableContent(uplift.getValue(), uplift.getRank());
			data.add(content);
		}
		this.upliftTable.setItems(data);
    }
    
    @FXML
    void updateUplift(ActionEvent event) {    	
    	if(this.senderRB.isSelected()){
    		List<UpliftTableContent> newUplift = new ArrayList<UpliftTableContent>();
    		for(int i = 0; i<this.senderRows; i++){
        		UpliftTableContent updatedUplift = new UpliftTableContent(this.upliftValueColumn.getCellData(i),
        				this.rankColumn.getCellData(i));
        		newUplift.add(updatedUplift);
    		}
    		SenderUplift.updateUpliftValues(newUplift);
    	}
    	if(this.subjectRB.isSelected()){
    		List<UpliftTableContent> newUplift = new ArrayList<UpliftTableContent>();
    		for(int i = 0; i<this.subjectRows; i++){
        		UpliftTableContent updatedUplift = new UpliftTableContent(this.upliftValueColumn.getCellData(i),
        				this.rankColumn.getCellData(i));
        		newUplift.add(updatedUplift);
    		}
    		SubjectUplift.updateUpliftValues(newUplift);
    	}
    	if(this.bodyRB.isSelected()){
    		List<UpliftTableContent> newUplift = new ArrayList<UpliftTableContent>();
    		for(int i = 0; i<this.bodyRows; i++){
        		UpliftTableContent updatedUplift = new UpliftTableContent(this.upliftValueColumn.getCellData(i),
        				this.rankColumn.getCellData(i));
        		newUplift.add(updatedUplift);
    		}
    		BodyUplift.updateUpliftValues(newUplift);
    	}
    	if(this.appRB.isSelected()){
    		List<UpliftTableContent> newUplift = new ArrayList<UpliftTableContent>();
    		for(int i = 0; i<this.appRows; i++){
        		UpliftTableContent updatedUplift = new UpliftTableContent(this.upliftValueColumn.getCellData(i),
        				this.rankColumn.getCellData(i));
        		newUplift.add(updatedUplift);
    		}
    		AppUplift.updateUpliftValues(newUplift);
    	}
    	if(this.dateRB.isSelected()){
    		List<UpliftTableContent> newUplift = new ArrayList<UpliftTableContent>();
    		for(int i = 0; i<this.dateRows; i++){
        		UpliftTableContent updatedUplift = new UpliftTableContent(this.upliftValueColumn.getCellData(i),
        				this.rankColumn.getCellData(i));
        		newUplift.add(updatedUplift);
    		}
    		DateUplift.updateUpliftValues(newUplift);
    	}
    	App.refreshNotificationsOnRankUpdate();
    }

    @FXML
    void loadNextNotification(ActionEvent event) {
    	App.setNextNotificaiton();
    	setFieldsFromNotification();
    }

    @FXML
    void loadPreviousNotification(ActionEvent event) {
    	App.setPrevNotification();
    	setFieldsFromNotification();
    }

    @FXML
    void notificationRadioClicked(ActionEvent event) {
    	if(this.nabbedRB.isSelected()){
    		setFieldsFromNotification();
    	}
    	if(this.customRB.isSelected()){
    		setEmptyFields();
    	}
    }

    @FXML
    void upliftRadioClicked(ActionEvent event) {
    	if(this.senderRB.isSelected()){
    		List<SenderUplift> senderList = SenderUplift.getUpliftValues();
    		this.senderRows = senderList.size();
    		ObservableList<UpliftTableContent> data =FXCollections.observableArrayList ();
    		for(SenderUplift uplift : senderList){
    			UpliftTableContent content = new UpliftTableContent(uplift.getValue(), uplift.getRank());
    			data.add(content);
    		}
    		this.upliftTable.setItems(data);
    	}
    	if(this.subjectRB.isSelected()){
    		List<SubjectUplift> subjectList = SubjectUplift.getUpliftValues();
    		this.subjectRows = subjectList.size();
    		ObservableList<UpliftTableContent> data =FXCollections.observableArrayList ();
    		for(SubjectUplift uplift : subjectList){
    			UpliftTableContent content = new UpliftTableContent(uplift.getValue(), uplift.getRank());
    			data.add(content);
    		}
    		this.upliftTable.setItems(data);
    	}
    	if(this.bodyRB.isSelected()){
    		List<BodyUplift> bodyList = BodyUplift.getUpliftValues();
    		this.bodyRows = bodyList.size();
    		ObservableList<UpliftTableContent> data =FXCollections.observableArrayList ();
    		for(BodyUplift uplift : bodyList){
    			UpliftTableContent content = new UpliftTableContent(uplift.getValue(), uplift.getRank());
    			data.add(content);
    		}
    		this.upliftTable.setItems(data);
    	}
    	if(this.appRB.isSelected()){
    		List<AppUplift> appList = AppUplift.getUpliftValues();
    		this.appRows = appList.size();
    		ObservableList<UpliftTableContent> data =FXCollections.observableArrayList ();
    		for(AppUplift uplift : appList){
    			UpliftTableContent content = new UpliftTableContent(uplift.getValue(), uplift.getRank());
    			data.add(content);
    		}
    		this.upliftTable.setItems(data);
    	}
    	if(this.dateRB.isSelected()){
    		List<DateUplift> dateList = DateUplift.getUpliftValues();
    		this.dateRows = dateList.size();
    		ObservableList<UpliftTableContent> data =FXCollections.observableArrayList ();
    		for(DateUplift uplift : dateList){
    			UpliftTableContent content = new UpliftTableContent(uplift.getValue(), uplift.getRank());
    			data.add(content);
    		}
    		this.upliftTable.setItems(data);
    	}
    }

    @FXML
    void userContextRadioClicked(ActionEvent event) {
    	if(this.nabbedUserRB.isSelected()){
    		UpliftedNotification notification = App.getNotification(); 
    		this.locationInput.setText("Calendar data to do!");
    		this.locationInput.setEditable(false);
    		this.eventInput.setText("Calendar data to do!");
    		this.eventInput.setEditable(false);
    	}
    	if(this.customUserRB.isSelected()){
    		this.locationInput.setText("");
    		this.locationInput.setEditable(true);
    		this.eventInput.setText("");
    		this.eventInput.setEditable(true);
    	}
    }


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		consoleTextArea.setText("Hello hello hello");
		
		
	}
    
    @FXML
    void sendNotification(ActionEvent event) {
    	UpliftedNotification notification = new UpliftedNotification();
    	
    	notification.setNotificationId(12345);
    	
    	notification.setSender(this.senderInput.getText());
    	notification.setSenderRank(UpliftedNotification.getRank("SenderUplift", 
    			this.senderInput.getText()));
    	
    	notification.setApp(this.applicationInput.getText());
    	notification.setAppRank(UpliftedNotification.getRank("AppUplift",
    			this.applicationInput.getText()));
    	
    	notification.setSubject(this.subjectInput.getText());
    	notification.setSubjectRank(UpliftedNotification.getRank("SubjectUplift",
    			this.subjectInput.getText()));
    	
    	notification.setBody(this.bodyInput.getText());
    	notification.setBodyRank(UpliftedNotification.getRank("BodyUplift",
    			this.bodyInput.getText()));
    	
    	notification.setDateImportance(this.dateInput.getText());
    	notification.setDateRank(UpliftedNotification.getRank("DateUplift",
    			this.dateInput.getText()));
    	
    	// get the date value
    	notification.setDate(new Date());
    	
    	if(this.nabbedRB.isSelected()){
        	String result = App.fireNotification(notification, "Nabbed");
        	consoleTextArea.setText(result);
    	}
    	if(this.customRB.isSelected()){
    		String result = App.fireNotification(notification, "Custom");
        	consoleTextArea.setText(result);
    	}
    }
    
    private void setFieldsFromNotification(){
    	UpliftedNotification notification = App.getNotification(); 
		this.senderInput.setText(notification.getSender());
		this.senderInput.setEditable(false);
		this.subjectInput.setText(notification.getSubject());
		this.subjectInput.setEditable(false);
		this.bodyInput.setText(notification.getBody());
		this.bodyInput.setEditable(false);
		this.applicationInput.setText(notification.getApp());
		this.applicationInput.setEditable(false);
		this.dateInput.setText(notification.getDateImportance());
		this.dateInput.setEditable(false);
    }
    
    private void setEmptyFields(){
    	this.senderInput.setText("");
		this.senderInput.setEditable(true);
		this.subjectInput.setText("");
		this.subjectInput.setEditable(true);
		this.bodyInput.setText("");
		this.bodyInput.setEditable(true);
		this.applicationInput.setText("");
		this.applicationInput.setEditable(true);
		this.dateInput.setText("");
		this.dateInput.setEditable(true);
    }
}
