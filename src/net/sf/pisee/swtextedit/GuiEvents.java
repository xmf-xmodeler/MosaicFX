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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import net.sf.pisee.swtextedit.dialog.About;
import net.sf.pisee.swtextedit.dialog.FileDialogUtil;
import net.sf.pisee.swtextedit.dialog.FindReplace;
import net.sf.pisee.swtextedit.dialog.FontDialogUtil;
import net.sf.pisee.swtextedit.dialog.SystemProperties;
import net.sf.pisee.swtextedit.util.IOUtil;
import net.sf.pisee.swtextedit.util.ItemUtil;
import net.sf.pisee.swtextedit.util.LangUtil;
import net.sf.pisee.swtextedit.util.PrintUtil;
import net.sf.pisee.swtextedit.util.StatusBarUtil;
import net.sf.pisee.swtextedit.util.StringUtil;
import net.sf.pisee.swtextedit.util.TextUtil;
import net.sf.pisee.swtextedit.util.UndoUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;

/**
 * Creating the UI events.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class GuiEvents {

   /** Instance of the subclass GuiWidgets. */
   private GuiWidgets widgets;

   /** Instance of the Find/Replace Dialog. */
   private FindReplace findReplace;

   /** Instances for the undo / redo function. */
   private final List undoStack = new LinkedList(),
         redoStack = new LinkedList();

   /** Private empty constructor. */
   private GuiEvents() {
   }

   /**
    * Default Constructor of GuiEvents.
    * 
    * @param widgets
    *           Instance of GuiWidgets.
    */
   GuiEvents(GuiWidgets widgets) {
      this.widgets = widgets;
   }

   /** Listener for the undo / redo function. */
   ExtendedModifyListener undoredo = new ExtendedModifyListener() {
      public void modifyText(ExtendedModifyEvent e) {
         redoStack.clear();
         int stackSize = widgets.getConfigData().getUndoStackSize();
         String newText = widgets.getStyledText().getText().substring(e.start,
               e.start + e.length);
         boolean newTextValue = !StringUtil.isValueEmpty(newText);
         if (e.replacedText.length() > 0) {
            if (undoStack.size() == stackSize) {
               undoStack.remove(undoStack.size() - 1);
            }
            if (newTextValue) {
               undoStack.add(0, new UndoUtil(e.replacedText,
                     e.replacedText.length() > 0, widgets.getStyledText()
                           .getCaretOffset()
                           - newText.length()));
               if (undoStack.size() == stackSize) {
                  undoStack.remove(undoStack.size() - 1);
               }
               undoStack.add(0, new UndoUtil(newText, false,
                     widgets.getStyledText().getCaretOffset()));
            }
            else {
               undoStack.add(0, new UndoUtil(e.replacedText,
                     e.replacedText.length() > 0, widgets.getStyledText()
                           .getCaretOffset()));
            }
         }
         else if (newTextValue) {
            if (undoStack.size() == stackSize) {
               undoStack.remove(undoStack.size() - 1);
            }
            undoStack.add(0, new UndoUtil(newText, false,
                  widgets.getStyledText().getCaretOffset()));
         }
      }
   };

   /** Listener if a key is pressed. */
   KeyListener keyPressed = new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), e.keyCode, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener if a key is released. */
   KeyListener keyReleased = new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for the file menu to enable or disable. */
   MenuListener enableSaveItem = new MenuAdapter() {
      public void menuShown(MenuEvent e) {
         widgets.getFileMenu().getItem(2).setEnabled(
               widgets.getConfigData().isHasChanged());
      }
   };

   /** Listener for the edit menu to enable or disable. */
   MenuListener enableEditItems = new MenuAdapter() {
      public void menuShown(MenuEvent e) {
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         ItemUtil.enableMenuItems(widgets.getEditPopup(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
      }
   };

   /** Listener for modifying the text. */
   ModifyListener textChanged = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
         if (!widgets.getConfigData().isHasChanged()) {
            widgets.getConfigData().setHasChanged(true);
            widgets.getFileMenu().getItem(2).setEnabled(true);
         }
      }
   };

   /** Listener if a mouse key is pressed. */
   MouseListener mousePressed = new MouseAdapter() {
      public void mouseDown(MouseEvent e) {
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener if a mouse key is released. */
   MouseListener mouseReleased = new MouseAdapter() {
      public void mouseUp(MouseEvent e) {
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for a new textfile. */
   SelectionListener newFile = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         if (widgets.getConfigData().isHasChanged()) {
            int state = FileDialogUtil.saveYesNoCancel(widgets.getShell(),
                  widgets.getConfigData());

            if (state == SWT.YES) {
               if (FileDialogUtil.fileDialog(widgets.getShell(),
                     widgets.getConfigData(), SWT.SAVE, widgets.getStyledText())) {
                  clearData(true);
               }
            }
            else if (state == SWT.NO) {
               clearData(true);
            }
         }
         else {
            clearData(true);
         }
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for opening a file. */
   SelectionListener open = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         if (widgets.getConfigData().isHasChanged()) {
            int state = FileDialogUtil.saveYesNoCancel(widgets.getShell(),
                  widgets.getConfigData());

            if (state == SWT.YES) {
               if (FileDialogUtil.fileDialog(widgets.getShell(),
                     widgets.getConfigData(), SWT.SAVE, widgets.getStyledText())) {
                  if (FileDialogUtil.fileDialog(widgets.getShell(),
                        widgets.getConfigData(), SWT.OPEN,
                        widgets.getStyledText())) {
                     clearData(false);
                  }
               }
            }
            else if (state == SWT.NO) {
               if (FileDialogUtil.fileDialog(widgets.getShell(),
                     widgets.getConfigData(), SWT.OPEN, widgets.getStyledText())) {
                  clearData(false);
               }
            }
         }
         else if (FileDialogUtil.fileDialog(widgets.getShell(),
               widgets.getConfigData(), SWT.OPEN, widgets.getStyledText())) {
            clearData(false);
         }
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for saving a file. */
   SelectionListener save = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         if (StringUtil.isValueEmpty(widgets.getConfigData().getFilename())) {
            if (FileDialogUtil.fileDialog(widgets.getShell(),
                  widgets.getConfigData(), SWT.SAVE, widgets.getStyledText())) {
               widgets.getConfigData().setHasChanged(false);
            }
         }
         else if (IOUtil.save(new File(widgets.getConfigData().getFilename()),
               widgets.getStyledText().getText())) {
            widgets.getConfigData().setHasChanged(false);
         }
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for saving a file as. */
   SelectionListener saveas = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         if (FileDialogUtil.fileDialog(widgets.getShell(),
               widgets.getConfigData(), SWT.SAVE, widgets.getStyledText())) {
            widgets.getConfigData().setHasChanged(false);
         }
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for printing the textfile. */
   SelectionListener print = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         PrintUtil.printDialog(widgets.getShell(), widgets.getStyledText(),
               widgets.getConfigData().getFont(), widgets.getConfigData()
                     .getForegroundColor(), widgets.getConfigData()
                     .getBackgroundColor());
      }
   };

   /** Listener for the exit button. */
   SelectionListener exit = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getShell().close();
      }
   };

   /** Listener for the undo event of the text widget. */
   SelectionListener undo = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getStyledText().removeExtendedModifyListener(undoredo);
         UndoUtil.undo(undoStack, redoStack, widgets.getStyledText());
         widgets.getStyledText().addExtendedModifyListener(undoredo);
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for the redo event of the text widget. */
   SelectionListener redo = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getStyledText().removeExtendedModifyListener(undoredo);
         UndoUtil.redo(redoStack, undoStack, widgets.getStyledText());
         widgets.getStyledText().addExtendedModifyListener(undoredo);
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for the cut event of the text widget. */
   SelectionListener cut = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getStyledText().cut();
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for the copy event of the text widget. */
   SelectionListener copy = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getStyledText().copy();
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
      }
   };

   /** Listener for the paste event of the text widget. */
   SelectionListener paste = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getStyledText().paste();
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for the delete event of the text widget. */
   SelectionListener del = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getStyledText().insert("");
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for the select all event of the text widget. */
   SelectionListener selAll = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getStyledText().selectAll();
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for the delete all event of the text widget. */
   SelectionListener delAll = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.getStyledText().setText("");
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for converting the text to uppercase. */
   SelectionListener uppercase = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.setStyledText(StringUtil.uppercase(widgets.getStyledText()));
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
      }
   };

   /** Listener for converting the text to lowercase. */
   SelectionListener lowercase = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.setStyledText(StringUtil.lowercase(widgets.getStyledText()));
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
      }
   };

   /** Listener for trimming leading and trailing whitespace. */
   SelectionListener trim = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         widgets.setStyledText(StringUtil.trim(widgets.getStyledText()));
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
      }
   };

   /** Listener for find / replace dialog. */
   SelectionListener find = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         if (findReplace == null || findReplace.isWidgetDisposed()) {
            findReplace = new FindReplace(widgets.getShell(),
                  widgets.getConfigData(), widgets.getStyledText(),
                  widgets.getEditMenu());
         }
         findReplace.forceActive();
      }
   };

   /** Listener for the wrap style of the styledtext widget. */
   SelectionListener wrap = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         TextUtil.wrap(widgets.getConfigData(), widgets.getStyledText());
      }
   };

   /** Listener for the font of the styledtext widget. */
   SelectionListener font = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         FontDialogUtil.font(widgets.getShell(), widgets.getStyledText(),
               widgets.getConfigData());
      }
   };

   /** Listener for the background color of the styledtext widget. */
   SelectionListener backColor = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         FontDialogUtil.backColor(widgets.getShell(), widgets.getStyledText(),
               widgets.getConfigData());
      }
   };

   /** Listener for the foreground color of the styledtext widget. */
   SelectionListener foreColor = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         FontDialogUtil.foreColor(widgets.getShell(), widgets.getStyledText(),
               widgets.getConfigData());
      }
   };

   /** Listener for the selection background color of the styledtext widget. */
   SelectionListener selectBackColor = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         FontDialogUtil.selectBackColor(widgets.getShell(),
               widgets.getStyledText(), widgets.getConfigData());
      }
   };

   /** Listener for the selection foreground color of the styledtext widget. */
   SelectionListener selectForeColor = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         FontDialogUtil.selectForeColor(widgets.getShell(),
               widgets.getStyledText(), widgets.getConfigData());
      }
   };

   /** Listener for the language of the program. */
   SelectionListener lang = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         String lang = (String) e.widget.getData("TEXTID");
         lang = lang.substring(lang.length() - 2, lang.length()).toUpperCase();
         if (!widgets.getConfigData().getLanguage().equals(lang)) {
            widgets.getConfigData().setLanguage(lang);
            LangUtil.setLang(widgets.getWidgets(), widgets.getConfigData());
         }
      }
   };

   /** Listener for the systeminfo message box. */
   SelectionListener systemconfig = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         new SystemProperties(widgets.getShell(), widgets.getConfigData());
      }
   };

   /** Listener for the about dialog. */
   SelectionListener about = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         new About(widgets.getShell(), widgets.getConfigData());
      }
   };

   /** Listener for the selection of the text widget. */
   SelectionListener selectText = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener if the shell gets the focus. */
   ShellListener shellFocus = new ShellAdapter() {
      public void shellActivated(ShellEvent e) {
         ItemUtil.enableMenuItems(widgets.getEditMenu(), widgets.getToolBar(),
               widgets.getConfigData(), widgets.getStyledText(), undoStack,
               redoStack);
         widgets.getFileMenu().getItem(2).setEnabled(
               widgets.getConfigData().isHasChanged());
         StatusBarUtil.status(widgets.getStatus1(), widgets.getStatus2(),
               widgets.getStatus3(), widgets.getStatus4(),
               widgets.getStatus5(), 0, widgets.getStyledText(),
               widgets.getConfigData().getFilename());
      }
   };

   /** Listener for closing the shell. */
   ShellListener shellExit = new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
         if (widgets.getConfigData().isHasChanged()) {
            switch (FileDialogUtil.saveYesNoCancel(widgets.getShell(),
                  widgets.getConfigData())) {
            case SWT.YES:
               e.doit = FileDialogUtil.fileDialog(widgets.getShell(),
                     widgets.getConfigData(), SWT.SAVE, widgets.getStyledText());
               break;
            case SWT.NO:
               e.doit = true;
               break;
            default:
               e.doit = false;
            }
         }
         else {
            e.doit = true;
         }
      }
   };

   /**
    * Set back all values for a new file.
    * 
    * @param untitled
    *           True for a new untitled text file.
    */
   private void clearData(boolean untitled) {
      if (untitled) {
         widgets.getStyledText().setText("");
         widgets.getConfigData().setFilename(null);
      }
      widgets.getConfigData().setHasChanged(false);
      undoStack.clear();
      redoStack.clear();
   }
}
