package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Collections;
import java.util.Vector;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
            ComboBox<Action> combo = new ComboBox<>();
            combo.getItems().addAll(Action.values());
            TableCell<MergePropertyDialog.Row, Action> cell = new TableCell<MergePropertyDialog.Row, Action>() {
                @Override
                protected void updateItem(Action action, boolean empty) {
                    super.updateItem(action, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
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
                    } else if(tableView.getItems().get(getIndex()).action != Action.MERGE_WITH){
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
	    
	    
	    tableView.getColumns().add(ownerColumn);
	    tableView.getColumns().add(nameColumn);
	    tableView.getColumns().add(levelColumn);
	    tableView.getColumns().add(propTypeColumn);
	    tableView.getColumns().add(valTypeColumn);
	    tableView.getColumns().add(resolveColumn);
	    tableView.getColumns().add(mergeWithColumn);

	    ownerColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
	    nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
	    levelColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.08));
	    propTypeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
	    valTypeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
	    resolveColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
	    mergeWithColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
	    	    
	    dialogPane.setContent(tableView);
	    setTitle("Merge Properties into " + mergeIntoClass.getName());
	    dialogPane.setPrefSize(1100, 500);
	}

	private void refresh() {
		tableView.refresh();
		getDialogPane().lookupButton(ButtonType.OK).setDisable(!dataIsValid());
	}
	
	private boolean dataIsValid() {
		boolean ok = true;
		for(Row row : tableView.getItems()) {
			if(row.action == Action.UNRESOLVED) ok = false;
			if(row.action == Action.KEEP || row.action == Action.MOVE_UP) {
				boolean duplicates = false;
				for(Row row2 : tableView.getItems()) {
					if(row != row2 && 
						row.getName().equals(row2.getName()) &&
						(row.action == Action.KEEP || row.action == Action.MOVE_UP))
						duplicates = true;
				}
				if(duplicates) ok = false;
			}
		}
		return ok;
	}

	private Vector<String> getAvailableMergeIntos() {
		Vector<String> vec = new Vector<>();
		for(Row row : tableView.getItems()) {
			if(row.action == Action.KEEP || row.action == Action.MOVE_UP) {
				vec.add(row.getName());
			}
		}
		return vec;
	}

	private final void  initValues() {
		tableView.getItems().add(new TestRow("Car",   "maxPax",    "1", "Attribute", "Integer"));
		tableView.getItems().add(new TestRow("Lorry", "maxWeight", "1", "Attribute", "Float"));
		tableView.getItems().add(new TestRow("Car",   "lenght",    "1", "Attribute", "Float"));
		tableView.getItems().add(new TestRow("Lorry", "length",    "1", "Attribute", "Float"));
		tableView.getItems().add(new TestRow("Lorry", "width",     "1", "Attribute", "Float"));	
		
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
		for(FmmlxOperation att : object.getOwnOperations()) {
			tableView.getItems().add(new OperationRow(att));
		}
		
	}

	public class Result {

		public Value[] createMessage() {
			Vector<Row> rows = new Vector<>(tableView.getItems());
			Collections.sort(rows);
			Value[] resolutions = new Value[rows.size()];
			for(int r = 0; r < rows.size(); r++) {
				Row row = rows.get(r);
				resolutions[r] = new Value(new Value[]{
						new Value(row.getOwner()),
						new Value(row.getName()),
						new Value(row.action.toString()),
						new Value(row.action == Action.MERGE_WITH?row.mergeWith:"void")});
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

		public final String getColor() {
			if(action == Action.UNRESOLVED) return "-fx-background-color: #ffbbbb;";
			else if(action == Action.KEEP || action == Action.MOVE_UP) {
				boolean noDuplicates = true;
				for(Row row : tableView.getItems()) {
					if(row != this && 
							row.getName().equals(this.getName()) &&
							(row.action == Action.KEEP || row.action == Action.MOVE_UP))
						noDuplicates = false;
				}
				if(noDuplicates) {
					return "-fx-background-color: #aaffdd;";
				} else {
					return "-fx-background-color: #ffbbbb;";
				}
			} else if(action == Action.DROP) return "-fx-background-color: #ffeebb;";
			else if(action == Action.MERGE_WITH) return "-fx-background-color: #eeeeee;";
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

	private enum Action {KEEP, DROP, MOVE_UP, MERGE_WITH, UNRESOLVED;

	public int compareTo2(Action that) {
		if(this == DROP && that != DROP) return -1;
		if(that == DROP && this != DROP) return 1;
		if(this == KEEP && that != KEEP) return -1;
		if(that == KEEP && this != KEEP) return 1;
		if(this == MOVE_UP && that != MOVE_UP) return -1;
		if(that == MOVE_UP && this != MOVE_UP) return 1;
		return 0;
	}} 
}
