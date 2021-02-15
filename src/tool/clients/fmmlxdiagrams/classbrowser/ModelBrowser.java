package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.LevelColorScheme;
import tool.clients.fmmlxdiagrams.LevelColorScheme.RedLevelColorScheme;
import tool.clients.fmmlxdiagrams.FmmlxSlot;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.ValueList;
import tool.clients.fmmlxdiagrams.menus.BrowserAttributeContextMenu;
import tool.clients.fmmlxdiagrams.menus.BrowserObjectContextMenu;
import tool.xmodeler.XModeler;


public class ModelBrowser extends CustomStage {

	private TextArea codeArea;
	private Label sourceLabel, targetLabel, visible, labelTransitive, labelSymmetric;
	private ListView<String> modelListView, parentsListView, sourceListView2, targetListView2;
	private ListView<FmmlxAssociation> fmmlxAssociationListView, sourceListView, targetListView; 
	private ListView<FmmlxOperation> fmmlxOperationListView;
	private ListView<FmmlxObject> fmmlxObjectListView;
	private ListView<FmmlxAttribute> fmmlxAttributeListView;
	private ListView<FmmlxSlot> slotListView;
	private CheckBox abstractCheckBox, targetVisible, sourceVisible, checkTransitive, checkSymmetric;
	private TextField modellBrowserTextFied, metaClassTextField, delegatesToTextField, operationInputTextField, operationOutputTexField,
						attributeBrowserTextField;
	private VBox modellBrowserVBox, classBrowserVBox, attributeBrowserVBox, abstractVBox, delegatesToVBox,
						operationOutputVBox, operationInputVBox, associationBrowserVBox, consoleContainerVBox, parentsVBox, sourceVBox, targetVBox;
	private HBox sourceHBox, targetHBox, associationBooleanAttributesHBox, visibleHBox;
	private SplitPane outerSplitPane;
	private GridPane mainGridPane, attributeGridpane;	
	FmmlxDiagramCommunicator communicator;
	private AbstractPackageViewer activePackage;
	
	
	private HashMap<String,AbstractPackageViewer> models = new HashMap<>();
	private RedLevelColorScheme levelColorScheme;
	
	public ModelBrowser(String project, String selectedModel, ObservableList<String> models) {
		super(StringValue.LabelAndHeaderTitle.modelBrowser+" " + project, XModeler.getStage(), 1400, 800);
		communicator = FmmlxDiagramCommunicator.getCommunicator();
		initAllElements();
		addAllElementToPane();			
		getContainer().getChildren().addAll(outerSplitPane);
		setOnCloseRequest(e -> onClose());
		modelListView.getItems().clear();
		modelListView.getItems().addAll(models);
		modelListView.getSelectionModel().clearSelection();
		if (selectedModel!=null) {
			modelListView.getSelectionModel().select(selectedModel);
		}
	}

	public void onClose() {
		clearAll(ClearSelectionMode.MODEL);
		for (String key:models.keySet()) {
			communicator.closeDiagram(models.get(key).getID());
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
			sourceListView.getItems().clear();
			targetListView.getItems().clear();
			parentsListView.getItems().clear();
			attributeBrowserTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			operationInputTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			operationOutputTexField.setText(StringValue.LabelAndHeaderTitle.empty);
			//associationBrowserTextField.setText(StringValue.LabelAndHeaderTitle.empty);
			sourceLabel.setText(StringValue.LabelAndHeaderTitle.source);
			targetLabel.setText(StringValue.LabelAndHeaderTitle.target);
			codeArea.clear();
		}
	}
	
	@Override
	protected void initAllElements() {
		mainGridPane = new GridPane();
		attributeGridpane = new GridPane();
		mainGridPane.setHgap(10);
		mainGridPane.setVgap(8);
		mainGridPane.setPadding(new Insets(3, 3, 3, 3));
		//setColumnConstrain(mainGridPane);

		modelListView = new ListView<>();
		fmmlxObjectListView = new ListView<>();
		parentsListView = new ListView<>();
		fmmlxAttributeListView = new ListView<>();
		slotListView = new ListView<>();
		fmmlxAssociationListView = new ListView<>();
		fmmlxOperationListView = new ListView<>();
		sourceListView = new ListView<>();
		targetListView = new ListView<>();
		sourceListView.setEditable(false);
		targetListView.setEditable(false);
		modellBrowserTextFied = new TextField();
		metaClassTextField = new TextField();
		metaClassTextField.setEditable(false);
		abstractCheckBox = new CheckBox();
		sourceVisible = new CheckBox();
		targetVisible = new CheckBox();
		checkTransitive = new CheckBox();
		checkSymmetric= new CheckBox();
		delegatesToTextField = new TextField();
		delegatesToTextField.setEditable(false);
		attributeBrowserTextField = new TextField();
		operationInputTextField = new TextField();
		operationInputTextField.setEditable(false);
		operationOutputTexField = new TextField();
		operationOutputTexField.setEditable(false);
		//associationBrowserTextField = new TextField();
		//associationBrowserTextField.setEditable(false);
		sourceLabel = new Label();
		targetLabel = new Label();
		visible = new Label(StringValue.LabelAndHeaderTitle.visible);
		labelTransitive = new Label (StringValue.LabelAndHeaderTitle.transitive);
		labelSymmetric = new Label (StringValue.LabelAndHeaderTitle.symmetric);
		sourceHBox = new HBox();
		targetHBox = new HBox();
		associationBooleanAttributesHBox = new HBox();
		associationBooleanAttributesHBox.setPadding(new Insets(3,3,3,3));
		visibleHBox = new HBox();
		visibleHBox.setPadding(new Insets(3,3,3,3));
		visibleHBox.setSpacing(10);
		
		codeArea = new TextArea();
		consoleContainerVBox= new VBox();
		consoleContainerVBox.getChildren().add(codeArea);
		
		outerSplitPane = new SplitPane();
		outerSplitPane.setOrientation(Orientation.VERTICAL);
		outerSplitPane.getItems().addAll(mainGridPane, consoleContainerVBox);
		
		VBox.setVgrow(outerSplitPane,Priority.ALWAYS);
		VBox.setVgrow(codeArea,Priority.ALWAYS);
		
		String doubleDots = " :";
		abstractVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.abstractSmall+doubleDots), abstractCheckBox);
		modellBrowserVBox= getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.project+doubleDots), modellBrowserTextFied);
		delegatesToVBox= getVBoxControl().joinNodeInVBox(new Label (StringValue.LabelAndHeaderTitle.delegatesTo), delegatesToTextField);
		operationOutputVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.output+doubleDots), operationOutputTexField);
		operationInputVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.input+doubleDots), operationInputTextField);
		classBrowserVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.metaClass+doubleDots), metaClassTextField);
		//associationBrowserVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.withSmall+doubleDots), associationBrowserTextField);
		sourceVBox = getVBoxControl().joinNodeInVBox(new Label (StringValue.LabelAndHeaderTitle.source), sourceListView);
		targetVBox = getVBoxControl().joinNodeInVBox(new Label (StringValue.LabelAndHeaderTitle.target), targetListView);
		attributeBrowserVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.aClassSmall+doubleDots), attributeBrowserTextField);
		parentsVBox = getVBoxControl().joinNodeInVBox(new Label(StringValue.LabelAndHeaderTitle.parent + doubleDots), parentsListView);
		
		
		modelListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onModelListViewNewValue(oldValue, newValue));
		modellBrowserTextFied.textProperty().addListener((observable, oldValue, newValue) 
				-> modellBrowserListerner(modelListView, oldValue, newValue));
		fmmlxObjectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onObjectListViewNewValue(oldValue, newValue));	
		metaClassTextField.textProperty().addListener((observable, oldValue, newValue) 
				-> classBrowserTextFieldListener(oldValue, newValue));
		abstractCheckBox.selectedProperty().addListener((ov,oldValue,newValue) 
			      ->onAbstractNewValue(oldValue, newValue));		
		fmmlxAttributeListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onAttributeListViewNewValue(oldValue, newValue));
		slotListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onSlotListViewNewValue(modelListView, oldValue, newValue));
		fmmlxOperationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onOperationListViewNewValue(oldValue, newValue));
		fmmlxAssociationListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) 
				-> onAssociationListViewNewValue(oldValue,newValue)); 
		
		fmmlxObjectListView.setCellFactory(new Callback<ListView<FmmlxObject>, ListCell<FmmlxObject>>() {

		    @Override
		    public ListCell<FmmlxObject> call(ListView<FmmlxObject> param) {
		        ListCell<FmmlxObject> cell = new ListCell<FmmlxObject>() {

		            @Override
		            protected void updateItem(FmmlxObject o, boolean empty) {
		                super.updateItem(o, empty);
		                if (o != null) {
		                    setText(o.getName());
		                    setGraphic(getLevelGraphic(o.getLevel()));
		                } else {
		                	setText("");
		                	setGraphic(null);
		                }
		            }

					private Node getLevelGraphic(int level) {
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
		        };
		        return cell;
		    }
		});
		
		fmmlxAttributeListView.setCellFactory(new Callback<ListView<FmmlxAttribute>, ListCell<FmmlxAttribute>>() {
	
		    @Override
		    public ListCell<FmmlxAttribute> call(ListView<FmmlxAttribute> param) {
		        ListCell<FmmlxAttribute> cell = new ListCell<FmmlxAttribute>() {
	
		            @Override
		            protected void updateItem(FmmlxAttribute att, boolean empty) {
		                super.updateItem(att, empty);
		                FmmlxObject o = fmmlxObjectListView.getSelectionModel().getSelectedItem();
		                if (att != null && o != null) {
		                    setText(att.getName() +": "+ att.getType());
		                    setGraphic(getLevelGraphic4Feature(att.getLevel(), o.getOwnAttributes().contains(att)));
		                } else {
		                	setText("");
		                	setGraphic(null);
		                }
		            }
	
		        };
		        return cell;
		    }
		});
		
		slotListView.setCellFactory(new Callback<ListView<FmmlxSlot>, ListCell<FmmlxSlot>>() {
			
		    @Override
		    public ListCell<FmmlxSlot> call(ListView<FmmlxSlot> param) {
		        ListCell<FmmlxSlot> cell = new ListCell<FmmlxSlot>() {
	
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
		        return cell;
		    }
		});
		
		fmmlxOperationListView.setCellFactory(new Callback<ListView<FmmlxOperation>, ListCell<FmmlxOperation>>() {
			
		    @Override
		    public ListCell<FmmlxOperation> call(ListView<FmmlxOperation> param) {
		        ListCell<FmmlxOperation> cell = new ListCell<FmmlxOperation>() {
	
		            @Override
		            protected void updateItem(FmmlxOperation operation, boolean empty) {
		                super.updateItem(operation, empty);
		                FmmlxObject o = fmmlxObjectListView.getSelectionModel().getSelectedItem();
		                if (operation != null && o != null) {
		                    setText(operation.getFullString(activePackage));
		                    setGraphic(getLevelGraphic4Feature(operation.getLevel(), o.getOwnAttributes().contains(operation)));
		                } else {
		                	setText("");
		                	setGraphic(null);
		                }
		            }
		        };
		        return cell;
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
		
		fmmlxAttributeListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
		        FmmlxAttribute att = fmmlxAttributeListView.getSelectionModel().getSelectedItem();
		        FmmlxObject object = fmmlxObjectListView.getSelectionModel().getSelectedItem();
		        if(att != null && object != null) {
		        	activePackage.getActions().changeNameDialog(object, tool.clients.fmmlxdiagrams.dialogs.PropertyType.Attribute, att);
		        }
			}
		});
		
		sourceListView.setCellFactory(new Callback<ListView<FmmlxAssociation>, ListCell<FmmlxAssociation>>() {

		    @Override
		    public ListCell<FmmlxAssociation> call(ListView<FmmlxAssociation> param) {
		        ListCell<FmmlxAssociation> cell = new ListCell<FmmlxAssociation>() {

		            protected void updateItem(FmmlxAssociation association, boolean empty) {
		                super.updateItem(association, empty);
		                if (association != null) {
		                    setText(association.getSourceNode().getName()+ " Multiplicity: " + association.getMultiplicityStartToEnd().toString());
		                    setGraphic(getLevelGraphic(association.getLevelSource()));
		                } else {
		                	setText("");
		                	setGraphic(null);
		                }
		            }

					private Node getLevelGraphic(int level) {
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
		        };
		        return cell;
		    }
		});
		
		targetListView.setCellFactory(new Callback<ListView<FmmlxAssociation>, ListCell<FmmlxAssociation>>() {

		    @Override
		    public ListCell<FmmlxAssociation> call(ListView<FmmlxAssociation> param) {
		        ListCell<FmmlxAssociation> cell = new ListCell<FmmlxAssociation>() {

		            protected void updateItem(FmmlxAssociation association, boolean empty) {
		                super.updateItem(association, empty);
		                if (association != null) {
		                    setText(association.getTargetNode().getName()+ " Mulitplicity: " + association.getMultiplicityEndToStart().toString());
		                    setGraphic(getLevelGraphic(association.getLevelTarget()));
		                } else {
		                	setText("");
		                	setGraphic(null);
		                }
		            }

					private Node getLevelGraphic(int level) {
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
		        };
		        return cell;
		    }
		});
		
	}
	
	private Node getLevelGraphic4Feature(int level, boolean own) {
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

	@Override
	protected void addAllElementToPane() {
		List<Node> modelNode = new ArrayList<>();
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.model));
		modelNode.add(modelListView);
		modelNode.add(modellBrowserVBox);
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		modelNode.add(new Label(StringValue.LabelAndHeaderTitle.code));
		
		List<Node> objectNode = new ArrayList<>();
		objectNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		objectNode.add(new Label(StringValue.LabelAndHeaderTitle.objects));
		objectNode.add(fmmlxObjectListView);
		objectNode.add(classBrowserVBox);
		parentsVBox.setPrefHeight(300);
		objectNode.add(parentsVBox);
		objectNode.add(abstractVBox);
		objectNode.add(delegatesToVBox);	
		
		List<Node> attributeNode = new ArrayList<>();
		attributeNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		attributeNode.add(new Label(StringValue.LabelAndHeaderTitle.attributes));
		
		attributeGridpane.add(fmmlxAttributeListView, 0, 0);
		attributeGridpane.add(new Label(StringValue.LabelAndHeaderTitle.slots), 0	, 1);
		attributeGridpane.add(slotListView, 0, 2);
		
		ColumnConstraints col1 = new ColumnConstraints();
	    col1.setPercentWidth(100);
	    attributeGridpane.getColumnConstraints().add(col1);
	    attributeGridpane.setVgap(8);
		
		attributeNode.add(attributeGridpane);
		attributeNode.add(attributeBrowserVBox);	

		List<Node> operationNode = new ArrayList<>();
		operationNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		operationNode.add(new Label(StringValue.LabelAndHeaderTitle.operations));
		operationNode.add(fmmlxOperationListView);
		operationNode.add(operationOutputVBox);
		operationNode.add(operationInputVBox);
		
		List<Node> associationNode = new ArrayList<>();
		associationNode.add(new Label(StringValue.LabelAndHeaderTitle.empty));
		associationNode.add(new Label(StringValue.LabelAndHeaderTitle.associations));
		associationNode.add(fmmlxAssociationListView);
		//visible.setText(StringValue.LabelAndHeaderTitle.visible);
		sourceVisible.setPadding(new Insets(3, 3, 3, 3));
		targetVisible.setPadding(new Insets(3, 3, 3, 3));
		sourceHBox.getChildren().addAll(sourceLabel, sourceListView);
		targetHBox.getChildren().addAll(targetLabel, targetListView);
		associationBooleanAttributesHBox.setPadding(new Insets(3,3,3,3));
		associationBooleanAttributesHBox.setSpacing(10);
		associationBooleanAttributesHBox.getChildren().addAll(labelSymmetric, checkSymmetric, labelTransitive, checkTransitive);
		sourceLabel.setText("Source: ");
		sourceLabel.setPadding(new Insets(3, 3, 3, 3));		
		targetLabel.setText("Target:  ");
		targetLabel.setPadding(new Insets(3, 3, 3, 3));
		visibleHBox.getChildren().addAll(new Label("Visibility - Source: "), sourceVisible, new Label("Target :"), targetVisible);
		associationNode.add(sourceHBox);
		associationNode.add(targetHBox);
		associationNode.add(visibleHBox);
		associationNode.add(associationBooleanAttributesHBox);
		
		//associationNode.add(associationBrowserVBox);
		//associationNode.add(sourceVBox);
		//associationNode.add(targetVBox);
		
		getGridControl().addNodesToGrid(mainGridPane,modelNode, 0);
		getGridControl().addNodesToGrid(mainGridPane,objectNode, 1);
		getGridControl().addNodesToGrid(mainGridPane,attributeNode, 2);
		getGridControl().addNodesToGrid(mainGridPane,operationNode, 3);
		getGridControl().addNodesToGrid(mainGridPane,associationNode, 4);
	}

	private void onOperationListViewNewValue(FmmlxOperation oldValue, FmmlxOperation selectedOperation) {
		if (selectedOperation!=null) {
			codeArea.setText(selectedOperation.getBody());	
		} else{
			codeArea.setText("");	
		}
	}
	
	private void modellBrowserListerner(ListView<String> modelListView2, String oldValue, String newValue) {
		
	}

	private void classBrowserTextFieldListener(String oldValue, String newValue) {
		
	}

	private void onObjectListViewNewValue(FmmlxObject oldValue, FmmlxObject selectedObject) {
		if (selectedObject != null ) {
			selection.put("OBJ", selectedObject.getName());
			fmmlxAttributeListView.getItems().clear();
			fmmlxAttributeListView.getItems().addAll(selectedObject.getAllAttributes());
			slotListView.getItems().clear();
			slotListView.getItems().addAll(selectedObject.getAllSlots());
			fmmlxOperationListView.getItems().clear();
			fmmlxOperationListView.getItems().addAll(selectedObject.getAllOperations());
			fmmlxAssociationListView.getItems().clear();
			fmmlxAssociationListView.getItems().addAll(selectedObject.getAllRelatedAssociations());
			metaClassTextField.clear();
			metaClassTextField.setText(selectedObject.getMetaClassName());
			delegatesToTextField.clear();
			if (selectedObject.getDelegatesTo()==null) {
				delegatesToTextField.setText("No delegation");
			} else {
				delegatesToTextField.setText(selectedObject.getDelegatesTo().toString());	
			}
			abstractCheckBox.setSelected(selectedObject.isAbstract());
			parentsListView.getItems().clear();
			parentsListView.getItems().addAll(selectedObject.getParentsPaths());
		}			
		

	}

	private void onAssociationListViewNewValue(FmmlxAssociation oldValue, FmmlxAssociation association) {
		sourceListView.getItems().clear();
		sourceListView.getItems().add(association);
		targetListView.getItems().clear();
		targetListView.getItems().add(association);
		checkSymmetric.setSelected(association.isSymmetric());
		checkTransitive.setSelected(association.isTransitive());
		sourceVisible.setSelected(association.isSourceVisible());
		targetVisible.setSelected(association.isTargetVisible());
	}

	private void onAbstractNewValue(Boolean oldValue, Boolean newValue) {
		communicator.setClassAbstract(activePackage.getID(), fmmlxObjectListView.getSelectionModel().getSelectedItem().getName(), abstractCheckBox.isSelected());
		activePackage.updateDiagram();
	}

	private void setColumnConstrain(GridPane gridPane) {
		
	}

	public void updateDiagram(FmmlxDiagram diagram) {
		
	}
	
	private void onModelListViewNewValue(String oldSelectedPath, String selectedPath) {
		if(selectedPath == null || selectedPath.equals(oldSelectedPath)) return;
		if(!models.containsKey(selectedPath)) {
			Integer newDiagramID=communicator.createDiagram(selectedPath, "Test", "");
			ClassBrowserPackageViewer tempViewer = new ClassBrowserPackageViewer(communicator, newDiagramID, selectedPath, this);
			models.put(selectedPath, tempViewer);
		}
		activePackage = models.get(selectedPath);
		activePackage.updateDiagram();
	}	

	private void onSlotListViewNewValue(ListView<String> modelListView2, FmmlxSlot oldValue, FmmlxSlot newValue) {}
	
	private void onAttributeListViewNewValue(FmmlxAttribute oldValue, FmmlxAttribute newValue) {
		if(newValue != null) {selection.put("ATT", newValue.getName());}
		fmmlxAttributeListView.setContextMenu(new BrowserAttributeContextMenu(fmmlxObjectListView, fmmlxAttributeListView, activePackage));
	}

	public void notifyModelHasLoaded() {
		Platform.runLater(() -> {
			Vector<FmmlxObject> objects = activePackage.getObjects();
			levelColorScheme = new LevelColorScheme.RedLevelColorScheme(objects);
			
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
		String oS = selection.get("OBJ");
		for(int i = 0; i < fmmlxObjectListView.getItems().size() && oS != null; i++) {
			if(oS.equals(fmmlxObjectListView.getItems().get(i).getName())) {
				oS = null;
				fmmlxObjectListView.getSelectionModel().select(fmmlxObjectListView.getItems().get(i));
			}
		}
		String aS = selection.get("ATT");
		for(int i = 0; i < fmmlxAttributeListView.getItems().size() && aS != null; i++) {
			if(aS.equals(fmmlxAttributeListView.getItems().get(i).getName())) {
				aS = null;
				fmmlxAttributeListView.getSelectionModel().select(fmmlxAttributeListView.getItems().get(i));
			}
		}
	}

}
