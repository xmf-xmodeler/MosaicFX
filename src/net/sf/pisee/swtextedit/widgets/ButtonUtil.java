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

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * Creates a new Button widget with the given parameters.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class ButtonUtil {

   /** Private empty constructor. */
   private ButtonUtil() {
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.ButtonUtil#createButton(Composite,
    *      int, String, GridData, SelectionListener)
    */
   public static Button newButton(Composite parent, int style, String textID,
         GridData gridData, SelectionListener select) {
      return createButton(parent, style, textID, gridData, select);
   }

   /**
    * Creates a new Button with the given Parameters.
    * 
    * @param parent
    *           The parent of the new button.
    * @param style
    *           The style of the new button.
    * @param textID
    *           Sets the text of the button.
    * @param gridData
    *           The layout of the button (GridData).
    * @param select
    *           Adds the SelectionListener of the button.
    * @return Returns the new button.
    */
   private static Button createButton(Composite parent, int style,
         String textID, GridData gridData, SelectionListener select) {
      Button button = new Button(parent, style);

      if (!StringUtil.isValueEmpty(textID)) {
         button.setData("TEXTID", textID);
      }

      if (gridData != null) {
         button.setLayoutData(gridData);
      }

      if (select != null) {
         button.addSelectionListener(select);
      }

      return button;
   }
}
