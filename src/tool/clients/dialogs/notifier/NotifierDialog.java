package tool.clients.dialogs.notifier;

import java.util.concurrent.CountDownLatch;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


import tool.xmodeler.XModeler;

public class NotifierDialog {

  // how long the the tray popup is displayed after fading in (in milliseconds)
//  private static final int   DISPLAY_TIME  = 4500;
  // how long each tick is when fading in (in ms)
//  private static final int   FADE_TIMER    = 50;
  // how long each tick is when fading out (in ms)
//  private static final int   FADE_IN_STEP  = 30;
  // how many tick steps we use when fading out
//  private static final int   FADE_OUT_STEP = 8;

  // how high the alpha value is when we have finished fading in
//  private static final int   FINAL_ALPHA   = 225;

//  // title foreground color
//private static Color       _fgColor = Color.rgb(40, 73, 97);
 
//  // text foreground color
//  private static Color       _fgColor      = _titleFgColor;
//
//  // shell gradient background color - top
//  private static Color       _bgFgGradientDefault = Color.rgb(226, 239, 249);
//  private static Color       _bgBgGradientDefault = Color.rgb(177, 211, 243);
//  
//  private static Color       _bgFgGradient = Color.rgb(226, 239, 249);
//
//  private static Color       _bgFgGradient_warning = Color.rgb(226, 239, 50);
//  private static Color       _bgBgGradient_warning = Color.rgb(200, 220, 50);
//  private static Color       _bgFgGradient_error = Color.rgb(255, 150, 150);
//  // shell gradient background color - bottom
//  private static Color       _bgBgGradient = Color.rgb(177, 211, 243);
//
//  private static Color       _bgBgGradient_error = Color.rgb(255, 100, 100);
//  // shell border color
//  private static Color       _borderColor  = ColorCache.getColor(40, 73, 97);
//
//  // contains list of all active popup shells
//  private static List<Shell> _activeShells = new ArrayList<Shell>();
//
//  // image used when drawing
//  private static Image       _oldImage;
//
//  private static Shell       _shell;

  final static int TOAST_DELAY = 3500; //3.5 seconds
  final static int FADE_IN_DELAY = 500; //0.5 seconds
  final static int FADE_OUT_DELAY= 500; //0.5 seconds
  
  final static int BORDER_SIZE = 2;
  final static int TITLE_HEIGHT = 20;
  final static int TEXT_GAP = 10;
  final static int BOX_WIDTH = 352;
  final static int BOX_HEIGHT = 102;
  final static int TEXT_WIDTH_MAX = BOX_WIDTH - TEXT_GAP ;
  final static int TEXT_HEIGHT_MAX = BOX_HEIGHT - TEXT_GAP - TITLE_HEIGHT;
  
  /**
   * Creates and shows a notification dialog with a specific title, message and a
   * 
   * @param title
   * @param message
   * @param type
   */
  public static void notify(String title, String message, final NotificationType type) {
	  
	  
	  if (Thread.currentThread().getName().equals("JavaFX Application Thread")) { 
		  createNotifier(title,message,type);
		} else { // create a new Thread
//			System.err.println("Calling redraw from " + Thread.currentThread());
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				// we are on the right Thread already:
				createNotifier(title,message,type);
	    		l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	  }
  
  	private static void createNotifier(String title, String message, final NotificationType type) {
  		
  		Pane notificationPane = XModeler.getNotificationPane();
  		Stage xModelerStage = XModeler.getStage();
  		
  		
  		
  		Text titleText = new Text(title);
  		//titleText.setText(title + "\nSecond title line\n\n\n\n\n\n\n"); // For testing
  		titleText.setWrappingWidth(BOX_WIDTH - 2*TEXT_GAP);
  		titleText.setSmooth(true);
  		titleText.setFont(Font.font("Arial", FontWeight.BOLD , 14));
  		
  		Text messageText = new Text(message);
  		messageText.setWrappingWidth(BOX_WIDTH - 2*TEXT_GAP);
  		messageText.setSmooth(true);
  		messageText.setFont(Font.font("Arial", 14));
  		
  		
  		VBox titleAndMessage = new VBox(titleText, messageText);
  		titleAndMessage.setLayoutX(TEXT_GAP);
  		titleAndMessage.setLayoutY(TEXT_GAP);
  		titleAndMessage.setPadding(new Insets(TEXT_GAP));
  		titleAndMessage.setSpacing(TEXT_GAP/2);
  		//titleAndMessage.setMaxHeight(BOX_HEIGHT);
  		double tAMheight = titleAndMessage.getHeight();
  		
  		Rectangle r = new Rectangle(BOX_WIDTH, Math.max(BOX_HEIGHT, tAMheight));
  		
  		r.setFill(getColor(type));
  		r.setStroke(Color.BLACK);
  		r.setStrokeWidth(BORDER_SIZE);
  		r.setArcHeight(0.2);
  		r.setArcWidth(0.2);
  		
  		Pane rectStack = new Pane(r, titleAndMessage);  		
  		rectStack.setLayoutX(xModelerStage.getWidth()- BOX_WIDTH - XModeler.getVerticalBorderSize()*2-BORDER_SIZE);
  		rectStack.setLayoutY(xModelerStage.getHeight() - r.getHeight()  - XModeler.getHorizontalBorderSize(false)-BORDER_SIZE);
  		rectStack.setOpacity(0);
  		
  		notificationPane.getChildren().add(rectStack);
  		
        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(FADE_IN_DELAY), new KeyValue (rectStack.opacityProperty(), 1)); 
        fadeInTimeline.getKeyFrames().add(fadeInKey1);   
        fadeInTimeline.setOnFinished((ae) -> 
        {
            new Thread(() -> {
                try
                {
                    Thread.sleep(TOAST_DELAY);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                   Timeline fadeOutTimeline = new Timeline();
                    KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(FADE_OUT_DELAY), new KeyValue (rectStack.opacityProperty(), 0)); 
                    fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
                    fadeOutTimeline.setOnFinished((aeb) -> notificationPane.getChildren().remove(rectStack));
                    fadeOutTimeline.play();
            }).start();
        }); 
        fadeInTimeline.play();
  	}
  	/**
  	 * Creates and returns the LinearGradient according to NotificationType
  	 * @param type
  	 * @return LinearGradient
  	 */
  	private static LinearGradient getColor(NotificationType type) {
  		
  		Color bgFgGradient;
  		Color bgBgGradient;
  		
  		switch(type) {
  		
  		case WARN:
  			bgFgGradient = Color.rgb(226, 239, 50);
  			bgBgGradient = Color.rgb(200, 220, 50);
  			break;
  		
  		case ERROR:
  			bgFgGradient = Color.rgb(255, 150, 150);
  			bgBgGradient = Color.rgb(255, 100, 100);
  			break;
  		
  		case INFO:
  			bgFgGradient = Color.rgb(226, 239, 249);
  			bgBgGradient = Color.rgb(177, 211, 243);
  			break;
  		
  		default:
  			bgFgGradient = Color.rgb(249, 249, 249);
  			bgBgGradient = Color.rgb(240, 240, 240);
  			break;
  		}
  		
  		Stop[] stops = new Stop[] { new Stop(0, bgFgGradient), new Stop(1, bgBgGradient)};
		LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops );
		return lg;
  	}
//    _shell = new Shell(XModeler.getXModeler(), SWT.NO_FOCUS | SWT.NO_TRIM);
//    _shell.setLayout(new FillLayout());
//    _shell.setForeground(_fgColor);
//    _shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
//    _shell.addListener(SWT.Dispose, new Listener() {
//      @Override
//      public void handleEvent(Event event) {
//        _activeShells.remove(_shell);
//      }
//    });

//    final Composite inner = new Composite(_shell, SWT.NONE);

//    GridLayout gl = new GridLayout(2, false);
//    gl.marginLeft = 5;
//    gl.marginTop = 0;
//    gl.marginRight = 5;
//    gl.marginBottom = 5;
//
//    inner.setLayout(gl);
//    _shell.addListener(SWT.Resize, new Listener() {
//
//      @Override
//      public void handleEvent(Event e) {
//        try {
//          // get the size of the drawing area
//          Rectangle rect = _shell.getClientArea();
//          // create a new image with that size
//          Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
//          // create a GC object we can use to draw with
//          GC gc = new GC(newImage);
//
//          // fill background
//          if ( type == NotificationType.values()[5]){
//        	  //Info
//        	  gc.setForeground(_bgFgGradient);
//        	  gc.setBackground(_bgBgGradient);
//          }else if ( type == NotificationType.values()[3]){
//        	  //warning
//        	  gc.setForeground(_bgFgGradient_warning);
//        	  gc.setBackground(_bgBgGradient_warning);        	  
//          }else if ( type == NotificationType.values()[1]){
//        	  //error
//        	  gc.setForeground(_bgFgGradient_error);
//        	  gc.setBackground(_bgBgGradient_error);
//          }else{
//        	  //default
//        	  gc.setForeground(_bgFgGradient);
//        	  gc.setBackground(_bgBgGradient);
//          }
//        	  
//          gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);
//
//          // draw shell edge
//          gc.setLineWidth(2);
//          gc.setForeground(_borderColor);
//          gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
//          // remember to dipose the GC object!
//          gc.dispose();
//
//          // now set the background image on the shell
//          _shell.setBackgroundImage(newImage);
//
//          // remember/dispose old used iamge
//          if (_oldImage != null) {
//            _oldImage.dispose();
//          }
//          _oldImage = newImage;
//        } catch (Exception err) {
//          err.printStackTrace();
//        }
//      }
//    });
//
//    GC gc = new GC(_shell);
//
//    String lines[] = message.split("\n");
//    Point longest = null;
//    int typicalHeight = gc.stringExtent("X").y;
//
//    for (String line : lines) {
//      Point extent = gc.stringExtent(line);
//      if (longest == null) {
//        longest = extent;
//        continue;
//      }
//
//      if (extent.x > longest.x) {
//        longest = extent;
//      }
//    }
//    gc.dispose();
//
//    int minHeight = typicalHeight * lines.length;
//
//    CLabel imgLabel = new CLabel(inner, SWT.NONE);
//    imgLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));
//    imgLabel.setImage(type.getImage());
//
//    CLabel titleLabel = new CLabel(inner, SWT.NONE);
//    titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
//    titleLabel.setText(title);
//    titleLabel.setForeground(_titleFgColor);
//    Font f = titleLabel.getFont();
//    FontData fd = f.getFontData()[0];
//    fd.setStyle(SWT.BOLD);
//    fd.height = 11;
//    titleLabel.setFont(FontCache.getFont(fd));
//
//    Label text = new Label(inner, SWT.WRAP);
//    Font tf = text.getFont();
//    FontData tfd = tf.getFontData()[0];
//    tfd.setStyle(SWT.BOLD);
//    tfd.height = 8;
//    text.setFont(FontCache.getFont(tfd));
//    GridData gd = new GridData(GridData.FILL_BOTH);
//    gd.horizontalSpan = 2;
//    text.setLayoutData(gd);
//    text.setForeground(_fgColor);
//    text.setText(message);
//
//    minHeight = 100;
//
//    _shell.setSize(350, minHeight);
//
//    if (Display.getDefault().getActiveShell() == null || Display.getDefault().getActiveShell().getMonitor() == null) { return; }
//
//    Rectangle clientArea = XModeler.getXModeler().getBounds();
//
//    int startX = clientArea.x + clientArea.width - 352;
//    int startY = clientArea.y + clientArea.height - 102;
//
//    // move other shells up
//    if (!_activeShells.isEmpty()) {
//      List<Shell> modifiable = new ArrayList<Shell>(_activeShells);
//      Collections.reverse(modifiable);
//      for (Shell shell : modifiable) {
//        Point curLoc = shell.getLocation();
//        shell.setLocation(curLoc.x, curLoc.y - 100);
//        if (curLoc.y - 100 < 0) {
//          _activeShells.remove(shell);
//          shell.dispose();
//        }
//      }
//    }
//
//    _shell.setLocation(startX, startY);
//    _shell.setAlpha(0);
//    _shell.setVisible(true);
//
//    _activeShells.add(_shell);
//
//    fadeIn(_shell);
  }

//  private static void fadeIn(final Shell _shell) {
//    Runnable run = new Runnable() {
//
//      @Override
//      public void run() {
//        try {
//          if (_shell == null || _shell.isDisposed()) { return; }
//
//          int cur = _shell.getAlpha();
//          cur += FADE_IN_STEP;
//
//          if (cur > FINAL_ALPHA) {
//            _shell.setAlpha(FINAL_ALPHA);
//            startTimer(_shell);
//            return;
//          }
//
//          _shell.setAlpha(cur);
////          Display.getDefault().timerExec(FADE_TIMER, this);
//        } catch (Exception err) {
//          err.printStackTrace();
//        }
//      }
//
//    };
////    Display.getDefault().timerExec(FADE_TIMER, run);
//  }
//
//  private static void startTimer(final Shell _shell) {
//    Runnable run = new Runnable() {
//
//      @Override
//      public void run() {
//        try {
//          if (_shell == null || _shell.isDisposed()) { return; }
//
//          fadeOut(_shell);
//        } catch (Exception err) {
//          err.printStackTrace();
//        }
//      }
//
//    };
//    Display.getDefault().timerExec(DISPLAY_TIME, run);
//
//  }

//  private static void fadeOut(final Shell _shell) {
//    final Runnable run = new Runnable() {
//
//      @Override
//      public void run() {
//        try {
//          if (_shell == null || _shell.isDisposed()) { return; }
//
//          int cur = _shell.getAlpha();
//          cur -= FADE_OUT_STEP;
//
//          if (cur <= 0) {
//            _shell.setAlpha(0);
//            if (_oldImage != null) {
//              _oldImage.dispose();
//            }
//            _shell.dispose();
//            _activeShells.remove(_shell);
//            return;
//          }
//
//          _shell.setAlpha(cur);
//
//          Display.getDefault().timerExec(FADE_TIMER, this);
//
//        } catch (Exception err) {
//          err.printStackTrace();
//        }
//      }
//
//    };
//    Display.getDefault().timerExec(FADE_TIMER, run);
//
//  }
