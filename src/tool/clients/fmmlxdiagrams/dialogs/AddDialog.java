package tool.clients.fmmlxdiagrams.dialogs;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AddAttributeDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.AddDialogResult;

public class AddDialog extends CustomDialog<AddDialogResult>{
	
	private final String type;
	private final FmmlxDiagram diagram;
	private FmmlxObject object;

	public AddDialog(FmmlxDiagram diagram, FmmlxObject object, String type) {
		super();
		this.diagram = diagram;
		this.object = object;
		this.type = type;
		
		DialogPane dialogPane = getDialogPane();
		
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
				//TODO switch case with type
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
	}

	private void layoutContent() {
		switch (type) {
			case "class":
				addMetaClass();
				break;
			case "attribute":
				addAttribute();
				break;
			case "operation":
				addOperation();
				break;
			case "association":
				addAssociation();
				break;
			default:
				System.err.println("AddDialog: No matching content type!");	
		}

	}

	private void addAssociation() {
		// TODO Auto-generated method stub
		
	}

	private void addOperation() {
		// TODO Auto-generated method stub
		
	}

	private void addAttribute() {
		// TODO Auto-generated method stub
		
	}

	private void addMetaClass() {
		// TODO Auto-generated method stub
		
	}
}