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

import org.eclipse.swt.custom.StyledText;

import net.sf.pisee.swtextedit.config.GuiConfigData;

/**
 * Utility class for the styled text widget.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class TextUtil {

   /** Private empty constructor. */
   private TextUtil() {
   }

   /**
    * Wraps the lines of the styled text widget.
    * 
    * @param configData
    *           The configuration data of the GUI.
    * @param text
    *           The styled text widget.
    */
   public static void wrap(GuiConfigData configData, StyledText text) {
      if (configData.isWrap()) {
         configData.setWrap(false);
         text.setWordWrap(configData.isWrap());
      }
      else {
         configData.setWrap(true);
         text.setWordWrap(configData.isWrap());
      }
   }
}
