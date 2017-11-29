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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import net.sf.pisee.swtextedit.config.GuiConfigData;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Utility class for the language.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class LangUtil {

   /** Instance of Iterator. */
   private static Iterator iter;

   /** Instance of Widget. */
   private static Widget widget;

   /** Private empty constructor. */
   private LangUtil() {
   }

   /**
    * Detect the language of the system.
    * 
    * @return Return the language string.
    */
   public static String parseLang() {
      String lang = Locale.getDefault().getLanguage().toUpperCase();

      if ("EN".equals(lang)) {
         return lang;
      }
      else if ("DE".equals(lang)) {
         return lang;
      }
      else {
         System.err.println("The system language is neither English nor German!");

         return "EN";
      }
   }

   /**
    * Calls the methods setText & setToolTipText.
    * 
    * @param widgets
    *           All widgets of the GUI.
    * @param configData
    *           The configuration data of the GUI.
    */
   public static void setLang(HashSet widgets, GuiConfigData configData) {
      setText(widgets, configData);
      setToolTipText(widgets, configData);
   }

   /**
    * Changes the language (text) of the specific widget.
    * 
    * @param widgets
    *           The widget of which the language should be changed.
    * @param configData
    *           Instance of the configuration data.
    */
   private static void setText(HashSet widgets, GuiConfigData configData) {
      for (iter = widgets.iterator(); iter.hasNext();) {
         widget = (Widget) iter.next();
         if (widget.getData("TEXTID") != null) {
            if (widget.getClass().equals(MenuItem.class)) {
               ((MenuItem) widget).setText(configData.getLangRes().getString(
                     (String) (widget.getData("TEXTID"))));
            }
            else if (widget.getClass().equals(Button.class)) {
               ((Button) widget).setText(configData.getLangRes().getString(
                     (String) (widget.getData("TEXTID"))));
            }
            else if (widget.getClass().equals(Shell.class)) {
               ((Shell) widget).setText(configData.getLangRes().getString(
                     (String) (widget.getData("TEXTID"))));
            }
            else if (widget.getClass().equals(Label.class)) {
               ((Label) widget).setText(configData.getLangRes().getString(
                     (String) (widget.getData("TEXTID"))));
            }
            else if (widget.getClass().equals(Group.class)) {
               ((Group) widget).setText(configData.getLangRes().getString(
                     (String) (widget.getData("TEXTID"))));
            }
            else if (widget.getClass().equals(Link.class)) {
               ((Link) widget).setText(configData.getLangRes().getString(
                     (String) (widget.getData("TEXTID"))));
            }
            else {
               System.out.println("Widget can't be found!");
            }
         }
      }
   }

   /**
    * Changes the language (tooltip) of the specific widget.
    * 
    * @param widgets
    *           The widget of which the language should be changed.
    * @param configData
    *           Instance of the configuration data.
    */
   private static void setToolTipText(HashSet widgets, GuiConfigData configData) {
      for (iter = widgets.iterator(); iter.hasNext();) {
         widget = (Widget) iter.next();
         if (widget.getData("TOOLTIPID") != null) {
            if (widget.getClass().equals(ToolItem.class)) {
               ((ToolItem) widget).setToolTipText(configData.getLangRes()
                     .getString((String) (widget.getData("TOOLTIPID"))));
            }
            else if (widget.getClass().equals(TrayItem.class)) {
               ((TrayItem) widget).setToolTipText(configData.getLangRes()
                     .getString((String) (widget.getData("TOOLTIPID"))));
            }
            else if (widget.getClass().equals(Label.class)) {
               ((Label) widget).setToolTipText(configData.getLangRes()
                     .getString((String) (widget.getData("TOOLTIPID"))));
            }
            else {
               System.out.println("Widget can't be found!");
            }
         }
      }
   }
}
