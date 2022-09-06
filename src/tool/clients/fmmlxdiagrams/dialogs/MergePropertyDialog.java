package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Collections;
import java.util.Vector;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import xos.Value;

public class MergePropertyDialog extends Dialog<MergePropertyDialog.Result>{
	final FmmlxObject mergeIntoClass;
	final AbstractPackageViewer diagram;
	final TableView<MergePropertyDialog.Row> tableView = new TableView<>();

	public MergePropertyDialog(FmmlxObject mergeIntoClass, AbstractPackageViewer diagram) {
		super();
		this.mergeIntoClass = mergeIntoClass;
		this.diagram = diagram;
		
		layoutContent();
		initValues();
		
		setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) 
				return new MergePropertyDialog.Result(); 
			else 
				return null; });
	}

	private final void layoutContent() {
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
	    TableColumn<MergePropertyDialog.Row, String> ownerColumn = new TableColumn<>("Owner");
	    TableColumn<MergePropertyDialog.Row, String> nameColumn = new TableColumn<>("Name");
	    TableColumn<MergePropertyDialog.Row, String> levelColumn = new TableColumn<>("Level");
	    TableColumn<MergePropertyDialog.Row, String> propTypeColumn = new TableColumn<>("Property Type");
	    TableColumn<MergePropertyDialog.Row, String> valTypeColumn = new TableColumn<>("Value Type");
	    TableColumn<MergePropertyDialog.Row, Action> resolveColumn = new TableColumn<>("Resolve");
	    TableColumn<MergePropertyDialog.Row, String> mergeWithColumn = new TableColumn<>("Merge With");
	    TableColumn<MergePropertyDialog.Row, String> keepAsColumn = new TableColumn<>("Keep As");
	    
	    /*DO NOT DELETE: this is how the lambda expression looks like extended:
	     *  ownerColumn.setCellValueFactory(new Callback<CellDataFeatures<MergePropertyDialog.Row, String>, ObservableValue<String>>() {
		 *	    @Override public ObservableValue<String> call(CellDataFeatures<MergePropertyDialog.Row, String> f) {
		 *		    return new ReadOnlyObjectWrapper<String>("myValue");}});*/
	    
	    ownerColumn.setCellValueFactory(dataFeature -> {
	    	return new ReadOnlyObjectWrapper<String>(dataFeature.getValue().getOwner()); });
	    nameColumn.setCellValueFactory(dataFeature -> {
	    	return new ReadOnlyObjectWrapper<String>(dataFeature.getValue().getName()); });
	    levelColumn.setCellValueFactory(dataFeature -> {
	    	return new ReadOnlyObjectWrapper<String>(dataFeature.getValue().getLevel()); });
	    propTypeColumn.setCellValueFactory(dataFeature -> {
	    	return new ReadOnlyObjectWrapper<String>(dataFeature.getValue().getPropertyType()); });
	    valTypeColumn.setCellValueFactory(dataFeature -> {
	    	return new ReadOnlyObjectWrapper<String>(dataFeature.getValue().getValueType()); });
	    resolveColumn.setCellValueFactory(dataFeature -> {
	    	return new ReadOnlyObjectWrapper<Action>(dataFeature.getValue().action); });
	    mergeWithColumn.setCellValueFactory(dataFeature -> {
	    	return new ReadOnlyObjectWrapper<String>(dataFeature.getValue().mergeWith); });
	    
	    /*ownerColumn.setCellFactory(column -> {
	        return new TableCell<MergePropertyDialog.Row, String>() {
	        	@Override
	            protected void updateItem(String text, boolean empty) {
	                super.updateItem(text, empty);
	                if (text != null) {
	                	setText(text);
//	                	Row row = getTableView().getItems().get(getIndex());
	                	setStyle(tableView.getItems().get(getIndex()).getColor());
	                	
//	                	if(o.isAbstract()) setText("(" + o.getName() + " ^"+ o.getMetaClassName() + "^ " + ")"); else setText(o.getName()+ " ^"+ o.getMetaClassName() + "^");            	
//	                    setGraphic(ModelBrowser.getClassLevelGraphic(o.getLevel()));
	                } else { setText(""); setGraphic(null); }
	            }
	        };
	    });*/
	    
	    Callback<TableColumn<MergePropertyDialog.Row, String>, TableCell<MergePropertyDialog.Row, String>> defaultStringCellFactory = column -> {
	        return new TableCell<MergePropertyDialog.Row, String>() {
	            @Override
	            protected void updateItem(String text, boolean empty) {
	                super.updateItem(text, empty);
	                if (text != null) {
	                	setText(text);
//	                	System.err.println(getIndex() + " ? " + tableView.getSelectionModel().getSelectedIndices());
//	                	if(!(tableView.getSelectionModel().getSelectedItem() == tableView.getItems().get(getIndex()))) {
	                		setStyle(tableView.getItems().get(getIndex()).getColor());
//	                	} else {
//	                		System.err.println("none");
//	                	}
	                } else { setText(""); setGraphic(null); }
	            }
	        };
	    };

	    ownerColumn.setCellFactory(defaultStringCellFactory);
	    nameColumn.setCellFactory(defaultStringCellFactory);
	    levelColumn.setCellFactory(defaultStringCellFactory);
	    propTypeColumn.setCellFactory(defaultStringCellFactory);
	    valTypeColumn.setCellFactory(defaultStringCellFactory);
	    
	    resolveColumn.setCellFactory(column -> {
            
            TableCell<MergePropertyDialog.Row, Action> cell = new TableCell<MergePropertyDialog.Row, Action>() {
                @Override
                protected void updateItem(Action action, boolean empty) {
                    super.updateItem(action, empty);
                    
                    if (empty) {
                        setGraphic(null);
                    } else {
                    	ComboBox<Action> combo = new ComboBox<>();
                    	Row row = tableView.getItems().get(getIndex());
                    	boolean isTargetClass = row.getOwner().equals(mergeIntoClass.getName());
//                    	Vector<Action> availableActions = new Vector<>();
                    	
                    	combo.getItems().addAll(isTargetClass?new Action[] {Action.KEEP}:Action.values());
                        combo.setValue(action);
                        combo.setOnAction(e -> {
                        	tableView.getItems().get(getIndex()).action = combo.getValue();
                        	refresh();});
                        setGraphic(combo);
                        setPadding(new Insets(0));
                    }
                }
            };
            return cell ;
        });
	    
	    mergeWithColumn.setCellFactory(column -> {
            ComboBox<String> combo = new ComboBox<>();
            combo.getItems().addAll(getAvailableMergeIntos());
            TableCell<MergePropertyDialog.Row, String> cell = new TableCell<MergePropertyDialog.Row, String>() {
                @Override
                protected void updateItem(String otherProperty, boolean empty) {
                    super.updateItem(otherProperty, empty);
                    if (empty) {
                        setGraphic(null);
                    } else if(tableView.getItems().get(getIndex()).action != Action.MERGE_INTO){
                    	setGraphic(null);
                    } else {
                        combo.setValue(otherProperty);
                        combo.setOnAction(e -> {
                        	getTableView().getItems().get(getIndex()).mergeWith = combo.getValue();
                        	refresh();});
                        setGraphic(combo);
                        setPadding(new Insets(0));
                    }
                }
            };
            return cell ;
        });
	    
	    keepAsColumn.setCellFactory(column -> {
            TextField textField = new TextField();
            textField.setText("not yet implemented");
            TableCell<MergePropertyDialog.Row, String> cell = new TableCell<MergePropertyDialog.Row, String>() {
                @Override
                protected void updateItem(String otherProperty, boolean empty) {
                    super.updateItem(otherProperty, empty);
                    if (empty) {
                        setGraphic(null);
                    } else if(tableView.getItems().get(getIndex()).action != Action.KEEP_AS){
                    	setGraphic(null);
                    } else {
                    	textField.setText(otherProperty);
                    	textField.setOnKeyReleased(event -> {
                    		Row row = tableView.getItems().get(getIndex());
//                    		System.err.println(row.keepAs+" <== "+textField.getText());
                    		row.keepAs = textField.getText();
                    		getDialogPane().lookupButton(ButtonType.OK).setDisable(!dataIsValid());
					    });
//                        combo.setOnAction(e -> {
//                        	getTableView().getItems().get(getIndex()).mergeWith = combo.getValue();
//                        	refresh();});
                        setGraphic(textField);
                        setPadding(new Insets(0));
                    }
                }
            };
            return cell ;
        });
	    
	    keepAsColumn.setCellValueFactory(dataFeature-> new ReadOnlyObjectWrapper<>(dataFeature.getValue().keepAs));
//	    keepAsColumn.setCellValueFactory(new PropertyValueFactory<>("keepAs"));
//	    keepAsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
	    keepAsColumn.setOnEditCommit(event -> {
	    	System.err.print(event.getRowValue().keepAs+" <== "+event.getNewValue());
//	        String newValue = event.getNewValue();
//	        String oldValue = event.getOldValue();
	        event.getRowValue().keepAs = event.getNewValue();
//	        lookupTable.put(newKey,lookupTable.get(oldKey));
//	        lookupTable.remove(oldKey);
//	        updateOBList(lookupTable);
	    });
	    keepAsColumn.setEditable(true);

	    tableView.getColumns().add(ownerColumn);
	    tableView.getColumns().add(nameColumn);
	    tableView.getColumns().add(levelColumn);
	    tableView.getColumns().add(propTypeColumn);
	    tableView.getColumns().add(valTypeColumn);
	    tableView.getColumns().add(resolveColumn);
	    tableView.getColumns().add(mergeWithColumn);
	    tableView.getColumns().add(keepAsColumn);

	    ownerColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
	    nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
	    levelColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.05));
	    propTypeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
	    valTypeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
	    resolveColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
	    mergeWithColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
	    keepAsColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
	    	    
	    dialogPane.setContent(tableView);
	    setTitle("Merge Properties into " + mergeIntoClass.getName());
	    dialogPane.setPrefSize(1200, 500);
	}

	private void refresh() {
		tableView.refresh();
		getDialogPane().lookupButton(ButtonType.OK).setDisable(!dataIsValid());
	}
	
	private boolean dataIsValid() {
		boolean ok = true;
		Vector<Row> futureFeaturesInTarget = new Vector<>();
		for(Row row : tableView.getItems()) {
			if(row.action == Action.KEEP && row.getOwner().equals(mergeIntoClass.getName())) 
				futureFeaturesInTarget.add(row);
			if(row.action == Action.PULL_UP) 
				futureFeaturesInTarget.add(row);
		}
		for(Row row : tableView.getItems()) {
			if(row.action == Action.UNRESOLVED) ok = false;
			if(!row.getOwner().equals(mergeIntoClass.getName())) {
				if(row.action == Action.KEEP) {
					for(Row row2 : futureFeaturesInTarget) {
						if(row.getName().equals(row2.getName())) {
						ok = false;
						System.err.println("Conflict: KEEP: "+row.getName()); }
					}	
				} else if(row.action == Action.KEEP_AS) {
					for(Row row2 : futureFeaturesInTarget) {
						if(row.keepAs.equals(row2.getName())) {
						ok = false;
						System.err.println("Conflict: KEEP_AS: "+row.getName()); }
					}	
				} else if(row.action == Action.MERGE_INTO) {
					boolean found = false;
					for(Row row2 : futureFeaturesInTarget) {
						if(row.mergeWith != null && row.mergeWith.equals(row2.getName()))
							found = true;
					}
					if(!found) {
						ok = false;
						System.err.println("Conflict: MERGE_INTO: "+row.getName());
					}
				}
//			}
//			
//			if(row.action == Action.PULL_UP) {
//				boolean duplicates = false;
//
//				if(duplicates) ok = false;
//			}
//			
//			
//			if(row.action == Action.KEEP || row.action == Action.PULL_UP) {
//				boolean duplicates = false;
//				for(Row row2 : tableView.getItems()) {
//					if(row != row2 && 
//						row.getName().equals(row2.getName()) &&
//						(row.action == Action.KEEP || row.action == Action.PULL_UP))
//						duplicates = true;
//				}
//				if(duplicates) ok = false;
			}
		}
		return ok;
	}

	private Vector<String> getAvailableMergeIntos() {
		Vector<String> vec = new Vector<>();
		for(Row row : tableView.getItems()) {
			if(row.action == Action.KEEP || row.action == Action.PULL_UP) {
				vec.add(row.getName());
			}
		}
		return vec;
	}

	private final void initValues() {
//		tableView.getItems().add(new TestRow("Car",   "maxPax",    "1", "Attribute", "Integer"));
//		tableView.getItems().add(new TestRow("Lorry", "maxWeight", "1", "Attribute", "Float"));
//		tableView.getItems().add(new TestRow("Car",   "lenght",    "1", "Attribute", "Float"));
//		tableView.getItems().add(new TestRow("Lorry", "length",    "1", "Attribute", "Float"));
//		tableView.getItems().add(new TestRow("Lorry", "width",     "1", "Attribute", "Float"));	
		
		addProperties(mergeIntoClass);
		for(FmmlxObject o : mergeIntoClass.getInstances()) {
			addProperties(o);
		}		

	    refresh();
	}
	
	private void addProperties(FmmlxObject object) {
		for(FmmlxAttribute att : object.getOwnAttributes()) {
			tableView.getItems().add(new AttributeRow(att));
		}
//		for(FmmlxOperation att : object.getOwnOperations()) {
//			tableView.getItems().add(new OperationRow(att));
//		}
		
	}

	public class Result {

		public Value[] createMessage() {
			Vector<Row> rows0 = new Vector<>(tableView.getItems());
			Collections.sort(rows0);
			Vector<Row> rows = new Vector<>(); 
			for(Row row : rows0) {
				if(row.action != Action.KEEP) rows.add(row);
			}
			Value[] resolutions = new Value[rows.size()];
			for(int r = 0; r < rows.size(); r++) {
				Row row = rows.get(r);
				resolutions[r] = new Value(new Value[]{
						new Value("Attribute"), // for now only Attributes
						new Value(row.getOwner()),
						new Value(row.getName()),
						new Value(row.action.toString()),
						new Value(
							row.action == Action.MERGE_INTO?row.mergeWith:
							row.action == Action.KEEP_AS?row.keepAs:"void")});
			}
			
			return new Value[]{
					FmmlxDiagramCommunicator.getNoReturnExpectedMessageID(diagram.getID()),
					new Value(mergeIntoClass.getName()),
					new Value(resolutions)};
		}

	}
	
	private abstract class Row implements Comparable<Row>{
		public abstract String getName();
		public abstract String getOwner();
		public abstract String getLevel();
		public abstract String getPropertyType();
		public abstract String getValueType();
		public Action action = Action.UNRESOLVED;;
		public String mergeWith;
		public String keepAs = "";

		public final String getColor() {
			if(getOwner().equals(mergeIntoClass.getName())) return "-fx-background-color: #aaffdd;";
			else if(action == Action.UNRESOLVED) return "-fx-background-color: #ffbbbb;";
			else if((action == Action.KEEP || action == Action.KEEP_AS || action == Action.PULL_UP)) {
				boolean noDuplicates = true;
				String newName = action == Action.KEEP_AS?keepAs:getName();
				
				for(Row row : tableView.getItems()) {
					if(row != this && 
							row.getName().equals(newName) &&
							(row.action == Action.KEEP && getOwner().equals(mergeIntoClass.getName()) 
							|| row.action == Action.PULL_UP))
						noDuplicates = false;
				}
				
				if(noDuplicates) {
					return "-fx-background-color: #aaffdd;";
				} else {
					return "-fx-background-color: #ffbbbb;";
				}
			} else if(action == Action.DROP) return "-fx-background-color: #ffeebb;";
			else if(action == Action.MERGE_INTO) return "-fx-background-color: #eeeeee;";
			return "-fx-background-color: #ffffff;";
		}
		
		@Override
		public final int compareTo(Row that) {
			if(this.action == that.action) {
				if(this.getOwner().equals(that.getOwner())) {
					return this.getName().compareTo(that.getName());
				} else {
					return this.getOwner().compareTo(that.getOwner());
				}
			}
			return this.action.compareTo2(that.action);
		}	
	}
	
	private class AttributeRow extends Row {
		private final FmmlxAttribute property;

		public AttributeRow(FmmlxAttribute property) {
			this.property = property;
			if(getOwner().equals(mergeIntoClass.getName())) {
				this.action = Action.KEEP;
			}
		}

		@Override public String getName() { return property.getName();}
		@Override public String getOwner() { 
			String s = property.getOwnerPath();
			String[] S = s.split("::");
			return S[S.length-1];}
		@Override public String getLevel() { return property.getLevel()+"";}
		@Override public String getPropertyType() { return "Attribute";}
		@Override public String getValueType() { return property.getTypeShort();}
	}
	
	private class OperationRow extends Row {
		private final FmmlxOperation property;

		public OperationRow(FmmlxOperation property) {
			this.property = property;
		}

		@Override public String getName() { return property.getName();}
		@Override public String getOwner() { 
			String s = property.getOwner();
			String[] S = s.split("::");
			return S[S.length-1];}
		@Override public String getLevel() { return property.getLevel()+"";}
		@Override public String getPropertyType() { return "Operation";}
		@Override public String getValueType() { 
			String s = property.getType();
			String[] S = s.split("::");
			return S[S.length-1];}	}
			
	private class TestRow extends Row{
		@Deprecated private final String name;
		@Deprecated private final String owner;
		@Deprecated private final String level;
		@Deprecated private final String propertyType;
		@Deprecated private final String valueType;
		
		public TestRow(String owner, String name, String level, String propertyType,
				String valueType) {
			this.owner = owner;
			this.name = name;
			this.level = level;
			this.propertyType = propertyType;
			this.valueType = valueType;
		}

		public String getName() {return name;}
		public String getOwner() {return owner;}
		public String getLevel() {return level;}
		public String getPropertyType() {return propertyType;}
		public String getValueType() {return valueType;}

	}

	private enum Action {KEEP, KEEP_AS, DROP, PULL_UP, MERGE_INTO, UNRESOLVED;

	public int compareTo2(Action that) {
		if(this == DROP && that != DROP) return -1;
		if(that == DROP && this != DROP) return 1;
		if(this == KEEP && that != KEEP) return -1;
		if(that == KEEP && this != KEEP) return 1;
		if(this == KEEP_AS && that != KEEP_AS) return -1;
		if(that == KEEP_AS && this != KEEP_AS) return 1;
		if(this == PULL_UP && that != PULL_UP) return -1;
		if(that == PULL_UP && this != PULL_UP) return 1;
		return 0;
	}} 
}
