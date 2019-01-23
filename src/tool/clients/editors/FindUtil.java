package tool.clients.editors;

import org.fxmisc.richtext.InlineCssTextArea;

import tool.xmodeler.XModeler;

public class FindUtil {

  static String     selectedText = "initial xxx";
//  static StyledText keywordText;
  static String     keyword;
//  static Button     button;

  public static void show3(InlineCssTextArea textArea){
	  SearchWindow sw = new SearchWindow(textArea, XModeler.getStage());
	  sw.show();
  }
  
//  public static void show(Shell shell, final StyledText styledText) {
//    Menu menu = new Menu(shell);
//    GuiConfigData config = new GuiConfigData();
//    GuiWidgets widgets = new GuiWidgets(config, new HashSet());
//    FindReplace findReplace = new FindReplace(shell, config, styledText, widgets.getEditMenu());
//  }

//  public static String show2(Shell shell, final StyledText styledText) {
//
//    final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM);
//    dialog.setText("Find Text");
//    dialog.setSize(250, 150);
//
//    GridLayout layout = new GridLayout();
//    layout.numColumns = 2;
//    dialog.setLayout(layout);
//    dialog.addShellListener(new ShellAdapter() {
//      public void shellClosed(ShellEvent e) {
//        // don't dispose of the shell, just hide it for later use
//        e.doit = false;
//        dialog.setVisible(false);
//      }
//    });
//    Display display = dialog.getDisplay();
//
//    dialog.setLayout(new GridLayout(1, false));
//
//    // styledText = new StyledText(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//    GridData gridData = new GridData(GridData.FILL_BOTH);
//    gridData.horizontalSpan = 2;
//
//    keywordText = new StyledText(dialog, SWT.SINGLE | SWT.BORDER);
//    keywordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//    // Font font = new Font(shell.getDisplay(), "Courier New", 12, SWT.NORMAL);
//
//    button = new Button(dialog, SWT.PUSH);
//    button.setText("Find");
//
//    button.addSelectionListener(new SelectionAdapter() {
//      public void widgetSelected(SelectionEvent e) {
//        keyword = keywordText.getText();
//        styledText.redraw();
//      }
//    });
//
//    styledText.addLineStyleListener(new LineStyleListener() {
//      public void lineGetStyle(LineStyleEvent event) {
//        if (keyword == null || keyword.length() == 0) {
//          event.styles = new StyleRange[0];
//          return;
//        }
//
//        String line = event.lineText;
//        int cursor = -1;
//
//        LinkedList list = new LinkedList();
//        while ((cursor = line.indexOf(keyword, cursor + 1)) >= 0) {
//          list.add(getHighlightStyle(event.lineOffset + cursor, keyword.length(), dialog));
//        }
//
//        event.styles = (StyleRange[]) list.toArray(new StyleRange[list.size()]);
//      }
//    });
//
//    dialog.open();
//
//    while (!dialog.isDisposed()) {
//      if (!display.readAndDispatch()) {
//        display.sleep();
//      }
//    }
//
//    return selectedText;
//  }

//  private static StyleRange getHighlightStyle(int startOffset, int length, Shell shell) {
//    StyleRange styleRange = new StyleRange();
//    styleRange.start = startOffset;
//    styleRange.length = length;
//    styleRange.background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
//    return styleRange;
//  }
}