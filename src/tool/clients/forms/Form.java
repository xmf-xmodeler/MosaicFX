package tool.clients.forms;

import java.io.File;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import tool.clients.EventHandler;
import tool.clients.menus.MenuClient;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class Form {

	private String id;

	private ScrollPane form;
	private GridPane gridTextFields;
	private GridPane gridBoxes;
	private FlowPane root;

	private int rowLeft;
	private int rowRight;
	private int gridWidth = 150;

	private Hashtable<String, FormTextField> textFields = new Hashtable<String, FormTextField>();
	private Hashtable<String, Label> labels = new Hashtable<String, Label>();
	private Hashtable<String, List> lists = new Hashtable<String, List>();
	private Hashtable<String, TextArea> boxes = new Hashtable<String, TextArea>();
	private Hashtable<String, ComboBox<String>> combos = new Hashtable<String, ComboBox<String>>();
	private Hashtable<String, FormCheckBox> checks = new Hashtable<String, FormCheckBox>();
	private Hashtable<String, Button> buttons = new Hashtable<String, Button>();
	private Hashtable<String, TreeView<String>> trees = new Hashtable<String, TreeView<String>>();
	private Hashtable<String, TreeItem<String>> items = new Hashtable<String, TreeItem<String>>();
	private Hashtable<String, String> images = new Hashtable<String, String>();

	public Form(Tab parent, String id) {
		System.err.println("new Form(): " + id + " on " + parent);
		this.id = id;

		form = new ScrollPane();
		root = new FlowPane();
		root.setOrientation(Orientation.HORIZONTAL);

		gridTextFields = initializeGrid(2, gridWidth);
		gridBoxes = initializeGrid(1, gridWidth * 4);

		rowLeft = 0;
		rowRight = 0;

		root.getChildren().add(gridTextFields);
		root.getChildren().add(gridBoxes);

		form.setContent(root);
		form.setFitToHeight(true);
		form.setFitToWidth(true);

		parent.setContent(form);

	}

	public GridPane initializeGrid(int columns, int maxWidth) {
		GridPane grid = new GridPane();
		grid.setHgap(3);
		grid.setVgap(3);
		grid.setPadding(new Insets(10, 0, 0, 10));

		ColumnConstraints cc;

		for (int i = 0; i < columns; i++) {
			cc = new ColumnConstraints();
			cc.setMaxWidth(maxWidth);
			grid.getColumnConstraints().add(cc);
		}

		return grid;
	}

	@Override
	public String toString() {
		return "Form [id=" + id + " " + gridTextFields.getHeight() + " of " + form.getHeight() + "]";
	}

	///////////////////////// SETUP ( ADD NEW ELEMENTS) //////////////////////////

	public void newButton(String parentId, String id, String label) {
		if (this.id.equals(parentId)) {
			Button button = new Button();

			javafx.event.EventHandler<ActionEvent> eh = new javafx.event.EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (event.getSource() instanceof Button) {
						Button es = (Button) event.getSource();
						if (button == es) {
							System.err.println("buttonPressed: " + id);
							String id = getId(button);
							EventHandler handler = FormsClient.theClient().getHandler();
							Message message = handler.newMessage("buttonPressed", 1);
							message.args[0] = new Value(id);
							handler.raiseEvent(message);
						}
					}
				}
			};

			button.setOnAction(eh);

			button.setText(label);
			buttons.put(id, button);
			gridBoxes.add(button, 0, rowRight);
			rowRight++;
		}
	}

	public void newCheckBox(String parentId, final String id, boolean checked, String labelText) {
		if (this.id.equals(parentId)) {
			final FormCheckBox checkBox = new FormCheckBox(labelText);

			javafx.event.EventHandler<ActionEvent> eh = new javafx.event.EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					if (event.getSource() instanceof CheckBox) {
						CheckBox chk = (CheckBox) event.getSource();
						if (checkBox == chk) {
							xmf_setSelection(id, chk.isSelected());
						}
					}
				}
			};

			checkBox.setOnAction(eh);
			checkBox.setSelected(checked);

			checks.put(id, checkBox);

			gridTextFields.add(checkBox.getLabel(), 0, rowLeft);
			gridTextFields.add(checkBox, 1, rowLeft);
			rowLeft++;
		}
	}

	public void newComboBox(String parentId, String id) {
		if (this.id.equals(parentId)) {
			ComboBox<String> comboBox = new ComboBox<String>();

			combos.put(id, comboBox);
			gridBoxes.add(comboBox, 0, rowRight);
			rowRight++;

			comboBox.getSelectionModel().selectedItemProperty()
					.addListener(new javafx.beans.value.ChangeListener<String>() {
						public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
							EventHandler handler = FormsClient.theClient().getHandler();
							Message message = handler.newMessage("comboBoxSelection", 2);
							message.args[0] = new Value(id);
							message.args[1] = new Value(new_val);
							handler.raiseEvent(message);
						}
					});
		}
	}

	public void newList(String parentId, final String id, final String labelText) {
		if (this.id.equals(parentId)) {

			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				List list = new List(id, gridBoxes, rowRight - 2, labelText);
				lists.put(id, list);

				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void newNodeWithIcon(String parentId, String nodeId, String text, boolean editable, String icon, int index) {
		if (trees.containsKey(parentId))
			addRootNodeWithIcon(parentId, nodeId, text, editable, false, icon, index);
		else
			addNodeWithIcon(parentId, nodeId, text, editable, false, icon, index);
	}

	public void newText(String id, String string) {
		if (string.isEmpty() || string.equals(" "))
			return;
		Label text = new Label(string);
		labels.put(id, text);
		gridBoxes.add(text, 0, rowRight);
		rowRight++;
	}

	public void newTextBox(String parentId, String id, boolean editable, String labelText) {
		if (this.id.equals(parentId)) {
			final TextArea textBox = new TextArea();
			final Label label = new Label(labelText);

			textBox.setEditable(true);
			textBox.setMaxHeight(250);

			textBox.setContextMenu(new ContextMenu()); // remove default one
			textBox.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent click) {
					if (click.getClickCount() == 1 && click.getButton() == MouseButton.SECONDARY) {
						MenuClient.popup(id, textBox, (int) click.getSceneX(), (int) click.getSceneY());
					}
				}
			});
			boxes.put(id, textBox);
			labels.put(id, label);

			gridBoxes.add(label, 0, rowRight);
			rowRight++;
			gridBoxes.add(textBox, 0, rowRight++);
			rowRight++;
		}
	}

	public void newTextField(final String id, boolean editable, String labelText) {
		final FormTextField textField = new FormTextField(labelText);
		textField.setEditable(editable);
		textField.setDisable(!editable);

		textFields.put(id, textField);

		gridTextFields.add(textField.getLabel(), 0, rowLeft);
		gridTextFields.add(textField, 1, rowLeft);
		rowLeft++;

		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean wasFocused, Boolean isFocused) {
				if (!isFocused) {
					System.out.println("Textfield out focus");
					textChangedEvent(id, textField.getText());
				}
			}
		});

		textField.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {
				if (click.getClickCount() == 2) {
					System.err.println("text.setOnMouseClicked: " + click.getClickCount());
					doubleClick(textField);
				}
			}
		});
	}

	public void newTree(String parentId, String id, boolean editable) {
		if (this.id.equals(parentId)) {
			TreeView<String> treeView = new TreeView<String>();
			treeView.setMinHeight(200);
			treeView.setMinWidth(300);

			trees.put(id, treeView);
			gridBoxes.add(treeView, 0, rowRight);
			rowRight++;

			treeView.setOnMouseClicked(new javafx.event.EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent click) {
					if (click.getClickCount() == 2 && click.getButton() == MouseButton.PRIMARY) {
						doubleClick(treeView.getSelectionModel().getSelectedItem());
					} else if (click.getClickCount() == 1 && click.getButton() == MouseButton.SECONDARY) {
						TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
						if (item == null)
							return;
						String itemId = getId(item);
						if (itemId == null)
							return;
						MenuClient.popup(itemId, treeView, (int) click.getSceneX(), (int) click.getSceneY());
					}
				}
			});
		}

	}

	public void addComboItem(String comboId, String value) {
		if (combos.containsKey(comboId)) {
			combos.get(comboId).getItems().add(value);
		}
	}

	public void addListItem(String parentId, String id, String value) {
		for (String listId : lists.keySet()) {
			if (listId.equals(parentId))
				lists.get(listId).add(id, value);
		}
	}

	private void addNodeWithIcon(final String parentId, final String nodeId, final String text, boolean editable,
			final boolean expanded, final String icon, final int index) {
		if (items.containsKey(parentId)) {
			TreeItem<String> parent = items.get(parentId);
//			System.out.println(icon);
//			String iconFile = "icons/" + icon;
			ImageView image = new ImageView(new Image(new File(icon).toURI().toString()));
			TreeItem<String> item = new TreeItem<String>(text, image);
			images.put(nodeId, icon);
			items.put(nodeId, item);
			item.setExpanded(expanded);
			parent.getChildren().add((index == -1) ? parent.getChildren().size() : index, item);

		}
	}

	private void addRootNodeWithIcon(final String parentId, final String nodeId, final String text, boolean editable,
			final boolean expanded, final String icon, final int index) {
//		String iconFile = "icons/" + icon;
//		System.out.println(icon);
		ImageView image = new ImageView(new Image(new File(icon).toURI().toString()));
		TreeView<String> tree = trees.get(parentId);
		TreeItem<String> item = new TreeItem<String>(text, image);
		images.put(nodeId, icon);
		items.put(nodeId, item);
		item.setExpanded(expanded);
		tree.setRoot(item);
	}

	///////////////////////// UI ELEMENT VALUE SETTERS //////////////////////////

	public void check(String id) {
		for (String bid : checks.keySet())
			if (bid.equals(id) && !checks.get(id).isSelected())
				checks.get(id).setSelected(true);
	}

	public void uncheck(String id) {
		for (String bid : checks.keySet())
			if (bid.equals(id) && checks.get(id).isSelected())
				checks.get(id).setSelected(false);
	}

	public void setSelection(String comboId, int index) {
		for (String id : combos.keySet()) {
			if (id.equals(comboId)) {
				combos.get(id).getSelectionModel().select(index);
			}
		}
	}

	public void setText(String id, String string) {
		if (textFields.containsKey(id)) {
			TextField text = textFields.get(id);
			text.setText(string);
		}
		if (boxes.containsKey(id)) {
			TextArea text = boxes.get(id);
			text.setText(string);
		}
		if (items.containsKey(id)) {
			TreeItem<String> item = items.get(id);
			item.setValue(string);
		}
	}

	///////////////////////// OTHER STUFF //////////////////////////

	public void clear() {
		System.err.println("# # # clear all");
		labels.clear();
		textFields.clear();
		lists.clear();
		boxes.clear();
		combos.clear();
		checks.clear();
		buttons.clear();
		trees.clear();
		items.clear();
		images.clear();
		rowLeft = 0;
		rowRight = 0;

		gridBoxes.getChildren().clear();
		gridTextFields.getChildren().clear();
	}

	public void clear(String id) {
		if (getId().equals(id))
			clear();
		else {
			if (lists.containsKey(id)) {
				List l = lists.get(id);
				l.clear();
			}
		}
	}

	private void iHaventImplementedItYet() {
		new RuntimeException("I haven't implemented that yet:").printStackTrace();
	}

	private void doubleClick(TreeItem<String> item) {
		String id = getId(item);
		Message m = FormsClient.theClient().getHandler().newMessage("doubleSelected", 1);
		m.args[0] = new Value(id);
		FormsClient.theClient().getHandler().raiseEvent(m);
	}

	private void doubleClick(TextField item) {
		String id = getId(item);
		Message m = FormsClient.theClient().getHandler().newMessage("doubleSelected", 1);
		try {
			m.args[0] = new Value(id);
			FormsClient.theClient().getHandler().raiseEvent(m);
		} catch (Exception e) {
			System.err.println("Double click into nowhere detected...");
		}
	}

	public Hashtable<String, TextArea> getBoxes() {
		return boxes;
	}

	public Hashtable<String, Button> getButtons() {
		return buttons;
	}

	public Hashtable<String, FormCheckBox> getChecks() {
		return checks;
	}

	public Hashtable<String, ComboBox<String>> getCombos() {
		return combos;
	}

	public String getId() {
		return id;
	}

	private String getId(Button b) {
		for (String id : buttons.keySet())
			if (buttons.get(id) == b)
				return id;
		return null;
	}

	private String getId(TextField item) {
		for (String id : textFields.keySet())
			if (textFields.get(id) == item)
				return id;
		return null;
	}

	private String getId(TreeItem<String> item) {
		for (String id : items.keySet())
			if (items.get(id) == item)
				return id;
		return null;
	}

	public Hashtable<String, TreeItem<String>> getItems() {
		return items;
	}

	public Hashtable<String, Label> getLabels() {
		return labels;
	}

	public Hashtable<String, List> getLists() {
		return lists;
	}

	public Hashtable<String, FormTextField> getTextFields() {
		return textFields;
	}

	public Hashtable<String, TreeView<String>> getTrees() {
		return trees;
	}

	public void move(String id, int x, int y) {

		if (combos.containsKey(id)) {
			AnchorPane.setLeftAnchor(combos.get(id), 1. * x);
			AnchorPane.setTopAnchor(combos.get(id), 1. * y);
			return;
		}
		if (textFields.containsKey(id)) {
			AnchorPane.setLeftAnchor(textFields.get(id), 1. * x);
			AnchorPane.setTopAnchor(textFields.get(id), 1. * y);
			return;
		}
		if (labels.containsKey(id)) {
			AnchorPane.setLeftAnchor(labels.get(id), 1. * x);
			AnchorPane.setTopAnchor(labels.get(id), 1. * y);
			return;
		}
		if (checks.containsKey(id)) {
			AnchorPane.setLeftAnchor(checks.get(id), 1. * x);
			AnchorPane.setTopAnchor(checks.get(id), 1. * y);
			return;
		}
		if (buttons.containsKey(id)) {
			AnchorPane.setLeftAnchor(buttons.get(id), 1. * x);
			AnchorPane.setTopAnchor(buttons.get(id), 1. * y);
			return;
		}
		if (boxes.containsKey(id)) {
			AnchorPane.setLeftAnchor(boxes.get(id), 1. * x);
			AnchorPane.setTopAnchor(boxes.get(id), 1. * y);
			return;
		}
		if (trees.containsKey(id)) {
			AnchorPane.setLeftAnchor(trees.get(id), 1. * x);
			AnchorPane.setTopAnchor(trees.get(id), 1. * y);
			return;
		}
		if (lists.containsKey(id))
			throw new RuntimeException("The move()-operation for List is not yet implemented...");
		if (items.containsKey(id))
			throw new RuntimeException("The move()-operation for TreeItem is not yet implemented...");
		if (images.containsKey(id))
			throw new RuntimeException("The move()-operation for String/Image is not yet implemented...");
		System.err.println("move: " + id);
	}

	public void setSize(String id, double width, double height) {
		if (combos.containsKey(id)) {
			combos.get(id).setPrefSize(width, height);
			return;
		}
		if (textFields.containsKey(id)) {
			textFields.get(id).setPrefSize(width, height);
			return;
		}
		if (labels.containsKey(id)) {
			labels.get(id).setPrefSize(width, height);
			return;
		}
		if (checks.containsKey(id)) {
			checks.get(id).setPrefSize(width, height);
			return;
		}
		if (buttons.containsKey(id)) {
			buttons.get(id).setPrefSize(width, height);
			return;
		}
		if (boxes.containsKey(id)) {
			boxes.get(id).setPrefSize(width, height);
			return;
		}
		if (trees.containsKey(id)) {
			trees.get(id).setPrefSize(width, height);
			return;
		}
		if (lists.containsKey(id))
			throw new RuntimeException("The setSize()-operation for List is not yet implemented...");
		if (items.containsKey(id))
			throw new RuntimeException("The setSize()-operation for TreeItem is not yet implemented...");
		if (images.containsKey(id))
			throw new RuntimeException("The setSize()-operation for String/Image is not yet implemented...");
		throw new RuntimeException("The setSize()-operation for this type of Display is not yet implemented...");
	}

	private void select(String id) {
		EventHandler handler = FormsClient.theClient().getHandler();
		Message m = FormsClient.theClient().getHandler().newMessage("selected", 1);
		m.args[0] = new Value(id);
		handler.raiseEvent(m);
	}

	public void maximiseToCanvas(String id) {
		TreeView<String> tree = trees.get(id);
		if (tree != null) {
//      org.eclipse.swt.graphics.Point parentSize = tree.getParent().w
//      tree.setSize(parentSize);
			double GAP = 2.;
			AnchorPane.setLeftAnchor(tree, GAP);
			AnchorPane.setTopAnchor(tree, GAP);
			AnchorPane.setRightAnchor(tree, GAP);
			AnchorPane.setBottomAnchor(tree, GAP);

		}
	}

//
	public void changesMade(String id, boolean made) {
		System.err.println("changesMade//iHaventImplementedItYet();");
	}

	private void xmf_setSelection(String id, boolean state) {
		EventHandler handler = FormsClient.theClient().getHandler();
		Message message = handler.newMessage("setBoolean", 2);
		message.args[0] = new Value(id);
		message.args[1] = new Value(state);
		handler.raiseEvent(message);
	}

	public void textChangedEvent(String id, String text) {
		Message message = FormsClient.theClient().getHandler().newMessage("textChanged", 2);
		message.args[0] = new Value(id);
		message.args[1] = new Value(text);
		FormsClient.theClient().getHandler().raiseEvent(message);
	}

	public void writeXML(PrintStream out, boolean selected, String formLabel) {
//	  iHaventImplementedItYet();
		out.print("<Form id='" + getId() + "' selected='" + selected + "' label='"
				+ XModeler.encodeXmlAttribute(formLabel) + "'>");
		for (String id : textFields.keySet()) {
			FormTextField field = textFields.get(id);
			out.print("<TextField id='" + id + "'");
			out.print(" string='" + XModeler.encodeXmlAttribute(field.getText()) + "'");
			out.print(" label='" + XModeler.encodeXmlAttribute(field.getLabel().getText()) + "'");
			out.print(" editable='" + field.isEditable() + "'/>");
		}
		for (String id : labels.keySet()) {
			Label label = labels.get(id);
			out.print("<Label id='" + id + "'");
			out.print(" string='" + XModeler.encodeXmlAttribute(label.getText()) + "'/>");
		}
		for (List list : lists.values())
			list.writeXML(out);
		out.print("</Form>");
		for (String id : boxes.keySet()) {
			TextArea box = boxes.get(id);
			out.print("<TextBox id='" + id + "'");
			out.print(" string='" + XModeler.encodeXmlAttribute(box.getText()) + "'");
			out.print(" editable='" + box.isEditable() + "'/>");
		}
		for (String id : combos.keySet()) {
			ComboBox<String> combo = combos.get(id);
			out.print("<Combo id='" + id + "'");
			out.print(" string='" + XModeler.encodeXmlAttribute(combo.getValue()) + "'");
			out.print(" editable='" + combo.isEditable() + "'>");
			for (String value : combo.getItems())
				out.print("<Item item='" + XModeler.encodeXmlAttribute(value) + "'/>");
			out.print("</Combo>");
		}
		for (String id : checks.keySet()) {
			FormCheckBox checkBox = checks.get(id);
			out.print("<Check id='" + id + "'");
			out.print(" checked='" + checkBox.isSelected() + "'");
			out.print(" label='" + checkBox.getLabel().getText() + "'");
			out.print(" text='" + XModeler.encodeXmlAttribute(checks.get(id).getText()) + "'/>");
		}
		for (String id : buttons.keySet()) {
			out.print("<Button id='" + id + "'");
			out.print(" text='" + XModeler.encodeXmlAttribute(buttons.get(id).getText()) + "'/>");
		}
		for (String id : trees.keySet()) {
			TreeView<String> tree = trees.get(id);
			out.print("<Tree id='" + id + "'");
			out.print(" editable='true'>");
			writeXMLTreeItem(tree.getRoot(), out);
			out.print("</Tree>");
		}
	}

	private void writeXMLTreeItem(TreeItem<String> item, PrintStream out) {

//	    for (TreeItem item : children) {
		String id = null;
		for (String itemId : items.keySet()) {
			if (items.get(itemId) == item)
				id = itemId;
		}
		if (id == null)
			System.err.println("error: cannot find tree item " + item);
		String icon = images.get(id);
		out.print("<Item id='" + id + "' " + "text='" + XModeler.encodeXmlAttribute(item.getValue()) + "' " + "image='"
				+ icon + "' " + "expanded='" + item.isExpanded() + "'>");
		for (TreeItem<String> child : item.getChildren()) {
			writeXMLTreeItem(child, out);
		}
		out.print("</Item>");
//	    }
	}

	public String getText(String id) {
		if (boxes.containsKey(id))
			return boxes.get(id).getText();
		else
			return null;
	}

	public void removeItem(String id) {
		iHaventImplementedItYet();
	}

	public void delete(String id) {
		iHaventImplementedItYet();
//    FormsClient.theClient().runOnDisplay(new Runnable() {
//      public void run() {
//        if (combos.containsKey(id)) {
//          combos.get(id).dispose();
//          combos.remove(id);
//        } else if (textFields.containsKey(id)) {
//          textFields.get(id).dispose();
//          textFields.remove(id);
//        } else if (labels.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (checks.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (buttons.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (boxes.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (trees.containsKey(id)) {
//          labels.get(id).dispose();
//          labels.remove(id);
//        } else if (lists.containsKey(id)) {
//          lists.get(id).list.dispose();
//          lists.remove(id);
//        } else if (items.containsKey(id)) {
//          items.get(id).dispose();
//          items.remove(id);
//        } else if (images.containsKey(id)) {
//          images.remove(id);
//        } else System.err.println("Cannot delete " + id);
//      }
//    });
	}
}