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

import net.sf.pisee.swtextedit.config.GuiConfigData;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for the font, fore- and backgroundcolor of the styled text.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class FontDialogUtil {

   /** Instance of the color dialog. */
   private static ColorDialog colorDialog;

   /** Instance of default additive color model. */
   private static RGB rgb;

   /** Private empty constructor. */
   private FontDialogUtil() {
   }

   /**
    * Dialog for the font of the styled text.
    * 
    * @param parent
    *           The parent of the dialog.
    * @param text
    *           The text widget where the font should be changed.
    * @param configData
    *           The configuration data of the GUI.
    */
   public static void font(Shell parent, StyledText text,
         GuiConfigData configData) {
      FontDialog fontDialog = new FontDialog(parent);
      fontDialog.setFontList(configData.getFont().getFontData());
      FontData fontData = fontDialog.open();

      if (fontData != null) {
         configData.setFont(new Font(parent.getDisplay(), fontData));
         text.setFont(configData.getFont());
      }
   }

   /**
    * Dialog for the background color of the styled text.
    * 
    * @param parent
    *           The parent of the dialog.
    * @param text
    *           The text widget where the background color should be changed.
    * @param configData
    *           The configuration data of the GUI.
    */
   public static void backColor(Shell parent, StyledText text,
         GuiConfigData configData) {
      colorDialog = new ColorDialog(parent);
      colorDialog.setRGB(configData.getBackgroundColor().getRGB());
      rgb = colorDialog.open();

      if (rgb != null) {
         configData.setBackgroundColor(new Color(parent.getDisplay(), rgb));
         text.setBackground(configData.getBackgroundColor());
      }
   }

   /**
    * Dialog for the foreground color of the styled text.
    * 
    * @param parent
    *           The parent of the dialog.
    * @param text
    *           The text widget where the foreground color should be changed.
    * @param configData
    *           The configuration data of the GUI.
    */
   public static void foreColor(Shell parent, StyledText text,
         GuiConfigData configData) {
      colorDialog = new ColorDialog(parent);
      colorDialog.setRGB(configData.getForegroundColor().getRGB());
      rgb = colorDialog.open();

      if (rgb != null) {
         configData.setForegroundColor(new Color(parent.getDisplay(), rgb));
         text.setForeground(configData.getForegroundColor());
      }
   }

   /**
    * Dialog for the selection background color of the styled text.
    * 
    * @param parent
    *           The parent of the dialog.
    * @param text
    *           The text widget where the background color should be changed.
    * @param configData
    *           The configuration data of the GUI.
    */
   public static void selectBackColor(Shell parent, StyledText text,
         GuiConfigData configData) {
      colorDialog = new ColorDialog(parent);
      colorDialog.setRGB(configData.getSelectionBackground().getRGB());
      rgb = colorDialog.open();

      if (rgb != null) {
         configData.setSelectionBackground(new Color(parent.getDisplay(), rgb));
         text.setSelectionBackground(configData.getSelectionBackground());
      }
   }

   /**
    * Dialog for the selection foreground color of the styled text.
    * 
    * @param parent
    *           The parent of the dialog.
    * @param text
    *           The text widget where the foreground color should be changed.
    * @param configData
    *           The configuration data of the GUI.
    */
   public static void selectForeColor(Shell parent, StyledText text,
         GuiConfigData configData) {
      colorDialog = new ColorDialog(parent);
      colorDialog.setRGB(configData.getSelectionForeground().getRGB());
      rgb = colorDialog.open();

      if (rgb != null) {
         configData.setSelectionForeground(new Color(parent.getDisplay(), rgb));
         text.setSelectionForeground(configData.getSelectionForeground());
      }
   }
}
