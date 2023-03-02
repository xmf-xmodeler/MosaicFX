package tool.clients.fmmlxdiagrams.instancewizard;

import java.text.NumberFormat;
import java.util.Vector;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxEnum;

public class EnumWeighedGenerator extends Generator {
	
	private VBox pane;
	public static String name = "Weighed (Enum)";
	private FmmlxAttribute att;

	private Vector<String> items;
	private Vector<TextField> weightFields = new Vector<>();
	private Vector<TextField> percentFields = new Vector<>();
	private Vector<String> problems = new Vector<>();
	
	public EnumWeighedGenerator(FmmlxAttribute att, final AbstractPackageViewer diagram) {
		this.att = att;
		FmmlxEnum Enum = null;
		for(FmmlxEnum Enum2 : diagram.getEnums()) {
			if(Enum2.getName().equals(att.getTypeShort())) {
				Enum = Enum2;
			}
		}
		
		if(Enum == null) throw new RuntimeException("Enum " + att.getTypeShort() + "not found");
		items = Enum.getItems();
		
		GridPane weightPane = new GridPane();
		weightPane.setHgap(5.);
		weightPane.setVgap(5.);
		
		int itemCount = 0;
		for(String item : items) {
			Label label = new Label(item);
			TextField weightField = new TextField("1");
			weightFields.add(weightField);
			weightField.textProperty().addListener((obs, oldVal, newVal) -> {updatePercents();});
			weightField.setMaxWidth(100);
			
			TextField percentField = new TextField("void");
			percentFields.add(percentField);
			percentField.setDisable(true);
			percentField.setMaxWidth(100);


			weightPane.add(label, 0, itemCount);
			weightPane.add(weightField, 1, itemCount);
			weightPane.add(percentField, 2, itemCount);
						
			itemCount++;
		}
		
		ScrollPane weightScrollPane = new ScrollPane(weightPane);
		
		pane = new VBox(
				new Label("Choose weights for attribute " + att.getName() + ":"),
				weightScrollPane);
		VBox.setVgrow(weightScrollPane, Priority.ALWAYS);
		pane.setSpacing(5.);
		pane.setPadding(new Insets(5.));	
		
		updatePercents();
	}

	private void updatePercents() {
		problems.clear();
		double sum = 0;
		for(TextField wField : weightFields) {
			try{sum += Double.parseDouble(wField.getText());}
			catch (Exception e) {}
		}
		for(int i = 0; i < weightFields.size(); i++) {
			TextField pField = percentFields.get(i);
			if(sum == 0) {
				pField.setText("Division by Zero");
				problems.add("Generator for " + att.getName() + ": Division by Zero");
			} else {
				TextField wField = weightFields.get(i);
				try{
					pField.setText(NumberFormat.getPercentInstance().format(Double.parseDouble(wField.getText())/sum));
				} catch (NumberFormatException nfe) {
					problems.add("Generator for " + att.getName() + ", item " + items.get(i) + ": " + nfe.getMessage());
					pField.setText(nfe.getMessage());
				}
			}
		}	
	}
	
	@Override
	public Node getEditorPane() {		
		return pane;
	}
	
	@Override
	public Vector<String> getProblems() {
		return problems;
	}

	@Override
	public String generate() {
		double sum = 0;
		for(TextField wField : weightFields) {
			try{sum += Double.parseDouble(wField.getText());}
			catch (Exception e) {}
		}
		if(sum == 0) { 
			return "null"; 
		} else {
			double result = sum * Math.random();
			for(int i = 0; i < weightFields.size(); i++) {		
				TextField wField = weightFields.get(i);
				try{
					Double weight = Double.parseDouble(wField.getText());
					if(result < weight) {
						return att.getTypeShort() + "::" + items.get(i);
					} else {
						result -= weight;
					}
				} catch (NumberFormatException nfe) {
					// ignore
				}
			}	
		}
		return "null"; 
	}

}
 