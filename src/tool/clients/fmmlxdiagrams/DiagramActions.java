package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;

import tool.clients.fmmlxdiagrams.dialogs.*;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAttributeDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.AddInstanceDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeLevelDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeNameDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeOfDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.MetaClassDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.RemoveDialogResult;

import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

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


	public void removeDialog(FmmlxObject object,String type) {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			RemoveDialog dlg = new RemoveDialog(diagram, object,type);
			Optional<RemoveDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				RemoveDialogResult test = opt.get();
				System.out.println("!!!!!!!!!!!!! ");
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


	public void changeNameDialog(FmmlxObject object, String type) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeNameDialog dlg = new ChangeNameDialog(diagram, object, type);
			Optional<ChangeNameDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeNameDialogResult result = opt.get();
				System.err.println(result.toString());
				switch (result.getType()) {
					case "class":
						diagram.changeClassName(result);
						break;
					case "operation":
						diagram.changeOperationName(result);
						break;
					case "attribute":
						diagram.changeAttributeName(result);
						break;
				}
			}

			diagram.updateDiagram();
			latch.countDown();
		});
	}

	public void changeLevelDialog(FmmlxObject object, String type) {
		// TODO Auto-generated method stub
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeLevelDialog dlg = new ChangeLevelDialog(diagram, object, type);
			Optional<ChangeLevelDialogResult> opt= dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeLevelDialogResult result = opt.get();
				System.err.println(result.toString());
				switch (result.getType()) {
					case "class":
						diagram.changeClassLevel(result);
						break;
					case "attribute":
						diagram.changeAttributeLevel(result);
						break;
					case "operation":
						diagram.changeOperationLevel(result);
						break;
					case "association":
						diagram.changeAssociationLevel(result);
						break;
				}
			}

			diagram.updateDiagram();
			latch.countDown();
		});
		
	}

	public void changeOfDialog(FmmlxObject object) {
		
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeOfDialog dlg = new ChangeOfDialog(diagram, object);
			dlg.setTitle("Change Of");
			Optional<ChangeOfDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeOfDialogResult cod = result.get();
			}

			diagram.updateDiagram();
			l.countDown();
		});
		// TODO Auto-generated method stub
		
	}

}