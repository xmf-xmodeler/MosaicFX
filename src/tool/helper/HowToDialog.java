package tool.helper;

import java.io.File;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import tool.clients.fmmlxdiagrams.classbrowser.CodeBox;

public class HowToDialog extends Dialog<Void> {
	
	final WebView htmlViewer = new WebView();
    final WebEngine webEngine = htmlViewer.getEngine();
    final VBox topicList = new VBox();
	final CodeBox codeBox = new CodeBox(12, false, "N/A");
	
	public HowToDialog() {		
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.FINISH);
		dialogPane.setHeaderText("How to ...");		
		
		setWidth(800);
		setWidth(600);
		
		SplitPane splitPane2 = new SplitPane(htmlViewer, codeBox.getTextArea());
		splitPane2.setOrientation(Orientation.VERTICAL);
		SplitPane splitPane1 = new SplitPane(topicList, splitPane2);
		splitPane1.setOrientation(Orientation.HORIZONTAL);
		splitPane1.setDividerPosition(0, .05);
		getDialogPane().setContent(splitPane1);
		
		addTopics();

	}

	private void addTopics() {
		addTopic("Generate Random Numbers", "resources/webroot/index.html", CODE1);
		addTopic("Calculate GCD", "topic2.html", CODE2);
		addTopic("XMF -> Update Diagram", "topic3.html", CODE3);
	}
	

	private void addTopic(String buttonText, String htmlFile, String code) {
		Button b = new Button(buttonText);
		topicList.getChildren().add(b);
		b.setOnAction(e-> {
			File f = new File(htmlFile);
			try {
				webEngine.load(f.toURI().toURL().toString());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			codeBox.setText(code);
		});		
	}
	
	private static final String CODE3 = "Clients::FmmlxDiagrams::FmmlxDiagramClient.allInstances().at(0).communicator.triggerUpdate()";


	private static final String CODE2 = ""
			+ "@Operation gcd(a,b)\n"
			+ "  if a<0 or b<0 or a=null or b=null\n"
			+ "    or not a.isKindOf(Integer) or not b.isKindOf(Integer)\n"
			+ "  then throw Exception(\"invalid inputs\")\n"
			+ "  elseif b = 0 then a\n"
			+ "  else self.gcd(b, a.mod(b))\n"
			+ "  end\n"
			+ "end";
	
	private static final String CODE1 = ""
			+ "let"
			+ "  min = 20; max = 40"
			+ "in"
			+ "  Integer::random(max-min) + min"
			+ "end";

	
}
