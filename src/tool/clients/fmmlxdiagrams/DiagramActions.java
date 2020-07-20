package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import org.w3c.dom.Node;
import tool.clients.fmmlxdiagrams.classbrowser.ClassBrowserClient;
import tool.clients.fmmlxdiagrams.dialogs.*;
import tool.clients.fmmlxdiagrams.dialogs.association.AssociationDialog;
import tool.clients.fmmlxdiagrams.dialogs.association.AssociationValueDialog;
import tool.clients.fmmlxdiagrams.dialogs.association.ChangeTargetDialog;
import tool.clients.fmmlxdiagrams.dialogs.association.MultiplicityDialog;
import tool.clients.fmmlxdiagrams.dialogs.enumeration.AddEnumerationDialog;
import tool.clients.fmmlxdiagrams.dialogs.enumeration.DeleteEnumerationDialog;
import tool.clients.fmmlxdiagrams.dialogs.enumeration.EditEnumerationDialog;
import tool.clients.fmmlxdiagrams.dialogs.instance.AddInstanceDialog;
import tool.clients.fmmlxdiagrams.dialogs.instance.ChangeOfDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.InstanceGenerator;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.IValueGenerator;
import tool.clients.fmmlxdiagrams.dialogs.operation.AddOperationDialog;
import tool.clients.fmmlxdiagrams.dialogs.operation.ChangeBodyDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.*;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeLevelDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeNameDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeOwnerDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeParentDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeTypeDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.RemoveDialog;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.ValueGenerator;
import tool.clients.serializer.LogXmlManager;
import tool.clients.serializer.ObjectXmlManager;
import tool.clients.serializer.Serializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;


public class DiagramActions {

	private final double zoomLevel = Math.sqrt(2);

	private final FmmlxDiagram diagram;

	public DiagramActions(FmmlxDiagram diagram) {
		this.diagram = diagram;
	}

	public void redrawDiagram() {
		diagram.redraw();
	}
	
	public void classBrowserStage(boolean xmf) {
		if(xmf)  {
			diagram.getComm().openPackageBrowser();
		} else {
			Platform.runLater(() -> ClassBrowserClient.show(diagram));
		}
	}

	public void addMetaClassDialog() {
//		CountDownLatch l = new CountDownLatch(2);

		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog(diagram);
			dlg.setTitle("Add metaclass");
			String objectName;
			Optional<MetaClassDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				final MetaClassDialogResult mcdResult = result.get();

				Canvas canvas = diagram.getCanvas();
				canvas.setCursor(Cursor.CROSSHAIR);
				objectName = mcdResult.getName();

				EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
					public void handle(MouseEvent e) {

						int x = (int) e.getX();
						int y = (int) e.getY();
						if (x > 0 && y > 0) {
							diagram.getComm().addMetaClass(diagram, mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentIds(), mcdResult.isAbstract(), x, y);

							canvas.setCursor(Cursor.DEFAULT);
							canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
							diagram.updateDiagram();
//							l.countDown();
						}
					}
				};
				canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);

			}
		});
	}
	
	public void addMetaClassDialog(MouseEvent e) {
		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog(diagram);
			dlg.setTitle("Add metaclass");
			String objectName="";
			Optional<MetaClassDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				final MetaClassDialogResult mcdResult = result.get();

				objectName = mcdResult.getName();

				int x = (int) e.getX();
				int y = (int) e.getY();

				if (x > 0 && y > 0) {
					diagram.getComm().addMetaClass(diagram, mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentIds(), mcdResult.isAbstract(), x, y);
					
					diagram.updateDiagram();
				}
			}
		});
	}

	public void addInstanceDialog() {
		addInstanceDialog(null);
	}

	public void addInstanceDialog(FmmlxObject object) {
//		CountDownLatch l = new CountDownLatch(1);

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
							diagram.getComm().addNewInstance(diagram, aidResult.getOf(), aidResult.getName(), aidResult.getLevel(),
									aidResult.getParentId(), false, x, y);

							canvas.setCursor(Cursor.DEFAULT);
							canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);

							diagram.updateDiagram();
						}
					}
				};
				canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
			}
//			l.countDown();
		});
	}
	
	public void addInstanceDialog(FmmlxObject object, MouseEvent e) {
		Platform.runLater(() -> {
			AddInstanceDialog dialog = new AddInstanceDialog(diagram, object);
			dialog.setTitle("Add instance");
			Optional<AddInstanceDialogResult> result = dialog.showAndWait();

			if (result.isPresent()) {
				final AddInstanceDialogResult aidResult = result.get();

				int x = (int) e.getX();
				int y = (int) e.getY();
				
				if (x > 0 && y > 0) {
					diagram.getComm().addNewInstance(diagram, aidResult.getOf(), aidResult.getName(), aidResult.getLevel(),
							aidResult.getParentId(), false, x, y);

					diagram.updateDiagram();
				}
			}
		});
		
	}

	public void addAttributeDialog() {
		addAttributeDialog(null);
	}

	public void addAttributeDialog(FmmlxObject object) {

//		CountDownLatch l = new CountDownLatch(1);

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
				diagram.getComm().addAttribute(diagram, aad.getClassID(), aad.getName(), aad.getLevel(), aad.getType(), aad.getMultiplicity());
			}
			diagram.updateDiagram();
//			l.countDown();
		});
	}

	public void addEnumerationDialog() {
//		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddEnumerationDialog dlg;
			
			dlg = new AddEnumerationDialog(diagram);

			dlg.setTitle("Create Enumeration");
			Optional<AddEnumerationDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				AddEnumerationDialogResult aed = result.get();
				diagram.getComm().addEnumeration(diagram, aed.getEnumeration().getName()); 
			}
			diagram.updateDiagram();
//			l.countDown();
		});
		
	}
	
	public void editEnumerationDialog(String string, String enumName) {
//		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			EditEnumerationDialog dlg;
			
			dlg = new EditEnumerationDialog(diagram);

			if (string.equals("edit_element")) {
				dlg.setTitle("Edit Enumeration");
			} 
		
			dlg.show();

//			if (result.isPresent()) {
//				EditEnumerationDialogResult aed = result.get();
//				diagram.getComm().editEnumeration(diagram, aed.getEnumName(), aed.getNewEditedEnum().getItems());
//			}
//			diagram.updateDiagram();
//			l.countDown();
		});
	}
	
	public void deleteEnumerationDialog() {
//		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			DeleteEnumerationDialog dlg;
			
			dlg = new DeleteEnumerationDialog(diagram);

			dlg.setTitle("Delete Enumeration");
			Optional<DeleteEnumerationDialogResult> result = dlg.showAndWait();
			diagram.updateDiagram();
//			l.countDown();
		});
	}

	public void removeDialog(FmmlxObject object, PropertyType type) {
//		CountDownLatch l = new CountDownLatch(1);

		FmmlxProperty selectedFmmlxProperty = diagram.getSelectedProperty();

		Platform.runLater(() -> {
			RemoveDialog dlg = new RemoveDialog(diagram, object, type);
			if (belongsPropertyToObject(object, selectedFmmlxProperty, type)) {
				dlg.setSelected(selectedFmmlxProperty);
			}
			Optional<RemoveDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final RemoveDialogResult result = opt.get();
				switch (result.getType()) {
					case Class:
						diagram.getComm().removeClass(diagram, result.getObject().getId(), 0);
						break;
					case Operation:
						diagram.getComm().removeOperation(diagram, result.getObject().getId(), result.getOperation().getName(), 0);
						break;
					case Attribute:
						diagram.getComm().removeAttribute(diagram, result.getObject().getId(), result.getAttribute().getName(), 0);
						break;
					case Association:
						diagram.getComm().removeAssociation(diagram, result.getAssociation().getId(), 0);
					default:
						System.err.println("ChangeNameDialogResult: No matching content type!");
				}
			}
			diagram.updateDiagram();
//			l.countDown();
		});
	}


	public void zoomIn() {
		diagram.setZoom(Math.min(2, diagram.getZoom() * zoomLevel));
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
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeNameDialog dlg = new ChangeNameDialog(diagram, object, type, selectedProperty);

			Optional<ChangeNameDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeNameDialogResult result = opt.get();
				switch (result.getType()) {
					case Class:
						diagram.getComm().changeClassName(diagram, result.getObjectId(), result.getNewName());
						break;
					case Operation:
						diagram.getComm().changeOperationName(diagram, result.getObjectId(), result.getOldName(), result.getNewName());
						break;
					case Attribute:
						diagram.getComm().changeAttributeName(diagram, result.getObjectId(), result.getOldName(), result.getNewName());
						break;
//					case Association:
//						diagram.getComm().changeAssociationName(result.getObjectId(), result.getOldName(), result.getNewName());
					default:
						System.err.println("ChangeNameDialogResult: No matching content type!");
				}
			}
			diagram.updateDiagram();
//			latch.countDown();
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


	public void changeMultiplicityDialog(FmmlxObject object, PropertyType type) {
		FmmlxProperty selectedProperty = diagram.getSelectedProperty();
		
		if (selectedProperty != null && selectedProperty instanceof FmmlxAttribute && type == PropertyType.Attribute) {
			FmmlxAttribute att = (FmmlxAttribute) selectedProperty;
			Multiplicity oldMul = att.getMultiplicity();
			
			Platform.runLater(() -> {
				MultiplicityDialog md = new MultiplicityDialog(oldMul);
				Optional<MultiplicityDialogResult> mr = md.showAndWait();
				if(mr.isPresent()) {
					diagram.getComm().changeAttributeMultiplicity(diagram, object.id, att.name, oldMul, mr.get().convertToMultiplicity());
					diagram.updateDiagram();
				}
			});
			
		}
	}
	
	public void changeLevelDialog(FmmlxObject object, PropertyType type) {
//		CountDownLatch latch = new CountDownLatch(1);

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
						diagram.getComm().changeClassLevel(diagram, result.getObjectId(), result.getOldLevel(), result.getNewLevel());
						break;
					case Attribute:
						diagram.getComm().changeAttributeLevel(diagram, result.getObjectId(), result.getName(), result.getOldLevel(), result.getNewLevel());
						break;
					case Operation:
						diagram.getComm().changeOperationLevel(diagram, result.getObjectId(), result.getName(), result.getOldLevel(), result.getNewLevel());
						break;
//					case Association:
//						diagram.getComm().changeAssociationLevel(result.getObjectId(), result.getOldLevel(), result.getNewLevel());
//						break;
					default:
						System.err.println("ChangeLevelDialogResult: No matching content type!");
				}
				diagram.updateDiagram();
			}

//			latch.countDown();
		});
	}
	
	public void runInstanceGenerator(FmmlxObject object) {
		Platform.runLater(() -> {

			InstanceGenerator instanceGenerator = new InstanceGenerator(object);
			instanceGenerator.openDialog(diagram);

			for(int i =0 ; i< instanceGenerator.getNumberOfInstance(); i++){
				System.out.println("Name : "+instanceGenerator.getGeneratedInstanceName().get(i));
				for (Map.Entry<FmmlxAttribute, IValueGenerator> fmmlxAttributeIValueGeneratorEntry : instanceGenerator.getValue().entrySet()) {
					Map.Entry<FmmlxAttribute, ValueGenerator> pair = (Map.Entry) fmmlxAttributeIValueGeneratorEntry;
					System.out.println(instanceGenerator.getSelectedParent());
					System.out.println(pair.getKey().getName() + " : " + ((IValueGenerator) pair.getValue()).getGeneratedValue().get(i));
				}
				instanceGenerator.generateInstance(i, instanceGenerator.getGeneratedInstanceName().get(i), 15, 15);
			}
			if(instanceGenerator.getNumberOfInstance()>0){
				diagram.updateDiagram();
			}
		});
	}

	public void changeOfDialog(FmmlxObject object) {

//		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeOfDialog dlg = new ChangeOfDialog(diagram, object);
			Optional<ChangeOfDialogResult> cod = dlg.showAndWait();

			if (cod.isPresent()) {
				final ChangeOfDialogResult result = cod.get();
				diagram.getComm().changeOf(diagram, result.getObjectId(), result.getOldOfId(), result.getNewOfId());
				diagram.updateDiagram();
			}
//			l.countDown();
		});


	}

	public void changeOwnerDialog(FmmlxObject object, PropertyType type) {
//		CountDownLatch l = new CountDownLatch(1);

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
						diagram.getComm().changeAttributeOwner(diagram, result.getObject().getId(), result.getAttribute().getName(), result.getNewOwnerID());
						break;
					case Operation:
						diagram.getComm().changeOperationOwner(diagram, result.getObject().getId(), result.getOperation().getName(), result.getNewOwnerID());
						break;
					default:
						System.err.println("ChangeOwnerDialogResult: No matching content type!");
						break;
				}
				diagram.updateDiagram();
			}

//			l.countDown();
		});
	}

	public void changeParentsDialog(FmmlxObject object) {

//		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeParentDialog dlg = new ChangeParentDialog(diagram, object);
			Optional<ChangeParentDialogResult> cpd = dlg.showAndWait();

			if (cpd.isPresent()) {
				ChangeParentDialogResult result = cpd.get();
				diagram.getComm().changeParent(diagram, result.getObject().getId(), result.getCurrentParentIds(), result.getNewParentIds());
				diagram.updateDiagram();
			}

//			l.countDown();
		});

	}

	public void changeSlotValue(FmmlxObject hitObject, FmmlxSlot hitProperty) {

//		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeSlotValueDialog dlg = new ChangeSlotValueDialog(diagram, hitObject, hitProperty);
			Optional<ChangeSlotValueDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeSlotValueDialogResult slotValueDialogResult = result.get();
				diagram.getComm().changeSlotValue(diagram, slotValueDialogResult.getObject().getId(), slotValueDialogResult.getSlot().getName(), slotValueDialogResult.getNewValue());
				diagram.updateDiagram();
			}

//			l.countDown();
		});
	}

	public void updateDiagram() {
		diagram.updateDiagram();
	}
	
	public void printProtocol() {
		diagram.getComm().printProtocol(diagram);
	}

	public void toggleAbstract(FmmlxObject object) {
		diagram.getComm().setClassAbstract(diagram, object.getId(), !object.isAbstract());
		diagram.updateDiagram();
		
	}

	public void setShowOperations(CheckBox box) {
		boolean show = box.isSelected();
		diagram.setShowOperations(show);
		for (FmmlxObject o : diagram.getObjects()) {
			o.setShowOperations(show);
		}
		diagram.redraw();
	}
	
	public void setShowGettersAndSetters(CheckBox box) {
		boolean show = box.isSelected();
		diagram.setShowGettersAndSetters(show);
		for (FmmlxObject o : diagram.getObjects()) {
			o.setShowGettersAndSetters(show);
		}
		diagram.redraw();
		
	}


	public void setShowOperationValues(CheckBox box) {
		boolean show = box.isSelected();
		diagram.setShowOperationValues(show);
		for (FmmlxObject o : diagram.getObjects()) {
			o.setShowOperationValues(show);
		}
		diagram.redraw();
	}

	public void setShowSlots(CheckBox box) {
		boolean show = box.isSelected();
		diagram.setShowSlots(show);
		for (FmmlxObject o : diagram.getObjects()) {
			o.setShowSlots(show);
		}
		diagram.redraw();
	}
	
	public void setShowDerivedOperations(CheckBox box) {
		boolean show = box.isSelected();
		diagram.setShowDerivedOperations(show);
		for (FmmlxObject o : diagram.getObjects()) {
			o.setShowDerivedOperations(show);
		}
		diagram.redraw();
		
	}

	public void setShowDerivedAttributes(CheckBox box) {
		boolean show=box.isSelected();
		diagram.setShowDerivedAttributes(show);
		for (FmmlxObject o : diagram.getObjects()) {
			o.setShowDerivedAttributes(show);
		}
		diagram.redraw();
	}


	public void addOperationDialog(FmmlxObject object) {
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddOperationDialog dlg = new AddOperationDialog(diagram, object);
			Optional<AddOperationDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddOperationDialogResult result = opt.get();
				diagram.getComm().addOperation2(diagram, result.getObjectId(), result.getLevel(), result.getBody());
				diagram.updateDiagram();
			}
//			latch.countDown();
		});
	}

	public void changeTypeDialog(FmmlxObject object, PropertyType type) {
//		CountDownLatch latch = new CountDownLatch(1);

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
						diagram.getComm().changeAttributeType(diagram, result.getObject().getId(), result.getAttribute().getName(),
								result.getOldType(), result.getNewType());
						break;
					case Operation:
						diagram.getComm().changeOperationType(diagram, result.getObject().getId(), result.getOperation().getName(),
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

//			latch.countDown();
		});
	}

	public void changeTargetDialog(FmmlxObject object, PropertyType type) {
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeTargetDialog dlg = new ChangeTargetDialog(diagram, object, type);
			Optional<ChangeTargetDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeTargetDialogResult result = opt.get();
				diagram.getComm().changeAssociationTarget(diagram, result.getObject().getId(), result.getAssociationName(), result.getOldTargetID(), result.getNewTargetID());
				diagram.updateDiagram();
			}

//			latch.countDown();
		});
	}

	public void changeBodyDialog(FmmlxObject object) {
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeBodyDialog dlg = new ChangeBodyDialog(diagram, object);
			Optional<ChangeBodyDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeBodyDialogResult result = opt.get();
				diagram.getComm().changeOperationBody(diagram, result.getObject().getId(), result.getSelectedItem().getName(), result.getBody());
				diagram.updateDiagram();
			}
//			latch.countDown();
		});
	}

	public void addAssociationDialog(FmmlxObject source, FmmlxObject target) {
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			AssociationDialog dlg = new AssociationDialog(diagram, source, target, false);
			Optional<AssociationDialogResult> opt = dlg.showAndWait();
			diagram.setStandardMouseMode();

			if (opt.isPresent()) {
				final AssociationDialogResult result = opt.get();
				diagram.getComm().addAssociation(diagram,
						result.getSource().id, result.getTarget().id,
						result.getNewIdentifierSource(), result.getNewIdentifierTarget(),
						result.getNewDisplayName(),
						null, result.getMultiplicitySource(), result.getMultiplicityTarget(),
						result.getNewInstLevelSource(), result.getNewInstLevelTarget(),
						result.isSourceVisibleFromTarget(),  result.isTargetVisibleFromSource(), 
						result.isSymmetric(), result.isTransitive()
						);
				diagram.updateDiagram();
			}
//			latch.countDown();
		});
	}

	public void editAssociationDialog(final FmmlxAssociation association) {
		Platform.runLater(() -> {
			AssociationDialog dlg = new AssociationDialog(diagram, association, true);
			Optional<AssociationDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AssociationDialogResult result = opt.get();
				
				if(result.getAssociation().isSourceVisible() != result.isSourceVisibleFromTarget()) {
					diagram.getComm().setAssociationEndVisibility(diagram, result.getAssociation().id, false, result.isSourceVisibleFromTarget());				
				}
				if(result.getAssociation().isTargetVisible() != result.isTargetVisibleFromSource()) {
					diagram.getComm().setAssociationEndVisibility(diagram, result.getAssociation().id, true, result.isTargetVisibleFromSource());				
				}
				
				if(!result.getAssociation().getAccessNameEndToStart().equals(result.getNewIdentifierSource())) {
					System.err.println("getAccessNameEndToStart:" + result.getAssociation().getAccessNameEndToStart() + "--> " + result.getNewIdentifierSource());
					diagram.getComm().changeAssociationStart2EndAccessName(diagram, result.getAssociation().id, result.getNewIdentifierSource());
				}
				if(!result.getAssociation().getAccessNameStartToEnd().equals(result.getNewIdentifierTarget())) {
					System.err.println("getAccessNameStartToEnd:" + result.getAssociation().getAccessNameStartToEnd() + "--> " + result.getNewIdentifierTarget());
					diagram.getComm().changeAssociationEnd2StartAccessName(diagram, result.getAssociation().id, result.getNewIdentifierTarget());
				}
				
				if(!result.getAssociation().getLevelEndToStart().equals(result.getNewInstLevelSource())) {
					System.err.println("getLevelEndToStart:" + result.getAssociation().getLevelEndToStart() + "--> " + result.getNewInstLevelSource());
					diagram.getComm().changeAssociationStart2EndLevel(diagram, result.getAssociation().id, result.getNewInstLevelSource());
				}
				if(!result.getAssociation().getLevelStartToEnd().equals(result.getNewInstLevelTarget())) {
					System.err.println("getLevelStartToEnd:" + result.getAssociation().getLevelStartToEnd() + "--> " + result.getNewInstLevelTarget());
					diagram.getComm().changeAssociationEnd2StartLevel(diagram, result.getAssociation().id, result.getNewInstLevelTarget());
				}
				
				if(!result.getAssociation().getMultiplicityEndToStart().equals(result.getMultiplicitySource())) {
					diagram.getComm().changeAssociationEnd2StartMultiplicity(diagram, result.getAssociation().id, result.getMultiplicitySource());
				}
				if(!result.getAssociation().getMultiplicityStartToEnd().equals(result.getMultiplicityTarget())) {
					diagram.getComm().changeAssociationStart2EndMultiplicity(diagram, result.getAssociation().id, result.getMultiplicityTarget());
				}
				
				if(!result.getAssociation().getName().equals(result.getNewDisplayName())) {
					System.err.println("getName:" +result.getAssociation().getName()  + "--> " + result.getNewDisplayName());
					diagram.getComm().changeAssociationForwardName(diagram, result.getAssociation().id, result.getNewDisplayName());
				}
					
				diagram.updateDiagram();
			}
		});
	}

	public void setDrawEdgeMode(FmmlxObject source, PropertyType type) {
		diagram.setSelectedObject(source);
		diagram.setDrawEdgeMouseMode(type, source);
		diagram.storeLastClick(source.getCenterX(), source.getCenterY());
		diagram.deselectAll();
	}
	
	public void addDelegation(FmmlxObject delegateFrom, FmmlxObject delegateTo) {
		if (delegateFrom != null && delegateTo != null) { // just for safety
			diagram.getComm().addDelegation(diagram, delegateFrom.id, delegateTo.id);
		}		
	}
	
	public void setRoleFiller(FmmlxObject delegateFrom, FmmlxObject delegateTo) {
		if (delegateFrom != null && delegateTo != null) { // just for safety
			diagram.getComm().setRoleFiller(diagram, delegateFrom.id, delegateTo.id);
		}		
	}

	public void addAssociationInstance(FmmlxObject source, FmmlxObject target) {
		if (source != null && target != null) {
			// this case is relatively easy. We have two objects. Now we try to find the 
			// association they belong to. If there are more than one, show a dialog to pick one.
			// if there is only one, or one has been picked: proceed to xmf, otherwise nothing
			FmmlxAssociation association = null;
			Vector<FmmlxAssociation> associations = diagram.findAssociations(source, target);
			if (associations.size() > 1) {
				ChoiceDialog<FmmlxAssociation> dialog = new ChoiceDialog<>(associations.firstElement(), associations);
				dialog.setTitle("Choose Association Dialog");
				dialog.setHeaderText("More than one matching association found");
				dialog.setContentText("Choose an association:");

				// Traditional way to get the response value.
				Optional<FmmlxAssociation> result = dialog.showAndWait();
				if (result.isPresent()){
				    association = result.get();
				} else {
					// do nothing
				}
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
				diagram.getComm().addAssociationInstance(diagram, sourceF.id, targetF.id, associationF.id);
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

	public void removeAssociationInstance(FmmlxLink instance) {
		diagram.getComm().removeAssociationInstance(diagram, instance.id);
		diagram.updateDiagram();
	}

	public void removeAssociation(FmmlxAssociation association) {
		diagram.getComm().removeAssociation(diagram, association.id, -1);
		diagram.updateDiagram();
	}

	public void associationValueDialog(FmmlxObject object, PropertyType association) {
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			AssociationValueDialog dlg = new AssociationValueDialog(diagram);
			Optional<AssociationValueDialogResult> opt = dlg.showAndWait();

//			latch.countDown();
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

	public void levelRaiseAll() {diagram.getComm().levelRaiseAll(diagram);diagram.updateDiagram();}
	public void levelLowerAll() {diagram.getComm().levelLowerAll(diagram);diagram.updateDiagram();}

	public void levelRaiseRelated(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelLowerRelated(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelInsertBelow(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelRemoveThis(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}

	public void assignToGlobal(FmmlxObject object) {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Assign to Global Variable");
		dialog.setHeaderText("Global Variable Name:");
		 
		Optional<String> result = dialog.showAndWait();
		 
		if (result.isPresent()) 		 
			diagram.getComm().assignToGlobal(diagram, object, result.get());
	}


	public void showBody(FmmlxObject object, FmmlxOperation operation) {
		diagram.getComm().showBody(diagram, object, operation);
	}

	public void addMissingLink(FmmlxObject obj, FmmlxAssociation assoc) {
		Platform.runLater(() -> {
			AddMissingLinkDialog dlg = new AddMissingLinkDialog(diagram, obj, assoc);
			Optional<AddMissingLinkDialogResult> solution = dlg.showAndWait();

			if(solution.isPresent()) {
				if(solution.get().createNew) {
					addInstanceDialog(solution.get().selection);
				} else {
					diagram.getComm().addAssociationInstance(diagram, obj.getId(), solution.get().selection.getId(), assoc.getId());
					diagram.updateDiagram();
				}
			}
		});
	}

	public Vector<String> testEvalList(String text) {
		try {
			return diagram.getComm().evalList(diagram, text);
		} catch (TimeOutException e) {
			return new Vector<>(Arrays.asList(new String[] {"Time", "Out", "Exception"}));
		}	
	}

	public void save() {
		Platform.runLater(() -> {
			Serializer serializer = new Serializer();
			try {
				serializer.saveState(diagram);
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		});
	}


//	public void attributeGeneratorDialog(FmmlxAttribute tmp, InstanceGeneratorGenerateType selectedType) {
//		Platform.runLater(() -> {
//			if(selectedType!=null) {
//				AttributeGeneratorDialog dlg = new AttributeGeneratorDialog(tmp, selectedType);
//				Optional<AttributeGeneratorDialogResult> opt = dlg.showAndWait();
//			}
//		});
//	}	
}
