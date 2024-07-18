package tool.xmodeler.didactic_ml.learning_unit_managers;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.ControlCenterClient;
import tool.xmodeler.didactic_ml.diagram_preperation_actions.DiagramPreparationActions;
import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;
import tool.xmodeler.didactic_ml.frontend.task_description_viewer.TaskDescriptionViewer;
import tool.xmodeler.didactic_ml.learning_unit_tasks.LearningUnitTasks;
import tool.xmodeler.didactic_ml.sucess_conditions.SuccessCondition;
import xos.Message;
import xos.Value;

/**
 * Class used for manage learning units.
 * This class is used to build up needed models and contains the logic how conditions are checked 
 * and will update the diagram view if needed. At one time there should only be one manager. Manager are defined by a set of project name and diagram name. 
 * Please note that there is a small risk, that a user could create a diagram with the same parameter. This would lead to bugs.
 */
public abstract class LearningUnitManager implements Startable {
	
	// 
	// conditions
	/**
	 * use this if you want to load models that already fulfill some success conditions. It will raise backend exceptions but it still works. 
	 */
	private static boolean TESTMODUS = true;

	private static LearningUnitManager instance;
	protected static FmmlxDiagram diagram;
	protected static String projectName;
	protected static String diagramName;
	private final TaskDescriptionViewer descriptionViewer = new TaskDescriptionViewer();
	protected static LearningUnit learningUnit;
	protected static SuccessCondition sucessCondition;
	/**
	 * If preparation actions are needed this var should be set in the constructor of the subtype.
	 * It is possible that this variable is null while the hole execution of one learning unit. 
	 */
	protected static DiagramPreparationActions preperationActions;
	
	protected LearningUnitManager() {
		if (instance != null) {
			throw new RuntimeException("There should only be one type of Manager at once. When a manager is not used anymore the stop function should be called."
					+ "After this you could instantiate a new instance.");
		}
		instance = this;	
		createLearningUnitDiagram();
	}
	
	/**
	 * Every subclass needs to call this constructor. Please use unique project and diagram names.
	 * This allows more flexibility to distinguish the models and react to them in the code. After you have called them make sure to define the needed components for a manager.
	 * See ToolInstroductionManager as example. 
	 * 
	 * @param projectName defines the project, that is created and used in the learningUnit
	 * @param diagramName defines the diagram, that is created and used in the learningUnit
	 */
	protected LearningUnitManager(String projectName, String diagramName) {
		LearningUnitManager.projectName = projectName;
		LearningUnitManager.diagramName = diagramName;
		LearningUnitManager.instance = this;	
		createLearningUnitDiagram();
	}

	/**
	 * uses backend function to set up a diagram that is used for the presentation of the learning unit.
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

	/**
	 * Returns singelton of this class. Depending on which constructor was called this function returns another subtype.
	 * Be aware that you need to extend this function if you want to add new subclass of {@link LearningUnit}.
	 * 
	 * @return dynamic subtyped instance of LearningUnitManager
	 */
	public static synchronized <Manager extends LearningUnitManager> Manager getInstance() {
		if (instance == null) {
			if (TESTMODUS) {
				instance = new ToolIntroductionManager(); //change type to the LearningUnit you want to test
				instance.getDescriptionViewer().show();
			}
		} else {
			switch (learningUnit) {
			case TOOL_INTRO:
				return (Manager) ToolIntroductionManager.class.cast(instance);

			case CLASSIFICATION_INSTANTIATION:
				return (Manager) ClassificationInstantiationManager.class.cast(instance);

			default:
				throw new RuntimeException("LearningUnitManager instance needs to be first intialized.");
			}
		}
		return null;
	}
	
	
	/**
	 * This function is called to check if the user matches the condition of the current task.
	 * The conditions are defined in the sucessCondition reference of this class.
	 */
	public void checkSucessCondition() {
		// is needed because otherwise the changes of the update are not reflected in
		// the check...just for security if the user clicks fast
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}

		if (sucessCondition.checkSuccessCondition()) {
			descriptionViewer.giveUserFeedback(true);
			diagram.getViewPane().loadNextStage();
			String nextDescription = (LearningUnitTasks.getTaskDescritpion(diagram.getViewPane().getCurrentTaskName()));
			descriptionViewer.loadHtmlContent(nextDescription);
			descriptionViewer.getDescriptionHistory().push(nextDescription);
			descriptionViewer.updateGui();
			if (LearningUnitTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName()) == LearningUnitTasks.getHighestPrecedence()) {
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
	
	public void start() {
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(projectName, diagramName);
		String description = LearningUnitTasks.getFirstTaskDescription();
		descriptionViewer.loadHtmlContent(description); //loads first task description
		descriptionViewer.getDescriptionHistory().push(description);
		descriptionViewer.show();
	}
	
	public static boolean isInitialized() {
		return (instance != null);
	}
	
	//TODO mark lu as passed
	public void stop() {
		ControlCenterClient.removeProject(projectName);
		LearningUnitTasks.tearDown() ;
		instance = null;
		learningUnit = null;
		sucessCondition = null;
		preperationActions = null;
	}
	
	public void setDiagram(FmmlxDiagram diagram) {
		LearningUnitManager.diagram = diagram;
		sucessCondition.setDiagram(diagram);
		diagram.setInLearningUnitMode(true);
	}

	public static FmmlxDiagram getDiagram() {
		return diagram;
	}

	public TaskDescriptionViewer getDescriptionViewer() {
		return descriptionViewer;
	}
	
	public boolean needsPreparationActions() {
		return !(preperationActions == null);
	}

	public static DiagramPreparationActions getPreperationActions() {
		return preperationActions;
	}
}