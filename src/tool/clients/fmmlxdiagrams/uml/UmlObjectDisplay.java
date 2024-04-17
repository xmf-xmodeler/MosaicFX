package tool.clients.fmmlxdiagrams.uml;

import tool.clients.fmmlxdiagrams.*;

import java.util.Map;
import java.util.Vector;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer.PathNotFoundException;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.graphics.NodeBaseElement;
import tool.clients.fmmlxdiagrams.graphics.NodeBox;
import tool.clients.fmmlxdiagrams.graphics.NodeElement;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.NodeLabel;
import tool.clients.fmmlxdiagrams.classbrowser.ModelBrowser;
import tool.clients.fmmlxdiagrams.graphics.NodeImage;
import tool.xmodeler.ControlCenterClient;



public class UmlObjectDisplay extends AbstractFmmlxObjectDisplay {
	
	static int GAP = 5;
	protected int minWidth = 100;

	final int INST_LEVEL_WIDTH = 7;
	final int MIN_BOX_HEIGHT = 4;
	final int EXTRA_Y_PER_LINE = 3;
	
	public UmlObjectDisplay(FmmlxDiagram diagram, FmmlxObject object) {
		super(diagram, object);
	}

	private final static NodeBaseElement.Action NO_ACTION = null;
	
	public Color getLevelFontColor(double opacity, FmmlxDiagram diagram) {	//Black should be fine for now
		return Color.BLACK; //diagram.levelColorScheme.getLevelFgColor(level, opacity);
	}
	
	public Color getLevelBackgroundColor(FmmlxDiagram diagram) {		//rewrote original function to fit for UML and be simpler
		int level = this.object.getLevel().getMaxLevel();
		Color colour = Color.RED; 	//if red something went wrong
		
		switch(level) {
		case 1: 
			colour = Color.WHITE;
			break;
		case 0:
			colour = Color.LIGHTGREY;
			break;
		}
		
        return colour;
	}

	public void layout(Map<DiagramDisplayProperty, Boolean> diagramDisplayProperties) {
		//determine text height

		double neededWidth = calculateNeededWidth(diagram, diagramDisplayProperties);
		
		double textHeight = FmmlxDiagram.calculateTextHeight();
		double lineHeight = textHeight + EXTRA_Y_PER_LINE;
		double currentY = 0;	
		int headerLines = 2;	//We do not care about parents so this is hard coded now
		
		NodeGroup group = new NodeGroup(new Affine(1, 0, object.getX(), 0, 1, object.getY()));
		object.rootNodeElement = group;	

		NodeBox header = new NodeBox(0, currentY, neededWidth, textHeight * headerLines + EXTRA_Y_PER_LINE, getLevelBackgroundColor(diagram), Color.BLACK, (x) -> 1., PropertyType.Class);
		header.setAction( ()-> {
			Vector<String> models = new Vector<>(); 
			models.add(diagram.getPackagePath());
			ModelBrowser modelBrowser = ControlCenterClient.getClient().getControlCenter().showModelBrowser("(Project)", diagram.getPackagePath(), models);
			Platform.runLater(()-> modelBrowser.setSelectedObjectAndProperty(object, null));
		});
		
		group.addNodeElement(header);
		
		String ofName = "^" + FmmlxObject.getRelativePath(object.getPath(), object.getOfPath()) + "^";
		if(ofName.equals("^FMMLx::MetaClass^")) ofName = "";	//We only want this for objects so classes should remain empty
		
		NodeLabel metaclassLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight, getLevelFontColor(.65, diagram), null, object, NO_ACTION, ofName, FontPosture.REGULAR, FontWeight.BOLD) ;
		NodeLabel nameLabel = new NodeLabel(Pos.BASELINE_CENTER, neededWidth / 2, textHeight * 2, getLevelFontColor(1., diagram), null, object, ()-> diagram.getActions().changeNameDialog(object, PropertyType.Class), object.getRelativeName(), object.isAbstract()?FontPosture.ITALIC:FontPosture.REGULAR, FontWeight.BOLD);
		
		header.addNodeElement(metaclassLabel);
		header.addNodeElement(nameLabel);
		
		currentY += headerLines * textHeight + EXTRA_Y_PER_LINE;
		
		int attSize = countAttributesToBeShown(diagramDisplayProperties);		//Attributes code copied from DefaultFmmlxObjectDisplay.java
		double attBoxHeight = Math.max(lineHeight * attSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double yAfterAttBox = currentY + attBoxHeight;
		double attY = 0;
		NodeBox attBox = new NodeBox(0, currentY, neededWidth, attBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Attribute);
		group.addNodeElement(attBox);

		for (FmmlxAttribute att : object.getOwnAttributes()) {
			attY += lineHeight;
			NodeLabel.Action changeAttNameAction = () -> diagram.getActions().changeNameDialog(object, PropertyType.Attribute, att);
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.BLACK, null, att, changeAttNameAction, att.getName() + ": " + att.getTypeShort() /*+"["+ att.getMultiplicity() + "]"*/);
			attBox.addNodeElement(attLabel);
		}
		for (FmmlxAttribute att : object.getOtherAttributes()) {
			if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDATTRIBUTES)) {
			attY += lineHeight;
			String ownerName = att.getOwnerPath();
			try{ownerName = diagram.getObjectByPath(att.getOwnerPath()).getName();} catch (Exception e) {}
			NodeLabel attLabel = new NodeLabel(Pos.BASELINE_LEFT, 14, attY, Color.GRAY, null, att, NO_ACTION, att.getName() + ": " + att.getTypeShort() /*+"["+ att.getMultiplicity() + "]"*/);
			attBox.addNodeElement(attLabel);
			}
		}
		currentY = yAfterAttBox;

		double yAfterOpsBox = currentY;

		int opsSize = countOperationsToBeShown(diagramDisplayProperties);
		double opsBoxHeight = Math.max(lineHeight * opsSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double opsY = 0;
		NodeBox opsBox = new NodeBox(0, currentY, neededWidth, opsBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Operation);
		if (!object.isHidden() && diagramDisplayProperties.get(DiagramDisplayProperty.OPERATIONS) && opsSize > 0) {
			yAfterOpsBox = currentY + opsBoxHeight;
			group.addNodeElement(opsBox);
			for (FmmlxOperation o : object.getOwnOperations()) {
				if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					opsY += lineHeight;
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
				if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
						opsY += lineHeight;
						try{
							NodeImage inhIcon = new NodeImage(14, opsY, (diagram.getObjectByPath(o.getOwner()).getLevel() == object.getLevel()) ? "resources/gif/Inheritance.gif" : "resources/gif/Dependency.gif", o, NO_ACTION);
							opsBox.addNodeElement(inhIcon);
						} catch (Exception e) {
							NodeImage inhIcon = new NodeImage(14, opsY, "resources/gif/user/Query2.gif", o, NO_ACTION);
							opsBox.addNodeElement(inhIcon);	
						}
						int labelX = 30;
						if(o.isDelegateToClassAllowed()) {
							NodeImage delIcon = new NodeImage(30, opsY, "resources/gif/XCore/delegationDown.png", o, NO_ACTION);
							opsBox.addNodeElement(delIcon);
							labelX +=16;
						}	
						try{
							NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).getName() + ")");
							opsBox.addNodeElement(oLabel);
						} catch (Exception e) {
							NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + o.getOwner() + ")");
							opsBox.addNodeElement(oLabel);
						}
					}
				}
			}
			for (FmmlxOperation o : object.getDelegatedOperations()) {
				if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
					opsY += lineHeight;
					String iconS = "resources/gif/Inheritance.gif";
					try{
						if(diagram.getObjectByPath(object.getOfPath()).getAllOperations().contains(o)) iconS = "resources/gif/Dependency.gif";
					} catch (PathNotFoundException pnfe) {}
					if(object.getDelegatesTo(false) != null && object.getDelegatesTo(false).getAllOperations().contains(o)) iconS = "resources/gif/XCore/delegation.png";
						
					NodeImage delIcon = new NodeImage(14, opsY, iconS, o, NO_ACTION);
					opsBox.addNodeElement(delIcon);
					int labelX = 30;
					if(o.isDelegateToClassAllowed()) {
						NodeImage del2Icon = new NodeImage(30, opsY, "resources/gif/XCore/delegationDown.png", o, NO_ACTION);
						opsBox.addNodeElement(del2Icon);
						labelX +=16;
					}	
					NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, labelX, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).getName() + ")");
					opsBox.addNodeElement(oLabel);
					}
				}
			}			
			for (FmmlxOperation o : object.getDelegateToClassOperations()) {
				if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
					if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
						opsY += lineHeight;
						NodeLabel oLabel = new NodeLabel(Pos.BASELINE_LEFT, 30, opsY, Color.GRAY, null, o, NO_ACTION, o.getFullString(diagram) + " (from " + diagram.getObjectByPath(o.getOwner()).getName() + ")");
						opsBox.addNodeElement(oLabel);
						NodeImage delIcon = new NodeImage(14, opsY, "resources/gif/XCore/delegationUp.png", o, NO_ACTION);
						opsBox.addNodeElement(delIcon);
					}
				}
			}
		}
		currentY = yAfterOpsBox;
		
		double yAfterSlotBox = currentY;
		int slotSize = object.getSlots().size();
		double slotBoxHeight = Math.max(lineHeight * slotSize + EXTRA_Y_PER_LINE, MIN_BOX_HEIGHT);
		double slotsY = 0;
		NodeBox slotsBox = new NodeBox(0, currentY, neededWidth, slotBoxHeight, Color.WHITE, Color.BLACK, (x) -> 1., PropertyType.Slot);
		if (diagramDisplayProperties.get(DiagramDisplayProperty.SLOTS) && slotSize > 0) {
			yAfterSlotBox = currentY + slotBoxHeight;
			group.addNodeElement(slotsBox);
			for (FmmlxSlot s : object.getSlots()) {
				slotsY += lineHeight;
				NodeLabel.Action changeSlotValueAction = () -> diagram.getActions().changeSlotValue(object, s);
				NodeLabel slotNameLabel = new NodeLabel(Pos.BASELINE_LEFT, 3, slotsY, Color.BLACK, null, s, changeSlotValueAction, s.getName() + " = ");
				slotsBox.addNodeElement(slotNameLabel);
				NodeLabel slotValueLabel = new NodeLabel(Pos.BASELINE_LEFT, 3 + slotNameLabel.getWidth(), slotsY, new Color(0.0,0.4,0.2,1.0), new Color(0.85,0.9,0.85,1.0), s, changeSlotValueAction, "" + s.getValue());
				slotsBox.addNodeElement(slotValueLabel);
			}
		}
		currentY = yAfterSlotBox;

	}
	

	
	private double calculateNeededWidth(FmmlxDiagram diagram, Map<DiagramDisplayProperty, Boolean> diagramDisplayProperties) {
		double neededWidth = FmmlxDiagram.calculateTextWidth(object.getRelativeName()); 

		try {
			String ofName = FmmlxObject.getRelativePath(diagram.getPackagePath(), object.getOfPath());
			neededWidth = Math.max(neededWidth, FmmlxDiagram.calculateTextWidth(object.getLevel() + "^" + ofName + "^"));
		} catch (PathNotFoundException e) {
			neededWidth = Math.max(neededWidth, FmmlxDiagram.calculateTextWidth(object.getLevel() + "^???^"));
		}
		
		//determine maximal width of attributes
		for (FmmlxAttribute att : object.getOwnAttributes()) {
			neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(att.getName() + ": " + att.getTypeShort() +"["+ att.getMultiplicity() + "]") + INST_LEVEL_WIDTH, neededWidth);
		}
		for (FmmlxAttribute att : object.getOtherAttributes()) {
			if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDATTRIBUTES)) {
				String ownerName = att.getOwnerPath();
				try{ownerName = diagram.getObjectByPath(att.getOwnerPath()).getName();} catch (Exception e) {}
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(att.getName() + ": " + att.getTypeShort() +"["+ att.getMultiplicity() + "]" + " (from " + ownerName + ")") + INST_LEVEL_WIDTH, neededWidth);
			}
		}
//		//determine maximal width of operations
		if (diagramDisplayProperties.get(DiagramDisplayProperty.OPERATIONS)) {
			for (FmmlxOperation o : object.getOwnOperations()) {
				if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS)  ||  !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
				String text = o.getFullString(diagram);
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(text) + INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
				}
			}	
			for (FmmlxOperation o : object.getOtherOperations()) {
				if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
						String owner = o.getOwner();
						try{owner = diagram.getObjectByPath(o.getOwner()).getName();} catch (Exception e) {}
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + owner + ")") + 4 * INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
					}
				}
			}	
			for (FmmlxOperation o : object.getDelegatedOperations()) {
				if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
						String owner = o.getOwner();
						try{owner = diagram.getObjectByPath(o.getOwner()).getName();} catch (Exception e) {}
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + owner + ")") + 4 * INST_LEVEL_WIDTH + (o.isDelegateToClassAllowed()?16:0), neededWidth);
					}
				}
			}
			for (FmmlxOperation o : object.getDelegateToClassOperations()) {
				if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
					if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
						String owner = o.getOwner();
						try{owner = diagram.getObjectByPath(o.getOwner()).getName();} catch (Exception e) {}
						neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(o.getFullString(diagram) + " (from " + owner + ")") + 4 * INST_LEVEL_WIDTH, neededWidth);
					}
				}
			}
		}
		//determine maximal width of slots
		if (diagramDisplayProperties.get(DiagramDisplayProperty.SLOTS) && object.getSlots().size() > 0) {
			for (FmmlxSlot slot : object.getSlots()) {
				neededWidth = Math.max(FmmlxDiagram.calculateTextWidth(slot.getName() + " = " + slot.getValue()), neededWidth);

			}
		}
		if (diagramDisplayProperties.get(DiagramDisplayProperty.OPERATIONVALUES)) {
			for (FmmlxOperationValue opValue : object.getAllOperationValues()) {
				if (opValue.getValue().length() > 40) {
					neededWidth = Math.max(2+FmmlxDiagram.calculateTextWidth(opValue.getName() + " -> " + " Double click for value"), neededWidth);
				} else neededWidth = Math.max(2+FmmlxDiagram.calculateTextWidth(opValue.getName() + " -> " + opValue.getValue()), neededWidth);
			}
		}
		
		if (diagramDisplayProperties.get(DiagramDisplayProperty.CONSTRAINTS)) {
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
	
	private int countAttributesToBeShown(Map<DiagramDisplayProperty, Boolean> diagramDisplayProperties) {
		return (diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDATTRIBUTES)?1:0)*object.getOtherAttributes().size() + object.getOwnAttributes().size();
	}
	
	private boolean hasParents() {
		return object.getParentsPaths().size() != 0;
	}

	private int countOperationsToBeShown(Map<DiagramDisplayProperty, Boolean> diagramDisplayProperties) {
		int counter=0;
		for (FmmlxOperation o : object.getOwnOperations()) {
			if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) ||  !(o.getName().startsWith("set") || o.getName().startsWith("get"))) {
			counter++;	
			}
		}

		for (FmmlxOperation o : object.getOtherOperations()) {
			if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
				counter++;
				}
			}
		}		
		for (FmmlxOperation o : object.getDelegatedOperations()) {
			if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
				counter++;
				}
			}
		}		
		for (FmmlxOperation o : object.getDelegateToClassOperations()) {
			if(diagramDisplayProperties.get(DiagramDisplayProperty.GETTERSANDSETTERS) || !(o.getName().startsWith("set") || o.getName().startsWith("get"))){
				if(diagramDisplayProperties.get(DiagramDisplayProperty.DERIVEDOPERATIONS)) {
				counter++;
				}
			}
		}
		return counter;
	}
	
	private String getParentsList(FmmlxDiagram diagram) {
		StringBuilder parentsList = new StringBuilder("extends ");
		for (String parentName : object.getParentsPaths()) {
			try {
				FmmlxObject parent = null;
				try {parent = diagram.getObjectByPath(parentName);} catch (PathNotFoundException e) {
					if(!("Root::XCore::Object".equals(parentName) 
							|| "Root::FMMLx::MetaClass".equals(parentName)
							|| "Root::FMMLx::FmmlxObject".equals(parentName)))
					parentsList.append(parentName).append(", ");
				}
				InheritanceEdge edge = diagram.getInheritanceEdge(object, parent);
				if(edge != null && !edge.isVisible()) {
					parentName = parent.getName();
					parentsList.append(parentName).append(", ");
				} 
			} catch (Exception e) {
				e.printStackTrace();
				parentName = e.getMessage();
				parentsList.append(parentName).append(", ");
			}
		}
		if(!("extends ".equals(parentsList.toString()))) return parentsList.substring(0, parentsList.length() - 2);
		
		return "";
	}

}
