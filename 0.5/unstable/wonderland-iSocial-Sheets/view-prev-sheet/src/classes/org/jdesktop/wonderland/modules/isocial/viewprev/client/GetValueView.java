/**
 * iSocial Project
 * http://isocial.missouri.edu
 *
 * Copyright (c) 2011, University of Missouri iSocial Project, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The iSocial project designates this particular file as
 * subject to the "Classpath" exception as provided by the iSocial
 * project in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.modules.isocial.viewprev.client;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.modules.isocial.client.ISocialManager;
import org.jdesktop.wonderland.modules.isocial.client.view.SheetView;
import org.jdesktop.wonderland.modules.isocial.client.view.annotation.View;
import org.jdesktop.wonderland.modules.isocial.common.model.Instance;
import org.jdesktop.wonderland.modules.isocial.common.model.Result;
import org.jdesktop.wonderland.modules.isocial.common.model.Role;
import org.jdesktop.wonderland.modules.isocial.common.model.Sheet;
import org.jdesktop.wonderland.modules.isocial.common.model.SheetDetails;
import org.jdesktop.wonderland.modules.isocial.viewprev.common.GetValueSheet;
import org.jdesktop.wonderland.modules.isocial.viewprev.common.SetValueResult;
import org.jdesktop.wonderland.modules.isocial.viewprev.common.SetValueSheet;

/**
 * View class for setting a value
 * @author Jonathan Kaplan <jonathankap@wonderbuilders.com>
 */
@View(value=GetValueSheet.class)
public class GetValueView extends javax.swing.JPanel implements SheetView {
    private static final Logger LOGGER =
            Logger.getLogger(GetValueView.class.getName());

    private ISocialManager manager;
    private Sheet sheet;
    private GetValueSheet details;

    /** Creates new form SetValueView */
    public GetValueView() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/isocial/viewprev/client/Bundle"); // NOI18N
        nameLabel.setText(bundle.getString("GetValueView.nameLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameLabel)
                .addContainerGap(153, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameLabel)
                .addGap(35, 35, 35))
        );
    }// </editor-fold>//GEN-END:initComponents

    public void initialize(ISocialManager manager, Sheet sheet, Role role) {
        this.manager = manager;
        this.sheet = sheet;
        this.details = (GetValueSheet) sheet.getDetails();

        String value = "unknown";

        // find the previous value of a sheet with the same name
        try {
            value = findPreviousValue(details.getInternalName());
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Error reading data", ioe);
        }

        nameLabel.setText("Value of " + details.getInternalName() + " is " +
                           value);
    }

    /**
     * Find a previous setValueSheet with the same name as this sheet
     */
    private String findPreviousValue(String name) throws IOException {
        for (Instance i : manager.getInstances()) {
            for (Sheet s : i.getSheets()) {
                // check if this sheet is a setter matching this getter
                if (isSetValueSheet(s, details.getInternalName())) {
                    return getSheetValue(i, s);
                }
            }
        }

        // not found
        return "unknown";
    }

    /**
     * Check if the given sheet is a SetValueSheet with the given name
     */
    private boolean isSetValueSheet(Sheet sheet, String name) {
        SheetDetails d = sheet.getDetails();
        return d instanceof SetValueSheet &&
               ((SetValueSheet) d).getInternalName().equals(name);
    }

    /**
     * Find a result for this user and extract its value
     */
    private String getSheetValue(Instance i, Sheet s) throws IOException {
        // get all results
        Collection<Result> results = manager.getResultsForInstance(i.getId(), s.getId());

        // find result for this user
        for (Result result : results) {
            if (result.getCreator().equals(manager.getUsername())) {
                SetValueResult d = (SetValueResult) result.getDetails();
                return String.valueOf(d.getValue());
            }
        }

        // not found
        return "unknown";
    }


    public String getMenuName() {
        return "Get " + details.getInternalName();
    }

    public boolean isAutoOpen() {
        return false;
    }

    public HUDComponent open(HUD hud) {
        return hud.createComponent(this);
    }

    public void close() {
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel nameLabel;
    // End of variables declaration//GEN-END:variables

}
