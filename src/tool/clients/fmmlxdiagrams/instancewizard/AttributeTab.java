package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.Collection;
import java.util.Vector;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class AttributeTab extends Tab {
	private final VBox vBox;
	private ComboBox<String> generatorChooser;
	private Generator generator;
	private FmmlxAttribute attribute;
	
	public AttributeTab(FmmlxAttribute attribute, final AbstractPackageViewer diagram) {
		super(attribute.getName());
		this.attribute = attribute;
		generatorChooser = new ComboBox<String>();
		vBox = new VBox(generatorChooser);
		setContent(vBox);		
		
		if("Integer".equals(attribute.getTypeShort())) {
			generatorChooser.getItems().add("Integer 1");
			generatorChooser.getItems().add("Integer 2");
		} 
		
		if("Float".equals(attribute.getTypeShort())) {
			generatorChooser.getItems().add(GaussianGenerator.name);
//			generatorChooser.getItems().add("Float 2");
		} 
		
		if("Integer".equals(attribute.getTypeShort()) || "Float".equals(attribute.getTypeShort())) {
			generatorChooser.getItems().add("Integer/Float");
		} 

		if("Boolean".equals(attribute.getTypeShort())) {
			generatorChooser.getItems().add(BooleanGenerator.name);
//			generatorChooser.getItems().add("Boolean 2");
		}
		
		if("String".equals(attribute.getTypeShort())) {
			generatorChooser.getItems().add("String 1");
			generatorChooser.getItems().add("String 2");
		}
		
		generatorChooser.getItems().add(ListGenerator.name);
//		generatorChooser.getItems().add("Generic 2");
		
		generatorChooser.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			// clear 
			if(vBox.getChildren().retainAll(generatorChooser));
			generator = null;
			
			if(newVal != null) {
				if(BooleanGenerator.name.equals(newVal)) {
					generator = new BooleanGenerator(attribute);
				} else if(GaussianGenerator.name.equals(newVal)) {
					generator = new GaussianGenerator(attribute);
				}else if(ListGenerator.name.equals(newVal)) {
					generator = new ListGenerator(attribute, diagram);
				}
			}
			
			if(generator != null) {
				vBox.getChildren().add(generator.getEditorPane());
			}
		});		
		
		generatorChooser.setValue(null);
		
	}

	public String generate() {
		return generator.generate();
	}

	public FmmlxAttribute getAttribute() {
		return attribute;
	}

	public java.util.Vector<String> getProblems() {
		if(generator == null) {
			java.util.Vector<String> problems = new Vector<>();
			problems.add("No generator selected for " + attribute.getName());
			return problems;
		} else {
			return generator.getProblems();
		}
		
	}

}
