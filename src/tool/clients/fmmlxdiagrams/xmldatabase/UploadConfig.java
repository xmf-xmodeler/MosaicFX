package tool.clients.fmmlxdiagrams.xmldatabase;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.helper.userProperties.PropertyManager;
import tool.clients.fmmlxdiagrams.dialogs.CreateMetaClassDialog.Result;
import javafx.scene.control.DialogPane;


public class UploadConfig extends CustomDialog<UploadConfig.Result>{
	
	private Label hostname = new Label("hostname");
	private Label port = new Label ("port");
	private Label userLabel = new Label("user");
	private Label passwordLabel = new Label("password");

	
	private TextField hostnameTextfield = new TextField();
	private TextField portTextfield	= new TextField();
	private TextField userTextfield = new TextField();
	private TextField passwordTextfield = new TextField();
	
	public GridPane gridPane = new GridPane();

	public UploadConfig() {
		
		DialogPane dialogPane = getDialogPane();
		
			dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
			
			dialogPane.setHeaderText("Connection Data");
			
			layout();
			
			dialogPane.setContent(flow);
			
			final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
			
			
			setResult();
	}
	
	private void layout() {
		this.gridPane.add(hostname,0,1);
		this.gridPane.add(port, 0, 2);
		this.gridPane.add(userLabel, 0, 3);
		this.gridPane.add(passwordLabel, 0, 4);
		
		this.gridPane.add(hostnameTextfield,1,1);
		this.gridPane.add(portTextfield,1,2);
		this.gridPane.add(userTextfield, 1, 3);
		this.gridPane.add(passwordTextfield, 1, 4);
		
	}
	public void setResult() 
	{
		PropertyManager manager = new PropertyManager();
		manager.setProperty("hostname",this.hostnameTextfield.getText());
		manager.setProperty("port", this.portTextfield.getText());
		manager.setProperty("user", this.userTextfield.getText());
		manager.setProperty("password", this.passwordTextfield.getText());
	}
	
	public class Result {
		public final String uri;
		public final String user;
		public final String password;
		
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
