package tool.clients.fmmlxdiagrams;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

import javax.management.MBeanOperationInfo;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import tool.clients.customui.CustomUI;
import tool.clients.customui.FXMLExporter;
import tool.clients.dialogs.enquiries.FindClassDialog;
import tool.clients.dialogs.enquiries.FindImplementationDialog;
import tool.clients.dialogs.enquiries.FindSendersOfMessages;
import tool.clients.fmmlxdiagrams.FmmlxDiagram.DiagramViewPane;
import tool.clients.fmmlxdiagrams.classbrowser.ClassBrowserClient;
import tool.clients.fmmlxdiagrams.classbrowser.ObjectBrowser;
import tool.clients.fmmlxdiagrams.dialogs.*;
import tool.clients.fmmlxdiagrams.dialogs.shared.*;
import tool.clients.fmmlxdiagrams.graphics.SvgExporter;
import tool.clients.fmmlxdiagrams.graphics.View;
import tool.clients.fmmlxdiagrams.instancegenerator.InstanceGenerator;
import tool.clients.fmmlxdiagrams.instancegenerator.valuegenerator.IValueGenerator;
import tool.clients.fmmlxdiagrams.instancewizard.InstanceWizard;
import tool.clients.importer.FMMLxImporter;
import tool.clients.serializer.FmmlxSerializer;
import tool.xmodeler.XModeler;

public class DiagramActions {

	private final AbstractPackageViewer diagram;
	private HashMap<String, Map<String, String>> customGUIslotValues;

	public AbstractPackageViewer getDiagram() {
		return diagram;
	}

	public DiagramActions(AbstractPackageViewer diagram) {
		this.diagram = diagram;
		this.customGUIslotValues = new HashMap<>();
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
							mcdResult.isAbstract, 0, 0, true);
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
						mcdResult.level, mcdResult.getParentNames(), mcdResult.isAbstract, 
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
                            aidResult.getParentNames(), aidResult.isAbstract, 0, 0, true);
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
                        (int) (p.getX()+.5), (int) (p.getY()+.5), false);
				diagram.updateDiagram();
			}
		});
	}
	
	// FH Method for adding Instances without dialog that are automatically hidden
		public String addInstance(String className, String instanceName) {
			Vector<String> parents = new Vector<String>();
			diagram.getComm().addNewInstance(this.diagram.getID(), className, instanceName, 0, parents, false, 0, 0, false);
			return instanceName;

		}
		// adding instances that can be shown
		public String addInstance(String className, String instanceName, boolean hidden) {
			Vector<String> parents = new Vector<String>();
			diagram.getComm().addNewInstance(this.diagram.getID(), className, instanceName, 0, parents, false, 0, 0, hidden);
			return instanceName;
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
						diagram.getComm().removeAssociation(diagram.getID(), result.object.getName(), 0);
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
						diagram.getComm().changeClassLevel(diagram.getID(), result.getObjectName(), result.newLevel);
						break;
					case Attribute:
						diagram.getComm().changeAttributeLevel(diagram.getID(), result.getObjectName(), result.name, result.oldLevel, result.newLevel);
						break;
					case Operation:
						diagram.getComm().changeOperationLevel(diagram.getID(), result.getObjectName(), result.name, result.oldLevel, result.newLevel);
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
			Optional<AddOperationDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AddOperationDialog.Result result = opt.get();
				diagram.getComm().addOperation2(diagram.getID(), result.object.getName(), result.level, result.body);
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
			Optional<AssociationDialog.Result> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				final AssociationDialog.Result result = opt.get();
				
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
					System.err.println("changeName:" +result.selectedAssociation.getName()  + "-->" + result.newDisplayName);
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

	public void addMissingLink(FmmlxObject obj, FmmlxAssociation assoc) {
		Platform.runLater(() -> {
			AddMissingLinkDialog dlg = new AddMissingLinkDialog(obj, assoc);
			Optional<AddMissingLinkDialog.Result> solution = dlg.showAndWait();

			if(solution.isPresent()) {
				if(solution.get().createNew) {
					addInstanceDialog(solution.get().selection, diagram.getActiveView());
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
	public void showUnhideElementsDialog(AbstractPackageViewer dialog) {
		new UnhideElementsDialog(dialog).showDialog();
	}

	public void openInstanceWizard(FmmlxObject theClass, DiagramViewPane view) {
		InstanceWizard wizard = new InstanceWizard(diagram, theClass, theClass.getLevel()-1);
		System.err.println("showing Wizard...");
		wizard.showAndWait();
	}
	
	// F.H. instantiate StandardGUI from given Domain Model
	public void instantiateGUI(FmmlxObject object) {
		// instantiate CustomGUI instances
		Vector<CanvasElement> vector = this.diagram.getSelectedObjects();
		this.customGUIslotValues.clear();
		customGUIslotValues = this.instantiateCustomGUI(vector);
		this.diagram.updateDiagram();
		
		// fill slots after diagram has updated
		// 100 ms seems to be enough
		new java.util.Timer().schedule(new java.util.TimerTask() {
		@Override
			public void run() {
				addSlotValuesCustomGUI(customGUIslotValues);
				diagram.updateDiagram();
			} 
		}, 100);
	}

	public HashMap<String, Map<String, String>> instantiateCustomGUI(Vector <CanvasElement> selectedObjects) {
		// TODO rename classes names to new names by luca
		// TODO better error handling if image is missing
		
		// name of gui
		String guiName = "";
		
		// for the fxml export
		int rowCount = 0;
		
		// instance of customGUI
		String guiInstanceName = addInstance("UserInterface", "gui" + UUID.randomUUID().toString().replace("-", ""), false);

		// objects for customGUI
		FmmlxObject object;
		Vector<FmmlxObject> objectsCommonClass = new Vector<FmmlxObject>();
		
		String assocName="";
		
		String referenceInstanceName;
		String injectionInstanceName;
		String actionInstanceName;
		String virtualInstanceName;
		String parameterInstanceName;
		
		Vector<FmmlxAttribute> attributes;
		Vector<FmmlxOperation> operations;
		
		String multiplicity;
		char endChar;
		
		// Slot Values that can already be determined in this method should be saved for performance
		// first String		-> 	instanceName
		// second String 	->	slotName
		// third String		->	value
		HashMap<String, Map<String, String>> slotValues = new HashMap<String, Map<String, String>>();
		HashMap<String, String> helper = new HashMap<String, String>();
		
		// map for is parent 1. entry -> parent 2. -> child
		// Referenznamen, bis diese aufgelöst werden können
		HashMap<String, String> isChildAssocs = new HashMap<>();
		HashMap<String, String> commonClassReferenceMap = new HashMap<>();
		
		// used if the commonClass needs a listInjection or not
		Boolean isList;
		String isHead="";
		boolean isActionInjection;
		
		for (CanvasElement element : selectedObjects) {
			 object = (FmmlxObject) element;
			 if (object.getMetaClassName().equals("CommonClass")) objectsCommonClass.add(object);
		}

		// get associations that are mapped
		FmmlxAssociation associationDerivedFrom = this.diagram.getAssociationByPath(diagram.getPackagePath() + "::derivedFrom");
		FmmlxAssociation associationComposedOf = this.diagram.getAssociationByPath(diagram.getPackagePath() + "::composedOf");
		FmmlxAssociation associationRefersToStateOf = this.diagram.getAssociationByPath(diagram.getPackagePath() + "::refersToStateOf");
		FmmlxAssociation associationIsParent = this.diagram.getAssociationByPath(diagram.getPackagePath() + "::isParent");
		FmmlxAssociation associationIsChild = this.diagram.getAssociationByPath(diagram.getPackagePath() + "::isChild");
		FmmlxAssociation associationUses = this.diagram.getAssociationByPath(diagram.getPackagePath() + "::uses");
		FmmlxAssociation associationRepresentedAs = this.diagram.getAssociationByPath(diagram.getPackagePath() + "::representedAs");

		// create standard GUI
		GridPane rechteSeiteGrid = new GridPane();
		rechteSeiteGrid.setHgap(3);
		rechteSeiteGrid.setVgap(3);
		rechteSeiteGrid.setPadding(new Insets(3, 3, 3, 3));
	
		// instantiate references and injections for domain classes
		for (FmmlxObject o : objectsCommonClass) {
			
			isHead = "false";
			isList = false;
			isActionInjection = false;
			assocName = "";
			
			// create reference
			referenceInstanceName = addInstance("Reference", "ref" + UUID.randomUUID().toString().replace("-", ""));
			
			// reference mapping
			commonClassReferenceMap.put(o.getName(), referenceInstanceName);

			// find associations and head
			
			// ANNAHME: Jede CommonClass hat nur eine Assoziation die "eingehend" ist. 
			// Diese bildet die Grundlage für die isHead Beziehung und die Assozioation in der Referenz
			// TODO Was ist wenn zyklische Beziehung
			
			for (FmmlxAssociation assoc : o.getAllRelatedAssociations()){	
				
				// association needs to be "inside" the selected objects
				if (assoc.getTargetNode().equals(o) && selectedObjects.contains(assoc.getSourceNode())) {

					assocName = assoc.getName();
				
					// add reference to map
					isChildAssocs.put(referenceInstanceName, assoc.getSourceNode().getName());
					
					// check if multiplicity > 1 then list
					multiplicity = assoc.getMultiplicityStartToEnd().toString();
					endChar = multiplicity.charAt(multiplicity.length() - 1);
					
					if (endChar == '*') {
						isList = true;
					}else  {
						int a = Character.getNumericValue(endChar);
						isList = (a>1) ? true: false;
					}
					
					// to avoid analyzing multiple associations in the same clas
					continue;
				}
			}
			
			
			// if gui is only one class then it is automatically head
			if (objectsCommonClass.size() == 1) isHead = "true";
			// if no further associations are outgoing it is head
			if (assocName.equals("")) isHead="true";
			
			
			// if head --> listInjection needed
			// head defines name for gui
			if (isHead.equals("true")) {
				isList = true;
				guiName = o.getName() + "CustomUI";
			}
			
			helper.put("associationName",assocName);
			helper.put("isHead", isHead);
			slotValues.put(referenceInstanceName, (Map<String, String>) helper.clone());
			helper.clear();
			
			// List Injection
			if (isList) {
				injectionInstanceName = addInstance("ListInjection", "list" + UUID.randomUUID().toString().replace("-", ""));
				addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());
				addAssociation(injectionInstanceName, referenceInstanceName, associationDerivedFrom.getName());
				helper.put("isListView", "true");
				helper.put("nameOfModelElement", o.getName());
				helper.put("idOfUIElement", injectionInstanceName);
				slotValues.put(injectionInstanceName, (Map<String, String>) helper.clone());
				helper.clear();
				
				// add to standardGUI
				Label instancesOfClassLabel = new Label("Instances of "+ o.getName());
				instancesOfClassLabel.setFont(Font.font(Font.getDefault().getName(), FontWeight.BOLD, FontPosture.REGULAR, Font.getDefault().getSize()));
				rechteSeiteGrid.add(instancesOfClassLabel, 0, rowCount++);
				
				ListView<String> objectListView = new ListView<>();
				objectListView.setId(injectionInstanceName);
				rechteSeiteGrid.add(objectListView, 0, rowCount++);
			}
			

			
			// add link "refersToStateOf" -> Reference + CommonClassInstance
			// ANNAHME: Jede gewählte CommonClass hat mind. 1 Instanz. 
			// TODO: Prio 3 -> Wie damit umgehen, wenn keine Instanz existiert
			addAssociation(referenceInstanceName, o.getInstances().get(0).getName(), associationRefersToStateOf.getName());
			
			attributes = o.getAllAttributes();
			operations = o.getAllOperations();

			// Standard GUI
			if (attributes.size() > 0){
				rechteSeiteGrid.add(new Label("Slots:"), 0, rowCount++);
			}
			
			
			// add slotInjections for slots
			for (FmmlxAttribute attribute : attributes) {
				
				// add instance
				injectionInstanceName = addInstance("SlotInjection", "slot" + UUID.randomUUID().toString().replace("-", ""));
				
				helper.put("idOfUIElement", injectionInstanceName);
				helper.put("nameOfModelElement", attribute.getName());
				slotValues.put(injectionInstanceName, (Map<String, String>) helper.clone());
				helper.clear();
				
				// add associations
				addAssociation(injectionInstanceName, referenceInstanceName, associationDerivedFrom.getName());
				addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());

				// Standard GUI
				TextField valueTextField = new TextField(attribute.getName());
				valueTextField.setId(injectionInstanceName);
				Label slotName = new Label(attribute.getName());
				
				rechteSeiteGrid.add(slotName, 0, rowCount);
				rechteSeiteGrid.add(valueTextField, 1, rowCount);
				rowCount++;
			}
			
			// Standard GUI
			if (operations.size()>0) {
				rechteSeiteGrid.add(new Label("Operations:"), 0, rowCount++);
			}
			

			// add actionInjections for operations
			for (FmmlxOperation operation : operations) {
				
				// if method is monitor than action; otherwise actionInjection
				// TODO: maybe find something better more robust approach
				String body = operation.getBody();
				isActionInjection = body.contains("monitor=true") ? true: false;
				
				helper.put("nameOfModelElement", operation.getName());
				
				// if action
				if (isActionInjection) {
					injectionInstanceName = addInstance("ActionInjection", "actInj" + UUID.randomUUID().toString().replace("-", ""));
					helper.put("idOfUIElement", injectionInstanceName);
					
					slotValues.put(injectionInstanceName, (Map<String, String>) helper.clone());
					helper.clear();
					
					// add associations
					addAssociation(injectionInstanceName, referenceInstanceName, associationDerivedFrom.getName());
					addAssociation(injectionInstanceName, guiInstanceName, associationComposedOf.getName());
					
					//standard GUI
					Label actionValue = new Label("");
					actionValue.setId(injectionInstanceName);

					rechteSeiteGrid.add(new Label(operation.getName()), 0, rowCount);
					rechteSeiteGrid.add(actionValue, 1, rowCount);
					rowCount++;
					
				}else {
					actionInstanceName = addInstance("Action", "act" + UUID.randomUUID().toString().replace("-", ""));
					helper.put("idOfUIElement", actionInstanceName);
					
					slotValues.put(actionInstanceName, (Map<String, String>) helper.clone());
					helper.clear();
					
					// add associations
					addAssociation(actionInstanceName, referenceInstanceName, associationDerivedFrom.getName());
					
					int paramCounter = 0;
					
					//standard GUI
					Button wertAendern = new Button(operation.getName());
					rechteSeiteGrid.add(wertAendern, 1, rowCount);
					wertAendern.setId(actionInstanceName);
					rowCount++;
						
						
					// parameters
					for (String paramType : operation.getParamTypes()) {
						paramCounter ++;
						parameterInstanceName = addInstance("Parameter", "par" + UUID.randomUUID().toString().replace("-", ""));
						addAssociation(parameterInstanceName, actionInstanceName, associationUses.getName());
						
						paramType = paramType.substring(paramType.lastIndexOf("::")+2);

						helper.put("dataType",paramType);
						helper.put("orderNo", String.valueOf(paramCounter));
						helper.put("value","");
						slotValues.put(parameterInstanceName, (Map<String, String>) helper.clone());
						helper.clear();
						
						
						
						// virtual for every parameter
						virtualInstanceName = addInstance("Virtual", "vir" + UUID.randomUUID().toString().replace("-", ""));
						helper.put("idOfUIElement", virtualInstanceName);
						slotValues.put(virtualInstanceName, (Map<String, String>) helper.clone());
						helper.clear();
						addAssociation(virtualInstanceName, parameterInstanceName, associationRepresentedAs.getName());
						
						//standard GUI
						TextField valueTextField = new TextField(operation.getName());
						valueTextField.setId(virtualInstanceName);

						rechteSeiteGrid.add(new Label(operation.getName()), 0, rowCount);
						rechteSeiteGrid.add(valueTextField, 1, rowCount);
						rowCount++;
					
					}
				}
			}
			
			
		} // end of for for objects

		// build references correctly
		for (Entry<String, String> entryIsChild : isChildAssocs.entrySet()) {
			if (!entryIsChild.getValue().startsWith("ref")) {
				isChildAssocs.replace(entryIsChild.getKey(), commonClassReferenceMap.get(entryIsChild.getValue()));
			}			
		}
		
				
		// now the map isChild is filled with the names of the references needed to map if there are any isChild associations
		if (selectedObjects.size()>1) {
			for (Entry<String, String> entry : isChildAssocs.entrySet()) {
				// add ischild
				addAssociation(entry.getKey(), entry.getValue(), associationIsParent.getName());
				// add isparent in other direction
				addAssociation(entry.getValue(), entry.getKey(), associationIsChild.getName()); 
			}
		}
		

		// export standard gui
		// File picker as save dialogue
		// TODO: add better instantiation dialog which includes the save option
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select location for saving the GUI extraction");
        fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("JavaFX as XML", "*.fxml"),
		        new ExtensionFilter("All Files", "*.*"));
        
        ScrollPane defaultGUIPane = new ScrollPane();
        
        Scene scene = new Scene(defaultGUIPane);
		Stage stage = new Stage();
		stage.setScene(scene);
		
		stage.setTitle("Standard GUI");
		stage.setWidth(800);
		stage.setHeight(400);
		
		
        File file = fileChooser.showSaveDialog(stage);
	
		if (file != null) {
			FXMLExporter exporter = new FXMLExporter(file.getPath());
			exporter.export(rechteSeiteGrid);
		}
		
		
		helper.put("pathToFXML", file.getPath());
		helper.put("titleOfUI", guiName);
		//helper.put("pathToIconOfWindow", "");
		
		slotValues.put(guiInstanceName, (Map<String, String>) helper.clone());
		helper.clear();
		
		return slotValues;
	}

	private void addAssociation(String instanceName, String instance2Name, String assocName) {
		if (instanceName!=null && instance2Name!=null && assocName!=null) {
			this.diagram.comm.addAssociationInstance(this.diagram.diagramID, instanceName, instance2Name, assocName);
		} else {
			System.err.println("Association cannot be instantiated, because one of the parameters is null");
		}
		
	}

	public void addSlotValuesCustomGUI(HashMap<String, Map<String, String>> slotValues) {
		
		
		Vector <FmmlxObject> references = new Vector<>();
		Vector <FmmlxObject> slotInjections = new Vector<>();
		Vector <FmmlxObject> actionInjections = new Vector<>();
		Vector <FmmlxObject> listInjections = new Vector<>();
		Vector <FmmlxObject> parameters = new Vector<>();
		Vector <FmmlxObject> virtuals = new Vector<>();
		Vector <FmmlxObject> actions = new Vector<>();
		Vector <FmmlxObject> customGuiInterface = new Vector<>();
		
		Vector <FmmlxObject> objects = this.diagram.getObjects();
		
		for (FmmlxObject o : objects) {
			
			if (o.getMetaClassName().equals("Reference")) references.add(o);
			if (o.getMetaClassName().equals("SlotInjection")) slotInjections.add(o);
			if (o.getMetaClassName().equals("ActionInjection")) actionInjections.add(o);
			if (o.getMetaClassName().equals("ListInjection")) listInjections.add(o);
			if (o.getMetaClassName().equals("Action")) actions.add(o);
			if (o.getMetaClassName().equals("Parameter")) parameters.add(o);
			if (o.getMetaClassName().equals("Virtual")) virtuals.add(o); 
			if (o.getMetaClassName().equals("UserInterface")) customGuiInterface.add(o); 
			
		}
		
		// TODO	Füllen von Attributen
		// CustomGui	->	pathTOIconOfWindow, titleOfUI <- automatisch machbar -- sinnvoll?

		for (FmmlxObject o : customGuiInterface) {
			if (slotValues.containsKey(o.getName())){
				String path = slotValues.get(o.getName()).get("pathToFXML");
				path = path.replace("\\", "\\\\");
				
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "pathToFXML",
						"\"" + path + "\"");
				
				
				// auto icon
				String pathIcon = "C:\\\\Users\\\\fhend\\\\OneDrive\\\\Desktop\\\\XModelerIconUndGUI\\\\invoice.png";
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "pathToIconOfWindow",
						"\"" + pathIcon + "\"");
				
				String UIName = slotValues.get(o.getName()).get("titleOfUI");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "titleOfUI","\"" + UIName + "\"");
			}
		}
		
		for (FmmlxObject o : slotInjections) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "idOfUIElement", "\"" + slotValues.get(o.getName()).get("idOfUIElement") + "\"");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "nameOfModelElement",
						"\"" + slotValues.get(o.getName()).get("nameOfModelElement") + "\"");
			}
		}
		
		for (FmmlxObject o: references) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "associationName", "\"" + slotValues.get(o.getName()).get("associationName") + "\"");
				
				if (slotValues.get(o.getName()).get("isHead").equals("true")) {
					diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "isHead", "true");
				}else {
					diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "isHead", "false");
				}
			}
		}
		
		for (FmmlxObject o : actionInjections) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "eventName", "\""+ "ACTION" +"\"");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "idOfUIElement", "\""+ slotValues.get(o.getName()).get("idOfUIElement") +"\"");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "nameOfModelElement",
						"\"" + slotValues.get(o.getName()).get("nameOfModelElement") + "\"");
			}
			
		}
		
		for (FmmlxObject o : listInjections) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "idOfUIElement", "\""+ slotValues.get(o.getName()).get("idOfUIElement")+"\"");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "nameOfModelElement",
						"\"" + slotValues.get(o.getName()).get("nameOfModelElement") + "\"");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "isListView", "true");
			}
		}
		
		for (FmmlxObject o : actions) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "eventName", "\""+ "ACTION" +"\"");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "idOfUIElement", "\""+ slotValues.get(o.getName()).get("idOfUIElement") +"\"");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "nameOfModelElement",
						"\"" + slotValues.get(o.getName()).get("nameOfModelElement") + "\"");
			}
			
		}
		
		for (FmmlxObject o : virtuals) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "idOfUIElement", "\""+ slotValues.get(o.getName()).get("idOfUIElement") +"\"");
			}	
		}
		
		
		for (FmmlxObject o : parameters) {
			if (slotValues.containsKey(o.getName())) {
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "orderNo", slotValues.get(o.getName()).get("orderNo").toString());
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "dataType", "\""+ slotValues.get(o.getName()).get("dataType").toString() + "\"");
				diagram.getComm().changeSlotValue(this.diagram.getID(), o.getName(), "value", "\"\"");
			}
			
		}
		
	}
}
