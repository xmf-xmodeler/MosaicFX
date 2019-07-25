package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import tool.clients.fmmlxdiagrams.dialogs.*;
import tool.clients.fmmlxdiagrams.dialogs.results.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

public class DiagramActions {

	private final double zoomLevel = Math.sqrt(2);

	private FmmlxDiagram diagram;

	private boolean showOperations;
	private boolean showOperationValues;
	private boolean showSlots;

	DiagramActions(FmmlxDiagram diagram) {
		this.diagram = diagram;
		showOperations = true;
		showOperationValues = true;
		showSlots = true;
	}

	public void redrawDiagram() {
		diagram.redraw();
	}

	public void addMetaClassDialog() {
		CountDownLatch l = new CountDownLatch(2);

		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog(diagram);
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
							diagram.addMetaClass(mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentIds(), mcdResult.isAbstract(), x, y);

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
		addInstanceDialog(null);
	}

	public void addInstanceDialog(FmmlxObject object) {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddInstanceDialog dialog = new AddInstanceDialog(diagram, object);
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
									aidResult.getParentId(), false, x, y);

							canvas.setCursor(Cursor.DEFAULT);
							canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);

							diagram.updateDiagram();
						}
					}
				};
				canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);

			}

			l.countDown();
		});
	}

	public void addAttributeDialog() {
		addAttributeDialog(null);
	}

	public void addAttributeDialog(FmmlxObject object) {

		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddAttributeDialog dlg;
			if (object != null) {
				dlg = new AddAttributeDialog(diagram, object);
			} else {
				dlg = new AddAttributeDialog(diagram);
			}

			dlg.setTitle("Add Attribute");
			Optional<AddAttributeDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				AddAttributeDialogResult aad = result.get();
				diagram.addAttribute(aad.getClassID(), aad.getName(), aad.getLevel(), aad.getType(), aad.getMultiplicity());
			}
			diagram.updateDiagram();
			l.countDown();


		});
	}


	public void removeDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			RemoveDialog dlg = new RemoveDialog(diagram, object, type);
			Optional<RemoveDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final RemoveDialogResult result = opt.get();
				System.err.println(result.toString());
				switch (result.getType()) {
					case Class:
						diagram.removeClass(result);
						break;
					case Operation:
						diagram.removeOperation(result);
						break;
					case Attribute:
						diagram.removeAttribute(result);
						break;
					case Association:
						diagram.removeAssociation(result);
					default:
						System.err.println("ChangeNameDialogResult: No matching content type!");
				}
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

	public void changeNameDialog(FmmlxObject object, PropertyType type, FmmlxProperty selectedProperty) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeNameDialog dlg = new ChangeNameDialog(diagram, object, type, selectedProperty);

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
					case Association:
						diagram.changeAssociationName(result);
					default:
						System.err.println("ChangeNameDialogResult: No matching content type!");
				}
			}
			diagram.updateDiagram();
			latch.countDown();
		});
	}

	public void changeNameDialog(FmmlxObject object, PropertyType type) {
		changeNameDialog(object, type, null);
	}

	public void changeLevelDialog(FmmlxObject object, PropertyType type) {
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
					default:
						System.err.println("ChangeLevelDialogResult: No matching content type!");
				}
				diagram.updateDiagram();
			}

			latch.countDown();
		});

	}

	public void changeOfDialog(FmmlxObject object) {

		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeOfDialog dlg = new ChangeOfDialog(diagram, object);
			Optional<ChangeOfDialogResult> cod = dlg.showAndWait();

			if (cod.isPresent()) {
				final ChangeOfDialogResult result = cod.get();
				diagram.changeOf(result);
				diagram.updateDiagram();
			}
			l.countDown();
		});


	}

	public void changeOwnerDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeOwnerDialog dlg = new ChangeOwnerDialog(diagram, object, type);
			Optional<ChangeOwnerDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeOwnerDialogResult result = opt.get();
				System.err.println(result);
				switch (result.getType()) {
					case Attribute:
						diagram.changeAttributeOwner(result);
						break;
					case Operation:
						diagram.changeOperationOwner(result);
						break;
					default:
						System.err.println("ChangeOwnerDialogResult: No matching content type!");
						break;
				}
				diagram.updateDiagram();
			}

			l.countDown();
		});
	}

	public void changeParentsDialog(FmmlxObject object) {

		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeParentDialog dlg = new ChangeParentDialog(diagram, object);
			Optional<ChangeParentDialogResult> cpd = dlg.showAndWait();

			if (cpd.isPresent()) {
				ChangeParentDialogResult result = cpd.get();
				diagram.changeParent(result);
				diagram.updateDiagram();
			}

			l.countDown();
		});

	}

	public void changeSlotValue(FmmlxObject hitObject, FmmlxSlot hitProperty) {

		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeSlotValueDialog dlg = new ChangeSlotValueDialog(hitObject, hitProperty);
			Optional<ChangeSlotValueDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeSlotValueDialogResult slotValueDialogResult = result.get();
				diagram.changeSlotValue(slotValueDialogResult);
				diagram.changeSlotValue(slotValueDialogResult);
				diagram.updateDiagram();
			}

			l.countDown();
		});
	}

	public void updateDiagram() {
		diagram.updateDiagram();
	}

	public void toggleIsAbstract(FmmlxObject object) {
		object.toogleIsAbstract();
		diagram.redraw();
	}

	public void toggleShowOperations() {
		for (FmmlxObject o : diagram.getObjects()) {
			if (o.getShowOperations() == showOperations) {
				o.toogleShowOperations();
			}
		}
		showOperations = !showOperations;
		diagram.redraw();
	}

	public void toggleShowOperationValues() {
		for (FmmlxObject o : diagram.getObjects()) {
			if (o.getShowOperationValues() == showOperationValues) {
				o.toogleShowOperationValues();
			}
		}
		showOperationValues = !showOperationValues;
		diagram.redraw();
	}

	public void toggleShowSlots() {
		for (FmmlxObject o : diagram.getObjects()) {
			if (o.getShowSlots() == showSlots) {
				o.toogleShowSlots();
			}
		}
		showSlots = !showSlots;
		diagram.redraw();
	}

	public void addDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddDialog dlg = new AddDialog(diagram, object, type);
			Optional<AddDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddDialogResult result = opt.get();
				System.err.println(result);
				switch (result.getType()) {
					case Class:
						//TODO diagram.addMetaClass(result);
						break;
					case Attribute:
						//TODO diagram.addAttribute(result);
						break;
					case Operation:
						diagram.addOperation(result);
						break;
					case Association:
						break;
					default:
						System.err.println("AddDialogResult: No matching content type!");
				}
				diagram.updateDiagram();
			}
			latch.countDown();
		});
	}

	public void changeTypeDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeTypeDialog dlg = new ChangeTypeDialog(object, type);
			Optional<ChangeTypeDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeTypeDialogResult result = opt.get();
				System.err.println("Change type result " + result);
				switch (result.getType()) {
					case Attribute:
						diagram.changeTypeAttribute(result);
						break;
					case Operation:
						diagram.changeTypeOperation(result);
						break;
					case Association:
						diagram.changeTypeAssociation(result);
						break;
					default:
						System.err.println("AddDialogResult: No matching content type!");
				}
				diagram.updateDiagram();
			}

			latch.countDown();
		});
	}

	//TODO: needs to be fixed -> dialog for different types > only type of comboBox changes according to type
	//		result needs to be extended to save the changed property

	public void changeMultiplicityDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeMultiplicityDialog dlg = new ChangeMultiplicityDialog(object);
			Optional<MultiplicityDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final MultiplicityDialogResult result = opt.get();
				System.err.println(result);
				diagram.changeMulitiplicityAttribute(result);
				diagram.updateDiagram();
			}
			latch.countDown();
		});
	}

	public void changeTargetDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeTargetDialog dlg = new ChangeTargetDialog(diagram, object, type);
			Optional<ChangeTargetDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeTargetDialogResult result = opt.get();
				System.err.println(result);
				diagram.changeTargetAssociation(result);
				diagram.updateDiagram();
			}

			latch.countDown();
		});
	}

	public void changeBodyDialog(FmmlxObject object) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeBodyDialog dlg = new ChangeBodyDialog(diagram, object);
			Optional<ChangeBodyDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeBodyDialogResult result = opt.get();
				System.err.println(result);
				diagram.changeBody(result);
				diagram.updateDiagram();
			}
			latch.countDown();
		});
	}

	public void setAssociationMode(FmmlxObject source) {
		diagram.setSelectedObject(source);
		diagram.setAssociationMouseMode();
		diagram.storeLastClick(source.getCenterX(), source.getCenterY());
	}

	public void addAssociationDialog(FmmlxObject source, FmmlxObject target) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddAssociationDialog dlg = new AddAssociationDialog(diagram, source, target);
			Optional<AddAssociationDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddAssociationDialogResult result = opt.get();
				diagram.addAssociation(result);
				diagram.updateDiagram();
			}
			latch.countDown();
		});
	}
}