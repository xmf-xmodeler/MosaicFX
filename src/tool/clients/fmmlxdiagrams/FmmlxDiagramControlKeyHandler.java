package tool.clients.fmmlxdiagrams;

import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import tool.helper.persistence.XMLCreator;

/**
 * This class is used to handle all KeyInputs form the FmmlxDiagram that are
 * combined with the control-key. Please do not forget to add the shortcuts you
 * will add here to the ShortcutDialog-class.
 */
public class FmmlxDiagramControlKeyHandler {

	private final FmmlxDiagram diagram;

	public FmmlxDiagramControlKeyHandler(FmmlxDiagram fmmlxDiagram) {
		this.diagram = fmmlxDiagram;
	}

	public void handle(KeyCode code) {

		switch (code) {
		case M:
			handleM();
			break;

		case R:
			handleR();
			break;

		case F:
			handleF();
			break;

		case A:
			handleA();
			break;

		case S:
			handleS();
			break;

		default:
			break;
		}
	}

	private void handleS() {
		new XMLCreator().createAndSaveXMLRepresentation(diagram.getPackagePath(),diagram);
	}

	private void handleA() {
		diagram.selectAll();
	}

	private void handleF() {
		diagram.actions.centerViewOnObject();
	}

	private void handleR() {
		diagram.getActiveDiagramViewPane().canvasTransform.prependRotation(10,
				new Point2D(diagram.getActiveDiagramViewPane().canvas.getWidth() / 2,
						diagram.getActiveDiagramViewPane().canvas.getHeight() / 2));
		diagram.redraw();
	}

	private void handleM() {
		diagram.getActiveDiagramViewPane().canvasTransform.prependScale(-1, 1,
				new Point2D(diagram.getActiveDiagramViewPane().canvas.getWidth() / 2,
						diagram.getActiveDiagramViewPane().canvas.getHeight() / 2));
		diagram.redraw();
	}
}