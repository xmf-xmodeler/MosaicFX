package tool.clients.fmmlxdiagrams.xmldatabase;

import java.util.Optional;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.core.config.Property;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.helper.user_properties.PropertyManager;
import tool.helper.user_properties.PropertyManagerStage;
import tool.xmodeler.ControlCenter;

/**
 * 
 * @author Nicolas Engel
 * This class represents a custom dialog for uploading configuration data for the connection to the XML database.
 * It extends from  custom dialog type and provides a form for entering connection details
 * such as hostname, database name, port, user, and password.
 */
public class UploadConfig extends CustomDialog<UploadConfig.Result>{
	
	private Label hostname = new Label("Hostname");
	private Label databaseName = new Label("Database name");
	private Label port = new Label ("Port");
	private Label userLabel = new Label("User");
	private Label passwordLabel = new Label("Password");

	
	private TextField hostnameTextfield = new TextField();
	private TextField databaseNameTextfield = new TextField();
	private TextField portTextfield	= new TextField();
	private TextField userTextfield = new TextField();
	private TextField passwordTextfield = new TextField();
	
	private Button checkConnectionBtn = new Button("Check Database Connection");
	
	public GridPane gridPane = new GridPane();

	/**
	 * The constructor initializes the dialog with all necessary UI components and sets up the layout.
	 * It creates a dialog pane with OK and Cancel buttons, a header text, and adds the layout to the dialog's content.
	 */
	public UploadConfig() {
		
		DialogPane dialogPane = getDialogPane();
		
		dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
		
		dialogPane.setHeaderText("Connection Data");
		
		layout();
		
		dialogPane.setContent(gridPane);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
	}
	
	/**
     * Sets up the grid layout for the connection data form.
     * This method organizes the labels and text fields on a grid pane.
     */
	private void layout() {
	    Label[] labels = {hostname, databaseName, port, userLabel, passwordLabel};
	    TextField[] textFields = {hostnameTextfield, databaseNameTextfield, portTextfield, userTextfield, passwordTextfield};
	    String[] properties = {"hostname", "databaseName", "port", "user", "password"}; // Empty string for the password field since it's not set from properties
	    
	    this.gridPane.setPadding(new Insets(10, 10, 10, 10));
	    this.gridPane.setHgap(10); // Optional: Horizontal gap between columns
	    this.gridPane.setVgap(10); // Optional: Vertical gap between rows
	    
	    for (int i = 0; i < labels.length; i++) {
	        this.gridPane.add(labels[i], 0, i + 1);       // Add label to column 0
	        this.gridPane.add(textFields[i], 1, i + 1);   // Add text field to column 1

	        // Set text field values from properties, except for the password
	        if (!properties[i].isEmpty()) {
	            textFields[i].setText(PropertyManager.getProperty(properties[i]));
	        }
	    }
	    
	    checkConnectionBtn.setOnAction(this::connectionMessage);
	    this.gridPane.add(checkConnectionBtn, 0, labels.length+2); //+1 reserved for OK button
	}
	
	private void connectionMessage(ActionEvent e) {
		XMLDatabase db = new XMLDatabase();
		Alert alert;
		PropertyManager properties = new PropertyManager();
		
		if(db.isConnected()) {
			alert = new Alert(AlertType.CONFIRMATION);
	        alert.setContentText("Connection to Database <" + properties.getProperty("databaseName").toString() +  "> successful.");
		} else {
			alert = new Alert(AlertType.ERROR);
	        alert.setContentText(db.getErrorMessage());
		}
		
        alert.setTitle("Check connection to BaseX Database");
        alert.setHeaderText(null);
        alert.getButtonTypes().setAll(ButtonType.OK);
        Optional<ButtonType> result = alert.showAndWait();
	}
	
	/**
     * Saves the connection data entered by the user into the property manager.
     * This method retrieves the text from each text field and stores it using the property manager.
     */
	public void setResult(PropertyManagerStage pms) 
	{
		String hostname = this.hostnameTextfield.getText();
		String dbName = this.databaseNameTextfield.getText();
		String port = this.portTextfield.getText();
		String user = this.userTextfield.getText();
		String pw = this.passwordTextfield.getText();

				
	    PropertyManager manager = new PropertyManager();
	    manager.setProperty("hostname", hostname);
	    manager.setProperty("databaseName", dbName);
	    manager.setProperty("port", port);
	    manager.setProperty("user", user);
	    manager.setProperty("password", pw);
	    
	    ((ControlCenter) pms.getWindows().get(0)).toggleDatabaseVisbility();

	    // Show success message
	    
	    Alert alert = new Alert(AlertType.CONFIRMATION);
	    alert.setContentText("The following connection data has been saved:\n"
	    		+ "Host: " +  hostname +"\n"
	    		+ "Database name: " + dbName + "\n"
	    		+ "Port number: " + port + "\n"
	    		+ "User name: " + user + "\n"
	    		+ "Password: " + pw + "\n");
		alert.setTitle("New connection data saved");
        alert.setHeaderText(null);
        alert.getButtonTypes().setAll(ButtonType.OK);
        Optional<ButtonType> result = alert.showAndWait();
	    
	}
	
	/**
	 * @author Nicolas Engel
     * Nested class to hold the result of the upload configuration dialog.
     */
	public class Result {
		public final String uri;
		public final String user;
		public final String password;
		
		/**
         * Constructs a new result instance with specified URI, user, and password.
         * @param uri The URI for the connection.
         * @param user The user name for the connection.
         * @param password The password for the connection.
         */
		public Result(String uri, String user, String password) {
			this.uri = uri;
			this.user = user;
			this.password = password;
		}

		
	}

	public void setUriTextfield(String uri) {
		this.hostnameTextfield.setText(uri);
	}

	public void setUserTextfield(String user) {
		this.userTextfield.setText(user);;
	}

//	public void setPasswordTextfield(String password) {
//		this.passwordTextfield.setText(password);
//	}
	
}
