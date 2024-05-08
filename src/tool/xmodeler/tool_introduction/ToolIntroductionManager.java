package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.ControlCenter;
import xos.Message;
import xos.Value;

public class ToolIntroductionManager {

	private static ToolIntroductionManager instance;
	private static FmmlxDiagram diagram;
	private static String projectName = "ToolIntroductionABC";
	private static String diagramName = "ToolIntroductionDiagramXYZ";
	/**
	 * After a check is started this variable is set to true. It is used to avoid loops.
	 * If the DiagramPrepairActions are used there are diagram updates in the functions.
	 * Normally the SucessConditionCheck is executed on diagram update. If this var is true
	 * a diagram update will not trigger a condition check
	 */
	private static boolean checkingProcessStarted;
		
	public ToolIntroductionManager(ControlCenter controlCenter) {
		instance = this;
		System.err.println("StartIntoductionMan");
		generateIntroductionDiagram(controlCenter);
	}

	public static ToolIntroductionManager getInstance() {
		if (instance == null) {
			instance = new ToolIntroductionManager(null);
			//throw new NullPointerException("ToolIntroductionManager needs to be first intialized.");
		}
		return instance;
	}
	
	public static boolean isInitialized() {
		return (instance != null);
	}

	private void generateIntroductionDiagram(ControlCenter controlCenter) {
		Message message = WorkbenchClient.theClient().getHandler().newMessage("addProject", 1);
		message.args[0] = new Value(projectName);
		WorkbenchClient.theClient().getHandler().raiseEvent(message);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
				throw new RuntimeException();
		}
		FmmlxDiagramCommunicator.getCommunicator().createDiagram(projectName, diagramName, "",
				FmmlxDiagramCommunicator.DiagramType.ClassDiagram, true, diagramID -> {
			//		controlCenter.getControlCenterClient().getDiagrams(diagramName);
				});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

	public void start() {
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(projectName, diagramName);	}

	public void checkSucessCondition() {
		
		if (!checkingProcessStarted) {
			checkingProcessStarted = true;
			//is needed because otherwise the changes of the update are not reflected in the check
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException();
			}
			
			if (new SucessCondition(diagram).checkSucessCondition()) {
				diagram.getViewPane().loadNextStage();
			}
			checkingProcessStarted = false;
		}
	}

	public void setDiagram(FmmlxDiagram diagram) {
		this.diagram = diagram;
	}
}
