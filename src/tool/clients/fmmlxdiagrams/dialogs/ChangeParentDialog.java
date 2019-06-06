package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeParentDialogResult;

public class ChangeParentDialog extends CustomDialog<ChangeParentDialogResult>{

	private final FmmlxDiagram diagram;
	private FmmlxObject object;
	
	private ObservableList<String> currentParentList;
	private ObservableList<String> newParentList;
	
	private Label selectedObjectLabel;
	private Label currentParentsLabel;
	private Label newParentLabel;
	
	private TextField selectedObjectTextField;
	private ListView<String> currentParentsListView;
	private ListView<String> newParentListView;
	

	public ChangeParentDialog(FmmlxDiagram diagram, FmmlxObject object) {
		// TODO Auto-generated constructor stub
		super();
		
		this.diagram=diagram;
		this.object=object;
		
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
		
		initializeListView();
		
		grid.add(selectedObjectLabel, 0, 0);
		grid.add(selectedObjectTextField, 1, 0);
		grid.add(currentParentsLabel, 0, 1);
		grid.add(currentParentsListView, 1, 1);
		grid.add(newParentLabel, 0, 2);
		grid.add(newParentListView, 1, 2);
		
		
		// TODO Auto-generated method stub
		
	}


	private void initializeListView() {
		// TODO Auto-generated method stub
		
		currentParentList=getCurrentParent(object.getParents());
		newParentList = diagram.getAllPossibleParentList();
		
		currentParentsListView = new ListView<String>(currentParentList);
		currentParentsListView.setPrefHeight(75);
		currentParentsListView.setPrefWidth(COLUMN_WIDTH);
		currentParentsListView.setDisable(true);
		
		newParentListView = new ListView<String>(newParentList);
		newParentListView.setPrefHeight(75);
		newParentListView.setPrefWidth(COLUMN_WIDTH);
		newParentListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
	}


	private ObservableList<String> getCurrentParent(Vector<Integer> parentsID) {
		ArrayList<String> resutlStrings= new ArrayList<String>();
		
		/* TODO
		 * if(parentsID.size()>0) { for(int i = 0; i < parentsID.size(); i++) { for
		 * (FmmlxObject object : diagram.getObjects()) {
		 * if(parentsID.get(i)==object.getId()) { resutlStrings.add(object.getName()); }
		 * } }
		 * 
		 * }
		 */
		
		ObservableList<String> result = FXCollections.observableArrayList(resutlStrings);
		return result;
	}
	
	

}
