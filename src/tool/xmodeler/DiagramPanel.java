package tool.xmodeler;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;

public class DiagramPanel extends SplitPane {
	
	Operation activeOperation ;
	
	public DiagramPanel() {
		
		setOrientation(Orientation.HORIZONTAL);
		
		//Create List of Operations
		ListView<Operation> operationList = new ListView<Operation>();
		operationList.getItems().add(new Class());
		operationList.getItems().add(new Circle());
		
		operationList.getSelectionModel().selectedItemProperty().addListener(
		            new ChangeListener<Operation>() {
		            	
						@Override
						public void changed(ObservableValue<? extends Operation> arg0, Operation arg1, Operation arg2) {
							// TODO Auto-generated method stub
							activeOperation = arg2;
						}
		        });
		
		//Create Canvas
		Canvas canvas = new Canvas(1080,560);
		GraphicsContext gc = canvas.getGraphicsContext2D();
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				activeOperation.draw(gc, event.getX(), event.getY());
			}
        	
		});
	
		getItems().addAll(operationList, new ScrollPane(canvas));
		
		setDividerPosition(0, 0.1);
		
	}

}
