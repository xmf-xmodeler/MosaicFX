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
import net.sf.pisee.swtextedit.dialog.FileDialogUtil;
import net.sf.pisee.swtextedit.util.LangUtil;

import org.eclipse.swt.widgets.Display;

/**
 * The top class of the UI.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class SWTextEdit {

   /** Instance of Display for the Shell. */
   private final Display display = new Display();

   /** Instance of GuiConfigData for all configuration data. */
   private final GuiConfigData configData = new GuiConfigData();

   /** HashSet for the language control. */
   private final HashSet widgets = new HashSet();

   /** Instance of the UI widgets. */
   private final GuiWidgets guiWidgets = new GuiWidgets(configData, widgets);

   /** Private empty constructor. */
   private SWTextEdit() {
   }

   /**
    * Default Constructor of SWTextEdit.
    * 
    * @param file
    *           Argument loaded with SWTextEdit.
    */
   SWTextEdit(String file) {
      configData.setFilename(file);
      openClose();
   }

   /**
    * Opens and closes the program.
    */
   private void openClose() {
      guiWidgets.getShell().open();

      LangUtil.setLang(widgets, configData);
      FileDialogUtil.open(guiWidgets.getStyledText(), guiWidgets.getShell(),
            configData);

      while (!guiWidgets.getShell().isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }

      display.dispose();
   }
}
