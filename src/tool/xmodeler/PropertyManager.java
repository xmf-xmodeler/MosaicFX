package tool.xmodeler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tool.clients.menus.MenuClient;
import tool.helper.IconGenerator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class PropertyManager {

	//settings
	static String filePath;
	static Properties properties;

	//ui
	Stage stage;
	GridPane generalGrid;
	GridPane debugGrid;

	public PropertyManager(String filePath) {
		PropertyManager.filePath = filePath;
		PropertyManager.properties = new Properties();

		loadProperties();
	}

	private void loadProperties() {
		try {
			properties.load(new FileInputStream(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void storeProperties() {
        setXmfDebugging();
		try {
			properties.store(new FileOutputStream(filePath), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//public

	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
		storeProperties();
	}

	public static String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static int getProperty(String key, int defaultValue) {
		return Integer.parseInt(properties.getProperty(key, defaultValue+""));
	}

	public static boolean getProperty(String key, boolean defaultValue) {
		return Boolean.parseBoolean(properties.getProperty(key, defaultValue+""));
	}

	// GUI

	public void getUserInterface() {
		try {
			//init gridpanes
			generalGrid = getGridpane();
			debugGrid = getGridpane();

			//fill gridpanes
			fillPropGrid();
			fillDebugGrid();

			//labels
			Label generalLabel = new Label("General:");
			Label debugLabel = new Label("Debug:");

			//control buttons
			HBox buttons = getControlButtons();

			//merge into mainVbox
			VBox gridPanes = new VBox(generalLabel, generalGrid, debugLabel, debugGrid);
			gridPanes.setPadding(new Insets(10));
			VBox mainVbox = new VBox(new ScrollPane(gridPanes), buttons);
			mainVbox.setPadding(new Insets(10));
			mainVbox.setSpacing(10);

			// init scene
			initScene(mainVbox);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private HBox getControlButtons() {
		Button btnCancel = new Button("Cancel");
		btnCancel.setOnAction(this::onCancelButtonClicked);

		Button btnSave = new Button("Save");
		btnSave.setOnAction(this::onSaveButtonClicked);

		//layout
		HBox buttons = new HBox(btnCancel, btnSave);
		buttons.setSpacing(10);
		buttons.setAlignment(Pos.CENTER_RIGHT);
		return buttons;
	}

	private void onCancelButtonClicked(ActionEvent actionEvent) {
		stage.close();
	}

	private void onSaveButtonClicked(ActionEvent actionEvent) {
		parseGridPane(generalGrid);
		parseGridPane(debugGrid);
		stage.close();
	}

	private void parseGridPane(GridPane gridPane) {
		//parse rows and col into array
		Node[][] gridPaneNodes = getGridPaneRows(gridPane);

		//loop through all rows
		for (int row = 0; row < getGridLength(gridPane); row++) { //height
			// get key label
			String key = ((Label) gridPaneNodes[0][row]).getText();
			// get value field
			String value = getNodeValue(gridPaneNodes[1][row]);
			System.out.println(("key: " + key + ", val: " + value));
			if (value.isEmpty()) properties.remove(key);
			else properties.setProperty(key, value);
		}

		storeProperties();
	}

	private String getNodeValue(Node node) {
		String value = "";
		if (node instanceof TextField) {
			value = ((TextField) node).getText();
		}
		if (node instanceof CheckBox) {
			value = String.valueOf(((CheckBox) node).isSelected());
		}
		return value;
	}

	private Node[][] getGridPaneRows(GridPane gridPane) {
		Node[][] gridPaneNodes = new Node[2][getGridLength(gridPane)]; //width, height
		for (Node child : gridPane.getChildren()) {
			Integer column = GridPane.getColumnIndex(child);
			Integer row = GridPane.getRowIndex(child);
			if (column != null && row != null) {
				gridPaneNodes[column][row] = child;
			}
		}
		return gridPaneNodes;
	}

	private GridPane getGridpane() {
		GridPane gridPane = new GridPane();
		gridPane.setPadding(new Insets(10, 0, 10, 0));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		return gridPane;
	}

	private void initScene(VBox vb) {
		if (stage != null) stage.close();

		Scene scene = new Scene(vb);
		stage = new Stage();
		stage.setTitle("Preferences");
		stage.getIcons().add(IconGenerator.getImage("shell/mosaic32"));
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}

	private void fillPropGrid() {
		addRow(generalGrid, "TOOL_X", getProperty("TOOL_X", 100));
		addRow(generalGrid, "TOOL_Y", getProperty("TOOL_X", 100));
		addRow(generalGrid, "TOOL_WIDTH",  getProperty("TOOL_WIDTH", 1200));
		addRow(generalGrid, "TOOL_HEIGHT", getProperty("TOOL_HEIGHT", 900));
		addRow(generalGrid, "fileDialogPath", getProperty("fileDialogPath", ""));
	}

	private void fillDebugGrid() {
		addRow(debugGrid, "MONITOR_CLIENT_COMMUNICATION", getProperty("MONITOR_CLIENT_COMMUNICATION", false));
		addRow(debugGrid, "MONITOR_DAEMON_FIRING", getProperty("MONITOR_DAEMON_FIRING", false));
		addRow(debugGrid, "IGNORE_SAVE_IMAGE", getProperty("IGNORE_SAVE_IMAGE", false));
		addRow(debugGrid, "LOG_XMF_OUTPUT", getProperty("LOG_XMF_OUTPUT", false));
		addRow(debugGrid, "MONITOR_CALLS", "Open", actionEvent -> {
			MenuClient.openCallMonitor();
		});
		addRow(debugGrid, "PERFORMANCE_MONITOR", "Open", actionEvent -> {
			MenuClient.openPerformanceMonitor();
		});
	}

	private GridPane addRow(GridPane pane, String key, String value) {
		pane.addRow(getGridLength(pane), getKeyLabel(key), getValueTextField(value));
		return pane;
	}

	private GridPane addRow(GridPane pane, String key, int value) {
		pane.addRow(getGridLength(pane), getKeyLabel(key), getValueIntField(value));
		return pane;
	}

	private GridPane addRow(GridPane pane, String key, boolean value) {
		pane.addRow(getGridLength(pane), getKeyLabel(key), getValueCheckBox(value));
		return pane;
	}

	private GridPane addRow(GridPane pane, String key, String buttonText, EventHandler<ActionEvent> onAction) {
		pane.addRow(getGridLength(pane), getKeyLabel(key), getButton(buttonText, onAction));
		return pane;
	}

	private Label getKeyLabel(String value) {
		return new Label(value);
	}

	private TextField getValueTextField(String value) {
		return new TextField(value);
	}

	private TextField getValueIntField(int value) {
		TextField textField = new TextField(value + "");

		// force the field to accept int only
		textField.textProperty().addListener((observableValue, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				textField.setText(newValue.replaceAll("[^\\d]", ""));
			}
		});

		return textField;
	}

	private CheckBox getValueCheckBox(boolean value) {
		CheckBox checkBox = new CheckBox("");
		checkBox.setSelected(value);
		return checkBox;
	}

	private Button getButton(String text, EventHandler<ActionEvent> onAction) {
		Button button = new Button(text);
		button.setOnAction(onAction);
		return button;
	}

	private int getGridLength(GridPane gridPane) { //TODO: Reimplementing. Does not work for JAVA 14!
		int rows = 0;
		try {
			Method method = gridPane.getClass().getDeclaredMethod("getNumberOfRows");
			method.setAccessible(true);
			rows = (Integer) method.invoke(gridPane);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rows;
	}

    //set xmf debugging values
    private static void setXmfDebugging() {
        MenuClient.setClientCommunicationMonitoring(getProperty("MONITOR_CLIENT_COMMUNICATION", false));
        MenuClient.setDaemonMonitoring(getProperty("MONITOR_DAEMON_FIRING", false));
    }

    public static void setXmfSettings() {
	    if (properties != null) setXmfDebugging();
    }
}
