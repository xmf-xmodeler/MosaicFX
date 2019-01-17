package de.unidue.icb.xmf.multilevel.gui;

public class OverallDeletionDialog {

	/**
	 * Launch the application.
	 * @param args
	 */

	public static DeletionSettings deletionSetting; 
	
//	static Display display;
//	static Shell shlDeletionSettings;
//	
//	public static Composite coCurrent;
//	public static Composite coAll;
//	public static Composite coSlotValue;
//	
//	public static Button btnAlways;
//	public static Button btnAsk;
//	public static Button btnCurrent;
//	public static Button btnAll;
//	public static Button btnDeleteSlot;
//	public static Button btnOnlyNull; 
	
	/**
	 * 
	 */
	public static DeletionSettings showDialog() {
		
		throw new RuntimeException("Haven't implemented it yet");
		
//		Display.getDefault().syncExec(new Runnable() {
//		    public void run() {
//		display = Display.getDefault();
//		shlDeletionSettings = new Shell();
//		
//		shlDeletionSettings.setSize(323, 207);
//		shlDeletionSettings.setText("Deletion Settings");
//		shlDeletionSettings.setLayout(new RowLayout(SWT.VERTICAL));
//		
//		btnAlways = new Button(shlDeletionSettings, SWT.RADIO);
//		btnAlways.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				OverallDeletionDialog.btnCurrent.setEnabled(true);
//				OverallDeletionDialog.btnAll.setEnabled(true);
//				OverallDeletionDialog.btnDeleteSlot.setEnabled(true);
//				OverallDeletionDialog.btnOnlyNull.setEnabled(true);
//				if(btnAll.getSelection()){
//					coCurrent.setBackground(display.getSystemColor(SWT.COLOR_RED));
//					coAll.setBackground(display.getSystemColor(SWT.COLOR_RED));
//				}else{
//					coAll.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
//					if(btnCurrent.getSelection()){
//						coCurrent.setBackground(display.getSystemColor(SWT.COLOR_RED));
//					}else{
//						coCurrent.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
//					}
//				}
//				if(btnDeleteSlot.getSelection()){
//					coSlotValue.setBackground(display.getSystemColor(SWT.COLOR_RED));
//				}else{
//					coSlotValue.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
//				}
//				deletionSetting.setGlobalSettings(btnAlways.getSelection());
//			}
//		});
//		btnAlways.setSelection(true);
//		btnAlways.setText("Always...");
//		
//		Composite composite = new Composite(shlDeletionSettings, SWT.NONE);
//		composite.setLayoutData(new RowData(300, SWT.DEFAULT));
//		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
//		rl_composite.fill = true;
//		composite.setLayout(rl_composite);
//		
//		Composite composite_1 = new Composite(composite, SWT.BORDER);
//		composite_1.setLayout(new FillLayout(SWT.VERTICAL));
//		composite_1.setLayoutData(new RowData(27, 81));
//		
//		coCurrent = new Composite(composite_1, SWT.NONE);
//		coCurrent.setBackground(display.getSystemColor(SWT.COLOR_RED));
//		
//		coAll = new Composite(composite_1, SWT.NONE);
//		coAll.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
//		
//		coSlotValue = new Composite(composite_1, SWT.NONE);
//		coSlotValue.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
//		
//		Composite composite_2 = new Composite(composite, SWT.NONE);
//		composite_2.setLayout(new FillLayout(SWT.VERTICAL));
//		composite_2.setLayoutData(new RowData(258, 80));
//		
//		btnCurrent = new Button(composite_2, SWT.RADIO);
//		btnCurrent.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				OverallDeletionDialog.coCurrent.setBackground(display.getSystemColor(SWT.COLOR_RED));
//				OverallDeletionDialog.coAll.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
//				deletionSetting.setDeleteAll(btnAll.getSelection());
//			}
//		});
//		btnCurrent.setSelection(true);
//		btnCurrent.setText("Current Level only ");
//		
//		btnAll = new Button(composite_2, SWT.RADIO);
//		btnAll.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				OverallDeletionDialog.coCurrent.setBackground(display.getSystemColor(SWT.COLOR_RED));
//				OverallDeletionDialog.coAll.setBackground(display.getSystemColor(SWT.COLOR_RED));
//				deletionSetting.setDeleteAll(btnAll.getSelection());
//			}
//		});
//		btnAll.setText("All Levels");
//		
//		Composite composite_6 = new Composite(composite_2, SWT.NONE);
//		composite_6.setLayout(new FillLayout(SWT.HORIZONTAL));
//		
//		btnDeleteSlot = new Button(composite_6, SWT.CHECK);
//		btnDeleteSlot.getSelection();
//		btnDeleteSlot.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				if( ((Button) e.getSource()).getSelection()){
//					OverallDeletionDialog.coSlotValue.setBackground(display.getSystemColor(SWT.COLOR_RED));
//				}else{
//					OverallDeletionDialog.coSlotValue.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
//				}
//				deletionSetting.setDeleteSlotValues(btnDeleteSlot.getSelection());	
//			}
//		});
//		btnDeleteSlot.setText("Delete Slot Values");
//		
//		btnOnlyNull = new Button(composite_6, SWT.CHECK);
//		btnOnlyNull.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				deletionSetting.setDeleteOnlyNull(btnOnlyNull.getSelection());
//			}
//		});
//		btnOnlyNull.setText("Null Values only");
//		
//		btnAsk = new Button(shlDeletionSettings, SWT.RADIO);
//		btnAsk.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				OverallDeletionDialog.btnCurrent.setEnabled(false);
//				OverallDeletionDialog.btnAll.setEnabled(false);
//				OverallDeletionDialog.btnDeleteSlot.setEnabled(false);
//				OverallDeletionDialog.btnOnlyNull.setEnabled(false);
//				coCurrent.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//				coAll.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//				coSlotValue.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//				deletionSetting.setGlobalSettings(btnAlways.getSelection());
//			}
//		});
//		btnAsk.setText("Ask every time");
//		
//		Composite composite_7 = new Composite(shlDeletionSettings, SWT.NONE);
//		composite_7.setLayoutData(new RowData(298, SWT.DEFAULT));
//		composite_7.setLayout(new FillLayout(SWT.HORIZONTAL));
//		
//		Button btnOk = new Button(composite_7, SWT.NONE);
//		btnOk.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				//e.display.dispose();
//				shlDeletionSettings.close();
//			}
//		});
//		btnOk.setText("Ok");
//		
//		Button btnCancel = new Button(composite_7, SWT.NONE);
//		btnCancel.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
////				e.display.dispose();
//				shlDeletionSettings.close();
//			}
//		});
//		btnCancel.setText("Cancel");
//		
//		deletionSetting = new DeletionSettings(btnAlways.getSelection(), btnAll.getSelection(), btnDeleteSlot.getSelection(), btnOnlyNull.getSelection());
//		
//		shlDeletionSettings.open();
//		shlDeletionSettings.layout();
//		while (!shlDeletionSettings.isDisposed()) {
//			if (!display.readAndDispatch()) {
//				display.sleep();
//			}
//		}
//		    }
//		});
//		return deletionSetting;
	}
}
