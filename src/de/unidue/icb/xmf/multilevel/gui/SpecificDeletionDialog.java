package de.unidue.icb.xmf.multilevel.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.ModifyListener;
//import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class SpecificDeletionDialog {
	
	private static SpecificDeletion specificDeletion; 
	
	private static Display display;
	
//	private static Text txExpression;
//	
//	private static Button btnDeleteSlot;
//	private static Button radioAll;
//	private static Button radioNull ;
//	private static Button radioExpression;
	
	private static Scale scLevel;

	public static Composite coInst;

	private static Composite[] coListCon;
	private static Composite[] coListCol ;
	private static Label[] lblList;
	
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		int definitionLayer = 5;
		int instantiationLayer = 0;
		System.out.println(showDialog(definitionLayer, instantiationLayer));
	}
	/**
	 * 
	 */
	public static SpecificDeletion showDialog(int definitionLayer, int instantiationLayer) {
		specificDeletion = new SpecificDeletion(definitionLayer, instantiationLayer);
		
		Display.getDefault().syncExec(new Runnable() {
			private Shell shlIntrinsicDeletion;

			public void run() {
		display = Display.getDefault();
		shlIntrinsicDeletion = new Shell();
		shlIntrinsicDeletion.setSize(255, 120+(specificDeletion.getInstantiationLayer()-specificDeletion.getInstantiationLayer())*25);
		shlIntrinsicDeletion.setText("intrinsic Deletion");
		RowLayout rl_shlIntrinsicDeletion = new RowLayout(SWT.VERTICAL);
		shlIntrinsicDeletion.setLayout(rl_shlIntrinsicDeletion);
		
		Label lblDeletion = new Label(shlIntrinsicDeletion, SWT.NONE);
		lblDeletion.setText("Deletion attribute from M"+specificDeletion.getDefinitionLayer()+", intrinsic on M"+specificDeletion.getInstantiationLayer());
		
		Composite composite = new Composite(shlIntrinsicDeletion, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.VERTICAL);
		rl_composite.spacing = 0;
		rl_composite.marginTop = 0;
		rl_composite.marginRight = 0;
		rl_composite.marginLeft = 0;
		rl_composite.marginBottom = 0;
		composite.setLayout(rl_composite);
		
		Composite composite_8 = new Composite(composite, SWT.NONE);
		RowLayout rl_composite_8 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_8.spacing = 0;
		rl_composite_8.marginTop = 0;
		rl_composite_8.marginRight = 0;
		rl_composite_8.marginLeft = 0;
		rl_composite_8.marginBottom = 0;
		composite_8.setLayout(rl_composite_8);
		
		Composite composite_4 = new Composite(composite_8, SWT.NONE);
		RowLayout rl_composite_4 = new RowLayout(SWT.VERTICAL);
		rl_composite_4.spacing = 0;
		rl_composite_4.marginTop = 0;
		rl_composite_4.marginRight = 0;
		rl_composite_4.marginLeft = 0;
		rl_composite_4.marginBottom = 0;
		composite_4.setLayout(rl_composite_4);
		
		coListCon = new Composite[specificDeletion.getDefinitionLayer()-specificDeletion.getInstantiationLayer()];
		coListCol = new Composite[specificDeletion.getDefinitionLayer()-specificDeletion.getInstantiationLayer()];
		lblList = new Label[specificDeletion.getDefinitionLayer()-specificDeletion.getInstantiationLayer()];
		
		for(int i = specificDeletion.getDefinitionLayer(); i > specificDeletion.getInstantiationLayer(); i--){
			coListCon[specificDeletion.getDefinitionLayer()-i] = new Composite(composite_4, SWT.NONE);
			GridLayout gl_composite_1 = new GridLayout(2, true);
			gl_composite_1.marginWidth = 0;
			gl_composite_1.verticalSpacing = 0;
			gl_composite_1.marginHeight = 0;
			gl_composite_1.horizontalSpacing = 0;
			coListCon[specificDeletion.getDefinitionLayer()-i].setLayout(gl_composite_1);
			coListCon[specificDeletion.getDefinitionLayer()-i].setLayoutData(new RowData(50, 25));
			//definitionLayer-i
			coListCol[specificDeletion.getDefinitionLayer()-i] = new Composite(coListCon[specificDeletion.getDefinitionLayer()-i], SWT.NONE);
			GridData gd_compositeX = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
			gd_compositeX.heightHint = 25;
			gd_compositeX.widthHint = 25;
			coListCol[specificDeletion.getDefinitionLayer()-i].setLayoutData(gd_compositeX);
			
			lblList[specificDeletion.getDefinitionLayer()-i] = new Label(coListCon[specificDeletion.getDefinitionLayer()-i], SWT.CENTER);
			lblList[specificDeletion.getDefinitionLayer()-i].setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
			lblList[specificDeletion.getDefinitionLayer()-i].setText("M"+i);
		}
		
		coListCol[0].setBackground(display.getSystemColor(SWT.COLOR_RED));
		
		Composite composite_3 = new Composite(composite_8, SWT.NONE);
		RowLayout rl_composite_3 = new RowLayout(SWT.VERTICAL);
		rl_composite_3.spacing = 0;
		rl_composite_3.marginTop = 0;
		rl_composite_3.marginRight = 0;
		rl_composite_3.marginLeft = 0;
		rl_composite_3.marginBottom = 0;
		composite_3.setLayout(rl_composite_3);
		
		scLevel = new Scale(composite_3, SWT.VERTICAL);
		scLevel.setLayoutData(new RowData(SWT.DEFAULT, (specificDeletion.getDefinitionLayer()-specificDeletion.getInstantiationLayer())*25));
		scLevel.setIncrement(2);
		scLevel.setPageIncrement(1);
		scLevel.setMaximum(specificDeletion.getDefinitionLayer()-specificDeletion.getInstantiationLayer()-1);
		scLevel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				for(int i = 0; i < coListCol.length; i++){
					if(i <= scLevel.getSelection()){
						coListCol[i].setBackground(display.getSystemColor(SWT.COLOR_RED));
//						if(i+1 == coListCol.length){
//							coInst.setBackground(display.getSystemColor(SWT.COLOR_RED));
//							specificDeletion.setDeleteSlotValues(true);
//						}
					}else{
						coListCol[i].setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//						if(i+1 == coListCol.length){
//							coInst.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//							specificDeletion.setDeleteSlotValues(false);
//						}
					}
				}	
				//specificDeletion.getDefinitionLayer()
				specificDeletion.setDeletionUntilLayer(specificDeletion.getDefinitionLayer()-scLevel.getSelection());
				if(scLevel.getSelection()==coListCol.length-1){
					coInst.setBackground(display.getSystemColor(SWT.COLOR_RED));
					specificDeletion.setDeletionUntilLayer(specificDeletion.getDeletionUntilLayer()-1);
				}else{
					coInst.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				}
					
			}
		});

		
		Composite composite_14 = new Composite(composite, SWT.NONE);
		RowLayout rl_composite_14 = new RowLayout(SWT.HORIZONTAL);
		rl_composite_14.marginBottom = 0;
		rl_composite_14.marginLeft = 0;
		rl_composite_14.marginTop = 0;
		rl_composite_14.marginRight = 0;
		rl_composite_14.spacing = 0;
		composite_14.setLayout(rl_composite_14);
		
		Composite composite_7 = new Composite(composite_14, SWT.NONE);
		GridLayout gl_composite_7 = new GridLayout(2, true);
		gl_composite_7.verticalSpacing = 0;
		gl_composite_7.marginWidth = 0;
		gl_composite_7.marginHeight = 0;
		gl_composite_7.horizontalSpacing = 0;
		composite_7.setLayout(gl_composite_7);
		
		coInst = new Composite(composite_7, SWT.NONE);
		GridData gd_coInst = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_coInst.heightHint = 25;
		gd_coInst.widthHint = 25;
		coInst.setLayoutData(gd_coInst);
		
		Label lblNewLabel_3 = new Label(composite_7, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("M"+specificDeletion.getInstantiationLayer());
		
//		Composite composite_9 = new Composite(composite_14, SWT.NONE);
//		composite_9.setLayout(new RowLayout(SWT.VERTICAL));
//		
//		btnDeleteSlot = new Button(composite_9, SWT.CHECK);
//		btnDeleteSlot.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				specificDeletion.setDeleteSlotValues(SpecificDeletionDialog.btnDeleteSlot.getSelection());
//				if(SpecificDeletionDialog.btnDeleteSlot.getSelection()){
//					coInst.setBackground(display.getSystemColor(SWT.COLOR_RED));
//					SpecificDeletionDialog.radioAll.setEnabled(true);
//					SpecificDeletionDialog.radioExpression.setEnabled(true);
//					SpecificDeletionDialog.radioNull.setEnabled(true);
//					if(SpecificDeletionDialog.radioExpression.getSelection()){
//						txExpression.setEnabled(true);
//					}else{
//						txExpression.setEnabled(false);
//					}
//				}else{
//					coInst.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//					SpecificDeletionDialog.radioAll.setEnabled(false);
//					SpecificDeletionDialog.radioExpression.setEnabled(false);
//					SpecificDeletionDialog.radioNull.setEnabled(false);
//					txExpression.setEnabled(false);
//				}
//			}
//		});
//		btnDeleteSlot.setText("Delete Slot Values");
//		
//		radioAll = new Button(composite_9, SWT.RADIO);
//		radioAll.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				specificDeletion.setDeleteMode(SpecificDeletion.deleteMode_All );
//			}
//		});
//		radioAll.setSelection(true);
//		radioAll.setText("All");
//		radioAll.setEnabled(false);
//		
//		radioNull = new Button(composite_9, SWT.RADIO);
//		radioNull.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				specificDeletion.setDeleteMode(SpecificDeletion.deleteMode_NullValue);
//			}
//		});
//		radioNull.setText("Null Values");
//		radioNull.setEnabled(false);
		
//		radioExpression = new Button(composite_9, SWT.RADIO);
//		radioExpression.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				specificDeletion.setDeleteMode(SpecificDeletion.deleteMode_Expression );
//				if(SpecificDeletionDialog.radioExpression.getSelection()){
//					txExpression.setEnabled(true);
//				}else{
//					txExpression.setEnabled(false);
//				}
//			}
//		});
//		radioExpression.setText("With Expression");
//		radioExpression.setEnabled(false);
//		
//		txExpression = new Text(composite_9, SWT.BORDER);
//		txExpression.addModifyListener(new ModifyListener() {
//			public void modifyText(ModifyEvent e) {
//				specificDeletion.setDeleteExpression(txExpression.getText());
//			}
//		});
//		txExpression.setLayoutData(new RowData(116, SWT.DEFAULT));
//		txExpression.setEnabled(false);
		
		Composite composite_10 = new Composite(shlIntrinsicDeletion, SWT.NONE);
		composite_10.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_10.setLayoutData(new RowData(192, SWT.DEFAULT));
		
		Button btnNewButton = new Button(composite_10, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shlIntrinsicDeletion.close();
			}
		});
		btnNewButton.setText("Ok");
		
		Button btnNewButton_1 = new Button(composite_10, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shlIntrinsicDeletion.close();
			}
		});
		btnNewButton_1.setText("Cancel");

		shlIntrinsicDeletion.layout();
		shlIntrinsicDeletion.pack();
		shlIntrinsicDeletion.open();
		
		while (!shlIntrinsicDeletion.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
			}
		});
		return specificDeletion;
	}
}
