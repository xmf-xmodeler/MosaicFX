package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeParentDialogResult;

import java.util.ArrayList;
import java.util.Vector;

public class ChangeParentDialog extends CustomDialog<ChangeParentDialogResult> {

	private final FmmlxDiagram diagram;
	private FmmlxObject object;

	private ObservableList<FmmlxObject> currentParentList;
	private ObservableList<FmmlxObject> newParentList;

	private Label selectedObjectLabel;
	private Label currentParentsLabel;
	private Label newParentLabel;

	private TextField selectedObjectTextField;
	private ListView<FmmlxObject> currentParentsListView;
	private ListView<FmmlxObject> newParentListView;


	public ChangeParentDialog(FmmlxDiagram diagram, FmmlxObject object) {
		// TODO Auto-generated constructor stub
		super();

		this.diagram = diagram;
		this.object = object;

		DialogPane dialogPane = getDialogPane();
		dialogPane.setHeaderText("Change Parent");
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
				//TODO
			}
			return null;
		});

	}


	private boolean validateUserInput() {
		// TODO Auto-generated method stub
		return false;
	}


	private void setLayoutContent() {

		selectedObjectLabel = new Label("Selected Object");
		currentParentsLabel = new Label("Current Parent");

		selectedObjectTextField = new TextField();
		selectedObjectTextField.setText(object.getName());
		selectedObjectTextField.setDisable(true);

		newParentLabel = new Label("Select New Parent");

		currentParentsListView = initializeListView(currentParentList, SelectionMode.MULTIPLE);
		currentParentsListView.setDisable(true);
		newParentListView = initializeListView(newParentList, SelectionMode.MULTIPLE);
		//initializeListView();

		grid.add(selectedObjectLabel, 0, 0);
		grid.add(selectedObjectTextField, 1, 0);
		grid.add(currentParentsLabel, 0, 1);
		grid.add(currentParentsListView, 1, 1);
		grid.add(newParentLabel, 0, 2);
		grid.add(newParentListView, 1, 2);


		// TODO Auto-generated method stub

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
