/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.timeline.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * A configuration panel for Flickr searches
 * @author nsimpson
 */
public class FlickrConfigurationPanel extends javax.swing.JPanel {

    private PropertyChangeSupport listeners;

    public enum SEARCH_TYPE {

        TAGS, TEXT
    };

        /**
     * Adds a bound property listener to the dialog
     * @param listener a listener for dialog events
     */
    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null) {
            listeners = new PropertyChangeSupport(this);
        }
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Removes a bound property listener from the dialog
     * @param listener the listener to remove
     */
    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listeners != null) {
            listeners.removePropertyChangeListener(listener);
        }
    }

    public FlickrConfigurationPanel() {
        initComponents();
    }

    public void setQueryText(String text) {
        searchTextField.setText(text);
    }

    public String getQueryText() {
        return searchTextField.getText();
    }

    public void setSearchType(SEARCH_TYPE type) {
        if (type == SEARCH_TYPE.TAGS) {
            tagsRadioButton.setSelected(true);
        } else {
            fullTextRadioButton.setSelected(true);
        }
    }

    public SEARCH_TYPE getSearchType() {
        return tagsRadioButton.isSelected() ? SEARCH_TYPE.TAGS : SEARCH_TYPE.TEXT;
    }

    public int getResultLimit() {
        int limit = Integer.MAX_VALUE;

        if (return1RadioButton.isSelected()) {
            limit = 1;
        } else if (return2RadioButton.isSelected()) {
            limit = 2;
        } else if (return4RadioButton.isSelected()) {
            limit = 4;
        } else if (return6RadioButton.isSelected()) {
            limit = 6;
        } else if (return8RadioButton.isSelected()) {
            limit = 8;
        }
        return limit;
    }

    public void setResultLimit(int limit) {
        if (limit == 1) {
            return1RadioButton.setSelected(true);
        } else if (limit == 2) {
            return2RadioButton.setSelected(true);
        } else if (limit == 4) {
            return4RadioButton.setSelected(true);
        } else if (limit == 6) {
            return6RadioButton.setSelected(true);
        } else if (limit == 8) {
            return8RadioButton.setSelected(true);
        } else if (limit == Integer.MAX_VALUE) {
            returnAllRadioButton.setSelected(true);
        }
    }
    
    public void setCreativeCommons(boolean commons) {
        creativeCommonsCheckBox.setSelected(commons);
    }
    
    public boolean isCreativeCommons() {
        return creativeCommonsCheckBox.isSelected();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tagsOrTextButtonGroup = new javax.swing.ButtonGroup();
        resultsButtonGroup = new javax.swing.ButtonGroup();
        searchByButtonGroup = new javax.swing.ButtonGroup();
        returnButtonGroup = new javax.swing.ButtonGroup();
        configurationLabel = new javax.swing.JLabel();
        searchLabel = new javax.swing.JLabel();
        tagsRadioButton = new javax.swing.JRadioButton();
        fullTextRadioButton = new javax.swing.JRadioButton();
        searchTextField = new javax.swing.JTextField();
        returnLabel = new javax.swing.JLabel();
        return1RadioButton = new javax.swing.JRadioButton();
        return2RadioButton = new javax.swing.JRadioButton();
        return4RadioButton = new javax.swing.JRadioButton();
        return6RadioButton = new javax.swing.JRadioButton();
        return8RadioButton = new javax.swing.JRadioButton();
        returnAllRadioButton = new javax.swing.JRadioButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        creativeCommonsCheckBox = new javax.swing.JCheckBox();
        byLabel = new javax.swing.JLabel();
        interestingnessRadioButton = new javax.swing.JRadioButton();
        relevanceRadioButton = new javax.swing.JRadioButton();

        configurationLabel.setFont(configurationLabel.getFont().deriveFont(configurationLabel.getFont().getStyle() | java.awt.Font.BOLD));
        configurationLabel.setText("Flickr Query Configuration");

        searchLabel.setText("Search for: ");

        tagsOrTextButtonGroup.add(tagsRadioButton);
        tagsRadioButton.setSelected(true);
        tagsRadioButton.setText("tags");

        tagsOrTextButtonGroup.add(fullTextRadioButton);
        fullTextRadioButton.setText("full text");

        searchTextField.setText("horses and boats");

        returnLabel.setText("Return:");

        returnButtonGroup.add(return1RadioButton);
        return1RadioButton.setSelected(true);
        return1RadioButton.setText("1");

        returnButtonGroup.add(return2RadioButton);
        return2RadioButton.setText("2");

        returnButtonGroup.add(return4RadioButton);
        return4RadioButton.setText("4");

        returnButtonGroup.add(return6RadioButton);
        return6RadioButton.setText("6");

        returnButtonGroup.add(return8RadioButton);
        return8RadioButton.setText("8");

        returnButtonGroup.add(returnAllRadioButton);
        returnAllRadioButton.setText("All items");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        creativeCommonsCheckBox.setSelected(true);
        creativeCommonsCheckBox.setText("with Creative Commons license");

        byLabel.setText("by:");

        searchByButtonGroup.add(interestingnessRadioButton);
        interestingnessRadioButton.setText("interestingness");

        searchByButtonGroup.add(relevanceRadioButton);
        relevanceRadioButton.setText("relevance");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(configurationLabel)
                .addContainerGap(134, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(searchLabel)
                    .addComponent(byLabel)
                    .addComponent(returnLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(creativeCommonsCheckBox)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tagsRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fullTextRadioButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(relevanceRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(interestingnessRadioButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(return1RadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(return2RadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(return4RadioButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(return6RadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(return8RadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(returnAllRadioButton))
                    .addComponent(searchTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(146, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(configurationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(fullTextRadioButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchLabel)
                            .addComponent(searchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tagsRadioButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(relevanceRadioButton)
                    .addComponent(interestingnessRadioButton)
                    .addComponent(byLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(return2RadioButton)
                            .addComponent(return1RadioButton)
                            .addComponent(return4RadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(returnAllRadioButton)
                            .addComponent(return6RadioButton)
                            .addComponent(return8RadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(creativeCommonsCheckBox))
                    .addComponent(returnLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // TODO: updateQuery
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // TODO: dismiss dialog
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel byLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel configurationLabel;
    private javax.swing.JCheckBox creativeCommonsCheckBox;
    private javax.swing.JRadioButton fullTextRadioButton;
    private javax.swing.JRadioButton interestingnessRadioButton;
    private javax.swing.JButton okButton;
    private javax.swing.JRadioButton relevanceRadioButton;
    private javax.swing.ButtonGroup resultsButtonGroup;
    private javax.swing.JRadioButton return1RadioButton;
    private javax.swing.JRadioButton return2RadioButton;
    private javax.swing.JRadioButton return4RadioButton;
    private javax.swing.JRadioButton return6RadioButton;
    private javax.swing.JRadioButton return8RadioButton;
    private javax.swing.JRadioButton returnAllRadioButton;
    private javax.swing.ButtonGroup returnButtonGroup;
    private javax.swing.JLabel returnLabel;
    private javax.swing.ButtonGroup searchByButtonGroup;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JTextField searchTextField;
    private javax.swing.ButtonGroup tagsOrTextButtonGroup;
    private javax.swing.JRadioButton tagsRadioButton;
    // End of variables declaration//GEN-END:variables
}
