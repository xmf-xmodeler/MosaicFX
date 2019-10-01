package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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
							diagram.getComm().addMetaClass(mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentIds(), mcdResult.isAbstract(), x, y);

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
							diagram.getComm().addNewInstance(aidResult.getOf(), aidResult.getName(), aidResult.getLevel(),
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
				diagram.getComm().addAttribute(aad.getClassID(), aad.getName(), aad.getLevel(), aad.getType(), aad.getMultiplicity());
			}
			diagram.updateDiagram();
			l.countDown();
		});
	}


	public void removeDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch l = new CountDownLatch(1);

		FmmlxProperty selectedFmmlxProperty = diagram.getSelectedProperty();

		Platform.runLater(() -> {
			RemoveDialog dlg = new RemoveDialog(diagram, object, type);
			if (belongsPropertyToObject(object, selectedFmmlxProperty, type)) {
				dlg.setSelected(selectedFmmlxProperty);
			}
			Optional<RemoveDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final RemoveDialogResult result = opt.get();
				System.err.println(result.toString());
				switch (result.getType()) {
					case Class:
						diagram.getComm().removeClass(result.getObject().getId(), 0);
						break;
					case Operation:
						diagram.getComm().removeOperation(result.getObject().getId(), result.getOperation().getName(), 0);
						break;
					case Attribute:
						diagram.getComm().removeAttribute(result.getObject().getId(), result.getAttribute().getName(), 0);
						break;
					case Association:
						diagram.getComm().removeAssociation(result.getAssociation().getId(), 0);
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
				switch (result.getType()) {
					case Class:
						diagram.getComm().changeClassName(result.getObjectId(), result.getNewName());
						break;
					case Operation:
						diagram.getComm().changeOperationName(result.getObjectId(), result.getOldName(), result.getNewName());
						break;
					case Attribute:
						diagram.getComm().changeAttributeName(result.getObjectId(), result.getOldName(), result.getNewName());
						break;
//					case Association:
//						diagram.getComm().changeAssociationName(result.getObjectId(), result.getOldName(), result.getNewName());
					default:
						System.err.println("ChangeNameDialogResult: No matching content type!");
				}
			}
			diagram.updateDiagram();
			latch.countDown();
		});
	}

	public void changeNameDialog(FmmlxObject object, PropertyType type) {
		FmmlxProperty selectedProperty = diagram.getSelectedProperty();
		if (belongsPropertyToObject(object, selectedProperty, type)) {
			changeNameDialog(object, type, selectedProperty);
		} else {
			changeNameDialog(object, type, null);
		}
	}

	public void changeLevelDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch latch = new CountDownLatch(1);

		FmmlxProperty selectedProperty = diagram.getSelectedProperty();

		Platform.runLater(() -> {
			ChangeLevelDialog dlg = new ChangeLevelDialog(diagram, object, type);
			if (belongsPropertyToObject(object, selectedProperty, type)) {
				dlg.setSelected(selectedProperty);
			}
			Optional<ChangeLevelDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeLevelDialogResult result = opt.get();
				switch (result.getType()) {
					case Class:
						diagram.getComm().changeClassLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());
						break;
					case Attribute:
						diagram.getComm().changeAttributeLevel(result.getObjectId(), result.getName(), result.getOldLevel(), result.getNewLevel());
						break;
					case Operation:
						diagram.getComm().changeOperationLevel(result.getObjectId(), result.getName(), result.getOldLevel(), result.getNewLevel());
						break;
//					case Association:
//						diagram.getComm().changeAssociationLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());
//						break;
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
				diagram.getComm().changeOf(result.getObjectId(), result.getOldOfId(), result.getNewOfId());
				diagram.updateDiagram();
			}
			l.countDown();
		});


	}

	public void changeOwnerDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch l = new CountDownLatch(1);

		FmmlxProperty selectedProperty = diagram.getSelectedProperty();

		Platform.runLater(() -> {
			ChangeOwnerDialog dlg = new ChangeOwnerDialog(diagram, object, type);
			if (belongsPropertyToObject(object, selectedProperty, type)) {
				dlg.setSelected(selectedProperty);
			}

			Optional<ChangeOwnerDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeOwnerDialogResult result = opt.get();
				switch (result.getType()) {
					case Attribute:
						diagram.getComm().changeAttributeOwner(result.getObject().getId(), result.getNewOwnerID());
						break;
					case Operation:
						diagram.getComm().changeOperationOwner(result.getObject().getId(), result.getNewOwnerID());
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
				diagram.getComm().changeParent(result.getObject().getId(), result.getCurrentParentIds(), result.getNewParentIds());
				diagram.updateDiagram();
			}

			l.countDown();
		});

	}

	public void changeSlotValue(FmmlxObject hitObject, FmmlxSlot hitProperty) {

		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeSlotValueDialog dlg = new ChangeSlotValueDialog(diagram, hitObject, hitProperty);
			Optional<ChangeSlotValueDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeSlotValueDialogResult slotValueDialogResult = result.get();
				diagram.getComm().changeSlotValue(slotValueDialogResult.getObject().getId(), slotValueDialogResult.getSlot().getName(), slotValueDialogResult.getNewValue());
				diagram.updateDiagram();
			}

			l.countDown();
		});
	}

	public void updateDiagram() {
		diagram.updateDiagram();
	}

	public void toggleAbstract(FmmlxObject object) {
		diagram.getComm().setClassAbstract(object.getId(), !object.isAbstract());
		diagram.redraw();
	}

	public void toggleShowOperations() {
		for (FmmlxObject o : diagram.getObjects()) {
			if (o.getShowOperations() == showOperations) {
				o.toggleShowOperations();
			}
		}
		showOperations = !showOperations;
		diagram.updateDiagram();
	}

	public void toggleShowOperationValues() {
		for (FmmlxObject o : diagram.getObjects()) {
			if (o.getShowOperationValues() == showOperationValues) {
				o.toggleShowOperationValues();
			}
		}
		showOperationValues = !showOperationValues;
		diagram.redraw();
	}

	public void toggleShowSlots() {
		for (FmmlxObject o : diagram.getObjects()) {
			if (o.getShowSlots() == showSlots) {
				o.toggleShowSlots();
			}
		}
		showSlots = !showSlots;
		diagram.updateDiagram();
	}

	public void addOperationDialog(FmmlxObject object) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddOperationDialog dlg = new AddOperationDialog(diagram, object);
			Optional<AddOperationDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddOperationDialogResult result = opt.get();
				diagram.getComm().addOperation2(result.getObjectId(), result.getLevel(), result.getBody());
				diagram.updateDiagram();
			}
			latch.countDown();
		});
	}

	public void changeTypeDialog(FmmlxObject object, PropertyType type) {
		CountDownLatch latch = new CountDownLatch(1);

		FmmlxProperty selectedProperty = diagram.getSelectedProperty();

		Platform.runLater(() -> {
			ChangeTypeDialog dlg = new ChangeTypeDialog(object, type);
			if (belongsPropertyToObject(object, selectedProperty, type)) {
				dlg.setSelected(selectedProperty);
			}
			Optional<ChangeTypeDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeTypeDialogResult result = opt.get();

				switch (result.getType()) {
					case Attribute:
						diagram.getComm().changeAttributeType(result.getObject().getId(), result.getAttribute().getName(),
								result.getOldType(), result.getNewType());
						break;
					case Operation:
						diagram.getComm().changeOperationType(result.getObject().getId(), result.getOperation().getName(),
								result.getOldType(), result.getNewType());
						break;
//					case Association:
//						diagram.getComm().changeAssociationType(result.getObject().getId(), result.getAssociation().getName(),
//								result.getOldType(), result.getNewType());
//						break;
					default:
						System.err.println("AddDialogResult: No matching content type!");
				}
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
				diagram.getComm().changeAssociationTarget(result.getObject().getId(), result.getAssociationName(), result.getOldTargetID(), result.getNewTargetID());
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
				diagram.getComm().changeOperationBody(result.getObject().getId(), result.getSelectedItem().getName(), result.getBody());
				diagram.updateDiagram();
			}
			latch.countDown();
		});
	}

	public void addAssociationDialog(FmmlxObject source, FmmlxObject target) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddAssociationDialog dlg = new AddAssociationDialog(diagram, source, target);
			Optional<AddAssociationDialogResult> opt = dlg.showAndWait();
			diagram.setStandardMouseMode();

			if (opt.isPresent()) {
				final AddAssociationDialogResult result = opt.get();
				diagram.getComm().addAssociation(
						result.getSource().id, result.getTarget().id,
						result.getIdentifierSource(), result.getIdentifierTarget(),
						result.getDisplayNameSource(), result.getDisplayNameTarget(),
						result.getMultiplicitySource(), result.getMultiplicityTarget(),
						result.getInstLevelSource(), result.getInstLevelTarget()
				);
				diagram.updateDiagram();
			}
			latch.countDown();
		});
	}

	public void editAssociationDialog(FmmlxObject object, PropertyType association) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			EditAssociationDialog dlg = new EditAssociationDialog(diagram, object);
			Optional<EditAssociationDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final EditAssociationDialogResult result = opt.get();
				diagram.getComm().editAssociation(result.getSelectedAssociation().getId(),
						result.getSource(), result.getTarget(),
						result.getNewInstLevelSource(), result.getNewInstLevelTarget(),
						result.getNewDisplayNameSource(), result.getNewDisplayNameTarget(),
						result.getNewIdentifierSource(), result.getNewIdentifierTarget(),
						result.getMultiplicitySource(), result.getMultiplicityTarget());
				diagram.updateDiagram();
			}
			latch.countDown();
		});
	}

	public void setDrawEdgeMode(FmmlxObject source, PropertyType type) {
		diagram.setSelectedObject(source);
		diagram.setDrawEdgeMouseMode(type);
		diagram.storeLastClick(source.getCenterX(), source.getCenterY());
	}

	public void addAssociationInstance(FmmlxObject source, FmmlxObject target) {
		if (source != null && target != null) {
			// this case is relatively easy. We have two objects. Now we try to find the 
			// association they belong to. If there are more than one, show a dialog to pick one.
			// if there is only one, or one has been picked: proceed to xmf, otherwise nothing
			FmmlxAssociation association = null;
			Vector<FmmlxAssociation> associations = diagram.findAssociations(source, target);
			if (associations.size() > 1) {
				new Alert(AlertType.ERROR, "The programmer was too lazy to implement the dialog here. Proceed with random Association.", ButtonType.OK).showAndWait();
				association = associations.firstElement();
			} else if (associations.size() == 1) {
				association = associations.firstElement();
			} else {
				// if associations.size() == 0 then association remains null
				new Alert(AlertType.ERROR, "The selected objects don't fit any Association definition.", ButtonType.OK).showAndWait();
			}
			if (association != null) {
				final FmmlxObject sourceF = source;
				final FmmlxObject targetF = target;
				final FmmlxAssociation associationF = association;
//				CountDownLatch l = new CountDownLatch(1);

//				Platform.runLater(() -> {
				diagram.getComm().addAssociationInstance(sourceF.id, targetF.id, associationF.id);
//					l.countDown();
//				    });			
//				diagram.updateDiagram();
			}
		} else if (source != null ^ target != null) { // XOR
			// In this case only one object is set. If only second is set: swap them
			if (target != null) {
				source = target;
				target = null;
			} // swap
			// now: source != null and target == null
			// We don't know the association, so we try to figure it out:
			Vector<FmmlxAssociation> associations = diagram.findAssociations(source, target);
			new Alert(AlertType.ERROR, "No strategy for this situation yet. Choose two objects to create an Association Instance instead.", ButtonType.OK).showAndWait();
		} else {
			// nothing supplied
			new Alert(AlertType.ERROR, "No strategy for this situation yet. Choose two objects to create an Association Instance instead.", ButtonType.OK).showAndWait();
		}
	}

	public void removeAssociationInstance(FmmlxAssociationInstance instance) {
		diagram.getComm().removeAssociationInstance(instance.id);
		diagram.updateDiagram();
	}

	public void removeAssociation(FmmlxAssociation association) {
		diagram.getComm().removeAssociation(association.id, -1);
		diagram.updateDiagram();
	}

	public void associationValueDialog(FmmlxObject object, PropertyType association) {
		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			AssociationValueDialog dlg = new AssociationValueDialog(diagram);
			Optional<AssociationValueDialogResult> opt = dlg.showAndWait();

			latch.countDown();
		});
	}

	public boolean belongsPropertyToObject(FmmlxObject object, FmmlxProperty property, PropertyType dialogType) {
		if (property != null && property.getPropertyType() == dialogType) {
			switch (dialogType) {
				case Attribute:
					return belongsAttributeToObject(object, (FmmlxAttribute) property);
				case Operation:
					return belongsOperationToObject(object, (FmmlxOperation) property);
				default:
					return false;
			}
		}
		return false;
	}

	private boolean belongsAttributeToObject(FmmlxObject object, FmmlxAttribute selectedAttribute) {
		Vector<FmmlxAttribute> objectAttributes = object.getAllAttributes();
		for (FmmlxAttribute attribute : objectAttributes) {
			if (attribute == selectedAttribute) {
				return true;
			}
		}
		return false;
	}

	private boolean belongsOperationToObject(FmmlxObject object, FmmlxOperation selectedOperation) {
		Vector<FmmlxOperation> objectOperations = object.getOwnOperations();
		for (FmmlxOperation operation : objectOperations) {
			if (operation == selectedOperation) {
				return true;
			}
		}
		return false;
	}

	public void levelRaiseAll() {diagram.getComm().levelRaiseAll();}
	public void levelLowerAll() {diagram.getComm().levelLowerAll();}

	public void levelRaiseRelated(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelLowerRelated(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelInsertBelow(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelRemoveThis(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}


}
