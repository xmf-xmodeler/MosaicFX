package tool.xmodeler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

//import tool.clients.editors.EditorResizeListener;

public class SplitSashForm extends SashForm {

  CTabFolder folder1 = new CTabFolder(this, SWT.BORDER);
  CTabFolder folder2 = new CTabFolder(this, SWT.BORDER);

  public SplitSashForm(Composite parent) {
    super(parent, SWT.HORIZONTAL);
    addDragListener(folder1);
    addDragListener(folder2);
    setMaximizedControl(folder1);
    folder1.setMaximizeVisible(true);
    folder2.setMaximizeVisible(true);
    folder1.setMinimizeVisible(true);
    folder2.setMinimizeVisible(true);
//    folder1.addCTabFolder2Listener(new EditorResizeListener());
//    folder2.addCTabFolder2Listener(new EditorResizeListener());
  }

  public CTabFolder getFolder1() {
    return folder1;
  }

  public CTabFolder getFolder2() {
    return folder2;
  }

  private void addDragListener(CTabFolder folder) {
    Listener dragListener = new Listener() {
      private CTabItem dragItem;

      public void handleEvent(Event event) {
        Point mouseLocation = new Point(event.x, event.y);
        switch (event.type) {
          case SWT.DragDetect: {
            CTabItem item = folder.getItem(mouseLocation);
            if (dragItem == null && item != null) {
              dragItem = item;
              folder.setCapture(true);
            }
            break;
          }
          case SWT.MouseUp: {
            if (dragItem != null && !folder.getBounds().contains(mouseLocation)) {
              if (mouseLocation.x >= folder.getBounds().x && mouseLocation.x <= folder.getBounds().x + folder.getBounds().width)
                setOrientation(SWT.VERTICAL);
              else setOrientation(SWT.HORIZONTAL);
              drop(dragItem, folder);
              dragItem.dispose();
              dragItem = null;
            }
            break;
          }
          default:
            System.err.println(event);
        }
      }
    };
    folder.addListener(SWT.DragDetect, dragListener);
    folder.addListener(SWT.MouseUp, dragListener);
  }

  private void drop(CTabItem tab, CTabFolder folder) {
    CTabFolder source = folder == folder1 ? folder : folder2;
    CTabFolder target = folder == folder2 ? folder1 : folder2;
    CTabItem newTab = new CTabItem(target, SWT.CLOSE, target.getItemCount());
    newTab.setText(tab.getText());
    Control control = tab.getControl();
    tab.setControl(null);
    tab.dispose();
    control.setParent(target);
    newTab.setControl(control);
    target.setSelection(newTab);
    if (source.getItemCount() == 0)
      setMaximizedControl(target);
    else if (target.getItemCount() == 0)
      setMaximizedControl(source);
    else setMaximizedControl(null);
  }

}
