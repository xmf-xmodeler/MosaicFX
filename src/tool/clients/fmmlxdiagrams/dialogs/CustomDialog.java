package tool.clients.fmmlxdiagrams.dialogs;

import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class CustomDialog<R> extends Dialog<R> {

	protected final int COLUMN_WIDTH = 150;

	GridPane grid;

	public CustomDialog() {
		super();
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
			grid.getColumnConstraints().add(cc);
		}
		
		return grid;
	}
}
