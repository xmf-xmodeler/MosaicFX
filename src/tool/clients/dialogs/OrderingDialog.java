package tool.clients.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class OrderingDialog extends Dialog {

  // This dialog allows a list of elements to be ordered.

  private String   text;
  private String   message;
  private String[] sources;
  private String[] targets;

  private List     sourceList;
  private List     targetList;

  public OrderingDialog(Shell shell, String text, String message, String[] sources) {

    // The dialog window text is 'text'. The dialog has some explanatory
    // text as 'message'. The source list of elements is moved to the
    // target list of elements. Buttons are provided to move source elements
    // to the target elements and to remove elements from the target list.

    super(shell);
    this.text = text;
    this.message = message;
    this.sources = sources;
  }

  protected void configureShell(Shell shell) {
    super.configureShell(shell);
    shell.setText(text);
  }

  public void okPressed() {
    targets = targetList.getItems();
    super.okPressed();
  }

  protected Control createDialogArea(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    layout.makeColumnsEqualWidth = false;
    layout.horizontalSpacing = 5;
    layout.verticalSpacing = 10;
    container.setLayout(layout);
    createScrollArea(container);
    createSourceList(container).setLayoutData(new GridData(GridData.FILL_BOTH));
    createButtonArea(container);
    createTargetList(container).setLayoutData(new GridData(GridData.FILL_BOTH));
    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.horizontalSpan = 3;
    Dialog.applyDialogFont(container);
    return parent;
  }

  private Composite createScrollArea(Composite parent) {
    Group container = new Group(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    layout.marginWidth = layout.marginHeight = 6;
    container.setLayout(layout);

    GridData gd = new GridData(GridData.FILL_HORIZONTAL);
    gd.widthHint = 150;
    gd.horizontalSpan = 3;
    container.setLayoutData(gd);
    container.setText("Message");

    Label filterLabel = new Label(container, SWT.WRAP);
    filterLabel.setText(message);
    GridData gd2 = new GridData(GridData.FILL_BOTH);
    gd2.heightHint = 50;
    filterLabel.setLayoutData(gd2);

    return container;
  }

  private Composite createSourceList(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    container.setLayout(layout);
    container.setLayoutData(new GridData());
    Label label = new Label(container, SWT.NONE);
    label.setText("Source List");
    sourceList = new List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.widthHint = 225;
    gd.heightHint = 200;
    sourceList.setLayoutData(gd);
    sourceList.setItems(sources);
    return container;
  }

  protected Composite createTargetList(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 0;
    container.setLayout(layout);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    Label label = new Label(container, SWT.NONE);
    label.setText("Target List");
    targetList = new List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
    GridData gd = new GridData(GridData.FILL_BOTH);
    gd.widthHint = 225;
    gd.heightHint = 200;
    targetList.setLayoutData(gd);
    return container;
  }

  private Composite createButtonArea(Composite parent) {
    Composite comp = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.marginWidth = layout.marginHeight = 0;
    comp.setLayout(layout);
    comp.setLayoutData(new GridData(GridData.FILL_VERTICAL));
    Composite container = new Composite(comp, SWT.NONE);
    layout = new GridLayout();
    layout.marginWidth = 0;
    layout.marginHeight = 30;
    container.setLayout(layout);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));

    Button button = new Button(container, SWT.PUSH);
    button.setText("Add >>");
    button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    button.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int selectedIndex = sourceList.getSelectionIndex();
        if (selectedIndex >= 0) {
          String selectedText = sourceList.getItem(selectedIndex);
          sourceList.remove(selectedIndex);
          targetList.add(selectedText);
        }
      }
    });

    button = new Button(container, SWT.PUSH);
    button.setText("<< Remove");
    button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    button.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int selectedIndex = targetList.getSelectionIndex();
        if (selectedIndex >= 0) {
          String selectedText = targetList.getItem(selectedIndex);
          targetList.remove(selectedIndex);
          sourceList.add(selectedText);
        }
      }
    });

    new Label(container, SWT.NONE);

    Button up = new Button(container, SWT.PUSH);
    up.setText("Move up");
    up.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    up.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int selectedIndex = targetList.getSelectionIndex();
        if (selectedIndex - 1 >= 0) {
          String selectedText = targetList.getItem(selectedIndex);
          targetList.remove(selectedIndex);
          targetList.add(selectedText, selectedIndex - 1);
          targetList.select(selectedIndex - 1);
        }
      }
    });

    Button down = new Button(container, SWT.PUSH);
    down.setText("Move down");
    down.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    down.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int selectedIndex = targetList.getSelectionIndex();
        if (selectedIndex + 1 < targetList.getItemCount()) {
          String selectedText = targetList.getItem(selectedIndex);
          targetList.remove(selectedIndex);
          targetList.add(selectedText, selectedIndex + 1);
          targetList.select(selectedIndex + 1);
        }
      }
    });
    return container;
  }

  public String[] getChoice() {
    return targets;
  }
}
