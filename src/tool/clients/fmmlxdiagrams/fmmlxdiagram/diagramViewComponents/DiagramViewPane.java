package tool.clients.fmmlxdiagrams.fmmlxdiagram.diagramViewComponents;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.util.Callback;
import tool.clients.fmmlxdiagrams.CanvasElement;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.DiagramDisplayProperty;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxDiagramControlKeyHandler;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.Issue;
import tool.clients.fmmlxdiagrams.classbrowser.ModelBrowser;
import tool.clients.fmmlxdiagrams.dialogs.PropertyType;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram.DiagramCanvas;
import tool.clients.fmmlxdiagrams.graphics.ConcreteSyntax;
import tool.clients.fmmlxdiagrams.graphics.ConcreteSyntaxPattern;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;
import tool.clients.fmmlxdiagrams.newpalette.FmmlxPalette;
import tool.helper.persistence.XMLCreator;
import tool.xmodeler.tool_introduction.DiagramPreperationActions;
import tool.xmodeler.tool_introduction.DiagramViewState;
import tool.xmodeler.tool_introduction.ToolIntroductionManager;

/**
 * SplitPane instance that serves as full gui for the diagram view. All diagram
 * view parts are build and controlled in this class.
 */
public class DiagramViewPane extends SplitPane {

	private FmmlxDiagram diagram;

	private VBox canvasContainer;
	private TabPane diagramViewPane;
	private SplitPane palettSideBar;
	private SplitPane splitPane3;
	private ScrollPane issueScrollPane;
	private DiagramViewHeadToolBar diagramViewToolbar;
	private FmmlxPalette fmmlxPalette;
	private TableView<Issue> issueTable;
	private Vector<Vector<Object>> listOfViews;
	private DiagramViewState diagramViewState = null;

	private final Set<KeyCode> pressedKeys = new HashSet<>();
	public final HashMap<String, ConcreteSyntax> syntaxes = new HashMap<>();

	public DiagramViewPane(FmmlxDiagram fmmlxDiagram, Vector<Vector<Object>> listOfViews,
			DiagramViewHeadToolBar toolBar) {

		this.listOfViews = listOfViews;
		diagramViewToolbar = toolBar;
		diagram = fmmlxDiagram;

		initDiagramViewState();
		buildViewComponents(diagramViewState);
	}

	private void initDiagramViewState() {
		if (isIntroductionMode()) {
			diagramViewState = DiagramViewState.CREATE_CLASS_MOVIE;
			ToolIntroductionManager.getInstance().setDiagram(diagram);
		} else {
			diagramViewState = DiagramViewState.FULL_GUI;
		}
	}

	public  boolean isIntroductionMode() {
		return diagram.getProjectName().equals("ToolIntroductionABC")
				&& diagram.getDiagramName().equals("ToolIntroductionDiagramXYZ");
	}

	private void buildViewComponents(DiagramViewState state) {
		configPane();
		palettSideBar = buildPalettSideBar();
		DiagramCanvas zoomView = buildZoomView();

		fmmlxPalette = new FmmlxPalette(this, state);

		palettSideBar.getItems().clear();
		palettSideBar.getItems().addAll(fmmlxPalette.getToolBar(), zoomView);

		composeCanvasContainer(listOfViews);

		getItems().clear();

		getItems().addAll(palettSideBar, canvasContainer);
		// bug... by update the divider position is slightly different to original
		// position
		setDividerPosition(0, 0.2);

		// state invariant operations
		buildIssuePane();
		switchTableOnAndOffForIssues();
		initConcreteSyntax();

	}

	private void configPane() {
		setOrientation(Orientation.HORIZONTAL);
		setOnKeyReleased(this::handleKeyReleasedGlobal);
	}

	/**
	 * handles released keys on the hole pane
	 * 
	 * @param key to be handled
	 */
	private void handleKeyReleasedGlobal(KeyEvent event) {
		if (event.getCode() == KeyCode.ESCAPE) {
			diagram.getActiveDiagramViewPane().escapeCreationMode();
		}

		if (event.getCode() == KeyCode.DIGIT1) {
			// Use for tests
		}
	}

	private void composeCanvasContainer(Vector<Vector<Object>> listOfViews) {
		canvasContainer = new VBox();
		diagramViewPane = buildDiagramViewPane(listOfViews);
		canvasContainer.getChildren().clear();
		canvasContainer.getChildren().addAll(diagramViewToolbar, diagramViewPane);
	}

	private TabPane buildDiagramViewPane(Vector<Vector<Object>> listOfViews) {
		TabPane tabPane = new TabPane();

		for (Vector<Object> view : listOfViews) {
			DiagramCanvas dvp = diagram.new DiagramCanvas((String) view.get(0), false);
			float xx = 1.0f, tx = 0.0f, ty = 0.0f;
			try {
				xx = (float) view.get(1);
			} catch (Exception e) {
				System.err.println("Cannot read xx: " + e.getMessage() + " Using default instead");
			}
			try {
				tx = (float) view.get(2);
			} catch (Exception e) {
				System.err.println("Cannot read tx: " + e.getMessage() + " Using default instead");
			}
			try {
				ty = (float) view.get(3);
			} catch (Exception e) {
				System.err.println("Cannot read tx: " + e.getMessage() + " Using default instead");
			}
			dvp.canvasTransform = new Affine(xx, 0, tx, 0, xx, ty);
			tabPane.getTabs().add(new MyTab(this, dvp));
		}

		if (listOfViews.size() == 0) {
			DiagramCanvas dvp = diagram.new DiagramCanvas("default view", false);
			tabPane.getTabs().add(new MyTab(this, dvp));
		}

		tabPane.getTabs().add(new MyTab(this));

		tabPane.setFocusTraversable(true);
		tabPane.setOnKeyReleased(keyEvent -> {
			pressedKeys.remove(keyEvent.getCode());

			if (keyEvent.isControlDown()) {
				FmmlxDiagramControlKeyHandler handler = new FmmlxDiagramControlKeyHandler(
						FmmlxDiagramCommunicator.getDiagram(diagram.getID()));
				handler.handle(keyEvent.getCode());
			}

			if (keyEvent.getCode() == javafx.scene.input.KeyCode.F5) {
				diagram.getComm().triggerUpdate();
			}

			if (keyEvent.getCode() == javafx.scene.input.KeyCode.DELETE) {
				Vector<CanvasElement> hitObjects = diagram.getSelectedObjects();
				for (CanvasElement element : hitObjects) {
					if (element instanceof FmmlxObject) {
						new DiagramActions(diagram).removeDialog((FmmlxObject) element, PropertyType.Class);
					}
				}
			}

		});
		tabPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				pressedKeys.add(e.getCode());
				if (getPressedKeys().contains(KeyCode.CONTROL) && getPressedKeys().contains(KeyCode.A)) {
					diagram.selectAll();
				}
				if (getPressedKeys().contains(KeyCode.CONTROL) && getPressedKeys().contains(KeyCode.S)) {
					new XMLCreator().createAndSaveXMLRepresentation(diagram.getPackagePath(), diagram);
				}
				if (getPressedKeys().contains(KeyCode.F5)) {
					diagram.getComm().triggerUpdate();
				}
			}
		});
		tabPane.getSelectionModel().selectedItemProperty().addListener((foo, goo, newTabItem) -> {
			if (newTabItem.getContent() == null) {
				// pane with star selected
				tabPane.getTabs().add(new MyTab(this));
				final DiagramCanvas newView = diagram.new DiagramCanvas("new View", false);
				((MyTab) newTabItem).setText("");
				((MyTab) newTabItem).setView(newView);
				final java.util.Timer timer = new java.util.Timer();
				timer.schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						diagram.redraw();
						if (newView.getCanvas().getWidth() > 0)
							timer.cancel();
					}
				}, 100, 100);
			} else {
				diagram.redraw();
			}
		});

		// Resize of Canvas on rescale
		tabPane.heightProperty().addListener((observable, x, y) -> diagram.redraw());
		tabPane.widthProperty().addListener((observable, x, y) -> diagram.redraw());
		return tabPane;
	}

	private DiagramCanvas buildZoomView() {
		DiagramCanvas zoomView = diagram.new DiagramCanvas("", true);
		SplitPane.setResizableWithParent(zoomView, false);
		return zoomView;
	}

	private TableView<Issue> buildIssueTable() {
		TableView<Issue> issueTable = new TableView<Issue>();
		TableColumn<Issue, FmmlxObject> objectColumn = new TableColumn<>("Object");
		TableColumn<Issue, Issue> issueColumn = new TableColumn<>("Issue");
		issueColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		objectColumn.prefWidthProperty().bind(issueTable.widthProperty().multiply(0.3));
		issueColumn.prefWidthProperty().bind(issueTable.widthProperty().multiply(0.7));
		issueTable.getColumns().add(objectColumn);
		issueTable.getColumns().add(issueColumn);
		issueTable.getSelectionModel().setCellSelectionEnabled(true);
		issueTable.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					getActiveDiagramViewPane()
							.centerObject(issueTable.getSelectionModel().getSelectedItem().getAffectedObject(diagram));
				}
			}
		});

		objectColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Issue, FmmlxObject>, ObservableValue<FmmlxObject>>() {

					@Override
					public ObservableValue<FmmlxObject> call(CellDataFeatures<Issue, FmmlxObject> f) {
						try {
							return new ReadOnlyObjectWrapper<FmmlxObject>(f.getValue().getAffectedObject(diagram));
						} catch (Exception e) {
							return null;
						}
					}
				});
		objectColumn.setCellFactory((listView) -> {
			return new TableCell<Issue, FmmlxObject>() {

				@Override
				protected void updateItem(FmmlxObject o, boolean empty) {
					super.updateItem(o, empty);
					if (o != null) {
						if (o.isAbstract())
							setText("(" + o.getName() + " ^" + o.getMetaClassName() + "^ " + ")");
						else
							setText(o.getName() + " ^" + o.getMetaClassName() + "^");

						setGraphic(ModelBrowser.getClassLevelGraphic(o.getLevel().getMinLevel()));
					} else {
						setText("");
						setGraphic(null);
					}
				}
			};
		});

		issueColumn.setCellValueFactory(new Callback<CellDataFeatures<Issue, Issue>, ObservableValue<Issue>>() {

			@Override
			public ObservableValue<Issue> call(CellDataFeatures<Issue, Issue> f) {
				try {
					return new ReadOnlyObjectWrapper<Issue>(f.getValue());
				} catch (Exception e) {
					return null;
				}
			}
		});

		issueColumn.setCellFactory((listView) -> {
			return new TableCell<Issue, Issue>() {
				@Override
				protected void updateItem(Issue issue, boolean empty) {
					super.updateItem(issue, empty);
					if (issue != null) {
						if (Issue.Severity.FATAL.equals(issue.getSeverity())) {
							setGraphic(new ImageView(new javafx.scene.image.Image(
									new File("resources/gif/Classify/error.gif").toURI().toString())));
						}
						if (Issue.Severity.NORMAL.equals(issue.getSeverity())) {
							setGraphic(new ImageView(new javafx.scene.image.Image(
									new File("resources/gif/Classify/error.gif").toURI().toString())));
						}
						if (Issue.Severity.BAD_PRACTICE.equals(issue.getSeverity())) {
							setGraphic(new ImageView(new javafx.scene.image.Image(
									new File("resources/gif/User/Warning.gif").toURI().toString())));
						}
						if (Issue.Severity.USER_DEFINED.equals(issue.getSeverity())) {
							setGraphic(new ImageView(new javafx.scene.image.Image(
									new File("resources/gif/MDC/Listener.gif").toURI().toString())));
						}
						setText(issue.getText());
					} else {
						setText("");
						setGraphic(null);
					}
				}
			};
		});
		return issueTable;
	}

	private SplitPane buildPalettSideBar() {
		SplitPane palettSideBar = new SplitPane();
		palettSideBar.setOrientation(Orientation.VERTICAL);
		palettSideBar.setDividerPosition(0, 0.8);
		SplitPane.setResizableWithParent(palettSideBar, false);
		return palettSideBar;
	}

	private void buildIssuePane() {
		setIssueTable(buildIssueTable());
		issueScrollPane = new ScrollPane(getIssueTable());
	}

	public void switchTableOnAndOffForIssues() {
		canvasContainer.getChildren().clear();
		if (diagram.getDiagramViewToolBarModel().getPropertieValue(DiagramDisplayProperty.ISSUETABLE)) {
			getIssueTable().prefHeightProperty().bind(issueScrollPane.heightProperty());
			getIssueTable().prefWidthProperty().bind(issueScrollPane.widthProperty());
			splitPane3 = new SplitPane(diagramViewPane, issueScrollPane);
			splitPane3.setOrientation(Orientation.VERTICAL);
			canvasContainer.getChildren().addAll(diagramViewToolbar, splitPane3);
		} else {
			canvasContainer.getChildren().addAll(diagramViewToolbar, diagramViewPane);
		}
		Thread t = new Thread(() -> {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			diagram.redraw();
		});
		t.start();
	}

	private void initConcreteSyntax() {
		Thread t = new Thread(() -> {
			diagram.fetchDiagramData(a -> {
			});
			updateConcreteSyntaxes();
		});
		t.start();
	}

	private void updateConcreteSyntaxes() {
		File syntaxDir = new File(ConcreteSyntaxWizard.RESOURCES_CONCRETE_SYNTAX_REPOSITORY); // TODO: recursively
																								// searching all
		if (syntaxDir.isDirectory()) {
			Vector<File> directories = new Vector<>();
			Vector<File> files = new Vector<>();
			directories.add(syntaxDir);
			while (!directories.isEmpty()) {
				File dir = directories.remove(0);
				for (File file : dir.listFiles()) {
					if (file.isDirectory())
						directories.add(file);
					else if (file.getName().endsWith(".xml"))
						files.add(file);
				}
			}
			for (File file : files) {
				try {
					ConcreteSyntaxPattern group = ConcreteSyntaxPattern.load(file);
					if (group instanceof ConcreteSyntax) {
						ConcreteSyntax c = ((ConcreteSyntax) group);
						syntaxes.put(c.classPath + "@" + c.level, c);
					}
				} catch (Exception e) {
					System.err.println("reading " + file.getName() + " failed (" + e.getMessage() + "). Ignoring...");
				}
			}
		}
	}

	public void loadNextStage() {
		DiagramPreperationActions.prepair(diagram);
		buildViewComponents(diagramViewState.getNextState());
		diagramViewState = diagramViewState.getNextState();
		if (diagramViewState.getPrecedence() == 10) {
			ToolIntroductionManager.getInstance().getDescriptionViewer().exchangeCheckButton();
		}
	}

	public DiagramCanvas getActiveDiagramViewPane() {
		return (DiagramCanvas) diagramViewPane.getSelectionModel().getSelectedItem().getContent();
	}

	public FmmlxDiagram getDiagram() {
		return diagram;
	}

	public Set<KeyCode> getPressedKeys() {
		return pressedKeys;
	}

	public DiagramViewHeadToolBar getDiagramViewToolbar() {
		return diagramViewToolbar;
	}

	public FmmlxPalette getFmmlxPalette() {
		return fmmlxPalette;
	}

	public TableView<Issue> getIssueTable() {
		return issueTable;
	}

	public void setIssueTable(TableView<Issue> issueTable) {
		this.issueTable = issueTable;
	}

	public void setDiagramViewState(DiagramViewState diagramViewState) {
		this.diagramViewState = diagramViewState;
	}

	public DiagramViewState getDiagramViewState() {
		return diagramViewState;
	}
}