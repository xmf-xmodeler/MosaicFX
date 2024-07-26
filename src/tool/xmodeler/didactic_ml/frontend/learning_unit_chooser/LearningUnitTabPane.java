package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import tool.xmodeler.didactic_ml.frontend.ResourceLoader;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;

public class LearningUnitTabPane extends TabPane {

	private final LearningUnitChooser learningUnitChooser;
	private WebView learningGoalView = createStadardWebView();
	private WebView theoreticalBackgroundView = createStadardWebView();
	private TableView<SelfAssessmentTest> assessmentTableView = createAssessmentTableView();
	
	private Button okButton;

	public LearningUnitTabPane(LearningUnitChooser learningUnitChooser) {
		this.learningUnitChooser = learningUnitChooser;
		Tab learningGoalsTab = new Tab("Learning Goals", learningGoalView);
		Tab theoreticalBackgroundTab = new Tab("Theoretical Background", theoreticalBackgroundView);
		Tab assessmentTab = new Tab("Self-Assessment", createAssessmentContent());
		getTabs().addAll(learningGoalsTab, theoreticalBackgroundTab, assessmentTab);
	}

//	private VBox createIllustrationContent() {
//		VBox vbox = new VBox();
//
//		// Obere HBox für die Illustration und den Button
//		HBox illustrationBox = new HBox();
//		illustrationWebView = new WebView();
//		illustrationWebView.getEngine().loadContent("<h1>Illustration Content</h1>");
//		Button startIllustrationButton = new Button("Start Illustration");
//
//		startIllustrationButton.setOnAction(event -> {
//			System.out.println("Start Illustration");
//			illustrationWebView.getEngine().loadContent("<h1>Illustration Content</h1><p>Illustration started...</p>");
//		});
//
//		illustrationBox.getChildren().addAll(illustrationWebView, startIllustrationButton);
//		HBox.setHgrow(illustrationWebView, Priority.ALWAYS);
//
//		// Untere HBox für die Self-Assessment-Tests
//		HBox assessmentBox = new HBox();
//		TableView<SelfAssessmentTest> assessmentTableView = createAssessmentTableView();
//		assessmentBox.getChildren().add(assessmentTableView);
//		HBox.setHgrow(assessmentTableView, Priority.ALWAYS);
//
//		// Füge die beiden Boxen zum VBox hinzu
//		vbox.getChildren().addAll(illustrationBox, new Separator(), assessmentBox);
//		VBox.setVgrow(illustrationBox, Priority.ALWAYS);
//		VBox.setVgrow(assessmentBox, Priority.ALWAYS);
//		return vbox;
//	}

	private VBox createAssessmentContent() {
		VBox vbox = new VBox();
		Button startExampleButton = new Button();
		startExampleButton.setText("Show examplary Diagram");
		startExampleButton.setStyle("-fx-background-color: #ffa500;" + "-fx-border-color: #000000;"
				+ "-fx-border-width: 0.75px;" + "-fx-background-radius: 15px; " + "-fx-border-radius: 15px;");
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().addAll(startExampleButton, new Separator(), assessmentTableView);
		return vbox;
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
			// TODO implement finish logic
			// return new SimpleBooleanProperty(cellData.getValue().getId() == 0);
			return new SimpleBooleanProperty(false);
		});
		passedColumn.setPrefWidth(50);
		passedColumn.setSortable(false);
		tableView.getColumns().addAll(idColumn, testNameColumn, passedColumn);
		return tableView;
	}

	private ObservableList<SelfAssessmentTest> getAssessemntTests(LearningUnit lu) {
		ObservableList<SelfAssessmentTest> items = FXCollections.observableArrayList();
		items.addAll(SelfAssessmentTest.getTestsForLearningUnit(lu));
		return items;
	}

	public void disableOkButton(BooleanBinding disableBinding) {
		okButton.disableProperty().bind(disableBinding);
	}

	private WebView createStadardWebView() {
		WebView webView = new WebView();
		webView.getEngine().setUserStyleSheetLocation("file:" + ResourceLoader.getDidacticCssPath());
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