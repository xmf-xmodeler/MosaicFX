package tool.clients.fmmlxdiagrams.menus;


import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.DiagramDisplayModel;
import tool.clients.fmmlxdiagrams.DiagramDisplayProperty;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.ReturnCall;
import tool.clients.fmmlxdiagrams.Note;
import tool.clients.fmmlxdiagrams.dialogs.ShortcutDialog;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;
import tool.communication.java_to_python.MissingPythonRespondException;
import tool.communication.java_to_python.PythonFunction;
import tool.communication.java_to_python.PythonRequestWrapper;
import tool.helper.auxilaryFX.JavaFxButtonAuxilary;
import tool.helper.auxilaryFX.JavaFxMenuAuxiliary;
import tool.helper.auxilaryFX.JavaFxTooltipAuxilary;
import tool.helper.persistence.XMLCreator;
import tool.helper.persistence.XMLUtil;
import tool.xmodeler.ControlCenterClient;

public class DiagramViewHeadToolBar extends VBox {
	
	private DiagramDisplayModel diagramDisplayModel;
	private FmmlxDiagram fmmlxDiagram;
	private DiagramActions diagramActions;
	private Button updateButton;
	private Node updateSvg;
	
	// changes between a dev and a productive mode regarding the items in the menu
	private static final boolean DEBUG = false;
		
	public DiagramViewHeadToolBar(FmmlxDiagram fmmlxDiagram) {
		this.fmmlxDiagram = fmmlxDiagram;
		diagramActions = fmmlxDiagram.getActions();
		diagramDisplayModel = new DiagramDisplayModel(this);
				
		HBox hBox = new HBox();
		MenuBar menuBar = new MenuBar();
		menuBar.setStyle("-fx-background-color: #F3F3F3");
		hBox.getChildren().add(menuBar);
				
		Menu modelMenu = new Menu("Model");		
		Menu viewMenu = new Menu("View");
		viewMenu.setOnShowing(e -> renderHideNotesItem(viewMenu));
		Menu refactorMenu = new Menu("Refactor");
		Menu autoMlmMenu = new Menu("AutoMLM");
		Menu helpMenu = new Menu("Help");
		menuBar.getMenus().addAll(modelMenu, viewMenu, refactorMenu, autoMlmMenu, helpMenu);
//		setMenuBarOpenMenusOnHover(hBox, menuBar);
		
		buildModelMenu(modelMenu);
		buildViewMenu(viewMenu);	
		buildRefactorMenu(refactorMenu);
		buildAutoMlmMenu(autoMlmMenu);
		buildHelpMenu(helpMenu);
		
		ToolBar toolBar = buildToolBar();
		this.getChildren().addAll(hBox, toolBar);
	}

	private void buildAutoMlmMenu(Menu autoMlmMenu) {
		
		buildGUIMainButon(autoMlmMenu);
		
		if (DEBUG) {
			buildProcessStringButton(autoMlmMenu);
			buildIllegalArgumentsButton(autoMlmMenu);
			buildSimulateLostFielButton(autoMlmMenu);
		}
		
		
	}
	// FH main GUI für AutoMLM
	private void buildGUIMainButon(Menu autoMlmMenu) {
		EventHandler<ActionEvent> onButtonClicked = e -> {
			diagramActions.addAutoMLMDialog();;
		};
		JavaFxMenuAuxiliary.addMenuItem(autoMlmMenu, "Main GUI", onButtonClicked);
	}
	
	private void buildSimulateLostFielButton(Menu autoMlmMenu) {
		EventHandler<ActionEvent> onButtonClicked = e -> {
			String[] args = {"foo"};
			PythonRequestWrapper wrapper = new PythonRequestWrapper(PythonFunction.SIMULATE_LOST_FILE, args);
			wrapper.execute();
			System.err.println(wrapper.getResponse());
		};
		JavaFxMenuAuxiliary.addMenuItem(autoMlmMenu, "Test file loss", onButtonClicked);
	}

	private void buildIllegalArgumentsButton(Menu autoMlmMenu) {
		 EventHandler<ActionEvent> onButtonClicked = e -> {
			String[] args = {"foo"};
			PythonRequestWrapper wrapper = new PythonRequestWrapper(PythonFunction.ILLEGAL_ARGUMENTS, args);
			wrapper.execute();
			System.err.println(wrapper.getResponse());
	     };
		JavaFxMenuAuxiliary.addMenuItem(autoMlmMenu, "Illegal Arguments",  onButtonClicked);
	}

	private void buildProcessStringButton(Menu autoMlmMenu) {
		EventHandler<ActionEvent> onButtonClicked = e -> {
			String[] args = {"foo"};
			PythonRequestWrapper wrapper = new PythonRequestWrapper(PythonFunction.PROCESS_STRIGN, args);
			wrapper.execute();
			System.err.println(wrapper.getResponse());
		};
		JavaFxMenuAuxiliary.addMenuItem(autoMlmMenu, "Process String", onButtonClicked);
	}

	/**
	 * Was an idea to open Menus on hover. Was decided to not use this. Could be used later
	 */
	private void setMenuBarOpenMenusOnHover(HBox hBox, MenuBar menuBar) {
		for(int i = 0 ; i < hBox.getChildren().size() ; i++) {
            Node parentNode = hBox.getChildren().get(i);
            Menu menu = menuBar.getMenus().get(i);
            parentNode.setOnMouseEntered(e->{
                menu.show();
            });
        }
	}

	private ToolBar buildToolBar() {
		ToolBar toolBar = new ToolBar();		
		
		Button undoButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> {diagramActions.undo(); fmmlxDiagram.updateDiagram();}, "resources/png/undo.24.png");
		Button redoButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> {diagramActions.redo();fmmlxDiagram.updateDiagram();}, "resources/png/redo.24.png");
		
		JavaFxTooltipAuxilary.addTooltip(undoButton, "Undo last action(Strg + Z)");
		JavaFxTooltipAuxilary.addTooltip(redoButton, "Redo last action(Strg + Y)");
		
		Button zoomInButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> fmmlxDiagram.getActiveDiagramViewPane().zoomIn(), "resources/png/magnifier+.24.png");
		Button zoomOneButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> fmmlxDiagram.getActiveDiagramViewPane().zoomOne(), "resources/png/magnifier1.24.png");
		Button zoomOutButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> fmmlxDiagram.getActiveDiagramViewPane().zoomOut(), "resources/png/magnifier-.24.png");
		updateButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> fmmlxDiagram.updateDiagram(), "resources/png/update.24.png");
		updateSvg = updateButton.getGraphic();
		
		JavaFxTooltipAuxilary.addTooltip(updateButton, "Update Model(F5)");
		Button centerViewButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> diagramActions.centerViewOnObject(), "resources/png/target.24.png");
		JavaFxTooltipAuxilary.addTooltip(centerViewButton, "Center View on Object (Strg + F");
		Button saveButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> new XMLCreator().createAndSaveXMLRepresentation(fmmlxDiagram.getPackagePath()), "resources/png/save.24.png");
		JavaFxTooltipAuxilary.addTooltip(saveButton, "Save Model(Strg + S)");
		
		toolBar.getItems().addAll(undoButton, redoButton, new Separator(),zoomInButton, zoomOneButton, zoomOutButton, new Separator(), updateButton, centerViewButton, saveButton );
		return toolBar;
	}
		
	private void buildViewMenu(Menu viewMenu) {
		JavaFxMenuAuxiliary.addMenuItem(viewMenu, "Hide/Unhide Elements...", e -> diagramActions.showUnhideElementsDialog());
		viewMenu.getItems().add(new SeparatorMenuItem());
				
		class ToggleMenuItem extends MenuItem {
			final String visibleText;
			final String invisibleText;
			final DiagramDisplayProperty property;
			
			private ToggleMenuItem(DiagramDisplayProperty property) {
				this.property = property;
				visibleText = "Hide " + property.getLabel();
				invisibleText = "Show " + property.getLabel();
				setText();
				//IssueTable is the only DiagramDisplayProperty which visualization is not requested from XMF
				if (property.equals(DiagramDisplayProperty.ISSUETABLE)) {
					setOnAction(e -> {toggleItem(); fmmlxDiagram.switchTableOnAndOffForIssues();});	
				} else {					
					setOnAction(e -> toggleItem());
				}
			}
			
			private void setText() {
				if (diagramDisplayModel.getPropertieValue(property)) {
					setText(visibleText);
				} else {
					setText(invisibleText);
				}
			}
			
			private void toggleItem() {
				diagramDisplayModel.toggleDisplayProperty(property);
				setText();
				fmmlxDiagram.triggerOverallReLayout();
				fmmlxDiagram.redraw();
			}
		}
		
		Map<DiagramDisplayProperty, MenuItem> itemMap = new HashMap<DiagramDisplayProperty, MenuItem>();
		EnumSet.allOf(DiagramDisplayProperty.class)
			.forEach(property -> itemMap.put(property,(MenuItem)new ToggleMenuItem(property)));
				
		Menu operationsMenu = new Menu("Operations");
		operationsMenu.getItems().addAll(itemMap.get(DiagramDisplayProperty.OPERATIONS), itemMap.get(DiagramDisplayProperty.OPERATIONVALUES), itemMap.get(DiagramDisplayProperty.DERIVEDOPERATIONS));
		
		Menu constraintsMenu = new Menu("Constraints");
		constraintsMenu.getItems().addAll(itemMap.get(DiagramDisplayProperty.CONSTRAINTS), itemMap.get(DiagramDisplayProperty.CONSTRAINTREPORTS));		
		
		viewMenu.getItems().addAll(
				operationsMenu,
				itemMap.get(DiagramDisplayProperty.SLOTS),
				itemMap.get(DiagramDisplayProperty.GETTERSANDSETTERS),
				itemMap.get(DiagramDisplayProperty.DERIVEDATTRIBUTES),
				constraintsMenu,
				itemMap.get(DiagramDisplayProperty.METACLASSNAME),
				itemMap.get(DiagramDisplayProperty.CONCRETESYNTAX),
				itemMap.get(DiagramDisplayProperty.ISSUETABLE)				
		);
		
		viewMenu.getItems().add(new SeparatorMenuItem());
		JavaFxMenuAuxiliary.addMenuItem(viewMenu, "Switch to Concrete Syntax Wizard", 
		e -> {
			ConcreteSyntaxWizard wizard = new ConcreteSyntaxWizard(fmmlxDiagram, null, null);
			wizard.start(new Stage());
			});
		JavaFxMenuAuxiliary.addMenuItem(viewMenu, "Switch to Model Browser",
				e -> {
					Vector<String> models = new Vector<>(); 
					models.add(fmmlxDiagram.getPackagePath());
					ControlCenterClient.getClient().getControlCenter().showModelBrowser("(Project)", fmmlxDiagram.getPackagePath(), models);});		
	}
	
	/**
	 * Defines local class which is used to create MenuItem
	 */
	class HideNotesItem extends MenuItem {

		String hideText = "Hide all Notes";
		String unhideText = "Unhide all Notes";

		/**
		 * Defines default rendering. In other cases the label and the actionEvent is
		 * changed according to unhide the notes. This case is seen as the case that
		 * appears most often.
		 */
		public HideNotesItem() {
			this.setText(unhideText);
			this.setOnAction(e -> {
				diagramActions.hideAllNotes();
				// Toggles ItemText
				this.setText("alter");
			});
		}

		public void setUnhiding() {
			this.setText(unhideText);
			this.setOnAction(e -> {
				for (Note note : fmmlxDiagram.getNotes()) {
					note.unhide(fmmlxDiagram);
				}
			});
		}

		public void setHiding() {
			this.setText(hideText);
			this.setOnAction(e -> {
				for (Note note : fmmlxDiagram.getNotes()) {
					note.hide(fmmlxDiagram);
				}
			});
		}
	}

	/**
	 * The view menu should contain a menuItem to hide or unhide all notes.
	 * Depending on the diagram data this menu needs to be rendered on different
	 * ways. If the diagram do not contain notes, the item is disabled. If it
	 * contains minimum one note it should show the text "Hide all" and implement
	 * the specific functionality. Otherwise it should be used to unhide all notes.
	 * 
	 * @param viewMenu parent menu the item is added to
	 */
	private void renderHideNotesItem(Menu viewMenu) {
		// Checks if the Menu already contains item
		HideNotesItem item = (HideNotesItem)DiagramViewHeadToolBar.findMenuItemByContainingText(viewMenu, "Note");
		if (item == null) {
			item = new HideNotesItem();
			//Position is set via hard coded number. If more elements are added to the Menu, this could lead to a wrong position of this item.
			viewMenu.getItems().add(9, item);				
		}
		
		//if no notes on the diagram the menu is disabled
		if (fmmlxDiagram.getNotes().isEmpty()) {
			item.setDisable(true);
		} else {
			item.setDisable(false);
			configureItem(item);
		}
	}

	/**
	 * In case the diagram has notes, the rendering of the menu item depends on the
	 * visibility of the contained notes. This function will render the item
	 * accordingly.
	 * 
	 * @param item
	 */
	private void configureItem(HideNotesItem item) {
		if (fmmlxDiagram.getNotes().allhidden()) {
			item.setUnhiding();
		} else {
			item.setHiding();
		}
	}

	private void buildRefactorMenu(Menu refactorMenu) {
		Menu levelsMenu = new Menu("Levels");
		refactorMenu.getItems().add(levelsMenu);
		JavaFxMenuAuxiliary.addMenuItem(levelsMenu, "Raise all", e -> diagramActions.levelRaiseAll());
		JavaFxMenuAuxiliary.addMenuItem(levelsMenu, "Lower all", e -> diagramActions.levelLowerAll());
	}

	private void buildModelMenu(Menu modelMenu) {
		JavaFxMenuAuxiliary.addMenuItem(modelMenu, "Save...", e -> new XMLCreator().createAndSaveXMLRepresentation(fmmlxDiagram.getPackagePath()));
		modelMenu.getItems().add(new SeparatorMenuItem());
				
		Menu enumMenu = new Menu("Enumeration");
		modelMenu.getItems().add(enumMenu);
		JavaFxMenuAuxiliary.addMenuItem(enumMenu, "Create", e -> diagramActions.addEnumerationDialog());
		JavaFxMenuAuxiliary.addMenuItem(enumMenu, "Edit", e -> diagramActions.editEnumerationDialog("edit_element",""));
		JavaFxMenuAuxiliary.addMenuItem(enumMenu, "Delete", e -> diagramActions.deleteEnumerationDialog());
			
		JavaFxMenuAuxiliary.addMenuItem(modelMenu, "Assign Global Variabel", e -> diagramActions.assignGlobalVariable());
		modelMenu.getItems().add(new SeparatorMenuItem());
				
		Menu exportMenu = new Menu("Export as...");
		modelMenu.getItems().add(exportMenu);
		JavaFxMenuAuxiliary.addMenuItem(exportMenu, "SVG", e -> diagramActions.exportSvg());
		JavaFxMenuAuxiliary.addMenuItem(exportMenu, "PNG", e -> diagramActions.exportPNG());
		
		JavaFxMenuAuxiliary.addMenuItem(modelMenu, "Merge Models",e -> diagramActions.mergeModels());	
	}
	
	private void buildHelpMenu(Menu helpMenu) {
		JavaFxMenuAuxiliary.addMenuItem(helpMenu, "Shortcutlist", e -> showShortcutDialog());
	}
	
	private void showShortcutDialog() {
		new ShortcutDialog().show();
	}

	public FmmlxDiagram getFmmlxDiagram() {
		return fmmlxDiagram;
	}
	
	 public DiagramDisplayModel getModel() {
		 return diagramDisplayModel;
	 }
	
	public void toggleUpdateButton(boolean loading) {
		Platform.runLater(() -> {
			if (loading) {
				ProgressIndicator progressIndicator = new ProgressIndicator();
				progressIndicator.setPrefSize(24, 24);
				updateButton.setGraphic(progressIndicator);
			} else {
				updateButton.setGraphic(updateSvg);
			}
		});
	}
	
	/**
	 * Helper function needed for adding the notesHideMenuItem. Be careful this is
	 * only a heuristic. If new elements are added that may contain the same
	 * chatsSeq this will lead to confusion with current implementation
	 * 
	 * @param menu you want to search for a item
	 * @param text of the menu item
	 * @return the found item
	 */
	private static MenuItem findMenuItemByContainingText(Menu menu, String text) {
		for (MenuItem item : menu.getItems()) {
			try {
				if (item.getText().contains(text)) {
					return item;
				}
			} catch (Exception e) {
				// Separator Menus do not have text
			}
		}
		return null;
	}
}
