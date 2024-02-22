package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Vector;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class DiagramStatisticsDialog extends Dialog {
	private AbstractPackageViewer diagram;

	
	public DiagramStatisticsDialog(AbstractPackageViewer diagram) {
		this.diagram = diagram;
		setTitle("Diagram Statistics");
		ButtonType okButtonType = ButtonType.OK;
		getDialogPane().getButtonTypes().add(okButtonType);	//not sure what this does

		GridPane statistics = buildGridPane();
		getDialogPane().setContent(statistics);
	}
	
	private GridPane buildGridPane() {
		GridPane gridPane = new GridPane();
		gridPane.setHgap(7);
		
		gridPane.add(new Label("Number of Objects: "), 0, 0, 1, 1);
		gridPane.add(new Label(""+diagram.getObjectsReadOnly().size()), 2, 0, 1, 1);

		gridPane.add(new Label("Number of Associations: "), 0, 2, 1, 1);
		gridPane.add(new Label(""+diagram.getAssociations().size()), 2, 2, 1, 1);

		gridPane.add(new Label("Number of Delegations: "), 0, 4, 1, 1);
		gridPane.add(new Label(""+ diagram.getDelegations().size()), 2, 4, 1, 1);

		gridPane.add(new Label("Number of Classes: "), 0, 6, 1, 1);
		gridPane.add(new Label(""+ this.getAllClasses()), 2, 6, 1, 1);
		
		//gridPane.add(new Label(""), 0, 8, 1, 1);


		return gridPane;
	}
	
	public void showDialog() {
		showAndWait();
	}
	
	private Integer getAllClasses() {
		Vector<FmmlxObject> objects = diagram.getObjectsReadOnly();
		int i = 0;
		
		while(i<objects.size()) {
			if(objects.get(i).getLevel().getMinLevel()==0) {
				objects.remove(i);
			}
			i++;
		}
		return objects.size();
	}

}
