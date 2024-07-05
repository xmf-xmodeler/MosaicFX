package tool.xmodeler.didactic_ml.learning_unit_managers;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.ControlCenterClient;
import tool.xmodeler.didactic_ml.DiagramViewState;
import tool.xmodeler.didactic_ml.frontend.task_description_viewer.TaskDescriptionViewer;
import tool.xmodeler.didactic_ml.sucess_conditions.SucessCondition;
import tool.xmodeler.didactic_ml.sucess_conditions.ToolIntroductionConditions;
import xos.Message;
import xos.Value;

public abstract class LearningUnitManager {

	
	// define assumtion, that at one time only on learning unit can be running + say if user uses same name then clash
	
	
	// use this if you want to load models that already fulfill some success
	// conditions
	private static boolean TESTMODUS = true;

	private static LearningUnitManager instance;
	private static FmmlxDiagram diagram;
	protected static String projectName = "LearningUnitXYZ";
	protected static String diagramName = "LearningUnitDiagramXYZ";
	private final TaskDescriptionViewer descriptionViewer = new TaskDescriptionViewer();

	protected LearningUnitManager() {
		instance = this;	
		createLearningUnitDiagram();
	}
	
	/**
	 * Used to differentiate ToolIntro.. needed because of frontend adaption
	 * @param projectName
	 * @param diagramName
	 */
	protected LearningUnitManager(String projectName, String diagramName) {
		this.projectName = projectName;
		this.diagramName = diagramName;
		instance = this;	
		createLearningUnitDiagram();
	}



	/**
	 * uses backend function to set up a diagram that is used for the presentation
	 * of the tool demo.
	 */
	protected void createLearningUnitDiagram() {
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
				});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

	protected static synchronized <Manager extends LearningUnitManager> Manager getInstance(Class<Manager> clazz) {
		if (instance == null) {
			if (TESTMODUS) {
				instance = new ToolIntroductionManager();
				instance.getDescriptionViewer().show();
			} else {
				throw new RuntimeException("LearningUnitManager instance needs to be first intialized.");				
			}	
		}
		return clazz.cast(instance);
	}
	
	//TODO make abstract
	public void checkSucessCondition() {
		// is needed because otherwise the changes of the update are not reflected in
		// the check...just for security if the user clicks fast
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}

		if (new ToolIntroductionConditions(diagram).checkSucessCondition()) {
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
	
	//TODO make abstract
	public void start() {
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(projectName, diagramName);
		String description = DiagramViewState.CREATE_CLASS_MOVIE.getTaskDescritpion();
		descriptionViewer.loadHtmlContent(description); //loads first task description
		descriptionViewer.getDescriptionHistory().push(description);
		descriptionViewer.show();
	}
	
	public static boolean isInitialized() {
		return (instance != null);
	}
	
	public void stop() {
		ControlCenterClient.removeProject(projectName);
		instance = null;
	}
	
	public void setDiagram(FmmlxDiagram diagram) {
		LearningUnitManager.diagram = diagram;
	}

	public static FmmlxDiagram getDiagram() {
		return diagram;
	}

	public TaskDescriptionViewer getDescriptionViewer() {
		return descriptionViewer;
	}
}
