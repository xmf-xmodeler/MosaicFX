package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import tool.clients.dialogs.enquiries.FindClassDialog;
import tool.clients.dialogs.enquiries.FindImplementationDialog;
import tool.clients.dialogs.enquiries.FindSendersOfMessages;
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
import tool.clients.fmmlxdiagrams.dialogs.operation.AddOperationDialog;
import tool.clients.fmmlxdiagrams.dialogs.operation.ChangeBodyDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.*;
import tool.clients.fmmlxdiagrams.dialogs.shared.*;
import tool.clients.fmmlxdiagrams.instancegenerator.InstanceGenerator;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.IValueGenerator;
import tool.clients.serializer.FmmlxSerializer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

public class DiagramActions {

	private final AbstractPackageViewer diagram;

	public DiagramActions(AbstractPackageViewer diagram) {
		this.diagram = diagram;
	}
	
	public void openClassBrowserStage(boolean xmf) {
		if(xmf)  {
			diagram.getComm().openPackageBrowser();
		} else {
			Platform.runLater(() -> ClassBrowserClient.show(diagram));
		}
	}

	public void addMetaClassDialog(Canvas canvas) {

		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog(diagram);
			dlg.setTitle("Add metaclass");
			Optional<MetaClassDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				final MetaClassDialogResult mcdResult = result.get();

				if(canvas != null) {				
					canvas.setCursor(Cursor.CROSSHAIR);
	
					EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
						public void handle(MouseEvent e) {
	
							int x = (int) e.getX();
							int y = (int) e.getY();
							if (x > 0 && y > 0) {
								diagram.getComm().addMetaClass(diagram.getID(), mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentNames(), mcdResult.isAbstract(), x, y);
	
								canvas.setCursor(Cursor.DEFAULT);
								canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
								diagram.updateDiagram();
							}
						}
					};
					canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
				} else {
					diagram.getComm().addMetaClass(diagram.getID(), mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentNames(), mcdResult.isAbstract(), 0, 0);
					diagram.updateDiagram();
				}

			}
		});
	}
	
	public void addMetaClassDialog(MouseEvent e) {
		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog(diagram);
			dlg.setTitle("Add metaclass");
			Optional<MetaClassDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				final MetaClassDialogResult mcdResult = result.get();

				int x = (int) e.getX();
				int y = (int) e.getY();

				if (x > 0 && y > 0) {
					diagram.getComm().addMetaClass(diagram.getID(), mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentNames(), mcdResult.isAbstract(), x, y);
					diagram.updateDiagram();
				}
			}
		});
	}

	public void addInstanceDialog(Canvas canvas) {
		addInstanceDialog(null, canvas);
	}

	public void addInstanceDialog(FmmlxObject object, Canvas canvas) {

		Platform.runLater(() -> {
			AddInstanceDialog dialog = new AddInstanceDialog(diagram, object);
			dialog.setTitle("Add instance");
			Optional<AddInstanceDialogResult> result = dialog.showAndWait();

			if (result.isPresent()) {
				final AddInstanceDialogResult aidResult = result.get();

				if(canvas == null) {
					diagram.getComm().addNewInstance(diagram.getID(), aidResult.getOfName(), aidResult.getName(),
                            aidResult.getParentNames(), false, 0, 0);
					diagram.updateDiagram();
				} else {
					canvas.setCursor(Cursor.CROSSHAIR);
	
					EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
						public void handle(MouseEvent e) {
	
							int x = (int) e.getX();
							int y = (int) e.getY();
	
							if (x > 0 && y > 0) {
								diagram.getComm().addNewInstance(diagram.getID(), aidResult.getOfName(), aidResult.getName(),
	                                    aidResult.getParentNames(), false, x, y);
	
								canvas.setCursor(Cursor.DEFAULT);
								canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
	
								diagram.updateDiagram();
							}
						}
					};
					canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
				}
			}
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
					diagram.getComm().addNewInstance(diagram.getID(), aidResult.getOfName(), aidResult.getName(),
                            aidResult.getParentNames(), false, x, y);

					diagram.updateDiagram();
				}
			}
		});
	}

	public void addAttributeDialog() {
		addAttributeDialog(null);
	}

	public void addAttributeDialog(FmmlxObject object) {
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
				diagram.getComm().addAttribute(diagram.getID(), aad.className, aad.name, aad.level, aad.type, aad.multi);
			}
			diagram.updateDiagram();
		});
	}

	public void addEnumerationDialog() {
		Platform.runLater(() -> {
			AddEnumerationDialog dlg;
			
			dlg = new AddEnumerationDialog();

			dlg.setTitle("Create Enumeration");
			Optional<AddEnumerationDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				AddEnumerationDialogResult aed = result.get();
				diagram.getComm().addEnumeration(diagram.getID(), aed.getEnumeration().getName()); 
			}
			diagram.updateDiagram();
		});
		
	}
	
	public void editEnumerationDialog(String string, String enumName) {
		Platform.runLater(() -> {
			EditEnumerationDialog dlg;
			
			dlg = new EditEnumerationDialog(diagram);

			if (string.equals("edit_element")) {
				dlg.setTitle("Edit Enumeration");
			} 
		
			dlg.show();
		});
	}
	
	public void deleteEnumerationDialog() {
//		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			DeleteEnumerationDialog dlg;
			
			dlg = new DeleteEnumerationDialog(diagram);

			dlg.setTitle("Delete Enumeration");
			//Optional<DeleteEnumerationDialogResult> result = 
			dlg.showAndWait();
			diagram.updateDiagram();
//			l.countDown();
		});
	}

	public void removeDialog(FmmlxObject object, PropertyType type) {
		removeDialog(object, type, diagram.getSelectedProperty());
	}
	
	public <Property extends FmmlxProperty> void removeDialog(FmmlxObject object, PropertyType type, Property selectedFmmlxProperty) {
//		FmmlxProperty selectedFmmlxProperty = diagram.getSelectedProperty();

		Platform.runLater(() -> {
			RemoveDialog<Property> dlg = new RemoveDialog<Property>(object, type);
			if (belongsPropertyToObject(object, selectedFmmlxProperty, type)) {
				dlg.setSelected(selectedFmmlxProperty);
			}
			Optional<RemoveDialogResult<Property>> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final RemoveDialogResult<Property> result = opt.get();
				switch (type) {
					case Class:
						diagram.getComm().removeClass(diagram.getID(), result.getObject().getName(), 0);
						break;
					case Operation:
						diagram.getComm().removeOperation(diagram.getID(), result.getObject().getName(), result.getProperty().getName(), 0);
						break;
					case Attribute:
						diagram.getComm().removeAttribute(diagram.getID(), result.getObject().getName(), result.getProperty().getName(), 0);
						break;
					case Association:
						diagram.getComm().removeAssociation(diagram.getID(), result.getProperty().getName(), 0);
						break;
					case Constraint:
						diagram.getComm().removeConstraint(diagram.getID(), result.getObject().getPath(), result.getProperty().getName());
						break;
					default:
						System.err.println("RemoveDialogResult: No matching content type!");
				}
			}
			diagram.updateDiagram();
//			l.countDown();
		});
	}




	public <Property extends FmmlxProperty> void changeNameDialog(FmmlxObject object, PropertyType type, Property selectedProperty) {
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeNameDialog<Property> dlg = new ChangeNameDialog<Property>(diagram, object, type, selectedProperty);

			Optional<ChangeNameDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeNameDialogResult result = opt.get();
				switch (result.getType()) {
					case Class:
						diagram.getComm().changeClassName(diagram.getID(), result.getObjectName(), result.getNewName());
						break;
					case Operation:
						diagram.getComm().changeOperationName(diagram.getID(), result.getObjectName(), result.getOldName(), result.getNewName());
						break;
					case Attribute:
						diagram.getComm().changeAttributeName(diagram.getID(), result.getObjectName(), result.getOldName(), result.getNewName());
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
		changeMultiplicityDialog(object, type, diagram.getSelectedProperty());
	}
		
	public void changeMultiplicityDialog(FmmlxObject object, PropertyType type, FmmlxProperty selectedProperty) {

		if (selectedProperty instanceof FmmlxAttribute && type == PropertyType.Attribute) {
			FmmlxAttribute att = (FmmlxAttribute) selectedProperty;
			Multiplicity oldMul = att.getMultiplicity();
			
			Platform.runLater(() -> {
				MultiplicityDialog md = new MultiplicityDialog(oldMul);
				Optional<MultiplicityDialogResult> mr = md.showAndWait();
				if(mr.isPresent()) {
					diagram.getComm().changeAttributeMultiplicity(diagram.getID(), object.getName(), att.name, oldMul, mr.get().convertToMultiplicity());
					diagram.updateDiagram();
				}
			});
			
		}
	}
	
	public void changeLevelDialog(FmmlxObject object, PropertyType type) {
		changeLevelDialog(object, type, diagram.getSelectedProperty());
	}
		
	public void changeLevelDialog(FmmlxObject object, PropertyType type, FmmlxProperty selectedProperty) {

		Platform.runLater(() -> {
			ChangeLevelDialog dlg = new ChangeLevelDialog(object, type);
			if (belongsPropertyToObject(object, selectedProperty, type)) {
				dlg.setSelected(selectedProperty);
			}
			Optional<ChangeLevelDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeLevelDialogResult result = opt.get();
				switch (result.getType()) {
					case Class:
						diagram.getComm().changeClassLevel(diagram.getID(), result.getObjectName(), result.getNewLevel());
						break;
					case Attribute:
						diagram.getComm().changeAttributeLevel(diagram.getID(), result.getObjectName(), result.getName(), result.getOldLevel(), result.getNewLevel());
						break;
					case Operation:
						diagram.getComm().changeOperationLevel(diagram.getID(), result.getObjectName(), result.getName(), result.getOldLevel(), result.getNewLevel());
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
					System.out.println(instanceGenerator.getSelectedParent());
					System.out.println(fmmlxAttributeIValueGeneratorEntry.getKey().getName() + " : " + ((IValueGenerator) fmmlxAttributeIValueGeneratorEntry.getValue()).getGeneratedValue().get(i));
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
				diagram.getComm().changeOf(diagram.getID(), result.getObject().getName(), result.getOldOfName(), result.getNewOf().getName());
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
						diagram.getComm().changeAttributeOwner(diagram.getID(), result.getObject().getName(), result.getAttribute().getName(), result.getNewOwner());
						break;
					case Operation:
						diagram.getComm().changeOperationOwner(diagram.getID(), result.getObject().getName(), result.getOperation().getName(), result.getNewOwnerName());
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
				diagram.getComm().changeParent(diagram.getID(), result.getObject().getName(), result.getCurrentParentNames(), result.getNewParentNames());
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
				diagram.getComm().changeSlotValue(diagram.getID(), slotValueDialogResult.getObject().getName(), slotValueDialogResult.getSlot().getName(), slotValueDialogResult.getNewValue());
				diagram.updateDiagram();
			}

//			l.countDown();
		});
	}

	public void updateDiagram() {
		diagram.updateDiagram();
	}
	
	public void printProtocol() {
		diagram.getComm().printProtocol(diagram.getID());
	}

	public void toggleAbstract(FmmlxObject object) {
		diagram.getComm().setClassAbstract(diagram.getID(), object.getName(), !object.isAbstract());
		diagram.updateDiagram();		
	}
	
	public void addOperationDialog(FmmlxObject object) {

		Platform.runLater(() -> {
			AddOperationDialog dlg = new AddOperationDialog(diagram, object);
			Optional<AddOperationDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddOperationDialogResult result = opt.get();
				diagram.getComm().addOperation2(diagram.getID(), result.getObject().getName(), result.getLevel(), result.getBody());
				diagram.updateDiagram();
			}
		});
	}
	
	public void addConstraintDialog(FmmlxObject object) {

		Platform.runLater(() -> {
			AddConstraintDialog dlg = new AddConstraintDialog(diagram, object);
			Optional<AddConstraintDialog.AddConstraintDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddConstraintDialog.AddConstraintDialogResult result = opt.get();
				diagram.getComm().addConstraint(
						diagram.getID(), 
						result.object.getPath(), 
						result.constName, 
						result.instLevel, 
						result.body, 
						result.reason);
				diagram.updateDiagram();
			}
		});
	}
	
	public Object removeConstraintDialog(FmmlxObject object) {
		FmmlxProperty property = diagram.getSelectedProperty();
		
		
				
		return null;
	}

	public void changeTypeDialog(FmmlxObject object, PropertyType type) {
		changeTypeDialog(object, type, diagram.getSelectedProperty());
	}
	
    public void changeTypeDialog(FmmlxObject object, PropertyType type, FmmlxProperty selectedProperty) {

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
						diagram.getComm().changeAttributeType(diagram.getID(), result.getObject().getName(), result.getAttribute().getName(),
								result.getOldType(), result.getNewType());
						break;
					case Operation:
						diagram.getComm().changeOperationType(diagram.getID(), result.getObject().getName(), result.getOperation().getName(),
								result.getNewType());
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
			ChangeTargetDialog dlg = new ChangeTargetDialog(object, type);
			Optional<ChangeTargetDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeTargetDialogResult result = opt.get();
				diagram.getComm().changeAssociationTarget(diagram.getID(), result.getAssociationName(), result.getOldTargetName(), result.getNewTargetName());
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
				diagram.getComm().changeOperationBody(diagram.getID(), result.getObject().getName(), result.getSelectedItem().getName(), result.getBody());
				diagram.updateDiagram();
			}
//			latch.countDown();
		});
	}

	public void addAssociationDialog(FmmlxObject source, FmmlxObject target) {

		Platform.runLater(() -> {
			AssociationDialog dlg = new AssociationDialog(diagram, source, target, false);
			Optional<AssociationDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AssociationDialogResult result = opt.get();
				diagram.getComm().addAssociation(diagram.getID(),
						result.source.getName(), result.target.getName(),
						result.newIdentifierSource, result.newIdentifierTarget,
						result.newDisplayName,
						null, result.multTargetToSource, result.multSourceToTarget,
						result.newInstLevelSource, result.newInstLevelTarget,
						result.sourceVisibleFromTarget,  result.targetVisibleFromSource, 
						result.symmetric, result.transitive
						);
				diagram.updateDiagram();
			}
		});
	}

	public void editAssociationDialog(final FmmlxAssociation association) {
		Platform.runLater(() -> {
			AssociationDialog dlg = new AssociationDialog(diagram, association, true);
			Optional<AssociationDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AssociationDialogResult result = opt.get();
				
				if(result.selectedAssociation.isSourceVisible() != result.sourceVisibleFromTarget) {
					diagram.getComm().setAssociationEndVisibility(diagram.getID(), result.selectedAssociation.path, false, result.sourceVisibleFromTarget);				
				}
				if(result.selectedAssociation.isTargetVisible() != result.targetVisibleFromSource) {
					diagram.getComm().setAssociationEndVisibility(diagram.getID(), result.selectedAssociation.path, true, result.targetVisibleFromSource);				
				}
				
				if(!result.selectedAssociation.getAccessNameEndToStart().equals(result.newIdentifierSource)) {
					System.err.println("getAccessNameEndToStart:" + result.selectedAssociation.getAccessNameEndToStart() + "--> " + result.newIdentifierSource);
					diagram.getComm().changeAssociationStart2EndAccessName(diagram.getID(), result.selectedAssociation.path, result.newIdentifierSource);
				}
				if(!result.selectedAssociation.getAccessNameStartToEnd().equals(result.newIdentifierTarget)) {
					System.err.println("getAccessNameStartToEnd:" + result.selectedAssociation.getAccessNameStartToEnd() + "--> " + result.newIdentifierTarget);
					diagram.getComm().changeAssociationEnd2StartAccessName(diagram.getID(), result.selectedAssociation.path, result.newIdentifierTarget);
				}
				
				if(!result.selectedAssociation.getLevelSource().equals(result.newInstLevelSource)) {
					System.err.println("getLevelEndToStart:" + result.selectedAssociation.getLevelSource() + "--> " + result.newInstLevelSource);
					diagram.getComm().changeAssociationEnd2StartLevel(diagram.getID(), result.selectedAssociation.path, result.newInstLevelSource);
				}
				if(!result.selectedAssociation.getLevelTarget().equals(result.newInstLevelTarget)) {
					System.err.println("getLevelStartToEnd:" + result.selectedAssociation.getLevelTarget() + "--> " + result.newInstLevelTarget);
					diagram.getComm().changeAssociationStart2EndLevel(diagram.getID(), result.selectedAssociation.path, result.newInstLevelTarget);
				}
				
				if(!result.selectedAssociation.getMultiplicityEndToStart().equals(result.multTargetToSource)) {
					diagram.getComm().changeAssociationEnd2StartMultiplicity(diagram.getID(), result.selectedAssociation.getName(), result.multTargetToSource);
				}
				if(!result.selectedAssociation.getMultiplicityStartToEnd().equals(result.multSourceToTarget)) {
					diagram.getComm().changeAssociationStart2EndMultiplicity(diagram.getID(), result.selectedAssociation.getName(), result.multSourceToTarget);
				}
				
				if(!result.selectedAssociation.getName().equals(result.newDisplayName)) {
					System.err.println("getName:" +result.selectedAssociation.getName()  + "--> " + result.newDisplayName);
					diagram.getComm().changeAssociationForwardName(diagram.getID(), result.selectedAssociation.getName(), result.newDisplayName);
				}
					
				diagram.updateDiagram();
			}
		});
	}
	
	public void addDelegation(FmmlxObject delegateFrom, FmmlxObject delegateTo) {
		if (delegateFrom != null && delegateTo != null) { // just for safety
			diagram.getComm().addDelegation(diagram.getID(), delegateFrom.getName(), delegateTo.getName(), delegateFrom.level-1);
		}		
	}
	
	public void setRoleFiller(FmmlxObject delegateFrom, FmmlxObject delegateTo) {
		if (delegateFrom != null && delegateTo != null) { // just for safety
			diagram.getComm().setRoleFiller(diagram.getID(), delegateFrom.getName(), delegateTo.getName());
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
				//				CountDownLatch l = new CountDownLatch(1);

//				Platform.runLater(() -> {
				diagram.getComm().addAssociationInstance(diagram.getID(), source.getName(), target.getName(), association.getName());
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
		diagram.getComm().removeAssociationInstance(diagram.getID(), instance.path);
		diagram.updateDiagram();
	}

	public void removeAssociation(FmmlxAssociation association) {
		diagram.getComm().removeAssociation(diagram.getID(), association.getName(), -1);
		diagram.updateDiagram();
	}

	public void associationValueDialog(FmmlxObject object, PropertyType association) {
		Platform.runLater(() -> {
			AssociationValueDialog dlg = new AssociationValueDialog(diagram);
			//Optional<AssociationValueDialogResult> opt = 
		    dlg.showAndWait();
		});
	}

	public boolean belongsPropertyToObject(FmmlxObject object, FmmlxProperty property, PropertyType dialogType) {
		if(object.getOwnAttributes().contains(property)) return true;
		if(object.getOwnOperations().contains(property)) return true;
		if(object.getConstraints().contains(property)) return true;
		return false;
	}

	public void levelRaiseAll() {diagram.getComm().levelRaiseAll(diagram.getID());diagram.updateDiagram();}
	public void levelLowerAll() {diagram.getComm().levelLowerAll(diagram.getID());diagram.updateDiagram();}

	public void levelRaiseRelated(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelLowerRelated(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelInsertBelow(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}
	public void levelRemoveThis(FmmlxObject o) {throw new RuntimeException("Not implemented yet");}

	public void assignToGlobal(FmmlxObject object) {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Assign to Global Variable");
		dialog.setHeaderText("Global Variable Name:");
		 
		Optional<String> result = dialog.showAndWait();

		result.ifPresent(s -> diagram.getComm().assignToGlobal(diagram.getID(), object, s));
	}

	public void showBody(FmmlxObject object, FmmlxOperation operation) {
		diagram.getComm().showBody(diagram, object, operation);
	}

	public void addMissingLink(FmmlxObject obj, FmmlxAssociation assoc, Canvas canvas) {
		Platform.runLater(() -> {
			AddMissingLinkDialog dlg = new AddMissingLinkDialog(obj, assoc);
			Optional<AddMissingLinkDialogResult> solution = dlg.showAndWait();

			if(solution.isPresent()) {
				if(solution.get().createNew) {
					addInstanceDialog(solution.get().selection, canvas);
				} else {
					diagram.getComm().addAssociationInstance(diagram.getID(), obj.getName(), solution.get().selection.getName(), assoc.getName());
					diagram.updateDiagram();
				}
			}
		});
	}

	public Vector<String> testEvalList(String text) {
		try {
			return diagram.getComm().evalList(diagram, text);
		} catch (TimeOutException e) {
			return new Vector<>(Arrays.asList("Time", "Out", "Exception"));
		}	
	}

	public void save() {
		if(!(diagram instanceof FmmlxDiagram)) throw new IllegalArgumentException();
		Platform.runLater(() -> {
			try {
				String filePath = ((FmmlxDiagram) diagram).getFilePath();
				FmmlxDiagramCommunicator communicator = diagram.getComm();
				String label = ((FmmlxDiagram)diagram).getDiagramLabel();
				FmmlxSerializer serializer = new FmmlxSerializer(((FmmlxDiagram)diagram).getFilePath());
				serializer.save(diagram.getPackagePath(), filePath, label, diagram.getID(), communicator);
			} catch (TransformerException | ParserConfigurationException e) {
				e.printStackTrace();
			}
		});
	}

	public void openFindImplementationDialog() {

		Platform.runLater(() -> {
			FindImplementationDialog dlg = new FindImplementationDialog(diagram, diagram.getComm());
			dlg.showAndWait();
		});
	}

	public Object openFindClassDialog() {
		Platform.runLater(() -> {
			FindClassDialog dialog = new FindClassDialog();
			dialog.showAndWait();
		});
		return null; 
	}

	public Object openFindSendersDialog() {
		Platform.runLater(() -> {
			FindSendersOfMessages dialog = new FindSendersOfMessages(diagram, diagram.getComm());
			dialog.showAndWait();
		});
		return null;
	}

	public void hide(Vector<FmmlxObject> objects, boolean hide) {
		diagram.getComm().hideElements(diagram.getID(), objects, hide);
		diagram.updateDiagram();
	}

    public void testGetEdges() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() {
				Vector<Integer> ids = diagram.getComm().getAllDiagramIDs(diagram.getPackagePath());
				for (int id : ids){
					System.out.println("diagram id : "+id);
					diagram.getComm().testGetAllEdgePositions(id);
				}
				return null;
			}
		};
		new Thread(task).start();
    }

	public void testGetLabel() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() {
				Vector<Integer> ids = diagram.getComm().getAllDiagramIDs(diagram.getPackagePath());
				System.out.println(ids);
				for (int id : ids){
					System.out.println("diagram id : "+id);
					diagram.getComm().testGetAllLabelPositions(id);
				}
				return null;
			}
		};
		new Thread(task).start();
	}
}
