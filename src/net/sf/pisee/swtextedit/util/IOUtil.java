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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.custom.StyledText;

/**
 * Input / Output class.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class IOUtil {

   /** Private empty constructor. */
   private IOUtil() {
   }

   /**
    * Method for opening a file into the text widget.
    * 
    * @param file
    *           The file which will be converted into a String.
    * @param text
    *           The String of the file loaded into this text widget.
    * @return Returns the success of opening the file.
    */
   public static boolean open(File file, StyledText text) {
      try {
         BufferedReader br = new BufferedReader(new FileReader(file));
         StringBuffer buff = new StringBuffer();
         String line;
         while ((line = br.readLine()) != null) {
            buff.append(line + "\n");
         }
         text.setText(buff.toString());
         br.close();
         return true;
      } catch (IOException e) {
         e.printStackTrace();
      }

      return false;
   }

   /**
    * Method for saving the String into a file.
    * 
    * @param file
    *           The file which will be saved.
    * @param text
    *           The content of the file as String.
    * @return Returns the success of saving the file.
    */
   public static boolean save(File file, String text) {
      try {
         BufferedWriter bw = new BufferedWriter(new FileWriter(file));
         bw.write(text);
         bw.close();
         return true;
      } catch (IOException e) {
         e.printStackTrace();
      }

      return false;
   }
}
