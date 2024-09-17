package tool.clients.forms;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import tool.clients.Client;
import tool.clients.EventHandler;
import tool.helper.user_properties.PropertyManager;
import tool.xmodeler.XModeler;
import xos.Message;
import xos.Value;

public class FormsClient extends Client {

	public FormsClient() {
		super("com.ceteva.forms");
		
		setDebug(true);
		theClient = this;
	}

	public static void start(TabPane tabFolder) {
		FormsClient.tabFolder = tabFolder;
	}

	public static final int HIGH_RESOLUTION_FACTOR_OLD = 2;

	public static FormsClient theClient() {
		return theClient;
	}

	static FormsClient theClient;
	static TabPane tabFolder;

	static Hashtable<String, Tab> tabs = new Hashtable<String, Tab>();
	static Vector<Form> forms = new Vector<Form>();
	static Hashtable<String, FormTools> toolDefs = new Hashtable<String, FormTools>();

	private void addComboItem(Message message) {
		String parentId = message.args[0].strValue();
		String value = message.args[1].strValue();
		addComboItem(parentId, value);
	}

	private void addComboItem(final String parentId, final String value) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.addComboItem(parentId, value);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void addItem(Message message) {
		if (message.arity == 2)
			addComboItem(message);
		else
			addListItem(message);
	}

	private void addListItem(Message message) {
		String parentId = message.args[0].strValue();
		String id = message.args[1].strValue();
		String value = message.args[2].strValue();
		addListItem(parentId, id, value);
	}

	private void addListItem(final String parentId, final String id, final String value) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.addListItem(parentId, id, value);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void addNodeWithIcon(Message message) {
		String parentId = message.args[0].strValue();
		String nodeId = message.args[1].strValue();
		String text = message.args[2].strValue();
		boolean editable = message.args[3].boolValue;
		String icon = message.args[4].strValue();
		int index = -1;
		if (message.arity == 6)
			index = message.args[5].intValue;
		addNodeWithIcon(parentId, nodeId, text, editable, icon, index);
	}

	private void addNodeWithIcon(final String parentId, final String nodeId, final String text, final boolean editable,
			final String icon, final int index) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.newNodeWithIcon(parentId, nodeId, text, editable, icon, index);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Value callMessage(Message message) {
		if (message.hasName("getText"))
			return getText(message);
		else if (message.hasName("getTextDimension"))
			throw new RuntimeException("The method getTextDimension(message) was removed.");
//        return getTextDimension(message);
		else
			return super.callMessage(message);
	}

	private Value getText(Message message) {
		final String id = message.args[0].strValue();
		final String[] text = new String[] { "" };
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms) {
				String textIn = form.getText(id);
				if (textIn != null)
					text[0] = textIn;
			}
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Value(text[0]);
	}

	private void clearForm(Message message) {
		String id = message.args[0].strValue();
		final Form form = getForm(id);
		if (form != null) {
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				form.clear();
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else
			System.err.println("cannot find form to clear " + id);
	}

	private Form getForm(String id) {
		for (Form form : forms)
			if (form.getId().equals(id))
				return form;
		return null;
	}

	private FormTools getFormTools(String id) {
		if (toolDefs.containsKey(id))
			return toolDefs.get(id);
		else {
			FormTools formTools = new FormTools(id);
			toolDefs.put(id, formTools);
			return formTools;
		}
	}

	private void inflateButton(String parentId, Node button) {
		String id = XModeler.attributeValue(button, "id");
		String text = XModeler.attributeValue(button, "text");
		newButton(parentId, id, text);
	}

	private void inflateCheck(String parentId, Node check) {
		String id = XModeler.attributeValue(check, "id");
		boolean checked = XModeler.attributeValue(check, "checked").equals("true");
		String labelText = XModeler.attributeValue(check, "label");
		newCheckBox(parentId, id, checked, labelText);
	}

	private void inflateCombo(String parentId, Node combo) {
		String id = XModeler.attributeValue(combo, "id");
		newComboBox(parentId, id);
		NodeList items = combo.getChildNodes();
		for (int i = 0; i < items.getLength(); i++) {
			Node item = items.item(i);
			addComboItem(id, XModeler.attributeValue(item, "item"));
		}
	}

	private void inflateForm(Node form) {
		String id = XModeler.attributeValue(form, "id");
		String label = XModeler.attributeValue(form, "label");
		boolean selected = XModeler.attributeValue(form, "selected").equals("true");
		newForm(id, label, selected);
		NodeList elements = form.getChildNodes();
		for (int i = 0; i < elements.getLength(); i++)
			inflateFormElement(id, elements.item(i));
	}

	private void inflateFormClientElement(Node element) {
		if (element.getNodeName().equals("Form"))
			inflateForm(element);
		if (element.getNodeName().equals("FormTools"))
			inflateFormTools(element);
	}

	private void inflateFormElement(String parentId, Node element) {
		if (element.getNodeName().equals("TextField"))
			inflateTextField(parentId, element);
		else if (element.getNodeName().equals("Label"))
			inflateLabel(parentId, element);
		else if (element.getNodeName().equals("TextBox"))
			inflateTextBox(parentId, element);
		else if (element.getNodeName().equals("Combo"))
			inflateCombo(parentId, element);
		else if (element.getNodeName().equals("Check"))
			inflateCheck(parentId, element);
		else if (element.getNodeName().equals("Button"))
			inflateButton(parentId, element);
		else if (element.getNodeName().equals("Tree"))
			inflateTree(parentId, element);
		else if (element.getNodeName().equals("List"))
			inflateList(parentId, element);
		else
			System.err.println("Unknown type of form element: " + element.getNodeName());
	}

	private void inflateFormTools(Node formTools) {
		String id = XModeler.attributeValue(formTools, "id");
		FormTools tools = getFormTools(id);
		NodeList children = formTools.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			String toolId = XModeler.attributeValue(node, "id");
			String event = XModeler.attributeValue(node, "event");
//      String icon = XModeler.attributeValue(node, "icon");
			tools.addTool(event, toolId);
		}
	}

	private void inflateLabel(String parentId, Node label) {
		String id = XModeler.attributeValue(label, "id");
		String string = XModeler.attributeValue(label, "string");
		System.out.println("InflateLabel: " + string);
		newText(parentId, id, string);
		getForm(parentId).getLabels().get(id).setText(string);
	}

	private void inflateList(String parentId, Node list) {
		String id = XModeler.attributeValue(list, "id");
		String labelText = XModeler.attributeValue(list, "label");
		newList(parentId, id, labelText);
		NodeList items = list.getChildNodes();
		for (int i = 0; i < items.getLength(); i++) {
			String itemId = XModeler.attributeValue(items.item(i), "id");
			String value = XModeler.attributeValue(items.item(i), "value");
			addListItem(parentId, itemId, value);
		}
	}

	private void inflateTextBox(String parentId, Node textBox) {
		String id = XModeler.attributeValue(textBox, "id");
		String string = XModeler.attributeValue(textBox, "string");
		boolean editable = XModeler.attributeValue(textBox, "editable").equals("true");
		String labelText = XModeler.attributeValue(textBox, "label");
		newTextBox(parentId, id, editable, labelText);
		getForm(parentId).getBoxes().get(id).setText(string);
	}

	private void inflateTextField(String parentId, Node textField) {
		String id = XModeler.attributeValue(textField, "id");
		String string = XModeler.attributeValue(textField, "string");
		boolean editable = XModeler.attributeValue(textField, "editable").equals("true");
		String labelText = XModeler.attributeValue(textField, "label");
		newTextField(parentId, id, editable, labelText);
		getForm(parentId).getTextFields().get(id).setText(string);
	}

	private void inflateTree(String parentId, Node tree) {
		String id = XModeler.attributeValue(tree, "id");
		newTree(parentId, id, true);
		inflateTreeItems(tree);
	}

	private void inflateTreeItems(Node node) {
		String id = XModeler.attributeValue(node, "id");
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String childId = XModeler.attributeValue(child, "id");
			String text = XModeler.attributeValue(child, "text");
			String image = XModeler.attributeValue(child, "image");
			boolean expanded = XModeler.attributeValue(child, "expanded").equals("true");
			addNodeWithIcon(id, childId, text, expanded, image, i);
			inflateTreeItems(child);
		}
	}

	public void inflateXML(final Document doc) {
//	CountDownLatch l = new CountDownLatch(1);
//	Platform.runLater(() ->{
		try {
			NodeList formClients = doc.getElementsByTagName("Forms");
			if (formClients.getLength() == 1) {
				Node formClient = formClients.item(0);
				NodeList forms = formClient.getChildNodes();
				for (int i = 0; i < forms.getLength(); i++) {
					Node element = forms.item(i);
					inflateFormClientElement(element);
				}
			} else
				System.err.println("expecting exactly 1 editor client got: " + formClients.getLength());
		} catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}

	private void newButton(Message message) {
		String parentId = message.args[0].strValue();
		String id = message.args[1].strValue();
		String label = message.args[2].strValue();
//		int zoom = getDeviceZoomPercent();
		newButton(parentId, id, label);
	}

	private void newButton(final String parentId, final String id, final String label) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.newButton(parentId, id, label);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void newCheckBox(Message message) {
		String parentId = message.args[0].strValue();
		String id = message.args[1].strValue();
//		int zoom = getDeviceZoomPercent();
		boolean checked = message.args[2].boolValue;
		String labelText = message.args[3].strValue();
		newCheckBox(parentId, id, checked, labelText);
	}

	private void newCheckBox(final String parentId, final String id, final boolean checked, final String labelText) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.newCheckBox(parentId, id, checked, labelText);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void newComboBox(Message message) {
		String parentId = message.args[0].strValue();
		String id = message.args[1].strValue();
//		int zoom = getDeviceZoomPercent();
		newComboBox(parentId, id);
	}

	private void newComboBox(final String parentId, final String id) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.newComboBox(parentId, id);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void newForm(Message message) {
		String id = message.args[0].strValue();
//    String type = message.args[1].strValue(); // needed?
		String label = message.args[2].strValue();
		System.err.println("*** NEW FORM *** " + id + " *** " + label + " ***");
		newForm(id, label, true);
		System.err.println("NEW FORM done");
	}

	private void newForm(final String id, final String label, final boolean selected) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			Form form = new Form(id);
			forms.add(form);
			if(PropertyManager.getProperty("formsSeparately", true)) {
				createStage(form.getView(),label,id, form);
			} else {
				createTab(form,id,label);
			}
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void createStage(javafx.scene.Node node, String name, String id, Form form) {
		Stage stage = new Stage();
		BorderPane border = new BorderPane();
		border.setCenter(node);
		Scene scene = new Scene(border, 1000, 605);
		stage.setScene(scene);
		stage.setTitle(name);
		stage.show();
		stage.setOnCloseRequest((e) -> closeStage(stage, e, id, name, node,form));
	}
	
	private void createTab(Form form, String id, String label) {
		Tab tabItem = new Tab(label);
		tabFolder.getTabs().add(tabItem);
		tabs.put(id, tabItem);
		tabItem.setContent(form.getView());
		tabItem.setOnCloseRequest((e)->closeTab(e, id, label, form));
	}
	
	private void closeStage(Stage stage, Event wevent, String id, String name, javafx.scene.Node node, Form form) {
//		Alert alert = new Alert(AlertType.CONFIRMATION);
//		ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
//		ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
//		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
//		alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
//		alert.setTitle("Open form as tab instead?");
//		alert.setHeaderText(null);
//		Optional<ButtonType> result = alert.showAndWait();
//		if (result.get().getButtonData() == ButtonData.YES) {
//			PropertyManager.setProperty("formsSeparately", "false");
//			createTab(form, id, name);
//		} else if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
//			wevent.consume();
//		} else {
			close(form);
//		}
	}
	
	private void closeTab(Event wevent, String id, String name, Form form) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		ButtonType okButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
		alert.setTitle("Open form in stage instead?");
		alert.setHeaderText(null);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get().getButtonData() == ButtonData.YES) {
			PropertyManager.setProperty("formsSeparately", "true");
			createStage(form.getView(),name,id,form);
			tabs.remove(id);
		} else if (result.get().getButtonData() == ButtonData.CANCEL_CLOSE) {
			wevent.consume();
		} else {
			close(form);
			tabs.remove(id);
		}
	}
	
	private void close(Form form) {
		forms.remove(form);
		EventHandler handler = FormsClient.theClient().getHandler();
		Message message = handler.newMessage("formClosed", 1);
		message.args[0] = new Value(form.getId());
		handler.raiseEvent(message);
	}

	private void newList(Message message) {
		String parentId = message.args[0].strValue();
		String id = message.args[1].strValue();
//		int zoom = getDeviceZoomPercent();
		String labelText = message.args[2].strValue();
		newList(parentId, id, labelText);
	}

	private void newList(String parentId, String id, String labelText) {
		for (Form form : forms)
			form.newList(parentId, id, labelText);
	}

	private void newText(Message message) {
		String parentId = message.args[0].strValue();
		String id = message.args[1].strValue();
		String string = message.args[2].strValue();
		newText(parentId, id, string);
	}

	private void newText(final String parentId, final String id, final String string) {
		final Form form = getForm(parentId);
		if (form != null) {
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				form.newText(id, string);
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else
			System.err.println("cannot find text parent " + parentId);
	}

	private void newTextBox(Message message) {
		String parentId = message.args
				[0].strValue();
		String id = message.args[1].strValue();
//		int zoom = getDeviceZoomPercent();
		boolean editable = message.args[2].boolValue;
		String labelText = message.args[3].strValue();
		newTextBox(parentId, id, editable, labelText);
	}

	private void newTextBox(final String parentId, final String id, final boolean editable, final String labelText) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.newTextBox(parentId, id, editable, labelText);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void newTextField(Message message) {
		String parentId = message.args[0].strValue();
		String id = message.args[1].strValue();
//		int zoom = getDeviceZoomPercent();
		boolean editable = message.args[2].boolValue;
		String labelText = message.args[3].strValue();
		newTextField(parentId, id, editable, labelText);
	}

	private void newTextField(final String parentId, final String id, final boolean editable, final String labelText) {
		final Form form = getForm(parentId);
		if (form != null) {
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				form.newTextField(id, editable, labelText);
				l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else
			System.err.println("cannot find text field parent " + parentId);
	}

	private void newTree(Message message) {
		String parentId = message.args[0].strValue();
		String id = message.args[1].strValue();
//		int zoom = getDeviceZoomPercent();
		boolean editable = message.args[6].boolValue;
		newTree(parentId, id, editable);
	}

	private void newTree(final String parentId, final String id, final boolean editable) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.newTree(parentId, id, editable);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean processMessage(Message message) {
		return false;
	}

	private void selectForm(final String id) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			if (tabs.containsKey(id)) {
				Tab tab = tabs.get(id);
				if (!tabFolder.getTabs().contains(tab)) {
					tabFolder.getTabs().add(tab);
				}
				tabFolder.getSelectionModel().select(tabs.get(id));
			}
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void removeNode(Message message) {
		final String id = message.args[0].strValue();
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms) {
				form.removeItem(id);
			}
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void maximiseToCanvas(Message message) {
		final String id = message.args[0].strValue();
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.maximiseToCanvas(id);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void changesMade(Message message) {
		final String id = message.args[0].strValue();
		final boolean made = message.args[1].boolValue;
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.changesMade(id, made);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(final Message message) {
		System.err.println("***FORMMESSAGE: " + message);
		if (message.hasName("newForm"))
			newForm(message);
		else if (message.hasName("setTool"))
			setTool(message);
		else if (message.hasName("newText")) {
			newText(message);
			String string = message.args[2].strValue();
			if (string.length() < 150) {
				System.err.println("####### MESSAGE TO FORM CLIENT FOR EMPTY LABEL: " + string);
			}
		} else if (message.hasName("setText"))
			setText(message);
		else if (message.hasName("newTextField"))
			newTextField(message);
		else if (message.hasName("clearForm"))
			clearForm(message);
		else if (message.hasName("newList"))
			newList(message);
		else if (message.hasName("addItem"))
			addItem(message);
		else if (message.hasName("newTextBox"))
			newTextBox(message);
		else if (message.hasName("newComboBox"))
			newComboBox(message);
		else if (message.hasName("setSelection"))
			setSelection(message);
		else if (message.hasName("newCheckBox"))
			newCheckBox(message);
		else if (message.hasName("newButton"))
			newButton(message);
		else if (message.hasName("newTree"))
			newTree(message);
		else if (message.hasName("addNodeWithIcon"))
			addNodeWithIcon(message);
		else if (message.hasName("setVisible"))
			setVisible(message);
		else if (message.hasName("clear"))
			clear(message);
		else if (message.hasName("check"))
			check(message);
		else if (message.hasName("uncheck"))
			uncheck(message);
		else if (message.hasName("removeNode"))
			removeNode(message);
		else if (message.hasName("maximiseToCanvas"))
			maximiseToCanvas(message);
		else if (message.hasName("changesMade"))
			changesMade(message);
		else if (message.hasName("move"))
			move(message);
		else if (message.hasName("setSize"))
			setSize(message);
		else if (message.hasName("delete"))
			delete(message);
		else {
			super.sendMessage(message);
		}
	}

	private void move(Message message) {
		Value id = message.args[0];
		Value x = message.args[1];
		Value y = message.args[2];
		for (Form form : forms)
			form.move(id.strValue(), x.intValue, y.intValue);
	}

	private void setSize(Message message) {
		final Value id = message.args[0];
		final Value width = message.args[1];
		final Value height = message.args[2];
		for (Form form : forms)
			form.setSize(id.strValue(), width.intValue, height.intValue); //TODO: java.lang.RuntimeException: The setSize()-operation for this type of Display is not yet implemented...
	}

	private void delete(Message message) {
		final Value id = message.args[0];
		for (Form form : forms)
			form.delete(id.strValue());
	}

	private void check(Message message) {
		String id = message.args[0].strValue();
		check(id);
	}

	private void check(final String id) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.check(id);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void uncheck(final String id) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.uncheck(id);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void uncheck(Message message) {
		String id = message.args[0].strValue();
		uncheck(id);
	}

	private void clear(Message message) {
		String id = message.args[0].strValue();
		clear(id);
	}

	private void clear(final String id) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms) {
				form.clear(id);
			}
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setSelection(Message message) {
		String comboId = message.args[0].strValue();
		int index = message.args[1].intValue;
		setSelection(comboId, index);
	}

	private void setSelection(final String comboId, final int index) {
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms)
				form.setSelection(comboId, index);
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setText(Message message) {
		final Value id = message.args[0];
		final Value text = message.args[1];
		CountDownLatch l = new CountDownLatch(1);
		Platform.runLater(() -> {
			for (Form form : forms) {
				form.setText(id.strValue(), text.strValue());
			}
			l.countDown();
		});
		try {
			l.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setTool(Message message) {
		System.err.println("setTool: " + message);
		Value id = message.args[0];
		Value name = message.args[1];
		Value enabled = message.args[2];
		if (tabs.containsKey(id.strValue())) {
			FormTools formTools = getFormTools(id.strValue());
			formTools.setTools(name.strValue(), id.strValue(), enabled.boolValue);
		} else
			System.err.println("cannot find form " + id);
	}

	private void setVisible(Message message) {
		String id = message.args[0].strValue();
		selectForm(id);
	}

	public void toolItemEvent(String event, String id, boolean enabled) {
		Message m = null;
		if (event.equals("lockForm")) {
			m = getHandler().newMessage(event, 2);
			Value v = new Value(id);
			m.args[0] = v;
			Value enabled_value = new Value(!enabled);
			m.args[1] = enabled_value;
		} else {
			m = getHandler().newMessage(event, 1);
			Value v = new Value(id);
			m.args[0] = v;
		}
		getHandler().raiseEvent(m);
	}

	public void toolItemEvent(String event, String id) {
		toolItemEvent(event, id, false);
	}

	public void writeXML(PrintStream out) {
		out.print("<Forms>");
		for (Form form : forms)
			form.writeXML(out, tabFolder.getSelectionModel().getSelectedItem() == tabs.get(form.getId()),
					tabs.get(form.getId()).getText());
		for (FormTools tools : toolDefs.values())
			tools.writeXML(out);
		out.print("</Forms>");
	}

	public void doubleClick(String id) {
		EventHandler handler = getHandler();
		Message message = handler.newMessage("doubleSelected", 1);
		message.args[0] = new Value(id);
		handler.raiseEvent(message);
	}
}