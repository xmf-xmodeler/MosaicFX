package tool.xmodeler.didactic_ml.self_assessment_test_tasks.tool_intro;

import java.util.HashMap;
import java.util.Map;

import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import tool.xmodeler.didactic_ml.self_assessment_test_tasks.SelfAssessmentTestTasks;

/**
 * This subclass differs in that, that for the tool intro different frontend
 * states are defined. The state defines what the user can see. It is passed
 * through the different frontend parts of the diagramView to create a matching
 * frontend. The gui is build consecutive. So the next gui needs all elements of
 * the gui before. This is used for not replicate code. The full gui is defined
 * for the value 100.
 */
public class ToolIntroductionTasks extends SelfAssessmentTestTasks {
	
	public ToolIntroductionTasks() {
		super(SelfAssessmentTest.TOOL_INTRO);
	}

	@Override
	public void init() {
		tasks = new HashMap<>();
		tasks.put("CREATE_CLASS_MOVIE", 1);
		tasks.put("ADD_ATTRIBUTES_TO_MOVIE", 2);
		tasks.put("CREATE_CLASS_MOVIE_SHOWING", 3);
		tasks.put("ADD_ASSOC_BETWEEN_MOVIE_AND_SHOWING", 4);
		tasks.put("ADD_LINK_BETWEEN_MOVIEINST_AND_SHOWINGINST", 5); 
		tasks.put("CHANGE_CARDINALITY_OF_BUYS", 6);
		tasks.put("ADD_RATING_ENUM", 7);
		tasks.put("CHANGE_DATATYPE_TO_ENUM", 8);
		tasks.put("GET_REQUIRED_AGE_FUN_IS_ADDED", 9); 
		tasks.put("CONSTRAINT_IS_ADD", 10);
		tasks.put("FULL_GUI", 100);
	}
}