package tool.xmodeler.didactic_ml.learning_unit_managers;

/**
 * Class used for manage the ToolIntroduction process.
 * This class is used to build up needed models and contains the logic how conditions are checked 
 * and will update the diagram view if needed.
 */
public class ToolIntroductionManager extends LearningUnitManager {

	//TODO refactor. define in the super contructor for the call of class intor, the altering of the names
	public ToolIntroductionManager() {
		super("ToolIntroductionABC", "ToolIntroductionDiagramXYZ");
	}

	public static ToolIntroductionManager getInstance() {
		return LearningUnitManager.getInstance(ToolIntroductionManager.class);
	}	
}