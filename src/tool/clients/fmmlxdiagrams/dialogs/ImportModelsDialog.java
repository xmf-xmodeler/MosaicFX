package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Vector;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;

public class ImportModelsDialog extends Dialog<ImportModelsDialog.Result> {

	private Button addButton = new Button("Add");
	private Button removeButton = new Button("Remove");
	private ComboBox<String> addPackageBox = new ComboBox<>();
	private ListView<String> addedPackagesBox = new ListView<>();

	public ImportModelsDialog(AbstractPackageViewer diagram) {
		addedPackagesBox.getItems().addAll(diagram.getImportedPackages());
		addPackageBox.getItems().add("loading...");
		addPackageBox.getSelectionModel().select(0);
		addPackageBox.setEditable(true);
		diagram.getComm().getAllPackages(diagram.getID(), true, packages -> {
			addPackageBox.getItems().clear();
			addPackageBox.getItems().addAll(packages);
		});

		DialogPane dialogPane = getDialogPane();
//		setHeaderText("New MetaClass");
		setTitle("Edit Imports");

		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		HBox hBox2a = new HBox(addPackageBox, addButton);
		VBox vbox1 = new VBox(
				new Label("Select or type package path to be added and press \"Add\""),
				hBox2a,
				new Label("Packages to be imported:"),
				addedPackagesBox,
				new Label("Select packages to be removed and press \"Remove\""),
				removeButton,
				new Label("Press \"OK\" to accept changes"));
		
		dialogPane.setContent(vbox1);

		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new Result(addedPackagesBox.getItems());
			}
			return null;
		});
		
		addButton.setOnAction(event -> {
			addedPackagesBox.getItems().add(addPackageBox.getValue());
		});
		
		removeButton.setOnAction(event -> {
			addedPackagesBox.getItems().removeAll(
					addedPackagesBox.getSelectionModel().getSelectedItems());
		});
		
	}

	public class Result {

		public Result(ObservableList<String> items) {
			this.imports = new Vector<>(items);
		}

		public Vector<String> imports;

	}
}
