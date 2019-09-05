package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;

public interface Selectable {

	ContextMenu getContextMenu(DiagramActions actions);

	void moveTo(double d, double e, FmmlxDiagram diagram);

	void highlightElementAt(Point2D p);

}
