package tool.clients.fmmlxdiagrams.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class CustomDialog<R> extends Dialog<R> {

	protected int COLUMN_WIDTH = 75;

	FlowPane flow;
	GridPane grid;
	Label errorLabel;

	public CustomDialog() {
		super();

		initializeGrid();
		flow = new FlowPane();
		flow.setHgap(3);
		flow.setVgap(3);
		flow.setPrefWrapLength(250);

		flow.getChildren().add(grid);

		errorLabel = new Label();
		errorLabel.setTextFill(Color.RED);
		flow.getChildren().add(errorLabel);
	}

	public GridPane initializeGrid() {
		grid = new GridPane();
		grid.setHgap(3);
		grid.setVgap(3);
		grid.setPadding(new Insets(10, 10, 10, 10));

		ColumnConstraints cc;
		for (int i = 0; i < 2; i++) {
			cc = new ColumnConstraints();
			cc.setMaxWidth(COLUMN_WIDTH);
			cc.setMinWidth(COLUMN_WIDTH);
			cc.setFillWidth(true);
			cc.setHgrow(Priority.ALWAYS);
			// double size for second column
			COLUMN_WIDTH = COLUMN_WIDTH * 2;
			grid.getColumnConstraints().add(cc);
		}
		return grid;
	}

	public Label getErrorLabel() {
		return errorLabel;
	}

	public boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

}
