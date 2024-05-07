package tool.xmodeler.tool_introduction;

import java.util.Vector;

import tool.clients.fmmlxdiagrams.Level;
import tool.clients.fmmlxdiagrams.Level.UnparseableException;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;

/**
 * If in any stage of the tool introduction automatically added elements are
 * needed the functions that perform these actions are defined in this class
 */
public class DiagramPreperationActions {

	public static void prepair(FmmlxDiagram diagram) {
		switch (diagram.getViewPane().getDiagramViewState().getPrecedence()) {
		case 5:
			addCustomerAndTicket(diagram);
			return;

		default:
			return;
		}

	}

	public static void addCustomerAndTicket(FmmlxDiagram diagram) {
		String customerClassName = "Customer";
		addCustomer(diagram, customerClassName);
		String ticketClassName = "Ticket";
		addTicket(diagram, ticketClassName);
		associateCustomerAndTicket(diagram, customerClassName, ticketClassName);
		addCustomerInstance(diagram, customerClassName);
		diagram.updateDiagram();
	}

	private static void addCustomerInstance(FmmlxDiagram diagram, String customerClassName) {
		diagram.getComm().addNewInstance(diagram.getID(), diagram.getClassPath(customerClassName), "customer1",
				getLevelNull(), null, false, false, 400, 0, false);
	}

	private static void associateCustomerAndTicket(FmmlxDiagram diagram, String customerClassName,
			String ticketClassName) {
		Multiplicity targetToSourceMult = new Multiplicity(1, 1, true, false, false);
		Multiplicity sourceToTargetMult = new Multiplicity(0, 1, true, false, false);
		addAssociationOnLevelNull(diagram, customerClassName, ticketClassName, "buys", targetToSourceMult, sourceToTargetMult );
	}
	
	private static void addAssociationOnLevelNull(FmmlxDiagram diagram, String customerClassName, String ticketClassName, String assocName, Multiplicity targetToSourceMult, Multiplicity sourceToTargetMult) {
		diagram.getComm().addAssociation(diagram.getID(), customerClassName, ticketClassName,
				customerClassName.toLowerCase(), ticketClassName.toLowerCase(), assocName,
				diagram.getDefaultAssociation().path, targetToSourceMult, sourceToTargetMult, 0, 0, 0, 0, false, true,
				false, false, null, null, null, null);
	}

	private static void addTicket(FmmlxDiagram diagram, String ticketClassName) {
		createMetaClass(diagram, 1, ticketClassName, new int[]{0, 0});
		createAttributeOnLevelNull(diagram, ticketClassName, "price", "Float");
	}

	private static void addCustomer(FmmlxDiagram diagram, String customerClassName) {
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