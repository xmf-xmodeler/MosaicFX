package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.ControlCenter;
import xos.Message;
import xos.Value;

public class ToolIntroductionManager {

	private static ToolIntroductionManager instance;
	private FmmlxDiagram diagram;

	public ToolIntroductionManager(ControlCenter controlCenter) {
		instance = this;
		System.err.println("StartIntoductionMan");
		generateIntroductionDiagram(controlCenter);
	}

	public static ToolIntroductionManager getInstance() {
		if (instance == null) {
			throw new NullPointerException("ToolIntroductionManager needs to be first intialized.");
		}
		return instance;
	}

	private void generateIntroductionDiagram(ControlCenter controlCenter) {
		String projectName = "intro";
		Message message = WorkbenchClient.theClient().getHandler().newMessage("addProject", 1);
		message.args[0] = new Value(projectName);
		WorkbenchClient.theClient().getHandler().raiseEvent(message);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
				throw new RuntimeException();
		}
		String diagramName = "crazyTestName";
		FmmlxDiagramCommunicator.getCommunicator().createDiagram(projectName, diagramName, "",
				FmmlxDiagramCommunicator.DiagramType.ClassDiagram, true, diagramID -> {
					controlCenter.getControlCenterClient().getDiagrams(diagramName);
				});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(projectName, diagramName);
	}

	public void start() {
		// TODO Auto-generated method stub

	}

	public void checkSucessCondition() {
		//is needed because otherwise the changes of the update are not reflected in the check
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
		System.err.println("Check success condition.");
		if (SucessCondition.checkSucessCondition(diagram)) {
			System.err.println("correct");
			diagram.getViewPane().loadNextStage();
		}
	}

	public void setDiagram(FmmlxDiagram diagram) {
		this.diagram = diagram;
	}
}
