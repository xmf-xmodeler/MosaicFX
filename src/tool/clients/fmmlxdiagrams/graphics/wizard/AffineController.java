package tool.clients.fmmlxdiagrams.graphics.wizard;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.Vector;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;

public class AffineController {
	
	private boolean editable = true;
	private Action action = null;
	private Affine affine;
	public Affine getAffine() {return affine.clone();}
	public void setAffine(Affine affine) {this.affine = affine.clone(); updateGUI();}
	
	private GridPane matrixPane = new GridPane();
	public GridPane getMatrixPane() {return matrixPane;}
	private Vector<TextField> textfields = new Vector<>();
	private Vector<Button> buttons = new Vector<>();
	
	private VBox editPane = new VBox();
	public VBox getEditPane() {return editPane;}
	
	private static final DecimalFormat df = new DecimalFormat("0.00");

	private static final int XX = 0;
	private static final int XY = 1;
	private static final int TX = 2;
	private static final int YX = 3;
	private static final int YY = 4;
	private static final int TY = 5;	

	public AffineController() {this(new Affine());}
	
	public AffineController(Affine affine) {
		setupGUI();
		this.affine = affine.clone();
		updateGUI();
	}
	
	private void setupGUI() {
		for(int i = 0; i < 6; i++) {
			TextField f = new TextField();
			f.setEditable(false);
			f.setMaxWidth(65);
			matrixPane.add(f, i%3, i/3);
			textfields.add(f);
		}
		matrixPane.setHgap(5.);
		matrixPane.setVgap(5.);
//		matrixPane.setPadding(new Insets(5.));
		
		Button moveHButton = new Button("Move Horizontally"); buttons.add(moveHButton);
		Button moveVButton = new Button("Move Vertically");   buttons.add(moveVButton);
		Button scaleButton = new Button("Scale");             buttons.add(scaleButton);
		Button rotateButton = new Button("Rotate Clockwise"); buttons.add(rotateButton);
		
		moveHButton.setOnAction(ae -> {
			TextInputDialog dialog = new TextInputDialog("0.00");
			dialog.setTitle("Move Horizontally (right)");
			dialog.setContentText("move by:");
			
			Optional<String> result = dialog.showAndWait();

			if(result.isPresent()) {
				try{
					Double d = Double.parseDouble(result.get());
					affine.prependTranslation(d, 0.);
					updateGUI();
					action();
				} catch (Exception ex) {}
			}
		});		
		
		moveVButton.setOnAction(ae -> {
			TextInputDialog dialog = new TextInputDialog("0.00");
			dialog.setTitle("Move Vertically (down)");
			dialog.setContentText("move by:");
			
			Optional<String> result = dialog.showAndWait();

			if(result.isPresent()) {
				try{
					Double d = Double.parseDouble(result.get());
					affine.prependTranslation(0., d);
					updateGUI();
					action();
				} catch (Exception ex) {}
			}
		});	
		
		scaleButton.setOnAction(ae -> {
			TextInputDialog dialog = new TextInputDialog("1.00");
			dialog.setTitle("Scale both directions");
			dialog.setContentText("scale by:");
			
			Optional<String> result = dialog.showAndWait();

			if(result.isPresent()) {
				try{
					Double d = Double.parseDouble(result.get());
					affine.appendScale(d,d);
					updateGUI();
					action();
				} catch (Exception ex) {}
			}
		});	
		
		rotateButton.setOnAction(ae -> {
			TextInputDialog dialog = new TextInputDialog("1.00");
			dialog.setTitle("Rotate clockwise in degrees");
			dialog.setContentText("rotate by:");
			
			Optional<String> result = dialog.showAndWait();

			if(result.isPresent()) {
				try{
					Double d = Double.parseDouble(result.get());
					affine.appendRotation(d);
					updateGUI();
					action();
				} catch (Exception ex) {}
			}
		});		
		
		editPane.getChildren().addAll(buttons);		
		editPane.setSpacing(5.);
//		editPane.setPadding(new Insets(5.));
		
		for(Button b : buttons) {
			b.setMaxWidth(Double.POSITIVE_INFINITY);
		}

		for(int i = 0; i < 6; i++) {
			final int I = i;
			TextField f = textfields.get(i);
			f.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
				if(editable && e.getClickCount() == 2 && e.getButton() == MouseButton.PRIMARY) {
					TextInputDialog dialog = new TextInputDialog(""+affine.getElement(MatrixType.MT_2D_2x3, I/3, I%3));
					dialog.setTitle("Edit Value Manually");
					dialog.setContentText("new Value:");
					
					Optional<String> result = dialog.showAndWait();
	
					if(result.isPresent()) {
						try{
							Double d = Double.parseDouble(result.get());
							affine.setElement(MatrixType.MT_2D_2x3, I/3, I%3, d);
							updateGUI();
							action();
						} catch (Exception ex) {}
					}
				}
			});
		}
	}
	
	private void action() {
		if(action != null) action.run();		
	}
	private void updateGUI() {
		textfields.get(XX).setText(df.format(affine.getMxx()));
		textfields.get(XY).setText(df.format(affine.getMxy()));
		textfields.get(YX).setText(df.format(affine.getMyx()));
		textfields.get(YY).setText(df.format(affine.getMyy()));
		textfields.get(TY).setText(df.format(affine.getTy()));
		textfields.get(TX).setText(df.format(affine.getTx()));		
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
		for(Button b : buttons) b.setDisable(!editable);		
	}
	
	public static interface Action { public void run(); }
	
	public void setListener(Action action) {
		this.action = action;
	}
}
