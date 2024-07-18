package tool.xmodeler.didactic_ml.frontend.task_description_viewer;

import java.io.File;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tool.xmodeler.didactic_ml.learning_unit_managers.LearningUnitManager;
import tool.xmodeler.didactic_ml.learning_unit_managers.ToolIntroductionManager;

public class TaskDescriptionViewer extends Stage {

	private final WebView webView = new WebView();
	private final TaskDescriptionHistory descriptionHistory = new TaskDescriptionHistory(this);
	private Button checkButton = createButton("Check Condition", (a) -> LearningUnitManager.getInstance().checkSucessCondition());
	private final Button backwardsButton = createButton("< Back", (a) -> descriptionHistory.navigateBack());
	private final Button forwardsButton = createButton("Forward >", (a) -> descriptionHistory.navigateForward());
	private final ToolBar buttonBar = new ToolBar();

	public TaskDescriptionViewer() {

		setOnCloseRequest(this::showWarningDialog);
		setTitle("Task Description");
		//Used to identify stage to open it on shortcut
		getProperties().put("stageID", "TaskViewerStage");
		
		BorderPane root = new BorderPane();
		webView.setContextMenuEnabled(false);
		root.setCenter(webView);
		root.setBottom(buttonBar);
		buttonBar.getItems().add(checkButton);
		Scene scene = new Scene(root, 800, 600);
		setScene(scene);

		setCustomCssStyleSheet();
	}

	private void setCustomCssStyleSheet() {
		String customCssPath = "resources/css/taskDescription.css";
		File customCssFile = new File(customCssPath);
		webView.getEngine().setUserStyleSheetLocation("file:" + customCssFile.getAbsolutePath());
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
			ToolIntroductionManager.getInstance().stop();
		}
	}

	/**
	 * Closes diagram stage. If ToolIntro will be aborted the Diagram should also be
	 * closed.
	 */
	private void closeToolIntoDiagramStage() {
		LearningUnitManager.getDiagram().getStage().close();
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
				buttonBar.getItems().remove(checkButton);
				checkButton = createButton("Check Condition", (a) -> ToolIntroductionManager.getInstance().checkSucessCondition());
				buttonBar.getItems().add(checkButton);
			});
		};
		Thread thread = new Thread(task);
		thread.start();
	}

	/**
	 * This function is used to change the check Button in the last stage to the
	 * finish button
	 */
	public void alterCheckButtonText() {
		Platform.runLater(() -> {
			checkButton.setText("Finish");
			checkButton.setOnAction((a) -> {
				closeToolIntoDiagramStage();
				this.close();
				ToolIntroductionManager.getInstance().stop();
			});
		});
	}

	/**
	 * Matches GUI to the state of the message history. Depending on this state
	 * buttons are shown or not. Some buttons change the label in dependence to this
	 * state. Do not alter checking order because it will change the logic.
	 */
	public void updateGui() {
		updateBackwardsButton();
		exchangeCheckButton();
	}

	/** In case the user navigated back this function will exchange the checkButton with the forward button. 
	 * 
	 */
	private void exchangeCheckButton() {
		if (descriptionHistory.isForwardNavigable() && !isForwardsButtonContained()) {
			buttonBar.getItems().remove(checkButton);
			buttonBar.getItems().add(forwardsButton);
			return;
		}
		if (!descriptionHistory.isForwardNavigable() && isForwardsButtonContained()) {
			buttonBar.getItems().remove(forwardsButton);
			buttonBar.getItems().add(checkButton);
		}	
	}

	/**
	 * Adds backwards button when there is a history. If you navigate back until there is no more element to recall the button is removed.
	 */
	private void updateBackwardsButton() {
		if (descriptionHistory.size() == 1) {
			buttonBar.getItems().remove(backwardsButton);
			return;
		}
		if (descriptionHistory.isBackwardNavigable() && !isBackwardsButtonContained()) {
			buttonBar.getItems().add(0, backwardsButton);
		}
	}

	/**
	 * @return true if backwards button is item of buttonBar
	 */
	private boolean isBackwardsButtonContained() {
		return buttonBar.getItems().contains(backwardsButton);
	}
	
	/**
	 * @return true if forward button is item of buttonBar
	 */
	private boolean isForwardsButtonContained() {
		return buttonBar.getItems().contains(forwardsButton);
	}

	private Button createButton(String text, EventHandler<ActionEvent> handler) {
		Button button = new Button(text);
		button.setOnAction(handler);
		return button;
	}

	public TaskDescriptionHistory getDescriptionHistory() {
		return descriptionHistory;
	}
}