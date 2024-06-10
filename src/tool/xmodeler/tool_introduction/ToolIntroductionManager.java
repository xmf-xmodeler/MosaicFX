package tool.xmodeler.tool_introduction;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.ControlCenter;
import xos.Message;
import xos.Value;

/**
 * Class used for manage the ToolIntroduction process.
 * This class is used to build up needed models and contains the logic how conditions are checked 
 * and will update the diagram view if needed.
 */
public class ToolIntroductionManager {
	
	//use this if you want to load models that already fulfill some success conditions
	private static boolean TESTMODUS = true;

	private static ToolIntroductionManager instance;
	private static FmmlxDiagram diagram;
	private static String projectName = "ToolIntroductionABC";
	private static String diagramName = "ToolIntroductionDiagramXYZ";
	private final TaskDescriptionViewer descriptionViewer = new TaskDescriptionViewer();
		
	public ToolIntroductionManager(ControlCenter controlCenter) {
		instance = this;
		generateIntroductionDiagram(controlCenter);
	}

	public static ToolIntroductionManager getInstance() {
		if (instance == null) {
			if (TESTMODUS) {
				instance = new ToolIntroductionManager(null);
				instance.getDescriptionViewer().show();
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
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(projectName, diagramName);
		String description = DiagramViewState.CREATE_CLASS_MOVIE.getTaskDescritpion();
		descriptionViewer.loadHtmlContent(description); //loads first task description
		descriptionViewer.getDescriptionHistory().push(description);
		descriptionViewer.show();
	}
	
	public void stop() {
		instance = null;
	}

	public void checkSucessCondition() {
		// is needed because otherwise the changes of the update are not reflected in
		// the check...just for security if the user clicks fast
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}

		if (new SucessCondition(diagram).checkSucessCondition()) {
			descriptionViewer.giveUserFeedback(true);
			diagram.getViewPane().loadNextStage();
			String nextDescription = (diagram.getViewPane().getDiagramViewState().getTaskDescritpion());
			descriptionViewer.loadHtmlContent(nextDescription);
			descriptionViewer.getDescriptionHistory().push(nextDescription);
			descriptionViewer.updateGui();
			if (diagram.getViewPane().getDiagramViewState().getPrecedence() == 10) {
				//this time is needed to survive the user feedback
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				descriptionViewer.alterCheckButtonText();
			}
		} else {
			descriptionViewer.giveUserFeedback(false);
		}
	}

	public void setDiagram(FmmlxDiagram diagram) {
		ToolIntroductionManager.diagram = diagram;
	}

	public static FmmlxDiagram getDiagram() {
		return diagram;
	}

	public TaskDescriptionViewer getDescriptionViewer() {
		return descriptionViewer;
	}
}
