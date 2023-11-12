package tool.clients.fmmlxdiagrams;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import tool.clients.customui.CustomUI;
import tool.clients.dialogs.enquiries.FindClassDialog;
import tool.clients.dialogs.enquiries.FindImplementationDialog;
import tool.clients.dialogs.enquiries.FindSendersOfMessages;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.fmmlxdiagrams.classbrowser.ClassBrowserClient;
import tool.clients.fmmlxdiagrams.classbrowser.ObjectBrowser;
import tool.clients.fmmlxdiagrams.dialogs.AddAttributeDialog;
import tool.clients.fmmlxdiagrams.dialogs.AddConstraintDialog;
import tool.clients.fmmlxdiagrams.dialogs.AddEnumerationDialog;
import tool.clients.fmmlxdiagrams.dialogs.AddInstanceDialog;
import tool.clients.fmmlxdiagrams.dialogs.AddMissingLinkDialog;
import tool.clients.fmmlxdiagrams.dialogs.AddOperationDialog;
import tool.clients.fmmlxdiagrams.dialogs.AssociationDialog;
import tool.clients.fmmlxdiagrams.dialogs.AssociationTypeDialog;
import tool.clients.fmmlxdiagrams.dialogs.AssociationValueDialog;
import tool.clients.fmmlxdiagrams.dialogs.ChangeOfDialog;
import tool.clients.fmmlxdiagrams.dialogs.ChangeParentDialog;
import tool.clients.fmmlxdiagrams.dialogs.ChangeSlotValueDialog;
import tool.clients.fmmlxdiagrams.dialogs.ChangeTargetDialog;
import tool.clients.fmmlxdiagrams.dialogs.CreateMetaClassDialog;
import tool.clients.fmmlxdiagrams.dialogs.DeleteEnumerationDialog;
import tool.clients.fmmlxdiagrams.dialogs.EditEnumerationDialog;
import tool.clients.fmmlxdiagrams.dialogs.MergePropertyDialog;
import tool.clients.fmmlxdiagrams.dialogs.MultiplicityDialog;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.ShowCertainLevelDialog;
import tool.clients.fmmlxdiagrams.dialogs.UnhideElementsDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeLevelDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeNameDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeOwnerDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.ChangeTypeDialog;
import tool.clients.fmmlxdiagrams.dialogs.shared.RemoveDialog;
import tool.clients.fmmlxdiagrams.graphics.SvgExporter;
import tool.clients.fmmlxdiagrams.graphics.View;
import tool.clients.fmmlxdiagrams.instancewizard.InstanceWizard;
import tool.helper.userProperties.PropertyManager;
import tool.xmodeler.XModeler;

public class DiagramActions {

	private final AbstractPackageViewer diagram;

	public AbstractPackageViewer getDiagram() {
		return diagram;
	}

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

	public void addMetaClassDialog(final View view) {

		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog(diagram);
			dlg.setTitle("Add metaclass");
			Optional<CreateMetaClassDialog.Result> result = dlg.showAndWait();

			if (result.isPresent()) {
				final CreateMetaClassDialog.Result mcdResult = result.get();

				if(view != null) {				
					view.getCanvas().setCursor(Cursor.CROSSHAIR);
	
					EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
						public void handle(MouseEvent e) {
							// use mouse x/y to have a result in matrix does not invert
							double x = e.getX();
							double y = e.getY();
							
							try{
								Point2D p = view.getCanvasTransform().inverseTransform(new Point2D(x, y));
								x = p.getX(); y = p.getY();
							} catch (javafx.scene.transform.NonInvertibleTransformException ex) {}
													
							if (x > 0 && y > 0) {
								diagram.getComm().addMetaClass(diagram.getID(), 
										mcdResult.name, 
										mcdResult.level, 
										mcdResult.getParentNames(), 
										mcdResult.isAbstract, 
										mcdResult.isSingleton, 
										(int) (x+.5), (int) (y+.5), false);
	
								view.getCanvas().setCursor(Cursor.DEFAULT);
								view.getCanvas().removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
								diagram.updateDiagram();
							}
						}
					};
					view.getCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
				} else {
					diagram.getComm().addMetaClass(diagram.getID(), 
							mcdResult.name, 
							mcdResult.level, 
							mcdResult.getParentNames(),
							mcdResult.isAbstract,
							mcdResult.isSingleton,
							0, 0, true);
					diagram.updateDiagram();
					
				}

			}
		});
	}
	
	public void addMetaClassDialog(Point2D p) {
		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog(diagram);
			dlg.setTitle("Add metaclass");
			Optional<CreateMetaClassDialog.Result> result = dlg.showAndWait();

			if (result.isPresent()) {
				final CreateMetaClassDialog.Result mcdResult = result.get();

				diagram.getComm().addMetaClass(diagram.getID(), mcdResult.name, 
						mcdResult.level, mcdResult.getParentNames(), 
						mcdResult.isAbstract, mcdResult.isSingleton, 
						(int) (p.getX()+.5), (int) (p.getY()+.5), 
						false);
				diagram.updateDiagram();
			
			}
		});
	}

	public void addInstanceDialog(View view) {
		addInstanceDialog(null, view);
	}

	public void addInstanceDialog(FmmlxObject object, View view) {

		Platform.runLater(() -> {
			AddInstanceDialog dialog = new AddInstanceDialog(diagram, object);
			dialog.setTitle("Add instance");
			Optional<AddInstanceDialog.Result> result = dialog.showAndWait();

			if (result.isPresent()) {
				final AddInstanceDialog.Result aidResult = result.get();

				if(view == null) {
					diagram.getComm().addNewInstance(diagram.getID(), aidResult.getOfName(), 
							aidResult.name, aidResult.level,
                            aidResult.getParentNames(), 
                            aidResult.isAbstract, 
                            aidResult.isSingleton, 
                            0, 0, true);
					diagram.updateDiagram();
				} else {
					view.getCanvas().setCursor(Cursor.CROSSHAIR);
	
					EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
						public void handle(MouseEvent e) {
							double x = e.getX();
							double y = e.getY();
							
							try{
								Point2D p = view.getCanvasTransform().inverseTransform(new Point2D(x, y));
								x = p.getX(); y = p.getY();
							} catch (javafx.scene.transform.NonInvertibleTransformException ex) {}
							
	
							if (x > 0 && y > 0) {
								diagram.getComm().addNewInstance(diagram.getID(), aidResult.getOfName(), 
										aidResult.name, aidResult.level, 
										aidResult.getParentNames(), aidResult.isAbstract, 
			                            aidResult.isSingleton, 
										(int) (x+.5), (int) (y+.5), false);
	
								view.getCanvas().setCursor(Cursor.DEFAULT);
								view.getCanvas().removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
	
								diagram.updateDiagram();
							}
						}
					};
					view.getCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
				}
			}
		});
	}
	
	public void addInstanceDialog(FmmlxObject object, Point2D p) {
		Platform.runLater(() -> {
			AddInstanceDialog dialog = new AddInstanceDialog(diagram, object);
			dialog.setTitle("Add instance");
			Optional<AddInstanceDialog.Result> result = dialog.showAndWait();

			if (result.isPresent()) {
				final AddInstanceDialog.Result aidResult = result.get();
				diagram.getComm().addNewInstance(
						diagram.getID(), aidResult.getOfName(), aidResult.name,
						aidResult.level,
                        aidResult.getParentNames(), aidResult.isAbstract, 
                        aidResult.isSingleton, 
                        (int) (p.getX()+.5), (int) (p.getY()+.5), false);
				diagram.updateDiagram();
			}
		});
	}

	public void addAttributeDialog() {
		addAttributeDialog(null);
	}

	public void addAttributeDialog(FmmlxObject object) {
		Platform.runLater(() -> {
			final AddAttributeDialog dlg;
			if (object != null) {
				dlg = new AddAttributeDialog(diagram, object);
			} else {
				dlg = new AddAttributeDialog(diagram);
			}

			dlg.setTitle("Add Attribute");
			Optional<AddAttributeDialog.Result> result = dlg.showAndWait();

			if (result.isPresent()) {
				AddAttributeDialog.Result aad = result.get();
				diagram.getComm().addAttribute(diagram.getID(), aad.className, aad.name, aad.level, aad.type, aad.multi, aad.isIntrinsic, aad.isIncomplete, aad.isOptional);
			}
			diagram.updateDiagram();
		});
	}

	public void addEnumerationDialog() {
		Platform.runLater(() -> {
			AddEnumerationDialog dlg;
			
			dlg = new AddEnumerationDialog();

			dlg.setTitle("Create Enumeration");
			Optional<String> result = dlg.showAndWait();

			if (result.isPresent()) {
				String aed = result.get();
				diagram.getComm().addEnumeration(diagram.getID(), aed); 
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

		Platform.runLater(() -> {
			RemoveDialog<Property> dlg = new RemoveDialog<Property>(object, type);
			if (belongsPropertyToObject(object, selectedFmmlxProperty, type)) {
				dlg.setSelected(selectedFmmlxProperty);
			}
			Optional<RemoveDialog<Property>.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final RemoveDialog<Property>.Result result = opt.get();
				switch (type) {
					case Class:
						diagram.getComm().removeClass(diagram.getID(), result.object.getName(), 0);
						break;
					case Operation:
						diagram.getComm().removeOperation(diagram.getID(), result.object.getName(), result.property.getName(), 0);
						break;
					case Attribute:
						diagram.getComm().removeAttribute(diagram.getID(), result.object.getName(), result.property.getName(), 0);
						break;
					case Association: 
						diagram.getComm().removeAssociation(diagram.getID(), (FmmlxAssociation) result.property); 
						break;
					case Constraint:
						diagram.getComm().removeConstraint(diagram.getID(), result.object.getPath(), result.property.getName());
						break;
					default:
						System.err.println("RemoveDialogResult: No matching content type!");
				}
			}
			diagram.updateDiagram();
		});
	}

	public <Property extends FmmlxProperty> void changeNameDialog(FmmlxObject object, PropertyType type, Property selectedProperty) {
		Platform.runLater(() -> {
			ChangeNameDialog<Property> dlg = new ChangeNameDialog<Property>(diagram, object, type, selectedProperty);

			Optional<ChangeNameDialog<Property>.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeNameDialog<?>.Result result = opt.get();
				switch (result.type) {
					case Class:
						diagram.getComm().changeClassName(diagram.getID(), result.getObjectName(), result.newName);
						break;
					case Operation:
						diagram.getComm().changeOperationName(diagram.getID(), result.getObjectName(), result.oldName, result.newName);
						break;
					case Attribute:
						diagram.getComm().changeAttributeName(diagram.getID(), result.getObjectName(), result.oldName, result.newName);
						break;
//					case Association:
//						diagram.getComm().changeAssociationName(result.getObjectId(), result.getOldName(), result.getNewName());
					default:
						System.err.println("ChangeNameDialogResult: No matching content type!");
				}
			}
			diagram.updateDiagram();
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
				Optional<Multiplicity> mr = md.showAndWait();
				if(mr.isPresent()) {
					diagram.getComm().changeAttributeMultiplicity(diagram.getID(), object.getName(), att.name, oldMul, mr.get());
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
			Optional<ChangeLevelDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeLevelDialog.Result result = opt.get();
				switch (result.type) {
					case Class:
						diagram.getComm().changeClassLevel(diagram.getID(), result.getObjectPath(), result.newLevel);
						break;
					case Attribute:
						diagram.getComm().changeAttributeLevel(diagram.getID(), result.getObjectPath(), result.name, result.oldLevel, result.newLevel);
						break;
					case Operation:
						diagram.getComm().changeOperationLevel(diagram.getID(), result.getObjectPath(), result.name, result.oldLevel, result.newLevel);
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
	
	public void changeOfDialog(FmmlxObject object) {
		Platform.runLater(() -> {
			ChangeOfDialog dlg = new ChangeOfDialog(diagram, object);
			Optional<ChangeOfDialog.Result> cod = dlg.showAndWait();

			if (cod.isPresent()) {
				final ChangeOfDialog.Result result = cod.get();
				diagram.getComm().changeOf(diagram.getID(), result.object.getName(), result.oldOfName, result.newOf.getName());
				diagram.updateDiagram();
			}
		});
	}
	
	public void changeOwnerDialog(FmmlxObject object, PropertyType type) {
		FmmlxProperty selectedProperty = diagram.getSelectedProperty();
		if (belongsPropertyToObject(object, selectedProperty, type)) {
			changeOwnerDialog(object, type, selectedProperty);
		} else {
			changeOwnerDialog(object, type, null);
		}
	}
	
	private <Property extends FmmlxProperty> void changeOwnerDialog(FmmlxObject object, PropertyType type, Property selectedProperty) {
		Platform.runLater(() -> {
			ChangeOwnerDialog<Property> dlg = new ChangeOwnerDialog<Property>(diagram, object, type);
			if (belongsPropertyToObject(object, selectedProperty, type)) {
				dlg.setSelected(selectedProperty);
			}

			Optional<ChangeOwnerDialog<Property>.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeOwnerDialog<Property>.Result result = opt.get();
				switch (result.type) {
					case Attribute:
						diagram.getComm().changeAttributeOwner(diagram.getID(), result.object.getName(), result.property.getName(), result.newOwner.name);
						break;
					case Operation:
						diagram.getComm().changeOperationOwner(diagram.getID(), result.object.getName(), result.property.getName(), result.newOwner.name);
						break;
					default:
						System.err.println("ChangeOwnerDialogResult: No matching content type!");
						break;
				}
				diagram.updateDiagram();
			}
		});
	}

	public void changeParentsDialog(FmmlxObject object) {
		Platform.runLater(() -> {
			ChangeParentDialog dlg = new ChangeParentDialog(diagram, object);
			Optional<ChangeParentDialog.Result> cpd = dlg.showAndWait();

			if (cpd.isPresent()) {
				ChangeParentDialog.Result result = cpd.get();
				diagram.getComm().changeParent(diagram.getID(), result.object.getName(), result.getCurrentParentNames(), result.getNewParentNames());
				diagram.updateDiagram();
			}
		});

	}

	public void changeSlotValue(FmmlxObject hitObject, FmmlxSlot hitProperty) {
		if(hitProperty != null && "Boolean".equals(hitProperty.getType(diagram))){
			diagram.getComm().changeSlotValue(diagram.getID(), hitObject.getName(), hitProperty.getName(), "true".equals(hitProperty.getValue())?"false":"true");
			diagram.updateDiagram();			
		} else {
			ChangeSlotValueDialog dlg = new ChangeSlotValueDialog(diagram, hitObject, hitProperty);
			Optional<ChangeSlotValueDialog.Result> result = dlg.showAndWait();

			if (result.isPresent()) {
				ChangeSlotValueDialog.Result slotValueDialogResult = result.get();
				diagram.getComm().changeSlotValue(diagram.getID(), slotValueDialogResult.object.getName(), slotValueDialogResult.slot.getName(), slotValueDialogResult.newValue);
				diagram.updateDiagram();
			}
		}
	}

	public void updateDiagram() {
		diagram.updateDiagram();
	}

	public void toggleAbstract(FmmlxObject object) {
		diagram.getComm().setClassAbstract(diagram.getID(), object.getName(), !object.isAbstract());
		diagram.updateDiagram();		
	}
	
	public void toggleSingleton(FmmlxObject object) {
		diagram.getComm().setClassSingleton(diagram.getID(), object.getName(), !object.isSingleton());
		diagram.updateDiagram();		
	}
	
	public void addOperationDialog(FmmlxObject object) {

		Platform.runLater(() -> {
			AddOperationDialog dlg = new AddOperationDialog(diagram, object);
			Optional<AddOperationDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddOperationDialog.Result result = opt.get();
				diagram.getComm().addOperation(diagram.getID(), result.object.getName(), result.level, result.body);
				diagram.updateDiagram();
			}
		});
	}
	
	public void changeBodyDialog(FmmlxObject object, FmmlxOperation initiallySelectedOperation) {
		if(initiallySelectedOperation == null) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText("This MenuItem requires an Operation to be selected.");
			alert.show(); return;
		} 
		Platform.runLater(() -> {
			AddOperationDialog dlg = new AddOperationDialog(diagram, object, initiallySelectedOperation);
			Optional<AddOperationDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddOperationDialog.Result result = opt.get();
				diagram.getComm().changeOperationBody(diagram.getID(), result.object.getName(), result.name, result.body);
				diagram.updateDiagram();
			}
		});
	}
	
	public void addConstraintDialog(FmmlxObject object) {

		Platform.runLater(() -> {
			AddConstraintDialog dlg = new AddConstraintDialog(diagram, object);
			Optional<AddConstraintDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddConstraintDialog.Result result = opt.get();
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
		Optional<AddConstraintDialog.Result> opt = dlg.showAndWait();
		if(opt.isPresent()) {
			final AddConstraintDialog.Result result = opt.get();
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

//	public <Property extends FmmlxProperty> void changeTypeDialog(FmmlxObject object, PropertyType type) {
//		changeTypeDialog(object, type, diagram.getSelectedProperty());
//	}
	
    public <Property extends FmmlxProperty> void changeTypeDialog(FmmlxObject object, PropertyType type, Property selectedProperty, Vector<Property> availableProperties) {

		Platform.runLater(() -> {
			ChangeTypeDialog<Property> dlg = new ChangeTypeDialog<Property>(object, type, availableProperties, selectedProperty);
			if (belongsPropertyToObject(object, selectedProperty, type)) {
				dlg.setSelected(selectedProperty);
			}
			Optional<ChangeTypeDialog<Property>.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeTypeDialog<?>.Result result = opt.get();

				switch (result.type) {
					case Attribute:
						diagram.getComm().changeAttributeType(diagram.getID(), result.object.getName(), result.property.getName(),
								result.oldType, result.newType);
						break;
					case Operation:
						diagram.getComm().changeOperationType(diagram.getID(), result.object.getName(), result.property.getName(),
								result.newType);
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

    // CURRENTLY UNUNSED
	public void changeTargetDialog(FmmlxObject object, PropertyType type) {
//		CountDownLatch latch = new CountDownLatch(1);

		Platform.runLater(() -> {
			ChangeTargetDialog dlg = new ChangeTargetDialog(object, type);
			Optional<ChangeTargetDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final ChangeTargetDialog.Result result = opt.get();
				diagram.getComm().changeAssociationTarget(diagram.getID(), result.getAssociationName(), result.oldTargetName, result.newTargetName);
				diagram.updateDiagram();
			}

//			latch.countDown();
		});
	}

	public void addAssociationDialog(FmmlxObject source, FmmlxObject target) {

		Platform.runLater(() -> {
			AssociationDialog dlg = new AssociationDialog(diagram, source, target, false);
			Optional<AssociationDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AssociationDialog.Result result = opt.get();
				diagram.getComm().addAssociation(diagram.getID(),
						result.source.getName(), result.target.getName(),
						result.newIdentifierSource, result.newIdentifierTarget,
						result.newDisplayName,
						result.assocType.path, result.multTargetToSource, result.multSourceToTarget,
						result.newInstLevelSource, result.newInstLevelSource, 
						result.newInstLevelTarget, result.newInstLevelTarget,
						result.sourceVisibleFromTarget,  result.targetVisibleFromSource, 
						result.symmetric, result.transitive,
						result.sourceGetterName, result.sourceSetterName,
						result.targetGetterName, result.targetSetterName);
				diagram.updateDiagram();
			}
		});
	}

	public void editAssociationDialog(final FmmlxAssociation association) {
		Platform.runLater(() -> {
			AssociationDialog dlg = new AssociationDialog(diagram, association, true);
			Optional<AssociationDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AssociationDialog.Result result = opt.get();
				
				if(result.selectedAssociation.isSourceVisible() != result.sourceVisibleFromTarget) {
					diagram.getComm().setAssociationEndVisibility(diagram.getID(), result.selectedAssociation, false, result.sourceVisibleFromTarget);				
				}
				if(result.selectedAssociation.isTargetVisible() != result.targetVisibleFromSource) {
					diagram.getComm().setAssociationEndVisibility(diagram.getID(), result.selectedAssociation, true, result.targetVisibleFromSource);				
				}
				
				if(!result.selectedAssociation.getAccessNameEndToStart().equals(result.newIdentifierSource)) {
					System.err.println("getAccessNameEndToStart:" + result.selectedAssociation.getAccessNameEndToStart() + "--> " + result.newIdentifierSource);
					diagram.getComm().changeAssociationStart2EndAccessName(diagram.getID(), result.selectedAssociation, result.newIdentifierSource);
				}
				if(!result.selectedAssociation.getAccessNameStartToEnd().equals(result.newIdentifierTarget)) {
					System.err.println("getAccessNameStartToEnd:" + result.selectedAssociation.getAccessNameStartToEnd() + "--> " + result.newIdentifierTarget);
					diagram.getComm().changeAssociationEnd2StartAccessName(diagram.getID(), result.selectedAssociation, result.newIdentifierTarget);
				}
				
				if(!result.selectedAssociation.getLevelSource().equals(result.newInstLevelSource)) {
					System.err.println("getLevelEndToStart:" + result.selectedAssociation.getLevelSource() + "--> " + result.newInstLevelSource);
					diagram.getComm().changeAssociationEnd2StartLevel(diagram.getID(), result.selectedAssociation, result.newInstLevelSource);
				}
				if(!result.selectedAssociation.getLevelTarget().equals(result.newInstLevelTarget)) {
					System.err.println("getLevelStartToEnd:" + result.selectedAssociation.getLevelTarget() + "--> " + result.newInstLevelTarget);
					diagram.getComm().changeAssociationStart2EndLevel(diagram.getID(), result.selectedAssociation, result.newInstLevelTarget);
				}
				
				if(!result.selectedAssociation.getMultiplicityEndToStart().equals(result.multTargetToSource)) {
					diagram.getComm().changeAssociationEnd2StartMultiplicity(diagram.getID(), result.selectedAssociation, result.multTargetToSource);
				}
				if(!result.selectedAssociation.getMultiplicityStartToEnd().equals(result.multSourceToTarget)) {
					diagram.getComm().changeAssociationStart2EndMultiplicity(diagram.getID(), result.selectedAssociation, result.multSourceToTarget);
				}
				
				if(!result.selectedAssociation.getName().equals(result.newDisplayName)) {
					System.err.println("changeName:" +result.selectedAssociation.getName()  + "-->" + result.newDisplayName);
					diagram.getComm().changeAssociationForwardName(diagram.getID(), result.selectedAssociation, result.newDisplayName);
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
			for(FmmlxObject o : diagram.getObjectsReadOnly()) {
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
		diagram.getComm().addDelegation(diagram.getID(), delegateFrom.getName(), delegateTo.getName(), delegateFrom.level.getMinLevel()-1);
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
				for(FmmlxObject o : diagram.getObjectsReadOnly()) {
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
				diagram.getComm().addLink(diagram.getID(), source.getName(), target.getName(), association.getAccessNameStartToEnd());
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
		Dialog<Void> dialog = new Dialog<Void>();
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
			    () -> sourceCB.getValue() == null || targetCB.getValue() == null,
			    sourceCB.valueProperty(),
			    targetCB.valueProperty()
			));
		Optional<Void> result= dialog.showAndWait();
		if (result.isPresent()) {
			diagram.getComm().addLink(diagram.getID(), sourceCB.getSelectionModel().getSelectedItem().getName(), targetCB.getSelectionModel().getSelectedItem().getName(), association.getAccessNameStartToEnd());
			diagram.updateDiagram();
		} else {
			dialog.close();
		}
	}
	
	public void removeAssociationInstance(FmmlxLink link) {
		diagram.getComm().removeAssociationInstance(diagram.getID(), 
				link.getAssociation().getAccessNameStartToEnd(), 
				link.getSourceNode().getName(), 
				link.getTargetNode().getName());
		diagram.updateDiagram();
	}

	public void removeAssociation(FmmlxAssociation association) {
		diagram.getComm().removeAssociation(diagram.getID(), association);
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

	public void assignToGlobalVariable(FmmlxObject object) {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Assign to Global Variable");
		dialog.setHeaderText("Global Variable Name:");
		 
		Optional<String> result = dialog.showAndWait();

		result.ifPresent(s -> diagram.getComm().assignToGlobal(diagram.getID(), object, s));
	}
	
	public void assignGlobalVariable() {
		Optional<FmmlxObject> result = showChooseFmmlxObjectsDialog("Choose Object to assign to Global Variable", false);
		if (result.isPresent()) {
			assignToGlobalVariable(result.get());
		}
	}

	public void showBody(FmmlxObject object, FmmlxOperation operation) {
		diagram.getComm().showBody(diagram, object, operation);
	}

	public void addMissingLink(FmmlxObject obj, FmmlxAssociation assoc) {
		Platform.runLater(() -> {
			AddMissingLinkDialog dlg = new AddMissingLinkDialog(obj, assoc);
			Optional<AddMissingLinkDialog.Result> solution = dlg.showAndWait();

			if(solution.isPresent()) {
				if(solution.get().createNew) {
					addInstanceDialog(solution.get().selection, diagram.getActiveDiagramViewPane());
				} else {
					diagram.getComm().addLink(diagram.getID(), obj.getName(), solution.get().selection.getName(), assoc.getAccessNameStartToEnd());
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

	public void openFindImplementationDialog() {

		Platform.runLater(() -> {
			FindImplementationDialog dlg = new FindImplementationDialog(diagram, diagram.getComm());
			dlg.showAndWait();
		});
	}

	public Object openFindClassDialog() {
		Platform.runLater(() -> {
			FindClassDialog dialog = new FindClassDialog(diagram, diagram.getComm());
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

	// LM, 07.04.2023, New Action for execution of custom UI
	public void executeUI(FmmlxObject object) {
		
		Platform.runLater(() -> new CustomUI(diagram, object));
			
	}
	// End customUI
	
		
	public void showObjectBrowser(FmmlxObject object) {
			
		Platform.runLater(() -> new ObjectBrowser(diagram, object).show());
			
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
				FmmlxDiagram diagram2 = (FmmlxDiagram) diagram;
				Bounds bounds = diagram2.getBounds();
				double extraHeight = getExtraHeight();
				SvgExporter svgExporter;
				try {
					svgExporter = new SvgExporter(filePath, bounds,extraHeight);
					svgExporter.export(diagram, extraHeight);
				} catch (TransformerException | ParserConfigurationException e) {
					e.printStackTrace();
				}
			});
		}
		});
	}
	
	public void exportPNG() {
		
		DiagramViewPane mainViewPane = ((FmmlxDiagram) diagram).getActiveDiagramViewPane();
		
		mainViewPane.setMaxZoom();

	    FileChooser fileChooser = new FileChooser();

	    String initalDirectory = PropertyManager.getProperty("fileDialogPath", "");
	    if (!initalDirectory.equals("")) {
	    	File dir = new File(initalDirectory);
    		if(dir.exists()) fileChooser.setInitialDirectory(dir);
    	}

    	fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG","*.png"));
    	fileChooser.setTitle("Save Diagram to PNG");
	    File file = fileChooser.showSaveDialog(XModeler.getStage());

	    if(file != null){
	        WritableImage wi = new WritableImage((int)mainViewPane.canvas.getWidth(),(int)mainViewPane.canvas.getHeight());
	        try {
	        	ImageIO.write(SwingFXUtils.fromFXImage(mainViewPane.canvas.snapshot(null,wi),null),"png",file);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	private double getExtraHeight() {
		return (diagram.issues.size()+1) * 14;
	}

	public void showCertainLevel() {
		Platform.runLater(() ->{
			ShowCertainLevelDialog dlg = new ShowCertainLevelDialog(diagram);
			Optional<Vector<Integer>> result = dlg.showAndWait();

			if(result.isPresent()){
				final Vector<Integer> chosenLevel = result.get();
//				Vector<Integer> chosenLevel = sclResult.getChosenLevels();

				Vector<FmmlxObject> objects = diagram.getObjectsReadOnly();
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
			hide(diagram.getObjectsReadOnly(), false);
			updateDiagram();
		});
	}

	public void mergeModels() {
		Platform.runLater(() ->{
			FileChooser fc = new FileChooser();
			fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("xml", "*.xml"));
			fc.setTitle("choose File");
			File file = fc.showOpenDialog(XModeler.getStage());

			if(file!= null){
				//if you want to run this, you have to reimplement an FMMLxImporter
				//FMMLxImporter importer = new FMMLxImporter(file.getPath(), diagram);
				//importer.handleLogs();
			}
			updateDiagram();
		});
	}

	public void runOperation(String path, String opName) {
		try {
			diagram.getComm().runOperation(diagram.diagramID, path+"."+opName+"()");
			updateDiagram();
		} catch (TimeOutException e) {
			throw new RuntimeException("runOperation failed", e);
		}	
	}

	public void classify(Vector<FmmlxObject> objs) {
		String m = "";
		if(objs.size() == 1) {
			m = "Meta" + objs.get(0).getName();
		} else { for(int i = 0; i < objs.size(); i++) {
			int a = (int) (objs.get(i).getName().length() * 1. * i / objs.size());
			int b = -(int) (-objs.get(i).getName().length() * 1. * (i+1) / objs.size());
//			System.err.println(objs.get(i).getName()+","+a+","+b);
			m += objs.get(i).getName().substring(a,b);
		}}
		TextInputDialog dialog = new TextInputDialog(m);

		dialog.setTitle("Classify Elements");
		dialog.setHeaderText("New class requires a unique and valid name:");
		dialog.setContentText("Name:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			diagram.getComm().classify(diagram.diagramID, objs, result.get());
//		    this.label.setText(name);
		});
		
	}

	public void openMergePropertiesDialog(FmmlxObject mergeIntoClass) {
		MergePropertyDialog dialog = new MergePropertyDialog(mergeIntoClass, diagram);
		Optional<MergePropertyDialog.Result> result = dialog.showAndWait();
		if(result.isPresent()) {
			diagram.getComm().mergeProperties(mergeIntoClass, result.get().createMessage());
			diagram.updateDiagram();
		}
	}
	public void showUnhideElementsDialog() {
		new UnhideElementsDialog(diagram).showDialog();
	}

	public void openInstanceWizard(FmmlxObject theClass, DiagramViewPane view) {
		InstanceWizard wizard = new InstanceWizard(diagram, theClass, theClass.getLevel().getMinLevel()-1);
		System.err.println("showing Wizard...");
		wizard.showAndWait();
	}
	
	public void centerViewOnObject() {
		DiagramViewPane viewPane = ((FmmlxDiagram)diagram).getActiveDiagramViewPane();
		
		String dialogTitle = "Center view on specific Object";
		Optional<FmmlxObject> result = showChooseFmmlxObjectsDialog(dialogTitle, true);
		if (result.isPresent()) {
			viewPane.centerObject(result.get());
		}
	}
	
	public Optional<FmmlxObject> showChooseFmmlxObjectsDialog(String dialogTitle, boolean showVisible) {
		ChoiceDialog<FmmlxObject> dialog;
		if (showVisible) {
			Vector<FmmlxObject> visibleObjects = diagram.getVisibleObjectsReadOnly();			
			dialog = new ChoiceDialog<FmmlxObject>(null, visibleObjects);			
		} else {
			Vector<FmmlxObject> objects = diagram.getObjectsReadOnly();			
			dialog = new ChoiceDialog<FmmlxObject>(null, objects);
		}
		dialog.setTitle(dialogTitle);
		dialog.setContentText("Choose your FmmlxObject: ");
		Optional<FmmlxObject> result = dialog.showAndWait();
		return result;
	}

//	public void removeDelegation(String delegatorPath, String delegateePath) {
//		diagram.getComm().removeDelegation(diagram.diagramID, delegatorPath);
//	}
	
	public void undo() {
		diagram.getComm().undo(diagram.diagramID);	
	}
	
	public void redo() {
		diagram.getComm().redo(diagram.diagramID);
	}

	public void generateGetter(FmmlxObject object, FmmlxAttribute attribute) {
		Platform.runLater(() -> {
			AddOperationDialog dlg = new AddOperationDialog(diagram, object);
			dlg.initAttributeSetter(attribute);
			Optional<AddOperationDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddOperationDialog.Result result = opt.get();
				diagram.getComm().addOperation(diagram.getID(), result.object.getName(), result.level, result.body);
				diagram.updateDiagram();
			}
		});	
	}	
	
	public void generateSetter(FmmlxObject object, FmmlxAttribute attribute) {
		throw new RuntimeException("Not yet implemented!");
	}

	public void generateAssocGetter(
			FmmlxObject object, 
			FmmlxObject otherObject, 
			String endName, 
			Integer endInstLevel,
			Multiplicity endMult) {
		
		Platform.runLater(() -> {
			AddOperationDialog dlg = new AddOperationDialog(diagram, object);
			dlg.initAssociationSetter(endName, endInstLevel, otherObject.ownPath, endMult);
			Optional<AddOperationDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddOperationDialog.Result result = opt.get();
				diagram.getComm().addOperation(diagram.getID(), result.object.getName(), result.level, result.body);
				diagram.updateDiagram();
			}
		});	
	}

	public void associationTypeDialog(AssociationType oldType) {
		Platform.runLater(() -> {
			AssociationTypeDialog atd = new AssociationTypeDialog(oldType);
			Optional<AssociationType> opt = atd.showAndWait();
			
			if (opt.isPresent()) {
				final AssociationType result = opt.get();
				diagram.getComm().addAssociationType(diagram.getID(),
					result,
					xmfReturn -> {
						if(xmfReturn == null) {
							diagram.updateDiagram();
						} else {
							associationTypeDialog(xmfReturn);
						}						
					});
			}
		});
	}

}
