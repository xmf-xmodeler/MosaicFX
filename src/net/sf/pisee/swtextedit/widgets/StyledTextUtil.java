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

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

/**
 * Creates a new StyledText widget with the given parameters.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class StyledTextUtil {

   /** Private empty constructor. */
   private StyledTextUtil() {
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.StyledTextUtil#createStyledText(Composite,
    *      int, Menu, GridData, boolean, Font, Color, Color, Color, Color,
    *      boolean)
    */
   public static StyledText newStyledText(Composite parent, int style,
         Menu menu, GridData gridData, boolean focus, Font font, Color back,
         Color fore, Color selectBack, Color selectFore, boolean wrap) {
      return createStyledText(parent, style, menu, gridData, focus, font, back,
            fore, selectBack, selectFore, wrap);
   }

   /**
    * Creates a new StyledText with the given parameters.
    * 
    * @param parent
    *           The parent of the styled text.
    * @param style
    *           The style of the styled text.
    * @param menu
    *           The pop up menu of the styled text.
    * @param gridData
    *           The layout of the styled text.
    * @param focus
    *           If it's true the focus is set to the styled text widget.
    * @param font
    *           The font of the styled text.
    * @param back
    *           The backgroundcolor of the styled text.
    * @param fore
    *           The foregroundcolor of the styled text.
    * @param selectBack
    *           The selection background color of the styled text.
    * @param selectFore
    *           The selection foreground color of the styled text.
    * @param wrap
    *           Specifies if the widget wraps lines.
    * @return Returns of the text.
    */
   private static StyledText createStyledText(Composite parent, int style,
         Menu menu, GridData gridData, boolean focus, Font font, Color back,
         Color fore, Color selectBack, Color selectFore, boolean wrap) {
      StyledText styledText = new StyledText(parent, style);

      if (menu != null) {
         styledText.setMenu(menu);
      }

      if (gridData != null) {
         styledText.setLayoutData(gridData);
      }

      if (focus) {
         styledText.setFocus();
      }

      if (font != null) {
         styledText.setFont(font);
      }

      if (back != null) {
         styledText.setBackground(back);
      }

      if (fore != null) {
         styledText.setForeground(fore);
      }

      if (selectBack != null) {
         styledText.setSelectionBackground(selectBack);
      }

      if (selectFore != null) {
         styledText.setSelectionForeground(selectFore);
      }

      styledText.setWordWrap(wrap);

      return styledText;
   }
}
