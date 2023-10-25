package tool.clients.customui;

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
import tool.clients.fmmlxdiagrams.Multiplicity;
import tool.clients.fmmlxdiagrams.ReturnCall;

/*
 * FH 2023
 * The DeufaultUIModelGenerator generates the needed UI model necessary to create custom UIs.
 * This is needed if the user first models a domain model and later decides that a UI shall be included.
 * This class ensures that all needed classes are instantiated correctly and that all prior meta classes
 * are mapped to CommonClass.
 */

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

		Vector<FmmlxObject> objects = diagram.getObjectsReadOnly();
		Vector<Integer> levels = diagram.getAllObjectLevel();
		Vector<FmmlxAssociation> assocs = diagram.getAssociations();
		Vector<FmmlxLink> links = diagram.getAssociationInstance();

		Vector<FmmlxObject> objectsWithParents = new Vector<>();

		// objects must be created according to their level so instances can be created
		// performance may not be optimal when there are many objects -> maybe find a
		// different approach
		for (int level : levels) {

			// create dummy class for level > 0
			if (level > 0)
				actions.addInstance("CommonClassL" + (level + 1), "CommonClassL" + level);

			for (FmmlxObject o : objects) {

				if (o.getName().contains("CommonClass")) {
					continue;
				}

				// only generate the objects of the current level
				if (level != o.getLevel()) {
					continue;
				}

				// ignore commonclass objects
				if (o.getName().contains("CommonClass")) {
					continue;
				}

				Vector<String> parents = new Vector<>();
				try {
					if (o.getParentsPaths().size() > 1) {
						objectsWithParents.add(o);
					}
				} catch (Exception e) {
					// no parents have been set
				}

				int x = (int) Math.round(o.getX());
				int y = (int) Math.round(o.getY());

				if (o.getMetaClassName().equals("Root::FMMLx::MetaClass")) {
					// if the class is on an instance of metaclass than it needs to be removed from
					// the canvas
					diagram.getComm().removeClass(diagram.getID(), o.getName(), 0);

					String commonClassName = "CommonClassL" + (o.getLevel() + 1);
					// instances of meta class must be regenerated as instances of the corresponding
					// commonclass
					diagram.getComm().addNewInstance(diagram.getID(), commonClassName, o.getName(), o.getLevel(),
							parents, o.isAbstract(), o.isCollective(), x, y, o.isHidden());
				} else {
					// instances of other classes must not be deleted but regenerated
					diagram.getComm().addNewInstance(diagram.getID(), o.getMetaClassName(), o.getName(), o.getLevel(),
							parents, o.isAbstract(), o.isCollective(), x, y, o.isHidden());
				}

				if (o.getLevel() > 0) {

					// operation gets set at last AFTER associations so getter and setter from
					// associations can be set first and don't lead to duplications

					for (FmmlxAttribute att : o.getOwnAttributes()) {
						diagram.getComm().addAttribute(diagram.getID(), o.getName(), att.getName(), att.getLevel(),
								att.getType(), att.getMultiplicity());
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
					assoc.getName(), "", assoc.getMultiplicityEndToStart(), assoc.getMultiplicityStartToEnd(),
					assoc.getLevelSource(), assoc.getLevelTarget(), assoc.isSourceVisible(), assoc.isTargetVisible(),
					assoc.isSymmetric(), assoc.isTransitive());

		}

		for (FmmlxLink link : links) {
			diagram.getComm().addAssociationInstance(diagram.getID(), link.getSourceNode().getName(),
					link.getTargetNode().getName(), link.getAssociation().getName());
		}

		// operations are added
		// what about getter / setter from assocs?
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

				for (FmmlxAssociation assoc : assocs) {
					if (added)
						continue;
					// check if name contains get or set AND the the name of an association
					if ((op.getName().contains("get")||op.getName().contains("set"))&&(op.getName().toLowerCase().contains(assoc.getAccessNameEndToStart().toLowerCase()))) {
						added = true;
					}
					if ((op.getName().contains("get")||op.getName().contains("set"))&&(op.getName().toLowerCase().contains(assoc.getAccessNameStartToEnd().toLowerCase()))) {
						added = true;
					}

				}

				if (added) {
				} else {
					FmmlxObject newObject = diagram.getObjectByPath(oldObject.getName());
					diagram.getComm().addOperation2(diagram.getID(), newObject.getName(), op.getLevel(), op.getBody());
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

		actions.addInstance("UIControlElement", "Injection");
		actions.addInstance("UIElement", "Virtual");
		actions.addInstance("UIControlElement", "Action");

		parents.add("Injection");
		actions.addInstance("UIControlElement", "ListInjection", parents);
		actions.addInstance("UIControlElement", "SlotInjection", parents);

		parents.add("Action");
		actions.addInstance("UIControlElement", "ActionInjection", parents);
		parents.clear();

		// add association
		diagram.getComm().addAssociation(diagram.getID(), "UserInterface", "UIElement", "customUserInterface",
				"uIElement", "composedOf", null, new Multiplicity(0, 1, true, false, false),
				new Multiplicity(0, 2147483647, false, false, false), 0, 0, true, true, false, false);

		diagram.getComm().addAssociation(diagram.getID(), "Parameter", "UIElement", "parameter", "uIElement",
				"representedAs", null, new Multiplicity(0, 1, true, false, true),
				new Multiplicity(1, 1, true, false, true), 0, 0, true, true, false, false);

		diagram.getComm().addAssociation(diagram.getID(), "Action", "Parameter", "action", "parameter", "uses", null,
				new Multiplicity(0, 2147483647, false, false, true),
				new Multiplicity(0, 2147483647, false, false, true), 0, 0, true, true, false, false);

		diagram.getComm().addAssociation(diagram.getID(), "UIControlElement", "Reference", "controlElement",
				"reference", "derivedFrom", null, new Multiplicity(0, 2147483647, false, false, true),
				new Multiplicity(1, 1, true, false, true), 0, 0, true, true, false, false);

		diagram.getComm().addAssociation(diagram.getID(), "Reference", "Reference", "parent", "parent", "isParent",
				null, new Multiplicity(0, 2147483647, false, false, true), new Multiplicity(0, 1, true, false, true), 0,
				0, false, true, false, false);

		diagram.getComm().addAssociation(diagram.getID(), "Reference", "Reference", "child", "child", "isChild", null,
				new Multiplicity(0, 1, true, false, true), new Multiplicity(0, 2147483647, false, false, true), 0, 0,
				false, true, false, false);

		diagram.getComm().addAssociation(diagram.getID(), "Reference", commomClassName, "reference", "commonClass",
				"refersToStateOf", null, new Multiplicity(0, 2147483647, false, false, true),
				new Multiplicity(1, 1, true, false, true), 0, -1, false, true, false, false);

		// add Attributes
		Multiplicity multOne = new Multiplicity(1, 1, true, false, false);

		diagram.getComm().addAttribute(diagram.getID(), "UserInterface", "pathToFXML", 0, "String", multOne);
		diagram.getComm().addAttribute(diagram.getID(), "UserInterface", "pathToIconOfWindow", 0, "String", multOne);
		diagram.getComm().addAttribute(diagram.getID(), "UserInterface", "titleOfUI", 0, "String", multOne);

		diagram.getComm().addAttribute(diagram.getID(), "UIElement", "idOfUIElement", 0, "String", multOne);

		diagram.getComm().addAttribute(diagram.getID(), "Parameter", "dataType", 0, "String", multOne);
		diagram.getComm().addAttribute(diagram.getID(), "Parameter", "orderNo", 0, "Integer", multOne);
		diagram.getComm().addAttribute(diagram.getID(), "Parameter", "value", 0, "String", multOne);

		diagram.getComm().addAttribute(diagram.getID(), "Action", "eventName", 0, "String", multOne);

		diagram.getComm().addAttribute(diagram.getID(), "ListInjection", "isListView", 0, "Boolean", multOne);

		diagram.getComm().addAttribute(diagram.getID(), "UIControlElement", "nameOfModelElement", 0, "String", multOne);

		diagram.getComm().addAttribute(diagram.getID(), "Reference", "associationName", 0, "String", multOne);
		diagram.getComm().addAttribute(diagram.getID(), "Reference", "isHead", 0, "Boolean", multOne);

		// add functions
		String bodyRunAction = "@Operation runAction[monitor=false,delToClassAllowed=false]():XCore::Element\r\n"
				+ "  let a = self.getReference().getCommonClass() then\r\n"
				+ "      b = self.getParameters().size() then\r\n" + "      res = null\r\n"
				+ "  in @While a.name.toString() <> \"MetaClass\" do\r\n"
				+ "       if a.of().hasOperation(self.nameOfModelElement.asSymbol(),b)\r\n"
				+ "       then res := a.of().getOperation(self.nameOfModelElement).invoke(a,self.getParamValuesAsList())\r\n"
				+ "       else false\r\n" + "       end ;\r\n" + "       a := a.of()\r\n" + "     end;\r\n"
				+ "     res\r\n" + "  end\r\n" + "end";

		String bodySelectNewInstance = "@Operation selectNewInstance[monitor=false,delToClassAllowed=false](idUI : XCore::String,instanceName : XCore::String):XCore::Element\r\n"
				+ "  let a = self.getInstanceByID(idUI).asSeq().at(0)\r\n"
				+ "  in a.getReference().changeCommonClass(instanceName)\r\n" + "  end\r\n" + "end";

		String bodySendMessage = "@Operation sendMessage[monitor=false,delToClassAllowed=false](idUI : XCore::String,eventID : XCore::String):XCore::Element\r\n"
				+ "  let a = self.getInstanceByID(idUI)\r\n" + "  in @For obj in a.asSeq() do\r\n"
				+ "       if (obj.getEventName().toString() = eventID)\r\n" + "       then obj.runAction()\r\n"
				+ "       else false\r\n" + "       end \r\n" + "     end\r\n" + "  end\r\n" + "end";

		String bodyGetOf = "@Operation getOf[monitor=true,delToClassAllowed=false]():XCore::Class\r\n"
				+ "  self.getCommonClass().of()\r\n" + "end";

		String bodyGetAssociatons = "@Operation getAssociation[monitor=false,delToClassAllowed=false]():Associations::Association\r\n"
				+ "  if (self.associationName <> \"\")\r\n" + "  then self.owner.associations.asSeq()->select(i |\r\n"
				+ "         i.name.toString() = self.associationName).at(0)\r\n" + "  else false\r\n" + "  end \r\n"
				+ "end";

		String bodyChangeCommonClass = "@Operation changeCommonClass[monitor=false,delToClassAllowed=false](newCommonObject : XCore::String):XCore::Element\r\n"
				+ "  let a = Clients::FmmlxDiagrams::FmmlxManipulator() then\r\n"
				+ "      b = self.owner.classes->select(i |\r\n"
				+ "            i.name.toString() = newCommonObject)\r\n" + "  in if (b <> null)\r\n" + "     then \r\n"
				+ "       a.removeAssociationInstance(self.owner.getAssociation(\"refersToStateOf\"),self,self.getCommonClass());\r\n"
				+ "       a.addAssociationInstance(self.owner,self,b.asSeq().at(0),self.owner.getAssociation(\"refersToStateOf\"));\r\n"
				+ "       if (self.getChilds() <> null)\r\n" + "       then @For child in self.getChilds() do\r\n"
				+ "              child.checkCommonClass()\r\n" + "            end\r\n" + "       else false\r\n"
				+ "       end \r\n" + "     else false\r\n" + "     end \r\n" + "  end\r\n" + "end";

		String bodyCheckCommonClass = "@Operation checkCommonClass[monitor=false,delToClassAllowed=false]():XCore::Element\r\n"
				+ "  let a = self.getControlElements()->select(i |\r\n"
				+ "            i.of().name.toString() = \"ListInjection\")\r\n" + "  in if (not a->isEmpty)\r\n"
				+ "     then if (a.asSeq().at(0).getInstanceList().contains(self.getCommonClass()))\r\n"
				+ "          then true\r\n"
				+ "          else self.changeCommonClass(a.asSeq().at(0).getInstanceList().at(0).name.toString())\r\n"
				+ "          end \r\n"
				+ "     else self.changeCommonClass(self.owner.getLinkEnds(self.getParent().getCommonClass(),self.getAssociation().name).asSeq().at(0).name.toString())\r\n"
				+ "     end ;\r\n" + "     if (self.getChilds() <> null)\r\n"
				+ "     then @For child in self.getChilds() do\r\n" + "            child.checkCommonClass()\r\n"
				+ "          end\r\n" + "     else false\r\n" + "     end \r\n" + "  end\r\n" + "end";

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
				+ "  in @For obj in self.getParameters() do\r\n" + "       if obj.getDataType() = \"Integer\"\r\n"
				+ "       then \r\n" + "         a := a + Seq{obj.getValue().asInt()};\r\n"
				+ "         b := b + Seq{obj.getOrderNo()}\r\n" + "       else false\r\n" + "       end ;\r\n"
				+ "       if obj.getDataType() = \"Float\"\r\n" + "       then \r\n"
				+ "         a := a + Seq{obj.getValue().asFloat()};\r\n" + "         b := b + Seq{obj.getOrderNo()}\r\n"
				+ "       else false\r\n" + "       end ;\r\n" + "       if obj.getDataType() = \"Date\"\r\n"
				+ "       then \r\n" + "         e := obj.getValue();\r\n" + "         h := e.splitBy(\" \",0,0);\r\n"
				+ "         f := f.new();\r\n" + "         if h.at(1) = \"Jan\"\r\n" + "         then m := 1\r\n"
				+ "         else false\r\n" + "         end ;\r\n" + "         if h.at(1) = \"Feb\"\r\n"
				+ "         then m := 2\r\n" + "         else false\r\n" + "         end ;\r\n"
				+ "         if h.at(1) = \"Mar\"\r\n" + "         then m := 3\r\n" + "         else false\r\n"
				+ "         end ;\r\n" + "         if h.at(1) = \"Apr\"\r\n" + "         then m := 4\r\n"
				+ "         else false\r\n" + "         end ;\r\n" + "         if h.at(1) = \"May\"\r\n"
				+ "         then m := 5\r\n" + "         else false\r\n" + "         end ;\r\n"
				+ "         if h.at(1) = \"Jun\"\r\n" + "         then m := 6\r\n" + "         else false\r\n"
				+ "         end ;\r\n" + "         if h.at(1) = \"Jul\"\r\n" + "         then m := 7\r\n"
				+ "         else false\r\n" + "         end ;\r\n" + "         if h.at(1) = \"Aug\"\r\n"
				+ "         then m := 8\r\n" + "         else false\r\n" + "         end ;\r\n"
				+ "         if h.at(1) = \"Sep\"\r\n" + "         then m := 9\r\n" + "         else false\r\n"
				+ "         end ;\r\n" + "         if h.at(1) = \"Oct\"\r\n" + "         then m := 10\r\n"
				+ "         else false\r\n" + "         end ;\r\n" + "         if h.at(1) = \"Nov\"\r\n"
				+ "         then m := 11\r\n" + "         else false\r\n" + "         end ;\r\n"
				+ "         if h.at(1) = \"Dec\"\r\n" + "         then m := 12\r\n" + "         else false\r\n"
				+ "         end ;\r\n" + "         f := f.createDate(h.at(2).asInt(),m,h.at(0).asInt());\r\n"
				+ "         a := a + Seq{f};\r\n" + "         b := b + Seq{obj.getOrderNo()}\r\n"
				+ "       else false\r\n" + "       end ;\r\n" + "       if obj.getDataType() = \"String\"\r\n"
				+ "       then \r\n" + "         a := a + Seq{obj.getValue()};\r\n"
				+ "         b := b + Seq{obj.getOrderNo()}\r\n" + "       else false\r\n" + "       end \r\n"
				+ "     end;\r\n" + "     @While not b->isEmpty do\r\n"
				+ "       c := c + Seq{a.at(b.indexOf(b->max))};\r\n" + "       a := a.removeAt(b.indexOf(b->max));\r\n"
				+ "       b := b.removeAt(b.indexOf(b->max))\r\n" + "     end;\r\n" + "     if c.size() <> 0\r\n"
				+ "     then \r\n" + "       i := c.size() - 1;\r\n" + "       @While i >= 0 do\r\n"
				+ "         d := d + Seq{c.at(i)};\r\n" + "         i := i - 1\r\n" + "       end\r\n"
				+ "     else false\r\n" + "     end ;\r\n" + "     d\r\n" + "  end\r\n" + "end";

		String bodyGetInstanceList = "@Operation getInstanceList[monitor=true,delToClassAllowed=false]():XCore::Seq(ControllerMapping::CommonClass)\r\n"
				+ "  let a = Seq{};\r\n" + "      b = self.getReference();\r\n" + "      c = self.owner\r\n"
				+ "  in if (b.isHead)\r\n" + "     then b.getCommonClass().of().allInstances()->select(i |\r\n"
				+ "            true).asSeq()\r\n" + "     else b.getCommonClass().of().allInstances()->select(i |\r\n"
				+ "            c.hasAssociationInstance(b.getParent().getCommonClass(),i,b.getAssociation())).asSeq()\r\n"
				+ "     end \r\n" + "  end\r\n" + "end";

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

		diagram.getComm().addOperation2(diagram.getID(), "Action", 0, bodyRunAction);

		diagram.getComm().addOperation2(diagram.getID(), "Reference", 0, bodyChangeCommonClass);
		diagram.getComm().addOperation2(diagram.getID(), "Reference", 0, bodyCheckCommonClass);
		diagram.getComm().addOperation2(diagram.getID(), "Reference", 0, bodyGetOf);
		diagram.getComm().addOperation2(diagram.getID(), "Reference", 0, bodyGetAssociatons);

		diagram.getComm().addOperation2(diagram.getID(), "UIControlElement", 1, bodySelectNewInstance);
		diagram.getComm().addOperation2(diagram.getID(), "UIControlElement", 1, bodySendMessage);

		diagram.getComm().addOperation2(diagram.getID(), "UIElement", 1, bodyGetInstanceByID);
		diagram.getComm().addOperation2(diagram.getID(), "UIElement", 1, bodySetParameterValue);

		diagram.getComm().addOperation2(diagram.getID(), "Action", 0, bodyGetParamValuesAsList);

		diagram.getComm().addOperation2(diagram.getID(), "ListInjection", 0, bodyGetInstanceList);
		diagram.getComm().addOperation2(diagram.getID(), "ListInjection", 0, bodyGetInstanceNamesList);

		diagram.getComm().addOperation2(diagram.getID(), "ActionInjection", 0, bodyGetOperationValue);

		diagram.getComm().addOperation2(diagram.getID(), "SlotInjection", 0, bodyGetSlotValue);

		diagram.getComm().addOperation2(diagram.getID(), "Injection", 0, bodyGetInjection);

		// add constraints

		String constraintUniqueIDwithinUI = "self.of().allMetaInstances()->select(a |\r\n"
				+ "     a.level = 0).idOfUIElement.asSet().size() = self.of().allMetaInstances()->select(a |\r\n"
				+ "     a.level = 0).asSet().size()";

		String constraintOneListInjectionPerReference = "self.getControlElements()->select(i | i.of().name.toString() = \"ListInjection\").size() < 2";

		String constraintMissingParent = "if self.isHead = false\r\n" + "   then (self.getParent() <> null)\r\n"
				+ "   else true\r\n" + "   end ";

		diagram.getComm().addConstraint(diagram.getID(), diagram.getPackagePath() + "::Reference", "missingParent", 0,
				constraintMissingParent, "\"References not being the head must refer to a parent reference\"");

		diagram.getComm().addConstraint(diagram.getID(), diagram.getPackagePath() + "::Reference",
				"oneListInjectionPerReference", 0, constraintOneListInjectionPerReference,
				"\"More than one listInjection exists for this reference.\"");

		diagram.getComm().addConstraint(diagram.getID(), diagram.getPackagePath() + "::UIElement", "uniqueIDwithinUI",
				1, constraintUniqueIDwithinUI, "\"The ID of the UI element is not unique!\"");

		diagram.updateDiagram();
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
