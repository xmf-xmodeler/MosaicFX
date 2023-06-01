package tool.clients.customui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.Map.Entry;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.CanvasElement;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;

public class DefaultUIGenerator {

	public HashMap<String, Map<String, String>> instantiateCustomGUI(Vector<CanvasElement> selectedObjects,
			AbstractPackageViewer diagram, DiagramActions actions) {
		// TODO better error handling if image is missing

		// name of gui
		String guiName = "";

		// for the fxml export
		int rowCount = 0;

		// instance of customGUI
		String guiInstanceName = actions.addInstance("UserInterface",
				"gui" + UUID.randomUUID().toString().replace("-", ""), false);

		// objects for customGUI
		FmmlxObject object;
		Vector<FmmlxObject> objectsCommonClass = new Vector<FmmlxObject>();

		String assocName = "";

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

		// map for is parent 1. entry -> parent 2. -> child
		// Referenznamen, bis diese aufgelöst werden können
		HashMap<String, String> isChildAssocs = new HashMap<>();
		HashMap<String, String> commonClassReferenceMap = new HashMap<>();

		// used if the commonClass needs a listInjection or not
		Boolean isList;
		String isHead = "";
		boolean isActionInjection;

		for (CanvasElement element : selectedObjects) {
			object = (FmmlxObject) element;
			if (object.getMetaClassName().equals("CommonClass"))
				objectsCommonClass.add(object);
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
		for (FmmlxObject o : objectsCommonClass) {

			isHead = "false";
			isList = false;
			isActionInjection = false;
			assocName = "";

			// create reference
			referenceInstanceName = actions.addInstance("Reference",
					"ref" + UUID.randomUUID().toString().replace("-", ""));

			// reference mapping
			commonClassReferenceMap.put(o.getName(), referenceInstanceName);

			// find associations and head

			// ANNAHME: Jede CommonClass hat nur eine Assoziation die "eingehend" ist.
			// Diese bildet die Grundlage für die isHead Beziehung und die Assozioation in
			// der Referenz
			// TODO Was ist wenn dieser Fall nicht zutrifft?

			for (FmmlxAssociation assoc : o.getAllRelatedAssociations()) {

				// association needs to be "inside" the selected objects
				if (assoc.getTargetNode().equals(o) && selectedObjects.contains(assoc.getSourceNode())) {

					assocName = assoc.getName();

					// add reference to map
					isChildAssocs.put(referenceInstanceName, assoc.getSourceNode().getName());

					// check if multiplicity > 1 then list
					multiplicity = assoc.getMultiplicityStartToEnd().toString();
					endChar = multiplicity.charAt(multiplicity.length() - 1);

					if (endChar == '*') {
						isList = true;
					} else {
						int a = Character.getNumericValue(endChar);
						isList = (a > 1) ? true : false;
					}
					// to avoid analyzing multiple associations in the same class
					continue;
				}
			}

			// if gui is only one class then it is automatically head
			if (objectsCommonClass.size() == 1)
				isHead = "true";
			// if no further associations are outgoing it is head
			if (assocName.equals(""))
				isHead = "true";

			// if head --> listInjection needed
			// head defines name for gui
			if (isHead.equals("true")) {
				isList = true;
				guiName = o.getName() + " CustomUI";
			}

			helper.put("associationName", assocName);
			helper.put("isHead", isHead);
			slotValues.put(referenceInstanceName, (Map<String, String>) helper.clone());
			helper.clear();

			// List Injection
			if (isList) {
				injectionInstanceName = actions.addInstance("ListInjection",
						"list" + UUID.randomUUID().toString().replace("-", ""));
				actions.addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());
				actions.addAssociation(injectionInstanceName, referenceInstanceName, associationDerivedFrom.getName());
				helper.put("isListView", "true");
				helper.put("nameOfModelElement", o.getName());
				helper.put("idOfUIElement", injectionInstanceName);
				slotValues.put(injectionInstanceName, (Map<String, String>) helper.clone());
				helper.clear();

				// add to standardGUI
				Label instancesOfClassLabel = new Label("Instances of " + o.getName());
				instancesOfClassLabel.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD,
						FontPosture.REGULAR, Font.getDefault().getSize()));
				rechteSeiteGrid.add(instancesOfClassLabel, 0, rowCount++);

				ListView<String> objectListView = new ListView<>();
				objectListView.setId(injectionInstanceName);
				rechteSeiteGrid.add(objectListView, 0, rowCount++);
			}

			// add link "refersToStateOf" -> Reference + CommonClassInstance
			// ANNAHME: Jede gewählte CommonClass hat mind. 1 Instanz.
			// TODO: Prio 3 -> Wie damit umgehen, wenn keine Instanz existiert
			actions.addAssociation(referenceInstanceName, o.getInstances().get(0).getName(),
					associationRefersToStateOf.getName());

			attributes = o.getAllAttributes();
			operations = o.getAllOperations();

			// Standard GUI
			if (attributes.size() > 0) {
				rechteSeiteGrid.add(new Label("Slots:"), 0, rowCount++);
			}

			// add slotInjections for slots
			for (FmmlxAttribute attribute : attributes) {

				// add instance
				injectionInstanceName = actions.addInstance("SlotInjection",
						"slot" + UUID.randomUUID().toString().replace("-", ""));

				helper.put("idOfUIElement", injectionInstanceName);
				helper.put("nameOfModelElement", attribute.getName());
				slotValues.put(injectionInstanceName, (Map<String, String>) helper.clone());
				helper.clear();

				// add associations
				actions.addAssociation(injectionInstanceName, referenceInstanceName, associationDerivedFrom.getName());
				actions.addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());

				// Standard GUI
				TextField valueTextField = new TextField(attribute.getName());
				valueTextField.setId(injectionInstanceName);
				Label slotName = new Label(attribute.getName());

				rechteSeiteGrid.add(slotName, 0, rowCount);
				rechteSeiteGrid.add(valueTextField, 1, rowCount);
				rowCount++;
			}

			// Standard GUI
			if (operations.size() > 0) {
				rechteSeiteGrid.add(new Label("Operations:"), 0, rowCount++);
			}

			// add actionInjections for operations
			for (FmmlxOperation operation : operations) {

				// if method is monitor than action; otherwise actionInjection
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
					actions.addAssociation(injectionInstanceName, referenceInstanceName,
							associationDerivedFrom.getName());
					actions.addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());

					// standard GUI
					Label actionValue = new Label("");
					actionValue.setId(injectionInstanceName);

					rechteSeiteGrid.add(new Label(operation.getName()), 0, rowCount);
					rechteSeiteGrid.add(actionValue, 1, rowCount);
					rowCount++;

				} else {
					actionInstanceName = actions.addInstance("Action",
							"act" + UUID.randomUUID().toString().replace("-", ""));
					helper.put("idOfUIElement", actionInstanceName);

					slotValues.put(actionInstanceName, (Map<String, String>) helper.clone());
					helper.clear();

					// add associations
					actions.addAssociation(actionInstanceName, referenceInstanceName, associationDerivedFrom.getName());

					int paramCounter = 0;

					// standard GUI
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

		} // end of for for objects

		// build references correctly
		for (Entry<String, String> entryIsChild : isChildAssocs.entrySet()) {
			if (!entryIsChild.getValue().startsWith("ref")) {
				isChildAssocs.replace(entryIsChild.getKey(), commonClassReferenceMap.get(entryIsChild.getValue()));
			}
		}

		// now the map isChild is filled with the names of the references needed to map
		// if there are any isChild associations
		// TODO check whether the changes in the cardinality can be better represented
		// here ? -> tree structure now; no longer list
		if (selectedObjects.size() > 1) {
			for (Entry<String, String> entry : isChildAssocs.entrySet()) {
				// add ischild
				actions.addAssociation(entry.getKey(), entry.getValue(), associationIsParent.getName());
				// add isparent in other direction
				actions.addAssociation(entry.getValue(), entry.getKey(), associationIsChild.getName());
			}
		}

		// export standard gui
		// File picker as save dialogue
		// TODO: add better instantiation dialog which includes the save option
		ScrollPane defaultGUIPane = new ScrollPane();

		Scene scene = new Scene(defaultGUIPane);
		Stage stage = new Stage();
		stage.setScene(scene);

		stage.setTitle(guiName);
		stage.setWidth(800);
		stage.setHeight(400);

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select location for saving the extraction of " + guiName);
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JavaFX as XML", "*.fxml"),
				new ExtensionFilter("All Files", "*.*"));

		File file = fileChooser.showSaveDialog(stage);
		String path = "";

		if (file != null) {
			FXMLExporter exporter = new FXMLExporter(file.getPath());
			exporter.export(rechteSeiteGrid);
			path = file.getPath();
		}

		helper.put("pathToFXML", path);
		helper.put("titleOfUI", guiName);
		// helper.put("pathToIconOfWindow", "");

		slotValues.put(guiInstanceName, (Map<String, String>) helper.clone());
		helper.clear();

		return slotValues;
	}

	// recursive function to get Objects for CustomgUI
	public Vector<CanvasElement> recurGetObjectsForGUI(Vector<CanvasElement> vector, FmmlxObject root, int depth) {
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

		// TODO Füllen von Attributen
		// CustomGui -> pathTOIconOfWindow, titleOfUI <- automatisch machbar --
		// sinnvoll?

		for (FmmlxObject o : customGuiInterface) {
			if (slotValues.containsKey(o.getName())) {
				String path = slotValues.get(o.getName()).get("pathToFXML");
				path = path.replace("\\", "\\\\");

				diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), "pathToFXML", "\"" + path + "\"");

				// auto icon
				// TODO change that with instantiation dialog
				String pathIcon = "C:\\\\Users\\\\fhend\\\\OneDrive\\\\Desktop\\\\XModelerIconUndGUI\\\\invoice.png";
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
}
