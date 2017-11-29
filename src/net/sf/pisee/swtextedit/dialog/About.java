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
package net.sf.pisee.swtextedit.dialog;

import java.util.HashSet;

import net.sf.pisee.swtextedit.config.GuiConfigData;
import net.sf.pisee.swtextedit.config.GuiIcons;
import net.sf.pisee.swtextedit.util.LangUtil;
import net.sf.pisee.swtextedit.widgets.ButtonUtil;
import net.sf.pisee.swtextedit.widgets.GridUtil;
import net.sf.pisee.swtextedit.widgets.GroupUtil;
import net.sf.pisee.swtextedit.widgets.LabelUtil;
import net.sf.pisee.swtextedit.widgets.LinkUtil;
import net.sf.pisee.swtextedit.widgets.ShellUtil;
import net.sf.pisee.swtextedit.widgets.TextUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * About dialog for information around the application.
 * 
 * @author pisee@users.sourceforge.net
 */
public final class About {

   /** Instances of the Groups. */
   private Group info, license;

   /** Instance of GUIConfigData for all configuration variables. */
   private GuiConfigData configData;

   /** HashSet for the language control. */
   private HashSet widgets = new HashSet();

   /** Instances for the version & buildID. */
   private Label version, buildID;

   /** Instance of Shell. */
   private Shell dialog;

   /** Private empty constructor. */
   private About() {
   }

   /**
    * Public constructor.
    * 
    * @param parent
    *           The parent of the dialog.
    * @param configData
    *           The configuration values of the GUI.
    */
   public About(Shell parent, GuiConfigData configData) {
      this.configData = configData;

      shell(parent);
      groups();
      okButton();

      LangUtil.setLang(widgets, configData);

      version.setText(version.getText() + configData.getVersion());
      buildID.setText(buildID.getText() + configData.getBuildID());

      dialog.open();
   }

   /**
    * Creates a new Shell for the about dialog.
    * 
    * @param parent
    *           The parent of the dialog.
    */
   private void shell(Shell parent) {
      dialog = ShellUtil.newShell(parent, SWT.DIALOG_TRIM
            | SWT.APPLICATION_MODAL, "about", GuiIcons.text, 450, 480,
            GridUtil.newGridLayout(5, 5, 5, 5, 1, false), true, false);
      widgets.add(dialog);
   }

   /**
    * Two groups for program information and the license.
    */
   private void groups() {
      info = GroupUtil.newGroup(dialog, SWT.SHADOW_IN, GridUtil.newGridData(),
            GridUtil.newGridLayout(10, 10, 1, 5, 1, false), "about_info");
      widgets.add(info);

      license = GroupUtil.newGroup(dialog, SWT.SHADOW_OUT,
            GridUtil.newGridData(true, true), GridUtil.newGridLayout(10, 10, 1,
                  5, 1, false), "about_license");
      widgets.add(license);

      infoGroup();
      licenseGroup();
   }

   /**
    * Information and weblinks.
    */
   private void infoGroup() {
      widgets.add(LabelUtil.newLabel(info, GridUtil.newGridData(),
            "about_info_name"));
      version = LabelUtil.newLabel(info, GridUtil.newGridData(),
            "about_info_version");
      widgets.add(version);
      buildID = LabelUtil.newLabel(info, GridUtil.newGridData(),
            "about_info_buildid");
      widgets.add(buildID);

      LabelUtil.hLine(info);

      widgets.add(LinkUtil.newLink(info, SWT.NONE, GridUtil.newGridData(),
            "about_info_author", piseeMail));
      widgets.add(LinkUtil.newLink(info, SWT.NONE, GridUtil.newGridData(),
            "about_info_web", piseeHP));
   }

   /**
    * Info about the license.
    */
   private void licenseGroup() {
      widgets.add(LabelUtil.newLabel(license, GridUtil.newGridData(),
            "about_license_swt"));
      widgets.add(LinkUtil.newLink(license, SWT.NONE, GridUtil.newGridData(),
            "about_license_swt_link", epl));

      LabelUtil.hLine(license);

      widgets.add(LinkUtil.newLink(license, SWT.NONE, GridUtil.newGridData(),
            "about_license_gpl", gpl));

      Text text = TextUtil.newText(license, SWT.BORDER | SWT.MULTI
            | SWT.V_SCROLL, GridUtil.newGridData(true, true), false, false);
      text.setText(configData.getLangRes().getString("license"));
   }

   /**
    * Button for closing the dialog.
    */
   private void okButton() {
      Button button = ButtonUtil.newButton(dialog, SWT.PUSH, "button_ok",
            GridUtil.newGridData(SWT.CENTER, SWT.FILL, true, false, 75, -1),
            close);
      widgets.add(button);
      button.setFocus();
      button.setLocation(dialog.getBounds().width / 2,
            dialog.getBounds().height - 50);
      dialog.setDefaultButton(button);
   }

   /** Listener for the mail. */
   private SelectionListener piseeMail = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         Program.launch(configData.getLangRes().getString("mail"));
      }
   };

   /** Listener for the website. */
   private SelectionListener piseeHP = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         Program.launch(configData.getLangRes().getString("web"));
      }
   };

   /** Listener for the eclipse license. */
   private SelectionListener epl = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         Program.launch(configData.getLangRes().getString("epl"));
      }
   };

   /** Listener for the GPL license. */
   private SelectionListener gpl = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         Program.launch(configData.getLangRes().getString("gpl"));
      }
   };

   /** Listener for the OK button. */
   private SelectionListener close = new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
         dialog.close();
      }
   };
}
