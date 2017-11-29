package tool.clients.editors.texteditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import tool.xmodeler.XModeler;

public class Timer extends Tool implements Runnable {

  private static final Color GREEN = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
  private static final Color BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

  interface TimerAction {
    public void action();
  }

  TimerAction action;
  TimerAction update;
  int         delay;
  int         time;
  int         increment;

  public Timer(int delay, int increment, TimerAction action, TimerAction update) {
    this.delay = delay;
    this.update = update;
    this.action = action;
    this.increment = increment;
    this.time = delay;
  }

  public synchronized void ping() {
    if (time >= delay) {
      startTimer(0);
    } else resetTimer();
  }

  private synchronized void resetTimer() {
    time = 0;
  }

  private synchronized void startTimer(int start) {
    time = start;
    new Thread(this).start();
  }

  public void run() {
    try {
      synchronized (this) {
        wait(increment);
      }
      increment();
      if (time >= delay) {
        time = delay;
        action.action();
      } else {
        update.action();
        run();
      }
    } catch (InterruptedException e) {
      e.printStackTrace(System.err);
    }
  }

  private void increment() {
    time += increment;
  }

  public void paint(GC gc, int x, int y, int width, int height) {
    drawPie(gc, x, y, width, height);
  }

  void drawPie(GC g, int x, int y, int width, int height) {
    double curValue = 0.0D;
    int startAngle = 0;
    startAngle = (int) (curValue * 360 / delay);
    int arcAngle = (int) (time * 360 / delay);
    g.setBackground(GREEN);
    g.setForeground(BLACK);
    g.fillArc(x, y, width, height, startAngle, arcAngle);
    g.drawOval(x, y, width, height);
  }

  public String toolTip() {
    return "timer";
  }

  public synchronized void click(TextEditor editor) {
    if (time == delay)
      startTimer(delay);
    else time = delay;
  }

  public void rightClick(int x,int y) {
    TimerDialog dialog = new TimerDialog(XModeler.getXModeler(), delay, SWT.DIALOG_TRIM);
    int newDelay = dialog.open(x,y);
    if(newDelay >= 0) {
      delay = newDelay;
      increment = (int)delay/10;
      time = delay;
    }
  }
}
