package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.HashMap;
import java.util.Vector;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.Constraint;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class InstanceWizard extends Dialog<InstanceWizard.Result> {
	TabPane tabPane = new TabPane();
	Spinner<Integer> instanceSpinner = new Spinner<Integer>(1, Integer.MAX_VALUE, 10);
	Spinner<Integer> timeoutSpinner = new Spinner<Integer>(1, Integer.MAX_VALUE, 10);
	private int instanceCreationCounter = 0;
	private int timeoutCounter = 0;
	private int maxTimeOutCounter;
	AbstractPackageViewer diagram;
	FmmlxObject theClass;
	private HashMap<Constraint, CheckBox> constraintBoxes = new HashMap<>();
	
	public InstanceWizard(AbstractPackageViewer diagram, FmmlxObject theClass, int level) {
		this.diagram = diagram;
		this.theClass = theClass;
		
		this.setResizable(true);
		
		for(FmmlxAttribute att : theClass.getAllAttributes()) if(att.getLevel() == level) {
			AttributeTab t = new AttributeTab(att, diagram);
			tabPane.getTabs().add(t);
		}
		
		VBox constraintBox = new VBox();
		constraintBox.getChildren().add(new Separator());
		constraintBox.getChildren().add(new Label("Select constraints which must be met:"));
		constraintBox.getChildren().add(new Label("If failed, the instance is discarded and tried again."));
		for(Constraint c : theClass.getConstraints()) { // TODO: gather all constraints from meta-classes as well
			CheckBox checkBox = new CheckBox(c.getName());
			constraintBox.getChildren().add(checkBox);
			constraintBoxes.put(c, checkBox);
		}

		tabPane.setMinHeight(600);
		tabPane.setMinWidth(450);
		VBox.setVgrow(tabPane, Priority.ALWAYS);

		GridPane spinnerPane = new GridPane();
		spinnerPane.add(new Label("Number of Instances:"), 0, 0);
		spinnerPane.add(new Label("Max failed attempts:"), 0, 1);
		spinnerPane.add(instanceSpinner, 1, 0);
		spinnerPane.add(timeoutSpinner, 1, 1);
		instanceSpinner.setEditable(true);
		instanceSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) instanceSpinner.increment(0); }); // Javafx-Bug
		timeoutSpinner.setEditable(true);
		timeoutSpinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) timeoutSpinner.increment(0); }); // Javafx-Bug
		spinnerPane.setHgap(5.);
		spinnerPane.setVgap(5.);
		
		VBox vBox = new VBox(
				new Label("Instance Generator for " + theClass.getName()), 
				spinnerPane, 
				tabPane, 
				constraintBox);
		vBox.setSpacing(5.);
		vBox.setPadding(new Insets(5.));
		
		getDialogPane().setContent(new ScrollPane(vBox));
		
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(javafx.event.ActionEvent.ACTION, 
		    event -> {
		        if (!checkInputs()) {
		            event.consume();
		        }
		    }
		);
		
		setResultConverter(button -> {
			if (button == ButtonType.OK) {
				generateInstances(instanceSpinner.getValue(), timeoutSpinner.getValue()); 
				return null; }
			else 
				return null; 
		});
	}

	private void generateInstances(int numberOfInstances, int maxTimeOut) {
		instanceCreationCounter = numberOfInstances;
		maxTimeOutCounter = maxTimeOut;
		generateInstance();
	}
	
	public void notifyInstanceCreated(Boolean success) {
		if(success) {
			instanceCreationCounter--;
			timeoutCounter = maxTimeOutCounter;
		} else {
			timeoutCounter--;
		}
		if(instanceCreationCounter > 0 && timeoutCounter > 0) {
			generateInstance();
		} else {
			diagram.updateDiagram();
		}
	}
	
	private void generateInstance() {
		String namePrefix = theClass.getName();
		if(theClass.getLevel().getMinLevel() == 1) namePrefix = namePrefix.substring(0,1).toLowerCase() + namePrefix.substring(1);
		
		Vector<Vector<String>> slotValues = new Vector<>();
		for(Tab t : tabPane.getTabs()) {
			AttributeTab aTab = (AttributeTab) t;
//			System.err.println("\t" + aTab.getAttribute().getName() + ": " + aTab.generate());
			Vector<String> slotItem = new Vector<>();
			slotItem.add(aTab.getAttribute().getName());
			slotItem.add(aTab.generate());
			slotValues.add(slotItem);
		}
		
		Vector<String> mandatoryConstraints = new Vector<>();
		for(Constraint c : constraintBoxes.keySet()) {
			CheckBox box = constraintBoxes.get(c);
			if(box.isSelected()) {
				mandatoryConstraints.add(c.getName());
			}
		}
		
		diagram.getComm().addGeneratedInstance(
				diagram,
				theClass,
				theClass.getLevel().getMinLevel()-1,
				namePrefix,
				slotValues,
				mandatoryConstraints,
				success->notifyInstanceCreated(success));
	}

	private boolean checkInputs() {
		Vector<String> problems = new Vector<>();
		for(Tab t : tabPane.getTabs()) {
			AttributeTab aTab = (AttributeTab) t;
			problems.addAll(aTab.getProblems());
		}
		if(problems.isEmpty()) {
			return true;
		} else {
			String s = "";
			for(String p : problems) {
				s += p + "\n";
			}
			Alert alert = new Alert(AlertType.ERROR, s, ButtonType.CANCEL);
			alert.showAndWait();
			return false;
		}
	}

	public class Result {
		
	}
}
