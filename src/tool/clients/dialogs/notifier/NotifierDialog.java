package tool.clients.dialogs.notifier;

import java.util.concurrent.CountDownLatch;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
  private static final int   DISPLAY_TIME  = 4500;
  // how long each tick is when fading in (in ms)
  private static final int   FADE_TIMER    = 50;
  // how long each tick is when fading out (in ms)
  private static final int   FADE_IN_STEP  = 30;
  // how many tick steps we use when fading out
  private static final int   FADE_OUT_STEP = 8;

  // how high the alpha value is when we have finished fading in
  private static final int   FINAL_ALPHA   = 225;

//  // title foreground color
//private static Color       _fgColor = Color.rgb(40, 73, 97);
 
//  // text foreground color
//  private static Color       _fgColor      = _titleFgColor;
//
//  // shell gradient background color - top
  private static Color       _bgFgGradient = Color.rgb(226, 239, 249);

//  private static Color       _bgFgGradient_warning = ColorCache.getColor(226, 239, 50);
//  private static Color       _bgFgGradient_error = ColorCache.getColor(255, 150, 150);
//  // shell gradient background color - bottom
  private static Color       _bgBgGradient = Color.rgb(177, 211, 243);
//  private static Color       _bgBgGradient_warning = ColorCache.getColor(200, 220, 50);
//  private static Color       _bgBgGradient_error = ColorCache.getColor(255, 100, 100);
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

  static int toastDelay = 4500; //3.5 seconds
  static int fadeInDelay = 500; //0.5 seconds
  static int fadeOutDelay= 500; //0.5 seconds
  
  static int _StageBorderSize = 20; // estimated window border
  static int _borderSize = 2;
  static int _titleHeight = 20;
  static int _textGap = 20;
  static int _boxWidth = 250;
  static int _boxHeight = 180;
  static int _textWidthMax = _boxWidth - _textGap ;
  static int _textHeightMax = _boxHeight - _textGap - _titleHeight;
  
  /**
   * Creates and shows a notification dialog with a specific title, message and a
   * 
   * @param title
   * @param message
   * @param type
   */
  public static void notify(String title, String message, final NotificationType type) {
	  
	  if (Thread.currentThread().getName().equals("JavaFX Application Thread")) { 
		  paintNotifier(title,message,type);
		} else { // create a new Thread
//			System.err.println("Calling redraw from " + Thread.currentThread());
			CountDownLatch l = new CountDownLatch(1);
			Platform.runLater(() -> {
				// we are on the right Thread already:
				paintNotifier(title,message,type);
	    		l.countDown();
			});
			try {
				l.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	  }
  
  	private static void paintNotifier(String title, String message, final NotificationType type) {
  		
  		Pane notificationPane = XModeler.getNotificationPane();
  		Stage xModelerStage = XModeler.getStage();
  		
  		Rectangle r = new Rectangle(_boxWidth, _boxHeight);
  		
  		Stop[] stops = new Stop[] { new Stop(0, _bgFgGradient), new Stop(1, _bgBgGradient)};
  		LinearGradient lg = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops );
  		r.setFill(lg);
  		r.setStroke(Color.BLACK);
  		r.setStrokeWidth(_borderSize);
  		r.setArcHeight(0.2);
  		r.setArcWidth(0.2);
  		
  		Text titleText = new Text(title);
  		titleText.setX(_textGap);
  		titleText.setY(_textGap);
  		titleText.setWrappingWidth(_boxWidth - 2*_textGap);
  		titleText.setSmooth(true);
  		titleText.setFont(Font.font("Arial", FontWeight.BOLD , 14));
  		
  		Text messageText = new Text(message);
  		messageText.setX(_textGap);
  		messageText.setY(2*_textGap);
  		messageText.setWrappingWidth(_boxWidth - 2*_textGap);
  		messageText.setSmooth(true);
  		messageText.setFont(Font.font("Arial", 14));
  		
  		StackPane rectStack = new StackPane(new Group(r, new Pane(titleText), new Pane(messageText)));  		
  		rectStack.setLayoutX(xModelerStage.getWidth()- _boxWidth - _borderSize - 16);
  		rectStack.setLayoutY(xModelerStage.getHeight() - _boxHeight - _borderSize - 48);
  		rectStack.setOpacity(0);
  		
  		notificationPane.getChildren().add(rectStack);
  		
        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(fadeInDelay), new KeyValue (rectStack.opacityProperty(), 1)); 
        fadeInTimeline.getKeyFrames().add(fadeInKey1);   
        fadeInTimeline.setOnFinished((ae) -> 
        {
            new Thread(() -> {
                try
                {
                    Thread.sleep(toastDelay);
                }
                catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                   Timeline fadeOutTimeline = new Timeline();
                    KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(fadeOutDelay), new KeyValue (rectStack.opacityProperty(), 0)); 
                    fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
                    fadeOutTimeline.setOnFinished((aeb) -> notificationPane.getChildren().remove(rectStack));
                    fadeOutTimeline.play();
            }).start();
        }); 
        fadeInTimeline.play();
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
