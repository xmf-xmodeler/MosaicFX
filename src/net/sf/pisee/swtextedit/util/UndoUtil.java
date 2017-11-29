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

import java.util.List;

import org.eclipse.swt.custom.StyledText;

/**
 * Utility class for the Undo / Redo action.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class UndoUtil {

   /** Instance of the boolean value if the text was replaced. */
   private boolean replaced;

   /** Instance of the caret positionin the text. */
   private int caret;

   /** Instance of the text for the undo action. */
   private String text;

   /** Private empty constructor. */
   private UndoUtil() {
   }

   /**
    * Creates a new instance of the class with the given parameters.
    * 
    * @param text
    *           The text for the undo action.
    * @param replaced
    *           The boolean value if the text was replaced.
    * @param caret
    *           The int value for the caret position.
    */
   public UndoUtil(String text, boolean replaced, int caret) {
      this.text = text;
      this.replaced = replaced;
      this.caret = caret;
   }

   /**
    * Undo function of the styled text.
    * 
    * @param undoStack
    *           The list which save all undo actions.
    * @param redoStack
    *           The list which save all redo actions.
    * @param styledText
    *           The text widget for the undo function.
    */
   public static void undo(List undoStack, List redoStack, StyledText styledText) {
      if (undoStack.size() > 0) {
         UndoUtil lastEdit = (UndoUtil) undoStack.remove(0);
         int text = lastEdit.getText().length();
         int caret = lastEdit.getCaret();
         try {
            if (lastEdit.isReplaced()) {
               styledText.replaceTextRange(caret, 0, lastEdit.getText());
            }
            else {
               styledText.replaceTextRange(caret - text, text, "");
            }
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
         }
         redoStack.add(0, lastEdit);
      }
   }

   /**
    * Redo function of the styled text.
    * 
    * @param redoStack
    *           The list which save all redo actions.
    * @param undoStack
    *           The list which save all undo actions.
    * @param styledText
    *           The text widget for the undo function.
    */
   public static void redo(List redoStack, List undoStack, StyledText styledText) {
      if (redoStack.size() > 0) {
         UndoUtil lastEdit = (UndoUtil) redoStack.remove(0);
         int text = lastEdit.getText().length();
         int caret = lastEdit.getCaret();
         try {
            if (lastEdit.isReplaced()) {
               styledText.replaceTextRange(caret, text, "");
            }
            else {
               styledText.replaceTextRange(caret - text, 0, lastEdit.getText());
            }
         } catch (IllegalArgumentException e) {
            e.printStackTrace();
         }
         undoStack.add(0, lastEdit);
      }
   }

   /**
    * @return Return replaced.
    */
   public boolean isReplaced() {
      return replaced;
   }

   /**
    * @return Return caret.
    */
   public int getCaret() {
      return caret;
   }

   /**
    * @return Return text.
    */
   public String getText() {
      return text;
   }
}
