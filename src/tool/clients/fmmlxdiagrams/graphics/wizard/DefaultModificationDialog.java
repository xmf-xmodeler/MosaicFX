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
import tool.clients.fmmlxdiagrams.graphics.Condition;
import tool.clients.fmmlxdiagrams.graphics.Modification.Consequence;

public class DefaultModificationDialog extends Dialog<DefaultModificationDialog.Result>{

//	private boolean slot;
	private ComboBox<FmmlxAttribute> attBox;
	private ComboBox<FmmlxOperation> opBox;
	private ComboBox<Consequence> cBox;
	private TextField refTextField;
	public static enum DataType {BOOLEAN, NUMBER, STRING}
	
	public DefaultModificationDialog(FmmlxObject type, int level, final Class<?> condition) {
		Vector<Consequence> cList = new Vector<>();
		Boolean slot = null;
		
		DataType dataType = null;
		if(condition == Condition.BooleanSlotCondition.class || condition == Condition.BooleanOpValCondition.class) {
			dataType = DataType.BOOLEAN;
			cList.add(Consequence.SHOW_IF);
			cList.add(Consequence.SHOW_IF_NOT);			
		}
		if(condition == Condition.BooleanSlotCondition.class) {
			slot = true;
		}
		if(condition == Condition.BooleanOpValCondition.class) {
			slot = false;
		}
		
		GridPane grid = new GridPane();
		Label refLabel = new Label(slot?"Attribute":"Operation");
		grid.add(refLabel, 0, 0);
		
		if(type == null) {
			refTextField = new TextField();
			grid.add(refTextField, 1, 0);
		} else {
			if(slot) {
				Vector<FmmlxAttribute> atts = new Vector<>();
				for(FmmlxAttribute a : type.getAllAttributes()) {
					if(a.getLevel() == level && 
							("Boolean".equals(a.getType()) && dataType == DataType.BOOLEAN ||
							 "Integer".equals(a.getType()) && dataType == DataType.NUMBER ||
							 "Float".equals(a.getType()) && dataType == DataType.NUMBER ||
							 "String".equals(a.getType()) && dataType == DataType.STRING)) {
						atts.add(a);
					}
				}
				Collections.sort(atts);
				attBox = new ComboBox<>(FXCollections.observableArrayList(atts));
				grid.add(attBox, 1, 0);
			} else {
				Vector<FmmlxOperation> ops = new Vector<>();
				for(FmmlxOperation o : type.getAllOperations()) {
					if(o.getLevel() == level && o.isMonitored() && 
							("Boolean".equals(o.getType()) && dataType == DataType.BOOLEAN ||
							 "Integer".equals(o.getType()) && dataType == DataType.NUMBER ||
							 "Float".equals(o.getType()) && dataType == DataType.NUMBER ||
							 "String".equals(o.getType()) && dataType == DataType.STRING)) {
						ops.add(o);
					}
				}
				Collections.sort(ops);
				opBox = new ComboBox<>(FXCollections.observableArrayList(ops));
				grid.add(opBox, 1, 0);
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
	            				null, null, 
	            				cBox.getValue(),
	            				null, null, null);
	        		} else if(condition == Condition.BooleanOpValCondition.class) {
	            		return new Result(
	            				null,
	            				opBox.getValue(),
	            				null,
	            				cBox.getValue(),
	            				null, null, null);
	        		} else return null;
	            } else {
	        	    return null;
	            }
		    });
			
		}
		
		
		
		
	}
	
	
	public class Result {
		public final FmmlxAttribute att;
		public final FmmlxOperation op;
		public final String ref;
		public final Consequence consequence;
		public final String match;
		public Double min;
		public Double max;
		
		public Result(FmmlxAttribute att, FmmlxOperation op, String ref, Consequence consequence, String match,
				Double min, Double max) {
			super();
			this.att = att;
			this.op = op;
			this.ref = ref;
			this.consequence = consequence;
			this.match = match;
			this.min = min;
			this.max = max;
		}
		
		
	}
}
