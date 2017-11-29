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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Creates a new Label widget with the given parameters.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class LabelUtil {

   /** Private empty constructor. */
   private LabelUtil() {
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.LabelUtil#createLabel(Composite, int,
    *      GridData, String)
    */
   public static Label hLine(Composite parent) {
      return createLabel(parent, SWT.SEPARATOR | SWT.HORIZONTAL,
            GridUtil.newGridData(), null);
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.LabelUtil#createLabel(Composite, int,
    *      GridData, String)
    */
   public static Label vLine(Composite parent) {
      return createLabel(parent, SWT.SEPARATOR | SWT.VERTICAL,
            GridUtil.newGridData(false, true), null);
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.LabelUtil#createLabel(Composite, int,
    *      GridData, String)
    */
   public static Label newLabel(Composite parent, GridData gridData,
         String textID) {
      return createLabel(parent, SWT.LEFT, gridData, textID);
   }

   /**
    * Creates a new Label with the given Parameters.
    * 
    * @param parent
    *           The parent of the new label.
    * @param style
    *           The style of the new label.
    * @param gridData
    *           The layout of the label (GridData).
    * @param textID
    *           The text of the label.
    * @return Returns the new label.
    */
   private static Label createLabel(Composite parent, int style,
         GridData gridData, String textID) {
      Label label = new Label(parent, style);

      if (gridData != null) {
         label.setLayoutData(gridData);
      }

      if (!StringUtil.isValueEmpty(textID)) {
         label.setData("TEXTID", textID);
      }

      return label;
   }
}
