package tool.clients.fmmlxdiagrams.dialogs.enumeration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Vector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import tool.clients.fmmlxdiagrams.dialogs.CustomDialog;
import tool.clients.fmmlxdiagrams.dialogs.InputChecker;
import tool.clients.fmmlxdiagrams.dialogs.results.AddEnumElementDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeEnumItemNameDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.ChangeEnumNameDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.results.EditEnumerationDialogResult;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxEnum;
import tool.clients.fmmlxdiagrams.TimeOutException;

public class EditEnumerationDialog extends CustomDialog<EditEnumerationDialogResult>{
	
	private Label chooseEnumLabel;
	private Label inputElementLabel;

	private ComboBox<FmmlxEnum> chooseEnumComboBox;
	private ListView<String> inputElementListview;

	private Button addItemButton;
	private Button removeItemButton;
	private Button changeNameButton;
	private Button changeItemNameButton;
	
	private AbstractPackageViewer diagram;
	
	public EditEnumerationDialog(AbstractPackageViewer diagram) {
		super();
		this.diagram=diagram;
		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		dialogPane.setHeaderText("Edit Enumeration");

		layout();

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
	
				/*
				 * for (String tmp : inputElementListview.getItems()) { enumElement.add(tmp); }
				 * if(chooseEnumComboBox.getSelectionModel().getSelectedItem()!=null) { return
				 * new EditEnumerationDialogResult(chooseEnumComboBox.getSelectionModel().
				 * getSelectedItem().getName(), new
				 * FmmlxEnum(chooseEnumComboBox.getSelectionModel().getSelectedItem().getName(),
				 * new Vector<>())); } else { return new
				 * EditEnumerationDialogResult(chooseEnumComboBox.getSelectionModel().
				 * getSelectedItem().getName(), new
				 * FmmlxEnum(chooseEnumComboBox.getSelectionModel().getSelectedItem().getName(),
				 * enumElement)); }
				 */
			}
			return null;
		});
	}

	private boolean validateUserInput() {
		if(chooseEnumComboBox.getSelectionModel().getSelectedItem()==null) {
			errorLabel.setText(StringValue.ErrorMessage.selectEnumeration);
			return false;
		}
		
		Set<String> set = new HashSet<String>(inputElementListview.getItems());
		if(set.size() < inputElementListview.getItems().size()){
			errorLabel.setText(StringValue.ErrorMessage.thereAreDuplicates);
			return false;
		}
		
		for (String tmp : inputElementListview.getItems()) {
			if (!InputChecker.validateName(tmp)) {
				errorLabel.setText("\""+tmp+"\""+ " is not valid name for enumeration's element");
				return false;
			} 
		}
		errorLabel.setText("");
		return true;
	}

	private void layout() {
		chooseEnumLabel = new Label("Choose Enumeration");
		inputElementLabel = new Label("Items");
//		enumItems = new Vector<String>();
		
		inputElementListview = initializeListView(0);
		inputElementListview.setEditable(true);
		inputElementListview.setCellFactory(TextFieldListCell.forListView());
		chooseEnumComboBox = (ComboBox<FmmlxEnum>) initializeComboBoxEnum(getEnumList());
		chooseEnumComboBox.valueProperty().addListener((observable, oldValue, newValue1) -> {
			if (newValue1 != null) {
				inputElementListview.getItems().clear();
				for(String tmp: chooseEnumComboBox.getSelectionModel().getSelectedItem().getItems()) {
					inputElementListview.getItems().add(tmp);
				}
			}
		});
		
		addItemButton = new Button("Add Element");
		removeItemButton = new Button("Remove Element");
		changeNameButton = new Button("Change Name");
		changeItemNameButton = new Button ("Change ItemName");
		
		List<Node> labelNodes = new ArrayList<Node>(); 
		List<Node> editorNodes = new ArrayList<Node>();
		

		labelNodes.add(chooseEnumLabel);
		labelNodes.add(new Label(" "));
		labelNodes.add(new Label(" "));
		labelNodes.add(inputElementLabel);
			
		editorNodes.add(joinNodeElementInHBox(chooseEnumComboBox, changeNameButton));
		editorNodes.add(new Label(" "));
		editorNodes.add(new Label(" "));
		editorNodes.add(inputElementListview);
		editorNodes.add(joinNodeElementInHBox(addItemButton, removeItemButton));
		editorNodes.add(changeItemNameButton);
		
		addItemButton.setOnAction(e -> addElement(chooseEnumComboBox.getSelectionModel().getSelectedItem(),inputElementListview));
		removeItemButton.setOnAction(e ->removeElement(inputElementListview.getSelectionModel().getSelectedItem(),inputElementListview));
		changeNameButton.setOnAction(e -> changeEnumNameDialog(chooseEnumComboBox.getSelectionModel().getSelectedItem()));  
		changeItemNameButton.setOnAction(e-> {
			try {
				changeItemNameDialog(chooseEnumComboBox.getSelectionModel().getSelectedItem(), inputElementListview.getSelectionModel().getSelectedItem());
			} catch (TimeOutException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		addNodesToGrid(labelNodes,0); 
		addNodesToGrid(editorNodes, 1);
		 
		
	} 

	private void changeEnumNameDialog(FmmlxEnum selectedItem) {
		ChangeEnumName dlg = new ChangeEnumName(diagram, selectedItem);
		Optional<ChangeEnumNameDialogResult> opt = dlg.showAndWait();

		if (opt.isPresent()) {
			ChangeEnumNameDialogResult result = opt.get();
			diagram.getComm().changeEnumerationName(diagram.getID(), result.getOldName(), result.getNewName());
		}
		
	}
	
	private void changeItemNameDialog(FmmlxEnum selectedEnum, String selectedItem) throws TimeOutException {
		ChangeEnumItemName dlg = new ChangeEnumItemName(diagram, selectedEnum, selectedItem);
		Optional<ChangeEnumItemNameDialogResult> opt = dlg.showAndWait();
		
		if(opt.isPresent()) {
			ChangeEnumItemNameDialogResult result = opt.get();
				diagram.getComm().changeEnumerationItemName(diagram.getID(), result.getEnumName(), result.getOldName(), result.getNewName());
		}
	}

	private ObservableList<FmmlxEnum> getEnumList() {	
		Vector<FmmlxEnum> enums = diagram.getEnums();		
		return FXCollections.observableArrayList(enums);
	}
	
	private void removeElement(String string, ListView<String> list) {
		if(chooseEnumComboBox.getSelectionModel().getSelectedItem()!=null) {
			
			list.getItems().remove(string);
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					try {				
						diagram.getComm().removeEnumerationItem(diagram.getID(), chooseEnumComboBox.getSelectionModel().getSelectedItem().getName(), string);
						diagram.updateEnums();
					} catch (TimeOutException e) {
						e.printStackTrace();
					}
					return null;
				}

			};
			
			new Thread(task).start();
			
//			for(String itemToBeRemoved : observableList) {
//				diagram.getComm().removeEnumerationItem(
//						diagram, 
//						chooseEnumComboBox.getSelectionModel().getSelectedItem().getName(), 
//						itemToBeRemoved);
//			}
//			inputElementListview.getItems().removeAll(observableList);
		} else {
			errorLabel.setText(StringValue.ErrorMessage.selectEnumeration);
		}
	}

	private void addElement(FmmlxEnum selectedEnum, ListView<String> list) {
		if(chooseEnumComboBox.getSelectionModel().getSelectedItem()!=null) {
			AddEnumElement dlg = new AddEnumElement(selectedEnum, list);
			Optional<AddEnumElementDialogResult> opt = dlg.showAndWait();

			if (opt.isPresent()) {
				AddEnumElementDialogResult result = opt.get();

				list.getItems().add(result.getName());
				dlg.close();
				
				Task<Void> task = new Task<Void>() {

					@Override
					protected Void call() throws Exception {
						try { 
					        diagram.getComm().addEnumerationItem(
							diagram.getID(), 
							chooseEnumComboBox.getSelectionModel().getSelectedItem().getName(), 
							result.getName());
					        diagram.updateEnums();
					} catch (TimeOutException e) {
						e.printStackTrace();
					}
						return null;
					}
				};
				
				new Thread(task).start();		
				
//				
//				inputElementListview.getItems().addAll(diagram.getEnum(selectedEnum.getName()).getItems());
			}
		} else {
			errorLabel.setText(StringValue.ErrorMessage.selectEnumeration);
		}
	}
}
