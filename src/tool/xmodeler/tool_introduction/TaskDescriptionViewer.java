package tool.xmodeler.tool_introduction;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import tool.xmodeler.XModeler;

public class TaskDescriptionViewer extends Stage {

	private final WebView webView = new WebView();
	private Button checkButton;
	private final BorderPane root;

	public TaskDescriptionViewer() {

		setOnCloseRequest(this::showWarningDialog);

		checkButton = buildCheckButton();

		root = new BorderPane();
		root.setCenter(webView);
		root.setBottom(checkButton);

		Scene scene = new Scene(root, 800, 600);

		setTitle("Task Description");
		setScene(scene);
		webView.setContextMenuEnabled(false);
		setCustomCssStyleSheet();
	}

	private void setCustomCssStyleSheet() {
		String customCssPath = "resources/css/taskDescription.css";
	    File customCssFile = new File(customCssPath);    
        webView.getEngine().setUserStyleSheetLocation("file:"+ customCssFile.getAbsolutePath());
	}

	private Button buildCheckButton() {
		Button button = new Button("Check Condition");
		button.setOnAction((a) -> {
			ToolIntroductionManager.getInstance().checkSucessCondition();
		});
		return button;

	}

	/**
	 * If users tries to exit the Tool Intro stage he is informed, that he will end
	 * the Tool Introduction. If he confirms the Tool Intro stage is closed and the
	 * diagram stage is closed.
	 */
	public void showWarningDialog(WindowEvent event) {
		event.consume();

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm closing");
		alert.setHeaderText("You are going to end the Tool Introduction");
		alert.setContentText(
				"If you confirm the Tool Introduction is aborted and you return to the start view of the XModeler");
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			closeToolIntoDiagramStage();
			this.close();
		}
	}

	/**
	 * Closes diagram stage. If ToolIntro will be aborted the Diagram should also be
	 * closed.
	 */
	private void closeToolIntoDiagramStage() {
		for (Window window : Window.getWindows()) {
			if (window instanceof Stage) {
				Stage stage = (Stage) window;
				if ("ToolIntroductionABC::ToolIntroductionDiagramXYZ".equals(stage.getTitle())) {
					stage.close();
					return;
				}
			}
		}
	}

	public void loadHtmlContent(String content) {
		webView.getEngine().loadContent(content);
	}

	/**
	 * Changes the color of the check button. This is used to inform the user about
	 * the result of the check. By valid check the button will be green for a small
	 * time period. If the check does not validate the button is colored red.
	 * 
	 * @param checkValid boolean that represents the check result.
	 */
	public void giveUserFeedback(Boolean checkValid) {
		if (checkValid) {
			setButtonColor("green");
		} else {
			setButtonColor("red");
		}
	}

	private void setButtonColor(String buttonColor) {
		Runnable task = () -> {
			Platform.runLater(() -> {
				String existingStyle = checkButton.getStyle();
				StringBuilder inlineCss = new StringBuilder("-fx-background-color: ");
				inlineCss.append(buttonColor);
				inlineCss.append(";");
				checkButton.setStyle(existingStyle + inlineCss.toString());
			});

			try {
				Thread.sleep(1000); // wait one second so the user can recognize the result.
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			Platform.runLater(() -> {
				// Quick fix. After the coloring the button needs to be set back to default
				// style. I did not find a good solution.
				// setStyle(null) and setBackground(null) lead to bad results.
				root.getChildren().remove(checkButton);
				checkButton = buildCheckButton();
				root.setBottom(checkButton);
			});
		};
		Thread thread = new Thread(task);
		thread.start();
	}
	
	/**
	 * This function is used to change the check Button in the last stage to the finish button
	 */
	public void exchangeCheckButton() {
		Platform.runLater(() -> {
			checkButton.setText("Finish");
			checkButton.setOnAction((a) -> {
				closeToolIntoDiagramStage();
				this.close();
				ToolIntroductionManager.getInstance().stop();
			});
		});		
	}
}