package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.Collection;
import java.util.Vector;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
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
		vBox.setSpacing(5.);
		vBox.setPadding(new Insets(5.));	
		setContent(vBox);		
		
		if("Integer".equals(attribute.getTypeShort())) {
			generatorChooser.getItems().add(IntegerEqualGenerator.name);
		} 
		
		if("Float".equals(attribute.getTypeShort())) {
			generatorChooser.getItems().add(FloatGaussianGenerator.name);
			generatorChooser.getItems().add(FloatEqualGenerator.name);
		} 
		
//		if("Integer".equals(attribute.getTypeShort()) || "Float".equals(attribute.getTypeShort())) {
//			// none yet
//		} 

		if("Boolean".equals(attribute.getTypeShort())) {
			generatorChooser.getItems().add(BooleanGenerator.name);
		}
		
		if(diagram.isEnum(attribute.getType())) {
			generatorChooser.getItems().add(EnumWeighedGenerator.name);
		}
		
//		if("String".equals(attribute.getTypeShort())) {
//			// none yet
//		}

		generatorChooser.getItems().add(ListGenerator.name);
		generatorChooser.getItems().add(ExpressionGenerator.name);
		
		generatorChooser.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			// clear 
			if(vBox.getChildren().retainAll(generatorChooser));
			generator = null;
			
			if(newVal != null) {
				if(BooleanGenerator.name.equals(newVal)) {
					generator = new BooleanGenerator(attribute);
				} else if(FloatGaussianGenerator.name.equals(newVal)) {
					generator = new FloatGaussianGenerator(attribute);
				} else if(ListGenerator.name.equals(newVal)) {
					generator = new ListGenerator(attribute, diagram);
				} else if(ExpressionGenerator.name.equals(newVal)) {
					generator = new ExpressionGenerator(attribute, diagram);
				} else if(IntegerEqualGenerator.name.equals(newVal)) {
					generator = new IntegerEqualGenerator(attribute);
				} else if(FloatEqualGenerator.name.equals(newVal)) {
					generator = new FloatEqualGenerator(attribute);
				} else if(EnumWeighedGenerator.name.equals(newVal)) {
					generator = new EnumWeighedGenerator(attribute, diagram);
				}
			}
			
			if(generator != null) {
				Node n = generator.getEditorPane();
				vBox.getChildren().add(n);
				VBox.setVgrow(n, Priority.ALWAYS);
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