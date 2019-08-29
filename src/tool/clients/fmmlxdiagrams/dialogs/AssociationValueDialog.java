package tool.clients.fmmlxdiagrams.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AssociationValueDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.EditAssociationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringvalue.StringValueDialog;

public class AssociationValueDialog extends CustomDialog<AssociationValueDialogResult>{
	
	private FmmlxDiagram diagram;
	private FmmlxObject object;
	private DialogPane dialogPane;
	
	private Label classALabel;
	private Label associationLabel;
	private Label classBLabel;
	
	private ListView<FmmlxObject> classAListView;
	private ListView<FmmlxObject> associationListView;
	private ListView<FmmlxObject> classBListView;	
	
	private ArrayList<Node> classANodes;
	private List<Node> associationNodes;
	private List<Node> classBNodes;

	private ButtonType plusButtonType;
	private ButtonType minusButtonType;
	private ButtonType midlleButtonType;
	
	public AssociationValueDialog(FmmlxDiagram diagram, FmmlxObject object) {
		this.diagram=diagram;
		this.object=object;
		
		dialogPane = getDialogPane();
		plusButtonType = new ButtonType("+");
		minusButtonType = new ButtonType("-");
		midlleButtonType = new ButtonType("-> ->");
		dialogPane.getButtonTypes().addAll(plusButtonType, minusButtonType, midlleButtonType, ButtonType.OK, ButtonType.CANCEL);
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
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
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
		dialogPane.setHeaderText(StringValueDialog.LabelAndHeaderTitle.associationValue);
		classALabel = new Label(StringValueDialog.LabelAndHeaderTitle.classALabel);
		associationLabel = new Label(StringValueDialog.LabelAndHeaderTitle.association);
		classBLabel = new Label(StringValueDialog.LabelAndHeaderTitle.classBLabel);
		
		classAListView = initializeListView(null, SelectionMode.MULTIPLE);
		associationListView = initializeListView(null, SelectionMode.MULTIPLE);
		classBListView= initializeListView(null, SelectionMode.MULTIPLE);
		
		classANodes = new ArrayList<>();
		associationNodes = new ArrayList<>();
		classBNodes = new ArrayList<>();
		
		classANodes.add(classALabel);
		classANodes.add(classAListView);
		
		associationNodes.add(associationLabel);
		associationNodes.add(associationListView);
		
		classBNodes.add(classBLabel);
		classBNodes.add(classBListView);
		
		
		addNodesToGrid(classANodes, 0);
		addNodesToGrid(associationNodes, 1);
		addNodesToGrid(classBNodes, 2);
		
	}

}
