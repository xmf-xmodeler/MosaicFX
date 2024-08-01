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
import tool.clients.fmmlxdiagrams.FmmlxDiagramCommunicator;
import tool.helper.persistence.XMLParser;
import tool.xmodeler.didactic_ml.UserDataProcessor;
import tool.xmodeler.didactic_ml.frontend.ResourceLoader;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;

public class LearningUnitTabPane extends TabPane {

	private final LearningUnitChooser learningUnitChooser;
	private WebView learningGoalView = createStadardWebView();
	private WebView theoreticalBackgroundView = createStadardWebView();
	private TableView<SelfAssessmentTest> assessmentTableView = createAssessmentTableView();

	public LearningUnitTabPane(LearningUnitChooser learningUnitChooser) {
		this.learningUnitChooser = learningUnitChooser;
		getStylesheets().add(ResourceLoader.getDidacticCssUrl().toExternalForm());
		Tab learningGoalsTab = new Tab("Learning Goals", learningGoalView);
		Tab theoreticalBackgroundTab = new Tab("Theoretical Background", createTheoreticalBackgroundPane());
		Tab assessmentTab = new Tab("Self-Assessment", createAssessmentContent());
		getTabs().addAll(learningGoalsTab, theoreticalBackgroundTab, assessmentTab);
	}

	private Node createTheoreticalBackgroundPane() {
		VBox box = new VBox();
		
		BorderPane borderPane = new BorderPane();
		Button startExampleButton = new Button();
		startExampleButton.setOnAction(this::openExampleDiagram);
		startExampleButton.setText("Show examplary Diagram");
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
	}

	private void openExampleDiagram(ActionEvent event) {
		File inputFile = ResourceLoader.getExampleDiagramFile(learningUnitChooser.getSelectedLearningUnit());
		XMLParser parser = new XMLParser(inputFile);
		parser.parseXMLDocument();
		FmmlxDiagramCommunicator.getCommunicator().openDiagram("ExampleDiagram", "example");
	}

	private TableView<SelfAssessmentTest> createAssessmentTableView() {
		TableView<SelfAssessmentTest> tableView = new TableView<>();

		TableColumn<SelfAssessmentTest, String> idColumn = new TableColumn<>("Id");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
		idColumn.setPrefWidth(20);
		idColumn.setSortable(false);

		TableColumn<SelfAssessmentTest, String> testNameColumn = new TableColumn<>("Test Name");
		testNameColumn.setCellValueFactory(new PropertyValueFactory<>("prettyName"));
		testNameColumn.setPrefWidth(400);
		testNameColumn.setSortable(false);

		TableColumn<SelfAssessmentTest, Boolean> passedColumn = new TableColumn<>("Passed");
		 passedColumn.setCellValueFactory(cellData -> {
	            SelfAssessmentTest test = cellData.getValue();
	            boolean finished = UserDataProcessor.userHasFinishedTest(test);
	            return new SimpleBooleanProperty(finished);
	        });
		passedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(passedColumn));
		passedColumn.setSortable(false);
		passedColumn.setPrefWidth(50);
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
}