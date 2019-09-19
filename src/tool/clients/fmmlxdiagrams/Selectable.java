package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;

public interface Selectable {

	ContextMenu getContextMenu(DiagramActions actions);

	void moveTo(double d, double e, FmmlxDiagram diagram);
	
	boolean isHit(double x, double y);

	void highlightElementAt(Point2D p);

	void setOffsetAndStoreLastValidPosition(Point2D p);
	
	double getMouseMoveOffsetX();
	double getMouseMoveOffsetY();
	

}
