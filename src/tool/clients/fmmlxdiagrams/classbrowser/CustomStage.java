package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.xmodeler.XModeler;

public class CustomStage extends Stage{

	protected Scene scene;
	
	public CustomStage() {
		super();
		setWidth(1100);
		setHeight(800);		
		setOnShowing(e-> onShow());
	}
	
	private void onShow() {
		double centerXPosition = XModeler.getStage().getX() + XModeler.getStage().getWidth()/2d;
        double centerYPosition = XModeler.getStage().getY() + XModeler.getStage().getHeight()/2d;
        
        this.setX(centerXPosition - this.getWidth()/2d);
        this.setY(centerYPosition - this.getHeight()/2d);
        this.show();
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
	
	
	public VBox joinNodeInVBox(Node node1, Node node2) {
		VBox result = new VBox();
		GridPane grid = new GridPane();
		grid.add(node1, 0, 0);
		grid.add(node2, 1, 0);
		
		ColumnConstraints col1 = new ColumnConstraints();
	    col1.setPercentWidth(33);
	    ColumnConstraints col2 = new ColumnConstraints();
	    col2.setPercentWidth(68);

	    grid.getColumnConstraints().addAll(col1,col2);
	    
		result.getChildren().add(grid);
		return result;
	}

}
