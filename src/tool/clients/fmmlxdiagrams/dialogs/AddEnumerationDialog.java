package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.dialogs.results.AddEnumerationDialogResult;

public class AddEnumerationDialog extends CustomDialog<AddEnumerationDialogResult>{
	
	private FmmlxDiagram diagram;
	private Label nameLabel;
	private Label elementLabel;
	
	private TextField nameTextField;

	public AddEnumerationDialog(FmmlxDiagram diagram) {
		super();
		
		this.diagram=diagram;

		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		addElementToGrid();

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
		// TODO Auto-generated method stub
		
	}

	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
	}

	private void addElementToGrid() {
		// TODO Auto-generated method stub
		
	}

}
