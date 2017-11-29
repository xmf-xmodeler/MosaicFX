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
package net.sf.pisee.swtextedit.config;

import java.util.ResourceBundle;

import net.sf.pisee.swtextedit.util.LangUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * All configuration data of the GUI.<br>
 * All variables are initialized with the default values.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class GuiConfigData {

   /** Constants for the program version and the buildID. */
   private final String version = "0.1.6 (alpha)", buildID = "20060905-5";

   /** Boolean value if text has changed. */
   private boolean hasChanged = false;

   /** Specifies if the shell should be maximized. */
   private boolean maximize = true;

   /** Specifies if the text in the styledtext should be wrapped. */
   private boolean wrap = true;

   /** Values for the size of the Shell. */
   private int shellWidth = 640, shellHeight = 480;

   /** Value for the undo / redo action. */
   private int undoStackSize = 100;

   /** The background color of the text widget. */
   private Color backgroundColor = new Color(null, 255, 255, 255);

   /** The foreground color of the text widget. */
   private Color foregroundColor = new Color(null, 0, 0, 0);

   /** The selection background color of the text widget. */
   private Color selectionBackground = new Color(null, 178, 180, 191);

   /** The selection foreground color of the text widget. */
   private Color selectionForeground = new Color(null, 0, 0, 0);

   /** The font of the text widget. */
   private Font font = new Font(null, new FontData("Tahoma", 8, SWT.NORMAL));

   /** The language file of the specific language. */
   private ResourceBundle langRes = ResourceBundle.getBundle("lang/"
 + LangUtil.parseLang());

   /** The path and name of the open file. */
   private String filename = null;

   /** Instance of the system language. */
   private String language = LangUtil.parseLang();

   /** Public empty constructor. */
   public GuiConfigData() {
   }

   /**
    * @return Return version.
    */
   public String getVersion() {
      return version;
   }

   /**
    * @return Return buildID.
    */
   public String getBuildID() {
      return buildID;
   }

   /**
    * @return Return hasChanged.
    */
   public boolean isHasChanged() {
      return hasChanged;
   }

   /**
    * @param hasChanged
    *           Set hasChanged.
    */
   public void setHasChanged(boolean hasChanged) {
      this.hasChanged = hasChanged;
   }

   /**
    * @return Return maximize.
    */
   public boolean isMaximize() {
      return maximize;
   }

   /**
    * @param maximize
    *           Set maximize.
    */
   public void setMaximize(boolean maximize) {
      this.maximize = maximize;
   }

   /**
    * @return Return wrap.
    */
   public boolean isWrap() {
      return wrap;
   }

   /**
    * @param wrap
    *           Set wrap.
    */
   public void setWrap(boolean wrap) {
      this.wrap = wrap;
   }

   /**
    * @return Return shellHeight.
    */
   public int getShellHeight() {
      return shellHeight;
   }

   /**
    * @param shellHeight
    *           Set shellHeight.
    */
   public void setShellHeight(int shellHeight) {
      this.shellHeight = shellHeight;
   }

   /**
    * @return Return shellWidth.
    */
   public int getShellWidth() {
      return shellWidth;
   }

   /**
    * @param shellWidth
    *           Set shellWidth.
    */
   public void setShellWidth(int shellWidth) {
      this.shellWidth = shellWidth;
   }

   /**
    * @return Return undoStackSize.
    */
   public int getUndoStackSize() {
      return undoStackSize;
   }

   /**
    * @param undoStackSize
    *           Set undoStackSize.
    */
   public void setUndoStackSize(int undoStackSize) {
      this.undoStackSize = undoStackSize;
   }

   /**
    * @return Return backgroundColor.
    */
   public Color getBackgroundColor() {
      return backgroundColor;
   }

   /**
    * @param backgroundColor
    *           Set backgroundColor.
    */
   public void setBackgroundColor(Color backgroundColor) {
      this.backgroundColor = backgroundColor;
   }

   /**
    * @return Return foregroundColor.
    */
   public Color getForegroundColor() {
      return foregroundColor;
   }

   /**
    * @param foregroundColor
    *           Set foregroundColor.
    */
   public void setForegroundColor(Color foregroundColor) {
      this.foregroundColor = foregroundColor;
   }

   /**
    * @return Return selectionBackground.
    */
   public Color getSelectionBackground() {
      return selectionBackground;
   }

   /**
    * @param selectionBackground
    *           Set selectionBackground.
    */
   public void setSelectionBackground(Color selectionBackground) {
      this.selectionBackground = selectionBackground;
   }

   /**
    * @return Return selectionForeground.
    */
   public Color getSelectionForeground() {
      return selectionForeground;
   }

   /**
    * @param selectionForeground
    *           Set selectionForeground.
    */
   public void setSelectionForeground(Color selectionForeground) {
      this.selectionForeground = selectionForeground;
   }

   /**
    * @return Return font.
    */
   public Font getFont() {
      return font;
   }

   /**
    * @param font
    *           Set font.
    */
   public void setFont(Font font) {
      this.font = font;
   }

   /**
    * @return Return langRes.
    */
   public ResourceBundle getLangRes() {
      return langRes;
   }

   /**
    * @return Return filename.
    */
   public String getFilename() {
      return filename;
   }

   /**
    * @param filename
    *           Set filename.
    */
   public void setFilename(String filename) {
      this.filename = filename;
   }

   /**
    * @return Return language.
    */
   public String getLanguage() {
      return language;
   }

   /**
    * @param language
    *           Set language.
    */
   public void setLanguage(String language) {
      this.language = language;
      this.langRes = ResourceBundle.getBundle("lang/" + language);
   }
}
