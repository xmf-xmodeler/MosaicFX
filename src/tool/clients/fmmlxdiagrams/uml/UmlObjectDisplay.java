package tool.clients.fmmlxdiagrams.uml;

import java.util.Map;
import java.util.Vector;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.AbstractFmmlxObjectDisplay;
import tool.clients.fmmlxdiagrams.DiagramDisplayProperty;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.graphics.NodeBaseElement;
import tool.clients.fmmlxdiagrams.graphics.NodeBox;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.NodeLabel;

public class UmlObjectDisplay extends AbstractFmmlxObjectDisplay {
	
	public UmlObjectDisplay(FmmlxDiagram diagram, FmmlxObject object) {
		super(diagram, object);
	}

	private final static NodeBaseElement.Action NO_ACTION = null;

	public void layout(Map<DiagramDisplayProperty, Boolean> diagramDisplayProperties) {
		NodeGroup group = new NodeGroup(new Affine(1, 0, object.getX(), 0, 1, object.getY()));

		NodeBox header = new NodeBox(0, 0, 100, 50, Color.LIGHTGRAY, Color.BLACK, (x) -> 1., PropertyType.Class);
		NodeLabel nameLabel = new NodeLabel(Pos.BASELINE_CENTER, 10, 40, Color.DARKBLUE, null, object, ()-> diagram.getActions().changeNameDialog(object, PropertyType.Class), object.getName(), object.isAbstract()?FontPosture.ITALIC:FontPosture.REGULAR, FontWeight.BOLD);

		header.addNodeElement(nameLabel);
		group.addNodeElement(header);
		
		
		object.rootNodeElement = group;
	}

}
