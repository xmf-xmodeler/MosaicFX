package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import tool.clients.fmmlxdiagrams.*;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer.ViewerStatus;
import tool.clients.fmmlxdiagrams.LevelColorScheme.FixedBlueLevelColorScheme;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.menus.BrowserAssociationContextMenu;
import tool.clients.fmmlxdiagrams.menus.BrowserAttributeContextMenu;
import tool.clients.fmmlxdiagrams.menus.BrowserOperationContextMenu;
import tool.xmodeler.XModeler;

public final class ModelBrowser extends CustomStage {
	
	private final CodeBox operationCodeArea = new CodeBox(10,true,"");
	private final CodeBox constraintBodyArea = new CodeBox(10,false,"");
	private final CodeBox constraintReasonArea = new CodeBox(10,false,"");
	private final CodeBox issueArea = new CodeBox(10,false,"");
	private final ListView<String> modelListView   = new ListView<>();
	private final ListView<String> parentsListView = new ListView<>();
	private final ListView<Issue>  issueListView   = new ListView<>();
	private final ListView<Constraint> constraintListView = new ListView<>();
	private final ListView<FmmlxObject> linkedObjectsListView = new ListView<>();
	private final ListView<FmmlxOperationValue> operationValueListView = new ListView<>();
	private final ListView<FmmlxAssociation> fmmlxAssociationListView = new ListView<>();
	private final ListView<FmmlxAssociation> linksListView = new ListView<>(); 
	private final ListView<FmmlxOperation> fmmlxOperationListView = new ListView<>();
	private final ListView<FmmlxObject> fmmlxObjectListView = new ListView<>();
	private final ListView<FmmlxAttribute> fmmlxAttributeListView = new ListView<>();
	private final ListView<FmmlxSlot> slotListView = new ListView<>();

	private final Button opCodeButton = new Button("Commit");
	private final Button conCodeButton = new Button("Commit");
	
	private CheckBox abstractCheckBox = new CheckBox(); 
//	private CheckBox targetVisible    = new CheckBox(); 
//	private CheckBox sourceVisible    = new CheckBox(); 
//	private CheckBox checkTransitive  = new CheckBox(); 
//	private CheckBox checkSymmetric   = new CheckBox(); 
	
	private TextField metaClassTextField, delegatesToTextField, operationInputTextField, operationOutputTexField,
						attributeBrowserTextField;
	private HBox associationBooleanAttributesHBox, visibleHBox;
	private TabPane codeAndConstraintTabPane;
	private Tab operationTab = new Tab("Operation");
	private Tab constraintTab = new Tab("Constraint");
	private Tab issueTab = new Tab("Issue");
	FmmlxDiagramCommunicator communicator;
	private AbstractPackageViewer activePackage;
	Label statusLabel = new Label("Status: not initialized");
	
	private HashMap<String,AbstractPackageViewer> models = new HashMap<>();
	private FixedBlueLevelColorScheme levelColorScheme;
	
	public ModelBrowser(String project, String initialModel, ObservableList<String> models) {
		super(StringValue.LabelAndHeaderTitle.modelBrowser+" " + project, XModeler.getStage(), 1500, 800);
		System.err.println("ModelBrowser ("+initialModel+") start");
		communicator = FmmlxDiagramCommunicator.getCommunicator();
		
		initElements();
		addSelectionListeners();
		addCellFactories();
		addDoubleClickListeners();
		SplitPane outerSplitPane = layoutElements();		
		
		System.err.println("ModelBrowser ("+initialModel+") I");
		
		getContainer().getChildren().addAll(outerSplitPane);

		setOnCloseRequest(e -> onClose());
		
		modelListView.getItems().clear();
		modelListView.getItems().addAll(models);
		modelListView.getSelectionModel().clearSelection();
		if (initialModel!=null) {
			System.err.println("ModelBrowser ("+initialModel+") IIa");
			modelListView.getSelectionModel().select(initialModel);
			System.err.println("ModelBrowser ("+initialModel+") IIb");
		}

		fmmlxObjectListView.setContextMenu(new BrowserObjectContextMenu());
		System.err.println("ModelBrowser ("+initialModel+") IIc");
	}

	public void onClose() {
		clearAll(ClearSelectionMode.MODEL);
		for (String key:models.keySet()) {
			communicator.close(models.get(key), false);
		}
		hide();
	}

	private void clearAll(ClearSelectionMode mode) {	
		if (mode == ClearSelectionMode.MODEL) {
			fmmlxObjectListView.getItems().clear();
			metaClassTextField.setText(StringValue.LabelAndHeaderTitle.empty);
		}
		
		if (mode == ClearSelectionMode.OBJECT || mode == ClearSelectionMode.MODEL) {
			fmmlxAttributeListView.getItems().clear();
			slotListView.getItems().clear();
			fmmlxOperationListView.getItems().clear();
			fmmlxAssociationListView.getItems().clear();
			parentsListView.getItems().clear();
			issueListView.getItems().clear();
			linkedObjectsListView.getItems().clear();
			operationValueListView.getItems().clear();
			constraintListView.getItems().clear();
			attributeBrowserTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			operationInputTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			operationOutputTexField.setText(StringValue.LabelAndHeaderTitle.empty);
			//associationBrowserTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			updateOperationTab(null, false, false);
			updateConstraintTab(null, false, false);
		}
	}
	
	protected void initElements() {

		metaClassTextField = new TextField();
		metaClassTextField.setEditable(false);

		delegatesToTextField = new TextField();
		delegatesToTextField.setEditable(false);
		attributeBrowserTextField = new TextField();
		operationInputTextField = new TextField();
		operationInputTextField.setEditable(false);
		operationOutputTexField = new TextField();
		operationOutputTexField.setEditable(false);
//		sourceLabel = new Label();
//		targetLabel = new Label();
//		visible = new Label(StringValue.LabelAndHeaderTitle.visible);
//		labelTransitive = new Label (StringValue.LabelAndHeaderTitle.transitive);
//		labelSymmetric = new Label (StringValue.LabelAndHeaderTitle.symmetric);
		associationBooleanAttributesHBox = new HBox();
		associationBooleanAttributesHBox.setPadding(new Insets(5,5,5,5));
		visibleHBox = new HBox();
		visibleHBox.setPadding(new Insets(5,5,5,5));
		visibleHBox.setSpacing(10);		
		
		opCodeButton.setDisable(true);
		conCodeButton.setDisable(true);
		
		statusLabel.setFont(Font.font(statusLabel.getFont().getFamily(), FontWeight.BOLD, statusLabel.getFont().getSize()));
	}
	
	private void addDoubleClickListeners() {

		fmmlxAttributeListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
		        FmmlxAttribute att = fmmlxAttributeListView.getSelectionModel().getSelectedItem();
		        FmmlxObject object = fmmlxObjectListView.getSelectionModel().getSelectedItem();
		        if(att != null && object != null) {
		        	activePackage.getActions().changeNameDialog(object, tool.clients.fmmlxdiagrams.dialogs.PropertyType.Attribute, att);
		        }
			}
		});
		
		slotListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
		        FmmlxSlot slot = slotListView.getSelectionModel().getSelectedItem();
		        FmmlxObject object = fmmlxObjectListView.getSelectionModel().getSelectedItem();
		        if(slot != null && object != null) {
		        	activePackage.getActions().changeSlotValue(object, slot);
		        }
			}
		});	
		

		issueListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				
				Issue issue = issueListView.getSelectionModel().getSelectedItem();
		        FmmlxObject object = fmmlxObjectListView.getSelectionModel().getSelectedItem();
		        if(issue != null && object != null) {
		        	Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Constraint report / Issue");
					alert.setHeaderText(null);
					alert.setContentText(issue.toString());
					alert.showAndWait();
		        	issue.performResolveAction(activePackage);
		        }
			}
		});	
	}

	private void addCellFactories() {
		
		fmmlxObjectListView.setCellFactory((listView) -> {
			return new ListCell<FmmlxObject>() {

	            @Override
	            protected void updateItem(FmmlxObject o, boolean empty) {
	                super.updateItem(o, empty);
	                if (o != null) {
	                    if (activePackage.getIssues(o).size()>0) {
	                    	setStyle("-fx-control-inner-background: tomato;");
	                    } 
	                    else {
	                    	setStyle("-fx-control-inner-background: white;");
	                    }
	                    
	                	if(o.isAbstract()) setText("(" + o.getName() + " ^"+ o.getMetaClassName() + "^ " + ")"); else setText(o.getName()+ " ^"+ o.getMetaClassName() + "^");
	                	
	                    setGraphic(getClassLevelGraphic(o.getLevel()));
	                } else { setText(""); setGraphic(null); }
	            }
	        };
	    });
		
		fmmlxAttributeListView.setCellFactory((listView) -> {
			return new ListCell<FmmlxAttribute>() {

	            @Override
	            protected void updateItem(FmmlxAttribute att, boolean empty) {
	                super.updateItem(att, empty);
	                FmmlxObject o = fmmlxObjectListView.getSelectionModel().getSelectedItem();
	                if (att != null && o != null) {
	                    setText(att.getName() +": "+ att.getType());
	                    setGraphic(getFeatureLevelGraphic(att.getLevel(), o.getOwnAttributes().contains(att)));
	                } else { setText(""); setGraphic(null); }	            }
	        };
		});
		
		slotListView.setCellFactory((listView) -> {
			return new ListCell<FmmlxSlot>() {
	
	            @Override
	            protected void updateItem(FmmlxSlot slot, boolean empty) {
	                super.updateItem(slot, empty);
	                FmmlxObject o = fmmlxObjectListView.getSelectionModel().getSelectedItem();
	                if (slot != null && o != null) {
	                    setText(slot.getName()+" = " + slot.getValue());
	                } else {
	                	setText("");
	                }
	            }
	        };
		});
		
		operationValueListView.setCellFactory((listView) -> {
			return new ListCell<FmmlxOperationValue>() {
	
	            @Override protected void updateItem(FmmlxOperationValue opValue, boolean empty) {
	                super.updateItem(opValue,empty);
	                FmmlxObject o = fmmlxObjectListView.getSelectionModel().getSelectedItem();
	                if (opValue != null && o != null) {
	                    setText(opValue.getName()  + "() = " + opValue.getValue());
	                } else {
	                	setText("");
	                }
	            }
	        };
		});
		
		fmmlxOperationListView.setCellFactory((listView) -> {
			return new ListCell<FmmlxOperation>() {
	
				@Override protected void updateItem(FmmlxOperation operation, boolean empty) {
	                super.updateItem(operation, empty);
	                FmmlxObject o = fmmlxObjectListView.getSelectionModel().getSelectedItem();
	                if (operation != null && o != null) {
	                    setText(operation.getFullString(activePackage));
	                    setGraphic(getFeatureLevelGraphic(operation.getLevel(), o.getOwnOperations().contains(operation)));
	                } else { setText(""); setGraphic(null); }
	            }
	        };
		});
		
				
		 
		Callback<ListView<FmmlxAssociation>, ListCell<FmmlxAssociation>> associationFactory = (listView) -> { 
			return new ListCell<FmmlxAssociation>() {
				@Override protected void updateItem(FmmlxAssociation association, boolean empty) {
					super.updateItem(association, empty);
					if (association!=null) {
						setText("{"+ association.getLevelSource() +"} " + association.getSourceNode().getName() + " [" + association.getMultiplicityStartToEnd().toString()+"]" +" " + association.getName() +" {"+ association.getLevelTarget() + "} " + association.getTargetNode().getName()+ " [" + association.getMultiplicityEndToStart().toString()+"]");
					} else { setText(""); setGraphic(null);  }
				}
			};
		};
		fmmlxAssociationListView.setCellFactory(associationFactory);
		
		constraintListView.setCellFactory((listView) -> {
			return new ListCell<Constraint>() {

				@Override protected void updateItem(Constraint constraint, boolean empty) {
	                super.updateItem(constraint, empty);
	                if (constraint != null) {
	                    setText(constraint.getName());
	                    setGraphic(getFeatureLevelGraphic(constraint.getLevel(), true));
	                } else { setText(""); setGraphic(null); }
	            }
	        };
		});
		
		issueListView.setCellFactory((listView) -> {
			return new ListCell<Issue>() {

	        	@Override protected void updateItem(Issue issue, boolean empty) {
	        		final Tooltip tooltip = new Tooltip();
	        		super.updateItem(issue, empty);
	                if (issue != null) {
	                	setTextFill(Color.INDIANRED);
	                	setText(issue.toString());
	                	tooltip.setText(issue.toString());
	                	setTooltip(tooltip);
	                } else {
	                	setText("");
	                	setTooltip(null);
	                }
	            }
	        };
		});
		
		linksListView.setCellFactory(associationFactory);
		
		
		
	}

	private void addSelectionListeners() {
		modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onModelListViewNewValue(oldValue, newValue));

		fmmlxObjectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onObjectListViewNewValue(oldValue, newValue));	
		abstractCheckBox.selectedProperty().addListener((ov,oldValue,newValue) 
			      ->onAbstractNewValue(oldValue, newValue));		

		fmmlxAttributeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onAttributeListViewNewValue(oldValue, newValue));
		fmmlxOperationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onOperationListViewNewValue(oldValue, newValue));
		constraintListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onConstraintListViewNewValue(oldValue,newValue));	

		slotListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onSlotListViewNewValue(oldValue,newValue));	
		operationValueListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onOperationValueListViewNewValue(oldValue,newValue));	
		issueListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onIssueListViewNewValue(oldValue,newValue));	

		fmmlxAssociationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onAssociationListViewNewValue(oldValue,newValue));
		
		linksListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onLinksListViewNewValue(oldValue, newValue));
	}

	protected SplitPane layoutElements() {
		final ColumnConstraints FILL = new ColumnConstraints(); FILL.setFillWidth(true); FILL.setHgrow(Priority.ALWAYS);

		GridPane mainGridPane = new GridPane();
		mainGridPane.setHgap(10);
		mainGridPane.setVgap(8);
		mainGridPane.setPadding(new Insets(5,5,5,5));		
		
		GridPane modelColumnGrid = new GridPane();
		modelColumnGrid.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
		modelColumnGrid.setVgap(3);
		modelColumnGrid.setPadding(new Insets(3,3,3,3));
		modelColumnGrid.add(new Label(StringValue.LabelAndHeaderTitle.model), 0,0);
		modelColumnGrid.add(modelListView, 0,1);		
		modelColumnGrid.add(new Label(StringValue.LabelAndHeaderTitle.project + ": [TODO]"), 0,2);
		
		Button button1 = new Button("Update...");
		modelColumnGrid.add(button1, 0,3);
		button1.setOnAction(e -> activePackage.updateDiagram());
		modelColumnGrid.add(statusLabel, 0,4);
		GridPane.setHalignment(statusLabel, javafx.geometry.HPos.CENTER);
		Button button3 = new Button("Do not push!");
		modelColumnGrid.add(button3, 0,5);
//		button3.setOnAction(e -> System.exit(0));
		
		button1.setMaxWidth(300);
		statusLabel.setMaxWidth(300);
		button3.setMaxWidth(300);
		modelColumnGrid.getColumnConstraints().add(FILL);
		
		GridPane objectPropertyPane = new GridPane();
		objectPropertyPane.setPadding(new Insets(3,3,3,3));
		objectPropertyPane.setVgap(3);
		objectPropertyPane.setHgap(3);
		
		objectPropertyPane.add(new Label(StringValue.LabelAndHeaderTitle.objects), 0,0, 2,1);
		objectPropertyPane.add(fmmlxObjectListView, 0,1, 2,1);		
		objectPropertyPane.add(new Label(StringValue.LabelAndHeaderTitle.metaClass + ": "), 0,2);
		objectPropertyPane.add(metaClassTextField, 1,2);
		objectPropertyPane.add(new Label(StringValue.LabelAndHeaderTitle.parent + ": "), 0,3);
		objectPropertyPane.add(parentsListView, 1,3);
		parentsListView.setMaxHeight(70);
		parentsListView.setMinHeight(70);
		objectPropertyPane.add(new Label(StringValue.LabelAndHeaderTitle.abstractBig + ": "), 0,4);
		objectPropertyPane.add(abstractCheckBox, 1,4);
		objectPropertyPane.add(new Label(StringValue.LabelAndHeaderTitle.delegatesTo), 0,5);
		objectPropertyPane.add(delegatesToTextField, 1,5);

		objectPropertyPane.getColumnConstraints().add(new ColumnConstraints(100, 125, 150));
		objectPropertyPane.getColumnConstraints().add(FILL);
		
		GridPane propertyGrid = new GridPane();
		propertyGrid.setVgap(3);
		propertyGrid.setHgap(3);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.attributes),0,0);
		propertyGrid.add(fmmlxAttributeListView,0,1);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.slots),1,0);
		propertyGrid.add(slotListView,1,1);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.operations),0,2);
		propertyGrid.add(fmmlxOperationListView,0,3);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.operationValues),1,2);
		propertyGrid.add(operationValueListView,1,3);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.constraint),0,4);
		propertyGrid.add(constraintListView,0,5);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.constraintValues),1,4);
		propertyGrid.add(issueListView,1,5);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.associations), 2,0);
		propertyGrid.add(fmmlxAssociationListView, 2, 1);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.links), 2,2);
		propertyGrid.add(linksListView, 2, 3);
		propertyGrid.add(new Label(StringValue.LabelAndHeaderTitle.linkedObjects), 2,4);
		propertyGrid.add(linkedObjectsListView, 2, 5);
		
		for(int i=0;i<3;i++) propertyGrid.getColumnConstraints().add(new ColumnConstraints(150, 150, Integer.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true));
		
//		GridPane associationsGrid = new GridPane();
//		associationsGrid.setHgap(3);
//		associationsGrid.setVgap(3);
//		associationsGrid.add(new Label(StringValue.LabelAndHeaderTitle.empty),0, 0);
//		associationsGrid.add(new Label(StringValue.LabelAndHeaderTitle.source), 0, 1);
//		associationsGrid.add(sourceListView, 1, 1);
//		sourceListView.setMaxHeight(30);
//		associationsGrid.add(new Label (StringValue.LabelAndHeaderTitle.visible), 2, 1);
//		associationsGrid.add(sourceVisible, 3, 1);
//		associationsGrid.add(new Label(StringValue.LabelAndHeaderTitle.target), 0, 2);
//		associationsGrid.add(targetListView, 1, 2);
//		targetListView.setMaxHeight(30);
//		associationsGrid.add(new Label (StringValue.LabelAndHeaderTitle.visible), 2, 2);
//		associationsGrid.add(targetVisible, 3, 2);
		
		mainGridPane.add(modelColumnGrid, 0, 0);
		mainGridPane.add(objectPropertyPane, 1,0);
		mainGridPane.add(propertyGrid,2,0);
		//mainGridPane.add(associationsGrid, 3, 0);
		
		mainGridPane.getColumnConstraints().add(new ColumnConstraints(150, 180, 250, Priority.SOMETIMES, HPos.LEFT, true));
		mainGridPane.getColumnConstraints().add(new ColumnConstraints(150, 200, 300, Priority.SOMETIMES, HPos.LEFT, true));
		mainGridPane.getColumnConstraints().add(new ColumnConstraints(450, 600, Integer.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true));
				
		SplitPane outerSplitPane = new SplitPane();
		outerSplitPane.setOrientation(Orientation.VERTICAL);
		codeAndConstraintTabPane = new TabPane();
		codeAndConstraintTabPane.getTabs().addAll(operationTab, constraintTab, issueTab);
		outerSplitPane.getItems().addAll(mainGridPane, codeAndConstraintTabPane);
		
		GridPane operationPane = new GridPane();
		operationPane.setVgap(3);
		operationPane.add(operationCodeArea.virtualizedScrollPane, 0, 0);
		operationPane.add(opCodeButton, 0, 1);
		operationPane.getColumnConstraints().add(FILL);
		operationPane.getRowConstraints().add(new RowConstraints(1, Integer.MAX_VALUE, Integer.MAX_VALUE));
		operationTab.setContent(operationPane);
		
		GridPane constraintPane = new GridPane();
		constraintPane.setVgap(3);
		constraintPane.add(constraintBodyArea.virtualizedScrollPane, 0, 0);
		constraintPane.add(constraintReasonArea.virtualizedScrollPane, 0, 1);
		constraintPane.add(conCodeButton, 0, 2);
		constraintPane.getRowConstraints().add(new RowConstraints(1, Integer.MAX_VALUE, Integer.MAX_VALUE));
		constraintPane.getRowConstraints().add(new RowConstraints(1, Integer.MAX_VALUE, Integer.MAX_VALUE));
		constraintPane.getColumnConstraints().add(FILL);
		constraintTab.setContent(constraintPane);
		
		GridPane issuePane = new GridPane();
		issuePane.setVgap(3);
		issuePane.add(issueArea.virtualizedScrollPane, 0, 0);
		issuePane.getColumnConstraints().add(FILL);
		issuePane.getRowConstraints().add(new RowConstraints(1, Integer.MAX_VALUE, Integer.MAX_VALUE));
		issueTab.setContent(issuePane);
		
		VBox.setVgrow(outerSplitPane,Priority.ALWAYS);		
		return outerSplitPane;
	}

	private void onObjectListViewNewValue(FmmlxObject oldValue, FmmlxObject selectedObject) {
		
		boolean noObject = selectedObject == null;
		
		fmmlxAttributeListView.getItems().clear();
		onAttributeListViewNewValue(null, null);
		slotListView.getItems().clear();
		fmmlxOperationListView.getItems().clear();
		onOperationListViewNewValue(null, null);
		operationValueListView.getItems().clear();
		fmmlxAssociationListView.getItems().clear();
		onAssociationListViewNewValue(null, null);
		onConstraintListViewNewValue(null, null);
		constraintListView.getItems().clear();
		metaClassTextField.clear();
		delegatesToTextField.clear();
		constraintListView.getItems().clear();
		issueListView.getItems().clear();
		parentsListView.getItems().clear();	
		linksListView.getItems().clear();

		delegatesToTextField.setDisable(noObject);
		abstractCheckBox.setDisable(    noObject);
		parentsListView.setDisable(     noObject);
		
		if (!noObject) {
			selection.put("OBJ", selectedObject.getName());
			fmmlxAttributeListView.getItems().addAll(selectedObject.getAllAttributes());
			slotListView.getItems().addAll(selectedObject.getAllSlots());
			fmmlxOperationListView.getItems().addAll(selectedObject.getAllOperations());
			fmmlxAssociationListView.getItems().addAll(selectedObject.getAllRelatedAssociations());
			metaClassTextField.setText(selectedObject.getMetaClassName());
			operationValueListView.getItems().addAll(selectedObject.getOperationValues());
			constraintListView.getItems().addAll(selectedObject.getConstraints());
			issueListView.getItems().addAll(activePackage.getIssues(selectedObject));	
		    linksListView.getItems().addAll(selectedObject.findAssociationsForLinks());
			if (selectedObject.getDelegatesTo(false) == null) {
				if (selectedObject.getDelegatesTo(true) == null) {
					delegatesToTextField.setText("No delegation");
				} else {
					delegatesToTextField.setText("(" + selectedObject.getDelegatesTo(true).toString() + ")");	
				}
			} else {
				delegatesToTextField.setText(selectedObject.getDelegatesTo(false).toString());	
			}
			abstractCheckBox.setSelected(selectedObject.isAbstract());
			parentsListView.getItems().addAll(selectedObject.getParentsPaths());
		}
		fmmlxObjectListView.setContextMenu(new BrowserObjectContextMenu());
	}

	private void onAssociationListViewNewValue(FmmlxAssociation oldValue, FmmlxAssociation association) {
		if (association!=null) {
//			checkSymmetric.setDisable(false);
//			checkTransitive.setDisable(false);
//			sourceVisible.setDisable(false);
//			targetVisible.setDisable(false);
//			checkSymmetric.setSelected(association.isSymmetric());
//			checkTransitive.setSelected(association.isTransitive());
//			sourceVisible.setSelected(association.isSourceVisible());
//			targetVisible.setSelected(association.isTargetVisible());
		} else {
//			checkSymmetric.setDisable(true);
//			checkTransitive.setDisable(true);
//			sourceVisible.setDisable(true);
//			targetVisible.setDisable(true);
		}
		fmmlxAssociationListView.setContextMenu(new BrowserAssociationContextMenu(fmmlxObjectListView, fmmlxAssociationListView, activePackage));
	}

	private void onAbstractNewValue(Boolean oldValue, Boolean newValue) {
		communicator.setClassAbstract(activePackage.getID(), fmmlxObjectListView.getSelectionModel().getSelectedItem().getName(), abstractCheckBox.isSelected());
		activePackage.updateDiagram();
	}
	
	private void onModelListViewNewValue(String oldSelectedPath, String selectedPath) {
		if(selectedPath == null || selectedPath.equals(oldSelectedPath)) return;
		if(!models.containsKey(selectedPath)) {
			Integer newDiagramID = communicator.createDiagram(selectedPath, "Test", "", FmmlxDiagramCommunicator.DiagramType.ModelBrowser);
			ClassBrowserPackageViewer tempViewer = new ClassBrowserPackageViewer(communicator, newDiagramID, selectedPath, this);
			models.put(selectedPath, tempViewer);
		}
		activePackage = models.get(selectedPath);
		activePackage.updateDiagram();
	}	

	private void onAttributeListViewNewValue(FmmlxAttribute oldAtt, FmmlxAttribute newAtt) {
		if(newAtt != null) {selection.put("ATT", newAtt.getName());}
		fmmlxAttributeListView.setContextMenu(new BrowserAttributeContextMenu(fmmlxObjectListView.getSelectionModel().getSelectedItem(), newAtt, activePackage));
	}

	private void onOperationListViewNewValue(FmmlxOperation oldValue, FmmlxOperation newOp) {
		if (newOp!=null) {
			selection.put("OPE", newOp.getName());
			updateOperationTab(newOp, activePackage.getObjectByPath(newOp.getOwner()) == fmmlxObjectListView.getSelectionModel().getSelectedItem(), true);
			fmmlxOperationListView.setContextMenu(new BrowserOperationContextMenu(fmmlxObjectListView.getSelectionModel().getSelectedItem(), newOp, activePackage));
			opCodeButton.setDisable(false);
			opCodeButton.setOnAction(e -> {
				activePackage.getComm().changeOperationBody(
						activePackage.getID(), 
						activePackage.getObjectByPath(newOp.getOwner()).getName(), 
						newOp.getName(), 
						operationCodeArea.getText());
				activePackage.updateDiagram();
			});
		} else{
			updateOperationTab(null, false, false);
		}
	}

	private void onOperationValueListViewNewValue(FmmlxOperationValue oldValue, FmmlxOperationValue newOpV) {
		if (newOpV!=null) {
			selection.put("OPV", newOpV.getName());
			opCodeButton.setDisable(true);
			
			MenuItem selectDefValueItem = new MenuItem("Select Definition");
			final FmmlxOperation op = activePackage.getOperation(newOpV);
			final FmmlxObject obj = activePackage.getObjectByPath(op.getOwner());

			updateOperationTab(op, false, true);
			selectDefValueItem.setOnAction(e -> setSelectedObjectAndProperty(obj, op)); 
			operationValueListView.setContextMenu(new ContextMenu(selectDefValueItem));			
		} else{
			updateOperationTab(null, false, false);
			operationValueListView.setContextMenu(null);
		}
	}
	
	private void onSlotListViewNewValue(FmmlxSlot oldValue, final FmmlxSlot newSlot) {
		if (newSlot!=null) {
			selection.put("SLO", newSlot.getName());
			final FmmlxObject object = fmmlxObjectListView.getSelectionModel().getSelectedItem();
			MenuItem changeSlotValueItem = new MenuItem("Change Value");
			changeSlotValueItem.setOnAction(e -> activePackage.getActions().changeSlotValue(object, newSlot)); 
			slotListView.setContextMenu(new ContextMenu(changeSlotValueItem));
		} else{
			slotListView.setContextMenu(null);
		}
	}
	
	private void onConstraintListViewNewValue(Constraint oldValue, Constraint constraint) {
		if (constraint != null) {
			selection.put("CON", constraint.getName());
			updateConstraintTab(constraint, true, true);
		} else {
			updateConstraintTab(null, false, false);
		}
		constraintListView.setContextMenu(new BrowserConstraintContextMenu());
	}
	
	private void onIssueListViewNewValue(Issue oldValue, Issue issue) {
		if (issue!=null) {
			selection.put("ISS", issue.getText());
			updateIssueTab(issue, false, true);
		} else {
			updateIssueTab(null, false, false);
		}
	}	

	private void onLinksListViewNewValue(FmmlxAssociation oldValue, FmmlxAssociation association) {
		linkedObjectsListView.getItems().clear();
		if (association!=null) {
			FmmlxObject obj = fmmlxObjectListView.getSelectionModel().getSelectedItem();
			if (obj!=null) {
				for (Edge<?> edge : activePackage.getEdges()) {
					if (edge instanceof FmmlxLink) {
						FmmlxLink link = (FmmlxLink) edge; 
						if (link.getOfAssociation() == association) {
							if (link.getSourceNode() == obj) {
								linkedObjectsListView.getItems().add(link.getTargetNode());
							}
							if (link.getTargetNode() == obj) {
								linkedObjectsListView.getItems().add(link.getSourceNode());
							}	
						}
					}
				}
			}
		} else {
			
		}
		linksListView.setContextMenu(new BrowserLinkedObjectContextMenu());
	}

	
	private void updateOperationTab(FmmlxOperation operation, boolean editable, boolean select) {
		if(select) codeAndConstraintTabPane.getSelectionModel().select(operationTab);
		opCodeButton.setDisable(!editable);
		if(operation != null) {
			operationCodeArea.setText(operation.getBody());
			operationTab.setText("Operation" + " (" + operation.getName() + ")");
		} else {
			operationCodeArea.setText("");
			operationTab.setText("Operation");
		}
	}	
	
	private void updateConstraintTab(Constraint constraint, boolean editable, boolean select) {
		if(select) codeAndConstraintTabPane.getSelectionModel().select(constraintTab);
		conCodeButton.setDisable(!editable);
		if(constraint != null) {
			constraintBodyArea.setText(constraint.getBodyFull());
			constraintReasonArea.setText(constraint.getReasonFull());
			constraintTab.setText("Constraint" + " (" + constraint.getName()+ ")");
		} else {
			constraintBodyArea.setText("");
			constraintReasonArea.setText("");
			constraintTab.setText("Constraint");			
		}
		conCodeButton.setOnAction(e->{activePackage.getComm().changeConstraintBodyAndReason(
				activePackage.getID(), 
				fmmlxObjectListView.getSelectionModel().getSelectedItem().getPath(), 
				constraint.getName(), 
				constraintBodyArea.getText(), 
				constraintReasonArea.getText());});
	}
	
	private void updateIssueTab(Issue issue, boolean editable, boolean select) {
		if(select) {
			codeAndConstraintTabPane.getSelectionModel().select(issueTab);
			if (issue!=null) {
				issueArea.setText(issue.getText());
				issueTab.setText("Issue");
			} else {
				issueArea.setText("");
				issueTab.setText("Issue");
			}
		}
	}

	public void notifyModelHasLoaded() {
		Platform.runLater(() -> {
			Vector<FmmlxObject> objects = activePackage.getObjects();
			levelColorScheme = new LevelColorScheme.FixedBlueLevelColorScheme();
			
			Collections.sort(objects, new Comparator<FmmlxObject>() {

				@Override
				public int compare(FmmlxObject o1, FmmlxObject o2) {
					if(o1.getLevel() < o2.getLevel()) return 1;
					if(o1.getLevel() > o2.getLevel()) return -1;
					return o1.getName().compareTo(o2.getName());
				}
			});

			fmmlxObjectListView.getItems().clear();
			fmmlxObjectListView.getItems().addAll(objects);
			//set Flag for loaded okay...
			restoreSelection();
		});
	}
	
	private transient HashMap<String, String> selection = new HashMap<>();
	
	private void restoreSelection() {
		selectItemByString(fmmlxObjectListView, selection.get("OBJ"));

		selectItemByString(fmmlxAttributeListView, selection.get("ATT"));
		selectItemByString(fmmlxOperationListView, selection.get("OPE"));
		selectItemByString(constraintListView, selection.get("CON"));

		selectItemByString(slotListView, selection.get("SLO"));
		selectItemByString(operationValueListView, selection.get("OPV"));
		selectItemByString(issueListView, selection.get("ISS"));
		
		selectItemByString(fmmlxAssociationListView, selection.get("SLO"));
		selectItemByString(linksListView, selection.get("LI1"));
		selectItemByString(linkedObjectsListView, selection.get("LI2"));
		selectItemByString(issueListView, selection.get("ISS"));
	}
	
	private <E extends FmmlxProperty> void selectItemByString(ListView<E> list, String key) {
		for(int i = 0; i < list.getItems().size() && key != null; i++) {
			if(key.equals(list.getItems().get(i).getName())) {
				key = null;
				list.getSelectionModel().select(list.getItems().get(i));
			}
		}
	}
	
	private Node getClassLevelGraphic(int level) {
		if(level == -1) return null;
		double SIZE = 16;
		Canvas canvas = new Canvas(SIZE, SIZE);
		Text temp = new Text(level+"");
		GraphicsContext g = canvas.getGraphicsContext2D();
		g.setFill(levelColorScheme.getLevelBgColor(level));
		g.fillRoundRect(0, 0, SIZE, SIZE, SIZE/2, SIZE/2);
		g.setFill(levelColorScheme.getLevelFgColor(level, 1.));
		g.fillText(level+"", 
				SIZE/2 - temp.getLayoutBounds().getWidth()/2., 
				SIZE/2 + temp.getLayoutBounds().getHeight()/2. - 4);
		return canvas;
	}
	
	private Node getFeatureLevelGraphic(int level, boolean own) {
		if(level == -1) return null;
		double SIZE = 16;
		Canvas canvas = new Canvas(SIZE, SIZE);
		Text temp = new Text(level+"");
		GraphicsContext g = canvas.getGraphicsContext2D();
		g.setFill(own?Color.BLACK:Color.GRAY);
		g.fillRoundRect(0, 0, SIZE, SIZE, SIZE/3, SIZE/3);
		g.setFill(Color.WHITE);
		g.fillText(level+"", 
				SIZE/2 - temp.getLayoutBounds().getWidth()/2., 
				SIZE/2 + temp.getLayoutBounds().getHeight()/2. - 4);
		return canvas;
	}

	public void setSelectedObjectAndProperty(FmmlxObject object, FmmlxProperty property) {
		fmmlxObjectListView.scrollTo(object);
		fmmlxObjectListView.getSelectionModel().select(object);
		
		if(property == null) return;
		
		if(property instanceof FmmlxAttribute) {
			FmmlxAttribute att = (FmmlxAttribute) property;
			fmmlxAttributeListView.scrollTo(att);
			fmmlxAttributeListView.getSelectionModel().select(att);
		} else if(property instanceof FmmlxOperation) {
			FmmlxOperation att = (FmmlxOperation) property;
			fmmlxOperationListView.scrollTo(att);
			fmmlxOperationListView.getSelectionModel().select(att);
		} else{
			throw new RuntimeException("not yet implemented for " + property.getClass().getName());
		}		
	}

	public void setStatusButton(ViewerStatus newStatus) {
		Platform.runLater(() -> {
			switch(newStatus) {
			case DIRTY : 
				if(!statusLabel.getText().startsWith("Status: Already Loading"))
					statusLabel.setText("Status: Already Loading");
				else
					statusLabel.setText(statusLabel.getText() + "!");
				statusLabel.setTextFill(new Color(.8, .2, .5, 1.)); break;
			case LOADING : 
				statusLabel.setText("Status: Loading Model"); 
				statusLabel.setTextFill(new Color(.8, .5, .2, 1.)); break;
			case CLEAN : 
				statusLabel.setText("Status: View Updated");
				statusLabel.setTextFill(new Color(.2, .5, .8, 1.)); break;
			}
		});
	}
	
	private class BrowserObjectContextMenu extends ContextMenu {

		private final FmmlxObject object;
		private final DiagramActions actions;
		
		public BrowserObjectContextMenu() {
			
			this.actions = activePackage.getActions();
			this.object = fmmlxObjectListView.getSelectionModel().getSelectedItem();
			setAutoHide(true);
			
			addNewMenuItem(this, "Add Class", e -> actions.addMetaClassDialog((javafx.scene.canvas.Canvas) null), ALWAYS);
			if(object!=null) {
				addNewMenuItem(this, "Add Instance of " + object.getName(), e -> actions.addInstanceDialog(object, (javafx.scene.canvas.Canvas) null), () -> {return object.getLevel() >= 1 && !object.isAbstract();});
				addNewMenuItem(this, "Instance Generator", e -> actions.runInstanceGenerator(object), NEVER);
	
				getItems().add(new SeparatorMenuItem());
	
				addNewMenuItem(this, "Change Name", e -> actions.changeNameDialog(object, PropertyType.Class, object), ALWAYS);
				addNewMenuItem(this, "Remove Object", e -> actions.removeDialog(object, PropertyType.Class, object), ALWAYS);
				addNewMenuItem(this, "Change Metaclass",e -> {new javafx.scene.control.Alert(
						AlertType.INFORMATION, "Really ?", 
						javafx.scene.control.ButtonType.NO, 
						javafx.scene.control.ButtonType.CANCEL).showAndWait();}, ALWAYS);
				addNewMenuItem(this, "Change Superclasses", e -> actions.changeParentsDialog(object), () -> {return object.getLevel() >= 1;});
				addNewMenuItem(this, "Set Delegation", e -> actions.setDelegation(object, null), () -> {return object.getLevel() >= 1;});
				addNewMenuItem(this, "Remove Delegation", e -> actions.removeDelegation(object), () -> {return object.getDelegatesTo(false) != null;});
				addNewMenuItem(this, "Set RoleFiller", e -> actions.setRoleFiller(object, null), () -> {return object.getDelegatesTo(true)!= null;});
				addNewMenuItem(this, "Remove RoleFiller", e -> actions.removeRoleFiller(object), () -> {return object.getRoleFiller() != null;});
				addNewMenuItem(this, object.isAbstract()?"Make Concrete":"Make Abstract", e -> actions.toggleAbstract(object), () -> {return object.getLevel() >= 1 && object.getInstances().size() > 0;});
			}
		}
	}
	
	private class BrowserLinkedObjectContextMenu extends ContextMenu {
		
		private final FmmlxObject object;
		//private final FmmxObject target;
		private final DiagramActions actions;
		private final FmmlxAssociation association;
		
		public BrowserLinkedObjectContextMenu() {
			this.object = fmmlxObjectListView.getSelectionModel().getSelectedItem();
			this.actions = activePackage.getActions();
			this.association = linksListView.getSelectionModel().getSelectedItem();
			setAutoHide(true);
			addNewMenuItem(this, "Add instance for Link", e -> actions.addAssociationInstanceDialog(null,null,association),ALWAYS);
			
		}
		
	}
	
	private class BrowserConstraintContextMenu extends ContextMenu {
		
		private final FmmlxObject object;
		private final DiagramActions actions;
		private final Constraint constraint;
		
		public BrowserConstraintContextMenu() {
			this.object = fmmlxObjectListView.getSelectionModel().getSelectedItem();
			this.actions = activePackage.getActions();
			this.constraint = constraintListView.getSelectionModel().getSelectedItem();
			setAutoHide(true);
			addNewMenuItem(this,"Add Constraint", e -> actions.addConstraintDialog(object), ALWAYS);
			addNewMenuItem(this,"Remove Constraint", e -> actions.removeDialog(object,PropertyType.Constraint, constraint),() -> {return constraint != null;});
		}
	}
	
	private void addNewMenuItem(ContextMenu parentMenu, String text, EventHandler<ActionEvent> action, Enabler enabler) {
		MenuItem item = new MenuItem(text);
		item.setOnAction(action);
		item.setDisable(!enabler.isEnabled());
		parentMenu.getItems().add(item);
	}
	
	private void notYetImplemented() {
		new javafx.scene.control.Alert(AlertType.ERROR, "Not yet implemented", ButtonType.CANCEL).showAndWait(); return;
	}
	
	private interface Enabler {
		boolean isEnabled();
	}
	private static final Enabler ALWAYS = () -> true; 
	private static final Enabler NEVER = () -> false; 
}