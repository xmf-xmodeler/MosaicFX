package tool.clients.fmmlxdiagrams.dialogs.shared;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeParentDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

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
		dialogPane.setHeaderText(StringValue.LabelAndHeaderTitle.changeParent);
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
		return true;
	}

	private void setLayoutContent() {

		selectedObjectLabel = new Label(StringValue.LabelAndHeaderTitle.selectedObject);
		currentParentsLabel = new Label(StringValue.LabelAndHeaderTitle.currentParent);
	
		selectedObjectTextField = new TextField();
		selectedObjectTextField.setText(object.getName());
		selectedObjectTextField.setDisable(true);

		newParentLabel = new Label(StringValue.LabelAndHeaderTitle.selectNewParent);

		currentParentList = getCurrentParents();
		currentParentsListView = initializeListView(currentParentList, SelectionMode.MULTIPLE);
		currentParentsListView.setDisable(true);
		newParentListView = initializeListView(possibleParents, SelectionMode.MULTIPLE);
		

		
		
		possibleParents = diagram.getAllPossibleParents(object.getLevel());
		if(possibleParents.contains(object)) {
			possibleParents.remove(object);
		}
		newParentListView.setItems(possibleParents);
		newParentListView.setDisable(false);
		if (possibleParents.size() == 0) {
			newParentListView.setDisable(true);
		}
		
//		int[] parentIndices = new int[currentParentList.size()]; 
		for(int i = 0; i < currentParentList.size(); i++) {
			System.err.println("selecting parent " + currentParentList.get(i).getName());
			newParentListView.getSelectionModel().select(currentParentList.get(i));
			System.err.println("selected " + newParentListView.getSelectionModel().getSelectedIndices());
//			parentIndices[i] = newParentListView.getItems().indexOf(currentParentList.get(i));
		}
//		newParentListView.getSelectionModel().selectIndices(parentIndices.length, parentIndices);
		
		grid.add(selectedObjectLabel, 0, 0);
		grid.add(selectedObjectTextField, 1, 0);
		grid.add(currentParentsLabel, 0, 1);
		grid.add(currentParentsListView, 1, 1);
		grid.add(newParentLabel, 0, 2);
		grid.add(newParentListView, 1, 2);
	}

	private ObservableList<FmmlxObject> getCurrentParents() {
		ArrayList<FmmlxObject> resultList = new ArrayList<>();
		Vector<Integer> parentIds = object.getParents();

		for (Integer id : parentIds) {
			FmmlxObject o = diagram.getObjectById(id);
			resultList.add(o);
		}
		
		ObservableList<FmmlxObject> result = FXCollections.observableArrayList(resultList);
		return result;
	}
}
