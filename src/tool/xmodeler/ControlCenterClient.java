package tool.xmodeler;

import java.util.Collections;
import java.util.Optional;
import java.util.Vector;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import tool.clients.workbench.WorkbenchClient;
import xos.Message;
import xos.Value;

public class ControlCenterClient {
	
	private ControlCenterClient() {}

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
		controlCenter.setAllCategories(vec);
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
		System.err.println("Modelpath: "+ modelPath);
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
			int id = value.values[0].intValue;
			String name = value.values[1].strValue();
			vec.add(name);
			System.err.println("Message: "+ value);
		}
		System.err.println("Message: "+ message);
		controlCenter.setDiagrams(vec);
	}
	
	public void createNewProject() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Name for new Project");
		dialog.setHeaderText("Enter a name for the new Project");
		Optional<String> result = dialog.showAndWait();
		String projectName = "";
		if (result.isPresent()) {
		    projectName = result.get();
			Message message = WorkbenchClient.theClient().getHandler().newMessage("addProject",1);
			message.args[0] = new Value(projectName);
			WorkbenchClient.theClient().getHandler().raiseEvent(message);
		}
	}
}
