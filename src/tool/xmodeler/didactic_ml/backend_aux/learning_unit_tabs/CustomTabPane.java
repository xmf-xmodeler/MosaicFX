package tool.xmodeler.didactic_ml.backend_aux.learning_unit_tabs;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class CustomTabPane extends TabPane {
    public CustomTabPane() {
        Tab learningGoalsTab = new Tab("LearningGoals", createLearningGoalsContent());
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
        TextArea illustrationTextArea = new TextArea("Illustration Content");
        illustrationTextArea.setEditable(false);
        illustrationTextArea.setWrapText(true);
        vbox.getChildren().add(illustrationTextArea);
        VBox.setVgrow(illustrationTextArea, Priority.ALWAYS);
        return vbox;
    }

    private VBox createAssessmentContent() {
        VBox vbox = new VBox();
        vbox.getChildren().add(new Label("Assessment Content"));
        VBox.setVgrow(vbox, Priority.ALWAYS);
        return vbox;
    }
}
