package tool.clients.fmmlxdiagrams.graphics.wizard;

import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.graphics.AbstractSyntax;
import tool.clients.fmmlxdiagrams.graphics.NodeGroup;
import tool.clients.fmmlxdiagrams.graphics.View;

public class PreviewGrid<NodeElementType extends NodeGroup> extends GridPane {
	
	private int selectedIndex = -1;
	private Vector<NodeElementType> syntaxes = new Vector<>();
	private SelectionChangedListener<NodeElementType> selectionChangedListener = e -> {};
	
	public PreviewGrid(Vector<NodeElementType> syntaxes) {
		super();
		this.syntaxes = syntaxes;
		setMaxWidth(500);
		setMinWidth(500);
		setPrefWidth(500);
		updateContent();
	}
	
	public void select(NodeElementType cs) {
		int oldIndex = selectedIndex;
		selectedIndex = -1;
		for(int i = 0; i < syntaxes.size(); i++) {
			if(syntaxes.get(i) == cs) {
				selectedIndex = i;
			}
		}
		updateContent();
		if(oldIndex != selectedIndex) selectionChangedListener.selectionChanged(cs);
	}

	public void updateContent() {
		getChildren().clear();
		for(int i = 0; i < syntaxes.size(); i++) {
			add(new ConcreteSyntaxTile(syntaxes.get(i)), i%4, i/4);
		}
	}
	
	private class ConcreteSyntaxTile extends VBox {
		private NodeElementType group;
		private ConcreteSyntaxTile(NodeElementType group) {
			this.group = group;
			setMinWidth(125);
			setMinHeight(125);
			setMaxWidth(125);
			setMaxHeight(125);

			final MiniCanvas canvas = new MiniCanvas();
			
			getChildren().add(canvas);
			getChildren().add(new Label(group.toString()));
			if(group instanceof AbstractSyntax) {
				getChildren().add(new Label(((AbstractSyntax)group).getFile().getName()));
			}
			group.paintOn(canvas, false);
			
			ChangeListener<Number> canvasChangeListener = (obs, oldVal, newVal) -> {
				Affine a = group.getZoomViewTransform(canvas.canvas);
				canvas.affine = a;
				if(PreviewGrid.this.getChildren().indexOf(this) == selectedIndex) {
					GraphicsContext gc = canvas.canvas.getGraphicsContext2D();
					gc.setFill(Color.CORNFLOWERBLUE);
					gc.setTransform(new Affine());
					gc.fillRect(0, 0, canvas.canvas.getWidth(), canvas.canvas.getHeight());
				}
				group.paintOn(canvas, false);
			};
			
			canvas.widthProperty().addListener(canvasChangeListener);
			canvas.heightProperty().addListener(canvasChangeListener);
			
			setOnMouseClicked(me -> {
				if(me.getButton() == MouseButton.PRIMARY) {
					selectedIndex = PreviewGrid.this.getChildren().indexOf(this);
					updateContent();
					selectionChangedListener.selectionChanged(this.group);
				}
			});
		}
	}	

	public interface SelectionChangedListener<NodeElementType extends NodeGroup> {
		public void selectionChanged(NodeElementType newSelection);
	}
	
	public void setOnSelectionChanged(SelectionChangedListener<NodeElementType> selectionChangedListener) {
		this.selectionChangedListener = selectionChangedListener;
	}
	
	private class MiniCanvas extends Pane implements View {
		Canvas canvas; 
		Affine affine;		

		public MiniCanvas() {
			canvas = new Canvas(125,80);
			affine = new Affine();
			getChildren().add(canvas);
			setMinSize(125,80);
			setMaxSize(125,80);
			setPrefSize(125,80);
			canvas.widthProperty().bind(this.widthProperty());
			canvas.heightProperty().bind(this.heightProperty());
		}
		
		@Override public Canvas getCanvas() { return canvas; }
		@Override public Affine getCanvasTransform() { return affine; }
		@Override public void centerObject() {}
		@Override public void centerObject(FmmlxObject affectedObject) {}
	}

	public NodeElementType getSelectedElement() {
		return syntaxes.get(selectedIndex);
	}
}
