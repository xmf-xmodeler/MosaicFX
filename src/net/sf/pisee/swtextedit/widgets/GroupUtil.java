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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * Creates a new Group widget with the given parameters.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class GroupUtil {

   /** Private empty constructor. */
   private GroupUtil() {
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.GroupUtil#createGroup(Composite, int,
    *      GridData, GridLayout, String)
    */
   public static Group newGroup(Composite parent, int style, GridData gridData,
         GridLayout gridLayout, String textID) {
      return createGroup(parent, style, gridData, gridLayout, textID);
   }

   /**
    * Creates a new Group with the given parameters.
    * 
    * @param parent
    *           The parent of the new group.
    * @param style
    *           The style of the new group.
    * @param gridData
    *           The layout of the group (GridData).
    * @param gridLayout
    *           The layout of the widgets in the group (GridLayout).
    * @param textID
    *           The text of the group.
    * @return Returns the new group.
    */
   private static Group createGroup(Composite parent, int style,
         GridData gridData, GridLayout gridLayout, String textID) {
      Group group = new Group(parent, style);

      if (gridData != null) {
         group.setLayoutData(gridData);
      }

      if (gridLayout != null) {
         group.setLayout(gridLayout);
      }

      if (!StringUtil.isValueEmpty(textID)) {
         group.setData("TEXTID", textID);
      }

      return group;
   }
}
