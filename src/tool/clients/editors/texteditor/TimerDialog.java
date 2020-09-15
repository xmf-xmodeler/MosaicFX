package tool.clients.editors.texteditor;

public class TimerDialog {

  int    delay;
//  Slider slider;

  public TimerDialog(Object parent, double delay, int style) {
//    super(parent, style);
//    this.delay = delay;   
  }

  public int open(int x,int y) {
    // Create the dialog window
//    Shell shell = new Shell(getParent(), getStyle());
//    shell.setText(getText());
//    createContents(shell);
//    shell.pack();
//    shell.setLocation(x, y);
//    shell.open();
//    Display display = getParent().getDisplay();
//    while (!shell.isDisposed()) {
//      if (!display.readAndDispatch()) {
//        display.sleep();
//      }
//    }
    return delay;
  }

//  private void createContents(final Shell shell) {
//    shell.setLayout(new GridLayout(1, true));
//
//    // Show the message
//    Label label = new Label(shell, SWT.NONE);
//    label.setText("Delay: " + delay);
//    GridData data = new GridData();
//    data.horizontalSpan = 2;
//    label.setLayoutData(data);
//    
//    slider = new Slider(shell,SWT.VERTICAL);
//    slider.setMinimum(100);
//    slider.setMaximum(3000);
//    slider.setSelection(delay);
//    slider.addSelectionListener(new SelectionListener() {
//
//      public void widgetDefaultSelected(SelectionEvent arg0) {
//        
//      }
//
//      public void widgetSelected(SelectionEvent arg0) {
//        delay = slider.getSelection();
//        label.setText("Delay: " + delay);
//      }});
//
//    // Create the OK button and add a handler
//    // so that pressing it will set input
//    // to the entered value
//    Button ok = new Button(shell, SWT.PUSH);
//    ok.setText("OK");
//    data = new GridData(GridData.FILL_HORIZONTAL);
//    ok.setLayoutData(data);
//    ok.addSelectionListener(new SelectionAdapter() {
//      public void widgetSelected(SelectionEvent event) {
//        delay = slider.getSelection();
//        shell.close();
//      }
//    });
//
//    // Create the cancel button and add a handler
//    // so that pressing it will set input to null
//    Button cancel = new Button(shell, SWT.PUSH);
//    cancel.setText("Cancel");
//    data = new GridData(GridData.FILL_HORIZONTAL);
//    cancel.setLayoutData(data);
//    cancel.addSelectionListener(new SelectionAdapter() {
//      public void widgetSelected(SelectionEvent event) {
//        delay = -1;
//        shell.close();
//      }
//    });
//
//    // Set the OK button as the default, so
//    // user can type input and press Enter
//    // to dismiss
//    shell.setDefaultButton(ok);
//  }
}
