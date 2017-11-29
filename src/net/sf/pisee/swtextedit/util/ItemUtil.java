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
package net.sf.pisee.swtextedit.util;

import java.util.List;

import net.sf.pisee.swtextedit.config.GuiConfigData;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

/**
 * Utility class for enable / disable the menu and the toolbar.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class ItemUtil {

   /** Private empty constructor. */
   private ItemUtil() {
   }

   /**
    * Enables / disables the edit menu items.
    * 
    * @param menu
    *           The parent of the menu items.
    * @param toolBar
    *           The parent of the tool items.
    * @param configData
    *           Instance of the configuration values.
    * @param text
    *           The Instance of the text widget.
    * @param undoStack
    *           The list which save all undo actions.
    * @param redoStack
    *           The list which save all redo actions.
    */
   public static void enableMenuItems(Menu menu, ToolBar toolBar,
         GuiConfigData configData, StyledText text, List undoStack,
         List redoStack) {
      int count = text.getSelectionCount();
      int charCount = text.getCharCount();
      menu.getItem(0).setEnabled(undoStack.size() > 0); // undo
      menu.getItem(1).setEnabled(redoStack.size() > 0); // redo
      menu.getItem(3).setEnabled(count > 0); // cut
      menu.getItem(4).setEnabled(count > 0); // copy
      menu.getItem(5)
            .setEnabled(
                  (String) (new Clipboard(menu.getDisplay()).getContents(TextTransfer.getInstance())) != null); // paste
      menu.getItem(6).setEnabled(count > 0); // delete
      menu.getItem(8).setEnabled(charCount > 0); // select all
      menu.getItem(9).setEnabled(charCount > 0); // delete all

      if (menu.getItemCount() > 10) {
         menu.getItem(11).setEnabled(count > 0 || charCount > 0); // uppercase
         menu.getItem(12).setEnabled(count > 0 || charCount > 0); // lowercase
         menu.getItem(14).setEnabled(count > 0 || charCount > 0); // trim
      }

      enableToolItems(toolBar, menu, configData);
   }

   /**
    * Enables / disables the edit tool items.
    * 
    * @param tool
    *           The parent of the tool items.
    * @param menu
    *           The parent of the menu items.
    * @param configData
    *           Instance of the configuration values.
    */
   private static void enableToolItems(ToolBar tool, Menu menu,
         GuiConfigData configData) {
      tool.getItem(2).setEnabled(configData.isHasChanged()); // save
      tool.getItem(5).setEnabled(menu.getItem(0).isEnabled()); // undo
      tool.getItem(6).setEnabled(menu.getItem(1).isEnabled()); // redo
      tool.getItem(8).setEnabled(menu.getItem(3).isEnabled()); // cut
      tool.getItem(9).setEnabled(menu.getItem(4).isEnabled()); // copy
      tool.getItem(10).setEnabled(menu.getItem(5).isEnabled()); // paste
      tool.getItem(11).setEnabled(menu.getItem(6).isEnabled()); // delete
   }
}
