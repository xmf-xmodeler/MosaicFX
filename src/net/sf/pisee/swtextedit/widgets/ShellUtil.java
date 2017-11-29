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

import net.sf.pisee.swtextedit.util.ImageUtil;
import net.sf.pisee.swtextedit.util.StringUtil;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;

/**
 * Creates a new Shell widget with the given parameters.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class ShellUtil {

   /** Private empty constructor. */
   private ShellUtil() {
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.ShellUtil#createShell(Shell, int,
    *      String, String, int, int, GridLayout, boolean, boolean)
    */
   public static Shell newShell(Shell parent, int style, String textID,
         String icon, int width, int height, GridLayout gridLayout,
         boolean middle, boolean maximize) {
      return createShell(parent, style, textID, icon, width, height,
            gridLayout, middle, maximize);
   }

   /**
    * @see net.sf.pisee.swtextedit.widgets.ShellUtil#createShell(Shell, int,
    *      String, String, int, int, GridLayout, boolean, boolean)
    */
   public static Shell newShell(int style, String textID, String icon,
         int width, int height, GridLayout gridLayout, boolean middle,
         boolean maximize) {
      return createShell(null, style, textID, icon, width, height, gridLayout,
            middle, maximize);
   }

   /**
    * Creates a new Shell with the given Parameters.
    * 
    * @param parent
    *           Receives the parent of the shell (Shell).
    * @param style
    *           Receives the style of the shell.
    * @param textID
    *           Receives the text of the shell.
    * @param icon
    *           The icon of the shell.
    * @param width
    *           The width of the shell.
    * @param height
    *           The height of the shell.
    * @param gridLayout
    *           The layout of the shell (GridLayout).
    * @param middle
    *           Specifies if the shell should be placed in the middle of the
    *           screen.
    * @param maximize
    *           Specifies if the shell should be maximized.
    * @return Returns the new Shell.
    */
   private static Shell createShell(Shell parent, int style, String textID,
         String icon, int width, int height, GridLayout gridLayout,
         boolean middle, boolean maximize) {
      Shell shell = null;

      if (parent != null) {
         shell = new Shell(parent, style);
      }
      else {
         shell = new Shell(style);
      }

      if (!StringUtil.isValueEmpty(textID)) {
         shell.setData("TEXTID", textID);
      }

      if (!StringUtil.isValueEmpty(icon)) {
         shell.setImage(ImageUtil.newImage(shell.getDisplay(), icon));
      }

      shell.setSize(width, height);

      if (gridLayout != null) {
         shell.setLayout(gridLayout);
      }

      if (middle) {
         Rectangle r = shell.getDisplay().getBounds();
         Rectangle s = shell.getBounds();
         int shellX = (r.width - s.width) / 2;
         int shellY = (r.height - s.height) / 2;
         shell.setLocation(shellX, shellY);
      }

      if (maximize) {
         shell.setMaximized(maximize);
      }

      return shell;
   }
}
