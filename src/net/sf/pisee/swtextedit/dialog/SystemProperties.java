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

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;

import net.sf.pisee.swtextedit.config.GuiConfigData;
import net.sf.pisee.swtextedit.config.GuiIcons;
import net.sf.pisee.swtextedit.util.LangUtil;
import net.sf.pisee.swtextedit.widgets.GridUtil;
import net.sf.pisee.swtextedit.widgets.ShellUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Shows all important system properties.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class SystemProperties {

   /** HashSet for the language control. */
   private HashSet widgets = new HashSet();

   /** Instance of Shell. */
   private Shell sysProps;

   /** Private empty constructor. */
   private SystemProperties() {
   }

   /** Public constructor. */
   public SystemProperties(Shell parent, GuiConfigData configData) {
      shell(parent);
      table();

      LangUtil.setLang(widgets, configData);

      sysProps.open();
   }

   /**
    * Creates a new Shell for the systeminformation dialog.
    * 
    * @param parent
    *           The parent of the dialog.
    */
   private void shell(Shell parent) {
      sysProps = ShellUtil.newShell(parent, SWT.DIALOG_TRIM
            | SWT.APPLICATION_MODAL, "systeminfo_title", GuiIcons.text, 640,
            480, GridUtil.newGridLayout(0, 0, 0, 0, 1, false), true, false);
      widgets.add(sysProps);
   }

   /**
    * Creates a table with the system properties.
    */
   private void table() {
      Table tbl = new Table(sysProps, SWT.FULL_SELECTION);
      tbl.setLayoutData(GridUtil.newGridData(true, true));
      tbl.setLinesVisible(true);

      TableColumn col1 = new TableColumn(tbl, SWT.LEFT, 0);
      TableColumn col2 = new TableColumn(tbl, SWT.LEFT, 1);

      try {
         Properties properties = System.getProperties();
         for (Enumeration e = properties.keys(); e.hasMoreElements();) {
            TableItem item = new TableItem(tbl, SWT.NONE);
            Object key = e.nextElement();
            Object value = properties.get(key);
            item.setText(0, (String) key);
            item.setText(1, (String) value);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      col1.pack();
      col2.pack();
   }
}
