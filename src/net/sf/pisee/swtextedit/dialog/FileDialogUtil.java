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

import java.io.File;

import net.sf.pisee.swtextedit.config.GuiConfigData;
import net.sf.pisee.swtextedit.util.IOUtil;
import net.sf.pisee.swtextedit.util.StringUtil;
import net.sf.pisee.swtextedit.widgets.MessageBoxUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for opening or saving.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class FileDialogUtil {

   /** The status of opening and saving the file. */
   private static boolean status;

   /** Instance of the file for opening and saving. */
   private static File file;

   /** String of the path and name of the file. */
   private static String path;

   /** Private empty constructor. */
   private FileDialogUtil() {
   }

   /**
    * FileDialog for opening or saving a textfile.
    * 
    * @param shell
    *           The parent of the dialog.
    * @param configData
    *           The configuration values of the GUI.
    * @param style
    *           The style which may be OPEN or SAVE.
    * @param text
    *           The text widget for the text file.
    * @return Returns the path and filename.
    */
   public static boolean fileDialog(Shell shell, GuiConfigData configData,
         int style, StyledText text) {
      FileDialog fileDialog = new FileDialog(shell, style);
      fileDialog.setFilterNames(new String[] { configData.getLangRes()
            .getString("filedialog_filternames_all") });
      fileDialog.setFilterExtensions(new String[] { "*.*" });
      if (!StringUtil.isValueEmpty(configData.getFilename())) {
         fileDialog.setFileName(configData.getFilename());
      }
      status = false;

      if (style == SWT.OPEN) {
         do {
            path = fileDialog.open();
            if (!StringUtil.isValueEmpty(path)) {
               file = new File(path);
               if (file.getAbsoluteFile().exists()) {
                  status = IOUtil.open(file, text);
                  if (status) {
                     configData.setFilename(path);
                  }
                  else {
                     errorMsg(shell, configData.getLangRes().getString(
                           "filedialog_error_open"));
                  }
               }
               else {
                  errorMsg(shell, configData.getLangRes().getString(
                        "filedialog_error_found"));
               }
            }
            else {
               return false;
            }
         } while (!status);
      }
      else if (style == SWT.SAVE) {
         do {
            path = fileDialog.open();
            if (!StringUtil.isValueEmpty(path)) {
               file = new File(path);
               if (file.getAbsoluteFile().exists()) {
                  if (overWriteFile(shell, configData)) {
                     status = IOUtil.save(file, text.getText());
                     if (status) {
                        configData.setFilename(path);
                     }
                     else {
                        errorMsg(shell, configData.getLangRes().getString(
                              "filedialog_error_save"));
                     }
                  }
               }
               else {
                  status = IOUtil.save(file, text.getText());
                  if (status) {
                     configData.setFilename(path);
                  }
                  else {
                     errorMsg(shell, configData.getLangRes().getString(
                           "filedialog_error_save"));
                  }
               }
            }
            else {
               return false;
            }
         } while (!status);
      }

      return true;
   }

   /**
    * For opening a text file at startup.
    * 
    * @param text
    *           The text widget for the text file.
    * @param shell
    *           The parent of the message box.
    * @param configData
    *           The configuration values of the GUI.
    */
   public static void open(StyledText text, Shell shell,
         GuiConfigData configData) {
      path = configData.getFilename();
      if (!StringUtil.isValueEmpty(path)) {
         file = new File(path);
         if (file.getAbsoluteFile().exists()) {
            status = IOUtil.open(file, text);
            if (!status) {
               errorMsg(shell, configData.getLangRes().getString(
                     "filedialog_error_open"));
            }
         }
         else {
            errorMsg(shell, configData.getLangRes().getString(
                  "filedialog_error_found"));
         }
      }

      configData.setHasChanged(false);
   }

   /**
    * Message box for Warnings.
    * 
    * @param shell
    *           The parent of the message box.
    * @param text
    *           The text which will be shown.
    */
   private static void errorMsg(Shell shell, String text) {
      MessageBoxUtil.newMessageBox(shell, SWT.ICON_WARNING | SWT.OK, text, path)
            .open();
   }

   /**
    * Message box for overwriting existing files.
    * 
    * @param shell
    *           The parent of the message box.
    * @param configData
    *           The configuration values of the GUI.
    * @return Returns the choice of the user.
    */
   private static boolean overWriteFile(Shell shell, GuiConfigData configData) {
      if (MessageBoxUtil.newMessageBox(shell,
            SWT.ICON_WARNING | SWT.YES | SWT.NO,
            configData.getLangRes().getString("filedialog_save_overwrite"),
            path).open() == SWT.YES) {
         return true;
      }

      return false;
   }

   /**
    * Message box of "Yes", "No" & "Cancel".
    * 
    * @param shell
    *           The parent of the message box.
    * @param configData
    *           The configuration values of the GUI.
    * @return Returns the choice of the user.
    */
   public static int saveYesNoCancel(Shell shell, GuiConfigData configData) {
      String message;

      if (StringUtil.isValueEmpty(configData.getFilename())) {
         message = configData.getLangRes()
               .getString("filedialog_save_untitled");
      }
      else {
         message = "\"" + configData.getFilename() + "\"";
      }

      return MessageBoxUtil.newMessageBox(shell,
            SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL,
            configData.getLangRes().getString("filedialog_save"), message)
            .open();
   }
}
