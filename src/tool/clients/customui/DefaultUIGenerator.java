package tool.clients.customui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;

public class DefaultUIGenerator {

	public HashMap<String, Map<String, String>> instantiateCustomGUI(Vector<FmmlxObject> objects,
			Vector<FmmlxAssociation> associations, AbstractPackageViewer diagram, DiagramActions actions,
			String pathIcon, String pathGUI, String titleGUI, Vector<FmmlxObject> roots, int distance) {

		// for the fxml export
		int rowCount = 0;

		// instance of customGUI
		String guiInstanceName = actions.addInstance("UserInterface",
				"gui" + UUID.randomUUID().toString().replace("-", ""), false);

		// objects for customGUI
		Vector<FmmlxObject> objectsCommonClass = new Vector<FmmlxObject>();

		String referenceInstanceName;
		String injectionInstanceName;
		String actionInstanceName;
		String virtualInstanceName;
		String parameterInstanceName;

		Vector<FmmlxAttribute> attributes;
		Vector<FmmlxOperation> operations;

		String multiplicity;
		char endChar;

		// Slot Values that can already be determined in this method should be saved for
		// performance
		// first String -> instanceName
		// second String -> slotName
		// third String -> value
		HashMap<String, Map<String, String>> slotValues = new HashMap<String, Map<String, String>>();
		HashMap<String, String> helper = new HashMap<String, String>();

		// used if the commonClass needs a listInjection or not
		Boolean isList;
		String isHead = "";

		if (!roots.isEmpty()) {
			for (FmmlxObject root : roots) {
				// add recursively by root

				// wenn keine distanz gegeben ist -> head ist relevant für kreise aber nicht um
				// rekursiv zu suchen
				if (distance > 0) {
					objects = this.recurGetObjectsForGUI(objects, root, distance);
				} else {
					objects.addAll(roots);
				}
				// add needed assocs
				for (FmmlxObject o : objects) {
					for (FmmlxAssociation a : o.getAllRelatedAssociations()) {
						if (!associations.contains(a))
							associations.add(a);
					}

				}
			}
		}

		boolean isActionInjection;

		for (FmmlxObject o : objects) {
			if (o.getMetaClassName().equals("CommonClass"))
				objectsCommonClass.add(o);
		}

		// get associations that are mapped
		FmmlxAssociation associationDerivedFrom = diagram
				.getAssociationByPath(diagram.getPackagePath() + "::derivedFrom");
		FmmlxAssociation associationComposedOf = diagram
				.getAssociationByPath(diagram.getPackagePath() + "::composedOf");
		FmmlxAssociation associationRefersToStateOf = diagram
				.getAssociationByPath(diagram.getPackagePath() + "::refersToStateOf");
		FmmlxAssociation associationIsParent = diagram.getAssociationByPath(diagram.getPackagePath() + "::isParent");
		FmmlxAssociation associationIsChild = diagram.getAssociationByPath(diagram.getPackagePath() + "::isChild");
		FmmlxAssociation associationUses = diagram.getAssociationByPath(diagram.getPackagePath() + "::uses");
		FmmlxAssociation associationRepresentedAs = diagram
				.getAssociationByPath(diagram.getPackagePath() + "::representedAs");

		// create standard GUI
		GridPane rechteSeiteGrid = new GridPane();
		rechteSeiteGrid.setHgap(3);
		rechteSeiteGrid.setVgap(3);
		rechteSeiteGrid.setPadding(new Insets(3, 3, 3, 3));

		// instantiate references and injections for domain classes

		ArrayList<Reference> referenceMapping = new ArrayList<>();
		Boolean head;

		Boolean atLeastOneHead = false;

		// wenn keine root explizit gegeben -> dann sind alle associationen gegeben
		// get all needed references
		for (FmmlxObject o : objectsCommonClass) {
			head = true;
			for (FmmlxAssociation assoc : associations) {
				if (assoc.getTargetNode().equals(o)) {

					head = false;
					referenceInstanceName = actions.addInstance("Reference",
							"ref" + UUID.randomUUID().toString().replace("-", ""));

					referenceMapping.add(new Reference(o, assoc, referenceInstanceName, false,
							new Reference(assoc.getSourceNode())));
				}
			}

			// wenn root -> dann auch head
			if (!head && roots.contains(o)) {
				head = true;
			}

			if (head) {
				referenceInstanceName = actions.addInstance("Reference",
						"ref" + UUID.randomUUID().toString().replace("-", ""));
				referenceMapping.add(new Reference(o, null, referenceInstanceName, true));
				atLeastOneHead = true;
			}
		}

		// TODO change to alert for user not console
		if (!atLeastOneHead) {
			System.err.println("Head has not been set explicitly and no head could be detected\n"
					+ "One reason for that could be a circle in your model.\n" + "You have to set a head manually");

			return slotValues;

		}

		// mapping von parent
		for (Reference reference : referenceMapping) {

			boolean parentSet = false;
			if (reference.isHead()) {
				continue;
			}

			for (Reference reference2 : referenceMapping) {

				if (reference2.equals(reference)) {
					continue;
				}

				// wenn parent auf head verweist
				if (reference2.getObject().equals(reference.getParent().getObject()) && reference2.getAssoc() == null
						&& parentSet == false) {
					reference.setParent(reference2);
					parentSet = true;
					System.err.println(reference.getReferenceInstanceName() + " hat als parent head ("
							+ reference2.getReferenceInstanceName() + ")bekommen");
				}
			}

			// wenn parent nicht auf head verweist -> mapping auf ein beliebiges mögliches
			// parent objekt
			// beliebig klingt drastisch, ist es aber nicht
			if (!parentSet) {
				for (Reference reference2 : referenceMapping) {
					if (reference2.isHead()) {
						continue;
					}
					if (!parentSet && reference2.getObject().equals(reference.getParent().getObject())) {
						reference.setParent(reference2);
						parentSet = true;
						System.err.println(reference.getReferenceInstanceName() + " hat als parent ("
								+ reference2.getReferenceInstanceName() + ")bekommen");
					}
				}

			}

			if (!parentSet) {
				System.err.println("kein parent fuer " + reference.getReferenceInstanceName());
			}

		}

		for (Reference reference : referenceMapping) {

			// add attributes to reference

			if (reference.isHead()) {
				isList = true;
				isHead = "true";
				helper.put("associationName", "");
				helper.put("isHead", isHead);

			} else {

				multiplicity = reference.getAssoc().getMultiplicityStartToEnd().toString();
				endChar = multiplicity.charAt(multiplicity.length() - 1);
				isHead = "false";

				if (endChar == '*') {
					isList = true;
				} else {
					int a = Character.getNumericValue(endChar);
					isList = (a > 1) ? true : false;
				}

				helper.put("associationName", reference.getAssoc().getName());
				helper.put("isHead", isHead);
			}

			slotValues.put(reference.getReferenceInstanceName(), (Map<String, String>) helper.clone());
			helper.clear();

			// take care of parent child

			// add link "refersToStateOf" -> Reference + CommonClassInstance ANNAHME:
			// Jede gewählte CommonClass hat mind. 1 Instanz.
			// TODO: Prio 3 -> Wie damit umgehen, wenn keine Instanz existiert
			// TODO: Was ist wenn die Instanz nicht den richtigen
			actions.addAssociation(reference.getReferenceInstanceName(),
					reference.getObject().getInstances().get(0).getName(), associationRefersToStateOf.getName());

			Label instancesOfClassLabel = new Label("Instance(s) of " + reference.getObject().getName());
			instancesOfClassLabel.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR,
					Font.getDefault().getSize()));

			// beschreibung der assoziation
			if (!reference.isHead()) {
				Label assocDesc = new Label("Association: " + reference.getAssoc().getSourceNode().getName().toString()
						+ " " + reference.getAssoc().getName().toString() + " "
						+ reference.getAssoc().getTargetNode().toString());
				rechteSeiteGrid.add(assocDesc, 0, rowCount++);
			} else { // bezeichnung head hinzufügen
				instancesOfClassLabel.setText(instancesOfClassLabel.getText() + " (head)");
			}

			rechteSeiteGrid.add(instancesOfClassLabel, 0, rowCount++);

			if (isList) {
				injectionInstanceName = actions.addInstance("ListInjection",
						"list" + UUID.randomUUID().toString().replace("-", ""));

				actions.addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());
				actions.addAssociation(injectionInstanceName, reference.getReferenceInstanceName(),
						associationDerivedFrom.getName());

				helper.put("isListView", "true");
				helper.put("nameOfModelElement", reference.getObject().getName());
				helper.put("idOfUIElement", injectionInstanceName);

				slotValues.put(injectionInstanceName, (Map<String, String>) helper.clone());
				helper.clear();

				ListView<String> objectListView = new ListView<>();
				objectListView.setId(injectionInstanceName);
				rechteSeiteGrid.add(objectListView, 0, rowCount++);
			}

			attributes = reference.getObject().getAllAttributes();
			operations = reference.getObject().getAllOperations();

			if (attributes.size() > 0) {
				rechteSeiteGrid.add(new Label("Slots:"), 0, rowCount++);
			}

			for (FmmlxAttribute attribute : attributes) {
				injectionInstanceName = actions.addInstance("SlotInjection",
						"slot" + UUID.randomUUID().toString().replace("-", ""));

				helper.put("idOfUIElement", injectionInstanceName);
				helper.put("nameOfModelElement", attribute.getName());
				slotValues.put(injectionInstanceName, (Map<String, String>) helper.clone());
				helper.clear();

				actions.addAssociation(injectionInstanceName, reference.getReferenceInstanceName(),
						associationDerivedFrom.getName());
				actions.addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());

				TextField valueTextField = new TextField(attribute.getName());
				valueTextField.setId(injectionInstanceName);
				valueTextField.setEditable(false);
				Label slotName = new Label(attribute.getName());

				rechteSeiteGrid.add(slotName, 0, rowCount);
				rechteSeiteGrid.add(valueTextField, 1, rowCount);
				rowCount++;
			}

			if (operations.size() > 0) {
				rechteSeiteGrid.add(new Label("Operations:"), 0, rowCount++);
			}

			// add actionInjections for operations
			for (FmmlxOperation operation : operations) {

				// if method is monitor than action; otherwise acttionInjection //
				// TODO: maybe find something better more robust approach

				String body = operation.getBody();
				isActionInjection = body.contains("monitor=true") ? true : false;

				helper.put("nameOfModelElement", operation.getName());

				// if action
				if (isActionInjection) {
					injectionInstanceName = actions.addInstance("ActionInjection",
							"actInj" + UUID.randomUUID().toString().replace("-", ""));
					helper.put("idOfUIElement", injectionInstanceName);

					slotValues.put(injectionInstanceName, (Map<String, String>) helper.clone());
					helper.clear();

					// add associations
					actions.addAssociation(injectionInstanceName, reference.getReferenceInstanceName(),
							associationDerivedFrom.getName());
					actions.addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());

					// standard GUI
					Label actionValue = new Label("");
					actionValue.setId(injectionInstanceName);

					rechteSeiteGrid.add(new Label(operation.getName()), 0, rowCount);
					rechteSeiteGrid.add(actionValue, 1, rowCount);
					rowCount++;

				} else {
					actionInstanceName = actions.addInstance("Action", "act" +

							UUID.randomUUID().toString().replace("-", ""));
					helper.put("idOfUIElement", actionInstanceName);

					slotValues.put(actionInstanceName, (Map<String, String>) helper.clone());
					helper.clear();

					// add associations
					actions.addAssociation(actionInstanceName, reference.getReferenceInstanceName(),
							associationDerivedFrom.getName());

					int paramCounter = 0;
					Button wertAendern = new Button(operation.getName());

					rechteSeiteGrid.add(wertAendern, 1, rowCount);
					wertAendern.setId(actionInstanceName);
					rowCount++;

					Vector<String> paramNames = operation.getParamNames();

					// parameters
					for (String paramType : operation.getParamTypes()) {
						paramCounter++;
						parameterInstanceName = actions.addInstance("Parameter",
								"par" + UUID.randomUUID().toString().replace("-", ""));
						actions.addAssociation(parameterInstanceName, actionInstanceName, associationUses.getName());

						paramType = paramType.substring(paramType.lastIndexOf("::") + 2);

						helper.put("dataType", paramType);
						helper.put("orderNo", String.valueOf(paramCounter));
						helper.put("value", "");
						slotValues.put(parameterInstanceName, (Map<String, String>) helper.clone());
						helper.clear();

						// virtual for every parameter
						virtualInstanceName = actions.addInstance("Virtual",
								"vir" + UUID.randomUUID().toString().replace("-", ""));
						helper.put("idOfUIElement", virtualInstanceName);
						slotValues.put(virtualInstanceName, (Map<String, String>) helper.clone());
						helper.clear();
						actions.addAssociation(virtualInstanceName, parameterInstanceName,
								associationRepresentedAs.getName());

						// standard GUI
						TextField valueTextField = new TextField(operation.getName());
						valueTextField.setId(virtualInstanceName);

						rechteSeiteGrid.add(new Label(paramNames.get(paramCounter - 1).toString()), 0, rowCount);
						rechteSeiteGrid.add(valueTextField, 1, rowCount);
						rowCount++;

					}
				}
			}
		} // end for references

		// mapping isChild and isParent relations
		for (Reference reference : referenceMapping) {

			if (reference.isHead())
				continue;

			actions.addAssociation(reference.getReferenceInstanceName(),
					reference.getParent().getReferenceInstanceName(), associationIsParent.getName());
			actions.addAssociation(reference.getParent().getReferenceInstanceName(),
					reference.getReferenceInstanceName(), associationIsChild.getName());
		}

		// export standard gui
		FXMLExporter exporter = new FXMLExporter(pathGUI);
		exporter.export(rechteSeiteGrid);

		helper.put("pathToFXML", pathGUI);
		helper.put("titleOfUI", titleGUI);
		helper.put("pathToIconOfWindow", pathIcon);

		slotValues.put(guiInstanceName, (Map<String, String>) helper.clone());
		helper.clear();

		return slotValues;
	}

	// recursive function to get Objects for CustomgUI
	public Vector<FmmlxObject> recurGetObjectsForGUI(Vector<FmmlxObject> vector, FmmlxObject root, int depth) {
		// only go to certain depth
		if (!(depth > 0))
			return vector;
		depth--;

		// add root if not added already
		if (!vector.contains(root))
			vector.add(root);

		// can this be done better ? - look in both directions
		if (root.getAllRelatedAssociations().size() > 0) {
			for (FmmlxAssociation assoc : root.getAllRelatedAssociations()) {
				// vector = recurGetObjectsForGUI(vector, assoc.getSourceNode(), depth);
				if (root.equals(assoc.getSourceNode())) {
					vector = recurGetObjectsForGUI(vector, assoc.getTargetNode(), depth);
				}
			}
		} else {
			// if no associations then cancel
			depth = 0;
		}

		return vector;
	}

	public void addSlotValuesCustomGUI(HashMap<String, Map<String, String>> slotValues, AbstractPackageViewer diagram) {
		// TODO refactor this

		Vector<FmmlxObject> references = new Vector<>();
		Vector<FmmlxObject> slotInjections = new Vector<>();
		Vector<FmmlxObject> actionInjections = new Vector<>();
		Vector<FmmlxObject> listInjections = new Vector<>();
		Vector<FmmlxObject> parameters = new Vector<>();
		Vector<FmmlxObject> virtuals = new Vector<>();
		Vector<FmmlxObject> actions = new Vector<>();
		Vector<FmmlxObject> customGuiInterface = new Vector<>();

		Vector<FmmlxObject> objects = diagram.getObjects();

		for (FmmlxObject o : objects) {

			if (o.getMetaClassName().equals("Reference"))
				references.add(o);
			if (o.getMetaClassName().equals("SlotInjection"))
				slotInjections.add(o);
			if (o.getMetaClassName().equals("ActionInjection"))
				actionInjections.add(o);
			if (o.getMetaClassName().equals("ListInjection"))
				listInjections.add(o);
			if (o.getMetaClassName().equals("Action"))
				actions.add(o);
			if (o.getMetaClassName().equals("Parameter"))
				parameters.add(o);
			if (o.getMetaClassName().equals("Virtual"))
				virtuals.add(o);
			if (o.getMetaClassName().equals("UserInterface"))
				customGuiInterface.add(o);

		}

		for (FmmlxObject o : customGuiInterface) {
			if (slotValues.containsKey(o.getName())) {
				String path = slotValues.get(o.getName()).get("pathToFXML");
				path = path.replace("\\", "\\\\");

				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "pathToFXML", "\"" + path + "\"");

				// auto icon
				String pathIcon = slotValues.get(o.getName()).get("pathToIconOfWindow");
				pathIcon = pathIcon.replace("\\", "\\\\");

				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "pathToIconOfWindow",
						"\"" + pathIcon + "\"");

				String UIName = slotValues.get(o.getName()).get("titleOfUI");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "titleOfUI", "\"" + UIName + "\"");
			}
		}

		for (FmmlxObject o : slotInjections) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "idOfUIElement",
						"\"" + slotValues.get(o.getName()).get("idOfUIElement") + "\"");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "nameOfModelElement",
						"\"" + slotValues.get(o.getName()).get("nameOfModelElement") + "\"");
			}
		}

		for (FmmlxObject o : references) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "associationName",
						"\"" + slotValues.get(o.getName()).get("associationName") + "\"");

				if (slotValues.get(o.getName()).get("isHead").equals("true")) {
					diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "isHead", "true");
				} else {
					diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "isHead", "false");
				}
			}
		}

		for (FmmlxObject o : actionInjections) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "eventName", "\"" + "ACTION" + "\"");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "idOfUIElement",
						"\"" + slotValues.get(o.getName()).get("idOfUIElement") + "\"");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "nameOfModelElement",
						"\"" + slotValues.get(o.getName()).get("nameOfModelElement") + "\"");
			}

		}

		for (FmmlxObject o : listInjections) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "idOfUIElement",
						"\"" + slotValues.get(o.getName()).get("idOfUIElement") + "\"");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "nameOfModelElement",
						"\"" + slotValues.get(o.getName()).get("nameOfModelElement") + "\"");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "isListView", "true");
			}
		}

		for (FmmlxObject o : actions) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "eventName", "\"" + "ACTION" + "\"");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "idOfUIElement",
						"\"" + slotValues.get(o.getName()).get("idOfUIElement") + "\"");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "nameOfModelElement",
						"\"" + slotValues.get(o.getName()).get("nameOfModelElement") + "\"");
			}

		}

		for (FmmlxObject o : virtuals) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "idOfUIElement",
						"\"" + slotValues.get(o.getName()).get("idOfUIElement") + "\"");
			}
		}

		for (FmmlxObject o : parameters) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "orderNo",
						slotValues.get(o.getName()).get("orderNo").toString());
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "dataType",
						"\"" + slotValues.get(o.getName()).get("dataType").toString() + "\"");
				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "value", "\"\"");
			}

		}

	}

	public class Reference {
		private FmmlxObject object;
		private FmmlxAssociation assoc;
		private String referenceInstanceName;
		private Boolean head;
		private Reference parent;

		public FmmlxObject getObject() {
			return object;
		}

		public FmmlxAssociation getAssoc() {
			return assoc;
		}

		public String getReferenceInstanceName() {
			return referenceInstanceName;
		}

		public boolean isHead() {
			return head;
		}

		public Reference(FmmlxObject object) {
			this.object = object;
		}

		public Reference(FmmlxObject object, FmmlxAssociation assoc, String referenceInstanceName, boolean head) {
			this.object = object;
			this.assoc = assoc;
			this.referenceInstanceName = referenceInstanceName;
			this.head = head;
		}

		public Reference(FmmlxObject object, FmmlxAssociation assoc, String referenceInstanceName, Boolean head,
				Reference parent) {
			super();
			this.object = object;
			this.assoc = assoc;
			this.referenceInstanceName = referenceInstanceName;
			this.head = head;
			this.parent = parent;
		}

		public Reference getParent() {
			return parent;
		}

		public void setParent(Reference parent) {
			if (parent != null)
				this.head = false;
			this.parent = parent;
		}

	}

}
