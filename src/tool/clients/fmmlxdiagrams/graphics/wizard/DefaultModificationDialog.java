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
	private ComboBox<Consequence> cBox;
	private TextField refTextField;
	public static enum DataType {BOOLEAN, NUMBER, STRING}
	public static enum PropertyType {SLOT, OPVAL, CONSTRAINT}
	
	public DefaultModificationDialog(FmmlxObject type, int level, final Class<?> condition, NodeElement nodeElement) {
		Vector<Consequence> cList = new Vector<>();
		PropertyType slot = null;
		
		DataType dataType = null;
		if(condition == Condition.BooleanSlotCondition.class || condition == Condition.BooleanOpValCondition.class) {
			dataType = DataType.BOOLEAN;
			cList.add(Consequence.SHOW_IF);
			cList.add(Consequence.SHOW_IF_NOT);			
		} else if(condition == Condition.ReadFromSlotCondition.class || condition == Condition.ReadFromOpValCondition.class) {
			dataType = DataType.STRING;
			if(nodeElement instanceof NodeLabel) cList.add(Consequence.READ_FROM_SLOT);
			if(nodeElement instanceof NodePath) cList.add(Consequence.SET_COLOR);		
		}
		if(condition == Condition.BooleanSlotCondition.class || condition == Condition.ReadFromSlotCondition.class) {
			slot = PropertyType.SLOT;
		} else if(condition == Condition.BooleanOpValCondition.class || condition == Condition.ReadFromOpValCondition.class) {
			slot = PropertyType.OPVAL;
		} else if(condition == Condition.BooleanConstraintCondition.class) {
			slot = PropertyType.CONSTRAINT;
		} else {
			throw new RuntimeException("Condition not recognized.");
		}
		
		GridPane grid = new GridPane();
		Label refLabel = new Label( slot == PropertyType.SLOT?"Attribute":
									slot == PropertyType.OPVAL?"Operation":"Constraint");
		grid.add(refLabel, 0, 0);
		
		if(type == null) {
			refTextField = new TextField();
			grid.add(refTextField, 1, 0);
		} else {
			if(slot == PropertyType.SLOT) {
				Vector<FmmlxAttribute> atts = new Vector<>();
				for(FmmlxAttribute a : type.getAllAttributes()) {
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
				grid.add(attBox, 1, 0);
			} else if(slot == PropertyType.OPVAL) {
				Vector<FmmlxOperation> ops = new Vector<>();
				for(FmmlxOperation o : type.getAllOperations()) {
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
				grid.add(opBox, 1, 0);
			} else { // if(slot == PropertyType.CONSTRAINT) 
				System.err.println("Dialog for Constraints not yet implemeted");
			}
			
			Label cLabel = new Label("Consequence");
			cBox = new ComboBox<>(FXCollections.observableArrayList(cList));
			grid.add(cLabel, 0, 1);
			grid.add(cBox, 1, 1);
			
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
	}
}
