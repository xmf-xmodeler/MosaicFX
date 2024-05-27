package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Vector;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class DomainspecificDatatypesDialog extends CustomDialog<String> {

	private AbstractPackageViewer diagram;
	private GridPane grid = new GridPane();

	private Label lblSelectType;
	private ListView<String> lvSelectedClass;
	private Vector<String> classTypes;

	public DomainspecificDatatypesDialog(AbstractPackageViewer diagram) {

		super();
		ButtonType ok = ButtonType.OK;

		getDialogPane().getButtonTypes().add(ok);

		this.diagram = diagram;

		grid = layout();

		getDialogPane().setContent(grid);

		setResult();
	}

	private void setResult() {
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return lvSelectedClass.getSelectionModel().getSelectedItem();
			}
			return null;
		});
	}

	private GridPane layout() {
		lblSelectType = new Label("Select a domainspecific datatype!");
		lvSelectedClass = new ListView<String>();

		classTypes = getClassTypes();
		lvSelectedClass.getItems().setAll(classTypes);

		grid.add(lblSelectType, 1, 0);
		grid.add(lvSelectedClass, 1, 1);

		return grid;
	}

	private Vector<String> getClassTypes() {

		Vector<String> classNames = new Vector<String>();
		Vector<FmmlxObject> objects = diagram.getObjectsReadOnly();

		for (FmmlxObject o : objects) {

			if (o.getLevel().getMinLevel() > 0) {
				classNames.add(o.getName());
			}

		}
		return classNames;
	}
}
