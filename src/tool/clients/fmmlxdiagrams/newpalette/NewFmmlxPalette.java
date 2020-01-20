package tool.clients.fmmlxdiagrams.newpalette;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import com.sun.prism.paint.Paint;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class NewFmmlxPalette {
	
	public static HashMap<Integer, Paint> colors = null;
	private TreeView<PaletteTool> tree;
	private HashMap<String, PaletteGroup> paletteGroups = new HashMap<>();
	private FmmlxDiagram fmmlxDiagram;
	private TreeItem<PaletteTool> root; 
	
	public NewFmmlxPalette(FmmlxDiagram fmmlxDiagram) {
		super();
		this.tree = initCustomTreeView();
		this.fmmlxDiagram = fmmlxDiagram;
		populateTree();
	}

	private void populateTree() {
		PaletteTool rootTool = new ToolRoot(fmmlxDiagram, "Root", "root", "");
		root = new TreeItem<PaletteTool>(rootTool);
		tree.setRoot(root);
		tree.setShowRoot(false);
		initGroup();
	}
	
	public void populate() {
		populateGroup();
	}

	private void populateGroup() {
		
		Iterator<Entry<String, PaletteGroup>> it = paletteGroups.entrySet().iterator();
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        ((PaletteGroup) pair.getValue()).populate(fmmlxDiagram);    
	    }
	}

	private void initGroup() {			
		
		PaletteTool modelsGroupTooL = new ToolGroup(fmmlxDiagram, "Models", "models", "");
		PaletteTool relatiosshipGroupTool = new ToolGroup(fmmlxDiagram, "Relationsship", "relationsship", "");
		PaletteTool classGroupTool = new ToolGroup(fmmlxDiagram, "Classes/Object", "classes", "");
		
		PaletteGroup modelsGroup = new PaletteGroupModels(modelsGroupTooL);
		PaletteGroup relationsshipGroup = new PaletteGroupRelationsship(relatiosshipGroupTool);
		PaletteGroup classGroup = new PaletteGroupClass(classGroupTool);
		
		paletteGroups.put("Models", modelsGroup);
		paletteGroups.put("Relationsship", relationsshipGroup);
		paletteGroups.put("Class", classGroup);
		
		root.getChildren().add(modelsGroup);
		root.getChildren().add(relationsshipGroup);
		root.getChildren().add(classGroup);
	
	}

	public TreeView<PaletteTool> getToolBar() {
		return tree;
	}
	
	public TreeView<PaletteTool> initCustomTreeView() {

		TreeView<PaletteTool> treeView = new TreeView<PaletteTool>();
		
		treeView.setCellFactory(param -> new TreeCell<PaletteTool>() {
			protected void updateItem(PaletteTool item, boolean empty) {
				super.updateItem(item, empty);
				
				if (empty || item == null || item.getLabel() == null) {
					setText("");
					setGraphic(null);
					setBorder(new Border(new BorderStroke(null, null, null, null,new Insets(2, 5, 2, 5))));
				} else { 
					
					if (!item.getIcon().equals("")) {
						ImageView imageView = new ImageView(new javafx.scene.image.Image(new File(item.getIcon()).toURI().toString()));
						setGraphic(imageView);
						setBorder(new Border(new BorderStroke(null, null, null, null,new Insets(2, 5, 2, 5))));
					} else {
						setGraphic(null);
						setBorder(new Border(new BorderStroke(null, null, null, null,new Insets(2, 5, 2, 5))));
					}	
					
					if(item.getLevel()==1000) {
						setText(item.getLabel());
						if(item.getId().equals("metaClass")) {
							setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED, new CornerRadii(10), new BorderWidths(2),new Insets(2, 5, 2, 25))));
						} else {
							setBorder(null);
						}
						
					} else {
						setText(item.getLabel());				
						if(item.getLevel()==1) {							
							setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1),new Insets(2, 5, 2, 25))));
						} else {
							setBorder(new Border(new BorderStroke(FmmlxObject.colors.get(item.getLevel()), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(1),new Insets(2, 5, 2, 25))));					
						}
					}
					
					if(item.isAbstract()) {
						setFont(fmmlxDiagram.getPaletteFontKursiv());
					} else {			
						setFont(fmmlxDiagram.getPaletteFont());
					}
				}
			};
		});
		
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<PaletteTool>>() {

	        @Override
	        public void changed(
	        		ObservableValue<? extends TreeItem<PaletteTool>> observable, 
	        		TreeItem<PaletteTool> oldValue,
	        		TreeItem<PaletteTool> newValue) {
	        	
	        	if(newValue == null) return;
	        	
	            if(newValue.getChildren().isEmpty()) {
	            	
	            	TreeItem<PaletteTool> parent = newValue.getParent();
	            	if(parent instanceof PaletteGroup) {
	            		newValue.getValue().widgetSelected();
	            	}
	            }
	        }
		  });
		return treeView;
	}

	public void clearAllGroup() {
		Iterator<Entry<String, PaletteGroup>> groupIterator = paletteGroups.entrySet().iterator();
		
		while(groupIterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Entry) groupIterator.next();
			((PaletteGroup) pair.getValue()).clearTreeItem();
			((PaletteGroup) pair.getValue()).clearTool();
		}
	}

	public HashMap<String, PaletteGroup> getFmmlxGroups() {
		return paletteGroups;
	}

	public void clearSelection() {
		if (Thread.currentThread().getName().equals("JavaFX Application Thread")) {
			tree.getSelectionModel().clearSelection();	
		} else { 
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				tree.getSelectionModel().clearSelection();
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	public synchronized void update() {
		clearAllGroup();
		populate();		
	}

}
