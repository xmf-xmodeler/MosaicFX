package tool.clients.fmmlxdiagrams.instancegenerator;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;
import tool.clients.fmmlxdiagrams.classbrowser.CustomStage;
import tool.clients.fmmlxdiagrams.dialogs.stringandvalue.StringValue;
import tool.xmodeler.XModeler;

public class InstanceGeneratorStage extends CustomStage {
	
	private GridPane mainGridPane;
	private SplitPane outerSplitPane;

	public InstanceGeneratorStage() {
		super(StringValue.LabelAndHeaderTitle.instanceGenerator, XModeler.getStage(), 800, 600);
		
		
		initAllElement();
		addAllElementToPane();			
		getContainer().getChildren().addAll(outerSplitPane);
		
		setOnCloseRequest(e -> onClose());
	}
	
	public void onClose() {
		hide();
	}

	@Override
	protected void initAllElement() {
		mainGridPane = new GridPane();
		mainGridPane.setHgap(10);
		mainGridPane.setVgap(8);
		mainGridPane.setPadding(new Insets(3, 3, 3, 3));
		setColumnConstrain(mainGridPane);
		
		outerSplitPane = new SplitPane();
		outerSplitPane.setOrientation(Orientation.VERTICAL);
		outerSplitPane.getItems().addAll(mainGridPane);
		
		VBox.setVgrow(outerSplitPane,Priority.ALWAYS);

	}

	@Override
	protected void addAllElementToPane() {
		// TODO Auto-generated method stub

	}

	public void updateDiagram(FmmlxDiagram diagram) {
		// TODO Auto-generated method stub
		
	}
	
	public void update(FmmlxObject object) {
		setTitle(StringValue.LabelAndHeaderTitle.instanceGenerator+" : "+object.getName());
		
	}

	private void setColumnConstrain(GridPane gridPane) {
		ColumnConstraints cc;
		for (int i = 0; i < 6; i++) {
			cc = new ColumnConstraints();
			cc.setFillWidth(true);
			cc.setHgrow(Priority.ALWAYS);
			gridPane.getColumnConstraints().add(cc);
		}
	}

}
