package tool.clients.fmmlxdiagrams.instancewizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.Constraint;
import tool.clients.fmmlxdiagrams.FmmlxAttribute;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.helper.persistence.XMLInstanceStub;

public class InstanceWizard extends Dialog<Object> {
	private TabPane tabPane = new TabPane();
	private Spinner<Integer> instanceSpinner = new Spinner<>(1, Integer.MAX_VALUE, 10);
	private Spinner<Integer> timeoutSpinner = new Spinner<>(1, Integer.MAX_VALUE, 10);
	private final CheckBox createObjectsVisibileBox = new CheckBox();
	private boolean createObjectsVisible;
	private final ArrayList<String> visibleObjects = new ArrayList<>();
	private int instanceCreationCounter = 0;
	private int timeoutCounter = 0;
	private int maxTimeOutCounter;
	private AbstractPackageViewer diagram;
	private FmmlxObject theClass;
	private HashMap<Constraint, CheckBox> constraintBoxes = new HashMap<>();
	private final ProgressBar pb = new ProgressBar();
	private int instancesToCreate;
	private final Stage progressIndicatorStage = new Stage();
	
	public InstanceWizard(AbstractPackageViewer diagram, FmmlxObject theClass, int level) {
		this.diagram = diagram;
		this.theClass = theClass;		
		
		for(FmmlxAttribute att : theClass.getAllAttributes()) if(att.getLevel() == level) {
			AttributeTab t = new AttributeTab(att, diagram);
			tabPane.getTabs().add(t);
		}
		
		this.setResizable(true);
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
		spinnerPane.add(new Label("Create objects visible?"), 0, 2);
		spinnerPane.add(createObjectsVisibileBox, 1, 2);
		
		
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
				createObjectsVisible = createObjectsVisibileBox.isSelected();
		        if (!checkInputs()) {
		            event.consume();
		        }
		    }
		);
		
		setResultConverter(button -> {
			if (button == ButtonType.OK) {
				instancesToCreate = instanceSpinner.getValue();
				generateInstances(timeoutSpinner.getValue()); 
				return null; }
			else 
				return null; 
		});
	}

	private void generateInstances(int maxTimeOut) {
		showProgressIndicator();
		instanceCreationCounter = instancesToCreate;
		maxTimeOutCounter = maxTimeOut;
		generateInstance();
	}

	private void showProgressIndicator() {
		progressIndicatorStage.initModality(Modality.APPLICATION_MODAL);
		progressIndicatorStage.setAlwaysOnTop(true);
		progressIndicatorStage.setResizable(false);
		progressIndicatorStage.initStyle(StageStyle.UTILITY);
		//Prevents Window from being closed
		progressIndicatorStage.setOnCloseRequest(Event::consume);
		final HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setAlignment(Pos.CENTER);
		Scene scene = new Scene(hb, 400, 150);
		progressIndicatorStage.setScene(scene);
		progressIndicatorStage.setTitle("Wait for instances beeing created");
		pb.setProgress(0);
		hb.getChildren().add(pb);
		progressIndicatorStage.show();
	}
	
	private void notifyInstanceCreated(Vector response) {
		Boolean success = (Boolean) response.get(0);
		if(success) {
			instanceCreationCounter--;
			timeoutCounter = maxTimeOutCounter;
			Platform.runLater(()-> 
			{pb.setProgress(1-((double)instanceCreationCounter/ instancesToCreate));});
			
			
			String objectName = (String) response.get(1);
			String objectPath = diagram.getPackagePath() + "::" + objectName;
			rearangeInstance(objectPath);
			
			
		} else {
			timeoutCounter--;
		}
		if(instanceCreationCounter > 0 && timeoutCounter > 0) {
			generateInstance();
		} else {
			Platform.runLater(progressIndicatorStage::close);
			ReturnCall<Object> onDiagramUpdated = o -> setObjectsVisible();
			diagram.updateDiagram(onDiagramUpdated);
		}
	}
	
	//TODO while creating the gui should be blocked... see logic in the update diagramFunction

	private void setObjectsVisible() {
		if (createObjectsVisible) {
			Vector<FmmlxObject> objectsVector = new Vector();
			for (String objectName : visibleObjects) {
				String objectPath = diagram.getPackagePath() + "::" + objectName;
				FmmlxObject object = diagram.getObjectByPath(objectPath);
				objectsVector.add(object);
			}
			//do note use the function of DiagramActions because in this function an DiagramUpdate is called which would lead to an clearing of the objectList and an exception
			diagram.getComm().hideElements(diagram.getID(), objectsVector, false);
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
				this::notifyInstanceCreated);
	}

	private void rearangeInstance(String objectPath) {
		 int x = calculateNewPosition("x");
		 int y = calculateNewPosition("y"); 
         String ref = objectPath;
         XMLInstanceStub stub = new XMLInstanceStub(ref, !createObjectsVisible, x, y);
         diagram.getComm().sendObjectInformation(diagram.getID(), stub);
	}

	private int calculateNewPosition(String string) {
		//the value of the maxObjPerRow is arbitrarily chosen. Future-extensions would be to let the user choose this values
		final int maxObjPerRow = 10; 
		int instancesCreated = instancesToCreate - instanceCreationCounter;
		
		int oldPosition = calculateOldPoition(string);
		int offset = calculateOffset(string);
		//the value of the gap is arbitrarily chosen. Future-extensions would be to differentiate between hgap and vgap or to let the user choose this values
		int gap = 10;
		
		int col = (int) Math.ceil((float)instancesCreated/ maxObjPerRow);
		int row = (instancesCreated - (col -1) * maxObjPerRow) - 1;
		
		int newPosition = 0;		
		switch (string) {
		case "x":
			newPosition = oldPosition + col *(offset + gap);
			break;
		case "y":
			//because the first element of a row is on position 0 it needs to be added a minimum offset so the instance does not overlap with theClass. Therefore 1 is added.
			newPosition = oldPosition + (1 + row) *(offset + gap);
			break;
		}
		return newPosition;
	}

	/**
	*Returns the needed offset. Depending if the input is x or y the width or height of theClass is returned
	*@param string defines if width or height is returned
	*/
	private int calculateOffset(String string) {
		int offset = 0;
		switch (string) {
		case "x":
			offset = (int) theClass.getWidth();
			break;
		case "y":
			offset = (int) theClass.getHeight();
			break;
		}
		return offset;
	}

	
	/**
	*Returns the position of theClass
	*@param string defines if y or x value is returned
	*/
	private int calculateOldPoition(String string) {
		int oldPosition = 0;
		switch (string) {
		case "x":
			oldPosition = (int) theClass.getX();
			break;
		case "y":
			oldPosition = (int) theClass.getY();
			break;
		}
		return oldPosition;
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
}