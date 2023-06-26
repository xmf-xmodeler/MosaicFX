package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Vector;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.LevelColorScheme.FixedBlueLevelColorScheme;
import tool.helper.auxilaryFX.JavaFxButtonAuxilary;
import tool.helper.auxilaryFX.JavaFxTooltipAuxilary;

public class UnhideElementsDialog extends Dialog<Vector<FmmlxObject>> {
	private AbstractPackageViewer diagram;
	private Vector<FmmlxObject> hiddenObjects = new Vector<>();
	private Vector<FmmlxObject> shownObjects = new Vector<>();
	private Vector<FmmlxObject> objects;
	private ListView<FmmlxObject> hiddenObjectsListView = new ListView<>();
	private ListView<FmmlxObject> shownObjectsListView = new ListView<>();
	private FixedBlueLevelColorScheme levelColorScheme = new FixedBlueLevelColorScheme();

	public UnhideElementsDialog(AbstractPackageViewer diagram) {
		this.diagram = diagram;
		setTitle("Hide/ Unhide Elements");
		ButtonType okButtonType = ButtonType.OK;
		setResultConverter(dialogButton -> {
			return new Vector<FmmlxObject>(shownObjectsListView.getItems());
		});
		getDialogPane().getButtonTypes().add(okButtonType);
		objects = diagram.getObjectsReadOnly();
		distributeObjects();
		buildListViews();
		buildButtonVBox();
		setTableDoubleclickAction();
		GridPane selectionView = buildGridPane();
		hiddenObjectsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		shownObjectsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		getDialogPane().setContent(selectionView);
	}

	private void distributeObjects() {
		for (FmmlxObject o : objects) {
			if (o.isHidden()) {
				hiddenObjects.add(o);
			} else if (o.isHidden() == false) {
				shownObjects.add(o);
			}
		}
	}

	private void buildListViews() {
		hiddenObjectsListView.getItems().addAll(hiddenObjects);
		shownObjectsListView.getItems().addAll(shownObjects);
		sortBothListViews();
		addCellFactory(shownObjectsListView);
		addCellFactory(hiddenObjectsListView);
		hiddenObjectsListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				addSelectedObjectsToShownObjectsList();
				sortListView(shownObjectsListView);
			}
		});
		shownObjectsListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				addSelectedObjectsToHiddenObjectsList();
				sortListView(hiddenObjectsListView);
			}
		});
	}

	private VBox buildButtonVBox() {
		VBox buttonVBox = new VBox();
		buttonVBox.setAlignment(Pos.CENTER);
		buttonVBox.setPrefWidth(60);

		Button toShow = JavaFxButtonAuxilary.createButton("<<", e -> {
			addSelectedObjectsToShownObjectsList();
		});
		toShow.setMinWidth(buttonVBox.getPrefWidth());
		JavaFxTooltipAuxilary.addTooltip(toShow, "Unhide single Object or a list of selected Objects");

		Button allToShow = JavaFxButtonAuxilary.createButton("<<<", e -> {
			addAllObjectsToShownObjectsList();
		});
		allToShow.setMinWidth(buttonVBox.getPrefWidth());
		JavaFxTooltipAuxilary.addTooltip(allToShow, "Unhide all Objects");

		// Only used for smoother UI
		Button dummy = new Button();
		dummy.setVisible(false);

		Button toHide = JavaFxButtonAuxilary.createButton(">>", e -> {
			addSelectedObjectsToHiddenObjectsList();
		});
		toHide.setMinWidth(buttonVBox.getPrefWidth());
		JavaFxTooltipAuxilary.addTooltip(toHide, "Hide single Object or a list of selected Objects");

		Button allToHide = JavaFxButtonAuxilary.createButton(">>>", e -> {
			addAllObjectsToHiddenObjectsList();
		});
		allToHide.setMinWidth(buttonVBox.getPrefWidth());
		JavaFxTooltipAuxilary.addTooltip(allToHide, "Hide all Objects");

		buttonVBox.getChildren().addAll(toHide, allToHide, dummy, toShow, allToShow);
		return buttonVBox;
	}

	private void addAllObjectsToHiddenObjectsList() {
		hiddenObjectsListView.getItems().addAll(shownObjectsListView.getItems());
		shownObjectsListView.getItems().removeAll(shownObjectsListView.getItems());
		sortListView(hiddenObjectsListView);
	}

	private void addSelectedObjectsToHiddenObjectsList() {
		hiddenObjectsListView.getItems().addAll(shownObjectsListView.getSelectionModel().getSelectedItems());
		shownObjectsListView.getItems().removeAll(shownObjectsListView.getSelectionModel().getSelectedItems());
		sortListView(hiddenObjectsListView);
	}

	private void addAllObjectsToShownObjectsList() {
		shownObjectsListView.getItems().addAll(hiddenObjectsListView.getItems());
		hiddenObjectsListView.getItems().removeAll(hiddenObjectsListView.getItems());
		sortListView(shownObjectsListView);
	}

	private void addSelectedObjectsToShownObjectsList() {
		shownObjectsListView.getItems().addAll(hiddenObjectsListView.getSelectionModel().getSelectedItems());
		hiddenObjectsListView.getItems().removeAll(hiddenObjectsListView.getSelectionModel().getSelectedItems());
		sortListView(shownObjectsListView);
	}
	
	private void setTableDoubleclickAction() {
		hiddenObjectsListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				FmmlxObject selectedItem = hiddenObjectsListView.getSelectionModel().getSelectedItem();
				shownObjectsListView.getItems().add(selectedItem);
				hiddenObjectsListView.getItems().remove(selectedItem);
			}
		});
		shownObjectsListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				FmmlxObject selectedItem = shownObjectsListView.getSelectionModel().getSelectedItem();
				hiddenObjectsListView.getItems().add(selectedItem);
				shownObjectsListView.getItems().remove(selectedItem);
			}
		});
	}

	private GridPane buildGridPane() {
		GridPane gridPane = new GridPane();
		gridPane.setHgap(7);
		gridPane.add(new Label("Shown Elements"), 0, 0, 1, 1);
		gridPane.add(shownObjectsListView, 0, 1, 1, 1);
		VBox toolBar = buildButtonVBox();
		gridPane.add(toolBar, 1, 1, 1, 1);
		gridPane.add(new Label("Hidden Elements"), 2, 0, 1, 1);
		gridPane.add(hiddenObjectsListView, 2, 1, 1, 1);
		gridPane.setPadding(new Insets(15, 15, 15, 15));
		return gridPane;
	};

	public void showDialog() {
		Optional<Vector<FmmlxObject>> result = showAndWait();
		if (result.isPresent()) {
			new DiagramActions(diagram).hide(result.get(), false);
		}
		Vector<FmmlxObject> resultHide = new Vector<>();
		resultHide.addAll(hiddenObjectsListView.getItems());
		new DiagramActions(diagram).hide(resultHide, true);
	}

	private void addCellFactory(ListView<FmmlxObject> listView) {
		listView.setCellFactory(lv -> {
			return new ListCell<FmmlxObject>() {
				protected void updateItem(FmmlxObject o, boolean empty) {
					super.updateItem(o, empty);
					if (o != null) {
						if (o.isAbstract())
							setText("(" + o.getName() + " ^" + o.getMetaClassName() + "^ " + ")");
						else
							setText(o.getName() + " ^" + o.getMetaClassName() + "^");
						setGraphic(getClassLevelGraphic(o.getLevel()));
					} else {
						setText("");
						setGraphic(null);
					}
				}
			};
		});
	}

	private Node getClassLevelGraphic(int level) {
		double SIZE = 16;
		Canvas canvas = new Canvas(SIZE, SIZE);
		String text = level == -1 ? "?" : (level + "");
		Text temp = new Text(text);
		GraphicsContext g = canvas.getGraphicsContext2D();
		g.setFill(levelColorScheme.getLevelBgColor(level));
		g.fillRoundRect(0, 0, SIZE, SIZE, SIZE / 2, SIZE / 2);
		g.setFill(levelColorScheme.getLevelFgColor(level, 1.));
		g.fillText(text, SIZE / 2 - temp.getLayoutBounds().getWidth() / 2.,
				SIZE / 2 + temp.getLayoutBounds().getHeight() / 2. - 4);
		return canvas;
	}

	public void sortListView(ListView<FmmlxObject> listView) {
		Vector<FmmlxObject> v = new Vector<>(listView.getItems());
		Collections.sort(v, new Comparator<FmmlxObject>() {
			public int compare(FmmlxObject thisObject, FmmlxObject anotherObject) {
				if (thisObject.getLevel() > anotherObject.getLevel()) {
					return -1;
				} else if (thisObject.getLevel() < anotherObject.getLevel()) {
					return 1;
				} else {
					return thisObject.getName().compareTo(anotherObject.getName());
				}
			}
		});
		listView.getItems().clear();
		listView.getItems().addAll(v);
	}
	
	public void sortBothListViews() {
		sortListView(hiddenObjectsListView);
		sortListView(shownObjectsListView);
	}
}