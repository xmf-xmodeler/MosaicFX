package tool.clients.fmmlxdiagrams;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import tool.clients.dialogs.enquiries.FindClassDialog;
import tool.clients.dialogs.enquiries.FindImplementationDialog;
import tool.clients.dialogs.enquiries.FindSendersOfMessages;
import tool.clients.exporter.svg.SvgExporter;
import tool.clients.fmmlxdiagrams.classbrowser.ClassBrowserClient;
import tool.clients.fmmlxdiagrams.classbrowser.ObjectBrowser;
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
import tool.clients.importer.FMMLxImporter;
import tool.clients.serializer.FmmlxSerializer;
import tool.xmodeler.XModeler;
import xos.Value;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
								diagram.getComm().addMetaClass(diagram.getID(), mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentNames(), mcdResult.isAbstract(), x, y, false);
	
								canvas.setCursor(Cursor.DEFAULT);
								canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
								diagram.updateDiagram();
							}
						}
					};
					canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
				} else {
					diagram.getComm().addMetaClass(diagram.getID(), mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentNames(), mcdResult.isAbstract(), 0, 0, true);
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
					diagram.getComm().addMetaClass(diagram.getID(), mcdResult.getName(), mcdResult.getLevel(), mcdResult.getParentNames(), mcdResult.isAbstract(), x, y, false);
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
                            aidResult.getParentNames(), aidResult.isAbstract(), 0, 0, true);
					diagram.updateDiagram();
				} else {
					canvas.setCursor(Cursor.CROSSHAIR);
	
					EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
						public void handle(MouseEvent e) {
	
							int x = (int) e.getX();
							int y = (int) e.getY();
	
							if (x > 0 && y > 0) {
								diagram.getComm().addNewInstance(diagram.getID(), aidResult.getOfName(), aidResult.getName(),
	                                    aidResult.getParentNames(), aidResult.isAbstract(), x, y, false);
	
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
                            aidResult.getParentNames(), aidResult.isAbstract(), x, y, false);

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
				diagram.getComm().changeParent(diagram.getID(), result.object.getName(), result.getCurrentParentNames(), result.getNewParentNames());
				diagram.updateDiagram();
			}

//			l.countDown();
		});

	}

	public void changeSlotValue(FmmlxObject hitObject, FmmlxSlot hitProperty) {

//		Platform.runLater(() -> {
			ChangeSlotValueDialog dlg = new ChangeSlotValueDialog(diagram, hitObject, hitProperty);
			Optional<ChangeSlotValueDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeSlotValueDialogResult slotValueDialogResult = result.get();
				diagram.getComm().changeSlotValue(diagram.getID(), slotValueDialogResult.getObject().getName(), slotValueDialogResult.getSlot().getName(), slotValueDialogResult.getNewValue());
				diagram.updateDiagram();
			}
//		});
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
	
	public void editConstraint(FmmlxObject object, Constraint constraint) {
		Platform.runLater(() -> {
		AddConstraintDialog dlg = new AddConstraintDialog(diagram,object,constraint);
		Optional<AddConstraintDialog.AddConstraintDialogResult> opt = dlg.showAndWait();
		if(opt.isPresent()) {
			final AddConstraintDialog.AddConstraintDialogResult result = opt.get();
					diagram.getComm().editConstraint(
							diagram.getID(), 
							object.getPath(),
							result.object.getPath(),
							constraint.name,
							result.constName,
							constraint.level,
							result.instLevel, 
							constraint.bodyFull,
							result.body,
							constraint.reasonFull,
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

	public void changeBodyDialog(FmmlxObject object, FmmlxOperation initiallySelectedOperation) {
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeBodyDialog dlg = new ChangeBodyDialog(diagram, object, initiallySelectedOperation);
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
					diagram.getComm().setAssociationEndVisibility(diagram.getID(), result.selectedAssociation.getName(), false, result.sourceVisibleFromTarget);				
				}
				if(result.selectedAssociation.isTargetVisible() != result.targetVisibleFromSource) {
					diagram.getComm().setAssociationEndVisibility(diagram.getID(), result.selectedAssociation.getName(), true, result.targetVisibleFromSource);				
				}
				
				if(!result.selectedAssociation.getAccessNameEndToStart().equals(result.newIdentifierSource)) {
					System.err.println("getAccessNameEndToStart:" + result.selectedAssociation.getAccessNameEndToStart() + "--> " + result.newIdentifierSource);
					diagram.getComm().changeAssociationStart2EndAccessName(diagram.getID(), result.selectedAssociation.getName(), result.newIdentifierSource);
				}
				if(!result.selectedAssociation.getAccessNameStartToEnd().equals(result.newIdentifierTarget)) {
					System.err.println("getAccessNameStartToEnd:" + result.selectedAssociation.getAccessNameStartToEnd() + "--> " + result.newIdentifierTarget);
					diagram.getComm().changeAssociationEnd2StartAccessName(diagram.getID(), result.selectedAssociation.getName(), result.newIdentifierTarget);
				}
				
				if(!result.selectedAssociation.getLevelSource().equals(result.newInstLevelSource)) {
					System.err.println("getLevelEndToStart:" + result.selectedAssociation.getLevelSource() + "--> " + result.newInstLevelSource);
					diagram.getComm().changeAssociationEnd2StartLevel(diagram.getID(), result.selectedAssociation.getName(), result.newInstLevelSource);
				}
				if(!result.selectedAssociation.getLevelTarget().equals(result.newInstLevelTarget)) {
					System.err.println("getLevelStartToEnd:" + result.selectedAssociation.getLevelTarget() + "--> " + result.newInstLevelTarget);
					diagram.getComm().changeAssociationStart2EndLevel(diagram.getID(), result.selectedAssociation.getName(), result.newInstLevelTarget);
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
	
	public void setDelegation(FmmlxObject delegateFrom, FmmlxObject delegateTo) {
		if(delegateFrom == null) {
			new javafx.scene.control.Alert(AlertType.ERROR, "Delegation Source Missing", ButtonType.CANCEL).showAndWait(); return;
		}
		if(delegateTo == null) {
			Vector<FmmlxObject> delegationCandidates = new Vector<>();
			for(FmmlxObject o : diagram.getObjects()) {
				if(o != delegateFrom && o.level == delegateFrom.level) delegationCandidates.add(o);
			}
			ChoiceDialog<FmmlxObject> delegationChooseDialog = new ChoiceDialog<FmmlxObject>(delegateFrom.getDelegatesTo(false), delegationCandidates);
			delegationChooseDialog.setTitle("Set Delegation");
			delegationChooseDialog.setHeaderText("Select delegation target for " + delegateFrom.getName());
			Optional<FmmlxObject> chosenTarget = delegationChooseDialog.showAndWait();
			if(chosenTarget.isPresent()) {
				delegateTo = chosenTarget.get();
			} else {
				new javafx.scene.control.Alert(AlertType.ERROR, "Delegation Target Missing", ButtonType.CANCEL).showAndWait(); return;
			}
		}
		diagram.getComm().addDelegation(diagram.getID(), delegateFrom.getName(), delegateTo.getName(), delegateFrom.level-1);
		diagram.updateDiagram();
	}
	
	public void removeDelegation(FmmlxObject delegateFrom) {
		if(delegateFrom == null) {
			new javafx.scene.control.Alert(AlertType.ERROR, "Delegation Source Missing", ButtonType.CANCEL).showAndWait(); return;
		}
		diagram.getComm().removeDelegation(diagram.getID(), delegateFrom.getName());
		diagram.updateDiagram();
	}
	
	public void removeRoleFiller(FmmlxObject role) {
		if(role == null) {
			new javafx.scene.control.Alert(AlertType.ERROR, "Role Missing", ButtonType.CANCEL).showAndWait(); return;
		}
		diagram.getComm().removeRoleFiller(diagram.getID(), role.getName());
		diagram.updateDiagram();

	}
	
	public void setRoleFiller(final FmmlxObject role, final FmmlxObject roleFiller) {
		Platform.runLater(() -> {
			FmmlxObject roleFiller_Local = roleFiller;
			if(role == null) {
				new javafx.scene.control.Alert(AlertType.ERROR, "Role Missing", ButtonType.CANCEL).showAndWait(); return;
			}
			if(roleFiller_Local == null) {
				Vector<FmmlxObject> roleFillerCandidates = new Vector<>();
				FmmlxObject delegateFrom = diagram.getObjectByPath(role.getOfPath());
				
				DelegationEdge de = delegateFrom.getDelegatesToEdge(true);
				if(de == null) {
					new javafx.scene.control.Alert(AlertType.ERROR, "Delegation Missing", ButtonType.CANCEL).showAndWait(); return;
				}
				FmmlxObject delegateTo = de.targetNode;
				for(FmmlxObject o : diagram.getObjects()) {
					if(o.getAllAncestors().contains(delegateTo)) roleFillerCandidates.add(o);
				}
				ChoiceDialog<FmmlxObject> delegationChooseDialog = new ChoiceDialog<FmmlxObject>(null, roleFillerCandidates);
				delegationChooseDialog.setTitle("Set RoleFiller");
				delegationChooseDialog.setHeaderText("Select roleFiller for " + role.getName());
				Optional<FmmlxObject> chosenTarget = delegationChooseDialog.showAndWait();
				if(chosenTarget.isPresent()) {
					roleFiller_Local = chosenTarget.get();
				} else {
					new javafx.scene.control.Alert(AlertType.ERROR, "Delegation Target Missing", ButtonType.CANCEL).showAndWait(); return;
				}
			}
			diagram.getComm().setRoleFiller(diagram.getID(), role.getName(), roleFiller_Local.getName());
			diagram.updateDiagram();
		});
	}

	public void addAssociationInstance(FmmlxObject source, FmmlxObject target, FmmlxAssociation association) {
		if (source != null && target != null) {
			// this case is relatively easy. We have two objects. Now we try to find the 
			// association they belong to. If there are more than one, show a dialog to pick one.
			// if there is only one, or one has been picked: proceed to xmf, otherwise nothing
			//FmmlxAssociation association = null;
			if(association==null) {
				association=chooseAssociation(source, target);
				
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
//			if (target != null) {
//				source = target;
//				target = null;
//			} // swap
			// now: source != null and target == null
			// We don't know the association, so we try to figure it out:
			if (association==null) {
				association=chooseAssociation(source, target);
			}
			
			if(association!=null) {
				
				if((source==null || association.sourceNode.getInstancesByLevel(association.getLevelSource()).contains(source)) && (target==null ||association.targetNode.getInstancesByLevel(association.getLevelTarget()).contains(target))) {
					addAssociationInstanceDialog(source, target, association);		
				} else if((target==null || association.sourceNode.getInstancesByLevel(association.getLevelSource()).contains(target)) && (source==null ||association.targetNode.getInstancesByLevel(association.getLevelTarget()).contains(source))){
					addAssociationInstanceDialog(target, source, association);	
				} else {
					new Alert(AlertType.ERROR, "Selected elements don't fit association definition.", ButtonType.OK).showAndWait();
				}
				
			} else {
				new Alert(AlertType.ERROR, "No strategy for this situation yet. Choose two objects to create an Association Instance instead.", ButtonType.OK).showAndWait();	
			}
		} else {
			// nothing supplied
			if(association==null) {
				addAssociationInstanceDialog(source, target, association);	
			} else {
				new Alert(AlertType.ERROR, "No strategy for this situation yet. Choose two objects to create an Association Instance instead.", ButtonType.OK).showAndWait();	
			}	
		}
	}

	private FmmlxAssociation chooseAssociation(FmmlxObject source, FmmlxObject target) {
		FmmlxAssociation association=null;
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
		return association;
	}

	public void addAssociationInstanceDialog(FmmlxObject source, FmmlxObject target, FmmlxAssociation association) {
		Dialog dialog = new Dialog();
		dialog.setTitle("Generate Instance for Link");
		dialog.setHeaderText("Choose Source & Target for the Link via Dropdown Menu");
		Label sourceLabel = new Label("Source: ");
		Label targetLabel = new Label("Target: ");
		ComboBox<FmmlxObject> sourceCB = new ComboBox<FmmlxObject>();
		ComboBox<FmmlxObject> targetCB = new ComboBox<FmmlxObject>();
		Vector<FmmlxObject> sourceList = new Vector<FmmlxObject>();
		Vector<FmmlxObject> targetList = new Vector<FmmlxObject>();
		if(source!=null) {
			sourceList.add(source);
			sourceCB.getItems().addAll(sourceList);
			sourceCB.setValue(source);
		} else {
			sourceList.addAll(association.sourceNode.getInstancesByLevel(association.getLevelSource()));
			sourceCB.getItems().addAll(sourceList);
		}
		if(target!=null) {
			targetList.add(target);
			targetCB.getItems().addAll(targetList);
			targetCB.setValue(target);
		} else {
			targetList.addAll(association.targetNode.getInstancesByLevel(association.getLevelTarget()));
			targetCB.getItems().addAll(targetList);
		}
		Label space=new Label("        ");
		GridPane grid = new GridPane();
		grid.add(sourceLabel, 1, 1);
		grid.add(sourceCB, 1, 2);
		grid.add(space,2,1);
		grid.add(targetLabel, 3, 1);
		grid.add(targetCB, 3, 2);
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(Bindings.createBooleanBinding(
			    () -> (sourceCB.getValue() == null) || targetCB.getValue()==null,
			    sourceCB.valueProperty(),
			    targetCB.valueProperty()
			));
		Optional result= dialog.showAndWait();
		if (result.isPresent()) {
			diagram.getComm().addAssociationInstance(diagram.getID(), sourceCB.getSelectionModel().getSelectedItem().getName(), targetCB.getSelectionModel().getSelectedItem().getName(), association.getName());
		} else {
			dialog.close();
		}
	}
	
	public void removeAssociationInstance(FmmlxLink link) {
		diagram.getComm().removeAssociationInstance(diagram.getID(), 
				link.getOfName(), 
				link.getSourceNode().getName(), 
				link.getTargetNode().getName());
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

	public void unhideElementsDialog() {
		Dialog<Vector<FmmlxObject>> unhideElementsDialog = new Dialog<>();
		unhideElementsDialog.setTitle("Unhide Elements");
		
		ButtonType okButtonType = new ButtonType("Unhide", ButtonData.OK_DONE);
		unhideElementsDialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
		
		
		
		Vector<FmmlxObject> hiddenElements = new Vector<>();
		Vector<FmmlxObject> shownElements = new Vector<>();
		for(FmmlxObject o : diagram.getObjects()) {
			if(o.hidden) {
				hiddenElements.add(o);
			} else if (o.hidden==false) {
				shownElements.add(o);
			}
		}
		
		ListView<FmmlxObject> listView = new ListView<>();
		listView.getItems().addAll(hiddenElements);
		sortListView(listView);
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ListView<FmmlxObject> shownElementsListView = new ListView<>();
		shownElementsListView.getItems().addAll(shownElements);
		sortListView(shownElementsListView);
		shownElementsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		GridPane gridPane = new GridPane();
		Button toShow = new Button("<<");
		toShow.setOnAction(e->{
			shownElementsListView.getItems().addAll(listView.getSelectionModel().getSelectedItems());
			listView.getItems().removeAll(listView.getSelectionModel().getSelectedItems());
			sortListView(shownElementsListView);
		});
		Button toHide = new Button(">>");
		toHide.setOnAction(e->{
			listView.getItems().addAll(shownElementsListView.getSelectionModel().getSelectedItems());
			shownElementsListView.getItems().removeAll(shownElementsListView.getSelectionModel().getSelectedItems());
			sortListView(listView);
		});
		Button filter = new Button("Filters");
		
		Label shownElementsLabel = new Label("Shown Elements");
		Label hiddenElementsLabel = new Label("Hidden Elements");
		gridPane.add(shownElementsLabel, 0, 0,1,1);
		gridPane.add(shownElementsListView, 0, 1,1,5);
		gridPane.add(toHide, 1, 3,1,1);
		gridPane.add(toShow, 1, 4,1,1);
		gridPane.add(filter, 1, 5,1,1);
		gridPane.add(hiddenElementsLabel, 2, 0,1,1);
		gridPane.add(listView, 2, 1,1,5);
		gridPane.setPadding(new Insets(15,15,15,15));
		unhideElementsDialog.getDialogPane().setContent(gridPane);
	

		unhideElementsDialog.setResultConverter(dialogButton -> {
		    if (dialogButton == okButtonType) {
		    	Vector<FmmlxObject> result = new Vector<>();
		    	result.addAll(shownElementsListView.getItems());
		        return result;
		    }
		    return null;
		});
		
		
		Optional<Vector<FmmlxObject>> result = unhideElementsDialog.showAndWait();
				
    	result.ifPresent(vec -> {hide(vec, false);});
    	
    	Vector<FmmlxObject> resultHide = new Vector<>();
    	resultHide.addAll(listView.getItems());
    	hide(resultHide,true);
    	
	}
	
	public void sortListView(ListView<FmmlxObject> listView) {
		Vector<FmmlxObject> v = new Vector<>(listView.getItems());
		Collections.sort(v,new Comparator<FmmlxObject>() {
			public int compare(FmmlxObject thisObject, FmmlxObject anotherObject) {
				if(thisObject.getLevel()>anotherObject.getLevel()) {
					return -1;
				} else if (thisObject.getLevel()<anotherObject.getLevel()) {
					return 1;
				} else {
					return thisObject.name.compareTo(anotherObject.getName());
				}
			}	
		});
		listView.getItems().clear();
		listView.getItems().addAll(v);
	}
		
	public void showObjectBrowser(FmmlxObject object) {
			
		Platform.runLater(() -> new ObjectBrowser(diagram, object, null).show());
			
	}

	public void exportSvg() {
		Platform.runLater(() ->{
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("svg", "*.svg"));
		fc.setTitle("Export File");
		File file = fc.showSaveDialog(XModeler.getStage());

		if(file!= null){
			if(!(diagram instanceof FmmlxDiagram)) throw new IllegalArgumentException();
			Platform.runLater(() -> {
				String filePath = file.getPath();
				double width = diagram.getCanvas().getWidth();
				double height = diagram.getCanvas().getHeight() ;
				double extraHeight = getExtraHeight();
				SvgExporter svgExporter;
				try {
					svgExporter = new SvgExporter(filePath, width, height+extraHeight);
					svgExporter.export(diagram, extraHeight);
				} catch (TransformerException | ParserConfigurationException e) {
					e.printStackTrace();
				}
			});
		}
		});
	}

	private double getExtraHeight() {
		return (diagram.issues.size()+1) * 14;
	}

	public void showCertainLevel() {
		Platform.runLater(() ->{
			ShowCertainLevelDialog dlg = new ShowCertainLevelDialog(diagram);
			Optional<ShowCertainLevelDialogResult> result = dlg.showAndWait();

			if(result.isPresent()){
				final ShowCertainLevelDialogResult sclResult = result.get();
				Vector<Integer> chosenLevel = sclResult.getChosenLevels();

				Vector<FmmlxObject> objects = diagram.getObjects();
				Vector<FmmlxObject> hiddenObjects = new Vector<>();
				Vector<FmmlxObject> unHiddenObjects = new Vector<>();
				for(FmmlxObject obj : objects){
					if(!chosenLevel.contains(obj.getLevel())){
						hiddenObjects.add(obj);
					} else {
						unHiddenObjects.add(obj);
					}
				}
				hide(unHiddenObjects, false);
				hide(hiddenObjects, true);
				updateDiagram();
			}
		});
	}

	public void showAll() {
		Platform.runLater(() ->{
			hide(diagram.getObjects(), false);
			updateDiagram();
		});
	}

	public void importDiagram() {
		Platform.runLater(() ->{
			FileChooser fc = new FileChooser();
			fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xml", "*.xml"));
			fc.setTitle("choose File");
			File file = fc.showOpenDialog(XModeler.getStage());

			if(file!= null){
				FMMLxImporter importer = new FMMLxImporter(file.getPath(), diagram);
				importer.handleLogs();
			}
			updateDiagram();
		});
	}
}
