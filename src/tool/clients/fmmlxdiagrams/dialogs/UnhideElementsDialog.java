package tool.clients.fmmlxdiagrams.dialogs;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Vector;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.LevelColorScheme.FixedBlueLevelColorScheme;
import tool.clients.fmmlxdiagrams.Note;
import tool.helper.auxilaryFX.JavaFxButtonAuxilary;
import tool.helper.auxilaryFX.JavaFxTooltipAuxilary;

@SuppressWarnings("rawtypes")
public class UnhideElementsDialog extends Dialog {
	private AbstractPackageViewer diagram;
	private Vector<tool.clients.fmmlxdiagrams.Node> hiddenObjects = new Vector<>();
	private Vector<tool.clients.fmmlxdiagrams.Node> shownObjects = new Vector<>();
	private ListView<tool.clients.fmmlxdiagrams.Node> hiddenObjectsListView = new ListView<>();
	private ListView<tool.clients.fmmlxdiagrams.Node> shownObjectsListView = new ListView<>();
	
	public UnhideElementsDialog(AbstractPackageViewer diagram) {
		this.diagram = diagram;
		setTitle("Hide/ Unhide Elements");
		ButtonType okButtonType = ButtonType.OK;
		getDialogPane().getButtonTypes().add(okButtonType);
		distributeNodes();
		buildListViews();
		buildButtonVBox();
		setTableDoubleclickAction();
		GridPane selectionView = buildGridPane();
		hiddenObjectsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		shownObjectsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);		
		getDialogPane().setContent(selectionView);
	}

	private void distributeNodes() {
		for (tool.clients.fmmlxdiagrams.Node n : diagram.getAllNodes()) {
			if (n.isHidden()) {
				hiddenObjects.add(n);
			} else {
				shownObjects.add(n);
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
				tool.clients.fmmlxdiagrams.Node selectedItem = hiddenObjectsListView.getSelectionModel().getSelectedItem();
				shownObjectsListView.getItems().add(selectedItem);
				hiddenObjectsListView.getItems().remove(selectedItem);
			}
		});
		shownObjectsListView.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				tool.clients.fmmlxdiagrams.Node selectedItem = shownObjectsListView.getSelectionModel().getSelectedItem();
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
	}

	public void showDialog() {
		@SuppressWarnings("rawtypes")
		Optional result = showAndWait();
		if (result.isPresent()) {

			//make all nodes visible that were selected by the user to be visible
			for (tool.clients.fmmlxdiagrams.Node node : hiddenObjectsListView.getItems()) {
				node.hide(diagram);
			}
			//make all nodes invisible that were selected by the user to not be visible
			for (tool.clients.fmmlxdiagrams.Node node : shownObjectsListView.getItems()) {
				node.unhide(diagram);
			}
		}
	}

	private void addCellFactory(ListView<tool.clients.fmmlxdiagrams.Node> listView) {
		listView.setCellFactory(lv -> {
			return new ListCell<tool.clients.fmmlxdiagrams.Node>() {
				protected void updateItem(tool.clients.fmmlxdiagrams.Node o, boolean empty) {
					super.updateItem(o, empty);
					if (o != null) {
						if (o instanceof FmmlxObject) {
							renderListEntryForFmmlxObject(o);
						}
						if (o instanceof Note) {
							Note n = (Note) o;
							setText("Note Id:" + n.getId());
							setGraphic(getNoteGraphic());
						}
					} else {
						setText("");
						setGraphic(null);
					}
				}

				private void renderListEntryForFmmlxObject(tool.clients.fmmlxdiagrams.Node o) {
					FmmlxObject o2 = (FmmlxObject) o;

					if (o2.isAbstract())
						setText("(" + o2.getName() + " ^" + o2.getMetaClassName() + "^ " + ")");
					else
						setText(o2.getName() + " ^" + o2.getMetaClassName() + "^");
					setGraphic(getClassLevelGraphic(o2.getLevel().getMinLevel()));
				}
			};
		});
	}

	private Node getClassLevelGraphic(int level) {
		FixedBlueLevelColorScheme levelColorScheme = new FixedBlueLevelColorScheme();
		final double SIZE = 16;
		Canvas canvas = new Canvas(SIZE, SIZE);
		String text = level == -1 ? "?" : (level + "");
		Text temp = new Text(text);
		
		GraphicsContext g = canvas.getGraphicsContext2D();
		g.setFill(levelColorScheme.getLevelBgColor(level));
		g.fillRoundRect(0, 0, SIZE, SIZE, SIZE / 2, SIZE / 2);
		g.setFill(levelColorScheme.getLevelFgColor(level, 1.));
		g.fillText(text, SIZE / 2 - temp.getLayoutBounds().getWidth() / 2.,SIZE / 2 + temp.getLayoutBounds().getHeight() / 2. - 4);
		return canvas;
	}
	
	private Node getNoteGraphic() {
		final double SIZE = 16;
		Canvas canvas = new Canvas(SIZE, SIZE);
		String text = "N ";
		Text temp = new Text(text);
		GraphicsContext g = canvas.getGraphicsContext2D();
		g.setFill(javafx.scene.paint.Color.valueOf("#F9EC72"));
		g.fillRoundRect(0, 0, SIZE, SIZE, SIZE / 2, SIZE / 2);
		g.setFill(Color.BLACK);
		g.fillText(text, SIZE / 2 - temp.getLayoutBounds().getWidth() / 2.,
				SIZE / 2 + temp.getLayoutBounds().getHeight() / 2. - 4);
		return canvas;
	}

	@SuppressWarnings("unchecked")
	public void sortListView(ListView<tool.clients.fmmlxdiagrams.Node> listView) {		
		Vector<FmmlxObject> sortedObjects = sortObjects( new Vector<tool.clients.fmmlxdiagrams.Node> (listView.getItems()));
		Vector<Note> notesV = sortNotes(new Vector<tool.clients.fmmlxdiagrams.Node> (listView.getItems()));
		
		listView.getItems().clear();
		listView.getItems().addAll(sortedObjects);
		listView.getItems().addAll(notesV);
	}

	private Vector<Note> sortNotes(Vector<tool.clients.fmmlxdiagrams.Node> vector) {
		Vector<Note> notesV = new Vector<>();
		for (tool.clients.fmmlxdiagrams.Node node : vector) {
			if (node instanceof Note) {
				notesV.add((Note) node);
			}
		}
		Collections.sort(notesV, new Comparator<Note>() {
			@Override
			public int compare(Note o1, Note o2) {
				if (o1.getId() > o2.getId()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		return notesV;
	}

	private Vector<FmmlxObject> sortObjects(Vector<tool.clients.fmmlxdiagrams.Node> v) {
		Vector<FmmlxObject> objectsV = new Vector<>();
		for (tool.clients.fmmlxdiagrams.Node node : v) {
			if (node instanceof FmmlxObject) {
				objectsV.add((FmmlxObject) node);
			}
		} 
		Collections.sort(objectsV, new Comparator<FmmlxObject>() {
			public int compare(FmmlxObject thisObject, FmmlxObject anotherObject) {
				if (thisObject.getLevel().getMinLevel() > anotherObject.getLevel().getMinLevel()) {
					return -1;
				} else if (thisObject.getLevel().getMinLevel() < anotherObject.getLevel().getMinLevel()) {
					return 1;
				} else {
					return thisObject.getName().compareTo(anotherObject.getName());
				}
			}
		});
		return objectsV;
	}
	
	public void sortBothListViews() {
		sortListView(hiddenObjectsListView);
		sortListView(shownObjectsListView);
	}
}