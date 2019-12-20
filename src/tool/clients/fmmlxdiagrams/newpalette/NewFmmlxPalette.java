package tool.clients.fmmlxdiagrams.newpalette;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class NewFmmlxPalette {
	
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
		root.setExpanded(true);
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
		
		PaletteTool modelsGroupTooL = new GroupToolModels(fmmlxDiagram, "Models", "models", "");
		PaletteTool relatiosshipGroupTool = new GroupToolRelationsship(fmmlxDiagram, "Relationsship", "relationsship", "");
		PaletteTool classGroupTool = new GroupToolClass(fmmlxDiagram, "Classes/Object", "classes", "");
		
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
					setText(null);
				} else {
					ImageView image = new ImageView(new javafx.scene.image.Image(new File(item.getIcon()).toURI().toString()));
					setGraphic(image);
					setText(item.getLabel());
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
		Iterator groupIterator = paletteGroups.entrySet().iterator();
		
		while(groupIterator.hasNext()) {
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

	public void deselect() {
		// TODO Auto-generated method stub
		
	}



}
