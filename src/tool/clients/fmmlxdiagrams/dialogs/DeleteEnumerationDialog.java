package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.dialogs.results.DeleteEnumerationDialogResult;

public class DeleteEnumerationDialog extends CustomDialog<DeleteEnumerationDialogResult>{
	

	private Label enumListLabel;
	
	private ListView<FmmlxEnum> enumListview;
	
	private Button deleteButton;
	
	public DeleteEnumerationDialog() {
		super();
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setHeaderText("Delete Enumeration");

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
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
				return new DeleteEnumerationDialogResult(enumListview.getSelectionModel().getSelectedItems());
			}
			return null;
		});
		
	}

	private boolean validateUserInput() {
		if (enumListview.getSelectionModel().getSelectedItem()!=null) {
			return true;
		}
		return false;
	}

	private void addElementToGrid() {
		enumListLabel = new Label("Enum List");
		enumListview=initializeEnumListView(null, SelectionMode.MULTIPLE);
		
		deleteButton = new Button("Delete Enumeration");
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();
		
		labelNode.add(enumListLabel);
		editorNode.add(enumListview);
		editorNode.add(deleteButton);
		
		addNodesToGrid(labelNode,0);
		addNodesToGrid(editorNode, 1);
	}

}
