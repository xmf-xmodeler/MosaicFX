package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxEnum;

public class DeleteEnumerationDialog extends CustomDialog<Void>{
	private Label enumListLabel;	
	private ListView<FmmlxEnum> enumListview;
	private Button deleteButton;
	private AbstractPackageViewer diagram;
	
	public DeleteEnumerationDialog(AbstractPackageViewer diagram) {
		super();
		this.diagram=diagram;
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.CLOSE);
		dialogPane.setHeaderText("Delete Enumeration");

		addElementToGrid();

		dialogPane.setContent(flow);

		setResult();
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {	
				return null;
		});
	}

	private boolean validateUserInput() {
		if (enumListview.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText("Please select at least one enumeration!");
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	private void addElementToGrid() {
		enumListLabel = new Label("Enum List");
		enumListview=initializeEnumListView(getEnumList(), SelectionMode.MULTIPLE);
		
		deleteButton = new Button("Delete Enumeration");
		deleteButton.setOnAction(e->removeEnum());
		
		List<Node> labelNode = new ArrayList<Node>();
		List<Node> editorNode = new ArrayList<Node>();
		
		labelNode.add(enumListLabel);
		editorNode.add(enumListview);
		editorNode.add(deleteButton);
		
		addNodesToGrid(labelNode,0);
		addNodesToGrid(editorNode, 1);
	}

	private ObservableList<FmmlxEnum> getEnumList() {
		Vector<FmmlxEnum> enums = diagram.getEnums();
		
		return FXCollections.observableArrayList(enums);
	}


	private void removeEnum() {
		if(validateUserInput()) {
//			DeleteEnumerationDialogResult aed = new DeleteEnumerationDialogResult(enumListview.getSelectionModel().getSelectedItems());
			for(FmmlxEnum e : enumListview.getSelectionModel().getSelectedItems()) {
				diagram.getComm().removeEnumeration(this.diagram.getID(), e.getName());
			}
		}
	}


}
