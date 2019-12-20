package tool.clients.fmmlxdiagrams.newpalette;

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
import tool.clients.fmmlxdiagrams.FmmlxDiagram;

public class NewFmmlxPalette {
	
	private TreeView<FmmlxTool> tree;
	private HashMap<String, PaletteGroup> paletteGroups = new HashMap<>();
	private FmmlxDiagram fmmlxDiagram;
	private TreeItem<FmmlxTool> root; 
	
	public NewFmmlxPalette(FmmlxDiagram fmmlxDiagram) {
		super();
		this.tree = initCustomTreeView();
		this.fmmlxDiagram = fmmlxDiagram;
		populateTree();
	}

	private void populateTree() {
		FmmlxTool rootTool = new ToolRoot(fmmlxDiagram, "Root", "root", "");
		root = new TreeItem<FmmlxTool>(rootTool);
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
		
		FmmlxTool modelsGroupTooL = new GroupToolModels(fmmlxDiagram, "Models", "models", "");
		FmmlxTool relatiosshipGroupTool = new GroupToolRelationsship(fmmlxDiagram, "Relationsship", "relationsship", "");
		FmmlxTool classGroupTool = new GroupToolClass(fmmlxDiagram, "Classes/Object", "classes", "");
		
		PaletteGroup modelsGroup = new PaletteGroupModels(modelsGroupTooL);
		PaletteGroup relationsshipGroup = new PaletteGroupRelationsship(relatiosshipGroupTool);
		PaletteGroup classGroup = new PaletteGroupClass(classGroupTool);
		
		paletteGroups.put("Models", modelsGroup);
		paletteGroups.put("Relationsship", relationsshipGroup);
		paletteGroups.put("Class", classGroup);
		
		root.getChildren().add(modelsGroup);
		root.getChildren().add(relationsshipGroup);
		root.getChildren().add(classGroup);
		
//		Iterator<Entry<String, PaletteGroup>> it = paletteGroups.entrySet().iterator();
//	    while (it.hasNext()) {
//	        @SuppressWarnings("rawtypes")
//			Map.Entry pair = (Map.Entry)it.next();
//	        root.getChildren().add((TreeItem<FmmlxTool>) pair.getValue());    
//	    }
	
	}

	public TreeView<FmmlxTool> getToolBar() {
		return tree;
	}
	
	public TreeView<FmmlxTool> initCustomTreeView() {

		TreeView<FmmlxTool> treeView = new TreeView<FmmlxTool>();
		
		treeView.setCellFactory(param -> new TreeCell<FmmlxTool>() {
			protected void updateItem(FmmlxTool item, boolean empty) {
				super.updateItem(item, empty);
				
				if (empty || item == null || item.getLabel() == null) {
					setText(null);
				} else {
					setText(item.getLabel());
				}
			};
		});
		
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<FmmlxTool>>() {

	        @Override
	        public void changed(
	        		ObservableValue<? extends TreeItem<FmmlxTool>> observable, 
	        		TreeItem<FmmlxTool> oldValue,
	        		TreeItem<FmmlxTool> newValue) {
	        	
	        	if(newValue == null) return;
	        	
	            if(newValue.getChildren().isEmpty()) {
	            	
	            	TreeItem<FmmlxTool> parent = newValue.getParent();
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
