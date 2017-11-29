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
package net.sf.pisee.swtextedit.widgets;

import net.sf.pisee.swtextedit.util.ImageUtil;
import net.sf.pisee.swtextedit.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * Creates a new Menu & MenuItem widget with the given parameters.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class MenuUtil {

   /** Private empty constructor. */
   private MenuUtil() {
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.MenuUtil#newMenu(Shell, int,
    *      MenuListener)
    */
   public static Menu newMenu(Shell shell, int style, MenuListener enable) {
      return createMenu(shell, style, enable);
   }

   /**
    * Creates a new Menu with the given Parameters.
    * 
    * @param shell
    *           The parent of the menu (shell).
    * @param style
    *           The style of the menu.
    * @param enable
    *           Menulistener to enable / disable the menu.
    * @return Returns the new menu.
    */
   private static Menu createMenu(Shell shell, int style, MenuListener enable) {
      Menu menu = new Menu(shell, style);

      if (enable != null) {
         menu.addMenuListener(enable);
      }

      return menu;
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.MenuUtil#createMenuItem(Menu, int,
    *      String, Menu, String, int, SelectionListener, boolean)
    */
   public static MenuItem newMenuName(Menu parent, int style, String textID,
         Menu setMenu) {
      return createMenuItem(parent, style, textID, setMenu, null, 0, null,
            false);
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.MenuUtil#createMenuItem(Menu, int,
    *      String, Menu, String, int, SelectionListener, boolean)
    */
   public static MenuItem newMenuItem(Menu parent, String textID, String image,
         int accelerator, SelectionListener select) {
      return createMenuItem(parent, SWT.PUSH, textID, null, image, accelerator,
            select, false);
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.MenuUtil#createMenuItem(Menu, int,
    *      String, Menu, String, int, SelectionListener, boolean)
    */
   public static MenuItem newMenuItemStyle(Menu parent, int style,
         String textID, int accelerator, SelectionListener select,
         boolean selection) {
      return createMenuItem(parent, style, textID, null, null, accelerator,
            select, selection);
   }

   /**
    * Creates a new menu item with the given parameters.
    * 
    * @param parent
    *           The parent of the menu item (menu).
    * @param style
    *           The style of the menu item.
    * @param textID
    *           The text of the menu item.
    * @param setMenu
    *           The menu for the MenuItem.
    * @param image
    *           Location of the image.
    * @param accelerator
    *           The key accelerator of the menu item.
    * @param select
    *           The event of the menu item.
    * @param selection
    *           Sets the selection of the item.
    * @return Returns the new menu item.
    */
   private static MenuItem createMenuItem(Menu parent, int style,
         String textID, Menu setMenu, String image, int accelerator,
         SelectionListener select, boolean selection) {
      MenuItem menuItem = new MenuItem(parent, style);

      if (!StringUtil.isValueEmpty(textID)) {
         menuItem.setData("TEXTID", textID);
      }

      if (setMenu != null) {
         menuItem.setMenu(setMenu);
      }

      if (accelerator > 0) {
         menuItem.setAccelerator(accelerator);
      }

      if (select != null) {
         menuItem.addSelectionListener(select);
      }

      if (!StringUtil.isValueEmpty(image)) {
         menuItem.setImage(ImageUtil.newImage(parent.getDisplay(), image));
      }

      if (selection) {
         menuItem.setSelection(selection);
      }

      return menuItem;
   }
}
