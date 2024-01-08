package tool.clients.fmmlxdiagrams.graphics.wizard;

import java.util.Collections;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import tool.clients.fmmlxdiagrams.Constraint;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.FmmlxOperation;
import tool.clients.fmmlxdiagrams.FmmlxProperty;
import tool.clients.fmmlxdiagrams.graphics.Condition;
import tool.clients.fmmlxdiagrams.graphics.Modification.Consequence;
import tool.clients.fmmlxdiagrams.graphics.NodeElement;
import tool.clients.fmmlxdiagrams.graphics.NodeLabel;
import tool.clients.fmmlxdiagrams.graphics.NodePath;

public class DefaultModificationDialog extends Dialog<DefaultModificationDialog.Result>{

	private ComboBox<FmmlxAttribute> attBox;
	private ComboBox<FmmlxOperation> opBox;
	private ComboBox<Constraint> constraintBox;	
	private ComboBox<Consequence> cBox;
	private TextField minField = new TextField();
	private TextField maxField = new TextField();
	private TextField refTextField;
	public static enum DataType {BOOLEAN, NUMBER, STRING}
	public static enum PropertyType {SLOT, OPVAL, CONSTRAINT}
	
	public DefaultModificationDialog(FmmlxObject obj, int level, final Class<?> condition, NodeElement nodeElement) {
		Vector<Consequence> cList = new Vector<>();
		PropertyType type = null;
		boolean isNumeric = condition == Condition.NumCompareSlotCondition.class;
		DataType dataType = null;
		if(condition == Condition.BooleanSlotCondition.class || condition == Condition.BooleanOpValCondition.class || condition == Condition.NumCompareSlotCondition.class) {
			dataType = DataType.BOOLEAN;
			cList.add(Consequence.SHOW_IF);
			cList.add(Consequence.SHOW_IF_NOT);		
		} else if(condition == Condition.ReadFromSlotCondition.class || condition == Condition.ReadFromOpValCondition.class) {
			dataType = DataType.STRING;
			if(nodeElement instanceof NodeLabel) cList.add(Consequence.READ_FROM_SLOT);
			if(nodeElement instanceof NodePath) cList.add(Consequence.SET_COLOR);		
		}
		if(condition == Condition.BooleanSlotCondition.class || condition == Condition.ReadFromSlotCondition.class || condition == Condition.NumCompareSlotCondition.class) {
			type = PropertyType.SLOT;
		} else if(condition == Condition.BooleanOpValCondition.class || condition == Condition.ReadFromOpValCondition.class) {
			type = PropertyType.OPVAL;
		} else if(condition == Condition.BooleanConstraintCondition.class) {
			type = PropertyType.CONSTRAINT;
			dataType = DataType.BOOLEAN;
		} else {
			throw new RuntimeException("Condition not recognized.");
		}
		
		
		
		int y = 0;
		GridPane grid = new GridPane();
		Label refLabel = new Label( type == PropertyType.SLOT?"Attribute":
									type == PropertyType.OPVAL?"Operation":"Constraint");
		grid.setHgap(6); grid.setVgap(6);
		grid.add(refLabel, 0, y);
		
		if(obj == null) {
			refTextField = new TextField();
			grid.add(refTextField, 1, y);
		} else {
			if(type == PropertyType.SLOT) {
				Vector<FmmlxAttribute> atts = new Vector<>();
				for(FmmlxAttribute a : obj.getAllAttributes()) {
					if(a.getLevel() == level && 
							("Boolean".equals(a.getType()) && dataType == DataType.BOOLEAN ||
							 "Integer".equals(a.getType()) && dataType == DataType.NUMBER ||
							 "Float".equals(a.getType()) && dataType == DataType.NUMBER ||
							 dataType == DataType.STRING)) {
						atts.add(a);
					}
				}
				Collections.sort(atts);
				attBox = new ComboBox<>(FXCollections.observableArrayList(atts));
				grid.add(attBox, 1, y);
			} else if(type == PropertyType.OPVAL) {
				Vector<FmmlxOperation> ops = new Vector<>();
				for(FmmlxOperation o : obj.getAllOperations()) {
					if(o.getLevel() == level && o.isMonitored() && 
							("Root::XCore::Boolean".equals(o.getType()) && dataType == DataType.BOOLEAN ||
							 "Root::XCore::Integer".equals(o.getType()) && dataType == DataType.NUMBER ||
							 "Root::XCore::Float".equals(o.getType()) && dataType == DataType.NUMBER ||
							 dataType == DataType.STRING)) {
						ops.add(o);
					}
				}
				Collections.sort(ops);
				opBox = new ComboBox<>(FXCollections.observableArrayList(ops));
				grid.add(opBox, 1, y);
			} else { // if(slot == PropertyType.CONSTRAINT) 
				Vector<Constraint> cons = new Vector<>();
				for(Constraint c : obj.getAllConstraints()) {
					if(c.getLevel() == level) {
						cons.add(c);
					}
				}
				Collections.sort(cons);
				constraintBox = new ComboBox<>(FXCollections.observableArrayList(cons));
				grid.add(constraintBox, 1, y);
			}
			
			y++;
			
			if(isNumeric) {
				grid.add(new Label("min"), 0, y);
				grid.add(minField, 1, y);
				grid.add(new Label("max"), 0, y+1);
				grid.add(maxField, 1, y+1);
				y += 2;
			}
			
			Label cLabel = new Label("Consequence");
			cBox = new ComboBox<>(FXCollections.observableArrayList(cList));
			grid.add(cLabel, 0, y);
			grid.add(cBox, 1, y);
			
			getDialogPane().setContent(grid);
			getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

			setResultConverter((dialogButton) -> {
	            if (dialogButton != null && dialogButton.getButtonData() == ButtonData.OK_DONE){
	            	if(condition == Condition.BooleanSlotCondition.class) {
	            		return new Result(
            				attBox.getValue(),
            				null, 
            				cBox.getValue());
	        		} else if(condition == Condition.BooleanOpValCondition.class) {
	            		return new Result(
            				opBox.getValue(),
            				null,
            				cBox.getValue());
	        		} else if(condition == Condition.BooleanConstraintCondition.class) {
	            		return new Result(
            				constraintBox.getValue(),
            				null,
            				cBox.getValue());
	        		} else if(condition == Condition.ReadFromSlotCondition.class) {
	            		return new Result(
            				attBox.getValue(),
            				null, 
            				cBox.getValue());
	        		} else if(condition == Condition.ReadFromOpValCondition.class) {
	            		return new Result(
            				opBox.getValue(),
            				null,
            				cBox.getValue());
	        		} else if(condition == Condition.NumCompareSlotCondition.class) {
	            		return new Result(
	            			attBox.getValue(),
            				null,
            				cBox.getValue(),
            				Double.parseDouble(minField.getText()),
            				Double.parseDouble(maxField.getText()));
	        		} else return null;
	            } else {
	        	    return null;
	            }
		    });			
		}				
	}	
	
	public class Result {
		public final FmmlxProperty property;
		public final String ref;
		public final Consequence consequence;
		public final String match;
		public final Double min;
		public final Double max;
		
		private Result(FmmlxProperty property, String ref, Consequence consequence, String match,
				Double min, Double max) {
			super();
			this.property = property;
			this.ref = ref;
			this.consequence = consequence;
			this.match = match;
			this.min = min;
			this.max = max;
		}
		
		private Result(FmmlxProperty property, String ref, Consequence consequence) {
			this(property, ref, consequence, null, null, null);
		}
		
		private Result(FmmlxProperty property, String ref, Consequence consequence, String match) {
			this(property, ref, consequence, match, null, null);
		}
		
		private Result(FmmlxProperty property, String ref, Consequence consequence, Double min, Double max) {
			this(property, ref, consequence, null, min, max);
		}
		
		public String getPropertyName() {
			return property.getName();
		}

		public Double getNumMin() { return min;}
		public Double getNumMax() { return max;}
	}
}
