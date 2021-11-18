package tool.clients.fmmlxdiagrams.dialogs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer.PathNotFoundException;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;

import java.util.ArrayList;
import java.util.Vector;

public class ChangeParentDialog extends CustomDialog<ChangeParentDialog.Result> {

	private final AbstractPackageViewer diagram;
	private FmmlxObject object;

	private ObservableList<FmmlxObject> currentParentList;
	private ObservableList<FmmlxObject> possibleParents;

	private Label selectedObjectLabel;
	private Label currentParentsLabel;
	private Label newParentLabel;

	private TextField selectedObjectTextField;
	private ListView<FmmlxObject> currentParentsListView;
	private ListView<FmmlxObject> newParentListView;


	public ChangeParentDialog(AbstractPackageViewer diagram, FmmlxObject object) {
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
				return new Result(object, 
						new Vector<>(newParentListView.getSelectionModel().getSelectedItems()), 
						new Vector<>(currentParentList));
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
		possibleParents.remove(object);
		newParentListView.setItems(possibleParents);
		newParentListView.setDisable(false);
		if (possibleParents.size() == 0) {
			newParentListView.setDisable(true);
		}

		for (FmmlxObject fmmlxObject : currentParentList) {
			newParentListView.getSelectionModel().select(fmmlxObject);
		}
		
		grid.add(selectedObjectLabel, 0, 0);
		grid.add(selectedObjectTextField, 1, 0);
		grid.add(currentParentsLabel, 0, 1);
		grid.add(currentParentsListView, 1, 1);
		grid.add(newParentLabel, 0, 2);
		grid.add(newParentListView, 1, 2);
	}

	private ObservableList<FmmlxObject> getCurrentParents() {
		ArrayList<FmmlxObject> resultList = new ArrayList<>();
		Vector<String> parentNames = object.getParentsPaths();

		for (String name : parentNames) {
			try{
				FmmlxObject o = diagram.getObjectByPath(name);
				if(o != null) resultList.add(o);
			} catch (PathNotFoundException pnfe) {}
		}

		return FXCollections.observableArrayList(resultList);
	}
	

	public class Result {
		
		public final FmmlxObject object;
		public final Vector<FmmlxObject> oldParents;
		public final Vector<FmmlxObject> newParents;
		
		public Result(FmmlxObject object, Vector<FmmlxObject> newList, Vector<FmmlxObject> oldList) {
			this.object=object;
			this.oldParents=oldList;
			this.newParents=newList;
		}
	
		public Vector<String> getCurrentParentNames() {
			Vector<String> parentNames = new Vector<>();
			for (FmmlxObject p : oldParents) {
				parentNames.add(p.getName());
			}
			return parentNames;
		}
		
		public Vector<String> getNewParentNames() {
			Vector<String> parentNames = new Vector<>();
			for (FmmlxObject p : newParents) {
				parentNames.add(p.getName());
			}
			return parentNames;
		}
	}
}
