package tool.clients.fmmlxdiagrams;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import tool.clients.fmmlxdiagrams.dialogs.AddAttributeDialog;
import tool.clients.fmmlxdiagrams.dialogs.AddInstanceDialog;
import tool.clients.fmmlxdiagrams.dialogs.CreateMetaClassDialog;
import tool.clients.fmmlxdiagrams.dialogs.MetaClassDialogResult;

public class Palette extends GridPane {

	private final FmmlxDiagram diagram;

	public Palette(FmmlxDiagram diagram) {
		this.diagram = diagram;
		setMinSize(200, 600);
		setPrefSize(200, 600);
		setPadding(new Insets(10, 10, 10, 10));
		setVgap(5);
		setHgap(5);

		addButton("Add MetaClass", 0, e -> addMetaClassDialog());
		addButton("Add Instance", 1, e -> addInstanceDialog());
		addButton("Remove MetaClass/Instance", 2, e -> System.out.println("Button 2"));
		addButton("Add Attribute", 3, e -> addAttributeDialog());
		addButton("Edit Attribute", 4, e -> System.out.println("Button 4"));
		addButton("Remove Attribute", 5, e -> System.out.println("Button 5"));
		addButton("Change Slot Value", 6, e -> System.out.println("Button 6"));

		ColumnConstraints cc = new ColumnConstraints();
		cc.setFillWidth(true);
		cc.setHgrow(Priority.ALWAYS);
		getColumnConstraints().add(cc);

	}

	private void addAttributeDialog() {
		// TODO Auto-generated method stub
		CountDownLatch l = new CountDownLatch(1);
		
		Platform.runLater(() -> {
			AddAttributeDialog dlg = new AddAttributeDialog();
			Optional<MetaClassDialogResult> opt = dlg.showAndWait();
			
			if(opt.isPresent()) {
				MetaClassDialogResult test = opt.get();
				System.out.println("!!!!!!!!!!!!! " + test.getName() + " " + test.getLevel());
			}
			
			diagram.updateDiagram();
			l.countDown();
		});
	}

	private void addButton(String string, int y, EventHandler<ActionEvent> eventHandler) {
		Button button = new Button(string);
		button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		button.setPrefHeight(50);
		button.setOnAction(eventHandler);
		add(button, 0, y);

	}

	private FmmlxDiagramCommunicator getComm() {
		return diagram.comm;
	}

	private void addMetaClassDialog() {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog();
			Optional<MetaClassDialogResult> opt = dlg.showAndWait();
			
			if(opt.isPresent()) {
				MetaClassDialogResult test = opt.get();
				System.out.println("!!!!!!!!!!!!! " + test.getName() + " " + test.getLevel());
			}
			
			diagram.updateDiagram();
			l.countDown();
		});
	}
	
	private void addInstanceDialog() {
		CountDownLatch l = new CountDownLatch(1);
		
		Platform.runLater(() -> {
			AddInstanceDialog dlg = new AddInstanceDialog("");
			dlg.showAndWait();
			
			l.countDown();
		});
	}


}
