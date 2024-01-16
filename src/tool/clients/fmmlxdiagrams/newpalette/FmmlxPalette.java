package tool.clients.fmmlxdiagrams.newpalette;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Vector;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tool.clients.fmmlxdiagrams.FmmlxDiagram;
import tool.clients.fmmlxdiagrams.FmmlxObject;

public class FmmlxPalette {

	private final VBox node;
	private final TreeView<AbstractTreeType> treeView;
	private final FmmlxDiagram fmmlxDiagram;
	private TreeItem<AbstractTreeType> root;
	private TreeItem<AbstractTreeType> relationships = new TreeItem<AbstractTreeType>(new TreeGroup("Relationships"));
	private TreeItem<AbstractTreeType> elements = new TreeItem<AbstractTreeType>(new TreeGroup("Elements"));
	private boolean showMetaClassName = false;

	// Vector<FmmlxObject> objects = diagram.getObjects();

	public Node getNode() {
		return node;
	}

	public FmmlxPalette(FmmlxDiagram fmmlxDiagram) {
		this.node = new VBox();
		this.treeView = new TreeView<>();
		this.fmmlxDiagram = fmmlxDiagram;

		treeView.setCellFactory(param -> new TreeCell<AbstractTreeType>() {
			protected void updateItem(AbstractTreeType item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText("");
					setGraphic(null);
					setBorder(new Border(new BorderStroke(null, null, null, null, null)));
					setBackground(new Background(new BackgroundFill(null, null, null)));
				} else {
					setText(item.toString());
					setGraphic(item.getIcon());
					if (item.getLevel() == 1000) {
						setText(item.toString());
						setTextFill(Color.BLACK);
						setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.DASHED,
								new CornerRadii(10), new BorderWidths(2), new Insets(2, 5, 2, 25))));
						setBackground(new Background(
								new BackgroundFill(Color.WHITE, new CornerRadii(10), new Insets(2, 5, 2, 25))));
					} else {
						setTextFill(Color.WHITE);
						setBorder(new Border(new BorderStroke(
								fmmlxDiagram.levelColorScheme.getLevelBgColor(item.getLevel()), BorderStrokeStyle.SOLID,
								new CornerRadii(10), new BorderWidths(1), new Insets(2, 5, 2, 25))));
						setBackground(new Background(
								new BackgroundFill(fmmlxDiagram.levelColorScheme.getLevelBgColor(item.getLevel()),
										new CornerRadii(10), new Insets(2, 5, 2, 25))));
					}
					if (item instanceof DefaultTool && item.getLevel() != 1000 || item instanceof TreeGroup) {
						setBorder(null);
						setBackground(null);
						setTextFill(Color.BLACK);
					}
				}
			}
		});

		treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null) {
				newValue.getValue().action.perform(null);
			}
//			if (newValue.getValue().toString().equals("MetaClass")) {
//				fmmlxDiagram.setNodeCreationType("MetaClass");
//			} else if (newValue.getValue().toString().equals("Association")) {
//				fmmlxDiagram.setEdgeCreationType("association");
//			} else if (newValue.getValue().toString().equals("Link")) {
//				fmmlxDiagram.setEdgeCreationType("associationInstance");
//			} else if (newValue.getValue().toString().equals("Delegation")) {
//				fmmlxDiagram.setEdgeCreationType("delegation");
//			} else {
//				newValue.getValue().action.perform(null);
////				
////				fmmlxDiagram.setNodeCreationType(newValue.getValue().toString());
//			}
		});
	}

	public synchronized void update() {
		Platform.runLater(() -> {
			treeView.getSelectionModel().clearSelection();
			elements.getChildren().clear();
			relationships.getChildren().clear();

			treeView.setRoot(null);
			this.root = new TreeItem<AbstractTreeType>();
			treeView.setRoot(root);
			treeView.setShowRoot(false);
			root.getChildren().add(relationships);
			root.getChildren().add(elements);
			
			DefaultTool associationTool = 
					new DefaultTool("Association", "resources/gif/Association.gif", point -> fmmlxDiagram.setEdgeCreationType("association"));
			DefaultTool linkTool = 
					new DefaultTool("Link", "resources/gif/Association.gif", point -> fmmlxDiagram.setEdgeCreationType("associationInstance"));
			DefaultTool delegationTool = 
					new DefaultTool("Delegation", "resources/gif/XCore/Delegation.png", point -> fmmlxDiagram.setEdgeCreationType("delegation"));
			DefaultTool metaClassTool = 
					new DefaultTool("MetaClass", "resources/gif/class.gif", point -> fmmlxDiagram.setNodeCreationType("MetaClass"));
			DefaultTool noteTool = 
					new DefaultTool("Note", "resources/png/note.16.png", point -> fmmlxDiagram.activateNoteCreationMode());
			
			

			TreeItem<AbstractTreeType> association = new TreeItem<AbstractTreeType>(associationTool);
			TreeItem<AbstractTreeType> link = new TreeItem<AbstractTreeType>(linkTool);
			TreeItem<AbstractTreeType> delegation = new TreeItem<AbstractTreeType>(delegationTool);
			TreeItem<AbstractTreeType> metaClass = new TreeItem<AbstractTreeType>(metaClassTool);
			TreeItem<AbstractTreeType> note = new TreeItem<AbstractTreeType>(noteTool);

			elements.getChildren().add(metaClass);
			relationships.getChildren().addAll(association, link, delegation, note);
	
			Vector<FmmlxObject> objects = fmmlxDiagram.getObjectsReadOnly();
			ArrayList<Integer> levelList = new ArrayList<Integer>();
			for (FmmlxObject o : objects) {
				levelList.add(o.getLevel().getMinLevel());
			}
			Set<Integer> levelSet = new LinkedHashSet<Integer>(levelList);
			levelList = new ArrayList<Integer>(levelSet);
			Collections.sort(levelList, Collections.reverseOrder());
			HashMap<Integer, TreeItem<AbstractTreeType>> levels = new HashMap<>();
			for (int i : levelList) {
				if(i!=0) {
					TreeItem<AbstractTreeType> levelGroup = new TreeItem<AbstractTreeType>(new TreeGroup("Level " + i));
					levels.put(i, levelGroup);
					levelGroup.setExpanded(true);
					elements.getChildren().add(levelGroup);
				}
			}
			Collections.sort(objects);
			for(final FmmlxObject o : objects) {
				if (o.getLevel().getMinLevel() > 0 && !o.isAbstract()) {
					TreeItem<AbstractTreeType> levelGroup = levels.get(o.getLevel().getMinLevel());
					TreeItem<AbstractTreeType> classItem = new TreeItem<AbstractTreeType>(new InstanceTool(o, p -> fmmlxDiagram.setNodeCreationType(o.getPath())));
					levelGroup.getChildren().add(classItem);
				}
			}
			levelSet.clear();
			relationships.setExpanded(true);
			elements.setExpanded(true);
		});
	}

	public TreeView getToolBar() {
		return treeView;
	}

	private abstract class AbstractTreeType {
		final Action action;

		protected abstract Node getIcon();

		protected abstract int getLevel();
		
		protected AbstractTreeType(Action action) {this.action = action;}
	}

	private class TreeGroup extends AbstractTreeType {
		final String name;

		private TreeGroup(String name) {
			super(point -> {});
			this.name = name;
		}

		public String toString() {
			return name;
		}

		@Override
		protected Node getIcon() {
			return null;
		}

		protected int getLevel() {
			return 0;
		}

	}

	private class InstanceTool extends AbstractTreeType {
		final FmmlxObject object;

		public InstanceTool(FmmlxObject object, Action action) {
			super(action);
			this.object = object;
//			this.action = p -> fmmlxDiagram.setNodeCreationType(object.getPath());
		}

		public String toString() {
			
			if (showMetaClassName==true){
			return object.getName() + " ^" + object.getMetaClassName() + "^";
		    } else {
			return object.getName();
			}
		}

		@Override
		protected Node getIcon() {
			return object.getConcreteSyntaxIcon(48);
		}

		protected int getLevel() {
			return object.getLevel().getMinLevel();
		}

	}

	private class DefaultTool extends AbstractTreeType {

		String name;
		ImageView icon;

		public DefaultTool(String name, String pathToIcon, Action action) {
			super(action);
			this.name = name;
			this.icon = new ImageView(new javafx.scene.image.Image(new File(pathToIcon).toURI().toString()));
		}

		public String toString() {
			return name;
		}

		@Override
		protected Node getIcon() {
			return icon;
		}

		protected int getLevel() {
			if (name.equals("MetaClass"))
				return 1000;
			return 0;
		}

	}

	private interface Action {
		void perform(Point2D point);
	}
	
	public boolean isShowMetaClassName() {
		return showMetaClassName;
	}

	public void setShowMetaClassName(boolean showMetaClassName) {
		this.showMetaClassName = showMetaClassName;
	}

}
