package tool.clients.fmmlxdiagrams.graphics.wizard;

import java.util.HashSet;
import java.util.Optional;
import java.util.Vector;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.transform.Affine;
import javafx.util.Callback;
import tool.clients.fmmlxdiagrams.graphics.*;
import tool.clients.fmmlxdiagrams.graphics.Modification.Consequence;

public class ConcreteSyntaxTree extends TreeView<NodeElement>{
	
	private ConcreteSyntaxWizard owner;
	private HashSet<NodeElement> expandedNodes = new HashSet<>(); 
	
	public ConcreteSyntaxTree(ConcreteSyntaxWizard owner) {
		this.owner = owner;
		setCellFactory(new Callback<TreeView<NodeElement>, TreeCell<NodeElement>>() {
			@Override
			public TreeCell<NodeElement> call(TreeView<NodeElement> treeview) {
				return new TreeCellWithMenu();
			}
	    });
	}

	public void setTree(NodeGroup newRoot) {
		NodeElement oldRoot = getRoot().getValue();
		expandedNodes.clear();
		if(oldRoot == newRoot) storeExpanded(getRoot());
		
		TreeItem<NodeElement> rootElement = new TreeItem<NodeElement>(newRoot);
		this.setRoot(rootElement);
		
		for (NodeElement child : newRoot.getChildren()) {
			addToTree(child, rootElement, expandedNodes.contains(child));
		}
		rootElement.setExpanded(true);
	}


	private void storeExpanded(TreeItem<NodeElement> item) {
		if(item.isExpanded()) expandedNodes.add(item.getValue());
		for(TreeItem<NodeElement> i : item.getChildren()) {
			storeExpanded(i);
		}
	}

	private void addToTree(NodeElement element, TreeItem<NodeElement> parentItem, boolean expanded) {
		TreeItem<NodeElement> item = new TreeItem<NodeElement>(element);
		parentItem.getChildren().add(item);
		for (NodeElement elm : element.getChildren()) {
			addToTree(elm,item,expandedNodes.contains(elm));
		}
		item.setExpanded(expanded);
	}
	
	private class TreeCellWithMenu extends TextFieldTreeCell<NodeElement> {

	    @Override
	    public void updateItem(NodeElement item, boolean empty) {
	        super.updateItem(item, empty);
	        if (empty || item == null) {
	            setText(null);
	            setGraphic(null);
	            setContextMenu(null);
	        } else {
	            setText(item.toString());
	            setContextMenu(getContextMenuForItem(item, owner));
	        }
	    }
	}
	
    private ContextMenu getContextMenuForItem(final NodeElement item, ConcreteSyntaxWizard parent) {
		ContextMenu menu = new ContextMenu();
		
		// Figure out whether the item is inside an SVG:
		boolean insideSVG = false;
		NodeElement svgRoot = item;
		while(!insideSVG && svgRoot != null) {
			insideSVG |= (svgRoot != item) && (svgRoot instanceof SVGGroup);
			svgRoot = svgRoot.getOwner();
		}
		
		if(!insideSVG) {
			if(item instanceof NodeGroup) {
				MenuItem addgroupItem = new MenuItem("Add Group");
				addgroupItem.setOnAction(event -> {
					NodeGroup newGroup = new NodeGroup();
					((NodeGroup) item).addNodeElement(newGroup);
					parent.updateUI(item);
				});
				
				MenuItem addLabelItem = new MenuItem("Add Label");
				addLabelItem.setOnAction(event -> {
					
					String newID = ConcreteSyntaxWizard.getNextAvailableID("label", item.getRoot());
					EditLabelDialog nld = new EditLabelDialog(newID);
					Optional<EditLabelDialog.Result> result = nld.showAndWait();
					
					if(result.isPresent()) {
						NodeLabel newLabel = new NodeLabel(
							result.get().alignment, 
							new Affine(), 
							result.get().fgColor, result.get().bgColor, 
							null, null, 
							result.get().id, 
							false, -1);
						newLabel.setID(result.get().id);
						((NodeGroup) item).addNodeElement(newLabel);
						parent.updateUI(item);	
					}
				});
				
				MenuItem addSvgItem = new MenuItem("Add SVG");
				addSvgItem.setOnAction(event -> {
					Vector<SVGGroup> svgCache = ConcreteSyntaxWizard.loadSVGs();
					SvgChooseDialog scd = new SvgChooseDialog(svgCache, ConcreteSyntaxWizard.getNextAvailableID("svg", item.getRoot()));
					Optional<SVGGroup> result = scd.showAndWait();
					
					if(result.isPresent()) {
						((NodeGroup) item).addNodeElement(result.get());
						parent.updateUI(item);
					}						
				});
									
				menu.getItems().addAll(addSvgItem, addLabelItem, addgroupItem);
			} else if(item instanceof NodeLabel) {
				MenuItem editLabelItem = new MenuItem("Edit Label");
				editLabelItem.setOnAction(event -> {
					NodeLabel label = (NodeLabel) item;
					EditLabelDialog eld = new EditLabelDialog(label.getID());
					eld.setValues(label);
					Optional<EditLabelDialog.Result> result = eld.showAndWait();
					
					if(result.isPresent()) {
						label.setAlignment(result.get().alignment);
						label.setFgColor(result.get().fgColor);
						label.setBgColor(result.get().bgColor);
						label.setID(result.get().id);

						parent.updateUI(item);	
					}
				});
				
				if(menu.getItems().size() != 0) menu.getItems().addAll(new SeparatorMenuItem());
				menu.getItems().add(editLabelItem);

			}
			
			if(svgRoot != item && item.getOwner() != null) {
				NodeGroup itemParent = item.getOwner();
				int patentSize = itemParent.getChildren().size();
				int currentPos = itemParent.getChildren().indexOf(item);
				MenuItem moveTopItem = new MenuItem("Move to Top");
				moveTopItem.setDisable(currentPos == 0);
				moveTopItem.setOnAction(event -> {
					itemParent.getChildren().remove(item);
					itemParent.getChildren().insertElementAt(item, 0);
					parent.updateUI(item);
				});
				MenuItem moveUpItem = new MenuItem("Move Up");
				moveUpItem.setOnAction(event -> {
					itemParent.getChildren().remove(item);
					itemParent.getChildren().insertElementAt(item, currentPos - 1);
					parent.updateUI(item);
				});
				moveUpItem.setDisable(currentPos <= 1);
				MenuItem moveDownItem = new MenuItem("Move Down");
				moveDownItem.setOnAction(event -> {
					itemParent.getChildren().remove(item);
					itemParent.getChildren().insertElementAt(item, currentPos);
					parent.updateUI(item);
				});
				moveDownItem.setDisable(currentPos >= patentSize - 2);
				MenuItem moveBottomItem = new MenuItem("Move to Bottom");
				moveBottomItem.setDisable(currentPos == patentSize - 1);
				moveBottomItem.setOnAction(event -> {
					itemParent.getChildren().remove(item);
					itemParent.getChildren().add(item);
					parent.updateUI(item);
				});
				if(menu.getItems().size() != 0) menu.getItems().addAll(new SeparatorMenuItem());
				menu.getItems().addAll(moveTopItem, moveUpItem, moveDownItem, moveBottomItem);
			}
			
			MenuItem deleteItem = new MenuItem("Delete");
			deleteItem.setOnAction(event -> {
				item.getOwner().getChildren().remove(item);
				parent.updateUI(item);
			});
			if(menu.getItems().size() != 0) menu.getItems().addAll(new SeparatorMenuItem());
			menu.getItems().addAll(deleteItem);
		}
		
		Menu modMenu = new Menu("Modifications");
		if(item instanceof NodeLabel) {
			MenuItem readFromSlotItem = new MenuItem("Read from Attribute");
			readFromSlotItem.setDisable(parent.getSelectedClass() == null || parent.getSelectedLevel() == null);
			readFromSlotItem.setOnAction(event -> {
				DefaultModificationDialog dmd = new DefaultModificationDialog(parent.getSelectedClass(), parent.getSelectedLevel(), Condition.ReadFromSlotCondition.class, item);
				Optional<DefaultModificationDialog.Result> result = dmd.showAndWait();
				
				if(result.isPresent()) {
					Consequence consequence = result.get().consequence;
					
					if(result.get().property != null && consequence != null) {
						Condition<String> condition = new Condition.ReadFromSlotCondition(result.get().getPropertyName());
						Modification mod = new Modification(
								condition, 
								consequence, 
								item.getID(), 
								item.getID());							
						
						((ConcreteSyntax) item.getRoot()).addModification(mod);

						parent.updateUI(item);
					}
				}						
			});
			MenuItem readFromOpValItem = new MenuItem("Read from Operation");
			readFromOpValItem.setDisable(parent.getSelectedClass() == null || parent.getSelectedLevel() == null);
			readFromOpValItem.setOnAction(event -> {
				DefaultModificationDialog dmd = new DefaultModificationDialog(parent.getSelectedClass(), parent.getSelectedLevel(), Condition.ReadFromOpValCondition.class, item);
				Optional<DefaultModificationDialog.Result> result = dmd.showAndWait();
				
				if(result.isPresent()) {
					Consequence consequence = result.get().consequence;
					
					if(result.get().property != null  && consequence != null) {
						Condition<String> condition = new Condition.ReadFromOpValCondition(result.get().getPropertyName());
						Modification mod = new Modification(
								condition, 
								consequence, 
								item.getID(), 
								item.getID());							
						
						((ConcreteSyntax) item.getRoot()).addModification(mod);

						parent.updateUI(item);
					}
				}						
			});
			modMenu.getItems().addAll(readFromSlotItem, readFromOpValItem);
		} else {
			MenuItem boolSlotItem = new MenuItem("Depends on Attribute (Boolean)");
			boolSlotItem.setDisable(parent.getSelectedClass() == null || parent.getSelectedLevel() == null);
			boolSlotItem.setOnAction(event -> {
				DefaultModificationDialog dmd = new DefaultModificationDialog(parent.getSelectedClass(), parent.getSelectedLevel(), Condition.BooleanSlotCondition.class, item);
				Optional<DefaultModificationDialog.Result> result = dmd.showAndWait();
				
				if(result.isPresent()) {
					Consequence consequence = result.get().consequence;
					
					if(result.get().property != null  && consequence != null) {
						Condition<Boolean> condition = new Condition.BooleanSlotCondition(result.get().getPropertyName());
						Modification mod = new Modification(
								condition, 
								consequence, 
								item.getID(), 
								item.getID());							
						
						((ConcreteSyntax) item.getRoot()).addModification(mod);

						parent.updateUI(item);
					}
				}						
			});
			MenuItem boolOpValItem = new MenuItem("Depends on Operation (Boolean)");
			boolOpValItem.setDisable(parent.getSelectedClass() == null || parent.getSelectedLevel() == null);
			boolOpValItem.setOnAction(event -> {
				DefaultModificationDialog dmd = new DefaultModificationDialog(parent.getSelectedClass(), parent.getSelectedLevel(), Condition.BooleanOpValCondition.class, item);
				Optional<DefaultModificationDialog.Result> result = dmd.showAndWait();
				
				if(result.isPresent()) {
					Consequence consequence = result.get().consequence;
					
					if(result.get().property != null  && consequence != null) {
						Condition<Boolean> condition = new Condition.BooleanOpValCondition(result.get().getPropertyName());
						Modification mod = new Modification(
								condition, 
								consequence, 
								item.getID(), 
								item.getID());							
						
						((ConcreteSyntax) item.getRoot()).addModification(mod);

						parent.updateUI(item);
					}
				}						
			});
			MenuItem boolConItem = new MenuItem("Depends on Constraint");
			boolConItem.setDisable(parent.getSelectedClass() == null || parent.getSelectedLevel() == null);
			boolConItem.setOnAction(event -> {
				DefaultModificationDialog dmd = new DefaultModificationDialog(parent.getSelectedClass(), parent.getSelectedLevel(), Condition.BooleanConstraintCondition.class, item);
				Optional<DefaultModificationDialog.Result> result = dmd.showAndWait();
				
				if(result.isPresent()) {
					Consequence consequence = result.get().consequence;
					
					if(result.get().property != null && consequence != null) {
						Condition<Boolean> condition = new Condition.BooleanConstraintCondition(result.get().getPropertyName());
						Modification mod = new Modification(
								condition, 
								consequence, 
								item.getID(), 
								item.getID());							
						
						((ConcreteSyntax) item.getRoot()).addModification(mod);

						parent.updateUI(item);
					}
				}						
			});
			MenuItem numSlotItem = new MenuItem("Depends on Attribute (Number Range)");
			numSlotItem.setDisable(true);
			MenuItem numOpValItem = new MenuItem("Depends on Operation (Number Range)");
			numOpValItem.setDisable(true);
			modMenu.getItems().addAll(boolSlotItem, boolOpValItem, boolConItem, numSlotItem, numOpValItem);
		}
		if(item instanceof NodePath) {
			MenuItem colorSlotItem = new MenuItem("Read Color from Attribute (Hex)");
			colorSlotItem.setDisable(parent.getSelectedClass() == null || parent.getSelectedLevel() == null);
			colorSlotItem.setOnAction(event -> {
				DefaultModificationDialog dmd = new DefaultModificationDialog(parent.getSelectedClass(), parent.getSelectedLevel(), Condition.ReadFromSlotCondition.class, item);
				Optional<DefaultModificationDialog.Result> result = dmd.showAndWait();
				
				if(result.isPresent()) {
					Consequence consequence = result.get().consequence;
					
					if(result.get().property != null && consequence != null) {
						Condition<String> condition = new Condition.ReadFromSlotCondition(result.get().getPropertyName());
						Modification mod = new Modification(
								condition, 
								consequence, 
								item.getID(), 
								item.getID());							
						
						((ConcreteSyntax) item.getRoot()).addModification(mod);

						parent.updateUI(item);
					}
				}						
			});
			MenuItem colorOpValItem = new MenuItem("Read Color from Operation (Hex)");
			colorOpValItem.setDisable(parent.getSelectedClass() == null || parent.getSelectedLevel() == null);
			colorOpValItem.setOnAction(event -> {
				DefaultModificationDialog dmd = new DefaultModificationDialog(parent.getSelectedClass(), parent.getSelectedLevel(), Condition.ReadFromOpValCondition.class, item);
				Optional<DefaultModificationDialog.Result> result = dmd.showAndWait();
				
				if(result.isPresent()) {
					Consequence consequence = result.get().consequence;
					
					if(result.get().property != null  && consequence != null) {
						Condition<String> condition = new Condition.ReadFromOpValCondition(result.get().getPropertyName());
						Modification mod = new Modification(
								condition, 
								consequence, 
								item.getID(), 
								item.getID());							
						
						((ConcreteSyntax) item.getRoot()).addModification(mod);

						parent.updateUI(item);
					}
				}						
			});
			modMenu.getItems().addAll(colorSlotItem, colorOpValItem);
		}
		if(menu.getItems().size() != 0) menu.getItems().addAll(new SeparatorMenuItem());
		menu.getItems().addAll(modMenu);
		
		return menu;
	}
}
