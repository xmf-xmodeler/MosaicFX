/*
 * SWTextEdit
 * Copyright (C) 2006 Philipp Seerainer
 * pisee@users.sourceforge.net
 * http://pisee.sourceforge.net/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package net.sf.pisee.swtextedit.dialog;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sf.pisee.swtextedit.config.GuiConfigData;
import net.sf.pisee.swtextedit.config.GuiIcons;
import net.sf.pisee.swtextedit.util.LangUtil;
import net.sf.pisee.swtextedit.widgets.ButtonUtil;
import net.sf.pisee.swtextedit.widgets.GridUtil;
import net.sf.pisee.swtextedit.widgets.GroupUtil;
import net.sf.pisee.swtextedit.widgets.LabelUtil;
import net.sf.pisee.swtextedit.widgets.ShellUtil;
import net.sf.pisee.swtextedit.widgets.TextUtil;

/**
 * Find / Replace dialog.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class FindReplace {

  /** Instances of the Buttons. */
  private Button        forward, backward, sensitiveButton, findButton, replacefindButton, replaceButton, replaceallButton;

  /** Instance of GuiConfigData for all configuration data. */
  private GuiConfigData configData;

  /** HashSet for the language control. */
  private HashSet       widgets = new HashSet();

  /** Shows infos of the dialog box. */
  private Label         info;

  /** Instance of the menu to get the menu item for this class. */
  private Menu          edit;

  /** Instance of Shell. */
  private Shell         dialog;

  /** Instance of StyledText. */
  private StyledText    text;

  /** Instances of the text fields. */
  private Text          searchText, replaceText;

  /** Private empty constructor. */
  private FindReplace() {
  }

  /**
   * Public constructor.
   * 
   * @param parent
   *          The parent of the dialog.
   * @param configData
   *          The configuration values of the GUI.
   * @param text
   *          The text widget for the text file.
   * @param edit
   *          The edit menu of the drop down menu.
   */
  public FindReplace(Shell parent, GuiConfigData configData, StyledText text, Menu edit) {
    this.configData = configData;
    this.text = text;
    this.edit = edit;

    shell(parent);
    search();
    options();
    buttons();

    dialog.open();

    LangUtil.setLang(widgets, configData);
  }

  /**
   * Creates a new Shell for the find / replace dialog.
   * 
   * @param parent
   *          The parent of the dialog.
   */
  private void shell(Shell parent) {
    edit.getItem(11).setEnabled(false);

    dialog = ShellUtil.newShell(parent, SWT.TOOL | SWT.DIALOG_TRIM, "find", GuiIcons.text, 300, 230, GridUtil.newGridLayout(5, 5, 5, 5, 4, true), true, false);
    widgets.add(dialog);

    dialog.addShellListener(shellFocus);
    dialog.addShellListener(shellExit);
  }

  /**
   * The two text fields find and replace.
   */
  private void search() {
    GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);

    widgets.add(LabelUtil.newLabel(dialog, gridData, "find_find_text"));

    searchText = TextUtil.newText(dialog, SWT.SINGLE | SWT.BORDER, GridUtil.newGridData(3, 1), true, true);
    searchText.addModifyListener(searchModify);
    searchText.setFocus();

    widgets.add(LabelUtil.newLabel(dialog, gridData, "find_replace_text"));

    replaceText = TextUtil.newText(dialog, SWT.SINGLE | SWT.BORDER, GridUtil.newGridData(3, 1), false, true);
    replaceText.addModifyListener(replaceModify);
  }

  /**
   * Options for direction, scope and case sensitive.
   */
  private void options() {
    Group direction = GroupUtil.newGroup(dialog, SWT.SHADOW_IN, GridUtil.newGridData(2, 1), GridUtil.newGridLayout(5, 5, 5, 5, 1, false), "find_direction");
    widgets.add(direction);

    forward = ButtonUtil.newButton(direction, SWT.RADIO, "find_direction_forward", GridUtil.newGridData(), null);
    widgets.add(forward);
    forward.setSelection(true);
    backward = ButtonUtil.newButton(direction, SWT.RADIO, "find_direction_backward", GridUtil.newGridData(), null);
    widgets.add(backward);

    Group options = GroupUtil.newGroup(dialog, SWT.SHADOW_IN, GridUtil.newGridData(2, 1), GridUtil.newGridLayout(5, 5, 5, 5, 1, false), "find_options");
    widgets.add(options);

    sensitiveButton = ButtonUtil.newButton(options, SWT.CHECK, "find_options_sensitive", GridUtil.newGridData(), sensitive);
    widgets.add(sensitiveButton);
  }

  /**
   * Buttons for find, replace/find, replace and replace all.
   */
  private void buttons() {
    findButton = ButtonUtil.newButton(dialog, SWT.PUSH, "find_find", GridUtil.newGridData(2, 1), find);
    widgets.add(findButton);
    findButton.setEnabled(false);
    findButton.setLocation(dialog.getBounds().width / 4, dialog.getBounds().height - 100);
    dialog.setDefaultButton(findButton);

    replacefindButton = ButtonUtil.newButton(dialog, SWT.PUSH, "find_replacefind", GridUtil.newGridData(2, 1), replacefind);
    widgets.add(replacefindButton);
    replacefindButton.setEnabled(false);

    replaceButton = ButtonUtil.newButton(dialog, SWT.PUSH, "find_replace", GridUtil.newGridData(2, 1), replace);
    widgets.add(replaceButton);
    replaceButton.setEnabled(false);

    replaceallButton = ButtonUtil.newButton(dialog, SWT.PUSH, "find_replaceall", GridUtil.newGridData(2, 1), replaceall);
    widgets.add(replaceallButton);
    replaceallButton.setEnabled(false);

    info = LabelUtil.newLabel(dialog, new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1), null);

    widgets.add(ButtonUtil.newButton(dialog, SWT.PUSH, "button_close", GridUtil.newGridData(2, 1), close));
  }

  /** Modify listener for the search field. */
  private ModifyListener    searchModify  = new ModifyListener() {
                                            public void modifyText(ModifyEvent e) {
                                              enableButtons();
                                            }
                                          };

  /** Modify listener for the replace field. */
  private ModifyListener    replaceModify = new ModifyListener() {
                                            public void modifyText(ModifyEvent e) {
                                              enableButtons();
                                            }
                                          };

  /** Listener for the sensitive check button. */
  private SelectionListener sensitive     = new SelectionAdapter() {
                                            public void widgetSelected(SelectionEvent e) {
                                              enableButtons();
                                            }
                                          };

  /** Listener for the find button. */
  private SelectionListener find          = new SelectionAdapter() {
                                            public void widgetSelected(SelectionEvent e) {
                                              if (findEntry()) {
                                                info.setText("");
                                              } else {
                                                info.setText(configData.getLangRes().getString("find_notfind"));
                                              }
                                              enableButtons();
                                              text.setFocus();
                                              dialog.setFocus();
                                            }
                                          };

  /** Listener for the replace/find button. */
  private SelectionListener replacefind   = new SelectionAdapter() {
                                            public void widgetSelected(SelectionEvent e) {
                                              replace();
                                              if (findEntry()) {
                                                info.setText("");
                                              } else {
                                                info.setText(configData.getLangRes().getString("find_notfind"));
                                              }
                                              enableButtons();
                                              text.setFocus();
                                              dialog.setFocus();
                                            }
                                          };

  /** Listener for the replace button. */
  private SelectionListener replace       = new SelectionAdapter() {
                                            public void widgetSelected(SelectionEvent e) {
                                              replace();
                                              enableButtons();
                                              text.setFocus();
                                              dialog.setFocus();
                                              info.setText("");
                                            }
                                          };

  /** Listener for the replace all button. */
  private SelectionListener replaceall    = new SelectionAdapter() {
                                            public void widgetSelected(SelectionEvent e) {
                                              int counter;
                                              if (forward.getSelection()) {
                                                text.setCaretOffset(-1);
                                              } else {
                                                text.setCaretOffset(text.getCharCount());
                                              }
                                              for (counter = 0; findEntry(); counter++) {
                                                replace();
                                              }
                                              enableButtons();
                                              text.setFocus();
                                              dialog.setFocus();
                                              info.setText(counter + " " + configData.getLangRes().getString("find_replaced"));
                                            }
                                          };

  /** Listener for the close event. */
  private SelectionListener close         = new SelectionAdapter() {
                                            public void widgetSelected(SelectionEvent e) {
                                              dialog.close();
                                            }
                                          };

  /** Listener if the shell gets the focus. */
  private ShellListener     shellFocus    = new ShellAdapter() {
                                            public void shellActivated(ShellEvent e) {
                                              enableButtons();
                                            }
                                          };

  /** Listener for closing the dialog. */
  private ShellListener     shellExit     = new ShellAdapter() {
                                            public void shellClosed(ShellEvent e) {
                                              if (e.doit) {
                                                edit.getItem(11).setEnabled(true);
                                              }
                                            }
                                          };

  /**
   * Enables the buttons.
   */
  private void enableButtons() {
    if (text.getText().length() > 0 && searchText.getCharCount() > 0 && text.getText().length() >= searchText.getCharCount()) {
      findButton.setEnabled(true);
      replaceallButton.setEnabled(true);
    } else {
      findButton.setEnabled(false);
      replaceallButton.setEnabled(false);
    }
    if (searchText.getCharCount() > 0 && text.getSelectionCount() > 0 && searchText.getText().equalsIgnoreCase(text.getSelectionText()) && !sensitiveButton.getSelection()) {
      replaceButton.setEnabled(true);
      replacefindButton.setEnabled(true);
    } else if (searchText.getCharCount() > 0 && text.getSelectionCount() > 0 && searchText.getText().equals(text.getSelectionText()) && sensitiveButton.getSelection()) {
      replaceButton.setEnabled(true);
      replacefindButton.setEnabled(true);
    } else {
      replaceButton.setEnabled(false);
      replacefindButton.setEnabled(false);
    }
  }

  /**
   * Function for searching.
   * 
   * @return Return boolean state.
   */
  private boolean findEntry() {
    String searchString = searchText.getText();
    String textString = text.getText();
    int offset = text.getCaretOffset();
    int start = -1;

    if (!sensitiveButton.getSelection()) {
      searchString = searchString.toLowerCase();
      textString = textString.toLowerCase();
    }

    if (forward.getSelection()) {
      start = textString.indexOf(searchString, offset);
    } else if (text.getSelectionRange().y > searchString.length()) {
      start = textString.lastIndexOf(searchString, offset - 1);
    } else {
      start = textString.lastIndexOf(searchString, offset - text.getSelectionRange().y - 1);
    }

    if (start > -1) {
      text.setSelection(start, start + searchString.length());
      return true;
    }

    return false;
  }

  /**
   * Function for replacing.
   */
  private void replace() {
    int start = text.getSelectionRange().x;
    text.replaceTextRange(start, text.getSelectionCount(), replaceText.getText());
    text.setSelection(start, start + replaceText.getText().length());
  }

  /**
   * Moves the window to the top of the drawing order.
   */
  public void forceActive() {
    dialog.forceActive();
  }

  /**
   * @return Returns if the dialog is closed.
   */
  public boolean isWidgetDisposed() {
    return dialog.isDisposed();
  }
}
