package tool.xmodeler.didactic_ml.frontend.learning_unit_chooser;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.web.WebView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;

public class LearningUnitTabPane extends TabPane {

    private WebView illustrationWebView;
    private Button okButton;
    private Button cancelButton;

    public LearningUnitTabPane() {
        
        // Tabs erstellen und hinzufügen
        Tab learningGoalsTab = new Tab("Learning Goals", createLearningGoalsContent());
        Tab theoreticalBackgroundTab = new Tab("Theoretical Background", createTheoreticalBackgroundContent());
        Tab illustrationTab = new Tab("Illustration", createIllustrationContent());
        Tab assessmentTab = new Tab("Assessment", createAssessmentContent());

        getTabs().addAll(learningGoalsTab, theoreticalBackgroundTab, illustrationTab, assessmentTab);
    }
      

    private VBox createLearningGoalsContent() {
        VBox vbox = new VBox();
        WebView webView = new WebView();
        webView.getEngine().loadContent("<h1>Learning Goals Content</h1>");
        vbox.getChildren().add(webView);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        return vbox;
    }

    private VBox createTheoreticalBackgroundContent() {
        VBox vbox = new VBox();
        WebView webView = new WebView();
        webView.getEngine().loadContent("<h1>Heading</h1><p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.</p>");
        vbox.getChildren().add(webView);
        VBox.setVgrow(webView, Priority.ALWAYS);
        return vbox;
    }

    private VBox createIllustrationContent() {
        VBox vbox = new VBox();

        // Obere HBox für die Illustration und den Button
        HBox illustrationBox = new HBox();
        illustrationWebView = new WebView();
        illustrationWebView.getEngine().loadContent("<h1>Illustration Content</h1>");
        Button startIllustrationButton = new Button("Start Illustration");

        startIllustrationButton.setOnAction(event -> {
            System.out.println("Start Illustration");
            illustrationWebView.getEngine().loadContent("<h1>Illustration Content</h1><p>Illustration started...</p>");
        });

        illustrationBox.getChildren().addAll(illustrationWebView, startIllustrationButton);
        HBox.setHgrow(illustrationWebView, Priority.ALWAYS);

        // Untere HBox für die Self-Assessment-Tests
        HBox assessmentBox = new HBox();
        TableView<SelfAssessmentTest> assessmentTableView = createAssessmentTableView();
        assessmentBox.getChildren().add(assessmentTableView);
        HBox.setHgrow(assessmentTableView, Priority.ALWAYS);

        // Füge die beiden Boxen zum VBox hinzu
        vbox.getChildren().addAll(illustrationBox, new Separator(), assessmentBox);
        VBox.setVgrow(illustrationBox, Priority.ALWAYS);
        VBox.setVgrow(assessmentBox, Priority.ALWAYS);
        return vbox;
    }

    private VBox createAssessmentContent() {
        VBox vbox = new VBox();
        WebView webView = new WebView();
        webView.getEngine().loadContent("<h1>Assessment Content</h1>");
        vbox.getChildren().add(webView);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        return vbox;
    }

    private TableView<SelfAssessmentTest> createAssessmentTableView() {
        TableView<SelfAssessmentTest> tableView = new TableView<>();

        TableColumn<SelfAssessmentTest, String> testNameColumn = new TableColumn<>("Test Name");
        testNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SelfAssessmentTest, Boolean> passedColumn = new TableColumn<>("Passed");
        passedColumn.setCellValueFactory(new PropertyValueFactory<>("passed"));

        tableView.getColumns().addAll(testNameColumn, passedColumn);
        return tableView;
    }

    public void disableOkButton(BooleanBinding disableBinding) {
        okButton.disableProperty().bind(disableBinding);
    }
}
