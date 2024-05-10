package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.ControlCenter;
import xos.Message;
import xos.Value;

/**
 * Class used for manage the ToolIntroduction process.
 * This class is used to build up needed models and contains the logic how conditions are checkd 
 * and will update the diagram view if needed.
 */
public class ToolIntroductionManager {
	
	private static boolean TESTMODUS = false;

	private static ToolIntroductionManager instance;
	private static FmmlxDiagram diagram;
	private static String projectName = "ToolIntroductionABC";
	private static String diagramName = "ToolIntroductionDiagramXYZ";
		
	public ToolIntroductionManager(ControlCenter controlCenter) {
		instance = this;
		generateIntroductionDiagram(controlCenter);
	}

	public static ToolIntroductionManager getInstance() {
		if (instance == null) {
			if (TESTMODUS) {
				instance = new ToolIntroductionManager(null);				
			} else {
				throw new NullPointerException("ToolIntroductionManager needs to be first intialized.");				
			}	
		}
		return instance;
	}
	
	public static boolean isInitialized() {
		return (instance != null);
	}

	/**
	 * uses backend function to set up a diagram that is used for the presentation of the tool demo.
	 * @param controlCenter could be used to update the control center (not used right now)
	 */
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
				FmmlxDiagramCommunicator.DiagramType.ClassDiagram, true, diagramID -> {});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

	public void start() {
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(projectName, diagramName);}

	public void checkSucessCondition() {
		// is needed because otherwise the changes of the update are not reflected in
		// the check...just for security if the user clicks fast
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}

		if (new SucessCondition(diagram).checkSucessCondition()) {
			diagram.getViewPane().loadNextStage();
		}
	}

	public void setDiagram(FmmlxDiagram diagram) {
		ToolIntroductionManager.diagram = diagram;
	}
}
