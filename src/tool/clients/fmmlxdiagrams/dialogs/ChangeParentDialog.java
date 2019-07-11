package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeParentDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

import java.util.ArrayList;
import java.util.Vector;

public class ChangeParentDialog extends CustomDialog<ChangeParentDialogResult> {

	private final FmmlxDiagram diagram;
	private FmmlxObject object;

	private ObservableList<FmmlxObject> currentParentList;
	private ObservableList<FmmlxObject> possibleParents;

	private Label selectedObjectLabel;
	private Label currentParentsLabel;
	private Label newParentLabel;

	private TextField selectedObjectTextField;
	private ListView<FmmlxObject> currentParentsListView;
	private ListView<FmmlxObject> newParentListView;


	public ChangeParentDialog(FmmlxDiagram diagram, FmmlxObject object) {
		super();

		this.diagram = diagram;
		this.object = object;

		DialogPane dialogPane = getDialogPane();
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.changeParent);
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		setLayoutContent();

		dialogPane.setContent(flow);

		final Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, e -> {
			if (!validateUserInput()) {
				e.consume();
			}
		});

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new ChangeParentDialogResult(object, newParentListView.getSelectionModel().getSelectedItems());
			}
			return null;
		});
	}


	private boolean validateUserInput() {
		if (possibleParents.size()>=1) {
			if(newParentListView.getSelectionModel().getSelectedItems().size()==0) {
				errorLabel.setText(StringValueDialog.ErrorMessage.selectNewParent);
				return false;
			}
		}
		return true;
	}

	private void setLayoutContent() {

		selectedObjectLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectedObject);
		currentParentsLabel = new Label(StringValueDialog.LabelAndHeaderTitle.currentParent);
	

		selectedObjectTextField = new TextField();
		selectedObjectTextField.setText(object.getName());
		selectedObjectTextField.setDisable(true);

		newParentLabel = new Label(StringValueDialog.LabelAndHeaderTitle.selectNewParent);

		currentParentList = getCurrentParent();
		currentParentsListView = initializeListView(currentParentList, SelectionMode.MULTIPLE);
		currentParentsListView.setDisable(true);
		newParentListView = initializeListView(possibleParents, SelectionMode.MULTIPLE);
		possibleParents = diagram.getAllPossibleParents(object.getLevel());
		newParentListView.setItems(possibleParents);
		newParentListView.setDisable(false);
		if (possibleParents.size() == 0) {
			newParentListView.setDisable(true);
		}
		

		grid.add(selectedObjectLabel, 0, 0);
		grid.add(selectedObjectTextField, 1, 0);
		grid.add(currentParentsLabel, 0, 1);
		grid.add(currentParentsListView, 1, 1);
		grid.add(newParentLabel, 0, 2);
		grid.add(newParentListView, 1, 2);
	}

	private ObservableList<FmmlxObject> getCurrentParent() {
		ArrayList<FmmlxObject> resultList = new ArrayList<>();
		Vector<Integer> parentIds = object.getParents();

		if (!parentIds.isEmpty()) {
			for (Integer id : parentIds) {
				FmmlxObject o = diagram.getObjectById(id);
				resultList.add(o);
			}
		}

		ObservableList<FmmlxObject> result = FXCollections.observableArrayList(resultList);
		return result;
	}
}
