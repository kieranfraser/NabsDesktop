/**
 * Sample Skeleton for 'NabsDesktop.fxml' Controller Class
 */

package MastersProject.Controllers;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import MastersProject.GUI.DeliveryTableContent;
import MastersProject.GUI.DeliveryTableResultContent;
import MastersProject.GUI.DetailTableContent;
import MastersProject.GUI.UpliftTableContent;
import MastersProject.GoogleData.CalendarEvent;
import MastersProject.GoogleData.GoogleCalendarData;
import MastersProject.Inference.EventInference;
import MastersProject.Models.UpliftedNotification;
import MastersProject.Models.UpliftValues.AppUplift;
import MastersProject.Models.UpliftValues.BodyUplift;
import MastersProject.Models.UpliftValues.DateUplift;
import MastersProject.Models.UpliftValues.SenderUplift;
import MastersProject.Models.UpliftValues.SubjectUplift;
import MastersProject.Nabs.App;
import MastersProject.Utilities.DateUtility;
import MastersProject.Utilities.ResultCallback;
import PhDProject.FriendsFamily.Models.Notification;
import PhDProject.FriendsFamily.Models.User;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

public class NabsDesktopController implements Initializable{

	 @FXML // fx:id="phoneTab"
	 private Tab phoneTab; // Value injected by FXMLLoader
	 
	@FXML // fx:id="androidImg"
    private ImageView androidImg; // Value injected by FXMLLoader

    @FXML // fx:id="tabPaneReceiver"
    private TabPane tabPaneReceiver; // Value injected by FXMLLoader
//
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

    @FXML // fx:id="applicationInput"
    private TextField applicationInput; // Value injected by FXMLLoader

    @FXML // fx:id="nextBtn"
    private Button nextBtn; // Value injected by FXMLLoader

    @FXML // fx:id="prevBtn"
    private Button prevBtn; // Value injected by FXMLLoader
    
    @FXML // fx:id="datePicker"
    private DatePicker datePicker; // Value injected by FXMLLoader
    
    @FXML // fx:id="timeInput"
    private TextField timeInput; // Value injected by FXMLLoader

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
    
    @FXML // fx:id="infoBeadTab"
    private AnchorPane infoBeadTab; // Value injected by FXMLLoader

    @FXML // fx:id="senderBead"
    private Circle senderBead; // Value injected by FXMLLoader

    @FXML // fx:id="notificationBead"
    private Circle notificationBead; // Value injected by FXMLLoader
    
    @FXML // fx:id="kJan20"
    private RadioButton kJan20; // Value injected by FXMLLoader

    @FXML // fx:id="notificationExcel"
    private ToggleGroup notificationExcel; // Value injected by FXMLLoader

    @FXML // fx:id="kJan21"
    private RadioButton kJan21; // Value injected by FXMLLoader

    @FXML // fx:id="kJan22"
    private RadioButton kJan22; // Value injected by FXMLLoader

    @FXML // fx:id="oDec02"
    private RadioButton oDec02; // Value injected by FXMLLoader

    @FXML // fx:id="oDec10"
    private RadioButton oDec10; // Value injected by FXMLLoader

    @FXML // fx:id="oDec16"
    private RadioButton oDec16; // Value injected by FXMLLoader

    @FXML // fx:id="notificationTracker"
    private Text notificationTracker; // Value injected by FXMLLoader
    
    @FXML // fx:id="simulateButton"
    private Button simulateButton; // Value injected by FXMLLoader
        
    private int senderRows;
    private int subjectRows;
    private int bodyRows;
    private int appRows;
    private int dateRows;
    
    private int senderRank;
    private int subjectRank;
    private int appRank;
    private int bodyRank;
    private int dateRank;
    

    /**
     * NAbSim variables
     */
    @FXML
    private ListView<String> userListView;
    private ArrayList<String> userList = new ArrayList<>();
    private ListProperty<String> listProperty = new SimpleListProperty<>();
    
    @FXML // fx:id="deliveryTableView"
    private TableView<DeliveryTableContent> deliveryTableView;
    @FXML // fx:id="deliveryTable_id"
    private TableColumn<DeliveryTableContent, Integer> deliveryTable_id;
    
    @FXML // fx:id="detailTableView"
    private TableView<DetailTableContent> detailTableView;
    @FXML // fx:id="detailTable_id"
    private TableColumn<DetailTableContent, Integer> detailTable_id;
    @FXML // fx:id="detailTable_sender"
    private TableColumn<DetailTableContent, String> detailTable_sender;
    @FXML // fx:id="detailTable_subject"
    private TableColumn<DetailTableContent, String> detailTable_subject;
    @FXML // fx:id="detailTable_app"
    private TableColumn<DetailTableContent, String> detailTable_app;
    
    @FXML // fx:id="detailTable_date"
    private TableColumn<DetailTableContent, Date> detailTable_date; 
    
    @FXML // fx:id="tableScrollbar"
    private ScrollBar tableScrollbar;
    
    @FXML
    private TableView<UpliftTableContent> rankingTableView;
    
    private int selectedNotificationId;
    
    private int numberDeliveryColumns;
    
    private List<UpliftedNotification> upliftedNotificationList;
    
    private ArrayList<String> deliveryList = new ArrayList<>();
    
    @FXML // fx:id="result1"
    private TableColumn<DeliveryTableContent, String> result1;

    @FXML // fx:id="result2"
    private TableColumn<DeliveryTableContent, String> result2;

    @FXML // fx:id="result3"
    private TableColumn<DeliveryTableContent, String> result3;

    @FXML // fx:id="result4"
    private TableColumn<DeliveryTableContent, String> result4;

    @FXML // fx:id="result5"
    private TableColumn<DeliveryTableContent, String> result5;
    
    @FXML // fx:id="calendarTableView"
    private TableView<CalendarEvent> calendarTableView; // Value injected by FXMLLoader

    @FXML // fx:id="calendarStart"
    private TableColumn<CalendarEvent, Date> calendarStart; // Value injected by FXMLLoader

    @FXML // fx:id="calendarEnd"
    private TableColumn<CalendarEvent, Date> calendarEnd; // Value injected by FXMLLoader

    @FXML // fx:id="calendarName"
    private TableColumn<CalendarEvent, String> calendarName; // Value injected by FXMLLoader

    @FXML // fx:id="calendarDescription"
    private TableColumn<CalendarEvent, String> calendarDescription; // Value injected by FXMLLoader
    

    @FXML
    void enterSender(ActionEvent event) {
    	this.tabPaneSender.getSelectionModel().select(1);
    	    	
    	/**
    	 * Init the notification input form
    	 */
		setFieldsFromNotification();
		
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
		
		/*List<SenderUplift> senderList = SenderUplift.getUpliftValues();
		this.senderRows = senderList.size();
		ObservableList<UpliftTableContent> data =FXCollections.observableArrayList ();
		for(SenderUplift uplift : senderList){
			UpliftTableContent content = new UpliftTableContent(uplift.getValue(), uplift.getRank());
			data.add(content);
		}
		this.upliftTable.setItems(data);*/
		setNotificationTracker();
		
		// NAbSim functionality
		
		userList = App.getUserStringList();
		userListView.itemsProperty().bind(listProperty);
        listProperty.set(FXCollections.observableArrayList(userList));

        userListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        userListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {            
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				newUserSelected(arg2);
			}
		});
        
        deliveryTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        deliveryTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DeliveryTableContent>(){

			@Override
			public void changed(ObservableValue<? extends DeliveryTableContent> observable,
					DeliveryTableContent oldValue, DeliveryTableContent newValue) {
				Platform.runLater(new Runnable() {
				    @Override public void run() {
						detailTableView.getSelectionModel().select(newValue.getNotificationId());
						selectedNotificationId = newValue.getNotificationId();
						updateCalendar();
				}});
			}
        });
        
        detailTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        detailTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<DetailTableContent>(){

			@Override
			public void changed(ObservableValue<? extends DetailTableContent> observable,
					DetailTableContent oldValue, DetailTableContent newValue) {
				Platform.runLater(new Runnable() {
				    @Override public void run() {
						deliveryTableView.getSelectionModel().select(newValue.getNotificationId());
						selectedNotificationId = newValue.getNotificationId();
						updateCalendar();
				}});
			}
        });
        
        numberDeliveryColumns = 1;
    }
    
    private void setNotificationTracker(){
		int sizeOfNotificationArray = App.getNotificationSize();
		int notificationNumber = App.getNotificationNumber()+1;
		this.notificationTracker.setText(notificationNumber+"/"+sizeOfNotificationArray);
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
    	setNotificationTracker();
    }

    @FXML
    void loadPreviousNotification(ActionEvent event) {
    	App.setPrevNotification();
    	setFieldsFromNotification();
    	setNotificationTracker();
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		consoleTextArea.setText("Nabs");
	}
    
    @FXML
    void sendNotification(ActionEvent event) {

		// temp - reset the context aware variables
		App.setNextBreak(new Date());
		App.setNextFreePeriod(new Date());
		App.setNextContextRelevant(new Date());
		
    	this.consoleTextArea.setText("Nabs");
    	UpliftedNotification notification = new UpliftedNotification();
    	
    	notification.setNotificationId(12345);
    	
    	notification.setSender(this.senderInput.getText());
    	/*notification.setSenderRank(UpliftedNotification.getRank("SenderUplift", 
    			this.senderInput.getText()));*/
    	notification.setSenderRank(this.senderRank);
    	
    	notification.setApp(this.applicationInput.getText());
    	/*notification.setAppRank(UpliftedNotification.getRank("AppUplift",
    			this.applicationInput.getText()));*/
    	notification.setAppRank(this.appRank);
    	
    	notification.setSubject(this.subjectInput.getText());
    	/*notification.setSubjectRank(UpliftedNotification.getRank("SubjectUplift",
    			this.subjectInput.getText()));*/
    	notification.setSubjectRank(this.subjectRank);
    	
    	notification.setBody(this.subjectInput.getText());
    	/*notification.setBodyRank(UpliftedNotification.getRank("BodyUplift",
    			this.subjectInput.getText()));*/
    	notification.setBodyRank(this.bodyRank);
    	
    	notification.setDateImportance(notification.getDateImportance());
    	/*notification.setDateRank(UpliftedNotification.getRank("DateUplift",
    			"not significant"));*/
    	notification.setDateRank(this.dateRank);
    	
    	// set the actual date value
    	String time = this.timeInput.getText();
    	Date date = DateUtility.localDateToDate(this.datePicker.getValue());
    	notification.setDate(DateUtility.createDateAndTime(date, time));
    	
    	// Set the user variables from user input fields
    	App.setUserLocation(this.locationInput.getText());
    	App.setUserEvent(this.eventInput.getText());
    	
    	if(this.nabbedRB.isSelected()){
        	//String result = App.fireNotification(notification, "Nabbed");
        	//consoleTextArea.setText(result);
    	}
    	if(this.customRB.isSelected()){
    		//String result = App.fireNotification(notification, "Custom");
    		String oldMessage = this.consoleTextArea.getText();
        	//consoleTextArea.setText(oldMessage+ "\n"+result);
    	}
    }
    
    private void setFieldsFromNotification(){
    	
    	// Sender Phone
    	UpliftedNotification notification = App.getNotification(); 
		this.senderInput.setText(notification.getSender());
		this.senderRank = notification.getSenderRank();
		this.senderInput.setEditable(false);
		this.subjectInput.setText(notification.getSubject());
		this.subjectRank = notification.getSubjectRank();
		this.subjectInput.setEditable(false);
		this.applicationInput.setText(notification.getApp());
		this.appRank = notification.getAppRank();
		this.applicationInput.setEditable(false);
		this.datePicker.setValue(DateUtility.dateToLocalDate(notification.getDate()));
		this.dateRank = notification.getDateRank();
		this.datePicker.setEditable(false);
		this.timeInput.setText(DateUtility.getTimeAsString(notification.getDate()));
		this.timeInput.setEditable(false);
		
		//Receiver Phone
		CalendarEvent event = null;
		try {
			event = GoogleCalendarData.getNextEvent(notification.getDate());
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<String> userDetails = EventInference.getCurrentLocationAndEventName(event, notification);
		this.locationInput.setText(userDetails.get(1));
		this.locationInput.setEditable(false);
		this.eventInput.setText(userDetails.get(0));
		this.eventInput.setEditable(false);
    }
    
    
    
    private void setEmptyFields(){
    	 
    	// sender phone 
    	
    	//this.senderInput.setText("");
		this.senderInput.setEditable(true);
		//this.subjectInput.setText("");
		this.subjectInput.setEditable(true);
		//this.applicationInput.setText("");
		this.applicationInput.setEditable(true);
		//this.datePicker.getEditor().clear();
		//this.datePicker.setValue(null);
		//this.timeInput.setText("");
		this.timeInput.setEditable(true);
		
		// receiver phone
		this.locationInput.setEditable(true);
    }
    
    @FXML
    void excelUplift(ActionEvent event) {
    	if(this.kJan20.isSelected()){
    		App.setNotificationsExcelInput("kieranJan20.xlsx");
    	}
		if(this.kJan21.isSelected()){
    		App.setNotificationsExcelInput("kieranJan21.xlsx");
		}
		if(this.kJan22.isSelected()){
    		App.setNotificationsExcelInput("kieranJan22.xlsx");
		}
		if(this.oDec02.isSelected()){
    		App.setNotificationsExcelInput("owenDec02.xlsx");
		}
		if(this.oDec10.isSelected()){
    		App.setNotificationsExcelInput("owenDec10.xlsx");
		}
		if(this.oDec16.isSelected()){
    		App.setNotificationsExcelInput("owenDec16.xlsx");
		}
		App.refreshNotificationExcelInput();
    }
    
    @FXML
    void prevPressed(KeyEvent event) {
    	this.loadPreviousNotification(new ActionEvent());
    }
    
    @FXML
    void nextPressed(KeyEvent event) {
    	this.loadNextNotification(new ActionEvent());
    }
    
    /**
     * NAbSim functions
     */
    
    /**
     * New user selected - want to load the associated notifications.
     * @param user
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void newUserSelected(String userId){
    	App.setSelectedUser(userId);
    	User user = App.getSelectedUser();

		System.out.println("selected user "+user.toString());
		    
		
		this.deliveryTable_id.setCellValueFactory(new PropertyValueFactory<DeliveryTableContent, Integer>("notificationId"));
		this.result1.setCellValueFactory(new PropertyValueFactory<DeliveryTableContent, String>("result1"));
		this.result2.setCellValueFactory(new PropertyValueFactory<DeliveryTableContent, String>("result2"));
		this.result3.setCellValueFactory(new PropertyValueFactory<DeliveryTableContent, String>("result3"));
		this.result4.setCellValueFactory(new PropertyValueFactory<DeliveryTableContent, String>("result4"));
		this.result5.setCellValueFactory(new PropertyValueFactory<DeliveryTableContent, String>("result5"));
		
		this.detailTable_id.setCellValueFactory(new PropertyValueFactory<DetailTableContent, Integer>("notificationId"));
		this.detailTable_sender.setCellValueFactory(new PropertyValueFactory<DetailTableContent, String>("sender"));
		this.detailTable_subject.setCellValueFactory(new PropertyValueFactory<DetailTableContent, String>("subject"));
		this.detailTable_app.setCellValueFactory(new PropertyValueFactory<DetailTableContent, String>("app"));
		this.detailTable_date.setCellValueFactory(new PropertyValueFactory<DetailTableContent, Date>("date"));
    	
    	List<Notification> notificationList = user.getNotifications();		
		upliftedNotificationList = App.readNotifications();
		
		
		ObservableList<DeliveryTableContent> dataDelivery =FXCollections.observableArrayList ();
		ObservableList<DetailTableContent> dataDetail =FXCollections.observableArrayList ();
		
		int id=0;
		for(Notification notification: notificationList){
			
			DeliveryTableContent deliveryContent = new DeliveryTableContent(id);
			DetailTableContent detailContent = new DetailTableContent(id, upliftedNotificationList.get(id).getSender(), 
					upliftedNotificationList.get(id).getSubject(), upliftedNotificationList.get(id).getApp(), upliftedNotificationList.get(id).getDate());
			
			dataDelivery.add(deliveryContent);
			dataDetail.add(detailContent);
			id++;
		}
		this.deliveryTableView.setItems(dataDelivery);
		this.detailTableView.setItems(dataDetail);
		
		this.tableScrollbar.setMax(dataDetail.size()); //make sure the max is equal to the size of the table row data.
		this.tableScrollbar.setMin(0); 
		this.tableScrollbar.valueProperty().addListener(new ChangeListener(){

			@Override
			public void changed(ObservableValue arg0, Object arg1, Object arg2) {
				
				Number value = (Number) arg2;
				deliveryTableView.scrollTo(value.intValue()); 
				detailTableView.scrollTo(value.intValue());
			}

		});
    	
    }
    
    @FXML
    void simulateButtonPress(ActionEvent event) {
    	final List<UpliftedNotification> upliftedNotificationList = App.readNotifications();
    	
    	App.setUserLocation(this.locationInput.getText());
    	App.setUserEvent(this.eventInput.getText());
    	deliveryList = new ArrayList<>();
    	
    	TableColumn<DeliveryTableResultContent, String> deliveryCol = new TableColumn<>("Delivery "+numberDeliveryColumns);

		ObservableList<DeliveryTableContent> result1  =FXCollections.observableArrayList ();
    	
    	App.resultCallback = new ResultCallback() {
			@Override
			public void resultCallback(int id, String result) {
				System.out.println("Callback result: \n"+result);
				try{
					deliveryList.set(id, result);
					DeliveryTableContent deliveryContent = new DeliveryTableContent(id);
					deliveryContent.setResult1(result);
					result1.set(id, deliveryContent);
				}catch(Exception e){
					deliveryList.add(id, result);
					DeliveryTableContent deliveryContent = new DeliveryTableContent(id);
					deliveryContent.setResult1(result);
					result1.add(id, deliveryContent);
				}
				
				deliveryTableView.setItems(result1);
			}
		};
		for(UpliftedNotification notification: upliftedNotificationList){
        	System.out.println("*************************************************************"+notification.getNotificationId());
			App.fireNotification(notification, "Custom");
		}
		
		
		System.out.println("function called");
		System.out.println(deliveryList.size());
		
				
    }
    
    private void updateCalendar(){
    	
    	// get notification using selected id

    	User user = App.getSelectedUser();
    	UpliftedNotification selectedNotification = null;
    	
    	for(UpliftedNotification notification: upliftedNotificationList){
    		if(notification.getNotificationId() == selectedNotificationId){
    			selectedNotification = notification;
    		}
    	}
    	
    	// use notification time to get list of Calendar events using getnextnevents()
    	ArrayList<CalendarEvent> events = null;
    	try {
    		events = GoogleCalendarData.getNextNEvents(selectedNotificationId, selectedNotification.getDate());
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	calendarStart.setCellValueFactory(new PropertyValueFactory<CalendarEvent, Date>("startDate"));
    	calendarEnd.setCellValueFactory(new PropertyValueFactory<CalendarEvent, Date>("endDate"));
    	calendarDescription.setCellValueFactory(new PropertyValueFactory<CalendarEvent, String>("description"));
    	calendarName.setCellValueFactory(new PropertyValueFactory<CalendarEvent, String>("summary"));
    	
    	// update calendarTableView with the list of calendarEvents
    	ObservableList<CalendarEvent> eventList  =FXCollections.observableArrayList ();
    	for(CalendarEvent event: events){
    		eventList.add(event);
    	}
    	calendarTableView.setItems(eventList);
    }
     
    
}
