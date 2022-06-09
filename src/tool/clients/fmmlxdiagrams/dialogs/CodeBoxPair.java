package tool.clients.fmmlxdiagrams.dialogs;

import java.awt.event.ActionListener;

import org.fxmisc.richtext.InlineCssTextArea;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import tool.clients.fmmlxdiagrams.AbstractPackageViewer;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.clients.fmmlxdiagrams.classbrowser.CodeBox;

public class CodeBoxPair {

	public final CodeBox bodyCodeBox;
	private final TextArea errorTextArea;
	private AbstractPackageViewer diagram;
	private ActionListener okButtonListener;
	private Boolean checkPassed = false;

	public CodeBoxPair(AbstractPackageViewer diagram, ActionListener okButtonListener) {
		errorTextArea = new TextArea();
		bodyCodeBox = new CodeBox(10,true,"");
		this.diagram = diagram;
		this.okButtonListener = okButtonListener;
		bodyCodeBox.setSyntaxCheckListener((e -> {checkPassed=false;okButtonListener.actionPerformed(null);checkBodySyntax();}));
		errorTextArea.setEditable(false);
		//errorTextArea.setDisable(true);
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

		diagram.getComm().checkSyntax(diagram, bodyCodeBox.getText(), returnCall);
	}
	
	public Boolean getCheckPassed() {
		return checkPassed;
	}
	
	public static class OperationException {
		public String message;
		public Integer lineCount;
		public Integer charCount;
	}
	
	public TextArea getErrorTextArea() {
		return errorTextArea;
	}
	
	public Region getBodyScrollPane() {
		return bodyCodeBox.getVirtualizedScrollPane();
	}
	
	public void setBodyText(String code) {
		bodyCodeBox.setText(code);
	}

	public String getBodyText() {
		return bodyCodeBox.getText();
	}

	public void setDiagram(AbstractPackageViewer activePackage) {
		this.diagram=activePackage;
	}

}
