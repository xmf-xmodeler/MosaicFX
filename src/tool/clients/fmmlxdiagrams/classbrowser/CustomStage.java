package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class CustomStage extends Stage{

	protected Scene scene;
	
	public CustomStage() {
		super();
		setWidth(1100);
		setHeight(800);
	}
	
	public ListView<FmmlxObject> initializeListView(ObservableList<FmmlxObject> list, SelectionMode selectionMode) {

		ListView<FmmlxObject> listView = new ListView<>(list);
		listView.setPrefHeight(75);

		listView.setCellFactory(param -> new ListCell<FmmlxObject>() {
			@Override
			protected void updateItem(FmmlxObject object, boolean empty) {
				super.updateItem(object, empty);

				if (empty || object == null || object.getName() == null) {
					setText(null);
				} else {
					setText(object.getName());
				}
			}
		});

		listView.getSelectionModel().setSelectionMode(selectionMode);
		return listView;
	}
	
	protected void addNodesToGrid(GridPane grid, List<Node> nodes, int columnIndex) {
		int counter = 0;
		for (Node node : nodes) {
			grid.add(node, columnIndex, counter);
			counter++;
		}
	}
	
	void addNodesToGrid(GridPane grid, List<Node> nodes) {
		int row = 0;
		int i = 0;
		while (i < nodes.size()) {
			grid.add(nodes.get(i), 0, row);
			i++;
			grid.add(nodes.get(i), 1, row);
			row++;
			i++;
		}
	}
	

}
