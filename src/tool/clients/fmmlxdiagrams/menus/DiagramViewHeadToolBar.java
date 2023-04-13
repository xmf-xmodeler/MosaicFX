package tool.clients.fmmlxdiagrams.menus;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tool.clients.fmmlxdiagrams.DiagramActions;
import tool.clients.fmmlxdiagrams.DiagramDisplayModel;
import tool.clients.fmmlxdiagrams.DiagramDisplayProperty;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.graphics.wizard.ConcreteSyntaxWizard;
import tool.helper.fXAuxilary.JavaFxButtonAuxilary;
import tool.helper.fXAuxilary.JavaFxMenuAuxiliary;
import tool.helper.fXAuxilary.JavaFxSvgAuxilary;
import tool.helper.fXAuxilary.JavaFxTooltipAuxilary;
import tool.helper.persistence.PackageSerializer;
import tool.xmodeler.ControlCenterClient;

public class DiagramViewHeadToolBar extends VBox {
	
	private DiagramDisplayModel model;
	private FmmlxDiagram fmmlxDiagram;
	private DiagramActions diagramActions;
	private Button updateButton;
	private Region updateSvg;
		
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
				
		String zoomInPath = "m15.97 17.031c-1.479 1.238-3.384 1.985-5.461 1.985-4.697 0-8.509-3.812-8.509-8.508s3.812-8.508 8.509-8.508c4.695 0 8.508 3.812 8.508 8.508 0 2.078-.747 3.984-1.985 5.461l4.749 4.75c.146.146.219.338.219.531 0 .587-.537.75-.75.75-.192 0-.384-.073-.531-.22zm-5.461-13.53c-3.868 0-7.007 3.14-7.007 7.007s3.139 7.007 7.007 7.007c3.866 0 7.007-3.14 7.007-7.007s-3.141-7.007-7.007-7.007zm-.744 6.26h-2.5c-.414 0-.75.336-.75.75s.336.75.75.75h2.5v2.5c0 .414.336.75.75.75s.75-.336.75-.75v-2.5h2.5c.414 0 .75-.336.75-.75s-.336-.75-.75-.75h-2.5v-2.5c0-.414-.336-.75-.75-.75s-.75.336-.75.75z";
		Region zoomInSvg = JavaFxSvgAuxilary.buildSvgShape(zoomInPath, 15, 15);
		Button zoomInButton = JavaFxButtonAuxilary.createButtonWithGraphic(null, e -> fmmlxDiagram.getActiveDiagramViewPane().zoomIn(), zoomInSvg);
		
		Button zoomOneButton = new Button("100%");
		zoomOneButton.setOnAction(e -> {fmmlxDiagram.getActiveDiagramViewPane().zoomOne(); new PackageSerializer(fmmlxDiagram).createPackageXML();});
		
		String zoomOutPath = "m15.97 17.031c-1.479 1.238-3.384 1.985-5.461 1.985-4.697 0-8.509-3.812-8.509-8.508s3.812-8.508 8.509-8.508c4.695 0 8.508 3.812 8.508 8.508 0 2.078-.747 3.984-1.985 5.461l4.749 4.75c.146.146.219.338.219.531 0 .587-.537.75-.75.75-.192 0-.384-.073-.531-.22zm-5.461-13.53c-3.868 0-7.007 3.14-7.007 7.007s3.139 7.007 7.007 7.007c3.866 0 7.007-3.14 7.007-7.007s-3.141-7.007-7.007-7.007zm3.256 6.26h-6.5c-.414 0-.75.336-.75.75s.336.75.75.75h6.5c.414 0 .75-.336.75-.75s-.336-.75-.75-.75z";
		Region zoomOutSvg = JavaFxSvgAuxilary.buildSvgShape(zoomOutPath, 15, 15);
		Button zoomOutButton = JavaFxButtonAuxilary.createButtonWithGraphic(null, e -> fmmlxDiagram.getActiveDiagramViewPane().zoomOut(), zoomOutSvg);
		
		String updatePath = "M7 9h-7v-7h1v5.2c1.853-4.237 6.083-7.2 11-7.2 6.623 0 12 5.377 12 12s-5.377 12-12 12c-6.286 0-11.45-4.844-11.959-11h1.004c.506 5.603 5.221 10 10.955 10 6.071 0 11-4.929 11-11s-4.929-11-11-11c-4.66 0-8.647 2.904-10.249 7h5.249v1z";
		updateSvg = JavaFxSvgAuxilary.buildSvgShape(updatePath, 15, 15);
		updateButton = JavaFxButtonAuxilary.createButtonWithGraphic(null, e -> fmmlxDiagram.updateDiagram(), updateSvg);
		JavaFxTooltipAuxilary.addTooltip(updateButton, "Update Model");
		
		String centerViewPah = "M 72.018548 14.696702 C 70.822093 14.678718 69.60648 15.167994 68.740185 16.251012 L 39.319621 53.032076 C 37.786872 54.948294 38.311075 57.539402 39.9941 58.885628 C 40.668623 59.425235 41.525782 59.764934 42.509828 59.785164 L 80.981756 60.454458 C 82.269565 60.4771 83.489791 59.900514 84.286393 58.904642 C 85.088629 57.901732 85.385396 56.591914 85.080833 55.340032 C 82.94217 46.491832 78.589594 28.47254 76.029469 17.889329 C 75.797222 16.928563 75.281354 16.161888 74.602726 15.619055 C 73.868256 15.031545 72.949124 14.71069 72.018548 14.696702 z M 143.68239 14.832566 C 142.75187 14.821828 141.824 15.117848 141.07263 15.687506 C 140.38426 16.209334 139.84496 16.957491 139.58435 17.906615 L 129.51348 55.043068 C 129.17613 56.286113 129.43652 57.610108 130.20698 58.626346 C 130.98288 59.649759 132.18005 60.258549 133.4684 60.269849 C 142.57092 60.363289 161.10739 60.556309 171.9953 60.666032 C 172.98368 60.676155 173.85344 60.36377 174.54594 59.838749 C 176.25907 58.539965 176.85829 55.964775 175.36873 54.000063 L 146.91304 16.467426 C 146.07915 15.367536 144.87876 14.846373 143.68239 14.832566 z M 69.93876 28.059064 L 75.735961 52.051299 L 51.091027 51.621927 L 69.93876 28.059064 z M 145.39745 28.225696 L 163.62705 52.270133 L 138.94547 52.014999 L 145.39745 28.225696 z M 106.76165 60.488337 C 94.508894 60.488335 84.490362 70.505143 84.490362 82.757902 C 84.490359 95.010665 94.508892 105.0292 106.76165 105.0292 C 119.01441 105.0292 129.0333 95.010662 129.03329 82.757902 C 129.03329 70.505145 119.01441 60.488339 106.76165 60.488337 z M 106.76165 68.488399 C 114.6909 68.4884 121.03323 74.828657 121.03323 82.757902 C 121.03324 90.687149 114.6909 97.029132 106.76165 97.029133 C 98.832405 97.029134 92.490076 90.687151 92.490078 82.757902 C 92.490078 74.828656 98.832407 68.488398 106.76165 68.488399 z M 135.55994 102.86436 C 134.61503 102.8484 133.68488 103.15616 132.93324 103.74765 C 131.92398 104.54188 131.3373 105.74977 131.3492 107.03811 C 131.41992 116.14083 131.56129 134.67794 131.64789 145.56605 C 131.65559 146.55446 131.98374 147.41821 132.52116 148.10113 C 133.85063 149.79057 136.43579 150.34322 138.37333 148.81848 L 175.38705 119.69073 C 177.31536 118.17322 177.39402 115.53084 176.06119 113.83718 C 175.52704 113.15834 174.76947 112.63245 173.8158 112.389 L 136.50373 102.98986 C 136.1915 102.91114 135.87491 102.86968 135.55994 102.86436 z M 80.146177 108.37117 C 79.754452 108.36042 79.358757 108.40569 78.969728 108.50945 C 70.170716 110.84223 52.25148 115.59027 41.727141 118.38257 C 40.77172 118.6359 40.016792 119.16835 39.489018 119.85875 C 38.183436 121.56666 38.291149 124.20837 40.249925 125.70573 L 77.668823 154.31042 C 79.618293 155.80066 82.197589 155.21958 83.506473 153.50734 C 84.031106 152.82111 84.351749 151.95651 84.350349 150.97225 L 84.173 112.49514 C 84.167311 111.20714 83.564088 109.99992 82.550932 109.22542 C 81.849462 108.68921 81.007972 108.39482 80.146177 108.37117 z M 139.70154 112.3655 L 163.6032 118.38741 L 139.89134 137.04742 L 139.70154 112.3655 z M 75.887382 117.9245 L 76.000429 142.57289 L 52.028591 124.24787 L 75.887382 117.9245 z ";
		Region centerViewSvg = JavaFxSvgAuxilary.buildSvgShape(centerViewPah, 15, 15);
		Button centerViewButton = JavaFxButtonAuxilary.createButtonWithGraphic(null, e -> diagramActions.centerViewOnObject(), centerViewSvg);
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
				progressIndicator.setPrefSize(15, 15);
				updateButton.setGraphic(progressIndicator);
			} else {
				updateButton.setGraphic(updateSvg);
			}
		});
	}
}