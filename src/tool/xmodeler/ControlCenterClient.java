package tool.xmodeler;

import java.util.Collections;
import java.util.Optional;
import java.util.Vector;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.workbench.WorkbenchClient;
import xos.Message;
import xos.Value;

public class ControlCenterClient {
	
	public ControlCenterClient() {}

	private static ControlCenterClient self;
	private ControlCenter controlCenter;
	
	public static void init(ControlCenter controlCenter) {
		if(self != null) throw new IllegalStateException("Already initialised!");
		self = new ControlCenterClient();
		self.controlCenter = controlCenter;
	}
	
	public static ControlCenterClient getClient() {
		if(self == null) throw new IllegalStateException("Not yet initialised!");
		return self;
	}

	public void getAllCategories() {
		Message message = WorkbenchClient.theClient().getHandler().newMessage("getAllCategories", 0);
	    WorkbenchClient.theClient().getHandler().raiseEvent(message);
	}
	
	public void setAllCategories(Message message) {
		Vector<String> vec = new Vector<String>();
		for(int i = 0; i < message.args[0].values.length; i++) {
			vec.add(message.args[0].values[i].strValue());
		}
		Collections.sort(vec);
	}
	
	public void getAllProjects() {
	    Message message = WorkbenchClient.theClient().getHandler().newMessage("getAllProjects", 0);
	    WorkbenchClient.theClient().getHandler().raiseEvent(message);
	}

	public void setAllProjects(Message message) {
		Vector<String> vec = new Vector<String>();
		for(int i = 0; i < message.args[0].values.length; i++) {
			String s = message.args[0].values[i].strValue();
			if(s.startsWith("Root::")) {
				s = s.substring(6);
			}
			vec.add(s);
		}
		
		Collections.sort(vec);
		controlCenter.setAllProjects(vec);		
	}
	
	public void getProjectModels(String projectPath) {
		if(projectPath == null) return;
	    Message message = WorkbenchClient.theClient().getHandler().newMessage("getProjectModels", 1);
	    message.args[0] = new Value(projectPath);
	    WorkbenchClient.theClient().getHandler().raiseEvent(message);
	}
	
	public void setProjectModels(Message message) {
		Vector<String> vec = new Vector<>();
		for(int i = 0; i < message.args[0].values.length; i++) {
			String s = message.args[0].values[i].strValue();
			if(s.startsWith("Root::")) {
				s = s.substring(6);
			}
			vec.add(s);
		}
//		Collections.sort(vec);
		controlCenter.setProjectModels(vec);		
	}
	
	public void getDiagrams(String modelPath) {
		if(modelPath == null) return;
		if(!modelPath.startsWith("Root::")) modelPath = "Root::" + modelPath;
		Message message = WorkbenchClient.theClient().getHandler().newMessage("getDiagrams", 1);
		message.args[0] = new Value(modelPath);
		WorkbenchClient.theClient().getHandler().raiseEvent(message);
	}
	
	public void setDiagrams(Message message) {
		Vector<String> vec = new Vector<>();
		for (int i = 0; i<message.args[0].values.length;i++) {
			Value value = message.args[0].values[i];
			//int id = value.values[0].intValue;
			String name = value.values[1].strValue();
			String type = value.values[2].strValue();
			try {
				if (FmmlxDiagramCommunicator.DiagramType.valueOf(type)==FmmlxDiagramCommunicator.DiagramType.ClassDiagram) {
					vec.add(name);
				}
			}
			catch(NullPointerException|ArrayIndexOutOfBoundsException|IllegalArgumentException exception) {
				System.err.println("DiagramType is missing! In line " + exception.getStackTrace()[0].getLineNumber());
			}
		}
		controlCenter.setDiagrams(vec);
	}
	
	public void createNewProject() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Name for new Project");
		dialog.setHeaderText("Enter a name for the new Project");
		Optional<String> result = dialog.showAndWait();
		String projectName = "";
		if (result.isPresent()) {
			if(InputChecker.isValidIdentifier(result.get())) {
				projectName = result.get();
				createProject(projectName);
			} else {
				new Alert(AlertType.ERROR, 
					"\"" + result.get() + "\" is not a valid identifier.", 
					new ButtonType("OK", ButtonData.YES)).showAndWait();
			}
		}
	}

	public void createProject(String projectName) {
		Message message = WorkbenchClient.theClient().getHandler().newMessage("addProject",1);
		message.args[0] = new Value(projectName);
		WorkbenchClient.theClient().getHandler().raiseEvent(message);
	}

	public ControlCenter getControlCenter() {
		return controlCenter;
	}
}
