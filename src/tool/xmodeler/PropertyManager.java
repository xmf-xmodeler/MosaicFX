package tool.xmodeler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tool.clients.menus.MenuClient;
import tool.helper.IconGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Properties;

public class PropertyManager {

	//settings
	static String filePath;
	static Properties properties;
	static double javaVersion = Double.parseDouble(System.getProperty("java.specification.version"));	

	//ui
	Stage stage;
	GridPane generalGrid;
	GridPane debugGrid;
	GridPane pathGrid;
	

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
		
		TabPane tabPane = new TabPane();
		Tab saveTab = new Tab("Savings");
		Tab userInterfaceTab  = new Tab("UserInterface");
		Tab debugTab = new Tab("Debugging");
		
		Button btnCancel = new Button("Cancel");
		btnCancel.setOnAction(this::onCancelButtonClicked);
		Button btnCancelSaves = new Button("Cancel");
		btnCancelSaves.setOnAction(this::onCancelButtonClicked);
		Button btnCancelDebug = new Button("Cancel");
		btnCancelDebug.setOnAction(this::onCancelButtonClicked);

		Button btnSave = new Button("Save");
		btnSave.setOnAction(this::onSaveButtonClicked);
		Button btnSaveSaves = new Button("Save");
		btnSaveSaves.setOnAction(this::onSaveButtonClicked);
		Button btnSaveDebug = new Button("Save");
		btnSaveDebug.setOnAction(this::onSaveButtonClicked);
		
		Image icon = new Image(new File("resources/gif/Package.gif").toURI().toString());
	    ImageView imageView = new ImageView(icon);
	    ImageView imageView2 = new ImageView(icon);
	    ImageView imageView3 = new ImageView(icon);
	    
		generalGrid = new GridPane();
		Label uiInfo = new Label("User Interface appearance");
		uiInfo.setStyle("-fx-font-weight: bold");
		Label toolX = new Label("Screen_X: ");
		TextField toolXField = new TextField("100");
		toolXField.setMaxWidth(120);
		Label toolY = new Label("Screen_Y: ");
		TextField toolYField = new TextField("100");
		toolYField.setMaxWidth(120);
		Label toolWidth = new Label("Tool Width: ");
		TextField toolWidthField = new TextField("1200");
		toolWidthField.setMaxWidth(120);
		Label toolHeight = new Label("Tool Height: ");
		TextField toolHeightField = new TextField("900");
		toolHeightField.setMaxWidth(120);
		Label separatInfo = new Label("Elements of the XModeler");
		separatInfo.setStyle("-fx-font-weight: bold");
		CheckBox formSeparately = new CheckBox("Forms in separat windows as default");
		formSeparately.setSelected(false);
		CheckBox consoleSeparately = new CheckBox("Console in a separat window as default");
		consoleSeparately.setSelected(false);
		CheckBox consoleShown = new CheckBox("Console shown as default");
		consoleShown.setSelected(false);
		CheckBox treeSeparately = new CheckBox("Editor tree in separat windows");
		treeSeparately.setSelected(false);
		Separator separator = new Separator();
		separator.setOrientation(Orientation.HORIZONTAL);
		generalGrid.setPadding(new Insets(5,5,5,5));
		generalGrid.setVgap(5);
		generalGrid.setHgap(5);
		generalGrid.add(uiInfo, 0, 0);
		generalGrid.add(toolX, 0, 1);
		generalGrid.add(toolXField, 0,1);
		GridPane.setHalignment(toolXField, HPos.LEFT);
		GridPane.setHalignment(toolXField, HPos.RIGHT);
		generalGrid.add(toolY, 0, 2);
		generalGrid.add(toolYField, 0,2);
		GridPane.setHalignment(toolYField, HPos.RIGHT);
		generalGrid.add(toolWidth, 0, 3);
		generalGrid.add(toolWidthField, 0,3);
		GridPane.setHalignment(toolWidthField, HPos.RIGHT);
		generalGrid.add(toolHeight, 0, 4);
		generalGrid.add(toolHeightField, 0,4);
		GridPane.setHalignment(toolHeightField, HPos.RIGHT);
		generalGrid.add(separator, 0, 5);
		generalGrid.add(separatInfo, 0, 6);
		generalGrid.add(formSeparately, 0, 7);
		generalGrid.add(consoleSeparately, 0, 8);
		generalGrid.add(consoleShown, 0, 9);
		generalGrid.add(treeSeparately, 0,10);
		generalGrid.add(btnSave, 0, 11);
		GridPane.setHalignment(btnSave, HPos.RIGHT);
		generalGrid.add(btnCancel, 1, 11);
		GridPane.setHalignment(btnCancel, HPos.LEFT);
		userInterfaceTab.setContent(generalGrid);
		
		pathGrid = new GridPane();
		pathGrid.setPadding(new Insets(5,5,5,5));
		pathGrid.setVgap(5);
		pathGrid.setHgap(5);
		Label saveInfo = new Label("Locations for files");
		saveInfo.setStyle("-fx-font-weight: bold");
		File folder = new File("");
		folder=new File(folder.toURI()).getParentFile();
		Label saveLabel = new Label("Directory for save files ");
		TextField saveTextField = new TextField(new File(folder, "Saves").toString());
		
		
		DirectoryChooser saveDirectoryChooser = new DirectoryChooser();
		saveDirectoryChooser.setInitialDirectory(folder);
		Button saveDirectory = new Button();
		saveDirectory.setGraphic(imageView);
		saveDirectory.setOnAction(e -> {
            File selectedSaveDirectory = saveDirectoryChooser.showDialog(stage);
        });
		
		DirectoryChooser backUpDirectoryChooser = new DirectoryChooser();
		backUpDirectoryChooser.setInitialDirectory(folder);
		Label backUpLabel = new Label("Directory for backUp files ");
		TextField backUpTextField = new TextField(new File(folder, "BackUp").toString());
		Button backUpDirectory = new Button();
		backUpDirectory.setGraphic(imageView2);
		backUpDirectory.setOnAction(e -> {
            File selectedBackUpDirectory = backUpDirectoryChooser.showDialog(stage);
        });
		
		DirectoryChooser graphicDirectoryChooser = new DirectoryChooser();
		graphicDirectoryChooser.setInitialDirectory(folder);
		Label graphicLabel = new Label("Directory for graphics ");
		TextField graphicTextField = new TextField(new File(folder, "Graphics").toString());
		Button graphicDirectory = new Button();
		graphicDirectory.setGraphic(imageView3);
		graphicDirectory.setOnAction(e -> {
            File selectedGraphicDirectory = graphicDirectoryChooser.showDialog(stage);
        });
		
		
		pathGrid.add(saveInfo, 0, 0);
		pathGrid.add(saveLabel, 0, 1);
		pathGrid.add(saveTextField, 1, 1);
		pathGrid.add(saveDirectory, 2,1);
		pathGrid.add(backUpLabel, 0, 2);
		pathGrid.add(backUpTextField, 1, 2);
		pathGrid.add(backUpDirectory, 2,2);
		pathGrid.add(graphicLabel, 0, 3);
		pathGrid.add(graphicTextField, 1, 3);
		pathGrid.add(graphicDirectory, 2,3);
		pathGrid.add(btnSaveSaves, 0, 4);
		GridPane.setHalignment(btnSaveSaves, HPos.RIGHT);
		pathGrid.add(btnCancelSaves, 1, 4);
		GridPane.setHalignment(btnCancelSaves, HPos.LEFT);
		
		
		saveTab.setContent(pathGrid);
		
		debugGrid = new GridPane();
		debugGrid.setPadding(new Insets(5,5,5,5));
		debugGrid.setVgap(5);
		debugGrid.setHgap(5);
		Label debugInfo = new Label("Degugging Options");
		debugInfo.setStyle("-fx-font-weight: bold");
		CheckBox monitorClientCommunication = new CheckBox("MONITOR_CLIENT_COMMUNICATION");
		monitorClientCommunication.setSelected(false);
		CheckBox monitorDaemonFiring = new CheckBox("MONITOR_DAEMON_FIRING");
		monitorDaemonFiring.setSelected(false);
		CheckBox monitorIgnoreSaveImage = new CheckBox("IGNORE_SAVE_IMAGE");
		monitorIgnoreSaveImage.setSelected(false);
		CheckBox LogXmfOutput = new CheckBox("LOG_XMF_OUTPUT");
		LogXmfOutput.setSelected(false);
		Label monitorCalls = new Label("Monitor calls");
		Button btnMonitorCalls = new Button("Open");
		btnMonitorCalls.setOnAction(actionEvent->MenuClient.openCallMonitor());
		Label perfomanceMonitor = new Label("Performance monitor");
		Button btnPerfomanceMonitor = new Button("Open");
		btnPerfomanceMonitor.setOnAction(actionEvent->MenuClient.openPerformanceMonitor());
		
		debugGrid.add(debugInfo, 0,0);
		debugGrid.add(monitorClientCommunication,0, 1);
		debugGrid.add(monitorDaemonFiring, 0,2);
		debugGrid.add(monitorIgnoreSaveImage, 0,3);
		debugGrid.add(LogXmfOutput, 0,4);
		debugGrid.add(monitorCalls, 0,5);
		debugGrid.add(btnMonitorCalls, 1,5);
		debugGrid.add(perfomanceMonitor, 0,6);
		debugGrid.add(btnPerfomanceMonitor, 1,6);
		debugGrid.add(btnSaveDebug, 0, 7);
		GridPane.setHalignment(btnSaveDebug, HPos.RIGHT);
		debugGrid.add(btnCancelDebug, 1, 7);
		GridPane.setHalignment(btnCancelDebug, HPos.LEFT);
		
		
		//fillDebugGrid();
		debugTab.setContent(debugGrid);
		
		
		
		
		
		tabPane.getTabs().addAll(saveTab,userInterfaceTab,debugTab);
		
		
		
		VBox vBox = new VBox(tabPane);
		initScene(vBox);
			
//		try {
//			//init gridpanes
//			generalGrid = getGridpane();
//			debugGrid = getGridpane();
//			pathGrid = getGridpane();
//
//			//fill gridpanes
//			fillPropGrid();
//			fillDebugGrid();
//			fillPathGrid();
//
//			//labels
//			Label generalLabel = new Label("General:");
//			Label debugLabel = new Label("Debug:");
//			Label pathLabel = new Label("Modelpaths:");
//
//			//control buttons
//			HBox buttons = getControlButtons();
//
//			//merge into mainVbox
//			VBox gridPanes = new VBox(generalLabel, generalGrid, debugLabel, debugGrid, pathLabel, pathGrid);
//			gridPanes.setPadding(new Insets(10));
//			VBox mainVbox = new VBox(new ScrollPane(gridPanes), buttons);
//			mainVbox.setPadding(new Insets(10));
//			mainVbox.setSpacing(10);
//
//			// init scene
//			initScene(mainVbox);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

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
		parseGridPane(pathGrid);
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
		stage.setWidth(350);
		stage.setResizable(false);
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.show();
	}

	private void fillPropGrid() {
		addRow(generalGrid, "TOOL_X", getProperty("TOOL_X", 100));
		addRow(generalGrid, "TOOL_Y", getProperty("TOOL_Y", 100));
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

	private void fillPathGrid() {
		//It looks doubled, but its necessary to get to the current folder.
		File folder = new File("");
		folder=new File(folder.toURI()).getParentFile();
		addRow(pathGrid, "Save", getProperty("backUpPath",new File(folder, "Saves").toString()));
		addRow(pathGrid, "BackUp", getProperty("backUpPath",new File(folder, "BackUps").toString()));
		addRow(pathGrid, "Graphics", getProperty("backUpPath",new File(folder, "Graphics").toString()));
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

	private int getGridLength(GridPane gridPane) { // Reimplementing. Does not work for JAVA 14!
		int rows = 0;
		try {
			Method method = gridPane.getClass().getDeclaredMethod("getNumberOfRows");
			method.setAccessible(true);
			rows = (Integer) method.invoke(gridPane);
		} catch (Exception e) {
			System.err.println("Error @getGridLength @PropertyManager");
			//e.printStackTrace();
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
