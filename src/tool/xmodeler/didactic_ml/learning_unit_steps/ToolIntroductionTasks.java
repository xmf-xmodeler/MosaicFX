package tool.xmodeler.didactic_ml.learning_unit_steps;

import static java.util.Map.entry;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

//TODO -> watch out. special lu because of gui adaption
/**
 * This enum is used in the DiagramViewIntroduction. There different states of
 * the diagramView are needed to help new users to deal with the XModeler. The
 * state defines what the user can see. It is passed through the different
 * frontend parts of the diagramView to create a matching frontend.
 */
public class ToolIntroductionTasks extends LearningUnitTasks {

	public ToolIntroductionTasks() {
		super("ToolIntroduction");
		tasks = Map.ofEntries(Map.entry("CREATE_CLASS_MOVIE", 1), Map.entry("ADD_ATTRIBUTES_TO_MOVIE", 2),
				Map.entry("CREATE_CLASS_MOVIE_SHOWING", 3), Map.entry("ADD_ASSOC_BETWEEN_MOVIE_AND_SHOWING", 4),
				Map.entry("ADD_LINK_BETWEEN_MOVIEINST_AND_SHOWINGINST", 5), Map.entry("CHANGE_CARDINALITY_OF_BUYS", 6),
				Map.entry("ADD_RATING_ENUM", 7), Map.entry("CHANGE_DATATYPE_TO_ENUM", 8),
				Map.entry("GET_REQUIRED_AGE_FUN_IS_ADDED", 9), Map.entry("CONSTRAINT_IS_ADD", 10),
				Map.entry("FULL_GUI", 100));
	}

	/**
	 * The gui is build consecutive. So the next gui needs all elements of the gui
	 * before. This is used for not replicate code. The full gui is defined for the
	 * value 100. In between all states can be inserted.
	 */

}