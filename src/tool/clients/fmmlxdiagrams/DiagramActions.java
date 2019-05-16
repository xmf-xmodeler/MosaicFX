package tool.clients.fmmlxdiagrams;

import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import tool.clients.fmmlxdiagrams.dialogs.AddAttributeDialog;
import tool.clients.fmmlxdiagrams.dialogs.AddInstanceDialog;
import tool.clients.fmmlxdiagrams.dialogs.ChangeAttributeNameDialog;
import tool.clients.fmmlxdiagrams.dialogs.CreateMetaClassDialog;
import tool.clients.fmmlxdiagrams.dialogs.EditAttributDialog;
import tool.clients.fmmlxdiagrams.dialogs.RemoveAttributDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAttributeDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.AddInstanceDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeAttributeNameDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.MetaClassDialogResult;

public class DiagramActions {

	private final double zoomLevel = Math.sqrt(2);

	private FmmlxDiagram diagram;

	DiagramActions(FmmlxDiagram diagram) {
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
					}
				};
				canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
			}
		});
	}

	public void addInstanceDialog() {
		addInstanceDialog(0);
	}

	public void addInstanceDialog(int ofId) {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddInstanceDialog dialog = new AddInstanceDialog(diagram, ofId);
			dialog.setTitle("Add instance");
			Optional<AddInstanceDialogResult> result = dialog.showAndWait();

			if (result.isPresent()) {
				final AddInstanceDialogResult aidResult = result.get();

				Canvas canvas = diagram.getCanvas();
				canvas.setCursor(Cursor.CROSSHAIR);

				EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
					public void handle(MouseEvent e) {

						int x = (int) e.getX();
						int y = (int) e.getY();

						if (x > 0 && y > 0) {
							diagram.addNewInstance(aidResult.getOf(), aidResult.getName(), aidResult.getLevel(),
									new Vector<String>(aidResult.getParents()), false, x, y);

							canvas.setCursor(Cursor.DEFAULT);
							canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);

							diagram.updateDiagram();
							l.countDown();
						}
					}
				};
				canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);

			}

			l.countDown();
		});
	}
	
	public void addAttributeDialog() {

		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddAttributeDialog dlg = new AddAttributeDialog(diagram);
			dlg.setTitle("Add Attribute");
			Optional<AddAttributeDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				AddAttributeDialogResult aad = result.get();
				System.out.println("!!!!!!!!!!!!! " + aad.getName() + " " + aad.getLevel());
				diagram.addAttribute(aad.getClassID(),aad.getName(), aad.getLevel(), aad.getType(), aad.getMultiplicity());
			}

			diagram.updateDiagram();
			l.countDown();
		});
	}

	public void editAttributeDialog() {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			EditAttributDialog dlg = new EditAttributDialog(diagram);
			Optional<MetaClassDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				MetaClassDialogResult test = opt.get();
				System.out.println("!!!!!!!!!!!!! " + test.getName() + " " + test.getLevel());
			}

			diagram.updateDiagram();
			l.countDown();
		});
	}

	public void changeAttributeNameDialog() {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeAttributeNameDialog dlg = new ChangeAttributeNameDialog(diagram, 0); // TODO change 0 into id of selected class 
			Optional<ChangeAttributeNameDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeAttributeNameDialogResult cad = result.get();
				diagram.changeAttributeName(cad.getClassID(), cad.getOldName(), cad.getNewName());
			}

			diagram.updateDiagram();
			l.countDown();
		});
	}
	
	
	public void removeAttributDialog() {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			RemoveAttributDialog dlg = new RemoveAttributDialog();
			Optional<MetaClassDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				MetaClassDialogResult test = opt.get();
				System.out.println("!!!!!!!!!!!!! " + test.getName() + " " + test.getLevel());
			}

			diagram.updateDiagram();
			l.countDown();
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
