package tool.xmodeler.didactic_ml.learning_unit_managers;

import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.workbench.WorkbenchClient;
import tool.xmodeler.ControlCenterClient;
import tool.xmodeler.didactic_ml.frontend.learning_unit_chooser.LearningUnit;
import tool.xmodeler.didactic_ml.frontend.task_description_viewer.TaskDescriptionViewer;
import tool.xmodeler.didactic_ml.learning_unit_steps.LearningUnitTasks;
import tool.xmodeler.didactic_ml.learning_unit_steps.ToolIntroductionTasks;
import tool.xmodeler.didactic_ml.sucess_conditions.SucessCondition;
import tool.xmodeler.didactic_ml.sucess_conditions.ToolIntroductionConditions;
import xos.Message;
import xos.Value;

/**
 * Class used for manage learning units.
 * This class is used to build up needed models and contains the logic how conditions are checked 
 * and will update the diagram view if needed.
 */
public abstract class LearningUnitManager implements Startable {

	
	// define assumtion, that at one time only on learning unit can be running + say if user uses same name then clash
	
	
	// use this if you want to load models that already fulfill some success
	// conditions
	private static boolean TESTMODUS = true;

	private static LearningUnitManager instance;
	protected static FmmlxDiagram diagram;
	protected static String projectName;
	protected static String diagramName;
	private final TaskDescriptionViewer descriptionViewer = new TaskDescriptionViewer();
	protected static LearningUnit learningUnit;
	protected static SucessCondition sucessCondition;
	
	protected LearningUnitManager() {
		instance = this;	
		createLearningUnitDiagram();
	}
	
	
	//TODO define contract.. every subclass should have own names. this makes it more flexible to define actions only dependent on one lu type
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
	
	public static synchronized <Manager extends LearningUnitManager> Manager getInstance() {
		if (instance == null) {
			if (TESTMODUS) {
				instance = new ToolIntroductionManager();
				instance.getDescriptionViewer().show();
			}
		} else {
			switch (learningUnit) {
			case TOOL_INTRO:
				return (Manager) ToolIntroductionManager.class.cast(instance);

			case CLASSIFICATION_INSTANTIATION:
				return (Manager) ClassificationInstantiationManager.class.cast(instance);

			default:
				break;
			}

			throw new RuntimeException("LearningUnitManager instance needs to be first intialized.");
		}
		return null;
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

		if (sucessCondition.checkSucessCondition()) {
			descriptionViewer.giveUserFeedback(true);
			diagram.getViewPane().loadNextStage();
			String nextDescription = (ToolIntroductionTasks.getTaskDescritpion(diagram.getViewPane().getDiagramViewState()));
			descriptionViewer.loadHtmlContent(nextDescription);
			descriptionViewer.getDescriptionHistory().push(nextDescription);
			descriptionViewer.updateGui();
			//TODO make abstract
			if (ToolIntroductionTasks.getPrecedence(diagram.getViewPane().getDiagramViewState()) == 10) {
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
	}
	
	public void setDiagram(FmmlxDiagram diagram) {
		LearningUnitManager.diagram = diagram;
		sucessCondition.setDiagram(diagram);
	}

	public static FmmlxDiagram getDiagram() {
		return diagram;
	}

	public TaskDescriptionViewer getDescriptionViewer() {
		return descriptionViewer;
	}
}
