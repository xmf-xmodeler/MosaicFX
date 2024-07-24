package tool.xmodeler.didactic_ml.backend_aux.learning_unit_tabs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssesmentTestManager;
import tool.xmodeler.didactic_ml.self_assesment_test_managers.SelfAssessmentTest;
import javafx.scene.control.Label;

public class CustomTabPane extends TabPane {

    

    public CustomTabPane() {
        
        
        Tab learningGoalsTab = new Tab("Learning Goals", createLearningGoalsContent());
        Tab theoreticalBackgroundTab = new Tab("Theoretical Background", createTheoreticalBackgroundContent());
        Tab illustrationTab = new Tab("Illustration", createIllustrationContent());
        Tab assessmentTab = new Tab("Assessment", createAssessmentContent());

        getTabs().addAll(learningGoalsTab, theoreticalBackgroundTab, illustrationTab, assessmentTab);
    }

    private VBox createLearningGoalsContent() {
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label("Learning Goals Content"));
        VBox.setVgrow(vbox, Priority.ALWAYS);
        return vbox;
    }

    private VBox createTheoreticalBackgroundContent() {
        VBox vbox = new VBox();
        Label heading = new Label("Heading");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea textArea = new TextArea("TheoreticalBackground Content");
        textArea.setWrapText(true);
        textArea.setEditable(false);
        vbox.getChildren().addAll(heading, textArea);
        VBox.setVgrow(textArea, Priority.ALWAYS);
        return vbox;
    }

    private VBox createIllustrationContent() {
        VBox vbox = new VBox();

        // Obere HBox für die Illustration und den Button
        HBox illustrationBox = new HBox();
        TextArea illustrationTextArea = new TextArea("Illustration Content");
        illustrationTextArea.setEditable(false);
        illustrationTextArea.setWrapText(true);
        Button startIllustrationButton = new Button("Start Illustration");

        startIllustrationButton.setOnAction(event -> {
            System.out.println("Start Illustration");
            illustrationTextArea.appendText("\nIllustration started...");
        });

        illustrationBox.getChildren().addAll(illustrationTextArea, startIllustrationButton);
        HBox.setHgrow(illustrationTextArea, Priority.ALWAYS);

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
        vbox.getChildren().add(new Label("Assessment Content"));
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
}
