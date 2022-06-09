package tool.clients.fmmlxdiagrams.dialogs;

import java.awt.event.ActionListener;
import javafx.scene.control.TextArea;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.ReturnCall;

public class CodeBox {

	public final TextArea bodyTextArea;
	public final TextArea errorTextArea;
	private AbstractPackageViewer diagram;
	private ActionListener okButtonListener;
	private Boolean checkPassed = false;

	public CodeBox(AbstractPackageViewer diagram, ActionListener okButtonListener) {
		errorTextArea = new TextArea();
		bodyTextArea = new TextArea();
		this.diagram = diagram;
		this.okButtonListener = okButtonListener;
		bodyTextArea.textProperty().addListener((a,b,c) -> {checkPassed=false;okButtonListener.actionPerformed(null);checkBodySyntax();});
		errorTextArea.setEditable(false);
	}

	void checkBodySyntax() {
		ReturnCall<OperationException> returnCall = opException -> {
			if (opException == null) {
				checkPassed = true;
				errorTextArea.setText("This operation compiles without parse/syntax error!");
				errorTextArea.setStyle("-fx-text-fill: darkgreen;" + "-fx-font-weight: bold;");
			} else {
				errorTextArea.setText(opException.message);
				errorTextArea.setStyle("-fx-text-fill: darkred;" + "-fx-font-weight: bold;");
				errorTextArea.setWrapText(true);
			}
			okButtonListener.actionPerformed(null);
		};

		diagram.getComm().checkSyntax(diagram, bodyTextArea.getText(), returnCall);
	}
	
	public Boolean getCheckPassed() {
		return checkPassed;
	}
	
	public static class OperationException {
		public String message;
		public Integer lineCount;
		public Integer charCount;
	}
}
