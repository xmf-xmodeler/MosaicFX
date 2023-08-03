package tool.clients.customui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import tool.clients.fmmlxdiagrams.ReturnCall;

public class DefaultUIGenerator {

	private AbstractPackageViewer diagram;
	private Vector<FmmlxObject> objects;
	private Vector<FmmlxAssociation> associations;
	private DiagramActions actions;
	private String pathIcon;
	private String pathGUI;
	private String titleGUI;
	private Vector<FmmlxObject> roots;
	private int distance;
	private int height;

	private HashMap<String, HashMap<String, String>> customGuiSlots;

	private String metaClassName = "Root::FMMLx::MetaClass";
	private String commonClassName = "CommonClass";

	public DefaultUIGenerator(AbstractPackageViewer diagram, Vector<FmmlxObject> objects,
			Vector<FmmlxAssociation> associations, DiagramActions actions, String pathIcon, String pathGUI,
			String titleGUI, Vector<FmmlxObject> roots, int distance, int height) {
		this.diagram = diagram;
		this.objects = objects;
		this.associations = associations;
		this.actions = actions;
		this.pathIcon = pathIcon;
		this.pathGUI = pathGUI;
		this.titleGUI = titleGUI;
		this.roots = roots;
		this.distance = distance;
		this.height = height;

	}

	public void instantiateCustomGUI() {

		// 3. step: slot values are set
		ReturnCall<Object> onUpdate = update -> {
			addSlotValuesCustomGUI(customGuiSlots);
			diagram.updateDiagram();
		};

		// 2. step: diagram is updated to ensure the instances are available
		ReturnCall<Vector<Object>> onInstanceCreated = onCreated -> {
			diagram.updateDiagram(onUpdate);
		};

		// 1. step: instantiate objects
		this.customGuiSlots = instantiateGUI(onInstanceCreated);

	}

	private boolean instanceOfCommonClass(FmmlxObject object) {

		boolean instanceOf = false;

		if (object.getMetaClassName().equals(metaClassName)) {
			return instanceOf;
		}

		FmmlxObject metaClass = this.diagram.getObjectByPath(object.getMetaClassName());

		if (metaClass.getName().equals(commonClassName))
			instanceOf = true;
		while (!instanceOf && !metaClass.getName().equals(metaClassName)) {
			instanceOf = instanceOfCommonClass(metaClass);
		}
		return instanceOf;
	}

	private HashMap<String, HashMap<String, String>> instantiateGUI(ReturnCall<Vector<Object>> onInstanceCreated) {

		if (pathGUI.equals("")) {
			raiseAlert("No Path has been set for the GUI. Extraction of GUI is not possible.");
			return null;
		}
		
		if (titleGUI.equals("")) {
			raiseAlert("No title has been set for the GUI. Extraction of GUI cancelled.");
			return null;
		}

		// for the fxml export
		int rowCount = 0;

		// instance of customGUI
		String guiInstanceName = actions.addInstance("UserInterface",
				"gui" + UUID.randomUUID().toString().replace("-", ""), false, onInstanceCreated);

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

		boolean isActionInjection;

		// TODO find better solution
		// Slot Values that can already be determined in this method should be saved for
		// performance
		// first String -> instanceName
		// second String -> slotName
		// third String -> value
		HashMap<String, HashMap<String, String>> slotValues = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> helper = new HashMap<String, String>();

		// used if the commonClass needs a listInjection or not
		Boolean isList;
		String isHead = "";

		if (!roots.isEmpty()) {
			for (FmmlxObject root : roots) {
				// add recursively by root
				if (distance > 0) {
					objects = this.recurGetObjectsForGUI(objects, root, distance);
				} else {
					objects.addAll(roots);
				}

				// add needed assocs
				for (FmmlxObject o : objects) {
					for (FmmlxAssociation a : o.getAllRelatedAssociations()) {
						if (!associations.contains(a)) {
							associations.add(a);
						}
					}
				}
			}
		}

		int i = 0;

		// check if height > 0 and additional assocs have to be considered
		if (height > 0 || height == -1) {
			// for all objects ...
			for (FmmlxObject o : objects) {
				// check if there is a metaclass ...
				i = 0;
				while ((height > i || height == -1) && !(o.getMetaClassName().equals(metaClassName))) {
					i += 1;
					o = diagram.getObjectByPath(diagram.getPackagePath() + "::" + o.getMetaClassName());
					// that has an association ...
					for (FmmlxAssociation assoc : o.getAllRelatedAssociations()) {
						// which is instantiated on level 0
						if (assoc.getSourceNode().equals(o) && assoc.getLevelSource() == 0) {
							if (!associations.contains(assoc)) {
								associations.add(assoc);
							}
						}
					}
				}
			}
		}

		// check if all objects are included for the needed assocs
		for (FmmlxAssociation assoc : associations) {
			if (!objects.contains(assoc.getTargetNode())) {
				objects.add(assoc.getTargetNode());
			}
		}

		// check if from commonClass
		for (FmmlxObject o : objects) {
			if (instanceOfCommonClass(o))
				objectsCommonClass.add(o);
		}

		// get associations that are mapped
		FmmlxAssociation associationDerivedFrom = diagram.getAssociationByPath(diagram.getPackagePath() + "::derivedFrom");
		FmmlxAssociation associationComposedOf = diagram.getAssociationByPath(diagram.getPackagePath() + "::composedOf");
		FmmlxAssociation associationRefersToStateOf = diagram.getAssociationByPath(diagram.getPackagePath() + "::refersToStateOf");
		FmmlxAssociation associationIsParent = diagram.getAssociationByPath(diagram.getPackagePath() + "::isParent");
		FmmlxAssociation associationIsChild = diagram.getAssociationByPath(diagram.getPackagePath() + "::isChild");
		FmmlxAssociation associationUses = diagram.getAssociationByPath(diagram.getPackagePath() + "::uses");
		FmmlxAssociation associationRepresentedAs = diagram.getAssociationByPath(diagram.getPackagePath() + "::representedAs");

		// create standard GUI
		GridPane rechteSeiteGrid = new GridPane();
		rechteSeiteGrid.setHgap(3);
		rechteSeiteGrid.setVgap(3);
		rechteSeiteGrid.setPadding(new Insets(3, 3, 3, 3));

		// instantiate references and injections for domain classes
		ArrayList<Reference> referenceMapping = new ArrayList<>();
		Boolean head;

		Boolean atLeastOneHead = false;

		// get all needed references
		for (FmmlxObject o : objectsCommonClass) {
			head = true;
			for (FmmlxAssociation assoc : associations) {
				if (assoc.getTargetNode().equals(o)) {

					head = false;
					// create reference for every object + assoc pair
					referenceInstanceName = actions.addInstance("Reference",
							"ref" + UUID.randomUUID().toString().replace("-", ""));
					referenceMapping.add(new Reference(o, assoc, referenceInstanceName, false,
							new Reference(assoc.getSourceNode())));
				}
			}

			// if root then automatically head
			if (!head && roots.contains(o)) {
				head = true;
			}

			if (head) {
				// create a reference for head
				referenceInstanceName = actions.addInstance("Reference",
						"ref" + UUID.randomUUID().toString().replace("-", ""));
				referenceMapping.add(new Reference(o, null, referenceInstanceName, true));
				atLeastOneHead = true;
			}
		}

		if (!atLeastOneHead) {
			raiseAlert("No head of GUI could detected. This configuration is invalid!");
			return null;
		}

		// maps references with parents
		referenceMapping = mapReferences(referenceMapping);

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

			slotValues.put(reference.getReferenceInstanceName(), (HashMap<String, String>) helper.clone());
			helper.clear();

			// add link "refersToStateOf" -> Reference + CommonClassInstance 
			
			// Assumption: Every Instance of CommonClass has at least one instance at level 0
			// TODO How to work when that is not the case
			actions.addAssociation(reference.getReferenceInstanceName(),
					reference.getObject().getInstances().get(0).getName(), associationRefersToStateOf.getName());

			Label instancesOfClassLabel = new Label("Instance(s) of " + reference.getObject().getName());
			instancesOfClassLabel.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR,
					Font.getDefault().getSize()));

			// description of the association for the standard gui
			if (!reference.isHead()) {
				Label assocDesc = new Label("Association: " + reference.getAssoc().getSourceNode().getName().toString()
						+ " " + reference.getAssoc().getName().toString() + " "
						+ reference.getAssoc().getTargetNode().toString());
				rechteSeiteGrid.add(assocDesc, 0, rowCount++);
			} else { // add name for head
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

				slotValues.put(injectionInstanceName, (HashMap<String, String>) helper.clone());
				helper.clear();

				ListView<String> objectListView = new ListView<>();
				objectListView.setId(injectionInstanceName);
				rechteSeiteGrid.add(objectListView, 0, rowCount++);
			}

			// all operations and attributes of all meta classes
			if (height == -1) {
				attributes = reference.getObject().getAllAttributes();
				operations = reference.getObject().getAllOperations();
			} else if (height == 0) {
				// only own attributes and operations
				attributes = reference.getObject().getOwnAttributes();
				operations = reference.getObject().getOwnOperations();
			} else {
				attributes = getAttributesByHeight(reference, height);
				operations = getOperationsByHeight(reference, height);
			}

			if (attributes.size() > 0) {
				rechteSeiteGrid.add(new Label("Slots:"), 0, rowCount++);
			}

			for (FmmlxAttribute attribute : attributes) {
				injectionInstanceName = actions.addInstance("SlotInjection",
						"slot" + UUID.randomUUID().toString().replace("-", ""));

				helper.put("idOfUIElement", injectionInstanceName);
				helper.put("nameOfModelElement", attribute.getName());
				slotValues.put(injectionInstanceName, (HashMap<String, String>) helper.clone());
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

				// if method is monitor than action; otherwise acttionInjection
				// TODO: maybe find something better more robust approach

				String body = operation.getBody();
				isActionInjection = body.contains("monitor=true") ? true : false;	
				
				helper.put("nameOfModelElement", operation.getName());

				// if actionInjection
				if (isActionInjection) {
					injectionInstanceName = actions.addInstance("ActionInjection",
							"actInj" + UUID.randomUUID().toString().replace("-", ""));
					helper.put("idOfUIElement", injectionInstanceName);

					slotValues.put(injectionInstanceName, (HashMap<String, String>) helper.clone());
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

					slotValues.put(actionInstanceName, (HashMap<String, String>) helper.clone());
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
						slotValues.put(parameterInstanceName, (HashMap<String, String>) helper.clone());
						helper.clear();

						// virtual for every parameter
						virtualInstanceName = actions.addInstance("Virtual",
								"vir" + UUID.randomUUID().toString().replace("-", ""));
						helper.put("idOfUIElement", virtualInstanceName);
						slotValues.put(virtualInstanceName, (HashMap<String, String>) helper.clone());
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

		slotValues.put(guiInstanceName, (HashMap<String, String>) helper.clone());
		helper.clear();

		return slotValues;
	}

	private ArrayList<Reference> mapReferences(ArrayList<Reference> referenceMapping) {
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
					}
				}

			}

			// MLM parent mapping
			if (!parentSet) {
				// get all instances of object
				for (FmmlxObject o : reference.getParent().getObject().getInstancesByLevel(1)) {

					// for every reference
					for (Reference r : referenceMapping) {
						// if there is a reference with a lower level instance
						if (r.getObject().equals(o)) {
							// set parent
							if (!parentSet) {
								reference.setParent(r);
								parentSet = true;
							}
						}
					}
				}
			}

			if (!parentSet) {
				System.err.println("kein parent fuer " + reference.getReferenceInstanceName());
			}

		}
		return referenceMapping;
	}

	// get Operations from meta classes
	private Vector<FmmlxOperation> getOperationsByHeight(Reference reference, int height) {

		Vector<FmmlxOperation> operations = new Vector<>();
		FmmlxObject object = reference.getObject();
		int i = 0;
		while (i < height && !object.getMetaClassName().equals(metaClassName)) {
			operations.addAll(reference.getObject().getOwnOperations());
			object = diagram.getObjectByPath(diagram.getPackagePath() + "::" + object.getMetaClassName());
			i += 1;
		}
		return operations;
	}

	// get Attributes from meta classes
	private Vector<FmmlxAttribute> getAttributesByHeight(Reference reference, int height) {
		Vector<FmmlxAttribute> attributes = new Vector<>();
		FmmlxObject object = reference.getObject();
		int i = 0;
		while (i < height && !object.getMetaClassName().equals(metaClassName)) {
			attributes.addAll(reference.getObject().getOwnAttributes());
			object = diagram.getObjectByPath(diagram.getPackagePath() + "::" + object.getMetaClassName());
			i += 1;
		}
		return attributes;
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

	private void addSlotValuesCustomGUI(HashMap<String, HashMap<String, String>> slotValues) {
		// TODO refactor this

		if (slotValues == null) {
			return;
		}

		Vector<FmmlxObject> references = new Vector<>();
		Vector<FmmlxObject> slotInjections = new Vector<>();
		Vector<FmmlxObject> actionInjections = new Vector<>();
		Vector<FmmlxObject> listInjections = new Vector<>();
		Vector<FmmlxObject> parameters = new Vector<>();
		Vector<FmmlxObject> virtuals = new Vector<>();
		Vector<FmmlxObject> actions = new Vector<>();
		Vector<FmmlxObject> customGuiInterface = new Vector<>();

		Vector<FmmlxObject> objects = diagram.getObjectsReadOnly();

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

	public void raiseAlert(String alertMessage) {
		Alert alert = new Alert(AlertType.CONFIRMATION, alertMessage);
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				return;
			}
		});
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
