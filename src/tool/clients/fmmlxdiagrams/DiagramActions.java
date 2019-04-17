package tool.clients.fmmlxdiagrams;

import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import tool.clients.fmmlxdiagrams.dialogs.CreateMetaClassDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.MetaClassDialogResult;

public class DiagramActions {
	
	private final double zoomLevel = Math.sqrt(2);
	
	FmmlxDiagram diagram;
	
	public DiagramActions(FmmlxDiagram diagram) {
		this.diagram = diagram;
	}
	
	public void addMetaClassDialog() {
		CountDownLatch l = new CountDownLatch(2);

		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog();
			dlg.setTitle("Add metaclass");
			Optional<MetaClassDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				final MetaClassDialogResult mcdResult = result.get();

				Canvas canvas = diagram.getCanvas();
				canvas.setCursor(Cursor.CROSSHAIR);

				EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
					public void handle(MouseEvent e) {

						int x = (int) e.getX();
						int y = (int) e.getY();

						if (x > 0 && y > 0) {
							diagram.addMetaClass(mcdResult.getName(), mcdResult.getLevel(),
									new Vector<Integer>(mcdResult.getParent()), false, x, y);

							canvas.setCursor(Cursor.DEFAULT);
							canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);

							diagram.updateDiagram();
							l.countDown();
						}
					};
				};
				canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
			}
		});
	}
	
	public void zoomIn() {
		diagram.setZoom(diagram.getZoom() * zoomLevel);
		diagram.redraw();
		}
	
	public void zoomOut() {
		diagram.setZoom(diagram.getZoom() / zoomLevel);
		diagram.redraw();
	}
	
	public void zoomOne() {
		diagram.setZoom(1.);
		diagram.redraw();
	}


}
