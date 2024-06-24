package tool.clients.fmmlxdiagrams.fmmlxdiagram.diagramViewComponents;

import java.util.Optional;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram.DiagramCanvas;

class MyTab extends Tab {
	/**
	 * 
	 */
	private final FmmlxDiagram fmmlxDiagram;
	final Label label;
	DiagramCanvas view;
	
	MyTab(DiagramViewPane diagramRootPane, DiagramCanvas view) {
		super("", view);
		this.fmmlxDiagram = diagramRootPane.getDiagram();
		this.view = view;
		this.label = new Label(view.name);
		setLabel();
		setCloseListener();
	    
		
	}
	
	public void setCloseListener() {
		this.setOnCloseRequest(new EventHandler<Event>() {

	        public void handle(Event e) {
	        	Alert alert = new Alert(AlertType.CONFIRMATION);
	        	alert.setTitle("Confirmation Dialog");
	        	alert.setHeaderText("Close Tab");
	        	alert.setContentText("Press OK to close the tab!");

	        	Optional<ButtonType> result = alert.showAndWait();
	        	if (result.get() == ButtonType.OK){
	        	   MyTab.this.fmmlxDiagram.views.remove(view);
	        	} else {
	        		e.consume();
	        	}
	        }
	    });
	}

	public void setView(DiagramCanvas newView) {
		this.view = newView;
		setContent(newView);
		label.setText(view.name);
		setLabel();
	}

	private void setLabel() {			
		setGraphic(label);
		label.setOnMouseClicked((event) -> {
			if (event.getClickCount() == 2) {
				TextInputDialog dialog = new TextInputDialog("new tab name");
				dialog.setTitle("Change tab name");
				dialog.setHeaderText("Change tab name");
				dialog.setContentText("Please enter the new name for this tab:");
				java.util.Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					view.name = result.get();
					label.setText(view.name);
				}
			}
		});
	}

	public MyTab(DiagramViewPane diagramRootPane) {
		super("*", null);
		this.fmmlxDiagram = diagramRootPane.getDiagram();
		this.label = new Label("void");
		setCloseListener();
		
	}		
}