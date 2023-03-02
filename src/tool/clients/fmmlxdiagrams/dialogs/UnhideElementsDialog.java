package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Vector;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.LevelColorScheme.FixedBlueLevelColorScheme;

public class UnhideElementsDialog extends Dialog<Vector<FmmlxObject>> {
	private AbstractPackageViewer diagram;
	private ButtonType okButtonType = new ButtonType("OK", ButtonData.OK_DONE);
	private Vector<FmmlxObject> hiddenElements = new Vector<>();
	private Vector<FmmlxObject> shownElements = new Vector<>();
	private Vector<FmmlxObject> objects;
	private ListView<FmmlxObject> hiddenElementsListView = new ListView<>();
	private ListView<FmmlxObject> shownElementsListView = new ListView<>();
	private GridPane gridPane = new GridPane();
	private Button toShow = new Button("<<");
	private Button allToShow = new Button("<<<");
	private Button toHide = new Button(">>");
	private Button allToHide = new Button(">>>");
	private Label shownElementsLabel = new Label("Shown Elements");
	private Label hiddenElementsLabel = new Label("Hidden Elements");

	public UnhideElementsDialog(AbstractPackageViewer diagram) {
		super();
		this.diagram = diagram;
		this.setTitle("Hide/ Unhide Elements");
		this.getDialogPane().getButtonTypes().add(okButtonType);
		objects = diagram.getObjects();
		distributeObjects();
		buildListViews();
		customizeButtons();
		buildGridPane();
		setTableDoubleclickAction();
		addOKButtonListener();
	}

	private void distributeObjects() {
		// Collections.sort(objects);
		for (FmmlxObject o : objects) {
			if (o.isHidden()) {
				hiddenElements.add(o);
			} else if (o.isHidden() == false) {
				shownElements.add(o);
			}
		}
	}

	private void buildListViews() {
		hiddenElementsListView.getItems().addAll(hiddenElements);
		sortListView(hiddenElementsListView);
		hiddenElementsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		shownElementsListView.getItems().addAll(shownElements);
		sortListView(shownElementsListView);
		shownElementsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		addCellFactory(shownElementsListView);
		addCellFactory(hiddenElementsListView);
	}

	private void customizeButtons() {
		toShow.setOnAction(e -> {
			shownElementsListView.getItems().addAll(hiddenElementsListView.getSelectionModel().getSelectedItems());
			hiddenElementsListView.getItems().removeAll(hiddenElementsListView.getSelectionModel().getSelectedItems());
			sortListView(shownElementsListView);
		});
		toShow.setMinWidth(75);
		addTooltip("Unhide single Elements or a list of selected Elements", toShow);
		allToShow.setOnAction(e -> {
			shownElementsListView.getItems().addAll(hiddenElementsListView.getItems());
			hiddenElementsListView.getItems().removeAll(hiddenElementsListView.getItems());
			sortListView(shownElementsListView);
		});
		addTooltip("Unhide all Elements", allToShow);
		allToShow.setMinWidth(75);
		toHide.setOnAction(e -> {
			hiddenElementsListView.getItems().addAll(shownElementsListView.getSelectionModel().getSelectedItems());
			shownElementsListView.getItems().removeAll(shownElementsListView.getSelectionModel().getSelectedItems());
			sortListView(hiddenElementsListView);
		});
		addTooltip("Hide single Elements or a list of selected Elements", allToShow);
		toHide.setMinWidth(75);
		allToHide.setOnMouseClicked(e -> {
			hiddenElementsListView.getItems().addAll(shownElementsListView.getItems());
			shownElementsListView.getItems().removeAll(shownElementsListView.getItems());
			sortListView(hiddenElementsListView);
		});
		addTooltip("Hide all Elements", allToHide);
		allToHide.setMinWidth(75);
	}

	private void addTooltip(String text, Button button) {
		Tooltip tooltip = new Tooltip();
		tooltip.setText(text);
		button.setTooltip(tooltip);
	}

	private void buildGridPane() {
		gridPane.add(shownElementsLabel, 0, 0, 1, 1);
		gridPane.add(shownElementsListView, 0, 1, 1, 1);
		GridPane buttonGridPane = new GridPane();
		buttonGridPane.setAlignment(javafx.geometry.Pos.CENTER);
		gridPane.add(buttonGridPane, 1, 1, 1, 1);
		buttonGridPane.add(toHide, 0, 0, 1, 1);
		buttonGridPane.add(toShow, 0, 1, 1, 1);
		buttonGridPane.add(allToHide, 0, 2, 1, 1);
		buttonGridPane.add(allToShow, 0, 3, 1, 1);
		gridPane.add(hiddenElementsLabel, 2, 0, 1, 1);
		gridPane.add(hiddenElementsListView, 2, 1, 1, 1);
		gridPane.setPadding(new Insets(15, 15, 15, 15));
		getDialogPane().setContent(gridPane);
	};

	private void setTableDoubleclickAction() {
		hiddenElementsListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				FmmlxObject selectedItem = hiddenElementsListView.getSelectionModel().getSelectedItem();
				shownElementsListView.getItems().add(selectedItem);
				hiddenElementsListView.getItems().remove(selectedItem);
			}
		});
		shownElementsListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				FmmlxObject selectedItem = shownElementsListView.getSelectionModel().getSelectedItem();
				hiddenElementsListView.getItems().add(selectedItem);
				shownElementsListView.getItems().remove(selectedItem);
			}
		});
	}

	private void addOKButtonListener() {
		setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				Vector<FmmlxObject> result = new Vector<>();
				result.addAll(shownElementsListView.getItems());
				return result;
			}
			return null;
		});

	}
	
	public void showDialog() {
		Optional<Vector<FmmlxObject>> result = showAndWait();
		if (result.isPresent()) {
			new DiagramActions(diagram).hide(result.get(), false);
		}

		Vector<FmmlxObject> resultHide = new Vector<>();
		resultHide.addAll(hiddenElementsListView.getItems());
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
//		if(level == -1) return null;
		FixedBlueLevelColorScheme levelColorScheme = new FixedBlueLevelColorScheme();
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
}