package tool.xmodeler.didactic_ml.diagram_preperation_actions;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.Level;
import tool.clients.fmmlxdiagrams.Level.UnparseableException;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.xmodeler.didactic_ml.learning_unit_tasks.ToolIntroductionTasks;

/**
 * If in any stage of the tool introduction automatically added elements are
 * needed the functions that perform these actions are defined in this class
 */
public class ToolIntroductionPreperation extends DiagramPreperationActions {

	//TODO make abstract
	
	/**
	 * Prepairs the diagram for the next task. The case must be the precedence before the task description.
	 * If in task 6 things should be there, the case must be 5.
	 * @param diagram
	 */
	public static void prepair(FmmlxDiagram diagram) {
		switch (ToolIntroductionTasks.getPrecedence(diagram.getViewPane().getCurrentTaskName())) {
		case 5:
			addCustomerAndTicket(diagram);
			return;
		case 8:
			addReturnAgeForRatingFun(diagram);
			return;
		case 9:
			addTicketConstrain(diagram);
			return;

		default:
			return;
		}

	}

	private static void addTicketConstrain(FmmlxDiagram diagram) {
		diagram.getComm().addConstraintAsync(diagram.getID(),
				"Root::ToolIntroductionABC::Ticket",
				"mayWatchMovie",
				0,
				"self.price <> 10.6",
				"\"Customer not allowed to watch the movie.\"");
		
	}

	private static void addReturnAgeForRatingFun(FmmlxDiagram diagram) {
		String funBody = "@Operation requiredAgeToWatch1	[monitor=true,delToClassAllowed=false]():XCore::Integer\r\n"
				+ "  if rating = Root::ToolIntroductionABC::RatingEnum.getEnumElement(\"PG_13\")\r\n"
				+ "  then 13\r\n"
				+ "  elseif rating = Root::ToolIntroductionABC::RatingEnum.getEnumElement(\"R\")\r\n"
				+ "  then 17\r\n"
				+ "  else 0\r\n"
				+ "  end \r\n"
				+ "end\r\n"
				+ "";
		diagram.getComm().addOperation(diagram.getID(), diagram.getClassPath("Movie"), 0, funBody);	
	}

	public static void addCustomerAndTicket(FmmlxDiagram diagram) {
		String customerClassName = "Customer";
		addCustomerClass(diagram, customerClassName);
		String ticketClassName = "Ticket";
		addTicket(diagram, ticketClassName);
		addAssociations(diagram, customerClassName, ticketClassName);
		addInstances(diagram, customerClassName, ticketClassName);
		addLinks(diagram);
		diagram.updateDiagram();
	}

	private static void addInstances(FmmlxDiagram diagram, String customerClassName, String ticketClassName) {
		addCustomerInstance(diagram, customerClassName);
		addMovieShwoingInstance(diagram);
		addTicketInstance(diagram, ticketClassName);
		addMovieInstance(diagram);
	}

	private static void addAssociations(FmmlxDiagram diagram, String customerClassName, String ticketClassName) {
		associateCustomerAndTicket(diagram, customerClassName, ticketClassName);
		associateMovieShowingAndTicket(diagram, ticketClassName);
	}

	private static void addLinks(FmmlxDiagram diagram) {
		diagram.getComm().addLink(diagram.getID(), "customer1", "ticket1", "ticket");
		diagram.getComm().addLink(diagram.getID(), "ticket1", "movieShowing2", "valid_for");
		diagram.getComm().addLink(diagram.getID(), "movieShowing2", "movie2", "shown_in");
	}

	private static void addMovieShwoingInstance(FmmlxDiagram diagram) {
		String instanceName = "movieShowing2";
		diagram.getComm().addNewInstance(diagram.getID(), diagram.getClassPath("MovieShowing"), instanceName,
				getLevelNull(), new Vector<>(), false, false, 800, 250, false);
		diagram.updateDiagram();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		updateMovieShowing2Slots(diagram, instanceName);
		
	}

	private static void updateMovieShowing2Slots(FmmlxDiagram diagram, String instanceName) {
		FmmlxObject obj = diagram.getObjectByName(instanceName);
		updateSlot(diagram, obj, "showDate", "Date::createDate(2024,06,03)");
		
	}

	private static void addMovieInstance(FmmlxDiagram diagram) {
		String instanceName = "movie2";
		diagram.getComm().addNewInstance(diagram.getID(), diagram.getClassPath("Movie"), instanceName,
				getLevelNull(), new Vector<>(), false, false, 150, 600, false);
		diagram.updateDiagram();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		updateMovie2Slots(diagram, instanceName);
	}

	private static void updateMovie2Slots(FmmlxDiagram diagram, String instanceName) {
		FmmlxObject obj = diagram.getObjectByName(instanceName);
		updateSlot(diagram, obj, "title", "\"Titanic\"");
		updateSlot(diagram, obj, "durationInMinutes", "195");
	}

	private static void associateMovieShowingAndTicket(FmmlxDiagram diagram, String ticketClassName) {
			Multiplicity targetToSourceMult = new Multiplicity(1, 1, true, false, false);
			Multiplicity sourceToTargetMult = new Multiplicity(0, 0, false, false, false);
			addAssociationOnLevelNull(diagram, ticketClassName, "MovieShowing", "valid_for", targetToSourceMult, sourceToTargetMult); 
	}

	private static void addTicketInstance(FmmlxDiagram diagram, String ticketClassName) {
		String instanceName = "ticket1";
		diagram.getComm().addNewInstance(diagram.getID(), diagram.getClassPath(ticketClassName), instanceName,
				getLevelNull(), new Vector<>(), false, false, 50, 400, false);
		diagram.updateDiagram();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		updateTicket1Slots(diagram, instanceName);
	}

	private static void updateTicket1Slots(FmmlxDiagram diagram, String instanceName) {
		FmmlxObject obj = diagram.getObjectByName(instanceName);
		updateSlot(diagram, obj, "price", "10.60");
	}

	private static void addCustomerInstance(FmmlxDiagram diagram, String customerClassName) {
		String instanceName = "customer1";
		diagram.getComm().addNewInstance(diagram.getID(), diagram.getClassPath(customerClassName), instanceName,
				getLevelNull(), new Vector<>(), false, false, 600, 300, false);
		diagram.updateDiagram();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		updateCustomer1Slots(diagram, instanceName);
	}

	private static void updateCustomer1Slots(FmmlxDiagram diagram, String instanceName) {
		FmmlxObject obj = diagram.getObjectByName(instanceName);
		updateSlot(diagram, obj, "firstName", "\"Josie\"");
		updateSlot(diagram, obj, "lastName", "\"Dough\"");
		updateSlot(diagram, obj, "dateOfBirth", "Date::createDate(2010,06,17)");
	}



	private static void updateSlot(FmmlxDiagram diagram, FmmlxObject obj, String slotName, String newValue) {
		FmmlxSlot slot = obj.getSlot(slotName);
		diagram.getComm().changeSlotValue(diagram.getID(), obj.getName(), slot.getName(), newValue);
	}

	private static void associateCustomerAndTicket(FmmlxDiagram diagram, String customerClassName,
			String ticketClassName) {
		Multiplicity targetToSourceMult = new Multiplicity(1, 1, true, false, false);
		Multiplicity sourceToTargetMult = new Multiplicity(0, 1, true, false, false);
		addAssociationOnLevelNull(diagram, customerClassName, ticketClassName, "buys", targetToSourceMult, sourceToTargetMult );
	}
	
	private static void addAssociationOnLevelNull(FmmlxDiagram diagram, String sourceName, String targetName, String assocName, Multiplicity targetToSourceMult, Multiplicity sourceToTargetMult) {
		diagram.getComm().addAssociation(diagram.getID(), sourceName, targetName,
				sourceName.toLowerCase(), targetName.toLowerCase(), assocName,
				diagram.getDefaultAssociation().path, targetToSourceMult, sourceToTargetMult, 0, 0, 0, 0, false, true,
				false, false, null, null, null, null);
	}

	private static void addTicket(FmmlxDiagram diagram, String ticketClassName) {
		createMetaClass(diagram, 1, ticketClassName, new int[]{0, 0});
		createAttributeOnLevelNull(diagram, ticketClassName, "price", "Float");
	}

	private static void addCustomerClass(FmmlxDiagram diagram, String customerClassName) {
		createMetaClass(diagram, 1, customerClassName, new int[]{400, 0});
		createAttributeOnLevelNull(diagram, customerClassName, "firstName", "String");
		createAttributeOnLevelNull(diagram, customerClassName, "lastName", "String");
		createAttributeOnLevelNull(diagram, customerClassName, "dateOfBirth", "Date");
		addGetAgeFunction(diagram, customerClassName);
	}

	private static void addGetAgeFunction(FmmlxDiagram diagram, String customerClassName) {
		String funBody = "@Operation getAge[monitor=true]():XCore::Integer if self.dateOfBirth <> null then self.dateOfBirth.age() else \"No date of birth entered\" end end";
		diagram.getComm().addOperation(diagram.getID(), diagram.getClassPath(customerClassName), 0, funBody);
	}

	private static void createAttributeOnLevelNull(FmmlxDiagram diagram, String className, String attName,
			String type) {
		Multiplicity multOne = new tool.clients.fmmlxdiagrams.Multiplicity(1, 1, true, false, false);
		diagram.getComm().addAttribute(diagram.getID(), diagram.getClassPath(className), attName, getLevelNull(), type,
				multOne, true, false, false);
	}

	private static Level getLevelNull() {
		Level levelNull;
		try {
			levelNull = Level.parseLevel("0");
		} catch (UnparseableException e) {
			throw new RuntimeException(e);
		}
		return levelNull;
	}

	// TODO: This function is copied from test.utils please reference this. Call the other function with overloading.
	public static void createMetaClass(FmmlxDiagram diagram, Integer level, String className, int[] position) {
		Level classLevel = null;
		try {
			classLevel = Level.parseLevel(level.toString());
		} catch (UnparseableException e) {
			throw new RuntimeException(e);
		}
		diagram.getComm().addMetaClass(diagram.getID(), className, classLevel, new Vector<>(), false, false, position[0], position[1],
				false);
	}
}