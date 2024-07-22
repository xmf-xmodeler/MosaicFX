package tool.clients.fmmlxdiagrams.dialogs;

import java.io.IOException;
import java.util.Optional;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.xmldatabase.XMLDatabase;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.ControlCenterClient;
import xos.Message;
import xos.Value;

public class RenameProjektDialog {
	
	public void start(AbstractPackageViewer diagram, XMLDatabase db)
	{
		Stage primaryStage = new Stage();
		// Create a confirmation alert
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Project Exists");
        alert.setHeaderText(null);
        alert.setContentText("A project with this name already exists in the database. Would you like to save to this project?");

        // Customize the buttons
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the dialog and wait for the user response
        alert.showAndWait().ifPresent(response -> {
            if (response == noButton) {
               String s = setNewName(diagram);
               String [] a = diagram.getPackagePath().split("::");
               String b = diagram.getPackagePath().substring(6);
               
                System.err.print(a);
              
                // 	schlieﬂen
                // diagram.getComm().;
                //	refresh
//                diagram.getComm().openDiagram(s, diagram.getPackagePath());
                //	reopen
                
            }
            else
            {
            	try {
					db.writeToDB((FmmlxDiagram)diagram);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }

	
    
    private String setNewName( AbstractPackageViewer diagram)
    {	
    	String projectPath = diagram.getPackagePath().substring(6);
    	TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("new Name for Project");
		dialog.setHeaderText("Enter a new name for the Project and save again");
		Optional<String> result = dialog.showAndWait();
		String projectName = "";
		if (result.isPresent()) {
			if(InputChecker.isValidIdentifier(result.get())) {
				projectName = result.get();
				Message message = WorkbenchClient.theClient().getHandler().newMessage("renameProject",2);
				message.args[0] = new Value(projectPath);
				message.args[1] = new Value(projectName);
				WorkbenchClient.theClient().getHandler().raiseEvent(message);
			} else {
				new Alert(AlertType.ERROR, 
					"\"" + result.get() + "\" is not a valid identifier.", 
					new ButtonType("OK", ButtonData.YES)).showAndWait();
			}
		}
		return projectName;
    }

}
