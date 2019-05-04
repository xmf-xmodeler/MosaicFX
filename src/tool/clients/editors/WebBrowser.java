package tool.clients.editors;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import tool.clients.EventHandler;
import tool.helper.IconGenerator;
import xos.Message;
import xos.Value;

import java.util.concurrent.CompletableFuture;

public class WebBrowser {

    private String id;
    private EventHandler eventHandler;
    private String startUrl;

    private WebView webView;
    private TextField urlField;
    private VBox browserVBox;

    protected WebBrowser(String id, EventHandler eventHandler) {
        this.id = id;
        this.eventHandler = eventHandler;
    }

    //exposed
    protected CompletableFuture<VBox> getBrowserVBox(String url, String text) {
        CompletableFuture<VBox> result = new CompletableFuture<>();
        result.runAsync(() -> {
            Platform.runLater(() -> {
                createBrowser(url, text);
                result.complete(browserVBox);
            });
        });
        return result;
    }

    protected void setUrl(String url) {
        if (startUrl != null) { url = startUrl; startUrl = null; }
        if (url != null && !url.isEmpty()) { //&&isURL(url) TODO: this doesnt work with strings that contain an html doc, why was it there in the first plac?
            if (webView != null) {
                String content = url;
                if (isLikelyToBeHTML(content)) {
                    Platform.runLater(() -> { webView.getEngine().loadContent(content); }); //TODO: we should consider not putting HTML docs into strings... ¯\_(ツ)_/¯
                } else {
                    Platform.runLater(() -> { webView.getEngine().load(content); });
                }
            }
            else {
                startUrl = url;
            }
        }
    }

    private void createBrowser(String url, String text) { //TODO: constructor
        browserVBox = new VBox();
        urlField = new TextField();
        webView = new WebView();

        //webview settings
        webView.setOnZoom(e -> {
            webView.setZoom(webView.getZoom()*e.getZoomFactor());
        });

        //control ui
        Button increaseZoom = new Button("+");
        increaseZoom.setOnAction((e)->{
            webView.setZoom(webView.getZoom()+0.1);
        });

        Button decreaseZoom = new Button("-");
        decreaseZoom.setOnAction((e)->{
            webView.setZoom(webView.getZoom()-0.1);
        });

        Button back = new Button("", IconGenerator.getImageView("User/Arrow4Left"));
        back.setOnAction((e)->{
            System.err.println("loc: "+webView.getEngine().getLocation());
            System.err.println(webView.getEngine().getHistory().getEntries());
            System.err.println(webView.getEngine().getHistory().getCurrentIndex());

            if( webView.getEngine().getHistory().getCurrentIndex()>0){
                webView.getEngine().getHistory().go(-1);
            } else {
                setUrl(webView.getEngine().getHistory().getEntries().get(0).getUrl());
            }
        });

        Button forward = new Button("", IconGenerator.getImageView("User/Arrow4Right"));
        forward.setOnAction((e)->{
            if( webView.getEngine().getHistory().getCurrentIndex()<webView.getEngine().getHistory().getEntries().size()-1){
                webView.getEngine().getHistory().go(1);
            }
        });

        urlField.setText("Enter URL here...");
        urlField.setOnKeyReleased(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) locationChanged(urlField.getText(), webView.getEngine().getLocation());
        });

        Button go = new Button("", IconGenerator.getImageView("User/Arrow2Right"));
        go.setOnAction(actionEvent -> {
            locationChanged(urlField.getText(), webView.getEngine().getLocation());
        });

        ProgressIndicator loadingSpinner = new ProgressIndicator(-1); //-1 = indeterminate mode
        loadingSpinner.setMaxSize(25, 25);
        loadingSpinner.setPadding(new Insets(0, 10, 0, 0));

        //webEngine settings

        webView.getEngine().locationProperty().addListener((observable, oldLocation, newLocation) -> {
            System.err.println("old: " + oldLocation + ", new: " + newLocation+", rep: ");

            if (newLocation.contains("http://snippet/")) {
                String replaced = newLocation.replace("http://snippet/", "snippet:/");
                setUrl(replaced);
            } else {
                locationChanged(webView.getEngine().getLocation(), urlField.getText());
            }
        });

        webView.getEngine().getLoadWorker().stateProperty().addListener(listener -> {
            //TODO: what was dis supposed to do?
        });

        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED || (webView.getEngine().getLocation()!=null && webView.getEngine().getLocation().contains("snippet:/")) ) {
                loadingSpinner.setVisible(false);
            } else {
                loadingSpinner.setVisible(true);
            }
        });

        BorderPane navBar = new BorderPane();
        HBox leftControls = new HBox();
        HBox rightControls = new HBox();
        HBox urlBar = new HBox();
        StackPane urlFieldStack = new StackPane();

        urlFieldStack.getChildren().addAll(urlField, loadingSpinner);
        StackPane.setAlignment(loadingSpinner, Pos.CENTER_RIGHT);

        leftControls.getChildren().addAll(back, forward);
        rightControls.getChildren().addAll(decreaseZoom, increaseZoom);
        urlBar.getChildren().addAll(urlFieldStack, go);
        navBar.setLeft(leftControls);
        navBar.setRight(rightControls);
        navBar.setCenter(urlBar);

        HBox.setHgrow(urlFieldStack, Priority.ALWAYS);

//        urlBar.setPadding(new Insets(0, 50, 0, 50));
        urlBar.setPadding(new Insets(0, 6, 0, 6));
        navBar.setPadding(new Insets(0, 6, 0, 6));

        browserVBox.getChildren().addAll(navBar, webView);

        setUrl(url); //TODO: move this off the ui thread

        //browserLocked = false;
//        URL.setURLStreamHandlerFactory(protocol -> "snippets:/".equals(protocol) ? new URLStreamHandler() { //TODO: this can be used to map xmodeler internal urls properly
//          protected URLConnection openConnection(URL url) throws IOException {
//            return new URLConnection(url) {
//              public void connect() throws IOException {
//                System.out.println("Connected!");
//              }
//            };
//          }
//        } : null);
    }

    //internal
    private boolean isURL(String url) {
        return url.startsWith("http://") || url.startsWith("file:/");
    }

    private boolean isLikelyToBeHTML(String s) { //TODO: oh boi
        s = s.trim();
        s = s.toLowerCase();
        if (s.startsWith("<html>")) return true;
        if (s.startsWith("<!doctype html")) return true;
        return false;
    }

    private void locationChanged(String newLocation, String oldLocation) { //TODO: this needs to be triggered on every address change
        urlField.setText(newLocation);
        if ( newLocation == null || newLocation.isEmpty() || newLocation.equals("about:blank")) return;
        if (!newLocation.equals(oldLocation)) sendUrlRequest(newLocation);
    }

    //xos
    private void sendUrlRequest(String location) {
        Message message = eventHandler.newMessage("urlRequest", 2);
        message.args[0] = new Value(id);
        message.args[1] = new Value(location);
        eventHandler.raiseEvent(message);
    }

//  private Stack<String> getBackQueue(Browser browser) {
//    if (!backQueues.containsKey(browser)) backQueues.put(browser, new Stack<String>());
//    return backQueues.get(browser);
//  }
//
//  private Stack<String> getForwardQueue(Browser browser) {
//    if (!forwardQueues.containsKey(browser)) forwardQueues.put(browser, new Stack<String>());
//    return forwardQueues.get(browser);
//  }



    //Old - will be removed

    //  boolean browserLocked = true;

//  Hashtable<Browser, Stack<String>> backQueues     = new Hashtable<Browser, Stack<String>>();
//  Hashtable<Browser, String>        browserCurrent = new Hashtable<Browser, String>();
//  Hashtable<Browser, Stack<String>> forwardQueues  = new Hashtable<Browser, Stack<String>>();

//    private void addToolBar(CTabFolder parent, Browser browser) {
//    ToolBar toolbar = new ToolBar(parent, SWT.NONE);
//    FormData data = new FormData();
//    data.top = new FormAttachment(0, 5);
//    toolbar.setLayoutData(data);
//    ToolItem itemBack = new ToolItem(toolbar, SWT.PUSH);
//    itemBack.setText(("Back"));
//    ToolItem itemForward = new ToolItem(toolbar, SWT.PUSH);
//    itemForward.setText(("Forward"));
//    final ToolItem itemStop = new ToolItem(toolbar, SWT.PUSH);
//    itemStop.setText(("Stop"));
//    final ToolItem itemRefresh = new ToolItem(toolbar, SWT.PUSH);
//    itemRefresh.setText(("Refresh"));
//    final ToolItem itemGo = new ToolItem(toolbar, SWT.PUSH);
//    itemGo.setText(("Go"));
//
//    itemBack.setEnabled(browser.isBackEnabled());
//    itemForward.setEnabled(browser.isForwardEnabled());
//    // Listener listener = new Listener() {
//    // public void handleEvent(Event event) {
//    // ToolItem item = (ToolItem) event.widget;
//    // if (item == itemBack)
//    // browser.back();
//    // else if (item == itemForward)
//    // browser.forward();
//    // else if (item == itemStop)
//    // browser.stop();
//    // else if (item == itemRefresh)
//    // browser.refresh();
//    // else if (item == itemGo)
//    // browser.setUrl(locationBar.getText());
//    // }
//    // };
//  }

//    runOnDisplay(new Runnable() {
//      public void run() {
//        CTabItem tabItem = new CTabItem(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
//        tabItem.setText(label);
//        tabItem.setShowClose(true);
//        tabs.put(id, tabItem);
//        Composite browserParent = new Composite(tabFolder, SWT.NONE);
//        //Vector<Object> buttons = new Vector<Object>();
//        Button up = new Button(browserParent, SWT.PUSH);
//        up.setText("+");
//        buttons.add(up);
//        Button down = new Button(browserParent, SWT.PUSH);
//        down.setText("-");
//        buttons.add(down);
//        Button b1a = new Button(browserParent, SWT.PUSH);
//        b1a.setImage(new Image(tabItem.getDisplay(), new ImageData("icons/User/Arrow4Left.gif")));
//        buttons.addElement(b1a);
//        Button b1b = new Button(browserParent, SWT.PUSH);
//        b1b.setImage(new Image(tabItem.getDisplay(), new ImageData("icons/User/Arrow4Right.gif")));
//        buttons.addElement(b1b);
//        Label b2 = new Label(browserParent, SWT.NONE);
//        b2.setText("URL:");
//        buttons.addElement(b2);
//        final Text b3 = new Text(browserParent, SWT.BORDER);
//        b3.setText("Enter URL here...");
//        buttons.addElement(b3);
//        final Browser browser = new Browser(browserParent, SWT.BORDER);
//        final int defaultZoom = 100;// XModeler.getDeviceZoomPercent();
//        final int[] zoom = new int[] { defaultZoom };
//        up.addListener(SWT.Selection, new Listener() {
//          public void handleEvent(Event arg0) {
//            zoom[0] += defaultZoom / 10;
//            browser.execute("document.body.style.zoom = \"" + zoom[0] + "%\"");
//            browser.redraw();
//          }
//        });
//        down.addListener(SWT.Selection, new Listener() {
//          public void handleEvent(Event arg0) {
//            if (zoom[0] > defaultZoom / 10) {
//              zoom[0] -= defaultZoom / 10;
//              browser.execute("document.body.style.zoom = \"" + zoom[0] + "%\"");
//              browser.redraw();
//            }
//          }
//        });
//        b1a.addListener(SWT.Selection, new Listener() {
//          public void handleEvent(Event arg0) {
//            // browser.back();
//            if (!getBackQueue(browser).isEmpty()) {
//              if (browserCurrent.containsKey(browser)) {
//                getForwardQueue(browser).push(browserCurrent.get(browser));
//              }
//              setUrl(id, getBackQueue(browser).pop(), false);
//            }
//          }
//        });
//        b1b.addListener(SWT.Selection, new Listener() {
//          public void handleEvent(Event arg0) {
//            // browser.forward();
//            if (!getForwardQueue(browser).isEmpty()) {
//              if (browserCurrent.containsKey(browser)) {
//                getBackQueue(browser).push(browserCurrent.get(browser));
//              }
//              setUrl(id, getForwardQueue(browser).pop(), false);
//            }
//          }
//        });
//        b3.addListener(SWT.DefaultSelection, new Listener() {
//          public void handleEvent(Event e) {
//            browser.setUrl(b3.getText());
//          }
//        });
//        browser.addLocationListener(new LocationListener() {
//          public void changed(LocationEvent event) {
//            if (event.top) b3.setText(event.location);
//          }
//
//          public void changing(LocationEvent event) {
//          }
//        });
//        tabItem.setControl(browserParent);
//        browser.setText(text);
//        browser.setJavascriptEnabled(true);
//        int buttonCount = buttons.size();
//        GridLayout gridLayout = new GridLayout();
//        gridLayout.numColumns = buttonCount;
//        browserParent.setLayout(gridLayout);
//        GridData gd = new GridData();
//        gd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
//        gd.horizontalAlignment = GridData.FILL;
//        gd.verticalAlignment = GridData.FILL;
//        gd.horizontalSpan = buttonCount;
//        browser.setLayoutData(gd);
//        gd = new GridData();
//        gd.grabExcessHorizontalSpace = true;
//        gd.horizontalAlignment = GridData.FILL;
//        b3.setLayoutData(gd);
//        browserLocked = false;
//        if (isURL(url)) {
//          browser.setUrl(url);
//        }
//        browsers.put(id, browser);
//        browser.setVisible(true);
//        browser.addLocationListener(EditorClient.this);
//        tabFolder.setSelection(tabItem);
//      }
//    });
}
