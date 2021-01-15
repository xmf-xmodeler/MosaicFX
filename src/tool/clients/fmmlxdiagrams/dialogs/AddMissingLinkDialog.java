package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.FmmlxAssociation;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.dialogs.results.AddMissingLinkDialogResult;

public class AddMissingLinkDialog extends CustomDialog<AddMissingLinkDialogResult> {
	
	private final boolean objIsSource;
	private final FmmlxObject classAtOtherEnd;
	private final Integer levelAtOtherEnd;

	private Vector<FmmlxObject> listOfPossibleLinkEnds;
	private Vector<FmmlxObject> listOfPossibleClassesForCreateAndLink;

	private final Label instructionLabel;
	private final RadioButton createLabel = new RadioButton("Create a new instance of");
	private final RadioButton pickLabel = new RadioButton("Pick an existing instance");
	private final RadioButton ignoreLabel = new RadioButton("Ignore the issue for a while");

//	private final RadioButton createButton = new RadioButton("Create and Link");
//	private final RadioButton pickButton = new RadioButton("Pick and Link");
//	private final RadioButton ignoreButton = new RadioButton("Do Nothing");

	private final ComboBox<FmmlxObject> createComboBox;
	private final ComboBox<FmmlxObject> pickComboBox;
	
	public AddMissingLinkDialog(final FmmlxObject obj, final FmmlxAssociation assoc) {
		
		objIsSource = obj.isInstanceOf(assoc.getSourceNode(), assoc.getLevelSource());
		classAtOtherEnd = objIsSource?assoc.getTargetNode():assoc.getSourceNode();
		levelAtOtherEnd = objIsSource?assoc.getLevelTarget():assoc.getLevelSource();
		
		listOfPossibleLinkEnds = classAtOtherEnd.getInstancesByLevel(levelAtOtherEnd);
		listOfPossibleClassesForCreateAndLink = classAtOtherEnd.getInstancesByLevel(levelAtOtherEnd+1);
		
		if(classAtOtherEnd.getLevel() == levelAtOtherEnd+1) {
			listOfPossibleClassesForCreateAndLink.add(classAtOtherEnd);
			listOfPossibleClassesForCreateAndLink.addAll(classAtOtherEnd.getAllChildren());
		}
		
		instructionLabel = new Label("The object " + obj.getName()
				+" requires a link to an instance of " + classAtOtherEnd.getName() 
				+ " at level " + levelAtOtherEnd + ".");
		
		final ToggleGroup group = new ToggleGroup();
		createLabel.setToggleGroup(group);
		pickLabel.setToggleGroup(group);
		ignoreLabel.setToggleGroup(group);
		ignoreLabel.setSelected(true);

		createComboBox = new ComboBox<>(FXCollections.observableArrayList(listOfPossibleClassesForCreateAndLink));
		pickComboBox   = new ComboBox<>(FXCollections.observableArrayList(listOfPossibleLinkEnds));
		
		createComboBox.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
			createLabel.setSelected(true);});
		pickComboBox.getSelectionModel().selectedItemProperty().addListener((a, b, c) -> {
			pickLabel.setSelected(true);});
		
		grid.add(instructionLabel, 0, 0, 3, 1);
		grid.add(createLabel, 0, 1);
		grid.add(pickLabel, 0, 2);
		grid.add(ignoreLabel, 0, 3, 2, 1);
		grid.add(createComboBox, 1, 1);
		grid.add(pickComboBox, 1, 2);
		
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
                if(createLabel.isSelected() && createComboBox.getSelectionModel().getSelectedIndex() >= 0) {
                	return new AddMissingLinkDialogResult(true, createComboBox.getSelectionModel().getSelectedItem());
                } else if (pickLabel.isSelected() && pickComboBox.getSelectionModel().getSelectedIndex() >= 0) {
                    return new AddMissingLinkDialogResult(false, pickComboBox.getSelectionModel().getSelectedItem());
                }
			}
			return null;
		});
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialogPane.setContent(flow);
	}

}
