package tool.clients.fmmlxdiagrams.menus;

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.DiagramDisplayModel;
import tool.clients.fmmlxdiagrams.DiagramDisplayProperty;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;
import tool.helper.FXAuxilary.JavaFxButtonAuxilary;
import tool.helper.FXAuxilary.JavaFxMenuAuxiliary;
import tool.helper.FXAuxilary.JavaFxSvgAuxilary;
import tool.helper.FXAuxilary.JavaFxTooltipAuxilary;
import tool.xmodeler.ControlCenterClient;

public class DiagramViewHeadToolBar extends VBox {
	
	private DiagramDisplayModel model;
	private FmmlxDiagram fmmlxDiagram;
	private DiagramActions diagramActions;
	private Button updateButton;
	private Node updateSvg;
		
	public DiagramViewHeadToolBar(FmmlxDiagram fmmlxDiagram) {
		this.fmmlxDiagram = fmmlxDiagram;
		diagramActions = fmmlxDiagram.getActions();
		model = new DiagramDisplayModel(this);
				
		HBox hBox = new HBox();
		MenuBar menuBar = new MenuBar();
		menuBar.setStyle("-fx-background-color: #F3F3F3");
		hBox.getChildren().add(menuBar);
				
		Menu modelMenu = new Menu("Model");		
		Menu viewMenu = new Menu("View");
		Menu refactorMenu = new Menu("Refactor");
		menuBar.getMenus().addAll(modelMenu, viewMenu, refactorMenu);
		setMenuBarOpenMenusOnHover(hBox, menuBar);
		buildModelMenu(modelMenu);
		buildViewMenu(viewMenu);	
		buildRefactorMenu(refactorMenu);
		ToolBar toolBar = buildToolBar();
		this.getChildren().addAll(hBox, toolBar);
	}

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
		
		Button zoomInButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> fmmlxDiagram.getActiveDiagramViewPane().zoomIn(), "resources/png/magnifier+.24.png");
		Button zoomOneButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> fmmlxDiagram.getActiveDiagramViewPane().zoomOne(), "resources/png/magnifier1.24.png");
		Button zoomOutButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> fmmlxDiagram.getActiveDiagramViewPane().zoomOut(), "resources/png/magnifier-.24.png");
		updateButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> fmmlxDiagram.updateDiagram(), "resources/png/update.24.png");
		updateSvg = updateButton.getGraphic();
		JavaFxTooltipAuxilary.addTooltip(updateButton, "Update Model");
		Button centerViewButton = JavaFxButtonAuxilary.createButtonWithPicture(null, e -> diagramActions.centerViewOnObject(), "resources/png/target.24.png");
		JavaFxTooltipAuxilary.addTooltip(centerViewButton, "Center View on Object");
		toolBar.getItems().addAll(zoomInButton, zoomOneButton, zoomOutButton, new Separator(), updateButton, centerViewButton );
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
				if (model.getPropertieValue(property)) {
					setText(visibleText);
				} else {
					setText(invisibleText);
				}
			}
			
			private void toggleItem() {
				model.toggleDisplayProperty(property);
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

	private void buildRefactorMenu(Menu refactorMenu) {
		Menu levelsMenu = new Menu("Levels");
		refactorMenu.getItems().add(levelsMenu);
		JavaFxMenuAuxiliary.addMenuItem(levelsMenu, "Raise all", e -> diagramActions.levelRaiseAll());
		JavaFxMenuAuxiliary.addMenuItem(levelsMenu, "Lower all", e -> diagramActions.levelLowerAll());
	}

	private void buildModelMenu(Menu modelMenu) {
		JavaFxMenuAuxiliary.addMenuItem(modelMenu, "Save...", e -> fmmlxDiagram.getComm().saveXmlFile2(fmmlxDiagram.getPackagePath(), fmmlxDiagram.getID()));
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
	
	public FmmlxDiagram getFmmlxDiagram() {
		return fmmlxDiagram;
	}
	
	 public DiagramDisplayModel getModell() {
		 return model;
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
}