package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import java.io.File;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator.NoDiagramFound;
import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.helper.persistence.XMLParser;
import tool.xmodeler.ControlCenterClient;
import tool.xmodeler.didactic_ml.UserDataProcessor;
import tool.xmodeler.didactic_ml.frontend.ResourceLoader;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;

public class LearningUnitTabPane extends TabPane {

	private final LearningUnitChooser learningUnitChooser;
	private WebView learningGoalView = createStadardWebView();
	private WebView theoreticalBackgroundView = createStadardWebView();
	private TableView<SelfAssessmentTest> assessmentTableView = createAssessmentTableView();
	private Tab theoreticalBackgroundTab = new Tab("Theoretical Background", createTheoreticalBackgroundPane());

	public LearningUnitTabPane(LearningUnitChooser learningUnitChooser) {
		this.learningUnitChooser = learningUnitChooser;
		getStylesheets().add(ResourceLoader.getDidacticCssUrl().toExternalForm());
		Tab learningGoalsTab = new Tab("Learning Objectives", learningGoalView);
		Tab assessmentTab = new Tab("Self-Assessment", createAssessmentContent());
		getTabs().addAll(learningGoalsTab, theoreticalBackgroundTab, assessmentTab);
	}

	private Node createTheoreticalBackgroundPane() {
		VBox box = new VBox();
		
		BorderPane borderPane = new BorderPane();
		Button startExampleButton = new Button();
		startExampleButton.setOnAction(this::openExampleDiagram);
		startExampleButton.setText("Open Example Model");
		startExampleButton.getStyleClass().add("didactic-button");
		borderPane.setCenter(startExampleButton);
		borderPane.setPrefHeight(150);
		
		box.getChildren().addAll(theoreticalBackgroundView, new Separator(), borderPane); 
		return box;
	}

	private VBox createAssessmentContent() {
		VBox vbox = new VBox();
		BorderPane buttonPane = new BorderPane();
		Button startSelfAssessmentButton = new Button();
		startSelfAssessmentButton.setText("Start Self-Assessment Test");
		startSelfAssessmentButton.disableProperty()
				.bind(createDisableBinding(assessmentTableView.getSelectionModel().selectedItemProperty()));
		startSelfAssessmentButton.getStyleClass().add("didactic-button");
		startSelfAssessmentButton.setOnAction(this::startAssessment);
		buttonPane.setCenter(startSelfAssessmentButton);
		buttonPane.setMinHeight(50);

		assessmentTableView.setMaxHeight(200);
		vbox.getChildren().addAll(assessmentTableView, buttonPane);
		return vbox;
	}

	/**
	 * Helper function. Needed to provide matching binding property
	 * 
	 * @param selectedItemProperty that needs to be checked
	 * @return the boolean if button should be enabled
	 */
	private BooleanBinding createDisableBinding(ReadOnlyObjectProperty<SelfAssessmentTest> selectedItemProperty) {
		return selectedItemProperty.isNull();
	}

	private void startAssessment(ActionEvent event) {
		SelfAssessmentTest test = assessmentTableView.getSelectionModel().getSelectedItem();
		LearningUnitManagerFactory.createLearningUnitManager(test).start();
		learningUnitChooser.close();
	}

	private void openExampleDiagram(ActionEvent event) {
		loadModel();
		String[] diagramDef = { "ExampleDiagram", "example" }; //make sure that the exported diagrams have this naming convention
		FmmlxDiagramCommunicator.getCommunicator().openDiagram(diagramDef[0], diagramDef[1]);
		learningUnitChooser.close();
		ControlCenterClient.getClient().getControlCenter().close();
		setOnCloseOfExampleDiagram(diagramDef);
	}

	//TODO: does only work once due to backend problems. Deleted diagram is referenced so the listener is attached to the wrong FmmlxDiagramInstance.
	private void setOnCloseOfExampleDiagram(String[] diagramDef) {
		Runnable myRunnable = new Runnable() { // needs to be runable becasue if it runs in the same thread it will not find the opened diagram
			@Override
			public void run() {
				try {
					Thread.sleep(1000); // wait to avoid to get a null response to the search function because diagram is not opened already
				} catch (InterruptedException e) {
					throw new RuntimeException();
				}
				FmmlxDiagram diagram = null;
				try {
					diagram = FmmlxDiagramCommunicator.getCommunicator().searchDiagram(diagramDef[0], diagramDef[1], 0);
					Stage s = diagram.getStage();
					s.setOnCloseRequest((e) -> {
						LearningUnitChooser luChooser = new LearningUnitChooser();
						luChooser.getLearningUnitTable().getSelectionModel().select(learningUnitChooser.getLearningUnitTable().getSelectionModel().getSelectedItem());
						luChooser.getLearningUnitTabPane().setTheoreticalTabFocused();
						luChooser.show();
						ControlCenterClient.getClient().removeProject(diagramDef[0]);
					});
				} catch (NoDiagramFound e) {
					System.err.println("Caught NoDiagramFound: " + e.getMessage());
				}
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
	}

	/**
	 * loads example diagram to backend
	 */
	private void loadModel() {
		File inputFile = ResourceLoader.getExampleDiagramFile(learningUnitChooser.getSelectedLearningUnit());
		XMLParser parser = new XMLParser(inputFile);
		parser.parseXMLDocument();
		try {
			Thread.sleep(1000); //wait to avoid to open the diagram before it is loaded to the backend
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}
	}

	private TableView<SelfAssessmentTest> createAssessmentTableView() {
		TableView<SelfAssessmentTest> tableView = new TableView<>();

		TableColumn<SelfAssessmentTest, String> idColumn = new TableColumn<>("Id");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
		idColumn.setPrefWidth(20);
		idColumn.setSortable(false);
		idColumn.setResizable(false);
//		idColumn.impl_setReorderable(false);

		TableColumn<SelfAssessmentTest, String> testNameColumn = new TableColumn<>("Test Name");
		testNameColumn.setCellValueFactory(new PropertyValueFactory<>("prettyName"));
		testNameColumn.setPrefWidth(500);
		testNameColumn.setMinWidth(300);
		testNameColumn.setSortable(false);
		testNameColumn.setResizable(true);
//		testNameColumn.impl_setReorderable(false);

		TableColumn<SelfAssessmentTest, Boolean> passedColumn = new TableColumn<>("Passed");
		 passedColumn.setCellValueFactory(cellData -> {
	            SelfAssessmentTest test = cellData.getValue();
	            boolean finished = UserDataProcessor.userHasFinishedTest(test);
	            return new SimpleBooleanProperty(finished);
	        });
		passedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(passedColumn));
		passedColumn.setSortable(false);
		passedColumn.setPrefWidth(50);
		passedColumn.setResizable(false);
//		passedColumn.impl_setReorderable(false);
		
		
		tableView.getColumns().addAll(idColumn, testNameColumn, passedColumn);
		return tableView;
	}

	private ObservableList<SelfAssessmentTest> getAssessemntTests(LearningUnit lu) {
		ObservableList<SelfAssessmentTest> items = FXCollections.observableArrayList();
		items.addAll(SelfAssessmentTest.getTestsForLearningUnit(lu));
		return items;
	}

	private WebView createStadardWebView() {
		WebView webView = new WebView();
		webView.getEngine().setUserStyleSheetLocation("file:" + ResourceLoader.getAbsolutDidacticCssPath());
		webView.setContextMenuEnabled(false);
		return webView;
	}

	public void updateView() {
		LearningUnit lu = learningUnitChooser.getSelectedLearningUnit();
		setLearningGoals(lu);
		setTheoreticalBackground(lu);
		populateAssessmentTable(lu);
	}

	private void populateAssessmentTable(LearningUnit lu) {
		assessmentTableView.setItems(getAssessemntTests(lu));
	}

	private void setTheoreticalBackground(LearningUnit lu) {
		String content = ResourceLoader.getTheoreticalBackground(lu);
		theoreticalBackgroundView.getEngine().loadContent(content);
	}

	private void setLearningGoals(LearningUnit lu) {
		String content = ResourceLoader.getLearningGoals(lu);
		learningGoalView.getEngine().loadContent(content);
	}
	
	private void setTheoreticalTabFocused() {
		getSelectionModel().select(theoreticalBackgroundTab);	
	}
}