package tool.clients.fmmlxdiagrams;

import java.util.Vector;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer.PathNotFoundException;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.IssueBox;
import tool.clients.fmmlxdiagrams.graphics.NodeBaseElement;
import tool.clients.fmmlxdiagrams.graphics.NodeBox;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.NodeImage;
import tool.clients.fmmlxdiagrams.graphics.NodeLabel;

public class DefaultFmmlxObjectDisplay extends AbstractFmmlxObjectDisplay {
	
	private final static NodeBaseElement.Action NO_ACTION = () -> {};

	static int GAP = 5;
	protected int minWidth = 100;

	final int INST_LEVEL_WIDTH = 7;
	final int MIN_BOX_HEIGHT = 4;
	final int EXTRA_Y_PER_LINE = 3;

	
	public DefaultFmmlxObjectDisplay(FmmlxDiagram diagram, FmmlxObject object) {
		super(diagram, object);
	}

	public Color getLevelBackgroundColor(FmmlxDiagram diagram) {
		return diagram.levelColorScheme.getLevelBgColor(this.object.level);
	}

	public Color getLevelFontColor(double opacity, FmmlxDiagram diagram) {
		return diagram.levelColorScheme.getLevelFgColor(this.object.level, opacity);
	}

	public void layout() {
		object.requiresReLayout = false;
		NodeGroup group = new NodeGroup(new Affine(1, 0, object.x, 0, 1, object.y));
		object.rootNodeElement = group;
		double neededWidth = calculateNeededWidth(diagram);
		
		//determine text height
		double textHeight = FmmlxDiagram.calculateTextHeight();
		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double currentY = 0;		

		String parentString = getParentsList(diagram);
		int headerLines = /*hasParents()*/(!"".equals(parentString)) ? 3 : 2;
		NodeBox header = new NodeBox(0, currentY, neededWidth, textHeight * headerLines + EXTRA_Y_PER_LINE, getLevelBackgroundColor(diagram), Color.BLACK, (x) -> 1., PropertyType.Class);
		group.addNodeElement(header);
		FmmlxObject ofObj = null;
		try {
			ofObj = diagram.getObjectByPath(object.getOfPath());
		} catch (PathNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
		String ofName = (ofObj == null) ? "MetaClass" : ofObj.name;
		
		NodeLabel metaclassLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight, getLevelFontColor(.65, diagram), null, object, NO_ACTION, "^" + ofName + "^", FontPosture.REGULAR, FontWeight.BOLD) ;
		NodeLabel levelLabel = new NodeLabel(Pos.BASELINE_LEFT, 4, textHeight * 2, getLevelFontColor(.4, diagram), null, object, NO_ACTION, "" + (-1==object.level?"?":object.level), FontPosture.REGULAR, FontWeight.BOLD, 2.);
		NodeLabel nameLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 2, getLevelFontColor(1., diagram), null, object, NO_ACTION, object.name, object.isAbstract()?FontPosture.ITALIC:FontPosture.REGULAR, FontWeight.BOLD);
		header.addNodeElement(metaclassLabel);
		header.addNodeElement(levelLabel);
		header.addNodeElement(nameLabel);

		if ((!"".equals(parentString))) {
			NodeLabel parentsLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 3, getLevelFontColor(1., diagram), null, object, NO_ACTION, parentString, object.isAbstract()?FontPosture.ITALIC:FontPosture.REGULAR, FontWeight.NORMAL);
			header.addNodeElement(parentsLabel);
		}

		currentY += headerLines * textHeight + EXTRA_Y_PER_LINE;
		
		Vector<Issue> issues = diagram.getIssues(object);
		if(issues.size() > 0 && object.showConstraintReports) {
			double issueBoxHeight = lineHeight * issues.size() + EXTRA_Y_PER_LINE;
			NodeBox issueBox = new IssueBox(0, currentY, neededWidth, issueBoxHeight, 
				Color.BLACK, Color.BLACK, (x) -> 1., PropertyType.Issue);
			group.addNodeElement(issueBox);
			double issY = 0;
			for(Issue i : issues) {
				issY += lineHeight;
				
				NodeLabel issueLabel = new NodeLabel(Pos.BASELINE_LEFT, IssueBox.BOX_SIZE * 1.5, issY, new Color(1., .8, 0., 1.), null, object, () -> i.performResolveAction(diagram), i.getText(), true, i.issueNumber);
				issueBox.addNodeElement(issueLabel);
				issueLabel.activateSpecialMode(neededWidth - 3 * IssueBox.BOX_SIZE);
			}
		
			currentY += issueBoxHeight;
		}

		int attSize = countAttributesToBeShown();
		double attBoxHeight = Math.max(lineHeight * attSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double yAfterAttBox = currentY + attBoxHeight;
		double attY = 0;
		NodeBox attBox = new NodeBox(0, currentY, neededWidth, attBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Attribute);
		group.addNodeElement(attBox);

		for (FmmlxAttribute att : object.getOwnAttributes()) {
			attY += lineHeight;
			NodeLabel.Action changeAttNameAction = () -> diagram.getActions().changeNameDialog(object, PropertyType.Attribute, att);
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.BLACK, null, att, changeAttNameAction, att.getName() + ": " + att.getTypeShort() +"["+ att.getMultiplicity() + "]");
			attBox.addNodeElement(attLabel);
			NodeLabel.Action changeAttLevelAction = () -> diagram.getActions().changeLevelDialog(object, PropertyType.Attribute);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, attY, Color.WHITE, Color.BLACK, att, changeAttLevelAction, att.level + "");
			attBox.addNodeElement(attLevelLabel);
		}
		for (FmmlxAttribute att : object.getOtherAttributes()) {
			if(object.showDerivedAttributes) {
			attY += lineHeight;
			String ownerName = att.ownerPath;
			try{ownerName = diagram.getObjectByPath(att.ownerPath).name;} catch (Exception e) {}
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.GRAY, null, att, NO_ACTION, att.getName() + ": " + att.getTypeShort() +"["+ att.getMultiplicity() + "]" + " (from " + ownerName + ")");
			attBox.addNodeElement(attLabel);
			NodeLabel attLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, attY, Color.WHITE, Color.GRAY, att, NO_ACTION, att.level == -1 ? " " : att.level + "");
			attBox.addNodeElement(attLevelLabel);
			}
		}
		currentY = yAfterAttBox;

		double yAfterOpsBox = currentY;

		int opsSize = countOperationsToBeShown();
		double opsBoxHeight = Math.max(lineHeight * opsSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double opsY = 0;
		NodeBox opsBox = new NodeBox(0, currentY, neededWidth, opsBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Operation);
		if (object.showOperations && opsSize > 0) {
			yAfterOpsBox = currentY + opsBoxHeight;
			group.addNodeElement(opsBox);
			for (FmmlxOperation o : object.getOwnOperations()) {
				if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					opsY += lineHeight;
					NodeLabel.Action changeOpLevelAction = () -> diagram.getActions().changeLevelDialog(object, PropertyType.Operation);
					NodeLabel opLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.BLACK, o, changeOpLevelAction, o.getLevelString() + "");
					opsBox.addNodeElement(opLevelLabel);
					int labelX = 14;
					if(o.isDelegateToClassAllowed()) {
						NodeImage delIcon = new NodeImage(14, opsY, "resources/gif/XCore/delegationDown.png", o, NO_ACTION);
						opsBox.addNodeElement(delIcon);
						labelX +=16;
					}					
					NodeLabel.Action changeOpBodyAction = () -> diagram.getActions().changeBodyDialog(object, o);
					NodeLabel opLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.BLACK, null, o, changeOpBodyAction, o.getFullString(diagram));
					opsBox.addNodeElement(opLabel);
				}
			}
			for (FmmlxOperation o : object.getOtherOperations()) {
				if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(object.showDerivedOperations) {
					opsY += lineHeight;
					NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, NO_ACTION, o.getLevelString() + "");
					opsBox.addNodeElement(oLevelLabel);
					NodeImage inhIcon = new NodeImage(14, opsY, (diagram.getObjectByPath(o.getOwner()).getLevel() == object.level) ? "resources/gif/Inheritance.gif" : "resources/gif/Dependency.gif", o, NO_ACTION);
					opsBox.addNodeElement(inhIcon);
					int labelX = 30;
					if(o.isDelegateToClassAllowed()) {
						NodeImage delIcon = new NodeImage(30, opsY, "resources/gif/XCore/delegationDown.png", o, NO_ACTION);
						opsBox.addNodeElement(delIcon);
						labelX +=16;
					}	
					NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")");
					opsBox.addNodeElement(oLabel);
					}
				}
			}
			for (FmmlxOperation o : object.getDelegatedOperations()) {
				if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(object.showDerivedOperations) {
					opsY += lineHeight;
					NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, NO_ACTION, o.getLevelString() + "");
					opsBox.addNodeElement(oLevelLabel);
					String iconS = "resources/gif/Inheritance.gif";
					try{
						if(diagram.getObjectByPath(object.ofPath).getAllOperations().contains(o)) iconS = "resources/gif/Dependency.gif";
					} catch (PathNotFoundException pnfe) {}
//					if(diagram.getObjectByPath(ofPath) != null && 
					if(object.getDelegatesTo(false) != null && object.getDelegatesTo(false).getAllOperations().contains(o)) iconS = "resources/gif/XCore/delegation.png";
						
					NodeImage delIcon = new NodeImage(14, opsY, iconS, o, NO_ACTION);
					opsBox.addNodeElement(delIcon);
					int labelX = 30;
					if(o.isDelegateToClassAllowed()) {
						NodeImage del2Icon = new NodeImage(30, opsY, "resources/gif/XCore/delegationDown.png", o, NO_ACTION);
						opsBox.addNodeElement(del2Icon);
						labelX +=16;
					}	
					NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")");
					opsBox.addNodeElement(oLabel);
					}
				}
			}			
			for (FmmlxOperation o : object.getDelegateToClassOperations()) {
				if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(object.showDerivedOperations) {
						opsY += lineHeight;
						NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, 30, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")");
						opsBox.addNodeElement(oLabel);
						NodeLabel oLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, opsY, Color.WHITE, Color.GRAY, o, NO_ACTION, o.getLevelString() + "");
						opsBox.addNodeElement(oLevelLabel);
						NodeImage delIcon = new NodeImage(14, opsY, "resources/gif/XCore/delegationUp.png", o, NO_ACTION);
						opsBox.addNodeElement(delIcon);
					}
				}
			}
		}
		currentY = yAfterOpsBox;
		
		double yAfterConstraintBox = currentY;
		int constraintSize = object.getConstraints().size();
		double constraintBoxHeight = Math.max(lineHeight * constraintSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double constraintY = 0;
		NodeBox coinstraintsBox = new NodeBox(0, currentY, neededWidth, constraintBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Constraint);
		if (object.showConstraints && constraintSize > 0) {
			yAfterConstraintBox = currentY + constraintBoxHeight;
			group.addNodeElement(coinstraintsBox);
			for (Constraint con : object.getConstraints()) {
				constraintY += lineHeight;
				NodeLabel.Action editConstraintAction = () -> diagram.getActions().editConstraint(object,con);
				NodeLabel constraintLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, constraintY, new Color(.8,0,0,1), null, con, editConstraintAction, con.getName());
				coinstraintsBox.addNodeElement(constraintLabel);
				NodeLabel constraintLevelLabel = new NodeLabel(Pos.BASELINE_CENTER, 7, constraintY, Color.WHITE, new Color(.8,0,0,1), con, NO_ACTION, con.level + "");
				coinstraintsBox.addNodeElement(constraintLevelLabel);
			}
		}
		currentY = yAfterConstraintBox;

		double yAfterSlotBox = currentY;
		int slotSize = object.slots.size();
		double slotBoxHeight = Math.max(lineHeight * slotSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double slotsY = 0;
		NodeBox slotsBox = new NodeBox(0, currentY, neededWidth, slotBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Slot);
		if (object.showSlots && slotSize > 0) {
			yAfterSlotBox = currentY + slotBoxHeight;
			group.addNodeElement(slotsBox);
			for (FmmlxSlot s : object.slots) {
				slotsY += lineHeight;
				NodeLabel.Action changeSlotValueAction = () -> diagram.getActions().changeSlotValue(object, s);
				NodeLabel slotNameLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, slotsY, Color.BLACK, null, s, changeSlotValueAction, s.getName() + " = ");
				slotsBox.addNodeElement(slotNameLabel);
				NodeLabel slotValueLabel = new NodeLabel(Pos.BASELINE_LEFT, 3 + slotNameLabel.getWidth(), slotsY, new Color(0.0,0.4,0.2,1.0), new Color(0.85,0.9,0.85,1.0), s, changeSlotValueAction, "" + s.getValue());
				slotsBox.addNodeElement(slotValueLabel);
			}
		}
		currentY = yAfterSlotBox;

		double yAfterOPVBox = currentY;
		int opvSize = object.getOperationValues().size();
//		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double opvBoxHeight = Math.max(lineHeight * opvSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double opvY = 0;
		NodeBox opvBox = new NodeBox(0, currentY, neededWidth, opvBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.OperationValue);
		if (object.showOperationValues && opvSize > 0) {
			yAfterOPVBox = currentY + opvBoxHeight;
			group.addNodeElement(opvBox);
			for (FmmlxOperationValue opv : object.getOperationValues()) {
				opvY += lineHeight;
				NodeLabel opvNameLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, opvY, Color.BLACK, null, opv, NO_ACTION, opv.getName() + "()->");
				opvBox.addNodeElement(opvNameLabel);
				NodeLabel opvValueLabel = new NodeLabel(Pos.BASELINE_LEFT, 5 + opvNameLabel.getWidth(), opvY, opv.isInRange()?Color.YELLOW:Color.RED, Color.BLACK, opv, NO_ACTION, "" + opv.getValue());
				opvBox.addNodeElement(opvValueLabel);
			}
		}
		currentY = yAfterOPVBox;

		NodeBox selectionBox = new NodeBox(0, 0, neededWidth, currentY, new Color(0, 0, 0, 0), Color.BLACK, (selected) -> selected?3:1, PropertyType.Selection);
		group.addNodeElement(selectionBox);

//		object.width = (int) neededWidth;
//		object.height = (int) currentY;

//		object.handlePressedOnNodeElement(object.lastClick, diagram);
	}

	private int countOperationsToBeShown() {
		int counter=0;
		for (FmmlxOperation o : object.getOwnOperations()) {
			if(object.showGettersAndSetters  ||  !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
			counter++;	
			}
		}

		for (FmmlxOperation o : object.getOtherOperations()) {
			if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(object.showDerivedOperations) {
				counter++;
				}
			}
		}		
		for (FmmlxOperation o : object.getDelegatedOperations()) {
			if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(object.showDerivedOperations) {
				counter++;
				}
			}
		}		
		for (FmmlxOperation o : object.getDelegateToClassOperations()) {
			if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(object.showDerivedOperations) {
				counter++;
				}
			}
		}
		return counter;
	}
	
	private int countAttributesToBeShown() {
		return (object.showDerivedAttributes?1:0)*object.getOtherAttributes().size() + object.getOwnAttributes().size();
	}

	private boolean hasParents() {
		return object.getParentsPaths().size() != 0;
	}

	private double calculateNeededWidth(FmmlxDiagram diagram) {
		double neededWidth = FmmlxDiagram.calculateTextWidth(object.name); 

		try {
			FmmlxObject of = diagram.getObjectByPath(object.ofPath);
			neededWidth = Math.max(neededWidth, FmmlxDiagram.calculateTextWidth(object.getLevel() + "^" + of.name + "^"));
		} catch (PathNotFoundException e) {
			neededWidth = Math.max(neededWidth, FmmlxDiagram.calculateTextWidth(object.getLevel() + "^MetaClass^"));
		}
		
		neededWidth += 30; // for level number;

		//determine maximal width of attributes
		for (FmmlxAttribute att : object.getOwnAttributes()) {
			neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(att.name + ": " + att.getTypeShort() +"["+ att.getMultiplicity() + "]") + INST_LEVEL_WIDTH, neededWidth);
		}
		for (FmmlxAttribute att : object.getOtherAttributes()) {
			if(object.showDerivedAttributes) {
				String ownerName = att.ownerPath;
				try{ownerName = diagram.getObjectByPath(att.ownerPath).name;} catch (Exception e) {}
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(att.name + ": " + att.getTypeShort() +"["+ att.getMultiplicity() + "]" + " (from " + ownerName + ")") + INST_LEVEL_WIDTH, neededWidth);
			}
		}
//		//determine maximal width of operations
		if (object.showOperations) {
			for (FmmlxOperation o : object.getOwnOperations()) {
				if(object.showGettersAndSetters  ||  !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
				String text = o.getFullString(diagram);
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(text) + INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
				}
			}	
			for (FmmlxOperation o : object.getOtherOperations()) {
				if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(object.showDerivedOperations) {
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")") + 4 * INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
					}
				}
			}	
			for (FmmlxOperation o : object.getDelegatedOperations()) {
				if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(object.showDerivedOperations) {
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")") + 4 * INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
					}
				}
			}
			for (FmmlxOperation o : object.getDelegateToClassOperations()) {
				if(object.showGettersAndSetters || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(object.showDerivedOperations) {
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).name + ")") + 4 * INST_LEVEL_WIDTH, neededWidth);
					}
				}
			}
		}
		//determine maximal width of slots
		if (object.showSlots && object.slots.size() > 0) {
			for (FmmlxSlot slot : object.slots) {
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(slot.getName() + " = " + slot.getValue()), neededWidth);

			}
		}
		if (object.showOperationValues) {
			for (FmmlxOperationValue opValue : object.getAllOperationValues()) {
				neededWidth = Math.max(2+FmmlxDiagram.calculateTextWidth(opValue.getName() + " -> " + opValue.getValue()), neededWidth);
			}
		}
		
		if (object.showConstraints) {
			for (Constraint con : object.getConstraints()) {
				neededWidth = Math.max(2+FmmlxDiagram.calculateTextWidth(con.getName()) + INST_LEVEL_WIDTH, neededWidth);
			}
		}

		if (hasParents()) {
			neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(getParentsList(diagram)), neededWidth);
		}

		//if minimum width is not reached just paint minimum
		return Math.max(neededWidth + 2 * GAP, minWidth);
	}
	
	private String getParentsList(FmmlxDiagram diagram) {
		StringBuilder parentsList = new StringBuilder("extends ");
		for (String parentName : object.getParentsPaths()) {
			try {
				FmmlxObject parent = null;
				try {parent = diagram.getObjectByPath(parentName);} catch (PathNotFoundException e) {}
				InheritanceEdge edge = diagram.getInheritanceEdge(object, parent);
				if(edge != null && !edge.isVisible()) {
					parentName = parent.name;
					parentsList.append(parentName).append(", ");
				} 
			} catch (Exception e) {
				e.printStackTrace();
				parentName = e.getMessage();
				parentsList.append(parentName).append(", ");
			}
		}
		//System.err.println(parentsList);
		if(!("extends ".equals(parentsList.toString()))) return parentsList.substring(0, parentsList.length() - 2);
		
		return "";
	}
	
}
