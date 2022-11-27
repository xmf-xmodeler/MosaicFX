package tool.clients.fmmlxdiagrams.graphics.wizard;

import java.util.Vector;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ButtonBar.ButtonData;
import tool.clients.fmmlxdiagrams.graphics.SVGGroup;

public class SvgChooseDialog  extends Dialog<SVGGroup>{

	Vector<SVGGroup> svgCache;
	
	public SvgChooseDialog(Vector<SVGGroup> svgCache, String id) {
		this.svgCache = svgCache;
		
		final PreviewGrid<SVGGroup> svgPane = new PreviewGrid<>(svgCache);
		Label idLabel = new Label("id");
		TextField idField = new TextField(id == null?("svg"+ConcreteSyntaxWizard.getRandomID()):id);
		HBox hBox = new HBox(idLabel, idField);
		ScrollPane pane = new ScrollPane(svgPane);
		pane.setMaxHeight(500);
		pane.setMinHeight(500);
		pane.setPrefHeight(500);
		VBox vBox = new VBox(hBox, pane);
		getDialogPane().setContent(vBox);
		getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

		setResultConverter((dialogButton) -> {
            if (dialogButton != null && dialogButton.getButtonData() == ButtonData.OK_DONE){
            	svgPane.getSelectedElement().setID(idField.getText());
            	return svgPane.getSelectedElement();
            } else {
        	    return null;
            }
	    });
	}

}
