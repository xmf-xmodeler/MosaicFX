package tool.xmodeler;

import java.util.Collections;
import java.util.Vector;

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

	public Vector<String> getAllCategories() {
		Vector<String> v = new Vector<>();
		v.add("Sales");
		v.add("Buying");
		return v;
	}
	
	public void getAllProjects() {
	    Message message = WorkbenchClient.theClient().getHandler().newMessage("getAllProjects", 0);
	    WorkbenchClient.theClient().getHandler().raiseEvent(message);
	}

	public void setAllProjects(Message message) {
		Vector<String> vec = new Vector<String>();
		for(int i = 0; i < message.args[0].values.length; i++) {
			vec.add(message.args[0].values[i].strValue());
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
			vec.add(message.args[0].values[i].strValue());
		}
//		Collections.sort(vec);
		controlCenter.setProjectModels(vec);		
	}
 
}
