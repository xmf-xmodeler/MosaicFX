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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Creates a new Text widget with the given parameters.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class TextUtil {

   /** Private empty constructor. */
   private TextUtil() {
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.TextUtil#createText(Composite, int,
    *      GridData, boolean, boolean)
    */
   public static Text newText(Composite parent, int style, GridData gridData,
         boolean focus, boolean editAble) {
      return createText(parent, style, gridData, focus, editAble);
   }

   /**
    * Creates a new Text with the given parameters.
    * 
    * @param parent
    *           The parent of the text.
    * @param style
    *           The style of the text.
    * @param gridData
    *           The layout of the text.
    * @param focus
    *           If it's true the focus is set to the text widget.
    * @param editAble
    *           True if the text is edit able or false if it's otherwise.
    * @return Returns of the text.
    */
   private static Text createText(Composite parent, int style,
         GridData gridData, boolean focus, boolean editAble) {
      Text text = new Text(parent, style);

      if (gridData != null) {
         text.setLayoutData(gridData);
      }

      if (focus) {
         text.setFocus();
      }

      if (!editAble) {
         text.setEditable(editAble);
      }

      return text;
   }
}
