package tool.clients.fmmlxdiagrams.dialogs;

import javax.swing.SpinnerNumberModel;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.AssociationType;

public class AssociationTypeDialog extends Dialog<AssociationType>{

	private TextField nameField = new TextField();
	private ColorPicker colorField = new ColorPicker();
	private Spinner<Integer> widthField; 
	private TextField dashField = new TextField();
	private ColorPicker colorFieldLink = new ColorPicker();
	private Spinner<Integer> widthFieldLink;
	private TextField dashFieldLink = new TextField();
	private TextField sClassField = new TextField();
	private TextField tClassField = new TextField();
	private TextField sMultField = new TextField();
	private TextField tMultField = new TextField();
	private LevelBox sLevelField = new LevelBox();
	private LevelBox tLevelField = new LevelBox();
	private TextField sDecoField = new TextField();
	private TextField tDecoField = new TextField();
	private TextField sDecoFieldLink = new TextField();
	private TextField tDecoFieldLink = new TextField();
	
	public AssociationTypeDialog(AssociationType oldType) {
		super();
		setTitle("Add Association Type");
		DialogPane dialogPane = getDialogPane();
		dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		GridPane gridPane = new GridPane();
		dialogPane.setContent(gridPane);
		gridPane.setHgap(3);
		gridPane.setVgap(3);
		gridPane.setPadding(new Insets(3, 3, 3, 3));
		
		if(oldType == null) {
			nameField.setText("MyNewAssociationType");
			sClassField.setText("Object");
			tClassField.setText("Object");
			sMultField.setText("0,null,0,null");
			tMultField.setText("0,null,0,null");
			colorField.setValue(Color.BLACK);
			colorFieldLink.setValue(Color.GRAY);
			sDecoField.setText("arrow");
			tDecoField.setText("arrow");
			sDecoFieldLink.setText("arrow");
			tDecoFieldLink.setText("arrow");
			dashFieldLink.setText("10,5");
			widthField = new Spinner<Integer>(1,Integer.MAX_VALUE,1);
			widthFieldLink = new Spinner<Integer>(1,Integer.MAX_VALUE,1);
		} else {
			nameField.setText(oldType.displayName);
			sClassField.setText(oldType.sourcePath);
			tClassField.setText(oldType.targetPath);
			sMultField.setText(oldType.sourceMult);
			tMultField.setText(oldType.targetMult);
			colorField.setValue(Color.web(oldType.color));
			colorFieldLink.setValue(Color.web(oldType.colorLink));
			sDecoField.setText(oldType.startDeco);
			tDecoField.setText(oldType.endDeco);
			dashField.setText(oldType.dashArray);
			dashFieldLink.setText(oldType.dashArrayLink);
			widthField = new Spinner<Integer>(1,Integer.MAX_VALUE,oldType.strokeWidth);
			widthFieldLink = new Spinner<Integer>(1,Integer.MAX_VALUE,oldType.strokeWidthLink);
		}
		
		int i = -1;	
		
		if(oldType != null && oldType._error_Mgs_ != null) {
			Label l = new Label(oldType._error_Mgs_);
			l.setTextFill(Color.RED);
			gridPane.add(l, 0, ++i, 3, 1);
		}
		
		gridPane.add(new Label("Association Type Name"), 0, ++i);
		gridPane.add(nameField, 1, i);
		
		gridPane.add(new Label("Source Class Path"), 0, ++i);
		gridPane.add(sClassField, 1, i);
		
		gridPane.add(new Label("Source Class Level"), 0, ++i);
		gridPane.add(sLevelField, 1, i);

		gridPane.add(new Label("Source Class Mult"), 0, ++i);
		gridPane.add(sMultField, 1, i);
		gridPane.add(new Label("minLow,maxLow,minHigh,maxHigh use null for non-limited"), 2, i);
		
		gridPane.add(new Label("Target Class Path"), 0, ++i);
		gridPane.add(tClassField, 1, i);
		
		gridPane.add(new Label("Target Class Level"), 0, ++i);
		gridPane.add(tLevelField, 1, i);
		
		gridPane.add(new Label("Target Class Mult"), 0, ++i);
		gridPane.add(tMultField, 1, i);
		
		gridPane.add(new Label("Association Color"), 0, ++i);
		gridPane.add(colorField, 1, i);

		gridPane.add(new Label("Association Stroke Width"), 0, ++i);
		gridPane.add(widthField, 1, i);

		gridPane.add(new Label("Association Dash Array"), 0, ++i);
		gridPane.add(dashField, 1, i);
		gridPane.add(new Label("an even number of comma separated integers, leave empty for solid"), 2, i);

		gridPane.add(new Label("Link Color"), 0, ++i);
		gridPane.add(colorFieldLink, 1, i);

		gridPane.add(new Label("Link Stroke Width"), 0, ++i);
		gridPane.add(widthFieldLink, 1, i);

		gridPane.add(new Label("Link Dash Array"), 0, ++i);
		gridPane.add(dashFieldLink, 1, i);

		gridPane.add(new Label("Navigable Source End Decoration (Association)"), 0, ++i);
		gridPane.add(sDecoField, 1, i);
		gridPane.add(new Label("one of none,arrow,circle,filledCircle,diamond,filledDiamond"), 2, i);
		
		gridPane.add(new Label("Navigable Target End Decoration (Association)"), 0, ++i);
		gridPane.add(tDecoField, 1, i);
		
		gridPane.add(new Label("Navigable Source End Decoration (Link)"), 0, ++i);
		gridPane.add(sDecoFieldLink, 1, i);

		gridPane.add(new Label("Navigable Target End Decoration (Link)"), 0, ++i);
		gridPane.add(tDecoFieldLink, 1, i);
		
		setResultConverter(dlgBtn -> {
			if (dlgBtn != null && dlgBtn.getButtonData() == ButtonData.OK_DONE) {
				return new AssociationType(
					nameField.getText(),
					"void",
					String.format( "%02X%02X%02X",
			            (int)( colorField.getValue().getRed() * 255 ),
			            (int)( colorField.getValue().getGreen() * 255 ),
			            (int)( colorField.getValue().getBlue() * 255 ) ),
					widthField.getValue(),
					dashField.getText(),
					sDecoField.getText(),
					tDecoField.getText(),
					String.format( "%02X%02X%02X",
			            (int)( colorFieldLink.getValue().getRed() * 255 ),
			            (int)( colorFieldLink.getValue().getGreen() * 255 ),
			            (int)( colorFieldLink.getValue().getBlue() * 255 ) ),
					widthFieldLink.getValue(),
					dashFieldLink.getText(),
					sDecoFieldLink.getText(),
					tDecoFieldLink.getText(),
					sClassField.getText(),
					tClassField.getText(),
					"",
					"",
					sMultField.getText(),
					tMultField.getText());						
			}
			return null;
		});
	}
}
