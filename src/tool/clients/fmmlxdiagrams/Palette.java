package tool.clients.fmmlxdiagrams;

import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import tool.clients.fmmlxdiagrams.dialogs.AddInstanceDialog;
import tool.clients.fmmlxdiagrams.dialogs.CreateMetaClassDialog;
import tool.clients.fmmlxdiagrams.dialogs.results.MetaClassDialogResult;

public class Palette extends GridPane {

	private final FmmlxDiagram diagram;
	
	private final double zoomLevel = Math.sqrt(2);

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
		addButton("Add Attribute", 3, e -> System.out.println("Button 3"));
		addButton("Edit Attribute", 4, e -> System.out.println("Button 4"));
		addButton("Remove Attribute", 5, e -> System.out.println("Button 5"));
		addButton("Change Slot Value", 6, e -> System.out.println("Button 6"));
		addButton("Zoom +", 7, e -> zoomIn());
		addButton("Zoom -", 8, e -> zoomOut());
		addButton("Zoom 100%", 9, e -> zoomOne());

		ColumnConstraints cc = new ColumnConstraints();
		cc.setFillWidth(true);
		cc.setHgrow(Priority.ALWAYS);
		getColumnConstraints().add(cc);

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
		CountDownLatch l = new CountDownLatch(2);

		Platform.runLater(() -> {
			CreateMetaClassDialog dlg = new CreateMetaClassDialog();
			dlg.setTitle("Add metaclass");
			Optional<MetaClassDialogResult> result = dlg.showAndWait();

			if (result.isPresent()) {
				final MetaClassDialogResult mcdResult = result.get();

				Canvas canvas = diagram.getCanvas();
				canvas.setCursor(Cursor.CROSSHAIR);

				EventHandler<MouseEvent> chooseLocation = new EventHandler<MouseEvent>() {
					public void handle(MouseEvent e) {

						int x = (int) e.getX();
						int y = (int) e.getY();

						if (x > 0 && y > 0) {
							diagram.addMetaClass(mcdResult.getName(), mcdResult.getLevel(),
									new Vector<Integer>(mcdResult.getParent()), false, x, y);

							canvas.setCursor(Cursor.DEFAULT);
							canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);

							diagram.updateDiagram();
							l.countDown();
						}
					};
				};
				canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseLocation);
			}
		});
	}

	private void addInstanceDialog() {
		CountDownLatch l = new CountDownLatch(1);

		Platform.runLater(() -> {
			AddInstanceDialog dlg = new AddInstanceDialog("");
			dlg.setTitle("Add instance");
			dlg.showAndWait();

			l.countDown();
		});
	}
	
	private void zoomIn() {
		diagram.setZoom(diagram.getZoom() * zoomLevel);
		diagram.redraw();
		}
	
	private void zoomOut() {
		diagram.setZoom(diagram.getZoom() / zoomLevel);
		diagram.redraw();
	}
	
	private void zoomOne() {
		diagram.setZoom(1.);
		diagram.redraw();
	}

	private void test2() {
		diagram.addInstance(diagram.getTestClassId(), "TestInstance", new Vector<Integer>(), false, 1,1);
		diagram.updateDiagram();
	}
}