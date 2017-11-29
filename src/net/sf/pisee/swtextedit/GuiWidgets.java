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
package net.sf.pisee.swtextedit;

import java.util.HashSet;

import net.sf.pisee.swtextedit.config.GuiConfigData;
import net.sf.pisee.swtextedit.config.GuiIcons;
import net.sf.pisee.swtextedit.widgets.GridUtil;
import net.sf.pisee.swtextedit.widgets.LabelUtil;
import net.sf.pisee.swtextedit.widgets.MenuUtil;
import net.sf.pisee.swtextedit.widgets.ShellUtil;
import net.sf.pisee.swtextedit.widgets.StyledTextUtil;
import net.sf.pisee.swtextedit.widgets.ToolUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Creating the UI widgets.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class GuiWidgets {

  /** Instance of GuiConfigData for all configuration data. */
  private GuiConfigData configData;

  /** HashSet for the language control. */
  private HashSet       widgets;

  /** Instance of the UI events. */
  private GuiEvents     events;

  /** Instances of Label for the statusbar. */
  private Label         status1, status2, status3, status4, status5;

  /** Instaces of Menu. */
  private Menu          fileMenu, editMenu, editPopup;

  /** Instance of the parent. */
  private Shell         shell;

  /** Instance of StyledText. */
  private StyledText    styledText;

  /** Instance of ToolBar. */
  private ToolBar       toolBar;

  /** Private empty constructor. */
  private GuiWidgets() {
  }

  /**
   * Default Constructor of GuiWidgets.
   * 
   * @param configData
   *          The configuration data.
   * @param widgets
   *          HashSet for the widgets.
   */
  public GuiWidgets(GuiConfigData configData, HashSet widgets) {
    this.configData = configData;
    this.widgets = widgets;
    this.events = new GuiEvents(this);

    shell();
    menu();
    toolbar();
    content();
    statusBar();
  }

  /**
   * Initializing the shell.
   */
  private void shell() {
    shell = ShellUtil.newShell(SWT.SHELL_TRIM, "text", GuiIcons.text, configData.getShellWidth(), configData.getShellHeight(), GridUtil.newGridLayout(0, 0, 0, 0, 1, false), false, configData.isMaximize());
    widgets.add(shell);

    shell.addShellListener(events.shellFocus);
    shell.addShellListener(events.shellExit);
  }

  /**
   * Menubar of the Shell.
   */
  private void menu() {
    shell.setMenuBar(new Menu(shell, SWT.BAR));

    fileMenu();
    editMenu();
    searchMenu();
    viewMenu();
    helpMenu();
  }

  /**
   * File menu.
   */
  private void fileMenu() {
    fileMenu = MenuUtil.newMenu(shell, SWT.DROP_DOWN, events.enableSaveItem);
    widgets.add(MenuUtil.newMenuName(shell.getMenuBar(), SWT.CASCADE, "text_menu_file", fileMenu));

    widgets.add(MenuUtil.newMenuItem(fileMenu, "text_menu_file_new", GuiIcons.newfile, SWT.CTRL + 'N', events.newFile));
    widgets.add(MenuUtil.newMenuItem(fileMenu, "text_menu_file_open", GuiIcons.open, SWT.CTRL + 'O', events.open));
    widgets.add(MenuUtil.newMenuItem(fileMenu, "text_menu_file_save", GuiIcons.save, SWT.CTRL + 'S', events.save));
    widgets.add(MenuUtil.newMenuItem(fileMenu, "text_menu_file_saveas", GuiIcons.saveas, SWT.CTRL + SWT.SHIFT + 'A', events.saveas));

    new MenuItem(fileMenu, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(fileMenu, "text_menu_file_print", GuiIcons.print, SWT.CTRL + 'P', events.print));

    new MenuItem(fileMenu, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(fileMenu, "text_menu_file_exit", null, SWT.ESC, events.exit));
  }

  /**
   * Edit menu.
   */
  private void editMenu() {
    editMenu = MenuUtil.newMenu(shell, SWT.DROP_DOWN, events.enableEditItems);
    widgets.add(MenuUtil.newMenuName(shell.getMenuBar(), SWT.CASCADE, "text_menu_edit", editMenu));

    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_undo", GuiIcons.undo, SWT.CTRL + 'Z', events.undo));
    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_redo", GuiIcons.redo, SWT.CTRL + 'Y', events.redo));

    new MenuItem(editMenu, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_cut", GuiIcons.cut, SWT.CTRL + 'X', events.cut));
    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_copy", GuiIcons.copy, SWT.CTRL + 'C', events.copy));
    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_paste", GuiIcons.paste, SWT.CTRL + 'V', events.paste));
    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_del", GuiIcons.del, SWT.NONE, events.del));

    new MenuItem(editMenu, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_selall", null, SWT.CTRL + 'A', events.selAll));
    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_delall", null, SWT.CTRL + SWT.SHIFT + SWT.DEL, events.delAll));

    new MenuItem(editMenu, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_uppercase", null, SWT.NONE, events.uppercase));
    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_lowercase", null, SWT.NONE, events.lowercase));

    new MenuItem(editMenu, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(editMenu, "text_menu_edit_trim", null, SWT.NONE, events.trim));
  }

  /**
   * Search menu.
   */
  private void searchMenu() {
    Menu searchMenu = MenuUtil.newMenu(shell, SWT.DROP_DOWN, null);
    widgets.add(MenuUtil.newMenuName(shell.getMenuBar(), SWT.CASCADE, "text_menu_search", searchMenu));

    widgets.add(MenuUtil.newMenuItem(searchMenu, "text_menu_search_find", GuiIcons.search, SWT.CTRL + 'F', events.find));
  }

  /**
   * View menu.
   */
  private void viewMenu() {
    Menu viewMenu = MenuUtil.newMenu(shell, SWT.DROP_DOWN, null);
    widgets.add(MenuUtil.newMenuName(shell.getMenuBar(), SWT.CASCADE, "text_menu_view", viewMenu));

    widgets.add(MenuUtil.newMenuItemStyle(viewMenu, SWT.CHECK, "text_menu_view_wrap", SWT.CTRL + 'W', events.wrap, configData.isWrap()));

    new MenuItem(viewMenu, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(viewMenu, "text_menu_view_font", null, SWT.CTRL + SWT.SHIFT + 'F', events.font));
    widgets.add(MenuUtil.newMenuItem(viewMenu, "text_menu_view_forecolor", null, SWT.NONE, events.foreColor));
    widgets.add(MenuUtil.newMenuItem(viewMenu, "text_menu_view_backcolor", null, SWT.NONE, events.backColor));
    widgets.add(MenuUtil.newMenuItem(viewMenu, "text_menu_view_selectforecolor", null, SWT.NONE, events.selectForeColor));
    widgets.add(MenuUtil.newMenuItem(viewMenu, "text_menu_view_selectbackcolor", null, SWT.NONE, events.selectBackColor));

    new MenuItem(viewMenu, SWT.SEPARATOR);

    Menu langMenu = MenuUtil.newMenu(shell, SWT.DROP_DOWN, null);
    widgets.add(MenuUtil.newMenuName(viewMenu, SWT.CASCADE, "text_menu_view_lang", langMenu));

    widgets.add(MenuUtil.newMenuItemStyle(langMenu, SWT.RADIO, "text_menu_view_lang_en", SWT.NONE, events.lang, configData.getLanguage().equals("EN")));
    widgets.add(MenuUtil.newMenuItemStyle(langMenu, SWT.RADIO, "text_menu_view_lang_de", SWT.NONE, events.lang, configData.getLanguage().equals("DE")));
  }

  /**
   * Help menu.
   */
  private void helpMenu() {
    Menu helpMenu = MenuUtil.newMenu(shell, SWT.DROP_DOWN, null);
    widgets.add(MenuUtil.newMenuName(shell.getMenuBar(), SWT.CASCADE, "text_menu_help", helpMenu));

    widgets.add(MenuUtil.newMenuItem(helpMenu, "text_menu_help_system", null, SWT.NONE, events.systemconfig));

    new MenuItem(helpMenu, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(helpMenu, "text_menu_help_about", GuiIcons.about, SWT.NONE, events.about));
  }

  /**
   * The toolbar of the shell.
   */
  private void toolbar() {
    toolBar = ToolUtil.newToolBar(shell, GridUtil.newGridData(), null);

    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.newfile, events.newFile, "text_toolbar_new"));
    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.open, events.open, "text_toolbar_open"));
    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.save, events.save, "text_toolbar_save"));
    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.print, events.print, "text_toolbar_print"));

    new ToolItem(toolBar, SWT.SEPARATOR);

    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.undo, events.undo, "text_toolbar_undo"));
    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.redo, events.redo, "text_toolbar_redo"));

    new ToolItem(toolBar, SWT.SEPARATOR);

    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.cut, events.cut, "text_toolbar_cut"));
    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.copy, events.copy, "text_toolbar_copy"));
    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.paste, events.paste, "text_toolbar_paste"));
    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.del, events.del, "text_toolbar_del"));

    new ToolItem(toolBar, SWT.SEPARATOR);

    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.search, events.find, "text_toolbar_find"));

    new ToolItem(toolBar, SWT.SEPARATOR);

    widgets.add(ToolUtil.newToolItem(toolBar, GuiIcons.about, events.about, "text_toolbar_about"));
  }

  /**
   * The content of the shell.
   */
  private void content() {
    styledText = StyledTextUtil.newStyledText(shell, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL, popupMenu(), GridUtil.newGridData(true, true), true, configData.getFont(), configData.getBackgroundColor(), configData.getForegroundColor(), configData.getSelectionBackground(), configData.getSelectionForeground(), configData.isWrap());

    styledText.addExtendedModifyListener(events.undoredo);
    styledText.addKeyListener(events.keyPressed);
    styledText.addKeyListener(events.keyReleased);
    styledText.addModifyListener(events.textChanged);
    styledText.addMouseListener(events.mousePressed);
    styledText.addMouseListener(events.mouseReleased);
    styledText.addSelectionListener(events.selectText);
  }

  /**
   * Popup menu of the text widget.
   */
  private Menu popupMenu() {
    editPopup = MenuUtil.newMenu(shell, SWT.POP_UP, events.enableEditItems);

    widgets.add(MenuUtil.newMenuItem(editPopup, "text_menu_edit_undo", GuiIcons.undo, SWT.CTRL + 'Z', events.undo));
    widgets.add(MenuUtil.newMenuItem(editPopup, "text_menu_edit_redo", GuiIcons.redo, SWT.CTRL + 'Y', events.redo));

    new MenuItem(editPopup, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(editPopup, "text_menu_edit_cut", GuiIcons.cut, SWT.CTRL + 'X', events.cut));
    widgets.add(MenuUtil.newMenuItem(editPopup, "text_menu_edit_copy", GuiIcons.copy, SWT.CTRL + 'C', events.copy));
    widgets.add(MenuUtil.newMenuItem(editPopup, "text_menu_edit_paste", GuiIcons.paste, SWT.CTRL + 'V', events.paste));
    widgets.add(MenuUtil.newMenuItem(editPopup, "text_menu_edit_del", GuiIcons.del, SWT.NONE, events.del));

    new MenuItem(editPopup, SWT.SEPARATOR);

    widgets.add(MenuUtil.newMenuItem(editPopup, "text_menu_edit_selall", null, SWT.CTRL + 'A', events.selAll));
    widgets.add(MenuUtil.newMenuItem(editPopup, "text_menu_edit_delall", null, SWT.NONE, events.delAll));

    return editPopup;
  }

  /**
   * The status line of the shell.
   */
  private void statusBar() {
    ToolBar statusBar = ToolUtil.newToolBar(shell, GridUtil.newGridData(), GridUtil.newGridLayout(5, 0, 5, 0, 9, false));

    GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, true);

    status1 = LabelUtil.newLabel(statusBar, gridData, null);

    LabelUtil.vLine(statusBar);

    gridData = GridUtil.newGridData(SWT.FILL, SWT.CENTER, false, true, 75, -1);

    status2 = LabelUtil.newLabel(statusBar, gridData, null);
    status2.setText("INS");

    LabelUtil.vLine(statusBar);

    status3 = LabelUtil.newLabel(statusBar, gridData, null);

    LabelUtil.vLine(statusBar);

    status4 = LabelUtil.newLabel(statusBar, gridData, null);

    LabelUtil.vLine(statusBar);

    status5 = LabelUtil.newLabel(statusBar, gridData, null);
  }

  /**
   * @return Return configData.
   */
  GuiConfigData getConfigData() {
    return configData;
  }

  /**
   * @return Return editMenu.
   */
  public Menu getEditMenu() {
    return editMenu;
  }

  /**
   * @return Return editPopup.
   */
  Menu getEditPopup() {
    return editPopup;
  }

  /**
   * @return Return fileMenu.
   */
  Menu getFileMenu() {
    return fileMenu;
  }

  /**
   * @return Return shell.
   */
  Shell getShell() {
    return shell;
  }

  /**
   * @return Return status1.
   */
  Label getStatus1() {
    return status1;
  }

  /**
   * @return Return status2.
   */
  Label getStatus2() {
    return status2;
  }

  /**
   * @return Return status3.
   */
  Label getStatus3() {
    return status3;
  }

  /**
   * @return Return status4.
   */
  Label getStatus4() {
    return status4;
  }

  /**
   * @return Return status5.
   */
  Label getStatus5() {
    return status5;
  }

  /**
   * @return Return styledText.
   */
  StyledText getStyledText() {
    return styledText;
  }

  /**
   * @param styledText
   *          Set styledText.
   */
  void setStyledText(StyledText styledText) {
    this.styledText = styledText;
  }

  /**
   * @return Return toolBar.
   */
  ToolBar getToolBar() {
    return toolBar;
  }

  /**
   * @return Return widgets.
   */
  HashSet getWidgets() {
    return widgets;
  }
}
