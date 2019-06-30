package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Vector;

import org.apache.poi.xslf.model.geom.CurveToCommand;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

import tool.clients.fmmlxdiagrams.dialogs.results.ChangeTargetDialogResult;

public class ChangeTargetDialog extends CustomDialog<ChangeTargetDialogResult>{
	private DialogPane dialogPane;
	private final PropertyType type;
	private FmmlxObject object;
	private Vector<FmmlxObject> objects;
	
	private Label classLabel;
	private Label currentTarget;
	private Label newTarget;
	
	private TextField classTextField;
	private TextField currentTargetTextField;
	private ComboBox<String> newTargetComboBox;

	public ChangeTargetDialog(FmmlxDiagram diagram, FmmlxObject object, PropertyType type) {
		super();
		this.type=type;
		this.object=object;
		this.objects=diagram.getObjects();
		
		dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		layoutContent();
		dialogPane.setContent(flow);
		
		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});
		
		setResult();
	
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				//TODO
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
	}

	private void layoutContent() {
		dialogPane.setHeaderText("Change Association Target");
		classLabel = new Label("Class");
		currentTarget = new Label("Current Target");
		newTarget = new Label("New Target");
		
		classTextField = new TextField();
		classTextField.setText(object.getName());
		classTextField.setDisable(true);
		currentTargetTextField = new TextField();
		currentTargetTextField.setDisable(true);
		newTargetComboBox = new ComboBox<String>();
		
		newTargetComboBox.setPrefWidth(COLUMN_WIDTH);
		
		grid.add(classLabel, 0, 0);
		grid.add(classTextField, 1, 0);
		grid.add(currentTarget, 0, 1);
		grid.add(currentTargetTextField, 1, 1);
		grid.add(newTarget, 0, 2);
		grid.add(newTargetComboBox, 1, 2);
	}

}
