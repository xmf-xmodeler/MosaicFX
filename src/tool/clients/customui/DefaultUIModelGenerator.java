package tool.clients.customui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.Constraint;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxLink;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.Level;
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.ReturnCall;

/*
 * FH 2023
 * The DeufaultUIModelGenerator generates the needed UI model necessary to create custom UIs.
 * This is needed if the user first models a domain model and later decides that a UI shall be included.
 * This class ensures that all needed classes are instantiated correctly and that all prior meta classes
 * are mapped to CommonClass.
*/

// FH 30.01.2024 TODO
// Bekannte Fehler - aktuell

// addAssociation an den Communicator auf XMF seite schicken -> 
// funktioniert nur wenn min und max level gleich sind; wird hier ignoriert -> 
// CommonClass läuft zunächst nur auf Levbel 0; kann bei Bedarf später angepasst werden


public class DefaultUIModelGenerator {

	AbstractPackageViewer diagram;
	DiagramActions actions;

	public DefaultUIModelGenerator(AbstractPackageViewer diagram) {
		this.diagram = diagram;
		this.actions = diagram.getActions();

	}

	public void generateUIModel() {
		// check for naming conflicts before the model can be generated
		if (!hasNoNamingConflicts()) {
			return;
		}

		Vector<Integer> levels = diagram.getAllObjectLevel();
		String commonClassName = "CommonClassL" + (levels.get(0) + 1);
		actions.addMetaClass(commonClassName, levels.get(0) + 1);

		// change from metaClass to CommonClass
		changeMetaClassForDiagram();

		// instantiate model
		instantiateUIModel(commonClassName);

		// only return after the diagram has been updated
		ReturnCall<Object> onUpdate = update -> {
			System.err.println("Update completed");
			return;
		};
		diagram.updateDiagram(onUpdate);

	}

	private void changeMetaClassForDiagram() {
		// objects must be created according to their level so instances can be created

		Vector<FmmlxObject> objects = diagram.getObjectsReadOnly();
		Vector<Integer> levelsV = diagram.getAllObjectLevel();
		Vector<FmmlxAssociation> assocs = diagram.getAssociations();
		Vector<FmmlxLink> links = diagram.getAssociationInstance();
		Vector<FmmlxObject> objectsWithParents = new Vector<>();

		// "easy" way to convert the levels into a unique vector for iterating through
		// it
		Set<Integer> levelsS = new HashSet<>(levelsV);
		Vector<Integer> levels = new Vector<>(levelsS);
		levels.sort(Collections.reverseOrder());

		for (int level : levels) {
			// create dummy class for level > 0
			if (level > 1)
				actions.addInstance("CommonClassL" + (level + 1), "CommonClassL" + level, level);

			for (FmmlxObject o : objects) {

				int x = (int) Math.round(o.getX());
				int y = (int) Math.round(o.getY());
				Vector<String> parents = new Vector<>();

				if (o.getName().contains("CommonClass")) {
					continue;
				}

				// only generate the objects of the current level
				if (!o.getLevel().isEqual(level)) {
					continue;
				}

				// ignore commonclass objects
				if (o.getName().contains("CommonClass")) {
					continue;
				}

				try {
					if (o.getParentsPaths().size() > 1) {
						objectsWithParents.add(o);
					}
				} catch (Exception e) {
					// no parents have been set
				}

				if (o.getMetaClassName().contains("MetaClass") || o.getMetaClassName().contains("CommonClass")) {
					// instances of meta class must be regenerated as instances of the corresponding
					// commonclass

					// if the class is on an instance of metaclass than it needs to be removed from
					// the canvas
					diagram.getComm().removeClass(diagram.getID(), o.getName(), 0);

					String commonClassName = "CommonClassL" + (o.getLevel().getMaxLevel() + 1);

					diagram.getComm().addNewInstance(diagram.getID(), commonClassName, o.getName(), o.getLevel(),
							parents, o.isAbstract(), o.isSingleton(), x, y, o.isHidden());
				} else {
					// instances of other classes must not be deleted but regenerated
					diagram.getComm().addNewInstance(diagram.getID(), o.getMetaClassName(), o.getName(), o.getLevel(),
							parents, o.isAbstract(), o.isSingleton(), x, y, o.isHidden());
				}

				if (o.getLevel().getMaxLevel() > 0) {

					// operation gets set at last AFTER associations so getter and setter from
					// associations can be set first and don't lead to duplications

					for (FmmlxAttribute att : o.getOwnAttributes()) {
						diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::" + o.getName(),
								att.getName(), new Level(att.getLevel()), att.getType(), att.getMultiplicity(), true,
								false, false);
					}

					for (Constraint constraint : o.getConstraints()) {
						diagram.getComm().addConstraint(diagram.getID(), o.getPath(), constraint.getName(),
								constraint.getLevel(), constraint.getBodyRaw(),
								"\"" + constraint.getReasonRaw() + "\"");
					}
				}
			}
		}

		for (FmmlxObject o : objectsWithParents) {

			Vector<String> parents = new Vector<>();
			Vector<String> parentsPath = o.getParentsPaths();

			for (String parent : parentsPath) {
				String[] helper = parent.split("::");
				if (helper[2].contains("metaClass") || helper[2].contains("Object"))
					continue;
				parents.add(helper[2]);
			}

			diagram.getComm().changeParent(diagram.getID(), o.getName(), new Vector<String>(), parents);
		}

		// slots have to be set separately
		for (FmmlxObject o : objects) {
			for (FmmlxSlot slot : o.getAllSlots()) {

				switch (slot.getType(diagram)) {

				case "Boolean", "Integer", "Float":
					diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), slot.getName(), slot.getValue());
					break;

				case "String":
					String stringValue = "\"" + slot.getValue() + "\"";
					diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), slot.getName(), stringValue);
					break;

				case "Date":
					String month = "";
					String[] dates;
					if (slot.getValue().equals("null")) {
						break;
					} else {
						dates = slot.getValue().split(" ");
					}

					switch (dates[1]) {

					case "Jan":
						month = "1";
						break;
					case "Feb":
						month = "2";
						break;
					case "Mar":
						month = "3";
						break;
					case "Apr":
						month = "4";
						break;
					case "May":
						month = "5";
						break;
					case "Jun":
						month = "6";
						break;
					case "Jul":
						month = "7";
						break;
					case "Aug":
						month = "8";
						break;
					case "Sep":
						month = "9";
						break;
					case "Oct":
						month = "10";
						break;
					case "Nov":
						month = "11";
						break;
					case "Dec":
						month = "12";
						break;
					default:
						month = "";
						raiseAlert("Slot cannot be parsed");
						break;
					}

					String dateValue = "Date::createDate(" + dates[2] + "," + month + "," + dates[0] + ")";

					diagram.getComm().changeSlotValue(diagram.getID(), o.getName(), slot.getName(), dateValue);
					break;

				default:
					raiseAlert("Parsing of the datatype " + slot.getType(diagram) + " is not supported at the moment.");
				}
			}
		}

		for (FmmlxAssociation assoc : assocs) {
			// add associations
			diagram.getComm().addAssociation(diagram.getID(), assoc.getSourceNode().getName(),
					assoc.getTargetNode().getName(), assoc.getAccessNameEndToStart(), assoc.getAccessNameStartToEnd(),
					assoc.getName(), null, assoc.getMultiplicityEndToStart(), assoc.getMultiplicityStartToEnd(),
					assoc.getLevelSource(), assoc.getLevelSource(), assoc.getLevelTarget(), assoc.getLevelTarget(),
					assoc.isSourceVisible(), assoc.isTargetVisible(), assoc.isSymmetric(), assoc.isTransitive(),
					assoc.getSourceNode().getName(), assoc.getSourceNode().getName(), assoc.getTargetNode().getName(),
					assoc.getTargetNode().getName());

		}

		for (FmmlxLink link : links) {
			diagram.getComm().addLink(diagram.getID(), link.getSourceNode().getName(), link.getTargetNode().getName(),
					link.getAssociation().getName());
		}

		// operations are added
		for (FmmlxObject oldObject : objects) {
			for (FmmlxOperation op : oldObject.getAllOperations()) {
				boolean added = false;

				Vector<FmmlxAttribute> attribute = oldObject.getAllAttributes();

				for (FmmlxAttribute att : attribute) {
					if (added)
						continue;

					if (op.getName().toLowerCase().contains(att.getName().toLowerCase())) {
						// don't add -> is getter für slot
						added = true;
					}
				}

				// add getter and setter from asociations
				for (FmmlxAssociation assoc : assocs) {
					if (added)
						continue;

					// check if name contains get or set AND the the name of an association
					if ((op.getName().contains("get") || op.getName().contains("set"))
							&& (op.getName().toLowerCase().contains(assoc.getAccessNameEndToStart().toLowerCase()))) {
						// must be added
						diagram.getComm().addOperation(diagram.getID(),
								diagram.getPackagePath() + "::" + oldObject.getName(), op.getLevel(), op.getBody());
						added = true;
					}
					if ((op.getName().contains("get") || op.getName().contains("set"))
							&& (op.getName().toLowerCase().contains(assoc.getAccessNameStartToEnd().toLowerCase()))) {
						diagram.getComm().addOperation(diagram.getID(),
								diagram.getPackagePath() + "::" + oldObject.getName(), op.getLevel(), op.getBody());
						added = true;
					}
				}

				if (added) {
				} else {
					diagram.getComm().addOperation(diagram.getID(),
							diagram.getPackagePath() + "::" + oldObject.getName(), op.getLevel(), op.getBody());
				}
			}
		}
	}

	private void instantiateUIModel(String commomClassName) {
		// add Classes and Instances
		Vector<String> parents = new Vector<String>();

		actions.addMetaClass("Parameter", 1);
		actions.addMetaClass("Reference", 1);
		actions.addMetaClass("UIElement", 2);
		actions.addMetaClass("UserInterface", 1);

		parents.add("UIElement");
		actions.addMetaClass("UIControlElement", 2, parents);
		parents.clear();

		actions.addInstance("UIControlElement", "Injection", 1);
		actions.addInstance("UIElement", "Virtual", 1);
		actions.addInstance("UIControlElement", "Action", 1);

		parents.add("Injection");
		actions.addInstance("UIControlElement", "ListInjection", 1, parents);
		actions.addInstance("UIControlElement", "SlotInjection", 1, parents);

		parents.add("Action");
		actions.addInstance("UIControlElement", "ActionInjection", 1, parents);
		parents.clear();

		// add association
		diagram.getComm().addAssociation(diagram.getID(), "UserInterface", "UIElement", "customUserInterface",
				"uIElement", "composedOf", null, new Multiplicity(0, 1, true, false, false),
				new Multiplicity(0, 2147483647, false, false, false), 0, 0, 0, 0, true, true, false, false,
				"customUserInterface", "customUserInterface", "uiElements", "uiElements");

		diagram.getComm().addAssociation(diagram.getID(), "Parameter", "Virtual", "parameter", "virtual",
				"representedAs", null, new Multiplicity(0, 1, true, false, true),
				new Multiplicity(1, 1, true, false, true), 0, 0, 0, 0, true, true, false, false, "getParameter",
				"setParameter", "getVirtual", "setVirtual");

		diagram.getComm().addAssociation(diagram.getID(), "Action", "Parameter", "action", "parameter", "uses", null,
				new Multiplicity(0, 2147483647, false, false, true),
				new Multiplicity(0, 2147483647, false, false, true), 0, 0, 0, 0, true, true, false, false, "getActions",
				"setActions", "getParameter", "setParameter");

		diagram.getComm().addAssociation(diagram.getID(), "UIControlElement", "Reference", "controlElement",
				"reference", "derivedFrom", null, new Multiplicity(0, 2147483647, false, false, true),
				new Multiplicity(1, 1, true, false, true), 0, 0, 0, 0, true, true, false, false, "getControlElements",
				"setControlElements", "getReference", "setReference");

		diagram.getComm().addAssociation(diagram.getID(), "Reference", "Reference", "parent", "child", "isParent", null,
				new Multiplicity(0, 1, true, false, true), new Multiplicity(0, 2147483647, false, false, true), 0, 0, 0,
				0, true, true, false, false, "getParent", "setParent", "getChilds", "setChilds");

		diagram.getComm().addAssociation(diagram.getID(), "Reference", "Reference", "childC", "parentC", "isChild",
				null, new Multiplicity(0, 1, true, false, true), new Multiplicity(0, 2147483647, false, false, true), 0,
				0, 0, 0, true, true, false, false, "getChildC", "setChildC", "getChildsC", "setChildsC");

		// max level for objects of commonClass is here set to 5 .... not sure whether a
		// higher value is better -> better use contigent classes
		// TBD: What side effects are possible
		diagram.getComm().addAssociation(diagram.getID(), "Reference", commomClassName, "reference", "commonClass",
				"refersToStateOf", null, new Multiplicity(0, 2147483647, false, false, true),
				new Multiplicity(1, 1, true, false, true), 0, 0, 0, 0, true, true, false, false, "getReferences",
				"setReferences", "getCommonClass", "setCommonClass");

		// add Attributes
		Multiplicity multOne = new Multiplicity(1, 1, true, false, false);

		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::UserInterface", "pathToFXML",
				new Level(0), "String", multOne, true, false, false);
		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::UserInterface",
				"pathToIconOfWindow", new Level(0), "String", multOne, true, false, false);
		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::UserInterface", "titleOfUI",
				new Level(0), "String", multOne, true, false, false);

		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::UIElement", "idOfUIElement",
				new Level(0), "String", multOne, true, false, false);

		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::Parameter", "dataType",
				new Level(0), "String", multOne, true, false, false);
		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::Parameter", "orderNo",
				new Level(0), "Integer", multOne, true, false, false);
		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::Parameter", "value", new Level(0),
				"String", multOne, true, false, false);

		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::Action", "eventName",
				new Level(0), "String", multOne, true, false, false);

		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::ListInjection", "isListView",
				new Level(0), "Boolean", multOne, true, false, false);

		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::UIControlElement",
				"nameOfModelElement", new Level(0), "String", multOne, true, false, false);

		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::Reference", "associationName",
				new Level(0), "String", multOne, true, false, false);
		diagram.getComm().addAttribute(diagram.getID(), diagram.getPackagePath() + "::Reference", "isHead",
				new Level(0), "Boolean", multOne, true, false, false);

		// add functions
		String bodyRunAction = "@Operation runAction[monitor=false,delToClassAllowed=false]():XCore::Element\r\n"
				+ "  let a = self.getReference().getCommonClass() then\r\n"
				+ "      b = self.getParameterSize() then\r\n" + "      res = null\r\n"
				+ "  in @While a.name.toString() <> \"MetaClass\" do\r\n"
				+ "       if a.of().hasOperation(self.nameOfModelElement.asSymbol(),b)\r\n"
				+ "       then res := a.of().getOperation(self.nameOfModelElement).invoke(a,self.getParamValuesAsList())\r\n"
				+ "       else false\r\n" + "       end ;\r\n" + "       a := a.of()\r\n" + "     end;\r\n"
				+ "     res\r\n" + "  end\r\n" + "end";

		String getParameterSize = "@Operation getParameterSize[monitor=false,delToClassAllowed=false]():XCore::Element\r\n"
				+ "  if self.getParameter() <> null\r\n" + "  then self.getParameter().size\r\n" + "  else 0\r\n"
				+ "  end \r\n" + "end";

		String bodySelectNewInstance = "@Operation selectNewInstance[monitor=false,delToClassAllowed=false](idUI : XCore::String,instanceName : XCore::String):XCore::Element\r\n"
				+ "  let a = self.getInstanceByID(idUI).asSeq().at(0)\r\n"
				+ "  in a.getReference().changeCommonClass(instanceName)\r\n" + "  end\r\n" + "end";

		String bodySendMessage = "@Operation sendMessage[monitor=false,delToClassAllowed=false](idUI : XCore::String,eventID : XCore::String):XCore::Element\r\n"
				+ "  let a = self.getInstanceByID(idUI)\r\n" + "  in @For obj in a.asSeq() do\r\n"
				+ "       if (obj.getEventName().toString() = eventID)\r\n" + "       then obj.runAction()\r\n"
				+ "       else false\r\n" + "       end \r\n" + "     end\r\n" + "  end\r\n" + "end";

		String bodyGetOf = "@Operation getOf[monitor=true,delToClassAllowed=false]():XCore::Class\r\n"
				+ "  self.getCommonClass().of()\r\n" + "end";

		// TODO change to monitir = false;
		String bodyGetAssociatons = "@Operation getAssociation[monitor=true,delToClassAllowed=false]():Associations::Association\r\n"
				+ "  if (self.associationName <> \"\")\r\n"
				+ "  then self.getCommonClass().of().attributes->select(a|a.isKindOf(Associations::End)).get(\"association\")->select(a|a.get(\"name\")=self.associationName).asSeq().at(0)\r\n"
				+ "  else false\r\n" + "  end \r\n" + "end";

		String bodyChangeCommonClass = "@Operation changeCommonClass[monitor=false,delToClassAllowed=false](newCommonObject : XCore::String):XCore::Element\r\n"
				+ "  let a = Clients::FmmlxDiagrams::FmmlxManipulator() then\r\n"
				+ "      b = self.owner.classes->select(i |\r\n"
				+ "            i.name.toString() = newCommonObject)->sel\r\n" + "  in if (b <> null)\r\n"
				+ "     then \r\n" + "       a.removeAssociationInstance(self.owner().getAssociations()->select(a |\r\n"
				+ "         a.get(\"name\") = \"refersToStateOf\")->sel,self,self.getCommonClass());\r\n"
				+ "       a.addAssociationInstance(self.owner,self,b,self.owner().getAssociations()->select(a |\r\n"
				+ "         a.get(\"name\") = \"refersToStateOf\")->sel);\r\n"
				+ "       if (self.getChild() <> null)\r\n" + "       then @For child in self.getChild() do\r\n"
				+ "              child.checkCommonClass()\r\n" + "            end\r\n" + "       else false\r\n"
				+ "       end \r\n" + "     else false\r\n" + "     end \r\n" + "  end\r\n" + "end";

		String bodyCheckCommonClass = "@Operation checkCommonClass[monitor=false,delToClassAllowed=false]():XCore::Element\r\n"
				+ "  let a = self.getControlElement()->select(i |\r\n"
				+ "            i.of().name.toString() = \"ListInjection\")\r\n"
				+ "  in if (not a->isEmpty)\r\n"
				+ "     then if (a.asSeq().at(0).getInstanceList().contains(self.getCommonClass()))\r\n"
				+ "          then true\r\n"
				+ "          else self.changeCommonClass(a.asSeq().at(0).getInstanceList().at(0).name.toString())\r\n"
				+ "          end \r\n"
				+ "     else self.changeCommonClass(self.getParent().getCommonClass().slots()->select(s |            s.type.isKindOf(Associations::End))->select(a |a.type.association = self.getAssociation()).value.asSeq().at(0).name.toString())\r\n"
				+ "     end ;\r\n"
				+ "     if (self.getChild() <> null)\r\n"
				+ "     then @For child in self.getChild() do\r\n"
				+ "            child.checkCommonClass()\r\n"
				+ "          end\r\n"
				+ "     else false\r\n"
				+ "     end \r\n"
				+ "  end\r\n"
				+ "end";

		String bodyGetInstanceByID = "@Operation getInstanceByID[monitor=false,delToClassAllowed=false](idUI : XCore::String):ControllerMapping::UIElement\r\n"
				+ "  self.of().parents->select(i |\r\n"
				+ "    i.name.toString() = \"UIElement\").asSeq().at(0)->allInstances->collect(i |\r\n"
				+ "    i.allInstances())->flatten->select(i |\r\n" + "    i.idOfUIElement = idUI)\r\n" + "end";

		String bodySetParameterValue = "@Operation setParameterValue[monitor=false,delToClassAllowed=false](idUI : XCore::String,valueAsString : XCore::String):XCore::Element\r\n"
				+ "  let a = self.getInstanceByID(idUI).asSeq().at(0)\r\n" + "  in try\r\n"
				+ "       a.getParameter().setValue(valueAsString);\r\n" + "       true\r\n" + "     catch(ex)\r\n"
				+ "       false\r\n" + "     end\r\n" + "  end\r\n" + "end";

		String bodyGetParamValuesAsList = "@Operation getParamValuesAsList[monitor=true,delToClassAllowed=false]():XCore::Element\r\n"
				+ "  let a = Seq{} then\r\n" + "      b = Seq{} then\r\n" + "      c = Seq{} then\r\n"
				+ "      d = Seq{} then\r\n" + "      e = \"\" then\r\n" + "      h = \"\" then\r\n"
				+ "      m = \"\" then\r\n" + "      f = Root::Auxiliary::Date then\r\n" + "      i = 0\r\n"
				+ "  in if self.getParameter() <> null\r\n" + "     then \r\n"
				+ "       @For obj in self.getParameter() do\r\n" + "         if obj.getDataType() = \"Integer\"\r\n"
				+ "         then \r\n" + "           a := a + Seq{obj.getValue().asInt()};\r\n"
				+ "           b := b + Seq{obj.getOrderNo()}\r\n" + "         else false\r\n" + "         end ;\r\n"
				+ "         if obj.getDataType() = \"Float\"\r\n" + "         then \r\n"
				+ "           a := a + Seq{obj.getValue().asFloat()};\r\n"
				+ "           b := b + Seq{obj.getOrderNo()}\r\n" + "         else false\r\n" + "         end ;\r\n"
				+ "         if obj.getDataType() = \"Date\"\r\n" + "         then \r\n"
				+ "           e := obj.getValue();\r\n" + "           h := e.splitBy(\" \",0,0);\r\n"
				+ "           f := f.new();\r\n" + "           if h.at(1) = \"Jan\"\r\n" + "           then m := 1\r\n"
				+ "           else false\r\n" + "           end ;\r\n" + "           if h.at(1) = \"Feb\"\r\n"
				+ "           then m := 2\r\n" + "           else false\r\n" + "           end ;\r\n"
				+ "           if h.at(1) = \"Mar\"\r\n" + "           then m := 3\r\n" + "           else false\r\n"
				+ "           end ;\r\n" + "           if h.at(1) = \"Apr\"\r\n" + "           then m := 4\r\n"
				+ "           else false\r\n" + "           end ;\r\n" + "           if h.at(1) = \"May\"\r\n"
				+ "           then m := 5\r\n" + "           else false\r\n" + "           end ;\r\n"
				+ "           if h.at(1) = \"Jun\"\r\n" + "           then m := 6\r\n" + "           else false\r\n"
				+ "           end ;\r\n" + "           if h.at(1) = \"Jul\"\r\n" + "           then m := 7\r\n"
				+ "           else false\r\n" + "           end ;\r\n" + "           if h.at(1) = \"Aug\"\r\n"
				+ "           then m := 8\r\n" + "           else false\r\n" + "           end ;\r\n"
				+ "           if h.at(1) = \"Sep\"\r\n" + "           then m := 9\r\n" + "           else false\r\n"
				+ "           end ;\r\n" + "           if h.at(1) = \"Oct\"\r\n" + "           then m := 10\r\n"
				+ "           else false\r\n" + "           end ;\r\n" + "           if h.at(1) = \"Nov\"\r\n"
				+ "           then m := 11\r\n" + "           else false\r\n" + "           end ;\r\n"
				+ "           if h.at(1) = \"Dec\"\r\n" + "           then m := 12\r\n" + "           else false\r\n"
				+ "           end ;\r\n" + "           f := f.createDate(h.at(2).asInt(),m,h.at(0).asInt());\r\n"
				+ "           a := a + Seq{f};\r\n" + "           b := b + Seq{obj.getOrderNo()}\r\n"
				+ "         else false\r\n" + "         end ;\r\n" + "         if obj.getDataType() = \"String\"\r\n"
				+ "         then \r\n" + "           a := a + Seq{obj.getValue()};\r\n"
				+ "           b := b + Seq{obj.getOrderNo()}\r\n" + "         else false\r\n" + "         end \r\n"
				+ "       end;\r\n" + "       @While not b->isEmpty do\r\n"
				+ "         c := c + Seq{a.at(b.indexOf(b->max))};\r\n"
				+ "         a := a.removeAt(b.indexOf(b->max));\r\n" + "         b := b.removeAt(b.indexOf(b->max))\r\n"
				+ "       end;\r\n" + "       if c.size() <> 0\r\n" + "       then \r\n"
				+ "         i := c.size() - 1;\r\n" + "         @While i >= 0 do\r\n"
				+ "           d := d + Seq{c.at(i)};\r\n" + "           i := i - 1\r\n" + "         end\r\n"
				+ "       else false\r\n" + "       end ;\r\n" + "       d\r\n" + "     else Set{}\r\n"
				+ "     end \r\n" + "  end\r\n" + "end";

		String bodyGetInstanceList = "@Operation getInstanceList[monitor=true,delToClassAllowed=false]():XCore::Seq(ControllerMapping::CommonClass)\r\n"
				+ "  let a = Seq{};\r\n" + "      b = self.getReference();\r\n" + "      c = self.owner\r\n"
				+ "  in if (b.isHead)\r\n" + "     then b.getCommonClass().of().allInstances()->select(i |\r\n"
				+ "            true).asSeq()\r\n" + "     else b.getParent().getCommonClass().slots()->select(s |\r\n"
				+ "            s.type.isKindOf(Associations::End))->select(a |\r\n"
				+ "            a.type.association = b.getAssociation()).value.asSeq()\r\n" + "     end \r\n"
				+ "  end\r\n" + "end";

		String bodyGetInstanceNamesList = "@Operation getInstanceNamesList[monitor=false,delToClassAllowed=false]():XCore::Seq(XCore::String)\r\n"
				+ "  let a = Seq{}\r\n" + "  in self.getInstanceList()->collect(i |\r\n"
				+ "       a := a + Seq{i.name});\r\n" + "     a\r\n" + "  end\r\n" + "end";

		String bodyGetOperationValue = "@Operation getOperationValue[monitor=false,delToClassAllowed=false]():XCore::String\r\n"
				+ "  self.runAction()\r\n" + "end";

		String bodyGetSlotValue = "@Operation getSlotValue[monitor=false,delToClassAllowed=false]():XCore::String\r\n"
				+ "  let a = self.getReference().getCommonClass() then\r\n" + "      ret = null\r\n"
				+ "  in @While a.name.toString() <> \"MetaClass\" do\r\n"
				+ "       if a.hasSlot(self.nameOfModelElement)\r\n"
				+ "       then ret := a.get(self.nameOfModelElement)\r\n" + "       else false\r\n" + "       end ;\r\n"
				+ "       a := a.of()\r\n" + "     end;\r\n" + "     ret\r\n" + "  end\r\n" + "end";

		String bodyGetInjection = "@Operation getInjection[monitor=true,delToClassAllowed=false]():XCore::Element\r\n"
				+ "  if self.of().name().toString() = \"SlotInjection\"\r\n" + "  then self.getSlotValue()\r\n"
				+ "  elseif self.of().name().toString() = \"ActionInjection\"\r\n"
				+ "  then self.getOperationValue()\r\n" + "  elseif self.of().name().toString() = \"ListInjection\"\r\n"
				+ "  then self.getInstanceNamesList()\r\n" + "  else false\r\n" + "  end \r\n" + "end";

		diagram.getComm().addOperation(diagram.getID(), "Action", 0, bodyRunAction);
		diagram.getComm().addOperation(diagram.getID(), "Action", 0, getParameterSize);

		diagram.getComm().addOperation(diagram.getID(), "Reference", 0, bodyChangeCommonClass);
		diagram.getComm().addOperation(diagram.getID(), "Reference", 0, bodyCheckCommonClass);
		diagram.getComm().addOperation(diagram.getID(), "Reference", 0, bodyGetOf);
		diagram.getComm().addOperation(diagram.getID(), "Reference", 0, bodyGetAssociatons);

		diagram.getComm().addOperation(diagram.getID(), "UIControlElement", 1, bodySelectNewInstance);
		diagram.getComm().addOperation(diagram.getID(), "UIControlElement", 1, bodySendMessage);

		diagram.getComm().addOperation(diagram.getID(), "UIElement", 1, bodyGetInstanceByID);
		diagram.getComm().addOperation(diagram.getID(), "UIElement", 1, bodySetParameterValue);

		diagram.getComm().addOperation(diagram.getID(), "Action", 0, bodyGetParamValuesAsList);

		diagram.getComm().addOperation(diagram.getID(), "ListInjection", 0, bodyGetInstanceList);
		diagram.getComm().addOperation(diagram.getID(), "ListInjection", 0, bodyGetInstanceNamesList);

		diagram.getComm().addOperation(diagram.getID(), "ActionInjection", 0, bodyGetOperationValue);

		diagram.getComm().addOperation(diagram.getID(), "SlotInjection", 0, bodyGetSlotValue);

		diagram.getComm().addOperation(diagram.getID(), "Injection", 0, bodyGetInjection);

		// add constraints

		String constraintUniqueIDwithinUI = "self.of().allMetaInstances()->select(a |\r\n"
				+ "     a.level = 0).idOfUIElement.asSet().size() = self.of().allMetaInstances()->select(a |\r\n"
				+ "     a.level = 0).asSet().size()";

		String constraintOneListInjectionPerReference = "self.getControlElement()->select(i | i.of().name.toString() = \"ListInjection\").size() < 2";

		String constraintMissingParent = "if self.isHead = false\r\n" + "   then (self.getParent() <> null)\r\n"
				+ "   else true\r\n" + "   end ";

		diagram.getComm().addConstraint(diagram.getID(), diagram.getPackagePath() + "::Reference", "missingParent", 0,
				constraintMissingParent, "\"References not being the head must refer to a parent reference\"");

		diagram.getComm().addConstraint(diagram.getID(), diagram.getPackagePath() + "::Reference",
				"oneListInjectionPerReference", 0, constraintOneListInjectionPerReference,
				"\"More than one listInjection exists for this reference.\"");

		diagram.getComm().addConstraint(diagram.getID(), diagram.getPackagePath() + "::UIElement", "uniqueIDwithinUI",
				1, constraintUniqueIDwithinUI, "\"The ID of the UI element is not unique!\"");

		// add getter and setter to associations
		ReturnCall<Object> onUpdate = update -> {
			createGetterAndSetterForAssocs();
			return;
		};
		diagram.updateDiagram(onUpdate);

	}

	private void createGetterAndSetterForAssocs() {

		Vector<FmmlxAssociation> assocs = new Vector<>();
		FmmlxAssociation associationDerivedFrom = diagram.getAssociationByPath(
				"AssociationMapping: " + diagram.getPackagePath() + "::UIControlElement::reference");
		assocs.add(associationDerivedFrom);
		FmmlxAssociation associationComposedOf = diagram
				.getAssociationByPath("AssociationMapping: " + diagram.getPackagePath() + "::UserInterface::uIElement");
		assocs.add(associationComposedOf);
		FmmlxAssociation associationRefersToStateOf = diagram
				.getAssociationByPath("AssociationMapping: " + diagram.getPackagePath() + "::Reference::commonClass");
		assocs.add(associationRefersToStateOf);
		FmmlxAssociation associationIsParent = diagram
				.getAssociationByPath("AssociationMapping: " + diagram.getPackagePath() + "::Reference::child");
		assocs.add(associationIsParent);
		FmmlxAssociation associationIsChild = diagram
				.getAssociationByPath("AssociationMapping: " + diagram.getPackagePath() + "::Reference::parentC");
		assocs.add(associationIsChild);
		FmmlxAssociation associationUses = diagram
				.getAssociationByPath("AssociationMapping: " + diagram.getPackagePath() + "::Action::parameter");
		assocs.add(associationUses);
		FmmlxAssociation associationRepresentedAs = diagram
				.getAssociationByPath("AssociationMapping: " + diagram.getPackagePath() + "::Parameter::virtual");
		assocs.add(associationRepresentedAs);

		// create getter and setter, bc this is not done automatically
		for (FmmlxAssociation association : assocs) {

			if (association == null) {
				System.err.println("association is null ...");
				continue;
			}

			String sourceEnd = association.sourceEnd.getNode().getName();
			String targetEnd = association.targetEnd.getNode().getName();

			String accessNameStartToEnd = association.getAccessNameStartToEnd();
			String accessNameEndToStart = association.getAccessNameEndToStart();

			String opName;
			String opBody;

			if (!targetEnd.contains("CommonClass")) {
				// adds getter for the associations
				opName = "get" + accessNameEndToStart.substring(0, 1).toUpperCase() + accessNameEndToStart.substring(1);

				opBody = "@Operation " + opName + "[monitor=false, getterKey=\"" + sourceEnd + "\"]()" + ":" + "Element"
						+ "\n" + "  self." + accessNameEndToStart + "\n" + "end";
				diagram.getComm().addOperation(diagram.getID(), targetEnd, 0, opBody);
			}

			if (!sourceEnd.contains("CommonClass")) {
				// in both directions
				opName = "get" + accessNameStartToEnd.substring(0, 1).toUpperCase() + accessNameStartToEnd.substring(1);
				opBody = "@Operation " + opName + "[monitor=false, getterKey=\"" + targetEnd + "\"]()" + ":" + "Element"
						+ "\n" + "  self." + accessNameStartToEnd + "\n" + "end";
				diagram.getComm().addOperation(diagram.getID(), sourceEnd, 0, opBody);
			}
		}
	}

	private boolean hasNoNamingConflicts() {
		// check for naming conflicts
		boolean noConflict = true;

		Vector<FmmlxObject> objects = diagram.getObjectsReadOnly();
		Vector<String> classNames = new Vector<>();
		classNames.add("UserInterface");
		classNames.add("UIElement");
		classNames.add("Paramater");
		classNames.add("Injection");
		classNames.add("Virtual");
		classNames.add("UIControlElement");
		classNames.add("Reference");
		classNames.add("CommonClass");
		classNames.add("ListInjection");
		classNames.add("ActionInjection");
		classNames.add("SlotInjection");
		classNames.add("Action");

		for (FmmlxObject o : objects) {
			for (String name : classNames) {
				if (o.getName().equals(name)) {
					raiseAlert("Mapping for UI could not be created.\nThe name " + name + " is already used.");
					return false;
				}
			}
		}

		Vector<FmmlxAssociation> assocs = diagram.getAssociations();
		Vector<String> assocNames = new Vector<>();
		assocNames.add("composedOf");
		assocNames.add("representedAs");
		assocNames.add("uses");
		assocNames.add("isParent");
		assocNames.add("isChild");
		assocNames.add("refersToStateOf");
		assocNames.add("derivedFrom");

		for (FmmlxAssociation assoc : assocs) {
			for (String name : assocNames) {
				if (assoc.getName().equals(name)) {
					raiseAlert("Mapping for UI could not be created.\nThe name " + name + " is already used.");
					return false;
				}
			}
		}

		return noConflict;
	}

	private void raiseAlert(String alertMessage) {
		Alert alert = new Alert(AlertType.CONFIRMATION, alertMessage);
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				return;
			}
		});
	}
}
