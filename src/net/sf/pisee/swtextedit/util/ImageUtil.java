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

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Utility class for the GUI.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class ImageUtil {

   /** Private empty constructor. */
   private ImageUtil() {
   }

   /**
    * Generates a new graphic.
    * 
    * @param display
    *           The parent of the image.
    * @param image
    *           Name of the image.
    * @return Returns the graphic.
    */
   public static Image newImage(Display display, String image) {
      try {
         Image img = new Image(display, ClassLoader.getSystemResource(
               "img/" + image).openStream());
         img.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

         return img;
      } catch (IOException e) {
         e.printStackTrace();
      }

      return null;
   }
}
