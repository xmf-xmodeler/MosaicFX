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

import net.sf.pisee.swtextedit.util.StringUtil;

import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * Creates a new MessageBox widget with the given parameters.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class MessageBoxUtil {

   /** Private empty constructor. */
   private MessageBoxUtil() {
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.MessageBoxUtil#createMessageBox(Shell,
    *      int, String, String)
    */
   public static MessageBox newMessageBox(Shell parent, int style, String text,
         String message) {
      return createMessageBox(parent, style, text, message);
   }

   /**
    * Creates a new MessageBox with the given Parameters.
    * 
    * @param parent
    *           The parent of the new mb.
    * @param style
    *           The style of the new mb.
    * @param text
    *           The titel text of the mb.
    * @param message
    *           The message or the question.
    * @return Returns the new mb.
    */
   private static MessageBox createMessageBox(Shell parent, int style,
         String text, String message) {
      MessageBox mb = new MessageBox(parent, style);

      if (!StringUtil.isValueEmpty(text)) {
         mb.setText(text);
      }

      if (!StringUtil.isValueEmpty(message)) {
         mb.setMessage(message);
      }

      return mb;
   }
}
