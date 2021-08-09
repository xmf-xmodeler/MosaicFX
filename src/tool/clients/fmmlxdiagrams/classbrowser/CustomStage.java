package tool.clients.fmmlxdiagrams.classbrowser;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.helper.IconGenerator;
import tool.xmodeler.XModeler;

public abstract class CustomStage extends Stage{

	private final Stage relativeStage;
	private final Scene scene;
	private final VBox container;
	private final StackPane root;
	private GridControl gridControl;
	private VBoxControl vBoxControl;
	
	
	public CustomStage(String title, Stage relativeStage, int width, int height) {
		super();
		this.setWidth(width);
		this.setHeight(height);
		this.setTitle("Model Browser");
		this.relativeStage=relativeStage;
		this.container = new VBox();
		this.root =  new StackPane(container);
		this.root.setPadding(new Insets(7));
		this.scene = new Scene(root);
		this.gridControl = new GridControl();
		this.vBoxControl = new VBoxControl();
		this.setScene(scene);
		this.setOnShowing(e-> onShow());
		this.getIcons().add(IconGenerator.getImage("shell/mosaic32"));
	}
	
	//protected abstract void initAllElements();
	
	//protected abstract void addAllElementToPane();
	
	//public abstract void onClose();
	
	protected VBox getContainer() {
		return container;
	}
	
	protected GridControl getGridControl() {
		return gridControl;
	}
	
	protected VBoxControl getVBoxControl() {
		return vBoxControl;
	}

	private void onShow() {
		double centerXPosition = relativeStage.getX() + relativeStage.getWidth()/2d;
        double centerYPosition = relativeStage.getY() + relativeStage.getHeight()/2d;
        
        this.setX(centerXPosition - this.getWidth()/2d);
        this.setY(centerYPosition - this.getHeight()/2d);
        this.show();
	}	
	
	protected class GridControl{
		
		protected void addNodesToGrid(GridPane grid, List<Node> nodes) {
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
		
		protected void addNodesToGrid(GridPane grid, List<Node> nodes, int columnIndex) {
			int counter = 0;
			for (Node node : nodes) {
				grid.add(node, columnIndex, counter);
				counter++;
			}
		}
	}
	
	protected class VBoxControl{
		
		protected VBox joinNodeInVBox(Node node1, Node node2) {
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
	
	

}
