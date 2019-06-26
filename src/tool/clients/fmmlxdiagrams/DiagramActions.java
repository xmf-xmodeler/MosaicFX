package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import tool.clients.fmmlxdiagrams.dialogs.*;
import tool.clients.fmmlxdiagrams.dialogs.results.*;

import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

public class DiagramActions {

	private final double zoomLevel = Math.sqrt(2);

	private FmmlxDiagram diagram;

	private boolean showOperations;

	DiagramActions(FmmlxDiagram diagram) {
		this.diagram = diagram;
		showOperations = false;
	}

	public void redrawDiagram() {
		diagram.redraw();
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
							System.err.println("MCD: " + mcdResult.isAbstract());
							diagram.addMetaClass(mcdResult.getName(), mcdResult.getLevel(),
									new Vector<>(mcdResult.getParent()), mcdResult.isAbstract(), x, y);

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
				System.err.println("AddAttributeDialog: " + aad.getName() + " " + aad.getLevel());
				diagram.addAttribute(aad.getClassID(), aad.getName(), aad.getLevel(), aad.getType(), aad.getMultiplicity());
			} else {
				System.err.println("AddAttributeDialog: no result");
			}

			diagram.updateDiagram();
			l.countDown();
		});
	}


	public void removeDialog(FmmlxObject object, DialogType type) {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			RemoveDialog dlg = new RemoveDialog(diagram, object, type);
			Optional<RemoveDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				RemoveDialogResult test = opt.get();
				// TODO 
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


	public void changeNameDialog(FmmlxObject object, DialogType type) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeNameDialog dlg = new ChangeNameDialog(diagram, object, type);
			Optional<ChangeNameDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeNameDialogResult result = opt.get();
				System.err.println(result.toString());
				switch (result.getType()) {
					case Class:
						diagram.changeClassName(result);
						break;
					case Operation:
						diagram.changeOperationName(result);
						break;
					case Attribute:
						diagram.changeAttributeName(result);
						break;
				}
			}

			diagram.updateDiagram();
			latch.countDown();
		});
	}

	public void changeLevelDialog(FmmlxObject object, DialogType type) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeLevelDialog dlg = new ChangeLevelDialog(diagram, object, type);
			Optional<ChangeLevelDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeLevelDialogResult result = opt.get();
				System.err.println(result.toString());
				switch (result.getType()) {
					case Class:
						diagram.changeClassLevel(result);
						break;
					case Attribute:
						diagram.changeAttributeLevel(result);
						break;
					case Operation:
						diagram.changeOperationLevel(result);
						break;
					case Association:
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
			ChangeParentDialog dlg = new ChangeParentDialog(diagram, object);
			dlg.setTitle("Change Of");
			Optional<ChangeParentDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeParentDialogResult cod = result.get();
				// TODO 
			}

			diagram.updateDiagram();
			l.countDown();
		});


	}

	public void changeParentsDialog(FmmlxObject object) {

		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeParentDialog dlg = new ChangeParentDialog(diagram, object);
			Optional<ChangeParentDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeParentDialogResult cod = result.get();
				// TODO 
			}

			diagram.updateDiagram();
			l.countDown();
		});

	}

	public void toogleIsAbstract(FmmlxObject object) {
		object.toogleIsAbstract();
		diagram.redraw();
	}

	public void toogleShowOperations() {
		for (FmmlxObject o : diagram.getObjects()) {
			if (o.getShowOperations() == showOperations) {
				o.toogleShowOperations();
			}
		}
		showOperations = !showOperations;
		diagram.redraw();
	}

	public void changeSlotValue(String string) {
		diagram.changeSlotValue(string);
		
	}
}
