package tool.clients.fmmlxdiagrams;


import java.util.List;

import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import tool.clients.fmmlxdiagrams.dialogs.RenameProjektDialog;
import java.util.Iterator;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;

import tool.clients.fmmlxdiagrams.fmmlxdiagram.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.xmldatabase.BranchManager;
import tool.clients.fmmlxdiagrams.xmldatabase.DefaultBranchManager;
import tool.clients.fmmlxdiagrams.xmldatabase.XMLDatabase;
import tool.helper.persistence.XMLCreator;
import tool.xmodeler.didactic_ml.frontend.task_description_viewer.TaskDescriptionViewer;

/**
 * This class is used to handle all KeyInputs form the FmmlxDiagram that are
 * combined with the control-key. Please do not forget to add the shortcuts you
 * will add here to the ShortcutDialog-class.
 */
public class FmmlxDiagramControlKeyHandler {

    private final FmmlxDiagram diagram;

    public FmmlxDiagramControlKeyHandler(FmmlxDiagram fmmlxDiagram) {
        this.diagram = fmmlxDiagram;
    }

    /**
     * Handles the KeyEvent and checks for combinations with Control and Shift keys.
     *
     * @param event The KeyEvent to process.
     */
    public void handle(KeyEvent event) {
        // Check if the Control key is pressed
        if (!event.isControlDown()) {
            return; // Exit if Ctrl is not pressed
        }

        // Get the pressed KeyCode
        KeyCode code = event.getCode();

        // Check for Ctrl + Shift + S first
        if (code == KeyCode.S && event.isShiftDown()) {
            handleCtrlShiftS(); // Handle Ctrl + Shift + S
            return; // Stop further processing
        }
        else
        {
        	
        

        // Handle other Ctrl + Key combinations
        switch (code) {
            case M:
                handleM();
                break;

            case R:
                handleR();
                break;

            case F:
                handleF();
                break;

            case A:
                handleA();
                break;

            case S:
                handleS(); // Handle Ctrl + S
                break;
            
            case T:
			        bringTaskViewerUpfront();
			       break;

            default:
                break;
        }
        }
    }


    /**
     * @author Nicolas Engel
     * Handles the "Ctrl + S" key combination.
     */
    private void handleS() {
        System.out.println("Ctrl + S pressed! Saving diagram...");
        new XMLCreator().createAndSaveXMLRepresentation(diagram.getPackagePath(), diagram);
    }
  
  	private void bringTaskViewerUpfront() {
		if (!diagram.isInLearningUnitMode()) {
			return;
		}
		try {
			findTaskDescriptionStage().toFront();			
		} catch (NullPointerException e) {
			 System.err.println("Cant find TaskViewStage: " + e.getMessage());
		}
	}
	
	private Stage findTaskDescriptionStage() {
		return TaskDescriptionViewer.mostRecentWindow;
//		Iterator<Window> i = Window.impl_getWindows();
//		
//        while(i.hasNext()) {
//        	Window window = i.next();
//            if (window instanceof Stage) {
//                Stage stage = (Stage) window;
//                Object stageID = stage.getProperties().get("stageID");
//                if ("TaskViewerStage".equals(stageID)) {
//                    return stage;
//                }
//            }
//        }
//        return null;
    }

    /**
     * Handles the "Ctrl + Shift + S" key combination.
     */
    private void handleCtrlShiftS() {
    	XMLDatabase db = new XMLDatabase();
    	try {
			List<String> documentNames =  db.getProjectDocumentNames();
			String diagramName = diagram.getPackagePath().substring(6) + "_versions.xml";
			diagramName.trim();
			if (diagramName != null && documentNames.contains(diagramName))
			{
				BranchManager manager = new BranchManager();
				if (DefaultBranchManager.getInstance().getDefaultBranch() == "main")
				{
					manager.writeToDB(diagram);
					return;
				}
				
				List<String> branches = manager.getAllBranches(diagramName);
				branches.replaceAll(String::trim);
				if(branches.contains(DefaultBranchManager.getInstance().getDefaultBranch()))
				{
					manager.addDiagramToBranch(diagram, diagramName, DefaultBranchManager.getInstance().getDefaultBranch());
				}
				else {
					RenameProjektDialog rpd = new RenameProjektDialog();
					Platform.runLater(() -> {
					rpd.start(diagram,db,documentNames);
					});
				}
				
				
				
			}
			else
			{
				RenameProjektDialog rpd = new RenameProjektDialog();
				Platform.runLater(() -> {
				rpd.start(diagram,db,documentNames);
				});
			}
		        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        
    }

    /**
     * Handles the "Ctrl + A" key combination.
     */
    private void handleA() {
        diagram.selectAll();
    }

    /**
     * Handles the "Ctrl + F" key combination.
     */
    private void handleF() {
        diagram.actions.centerViewOnObject();
    }

    /**
     * Handles the "Ctrl + R" key combination.
     */
    private void handleR() {
        diagram.getActiveDiagramViewPane().canvasTransform.prependRotation(10,
                new Point2D(diagram.getActiveDiagramViewPane().canvas.getWidth() / 2,
                        diagram.getActiveDiagramViewPane().canvas.getHeight() / 2));
        diagram.redraw();
    }

    /**
     * Handles the "Ctrl + M" key combination.
     */
    private void handleM() {
        diagram.getActiveDiagramViewPane().canvasTransform.prependScale(-1, 1,
                new Point2D(diagram.getActiveDiagramViewPane().canvas.getWidth() / 2,
                        diagram.getActiveDiagramViewPane().canvas.getHeight() / 2));
        diagram.redraw();
    }
}
