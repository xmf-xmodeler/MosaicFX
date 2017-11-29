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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility class for printing the text.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class PrintUtil {

   /** Instance of GC. */
   private static GC gc;

   /** Instance of Printer. */
   private static Printer printer;

   /** The text in the text widget. */
   private static String textToPrint;

   /** The StringBuffer for the printer. */
   private static StringBuffer wordBuffer;

   /** The Integer values of the site layout. */
   private static int leftMargin, rightMargin, topMargin, bottomMargin,
         tabWidth, lineHeight, x, y, index, end;

   /** Private empty constructor. */
   private PrintUtil() {
   }

   /**
    * Opens the print dialog.
    * 
    * @param shell
    *           The parent of the dialog.
    * @param text
    *           The text of the text widget.
    * @param font
    *           The font of the text widget.
    * @param foregroundColor
    *           The foreground color of the text widget.
    * @param backgroundColor
    *           The background color of the text widget.
    */
   public static void printDialog(Shell shell, StyledText text,
         final Font font, final Color foregroundColor,
         final Color backgroundColor) {
      PrintDialog dialog = new PrintDialog(shell, SWT.NONE);
      PrinterData data = dialog.open();
      if (data == null) {
         return;
      }

      if (data.printToFile) {
         data.fileName = "print.out";
      }
      textToPrint = text.getText();
      printer = new Printer(data);

      Thread printingThread = new Thread("Printing") {
         public void run() {
            print(font, foregroundColor, backgroundColor);
            printer.dispose();
         }
      };
      printingThread.start();
   }

   /**
    * Prepares the printer.
    * 
    * @param font
    *           The font of the text widget.
    * @param foregroundColor
    *           The foreground color of the text widget.
    * @param backgroundColor
    *           The background color of the text widget.
    */
   private static void print(Font font, Color foregroundColor,
         Color backgroundColor) {
      if (printer.startJob("Text")) {
         Rectangle clientArea = printer.getClientArea();
         Rectangle trim = printer.computeTrim(0, 0, 0, 0);
         Point dpi = printer.getDPI();
         leftMargin = dpi.x + trim.x;
         rightMargin = clientArea.width - dpi.x + trim.x + trim.width;
         topMargin = dpi.y + trim.y;
         bottomMargin = clientArea.height - dpi.y + trim.y + trim.height;

         int tabSize = 4;
         StringBuffer tabBuffer = new StringBuffer(tabSize);
         for (int i = 0; i < tabSize; i++)
            tabBuffer.append(' ');
         String tabs = tabBuffer.toString();

         gc = new GC(printer);

         FontData fontData = font.getFontData()[0];
         Font printerFont = new Font(printer, fontData.getName(),
               fontData.getHeight(), fontData.getStyle());
         gc.setFont(printerFont);
         tabWidth = gc.stringExtent(tabs).x;
         lineHeight = gc.getFontMetrics().getHeight();

         RGB rgb = foregroundColor.getRGB();
         Color printerForegroundColor = new Color(printer, rgb);
         gc.setForeground(printerForegroundColor);

         rgb = backgroundColor.getRGB();
         Color printerBackgroundColor = new Color(printer, rgb);
         gc.setBackground(printerBackgroundColor);

         printText();
         printer.endJob();

         printerFont.dispose();
         printerForegroundColor.dispose();
         printerBackgroundColor.dispose();
         gc.dispose();
      }
   }

   /**
    * Prepares the Text.
    */
   private static void printText() {
      printer.startPage();
      wordBuffer = new StringBuffer();
      x = leftMargin;
      y = topMargin;
      index = 0;
      end = textToPrint.length();
      while (index < end) {
         char c = textToPrint.charAt(index);
         index++;
         if (c != 0) {
            if (c == 0x0a || c == 0x0d) {
               if (c == 0x0d && index < end
                     && textToPrint.charAt(index) == 0x0a) {
                  index++;
               }
               printWordBuffer();
               newline();
            }
            else {
               if (c != '\t') {
                  wordBuffer.append(c);
               }
               if (Character.isWhitespace(c)) {
                  printWordBuffer();
                  if (c == '\t') {
                     x += tabWidth;
                  }
               }
            }
         }
      }
      if (y + lineHeight <= bottomMargin) {
         printer.endPage();
      }
   }

   /**
    * Prepares the WordBuffer.
    */
   private static void printWordBuffer() {
      if (wordBuffer.length() > 0) {
         String word = wordBuffer.toString();
         int wordWidth = gc.stringExtent(word).x;
         if (x + wordWidth > rightMargin) {
            newline();
         }
         gc.drawString(word, x, y, false);
         x += wordWidth;
         wordBuffer = new StringBuffer();
      }
   }

   /**
    * Generates a new line.
    */
   private static void newline() {
      x = leftMargin;
      y += lineHeight;
      if (y + lineHeight > bottomMargin) {
         printer.endPage();
         if (index + 1 < end) {
            y = topMargin;
            printer.startPage();
         }
      }
   }
}
