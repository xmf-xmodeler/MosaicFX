package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.workbench.WorkbenchClient;
import xos.Message;
import xos.Value;

public class ToolIntroductionManager {
	
	public ToolIntroductionManager() {
		generateIntroductionDiagram();
	}

	private void generateIntroductionDiagram() {
		String projectName = "intro";
		Message message = WorkbenchClient.theClient().getHandler().newMessage("addProject",1);
		message.args[0] = new Value(projectName);
		WorkbenchClient.theClient().getHandler().raiseEvent(message);
		String diagramName = "crazyTestName";
		FmmlxDiagramCommunicator.getCommunicator().createDiagram(projectName, diagramName, "",
				FmmlxDiagramCommunicator.DiagramType.ClassDiagram, false,(i) -> {});
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(projectName, diagramName);
	}

	public void start() {
		// TODO Auto-generated method stub
		
	}

}
